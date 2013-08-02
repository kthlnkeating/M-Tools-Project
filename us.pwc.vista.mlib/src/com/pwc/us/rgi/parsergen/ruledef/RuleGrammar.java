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

import com.pwc.us.rgi.parser.TokenFactory;
import com.pwc.us.rgi.parsergen.DelimitedListTokenType;
import com.pwc.us.rgi.parsergen.SequenceTokenType;
import com.pwc.us.rgi.parsergen.StringTokenType;

public class RuleGrammar {
	@CharSpecified(chars={','}, single=true)
	public TokenFactory<RuleSupply> comma;
	
	@CharSpecified(chars={':'}, single=true)
	public TokenFactory<RuleSupply> colon;

	@CharSpecified(chars={'('}, single=true)
	public TokenFactory<RuleSupply> lpar;
	@CharSpecified(chars={')'}, single=true)
	public TokenFactory<RuleSupply> rpar;
	
	@CharSpecified(chars={'['}, single=true)
	public TokenFactory<RuleSupply> lsqr;
	@CharSpecified(chars={']'}, single=true)
	public TokenFactory<RuleSupply> rsqr;
	
	@CharSpecified(chars={'{'}, single=true)
	public TokenFactory<RuleSupply> lcur;
	@CharSpecified(chars={'}'}, single=true)
	public TokenFactory<RuleSupply> rcur;
	
	@CharSpecified(chars={'\''}, single=true)
	public TokenFactory<RuleSupply> squote;
	@Choice({"escapedsquote", "escapedn", "escapedr", "escapedt", "noquote"})
	public TokenFactory<RuleSupply> squoted;

	@CharSpecified(chars={'"'}, single=true)
	public TokenFactory<RuleSupply> quote;
	@CharSpecified(excludechars={'"'})
	public TokenFactory<RuleSupply> quoted;

	@CharSpecified(chars={'|'}, single=true)
	public TokenFactory<RuleSupply> pipe;
	
	@CharSpecified(chars={'+', '-'}, single=true)
	public TokenFactory<RuleSupply> pm;
	
	@CharSpecified(chars={'1', '0'}, single=true)
	public TokenFactory<RuleSupply> bool;
	
	@WordSpecified("...")
	public TokenFactory<RuleSupply> ellipsis;
	@WordSpecified("\\'")
	public TokenFactory<RuleSupply> escapedsquote;
	@WordSpecified("\\n")
	public TokenFactory<RuleSupply> escapedn;
	@WordSpecified("\\r")
	public TokenFactory<RuleSupply> escapedr;
	@WordSpecified("\\t")
	public TokenFactory<RuleSupply> escapedt;
	@CharSpecified(excludechars={'\''}, single=true)
	public TokenFactory<RuleSupply> noquote;

	@StringTokenType(TSymbol.class)
	@CharSpecified(ranges={'a', 'z'})
	public TokenFactory<RuleSupply> specifiedsymbol; 

	@SequenceTokenType(TCharSymbol.class)
	@Sequence(value={"squote", "squoted", "squote"}, required="all")
	public TokenFactory<RuleSupply> charsymbol; 

	@Sequence(value={"ellipsis", "charsymbol"}, required="all")
	public TokenFactory<RuleSupply> charsymbolto; 
	@Sequence(value={"charsymbol", "charsymbolto", "sp"}, required="roo")
	public TokenFactory<RuleSupply> charsymbolwr; 
	@Sequence(value={"dpm", "charsymbolwr"}, required="all")
	public TokenFactory<RuleSupply> charsymbolpp; 
	@List(value="charsymbolpp")
	public TokenFactory<RuleSupply> charsymbollist; 
	@SequenceTokenType(TCharSymbol.class)
	@Sequence(value={"dpm", "charsymbolwr", "charsymbollist"}, required="oro")
	public TokenFactory<RuleSupply> charsymbolall; 

	@SequenceTokenType(TConstSymbol.class)
	@Sequence(value={"quote", "quoted", "quote", "colon", "bool"}, required="rrroo")
	public TokenFactory<RuleSupply> constsymbol; 
	
	@Choice({"specifiedsymbol", "charsymbolall", "constsymbol", "optionalsymbols", "requiredsymbols", "list"})
	public TokenFactory<RuleSupply> symbol; 
	
	@DelimitedListTokenType(TChoiceOfSymbols.class)
	@List(value="symbol", delim="choicedelimiter")
	public TokenFactory<RuleSupply> symbolchoice; 

	@CharSpecified(chars={' '})
	public TokenFactory<RuleSupply> sp;

	@Sequence(value={"sp", "lpar", "sp"}, required="oro")
	public TokenFactory<RuleSupply> openrequired;
	@Sequence(value={"sp", "rpar", "sp"}, required="oro")
	public TokenFactory<RuleSupply> closerequired;

	@Sequence(value={"pm", "sp"}, required="ro")
	public TokenFactory<RuleSupply> dpm;	
	
	@Sequence(value={"sp", "lsqr", "sp"}, required="oro")
	public TokenFactory<RuleSupply> openoptional;
	@Sequence(value={"sp", "rsqr", "sp"}, required="oro")
	public TokenFactory<RuleSupply> closeoptional;
	
	@Sequence(value={"sp", "lcur", "sp"}, required="oro")
	public TokenFactory<RuleSupply> openlist;
	@Sequence(value={"sp", "rcur", "sp"}, required="oro")
	public TokenFactory<RuleSupply> closelist;
	
	@Sequence(value={"sp", "pipe", "sp"}, required="oro")
	public TokenFactory<RuleSupply> choicedelimiter;

	@Sequence(value={"sp", "comma", "sp"}, required="oro")
	public TokenFactory<RuleSupply> delimiter;
	
	@SequenceTokenType(TOptionalSymbols.class)
	@Sequence(value={"openoptional", "sequence", "closeoptional"}, required="all")
	public TokenFactory<RuleSupply> optionalsymbols; 
	@SequenceTokenType(TRequiredSymbols.class)
	@Sequence(value={"openrequired", "sequence", "closerequired"}, required="all")
	public TokenFactory<RuleSupply> requiredsymbols;
	
	@Sequence(value={"colon", "anysymbols", "colon", "anysymbols", "colon", "bool", "colon", "bool"}, required="rrrroooo")
	public TokenFactory<RuleSupply> leftrightspec;
	@Sequence(value={"colon", "anysymbols", "leftrightspec"}, required="rro")
	public TokenFactory<RuleSupply> delimleftrightspec;
	@SequenceTokenType(TSymbolList.class)
	@Sequence(value={"openlist", "symbolchoice", "delimleftrightspec", "closelist"}, required="rror")
	public TokenFactory<RuleSupply> list;
	
	@Choice({"specifiedsymbol", "charsymbolall", "constsymbol"})
	public TokenFactory<RuleSupply> anysymbols;
	
	@DelimitedListTokenType(TSymbolSequence.class)
	@List(value="symbolchoice", delim="delimiter")
	public TokenFactory<RuleSupply> sequence;
}
