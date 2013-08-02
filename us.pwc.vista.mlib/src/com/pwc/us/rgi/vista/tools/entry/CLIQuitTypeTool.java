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

import com.pwc.us.rgi.m.tool.CommonToolParams;
import com.pwc.us.rgi.m.tool.entry.MEntryToolInput;
import com.pwc.us.rgi.m.tool.entry.MEntryToolResult;
import com.pwc.us.rgi.m.tool.entry.quittype.QuitType;
import com.pwc.us.rgi.m.tool.entry.quittype.QuitTypeTool;
import com.pwc.us.rgi.vista.tools.CLIParams;

class CLIQuitTypeTool extends CLIEntryTool<QuitType> {	
	public CLIQuitTypeTool(CLIParams params) {
		super(params);		
	}
	
	@Override
	protected MEntryToolResult<QuitType> getResult(MEntryToolInput input) {
		CommonToolParams params = CLIETParamsAdapter.toCommonToolParams(this.params);
		QuitTypeTool a = new QuitTypeTool(params);
		return a.getResult(input);			
	}
}
