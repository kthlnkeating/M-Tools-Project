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

public class Goto extends MultiCommand  {
	private static final long serialVersionUID = 1L;

	private static class NoPostConditionAtomicGotoFinder extends Visitor {
		private boolean hasOneWithNoPostCondition;
		
		@Override
		protected void visitAtomicGoto(AtomicGoto atomicGoto) {
			this.hasOneWithNoPostCondition = this.hasOneWithNoPostCondition || ! atomicGoto.hasPostCondition();
		}
		
		public boolean find(Node node) {
			this.hasOneWithNoPostCondition = false;
			node.accept(this);
			return this.hasOneWithNoPostCondition;
		}
		
		public static NoPostConditionAtomicGotoFinder INSTANCE = new NoPostConditionAtomicGotoFinder();
	}
		
	public Goto(Node postCondition, Node argument) {
		super(postCondition, argument);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitGoto(this);
	}

	@Override
	public ParentNode addSelf(ParentNode current, NodeList<Node> nodes, int level) {
		nodes.add(this);
		if (current.isCloseble() && ! this.hasPostCondition()) {
			Node argument = this.getArgument();
			if (argument != null) {
				boolean hasNoPostConditionOne = NoPostConditionAtomicGotoFinder.INSTANCE.find(argument);
				if (hasNoPostConditionOne) {
					DeadCmds deadCmds = new DeadCmds(level);
					return deadCmds.addSelf(current, nodes, level);
				}
			}
		}		
		return current;
	}
}
