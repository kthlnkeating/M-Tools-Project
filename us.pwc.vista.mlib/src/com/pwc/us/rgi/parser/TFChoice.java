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

import java.util.ArrayList;
import java.util.List;

import com.pwc.us.rgi.parsergen.ObjectSupply;

public class TFChoice<T extends Token> extends TFWithAdaptor<T> {
	private List<TokenFactory<T>> factories;
	
	public TFChoice(String name) {
		super(name);
		this.factories = new ArrayList<TokenFactory<T>>();
	}
	
	public void reset(int length) {
		this.factories = new ArrayList<TokenFactory<T>>(length);		
	}
	
	public void add(TokenFactory<T> tf) {
		this.factories.add(tf);
	}
	
	@Override
	public T tokenizeOnly(Text text, ObjectSupply<T> objectSupply) throws SyntaxErrorException {
		if (text.onChar()) {
			for (TokenFactory<T> f : this.factories) {
				T result = f.tokenize(text, objectSupply);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}
}
