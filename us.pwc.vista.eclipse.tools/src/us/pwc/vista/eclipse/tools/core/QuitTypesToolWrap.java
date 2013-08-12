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


import com.pwc.us.rgi.m.tool.CommonToolParams;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.entry.MEntryToolResult;
import com.pwc.us.rgi.m.tool.entry.RecursionDepth;
import com.pwc.us.rgi.m.tool.entry.quittype.QuitType;
import com.pwc.us.rgi.m.tool.entry.quittype.QuitTypeTool;

public class QuitTypesToolWrap extends MToolWrap {
	private QuitTypeTool getTool(ParseTreeSupply pts) {
		CommonToolParams params = new CommonToolParams(pts);
		params.getRecursionSpecification().setDepth(RecursionDepth.ALL);
		return new QuitTypeTool(params);
	}
	
	@Override
	public MEntryToolResult<QuitType> getRoutinesResult(IProject project, ParseTreeSupply pts, List<String> routineNames) {
		QuitTypeTool tool = this.getTool(pts);
		MEntryToolResult<QuitType> result = tool.getResultForRoutines(routineNames);
		return result;
	}

	@Override
	public MEntryToolResult<QuitType> getTagsResult(IProject project, ParseTreeSupply pts, String routineName, List<String> tags) {
		QuitTypeTool tool = this.getTool(pts);
		return tool.getResult(routineName, tags);
	}
}
