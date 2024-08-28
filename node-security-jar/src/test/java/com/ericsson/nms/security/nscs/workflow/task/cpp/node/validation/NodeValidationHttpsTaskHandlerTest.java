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


package com.ericsson.nms.security.nscs.workflow.task.cpp.node.validation;

import com.ericsson.nms.security.nscs.api.exception.UnSupportedNodeReleaseVersionException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.InvalidNodeException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.validation.NodeValidationHttpsTask;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by edarcia on 7/14/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class NodeValidationHttpsTaskHandlerTest {

    private static final NodeReference NODE = new NodeRef("CPP");

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    private NodeValidationHttpsTask task;

    @Mock
    private NodeReference nodeReference;

    @Mock
    private NodeValidatorUtility nodeValidator;

    @InjectMocks
    private NodeValidationHttpsTaskHandler handlerUnderTest;

    @Before
    public void setup() {
        when(task.getNode()).thenReturn(NODE);
        when(task.getNodeFdn()).thenReturn(NODE.getFdn());
    }

    @Test
    public void successHandlerInvocationTest() {
        handlerUnderTest.processTask(task);
        verify(nodeValidator, times(1)).validateNodeForHttpsStatus(Mockito.any(NodeReference.class));
    }

    @Test(expected = InvalidNodeException.class)
    public void failedHandlerInvocationTest() {
        doThrow(new UnSupportedNodeReleaseVersionException("Error at node validation")).when(nodeValidator).validateNodeForHttpsStatus(Mockito.any(NodeReference.class));
        handlerUnderTest.processTask(task);
    }
}
