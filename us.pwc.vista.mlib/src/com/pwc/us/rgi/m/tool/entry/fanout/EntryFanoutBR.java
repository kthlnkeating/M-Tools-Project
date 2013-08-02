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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.parsetree.data.Fanout;
import com.pwc.us.rgi.m.parsetree.data.FanoutType;
import com.pwc.us.rgi.m.parsetree.visitor.BlockRecorder;
import com.pwc.us.rgi.m.tool.EntryIdsByLabel;
import com.pwc.us.rgi.m.tool.entry.Block;
import com.pwc.us.rgi.m.tool.entry.BlockData;
import com.pwc.us.rgi.struct.HierarchicalMap;

public class EntryFanoutBR extends BlockRecorder<Fanout, BlockData<Fanout>> {
	@Override
	protected BlockData<Fanout> getNewBlockData(EntryId entryId, String[] params) {
		return new BlockData<Fanout>(entryId);
	}
	
	private Set<EntryId> getBlockFanouts(Block<Fanout, BlockData<Fanout>> b, String routineName, HierarchicalMap<String, Block<Fanout, BlockData<Fanout>>> siblings, Set<String> parentAlready) {
		Set<EntryId> r = new HashSet<EntryId>();
		List<EntryId> fs = b.getData().getFanoutIds();
		for (EntryId f : fs) {
			String rname = f.getRoutineName();
			if ((rname == null) || rname.equals(routineName)) {
				String label = f.getLabelOrDefault();
				Block<Fanout, BlockData<Fanout>> cb = b.getCallableBlocks().getChildBlock(label);
				if (cb != null) {
					Set<String> already = new HashSet<String>();
					already.add(label);
					Set<EntryId> cr = this.getBlockFanouts(cb, routineName, cb.getCallableBlocks(), already); 
					r.addAll(cr);				
					continue;
				} 
				if ((siblings != null)) {
					Block<Fanout, BlockData<Fanout>> sb = siblings.get(label);
					if ((sb != null)  && (! parentAlready.contains(label))) {
						parentAlready.add(label);
						Set<EntryId> sr = this.getBlockFanouts(sb, routineName, siblings, parentAlready); 
						r.addAll(sr);				
						continue;
					}
				}
			}
			r.add(f);
		}		
		return r;
	}
 	
	public EntryIdsByLabel getResults(Routine routine) {
		routine.accept(this);
		HierarchicalMap<String, Block<Fanout, BlockData<Fanout>>> bs = super.getBlocks();
		EntryIdsByLabel result = new EntryIdsByLabel();
		Set<String> tags = bs.keySet();
		String routineName = routine.getName();
		for (String tag : tags) {
			Block<Fanout, BlockData<Fanout>> b = bs.getThruHierarchy(tag);
			Set<EntryId> bfouts = this.getBlockFanouts(b, routineName, null, null);
			result.put(tag, bfouts);
		}		
		return result;		
	}
	
	@Override
	protected Fanout getFanout(EntryId id, FanoutType type) {
		return new Fanout(id, type);
	}
}
