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

public abstract class ParentNode extends BasicNode {
	private static final long serialVersionUID = 1L;

	protected Nodes<Node> nodes;

	public void setNodes(Nodes<Node> nodes) {
		this.nodes = nodes;
	}
	
	public Node getLastNode() {
		if (this.nodes == null) {
			return null;
		} else {
			return this.nodes.getLastNode();
		}
	}
	
	public void acceptSubNodes(Visitor visitor) {
		if (this.nodes != null) for (Node node : this.nodes.getNodes()) {
			if (node != null) {
				node.accept(visitor);
			}
		}
	}
		
	@Override
	public boolean setEntryList(InnerEntryList entryList) {
		boolean result = false;
		if (this.nodes != null) for (Node node : this.nodes.getNodes()) {
			boolean nodeResult = node.setEntryList(entryList);
			result = result || nodeResult; 					
		}
		return result;
	}
	
	@Override
	public ParentNode addSelf(ParentNode current, NodeList<Node> nodes, int level) {
		nodes.add(this);
		current.setNodes(nodes.copy());
		nodes.clear();
		return this;
	}
	
	public boolean isCloseble() {
		return false;
	}
}
