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

import java.io.Serializable;

public class TextPiece implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String data;
	private int beginIndex;
	private int endIndex;
	
	public TextPiece() {		
	}
	
	public TextPiece(String data) {
		this.data = data;
		this.beginIndex = 0;
		this.endIndex = data.length();
	}
	
	public TextPiece(String data, int beginIndex, int endIndex) {
		this.data = data;
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
	}
	
	public TextPiece(TextPiece rhs) {
		this.set(rhs);
	}
	
	@Override
	public String toString() {
		if (data == null) {
			return "";
		} else {
			return this.data.substring(this.beginIndex, this.endIndex);
		}
	}

	public void set(TextPiece rhs) {	
		this.data = rhs.data;
		this.beginIndex = rhs.beginIndex;
		this.endIndex = rhs.endIndex;
	}
	
	public void set(String data, int beginIndex, int endIndex) {	
		this.data = data;
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
	}
	
	public int length() {
		return this.endIndex - this.beginIndex;
	}
	
	public char charAt(int index) {
		return this.data.charAt(this.beginIndex + index);
	}
	
	public char escapedCharAt(int index) {
		char ch0th = this.charAt(this.beginIndex + index);
		if (ch0th == '\\') {
			switch (this.charAt(this.beginIndex + index + 1)) {
				case '\'': return '\'';
				case 'n': return '\n';
				case 'r': return '\r';
				case 't': return '\t';
				default: return ch0th;
			}
		} else {
			return ch0th;
		}
	}
	
	public int count(char ch) {
		int result = 0;
		for (int i=this.beginIndex; i<this.endIndex; ++i) {
			if (this.data.charAt(i) == ch) ++result;
		}
		return result;
	}
	
	public void add(TextPiece addlPiece) {
		if (addlPiece.data != null) {
			if ((this.data != addlPiece.data) || (this.endIndex != addlPiece.beginIndex)) {
				if (this.data == null) {
					this.data = addlPiece.data;
					this.beginIndex = addlPiece.beginIndex;
					this.endIndex = addlPiece.endIndex;
				} else {
					this.data = this.toString() + addlPiece.toString();
					this.beginIndex = 0;
					this.endIndex = this.data.length();
				}
			} else {
				this.endIndex = addlPiece.endIndex;
			}
		}
	}
}
