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

package com.pwc.us.rgi.m.struct;

public class LineLocation {
	private String tag;
	private int offset;
	
	public LineLocation(String tag, int offset) {
		this.tag = tag;
		this.offset = offset;
	}
	
	public String getTag() {
		return this.tag;
	}
	
	public int getOffset() {
		return this.offset;
	}
	
	@Override
	public boolean equals(Object rhs) {
		if ((rhs != null) && (rhs instanceof LineLocation)) {	
			LineLocation r = (LineLocation) rhs;
			return this.tag.equals(r.tag) && (this.offset == r.offset); 
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		String hashOrigin = this.tag == null ? "" : this.tag;
		String hashOffset = this.offset == 0 ? "" : String.valueOf(this.offset);
		String hashString = hashOffset + "^" + hashOrigin;
		int result = hashString.hashCode(); 
		return result;
	}
	
	@Override
	public String toString() {
		return this.tag + "+" + String.valueOf(this.offset);
	}
}
