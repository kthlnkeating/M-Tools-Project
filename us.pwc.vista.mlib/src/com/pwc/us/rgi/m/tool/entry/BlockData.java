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

import java.util.ArrayList;
import java.util.List;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.parsetree.data.Fanout;

public class BlockData<F extends Fanout> {
	private EntryId entryId;
	private List<F> fanouts = new ArrayList<F>();
	
	public BlockData(EntryId entryId) {
		this.entryId = entryId;
	}

	public EntryId getEntryId() {
		return this.entryId;
	}

	public void addFanout(F fanout) {
		this.fanouts.add(fanout);
	}	
	
	public List<F> getFanouts() {
		return this.fanouts;
	}
	
	public F getFanout(int index) {
		return this.fanouts.get(index);
	}
	
	public List<EntryId> getFanoutIds() {
		List<EntryId> result = new ArrayList<EntryId>();
		for (F fo : this.fanouts) {
			EntryId id = fo.getEntryId();
			result.add(id);
		}
		return result;
	}
}
