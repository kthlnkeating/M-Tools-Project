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

package us.pwc.vista.eclipse.tools.core;

import java.util.List;

import org.eclipse.core.resources.IProject;


import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.ToolResult;
import com.pwc.us.rgi.m.tool.routine.MRoutineToolInput;
import com.pwc.us.rgi.m.tool.routine.RoutineToolParams;
import com.pwc.us.rgi.m.tool.routine.error.ErrorTool;

public class ErrorsToolWrap extends MToolWrap {
	private ErrorTool getTool(ParseTreeSupply pts) {
		RoutineToolParams p = new RoutineToolParams(pts);
		ErrorTool tool = new ErrorTool(p);
		return tool;
	}
	
	@Override
	public ToolResult getRoutinesResult(IProject project, ParseTreeSupply pts, List<String> routineNames) {
		ErrorTool tool = this.getTool(pts);
		MRoutineToolInput input = new MRoutineToolInput();
		input.addRoutines(routineNames);
		ToolResult result = tool.getResult(input);
		return result;
	}

	@Override
	public ToolResult getTagsResult(IProject project, ParseTreeSupply pts, String routineName, List<String> tags) {
		ErrorTool tool = this.getTool(pts);
		return tool.getResult(routineName, tags);
	}
}
