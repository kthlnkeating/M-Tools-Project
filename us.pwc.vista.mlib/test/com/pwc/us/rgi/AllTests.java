package com.pwc.us.rgi;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	com.pwc.us.rgi.parsergen.ruledef.AllTests.class, 
	com.pwc.us.rgi.m.token.AllTests.class, 
	com.pwc.us.rgi.vista.repository.AllTests.class, 
	com.pwc.us.rgi.m.parsetree.data.AllTests.class,
	com.pwc.us.rgi.m.struct.AllTests.class,
	com.pwc.us.rgi.m.tool.entry.assumedvariables.AllTests.class,
	com.pwc.us.rgi.m.tool.entry.localassignment.AllTests.class,
	com.pwc.us.rgi.m.tool.entry.fanout.AllTests.class,	
	com.pwc.us.rgi.m.tool.entry.basiccodeinfo.AllTests.class,	
	com.pwc.us.rgi.m.tool.entry.quittype.AllTests.class,	
	com.pwc.us.rgi.m.tool.routine.error.AllTests.class,	
	com.pwc.us.rgi.m.tool.routine.fanout.AllTests.class,	
	com.pwc.us.rgi.m.tool.routine.fanin.AllTests.class,	
	com.pwc.us.rgi.m.tool.routine.topentries.AllTests.class,	
	com.pwc.us.rgi.m.tool.routine.occurance.AllTests.class,	
	com.pwc.us.rgi.vista.tools.AllTests.class, 
	com.pwc.us.rgi.vista.tools.entry.AllTests.class, 
	com.pwc.us.rgi.m.tool.entry.fanin.AllTests.class 
	})
public class AllTests {
}
