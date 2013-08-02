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
import com.pwc.us.rgi.m.parsetree.NodeList;
import com.pwc.us.rgi.parser.SequenceOfTokens;

public class MForCmdEQRHS extends MSequence {
	public MForCmdEQRHS(int length) {
		super(length);
	}
	
	public MForCmdEQRHS(SequenceOfTokens<MToken> tokens) {
		super(tokens);
	}
	
	@Override
	public Node getNode() {			
		Node node0 = this.getNode(0);
		Node node1 = this.getNode(1, 1);
		Node node2 = node1 == null ? null : this.getNode(2, 1); 
		
		int length = 1;
		if (node1 != null) {
			++length;
			if (node2 != null) ++length;
		}
		
		NodeList<Node> result = new NodeList<Node>(length);
		result.add(node0);
		if (node1 != null) {
			result.add(node1);
			if (node2 != null) result.add(node2);
		}
	
		return result;
	}
}