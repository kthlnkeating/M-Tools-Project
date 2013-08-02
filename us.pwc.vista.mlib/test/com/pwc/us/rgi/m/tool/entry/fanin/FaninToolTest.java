package com.pwc.us.rgi.m.tool.entry.fanin;

import java.util.Set;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pwc.us.rgi.m.MTestCommon;
import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.tool.CommonToolParams;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.entry.RecursionDepth;
import com.pwc.us.rgi.m.tool.entry.fanin.EntryFanins;
import com.pwc.us.rgi.m.tool.entry.fanin.FaninTool;

public class FaninToolTest { 
	private static ParseTreeSupply pts;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String[] routineNames = {"FINROU00", "FINROU01", "FINROU02", "FINROU03", "FINROU04"};
		pts = MTestCommon.getParseTreeSupply(routineNames);		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		pts = null;
	}
		
	private void checkResult(EntryFanins fanins, String faninEntry, String[] faninNextEntries) {
		EntryId faninEntryId = EntryId.getInstance(faninEntry); 
		Assert.assertTrue(fanins.hasFaninEntry(faninEntryId));
		Set<EntryId> s = fanins.getFaninNextEntries(faninEntryId);
		Assert.assertNotNull(s);
		Assert.assertEquals(faninNextEntries.length, s.size());
		for (String faninNextEntry : faninNextEntries) {
			EntryId faninNextEntryId = EntryId.getInstance(faninNextEntry);
			Assert.assertTrue(s.contains(faninNextEntryId));
		}
	}
		
	@Test
	public void testFanins0() {
		EntryId entryId = new EntryId("FINROU00", "ADD");
		CommonToolParams p = new CommonToolParams(pts);
		p.getRecursionSpecification().setDepth(RecursionDepth.ALL);
		FaninTool tool = new FaninTool(entryId, p, false);

		EntryFanins fanins = tool.getResult();
		
		Set<EntryId> s = fanins.getFaninEntries();
		Assert.assertEquals(15, s.size());
		this.checkResult(fanins, "FINROU01^FINROU01", new String[]{":4^FINROU01"});
		this.checkResult(fanins, ":4^FINROU01", new String[]{"ADD^FINROU00"});
		this.checkResult(fanins, "ADDALL^FINROU01", new String[]{"ADD^FINROU00"});
		this.checkResult(fanins, "CONDADD^FINROU01", new String[]{"CONDADD2^FINROU01"});
		this.checkResult(fanins, "CONDADD2^FINROU01", new String[]{"ADD^FINROU00"});
		this.checkResult(fanins, "MULTAALL^FINROU01", new String[]{"MULTADD^FINROU00"});
		this.checkResult(fanins, "ADD^FINROU02", new String[]{"ADDALL^FINROU01"});
		this.checkResult(fanins, "ADD2^FINROU02", new String[]{"ADD^FINROU02"});
		this.checkResult(fanins, "MULTADD^FINROU00", new String[]{"ADD^FINROU00"});
		this.checkResult(fanins, "FINROU03^FINROU03", new String[]{"TESTINDO^FINROU03"});
		this.checkResult(fanins, "TESTINDO^FINROU03", new String[]{"ADD^FINROU00"});
		this.checkResult(fanins, "FINROU04^FINROU04", new String[]{"TESTINDO^FINROU04"});
		this.checkResult(fanins, "TESTINDO^FINROU04", new String[]{":5^FINROU04"});
		this.checkResult(fanins, ":5^FINROU04", new String[]{"OTHER^FINROU02"});
		this.checkResult(fanins, "OTHER^FINROU02", new String[]{"ADD^FINROU00"});		
	}
	
	@Test
	public void testFanins1() {
		EntryId entryId = new EntryId("FINROU00", "ADD");
		CommonToolParams p = new CommonToolParams(pts);
		p.getRecursionSpecification().setDepth(RecursionDepth.ALL);
		FaninTool tool = new FaninTool(entryId, p, true);
		EntryFanins fanins = tool.getResult();
		Set<EntryId> s = fanins.getFaninEntries();
		Assert.assertEquals(13, s.size());
		this.checkResult(fanins, "FINROU01^FINROU01", new String[]{"ADD^FINROU00"});
		this.checkResult(fanins, "ADDALL^FINROU01", new String[]{"ADD^FINROU00"});
		this.checkResult(fanins, "CONDADD^FINROU01", new String[]{"CONDADD2^FINROU01"});
		this.checkResult(fanins, "CONDADD2^FINROU01", new String[]{"ADD^FINROU00"});
		this.checkResult(fanins, "MULTAALL^FINROU01", new String[]{"MULTADD^FINROU00"});
		this.checkResult(fanins, "ADD^FINROU02", new String[]{"ADDALL^FINROU01"});
		this.checkResult(fanins, "ADD2^FINROU02", new String[]{"ADD^FINROU02"});
		this.checkResult(fanins, "MULTADD^FINROU00", new String[]{"ADD^FINROU00"});
		this.checkResult(fanins, "FINROU03^FINROU03", new String[]{"TESTINDO^FINROU03"});
		this.checkResult(fanins, "TESTINDO^FINROU03", new String[]{"ADD^FINROU00"});
		this.checkResult(fanins, "FINROU04^FINROU04", new String[]{"TESTINDO^FINROU04"});
		this.checkResult(fanins, "TESTINDO^FINROU04", new String[]{"OTHER^FINROU02"});
		this.checkResult(fanins, "OTHER^FINROU02", new String[]{"ADD^FINROU00"});
	}
}
