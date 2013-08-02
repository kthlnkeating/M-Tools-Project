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
import com.pwc.us.rgi.m.parsetree.OpenCloseUseCmdNodes;
import com.pwc.us.rgi.parser.SequenceOfTokens;

public class OpenCloseUseCmdTokens {
	public static final class MAtomicOpenCmd extends MSequence {
		public MAtomicOpenCmd(SequenceOfTokens<MToken> tokens) {
			super(tokens);
		}
		
		@Override
		public Node getNode() {
			Node subNode = super.getNode();
			return new OpenCloseUseCmdNodes.AtomicOpenCmd(subNode);
		}		
	}
	
	public static final class MAtomicCloseCmd extends MSequence {
		public MAtomicCloseCmd(SequenceOfTokens<MToken> tokens) {
			super(tokens);
		}
		
		@Override
		public Node getNode() {
			Node subNode = super.getNode();
			return new OpenCloseUseCmdNodes.AtomicCloseCmd(subNode);
		}		
	}

	public static final class MAtomicUseCmd extends MSequence {
		public MAtomicUseCmd(SequenceOfTokens<MToken> tokens) {
			super(tokens);
		}
		
		@Override
		public Node getNode() {
			Node subNode = super.getNode();
			return new OpenCloseUseCmdNodes.AtomicUseCmd(subNode);
		}		
	}

	public static class MUseDeviceParameters extends MTokenCopy {
		public MUseDeviceParameters(MToken token) {
			super(token);
		}
		
		@Override
		public Node getNode(Node subNode) {
			return new OpenCloseUseCmdNodes.DeviceParameters(subNode);
		}
	}
	
	public static class MDeviceParameters extends MSequence {
		public MDeviceParameters(int length) {
			super(length);
		}
		
		public MDeviceParameters(SequenceOfTokens<MToken> tokens) {
			super(tokens);
		}
		
		@Override
		public Node getNode() {
			Nodes<Node> nodes = NodeUtilities.getNodes(this, this.size());
			OpenCloseUseCmdNodes.DeviceParameters result = new OpenCloseUseCmdNodes.DeviceParameters(nodes);
			return result;
		}
	}	
}
