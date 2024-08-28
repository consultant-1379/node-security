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
package com.ericsson.nms.security.nscs.workflow.task.cpp.moaction;

import java.util.*;

import javax.inject.Inject;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.*;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.RealTimeSecLog;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.rtsel.utility.RtselConstants;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.AddExternalServerTask;

@RunWith(MockitoJUnitRunner.class)
public class AddExternalServerTaskHandlerTest {
    @InjectMocks
    AddExternalServerTaskHandler addExternalServerTaskHandler;

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    NscsCMReaderService nscsCmReaderService;

    @Mock
    NormalizableNodeReference normalizableNodeRef;

    @Mock
    CmResponse cmResponseRtsel;;

    @Mock
    private CmObject cmObject;

    @Mock
    private MOActionService moAction;
    
    @Mock
    NscsNodeUtility nscsNodeUtility;

    AddExternalServerTask addExternalServerTask = new AddExternalServerTask();
    List<Map<String, Object>> serverList = new ArrayList<Map<String, Object>>();
    Map<String, Object> serverDetails = new HashMap<String, Object>();
    Map<String, Object> serverAttributes = new HashMap<String, Object>();

    @Before
    public void setUp() {

        final NodeReference nodeReference = new NodeRef("MeContext=ERBS_001");
        final String mirrorRootFdn = "ERBS_001";
        addExternalServerTask.setNode(nodeReference);
        Mockito.when(nscsCmReaderService.getNormalizableNodeReference(nodeReference)).thenReturn(normalizableNodeRef);
        Mockito.when(normalizableNodeRef.getFdn()).thenReturn(mirrorRootFdn);
        final Mo realTimeSecLogMo = Model.ME_CONTEXT.managedElement.systemFunctions.security.realTimeSecLog;
        final String requestedAttrs[] = { RealTimeSecLog.EXT_SERVER_LIST_CONFIG };
        Mockito.when(nscsCmReaderService.getMos(mirrorRootFdn, realTimeSecLogMo.type(), realTimeSecLogMo.namespace(), requestedAttrs)).thenReturn(cmResponseRtsel);
        buildServerConfigInfo();
        addExternalServerTask.setServerConfig(serverList);
        serverAttributes.put(RealTimeSecLog.EXT_SERVER_LIST_CONFIG, serverList);
    }

    @Test
    public void testProcessTask() {
        Mockito.when(cmResponseRtsel.getCmObjects()).thenReturn(Arrays.asList(cmObject));
        Mockito.when(cmResponseRtsel.getCmObjects().iterator().next().getAttributes()).thenReturn(serverAttributes);
        addExternalServerTaskHandler.processTask(addExternalServerTask);
        Mockito.verify(nscsLogger).workFlowTaskHandlerFinishedWithSuccess(addExternalServerTask, "Configuring ExternalServer task completed for node [" + addExternalServerTask.getNodeFdn() + "].");
    }

    @Test
    public void testConfigureExternalServer() {
        Mockito.when(cmResponseRtsel.getCmObjects()).thenReturn(Arrays.asList(cmObject));
        addExternalServerTaskHandler.processTask(addExternalServerTask);
        Mockito.verify(moAction).performMOAction(Mockito.any(String.class), Mockito.any(MoActionWithParameter.class), Mockito.anyList());
    }

    @Test
    public void testConfigureExternalServer_diffInput() {
        List<Map<String, Object>> serverListforNode = new ArrayList<Map<String, Object>>();
        Map<String, Object> serverDetailsforNode = new HashMap<String, Object>();
        serverDetailsforNode.put(RtselConstants.EXT_SERVER_ADDRESS, "172.10.100.20");
        serverDetailsforNode.put(RtselConstants.EXT_SERVER_PROTOCOL, "TLS_OVER_TCP");
        serverDetailsforNode.put(RtselConstants.EXT_SERVER_NAME, "syslog2");
        serverListforNode.add(serverDetailsforNode);
        addExternalServerTask.setServerConfig(serverListforNode);
        Mockito.when(cmResponseRtsel.getCmObjects()).thenReturn(Arrays.asList(cmObject));
        Mockito.when(cmResponseRtsel.getCmObjects().iterator().next().getAttributes()).thenReturn(serverAttributes);
        addExternalServerTaskHandler.processTask(addExternalServerTask);
        Mockito.verify(moAction).performMOAction(Mockito.any(String.class), Mockito.any(MoActionWithParameter.class), Mockito.anyList());
    }

    @Test(expected = WorkflowTaskException.class)
    public void testConfigureExternalServer_Exception() {
        addExternalServerTask.setServerConfig(null);
        Mockito.when(cmResponseRtsel.getCmObjects()).thenReturn(Arrays.asList(cmObject));
        addExternalServerTaskHandler.processTask(addExternalServerTask);
        final String errorMessage = NscsErrorCodes.FAILED_TO_CONFIGURE_EXT_SERVER;
        Mockito.verify(nscsLogger).workFlowTaskHandlerFinishedWithError(addExternalServerTask,
                "Action '" + MoActionWithParameter.RealTimeSecLog_addExternalServer + "' on node [" + addExternalServerTask.getNodeFdn() + "] failed!");
    }

    private void buildServerConfigInfo() {
        serverDetails.put(RtselConstants.EXT_SERVER_ADDRESS, "172.10.100.20");
        serverDetails.put(RtselConstants.EXT_SERVER_PROTOCOL, "TLS_OVER_TCP");
        serverDetails.put(RtselConstants.EXT_SERVER_NAME, "syslog");
        serverList.add(serverDetails);
        serverAttributes.put(RealTimeSecLog.EXT_SERVER_LIST_CONFIG, serverList);
    }
}
