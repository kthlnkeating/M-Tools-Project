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

public class IfCmd extends ParentNode {
	private static final long serialVersionUID = 1L;

	private Nodes<Node> conditions;
		
	public IfCmd() {
	}

	public IfCmd(Nodes<Node> conditions) {
		this.conditions = conditions;
	}

	@Override
	public void acceptSubNodes(Visitor visitor) {
		if (this.conditions != null) {
			for (Node node : this.conditions.getNodes()) {
				node.accept(visitor);
			}
		}
		super.acceptSubNodes(visitor);
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitIf(this);
	}
}