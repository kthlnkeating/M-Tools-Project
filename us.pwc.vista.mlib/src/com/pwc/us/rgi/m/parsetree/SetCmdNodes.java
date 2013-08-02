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

public class SetCmdNodes {
	public static class IndirectAtomicSet extends BasicNode {
		private static final long serialVersionUID = 1L;

		private Node node;
		
		public IndirectAtomicSet(Node node) {
			this.node = node;
		}

		public void acceptSubNodes(Visitor visitor) {
			this.node.accept(visitor);
		}
		
		@Override
		public void accept(Visitor visitor) {
			visitor.visitIndirectAtomicSet(this);
		}			
	}	

	public static class AtomicSet extends BasicNode {
		private static final long serialVersionUID = 1L;

		private Node lhs;
		private Node rhs;
		
		public AtomicSet(Node lhs, Node rhs) {
			this.lhs = lhs;
			this.rhs = rhs;
		}
		
		public void acceptSubNodes(Visitor visitor) {
			this.lhs.acceptPreAssignment(visitor);
			this.rhs.accept(visitor);			
			this.lhs.acceptPostAssignment(visitor, this.rhs);
		}
		
		@Override
		public void accept(Visitor visitor) {
			visitor.visitAtomicSet(this);
		}	
	}
		
	public static class MultiAtomicSet extends BasicNode {
		private static final long serialVersionUID = 1L;

		private Nodes<Node> lhss;
		private Node rhs;
		
		public MultiAtomicSet(Nodes<Node> lhss, Node rhs) {
			this.lhss = lhss;
			this.rhs = rhs;
		}
		
		public void acceptSubNodes(Visitor visitor) {
			for (Node lhs : this.lhss.getNodes()) {
				lhs.acceptPreAssignment(visitor);
			}
			this.rhs.accept(visitor);
			for (Node lhs : this.lhss.getNodes()) {
				lhs.acceptPostAssignment(visitor, this.rhs);
			}
		}
		
		@Override
		public void accept(Visitor visitor) {
			visitor.visitMultiAtomicSet(this);
		}	
	}
	
	public static class SetCmd extends MultiCommand {
		private static final long serialVersionUID = 1L;

		public SetCmd(Node postCondition, Node argument) {
			super(postCondition, argument);
		}

		@Override
		public void accept(Visitor visitor) {
			visitor.visitSet(this);
		}		
	}
 }
