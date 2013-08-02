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

package com.pwc.us.rgi.vista.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pwc.us.rgi.m.tool.ToolErrorException;
import com.pwc.us.rgi.util.CLIParamMgr;
import com.pwc.us.rgi.vista.tools.entry.CLIEntryTools;
import com.pwc.us.rgi.vista.tools.macro.MacroTools;
import com.pwc.us.rgi.vista.tools.repository.RepositoryTools;
import com.pwc.us.rgi.vista.tools.routine.CLIRoutineTools;
import com.pwc.us.rgi.vista.tools.utility.CLIUtilityTools;

public class MRoutineAnalyzer {
	private static CLIParams getCommandLineParamaters(String[] args) {
		try {
			CLIParams params = CLIParamMgr.parse(CLIParams.class, args);
			return params;
		} catch (Throwable t) {
			MRALogger.logError("Invalid command line options.", t);
			return null;
		}		
	}
	
	private static String getOptionMsg(Collection<String> options) {
		String result = "";
		for (String option : options) {
			if (! result.isEmpty()) {
				result += ", ";
			}
			result += option;
		}
		return "Possible run types: " + result;
	}

	private static String getRunTypeOptionsMsg(Tools[] rtss) {
		List<String> allOptions = new ArrayList<String>();
		for (Tools rts : rtss) {
			String name = rts.getName();
			allOptions.add(name);
		}
		return getOptionMsg(allOptions);
	}
	
	private static void logErrorWithOptions(String firstLineMsg, Tools[] rtss) {
		String secondLineMsg = getRunTypeOptionsMsg(rtss);
		MRALogger.logError(firstLineMsg + "\n" + secondLineMsg + "\n");
	}

	private static void logErrorWithOptions(String firstLineMsg, Tools rts) {
		String secondLineMsg = getOptionMsg(rts.getRunTypeOptions());
		MRALogger.logError(firstLineMsg + "\n" + secondLineMsg + "\n");
	}

	private static boolean run(Tools rts, String runTypeOption, CLIParams params) throws IOException {
		Tool rt = rts.getRunType(runTypeOption, params);
		if (rt != null) {
			MRALogger.logInfo("Started " + runTypeOption + ".");
			rt.run();
			MRALogger.logInfo("Ended " + runTypeOption + ".");
		    return true;
		} else {
			return false;
		}
	}
	
	public static void main(String[] args) {
		try {
			Tools[] rtss = new Tools[]{new RepositoryTools("repo"), new CLIEntryTools("entry"), new MacroTools("macro"), new CLIRoutineTools("routine"), new CLIUtilityTools("util")};
		
			CLIParams params = getCommandLineParamaters(args);	
			if (params == null) return;
			String runTypeOption = params.getPositional(0, null);
			if (runTypeOption == null) {				
				logErrorWithOptions("A run type option needs to be specified as the first positional argument.", rtss);
				return;				
			}
			
			params.popPositional();
			
			for (int i=0; i<rtss.length; ++i) {
				Tools tools = rtss[i];
				String toolsName = tools.getName(); 
				if (runTypeOption.equals(toolsName)) {
					if (params.positionals.size() == 0) {
						logErrorWithOptions("An addditional run type option needs to be specified following \"" + runTypeOption + "\".", tools);
						return;
					}
					String runTypeOptionAddl = params.positionals.get(0);
					params.popPositional();
					
					if (run(rtss[i], runTypeOptionAddl , params)) return;
					logErrorWithOptions("Specified run type option " + runTypeOptionAddl  + " is not known.", tools);
					return;
				}
			}			
			logErrorWithOptions("Invalid run type option " + runTypeOption + ".", rtss);
			return;				
		} catch (ToolErrorException e) {
			MRALogger.logError("Error running tool", e);
		} catch (IOException e) {
			MRALogger.logError("File error", e);
		} catch (Throwable t) {
			MRALogger.logError("Unexpected error.", t);
		}
	}
}
