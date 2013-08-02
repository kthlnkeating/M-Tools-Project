package com.pwc.us.rgi.parsergen.ruledef;

import com.pwc.us.rgi.parser.TextPiece;
import com.pwc.us.rgi.parser.Token;

public class TTCopy implements Token {
	private Token master;
	
	public TTCopy(Token token) {
		this.master = token;
	}
	
	@Override
	public TextPiece toValue() {
		return this.master.toValue();
	}
}
