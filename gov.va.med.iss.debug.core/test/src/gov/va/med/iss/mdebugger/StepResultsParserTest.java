package gov.va.med.iss.mdebugger;

import gov.va.med.iss.mdebugger.vo.StackVO;
import gov.va.med.iss.mdebugger.vo.StepResultsVO;
import gov.va.med.iss.mdebugger.vo.VariableVO;

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
		
		StepResultsVO vo;
//		vo = new StepResultsParser().parse(getDataFromFile("doneResults.txt"));
//		Assert.assertEquals(true, vo.isComplete());
		
		vo = new StepResultsParser().parse(getDataFromFile("stepOutResult1.txt"));
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
		    sb.append("\r\n");
		    try {
				read = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return sb.toString();

	}
	
}
