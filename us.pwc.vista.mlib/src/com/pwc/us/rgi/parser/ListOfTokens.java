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

public class ListOfTokens<T extends Token> extends CollectionOfTokens<T> {
	protected List<T> tokens;

	public ListOfTokens() {
	}
	
	public ListOfTokens(ListOfTokens<T> rhs) {
		this.tokens = rhs.tokens;
	}

	@Override
	public void setToken(int index, T token) {
		this.tokens.set(index, token);
	}

	@Override
	public void addToken(T token) {
		if (this.tokens == null) {
			this.tokens = new ArrayList<T>();
		}
		this.tokens.add(token);
	}

	@Override
	public T getToken(int index) {
		return this.tokens == null ? null : this.tokens.get(index);
	}
	
	@Override
	public int size() {
		return this.tokens == null ? 0 : this.tokens.size();
	}

	@Override
	public boolean hasToken() {
		return this.tokens != null;
	}
	
	@Override
	public TextPiece toValue() {	
		return TokenUtilities.toValue(this.tokens);
	}

	public Iterable<T> toLogicalIterable() {
		return this.tokens;
	}
	
	public Iterable<T> toIterable() {
		return this.tokens;
	}
}
