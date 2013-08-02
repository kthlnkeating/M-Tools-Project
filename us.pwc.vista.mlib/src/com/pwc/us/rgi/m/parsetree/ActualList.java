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

import com.pwc.us.rgi.m.parsetree.data.CallArgument;

public class ActualList extends NodeList<Node> {
	private static final long serialVersionUID = 1L;

	public ActualList(int size) {
		super(size);
	}
	
	@Override
	protected void acceptElements(Visitor visitor) {
		int index = 0;
		for (Node node : this.getNodes()) {
			if (node != null) node.acceptCallArgument(visitor, index);
			++index;
		}		
	}
 	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitActualList(this);
	}
	
	private void updateFanoutNode(FanoutNodeWithArguments fanoutNode) {
		CallArgument[] callArguments = new CallArgument[this.size()];
		int index = 0;
		for (Node node : this.getNodes()) {
			if (node != null) {
				CallArgument ca = node.toCallArgument();
				callArguments[index] = ca;
			}
			++index;
		}		

		fanoutNode.setCallArguments(callArguments);		
	}
	
	@Override
	public void update(AtomicDo atomicDo) {		
		this.updateFanoutNode(atomicDo);
	}
	
	@Override
	public void update(Extrinsic extrinsic) {		
		this.updateFanoutNode(extrinsic);
	}
}
