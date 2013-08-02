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

import com.pwc.us.rgi.parser.TextPiece;

public class Global extends NodeWithSubscripts {
	private static final long serialVersionUID = 1L;

	public Global(TextPiece name) {
		super(name);
	}
	
	public Global(TextPiece name, NodeList<Node> subscripts) {
		super(name, subscripts);
	}

	public String getAsString() {
		String result = '^' + this.getName().toString();
		Node subscript = this.getSubscript(0);
		if (subscript != null) {
			result += '(';
			String constValue = subscript.getAsConstExpr();
			if (constValue != null) {
				result += constValue;
			}
		}
		return result;
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitGlobal(this);
	}
}
