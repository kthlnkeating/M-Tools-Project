package com.pwc.us.rgi.m.token;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.struct.MRoutineContent;
import com.pwc.us.rgi.m.token.MLine;
import com.pwc.us.rgi.m.token.MRoutine;
import com.pwc.us.rgi.m.token.MTFSupply;
import com.pwc.us.rgi.m.token.MVersion;
import com.pwc.us.rgi.m.token.TFRoutine;
import com.pwc.us.rgi.m.tool.routine.error.ErrorRecorder;
import com.pwc.us.rgi.m.tool.routine.error.ErrorsByLabel;
import com.pwc.us.rgi.parsergen.ParseException;
import com.pwc.us.rgi.vista.repository.FileSupply;
import com.pwc.us.rgi.vista.tools.ErrorExemptions;

import junit.framework.Assert;

public class VistAFOIATest {
	@Test
	public void testAll() {
		try {
			MTFSupply m = MTFSupply.getInstance(MVersion.CACHE);
			final TFRoutine tf = new TFRoutine(m);
			final ErrorExemptions exemptions = ErrorExemptions.getVistAFOIAInstance();
			List<Path> paths = FileSupply.getAllMFiles();
			for (Path path : paths) {
				MRoutineContent content = MRoutineContent.getInstance(path); 
				MRoutine r = tf.tokenize(content);
				List<String> lines = content.getLines();
				List<MLine> results = r.asList();
				int count = results.size();
				Assert.assertEquals(lines.size(), count);
				for (int i=0; i<count; ++i) {
					String line = lines.get(i);
					MLine result = results.get(i);
					String readLine = result.toValue().toString();
					String msg = path.getFileName().toString() + " Line " +  String.valueOf(i);
					Assert.assertEquals("Different: " + msg, line, readLine);
				}
				ErrorRecorder ev = new ErrorRecorder(exemptions);
				ev.setOnlyFatal(true);
				Routine routine = r.getNode();
				ErrorsByLabel errors = ev.getErrors(routine);
				Assert.assertTrue(errors.isEmpty());						
			}			
		} catch (ParseException e) {
			fail("Exception: " + e.getMessage());			
		} catch (IOException e) {
			fail("Exception: " + e.getMessage());			
		}
	}
}
