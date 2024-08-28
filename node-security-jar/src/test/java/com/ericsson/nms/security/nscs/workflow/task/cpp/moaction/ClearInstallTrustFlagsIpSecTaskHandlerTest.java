/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.cpp.moaction;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.timer.IntervalJobAction.JobActionParameters;
import com.ericsson.nms.security.nscs.timer.IntervalJobService;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.ClearInstallTrustFlagsIpSecTask;

/**
 * 
 * @author emehsau
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ClearInstallTrustFlagsIpSecTaskHandlerTest {

    @Mock
    ClearInstallTrustFlagsIpSecTask mockClearInstallTrustFlagsIpSecTask;

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    MOActionService mockMOActionService;

    @Mock
    NscsCMReaderService readerService;

    @Mock
    NormalizableNodeReference normNode;
    
    @Mock
    private IntervalJobService timerJobService;

    @Mock
    private Map<String, Object> attributeMap;
    
    @Mock
    private NscsCapabilityModelService capabilityService;
    
    @Mock
    private WorkflowHandler workflowHandler;
    
    private final Map<JobActionParameters, Object> params = new HashMap<>();
    
    

    private static final String NODE_NAME = "MeContext=Node_123";

    final NodeRef nodeRef = new NodeRef(NODE_NAME);

    @InjectMocks
    ClearInstallTrustFlagsIpSecTaskHandler testObj;

    @Before
    public void setUp() {
        Mockito.when(mockClearInstallTrustFlagsIpSecTask.getNodeFdn()).thenReturn(nodeRef.getFdn());
        Mockito.when(mockClearInstallTrustFlagsIpSecTask.getNode()).thenReturn(nodeRef);
        Mockito.when(readerService.getNormalizableNodeReference(nodeRef)).thenReturn(normNode);
        
        params.put(JobActionParameters.WORKFLOW_HANDLER, workflowHandler);
        params.put(JobActionParameters.CM_READER, readerService);
        params.put(JobActionParameters.CAPABILITY_SERVICE, capabilityService);
    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.workflow.task.cpp.moaction.ClearInstallTrustFlagsIpSecTaskHandler#processTask(com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.ClearInstallTrustFlagsIpSecTask)}
     * .
     */
    @Test
    public void testProcessTask() {
        testObj.processTask(mockClearInstallTrustFlagsIpSecTask);
        Mockito.verify(mockMOActionService).performMOAction(normNode.getFdn(), MoActionWithoutParameter.IpSec_cancelInstallTrustedCertificates);
        
    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.workflow.task.cpp.moaction.ClearInstallTrustFlagsIpSecTaskHandler#processTask(com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.ClearInstallTrustFlagsIpSecTask)}
     * .
     */
    @Test(expected = RuntimeException.class)
    public void testProcessTask_RuntimeException() {
        Mockito.doThrow(new RuntimeException()).when(mockMOActionService).performMOAction(Mockito.any(String.class),
                Mockito.any(MoActionWithoutParameter.class));
        testObj.processTask(mockClearInstallTrustFlagsIpSecTask);
    }

}
