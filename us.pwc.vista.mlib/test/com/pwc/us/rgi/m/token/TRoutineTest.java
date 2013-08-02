package com.pwc.us.rgi.m.token;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pwc.us.rgi.m.MTestCommon;
import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.parsetree.visitor.FanoutRecorder;
import com.pwc.us.rgi.m.struct.LineLocation;
import com.pwc.us.rgi.m.token.MRoutine;
import com.pwc.us.rgi.m.token.MTFSupply;
import com.pwc.us.rgi.m.token.MVersion;

public class TRoutineTest {
	private static MTFSupply supplyStd95;
	private static MTFSupply supplyCache;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		supplyStd95 = MTFSupply.getInstance(MVersion.ANSI_STD_95);
		supplyCache = MTFSupply.getInstance(MVersion.CACHE);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		supplyStd95 = null;
		supplyCache = null;
	}
	
	private void checkFanouts(List<EntryId> result, String[] labels, String[] routines) {
		Assert.assertNotNull(result);
		Assert.assertEquals(routines.length, result.size());
		int index = 0;
		for (EntryId fout : result) {
			if (routines[index] == null) {
				Assert.assertNull(fout.getRoutineName());
			} else {
				Assert.assertEquals(routines[index], fout.getRoutineName());
			}
			if (labels[index] == null) {
				Assert.assertNull(fout.getTag());
			} else {
				Assert.assertEquals(labels[index], fout.getTag());				
			}
			++index;
		}
	}
	
	private void testCmdTest0(MTFSupply m) {
		String routineName = "CMDTEST0";
		MRoutine token = MTestCommon.getRoutineToken(routineName, m);
		Routine r = token.getNode();

		FanoutRecorder foutr = new FanoutRecorder();
		Map<LineLocation, List<EntryId>> fanouts = foutr.getFanouts(r);	
		List<EntryId> do1 = fanouts.get(new LineLocation("DO", 1));
		this.checkFanouts(do1, new String[]{"L0", "L1", "L2"}, new String[]{"R0", "R1", "R3"});
		List<EntryId> do3 = fanouts.get(new LineLocation("DO", 3));
		this.checkFanouts(do3, new String[]{"T0", "T1", "T2"}, new String[]{null, null, null});
		List<EntryId> do4 = fanouts.get(new LineLocation("DO", 4));
		this.checkFanouts(do4, new String[]{"T2"}, new String[]{null});
		List<EntryId> do5 = fanouts.get(new LineLocation("DO", 5));
		this.checkFanouts(do5, new String[]{"T0", "AR", "T1"}, new String[]{null, null, null});
		List<EntryId> do9 = fanouts.get(new LineLocation("DO", 9));
		this.checkFanouts(do9, new String[]{"T5"}, new String[]{"R5"});
		List<EntryId> do10 = fanouts.get(new LineLocation("DO", 10));
		this.checkFanouts(do10, new String[]{"2", "3", "7", "T8"}, new String[]{"R6", null, "R6", "R8"});
		List<EntryId> do13 = fanouts.get(new LineLocation("DO", 13));
		this.checkFanouts(do13, new String[]{null}, new String[]{"R10"});
		List<EntryId> do15 = fanouts.get(new LineLocation("DO", 15));
		this.checkFanouts(do15, new String[]{"A"}, new String[]{"X"});
		List<EntryId> do24 = fanouts.get(new LineLocation("DO", 24));
		this.checkFanouts(do24, new String[]{"AX"}, new String[]{"RX"});
		
		List<EntryId> go1 = fanouts.get(new LineLocation("GOTO", 1));
		this.checkFanouts(go1, new String[]{"L0", "L1", "L2"}, new String[]{"R0", "R1", "R2"});
		List<EntryId> go3 = fanouts.get(new LineLocation("GOTO", 3));
		this.checkFanouts(go3, new String[]{"T0", "T1", "T2"}, new String[]{null, null, null});
		List<EntryId> go4 = fanouts.get(new LineLocation("GOTO", 4));
		this.checkFanouts(go4, new String[]{"T2"}, new String[]{null});
		List<EntryId> go5 = fanouts.get(new LineLocation("GOTO", 5));
		this.checkFanouts(go5, new String[]{"T0", "T1"}, new String[]{null, null});
		List<EntryId> go9 = fanouts.get(new LineLocation("GOTO", 9));
		this.checkFanouts(go9, new String[]{"T5"}, new String[]{"R5"});
		List<EntryId> go10 = fanouts.get(new LineLocation("GOTO", 10));
		this.checkFanouts(go10, new String[]{"2", "3", "7", "T8"}, new String[]{"R6", null, "R6", "R8"});
		List<EntryId> go13 = fanouts.get(new LineLocation("GOTO", 13));
		this.checkFanouts(go13, new String[]{null}, new String[]{"R10"});
		List<EntryId> go19 = fanouts.get(new LineLocation("GOTO", 19));
		this.checkFanouts(go19, new String[]{"DE", "ARZ"}, new String[]{"RE", null});
		List<EntryId> go20 = fanouts.get(new LineLocation("GOTO", 20));
		this.checkFanouts(go20, new String[]{"DFR", "DF"}, new String[]{"JUH", "GDE"});
				
		Assert.assertEquals(18, fanouts.size());
		int fanoutCount = 0;
		for (List<EntryId> lfo : fanouts.values()) {
			fanoutCount += lfo.size();
		}
		Assert.assertEquals(37, fanoutCount);
}

	@Test
	public void testCmdTest0() {
		testCmdTest0(supplyCache);
		testCmdTest0(supplyStd95);		
	}
}
