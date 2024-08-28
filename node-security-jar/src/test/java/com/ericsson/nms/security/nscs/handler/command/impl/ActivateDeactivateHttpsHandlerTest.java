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

package com.ericsson.nms.security.nscs.handler.command.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.HttpsCommand;
import com.ericsson.nms.security.nscs.api.exception.HttpsActivateOrDeactivateWfException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.enums.JobGlobalStatusEnum;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * Created by ekrzsia on 7/21/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class ActivateDeactivateHttpsHandlerTest {

    @Mock
    private HttpsCommand httpsCommand;

    @Mock
    private CommandContext commandContext;

    @Mock
    private NscsJobCacheHandler nscsJobCacheHandler;

    @Mock
    private NscsCommandManager nscsCommandManager;

    @Mock
    private NscsLogger nscsLogger;

    @InjectMocks
    HttpsCommandHandlerHelper httpsCommandHandlerHelper;

    @Mock
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility;

    @Mock
    private NscsContextService nscsContextService;

    private final String NODE_FDN = "LTE02ERBS00001";
    private final String NODE_FDN1 = "LTE02ERBS00002";
    private final String NODE_FDN2 = "LTE02ERBS00003";

    private JobStatusRecord jobStatusRecord;
    private List<NodeReference> nodeReferenceList;
    private Map<String, Object> commandMap = new HashMap<>();
    private List<String> inputNodesList;

    @Before
    public void setupTest() {

        jobStatusRecord = new JobStatusRecord();
        UUID jobId = UUID.randomUUID();
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.PENDING);
        jobStatusRecord.setUserId("user");
        jobStatusRecord.setJobId(jobId);

        Mockito.when(nscsJobCacheHandler.insertJob((NscsCommandType) Mockito.any())).thenReturn(jobStatusRecord);

        inputNodesList = new LinkedList<>();
        inputNodesList.add(NODE_FDN);
        inputNodesList.add(NODE_FDN1);
        inputNodesList.add(NODE_FDN2);

        nodeReferenceList = NodeRef.from(inputNodesList);
        commandMap = new HashMap<>();
        commandMap.put(HttpsCommand.NODE_LIST_PROPERTY, inputNodesList);

        httpsCommand = new HttpsCommand();
        httpsCommand.setProperties(commandMap);
        Mockito.when(nscsInputNodeRetrievalUtility.getNodeReferenceList(httpsCommand)).thenReturn(nodeReferenceList);
    }

    @Test
    public void activateHttpsHandlerTest() {
        httpsCommandHandlerHelper.processActivate(httpsCommand, commandContext);
        Mockito.verify(nscsCommandManager, Mockito.times(1)).executeActivateHttpsWfs(nodeReferenceList,
                jobStatusRecord);
        Mockito.verify(nscsLogger, Mockito.times(1)).commandHandlerFinishedWithSuccess(Mockito.any(HttpsCommand.class),
                Mockito.anyString());
    }

    @Test(expected = HttpsActivateOrDeactivateWfException.class)
    public void executeActivateHttpsWfsExceptionTest() {
        Mockito.doThrow(
                new HttpsActivateOrDeactivateWfException("Exception from method executeActivateHttpsWfs invocation"))
                .when(nscsCommandManager).executeActivateHttpsWfs(nodeReferenceList, jobStatusRecord);
        httpsCommandHandlerHelper.processActivate(httpsCommand, commandContext);
    }

    @Test
    public void deactivateHttpsHandlerTest() {
        httpsCommandHandlerHelper.processDeactivate(httpsCommand, commandContext);
        Mockito.verify(nscsCommandManager, Mockito.times(1)).executeDeactivateHttpsWfs(nodeReferenceList,
                jobStatusRecord);
        Mockito.verify(nscsLogger, Mockito.times(1)).commandHandlerFinishedWithSuccess(Mockito.any(HttpsCommand.class),
                Mockito.anyString());
    }

    @Test(expected = HttpsActivateOrDeactivateWfException.class)
    public void executeActivateHttpsWfsException() {
        Mockito.doThrow(
                new HttpsActivateOrDeactivateWfException("Exception from method executeDeactivateHttpsWfs invocation"))
                .when(nscsCommandManager).executeDeactivateHttpsWfs(nodeReferenceList, jobStatusRecord);
        httpsCommandHandlerHelper.processDeactivate(httpsCommand, commandContext);
    }
}