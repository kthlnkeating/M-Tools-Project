package gov.va.mumps.debug.xtdebug;

import gov.va.mumps.debug.xtdebug.StepResultsParser;
import gov.va.mumps.debug.xtdebug.vo.ReadResultsVO;
import gov.va.mumps.debug.xtdebug.vo.StackVO;
import gov.va.mumps.debug.xtdebug.vo.StepResultsVO;
import gov.va.mumps.debug.xtdebug.vo.VariableVO;
import gov.va.mumps.debug.xtdebug.vo.WatchVO;
import gov.va.mumps.debug.xtdebug.vo.StepResultsVO.ResultReasonType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class StepResultsParserTest {
	
	@Test
	public void testParse() throws IOException {
		
		StepResultsParser testMe = new StepResultsParser();
		StepResultsVO vo;

		//Test a result coming back due to a step completing
		vo = testMe.parse(getDataFromFile("stepResult1.txt"));
		Assert.assertFalse(vo.isComplete());
		Assert.assertEquals("TSTBLAH2", vo.getRoutineName());
		Assert.assertEquals("W \"IM IN STACK3\"", vo.getNextCommnd());
		Assert.assertEquals(13, vo.getLineLocation());
		Assert.assertEquals("STACK3+2^TSTBLAH2", vo.getLocationAsTag());
		Iterator<StackVO> stackItr = vo.getStack();
		StackVO stack = stackItr.next();
		Assert.assertNull(stack.getCaller());
		Assert.assertEquals("STACK2^TSTBLAH2", stack.getStackName());
		stack = stackItr.next();
		Assert.assertEquals("STACK2+3^TSTBLAH2", stack.getCaller());
		Assert.assertEquals("STACK3^TSTBLAH2", stack.getStackName());
		
		Iterator<VariableVO> varItr = vo.getVariables();
		VariableVO var = varItr.next();
		Assert.assertEquals("%DT", var.getName());
		Assert.assertEquals("T", var.getValue());
		
		Assert.assertNull(vo.getWriteLine());
		
		//test breakpoint result coming back
		vo = testMe.parse(getDataFromFile("breakResult1.txt"));
		Assert.assertEquals(ResultReasonType.BREAKPOINT, vo.getResultReason());
		Assert.assertEquals("STACK5+1^TSTROUT", vo.getLocationAsTag());
		
		//test watchpoint
		vo = testMe.parse(getDataFromFile("watchResult1.txt"));
		Assert.assertEquals(ResultReasonType.WATCHPOINT, vo.getResultReason());
		Assert.assertEquals("STACK5+2^TSTROUT", vo.getLocationAsTag());
		Iterator<WatchVO> watchItr = vo.getWatchedVars();
		Assert.assertTrue(watchItr.hasNext());
		Assert.assertEquals("Q", watchItr.next().getVariableName());
		//TODO: assert prev and new values of variable when implemented and fixed
		
		//test Write results
		vo = testMe.parse(getDataFromFile("writeResult1.txt"));
		Assert.assertEquals(ResultReasonType.WRITE, vo.getResultReason());
		Assert.assertEquals("STACK5+3^TSTROUT", vo.getLocationAsTag());
		Assert.assertEquals("IM IN STACK2IM IN STACK5", vo.getWriteLine());
		
		//test read results
		vo = testMe.parse(getDataFromFile("readResult1.txt"));
		Assert.assertEquals(ResultReasonType.READ, vo.getResultReason());
		Assert.assertEquals("STACK5+5^TSTROUT", vo.getLocationAsTag());
		Assert.assertEquals("TEST PROMPT: ", vo.getWriteLine());
		ReadResultsVO readVO = vo.getReadResults();
		Assert.assertEquals(null, readVO.getMaxChars());
		Assert.assertEquals(30, (int)readVO.getTimeout());
		Assert.assertEquals(false, readVO.isStarRead());
		Assert.assertEquals(false, readVO.isTypeAhead());
	}
	
	@Ignore
	public String getDataFromFile(String fileName) {
		InputStream in = this.getClass().getResourceAsStream("testdata/" +fileName);

		StringBuilder sb=new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String read = null;
		try {
			read = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		while(read != null) {
		    sb.append(read);
		    sb.append("\n");
		    try {
				read = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return sb.toString();

	}
	
}
