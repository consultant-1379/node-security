/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.data.moaction;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestMoActionWithParameters {

	@Test
	public void testValueOf() {
		final MoActionWithParameter param = MoActionWithParameter.valueOf("Security_initCertEnrollment");
		assertNotNull(param);		
	} 
	@Test
	public void testValueOfThrowsException() {
		try {
			MoActionWithParameter.valueOf("initCertEnrollment");
			fail();
		} catch (final IllegalArgumentException e) {
			//ok
		}				
	}
}
