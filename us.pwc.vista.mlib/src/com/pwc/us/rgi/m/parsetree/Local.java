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

public class Local extends NodeWithSubscripts {
	private static final long serialVersionUID = 1L;

	public Local(TextPiece name) {
		super(name);
	}
	
	public Local(TextPiece name, NodeList<Node> subscripts) {
		super(name, subscripts);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitLocal(this);
	}

	@Override
	public void acceptPreAssignment(Visitor visitor) {
		this.acceptSubNodes(visitor);
	}

	@Override
	public void acceptPostAssignment(Visitor visitor, Node rhs) {
		visitor.setLocal(this, rhs);
	}
	
	@Override
	public void acceptExclusiveNew(Visitor visitor) {
	}
	
	@Override
	public void acceptNew(Visitor visitor) {
		visitor.newLocal(this);
	}
	
	@Override
	public void acceptExclusiveKill(Visitor visitor) {
	}
	
	@Override
	public void acceptKill(Visitor visitor) {
		this.acceptSubNodes(visitor);
		visitor.killLocal(this);
	}

	@Override
	public void acceptPreMerge(Visitor visitor) {
		this.acceptSubNodes(visitor);
	}

	@Override
	public void acceptPostMerge(Visitor visitor, Node rhs) {
		visitor.mergeLocal(this, rhs);
	}
	
	@Override
	public void acceptCallArgument(Visitor visitor, int order) {
		this.acceptSubNodes(visitor);
		visitor.passLocalByVal(this, order);
	}
}
