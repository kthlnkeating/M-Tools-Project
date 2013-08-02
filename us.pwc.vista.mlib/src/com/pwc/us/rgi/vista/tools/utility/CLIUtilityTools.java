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

package com.pwc.us.rgi.vista.tools.utility;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import com.pwc.us.rgi.struct.Filter;
import com.pwc.us.rgi.util.IOUtil;
import com.pwc.us.rgi.vista.repository.RepositoryInfo;
import com.pwc.us.rgi.vista.tools.CLIParams;
import com.pwc.us.rgi.vista.tools.MRALogger;
import com.pwc.us.rgi.vista.tools.Tool;
import com.pwc.us.rgi.vista.tools.Tools;

public class CLIUtilityTools extends Tools {
	private static class FlattenVistAFOIA extends Tool {		
		public FlattenVistAFOIA(CLIParams params) {
			super(params);
		}
		
		@Override
		public void run() {
			String outputFile = this.params.outputFile;
			if ((outputFile == null) || outputFile.isEmpty()) {
				MRALogger.logError("Directory " + outputFile + " is not found");
				return;
			}
			String root = RepositoryInfo.getLocationWithLog();
			if ((root == null) || (root.isEmpty())) {
				return;
			}
			Filter<Path> filter = new Filter<Path>() {
				@Override
				public boolean isValid(Path input) {
					String p = input.toString();
					return p.endsWith(".m");
				}
			};
			
			try {
				IOUtil.flattenDirectory(root, outputFile, filter);
			} catch (IOException e) {
				MRALogger.logError("Failed", e);
			}
		}			
	}
	
	public CLIUtilityTools(String name) {
		super(name);
	}

	@Override
	protected void updateTools(Map<String, MemberFactory> tools) {
		tools.put("flattenfoia", new MemberFactory() {				
			@Override
			public Tool getInstance(CLIParams params) {
				return new FlattenVistAFOIA(params);
			}
		});
		tools.put("parsetreesave", new MemberFactory() {				
			@Override
			public Tool getInstance(CLIParams params) {
				return new CLIParseTreeSaveTool(params);
			}
		});
	}
}
