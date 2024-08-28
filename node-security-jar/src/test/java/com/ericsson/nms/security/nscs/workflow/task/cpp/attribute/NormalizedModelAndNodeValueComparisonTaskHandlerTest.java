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

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.command.utility.WebServerStatus;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.MoAttributeHandler;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.InvalidNodeException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NormalizedModelAndNodeValueComparisonTaskHandlerTest {

    private static final String NODE_NAME = "Node";
    private static final boolean HTTPS_ATTRIBUTE_CONNECTIVITY_INFO = false;

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    private NscsCMReaderService readerService;

    @Mock
    private MoAttributeHandler moAttributeHandler;

    @InjectMocks
    private NormalizedModelAndNodeValueComparisonTaskHandler taskHandler;

    private WorkflowActionTask workflowActionTask;

    private WebServerStatus webServerStatus;

    @Before
    public void setUp() {

        workflowActionTask = new WorkflowActionTask();
        webServerStatus = WebServerStatus.HTTP;
    }

    @Test(expected = InvalidNodeException.class)
    public void processTaskShouldThrowExceptionWhenNodeRefIsNull() {

        when(readerService.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(null);

        taskHandler.processTask(workflowActionTask, webServerStatus);
    }

    @Test
    public void processTaskShouldInvokeMatchMethod() {

        NormalizableNodeReference normalizableNodeReference = mock(NormalizableNodeReference.class);
        when(readerService.getNormalizedNodeReference(any(NodeReference.class))).thenReturn(normalizableNodeReference);
        when(normalizableNodeReference.getFdn()).thenReturn(NODE_NAME);

        taskHandler.processTask(workflowActionTask, webServerStatus);

        verify(moAttributeHandler, times(1)).match(webServerStatus, HTTPS_ATTRIBUTE_CONNECTIVITY_INFO);
    }
}
