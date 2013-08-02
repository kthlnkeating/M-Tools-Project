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
import java.util.regex.Pattern;

import com.pwc.us.rgi.m.token.MVersion;
import com.pwc.us.rgi.m.tool.NamespaceFilter;
import com.pwc.us.rgi.m.tool.OutputFlags;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.SavedParsedTrees;
import com.pwc.us.rgi.m.tool.SourceCodeFiles;
import com.pwc.us.rgi.m.tool.SourceCodeSupply;
import com.pwc.us.rgi.m.tool.SourceCodeToParseTreeAdapter;
import com.pwc.us.rgi.output.FileTerminal;
import com.pwc.us.rgi.output.SystemTerminal;
import com.pwc.us.rgi.output.Terminal;
import com.pwc.us.rgi.vista.repository.RepositoryInfo;

public class CLIParamsAdapter {
	private static SourceCodeSupply sourceCodeSupply;
	private static ParseTreeSupply parseTreeSupply;
	
	public static NamespaceFilter getNamespaceFilter(List<String> included, List<String> excluded, List<String> excludedException) {
		NamespaceFilter filter = new NamespaceFilter();
		filter.addIncludedNamespaces(included);
		filter.addExcludedNamespaces(excluded);
		filter.addExcludedExceptionNamespaces(excludedException);
		return filter;
	}
		
	public static SourceCodeSupply getSourceCodeSupply(CLIParams params) {
		if (sourceCodeSupply == null) {
			String rootDirectory = params.rootDirectory;
			if ((rootDirectory == null) || (rootDirectory.isEmpty())) {
				rootDirectory = RepositoryInfo.getLocation();
			}
			try {
				sourceCodeSupply = SourceCodeFiles.getInstance(rootDirectory);
			}
			catch (IOException e) {
			}							
		}
		return sourceCodeSupply;
	}
	
	public static ParseTreeSupply getParseTreeSupply(CLIParams params) {
		if (CLIParamsAdapter.parseTreeSupply != null) {
			return CLIParamsAdapter.parseTreeSupply;
		}		
		if ((params.parseTreeDirectory == null) || params.parseTreeDirectory.isEmpty()) {
			SourceCodeSupply supply = CLIParamsAdapter.getSourceCodeSupply(params);
			CLIParamsAdapter.parseTreeSupply = new SourceCodeToParseTreeAdapter(supply);
			return CLIParamsAdapter.parseTreeSupply;
		} else {
			CLIParamsAdapter.parseTreeSupply = new SavedParsedTrees(params.parseTreeDirectory);
			return CLIParamsAdapter.parseTreeSupply;
		}		
	}
	
	public static RepositoryInfo getRepositoryInfo(CLIParams params) {
		MRARoutineFactory rf = MRARoutineFactory.getInstance(MVersion.CACHE);
		if (rf != null) {
			RepositoryInfo ri = RepositoryInfo.getInstance(rf);
			if (ri != null) {
				ri.addMDirectories(params.additionalMDirectories);
				ri.addMFiles(params.additionalMFiles);
				if ((params.ownershipFilePath != null) && (! params.ownershipFilePath.isEmpty())) {
					ri.readGlobalOwnership(params.ownershipFilePath);			
				}
			}	
			return ri;
		}		
		return null;
	}
	
	public static List<String> getCLIRoutines(CLIParams params) {
		List<String> specifiedRoutineNames = params.routines;
		boolean hasRegularExpression = false;
		Pattern p = Pattern.compile("[^a-zA-Z0-9\\%]");
		for (String routineName : specifiedRoutineNames) {
			hasRegularExpression = p.matcher(routineName).find();		
			if (hasRegularExpression) break;
		}
		if (hasRegularExpression) {
			List<String> expandedRoutineNames = new ArrayList<String>();
			ParseTreeSupply pts = CLIParamsAdapter.getParseTreeSupply(params);
			Collection<String> allRoutineNames = pts.getAllRoutineNames();
			boolean[] matched = new boolean[specifiedRoutineNames.size()];
			for (String routineName : allRoutineNames) {
				int index = 0;
				for (String specifiedRoutineName : specifiedRoutineNames) {
					if (routineName.matches(specifiedRoutineName)) {
						expandedRoutineNames.add(routineName);
						matched[index] = true;
					}
					++index;
				}				
			}
			for (int i=0; i<matched.length; ++i) {
				if (! matched[i]) {
					MRALogger.logError("No match is found for routine name: " + specifiedRoutineNames.get(i));					
				}
			}			
			return expandedRoutineNames;
		} else {
			return specifiedRoutineNames;
		}
	}
		
	public static FileTerminal getOutputFile(CLIParams params) {
		if ((params.outputFile == null) || params.outputFile.isEmpty()) {
			MRALogger.logError("File " + params.outputFile + " is not found");
			return null;
		} else {
			return new FileTerminal(params.outputFile);
		}
	}
	
	public static Terminal getTerminal(CLIParams params) {
		if ((params.outputFile == null) || params.outputFile.isEmpty()) {
			return new SystemTerminal();
		} else {
			return new FileTerminal(params.outputFile);
		}
	}
	
	public static OutputFlags toOutputFlags(CLIParams params) {
		OutputFlags of = new OutputFlags();
		List<String> outputFlags = params.outputFlags;
		for (String outputFlag : outputFlags) {
			if (outputFlag.equals("ignorenodata")) {
				of.setSkipEmpty(true);
			}
			if (outputFlag.equals("showdetail")) {
				of.setShowDetail(true);
			}				
		}
		return of;
	}
	
}
