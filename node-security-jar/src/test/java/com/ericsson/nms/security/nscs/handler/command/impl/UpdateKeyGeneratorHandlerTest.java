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
package com.ericsson.nms.security.nscs.handler.command.impl;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse;
import com.ericsson.nms.security.nscs.api.command.types.KeyGeneratorCommand;
import com.ericsson.nms.security.nscs.api.exception.KeyGenerationHandlerException;
import com.ericsson.nms.security.nscs.api.exception.MaxNodesExceededException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowNames;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;
import com.ericsson.oss.services.wfs.api.instance.WorkflowInstance;

@RunWith(MockitoJUnitRunner.class)
public class UpdateKeyGeneratorHandlerTest {

    public static final String SSH_KEY_UPDATE = "Sshkey update command executed";
    @InjectMocks
    private UpdateKeyGeneratorHandler updateKeygenHandler;

    @Mock
    private NscsLogger nscslogger;

    @Mock
    private NscsCMReaderService reader;

    @Mock
    private CommandContext cmdctx;

    @Mock
    private NormalizableNodeReference sgsnNormNodeRef;

    @Mock
    private WorkflowHandler wfHandler;

    @Mock
    private CmResponse response;

    @Mock
    private CmObject cmObjIntfs;

    private final NodeReference sgsnNodeRef = new NodeRef(SGSN_NODE_NAME);

    private KeyGeneratorCommand command = null;

    private static final String SGSN_NODE_NAME = "SGSN123___";
    private static final String SGSN_FDN = "NetworkElement=" + SGSN_NODE_NAME;

    public static final int MAX_LIMIT_WORKFLOW = 100;

    @Before
    public void setup() {
        command = new KeyGeneratorCommand();

        command.setCommandType(NscsCommandType.UPDATE_SSH_KEY);

        when(cmdctx.getValidNodes()).thenReturn(new LinkedList<NormalizableNodeReference>(Collections.singletonList(sgsnNormNodeRef)));

        doReturn(SGSN_NODE_NAME).when(sgsnNormNodeRef).getName();
        doReturn(SGSN_FDN).when(sgsnNormNodeRef).getFdn();
        doReturn("SGSN-MME").when(sgsnNormNodeRef).getNeType();
        doReturn(sgsnNodeRef).when(sgsnNormNodeRef).getNormalizedRef();
        doReturn(true).when(sgsnNormNodeRef).hasNormalizedRef();
        doReturn(sgsnNormNodeRef).when(reader).getNormalizableNodeReference(sgsnNodeRef);

    }

    @Test(expected = KeyGenerationHandlerException.class)
    @Ignore
    public void testProcessException() {

        final Map<String, Object> properties = new HashMap<>();
        properties.put("algorithm-type-size", "RSA_1024");
        command.setProperties(properties);

        final NscsMessageCommandResponse response = (NscsMessageCommandResponse) updateKeygenHandler.process(command, cmdctx);
    }

    @Test
    @Ignore
    public void testProcessResponse() {
        when(wfHandler.startWorkflowInstance(Mockito.any(NodeReference.class), Mockito.anyString(), Mockito.anyMap()))
                .thenReturn(new WorkflowInstance(null, null, null));

        final Map<String, Object> properties = new HashMap<>();
        properties.put("algorithm-type-size", "RSA_1024");
        command.setProperties(properties);

        final NscsMessageCommandResponse response = (NscsMessageCommandResponse) updateKeygenHandler.process(command, cmdctx);

        Assert.assertEquals(SSH_KEY_UPDATE, response.getMessage());
    }

    @Test
    @Ignore
    public void testProcess_WithoutAlgorithm() {
        when(wfHandler.startWorkflowInstance(Mockito.any(NodeReference.class), Mockito.anyString(), Mockito.anyMap()))
                .thenReturn(new WorkflowInstance(null, null, null));

        final Map<String, Object> properties = new HashMap<>();
        properties.put("algorithm-type-size", null);
        command.setProperties(properties);

        final List<NodeReference> nrList = new LinkedList<>();
        when(cmdctx.toNormalizedRef(Mockito.anyListOf(NormalizableNodeReference.class))).thenReturn(nrList);
        when(reader.getMOAttribute(nrList, Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.type(),
                Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.namespace(), NetworkElementSecurity.ALGORITHM_AND_KEY_SIZE))
                        .thenReturn(response);

        when(response.getCmObjects()).thenReturn(Collections.singletonList(cmObjIntfs));
        final Map<String, Object> mapAttr = new HashMap<>();
        mapAttr.put(NetworkElementSecurity.ALGORITHM_AND_KEY_SIZE, "RSA_1024");
        when(cmObjIntfs.getAttributes()).thenReturn(mapAttr);

        final NscsMessageCommandResponse response = (NscsMessageCommandResponse) updateKeygenHandler.process(command, cmdctx);

        Assert.assertEquals(SSH_KEY_UPDATE, response.getMessage());
    }

    @Ignore
    @Test(expected = MaxNodesExceededException.class)
    public void testProcess__max() {
        final int maxExceeded = 101;

        final List<NormalizableNodeReference> nodeList = new LinkedList<>();
        for (int i = 0; i < maxExceeded; i++) {
            nodeList.add(sgsnNormNodeRef);
        }
        when(cmdctx.getValidNodes()).thenReturn(nodeList);

        final WorkflowInstance workflows = null;
        final Map<String, Object> workflowVars = new HashMap<String, Object>();
        workflowVars.put(WorkflowParameterKeys.KEY_SSHKEYS_GENERATION_ALGORITHM.toString(), "RSA_1024");
        workflowVars.put(WorkflowParameterKeys.KEY_SSHKEYS_GENERATION_ISCREATE.toString(), false);

        final String wfn = WorkflowNames.WORKFLOW_SSHKeyGeneration.toString();
        when(wfHandler.startWorkflowInstance(sgsnNormNodeRef, wfn, workflowVars)).thenReturn(workflows);

        final NscsMessageCommandResponse response = (NscsMessageCommandResponse) updateKeygenHandler.process(command, cmdctx);
    }

}
