package com.pwc.us.rgi.parsergen.ruledef;

import com.pwc.us.rgi.parser.Token;
import com.pwc.us.rgi.parser.TokenFactory;
import com.pwc.us.rgi.parsergen.TokenType;
import com.pwc.us.rgi.parsergen.rulebased.Rule;

public class GrammarInError0 {
	@Rule("{'a'...'z'}")
	public TokenFactory<Token> name;

	@TokenType(TTNameA.class)
	@Rule("namex")
	public TokenFactory<Token> namea;
}
