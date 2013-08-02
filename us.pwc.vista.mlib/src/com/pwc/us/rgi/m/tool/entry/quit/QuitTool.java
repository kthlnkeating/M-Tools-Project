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

package com.pwc.us.rgi.m.tool.entry.quit;

import java.util.Set;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.parsetree.data.Fanout;
import com.pwc.us.rgi.m.parsetree.visitor.BlockRecorderFactory;
import com.pwc.us.rgi.m.tool.CommonToolParams;
import com.pwc.us.rgi.m.tool.entry.Block;
import com.pwc.us.rgi.m.tool.entry.CodeLocations;
import com.pwc.us.rgi.m.tool.entry.CodeLocationsAggregator;
import com.pwc.us.rgi.m.tool.entry.MEntryTool;
import com.pwc.us.rgi.struct.Filter;

public class QuitTool extends MEntryTool<CodeLocations, Fanout, CodeLocations> {
	private static class QuitRecorderFactory implements BlockRecorderFactory<Fanout, CodeLocations> {
		@Override
		public QuitRecorder getRecorder() {
			return new QuitRecorder();
		}
	}
	
	public QuitTool(CommonToolParams params) {
		super(params);
	}
	
	@Override
	protected QuitRecorderFactory getBlockRecorderFactory() {
		return new QuitRecorderFactory();
	}

	@Override
	protected CodeLocations getResult(Block<Fanout, CodeLocations> block, Filter<Fanout> filter, Set<EntryId> missingEntryIds) {
		CodeLocationsAggregator bcia = new CodeLocationsAggregator(block, blocksSupply);
		return bcia.get(filter, missingEntryIds);
	}
}
