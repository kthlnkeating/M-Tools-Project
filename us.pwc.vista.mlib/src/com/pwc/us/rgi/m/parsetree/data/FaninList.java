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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pwc.us.rgi.m.tool.entry.Block;
import com.pwc.us.rgi.m.tool.entry.BlockData;
import com.pwc.us.rgi.struct.Indexed;

public class FaninList<U extends Fanout, T extends BlockData<U>> {
	private Block<U, T> node;
	private List<Indexed<Block<U, T>>> faninNodes = new ArrayList<Indexed<Block<U, T>>>();
	private Set<Integer> existing = new HashSet<Integer>();
	
	public FaninList(Block<U, T> node) {
		this.node = node;
	}
			
	public void addFanin(Block<U, T> faninNode, int fanoutIndex) {
		int faninId = System.identityHashCode(faninNode);
		if (faninId != System.identityHashCode(this.node)) {
			if (! this.existing.contains(faninId)) {
				Indexed<Block<U, T>> e = new Indexed<Block<U, T>>(faninNode, fanoutIndex);
				this.faninNodes.add(e);
				this.existing.add(faninId);
			}
		}
	}
	
	public List<Indexed<Block<U, T>>> getFanins() {
		return this.faninNodes;
	}
}
