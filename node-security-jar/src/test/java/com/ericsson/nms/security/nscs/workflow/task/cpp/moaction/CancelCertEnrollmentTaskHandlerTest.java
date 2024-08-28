package com.ericsson.nms.security.nscs.workflow.task.cpp.moaction;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Security;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.timer.IntervalJobAction.JobActionParameters;
import com.ericsson.nms.security.nscs.timer.IntervalJobService;
import com.ericsson.nms.security.nscs.workflow.task.cpp.moaction.CancelCertEnrollmentTaskHandler.CheckCertEnrollStateIntervalJob;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoAttributeException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.CancelCertEnrollmentTask;

/**
 * Unit test for CancelCertEnrollmentTaskHandler
 * @author emaynes
 */
@RunWith(MockitoJUnitRunner.class)
public class CancelCertEnrollmentTaskHandlerTest {

    private static final NodeReference NODE = new NodeRef("MeContext=ERBS_001");

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    private MOActionService moActionService;

    @Mock
    private WorkflowHandler workflowHandler;

    @Mock
    private CancelCertEnrollmentTask task;
    
    @Mock
    private IntervalJobService timerJobService;
    
    @Mock
    private NscsCMReaderService readerService;
    
    @Mock
    private NormalizableNodeReference normNode;
    
    @Mock
    private CmResponse cmResponse;
    
    @Mock
    private CmObject cmObject;
    
    @Mock
    private Map<String, Object> attributeMap;
    
    private final Map<JobActionParameters, Object> params = new HashMap<>();

    private CheckCertEnrollStateIntervalJob checkCertEnrollJob;

    @InjectMocks
    private CancelCertEnrollmentTaskHandler handlerUnderTest;

    @Before
    public void setup() {
    	NscsLogger logger = Mockito.mock(NscsLogger.class);
    	checkCertEnrollJob = new CheckCertEnrollStateIntervalJob(NODE, logger, task);
        when(task.getNodeFdn()).thenReturn(NODE.getFdn());
        when(task.getNode()).thenReturn(NODE);
        when(readerService.getNormalizableNodeReference(NODE)).thenReturn(normNode);
        when(
                readerService.getMOAttribute(normNode, 
                		Model.ME_CONTEXT.managedElement.systemFunctions.security.type(),
                		Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace(), 
                		Security.CERT_ENROLL_STATE)).thenReturn(cmResponse);
        
        when(cmObject.getAttributes()).thenReturn(attributeMap);
        when(cmResponse.getCmObjects()).thenReturn(Arrays.asList(cmObject));
        
        params.put(JobActionParameters.WORKFLOW_HANDLER, workflowHandler);
        params.put(JobActionParameters.CM_READER, readerService);
    }

    @Test
    public void successHandlerInvocationTest() {
        handlerUnderTest.processTask(task);
        verify(moActionService).performMOAction(normNode.getFdn(), MoActionWithoutParameter.Security_cancelCertEnrollment);
        verify(timerJobService, atLeast(1)).createIntervalJob(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
                Mockito.any(CancelCertEnrollmentTaskHandler.CheckCertEnrollStateIntervalJob.class));
    }
    
    @Test
    public void testCheckCertEnrollStateIntervalJob_NullValue() {
        Assert.assertTrue(checkCertEnrollJob.doAction(params));
    }

    @Test(expected = RuntimeException.class)
    public void failedHandlerInvocationTest() {
        doThrow(new RuntimeException("Error at MO action execution")).when(moActionService).performMOAction(any(String.class), any(MoActionWithoutParameter.class));
        handlerUnderTest.processTask(task);
    }
    
    @Test
    public void testCheckCertEnrollStateIntervalJob_Value_IDLE() {
    	final String attValue = Security.CertEnrollStateValue.IDLE.toString();
    	when(attributeMap.get(Security.CERT_ENROLL_STATE)).thenReturn(attValue);
    	Assert.assertTrue(checkCertEnrollJob.doAction(params));
    }
    
    @Test
    public void testCheckCertEnrollStateIntervalJob_Value_ERROR() {
        final String attValue = Security.CertEnrollStateValue.ERROR.toString();
        when(attributeMap.get(Security.CERT_ENROLL_STATE)).thenReturn(attValue);
        Assert.assertFalse(checkCertEnrollJob.doAction(params));
    }

    @Test
    public void testCheckCertEnrollStateIntervalJob_Value_PREPARING_REQUEST() {
        final String attValue = Security.CertEnrollStateValue.PREPARING_REQUEST.toString();
        when(attributeMap.get(Security.CERT_ENROLL_STATE)).thenReturn(attValue);
        Assert.assertFalse(checkCertEnrollJob.doAction(params));
    }
    
    @Test
    public void testCheckCertEnrollStateIntervalJob_Value_POLLING() {
        final String attValue = Security.CertEnrollStateValue.POLLING.toString();
        when(attributeMap.get(Security.CERT_ENROLL_STATE)).thenReturn(attValue);
        Assert.assertFalse(checkCertEnrollJob.doAction(params));
    }
    
    @Test
    public void testCheckCertEnrollStateIntervalJob_Value_NEW_CREDS_AWAIT_CONF() {
        final String attValue = Security.CertEnrollStateValue.NEW_CREDS_AWAIT_CONF.toString();
        when(attributeMap.get(Security.CERT_ENROLL_STATE)).thenReturn(attValue);
        Assert.assertFalse(checkCertEnrollJob.doAction(params));
    }
    
    @Test(expected = MissingMoAttributeException.class)
    public void test_handlerInvocationWithMissingAttribute() {
        when(cmResponse.getCmObjects()).thenReturn(new ArrayList<CmObject>());
        checkCertEnrollJob.doAction(params);
    }

    @Test(expected = UnexpectedErrorException.class)
    public void test_handlerInvocationWithMultipleAttribute() {
        when(cmResponse.getCmObjects()).thenReturn(Arrays.asList(cmObject, cmObject));
        checkCertEnrollJob.doAction(params);
    }

}
