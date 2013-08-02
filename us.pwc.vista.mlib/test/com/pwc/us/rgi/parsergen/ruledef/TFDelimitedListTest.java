package com.pwc.us.rgi.parsergen.ruledef;

import static org.junit.Assert.fail;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pwc.us.rgi.charlib.CharPredicate;
import com.pwc.us.rgi.charlib.CharRangePredicate;
import com.pwc.us.rgi.parser.SyntaxErrorException;
import com.pwc.us.rgi.parser.TFCharacter;
import com.pwc.us.rgi.parser.TFDelimitedList;
import com.pwc.us.rgi.parser.TFString;
import com.pwc.us.rgi.parser.Text;
import com.pwc.us.rgi.parser.TextPiece;
import com.pwc.us.rgi.parser.Token;
import com.pwc.us.rgi.parser.TokenFactory;
import com.pwc.us.rgi.parser.Tokens;
import com.pwc.us.rgi.parsergen.ObjectSupply;
import com.pwc.us.rgi.parsergen.ruledef.TestObjectSupply;

public class TFDelimitedListTest {
	private static ObjectSupply<Token> objectSupply;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		objectSupply = new TestObjectSupply();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		objectSupply = null;
	}

	public static void validTokenCheck(TextPiece t, String v) {
		Assert.assertEquals(v, t.toString());
		Assert.assertEquals(v.length(), t.length());		
	}
	
	public static void validCheckBasic(TFDelimitedList<Token> f, String v, String expected, String[] iteratorResults) {
		Text text = new Text(v);
		try {
			@SuppressWarnings("unchecked")
			Tokens<Token> t = (Tokens<Token>) f.tokenize(text, objectSupply);
			validTokenCheck(t.toValue(), expected);
			int index = 0;
			for (Token tit : t.toLogicalIterable()) {
				validTokenCheck(tit.toValue(), iteratorResults[index]);
				++index;
			}
			Assert.assertEquals(iteratorResults.length, index);
			Assert.assertEquals(index, t.size());			
		} catch (SyntaxErrorException e) {
			fail("Unexpected xception: " + e.getMessage());
		}
	}

	public static void validCheck(TFDelimitedList<Token> f, String v, String addl, String[] iteratorResults) {
		validCheckBasic(f, v, v, iteratorResults);
		validCheckBasic(f, v + addl, v, iteratorResults);
	}

	public static void errorCheck(TFDelimitedList<Token> f, String v, int errorLocation) {
		Text text = new Text(v);
		try {
			f.tokenize(text, objectSupply);
			fail("Expected exception did not fire.");							
		} catch (SyntaxErrorException e) {
			Assert.assertEquals(errorLocation, text.getIndex());
		}
	}

	public static void nullCheck(TFDelimitedList<Token> f, String v) {
		Text text = new Text(v);
		try {
			Token t = f.tokenize(text, objectSupply);
			Assert.assertNull(t);
		} catch(SyntaxErrorException e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void test() {
		TokenFactory<Token> delimiter = new TFCharacter<Token>(",", new CharPredicate(','));
		TokenFactory<Token> element = new TFString<Token>("e", new CharRangePredicate('a', 'z'));
		
		TFDelimitedList<Token> dl = new TFDelimitedList<Token>("dl");
		try {
			dl.tokenize(new Text("a"), objectSupply);
			fail("Expected exception did not fire.");							
		} catch (IllegalStateException e) {
		} catch (SyntaxErrorException se) {
			fail("Unexpected exception: " + se.getMessage());			
		}
		
		dl.set(element, delimiter);
		validCheck(dl, "a", ")", new String[]{"a"});
		validCheck(dl, "a,b", ")", new String[]{"a", "b"});
		validCheck(dl, "a,b,c", ")", new String[]{"a", "b", "c"});
		errorCheck(dl, "a,", 2);
		errorCheck(dl, "a,B", 2);
		errorCheck(dl, "a,B,c", 2);
		errorCheck(dl, "a,b,C", 4);
		errorCheck(dl, "a,,c", 2);
		errorCheck(dl, "a,b,", 4);
		nullCheck(dl, "A");
		nullCheck(dl, "");
		nullCheck(dl, " ");
		nullCheck(dl, ",");
		
		dl.set(element, delimiter, true);
		validCheck(dl, "a", ")", new String[]{"a"});
		validCheck(dl, "a,b", ")", new String[]{"a", "b"});
		validCheck(dl, "a,b,c", ")", new String[]{"a", "b", "c"});
		validCheck(dl, "a,,c", ")", new String[]{"a", "", "c"});
		validCheck(dl, "a,b,", ")", new String[]{"a", "b", ""});
		validCheck(dl, ",", ")", new String[]{"", ""});		
		nullCheck(dl, "A");
		nullCheck(dl, "");
		nullCheck(dl, " ");
	}
}
