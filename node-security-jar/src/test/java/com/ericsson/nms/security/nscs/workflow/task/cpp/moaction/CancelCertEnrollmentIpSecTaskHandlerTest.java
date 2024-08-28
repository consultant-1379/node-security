/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.cpp.moaction;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.CancelCertEnrollmentIpSecTask;

@RunWith(MockitoJUnitRunner.class)
public class CancelCertEnrollmentIpSecTaskHandlerTest {

    private static final NodeReference NODE = new NodeRef("MeContext=ERBS_001");

    @Mock
    private NscsLogger nscsLogger;;

    @Mock
    private CancelCertEnrollmentIpSecTask mockCancelCertEnrollmentIpSecTask;

    @InjectMocks
    private CancelCertEnrollmentIpSecTaskHandler testObj;

    @Mock
    private MOActionService moActionService;

    @Mock
    private WorkflowHandler workflowHandler;

    @Mock
    private NscsCMReaderService readerService;

    @Mock
    private NormalizableNodeReference normalizable;

    @Before
    public void setup() {
        when(mockCancelCertEnrollmentIpSecTask.getNodeFdn()).thenReturn(NODE.getFdn());
        when(mockCancelCertEnrollmentIpSecTask.getNode()).thenReturn(NODE);
        when(readerService.getNormalizableNodeReference(NODE)).thenReturn(normalizable);
    }

    @Test
    @Ignore
    public void testProcessTask() {
        testObj.processTask(mockCancelCertEnrollmentIpSecTask);
        verify(moActionService).performMOAction(normalizable.getFdn(), MoActionWithoutParameter.IpSec_cancelCertEnrollment);
    }

    @Test(expected = RuntimeException.class)
    public void failedHandlerInvocationTest() {
        doThrow(new RuntimeException("Error at MO action execution")).when(moActionService).performMOAction(any(String.class), any(MoActionWithoutParameter.class));
        testObj.processTask(mockCancelCertEnrollmentIpSecTask);
    }
}
