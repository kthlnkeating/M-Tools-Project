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

package com.pwc.us.rgi.vista.tools.utility;

import java.util.Collection;

import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.SourceCodeSupply;
import com.pwc.us.rgi.m.tool.SourceCodeToParseTreeAdapter;
import com.pwc.us.rgi.vista.repository.visitor.SerializedRoutineWriter;
import com.pwc.us.rgi.vista.tools.CLIParams;
import com.pwc.us.rgi.vista.tools.CLIParamsAdapter;
import com.pwc.us.rgi.vista.tools.Tool;

class CLIParseTreeSaveTool extends Tool {
	private SourceCodeSupply sourceCodeSupply;
	
	public CLIParseTreeSaveTool(CLIParams params) {
		super(params);
		this.sourceCodeSupply = CLIParamsAdapter.getSourceCodeSupply(params);
	}
	
	@Override
	public void run() {
		ParseTreeSupply pts = new SourceCodeToParseTreeAdapter(this.sourceCodeSupply);
		Collection<String> routineNames = this.sourceCodeSupply.getAllRoutineNames();
		for(String routineName : routineNames) {
			Routine routine = pts.getParseTree(routineName);
			SerializedRoutineWriter srw = new SerializedRoutineWriter(this.params.parseTreeDirectory);
			srw.visitRoutine(routine);
		}
	}
}
