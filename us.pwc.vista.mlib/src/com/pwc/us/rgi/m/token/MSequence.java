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
import com.pwc.us.rgi.m.struct.MRefactorSettings;
import com.pwc.us.rgi.parser.SequenceOfTokens;

public class MSequence extends SequenceOfTokens<MToken> implements MToken {
	public MSequence(int length) {
		super(length);
	}
	
	public MSequence(SequenceOfTokens<MToken> tokens) {
		super(tokens);
	}
	
	public MSequence(MToken token0, MToken token1) {
		super(token0, token1);
	}
		
	@Override
	public Node getNode() {
		return NodeUtilities.getNodes(this, this.size());
	}

	public Node getNode(int index) {
		MToken subToken = this.getToken(index);
		return subToken == null ? null : subToken.getNode();
	}

	public Node getNode(int index0, int index1) {
		MToken t = this.getToken(index0, index1);
		if (t != null) {
			return t.getNode();
		}
		return null;
	}	

	public Node getNode(int index0, int index1, int index2) {
		MToken t = this.getToken(index0, index1, index2);
		if (t != null) {
			return t.getNode();
		}
		return null;
	}	

	@Override
	public void refactor(MRefactorSettings settings) {		
		for (MToken token : this.toIterable()) {
			if (token != null) {
				token.refactor(settings);
			}
		}
	}
}
