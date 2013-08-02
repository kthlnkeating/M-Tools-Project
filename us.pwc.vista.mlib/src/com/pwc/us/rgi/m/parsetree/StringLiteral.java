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

public class StringLiteral extends Literal {
	private static final long serialVersionUID = 1L;

	public StringLiteral(String value) {
		super(value);
	}

	public String getNakedValue() {
		String value = this.getAsConstExpr();
		return value.substring(1, value.length()-1);
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitStringLiteral(this);
	}

	@Override
	public void acceptCallArgument(Visitor visitor, int order) {
		visitor.passStringLiteral(this, order);
	}

	@Override
	public CallArgument toCallArgument() {
		return new CallArgument(CallArgumentType.STRING_LITERAL, this);
	}
}
