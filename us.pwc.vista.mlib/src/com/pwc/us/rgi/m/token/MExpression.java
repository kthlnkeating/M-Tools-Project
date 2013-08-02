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
import com.pwc.us.rgi.parser.SequenceOfTokens;

public class MExpression extends MSequence {
	public MExpression(int length) {
		super(length);
	}

	public MExpression(SequenceOfTokens<MToken> tokens) {
		super(tokens);
	}

	@Override
	public Node getNode() {
		if (this.getToken(1) == null) {
			return this.getToken(0).getNode();
		} else {
			return NodeUtilities.getNodes(this, this.size());
		}
	}
}
