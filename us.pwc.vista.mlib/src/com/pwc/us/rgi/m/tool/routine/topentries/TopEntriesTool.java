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

package com.pwc.us.rgi.m.tool.routine.topentries;

import java.util.Collections;
import java.util.List;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.tool.EntryIdsByRoutine;
import com.pwc.us.rgi.m.tool.EntryIdListResult;
import com.pwc.us.rgi.m.tool.routine.fanin.FaninTool;
import com.pwc.us.rgi.m.tool.routine.MRoutineToolInput;
import com.pwc.us.rgi.m.tool.routine.RoutineToolParams;

public class TopEntriesTool {
	private FaninTool faninTool;
	
	public TopEntriesTool(RoutineToolParams params) {
		this.faninTool = new FaninTool(params);
	}

	public EntryIdListResult getResult(MRoutineToolInput input) {
		EntryIdsByRoutine allLinks = this.faninTool.getResult(input);
		List<EntryId> result = allLinks.getEmptyEntries();
		Collections.sort(result);
		return new EntryIdListResult(result);
	}
}
