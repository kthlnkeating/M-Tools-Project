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

import com.pwc.us.rgi.m.tool.OutputFlags;
import com.pwc.us.rgi.m.tool.ToolResult;
import com.pwc.us.rgi.m.tool.entry.MEntryToolInput;
import com.pwc.us.rgi.m.tool.entry.MEntryToolResult;
import com.pwc.us.rgi.output.Terminal;
import com.pwc.us.rgi.vista.tools.CLIParams;
import com.pwc.us.rgi.vista.tools.CLIParamsAdapter;
import com.pwc.us.rgi.vista.tools.Tool;

abstract class CLIEntryTool<U extends ToolResult> extends Tool {		
	protected CLIParams params;
	
	public CLIEntryTool(CLIParams params) {
		super(params);
		this.params = params;
	}
	
	protected abstract MEntryToolResult<U> getResult(MEntryToolInput input);
	
	@Override
	public void run() throws IOException {
		Terminal t = CLIParamsAdapter.getTerminal(this.params);
		t.getTerminalFormatter().setTitleWidth(12);
		if (t != null) {
			MEntryToolInput input = CLIETParamsAdapter.getMEntryToolInput(this.params);
			if (input != null) {
				OutputFlags ofs = CLIParamsAdapter.toOutputFlags(this.params);
				MEntryToolResult<U> result = this.getResult(input);		
				result.write(t, ofs);
				t.stop();
			}
		}
	}
}

