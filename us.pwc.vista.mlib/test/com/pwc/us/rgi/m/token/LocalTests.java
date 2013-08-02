package com.pwc.us.rgi.m.token;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.pwc.us.rgi.parsergen.ruledef.TFDelimitedListTest;

@RunWith(Suite.class)
@SuiteClasses({
		TFCommandTest.class, TFDelimitedListTest.class, RoutineBeautifyTest.class,
		TFIntrinsicTest.class, TFTest.class, TFLineTest.class, TRoutineTest.class })
public class LocalTests {

}
