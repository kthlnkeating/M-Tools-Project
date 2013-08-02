package com.pwc.us.rgi.m;

import java.io.InputStream;

import junit.framework.Assert;

import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.struct.MRoutineContent;
import com.pwc.us.rgi.m.token.MRoutine;
import com.pwc.us.rgi.m.token.MTFSupply;
import com.pwc.us.rgi.m.token.MVersion;
import com.pwc.us.rgi.m.token.TFRoutine;
import com.pwc.us.rgi.m.tool.AccumulatingParseTreeAdapter;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.SourceCodeResources;
import com.pwc.us.rgi.m.tool.routine.error.ErrorRecorder;
import com.pwc.us.rgi.m.tool.routine.error.ErrorsByLabel;

public class MTestCommon {	
	public static <T> ParseTreeSupply getParseTreeSupply(String[] routineNames, MVersion version, boolean checkError) {
		int n = routineNames.length;
		String[] resourceNames = new String[n];
		for (int i=0; i<n; ++i) {
			String resourceName = "resource/" + routineNames[i] + ".m";
			resourceNames[i] = resourceName;
		}
		SourceCodeResources<MTestCommon> scr = SourceCodeResources.getInstance(MTestCommon.class, resourceNames);
		ParseTreeSupply pts = new AccumulatingParseTreeAdapter(scr, version);
		if (checkError) for (int i=0; i<routineNames.length; ++i) {
			String routineName = routineNames[i];
			Routine routine = pts.getParseTree(routineName);
			if (checkError) {
				ErrorRecorder er = new ErrorRecorder();
				ErrorsByLabel errors = er.getErrors(routine);
				Assert.assertTrue(errors.isEmpty());
			}
		}
		return pts;
	}
	
	public static <T> ParseTreeSupply getParseTreeSupply(String[] routineNames) {
		return getParseTreeSupply(routineNames, MVersion.CACHE, true);
	}
	
	public static MRoutine getRoutineToken(String routineName, MTFSupply m) {
		TFRoutine tf = new TFRoutine(m);
		String resourceName = "resource/" + routineName + ".m";
		InputStream is = MTestCommon.class.getResourceAsStream(resourceName);
		MRoutineContent content = MRoutineContent.getInstance(routineName, is);
		MRoutine r = tf.tokenize(content);
		return r;
	}
}
