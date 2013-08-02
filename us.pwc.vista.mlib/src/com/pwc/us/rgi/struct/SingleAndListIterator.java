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

package com.pwc.us.rgi.struct;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SingleAndListIterator<T> implements Iterator<T> {	
	private T leading;
	private Iterator<T> remainingIterator;
	
	public SingleAndListIterator(T leading, Iterable<T> iterable) {
		this.leading = leading;
		this.remainingIterator = iterable.iterator();
	}
	
	@Override
    public boolean hasNext() {
    	return (this.leading != null) || this.remainingIterator.hasNext();
    }

	@Override
	public T next() throws NoSuchElementException {
		if (this.leading == null) {
			return this.remainingIterator.next();
		} else {
			T result = this.leading;
			this.leading = null;
			return result;
		}
	}
	
	protected boolean inInitialState() {
		return this.leading != null;
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
