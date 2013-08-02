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

package com.pwc.us.rgi.parser;

import com.pwc.us.rgi.parsergen.ObjectSupply;

public class TFEmpty<T extends Token> extends TokenFactory<T> {
	private TokenFactory<T> expected;
	
	public TFEmpty(String name) {		
		super(name);
	}
	
	public TFEmpty(String name, TokenFactory<T> expected) {
		super(name);
		this.expected = expected;
	}
	
	@Override
	public T tokenize(Text text, ObjectSupply<T> objectSupply) throws SyntaxErrorException {
		if (text.onChar()) {
			if (this.expected == null) {
				return objectSupply.newEmpty();
			} else {
				Text textCopy = text.getCopy();
				Token t = this.expected.tokenize(textCopy, objectSupply);
				if (t != null)  {
					return objectSupply.newEmpty();
				}
			}
		}
		return null;
	}
}
