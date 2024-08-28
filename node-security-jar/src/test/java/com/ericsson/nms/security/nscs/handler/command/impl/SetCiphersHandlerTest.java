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

import static org.junit.Assert.assertTrue;

import java.util.*;

import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.CiphersConfigCommand;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CiphersConstants;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.SetCiphersResponseBuilder;
import com.ericsson.nms.security.nscs.handler.validation.ciphersconfig.CiphersConfigurationCommonValidator;
import com.ericsson.nms.security.nscs.handler.validation.ciphersconfig.SetCiphersValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.enums.JobGlobalStatusEnum;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

import javax.inject.Inject;

/**
 * Test Class for SetCiphersHandler
 *
 * @author xkumkam
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class SetCiphersHandlerTest {

    @InjectMocks
    SetCiphersHandler setCiphersHandler;

    @Mock
    private NscsCommandManager commandManager;

    @Mock
    private NscsJobCacheHandler nscsJobCacheHandler;

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    private CiphersConfigCommand command;

    @Mock
    private SetCiphersValidator setCiphersValidator;

    @Mock
    private SetCiphersResponseBuilder setCiphersResponseBuilder;

    @Mock
    private CommandContext context;

    @Inject
    CiphersConfigurationCommonValidator validator;

    @Mock
    private NscsContextService nscsContextService;

    @Mock
    private NscsNodeUtility nscsNodeUtility;

    private JobStatusRecord jobStatusRecord;
    private final String cipherFilterValue = "-aRSA:-3DES:SHA256";

    @Before
    public void setup() {
        jobStatusRecord = new JobStatusRecord();
        final UUID jobId = UUID.randomUUID();
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.PENDING);
        jobStatusRecord.setUserId("user");
        jobStatusRecord.setJobId(jobId);
        Map<String, Object> commandMap = new HashMap<>();
        commandMap.put(CiphersConfigCommand.CIPHER_FILTER_PROPERTY, cipherFilterValue);
        commandMap.put(CiphersConfigCommand.PROTOCOL_PROPERTY, CiphersConstants.PROTOCOL_TYPE_TLS);
        command.setProperties(commandMap);
    }

    /**
     * test method for  with no valid nodes protocol SSH
     */
    @Test
    public void testProcess_With_No_Valid_Nodes_SuccessScenario_SSH() throws NoSuchFieldException, IllegalAccessException {
        Mockito.when(command.getProtocolProperty()).thenReturn(CiphersConstants.PROTOCOL_TYPE_SSH);
        Mockito.when(command.getEncryptAlgosProperty()).thenReturn(CiphersConstants.ENCRYPT_ALGOS);
        Mockito.when(command.getKexProperty()).thenReturn(CiphersConstants.KEX);
        Mockito.when(command.getMacsProperty()).thenReturn(CiphersConstants.MACS);
        Mockito.when(nscsNodeUtility.convertStringToList(Mockito.anyString())).thenReturn(new ArrayList<>());

        Mockito.when(nscsJobCacheHandler.insertJob(NscsCommandType.SET_CIPHERS)).thenReturn(jobStatusRecord);
        List<NodeReference> nodeReferenceLst = new ArrayList<>();
        NodeReference nodeReference = new NodeRef("testNode");
        nodeReferenceLst.add(nodeReference);
        Mockito.when(command.getNodes()).thenReturn(nodeReferenceLst);

        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(CiphersConstants.NO_OF_COLUMNS);
        final  Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();
        Mockito.when(setCiphersResponseBuilder.buildResponseForAllInvalidInputNodes(command,invalidNodesErrorMap)).thenReturn(response);

        final NscsCommandResponse commandResponse = setCiphersHandler.process(command, context);
        assertTrue(commandResponse.isNameMultipleValueResponseType());
    }

    /**
     * test method for  with no valid nodes protocol SSH and empty property
     */
    @Test
    public void testProcess_With_No_Valid_Nodes_SuccessScenario_SSH_emptyproperty() throws NoSuchFieldException, IllegalAccessException {
        Mockito.when(command.getProtocolProperty()).thenReturn(CiphersConstants.PROTOCOL_TYPE_SSH);
        Mockito.when(command.getEncryptAlgosProperty()).thenReturn(CiphersConstants.EMPTY_TAG);
        Mockito.when(command.getKexProperty()).thenReturn(CiphersConstants.EMPTY_TAG);
        Mockito.when(command.getMacsProperty()).thenReturn(CiphersConstants.EMPTY_TAG);
        Mockito.when(nscsNodeUtility.convertStringToList(Mockito.anyString())).thenReturn(new ArrayList<>());

        Mockito.when(nscsJobCacheHandler.insertJob(NscsCommandType.SET_CIPHERS)).thenReturn(jobStatusRecord);
        List<NodeReference> nodeReferenceLst = new ArrayList<>();
        NodeReference nodeReference = new NodeRef("testNode");
        nodeReferenceLst.add(nodeReference);
        Mockito.when(command.getNodes()).thenReturn(nodeReferenceLst);

        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(CiphersConstants.NO_OF_COLUMNS);
        final  Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();
        Mockito.when(setCiphersResponseBuilder.buildResponseForAllInvalidInputNodes(command,invalidNodesErrorMap)).thenReturn(response);

        final NscsCommandResponse commandResponse = setCiphersHandler.process(command, context);
        assertTrue(commandResponse.isNameMultipleValueResponseType());
    }

    /**
     * test method for  with no valid nodes protocol SSH and empty tag
     */
    @Test
    public void testProcess_With_No_Valid_Nodes_SuccessScenario_SSH_emptytag() throws NoSuchFieldException, IllegalAccessException {
        Mockito.when(command.getProtocolProperty()).thenReturn(CiphersConstants.PROTOCOL_TYPE_SSH);
        Mockito.when(command.getEncryptAlgosProperty()).thenReturn("");
        Mockito.when(command.getKexProperty()).thenReturn("");
        Mockito.when(command.getMacsProperty()).thenReturn("");
        Mockito.when(nscsNodeUtility.convertStringToList(Mockito.anyString())).thenReturn(new ArrayList<>());

        Mockito.when(nscsJobCacheHandler.insertJob(NscsCommandType.SET_CIPHERS)).thenReturn(jobStatusRecord);
        List<NodeReference> nodeReferenceLst = new ArrayList<>();
        NodeReference nodeReference = new NodeRef("testNode");
        nodeReferenceLst.add(nodeReference);
        Mockito.when(command.getNodes()).thenReturn(nodeReferenceLst);

        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(CiphersConstants.NO_OF_COLUMNS);
        final  Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();
        Mockito.when(setCiphersResponseBuilder.buildResponseForAllInvalidInputNodes(command,invalidNodesErrorMap)).thenReturn(response);

        final NscsCommandResponse commandResponse = setCiphersHandler.process(command, context);
        assertTrue(commandResponse.isNameMultipleValueResponseType());
    }

    /**
     * test method for  with no valid nodes protocol TLS
     */
    @Test
    public void testProcess_With_No_Valid_Nodes_SuccessScenario_TLS() {
        Mockito.when(command.getProtocolProperty()).thenReturn(CiphersConstants.PROTOCOL_TYPE_TLS);
        Mockito.when(command.getCipherFilterProperty()).thenReturn(cipherFilterValue);
        Mockito.when(nscsJobCacheHandler.insertJob(NscsCommandType.SET_CIPHERS)).thenReturn(jobStatusRecord);
        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(CiphersConstants.NO_OF_COLUMNS);
        final  Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();
        Mockito.when(setCiphersResponseBuilder.buildResponseForAllInvalidInputNodes(command,invalidNodesErrorMap)).thenReturn(response);

        final NscsCommandResponse commandResponse = setCiphersHandler.process(command, context);
        assertTrue(commandResponse.isNameMultipleValueResponseType());
    }

    /**
     * test method for  with empty cipher filter for no valid nodes
     */
    @Test
    public void testProcess_With_Empty_Cipher_Filter_SuccessScenario() {
        Mockito.when(command.getProtocolProperty()).thenReturn(CiphersConstants.PROTOCOL_TYPE_TLS);
        String emptyCipherFilterValue = "<empty>";
        Mockito.when(command.getCipherFilterProperty()).thenReturn(emptyCipherFilterValue);
        Mockito.when(nscsJobCacheHandler.insertJob(NscsCommandType.SET_CIPHERS)).thenReturn(jobStatusRecord);
        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(CiphersConstants.NO_OF_COLUMNS);
        final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();
        Mockito.when(setCiphersResponseBuilder.buildResponseForAllInvalidInputNodes(command, invalidNodesErrorMap)).thenReturn(response);

        final NscsCommandResponse commandResponse = setCiphersHandler.process(command, context);
        assertTrue(commandResponse.isNameMultipleValueResponseType());
    }
}
