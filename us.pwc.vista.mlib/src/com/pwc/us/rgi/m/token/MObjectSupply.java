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

import com.pwc.us.rgi.parser.DelimitedListOfTokens;
import com.pwc.us.rgi.parser.ListOfTokens;
import com.pwc.us.rgi.parser.SequenceOfTokens;
import com.pwc.us.rgi.parser.TextPiece;
import com.pwc.us.rgi.parsergen.ObjectSupply;

public class MObjectSupply implements ObjectSupply<MToken> {
	@Override
	public MString newString(TextPiece piece) {
		return new MString(piece);
	}
	
	@Override
	public MSequence newSequence(SequenceOfTokens<MToken> tokens) {
		return new MSequence(tokens);
	}
	
	@Override
	public MList newList(ListOfTokens<MToken> tokens) {
		return new MList(tokens);
	}
	
	@Override
	public MDelimitedList newDelimitedList(DelimitedListOfTokens<MToken> tokens) {
		return new MDelimitedList(tokens);
	}
	
	@Override
	public MEmpty newEmpty() {
		return new MEmpty();
	}
}
