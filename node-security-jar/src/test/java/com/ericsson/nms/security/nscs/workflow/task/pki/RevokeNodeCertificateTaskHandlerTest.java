/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.pki;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.pki.RevokeNodeCertificateTask;

@RunWith(MockitoJUnitRunner.class)
public class RevokeNodeCertificateTaskHandlerTest {

	private static final NodeReference NODE = new NodeRef("node123");
	private static final String EXCEPTION_MESSAGE = "Exception";
	private static String certificateId = "123";
	private static String certificateAuthorityId = "ABC";
	private static String revocationReason = "keyCompromise";

	@Mock
	private Logger log;
	
	@Mock
    private SystemRecorder systemRecorder;
	
	@Mock
    private CppSecurityService mockSecurityService;

    @Mock
    private RevokeNodeCertificateTask task;
	
	@InjectMocks
	private RevokeNodeCertificateTaskHandler handlerUnderTest;
	
	@Mock
	private NscsCMReaderService readerService;

	@Mock
    private NormalizableNodeReference normRef;
	
	
	/**
	 * Setup test
	 */
	@Before
	public void setup() {
		//Task mock
		when(task.getNodeFdn()).thenReturn(NODE.getFdn());
		when(task.getCertificateId()).thenReturn(certificateId);
		when(task.getCertificateAuthorityId()).thenReturn(certificateAuthorityId);
		when(task.getRevocationReason()).thenReturn(revocationReason);
		when(readerService.getNormalizedNodeReference(NODE)).thenReturn(normRef);
	}
	
	/**
	 * @return
	 */
	private CmResponse buildCmResponse(EnrollmentMode enrollmentMode) {
        final CmResponse cmResponse = new CmResponse();
        final Map<String, Object> attributesMap = new HashMap<>();

        attributesMap.put(NetworkElementSecurity.ENROLLMENT_MODE, enrollmentMode.name());

        final Collection<CmObject> cmObjects = new ArrayList<>(1);
        final CmObject cmObject = new CmObject();
        cmObject.setAttributes(attributesMap);
        cmObject.setFdn(NODE.getFdn());
        cmObjects.add(cmObject);

        cmResponse.setTargetedCmObjects(cmObjects);
        cmResponse.setStatusCode(0);
        return cmResponse;

	}

	@Test
	@Ignore
	public void handlerInvocationTest_positive_SCEP() throws Exception {
		log.debug("Executing test handlerInvocationTest_positive_SCEP for handler RevokeNodeCertificateHandler");
		final CmResponse cmResponse = buildCmResponse(EnrollmentMode.SCEP);
		when(
				readerService.getMOAttribute(any(String.class), any(String.class), any(String.class),
						any(String.class))).thenReturn(cmResponse);
        Assert.assertEquals("Expected response", "true", handlerUnderTest.processTask(task));
	}
	
	@Test
	@Ignore
	public void handlerInvocationTest_positive_CMP() throws Exception {
		log.debug("Executing test handlerInvocationTest_positive_CMP for handler RevokeNodeCertificateHandler");

		final CmResponse cmResponse = buildCmResponse(EnrollmentMode.CMPv2_INITIAL);
		when(
				readerService.getMOAttribute(any(String.class), any(String.class), any(String.class),
						any(String.class))).thenReturn(cmResponse);
        Assert.assertEquals("Expected response", "true", handlerUnderTest.processTask(task));
        
        
        //			logger.info("No need to perform revocation for enrollmentMode [{}]", enrollmentMode.name());

	}
	
	@Test
	@Ignore
	public void handlerInvocationTest_negative() throws Exception {
		log.debug("Executing test handlerInvocationTest_negative for handler RevokeNodeCertificateHandler");
		final CmResponse cmResponse = buildCmResponse(EnrollmentMode.SCEP);
		when(
				readerService.getMOAttribute(any(String.class), any(String.class), any(String.class),
						any(String.class))).thenReturn(cmResponse);
        doThrow(new CppSecurityServiceException(EXCEPTION_MESSAGE)).when(
        		mockSecurityService).revokeCertificateByIssuerName(any(String.class), any(String.class), any(String.class));
        Assert.assertEquals("Expected response", "false", handlerUnderTest.processTask(task));
	}
}