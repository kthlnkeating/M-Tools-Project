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

public class NewCmdNodes {
	public static class AllNewCmd extends BasicNode {
		private static final long serialVersionUID = 1L;

		private Node postCondition;
		
		public AllNewCmd(Node postCondition) {
			this.postCondition = postCondition;
		}
		
		public void acceptSubNodes(Visitor visitor) {
			if (this.postCondition != null) {
				this.postCondition.accept(visitor);
			}
		}

		@Override
		public void accept(Visitor visitor) {
			visitor.visitAllNewCmd(this);
		}			
	}	

	public static class ExclusiveAtomicNew extends BasicNode {
		private static final long serialVersionUID = 1L;

		private Nodes<Node> nodes;
		
		public ExclusiveAtomicNew(Nodes<Node> nodes) {
			this.nodes = nodes;
		}
		
		public void acceptSubNodes(Visitor visitor) {			
			for (Node lhs : this.nodes.getNodes()) {
				lhs.acceptExclusiveNew(visitor);
			}
		}
		
		@Override
		public void accept(Visitor visitor) {
			visitor.visitExclusiveAtomicNew(this);
		}	
	}
	
	public static class AtomicNew extends BasicNode {
		private static final long serialVersionUID = 1L;

		private Node actual;
		
		public AtomicNew(Node node) {
			this.actual = node;
		}
		
		public void acceptSubNodes(Visitor visitor) {
			this.actual.acceptNew(visitor);
		}
		
		@Override
		public void accept(Visitor visitor) {
			visitor.visitAtomicNew(this);
		}	
	}
	
	public static class NewCmd extends MultiCommand {
		private static final long serialVersionUID = 1L;

		public NewCmd(Node postCondition, Node argument) {
			super(postCondition, argument);
		}

		@Override
		public void accept(Visitor visitor) {
			visitor.visitNew(this);
		}		
	}
}
