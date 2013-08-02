package com.pwc.us.rgi.vista.tools.entry;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.pwc.us.rgi.m.MTestCommon;
import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.entry.RecursionDepth;
import com.pwc.us.rgi.m.tool.entry.RecursionSpecification;
import com.pwc.us.rgi.m.tool.entry.assumedvariables.AssumedVariablesToolParams;
import com.pwc.us.rgi.m.tool.entry.basiccodeinfo.BasicCodeInfoToolParams;
import com.pwc.us.rgi.m.tool.entry.legacycodeinfo.LegacyCodeInfo;
import com.pwc.us.rgi.m.tool.entry.legacycodeinfo.LegacyCodeInfoTool;

public class EntryCodeInfoToolTest {
	private void testAssumedLocal(LegacyCodeInfo r, String[] expectedAssumeds, String[] expectedGlobals) {
		Set<String> assumeds = r.getAssumedVariables();
		Assert.assertEquals(expectedAssumeds.length, assumeds.size());
		for (String expectedOutput : expectedAssumeds) {
			Assert.assertTrue(assumeds.contains(expectedOutput));			
		}				

		Set<String> globals = new HashSet<String>(r.getBasicCodeInfo().getGlobals());
		Assert.assertEquals(expectedGlobals.length, globals.size());
		for (String expectedGlobal : expectedGlobals) {
			Assert.assertTrue(globals.contains(expectedGlobal));			
		}				
	}
	
	private void filemanTest(LegacyCodeInfo r, String[] expectedGlobals, String[] expectedCalls) {
		Set<String> globals = new HashSet<String>(r.getBasicCodeInfo().getFilemanGlobals());
		Assert.assertEquals(expectedGlobals.length, globals.size());
		for (String expectedGlobal : expectedGlobals) {
			Assert.assertTrue(globals.contains(expectedGlobal));			
		}				

		Set<String> calls = new HashSet<String>(r.getBasicCodeInfo().getFilemanCalls());
		Assert.assertEquals(expectedCalls.length, calls.size());
		for (String expectedCall : expectedCalls) {
			Assert.assertTrue(calls.contains(expectedCall));			
		}				
	}
	
	@Test
	public void testAssumedLocals() {
		String[] routineNames = {
				"APIROU00", "APIROU01", "APIROU02", "APIROU03", 
				"DMI", "DDI", "DIE", "FIE"};
		ParseTreeSupply pts = MTestCommon.getParseTreeSupply(routineNames);
				
		AssumedVariablesToolParams params = new AssumedVariablesToolParams(pts);
		RecursionSpecification rs = new RecursionSpecification();
		rs.setDepth(RecursionDepth.ALL);
		params.setRecursionSpecification(rs);

		BasicCodeInfoToolParams bcip = new BasicCodeInfoToolParams(pts, null);
		bcip.setRecursionSpecification(rs);
		
		LegacyCodeInfoTool a = new LegacyCodeInfoTool(params, bcip);
				
		this.testAssumedLocal(a.getResult(new EntryId("APIROU00", "FACT")), new String[]{"I"}, new String[0]);
		this.testAssumedLocal(a.getResult(new EntryId("APIROU00", "SUM")), new String[]{"M", "R", "I"}, new String[]{"^RGI0(\"EF\""});
		this.testAssumedLocal(a.getResult(new EntryId("APIROU00", "SUMFACT")), new String[]{"S", "P"}, new String[]{"^RGI0(\"EF\""});
		this.testAssumedLocal(a.getResult(new EntryId("APIROU00", "STORE")), new String[]{"K", "D", "R"}, new String[0]);
		this.testAssumedLocal(a.getResult(new EntryId("APIROU00", "STOREG")), new String[]{"K", "A", "D", "R"}, new String[0]);
		this.testAssumedLocal(a.getResult(new EntryId("APIROU00", "TOOTHER")), new String[]{"I", "M"}, new String[0]);
		this.testAssumedLocal(a.getResult(new EntryId("APIROU00", "TONONE")), new String[]{"A", "D", "ME", "NE", "HR"}, new String[0]);
		this.testAssumedLocal(a.getResult(new EntryId("APIROU00", "ZZ")), new String[]{"A", "D"}, new String[0]);
		this.testAssumedLocal(a.getResult(new EntryId("APIROU01", "SUMFACT")), new String[]{"S", "P"}, new String[]{"^RGI0(\"EF\"", "^UD(", "^UD(5", "^UM"});
		this.testAssumedLocal(a.getResult(new EntryId("APIROU01", "STORE")), new String[]{"K", "D", "R"}, new String[0]);
		this.testAssumedLocal(a.getResult(new EntryId("APIROU01", "LOOP")), new String[]{"S", "A", "C", "I", "J", "B", "D", "P"}, new String[]{"^RGI0(\"EF\"", "^UD(", "^UD(5", "^UM"});
		this.testAssumedLocal(a.getResult(new EntryId("APIROU03", "GPIND")), new String[]{"B", "A"}, new String[0]);
		this.testAssumedLocal(a.getResult(new EntryId("APIROU03", "CALL1")), new String[]{"A", "B"}, new String[0]);
		this.filemanTest(a.getResult(new EntryId("APIROU03", "FILEMAN")), new String[]{"^DIC(9.4","^DIE(9.5", "^DIK(9.6"}, new String[]{"CHK^DIE(10.1", "CHK^DMI(10.2", "CHK^DDI(10.3"});				
	}
}
