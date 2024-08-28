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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestTrustedCertCategory {

	@Test
	public void testValueOf() {
		final TrustedCertCategory catc = TrustedCertCategory.valueOf("CORBA_PEERS");
		assertEquals(TrustedCertCategory.CORBA_PEERS, catc);
	}
	@Test
	public void testToString() {
		final TrustedCertCategory catc = TrustedCertCategory.CORBA_PEERS;
		assertEquals("CORBA_PEERS", catc.toString());		
	}
	@Test
	public void testIscfString() {
		final TrustedCertCategory catc = TrustedCertCategory.CORBA_PEERS;
		assertEquals("corbaPeer", catc.getIscfCategoryName());		
		
		final TrustedCertCategory cati = TrustedCertCategory.IPSEC;
		assertEquals("ipsecPeer", cati.getIscfCategoryName());
	}
}
