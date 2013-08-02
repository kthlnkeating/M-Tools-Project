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

package com.pwc.us.rgi.m.token;

import com.pwc.us.rgi.m.parsetree.Node;
import com.pwc.us.rgi.m.parsetree.Nodes;
import com.pwc.us.rgi.m.parsetree.SetCmdNodes;
import com.pwc.us.rgi.parser.SequenceOfTokens;
import com.pwc.us.rgi.parser.Tokens;

public final class SetCmdTokens {
	public static final class MSingleAtomicSetCmd extends MSequence {
		public MSingleAtomicSetCmd(int length) {
			super(length);
		}
		
		public MSingleAtomicSetCmd(SequenceOfTokens<MToken> tokens) {
			super(tokens);
		}
		
		@Override
		public Node getNode() {
			MToken lhs = this.getToken(0);
			Node lhsNode = lhs.getNode();
			MToken rhs = this.getToken(2);
			if (rhs == null) {
				return new SetCmdNodes.IndirectAtomicSet(lhsNode);
			} else {
				Node rhsNode = rhs.getNode();
				return new SetCmdNodes.AtomicSet(lhsNode, rhsNode);
			}
		}		
	}

	public static final class MMultiAtomicSetCmd extends MSequence {
		public MMultiAtomicSetCmd(int length) {
			super(length);
		}
		
		public MMultiAtomicSetCmd(SequenceOfTokens<MToken> tokens) {
			super(tokens);
		}
		
		@Override
		public Node getNode() {
			Tokens<MToken> lhs = this.getTokens(0, 1);			
			MToken rhs = this.getToken(2);
			Nodes<Node> lhsNodes = NodeUtilities.getNodes(lhs.toLogicalIterable(), lhs.size());
			return new SetCmdNodes.MultiAtomicSet(lhsNodes, rhs.getNode());
		}		
	}
}
