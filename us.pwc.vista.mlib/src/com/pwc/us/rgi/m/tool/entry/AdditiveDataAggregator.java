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

import java.util.List;
import java.util.Set;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.parsetree.data.Fanout;
import com.pwc.us.rgi.m.parsetree.data.FanoutBlocks;
import com.pwc.us.rgi.struct.Filter;

public abstract class AdditiveDataAggregator<T, F extends Fanout, U extends BlockData<F>> {
	Block<F, U> block;
	BlocksSupply<F, U> supply;
	
	public AdditiveDataAggregator(Block<F, U> block, BlocksSupply<F, U> supply) {
		this.block = block;
		this.supply = supply;
	}
	
	protected abstract T getNewDataInstance(Block<F, U> block);
	
	protected abstract void updateData(T targetData, U fanoutData);
	
	public T get(Filter<Fanout> filter, Set<EntryId> missing) {
		FanoutBlocks<F, U> fanoutBlocks = this.block.getFanoutBlocks(this.supply, filter, missing);
		List<Block<F, U>> blocks = fanoutBlocks.getBlocks();
		T result = this.getNewDataInstance(this.block);
		for (Block<F, U> b : blocks) {
			this.updateData(result, b.getData());
		}
		return result;		
	}
}
