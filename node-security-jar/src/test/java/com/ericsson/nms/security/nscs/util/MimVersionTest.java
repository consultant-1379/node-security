package com.ericsson.nms.security.nscs.util;

import org.junit.Test;

import com.ericsson.nms.security.nscs.api.exception.InvalidVersionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MimVersionTest {

  @Test(expected=InvalidVersionException.class)
  public void testNullMimVersion() {
	  MimVersion mimVersion = new MimVersion(null);
	  assertFalse(mimVersion.isValid());
  }

  @Test(expected=InvalidVersionException.class)
  public void testEmptyStringMimVersion() {
	  MimVersion mimVersion = new MimVersion("");
	  assertFalse(mimVersion.isValid());
  }

  @Test(expected=InvalidVersionException.class)
  public void testOneDotStringMimVersion() {
	  MimVersion mimVersion = new MimVersion(".");
	  assertFalse(mimVersion.isValid());
  }

  @Test(expected=InvalidVersionException.class)
  public void testTwoDotsStringMimVersion() {
	  MimVersion mimVersion = new MimVersion("..");
	  assertFalse(mimVersion.isValid());
  }

  @Test(expected=InvalidVersionException.class)
  public void testEmptyFieldsStringMimVersion() {
	  MimVersion mimVersion = new MimVersion(" . . ");
	  assertFalse(mimVersion.isValid());
  }

  @Test(expected=InvalidVersionException.class)
  public void testMalformedStringMimVersion() {
	  MimVersion mimVersion = new MimVersion("5,1,3");
	  assertFalse(mimVersion.isValid());
  }

  @Test(expected=InvalidVersionException.class)
  public void testTooLongStringMimVersion() {
	  MimVersion mimVersion = new MimVersion("55.11.33.44");
	  assertFalse(mimVersion.isValid());
  }

  @Test(expected=InvalidVersionException.class)
  public void testOnlyMajorMimVersion() {
	  MimVersion mimVersion = new MimVersion("5");
	  assertFalse(mimVersion.isValid());
  }

  @Test(expected=InvalidVersionException.class)
  public void testOnlyMajorMalformedMimVersion() {
	  MimVersion mimVersion = new MimVersion("5.");
	  assertFalse(mimVersion.isValid());
  }

  @Test(expected=InvalidVersionException.class)
  public void testOnlyMajorAndMinorMimVersion() {
	  MimVersion mimVersion = new MimVersion("5.1");
	  assertFalse(mimVersion.isValid());
  }

  @Test(expected=InvalidVersionException.class)
  public void testOnlyMajorAndMinorMalformedStringMimVersion() {
	  MimVersion mimVersion = new MimVersion("55.11.");
	  assertFalse(mimVersion.isValid());
  }

  @Test(expected=InvalidVersionException.class)
  public void testLowerCaseMajorMimVersion() {
	  MimVersion mimVersion = new MimVersion("d.1.3");
	  assertFalse(mimVersion.isValid());
  }

  @Test(expected=InvalidVersionException.class)
  public void testMalformedMajorMimVersion() {
	  MimVersion mimVersion = new MimVersion("F5.1.3");
	  assertFalse(mimVersion.isValid());
  }

  @Test(expected=InvalidVersionException.class)
  public void testDoubleLiteralMajorMimVersion() {
	  MimVersion mimVersion = new MimVersion("AA.1.3");
	  assertFalse(mimVersion.isValid());
  }

  @Test(expected=InvalidVersionException.class)
  public void testMalformedMinorMimVersion() {
	  MimVersion mimVersion = new MimVersion("5.A.3");
	  assertFalse(mimVersion.isValid());
  }

  @Test(expected=InvalidVersionException.class)
  public void testMalformedPatchMimVersion() {
	  MimVersion mimVersion = new MimVersion("5.1.C");
	  assertFalse(mimVersion.isValid());
  }

  @Test
  public void testCorrectMimVersion() {
	  MimVersion mimVersion = new MimVersion("4.1.3");
	  assertTrue(mimVersion.isValid());
	  assertEquals(mimVersion.getMajorStr(), "4");
	  assertTrue(mimVersion.getMajorInt() == 4);
	  assertEquals(mimVersion.getMinorStr(), "1");
	  assertTrue(mimVersion.getMinorInt() == 1);
	  assertEquals(mimVersion.getPatchStr(), "3");
	  assertTrue(mimVersion.getPatchInt() == 3);
  }

  @Test
  public void testCorrectLiteralMimVersion() {
	  MimVersion mimVersion = new MimVersion("D.1.3");
	  assertTrue(mimVersion.isValid());
	  assertEquals(mimVersion.getMajorStr(), "D");
	  assertTrue(mimVersion.getMajorInt() == 4);
	  assertEquals(mimVersion.getMinorStr(), "1");
	  assertTrue(mimVersion.getMinorInt() == 1);
	  assertEquals(mimVersion.getPatchStr(), "3");
	  assertTrue(mimVersion.getPatchInt() == 3);
  }
  
  @Test(expected=InvalidVersionException.class)
  public void testIsEqualMalformedMimVersion() {
	  MimVersion mimVersion = new MimVersion("5.1.5");
	  assertTrue(mimVersion.isValid());
	  mimVersion.isEqualTo("e.1.5");
  }
  
  @Test(expected=InvalidVersionException.class)
  public void testIsLessThanMalformedMimVersion() {
	  MimVersion mimVersion = new MimVersion("5.1.5");
	  assertTrue(mimVersion.isValid());
	  mimVersion.isLessThan("e.1.5");
  }
  
  @Test(expected=InvalidVersionException.class)
  public void testIsLessThanOrEqualMalformedMimVersion() {
	  MimVersion mimVersion = new MimVersion("5.1.5");
	  assertTrue(mimVersion.isValid());
	  mimVersion.isLessThanOrEqualTo("e.1.5");
  }
  
  @Test(expected=InvalidVersionException.class)
  public void testIsGreaterThanMalformedMimVersion() {
	  MimVersion mimVersion = new MimVersion("5.1.5");
	  assertTrue(mimVersion.isValid());
	  mimVersion.isGreaterThan("e.1.5");
  }
  
  @Test(expected=InvalidVersionException.class)
  public void testIsGreaterThanOrEqualMalformedMimVersion() {
	  MimVersion mimVersion = new MimVersion("5.1.5");
	  assertTrue(mimVersion.isValid());
	  mimVersion.isGreaterThanOrEqualTo("e.1.5");
  }
  
  @Test
  public void testIsEqualMimVersion() {
	  MimVersion mimVersion = new MimVersion("5.1.5");
	  assertTrue(mimVersion.isValid());
	  mimVersion.isEqualTo("5.1.5");
  }
  
  @Test
  public void testIsEqualLiteralMimVersion() {
	  MimVersion mimVersion = new MimVersion("5.1.5");
	  assertTrue(mimVersion.isValid());
	  mimVersion.isEqualTo("E.1.5");
  }
  
  @Test
  public void testIsLessThanMimVersion() {
	  MimVersion mimVersion = new MimVersion("5.1.5");
	  assertTrue(mimVersion.isValid());
	  assertTrue(mimVersion.isLessThan("6.1.5"));
	  assertTrue(mimVersion.isLessThan("5.2.5"));
	  assertTrue(mimVersion.isLessThan("5.1.6"));
  }
  
  @Test
  public void testIsLessThanOrEqualToMimVersion() {
	  MimVersion mimVersion = new MimVersion("5.1.5");
	  assertTrue(mimVersion.isValid());
	  assertTrue(mimVersion.isLessThanOrEqualTo("5.1.5"));
	  assertTrue(mimVersion.isLessThanOrEqualTo("6.1.5"));
	  assertTrue(mimVersion.isLessThanOrEqualTo("5.2.5"));
	  assertTrue(mimVersion.isLessThanOrEqualTo("5.1.6"));
  }
  
  @Test
  public void testIsGreaterThanMimVersion() {
	  MimVersion mimVersion = new MimVersion("5.1.5");
	  assertTrue(mimVersion.isValid());
	  assertTrue(mimVersion.isGreaterThan("4.1.5"));
	  assertTrue(mimVersion.isGreaterThan("5.0.5"));
	  assertTrue(mimVersion.isGreaterThan("5.1.4"));
  }
  
  @Test
  public void testIsGreaterThanOrEqualToMimVersion() {
	  MimVersion mimVersion = new MimVersion("5.1.5");
	  assertTrue(mimVersion.isValid());
	  assertTrue(mimVersion.isGreaterThanOrEqualTo("5.1.5"));
	  assertTrue(mimVersion.isGreaterThanOrEqualTo("4.1.5"));
	  assertTrue(mimVersion.isGreaterThanOrEqualTo("5.0.5"));
	  assertTrue(mimVersion.isGreaterThanOrEqualTo("5.1.4"));
  }
  
  @Test
  public void test51200MimVersion() {
	  MimVersion mimVersion = new MimVersion("5.1.200");
	  assertTrue(mimVersion.isValid());
	  assertTrue(mimVersion.isGreaterThanOrEqualTo("5.1.200"));
  }
 
  @Test(expected=InvalidVersionException.class)
  public void testIsLessThanInvalidMimVersion() {
	  MimVersion mimVersion = new MimVersion("1.1.100");
	  assertTrue(mimVersion.isValid());
	  assertTrue(mimVersion.isLessThan("1.1"));
  }
  
  @Test(expected=InvalidVersionException.class)
  public void testIsLessThanOrEqualToInvalidMimVersion() {
	  MimVersion mimVersion = new MimVersion("1.1.100");
	  assertTrue(mimVersion.isValid());
	  assertTrue(mimVersion.isLessThanOrEqualTo(".1.100"));
  }
  
  @Test(expected=InvalidVersionException.class)
  public void testIsEqualToInvalidMimVersion() {
	  MimVersion mimVersion = new MimVersion("1.1.100");
	  assertTrue(mimVersion.isValid());
	  assertTrue(mimVersion.isEqualTo("1.A.100"));
  }
  
  @Test(expected=InvalidVersionException.class)
  public void testIsGreaterThanInvalidMimVersion() {
	  MimVersion mimVersion = new MimVersion("1.1.100");
	  assertTrue(mimVersion.isValid());
	  assertTrue(mimVersion.isGreaterThan("1.1 99"));
  }
  
  @Test(expected=InvalidVersionException.class)
 public void testIsGreaterThanOrEqualToInvalidMimVersion() {
	  MimVersion mimVersion = new MimVersion("1.1.100");
	  assertTrue(mimVersion.isValid());
	  assertTrue(mimVersion.isGreaterThanOrEqualTo("1_1_100"));
  }
 
}
