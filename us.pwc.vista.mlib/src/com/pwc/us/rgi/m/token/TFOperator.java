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

import java.util.HashMap;
import java.util.Map;

import com.pwc.us.rgi.parser.Text;
import com.pwc.us.rgi.parser.TextPiece;
import com.pwc.us.rgi.parser.TokenFactory;
import com.pwc.us.rgi.parsergen.ObjectSupply;

public class TFOperator extends TokenFactory<MToken> {
	private static class OperatorBranch {
		public Map<Character, OperatorBranch> nextBranch = new HashMap<Character, OperatorBranch>();
		public boolean validEnd;
	}
	
	public TFOperator(String name) {
		super(name);
	}
	
	private Map<Character, OperatorBranch> operators = new HashMap<Character, OperatorBranch>();
	
	public void addOperator(String operator) {
		Character ch = operator.charAt(0);
		OperatorBranch branch = this.operators.get(ch);
		if (branch == null) {
			branch = new OperatorBranch();
			this.operators.put(ch, branch);
		}
		for (int i=1; i<operator.length(); ++i) {
			Character nextCharacter = operator.charAt(i);
			OperatorBranch nextBranch = branch.nextBranch.get(nextCharacter);
			if (nextBranch == null) {
				nextBranch = new OperatorBranch();
				 branch.nextBranch.put(nextCharacter, nextBranch);
			}
			branch = nextBranch;
		}
		branch.validEnd = true;
	}
		
	@Override
	public MToken tokenize(Text text, ObjectSupply<MToken> objectSupply) {
		if (text.onChar()) {
			char ch = text.getChar();	
			OperatorBranch branch = this.operators.get(ch);
			if (branch != null) {
				int index = 1;
				while (text.onChar(index)) {
					ch = text.getChar(index);
					OperatorBranch nextBranch = branch.nextBranch.get(ch);
					if (nextBranch == null) break;
					branch = nextBranch;
					++index;
				}
				if (branch.validEnd) {
					TextPiece t = text.extractPiece(index);
					return new MOperator(t);
				}				
			}
		}
		return null;
	}
}
