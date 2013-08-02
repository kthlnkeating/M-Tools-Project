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
import java.util.List;

import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.ResultsByRoutine;
import com.pwc.us.rgi.m.tool.routine.MRoutineToolInput;
import com.pwc.us.rgi.m.tool.routine.occurance.Occurance;
import com.pwc.us.rgi.m.tool.routine.occurance.OccuranceTool;
import com.pwc.us.rgi.m.tool.routine.occurance.OccuranceToolParams;
import com.pwc.us.rgi.m.tool.routine.occurance.OccuranceType;
import com.pwc.us.rgi.output.Terminal;
import com.pwc.us.rgi.vista.tools.CLIParams;
import com.pwc.us.rgi.vista.tools.CLIParamsAdapter;

public class CLIOccuranceTool extends CLIRoutineTool {	
	public CLIOccuranceTool(CLIParams params) {
		super(params);
	}
	
	@Override
	public void run() throws IOException {
		Terminal t = CLIParamsAdapter.getTerminal(this.params);
		if (t != null) {
			ParseTreeSupply pts = CLIParamsAdapter.getParseTreeSupply(this.params);
			OccuranceToolParams p = new OccuranceToolParams(pts);
			if (this.params.occuranceType.size() > 0) {
				p.clearTypes();
				for (String otv : this.params.occuranceType) {
					OccuranceType ot = OccuranceType.valueOf(otv);
					p.addType(ot);
				}
			}
			OccuranceTool tool = new OccuranceTool(p);
			MRoutineToolInput input = CLIRTParamsAdapter.toMRoutineInput(this.params);
			ResultsByRoutine<Occurance, List<Occurance>> result = tool.getResult(input);
			this.write(result, t);
		}
	}
}
