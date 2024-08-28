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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.HttpsCommand;
import com.ericsson.nms.security.nscs.api.exception.CouldNotReadMoAttributeException;
import com.ericsson.nms.security.nscs.api.exception.GetHttpsWfException;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.utility.WebServerStatus;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.MoAttributeHandler;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;

@RunWith(MockitoJUnitRunner.class)
public class GetHttpsStatusHandlerTest {

    private static final ModelDefinition.Mo SECURITY_MO = Model.ME_CONTEXT.managedElement.systemFunctions.security;
    private static final ModelDefinition.Mo CPP_CONNECTIVITY_INFORMATION_MO = Model.NETWORK_ELEMENT.cppConnectivityInformation;
    private static final String WEBSERVER_ATTRIBUTE = ModelDefinition.Security.WEBSERVER;
    private static final String HTTPS_ATTRIBUTE = ModelDefinition.CppConnectivityInformation.HTTPS;
    private static final String NODE_NAME = "ERBS_001";
    private static final String FDN = "MeContext=" + NODE_NAME;
    private static final UUID JOB_UUID = UUID.randomUUID();
    private static final NormalizableNodeReference nodeReference = MockUtils.createNormalizableNodeRef(NODE_NAME);
    private static final JobStatusRecord statusRecord = new JobStatusRecord();
    private static final String[][] resultSuccessResponse = { { "NE status", "Configured Status", "Compare", "Error Message", "Suggested solution" },
            { "false", "HTTPS", "MISMATCH", "N/A", "N/A" } };
    private static final String[][] resultCouldNotReadMoAttributeException = {
            { "NE status", "Configured Status", "Compare", "Error Message", "Suggested solution" },
            { "N/A", "N/A", "N/A", "Could not read Mo Attribute", "Please check Online Help for correct syntax." } };
    private static final String[][] resultNodeNotSynchronized = {
            { "NE status", "Configured Status", "Compare", "Error Message", "Suggested solution" },
            { "N/A", "N/A", "N/A", "The node specified is not synchronized", "Please ensure the node specified is synchronized." } };
    private static final String[][] resultHttpsAttributeInvalidArgumentValueExceptionWhenNull = {
            { "NE status", "Configured Status", "Compare", "Error Message", "Suggested solution" },
            { null, "HTTPS", "MISMATCH", "An incorrect value null is encountered for HTTPS attribute.", "Update HTTPS value under CppConnectivityInformation MO to either true or false." } };
    private static final String[][] resultHttpsAttributeInvalidArgumentValueException = {
            { "NE status", "Configured Status", "Compare", "Error Message", "Suggested solution" },
            { "null", "HTTPS", "MISMATCH", "An incorrect value null is encountered for HTTPS attribute.", "Update HTTPS value under CppConnectivityInformation MO to either true or false." } };

    @Mock
    private Logger logger;

    @Mock
    private NscsLogger nscsLogger;

    @Spy
    @InjectMocks
    private GetHttpsStatusHandler statusHandler;

    @Mock
    private HttpsCommand httpsCommand;

    @Mock
    private NodeValidatorUtility nodeValidatorUtility;

    @Mock
    private NscsCMReaderService readerService;

    @Mock
    private MoAttributeHandler moAttributeHandler;

    @Mock
    private NscsJobCacheHandler nscsJobCacheHandler;

    @Mock
    private NscsCommandManager nscsCommandManager;

    @Mock
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility;

    private CommandContext commandContext;

    private  List<NodeReference> nodes = new ArrayList<>();

    @Before
    public void setUp(){
        nodes.add(new NodeRef(FDN));
        when(nscsInputNodeRetrievalUtility.getNodeReferenceList(httpsCommand)).thenReturn(nodes);
        doReturn(nodeReference).when(readerService).getNormalizableNodeReference(any(NodeReference.class));
        doReturn(statusRecord).when(nscsJobCacheHandler).insertJob(NscsCommandType.HTTPS_GET_STATUS);
        statusRecord.setJobId(JOB_UUID);
    }

    @Test
    public void processTaskShouldReturnSuccessResponse(){
        doReturn("HTTPS").when(moAttributeHandler).getMOAttributeValue(FDN, SECURITY_MO.type(), SECURITY_MO.namespace(), WEBSERVER_ATTRIBUTE);
        doReturn("HTTPS").when(moAttributeHandler).getMOAttributeValue(FDN, CPP_CONNECTIVITY_INFORMATION_MO.type(), CPP_CONNECTIVITY_INFORMATION_MO.namespace(), HTTPS_ATTRIBUTE);
        doReturn("MISMATCH").when(moAttributeHandler).match(any(WebServerStatus.class),any(Boolean.class));
        activateAndCheckTask(resultSuccessResponse);
    }

    @Test
    public void processTaskShouldReturnCouldNotReadMoAttributeResponse(){
        doThrow(new CouldNotReadMoAttributeException()).when(moAttributeHandler).getMOAttributeValue(FDN, SECURITY_MO.type(), SECURITY_MO.namespace(), WEBSERVER_ATTRIBUTE);
        activateAndCheckTask(resultCouldNotReadMoAttributeException);
    }

    @Test
    public void processTaskShouldReturnNodeNotSynchronizedResponse(){
        doThrow(new NodeNotSynchronizedException()).when(nodeValidatorUtility).validateNodeForHttpsStatus(nodeReference);
        activateAndCheckTask(resultNodeNotSynchronized);
    }

    @Test(expected = GetHttpsWfException.class)
    public void processTaskShouldThrowGetHttpsWfException(){
        doThrow(new NodeNotSynchronizedException()).when(nodeValidatorUtility).validateNodeForHttpsStatus(nodeReference);
        doThrow(new InvalidNodeNameException()).when(nscsCommandManager).executeGetHttpsStatusWfs(Matchers.anyListOf(NodeReference.class), Mockito.eq(statusRecord));
        statusHandler.process(httpsCommand, commandContext);
    }

    @Test
    public void processTaskShouldThrowException(){
        doThrow(new NullPointerException()).when(nodeValidatorUtility).validateNodeForHttpsStatus(nodeReference);
        statusHandler.process(httpsCommand, commandContext);
    }

    @Test
    public void processTaskShouldThrowInvalidArgumentValueException(){
        doReturn("HTTPS").when(moAttributeHandler).getMOAttributeValue(FDN, SECURITY_MO.type(), SECURITY_MO.namespace(), WEBSERVER_ATTRIBUTE);
        doReturn(null).when(moAttributeHandler).getMOAttributeValue(FDN, CPP_CONNECTIVITY_INFORMATION_MO.type(), CPP_CONNECTIVITY_INFORMATION_MO.namespace(), HTTPS_ATTRIBUTE);
        activateAndCheckTask(resultHttpsAttributeInvalidArgumentValueExceptionWhenNull);
        doReturn("HTTPS").when(moAttributeHandler).getMOAttributeValue(FDN, SECURITY_MO.type(), SECURITY_MO.namespace(), WEBSERVER_ATTRIBUTE);
        doReturn("null").when(moAttributeHandler).getMOAttributeValue(FDN, CPP_CONNECTIVITY_INFORMATION_MO.type(), CPP_CONNECTIVITY_INFORMATION_MO.namespace(), HTTPS_ATTRIBUTE);
        activateAndCheckTask(resultHttpsAttributeInvalidArgumentValueException);

    }

    private void activateAndCheckTask(String[][] expectedResults){
        NscsNameMultipleValueCommandResponse response = (NscsNameMultipleValueCommandResponse)statusHandler.process(httpsCommand,commandContext);
        Iterator<NscsNameMultipleValueCommandResponse.Entry> iterator = response.iterator();
        int index = 0;
        while(iterator.hasNext()){
            NscsNameMultipleValueCommandResponse.Entry entry = (NscsNameMultipleValueCommandResponse.Entry) iterator.next();
            assertThat(entry.getValues()).containsExactly(expectedResults[index]);
            index++;
        }
    }
}
