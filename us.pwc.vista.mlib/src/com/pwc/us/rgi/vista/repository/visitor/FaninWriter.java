//---------------------------------------------------------------------------
// Copyright 2013 PwC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//---------------------------------------------------------------------------

package com.pwc.us.rgi.vista.repository.visitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.parsetree.visitor.FanInRecorder;
import com.pwc.us.rgi.output.FileTerminal;
import com.pwc.us.rgi.struct.Filter;
import com.pwc.us.rgi.vista.repository.RepositoryInfo;
import com.pwc.us.rgi.vista.repository.RepositoryVisitor;
import com.pwc.us.rgi.vista.repository.VistaPackage;
import com.pwc.us.rgi.vista.repository.VistaPackages;
import com.pwc.us.rgi.vista.tools.MRALogger;

public class FaninWriter extends RepositoryVisitor {
	private RepositoryInfo repositoryInfo;
	private FileTerminal fileWrapper;
	private FanInRecorder faninRecorder;
	private boolean rawFormat;
	
	private static class EntryIdSource {
		public Set<String> packages;
		
		public void setPackages(Set<String> packages) {
			this.packages = packages;
		}
		
		public Set<String> getPackages() {
			return this.packages;
		}
	}
	
	private static class EntryIdWithSources implements Comparable<EntryIdWithSources> {
		public EntryId entryId;
		public EntryIdSource sources;
		
		public EntryIdWithSources(EntryId entryId, EntryIdSource source) {
			this.entryId = entryId;
			this.sources = source;
		}
		
		@Override
		public int compareTo(EntryIdWithSources rhs) {
			return this.entryId.compareTo(rhs.entryId);
		}		
	}
	
	public FaninWriter(RepositoryInfo repositoryInfo, FileTerminal fileWrapper, boolean rawFormat) {
		this.repositoryInfo = repositoryInfo;
		this.fileWrapper = fileWrapper;
		this.rawFormat = rawFormat;
	}
		
	@Override
	protected void visitVistaPackage(VistaPackage routinePackage) {
		Filter<EntryId> filter = routinePackage.getPackageFanoutFilter();
		this.faninRecorder.setFilter(filter);
		this.faninRecorder.setCurrentPackagePrefix(routinePackage.getPrimaryPrefix());
		super.visitVistaPackage(routinePackage);
	}

	public void visitRoutine(Routine routine) {
		if ((! routine.getName().startsWith("ZZ")) && (routine.getName().charAt(0) != '%')) {
			routine.accept(this.faninRecorder);
		}
	}
	
	protected void visitRoutinePackages(VistaPackages rps) {
		this.faninRecorder = new FanInRecorder(this.repositoryInfo);
		List<VistaPackage> packages = this.repositoryInfo.getAllPackages();
		for (VistaPackage p : packages) {
			p.accept(this);
		}
		Map<EntryId, Set<String>> codeFanins = this.faninRecorder.getFanIns();
		
		Map<EntryId, EntryIdSource> fanins = new HashMap<EntryId, EntryIdSource>();
		Set<EntryId> codeFaninsEIs = codeFanins.keySet();
		for (EntryId ei : codeFaninsEIs) {
			Set<String> packagePrefixes = codeFanins.get(ei);
			EntryIdSource source = new EntryIdSource();
			source.setPackages(packagePrefixes);
			fanins.put(ei, source);
		}
		codeFanins = null;
				
		Map<String, List<EntryIdWithSources>> faninsByPackage = new HashMap<String, List<EntryIdWithSources>>();
		Map<String, Set<String>> sourcePackagesByPackage = new HashMap<String, Set<String>>();

		for (VistaPackage p : packages) {
			String name = p.getPackageName();
			faninsByPackage.put(name, new ArrayList<EntryIdWithSources>());
			sourcePackagesByPackage.put(name, new HashSet<String>());
		}
		faninsByPackage.put("UNCATEGORIZED", new ArrayList<EntryIdWithSources>());
		Set<EntryId> faninEntryIds = fanins.keySet();
		for (EntryId f : faninEntryIds) {
			String routineName = f.getRoutineName();
			if (routineName == null) continue;
			if (routineName.isEmpty()) continue;
			VistaPackage p = this.repositoryInfo.getPackageFromRoutineName(routineName);
			String packageName = p.getPackageName();
			List<EntryIdWithSources> entryIds = faninsByPackage.get(packageName);
			EntryIdSource source = fanins.get(f);
			Set<String> sourcePrefixes = source.getPackages();
			sourcePackagesByPackage.get(packageName).addAll(sourcePrefixes);
			EntryIdWithSources fws = new EntryIdWithSources(f, source);
			entryIds.add(fws);
		}
		
		try {
			if (rawFormat) {
				List<VistaPackage> reportPackages = rps.getPackages();
				for (VistaPackage p : reportPackages) {
					String name = p.getPackageName();
					List<EntryIdWithSources> fs = faninsByPackage.get(name);
					Collections.sort(fs);
					for (EntryIdWithSources f : fs) {
						this.fileWrapper.writeEOL(f.entryId.toString());
					}
				}
				this.fileWrapper.stop();
			} else {
				int ndx = 0;
				this.fileWrapper.getTerminalFormatter().setTitleWidth(21);
				List<VistaPackage> reportPackages = rps.getPackages();
				boolean multi = reportPackages.size() > 1;
				for (VistaPackage p : reportPackages) {
					if (multi) {
						this.fileWrapper.writeEOL("--------------------------------------------------------------");
						this.fileWrapper.writeEOL();
					}
					String name = p.getPackageName();
					++ndx;
					String pkgNumberPrefix = multi ? String.valueOf(ndx) + ". " : "";
					if (sourcePackagesByPackage.get(name).size() > 40) {
						this.fileWrapper.writeEOL(pkgNumberPrefix + "COMMON SERVICE NAME: " + name);
					} else {
						this.fileWrapper.writeEOL(pkgNumberPrefix + "PACKAGE NAME: " + name);					
					}
					this.fileWrapper.writeEOL();
					List<EntryIdWithSources> fs = faninsByPackage.get(name);
					if (fs.size() == 0) {
						this.fileWrapper.writeEOL("   Not used by other packages");
						this.fileWrapper.writeEOL();					
					} else {
						Collections.sort(fs);
						for (EntryIdWithSources f : fs) {
							this.fileWrapper.writeEOL("  " + f.entryId.toString());
							List<String> sourcePackages = new ArrayList<String>(f.sources.getPackages());
							Collections.sort(sourcePackages);
							List<String> outputList = new ArrayList<String>();
							for (String source : sourcePackages) {
								VistaPackage vp = this.repositoryInfo.getPackageFromPrefix(source);							
								String pkgName = vp.getPackageName();
								outputList.add(pkgName);
							}
							this.fileWrapper.writeFormatted("CALLING PACKAGES", outputList);
							this.fileWrapper.writeEOL();
						}
					}
					if (multi) {
						this.fileWrapper.writeEOL("--------------------------------------------------------------");
						this.fileWrapper.writeEOL();
					}
				}
				this.fileWrapper.stop();
			} 
		} catch (IOException e) {
			MRALogger.logError("Unable to write result", e);
		}
	}
}