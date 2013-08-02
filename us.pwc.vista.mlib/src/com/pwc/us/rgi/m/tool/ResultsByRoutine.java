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

package com.pwc.us.rgi.m.tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.output.Terminal;

public abstract class ResultsByRoutine<T extends ToolResultPiece, U extends Collection<T>> implements ToolResult {
	private Map<String, ResultsByLabel<T, U>> map = new HashMap<String, ResultsByLabel<T, U>>();

	public void put(String routineName, ResultsByLabel<T, U> results) {
		this.map.put(routineName, results);
	}
	
	public Set<String> getRoutineNames() {
		return this.map.keySet();
	}
	
	public ResultsByLabel<T, U> getResults(String routineName) {
		return this.map.get(routineName);
	}
	
	public abstract ResultsByLabel<T, U> getNewResultsInstance();
	
	public U getResultsAddingWhenNone(String routineName, String label) {
		ResultsByLabel<T, U> results = this.map.get(routineName);
		if (results == null) {
			results = this.getNewResultsInstance();
			this.put(routineName, results);
		}
		return results.getResultsAddingWhenNone(label);		
	}
	
	public void addResult(EntryId entryId, T result){
		String routineName = entryId.getRoutineName();
		String label = entryId.getLabelOrDefault();
		U results = this.getResultsAddingWhenNone(routineName, label);
		results.add(result);
	}
	
	public List<EntryId> getEmptyEntries() {
		List<EntryId> emptyEntries = new ArrayList<EntryId>();
		Set<String> routineNames = this.map.keySet();
		for (String routineName : routineNames) {
			ResultsByLabel<T, U> results = this.map.get(routineName);
			List<String> emptyLabels = results.getLabelsWithEmptyResults();
			for (String emptyLabel : emptyLabels) {
				emptyEntries.add(new EntryId(routineName, emptyLabel));
			}
		}
		return emptyEntries;
	}
	
	@Override
	public boolean isEmpty() {
		Set<String> routineNames = this.map.keySet();
		for (String routineName : routineNames) {
			ResultsByLabel<T, U> results = this.map.get(routineName);
			if (! results.isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	public List<T> getAllFlattened() {
		List<T> allFlattened = new ArrayList<T>();
		Set<String> routineNames = this.map.keySet();
		for (String routineName : routineNames) {
			ResultsByLabel<T, U> results = this.map.get(routineName);
			List<T> allFlattenedLabel = results.getAllFlattened();
			allFlattened.addAll(allFlattenedLabel);
		}
		return allFlattened;
	}
	
	@Override
	public void write(Terminal t, OutputFlags flags) throws IOException {
		Set<String> rns = this.getRoutineNames();
		boolean found = false;
		for (String rn : rns) {
			ResultsByLabel<T, U> rsbl = this.getResults(rn);
			Set<String> labels = rsbl.getLabels();
			for (String label : labels) {
				U rs = rsbl.getResults(label);
				if ((rs == null) || (rs.size() == 0)) {
					if (! flags.getSkipEmpty(true)) {
						t.getTerminalFormatter().pushIndent();
						t.writeIndented(label + "^" + rn);	
						t.getTerminalFormatter().pushIndent();
						t.writeIndented("--");
						t.getTerminalFormatter().pullIndent();
						t.getTerminalFormatter().pullIndent();
						found = true;
					}
				} else {
					t.getTerminalFormatter().pushIndent();
					t.writeIndented(label + "^" + rn);	
					t.getTerminalFormatter().pushIndent();					
					for (T r : rs) {
						r.write(t, new EntryId(rn, label), flags);
					}
					t.getTerminalFormatter().pullIndent();
					t.getTerminalFormatter().pullIndent();
					found = true;
				}
			}		
		}
		if (! found) {
			t.writeEOL("None found.");
		}		
	}
}
