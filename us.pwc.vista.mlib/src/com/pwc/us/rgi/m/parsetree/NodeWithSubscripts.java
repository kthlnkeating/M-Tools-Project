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

import com.pwc.us.rgi.parser.TextPiece;

abstract class NodeWithSubscripts extends BasicNode {
	private static final long serialVersionUID = 1L;

	private TextPiece name;
	private NodeList<Node> subsripts;
	
	public NodeWithSubscripts(TextPiece name) {
		this.name = name;
	}
	
	public NodeWithSubscripts(TextPiece name, NodeList<Node> subscripts) {
		this.name = name;
		this.subsripts = subscripts;
	}
		
	public TextPiece getName() {
		return this.name;
	}
	
	public Nodes<Node> getSubscripts() {
		return this.subsripts;
	}
	
	public Node getSubscript(int index) {
		if ((this.subsripts == null) || (this.subsripts.size() <= index)) {
			return null;
		} else {
			return this.subsripts.getNodes().get(index);
		}
	}
	
	public boolean hasSubscripts() {
		return (this.subsripts != null) && (this.subsripts.size() > 0);
	}
	
	public void acceptSubNodes(Visitor visitor) {
		Nodes<Node> subscripts = this.getSubscripts();
		if (subscripts != null) {
			subscripts.accept(visitor);
		}				
	}
}
