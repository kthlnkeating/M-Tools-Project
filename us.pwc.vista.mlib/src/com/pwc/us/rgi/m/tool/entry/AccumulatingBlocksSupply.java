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

import java.util.HashMap;

import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.parsetree.data.Fanout;
import com.pwc.us.rgi.m.parsetree.visitor.BlockRecorder;
import com.pwc.us.rgi.m.parsetree.visitor.BlockRecorderFactory;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.struct.HierarchicalMap;

public class AccumulatingBlocksSupply<F extends Fanout, T extends BlockData<F>> extends BlocksSupply<F, T> {
	private ParseTreeSupply parseTreeSupply;
	private HashMap<String, HierarchicalMap<String, Block<F, T>>> blocks = new HashMap<String, HierarchicalMap<String, Block<F, T>>>();
	private BlockRecorderFactory<F, T> blockRecorder;
	
	public AccumulatingBlocksSupply(ParseTreeSupply parseTreeSupply, BlockRecorderFactory<F, T> brf) {
		this.parseTreeSupply = parseTreeSupply;
		this.blockRecorder = brf;
	}
	
	@Override
	public HierarchicalMap<String, Block<F, T>> getBlocks(String routineName) {
		if (! this.blocks.containsKey(routineName)) {			
			HierarchicalMap<String, Block<F, T>> result = null;
			Routine routine = this.parseTreeSupply.getParseTree(routineName);
			if (routine != null) {
				BlockRecorder<F, T> recorder = this.blockRecorder.getRecorder();
				routine.accept(recorder);
				result = recorder.getBlocks();
			}
			this.blocks.put(routineName, result);
			return result;
		}
		return this.blocks.get(routineName);
	}	
}
