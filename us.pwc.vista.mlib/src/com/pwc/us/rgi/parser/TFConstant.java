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

import com.pwc.us.rgi.parsergen.ObjectSupply;

public class TFConstant<T extends Token> extends TokenFactory<T> {
	private String value;
	private boolean ignoreCase;
	
	public TFConstant(String name, String value) {
		this(name, value, false);
	}
	
	public TFConstant(String name, String value, boolean ignoreCase) {
		super(name);
		this.value = value;
		this.ignoreCase = ignoreCase;
	}

	@Override
	public T tokenize(Text text, ObjectSupply<T> objectSupply) {
		TextPiece p = text.extractPiece(this.value, this.ignoreCase);
		return p == null ? null : objectSupply.newString(p);
	}
}
	