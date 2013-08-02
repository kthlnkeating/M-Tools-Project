package com.pwc.us.rgi.m.tool.entry.assumedvariables;

import java.util.Set;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pwc.us.rgi.m.MTestCommon;
import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.entry.RecursionDepth;
import com.pwc.us.rgi.m.tool.entry.RecursionSpecification;
import com.pwc.us.rgi.m.tool.entry.assumedvariables.AssumedVariables;
import com.pwc.us.rgi.m.tool.entry.assumedvariables.AssumedVariablesTool;
import com.pwc.us.rgi.m.tool.entry.assumedvariables.AssumedVariablesToolParams;

public class AVTTest {
	private static ParseTreeSupply pts;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String[] routineNames = {"APIROU00", "APIROU01", "APIROU02", "APIROU03","APIROU04", "DMI", "DDI", "DIE", "FIE"};
		pts = MTestCommon.getParseTreeSupply(routineNames);		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		pts = null;
	}
		
	private void testAssumedLocal(AssumedVariablesTool avt, String routineName, String labelName, String... expectedAssumeds) {
		EntryId entryId = new EntryId(routineName, labelName);
		AssumedVariables avs = avt.getResult(entryId);
		Set<String> assumeds = avs.toSet();
		Assert.assertEquals(expectedAssumeds.length, assumeds.size());
		for (String expectedOutput : expectedAssumeds) {
			Assert.assertTrue(assumeds.contains(expectedOutput));			
		}				
	}
	
	@Test
	public void testAssumedLocals0() {
		AssumedVariablesToolParams p = new AssumedVariablesToolParams(pts);
		p.getRecursionSpecification().setDepth(RecursionDepth.ALL);
		AssumedVariablesTool a = new AssumedVariablesTool(p);
		
		this.testAssumedLocal(a, "APIROU00", "FACT", "I");
		this.testAssumedLocal(a, "APIROU00", "SUM", "M", "R", "I");
		this.testAssumedLocal(a, "APIROU00", "SUMFACT", "S", "P");
		this.testAssumedLocal(a, "APIROU00", "STORE", "K", "D", "R");
		this.testAssumedLocal(a, "APIROU00", "STOREG", "K", "A", "D", "R");
		this.testAssumedLocal(a, "APIROU00", "TOOTHER", "I", "M");
		this.testAssumedLocal(a, "APIROU00", "TONONE", "A", "D", "ME", "NE", "HR");
		this.testAssumedLocal(a, "APIROU00", "ZZ", "A", "D");
		this.testAssumedLocal(a, "APIROU01", "SUMFACT", "S", "P");
		this.testAssumedLocal(a, "APIROU01", "STORE", "K", "D", "R");
		this.testAssumedLocal(a, "APIROU01", "LOOP", "S", "A", "C", "I", "J", "B", "D", "P");
		this.testAssumedLocal(a, "APIROU03", "GPIND", "B", "A");
		this.testAssumedLocal(a, "APIROU03", "CALL1", "A", "B");
		this.testAssumedLocal(a, "APIROU03", "NEWFOLVL", "V1");
		this.testAssumedLocal(a, "APIROU03", "NEWDOLVL", "B");
	}
		
	@Test
	public void testAssumedLocals1() {
		AssumedVariablesToolParams p = new AssumedVariablesToolParams(pts);		
		AssumedVariablesTool a = new AssumedVariablesTool(p);
		this.testAssumedLocal(a, "APIROU04", "INDOBLK", "I", "Y");		
	}
	
	@Test
	public void testAssumedLocals2() {		
		AssumedVariablesToolParams p = new AssumedVariablesToolParams(pts);
		p.addExpected("I");
		AssumedVariablesTool a = new AssumedVariablesTool(p);		
		this.testAssumedLocal(a, "APIROU04", "INDOBLK", "Y");	
	}
	
	@Test
	public void testAssumedLocals3() {		
		AssumedVariablesToolParams p = new AssumedVariablesToolParams(pts);		
		AssumedVariablesTool a = new AssumedVariablesTool(p);
		this.testAssumedLocal(a, "APIROU04", "ASSUMEV2", "I", "M");		
	}
	
	@Test
	public void testAssumedLocalsEntry() {		
		AssumedVariablesToolParams p = new AssumedVariablesToolParams(pts);		
		RecursionSpecification rs = p.getRecursionSpecification();
		rs.setDepth(RecursionDepth.ENTRY);
		AssumedVariablesTool a = new AssumedVariablesTool(p);		
		this.testAssumedLocal(a, "APIROU04", "ASSUMEV2", "I", "M", "V3");		
	}
	
	@Test
	public void testAssumedLocalsRoutine() {		
		AssumedVariablesToolParams p = new AssumedVariablesToolParams(pts);		
		RecursionSpecification rs = p.getRecursionSpecification();
		rs.setDepth(RecursionDepth.ROUTINE);
		AssumedVariablesTool a6 = new AssumedVariablesTool(p);		
		this.testAssumedLocal(a6, "APIROU04", "ASSUMEV2", "I", "M", "V1", "V3");				
	}
}