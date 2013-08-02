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

package com.pwc.us.rgi.output;

import java.util.Arrays;

public class TerminalFormatter {
	private String eol = Utility.getEOL();
	
	private int titleWidth = 0;
	private int width = 76;
	private int column = 0;
	private int listIndex = 0;
	private String indentStep = "  ";
	private String currentIndent = "";
	
	public void setTitleWidth(int titleWidth) {
		this.titleWidth = titleWidth;
	}
	
	public void setIndentStep(int indentStep) {
		this.indentStep = "";
		for (int i=0; i<indentStep; ++i) this.indentStep = this.indentStep + " "; 
	}
	
	public void pushIndent() {
		this.currentIndent = this.currentIndent + this.indentStep;
	}
	
	public void pullIndent() {
		this.currentIndent = this.currentIndent.substring(this.indentStep.length());
	}
	
	public String getIndent() {
		return this.currentIndent;
	}
	
	private static String getSpaces(int count) {
		char[] spaces = new char[count];
		Arrays.fill(spaces, ' ');
		return new String(spaces);		
	}
	
	private String getTitle(String title) {
		int length = title.length();
		if (this.titleWidth + 2 < length) {
			this.column = length;
			return title;
		} else {
			String result = TerminalFormatter.getSpaces(this.titleWidth - length - 2);
			result += title + ':' + ' ';
			this.column = result.length();
			return result;
		}
		
	}
	
	public String startList(String title) {
		this.listIndex = 0;
		return this.getTitle(title);
	}
	
	public String titled(String title, String msg) {
		String result = this.getTitle(title);
		result += msg;
		return result;
	}
	
	public String addToList(String listElement) {
		int length = listElement.length();
		if (this.listIndex == 0) {
			this.column += length;
			++this.listIndex;
			return listElement;
		}	
		int more = this.listIndex == 0 ? 1 : 0;
		if (this.column + length + 1 < this.width+more) {
			this.column += length + 1;
			++this.listIndex;
			return ',' + listElement;
		}
		String result = this.eol + TerminalFormatter.getSpaces(this.titleWidth);
		result += listElement;
		this.column = length + this.titleWidth;
		this.listIndex = 1;		
		return result;
	}
}
