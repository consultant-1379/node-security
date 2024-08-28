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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse;
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
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.enums.JobGlobalStatusEnum;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * Test Class for CrlCheckEnableHandler.
 * 
 * @author xkumkam
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CrlCheckEnableHandlerTest {

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    private NscsCommandManager commandManager;

    @Mock
    private NscsJobCacheHandler nscsJobCacheHandler;

    @InjectMocks
    CrlCheckEnableHandler cRLCheckEnableHandler;

    @Mock
    private NscsCMReaderService mockReaderService;
    
    @Mock
    NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility;

    @Mock
    private NormalizableNodeReference mockNormalizedNodeReference;

    @Mock
    private NscsContextService nscsContextService;

    JobStatusRecord jobStatusRecord;
    
    private CrlCheckCommand command;

    private CommandContext context;

    private Map<String, Object> commandMap = new HashMap<String, Object>();

    private String IPSEC;

    private List<String> inputNodesList;
    List<NodeReference> validNodesList = null;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        IPSEC = "IPSEC";

        inputNodesList = new LinkedList<String>();
        inputNodesList.add("node1");
        inputNodesList.add("node2");
        inputNodesList.add("node3");

        commandMap = new HashMap<String, Object>();
        commandMap.put(CrlCheckCommand.CERT_TYPE_PROPERTY, IPSEC);
        commandMap.put(NscsNodeCommand.NODE_LIST_PROPERTY, inputNodesList);

        command = new CrlCheckCommand();
        command.setProperties(commandMap);
        
        jobStatusRecord = new JobStatusRecord();
        UUID jobId = UUID.randomUUID();
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.PENDING);
        jobStatusRecord.setUserId("user");
        jobStatusRecord.setJobId(jobId);

    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.handler.command.impl.CRLCheckEnableHandler#process(com.ericsson.nms.security.nscs.api.command.types.CRLCheckCommand, com.ericsson.nms.security.nscs.handler.CommandContext)}
     * .
     */
    @Test
    public void testProcess() {

        Mockito.when(commandManager.validateCertTypeValue(IPSEC)).thenReturn(true);
        Mockito.when(commandManager.validateNodesForCrlCheck(Mockito.anyList(), Mockito.anyString(), Mockito.anyList(), Mockito.anyMap(), Mockito.anyMap(),Mockito.anyBoolean())).thenReturn(true);
        
        Mockito.when(nscsJobCacheHandler.insertJob((NscsCommandType) Mockito.any())).thenReturn(jobStatusRecord);
        NscsCommandResponse response = cRLCheckEnableHandler.process(command, context);
        Assert.assertEquals(((NscsMessageCommandResponse) response).getMessage(), String.format(CrlCheckEnableHandler.CRLCHECK_ENABLE_EXECUTED, jobStatusRecord.getJobId().toString()));
    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.handler.command.impl.CRLCheckEnableHandler#process(com.ericsson.nms.security.nscs.api.command.types.CRLCheckCommand, com.ericsson.nms.security.nscs.handler.CommandContext)}
     * .
     */
    @Test(expected = InvalidArgumentValueException.class)
    public void testProcess_InvalidArgumentValueException() {

        Mockito.when(commandManager.validateCertTypeValue(IPSEC)).thenReturn(false);

        cRLCheckEnableHandler.process(command, context);

    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.handler.command.impl.CRLCheckEnableHandler#process(com.ericsson.nms.security.nscs.api.command.types.CRLCheckCommand, com.ericsson.nms.security.nscs.handler.CommandContext)}
     * .
     */
    @Test
    public void testProcess_CrlCheckEnableNotExecuted() {

        Mockito.when(commandManager.validateCertTypeValue(IPSEC)).thenReturn(true);
        final NodeReference nodeRef = new NodeRef("node1");
        Mockito.when(mockReaderService.getNormalizableNodeReference(Mockito.any(NodeReference.class))).thenReturn(mockNormalizedNodeReference);
        Mockito.when(mockNormalizedNodeReference.getNormalizedRef()).thenReturn(nodeRef);
        validNodesList = new ArrayList<NodeReference>();
        validNodesList.add(nodeRef);

        Mockito.when(commandManager.validateNodesForCrlCheck(Mockito.anyList(), Mockito.anyString(), Mockito.anyList(), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyBoolean())).thenReturn(false);

        NscsCommandResponse response = cRLCheckEnableHandler.process(command, context);
        Assert.assertEquals(((NscsNameMultipleValueCommandResponse) response).getAdditionalInformation(), CrlCheckEnableHandler.CRLCHECK_ENABLE_NOT_EXECUTED);

    }

}
