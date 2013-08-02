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

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.parsetree.data.Fanout;
import com.pwc.us.rgi.struct.HierarchicalMap;

public abstract class BlocksSupply<F extends Fanout, T extends BlockData<F>> {
	public abstract HierarchicalMap<String, Block<F, T>> getBlocks(String routineName);
	
	public Block<F, T> getBlock(EntryId entryId) {
		String routineName = entryId.getRoutineName();
		HierarchicalMap<String, Block<F, T>> rbs = this.getBlocks(routineName);
		if (rbs != null) {
			String label = entryId.getLabelOrDefault();
			return rbs.getThruHierarchy(label);
		} 
		return null;		
	}
}
