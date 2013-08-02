package com.pwc.us.rgi.m.tool.routine.error;

import java.util.List;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pwc.us.rgi.m.MTestCommon;
import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.parsetree.data.TagLocations;
import com.pwc.us.rgi.m.struct.LineLocation;
import com.pwc.us.rgi.m.token.MVersion;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.routine.error.ErrorRecorder;
import com.pwc.us.rgi.m.tool.routine.error.ErrorWithLineIndex;
import com.pwc.us.rgi.m.tool.routine.error.ErrorsByLabel;

public class ErrorToolTest {
	private static ParseTreeSupply pts95;
	private static ParseTreeSupply ptsCache;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String[] routineNames = {"XRGITST0", "CMDTEST0", "ERRTEST0"};
		pts95 = MTestCommon.getParseTreeSupply(routineNames, MVersion.ANSI_STD_95, false);		
		ptsCache = MTestCommon.getParseTreeSupply(routineNames, MVersion.CACHE, false);		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		pts95 = null;
		ptsCache = null;
	}
		
	private ErrorsByLabel getErrors(Routine r) {
		ErrorRecorder v = new ErrorRecorder();
		ErrorsByLabel result = v.getErrors(r);
		return result;
	}
	
	public void testNonErrorFiles(ParseTreeSupply pts) {
		String[] routineNames = {"XRGITST0", "CMDTEST0"};
		for (String routineName : routineNames) {
			Routine r = pts.getParseTree(routineName);
			ErrorsByLabel result = this.getErrors(r);
			Assert.assertTrue(result.isEmpty());
		}
	}

	@Test
	public void testNonErrorFiles() {
		testNonErrorFiles(ptsCache);
		testNonErrorFiles(pts95);
	}
	
	private void testErrTest0Error(ErrorWithLineIndex error, TagLocations locations, String expectedTag, int expectedOffset) {
		int lineIndex = error.getLineIndex();
		LineLocation location = locations.getLineLocation(lineIndex);
		Assert.assertEquals(expectedTag, location.getTag());
		Assert.assertEquals(expectedOffset, location.getOffset());		
	}
	
	private void testErrTest0(ParseTreeSupply pts) {
		String routineName = "ERRTEST0";
		Routine r = pts.getParseTree(routineName);
		ErrorsByLabel result = this.getErrors(r);
		TagLocations locations = r.getTagLocations();
		
		List<ErrorWithLineIndex> ewl0 = result.getResults("MULTIPLY");		
		Assert.assertEquals(1, ewl0.size());		
		testErrTest0Error(ewl0.get(0), locations, "MULTIPLY", 2);
		
		List<ErrorWithLineIndex> ewl1 = result.getResults("MAIN");		
		Assert.assertEquals(2, ewl1.size());		
		testErrTest0Error(ewl1.get(0), locations, "MAIN", 3);
		testErrTest0Error(ewl1.get(1), locations, "MAIN", 5);
		
		List<ErrorWithLineIndex> ewl2 = result.getResults("DOERR");		
		Assert.assertEquals(1, ewl2.size());		
		testErrTest0Error(ewl2.get(0), locations, "DOERR", 2);
		
		List<ErrorWithLineIndex> ewl3 = result.getResults("DOERR2");		
		Assert.assertEquals(1, ewl3.size());		
		testErrTest0Error(ewl3.get(0), locations, "DOERR2", 4);
	}

	@Test
	public void testErrTest0() {
		testErrTest0(ptsCache);
		testErrTest0(pts95);		
	}
}
