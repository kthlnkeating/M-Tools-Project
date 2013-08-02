package com.pwc.us.rgi.m.struct;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.pwc.us.rgi.m.struct.LineLocation;

public class MLineLocationTest {	
	@Test
	public void testHashCode() {
		LineLocation p = new LineLocation("BEGIN", 3);
		Set<LineLocation> c = new HashSet<LineLocation>();
		c.add(p);
		Assert.assertTrue(c.contains(new LineLocation("BEGIN", 3)));
		Assert.assertFalse(c.contains(new LineLocation("BEGIN", 4)));
		Assert.assertFalse(c.contains(new LineLocation("BEGINX", 3)));
		Assert.assertTrue(c.contains(new LineLocation("BEGIN", 3)));
		c.add(new LineLocation("BEGIN", 3));
		Assert.assertEquals(1, c.size());
		c.add(new LineLocation("ARV", 2));
		Assert.assertEquals(2, c.size());
	}
		
	@Test
	public void testEquals() {
		LineLocation lhs0 = new LineLocation("BEGIN", 3);
		LineLocation rhs00 = new LineLocation("BEGIN", 3);
		Assert.assertEquals(lhs0, rhs00);
		LineLocation rhs01 = new LineLocation("BEGIN", 2);
		Assert.assertFalse(lhs0.equals(rhs01));
		LineLocation rhs02 = new LineLocation("BEGINX", 3);
		Assert.assertFalse(lhs0.equals(rhs02));
	}
}
