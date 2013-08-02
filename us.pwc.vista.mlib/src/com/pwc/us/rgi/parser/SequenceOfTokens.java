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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class SequenceOfTokens<T extends Token> extends CollectionOfTokens<T> implements Iterable<T> {
	private class SequenceStoreIterator implements Iterator<T> {
		private Iterator<T> iterator;
		private int iteratorIndex;
				
		public SequenceStoreIterator() {			
			if (SequenceOfTokens.this.tokens != null) {
				this.iterator = SequenceOfTokens.this.tokens.iterator();
			}
		}
		
		@Override
	    public boolean hasNext() {
			return this.iteratorIndex < SequenceOfTokens.this.index;
	    }
	
		@Override
		public T next() throws NoSuchElementException {
			if (this.iteratorIndex < SequenceOfTokens.this.index) {
				++this.iteratorIndex;
				return this.iterator.next();
			} else {
				throw new NoSuchElementException();
			}
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}		
	}

	protected List<T> tokens;

	private int index = 0;
	private int length = 0;
	
	public SequenceOfTokens(int length) {
		this.length = length;
	}

	public SequenceOfTokens(SequenceOfTokens<T> rhs) {
		this.tokens = rhs.tokens;
		this.index = rhs.index;
		this.length = rhs.length;
	}

	public SequenceOfTokens(T token0, T token1) {
		this.tokens = new ArrayList<T>(2);
		this.tokens.add(token0);
		this.tokens.add(token1);
		this.index = 2;
		this.length = 2;
	}	
	
	@Override
	public TextPiece toValue() {	
		return TokenUtilities.toValue(this);
	}

	public Iterable<T> toLogicalIterable() {
		return this;
	}
	
	public Iterable<T> toIterable() {
		return this;
	}

	public int size() {
		return this.index;
	}
	
	@Override
	public T getToken(int i) {
		if (this.index > i) {
			return this.tokens.get(i);
		} else {
			return null;
		}
	}
	
	@Override
	public Iterator<T> iterator() {
		if (this.tokens == null) {
			return Collections.emptyListIterator();
		} else {
			return this.new SequenceStoreIterator();
		}
	}
	
	@Override
	public void addToken(T token) {
		++index;
		if (token == null) {
			if (this.tokens != null) {
				this.tokens.add(token);
			}
		} else {
			if (this.tokens == null) {
				this.tokens = new ArrayList<T>(this.length);				
				for (int i=0; i<index-1; ++i) this.tokens.add(null);
			}
			this.tokens.add(token);
		}
	}
	
	@Override
	public void setToken(int index, T token) {
		if (this.index <= index) {		
			for (int i=this.index; i<index; ++i) {
				this.addToken(null);
			}
			this.addToken(token);
		} else {
			this.tokens.set(index, token);
		}
	}

	public void setLength(int length) {
		this.length = length;
		for (int i = this.index; index<tokens.size(); ++index) {
			this.tokens.set(i, null);
		}				
	}
	
	public void resetIndex(int index) {
		this.index = index;
	}
	
	public boolean isAllNull() {
		return this.tokens == null;
	}

	@Override
	public boolean hasToken() {
		return this.index > 0;
	}
}
