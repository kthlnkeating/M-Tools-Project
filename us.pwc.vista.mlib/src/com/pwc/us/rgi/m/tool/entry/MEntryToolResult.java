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

package com.pwc.us.rgi.m.tool.entry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.tool.MToolError;
import com.pwc.us.rgi.m.tool.OutputFlags;
import com.pwc.us.rgi.m.tool.ToolResult;
import com.pwc.us.rgi.output.Terminal;

public class MEntryToolResult<T extends ToolResult> implements ToolResult {
	private List<EntryId> entries = new ArrayList<EntryId>();
	private List<T> results = new ArrayList<>();
	
	private Map<Integer, MToolError> errors;
	private Set<EntryId> missingEntries;
	
	private void putError(Integer location, MToolError error) {
		if (this.errors == null) {
			this.errors = new HashMap<Integer, MToolError>();	
		}
		this.errors.put(location, error);		
	}
	
	public void add(MEntryToolResult<T> results) {
		int m = this.entries.size();
		this.entries.addAll(results.entries);
		this.results.addAll(results.results);
		if (results.errors != null) {
			Set<Integer> errorLines = results.errors.keySet();
			for (Integer i : errorLines) {
				int newLocation = i + m;
				MToolError error = results.errors.get(i);
				this.putError(newLocation, error);
			}
		}
		if (results.missingEntries != null) {
			if (this.missingEntries == null) {
				this.missingEntries = results.missingEntries;
			} else {
				this.missingEntries.addAll(results.missingEntries);
			}
		}
	}
		
	public void add(EntryId id, T result) {
		this.entries.add(id);
		this.results.add(result);
	}
	
	public void addError(EntryId id, MToolError error) {
		int n = this.entries.size();
		this.entries.add(id);
		this.results.add(null);
		this.putError(n, error);
	}
	
	public void setMissingEntries(Set<EntryId> missingEntries) {
		this.missingEntries = missingEntries;
	}
	
	public List<EntryId> getEntries() {
		return this.entries;
	}
	
	public List<T> getResults() {
		return this.results;
	}
	
	public MToolError getError(int index) {
		if (this.errors == null) {
			return null;
		} else {
			return this.errors.get(index);
		}
	}
	
	public Set<EntryId> getMissingEntries() {
		return this.missingEntries;
	}
	
	public <U extends ToolResult, V extends ToolResult> MEntryToolResult<U> merge(MEntryToolResult<V> addl, SingleResultMerger<U, T, V> singleMerger) {
		MEntryToolResult<U> result = new MEntryToolResult<U>();	
		int n = this.entries.size();
		for (int i=0; i<n; ++i) {
			EntryId id = this.entries.get(i);
			T t = this.results.get(i);
			V v = addl.results.get(i);
			U u = singleMerger.merge(t, v);
			result.add(id, u);
		}
		return result;	
	}
	
	@Override
	public boolean isEmpty() {
		if (this.results != null) {
			for (T result : this.results) {
				if (! result.isEmpty()) {
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public void write(Terminal t, OutputFlags flags) throws IOException {
		List<EntryId> entries = this.getEntries();
		List<T> resultList = this.getResults();
		int n = entries.size();
		boolean found = false;
		for (int i=0; i<n; ++i) {
			T u = resultList.get(i);
			if (u != null) {
				if ((! flags.getSkipEmpty(false)) || (! u.isEmpty())) {
					found = true;
					t.writeEOL(" " + entries.get(i).toString2());		
					u.write(t, flags);
				} 		
			} else {
				t.writeEOL(" " + entries.get(i).toString2());
				MToolError error = this.getError(i);
				t.writeEOL("  ERROR: " + error.getMessage());
			}
		}
		if (! found) {
			t.writeEOL(flags.getEmptyMessage());
		}		
	}
 }
