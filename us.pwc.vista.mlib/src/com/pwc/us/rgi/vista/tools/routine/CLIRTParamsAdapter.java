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

package com.pwc.us.rgi.vista.tools.routine;

import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.routine.MRoutineToolInput;
import com.pwc.us.rgi.m.tool.routine.RoutineToolParams;
import com.pwc.us.rgi.vista.tools.CLIParams;
import com.pwc.us.rgi.vista.tools.CLIParamsAdapter;

public class CLIRTParamsAdapter {
	public static RoutineToolParams toMRoutineToolParams(CLIParams params) {
		ParseTreeSupply pts = CLIParamsAdapter.getParseTreeSupply(params);
		RoutineToolParams rtparams = new RoutineToolParams(pts);
		rtparams.setResultRoutineFilter(params.resultRoutines);
		return rtparams;
	}
		
	public static MRoutineToolInput toMRoutineInput(CLIParams params) {
		MRoutineToolInput input = new MRoutineToolInput();
		input.addRoutines(CLIParamsAdapter.getCLIRoutines(params));
		return input;
	}
}
