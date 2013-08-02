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

public class StringPiece {
	private String data;
	private int beginIndex;
	private int endIndex;
	
	public StringPiece() {		
	}
	
	public StringPiece(String data, int beginIndex, int endIndex) {
		this.data = data;
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
	}
	
	@Override
	public String toString() {
		return this.data.substring(this.beginIndex, this.endIndex);
	}
	
	public void add(StringPiece addlPiece) {
		if (addlPiece.data != null) {
			if ((this.data != addlPiece.data) || (this.endIndex != addlPiece.beginIndex)) {
				if (this.data == null) {
					this.data = addlPiece.data;
					this.beginIndex = addlPiece.beginIndex;
					this.endIndex = addlPiece.endIndex;
				} else {
					this.data += this.toString() + addlPiece.toString();
					this.beginIndex = 0;
					this.endIndex = this.data.length();
				}
			} else {
				this.endIndex = addlPiece.endIndex;
			}
		}
	}
}
