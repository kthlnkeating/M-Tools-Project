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

package com.pwc.us.rgi.m.parsetree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NodeList<T extends Node> extends Nodes<T> {
	private static final long serialVersionUID = 1L;

	private List<T> nodes;
	
	public NodeList() {		
	}
	
	public NodeList(int size) {
		this.nodes = new ArrayList<T>(size);	
	}

	public void reset(int size) {
		this.nodes = new ArrayList<T>(size);
	}
	
	public void add(T node) {
		if (this.nodes == null) {
			this.nodes = new ArrayList<T>();
		}
		this.nodes.add(node);
	}
	
	@Override
	public T getFirstNode() {
		if (this.nodes != null) {
			return this.nodes.get(0);
		}
		return null;
	}
	
	@Override
	public T getLastNode() {
		if (this.nodes != null) {
			int lastIndex = this.nodes.size() - 1;
			if (lastIndex >= 0) {
				return this.nodes.get(lastIndex);
			}
		}
		return null;
	}
		
	@Override
	public List<T> getNodes() {
		if (this.nodes == null) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(this.nodes);
		}
	}
	
	public NodeList<T> copy() {
		NodeList<T> result = new NodeList<T>();
		if (this.nodes != null) {
			result.nodes = new ArrayList<T>(this.nodes.size());
			for (T node : this.nodes) {
				result.nodes.add(node);
			}
		}
		return result;
	}
	
	public void clear() {
		if (this.nodes != null) {
			this.nodes.clear();
		}
	}
	
	public int size() {
		if (this.nodes == null) {
			return 0;
		} else {
			return this.nodes.size();
		}
	}
}
