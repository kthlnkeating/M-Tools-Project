package com.pwc.us.rgi.m.tool.entry.quittype;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pwc.us.rgi.m.MTestCommon;
import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.struct.CodeLocation;
import com.pwc.us.rgi.m.tool.CommonToolParams;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.entry.RecursionDepth;
import com.pwc.us.rgi.m.tool.entry.assumedvariables.AssumedVariablesToolParams;
import com.pwc.us.rgi.m.tool.entry.quittype.CallType;
import com.pwc.us.rgi.m.tool.entry.quittype.CallTypeState;
import com.pwc.us.rgi.m.tool.entry.quittype.QuitType;
import com.pwc.us.rgi.m.tool.entry.quittype.QuitTypeState;
import com.pwc.us.rgi.m.tool.entry.quittype.QuitTypeTool;

public class QTTTest {
	private static ParseTreeSupply pts;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String[] routineNames = {"QTTTEST0", "QTTTEST1", "QTTTEST2"};
		pts = MTestCommon.getParseTreeSupply(routineNames);		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		pts = null;
	}

	private static void testCodeLocation(CodeLocation underTest, String routineName, int lineIndex) {
		CodeLocation cl = new CodeLocation(routineName, lineIndex);
		Assert.assertEquals(underTest, cl);					
	}
	
	private static QuitTypeTool getTool(RecursionDepth depth) {
		CommonToolParams p = new AssumedVariablesToolParams(pts);
		p.getRecursionSpecification().setDepth(RecursionDepth.ALL);
		QuitTypeTool qtt = new QuitTypeTool(p);
		return qtt;
	}
	
	@Test
	public void testQTTTest0() {
    	QuitTypeTool qtt = getTool(RecursionDepth.ALL);
		
		EntryId id = new EntryId("QTTTEST0","SUMXYZ");
		QuitType qt = qtt.getResult(id);
		
		QuitTypeState qts = qt.getQuitTypeState();
		Assert.assertEquals(QuitTypeState.CONFLICTING_QUITS, qts);			
		CodeLocation fql = qt.getFirstQuitLocation();
		testCodeLocation(fql, "QTTTEST0", 16);
		CodeLocation cql = qt.getConflictingLocation();
		testCodeLocation(cql, "QTTTEST0", 19);
		
		CallType ct = qt.getFanout(new EntryId("QTTTEST1", "SUMZ"));
		Assert.assertNotNull(ct);
		Assert.assertEquals(CallTypeState.DO_CONFLICTING, ct.getState());	
	}

	@Test
	public void testQTTTest1() {
    	QuitTypeTool qtt = getTool(RecursionDepth.ALL);
		
		EntryId id = new EntryId("QTTTEST0","SUMALL");
		QuitType qt = qtt.getResult(id);
		
		QuitTypeState qts = qt.getQuitTypeState();
		Assert.assertEquals(QuitTypeState.QUITS_WITHOUT_VALUE, qts);			
		
		CallType ct = qt.getFanout(new EntryId("QTTTEST1", "THRUERR0"));
		Assert.assertNull(ct);
	}

	@Test
	public void testQTTTest2() {
    	QuitTypeTool qtt = getTool(RecursionDepth.ALL);
		
		EntryId id = new EntryId("QTTTEST1","THRUERR0");
		QuitType qt = qtt.getResult(id);
		
		QuitTypeState qts = qt.getQuitTypeState();
		Assert.assertEquals(QuitTypeState.QUITS_WITHOUT_VALUE, qts);			
		
		CallType ct = qt.getFanout(new EntryId("QTTTEST2", "EXTR"));
		Assert.assertNotNull(ct);
		Assert.assertEquals(CallTypeState.DO_CONFLICTING, ct.getState());	
	}

	@Test
	public void testQTTTest3() {
    	QuitTypeTool qtt = getTool(RecursionDepth.ALL);
		
		EntryId id = new EntryId("QTTTEST1","THRUGOOD");
		QuitType qt = qtt.getResult(id);
		
		QuitTypeState qts = qt.getQuitTypeState();
		Assert.assertEquals(QuitTypeState.QUITS_WITH_VALUE, qts);			
		
		CallType ct0 = qt.getFanout(new EntryId("QTTTEST2", "EXTR"));
		Assert.assertNotNull(ct0);
		Assert.assertEquals(CallTypeState.EXTRINSIC_VERIFIED, ct0.getState());	
	
		CallType ct1 = qt.getFanout(new EntryId(null, "THRUERR0"));
		Assert.assertNotNull(ct1);
		Assert.assertEquals(CallTypeState.EXTRINSIC_CONFLICTING, ct1.getState());	
	}
}
