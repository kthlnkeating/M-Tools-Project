package com.pwc.us.rgi.parsergen.ruledef;

import static org.junit.Assert.*;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pwc.us.rgi.charlib.CharPredicate;
import com.pwc.us.rgi.charlib.Predicate;
import com.pwc.us.rgi.parser.SyntaxErrorException;
import com.pwc.us.rgi.parser.TFCharacter;
import com.pwc.us.rgi.parser.TFSequence;
import com.pwc.us.rgi.parser.Text;
import com.pwc.us.rgi.parser.Token;
import com.pwc.us.rgi.parser.TokenFactory;
import com.pwc.us.rgi.parsergen.ObjectSupply;
import com.pwc.us.rgi.parsergen.rulebased.DefinitionVisitor;
import com.pwc.us.rgi.parsergen.rulebased.FSRCustom;
import com.pwc.us.rgi.parsergen.rulebased.FactorySupplyRule;
import com.pwc.us.rgi.parsergen.ruledef.DefaultObjectSupply;
import com.pwc.us.rgi.parsergen.ruledef.RuleDefinitionParserGenerator;
import com.pwc.us.rgi.parsergen.ruledef.RuleGrammar;
import com.pwc.us.rgi.parsergen.ruledef.RuleSupply;
import com.pwc.us.rgi.parsergen.ruledef.RuleSupplyFlag;
import com.pwc.us.rgi.parsergen.ruledef.TConstSymbol;
import com.pwc.us.rgi.parsergen.ruledef.TOptionalSymbols;
import com.pwc.us.rgi.parsergen.ruledef.TRequiredSymbols;
import com.pwc.us.rgi.parsergen.ruledef.TSymbol;
import com.pwc.us.rgi.parsergen.ruledef.TSymbolSequence;

public class RuleGrammarTest {
	private static RuleGrammar spec;
	private static ObjectSupply<RuleSupply> objectSupply;
	private static ObjectSupply<Token> tokenObjectSupply;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RuleDefinitionParserGenerator parserGen = new RuleDefinitionParserGenerator();
		spec = parserGen.generate(RuleGrammar.class, RuleSupply.class);
		objectSupply = new DefaultObjectSupply();
		tokenObjectSupply = new TestObjectSupply();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		spec = null;
		objectSupply = null;
	}

	@Test
	public void testSymbol() {
		try {
			Text text = new Text("token");
			Token symbol = spec.symbol.tokenize(text, objectSupply);
			Assert.assertNotNull(symbol);
			Assert.assertTrue(symbol instanceof TSymbol);
			Assert.assertEquals("token", symbol.toValue().toString());
		} catch (SyntaxErrorException se) {
			fail("Unexpected exception: " + se.getMessage());			
		}
	}

	@Test
	public void testOptionalSymbols() {
		try {
			Text text = new Text("[a, b, ([c], [d])]");
			Token optionalSymbols = spec.optionalsymbols.tokenize(text, objectSupply);
			Assert.assertNotNull(optionalSymbols);
			Assert.assertTrue(optionalSymbols instanceof TOptionalSymbols);
			Assert.assertEquals("[a, b, ([c], [d])]", optionalSymbols.toValue().toString());
		} catch (SyntaxErrorException se) {
			fail("Unexpected exception: " + se.getMessage());			
		}
	}

	@Test
	public void testRequiredSymbols() {
		try {
			Text text = new Text("([a, b], [c], [d])");
			Token requiredSymbols = spec.requiredsymbols.tokenize(text, objectSupply);
			Assert.assertNotNull(requiredSymbols);
			Assert.assertTrue(requiredSymbols instanceof TRequiredSymbols);
			Assert.assertEquals("([a, b], [c], [d])", requiredSymbols.toValue().toString());
		} catch (SyntaxErrorException se) {
			fail("Unexpected exception: " + se.getMessage());			
		}
	}

	@Test
	public void testConstSymbols() {
		try {
			Text text = new Text("\"@(\"");
			Token constSymbol = spec.constsymbol.tokenize(text, objectSupply);
			Assert.assertNotNull(constSymbol);
			Assert.assertTrue(constSymbol instanceof TConstSymbol);
			Assert.assertEquals("\"@(\"", constSymbol.toValue().toString());
		} catch (SyntaxErrorException se) {
			fail("Unexpected exception: " + se.getMessage());			
		}
	}

	private void updateMap(DefinitionVisitor<Token> dv, char ch) {
		Predicate p = new CharPredicate(ch);
		TokenFactory<Token> f = new TFCharacter<Token>(String.valueOf(ch), p);
		FSRCustom<Token> r = new FSRCustom<Token>(f);
		dv.addTopRule(String.valueOf(ch), r);
	}
	
	private void testRule(TokenFactory<Token> f, String v, String compare) {
		try {
			Text text = new Text(v);
			Token result = f.tokenize(text, tokenObjectSupply);
			Assert.assertNotNull(result);
			Assert.assertEquals(compare, result.toValue().toString());
		} catch (SyntaxErrorException se) {
			fail("Unexpected exception: " + se.getMessage());			
		}
	}
	
	private void testRule(TokenFactory<Token> f, String v) {
		testRule(f, v, v);
	}
	
	private void testRuleError(TokenFactory<Token> f, String v, int location) {
		Text text = new Text(v);
		try {
			f.tokenize(text, tokenObjectSupply);
			fail("Expected exception did not fire");			
		} catch (SyntaxErrorException se) {
			Assert.assertEquals(location, text.getIndex());
		}
	}

	private void testRuleNull(TokenFactory<Token> f, String v) {
		try {
			Text text = new Text(v);
			Token result = f.tokenize(text, tokenObjectSupply);
			Assert.assertNull(result);
		} catch (SyntaxErrorException se) {
			fail("Unexpected exception: " + se.getMessage());			
		}
	}
	
	private void addMap(DefinitionVisitor<Token> dv) {
		char[] chs = {'x', 'y', 'a', 'b', 'c', 'd', 'e'};
		for (char ch : chs) {
			updateMap(dv, ch);
		}
	}
	
	@Test
	public void testList() {
		try {
			Text text = new Text("x, {y:',':'(':')'}, [{a}, [{b:':'}], [c], d], e");
			TSymbolSequence rule = (TSymbolSequence) spec.sequence.tokenize(text, objectSupply);
			Assert.assertNotNull(rule);
			Assert.assertTrue(rule instanceof TSymbolSequence);
			Assert.assertEquals("x, {y:',':'(':')'}, [{a}, [{b:':'}], [c], d], e", rule.toValue().toString());
			DefinitionVisitor<Token> dv = new DefinitionVisitor<Token>();
			this.addMap(dv);
			rule.accept(dv, "test", RuleSupplyFlag.TOP);
			FactorySupplyRule<Token> r = dv.topRules.get("test");
			for (FactorySupplyRule<Token> v : dv.toBeUpdated) {
				v.update();
			}
			TokenFactory<Token> f = r.getShellFactory();
			Assert.assertNotNull(f);
			Assert.assertTrue(f instanceof TFSequence);
			testRule(f, "x(y)e"); 
			testRule(f, "x(y,y)e"); 
			testRule(f, "x(y,y,y)e"); 
			testRule(f, "x(y)aaab:bde"); 
			testRule(f, "x(y,y)abde"); 
			testRule(f, "x(y,y)abcde"); 
			testRule(f, "x(y,y,y)acde"); 
			testRule(f, "x(y,y,y)aaaade"); 
			testRuleError(f, "xye", 1); 
			testRuleError(f, "x(ya)de", 3); 
			testRuleError(f, "x(yy)abde", 3); 
		} catch (SyntaxErrorException se) {
			fail("Unexpected exception: " + se.getMessage());			
		}
	}

	@Test
	public void testList0() {
		try {
			Text text = new Text("{y:',':'(':')'}, {a}");
			TSymbolSequence rule = (TSymbolSequence) spec.sequence.tokenize(text, objectSupply);
			Assert.assertNotNull(rule);
			Assert.assertTrue(rule instanceof TSymbolSequence);
			Assert.assertEquals("{y:',':'(':')'}, {a}", rule.toValue().toString());
			DefinitionVisitor<Token> dv = new DefinitionVisitor<Token>();
			this.addMap(dv);
			rule.accept(dv, "test", RuleSupplyFlag.TOP);
			FactorySupplyRule<Token> r = dv.topRules.get("test");
			for (FactorySupplyRule<Token> v : dv.toBeUpdated) {
				v.update();
			}
			TokenFactory<Token> f = r.getShellFactory();
			Assert.assertNotNull(f);
			Assert.assertTrue(f instanceof TFSequence);
			testRule(f, "(y)a"); 
			testRule(f, "(y,y)aa"); 
			testRuleError(f, "(y,x)aa", 3); 
			testRuleError(f, "(y,y)", 5); 
		} catch (SyntaxErrorException se) {
			fail("Unexpected exception: " + se.getMessage());			
		}
	}

	//@Test
	public void testChar() {
		try {
			Text text = new Text("intlit, ['.', intlit], ['E', ['+' | '-'], intlit]");
			TSymbolSequence rule = (TSymbolSequence) spec.sequence.tokenize(text, objectSupply);
			Assert.assertNotNull(rule);
			Assert.assertTrue(rule instanceof TSymbolSequence);
			Assert.assertEquals("intlit, ['.', intlit], ['E', ['+' | '-'], intlit]", rule.toValue().toString());
		} catch (SyntaxErrorException se) {
			fail("Unexpected exception: " + se.getMessage());			
		}
	}

	@Test
	public void testChar0() {
		try {
			Text text = new Text("'+' | '-'");
			TSymbolSequence rule = (TSymbolSequence) spec.sequence.tokenize(text, objectSupply);
			Assert.assertNotNull(rule);
			Assert.assertTrue(rule instanceof TSymbolSequence);
			Assert.assertEquals("'+' | '-'", rule.toValue().toString());
		} catch (SyntaxErrorException se) {
			fail("Unexpected exception: " + se.getMessage());			
		}
	}

	@Test
	public void testSequence() {
		try {
			Text text = new Text("x, y, [(a, b), [c], d], e");
			TSymbolSequence rule = (TSymbolSequence) spec.sequence.tokenize(text, objectSupply);
			Assert.assertNotNull(rule);
			Assert.assertTrue(rule instanceof TSymbolSequence);
			Assert.assertEquals("x, y, [(a, b), [c], d], e", rule.toValue().toString());
			DefinitionVisitor<Token> dv = new DefinitionVisitor<Token>();
			this.addMap(dv);
			rule.accept(dv, "test", RuleSupplyFlag.TOP);
			FactorySupplyRule<Token> r = dv.topRules.get("test");
			for (FactorySupplyRule<Token> v : dv.toBeUpdated) {
				v.update();
			}
			TokenFactory<Token> f = r.getShellFactory();
			Assert.assertNotNull(f);
			Assert.assertTrue(f instanceof TFSequence);
			testRule(f, "xye"); 
			testRule(f, "xyabde"); 
			testRule(f, "xyabcde"); 
			testRuleError(f, "xyae", 3); 
			testRuleError(f, "xyde", 2); 
		} catch (SyntaxErrorException se) {
			fail("Unexpected exception: " + se.getMessage());			
		}
	}
	
	private TokenFactory<Token> getFactory(DefinitionVisitor<Token> dv, String inputText, String name) {
		try {
			Text text = new Text(inputText);
			TSymbolSequence rule = (TSymbolSequence) spec.sequence.tokenize(text, objectSupply);
			Assert.assertNotNull(rule);
			Assert.assertTrue(rule instanceof TSymbolSequence);
			Assert.assertEquals(inputText, rule.toValue().toString());
			this.addMap(dv);			
			rule.accept(dv, "test", RuleSupplyFlag.TOP);
			FactorySupplyRule<Token> r = dv.topRules.get("test");
			for (FactorySupplyRule<Token> v : dv.toBeUpdated) {
				v.update();
			}
			TokenFactory<Token> f = r.getShellFactory();
			Assert.assertNotNull(f);
			dv.addTopRule(name, r);
			return f;
		} catch (SyntaxErrorException se) {
			fail("Unexpected exception: " + se.getMessage());
			return null;
		}
	}
	
	@Test
	public void testCharSpecified() {
		DefinitionVisitor<Token> dv = new DefinitionVisitor<Token>();
		TokenFactory<Token> namea = getFactory(dv, "{'a' + 'c' + 'd'...'f'}", "namea");
		dv = new DefinitionVisitor<Token>();
		TokenFactory<Token> nameb = getFactory(dv, "{'a'...'z' - 'd'...'f'}", "nameb");
		dv = new DefinitionVisitor<Token>();
		TokenFactory<Token> namec = getFactory(dv, "{'a'...'m' + 'q' - 'd'...'f' - 'i'}", "namec");
		dv = new DefinitionVisitor<Token>();
		TokenFactory<Token> named = getFactory(dv, "{- 'b' + 'a'...'z'}", "named");
		dv = new DefinitionVisitor<Token>();
		TokenFactory<Token> namee = getFactory(dv, "'%' + 'a'...'z' + 'A'...'Z', [{'a'...'z' + 'A'...'Z' + '0'...'9'}]", "namee");
		
		testRule(namea, "accdd"); 
		testRule(namea, "efcdd"); 
		testRuleNull(namea, "pqr"); 
		testRule(namea, "abc", "a"); 
			
		testRule(nameb, "apqzr"); 
		testRule(nameb, "xyzmn"); 
		testRule(nameb, "abcde", "abc"); 
		testRule(nameb, "xfdyz", "x"); 
		
		testRule(namec, "amccb"); 
		testRule(namec, "qqacc"); 
		testRule(namec, "qqaie", "qqa"); 
		testRule(namec, "gertf", "g"); 
		
		testRule(named, "accdd"); 
		testRule(named, "efcdd"); 
		testRuleNull(named, "bqr"); 
		testRule(named, "afbc", "af"); 

		testRule(namee, "TAG1"); 
		testRule(namee, "%"); 
		testRule(namee, "tAt1"); 
		testRule(namee, "%tag"); 
		testRuleNull(namee, "1bqr"); 
		testRule(namee, "af+bc", "af"); 
	}
	
	@Test
	public void testListInChoice() {
		DefinitionVisitor<Token> dv = new DefinitionVisitor<Token>();
		TokenFactory<Token> name = getFactory(dv, "{'a'...'z'}", "name");
		testRule(name, "rgi");
		dv = new DefinitionVisitor<Token>();
		dv.addTopRule("name", new FSRCustom<Token>(name));	
		TokenFactory<Token> number = getFactory(dv, "{'0'...'9'}", "number");
		testRule(number, "232");
		dv = new DefinitionVisitor<Token>();
		dv.addTopRule("name", new FSRCustom<Token>(name));	
		dv.addTopRule("number", new FSRCustom<Token>(number));			
		TokenFactory<Token> rule = getFactory(dv, "{name:',':'(':')'} | number", "rule");		
		testRule(rule, "(rgi,dc)");
		testRule(rule, "1235");
		testRuleNull(rule, "RGI"); 
		testRuleError(rule, "(rgi,A)", 5);
	}	
}
