package com.pwc.us.rgi.m.tool.routine.fanin;

import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pwc.us.rgi.m.MTestCommon;
import com.pwc.us.rgi.m.token.MVersion;
import com.pwc.us.rgi.m.tool.EntryIdsByRoutine;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.routine.MRoutineToolInput;
import com.pwc.us.rgi.m.tool.routine.MToolRoutineCommon;
import com.pwc.us.rgi.m.tool.routine.RoutineToolParams;

public class FaninToolTest {
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
		
	private void testFaninsResultSelected(ParseTreeSupply pts) {
		List<String> routinesUnderTest = Arrays.asList("RFIOA000", "RFIOB001", "RFIOC002", "RFIOXLLC");
		MRoutineToolInput input = new MRoutineToolInput();
		input.addRoutines(routinesUnderTest);
		
		RoutineToolParams params = new RoutineToolParams(pts);
		FaninTool tool = new FaninTool(params);

		EntryIdsByRoutine result = tool.getResult(input);
		MToolRoutineCommon.testResultRoutines(result, routinesUnderTest);
		
		for (String routineUnderTest : routinesUnderTest) {
			MToolRoutineCommon.testResultLabels(result, pts, routineUnderTest);
		}
		
		MToolRoutineCommon.testZeroResults(result, "RFIOA000", "RFIOA000", "FROM2", "DE0", "TOPD", "TOPE", "COME");
		MToolRoutineCommon.testResults(result, "RFIOA000", "INTD",  "RFIOA000", "TOPD");
		MToolRoutineCommon.testResults(result, "RFIOA000", "COMD",  "RFIOA000", "TOPD");
		MToolRoutineCommon.testResults(result, "RFIOA000", "INTE",  "RFIOA000", "TOPE");
		
		MToolRoutineCommon.testZeroResults(result, "RFIOB001", "RFIOB001", "BD0", "COME");
		MToolRoutineCommon.testResults(result, "RFIOB001", "COMD",  "RFIOA000", "TOPE");
				
		MToolRoutineCommon.testZeroResults(result, "RFIOC002", "RFIOC002");
		
		MToolRoutineCommon.testZeroResults(result, "RFIOXLLC", "RFIOXLLC", "T2");
		MToolRoutineCommon.testResults(result, "RFIOXLLC", "T0", "RFIOA000", "TOPD");
		MToolRoutineCommon.testResults(result, "RFIOXLLC", "T1", "RFIOB001", "COMD");
	}

	@Test
	public void testFaninsResultSelected() {
		testFaninsResultSelected(ptsCache);
		testFaninsResultSelected(pts95);		
	}
	
	private void testFaninsResultAll(ParseTreeSupply pts) {
		List<String> routinesUnderTest = Arrays.asList("RFIOA000", "RFIOB001", "RFIOC002", "RFIOXLLC");
		MRoutineToolInput input = new MRoutineToolInput();
		input.addRoutines(routinesUnderTest);
		
		RoutineToolParams params = new RoutineToolParams(pts);
		params.setResultRoutineFilter(Arrays.asList(".*"));
		FaninTool tool = new FaninTool(params);

		EntryIdsByRoutine result = tool.getResult(input);
		MToolRoutineCommon.testResultRoutines(result, routinesUnderTest);
		
		for (String routineUnderTest : routinesUnderTest) {
			MToolRoutineCommon.testResultLabels(result, pts, routineUnderTest);
		}
		
		MToolRoutineCommon.testZeroResults(result, "RFIOA000", "RFIOA000", "TOPD", "TOPE", "COME");
		MToolRoutineCommon.testResults(result, "RFIOA000", "INTD",  "RFIOA000", "TOPD");
		MToolRoutineCommon.testResults(result, "RFIOA000", "COMD",  "RFIOA000", "TOPD", "RFIOB000", "TOPD");
		MToolRoutineCommon.testResults(result, "RFIOA000", "INTE",  "RFIOA000", "TOPE");
		MToolRoutineCommon.testResults(result, "RFIOA000", "FROM2",  "RFIOA002", "RFIOA002");
		MToolRoutineCommon.testResults(result, "RFIOA000", "DE0",  "RFIOD000", "TOPE");
		
		MToolRoutineCommon.testZeroResults(result, "RFIOB001", "RFIOB001");
		MToolRoutineCommon.testResults(result, "RFIOB001", "COMD",  "RFIOA000", "TOPE");
		MToolRoutineCommon.testResults(result, "RFIOB001", "COME",  "RFIOC000", "TOPE");
		MToolRoutineCommon.testResults(result, "RFIOB001", "BD0",  "RFIOB000", "TOPD");
				
		MToolRoutineCommon.testZeroResults(result, "RFIOC002", "RFIOC002");
		
		MToolRoutineCommon.testZeroResults(result, "RFIOXLLC", "RFIOXLLC", "T2");
		MToolRoutineCommon.testResults(result, "RFIOXLLC", "T0", "RFIOA000", "TOPD", "RFIOB000", "TOPD", "RFIOC000", "TOPD", "RFIOD000", "TOPD");
		MToolRoutineCommon.testResults(result, "RFIOXLLC", "T1", "RFIOB001", "COMD", "RFIOA001", "COMD", "RFIOC001", "COMD", "RFIOD001", "COMD");
	}

	@Test
	public void testFanoutsResultAll() {
		testFaninsResultAll(ptsCache);
		testFaninsResultAll(pts95);		
	}
	
	private void testFaninsResultSpecified0(ParseTreeSupply pts) {
		List<String> routinesUnderTest = Arrays.asList("RFIOA000", "RFIOB001", "RFIOXLLC");
		MRoutineToolInput input = new MRoutineToolInput();
		input.addRoutines(routinesUnderTest);
		
		RoutineToolParams params = new RoutineToolParams(pts);
		params.setResultRoutineFilter(Arrays.asList("RFIO[A|B]00[1|0]","RFIO[C|A]00[1|0]","RFIO[B|C]00[1|0]", "RFIOXLLC"));
		FaninTool tool = new FaninTool(params);

		EntryIdsByRoutine result = tool.getResult(input);
		MToolRoutineCommon.testResultRoutines(result, routinesUnderTest);
		
		for (String routineUnderTest : routinesUnderTest) {
			MToolRoutineCommon.testResultLabels(result, pts, routineUnderTest);
		}
		
		MToolRoutineCommon.testZeroResults(result, "RFIOA000", "RFIOA000", "TOPD", "TOPE", "COME", "FROM2", "DE0");
		MToolRoutineCommon.testResults(result, "RFIOA000", "INTD",  "RFIOA000", "TOPD");
		MToolRoutineCommon.testResults(result, "RFIOA000", "COMD",  "RFIOA000", "TOPD", "RFIOB000", "TOPD");
		MToolRoutineCommon.testResults(result, "RFIOA000", "INTE",  "RFIOA000", "TOPE");
		
		MToolRoutineCommon.testZeroResults(result, "RFIOB001", "RFIOB001");
		MToolRoutineCommon.testResults(result, "RFIOB001", "COMD",  "RFIOA000", "TOPE");
		MToolRoutineCommon.testResults(result, "RFIOB001", "COME",  "RFIOC000", "TOPE");
		MToolRoutineCommon.testResults(result, "RFIOB001", "BD0",  "RFIOB000", "TOPD");
				
		MToolRoutineCommon.testZeroResults(result, "RFIOXLLC", "RFIOXLLC", "T2");
		MToolRoutineCommon.testResults(result, "RFIOXLLC", "T0", "RFIOA000", "TOPD", "RFIOB000", "TOPD", "RFIOC000", "TOPD");
		MToolRoutineCommon.testResults(result, "RFIOXLLC", "T1", "RFIOB001", "COMD", "RFIOA001", "COMD", "RFIOC001", "COMD");
	}

	@Test
	public void testFaninsResultSpecified0() {
		testFaninsResultSpecified0(ptsCache);
		testFaninsResultSpecified0(pts95);		
	}
}