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

import com.pwc.us.rgi.m.tool.routine.MRoutineToolInput;
import com.pwc.us.rgi.m.tool.routine.RoutineToolParams;
import com.pwc.us.rgi.m.tool.routine.error.ErrorTool;
import com.pwc.us.rgi.m.tool.routine.error.ErrorsByRoutine;
import com.pwc.us.rgi.output.Terminal;
import com.pwc.us.rgi.vista.tools.CLIParams;
import com.pwc.us.rgi.vista.tools.CLIParamsAdapter;

public class CLIErrorTool extends CLIRoutineTool {
	public CLIErrorTool(CLIParams params) {
		super(params);
	}
	
	@Override
	public void run() throws IOException {
		Terminal t = CLIParamsAdapter.getTerminal(this.params);
		if (t != null) {
			RoutineToolParams p = CLIRTParamsAdapter.toMRoutineToolParams(this.params);	
			ErrorTool tool = new ErrorTool(p);
			MRoutineToolInput input = CLIRTParamsAdapter.toMRoutineInput(this.params);
			ErrorsByRoutine result = tool.getResult(input);
			this.write(result, t);
		}
	}
}
