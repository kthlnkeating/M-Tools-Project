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

public class CodeLocation {
	private String routineName;
	private int lineIndex;
	
	public CodeLocation(String routineName, int lineIndex) {
		this.routineName = routineName;
		this.lineIndex = lineIndex;
	}

	public String getRoutineName() {
		return this.routineName;
	}
	
	public int getLineIndex() {
		return this.lineIndex;
	}

	@Override
	public String toString() {
		return "(" + this.routineName + ":" + String.valueOf(this.lineIndex) + ")";
	}

	@Override
	public boolean equals(Object rhs) {
		if ((rhs != null) && (rhs instanceof CodeLocation)) {	
			CodeLocation r = (CodeLocation) rhs;
			return this.routineName.equals(r.routineName) && (this.lineIndex == r.lineIndex); 
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		String hashString = this.routineName + ":" + String.valueOf(this.lineIndex);
		int result = hashString.hashCode(); 
		return result;
	}
}
