package com.pwc.us.rgi.m.tool.routine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.tool.EntryIdsByRoutine;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.ResultsByLabel;

public class MToolRoutineCommon {
	public static void testZeroResults(EntryIdsByRoutine result, String routineName, String... tags) {
		ResultsByLabel<EntryId, Set<EntryId>> resultsByLabel = result.getResults(routineName);
		for (String tag : tags) {
			Set<EntryId> actualTagResults = resultsByLabel.getResults(tag);
			Assert.assertTrue(actualTagResults.size() == 0);			
		}
		List<String> actualEmpty = resultsByLabel.getLabelsWithEmptyResults();
		Set<String> expectedEmpty = new HashSet<String>(Arrays.asList(tags));
		Assert.assertEquals(expectedEmpty.size(), actualEmpty.size());
		for (String a : actualEmpty) {
			Assert.assertTrue(expectedEmpty.contains(a));			
		}	
	}
	
	public static void testResults(EntryIdsByRoutine result, String routineName, String tagName, String... tagResults) {
		ResultsByLabel<EntryId, Set<EntryId>> resultsByLabel = result.getResults(routineName);
		Set<EntryId> actualTagResults = resultsByLabel.getResults(tagName);
		int expectedSize = tagResults.length / 2;
		Assert.assertEquals(expectedSize, actualTagResults.size());
		List<EntryId> expectedTagResult = new ArrayList<EntryId>(expectedSize);
		for (int i=0; i<expectedSize; ++i) {
			EntryId entryId = new EntryId(tagResults[2*i], tagResults[2*i+1]);
			expectedTagResult.add(entryId);
		}
		for (EntryId eid : expectedTagResult) {
			Assert.assertTrue(actualTagResults.contains(eid));
		}

	}
	
	public static void testResultLabels(EntryIdsByRoutine result, ParseTreeSupply pts, String routineName) {
		ResultsByLabel<EntryId, Set<EntryId>> resultsByLabel = result.getResults(routineName);
		Routine routine = pts.getParseTree(routineName);
		List<String> topTags = routine.getTopTags();
		Set<String> actualTags = resultsByLabel.getLabels();
		Assert.assertEquals(topTags.size(), actualTags.size());
		for (String r : topTags) {
			Assert.assertTrue(actualTags.contains(r));
		}
	}
		
	public static void testResultRoutines(EntryIdsByRoutine result, List<String> routinesUnderTest) {
		Set<String> resultRoutines = result.getRoutineNames();
		Assert.assertEquals(routinesUnderTest.size(), resultRoutines.size());
		for (String r : routinesUnderTest) {
			Assert.assertTrue(resultRoutines.contains(r));
		}
	}
}
