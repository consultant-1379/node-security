/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.cpp.attribute;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.ValidateNodeOAMCertificateTask;

/**
 * Unit test for ValidateNodeCertificateTaskHandler
 * 
 * @author tcsnapa
 */
@RunWith(MockitoJUnitRunner.class)
public class ValidateNodeOAMCertificateTaskHandlerTest {

    @InjectMocks
    ValidateNodeOAMCertificateTaskHandler validateNodeOAMCertificateTaskHandler;

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    private CppSecurityService cppSecurityService;

    @Mock
    private NormalizableNodeReference normNodeRef;

    @Mock
    private ValidateNodeOAMCertificateTask task;

    @Test
    public void testProcessTask_WithValidCertificateOnNode() {
        try {
            when(cppSecurityService.isNodeHasValidCertificate(task.getNodeFdn(), CertificateType.OAM.name())).thenReturn(true);
            final String result = validateNodeOAMCertificateTaskHandler.processTask(task);
            assertEquals("VALID_CERT", result);

        } catch (CppSecurityServiceException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testProcessTask_WithInvalidCertificateOnNode() {
        try {
            when(cppSecurityService.isNodeHasValidCertificate(task.getNodeFdn(), CertificateType.OAM.name())).thenReturn(false);
            final String result = validateNodeOAMCertificateTaskHandler.processTask(task);
            assertEquals("INVALID_CERT", result);

        } catch (CppSecurityServiceException e) {
            Assert.fail(e.getMessage());
        }
    }
}
