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

package com.pwc.us.rgi.m.tool.routine.fanin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.tool.EntryIdsByRoutine;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.ResultsByLabel;
import com.pwc.us.rgi.m.tool.routine.MRoutineToolInput;
import com.pwc.us.rgi.m.tool.routine.RoutineNameFilter;
import com.pwc.us.rgi.m.tool.routine.RoutineToolParams;
import com.pwc.us.rgi.m.tool.routine.fanout.FanoutTool;
import com.pwc.us.rgi.struct.Filter;

public class FaninTool {
	private RoutineToolParams params;
	
	public FaninTool(RoutineToolParams params) {
		this.params = params;
	}

	private RoutineToolParams getFanoutParams(MRoutineToolInput input) {
		List<String> routineNames = input.getRoutineNames(); 		
		Filter<EntryId> fanoutFilter = new RoutineNameFilter(routineNames);
		ParseTreeSupply pts = this.params.getParseTreeSupply();
		RoutineToolParams fanoutParams = new RoutineToolParams(pts);
		fanoutParams.setResultRoutineFilter(fanoutFilter);
		return fanoutParams;		
	}
	
	private MRoutineToolInput getFanoutInput(MRoutineToolInput input)  {
		Filter<EntryId> resultFilter = this.params.getResultRoutineFilter();
		if (resultFilter != null) {
			ParseTreeSupply pts = this.params.getParseTreeSupply();
			Collection<String> allRoutineNames = pts.getAllRoutineNames();
			List<String> inputRoutineNames = new ArrayList<String>();
			for (String routineName : allRoutineNames) {
				if (resultFilter.isValid(new EntryId(routineName, null))) {
					inputRoutineNames.add(routineName);
				}				
			}
			input = new MRoutineToolInput();
			input.addRoutines(inputRoutineNames);
		}
		return input;		
	}
	
	private EntryIdsByRoutine getFanoutResult(MRoutineToolInput input) {
		RoutineToolParams fanoutParams = this.getFanoutParams(input);
		FanoutTool fanoutTool = new FanoutTool(fanoutParams);
		MRoutineToolInput fanoutInput = this.getFanoutInput(input);
		EntryIdsByRoutine fanoutResult = fanoutTool.getResult(fanoutInput);
		return fanoutResult;
	}
	
	private EntryIdsByRoutine convertFanoutResult(EntryIdsByRoutine fanoutResult) {
		EntryIdsByRoutine result = new EntryIdsByRoutine();
		Set<String> resultRoutineNames = fanoutResult.getRoutineNames();
		for (String routineName : resultRoutineNames) {
			ResultsByLabel<EntryId, Set<EntryId>> rel = fanoutResult.getResults(routineName);
			Set<String> routineLabels = rel.getLabels();
			for (String label : routineLabels) {
				Set<EntryId> fanouts = rel.getResults(label);
				for (EntryId id : fanouts) {
					result.addResult(id, new EntryId(routineName, label));	
				}
			}			
		}
		return result;		
	}
	
	private void putEmptyLabels(EntryIdsByRoutine result, List<String> inputRoutineNames) {
		ParseTreeSupply pts = this.params.getParseTreeSupply();
		for (String name : inputRoutineNames) {
			Routine r = pts.getParseTree(name);
			List<String> tags = r.getTopTags();
			for (String tag : tags) {
				result.getResultsAddingWhenNone(name, tag);					
			}
		}		
	}
	
	public EntryIdsByRoutine getResult(MRoutineToolInput input) {
		EntryIdsByRoutine fanoutResult = this.getFanoutResult(input);		
		EntryIdsByRoutine result = this.convertFanoutResult(fanoutResult);
		List<String> inputRoutineNames = input.getRoutineNames();
		this.putEmptyLabels(result, inputRoutineNames);
		return result;
	}
}
