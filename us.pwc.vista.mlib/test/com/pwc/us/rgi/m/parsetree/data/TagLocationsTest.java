package com.pwc.us.rgi.m.parsetree.data;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pwc.us.rgi.m.MTestCommon;
import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.parsetree.data.TagLocations;
import com.pwc.us.rgi.m.struct.LineLocation;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;

public class TagLocationsTest {
	private static ParseTreeSupply pts;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String[] routineNames = {"TAGLOC00"};
		pts = MTestCommon.getParseTreeSupply(routineNames);		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		pts = null;
	}
	
	private void auxTest(TagLocations locations, int lineIndex, String expectedTag, int expectedOffset) {
		LineLocation actual = locations.getLineLocation(lineIndex);
		LineLocation expected = new LineLocation(expectedTag, expectedOffset); 
		Assert.assertEquals(expected, actual);		
	}
		
	@Test
	public void test() {
		Routine r = pts.getParseTree("TAGLOC00"); 
		TagLocations locations = r.getTagLocations();

		auxTest(locations, 0, "TAGLOC00", 0);
		auxTest(locations, 1, "TAGLOC00", 1);
		auxTest(locations, 2, "TAGLOC00", 2);
		auxTest(locations, 3, "E0", 0);
		auxTest(locations, 4, "E0", 1);
		auxTest(locations, 5, "E0", 2);
		auxTest(locations, 6, "IN0", 0);
		auxTest(locations, 7, "IN0", 1);
		auxTest(locations, 8, "IN0", 2);
		auxTest(locations, 9, "IN1", 0);
		auxTest(locations, 10, "IN1", 1);
		auxTest(locations, 11, "IN1", 2);
		auxTest(locations, 12, "IN2", 0);
		auxTest(locations, 13, "IN2", 1);
		auxTest(locations, 14, "IN2", 2);
		auxTest(locations, 15, "IN2", 3);
		auxTest(locations, 16, "E1", 0);
		auxTest(locations, 17, "E1", 1);
		auxTest(locations, 18, "E1", 2);
		auxTest(locations, 19, "E1", 3);
		auxTest(locations, 20, "E1", 4);
		auxTest(locations, 21, "E2", 0);
		auxTest(locations, 22, "E2", 1);
		auxTest(locations, 23, "E2", 2);
		auxTest(locations, 24, "E2", 3);
		auxTest(locations, 25, "E3", 0);
		auxTest(locations, 26, "E3", 1);
		auxTest(locations, 27, "E3", 2);
	}

}
