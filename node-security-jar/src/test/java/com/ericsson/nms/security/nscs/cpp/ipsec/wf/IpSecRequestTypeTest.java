/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.security.nscs.cpp.ipsec.wf;

import org.junit.Assert;
import org.junit.Test;

public class IpSecRequestTypeTest {

	@Test
	public void testInstantiation() {
		final IpSecRequestType ipSecRequestType = IpSecRequestType.IP_SEC_DISABLE;
		Assert.assertNotNull(ipSecRequestType.getRequestType());
	}
	
	@Test
	public void testValueOf() {
		final IpSecRequestType ipSecRequestType = IpSecRequestType.valueOf("IP_SEC_DISABLE");
		Assert.assertNotNull(ipSecRequestType.getRequestType());
	}

}
