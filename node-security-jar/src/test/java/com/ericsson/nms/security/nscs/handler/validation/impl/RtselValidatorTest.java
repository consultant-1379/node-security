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
package com.ericsson.nms.security.nscs.handler.validation.impl;

import java.util.*;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.rtsel.NodeInfoDetails;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.impl.GetCiphersConfigurationImpl;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CiphersConfigurationUtil;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.rtsel.request.model.*;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;

@RunWith(MockitoJUnitRunner.class)
public class RtselValidatorTest {

    @InjectMocks
    RtselValidator rtselValidator;

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    NscsCMReaderService reader;

    @Mock
    protected NodeValidatorUtility nodeValidatorUtility;

    @Mock
    protected NscsCapabilityModelService nscsCapabilityModelService;

    @Mock
    protected CiphersConfigurationUtil ciphersConfigurationUtil;

    @Mock
    protected GetCiphersConfigurationImpl getCiphersConfigurationImpl;

    @Mock
    private NormalizableNodeReference normNode;

    @Mock
    private NscsErrorCodes nscsErrorCodes;

    @Mock
    private NodeReference nodeRef;

    @Mock
    MoObject moObject;

    private static final String ENTITY_PROFILE_NAME = "MicroRBSOAM_CHAIN_EP";
    private static final String ENROLLMENT_MODE = "CMPv2_INITIAL";
    private static final String KEY_SIZE = "RSA_2048";
    private NodeInfo nodeInfo = new NodeInfo();
    private NodeRtselConfig nodeRtselConfig = new NodeRtselConfig();
    private Nodes nodes = new Nodes();
    private NodeFdns nodeFdns = new NodeFdns();

    List<NodeInfoDetails> nodeInfoDetailsList = new ArrayList<NodeInfoDetails>();
    Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<NodeReference, NscsServiceException>();
    List<NodeInfo> nodeInfoList = new ArrayList<NodeInfo>();
    Set<String> duplicateNodes = new HashSet<String>();
    List<NodeRtselConfig> inputNodeRtselConfigList = new ArrayList<NodeRtselConfig>();

    @Before
    public void setup() throws Exception {
        List<String> nodeFdnsList = new ArrayList<String>();
        nodeFdnsList.add("LTE02ERBS00003");
        nodeFdnsList.add("LTE02ERBS00003");
        nodeFdnsList.add("LTE02ERBS00004");
        nodeFdns.getNodeFdn().addAll(nodeFdnsList);

        nodeInfo.setNodeFdns(nodeFdns);
        nodeInfo.setEntityProfileName(ENTITY_PROFILE_NAME);
        nodeInfo.setEnrollmentMode(ENROLLMENT_MODE);
        nodeInfo.setKeySize(KEY_SIZE);

        nodes.getNodeInfo().add(nodeInfo);

        nodeRtselConfig.setNodes(nodes);
        nodeRtselConfig.setConnAttemptTimeOut(12);
        nodeRtselConfig.setExtServerLogLevel("INFO");
        nodeRtselConfig.setExtServerAppName("Ericsson");

        inputNodeRtselConfigList.add(nodeRtselConfig);
    }

    @Test
    public void testValidateNodesForRtsel_Exception() {
        rtselValidator.validateNodes(nodeRtselConfig, nodeInfoDetailsList, duplicateNodes, invalidNodesErrorMap, NscsCommandType.RTSEL_ACTIVATE);
    }

    @Test
    public void testValidateDuplicateRtsel() {
        Set<String> duplicateNodes = rtselValidator.getDuplicateNodesForActivateRtsel(inputNodeRtselConfigList, invalidNodesErrorMap);
        Assert.assertNotNull(duplicateNodes);
    }
}