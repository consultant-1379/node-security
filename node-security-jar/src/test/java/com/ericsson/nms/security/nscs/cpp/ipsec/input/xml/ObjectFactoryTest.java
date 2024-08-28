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

package com.ericsson.nms.security.nscs.cpp.ipsec.input.xml;

import org.junit.Assert;
import org.junit.Test;

public class ObjectFactoryTest {

	@Test
	public void testInstantiation() {
		Assert.assertNotNull(new ObjectFactory().createNodes());
		Assert.assertNotNull(new ObjectFactory().createNodesNodeDisableOMConfiguration());
		Assert.assertNotNull(new ObjectFactory().createNodesNodeEnableOMConfiguration1());
		Assert.assertNotNull(new ObjectFactory().createNodesNodeEnableOMConfiguration2());
		Assert.assertNotNull(new ObjectFactory().createNodesNodeDisableOMConfigurationRemoveTrust());
		Assert.assertNotNull(new ObjectFactory().createNodesNodeEnableOMConfiguration1TsRemoteIpAddressRanges());
		Assert.assertNotNull(new ObjectFactory().createNodesNodeEnableOMConfiguration2TsRemoteIpAddressRanges());
	}

}
