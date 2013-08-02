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
import java.util.List;
import java.util.Map;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.parsetree.data.EntryIdWithSource;
import com.pwc.us.rgi.output.FileTerminal;
import com.pwc.us.rgi.vista.repository.RepositoryInfo;
import com.pwc.us.rgi.vista.repository.VistaPackage;
import com.pwc.us.rgi.vista.repository.VistaPackages;
import com.pwc.us.rgi.vista.tools.MRALogger;

public class OptionRPCWriter {
	protected RepositoryInfo repositoryInfo;
	private FileTerminal fileWrapper;
	
	public OptionRPCWriter(RepositoryInfo repositoryInfo, FileTerminal fileWrapper) {
		this.repositoryInfo = repositoryInfo;
		this.fileWrapper = fileWrapper;
	}
		
	public void write(VistaPackages vps, List<EntryIdWithSource> values) {
		List<VistaPackage> packages = this.repositoryInfo.getAllPackages();
		Map<String, List<EntryIdWithSource>> valuesByPackage = new HashMap<String, List<EntryIdWithSource>>();
		for (VistaPackage p : packages) {
			String name = p.getPackageName();
			valuesByPackage.put(name, new ArrayList<EntryIdWithSource>());
		}
		for (EntryIdWithSource value : values) {
			EntryId eid = value.getEntryId();
			String routineName = eid.getRoutineName();
			if ((routineName != null) && (! routineName.isEmpty())) {
				VistaPackage vp = this.repositoryInfo.getPackageFromRoutineName(routineName);
				String name = vp.getPackageName();
				List<EntryIdWithSource> pkgOptions = valuesByPackage.get(name);
				pkgOptions.add(value);
			}
		}		
		try {
			int index = 0;
			List<VistaPackage> reportPackages = vps.getPackages();
			boolean multi = reportPackages.size() > 1;
			for (VistaPackage p : reportPackages) {
				++index;
				String name = p.getPackageName();
				String prefix = multi ? String.valueOf(index) + ". " : "";
				this.fileWrapper.writeEOL(prefix + name);
				this.fileWrapper.writeEOL();
				List<EntryIdWithSource> reportOptions = valuesByPackage.get(name);
				if ((reportOptions != null) && (reportOptions.size() > 0)) {
					Collections.sort(reportOptions);
					for (EntryIdWithSource reportOption : reportOptions) {
						this.fileWrapper.writeEOL(" NAME: " + reportOption.getSource());
						this.fileWrapper.writeEOL("  TAG: " + reportOption.getEntryId().toString());
						this.fileWrapper.writeEOL();
					}
				} else {
					this.fileWrapper.writeEOL("  No entries");
					this.fileWrapper.writeEOL();					
				}
			}
			this.fileWrapper.stop();
		} catch (IOException e) {
			MRALogger.logError("Unable to write result", e);
		}
	}
}