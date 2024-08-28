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
package com.ericsson.nms.security.nscs.manager;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManagerProcessor;
import com.ericsson.nms.security.nscs.api.command.manager.NscsRtselCommandManagerProcessor;
import com.ericsson.nms.security.nscs.api.rtsel.NodeInfoDetails;
import com.ericsson.nms.security.nscs.rtsel.request.model.*;
import com.ericsson.nms.security.nscs.rtsel.utility.RtselJobInfo;
import com.ericsson.nms.security.nscs.rtsel.utility.RtselUtility;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;

@RunWith(MockitoJUnitRunner.class)
public class NscsRtselCommandManagerBeanTest {

    @InjectMocks
    private NscsRtselCommandManagerBean nscsRtselCommandManagerBean;

    @Mock
    private NscsRtselCommandManagerProcessor nscsRtselCommandManagerProcessor;

    @Mock
    private NscsCommandManagerProcessor nscsCommandManagerProcessor;

    @Mock
    private Logger logger;
    
    @Mock
    RtselUtility rtselUtility;

    NodeInfo nodeInfo = new NodeInfo();
    NodeFdns nodeFdns = new NodeFdns();
    private NodeRtselConfig nodeRtselConfig = new NodeRtselConfig();

    private final String ENTITY_PROFILE_NAME = "MicroRBSOAM_CHAIN_EP";
    private final String ENROLLMENT_MODE_TYPE = "CMPv2_INITIAL";
    private final String KEY_SIZE = "RSA_2048";
    private final String NODE_FDN = "LTE02ERBS00003";
    private int ConnAttemptTimeOut = 12;
    private String ExtServerLogLevel = "INFO";
    private String ExtServerAppName = "Ericsson";
    JobStatusRecord jobStatusRecord;
    WfResult result;

    @Before
    public void setup() {

        Nodes rtselNodes = new Nodes();
        List<String> nodeFdnsList = new ArrayList<String>();
        nodeFdnsList.add(NODE_FDN);
        nodeFdns.getNodeFdn().addAll(nodeFdnsList);
        nodeInfo.setNodeFdns(nodeFdns);
        nodeInfo.setEnrollmentMode(ENROLLMENT_MODE_TYPE);
        nodeInfo.setEntityProfileName(ENTITY_PROFILE_NAME);
        nodeInfo.setKeySize(KEY_SIZE);

        rtselNodes.getNodeInfo().add(nodeInfo);
        nodeRtselConfig.setNodes(rtselNodes);
        nodeRtselConfig.setConnAttemptTimeOut(ConnAttemptTimeOut);
        nodeRtselConfig.setExtServerLogLevel(ExtServerLogLevel);
        nodeRtselConfig.setExtServerAppName(ExtServerAppName);

    }

    @Test
    public void testExecuteActivateRtselWfs() {
        final List<String> validNodeFdnsList = new ArrayList<String>();
        final List<NodeInfoDetails> nodeInfoDetailsList = new ArrayList<NodeInfoDetails>();

        final NodeInfoDetails nodeInfoDetails = new NodeInfoDetails(validNodeFdnsList, ENTITY_PROFILE_NAME, ENROLLMENT_MODE_TYPE, KEY_SIZE);
        validNodeFdnsList.add("LTE02ERBS00003");
        validNodeFdnsList.add("LTE02ERBS00004");
        nodeInfoDetails.setEnrollmentMode(ENROLLMENT_MODE_TYPE);
        nodeInfoDetails.setEntityProfileName(ENTITY_PROFILE_NAME);
        nodeInfoDetails.setKeySize(KEY_SIZE);
        nodeInfoDetails.setNodeFdnsList(validNodeFdnsList);
        nodeInfoDetailsList.add(nodeInfoDetails);
        Mockito.when(nscsRtselCommandManagerProcessor.executeActivateRtselSingleWf(NODE_FDN, nodeInfoDetails, nodeRtselConfig, jobStatusRecord, 1)).thenReturn(result);
        final List<RtselJobInfo> rtselJobInfoList = new ArrayList<RtselJobInfo>();
        rtselJobInfoList.add(new RtselJobInfo(nodeInfoDetailsList, new NodeRtselConfig()));
        nscsRtselCommandManagerBean.executeActivateRtselWfs(rtselJobInfoList, jobStatusRecord);


    }

}
