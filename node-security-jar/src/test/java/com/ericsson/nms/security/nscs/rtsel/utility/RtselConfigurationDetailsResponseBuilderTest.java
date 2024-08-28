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
package com.ericsson.nms.security.nscs.rtsel.utility;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;

/**
 * Test class for BuildRtselConfigurationDetailsResponse.
 * 
 * @author xvekkar
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class RtselConfigurationDetailsResponseBuilderTest {

    @InjectMocks
    RtselConfigurationDetailsResponseBuilder rtselConfigurationDetailsResponseBuilder;

    @Mock
    NscsLogger nscsLogger;

    private final Map<String, Object> rtselMap = new HashMap<String, Object>();
    private final List<Map<String, Object>> serverConfigList = new ArrayList<Map<String, Object>>();
    private final Map<String, Object> serverConfigMap = new HashMap<String, Object>();
    private final Map<String, Map<String, Object>> rtselDetailsMap = new LinkedHashMap<String, Map<String, Object>>();
    private final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();
    private int NO_OF_COLUMNS = 9;
    private final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(NO_OF_COLUMNS - 1);
    private final String featureState = "ACTIVATED";
    private final String connectionAttemptTimeout = "10";
    private final String externalServerApplicationName = "Ericsson";
    private final String externalServerLogLevel = "DEBUG";
    private final String status = "Feature is Activated";
    private final String nodeName1 = "LTE01ERBS00001";
    private final String nodeName2 = "LTE01ERBS00001";
    private NscsCommandResponse commandResponse = null;

    @Before
    public void setUp() {
        serverConfigMap.put(RtselConstants.EXT_SERVER_NAME, "SYSLOG-1");
        serverConfigMap.put(RtselConstants.EXT_SERVER_PROTOCOL, "TLS_OVER_TCP");
        serverConfigMap.put(RtselConstants.EXT_SERVER_ADDRESS, "10.10.10.10");
        serverConfigList.add(serverConfigMap);
        rtselMap.put(RtselConstants.FEATURESTATE, featureState);
        rtselMap.put(RtselConstants.EXT_SERVER_LOGLEVEL, externalServerLogLevel);
        rtselMap.put(RtselConstants.CONN_TIMEOUT, connectionAttemptTimeout);
        rtselMap.put(RtselConstants.EXT_SERVER_APPNAME, externalServerApplicationName);
        rtselMap.put(RtselConstants.STATUS, status);
        rtselMap.put(RtselConstants.EXT_SERVER_LIST_CONFIG, serverConfigList);

    }

    @Test
    public void testProcessValidNode() {
        rtselDetailsMap.put(nodeName1, rtselMap);
        commandResponse = rtselConfigurationDetailsResponseBuilder.buildRtselDetailsResponse(rtselDetailsMap, invalidNodesErrorMap);
        assertNotNull(commandResponse);
        assertTrue(response.isNameMultipleValueResponseType());
        assertEquals(((NscsNameMultipleValueCommandResponse) commandResponse).getValueSize(), (NO_OF_COLUMNS - 1));

    }

    @Test
    public void testProcessInvalidNode() {
        NodeReference nodeRef = new NodeRef(nodeName1);
        invalidNodesErrorMap.put(nodeRef, new NodeNotSynchronizedException());
        commandResponse = rtselConfigurationDetailsResponseBuilder.buildRtselDetailsResponse(rtselDetailsMap, invalidNodesErrorMap);
        assertNotNull(commandResponse);
        assertTrue(response.isNameMultipleValueResponseType());
        assertEquals(((NscsNameMultipleValueCommandResponse) commandResponse).getValueSize(), (NO_OF_COLUMNS - 1));

    }

    @Test
    public void testProcessAllMultipleNodeValid() {
        rtselDetailsMap.put(nodeName1, rtselMap);
        rtselDetailsMap.put(nodeName2, rtselMap);
        commandResponse = rtselConfigurationDetailsResponseBuilder.buildRtselDetailsResponse(rtselDetailsMap, invalidNodesErrorMap);
        assertNotNull(commandResponse);
        assertTrue(response.isNameMultipleValueResponseType());
        assertEquals(((NscsNameMultipleValueCommandResponse) commandResponse).getValueSize(), (NO_OF_COLUMNS - 1));

    }

    @Test
    public void testProcessAllMultipleNodeInValid() {
        NodeReference nodeRef1 = new NodeRef(nodeName1);
        NodeReference nodeRef2 = new NodeRef(nodeName1);
        invalidNodesErrorMap.put(nodeRef1, new NodeNotSynchronizedException());
        invalidNodesErrorMap.put(nodeRef2, new NodeDoesNotExistException());
        commandResponse = rtselConfigurationDetailsResponseBuilder.buildRtselDetailsResponse(rtselDetailsMap, invalidNodesErrorMap);
        assertNotNull(commandResponse);
        assertTrue(response.isNameMultipleValueResponseType());
        assertEquals(((NscsNameMultipleValueCommandResponse) commandResponse).getValueSize(), (NO_OF_COLUMNS - 1));
    }

    @Test
    public void testProcessPartialSuccess() {
        NodeReference nodeRef1 = new NodeRef(nodeName1);
        rtselDetailsMap.put(nodeName2, rtselMap);
        invalidNodesErrorMap.put(nodeRef1, new NodeNotSynchronizedException());
        commandResponse = rtselConfigurationDetailsResponseBuilder.buildRtselDetailsResponse(rtselDetailsMap, invalidNodesErrorMap);
        assertNotNull(commandResponse);
        assertTrue(response.isNameMultipleValueResponseType());
        assertEquals(((NscsNameMultipleValueCommandResponse) commandResponse).getValueSize(), (NO_OF_COLUMNS - 1));
    }
}
