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

package com.pwc.us.rgi.vista.tools.macro;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import com.pwc.us.rgi.vista.repository.RepositoryInfo;
import com.pwc.us.rgi.vista.tools.CLIParams;
import com.pwc.us.rgi.vista.tools.MRoutineAnalyzer;
import com.pwc.us.rgi.vista.tools.Tool;
import com.pwc.us.rgi.vista.tools.Tools;

public class MacroTools extends Tools {	
	private static class MRoutineAnalyzerTestbench extends Tool {		
		public MRoutineAnalyzerTestbench(CLIParams params) {
			super(params);
		}
		
		@Override
		public void run() {
			String root = RepositoryInfo.getLocationWithLog();
			if (root == null) return;
			
			String outputPath = this.params.getPositional(0, "C:\\Sandbox");
			String pathPrefix = this.params.getPositional(1, "C:\\Sandbox\\m_");
			
 			MRoutineAnalyzer.main(new String[]{"repo", "fanout", "-o", pathPrefix + "fo_all.txt"});				
			MRoutineAnalyzer.main(new String[]{"repo", "fanout", "-o", pathPrefix + "fo_gmpl.txt", "-p", "GMPL"});				
			MRoutineAnalyzer.main(new String[]{"repo", "fanout", "-o", pathPrefix + "fo_sd.txt", "-p", "SD"});				
			
			Path zgi = Paths.get(root, "Scripts", "ZGI.m");
			Path zgo = Paths.get(root, "Scripts", "ZGO.m");
			MRoutineAnalyzer.main(new String[]{"repo", "fanin", "-o", pathPrefix + "fi_all.txt",
					"-mf", zgi.toString(), "-mf", zgo.toString(), "-pe", "DENTAL RECORD MANAGER"});				
			MRoutineAnalyzer.main(new String[]{"repo", "fanin", "-o", pathPrefix + "fi_gmpl.txt", "-p", "GMPL"});				
			MRoutineAnalyzer.main(new String[]{"repo", "fanin", "-o", pathPrefix + "fi_sd.txt", "-p", "SD"});				
			
			MRoutineAnalyzer.main(new String[]{"repo", "option", "-o", pathPrefix + "opt_all.txt", "-pe", "DENTAL RECORD MANAGER"});				
			MRoutineAnalyzer.main(new String[]{"repo", "option", "-o", pathPrefix + "opt_gmpl.txt", "-p", "GMPL"});				
			MRoutineAnalyzer.main(new String[]{"repo", "option", "-o", pathPrefix + "opt_sd.txt", "-p", "SD"});				
			
			MRoutineAnalyzer.main(new String[]{"repo", "rpc", "-o", pathPrefix + "rpc_all.txt", "-pe", "DENTAL RECORD MANAGER"});				
			MRoutineAnalyzer.main(new String[]{"repo", "rpc", "-o", pathPrefix + "rpc_gmpl.txt", "-p", "GMPL"});				
			MRoutineAnalyzer.main(new String[]{"repo", "rpc", "-o", pathPrefix + "rpc_sd.txt", "-p", "SD"});				
			
			MRoutineAnalyzer.main(new String[]{"repo", "usesglb", "-o", pathPrefix + "uses_gmpl.txt", "-p", "GMPL", "-ownf", outputPath + "\\Ownership.csv"});				
			MRoutineAnalyzer.main(new String[]{"repo", "usesglb", "-o", pathPrefix + "uses_sd.txt", "-p", "SD", "-ownf", outputPath + "\\Ownership.csv"});				

			MRoutineAnalyzer.main(new String[]{"repo", "usedglb", "-o", pathPrefix + "used_gmpl.txt", "-p", "GMPL", "-ownf", outputPath + "\\Ownership.csv"});				
			MRoutineAnalyzer.main(new String[]{"repo", "usedglb", "-o", pathPrefix + "used_sd.txt", "-p", "SD", "-ownf", outputPath + "\\Ownership.csv"});				
			
			MRoutineAnalyzer.main(new String[]{"repo", "filemancall", "-o", pathPrefix + "fmc_all.txt"});	  //SPNRPC4 is due to error			

			MRoutineAnalyzer.main(new String[]{"util", "parsetreesave", "-ptd", outputPath + "\\serial"});		

			MRoutineAnalyzer.main(new String[]{"repo", "entry", "-p", "OR", "-r", "ORQQPL.*", "-o", pathPrefix + "einfo_cprspl_tags.txt"});
			
			MRoutineAnalyzer.main(new String[]{"entry", "info", "-i", pathPrefix + "einfo_cprspl_tags.txt", "-o", pathPrefix + "einfo_cprspl_0.txt",
						"-ptd", outputPath + "\\serial", "-rd", "all", "-xns", "%"});
			MRoutineAnalyzer.main(new String[]{"entry", "info", "-i", pathPrefix + "einfo_cprspl_tags.txt", "-o", pathPrefix + "einfo_cprspl_1.txt",
						"-ptd", outputPath + "\\serial", "-rd", "all", "-xns", "DI", "-xns", "DD", "-xns", "DM", "-xns", "%", "-xns", "Z", "-xns", "X", "-xxns", "XPAR"});
			MRoutineAnalyzer.main(new String[]{"entry", "info", "-i", pathPrefix + "einfo_cprspl_tags.txt", "-o", pathPrefix + "einfo_cprspl_2.txt",
						"-ptd", outputPath + "\\serial", "-rd", "all", "-ns", "OR", "-ns", "OCX", "-xns", "ORRC", "-xns", "ORRJ"});
			MRoutineAnalyzer.main(new String[]{"entry", "info", "-i", pathPrefix + "einfo_cprspl_tags.txt", "-o", pathPrefix + "einfo_cprspl_3.txt",
						"-ptd", outputPath + "\\serial", "-rd", "routine"});


			MRoutineAnalyzer.main(new String[]{"repo", "fanin", "-o", pathPrefix + "einfo_gmplfi_tags.txt", "--rawformat", "-p", "GMPL"});

			MRoutineAnalyzer.main(new String[]{"entry", "info", "-i", pathPrefix + "einfo_gmplfi_tags.txt", "-o", pathPrefix + "einfo_gmplfi_0.txt",
						"-ptd", outputPath + "\\serial", "-rd", "all", "-xns", "%"});
			MRoutineAnalyzer.main(new String[]{"entry", "info", "-i", pathPrefix + "einfo_gmplfi_tags.txt", "-o", pathPrefix + "einfo_gmplfi_1.txt",
						"-ptd", outputPath + "\\serial", "-rd", "all", "-xns", "DI", "-xns", "DD", "-xns", "DM", "-xns", "%", "-xns", "Z", "-xns", "X", "-xxns", "XPAR"});
			MRoutineAnalyzer.main(new String[]{"entry", "info", "-i", pathPrefix + "einfo_gmplfi_tags.txt", "-o", pathPrefix + "einfo_gmplfi_2.txt",
						"-ptd", outputPath + "\\serial", "-rd", "all", "-ns", "GMPL"});
			MRoutineAnalyzer.main(new String[]{"entry", "info", "-i", pathPrefix + "einfo_gmplfi_tags.txt", "-o", pathPrefix + "einfo_gmplfi_3.txt",
					"-ptd", outputPath + "\\serial", "-rd", "routine"});
			
			
			MRoutineAnalyzer.main(new String[]{"repo", "fanin", "-o", pathPrefix + "einfo_sdfi_tags.txt", "--rawformat", "-p", "SD"});

			MRoutineAnalyzer.main(new String[]{"entry", "info", "-i", pathPrefix + "einfo_sdfi_tags.txt", "-o", pathPrefix + "einfo_sdfi_0.txt",
						"-ptd", outputPath + "\\serial", "-rd", "all", "-xns", "%"});
			MRoutineAnalyzer.main(new String[]{"entry", "info", "-i", pathPrefix + "einfo_sdfi_tags.txt", "-o", pathPrefix + "einfo_sdfi_1.txt",
						"-ptd", outputPath + "\\serial", "-rd", "all", "-xns", "DI", "-xns", "DD", "-xns", "DM", "-xns", "%", "-xns", "Z", "-xns", "X", "-xxns", "XPAR", "-xxns", "XT", "-xxns", "XMX", "-xxns", "XM"});
			MRoutineAnalyzer.main(new String[]{"entry", "info", "-i", pathPrefix + "einfo_sdfi_tags.txt", "-o", pathPrefix + "einfo_sdfi_2.txt",
						"-ptd", outputPath + "\\serial", "\\serial", "-rd", "all", "-ns", "SD", "-ns", "SC"});		
			MRoutineAnalyzer.main(new String[]{"entry", "info", "-i", pathPrefix + "einfo_sdfi_tags.txt", "-o", pathPrefix + "einfo_sdfi_3.txt",
					"-ptd", outputPath + "\\serial", "-rd", "routine"});		
		}
	}

	public MacroTools(String name) {
		super(name);
	}
		
	@Override
	protected void updateTools(Map<String, MemberFactory> tools) {
		tools.put("mratb", new MemberFactory() {				
			@Override
			public Tool getInstance(CLIParams params) {
				return new MRoutineAnalyzerTestbench(params);
			}
		});
	}
}
