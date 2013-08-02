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

package com.pwc.us.rgi.struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pwc.us.rgi.m.parsetree.data.EntryObject;

public class HierarchicalMap<K, V extends EntryObject> extends HashMap<K, V> {
	private static final long serialVersionUID = 1L;
	
	private HierarchicalMap<K, V> parent;
	private List<V> children;
	
	public HierarchicalMap() {		
	}
	
	public HierarchicalMap(HierarchicalMap<K, V> parent) {
		this.parent = parent;
	}
	
	@Override
	public V put(K key, V value) {
		if ((this.parent != null) && (this.size() == 0)) {
			this.parent.addChild(value);
		}
		return super.put(key, value);
	}
	
	public void addChild(V child) {
		if (this.children == null) {
			this.children = new ArrayList<V>(2);
		}
		this.children.add(child);
	}
	
	public Pair<V, Boolean> getThruHierarchyWithLocalFlag(K key) {
		V result = super.get(key);
		if ((result == null) && (this.parent != null)) {
			return this.parent.getThruHierarchyWithLocalFlag(key);
		} else {
			boolean isLocal = this.parent != null;
			return new Pair<V, Boolean>(result, isLocal);
		}
	}
	
	public V getThruHierarchy(K key) {
		V result = super.get(key);
		if ((result == null) && (this.parent != null)) {
			return this.parent.getThruHierarchy(key);
		} else {
			return result;
		}
	}
	
	public HierarchicalMap<K, V> getParent() {
		return this.parent;
	}

	public V getChildBlock(String tag) {
		if (this.children != null) {	
			for (V child : this.children) {
				String childTag = child.getEntryId().getLabelOrDefault();
				if (tag.equals(childTag)) {				
					return child;
				}
			}
		}
		return null;
	}
}