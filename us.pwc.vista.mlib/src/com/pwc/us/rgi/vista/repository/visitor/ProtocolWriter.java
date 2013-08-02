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

public class ProtocolWriter {
	private RepositoryInfo repositoryInfo;
	private FileTerminal fileWrapper;
	private String protocolType;
	
	public ProtocolWriter(RepositoryInfo repositoryInfo, FileTerminal fileWrapper, String protocolType) {
		this.repositoryInfo = repositoryInfo;
		this.fileWrapper = fileWrapper;
		this.protocolType = protocolType;
	}
		
	public void write(VistaPackages vps) {
		List<VistaPackage> packages = this.repositoryInfo.getAllPackages(); 
		List<List<EntryIdWithSource>> protocolsMVL = this.repositoryInfo.getProtocolEntryPoints(protocolType); //multi value list
		Map<String, List<EntryIdWithSource>> protocolsByPackage = new HashMap<String, List<EntryIdWithSource>>();
		for (VistaPackage p : packages) {
			String name = p.getPackageName();
			protocolsByPackage.put(name, new ArrayList<EntryIdWithSource>());
		}
		for (List<EntryIdWithSource> protocols : protocolsMVL) {
			for (EntryIdWithSource protocol : protocols) {
				EntryId eid = protocol.getEntryId();
				String routineName = eid.getRoutineName();
				if ((routineName != null) && (!routineName.isEmpty())) {
					VistaPackage vp = this.repositoryInfo
							.getPackageFromRoutineName(routineName);
					String name = vp.getPackageName();
					List<EntryIdWithSource> pkgProtocols = protocolsByPackage
							.get(name);
					pkgProtocols.add(protocol);
				}
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
				List<EntryIdWithSource> reportProtocols = protocolsByPackage.get(name);
				if ((reportProtocols != null) && (reportProtocols.size() > 0)) {
					Collections.sort(reportProtocols);
					for (EntryIdWithSource reportProtocol : reportProtocols) {
						this.fileWrapper.writeEOL(" NAME: " + reportProtocol.getSource());
						this.fileWrapper.writeEOL("  TAG: " + reportProtocol.getEntryId().toString());
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