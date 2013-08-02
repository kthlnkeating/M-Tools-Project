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

package com.pwc.us.rgi.charlib;


public class CharRangePredicate implements Predicate {
	private char ch0;
	private char ch1;
	
	public CharRangePredicate(char ch0, char ch1) {
		if (ch0 < ch1) {
			this.ch0 = ch0;
			this.ch1 = ch1;	
		} else {
			this.ch0 = ch1;
			this.ch1 = ch0;				
		}
	}
	
	@Override
	public boolean check(char ch) {
		return (ch >= this.ch0) && (ch <= this.ch1);
	}
}
