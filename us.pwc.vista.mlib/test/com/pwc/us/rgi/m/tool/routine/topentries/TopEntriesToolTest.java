package com.pwc.us.rgi.m.tool.routine.topentries;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pwc.us.rgi.m.MTestCommon;
import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.token.MVersion;
import com.pwc.us.rgi.m.tool.EntryIdListResult;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.routine.MRoutineToolInput;
import com.pwc.us.rgi.m.tool.routine.RoutineToolParams;

public class TopEntriesToolTest {
	private static ParseTreeSupply pts95;
	private static ParseTreeSupply ptsCache;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String[] routineNames = {
				"RFIOA000", "RFIOA001", "RFIOA002",
				"RFIOB000", "RFIOB001", "RFIOB002",
				"RFIOC000", "RFIOC001", "RFIOC002",
				"RFIOD000", "RFIOD001", "RFIOD002", "RFIOXLLC"};
		pts95 = MTestCommon.getParseTreeSupply(routineNames, MVersion.ANSI_STD_95, false);		
		ptsCache = MTestCommon.getParseTreeSupply(routineNames, MVersion.CACHE, false);		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		pts95 = null;
		ptsCache = null;
	}
	
	private void verify(List<EntryId> entryIds, String... expecteds) {
		int expectedSize = expecteds.length / 2;
		Assert.assertEquals(expectedSize, entryIds.size());
		Set<EntryId> expectedSet = new HashSet<EntryId>();
		for (int i=0; i<expectedSize; ++i) {
			expectedSet.add(new EntryId(expecteds[2*i], expecteds[2*i+1]));
		}
		for (EntryId actual : entryIds) {
			Assert.assertTrue(expectedSet.contains(actual));
			
		}		
	}
		
	private void testTopEntriesResultSelected(ParseTreeSupply pts) {
		List<String> routinesUnderTest = Arrays.asList("RFIOA000", "RFIOB001", "RFIOC002", "RFIOXLLC");
		MRoutineToolInput input = new MRoutineToolInput();
		input.addRoutines(routinesUnderTest);
		
		RoutineToolParams params = new RoutineToolParams(pts);
		TopEntriesTool tool = new TopEntriesTool(params);

		EntryIdListResult result = tool.getResult(input);
		List<EntryId> entryIds = result.getEntryIdList();
		verify(entryIds, 
				"RFIOA000", "RFIOA000", "RFIOA000", "FROM2", 
				"RFIOA000", "DE0", "RFIOA000", "TOPD", 
				"RFIOA000", "TOPE", "RFIOA000", "COME",
				"RFIOB001", "RFIOB001", "RFIOB001", "BD0", 
				"RFIOB001", "COME", "RFIOC002", "RFIOC002",
				"RFIOXLLC", "RFIOXLLC", "RFIOXLLC", "T2");
	}

	@Test
	public void testTopEntriesResultSelected() {
		testTopEntriesResultSelected(ptsCache);
		testTopEntriesResultSelected(pts95);		
	}
	
	private void testTopEntriesResultAll(ParseTreeSupply pts) {
		List<String> routinesUnderTest = Arrays.asList("RFIOA000", "RFIOB001", "RFIOC002", "RFIOXLLC");
		MRoutineToolInput input = new MRoutineToolInput();
		input.addRoutines(routinesUnderTest);
		
		RoutineToolParams params = new RoutineToolParams(pts);
		params.setResultRoutineFilter(Arrays.asList(".*"));
		TopEntriesTool tool = new TopEntriesTool(params);

		EntryIdListResult result = tool.getResult(input);
		List<EntryId> entryIds = result.getEntryIdList();
		verify(entryIds, 
				"RFIOA000", "RFIOA000", "RFIOA000", "TOPD", 
				"RFIOA000", "TOPE", "RFIOA000", "COME",
				"RFIOB001", "RFIOB001", "RFIOC002", "RFIOC002",
				"RFIOXLLC", "RFIOXLLC", "RFIOXLLC", "T2");
	}

	@Test
	public void testTopEntriesResultAll() {
		testTopEntriesResultAll(ptsCache);
		testTopEntriesResultAll(pts95);		
	}
	
	private void testTopEntriesResultSpecified0(ParseTreeSupply pts) {
		List<String> routinesUnderTest = Arrays.asList("RFIOA000", "RFIOB001", "RFIOXLLC");
		MRoutineToolInput input = new MRoutineToolInput();
		input.addRoutines(routinesUnderTest);
		
		RoutineToolParams params = new RoutineToolParams(pts);
		params.setResultRoutineFilter(Arrays.asList("RFIO[A|B]00[1|0]","RFIO[C|A]00[1|0]","RFIO[B|C]00[1|0]", "RFIOXLLC"));
		TopEntriesTool tool = new TopEntriesTool(params);

		EntryIdListResult result = tool.getResult(input);
		List<EntryId> entryIds = result.getEntryIdList();
		verify(entryIds, 
				"RFIOA000", "RFIOA000", "RFIOA000", "FROM2", 
				"RFIOA000", "DE0", "RFIOA000", "TOPD", 
				"RFIOA000", "TOPE", "RFIOA000", "COME",
				"RFIOB001", "RFIOB001", 
				"RFIOXLLC", "RFIOXLLC", "RFIOXLLC", "T2");
	}

	@Test
	public void testTopEntriesResultSpecified0() {
		testTopEntriesResultSpecified0(ptsCache);
		testTopEntriesResultSpecified0(pts95);		
	}
}