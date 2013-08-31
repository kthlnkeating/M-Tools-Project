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
import com.pwc.us.rgi.m.tool.OutputFlags;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.entry.CodeLocations;
import com.pwc.us.rgi.m.tool.entry.MEntryToolResult;
import com.pwc.us.rgi.m.tool.entry.RecursionDepth;
import com.pwc.us.rgi.m.tool.entry.quit.QuitTool;

public class QuitLocationsToolWrap extends MToolWrap {
	@Override
	protected OutputFlags getOutputFlags() {
		OutputFlags fs = new OutputFlags();
		fs.setSkipEmpty(true);
		fs.setEmptyMessage("None found.");
		return fs;
	}
	
	private QuitTool getTool(ParseTreeSupply pts) {
		CommonToolParams params = new CommonToolParams(pts);
		params.getRecursionSpecification().setDepth(RecursionDepth.ALL);
		return new QuitTool(params);
	}
	
	@Override
	public MEntryToolResult<CodeLocations> getRoutinesResult(IProject project, ParseTreeSupply pts, List<String> routineNames) {
		QuitTool tool = this.getTool(pts);
		MEntryToolResult<CodeLocations> result = tool.getResultForRoutines(routineNames);
		return result;
	}

	@Override
	public MEntryToolResult<CodeLocations> getTagsResult(IProject project, ParseTreeSupply pts, String routineName, List<String> tags) {
		QuitTool tool = this.getTool(pts);
		return tool.getResult(routineName, tags);
	}
}
