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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class UniqueList<T> extends ArrayList<T> {
	private static final long serialVersionUID = 1L;
	
	private Set<T> set = new HashSet<T>();

	@Override
	public boolean add(T e) {
		if (this.set.contains(e)) {
			return false;
		} else {
			this.set.add(e);
			return super.add(e);
		}
	}

	@Override
	public void add(int index, T e) {
		if (! this.set.contains(e)) {
			this.set.add(e);
			super.add(index, e);
		}
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean result = false;
		for (T e : c) {
			if (! this.set.contains(e)) {
				this.set.add(e);
				super.add(e);
				result = true;
			}			
		}
		return result;
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		boolean result = false;
		for (T e : c) {
			if (! this.set.contains(e)) {
				this.set.add(e);
				super.add(index, e);
				result = true;
			}			
		}
		return result;
	}
}
