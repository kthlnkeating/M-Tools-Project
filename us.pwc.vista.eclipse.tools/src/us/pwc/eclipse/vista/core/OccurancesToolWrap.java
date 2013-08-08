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

package us.pwc.eclipse.vista.core;

import java.util.List;

import org.eclipse.core.resources.IProject;

import us.pwc.eclipse.vista.propertyPage.OccuranceTypesHandler;

import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.ResultsByRoutine;
import com.pwc.us.rgi.m.tool.ToolResult;
import com.pwc.us.rgi.m.tool.routine.MRoutineToolInput;
import com.pwc.us.rgi.m.tool.routine.occurance.Occurance;
import com.pwc.us.rgi.m.tool.routine.occurance.OccuranceTool;
import com.pwc.us.rgi.m.tool.routine.occurance.OccuranceToolParams;
import com.pwc.us.rgi.m.tool.routine.occurance.OccuranceType;

public class OccurancesToolWrap extends MToolWrap {
	private OccuranceTool getTool(IProject project, ParseTreeSupply pts) {
		OccuranceToolParams p = new OccuranceToolParams(pts);
		String[] types = OccuranceTypesHandler.getTypesFromStore(project);
		if (types != null) {
			p.clearTypes();
			for (String rawType : types) {
				String type = rawType.toUpperCase();
				type = type.replace(' ', '_');
				OccuranceType ot = OccuranceType.valueOf(type);
				p.addType(ot);
			}
		}		
		OccuranceTool tool = new OccuranceTool(p);
		return tool;
	}
	
	@Override
	public ResultsByRoutine<Occurance, List<Occurance>> getRoutinesResult(IProject project, ParseTreeSupply pts, List<String> routineNames) {
		OccuranceTool tool = this.getTool(project, pts);
		MRoutineToolInput input = new MRoutineToolInput();
		input.addRoutines(routineNames);
		ResultsByRoutine<Occurance, List<Occurance>> result = tool.getResult(input);
		return result;
	}

	@Override
	public ToolResult getTagsResult(IProject project, ParseTreeSupply pts, String routineName, List<String> tags) {
		OccuranceTool tool = this.getTool(project, pts);
		return tool.getResult(routineName, tags);
	}
}
