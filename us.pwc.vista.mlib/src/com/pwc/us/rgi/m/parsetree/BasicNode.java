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

import com.pwc.us.rgi.m.parsetree.data.CallArgument;
import com.pwc.us.rgi.m.parsetree.data.CallArgumentType;

public abstract class BasicNode implements Node {
	private static final long serialVersionUID = 1L;

	@Override
	public void acceptPreAssignment(Visitor visitor) {
		this.accept(visitor);
	}

	@Override
	public void acceptPostAssignment(Visitor visitor, Node rhs) {
	}

	@Override
	public boolean setEntryList(InnerEntryList entryList) {
		return false;
	}

	@Override
	public void acceptExclusiveNew(Visitor visitor) {
		this.accept(visitor);		
	}
	
	@Override
	public void acceptNew(Visitor visitor) {
		this.accept(visitor);
	}
	
	@Override
	public void acceptExclusiveKill(Visitor visitor) {
		this.accept(visitor);		
	}
	
	@Override
	public void acceptKill(Visitor visitor) {
		this.accept(visitor);
	}

	@Override
	public void acceptPreMerge(Visitor visitor) {
		this.accept(visitor);
	}

	@Override
	public void acceptPostMerge(Visitor visitor, Node rhs) {
	}

	@Override
	public void acceptCallArgument(Visitor visitor, int order) {
		this.accept(visitor);
	}
	
	@Override
	public CallArgument toCallArgument() {
		return new CallArgument(CallArgumentType.COMPLEX, this);
	}

	@Override
	public String getAsConstExpr() {
		return null;
	}

	@Override
	public ParentNode addSelf(ParentNode current, NodeList<Node> nodes, int level) {
		nodes.add(this);
		return current;
	}
	
	@Override
	public void update(AtomicGoto atomicGoto) {		
	}
	
	@Override
	public void update(AtomicDo atomicDo) {		
	}
	
	@Override
	public void update(Extrinsic extrinsic) {	
	}
}
