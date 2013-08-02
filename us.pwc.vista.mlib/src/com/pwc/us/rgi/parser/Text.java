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

import com.pwc.us.rgi.charlib.Predicate;

public class Text {
	private String text;
	private int index;
	
	public Text(String text) {
		this.text = text;
	}
	
	public Text(String text, int index) {
		this.text = text;
		this.index = index;
	}
	
	public boolean onEndOfText() {
		return this.index == this.text.length();
	}
	
	public boolean onChar() {
		return this.index < this.text.length();
	}
	
	public char getChar() {
		return this.text.charAt(this.index);
	}
	
	public boolean onChar(int forward) {
		return this.index+forward < this.text.length();
	}
	
	public char getChar(int forward) {
		return this.text.charAt(this.index+forward);
	}
	
	public int findEOL() {
		int i = 0;
		while (this.onChar(i)) {
			char ch = this.getChar(i);
			if ((ch == '\r') || (ch == '\n')) {
				break;
			}
			++i;
		}		
		return i;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	TextPiece extractPiece(String value, boolean ignoreCase) {
		if (this.text.length() >= this.index + value.length()) {
			if (ignoreCase) {
				String piece = this.text.substring(this.index, this.index+value.length());
				if (piece.equalsIgnoreCase(value)) {
					TextPiece result = new TextPiece();
					result.set(this.text, this.index, this.index+value.length());
					this.index += value.length();
					return result;
				}
			} else {
				if (this.text.startsWith(value, this.index)) {
					TextPiece result = new TextPiece();
					result.set(this.text, this.index, this.index+value.length());
					this.index += value.length();
					return result;
				}
			}
		}
		return null;	
	}
	
	TextPiece extractChar(Predicate predicate) {
		if (this.index < this.text.length()) {
			char ch = this.text.charAt(this.index);
			if (predicate.check(ch)) {
				++this.index;
				TextPiece result = new TextPiece();
				result.set(this.text, this.index-1, this.index);
				return result;
			}
		}
		return null;		
	}
	
	TextPiece extractPiece(Predicate predicate) {
		int fromIndex = this.index;
		while (this.onChar()) {
			char ch = this.getChar();
			if (! predicate.check(ch)) {
				if (fromIndex == this.index) {
					return null;
				} else {
					TextPiece result = new TextPiece();
					result.set(this.text, fromIndex, this.index);
					return result;
				}
			}
			++this.index;
		}
		if (fromIndex < this.text.length()) {
			TextPiece result = new TextPiece();
			result.set(this.text, fromIndex, this.index);			
			return result;
		} else {
			return null;
		}
	}
	
	public TextPiece extractPiece(int length) {
		TextPiece result = new TextPiece();
		result.set(this.text, this.index, this.index+length);
		this.index += length;
		return result;
	}
	
	TextPiece extractEOLToken() {
		if (this.onChar()) {
			char ch0th = this.getChar();
			if ((ch0th == '\n') || (ch0th == '\r')) {
				++this.index;
				if (this.onChar()) {
					char ch1st = this.getChar();
					if ((ch1st == '\n') || (ch1st == '\r')) {
						++this.index;
						TextPiece result = new TextPiece();
						result.set(this.text, this.index-2, this.index);
						return result;
					}
				}
				TextPiece result = new TextPiece();
				result.set(this.text, this.index-1, this.index);
				return result;
			}
		}
		return null;		
	}
	
	public Text getCopy() {
		return new Text(this.text, this.index);
	}

	public void copyFrom(Text text) {
		this.text = text.text;
		this.index = text.index;
	}
	
	public void resetIndex(int index) {
		this.index = index;
	}
}
