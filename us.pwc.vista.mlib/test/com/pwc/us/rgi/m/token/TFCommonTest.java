package com.pwc.us.rgi.m.token;

import static org.junit.Assert.fail;

import junit.framework.Assert;

import com.pwc.us.rgi.m.struct.MError;
import com.pwc.us.rgi.m.token.MList;
import com.pwc.us.rgi.m.token.MSequence;
import com.pwc.us.rgi.m.token.MToken;
import com.pwc.us.rgi.parser.SyntaxErrorException;
import com.pwc.us.rgi.parser.Text;
import com.pwc.us.rgi.parser.Token;
import com.pwc.us.rgi.parser.TokenFactory;
import com.pwc.us.rgi.parsergen.ObjectSupply;

public class TFCommonTest {
	private static void checkObjectType(Token t) {
		Assert.assertTrue(t instanceof MToken);
		if (t instanceof MSequence) {
			for (MToken r : ((MSequence) t).toIterable()) {
				if (r != null) checkObjectType(r);
			}
			return;
		}
		if (t instanceof MList) {
			for (MToken r : ((MList) t).toIterable()) {
				if (r != null) checkObjectType(r);
			}
			return;			
		}
	}
		
	public static void validTokenCheck(Token t, String v) {
		Assert.assertEquals(v, t.toValue().toString());
		Assert.assertEquals(v.length(), t.toValue().length());
		checkObjectType(t);
	}
	
	static Token validCheck(TokenFactory<MToken> f, ObjectSupply<MToken> objectSupply, String v, boolean checkWithSpace) {
		try {
			Text text = new Text(v);
			Token t = f.tokenize(text, objectSupply);
			validTokenCheck(t, v);
			if (checkWithSpace) {
				validCheck(f, objectSupply, v + " ", v);
			}
			return t;
		} catch(SyntaxErrorException e) {
			fail("Exception: " + e.getMessage());	
			return null;
		}
	}

	static Token validCheck(TokenFactory<MToken> f, ObjectSupply<MToken> objectSupply, String v) {
		return validCheck(f, objectSupply, v, true);
	}

	static Token validCheck(TokenFactory<MToken> f, ObjectSupply<MToken> objectSupply, String v, Class<? extends Token> cls) {
		Token t = validCheck(f, objectSupply, v, true);
		Assert.assertNotNull(t);
		Assert.assertTrue(t.getClass().equals(cls));
		return t;
	}

	static void nullCheck(TokenFactory<MToken> f, ObjectSupply<MToken> objectSupply, String v) {
		try {
			Text text = new Text(v);
			Token t = f.tokenize(text, objectSupply);
			Assert.assertNull(t);
		} catch(SyntaxErrorException e) {
			fail("Unexpected exception.");
		}
	}
	
	static void validCheckNS(TokenFactory<MToken> f, ObjectSupply<MToken> objectSupply, String v) {
		validCheck(f, objectSupply, v, false);
	}

	static void auxErrorCheck(TokenFactory<MToken> f, ObjectSupply<MToken> objectSupply, String v, int errorCode, int location) {
		Text text = new Text(v);
		try {
			f.tokenize(text, objectSupply);
			fail("Expected exception did not fire.");
		} catch(SyntaxErrorException e) {
			int actualLocation = text.getIndex();
			Assert.assertEquals(location, actualLocation);
			Assert.assertEquals(errorCode,  e.getCode() == 0 ? MError.ERR_GENERAL_SYNTAX : e.getCode());
		}
	}

	static void errorCheck(TokenFactory<MToken> f, ObjectSupply<MToken> objectSupply, String v, int errorCode, int location) {
		auxErrorCheck(f, objectSupply, v, errorCode, location);
		if (v.length() > location) {
			auxErrorCheck(f, objectSupply, v + " ", errorCode, location);
		}
	}
	
	static void validCheck(TokenFactory<MToken> f, ObjectSupply<MToken> objectSupply, String v, String compare) {
		try {
			Text text = new Text(v);
			Token t = f.tokenize(text, objectSupply);
			validTokenCheck(t, compare);
		} catch(SyntaxErrorException e) {
			fail("Exception: " + e.getMessage());			
		}
	}
}
