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

import java.util.Map;

import com.pwc.us.rgi.m.tool.CommonToolParams;
import com.pwc.us.rgi.m.tool.entry.CodeLocations;
import com.pwc.us.rgi.m.tool.entry.MEntryToolInput;
import com.pwc.us.rgi.m.tool.entry.MEntryToolResult;
import com.pwc.us.rgi.m.tool.entry.assumedvariables.AssumedVariablesToolParams;
import com.pwc.us.rgi.m.tool.entry.basiccodeinfo.BasicCodeInfoToolParams;
import com.pwc.us.rgi.m.tool.entry.fanout.EntryFanouts;
import com.pwc.us.rgi.m.tool.entry.fanout.FanoutTool;
import com.pwc.us.rgi.m.tool.entry.legacycodeinfo.LegacyCodeInfo;
import com.pwc.us.rgi.m.tool.entry.legacycodeinfo.LegacyCodeInfoTool;
import com.pwc.us.rgi.m.tool.entry.localassignment.LocalAssignmentTool;
import com.pwc.us.rgi.m.tool.entry.localassignment.LocalAssignmentToolParams;
import com.pwc.us.rgi.m.tool.entry.quit.QuitTool;
import com.pwc.us.rgi.vista.repository.RepositoryInfo;
import com.pwc.us.rgi.vista.tools.CLIParams;
import com.pwc.us.rgi.vista.tools.CLIParamsAdapter;
import com.pwc.us.rgi.vista.tools.Tool;
import com.pwc.us.rgi.vista.tools.Tools;

public class CLIEntryTools extends Tools {
	private static class EntryCodeInfoTool extends CLIEntryTool<LegacyCodeInfo> {	
		public EntryCodeInfoTool(CLIParams params) {
			super(params);
		}
		
		@Override
		public MEntryToolResult<LegacyCodeInfo> getResult(MEntryToolInput input) {
			RepositoryInfo ri = CLIParamsAdapter.getRepositoryInfo(this.params);
			AssumedVariablesToolParams p = CLIETParamsAdapter.toAssumedVariablesToolParams(this.params);
			BasicCodeInfoToolParams p2 = CLIETParamsAdapter.toBasicCodeInfoToolParams(this.params, ri);
			LegacyCodeInfoTool a = new LegacyCodeInfoTool(p, p2);
			return a.getResult(input);			
		}
	}

	private static class CLILocalAssignmentTool extends CLIEntryTool<CodeLocations> {	
		public CLILocalAssignmentTool(CLIParams params) {
			super(params);
		}
		
		@Override
		protected MEntryToolResult<CodeLocations> getResult(MEntryToolInput input) {
			LocalAssignmentToolParams params = CLIETParamsAdapter.toLocalAssignmentToolParams(this.params);
			LocalAssignmentTool a = new LocalAssignmentTool(params);
			return a.getResult(input);			
		}
	}

	private static class CLIQuitTool extends CLIEntryTool<CodeLocations> {	
		public CLIQuitTool(CLIParams params) {
			super(params);
		}
		
		@Override
		protected MEntryToolResult<CodeLocations> getResult(MEntryToolInput input) {
			CommonToolParams params = CLIETParamsAdapter.toCommonToolParams(this.params);
			QuitTool a = new QuitTool(params);
			return a.getResult(input);			
		}
	}

	private static class EntryFanoutTool extends CLIEntryTool<EntryFanouts> {	
		public EntryFanoutTool(CLIParams params) {
			super(params);
		}
		
		@Override
		protected MEntryToolResult<EntryFanouts> getResult(MEntryToolInput input) {
			CommonToolParams params = CLIETParamsAdapter.toCommonToolParams(this.params);
			FanoutTool a = new FanoutTool(params);
			return a.getResult(input);			
		}
	}

	public CLIEntryTools(String name) {
		super(name);
	}
	
	@Override
	protected void updateTools(Map<String, MemberFactory> tools) {
		tools.put("info", new MemberFactory() {				
			@Override
			public Tool getInstance(CLIParams params) {
				return new EntryCodeInfoTool(params);
			}
		});
		tools.put("assumedvar", new MemberFactory() {				
			@Override
			public Tool getInstance(CLIParams params) {
				return new CLIAssumedVariablesTool(params);
			}
		});
		tools.put("localassignment", new MemberFactory() {				
			@Override
			public Tool getInstance(CLIParams params) {
				return new CLILocalAssignmentTool(params);
			}
		});
		tools.put("quit", new MemberFactory() {				
			@Override
			public Tool getInstance(CLIParams params) {
				return new CLIQuitTool(params);
			}
		});
		tools.put("fanout", new MemberFactory() {				
			@Override
			public Tool getInstance(CLIParams params) {
				return new EntryFanoutTool(params);
			}
		});
		tools.put("fanin", new MemberFactory() {				
			@Override
			public Tool getInstance(CLIParams params) {
				return new CLIFaninTool(params);
			}
		});
		tools.put("quittype", new MemberFactory() {				
			@Override
			public Tool getInstance(CLIParams params) {
				return new CLIQuitTypeTool(params);
			}
		});		
	}
}
