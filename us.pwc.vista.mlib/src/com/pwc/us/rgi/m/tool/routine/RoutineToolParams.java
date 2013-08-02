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

package com.pwc.us.rgi.m.tool.routine;

import java.util.List;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.struct.Filter;

public class RoutineToolParams {
	private static class RoutineRegexFilter implements Filter<EntryId> {
		private List<String> routineRegexes;
		
		public RoutineRegexFilter(List<String> routineRegexes) {
			this.routineRegexes = routineRegexes;
		}
		
		@Override
		public boolean isValid(EntryId id) {
			String routineName = id.getRoutineName();
			if (routineName != null) {
				for (String routineRegex : routineRegexes) {
					if (routineName.matches(routineRegex)) return true;
				}
				return false;
			} else {
				return true;
			}
		}
	}

	private ParseTreeSupply parseTreeSupply;
	private Filter<EntryId> resultFilter;
	
	public RoutineToolParams(ParseTreeSupply parseTreeSupply) {
		this.parseTreeSupply = parseTreeSupply;		
	}
	
	public ParseTreeSupply getParseTreeSupply() {
		return this.parseTreeSupply;
	}
	
	public void setResultRoutineFilter(List<String> resultRoutineRegex) {
		if ((resultRoutineRegex == null) || (resultRoutineRegex.size() == 0)) {
			this.resultFilter = null;
		} else {
			this.resultFilter = new RoutineRegexFilter(resultRoutineRegex);
		}
	}
	
	public void setResultRoutineFilter(Filter<EntryId> filter) {
		this.resultFilter = filter;
	}
	
	public Filter<EntryId> getResultRoutineFilter() {
		return this.resultFilter;
	}
}
