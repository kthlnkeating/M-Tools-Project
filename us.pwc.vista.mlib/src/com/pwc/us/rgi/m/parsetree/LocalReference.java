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
import com.pwc.us.rgi.m.parsetree.data.CallArgumentType;

public class LocalReference extends BasicNode {
	private static final long serialVersionUID = 1L;

	private Local local;
	
	public LocalReference(Local local) {
		this.local = local;
	}
	
	@Override
	public void accept(Visitor visitor) {
		this.local.accept(visitor);
	}
	
	@Override
	public void acceptCallArgument(Visitor visitor, int order) {
		visitor.passLocalByRef(this.local, order);
	}

	@Override
	public CallArgument toCallArgument() {
		return new CallArgument(CallArgumentType.LOCAL_BY_REF, this);
	}
}
