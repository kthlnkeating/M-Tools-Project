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

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.pwc.us.rgi.struct.IterableSingle;
import com.pwc.us.rgi.struct.IterableSingleAndList;
import com.pwc.us.rgi.struct.SingleAndListIterator;
import com.pwc.us.rgi.struct.SingleIterator;

public class DelimitedListOfTokens<T extends Token> extends CollectionOfTokens<T> implements Iterable<T> {
	private static class TDelimitedListIterator<T extends Token> extends SingleAndListIterator<T> {
		public TDelimitedListIterator(T leading, Iterable<T> iterable) {
			super(leading, iterable);
		}
		
		@Override
		public T next() throws NoSuchElementException {
			if (this.inInitialState()) {
				return super.next();
			} else {
				@SuppressWarnings("unchecked")
				Tokens<T> ts = (Tokens<T>) super.next();
				return ts.getToken(1);
			}
		}
	}

	private T leadingToken;
	private Tokens<T> remainingTokens;
	
	public DelimitedListOfTokens(T leadingToken, Tokens<T> tailTokens) {
		this.leadingToken = leadingToken;
		this.remainingTokens = tailTokens;
	}

	public DelimitedListOfTokens(DelimitedListOfTokens<T> rhs) {
		this.leadingToken = rhs.leadingToken;
		this.remainingTokens = rhs.remainingTokens;
	}

	@Override
	public void setToken(int index, T token) {
		if (index == 0) {
			this.leadingToken = token;
		} else {
			this.remainingTokens.setToken(index-1, token);
		}
	}

	@Override
	public void addToken(T token) {
		if (this.remainingTokens == null) {
			this.remainingTokens = new ListOfTokens<T>();
		}
		this.remainingTokens.addToken(token);
	}

	@Override
	public T getToken(int index) {
		if (index == 0) {
			return this.leadingToken;
		} else if (this.remainingTokens != null) {
			return this.remainingTokens.getToken(index-1);
		} else {
			return null;
		}
	}

	@Override
	public int size() {
		return 1 + (this.remainingTokens == null ? 0 : this.remainingTokens.size());
	}

	@Override
	public boolean hasToken() {
		return true;
	}
	
	@Override
	public TextPiece toValue() {	
		TextPiece result = new TextPiece();
		result.add(this.leadingToken.toValue());
		if (this.remainingTokens != null) for (Token t : this.remainingTokens.toIterable()) if (t != null) {
			result.add(t.toValue());
		}		
		return result;
	}

	public Iterable<T> toLogicalIterable() {
		return this;
	}
	
	public Iterable<T> toIterable() {
		if (this.remainingTokens == null) {
			return new IterableSingle<T>(this.leadingToken);
		} else {
			return new IterableSingleAndList<T>(this.leadingToken, this.remainingTokens.toIterable());
		}
	}
	
	@Override
	public Iterator<T> iterator() {
		if (this.remainingTokens == null) {
			return new SingleIterator<T>(this.leadingToken);
		} else {
			return new TDelimitedListIterator<T>(this.leadingToken, this.remainingTokens.toIterable());
		}
	}
	
	public T getLogicalToken(int index) {
		if (index == 0) {
			return this.getToken(0);
		} else {
			Tokens<T> ts = this.getTokens(index);
			return ts.getToken(1);
		}
	}
}
