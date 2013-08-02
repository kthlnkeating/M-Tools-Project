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

import com.pwc.us.rgi.parser.SyntaxErrorException;
import com.pwc.us.rgi.parser.Text;
import com.pwc.us.rgi.parsergen.ObjectSupply;
import com.pwc.us.rgi.parsergen.ParseErrorException;
import com.pwc.us.rgi.parsergen.ParseException;

public class RuleParser {
	private RuleGrammar grammar;

	public RuleSupply getTopTFRule(String name, String ruleText) {
		if (this.grammar == null) {
			try {
				RuleDefinitionParserGenerator parserGen = new RuleDefinitionParserGenerator();
				this.grammar = parserGen.generate(RuleGrammar.class, RuleSupply.class);
			} catch (ParseException e) {
				throw new ParseErrorException("Error in rule grammar: " + e.getMessage());
			}
		}
		Text text = new Text(ruleText);
		try {
			ObjectSupply<RuleSupply> objectSupply = new DefaultObjectSupply();
			TSymbolSequence t = (TSymbolSequence) this.grammar.sequence.tokenize(text, objectSupply);
			int tLength = t.toValue().length();
			if (tLength != ruleText.length()) {
				String msg = "Error in rule " + name + " at position " + String.valueOf(tLength);		
				throw new ParseErrorException(msg);					
			}
			return t;
		} catch (SyntaxErrorException e) {
			int errorLocation = text.getIndex();
			String msg = "Error in rule " + name + " at position " + String.valueOf(errorLocation);		
			throw new ParseErrorException(msg);
		}		
	}	
}
