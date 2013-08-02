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

package com.pwc.us.rgi.m.tool.entry.localassignment;

import java.util.Set;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.parsetree.data.Fanout;
import com.pwc.us.rgi.m.parsetree.visitor.BlockRecorderFactory;
import com.pwc.us.rgi.m.tool.entry.Block;
import com.pwc.us.rgi.m.tool.entry.CodeLocations;
import com.pwc.us.rgi.m.tool.entry.CodeLocationsAggregator;
import com.pwc.us.rgi.m.tool.entry.MEntryTool;
import com.pwc.us.rgi.struct.Filter;

public class LocalAssignmentTool extends MEntryTool<CodeLocations, Fanout, CodeLocations> {
	private class EntryLocalAssignmentRecorderFactory implements BlockRecorderFactory<Fanout, CodeLocations> {
		@Override
		public LocalAssignmentRecorder getRecorder() {
			return new LocalAssignmentRecorder(LocalAssignmentTool.this.localsUnderTest);
		}
	}
	
	private Set<String> localsUnderTest;
	
	public LocalAssignmentTool(LocalAssignmentToolParams params) {
		super(params);
		this.localsUnderTest = params.getLocals();
	}
	
	@Override
	protected EntryLocalAssignmentRecorderFactory getBlockRecorderFactory() {
		return this.new EntryLocalAssignmentRecorderFactory();
	}

	@Override
	protected CodeLocations getResult(Block<Fanout, CodeLocations> block, Filter<Fanout> filter, Set<EntryId> missingEntryIds) {
		CodeLocationsAggregator bcia = new CodeLocationsAggregator(block, blocksSupply);
		return bcia.get(filter, missingEntryIds);
	}
}
