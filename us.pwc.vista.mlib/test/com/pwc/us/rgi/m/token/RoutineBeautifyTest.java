package com.pwc.us.rgi.m.token;

import java.util.List;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pwc.us.rgi.m.MTestCommon;
import com.pwc.us.rgi.m.struct.MRefactorSettings;
import com.pwc.us.rgi.m.token.MLine;
import com.pwc.us.rgi.m.token.MRoutine;
import com.pwc.us.rgi.m.token.MTFSupply;
import com.pwc.us.rgi.m.token.MVersion;

public class RoutineBeautifyTest {
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

	private void testrefactor(MTFSupply m, String routineName, String targetRoutineName) {
		MRoutine original = MTestCommon.getRoutineToken(routineName, m);
		MRoutine source = MTestCommon.getRoutineToken(routineName, m);
		MRoutine result = MTestCommon.getRoutineToken(targetRoutineName, m);
		
		source.refactor(new MRefactorSettings());
		List<MLine> originalLines = original.asList();
		List<MLine> sourceLines = source.asList();
		List<MLine> resultLines = result.asList();
		int n = resultLines.size();
		Assert.assertEquals( originalLines.size(), resultLines.size());
		Assert.assertEquals(sourceLines.size(), resultLines.size());
		for (int i=1; i<n; ++i) {
			String sourceLineValue = sourceLines.get(i).toValue().toString();
			String resultLineValue = resultLines.get(i).toValue().toString();
			Assert.assertEquals(sourceLineValue, resultLineValue);
			if ((i > 4) && (i < 12)) {
				Assert.assertFalse(sourceLineValue.equals(originalLines.get(i).toValue().toString()));				
			}
		}
		
		String a = source.toValue().toString();
		String b = result.toValue().toString();
		Assert.assertEquals(a, b);
	}
	
	public void testrefactor(MTFSupply m) {
		testrefactor(m, "BEAT0SRC", "BEAT0RST");
		testrefactor(m, "BEAT1SRC", "BEAT1RST");
	}
	
	@Test
	public void testrefactor() {
		testrefactor(supplyCache);
		testrefactor(supplyStd95);
	}
}
