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

public abstract class MultiCommand extends BasicNode {
	private static final long serialVersionUID = 1L;

	private Node postCondition;
	private Node argument;
	
	public MultiCommand(Node postCondition, Node argument) {
		this.postCondition = postCondition;
		this.argument = argument;
	}

	public Node getPostCondition() {
		return this.postCondition;
	}
	
	public Node getArgument() {
		return this.argument;
	}
	
	public boolean hasArgument() {
		return this.argument != null;
	}
	
	public void acceptSubNodes(Visitor visitor) {
		if (this.postCondition != null) {
			this.postCondition.accept(visitor);
		}
		if (argument != null) {
			this.argument.accept(visitor);
		}
	}
	
	public boolean hasPostCondition() {
		return this.postCondition != null;
	}
}
