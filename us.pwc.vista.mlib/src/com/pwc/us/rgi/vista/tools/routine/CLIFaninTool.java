//---------------------------------------------------------------------------
//Copyright 2013 PwC
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//---------------------------------------------------------------------------

package com.pwc.us.rgi.vista.tools.routine;

import java.io.IOException;

import com.pwc.us.rgi.m.tool.EntryIdsByRoutine;
import com.pwc.us.rgi.m.tool.routine.MRoutineToolInput;
import com.pwc.us.rgi.m.tool.routine.RoutineToolParams;
import com.pwc.us.rgi.m.tool.routine.fanin.FaninTool;
import com.pwc.us.rgi.output.Terminal;
import com.pwc.us.rgi.vista.tools.CLIParams;
import com.pwc.us.rgi.vista.tools.CLIParamsAdapter;

class CLIFaninTool extends CLIRoutineTool {		
	public CLIFaninTool(CLIParams params) {
		super(params);
	}
	
	@Override
	public void run() throws IOException {
		Terminal t = CLIParamsAdapter.getTerminal(this.params);
		if (t != null) {
			RoutineToolParams p = CLIRTParamsAdapter.toMRoutineToolParams(this.params);	
			FaninTool tool = new FaninTool(p);
			MRoutineToolInput input = CLIRTParamsAdapter.toMRoutineInput(this.params);
			EntryIdsByRoutine result = tool.getResult(input);
			this.write(result, t);
		}
	}
}
