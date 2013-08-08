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

package com.pwc.us.rgi.m.tool.routine.error;

import java.util.List;

import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.ToolResult;
import com.pwc.us.rgi.m.tool.ToolResultCollection;
import com.pwc.us.rgi.m.tool.routine.CollectionAsToolResult;
import com.pwc.us.rgi.m.tool.routine.MRoutineToolInput;
import com.pwc.us.rgi.m.tool.routine.RoutineToolParams;

public class ErrorTool {
	private ParseTreeSupply pts;
	
	public ErrorTool(RoutineToolParams params) {
		this.pts = params.getParseTreeSupply();
	}
	
	public ToolResult getResult(EntryId entryUnderTest) {
		String routineName = entryUnderTest.getRoutineName();
		Routine routine = this.pts.getParseTree(routineName);
		ErrorRecorder fr = new ErrorRecorder();
		ErrorsByLabel rfs = fr.getErrors(routine);
		String tag = entryUnderTest.getLabelOrDefault();
		List<ErrorWithLineIndex> result = rfs.getResults(tag);
		return new CollectionAsToolResult<ErrorWithLineIndex>(entryUnderTest, result);
	}
	
	public ToolResultCollection getResult(String routineName, List<String> tags) {
		Routine routine = this.pts.getParseTree(routineName);
		ErrorRecorder fr = new ErrorRecorder();
		ErrorsByLabel rfs = fr.getErrors(routine);
		ToolResultCollection r = new ToolResultCollection();
		for (String tag : tags) {
			List<ErrorWithLineIndex> result = rfs.getResults(tag);
			EntryId eid = new EntryId(routineName, tag);
			ToolResult catr = new CollectionAsToolResult<ErrorWithLineIndex>(eid, result);
			r.add(catr);
		}
		return r;
	}
	
	public ErrorsByRoutine getResult(List<String> routineNames) {
		ErrorsByRoutine result = new ErrorsByRoutine();
		ErrorRecorder fr = new ErrorRecorder();
		for (String routineName : routineNames) {
			Routine routine = this.pts.getParseTree(routineName);
			ErrorsByLabel rfs = fr.getErrors(routine);
			result.put(routineName, rfs);
		}
		return result;
	}

	public ErrorsByRoutine getResult(MRoutineToolInput input) {
		List<String> routineNames = input.getRoutineNames(); 
		return this.getResult(routineNames);
	}
}
