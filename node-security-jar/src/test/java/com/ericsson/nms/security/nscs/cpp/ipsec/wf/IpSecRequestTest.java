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
package com.ericsson.nms.security.nscs.cpp.ipsec.wf;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node;

public class IpSecRequestTest {

	final IpSecRequest testObj = new IpSecRequest();
	
	@Test
	public void testIsForceUpdate() {
		testObj.setForceUpdate(true);
		assertTrue("Force update is set", testObj.isForceUpdate());
	}

	
	
	@Test
	public void testGetNodeFdn() {
		testObj.setNodeFdn("Node123");
		assertNotNull("Node Fdn can't be null", testObj.getNodeFdn());
		assertEquals("Node name must be equal" , "Node123", testObj.getNodeFdn());
	}

	
	@Test
	public void testGetXmlRepresntationOfNode() {
		final Node xmlRepresntationOfNode = new Node();
		testObj.setXmlRepresntationOfNode(xmlRepresntationOfNode);
		assertNotNull("XMLRepresenation is not null", testObj.getXmlRepresntationOfNode());
		
	}

	
	@Test
	public void testIsEnableIPSec() {
		testObj.setIpSecRequestType(IpSecRequestType.IP_SEC_ENABLE_CONF1);
		assertEquals(IpSecRequestType.IP_SEC_ENABLE_CONF1, testObj.getIpSecRequestType());
	}


}
