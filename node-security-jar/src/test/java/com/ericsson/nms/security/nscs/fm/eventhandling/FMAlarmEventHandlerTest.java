package com.ericsson.nms.security.nscs.fm.eventhandling;

import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.exception.WorkflowHandlerException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedEventState;

/**
 * Unit test for NscsFMEventHandlingSeviceBean
 *
 * @author eabdsin
 */
@RunWith(MockitoJUnitRunner.class)
public class FMAlarmEventHandlerTest {

    private static final NodeReference NODE = new NodeRef("MeContext=ERBS_001");
    private static final String INSTALL_TRUSTED_CERTS_SPECIFIC_PROBLEM = "Download of Trusted Certificates completed";
    private static final ProcessedEventState PES_ACTIVE_ACK = ProcessedEventState.ACTIVE_ACKNOWLEDGED;
    private static final ProcessedEventState PES_ACTIVE_UNACK = ProcessedEventState.ACTIVE_UNACKNOWLEDGED;
    private static final ProcessedEventState PES_CLEARED_ACK = ProcessedEventState.CLEARED_ACKNOWLEDGED;
    private static final ProcessedEventState PES_CLEARED_UNACK = ProcessedEventState.CLEARED_UNACKNOWLEDGED;
    private static final ProcessedEventState PES_CLOSED = ProcessedEventState.CLOSED;

    @Spy
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Mock
    WorkflowHandler workflowHandler;

    @Mock
    ProcessedAlarmEvent mockAlarmEvent;

    @InjectMocks
    private FMAlarmEventHandlerService beanUnderTest;

    @Test
    public void testWorkflowHandlerIsCalled_Alarm_Active_Ack() {
        logger.debug("testWorkflowHandlerIsCalled_Alarm_Active_Ack()");
        doReturn(INSTALL_TRUSTED_CERTS_SPECIFIC_PROBLEM).when(mockAlarmEvent).getSpecificProblem();
        doReturn(PES_ACTIVE_ACK).when(mockAlarmEvent).getAlarmState();
        doReturn(NODE.getFdn()).when(mockAlarmEvent).getFdn();
        beanUnderTest.forwardMessageWorkflow(mockAlarmEvent);
        verify(workflowHandler).dispatchMessage(NODE, CPPAlarmEvent.CPPEvent9DownloadOfTrustedCertificatesCompleted.toString());
    }

    @Test
    public void testWorkflowHandlerIsCalled_Alarm_Active_UnAck() {
        logger.debug("testWorkflowHandlerIsCalled_Alarm_Active_UnAck()");
        doReturn(INSTALL_TRUSTED_CERTS_SPECIFIC_PROBLEM).when(mockAlarmEvent).getSpecificProblem();
        doReturn(PES_ACTIVE_UNACK).when(mockAlarmEvent).getAlarmState();
        doReturn(NODE.getFdn()).when(mockAlarmEvent).getFdn();
        beanUnderTest.forwardMessageWorkflow(mockAlarmEvent);
        verify(workflowHandler).dispatchMessage(NODE, CPPAlarmEvent.CPPEvent9DownloadOfTrustedCertificatesCompleted.toString());
    }

    @Test
    public void testWorkflowHandlerThrowsException() {
        logger.debug("testWorkflowHandlerThrowsException()");
        doReturn(INSTALL_TRUSTED_CERTS_SPECIFIC_PROBLEM).when(mockAlarmEvent).getSpecificProblem();
        doReturn(NODE.getFdn()).when(mockAlarmEvent).getFdn();
        doThrow(new WorkflowHandlerException()).when(workflowHandler).dispatchMessage(NODE, INSTALL_TRUSTED_CERTS_SPECIFIC_PROBLEM);
        verify(workflowHandler, never()).dispatchMessage(NODE, CPPAlarmEvent.CPPEvent9DownloadOfTrustedCertificatesCompleted.toString());
    }

    @Test
    public void testWorkflowHandlerWithNullMessage() {
        logger.debug("testWorkflowHandlerWithNullMessage()");
        doReturn("Non-existent Specific Problem").when(mockAlarmEvent).getSpecificProblem();
        beanUnderTest.forwardMessageWorkflow(mockAlarmEvent);
        verify(workflowHandler, never()).dispatchMessage(NODE, CPPAlarmEvent.CPPEvent9DownloadOfTrustedCertificatesCompleted.toString());
    }

    @Test
    public void testWorkflowHandlerNoDispatch_Alarm_Closed() {
        logger.debug("testWorkflowHandlerNoDispatch_Alarm_Closed()");
        doReturn(NODE.getFdn()).when(mockAlarmEvent).getFdn();
        doReturn(INSTALL_TRUSTED_CERTS_SPECIFIC_PROBLEM).when(mockAlarmEvent).getSpecificProblem();
        doReturn(PES_CLOSED).when(mockAlarmEvent).getAlarmState();
        beanUnderTest.forwardMessageWorkflow(mockAlarmEvent);
        verify(workflowHandler, never()).dispatchMessage(NODE, CPPAlarmEvent.CPPEvent9DownloadOfTrustedCertificatesCompleted.toString());
    }

    @Test
    public void testWorkflowHandlerNoDispatch_Alarm_Cleared_Ack() {
        logger.debug("testWorkflowHandlerNoDispatch_Alarm_Cleared_Ack()");
        doReturn(NODE.getFdn()).when(mockAlarmEvent).getFdn();
        doReturn(INSTALL_TRUSTED_CERTS_SPECIFIC_PROBLEM).when(mockAlarmEvent).getSpecificProblem();
        doReturn(PES_CLEARED_ACK).when(mockAlarmEvent).getAlarmState();
        beanUnderTest.forwardMessageWorkflow(mockAlarmEvent);
        verify(workflowHandler, never()).dispatchMessage(NODE, CPPAlarmEvent.CPPEvent9DownloadOfTrustedCertificatesCompleted.toString());
    }

    @Test
    public void testWorkflowHandlerNoDispatch_Alarm_Cleared_UnAck() {
        logger.debug("testWorkflowHandlerNoDispatch_Alarm_Cleared_UnAck()");
        doReturn(NODE.getFdn()).when(mockAlarmEvent).getFdn();
        doReturn(INSTALL_TRUSTED_CERTS_SPECIFIC_PROBLEM).when(mockAlarmEvent).getSpecificProblem();
        doReturn(PES_CLEARED_UNACK).when(mockAlarmEvent).getAlarmState();
        beanUnderTest.forwardMessageWorkflow(mockAlarmEvent);
        verify(workflowHandler, never()).dispatchMessage(NODE, CPPAlarmEvent.CPPEvent9DownloadOfTrustedCertificatesCompleted.toString());
    }

}
