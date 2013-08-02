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

package com.pwc.us.rgi.m.parsetree.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pwc.us.rgi.m.tool.entry.Block;
import com.pwc.us.rgi.m.tool.entry.BlockData;
import com.pwc.us.rgi.struct.ObjectIdContainer;

public class FanoutBlocks<U extends Fanout, T extends BlockData<U>> {
	private Map<Integer, FaninList<U, T>> faninListMap = new HashMap<Integer, FaninList<U, T>>();
	private List<Block<U, T>> list = new ArrayList<Block<U, T>>();
	private List<Block<U, T>> storedList = new ArrayList<Block<U, T>>();
	private int rootId;
	private ObjectIdContainer blockIdContainer;
	
	public FanoutBlocks(Block<U, T> root, ObjectIdContainer blockIdContainer) {
		this.list.add(root);
		this.rootId = System.identityHashCode(root);
		FaninList<U, T> faninList = new FaninList<U, T>(root);
		this.faninListMap.put(this.rootId, faninList);
		this.blockIdContainer = blockIdContainer;
	}
	
	public void add(Block<U, T> fanin, Block<U, T> fanout, int fanoutIndex) {
		Integer fanoutId = System.identityHashCode(fanout);
		boolean stored = this.blockIdContainer == null ? false : this.blockIdContainer.contains(fanoutId);
		if (stored) {
			FaninList<U, T> faninList = this.faninListMap.get(fanoutId);
			if (faninList == null) {
				this.storedList.add(fanout);
				faninList = new FaninList<U, T>(fanout);
				this.faninListMap.put(fanoutId, faninList);						
			}					
			faninList.addFanin(fanin, fanoutIndex);
		} else {
			FaninList<U, T> faninList = this.faninListMap.get(fanoutId);
			if (faninList == null) {
				this.list.add(fanout);
				faninList = new FaninList<U, T>(fanout);
				this.faninListMap.put(fanoutId, faninList);
			}
			faninList.addFanin(fanin, fanoutIndex);
		}
	}
	
	public Block<U, T> getBlock(int index) {
		if (index < this.list.size()) {
			return this.list.get(index);
		} else {
			return null;
		}
	}
	
	public int getSize() {
		return this.list.size();
	}
	
	public FaninList<U, T> getFaninList(int id) {
		return this.faninListMap.get(id);	
	}
	
	public FaninList<U, T> getFaninList(Block<U, T> block) {
		int id = System.identityHashCode(block);
		return this.faninListMap.get(id);	
	}
	
	public List<Block<U, T>> getBlocks() {
		return this.list;
	}
	
	public List<Block<U, T>> getEvaludatedBlocks() {
		return this.storedList;
	}
}
	
