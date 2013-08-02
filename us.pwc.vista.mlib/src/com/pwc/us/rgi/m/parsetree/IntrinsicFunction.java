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

public class IntrinsicFunction extends BasicNode {
	private static final long serialVersionUID = 1L;

	private String name;
	private Node arguments;
	
	public IntrinsicFunction(String name, Node arguments) {
		this.name = name;
		this.arguments = arguments;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Node getArguments() {
		return this.arguments;
	}
	
	public void acceptSubNodes(Visitor visitor) {
		if (this.arguments != null) {
			this.arguments.accept(visitor);
		}
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitIntrinsicFunction(this);
	}
}
