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

package com.pwc.us.rgi.m.tool.entry.fanout;

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.tool.OutputFlags;
import com.pwc.us.rgi.m.tool.ToolResult;
import com.pwc.us.rgi.output.Terminal;

public class EntryFanouts implements Serializable, ToolResult {
	private static final long serialVersionUID = 1L;

	private SortedSet<EntryId> fanoutEntries;

	public void add(EntryId fanout) {
		if (this.fanoutEntries == null) {
			this.fanoutEntries = new TreeSet<EntryId>();
		} 
		this.fanoutEntries.add(fanout);
	}
	
	public Set<EntryId> getFanouts() {
		return this.fanoutEntries;
	}
	
	@Override
	public void write(Terminal t, OutputFlags flags) throws IOException {
		Set<EntryId> r = this.getFanouts();
		if (r == null) {
			t.writeEOL("  --");				
		} else {
			for (EntryId f : r) {
				t.writeEOL("  " + f.toString2());
			}
		}
	}	
	
	@Override
	public boolean isEmpty() {
		return false;
	}
}
