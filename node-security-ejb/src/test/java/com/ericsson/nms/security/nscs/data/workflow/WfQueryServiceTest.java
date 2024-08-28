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
package com.ericsson.nms.security.nscs.data.workflow;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

import java.util.ArrayList;
import java.util.Date;

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

import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;
import com.ericsson.oss.services.wfs.api.WorkflowInstanceService;
import com.ericsson.oss.services.wfs.api.query.Query;
import com.ericsson.oss.services.wfs.api.query.WorkflowObject;
import com.ericsson.oss.services.wfs.api.query.impl.WorkflowObjectImpl;
import com.ericsson.oss.services.wfs.api.query.progress.WorkflowProgressQueryAttributes;
import com.ericsson.oss.services.wfs.jee.api.WorkflowQueryServiceRemote;

@RunWith(MockitoJUnitRunner.class)
public class WfQueryServiceTest {

    private static final String WORKFLOW_NAME = "CPPWorkflowName";
    private static final String NODE_FDN = "NODE_FDN";
    private static final String NODE2_FDN = "NODE2_FDN";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Spy
    private final Logger log = LoggerFactory.getLogger(WfQueryServiceBean.class);

    @Mock
    WorkflowInstanceService wfsInstanceService;

    @Mock
    WorkflowQueryServiceRemote queryService;

    @Mock
    NormalizableNodeReference nodeRefMock;

    @Mock
    NormalizableNodeReference nodeRefMock2;

    @Mock
    private NscsContextService ctxService;

    @InjectMocks
    WfQueryServiceBean beanUnderTest;

    @Before
    public void setUp() {
        doReturn(NODE_FDN).when(nodeRefMock).getFdn();
        doReturn(NODE2_FDN).when(nodeRefMock2).getFdn();
    }

    @Test
    public void testHasWorkflowInstanceInProgressNegative() {
        log.debug("start testWfAlreadyStartedFor");
        final boolean result = beanUnderTest.isWorkflowInProgress(nodeRefMock);
        assertFalse(result);
    }

    @Test
    public void testHasWorkflowInstanceInProgressPositive() {
        log.debug("start testWfAlreadyStartedForPositive");
        final ArrayList<WorkflowObject> workflowStatuses = new ArrayList<>();
        final WorkflowObjectImpl resultObjectFromQuery = new WorkflowObjectImpl();
        resultObjectFromQuery.setAttribute(WorkflowProgressQueryAttributes.QueryResult.WORKFLOW_INSTANCE_ID, "some id");
        resultObjectFromQuery.setAttribute(WorkflowProgressQueryAttributes.QueryResult.NODE_NAME, "CPPActivateSL2Start");
        resultObjectFromQuery.setAttribute(WorkflowProgressQueryAttributes.QueryResult.EVENT_TYPE, WorkflowProgressQueryAttributes.EventType.END);
        resultObjectFromQuery.setAttribute(WorkflowProgressQueryAttributes.QueryResult.EVENT_TIME, new Date());
        workflowStatuses.add(resultObjectFromQuery);

        doReturn(workflowStatuses).when(queryService).executeQuery(Mockito.any(Query.class));
        assertTrue(beanUnderTest.isWorkflowInProgress(nodeRefMock));
    }

    @Test
    public void testHasWorkflowInstanceInProgressHasWrongStepName() {
        log.debug("start testWfAlreadyStartedForPositive");
        final ArrayList<WorkflowObject> workflowStatuses = new ArrayList<>();
        final WorkflowObjectImpl resultObjectFromQuery = new WorkflowObjectImpl();
        resultObjectFromQuery.setAttribute(WorkflowProgressQueryAttributes.QueryResult.WORKFLOW_INSTANCE_ID, "some id");
        resultObjectFromQuery.setAttribute(WorkflowProgressQueryAttributes.QueryResult.NODE_NAME, "Incorrect Name");
        resultObjectFromQuery.setAttribute(WorkflowProgressQueryAttributes.QueryResult.EVENT_TYPE, WorkflowProgressQueryAttributes.EventType.END);
        resultObjectFromQuery.setAttribute(WorkflowProgressQueryAttributes.QueryResult.EVENT_TIME, new Date());
        workflowStatuses.add(resultObjectFromQuery);

        doReturn(workflowStatuses).when(queryService).executeQuery(Mockito.any(Query.class));
        assertFalse(beanUnderTest.isWorkflowInProgress(nodeRefMock));
    }

    @Test
    public void testHasWorkflowInstanceInProgressIsCompleted() {
        log.debug("start testWfAlreadyStartedForPositive");
        final ArrayList<WorkflowObject> workflowStatuses = new ArrayList<>();

        final WorkflowObjectImpl resultObjectFromQueryFinished = new WorkflowObjectImpl();
        resultObjectFromQueryFinished.setAttribute(WorkflowProgressQueryAttributes.QueryResult.WORKFLOW_INSTANCE_ID, "some id");
        resultObjectFromQueryFinished.setAttribute(WorkflowProgressQueryAttributes.QueryResult.NODE_NAME, "Success");
        resultObjectFromQueryFinished.setAttribute(WorkflowProgressQueryAttributes.QueryResult.EVENT_TYPE,
                WorkflowProgressQueryAttributes.EventType.END);
        resultObjectFromQueryFinished.setAttribute(WorkflowProgressQueryAttributes.QueryResult.EVENT_TIME, new Date());

        workflowStatuses.add(resultObjectFromQueryFinished);

        doReturn(workflowStatuses).when(queryService).executeQuery(Mockito.any(Query.class));
        assertFalse(beanUnderTest.isWorkflowInProgress(nodeRefMock));
    }

    @Test
    public void testHasWorkflowInstanceInProgressEmptyResultFromQueryService() {
        log.debug("start testWfAlreadyStartedForPositive");
        final ArrayList<WorkflowObject> workflowStatuses = new ArrayList<>();
        final WorkflowObjectImpl resultObjectFromQuery = new WorkflowObjectImpl();
        resultObjectFromQuery.setAttribute(WorkflowProgressQueryAttributes.QueryResult.WORKFLOW_INSTANCE_ID, "");
        resultObjectFromQuery.setAttribute(WorkflowProgressQueryAttributes.QueryResult.NODE_NAME, "");
        resultObjectFromQuery.setAttribute(WorkflowProgressQueryAttributes.QueryResult.EVENT_TYPE, WorkflowProgressQueryAttributes.EventType.END);
        resultObjectFromQuery.setAttribute(WorkflowProgressQueryAttributes.QueryResult.EVENT_TIME, new Date());

        workflowStatuses.add(resultObjectFromQuery);
        doReturn(workflowStatuses).when(queryService).executeQuery(Mockito.any(Query.class));
        assertFalse(beanUnderTest.isWorkflowInProgress(nodeRefMock));
    }

    @Test
    public void testHasWorkflowInstanceInProgressNegativeNoResult() {
        log.debug("start testWfAlreadyStartedForNegative");
        final WorkflowStatus workflowStatus = beanUnderTest.getWFFinalStatusByBusinessKey(nodeRefMock);
        System.out.println("\n\nworkflowStatus" + workflowStatus);
        assertNull(workflowStatus);
    }
}
