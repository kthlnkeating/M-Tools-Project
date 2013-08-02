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

import com.pwc.us.rgi.m.parsetree.EnvironmentFanoutRoutine;
import com.pwc.us.rgi.m.parsetree.FanoutLabel;
import com.pwc.us.rgi.m.parsetree.FanoutRoutine;
import com.pwc.us.rgi.m.parsetree.IndirectFanoutLabel;
import com.pwc.us.rgi.m.parsetree.IndirectFanoutRoutine;
import com.pwc.us.rgi.m.parsetree.Node;
import com.pwc.us.rgi.m.parsetree.PostConditional;
import com.pwc.us.rgi.parser.SequenceOfTokens;
import com.pwc.us.rgi.parser.TextPiece;

public class BasicTokens {
	public static class MTFanoutLabelA extends MTokenCopy {
		public MTFanoutLabelA(MToken token) {
			super(token);
		}
		
		@Override
		public Node getNode(Node subNode) {
			String value = this.toValue().toString();
			return new FanoutLabel(value, subNode);
		}		
	}
	
	public static class MTFanoutLabelB extends MTokenCopy {
		public MTFanoutLabelB(MToken token) {
			super(token);
		}
		
		@Override
		public Node getNode(Node subNode) {
			TextPiece value = this.toValue();
			return new FanoutLabel(value.toString(), subNode);
		}		
	}
	
	public static class MTIndirectFanoutLabel extends MTokenCopy {
		public MTIndirectFanoutLabel(MToken token) {
			super(token);
		}
		
		@Override
		public Node getNode(Node subNode) {
			return new IndirectFanoutLabel(subNode);
		}		
	}
	
	public static class MTFanoutRoutine extends MTokenCopy {
		public MTFanoutRoutine(MToken token) {
			super(token);
		}
		
		@Override
		public Node getNode(Node subNode) {
			String name = this.toValue().toString();
			return new FanoutRoutine(name, subNode);
		}		
	}
	
	public static class MTIndirectFanoutRoutine extends MTokenCopy {
		public MTIndirectFanoutRoutine(MToken token) {
			super(token);
		}
	
		@Override
		public Node getNode(Node subNode) {
			return new IndirectFanoutRoutine(subNode);
		}		
	}
	
	public static class MTEnvironmentFanoutRoutine extends MSequence {
		public MTEnvironmentFanoutRoutine(int length) {
			super(length);
		}
		
		public MTEnvironmentFanoutRoutine(SequenceOfTokens<MToken> tokens) {
			super(tokens);
		}
		
		@Override
		public Node getNode() {
			Node addlNode = super.getNode();
			return new EnvironmentFanoutRoutine(addlNode);
		}		
	}

	public static class MPostCondition extends MSequence {
		public MPostCondition(int length) {
			super(length);
		}
		
		public MPostCondition(SequenceOfTokens<MToken> tokens) {
			super(tokens);
		}
		
		@Override
		public Node getNode() {
			Node addlNode = this.getToken(1).getNode();
			return new PostConditional(addlNode);
		}		
	}
}
