package com.pwc.us.rgi.parsergen.ruledef;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({GrammarErrorTest.class, GrammarTest.class,
		       RuleGrammarTest.class, TFDelimitedListTest.class})
public class AllTests {
}
