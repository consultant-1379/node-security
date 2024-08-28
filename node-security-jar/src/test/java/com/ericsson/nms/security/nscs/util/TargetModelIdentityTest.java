package com.ericsson.nms.security.nscs.util;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ericsson.nms.security.nscs.api.exception.InvalidTargetModelIdentityException;

public class TargetModelIdentityTest {

	  @Test(expected=InvalidTargetModelIdentityException.class)
	  public void testNullTargetModelIdentity() {
		  TargetModelIdentity targetModelIdentity = new TargetModelIdentity(null);
		  assertFalse(targetModelIdentity.isValid());
	  }

	  @Test(expected=InvalidTargetModelIdentityException.class)
	  public void testEmptyTargetModelIdentity() {
		  TargetModelIdentity targetModelIdentity = new TargetModelIdentity("");
		  assertFalse(targetModelIdentity.isValid());
	  }

	  @Test(expected=InvalidTargetModelIdentityException.class)
	  public void testTargetModelIdentityWithInvalidOssModelIdentity() {
		  TargetModelIdentity targetModelIdentity = new TargetModelIdentity("ABC");
		  assertFalse(targetModelIdentity.isValid());
	  }

	  @Test
	  public void testTargetModelIdentityWithValidOssModelIdentity() {
		  TargetModelIdentity targetModelIdentity = new TargetModelIdentity("1234-567-890");
		  assertTrue("1234-567-890".equals(targetModelIdentity.getTargetModelIdentity()));
		  assertTrue(targetModelIdentity.isValid());
		  assertTrue(targetModelIdentity.isOssModelIdentity());
		  assertNull(targetModelIdentity.getNodeType());
		  assertNull(targetModelIdentity.getRelease());
		  assertNull(targetModelIdentity.getRevision());
	  }

	  @Test(expected=InvalidTargetModelIdentityException.class)
	  public void testTargetModelIdentityWithInvalidTargetModelIdentityRevision() {
		  TargetModelIdentity targetModelIdentity = new TargetModelIdentity("RadioNode-16A-13NV");
		  assertFalse(targetModelIdentity.isValid());
	  }

	  @Test
	  public void testTargetModelIdentityWithValidTargetModelIdentity() {
		  TargetModelIdentity targetModelIdentity = new TargetModelIdentity("RadioNode-16A-R13NV");
		  assertTrue("RadioNode-16A-R13NV".equals(targetModelIdentity.getTargetModelIdentity()));
		  assertTrue(targetModelIdentity.isValid());
		  assertFalse(targetModelIdentity.isOssModelIdentity());
		  assertTrue("RadioNode".equals(targetModelIdentity.getNodeType()));
		  assertTrue("16A".equals(targetModelIdentity.getRelease()));
		  assertTrue("R13NV".equals(targetModelIdentity.getRevision()));
	  }

          @Test
	  public void testTargetModelIdentityWithValidNewTargetModelIdentity() {
		  TargetModelIdentity targetModelIdentity = new TargetModelIdentity("16B-R28GY");
		  assertTrue("16B-R28GY".equals(targetModelIdentity.getTargetModelIdentity()));
		  assertTrue(targetModelIdentity.isValid());
		  assertFalse(targetModelIdentity.isOssModelIdentity());
		  assertTrue("16B".equals(targetModelIdentity.getRelease()));
		  assertTrue("R28GY".equals(targetModelIdentity.getRevision()));
	  }

          @Test
	  public void testTargetModelIdentityWithValidTargetModelIdentity2() {
		  TargetModelIdentity targetModelIdentity = new TargetModelIdentity("16B-G.12.260");
		  assertTrue("16B-G.12.260".equals(targetModelIdentity.getTargetModelIdentity()));
		  assertTrue(targetModelIdentity.isValid());
		  assertFalse(targetModelIdentity.isOssModelIdentity());
		  assertTrue("16B".equals(targetModelIdentity.getRelease()));
		  assertTrue("G.12.260".equals(targetModelIdentity.getRevision()));
	  }
        /**
         * test method testTargetModelIdentityWithValidTargetModelIdentity3() to check with the pattern TARGET_MODEL_IDENTITY_PATTERN_3
         */
        @Test
          public void testTargetModelIdentityWithValidTargetModelIdentity3Success() {
                  TargetModelIdentity targetModelIdentity = new TargetModelIdentity("R17B-GA");
                  assertTrue("R17B-GA".equals(targetModelIdentity.getTargetModelIdentity()));
                  assertTrue(targetModelIdentity.isValid());
                  assertTrue("17B".equals(targetModelIdentity.getRelease()));
                  assertTrue("GA".equals(targetModelIdentity.getRevision()));
          }

        /**
         * test method testTargetModelIdentityWithValidTargetModelIdentity4() to check with the pattern TARGET_MODEL_IDENTITY_PATTERN_4
         */
        @Test
          public void testTargetModelIdentityWithValidTargetModelIdentity4() {
                  TargetModelIdentity targetModelIdentity = new TargetModelIdentity("17.Q4-J.1.120");
                  assertTrue("17.Q4-J.1.120".equals(targetModelIdentity.getTargetModelIdentity()));
                  assertTrue(targetModelIdentity.isValid());
                  assertTrue("17.Q4".equals(targetModelIdentity.getRelease()));
                  assertTrue("J.1.120".equals(targetModelIdentity.getRevision()));
          }
        /**
         * test method testTargetModelIdentityWithValidTargetModelIdentity5() to check with the pattern TARGET_MODEL_IDENTITY_PATTERN_5
         */
        @Test
          public void testTargetModelIdentityWithValidTargetModelIdentity5() {
                  TargetModelIdentity targetModelIdentity = new TargetModelIdentity("17.Q4-R22A192");
                  assertTrue("17.Q4-R22A192".equals(targetModelIdentity.getTargetModelIdentity()));
                  assertTrue(targetModelIdentity.isValid());
                  assertTrue("17.Q4".equals(targetModelIdentity.getRelease()));
                  assertTrue("R22A192".equals(targetModelIdentity.getRevision()));
          }
        /**GetTrustCertInstallStateHandler
         * test method testMGWTargetModelIdentityWithValid() to check with the pattern TARGET_MODEL_IDENTITY_PATTERN_6
         */
        @Test
        public void testMGWTargetModelIdentityWithValid() {
          TargetModelIdentity targetModelIdentity = new TargetModelIdentity("6.9.2.0");
          assertTrue("6.9.2.0".equals(targetModelIdentity.getTargetModelIdentity()));
          assertTrue("6.9.2.0".equals(targetModelIdentity.getRevision()));
          assertTrue("6.9.2.0".equals(targetModelIdentity.getRelease()));
        }

        @Test(expected=InvalidTargetModelIdentityException.class)
        public void testMGWTargetModelIdentityWithInValid() {
          TargetModelIdentity targetModelIdentity = new TargetModelIdentity("MGW-6.2.2.0");
          assertFalse(targetModelIdentity.isValid());
        }

        @Test
        public void testTargetModelIdentityWithvDUOTargetModelIdentity() {
                TargetModelIdentity targetModelIdentity = new TargetModelIdentity("1.5.3");
                assertTrue("1.5.3".equals(targetModelIdentity.getTargetModelIdentity()));
                assertTrue(targetModelIdentity.isValid());
        }

}
