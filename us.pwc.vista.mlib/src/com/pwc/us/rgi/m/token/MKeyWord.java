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

package com.pwc.us.rgi.m.token;

import com.pwc.us.rgi.m.struct.KeywordRefactorFlags;
import com.pwc.us.rgi.m.struct.MNameWithMnemonic;
import com.pwc.us.rgi.m.struct.MRefactorSettings;
import com.pwc.us.rgi.parser.TextPiece;

public abstract class MKeyWord extends MString {
	private static final long serialVersionUID = 1L;

	public MKeyWord(TextPiece value) {
		super(value);
	}

	public abstract MNameWithMnemonic getNameWithMnemonic(String name);
	
	public abstract KeywordRefactorFlags getKeywordFlags(MRefactorSettings settings);

	@Override
	public void refactor(MRefactorSettings settings) {
		String value = this.toString();
		MNameWithMnemonic mnwm = this.getNameWithMnemonic(value.toUpperCase());
		KeywordRefactorFlags flags = this.getKeywordFlags(settings);
		String newValue = mnwm.refactor(flags, value);
		this.set(new TextPiece(newValue));
	}
}
