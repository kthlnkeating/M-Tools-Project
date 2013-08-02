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

import com.pwc.us.rgi.m.parsetree.Local;
import com.pwc.us.rgi.m.parsetree.Node;
import com.pwc.us.rgi.m.parsetree.NodeList;
import com.pwc.us.rgi.parser.SequenceOfTokens;
import com.pwc.us.rgi.parser.TextPiece;
import com.pwc.us.rgi.parser.Tokens;

public class MLocal extends MSequence {
	public MLocal(int length) {
		super(length);
	}

	public MLocal(SequenceOfTokens<MToken> tokens) {
		super(tokens);
	}

	@Override
	public Node getNode() {
		TextPiece name = this.getToken(0).toValue();
		Tokens<MToken> subsripts = this.getTokens(1);
		if (subsripts == null) {
			return new Local(name);
		} else {
			NodeList<Node> nodes = NodeUtilities.getSubscriptNodes(subsripts);
			return new Local(name, nodes);
		}
	}
}
