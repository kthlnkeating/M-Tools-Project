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

public abstract class CollectionOfTokens<T extends Token> implements Tokens<T> {
	@Override
	public T getToken(int index0, int index1) {
		Tokens<T> ts = this.getTokens(index0);
		if (ts == null) {
			return null;
		} else {
			return ts.getToken(index1);
		}
	}
	
	@Override
	public T getToken(int index0, int index1, int index2) {
		Tokens<T> ts = this.getTokens(index0);
		if (ts == null) {
			return null;
		} else {
			return ts.getToken(index1, index2);
		}
	}

	@Override
	public Tokens<T> getTokens(int index) {
		T t = this.getToken(index);
		if (t == null) {
			return null;
		} else {
			if (t instanceof Tokens) {
				@SuppressWarnings("unchecked")
				Tokens<T> ts = (Tokens<T>) t;
				return ts; 
			} else {
				return null;
			}
		}
	}
	
	@Override
	public Tokens<T> getTokens(int index0, int index1) {
		Tokens<T> ts = this.getTokens(index0);
		if (ts == null) {
			return null;
		} else {
			return ts.getTokens(index1);
		}
	}
	
	@Override
	public String toString() {
		return this.toValue().toString();
	}
}
