/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.cpp.moaction;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.CleanupM2MUserAndSmrsTask;

/**
 *
 * @author enmadmin
 */
@RunWith(MockitoJUnitRunner.class)
public class CleanupM2MUserAndSmrsTaskHandlerTest {

    @Mock
    CppSecurityService securityService;

    @Mock
    NscsCMReaderService readerService;

    @Mock
    private NormalizableNodeReference normNode;

    @Mock
    CleanupM2MUserAndSmrsTask task;

    @Mock
    private NscsLogger nscsLogger;

    @InjectMocks
    private CleanupM2MUserAndSmrsTaskHandler handlerUnderTest;

    private static final NodeReference NODE = new NodeRef("MeContext=ERBS_001");

    public CleanupM2MUserAndSmrsTaskHandlerTest() {
    }

    /**
     * Test of processTask method, of class CleanupM2MUserAndSmrsTaskHandler.
     */
    @Test
    public void testProcessTask() {
        when(task.getNode()).thenReturn(NODE);
        when(readerService.getNormalizableNodeReference(NODE)).thenReturn(normNode);
        when(normNode.getNeType()).thenReturn("ERBS");
        handlerUnderTest.processTask(task);
        verify(securityService).cancelSmrsAccountForNode(normNode.getName(), normNode.getNeType());
    }

}
