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

import com.pwc.us.rgi.parser.SequenceOfTokens;
import com.pwc.us.rgi.parser.Token;
import com.pwc.us.rgi.parser.Tokens;

public class TSymbolList extends TSequence implements SymbolList {
	public TSymbolList(int length) {
		super(length);
	}
	
	public TSymbolList(SequenceOfTokens<RuleSupply> tokens) {
		super(tokens);
	}
	
	@Override
	public RuleSupply getElement() {
		return this.getToken(1);
	}
	
	@Override
	public RuleSupply getDelimiter() {
		return this.getToken(2, 1);
	}
	
	@Override
	public RuleSupply getLeftParanthesis() {
		return this.getToken(2, 2, 1);
	}
	
	@Override
	public RuleSupply getRightParanthesis() {
		return this.getToken(2, 2, 3);
	}
	
	private boolean getFlag(int index) {
		Token f = this.getToken(2, 2, index);
		if (f == null) {
			return false;
		} else {
			return f.toValue().toString().equals("1");
		}		
	}
	
	@Override
	public boolean isEmptyAllowed() {
		return this.getFlag(5);
	}

	@Override
	public boolean isNoneAllowed() {
		return this.getFlag(7);
	}

	@Override
	public void accept(RuleDefinitionVisitor visitor, String name, RuleSupplyFlag flag) {
		Tokens<RuleSupply> delimiterGroup = this.getTokens(2);
		if (delimiterGroup == null) {
			this.getElement().acceptAsList(visitor, name, flag);
		} else {
			RuleSupply enclosedGroup = delimiterGroup.getToken(2);
			if (enclosedGroup == null) {
				visitor.visitDelimitedSymbolList(this.getElement(), this.getDelimiter(), name, flag);
			} else {
				visitor.visitEnclosedDelimitedSymbolList(this, name, flag);				
			}
		}
	}
}
