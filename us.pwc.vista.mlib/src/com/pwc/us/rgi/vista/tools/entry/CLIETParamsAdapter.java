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

package com.pwc.us.rgi.vista.tools.entry;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.tool.CommonToolParams;
import com.pwc.us.rgi.m.tool.NamespaceFilter;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.entry.MEntryToolInput;
import com.pwc.us.rgi.m.tool.entry.RecursionDepth;
import com.pwc.us.rgi.m.tool.entry.RecursionSpecification;
import com.pwc.us.rgi.m.tool.entry.assumedvariables.AssumedVariablesToolParams;
import com.pwc.us.rgi.m.tool.entry.basiccodeinfo.BasicCodeInfoToolParams;
import com.pwc.us.rgi.m.tool.entry.localassignment.LocalAssignmentToolParams;
import com.pwc.us.rgi.vista.repository.RepositoryInfo;
import com.pwc.us.rgi.vista.tools.CLIParams;
import com.pwc.us.rgi.vista.tools.CLIParamsAdapter;
import com.pwc.us.rgi.vista.tools.MRALogger;

class CLIETParamsAdapter {
	public static RecursionSpecification toRecursionSpecification(CLIParams params) {
		String rdName = params.recursionDepth;
		if ((rdName == null) || (rdName.isEmpty())) {
			rdName = "label";
		}
		RecursionSpecification rs = new RecursionSpecification();
		try {
			RecursionDepth d = RecursionDepth.get(rdName);
			rs.setDepth(d);
			NamespaceFilter filter = CLIParamsAdapter.getNamespaceFilter(params.includeNamespaces, params.excludeNamespaces, params.excludeExceptionNamespaces);
			rs.setNamespaceFilter(filter);
			return rs;
		} catch (Exception ex) {
			return null;
		}			
	}
	
	public static CommonToolParams toCommonToolParams(CLIParams params) {
		ParseTreeSupply pts = CLIParamsAdapter.getParseTreeSupply(params);
		CommonToolParams result = new CommonToolParams(pts);
		RecursionSpecification rs = toRecursionSpecification(params);
		result.setRecursionSpecification(rs);	
		return result;				
	}

	public static LocalAssignmentToolParams toLocalAssignmentToolParams(CLIParams params) {
		ParseTreeSupply pts = CLIParamsAdapter.getParseTreeSupply(params);
		LocalAssignmentToolParams result = new LocalAssignmentToolParams(pts);
		if (params.includes.size() > 0) {
			result.addLocals(params.includes);
		}
		RecursionSpecification rs = toRecursionSpecification(params);
		result.setRecursionSpecification(rs);	
		return result;		
	}

	public static AssumedVariablesToolParams toAssumedVariablesToolParams(CLIParams params) {
		ParseTreeSupply pts = CLIParamsAdapter.getParseTreeSupply(params);
		AssumedVariablesToolParams result = new AssumedVariablesToolParams(pts);
		if (params.excludes.size() > 0) {
			result.addExpected(params.excludes);
		}
		RecursionSpecification rs = toRecursionSpecification(params);
		result.setRecursionSpecification(rs);	
		return result;		
	}

	public static BasicCodeInfoToolParams toBasicCodeInfoToolParams(CLIParams params, RepositoryInfo repositoryInfo) {
		ParseTreeSupply pts = CLIParamsAdapter.getParseTreeSupply(params);
		BasicCodeInfoToolParams result = new BasicCodeInfoToolParams(pts, repositoryInfo);

		RecursionSpecification rs = toRecursionSpecification(params);
		result.setRecursionSpecification(rs);	
		return result;		
	}
	
	protected static List<String> getEntriesInString(CLIParams params) {
		if (params.inputFile != null) {
			try {
				Path path = Paths.get(params.inputFile);
				Scanner scanner = new Scanner(path);
				List<String> result = new ArrayList<String>();
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					result.add(line);
				}		
				scanner.close();
				return result;
			} catch (IOException e) {
				MRALogger.logError("Unable to open file " + params.inputFile);
				return null;
			}
		} else {
			return params.entries;
		}			
	}
	
	public static List<EntryId> getEntries(CLIParams params) {
		List<String> entriesInString = getEntriesInString(params);
		if (entriesInString != null) {
			List<EntryId> result = new ArrayList<EntryId>(entriesInString.size());
			for (String entryInString : entriesInString) {
				EntryId entryId = EntryId.getInstance(entryInString);
				result.add(entryId);
			}
			return result;
		}
		return null;
	}
	
	public static MEntryToolInput getMEntryToolInput(CLIParams params) {
		MEntryToolInput input = new MEntryToolInput();
		input.addRoutines(CLIParamsAdapter.getCLIRoutines(params));
		List<EntryId> entryIds = getEntries(params);
		input.addEntries(entryIds);
		return input;
	}
}
