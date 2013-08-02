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

public class TFEmptyVerified<T extends Token> extends TokenFactory<T> {
	private char ch;
	
	public TFEmptyVerified(String name, char ch) {
		super(name);
		this.ch = ch;
	}
	
	protected boolean isExpected(char ch) {
		return this.ch == ch;		
	}
		
	@Override
	public T tokenize(Text text, ObjectSupply<T> objectSupply) throws SyntaxErrorException {
		if (text.onChar()) {
			char ch = text.getChar();
			if (this.isExpected(ch)) {
				return objectSupply.newEmpty();
			} else {
				throw new SyntaxErrorException();
			}
		}
		return null;
	}
}
