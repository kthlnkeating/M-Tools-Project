//---------------------------------------------------------------------------
//Copyright 2013 PwC
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//---------------------------------------------------------------------------

package com.pwc.us.rgi.m.tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.output.Terminal;

public class EntryIdListResult implements ToolResult {
	private List<EntryId> toolResults = new ArrayList<EntryId>();

	public EntryIdListResult() {
		this.toolResults = new ArrayList<EntryId>();	
	}
	
	public EntryIdListResult(Collection<EntryId> toolResults) {
		this.toolResults = new ArrayList<EntryId>(toolResults);	
	}

	public void add(EntryId toolResult) {
		this.toolResults.add(toolResult);
	}
	
	public List<EntryId> getEntryIdList() {
		return this.toolResults;
	}
	
	@Override
	public boolean isEmpty() {
		return this.toolResults.isEmpty();
	}

	@Override
	public void write(Terminal t, OutputFlags flags) throws IOException {
		if (this.toolResults.isEmpty()) {
			t.writeEOL("None found");
			return;
		}
	
		for (EntryId r : this.toolResults) {
			r.write(t, flags);
		}		
	}

}
