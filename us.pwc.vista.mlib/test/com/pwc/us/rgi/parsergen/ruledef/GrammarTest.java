package com.pwc.us.rgi.parsergen.ruledef;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pwc.us.rgi.parser.SyntaxErrorException;
import com.pwc.us.rgi.parser.Text;
import com.pwc.us.rgi.parser.Token;
import com.pwc.us.rgi.parser.TokenFactory;
import com.pwc.us.rgi.parser.Tokens;
import com.pwc.us.rgi.parsergen.ObjectSupply;
import com.pwc.us.rgi.parsergen.rulebased.RuleBasedParserGenerator;
import com.pwc.us.rgi.parsergen.ruledef.TTString;
import com.pwc.us.rgi.parsergen.ruledef.TestObjectSupply;

public class GrammarTest {
	private static Grammar grammar;
	private static ObjectSupply<Token> objectSupply;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RuleBasedParserGenerator<Token> parserGen = new RuleBasedParserGenerator<Token>();
		grammar = parserGen.generate(Grammar.class, Token.class);
		objectSupply = new TestObjectSupply();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		grammar = null;
		objectSupply = null;
	}

	private void testCommon(String v, Token t) {
		Assert.assertNotNull(t);
		Assert.assertEquals(v, t.toValue().toString());
	}
	
	private void testCommonNumber(String v) {
		try {
			Text text = new Text(v, 0);
			Token number = grammar.number.tokenize(text, objectSupply);
			Assert.assertTrue(number instanceof TTNumber);
		} catch (SyntaxErrorException se) {
			fail("Unexpected exception: " + se.getMessage());			
		}				
	}
	
	@Test
	public void testNumber() {
		testCommonNumber("1");
		testCommonNumber("5.5");
		testCommonNumber("1.0E-7");
		testCommonNumber(".5E+7");
		testCommonNumber(".5");
	}

	@Test
	public void testPipe() {
		try {
			String v = "a+b.r";
			Text text = new Text(v);
			Token expr = grammar.expr.tokenize(text, objectSupply);
			testCommon(v, expr);
		} catch (SyntaxErrorException se) {
			fail("Unexpected exception: " + se.getMessage());			
		}				
	}
	
	public void testChoice(TokenFactory<Token> f, String v, Class<?> cls, int seqIndex) {
		try {
			Text text = new Text(v, 0);
			Token t = f.tokenize(text, objectSupply);
			this.testCommon(v, t);
			if (seqIndex < 0) {
				Assert.assertTrue(t.getClass().equals(cls));
			} else {
				@SuppressWarnings("unchecked")
				Token tseq = ((Tokens<Token>) t).getToken(seqIndex);
				Assert.assertTrue(tseq.getClass().equals(cls));
			}
		} catch (SyntaxErrorException se) {
			fail("Unexpected exception: " + se.getMessage());			
		}				
	}
	
	public void testChoiceError(TokenFactory<Token> f, String v) {
		try {
			Text text = new Text(v, 0);
			f.tokenize(text, objectSupply);
			fail("Expected exception did not fire");			
		} catch (SyntaxErrorException se) {
		}		
	}		
	
	@Test
	public void testChoice() {
		testChoice(grammar.testchoicea, "1", TTNumber.class, -1);
		testChoice(grammar.testchoicea, "a^a", TTNameA.class, 0);
		testChoice(grammar.testchoicea, "a:a", TTNameB.class, 0);
		testChoiceError(grammar.testchoicea, "a");
		
		testChoice(grammar.testchoiceb, "1", TTNumber.class, -1);
		testChoice(grammar.testchoiceb, "a^a", TTNameA.class, 0);
		testChoice(grammar.testchoiceb, "a:a", TTNameB.class, 0);
		testChoice(grammar.testchoiceb, "a", TTString.class, -1);
		testChoiceError(grammar.testchoicea, "a1");

		testChoice(grammar.testchoicec, "1", TTNumber.class, -1);
		testChoice(grammar.testchoicec, "a1", TTString.class, 0);
		testChoice(grammar.testchoicec, "a^a", TTNameA.class, 0);
		testChoice(grammar.testchoicec, "a", TTString.class, -1);

		testChoice(grammar.testchoiced, "1", TTNumber.class, -1);
		testChoice(grammar.testchoiced, "a1", TTString.class, 0);
		testChoice(grammar.testchoiced, "a^a", TTNameA.class, 0);
		testChoice(grammar.testchoiced, "a", TTNameB.class, -1);
	}
}
