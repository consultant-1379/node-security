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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.exception.DataAccessException;
import com.ericsson.nms.security.nscs.data.moaction.param.AttributeSpecBuilder;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.oss.services.cm.cmshared.dto.ActionSpecification;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.cm.cmwriter.api.CmWriterService;

@RunWith(MockitoJUnitRunner.class)
public class TestMOActionBean {

	@InjectMocks
	private MOActionServiceBean moaction;

	@Mock
	private AttributeSpecBuilder builder;
	@Mock 
	private CmWriterService writer;
	@Mock 
	private Logger log;

	private static final NodeReference NODE123 = new NodeRef("node123");

	@Test
	public void testMOActionWithoutParams() {
		final CmResponse response = getValidResponse();
		when(writer.performAction(any(String.class), any(ActionSpecification.class))).thenReturn(response);
		moaction.performMOAction(NODE123.getFdn(), MoActionWithoutParameter.Security_adaptSecurityLevel);
		verify(writer, times(1)).performAction(eq(MoActionWithoutParameter.Security_adaptSecurityLevel.getFDN(NODE123.getFdn())), any(ActionSpecification.class));
	}
	@Test
	public void testMOActionWithParams() {
		final CmResponse response = getValidResponse();
		final MoParams params = new MoParams();
		when(writer.performAction(any(String.class), any(ActionSpecification.class))).thenReturn(response);
		moaction.performMOAction(NODE123.getFdn(), MoActionWithParameter.Security_installTrustedCertificates, params);
		verify(writer, times(1)).performAction(eq(MoActionWithParameter.Security_installTrustedCertificates.getFDN(NODE123.getFdn())), any(ActionSpecification.class));
	}
	@Test
	public void testMOActionInvalidResponseThrowsException() {
		final CmResponse response = getInvalidResponse();
		when(writer.performAction(any(String.class), any(ActionSpecification.class))).thenReturn(response);
		try {
			moaction.performMOAction(NODE123.getFdn(), MoActionWithoutParameter.Security_adaptSecurityLevel);
			Assert.fail("No exception was thrown");
		} catch (final DataAccessException e ) {
			//ok
		}
		verify(writer, times(1)).performAction(eq(MoActionWithoutParameter.Security_adaptSecurityLevel.getFDN(NODE123.getFdn())), any(ActionSpecification.class));
	}
	private CmResponse getValidResponse() {
		final CmResponse response = new CmResponse();
		response.setStatusCode(1);
		return response;
	}
	private CmResponse getInvalidResponse() {
		final CmResponse response = new CmResponse();
		response.setStatusCode(-1);
		return response;
	}
}