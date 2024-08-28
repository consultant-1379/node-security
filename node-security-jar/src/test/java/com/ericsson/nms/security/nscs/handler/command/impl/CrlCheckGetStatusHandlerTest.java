/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.command.impl;

import java.util.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.CrlCheckCommand;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.logger.NscsLogger;

/**
 * Test Class for CrlCheckGetStatusHandler.
 * 
 * @author xchowja
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CrlCheckGetStatusHandlerTest {

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    private NscsCommandManager commandManager;

    @InjectMocks
    CrlCheckGetStatusHandler checkGetStatusHandler;

    @Mock
    private NscsCMReaderService mockReaderService;

    @Mock
    private NormalizableNodeReference mockNormalizedNodeReference;

    private CrlCheckCommand command;
    private CrlCheckCommand commandOAM;

    private CommandContext context;

    private Map<String, Object> commandMap;
    private Map<String, Object> commandMapOAM;

    private String IPSEC;
    private String OAM;

    private List<String> inputNodesList;
    List<NodeReference> validNodesList = null;

    private boolean cRLCheckPartialResponse = false;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        IPSEC = "IPSEC";
        OAM = "OAM";

        inputNodesList = new LinkedList<String>();
        inputNodesList.add("node1");
        inputNodesList.add("node2");
        inputNodesList.add("node3");

        commandMap = new HashMap<String, Object>();
        commandMap.put(CrlCheckCommand.CERT_TYPE_PROPERTY, IPSEC);
        commandMap.put(NscsNodeCommand.NODE_LIST_PROPERTY, inputNodesList);

        command = new CrlCheckCommand();
        command.setProperties(commandMap);

        commandMapOAM = new HashMap<String, Object>();
        commandMapOAM = new HashMap<String, Object>();
        commandMapOAM.put(CrlCheckCommand.CERT_TYPE_PROPERTY, OAM);
        commandMapOAM.put(NscsNodeCommand.NODE_LIST_PROPERTY, inputNodesList);

        commandOAM = new CrlCheckCommand();
        commandOAM.setProperties(commandMapOAM);

        if (!cRLCheckPartialResponse) {
            MockitoAnnotations.initMocks(this);
            cRLCheckPartialResponse = true;
        }
    }

    @Test
    public void testProcess_IPSEC() {

        Mockito.when(commandManager.validateCertTypeValue(IPSEC)).thenReturn(true);
        Mockito.when(commandManager.validateNodesForCrlCheck(Mockito.anyList(), Mockito.anyString(), Mockito.anyList(), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyBoolean())).thenReturn(true);

        NscsCommandResponse response = checkGetStatusHandler.process(command, context);
        Assert.assertNotNull(((NscsNameMultipleValueCommandResponse) response).getAdditionalInformation());
    }

    @Test
    public void testProcess_OAM() {

        Mockito.when(commandManager.validateCertTypeValue(OAM)).thenReturn(true);
        Mockito.when(commandManager.validateNodesForCrlCheck(Mockito.anyList(), Mockito.anyString(), Mockito.anyList(), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyBoolean())).thenReturn(true);
        NscsCommandResponse response = checkGetStatusHandler.process(commandOAM, context);
        Assert.assertNotNull(((NscsNameMultipleValueCommandResponse) response).getAdditionalInformation());
    }

    @Test(expected = InvalidArgumentValueException.class)
    public void testProcess_InvalidArgumentValueException() {

        Mockito.when(commandManager.validateCertTypeValue(IPSEC)).thenReturn(false);

        checkGetStatusHandler.process(command, context);

    }

    @Test
    public void testProcessIPSEC_CrlCheckGetStatusNotExecuted() {

        Mockito.when(commandManager.validateCertTypeValue(IPSEC)).thenReturn(true);
        final NodeReference nodeRef = new NodeRef("node1");
        Mockito.when(mockReaderService.getNormalizableNodeReference(Mockito.any(NodeReference.class))).thenReturn(mockNormalizedNodeReference);
        Mockito.when(mockNormalizedNodeReference.getNormalizedRef()).thenReturn(nodeRef);
        validNodesList = new ArrayList<NodeReference>();
        validNodesList.add(nodeRef);

        Mockito.when(commandManager.validateNodesForCrlCheck(Mockito.anyList(), Mockito.anyString(), Mockito.anyList(), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyBoolean())).thenReturn(false);

        NscsCommandResponse response = checkGetStatusHandler.process(command, context);
        Assert.assertNotNull(((NscsNameMultipleValueCommandResponse) response).getAdditionalInformation());

    }

}
