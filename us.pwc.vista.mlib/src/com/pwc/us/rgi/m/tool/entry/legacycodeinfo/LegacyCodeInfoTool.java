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

package com.pwc.us.rgi.m.tool.entry.legacycodeinfo;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.tool.entry.MEntryToolInput;
import com.pwc.us.rgi.m.tool.entry.MEntryToolResult;
import com.pwc.us.rgi.m.tool.entry.assumedvariables.AssumedVariables;
import com.pwc.us.rgi.m.tool.entry.assumedvariables.AssumedVariablesTool;
import com.pwc.us.rgi.m.tool.entry.assumedvariables.AssumedVariablesToolParams;
import com.pwc.us.rgi.m.tool.entry.basiccodeinfo.BasicCodeInfoTR;
import com.pwc.us.rgi.m.tool.entry.basiccodeinfo.BasicCodeInfoTool;
import com.pwc.us.rgi.m.tool.entry.basiccodeinfo.BasicCodeInfoToolParams;

public class LegacyCodeInfoTool {
	private AssumedVariablesTool avt;
	private BasicCodeInfoTool bcit;
	
	public LegacyCodeInfoTool(AssumedVariablesToolParams params, BasicCodeInfoToolParams params2) {
		this.avt = new AssumedVariablesTool(params);
		this.bcit = new BasicCodeInfoTool(params2);
	}
	
	public LegacyCodeInfo getResult(EntryId id) {
		AssumedVariables avr = this.avt.getResult(id);
		BasicCodeInfoTR bcir = this.bcit.getResult(id);
		LCIResultMerger merger = new LCIResultMerger();
		return merger.merge(avr, bcir);
	}

	public MEntryToolResult<LegacyCodeInfo> getResult(MEntryToolInput input) {
		MEntryToolResult<AssumedVariables> avr = this.avt.getResult(input);
		MEntryToolResult<BasicCodeInfoTR> bcir = this.bcit.getResult(input);
		LCIResultMerger merger = new LCIResultMerger();
		MEntryToolResult<LegacyCodeInfo> result = avr.merge(bcir, merger);
		return result;
	}
}
