package com.pwc.us.rgi.m.tool.routine.fanout;

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

public class FanoutToolTest {
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
		
	private void testFanoutsResultSelected(ParseTreeSupply pts) {
		List<String> routinesUnderTest = Arrays.asList("RFIOA000", "RFIOB001", "RFIOC002");
		MRoutineToolInput input = new MRoutineToolInput();
		input.addRoutines(routinesUnderTest);
		
		RoutineToolParams params = new RoutineToolParams(pts);
		FanoutTool tool = new FanoutTool(params);

		EntryIdsByRoutine result = tool.getResult(input);
		MToolRoutineCommon.testResultRoutines(result, routinesUnderTest);
		
		for (String routineUnderTest : routinesUnderTest) {
			MToolRoutineCommon.testResultLabels(result, pts, routineUnderTest);
		}
		
		MToolRoutineCommon.testZeroResults(result, "RFIOA000", "RFIOA000", "FROM2", "DE0", "INTD", "INTE", "COMD", "COME");
		MToolRoutineCommon.testResults(result, "RFIOA000", "TOPD",  "RFIOA000", "INTD", "RFIOA000", "COMD");
		MToolRoutineCommon.testResults(result, "RFIOA000", "TOPE",  "RFIOA000", "INTE", "RFIOB001", "COMD");
		
		MToolRoutineCommon.testZeroResults(result, "RFIOB001", "RFIOB001", "BD0", "COMD", "COME");
		
		MToolRoutineCommon.testZeroResults(result, "RFIOC002", "RFIOC002");
	}

	@Test
	public void testFanoutsResultSelected() {
		testFanoutsResultSelected(ptsCache);
		testFanoutsResultSelected(pts95);		
	}
	
	private void testFanoutsResultAll(ParseTreeSupply pts) {
		List<String> routinesUnderTest = Arrays.asList("RFIOB000", "RFIOC001", "RFIOD002");
		MRoutineToolInput input = new MRoutineToolInput();
		input.addRoutines(routinesUnderTest);
		
		RoutineToolParams params = new RoutineToolParams(pts);
		params.setResultRoutineFilter(Arrays.asList(".*"));
		FanoutTool tool = new FanoutTool(params);
		
		EntryIdsByRoutine result = tool.getResult(input);
		MToolRoutineCommon.testResultRoutines(result, routinesUnderTest);
		
		for (String routineUnderTest : routinesUnderTest) {
			MToolRoutineCommon.testResultLabels(result, pts, routineUnderTest);
		}
		
		MToolRoutineCommon.testZeroResults(result, "RFIOB000", "RFIOB000", "FROM2", "AE0", "INTD", "INTE", "COMD", "COME");
		MToolRoutineCommon.testResults(result, "RFIOB000", "TOPD",  "RFIOB000", "INTD", "RFIOB000", "COMD", "RFIOA000", "COMD", "RFIOB001", "BD0", "RFIOXLLC", "T0");
		MToolRoutineCommon.testResults(result, "RFIOB000", "TOPE",  "RFIOB000", "INTE", "RFIOC001", "COMD", "RFIOC000", "BE0", "RFIOA001", "COME");
		
		MToolRoutineCommon.testZeroResults(result, "RFIOC001", "RFIOC001", "CD0", "COME");
		MToolRoutineCommon.testResults(result, "RFIOC001", "COMD",  "RFIOXLLC", "T1");
		
		MToolRoutineCommon.testResults(result, "RFIOD002", "RFIOD002", "RFIOD000", "FROM2");
	}

	@Test
	public void testFanoutsResultAll() {
		testFanoutsResultAll(ptsCache);
		testFanoutsResultAll(pts95);		
	}
	
	private void testFanoutsResultSpecified0(ParseTreeSupply pts) {
		List<String> routinesUnderTest = Arrays.asList("RFIOC000", "RFIOD001", "RFIOA002");
		MRoutineToolInput input = new MRoutineToolInput();
		input.addRoutines(routinesUnderTest);
		
		RoutineToolParams params = new RoutineToolParams(pts);
		params.setResultRoutineFilter(Arrays.asList("RFIO[A|B|C|D]001"));
		FanoutTool tool = new FanoutTool(params);
		
		EntryIdsByRoutine result = tool.getResult(input);
		MToolRoutineCommon.testResultRoutines(result, routinesUnderTest);
		
		for (String routineUnderTest : routinesUnderTest) {
			MToolRoutineCommon.testResultLabels(result, pts, routineUnderTest);
		}
		
		MToolRoutineCommon.testZeroResults(result, "RFIOC000", "RFIOC000", "FROM2", "BE0", "INTD", "INTE", "COMD", "COME");
		MToolRoutineCommon.testResults(result, "RFIOC000", "TOPD", "RFIOC001", "CD0");
		MToolRoutineCommon.testResults(result, "RFIOC000", "TOPE", "RFIOD001", "COMD", "RFIOB001", "COME");
		
		MToolRoutineCommon.testZeroResults(result, "RFIOD001", "RFIOD001", "DD0", "COMD", "COME");
		
		MToolRoutineCommon.testZeroResults(result, "RFIOA002", "RFIOA002");
	}

	@Test
	public void testFanoutsResultSpecified0() {
		testFanoutsResultSpecified0(ptsCache);
		testFanoutsResultSpecified0(pts95);		
	}
	
	private void testFanoutsResultSpecified1(ParseTreeSupply pts) {
		List<String> routinesUnderTest = Arrays.asList("RFIOD000", "RFIOA001", "RFIOB002");
		MRoutineToolInput input = new MRoutineToolInput();
		input.addRoutines(routinesUnderTest);
		
		RoutineToolParams params = new RoutineToolParams(pts);
		params.setResultRoutineFilter(Arrays.asList("RFIOA.*", "RFIOC.*"));
		FanoutTool tool = new FanoutTool(params);
		
		EntryIdsByRoutine result = tool.getResult(input);
		MToolRoutineCommon.testResultRoutines(result, routinesUnderTest);
		
		for (String routineUnderTest : routinesUnderTest) {
			MToolRoutineCommon.testResultLabels(result, pts, routineUnderTest);
		}
		
		MToolRoutineCommon.testZeroResults(result, "RFIOD000", "RFIOD000", "FROM2", "CE0", "INTD", "INTE", "COMD", "COME");
		MToolRoutineCommon.testResults(result, "RFIOD000", "TOPD",  "RFIOC000", "COMD");
		MToolRoutineCommon.testResults(result, "RFIOD000", "TOPE",  "RFIOA001", "COMD", "RFIOA000", "DE0", "RFIOC001", "COME");
		
		MToolRoutineCommon.testZeroResults(result, "RFIOA001", "RFIOA001", "AD0", "COMD", "COME");
		
		MToolRoutineCommon.testZeroResults(result, "RFIOB002", "RFIOB002");
	}

	@Test
	public void testFanoutsResultSpecified1() {
		testFanoutsResultSpecified0(ptsCache);
		testFanoutsResultSpecified1(pts95);		
	}	
}