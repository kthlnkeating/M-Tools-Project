package com.pwc.us.rgi.m.tool.entry.localassignment;

import junit.framework.Assert;

import org.junit.Test;

import com.pwc.us.rgi.m.MTestCommon;
import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.struct.CodeLocation;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.entry.CodeLocations;
import com.pwc.us.rgi.m.tool.entry.RecursionDepth;
import com.pwc.us.rgi.m.tool.entry.RecursionSpecification;
import com.pwc.us.rgi.m.tool.entry.localassignment.LocalAssignmentTool;
import com.pwc.us.rgi.m.tool.entry.localassignment.LocalAssignmentToolParams;

public class LATTest {
	private void testLocations(CodeLocations r, CodeLocation[] expectedCodeLocations) {
		Assert.assertTrue(r.isIdenticalTo(expectedCodeLocations));
	}
	
	@Test
	public void test() {
		String[] routineNames = {
				"APIROU00", "APIROU01", "APIROU02", "APIROU03", 
				"DMI", "DDI", "DIE", "FIE"};
		ParseTreeSupply pts = MTestCommon.getParseTreeSupply(routineNames);
		LocalAssignmentToolParams p = new LocalAssignmentToolParams(pts);		
		RecursionSpecification rs = new RecursionSpecification();
		rs.setDepth(RecursionDepth.ALL);
		p.setRecursionSpecification(rs);
		p.addLocal("R");

		LocalAssignmentTool a = new LocalAssignmentTool(p);
				
		CodeLocation[] expectedCodeLocations = new CodeLocation[]{
				new CodeLocation("APIROU01", 9),
				new CodeLocation("APIROU00", 7),
				new CodeLocation("APIROU00", 8),
				new CodeLocation("APIROU00", 12),
				new CodeLocation("APIROU00", 13),
					};
		this.testLocations(a.getResult(new EntryId("APIROU01", "SUMFACT")), expectedCodeLocations);
	}
}
