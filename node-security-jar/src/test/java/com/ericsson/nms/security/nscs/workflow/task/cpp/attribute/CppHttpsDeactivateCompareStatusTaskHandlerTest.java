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

import com.ericsson.nms.security.nscs.api.exception.DataAccessException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.MoAttributeHandler;
import com.ericsson.nms.security.nscs.workflow.task.cpp.attribute.CppHttpsDeactivateCompareStatusTaskHandler;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.CppHttpsDeactivateCompareStatusTask;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CppHttpsDeactivateCompareStatusTaskHandlerTest {

    private static final String ATT_WEBSERVER = ModelDefinition.Security.WEBSERVER;
    private static final String ATT_HTTPS = ModelDefinition.CppConnectivityInformation.HTTPS;
    private static final ModelDefinition.Mo CPP_CI = Model.NETWORK_ELEMENT.cppConnectivityInformation;
    private static final ModelDefinition.Security securityMO = Model.ME_CONTEXT.managedElement.systemFunctions.security;

    private static final String OMIT = "OMIT";
    private static final String CLI = "CLI";
    private static final String HTTPS = "HTTPS";
    private static final String NODE_NAME = "Node";
    private static final String TRUE = "true";
    private static final String FALSE = "false";

    @Mock
    private NscsCMReaderService nscsReader;

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    private MoAttributeHandler moAttributeHandler;

    @InjectMocks
    private CppHttpsDeactivateCompareStatusTaskHandler statusTaskHandler;

    private CppHttpsDeactivateCompareStatusTask statusTask;

    @Before
    public void setUp() {
        statusTask = new CppHttpsDeactivateCompareStatusTask();
        doReturn(getNormalizableNodeReferenceMock()).when(nscsReader).getNormalizableNodeReference(any(NodeReference.class));
    }

    @Test
    public void processTaskShouldReturnCLIWhenHttpIsSet() {

        doReturn(HTTPS).when(moAttributeHandler)
                .getMOAttributeValue(NODE_NAME, securityMO.type(), securityMO.namespace(), ATT_WEBSERVER);

        final String result = statusTaskHandler.processTask(statusTask);
        assertEquals(CLI, result);

    }

    @Test
    public void processTaskShouldReturnOMITWhenCppHttpsIsFalse() {

        doReturn(FALSE).when(moAttributeHandler)
                .getMOAttributeValue(NODE_NAME, CPP_CI.type(), CPP_CI.namespace(), ATT_HTTPS);

        final String result = statusTaskHandler.processTask(statusTask);
        assertEquals(OMIT, result);
    }

    @Test(expected = NscsServiceException.class)
    public void processTaskShouldReThrowException() {

        doThrow(DataAccessException.class).when(nscsReader).getNormalizableNodeReference(any(NodeReference.class));

        statusTaskHandler.processTask(statusTask);
    }

    private NormalizableNodeReference getNormalizableNodeReferenceMock() {
        NormalizableNodeReference normalizableNodeReference = mock(NormalizableNodeReference.class);
        NodeReference nodeReference = mock(NodeReference.class);

        when(nodeReference.getFdn()).thenReturn(NODE_NAME);
        when(normalizableNodeReference.getNormalizedRef()).thenReturn(nodeReference);
        when(normalizableNodeReference.getNormalizableRef()).thenReturn(nodeReference);
        return normalizableNodeReference;
    }
}