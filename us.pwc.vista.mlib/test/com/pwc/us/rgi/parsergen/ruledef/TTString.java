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

package com.pwc.us.rgi.parsergen.ruledef;

import com.pwc.us.rgi.parser.TextPiece;
import com.pwc.us.rgi.parser.Token;

public class TTString extends TextPiece implements Token {
	private static final long serialVersionUID = 1L;
	
	public TTString(TextPiece value) {
		super(value);
	}
	
	@Override
	public TextPiece toValue() {
		return this;
	}
}
