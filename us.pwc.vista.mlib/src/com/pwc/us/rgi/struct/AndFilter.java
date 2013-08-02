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

public class AndFilter<T> implements Filter<T> {
	private Filter<T> filter0;
	private Filter<T> filter1;
	
	public AndFilter(Filter<T> filter0, Filter<T> filter1) {
		this.filter0 = filter0;
		this.filter1 = filter1;
	}
	
	public boolean isValid(T input) {
		return this.filter0.isValid(input) && this.filter1.isValid(input);
	}
}
