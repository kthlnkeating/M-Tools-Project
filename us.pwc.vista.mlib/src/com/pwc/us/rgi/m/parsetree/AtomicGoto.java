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

public class AtomicGoto extends FanoutNode {
	private static final long serialVersionUID = 1L;

	public LineOffset lineoffset;
	public PostConditional postCondition;
	
	public void setLineoffset(LineOffset lineoffset) {
		this.lineoffset = lineoffset;
	}
	
	public void setPostCondition(PostConditional postCondition) {
		this.postCondition = postCondition;
	}
	
	public void acceptSubNodes(Visitor visitor) {
		super.acceptLabelNodes(visitor);
		if (this.lineoffset != null) {
			this.lineoffset.accept(visitor);
		}
		super.acceptRoutineNodes(visitor);
		if (this.postCondition != null) {
			this.postCondition.accept(visitor);
		}
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitAtomicGoto(this);
	}

	public boolean hasPostCondition() {
		return this.postCondition != null;
	}
}