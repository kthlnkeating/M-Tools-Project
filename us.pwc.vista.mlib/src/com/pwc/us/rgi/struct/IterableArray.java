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

public class IterableArray<T> implements Iterable<T> {
	private T[] array;

	public IterableArray(T[] array) {
		this.array = array;
	}
	
	@Override
	public Iterator<T> iterator() {
		return this.new ArrayIterator();
	}
	
	private class ArrayIterator implements Iterator<T> {
		private int index;
		
		@Override
	    public boolean hasNext() {
	    	return (this.index < IterableArray.this.array.length);
	    }
	
		@Override
		public T next() throws NoSuchElementException {
			if (this.index >= IterableArray.this.array.length) {
				throw new NoSuchElementException();
			}
			T result = IterableArray.this.array[this.index];
			++this.index;
			return result;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
