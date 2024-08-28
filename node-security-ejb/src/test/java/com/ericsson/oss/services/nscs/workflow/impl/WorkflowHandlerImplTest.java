/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package com.ericsson.oss.services.nscs.workflow.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.MockUtils;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.data.workflow.WfQueryServiceBean;
import com.ericsson.oss.services.wfs.api.WorkflowMessageCorrelationException;
import com.ericsson.oss.services.wfs.api.instance.WorkflowInstance;
import com.ericsson.oss.services.wfs.jee.api.WorkflowInstanceServiceRemote;

/**
 * Unit test for class WorkflowHandlerImpl
 *
 * @author ealemca
 */
@RunWith(MockitoJUnitRunner.class)
public class WorkflowHandlerImplTest {

    private static final String WORKFLOW_NAME = "CPPWorkflowName";
    private static final String NODE_FDN = "NODE_FDN";
    private static final String NODE2_FDN = "NODE2_FDN";

    private static final String NODE3 = "Node3";
    private static final NodeRef NODE3_REF = new NodeRef(NODE3);
    private static final NodeRef NODE3_REF_NETWORK_ELEMENT = new NodeRef("NetworkElement=" + NODE3);
    private static final String NODE3_BK = WfQueryServiceBean.NSCS_BUSINESS_KEY_PREFIX + "MeContext=" + NODE3;
    private static final NormalizableNodeReference NODE3_NREF = MockUtils.createNormalizableNodeRef(NODE3);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Spy
    private final Logger log = LoggerFactory.getLogger(WorkflowHandlerImpl.class);

    @Mock
    WorkflowInstanceServiceRemote wfsInstanceService;

    @Mock
    NscsCMReaderService reader;

    @Mock
    NodeReference nodeRefMock;

    @Mock
    NodeReference nodeRefMock2;

    @InjectMocks
    WorkflowHandlerImpl beanUnderTest;

    @Before
    public void setUp() {

        doReturn(NODE_FDN).when(nodeRefMock).getFdn();
        doReturn(NODE2_FDN).when(nodeRefMock2).getFdn();

        doReturn(NODE3_NREF).when(reader).getNormalizableNodeReference(any(NodeReference.class));
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of startWorkflowInstances method, of class WorkflowHandlerImpl.
     * 
     * @throws java.lang.Exception
     */
    @Test
    public void testStartWorkflowInstances() throws Exception {
        log.debug("startWorkflowInstances");
        final List<NodeReference> nodes = new ArrayList<>();
        nodes.add(nodeRefMock);
        nodes.add(nodeRefMock2);
        final Set<WorkflowInstance> workflowInstances = beanUnderTest.startWorkflowInstances(nodes, WORKFLOW_NAME);
        log.debug("Set<WorkflowInstance> [{}]", workflowInstances.toString());
        assertNotNull("Set<WorkflowInstance> is null", workflowInstances);
        assertFalse("Set<WorkflowInstance> is empty", workflowInstances.isEmpty());
        verify(wfsInstanceService, times(2)).startWorkflowInstanceByDefinitionId(Mockito.any(String.class), Mockito.any(String.class),
                Mockito.any(Map.class));
    }

    /**
     * Test of getWorkflowByFdn method, of class WorkflowHandlerImpl.
     * 
     * @throws java.lang.Exception
     */
    @Test
    public void testGetWorkflowByFdn() throws Exception {
        log.debug("getWorkflowByFdn");
        final WorkflowInstance wf = beanUnderTest.getWorkflowByFdn(nodeRefMock);
        // TODO: write a real test once this is implemented
        assertNull("Not implemented yet", wf);
        verify(log).warn(Mockito.any(String.class));
    }

    /**
     * Test of dispatchMessage method, of class WorkflowHandlerImpl.
     * 
     * @throws java.lang.Exception
     */
    @Test
    public void testDispatchMessage() throws Exception {
        thrown.expect(RuntimeException.class);
        doThrow(new WorkflowMessageCorrelationException("")).when(wfsInstanceService).correlateMessage(Mockito.any(String.class),
                Mockito.any(String.class));
        log.debug("dispatchMessage");
        beanUnderTest.dispatchMessage(nodeRefMock, "CPPSomeMessage");
    }

    /**
     * Test exception being thrown by dispatchMessage method, of class WorkflowHandlerImpl.
     * 
     * @throws java.lang.Exception
     */
    @Test
    public void testDispatchMessageException() throws Exception {
        log.debug("dispatchMessage");
        beanUnderTest.dispatchMessage(nodeRefMock, "CPPSomeMessage");
        verify(wfsInstanceService).correlateMessage(Mockito.any(String.class), Mockito.any(String.class));
    }

    /**
     * Test of startWorkflowInstance method, of class WorkflowHandlerImpl.
     * 
     * @throws java.lang.Exception
     */
    @Test
    public void testStartWorkflowInstance() throws Exception {
        log.debug("startWorkflowInstance");
        final WorkflowInstance wf = beanUnderTest.startWorkflowInstance(nodeRefMock, WORKFLOW_NAME);
        verify(wfsInstanceService).startWorkflowInstanceByDefinitionId(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(Map.class));
    }

    /*
     * Test of startWorkflowInstances method with parameters as a map, of class WorkflowHandlerImpl.
     * 
     * @throws java.lang.Exception
     */
    @Test
    public void testStartWorkflowInstancesWithMap() throws Exception {
        log.debug("startWorkflowInstances with map parameters");
        final List<NodeReference> nodes = new ArrayList<>();
        nodes.add(nodeRefMock);
        nodes.add(nodeRefMock2);
        final Map<String, Object> workflowParams = new HashMap<String, Object>();
        workflowParams.put("PARAM_01", "VALUE_01");
        workflowParams.put("PARAM_02", "VALUE_02");
        workflowParams.put("PARAM_03", "VALUE_03");
        final Set<WorkflowInstance> workflowInstances = beanUnderTest.startWorkflowInstances(nodes, WORKFLOW_NAME, workflowParams);
        log.debug("Set<WorkflowInstance> [{}]", workflowInstances.toString());
        assertNotNull("Set<WorkflowInstance> is null", workflowInstances);
        assertFalse("Set<WorkflowInstance> is empty", workflowInstances.isEmpty());
        verify(wfsInstanceService, times(2)).startWorkflowInstanceByDefinitionId(Mockito.any(String.class), Mockito.any(String.class),
                Mockito.any(Map.class));
    }

    @Test
    public void testStartWFUsingNotNormalizedNodeReference() {

        final WorkflowInstance wf = beanUnderTest.startWorkflowInstance(NODE3_REF, WORKFLOW_NAME);

        verify(wfsInstanceService).startWorkflowInstanceByDefinitionId(Mockito.any(String.class), Mockito.eq(NODE3_BK), Mockito.any(Map.class));
    }

    @Test
    public void testDispatchUsingNotNormalizedNodeReference() throws WorkflowMessageCorrelationException {

        final String message = "Very important message";
        beanUnderTest.dispatchMessage(NODE3_REF, message);

        verify(wfsInstanceService).correlateMessage(message, NODE3_BK);
    }

    @Test
    public void testDispatchUsingNetworkElement() throws WorkflowMessageCorrelationException {

        final String message = "Very important message";
        beanUnderTest.dispatchMessage(NODE3_REF_NETWORK_ELEMENT, message);

        verify(wfsInstanceService).correlateMessage(message, NODE3_BK);
    }

    @Test
    public void testStartWFUsingNetworkElement() throws WorkflowMessageCorrelationException {

        final WorkflowInstance wf = beanUnderTest.startWorkflowInstance(NODE3_REF_NETWORK_ELEMENT, WORKFLOW_NAME);

        verify(wfsInstanceService).startWorkflowInstanceByDefinitionId(Mockito.any(String.class), Mockito.eq(NODE3_BK), Mockito.any(Map.class));
    }
}
