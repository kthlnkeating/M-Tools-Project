package com.pwc.us.rgi.m.tool.entry.basiccodeinfo;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import com.pwc.us.rgi.m.MTestCommon;
import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.entry.RecursionDepth;
import com.pwc.us.rgi.m.tool.entry.RecursionSpecification;
import com.pwc.us.rgi.m.tool.entry.basiccodeinfo.BasicCodeInfoTR;
import com.pwc.us.rgi.m.tool.entry.basiccodeinfo.BasicCodeInfoTool;
import com.pwc.us.rgi.m.tool.entry.basiccodeinfo.BasicCodeInfoToolParams;

public class BCITTest {
	private void testExpectedGlobal(BasicCodeInfoTR r, String[] expectedGlobals) {
		Set<String> globals = new HashSet<String>(r.getData().getGlobals());
		Assert.assertEquals(expectedGlobals.length, globals.size());
		for (String expectedGlobal : expectedGlobals) {
			Assert.assertTrue(globals.contains(expectedGlobal));			
		}				
	}
	
	private void filemanTest(BasicCodeInfoTR r, String[] expectedGlobals, String[] expectedCalls) {
		Set<String> globals = new HashSet<String>(r.getData().getFilemanGlobals());
		Assert.assertEquals(expectedGlobals.length, globals.size());
		for (String expectedGlobal : expectedGlobals) {
			Assert.assertTrue(globals.contains(expectedGlobal));			
		}				

		Set<String> calls = new HashSet<String>(r.getData().getFilemanCalls());
		Assert.assertEquals(expectedCalls.length, calls.size());
		for (String expectedCall : expectedCalls) {
			Assert.assertTrue(calls.contains(expectedCall));			
		}				
	}
	
	@Test
	public void testExpectedGlobals() {
		String[] routineNames = {
				"APIROU00", "APIROU01", "APIROU02", "APIROU03", 
				"DMI", "DDI", "DIE", "FIE"};
		ParseTreeSupply pts = MTestCommon.getParseTreeSupply(routineNames);

		BasicCodeInfoToolParams p = new BasicCodeInfoToolParams(pts, null);		
		RecursionSpecification rs = new RecursionSpecification();
		rs.setDepth(RecursionDepth.ALL);
		p.setRecursionSpecification(rs);
			
		BasicCodeInfoTool a = new BasicCodeInfoTool(p);
				
		this.testExpectedGlobal(a.getResult(new EntryId("APIROU00", "FACT")), new String[0]);
		this.testExpectedGlobal(a.getResult(new EntryId("APIROU00", "SUM")), new String[]{"^RGI0(\"EF\""});
		this.testExpectedGlobal(a.getResult(new EntryId("APIROU00", "SUMFACT")), new String[]{"^RGI0(\"EF\""});
		this.testExpectedGlobal(a.getResult(new EntryId("APIROU00", "STORE")), new String[0]);
		this.testExpectedGlobal(a.getResult(new EntryId("APIROU00", "STOREG")), new String[0]);
		this.testExpectedGlobal(a.getResult(new EntryId("APIROU00", "TOOTHER")), new String[0]);
		this.testExpectedGlobal(a.getResult(new EntryId("APIROU00", "TONONE")), new String[0]);
		this.testExpectedGlobal(a.getResult(new EntryId("APIROU00", "ZZ")), new String[0]);
		this.testExpectedGlobal(a.getResult(new EntryId("APIROU01", "SUMFACT")), new String[]{"^RGI0(\"EF\"", "^UD(", "^UD(5", "^UM"});
		this.testExpectedGlobal(a.getResult(new EntryId("APIROU01", "STORE")), new String[0]);
		this.testExpectedGlobal(a.getResult(new EntryId("APIROU01", "LOOP")), new String[]{"^RGI0(\"EF\"", "^UD(", "^UD(5", "^UM"});
		this.testExpectedGlobal(a.getResult(new EntryId("APIROU03", "GPIND")), new String[0]);
		this.testExpectedGlobal(a.getResult(new EntryId("APIROU03", "CALL1")), new String[0]);
		this.filemanTest(a.getResult(new EntryId("APIROU03", "FILEMAN")), new String[]{"^DIC(9.4","^DIE(9.5", "^DIK(9.6"}, new String[]{"CHK^DIE(10.1", "CHK^DMI(10.2", "CHK^DDI(10.3"});				
	}
}