/*------------------------------------------------------------------------------
 *******************************************************************************
l * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.command.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.command.types.TrustDistributeCommand;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.impl.TrustValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;
import com.ericsson.nms.security.nscs.utilities.XmlValidatorUtility;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.enums.JobGlobalStatusEnum;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

@RunWith(MockitoJUnitRunner.class)
public class TrustDistributeHandlerTest {

    private final String IPSEC = "IPSEC";
    private final String OAM = "OAM";
    private final String INVALID_CERT_TYPE = "OEM";
    private final String FILE_NAME = "testFileNewLine.txt";
    private final String FILE_TXT = "file:";
    private final String FILENAME_PROPERTY = "fileName";
    private List<NodeReference> nodeReferenceList = new ArrayList<>();
    private List<String> inputNodesList = new ArrayList<>();
    private final String NODE_FDN = "LTE02ERBS00001";
    private final String NODE_FDN1 = "LTE02ERBS00002";
    private final String NODE_FDN2 = "LTE02ERBS00003";

    @Mock
    private NscsLogger logger;

    @Mock
    private CommandContext mockCommandContext;

    @Mock
    private XmlValidatorUtility mockXmlUtility;

    @Mock
    private NscsCommandManager mockCommandManager;

    @InjectMocks
    private TrustDistributeHandler testObj;

    @Mock
    private NscsJobCacheHandler cacheHandler;

    @Mock
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility;

    @Mock
    TrustValidator trustValidator;
    
    @Mock
    NscsNodeCommand command;

    @Mock
    private NscsContextService nscsContextService;

    JobStatusRecord jobStatusRecord;

    @Before
    public void setup() {
        jobStatusRecord = new JobStatusRecord();
        UUID jobId = UUID.randomUUID();
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.PENDING);
        jobStatusRecord.setUserId("user");
        jobStatusRecord.setJobId(jobId);
        
        inputNodesList.add(NODE_FDN);
        inputNodesList.add(NODE_FDN1);
        inputNodesList.add(NODE_FDN2);
        nodeReferenceList = NodeRef.from(inputNodesList);
        
    }

    @Test
    public void testProcessAllValidNodesCertTypeIPSEC() {
        Mockito.when(mockCommandManager.validateCertTypeValue(Mockito.anyString())).thenReturn(true);

        final TrustDistributeCommand command = setupCommandIPSEC();
        Mockito.when(cacheHandler.insertJob((NscsCommandType) Mockito.any())).thenReturn(jobStatusRecord);
        final NscsCommandResponse response = testObj.process(command, mockCommandContext);
        Assert.assertNotNull("Response can't be null", response);
        Assert.assertEquals("Expecting workflow started successfully message.", TrustDistributeHandler.TRUST_DISTRIBUTION_EXECUTED + ". Perform 'secadm job get -j "
                + jobStatusRecord.getJobId().toString() + "' to get progress info." + TrustDistributeHandler.DEPRECATED_WARNING_MESSAGE, ((NscsMessageCommandResponse) response).getMessage());
    }

    @Test
    public void testProcessAllValidNodesCertTypeIPSEC_inputFile() {
        Mockito.when(mockCommandManager.validateCertTypeValue(Mockito.anyString())).thenReturn(true);

        final TrustDistributeCommand command = setupCommandIPSEC_inputFile();
        Mockito.when(cacheHandler.insertJob((NscsCommandType) Mockito.any())).thenReturn(jobStatusRecord);
        final NscsCommandResponse response = testObj.process(command, mockCommandContext);
        Assert.assertNotNull("Response can't be null", response);
        Assert.assertEquals("Expecting workflow started successfully message.", TrustDistributeHandler.TRUST_DISTRIBUTION_EXECUTED + ". Perform 'secadm job get -j "
                + jobStatusRecord.getJobId().toString() + "' to get progress info." + TrustDistributeHandler.DEPRECATED_WARNING_MESSAGE, ((NscsMessageCommandResponse) response).getMessage());
    }

    @Test
    public void testProcessAllValidNodesCertTypeOAM() {
        Mockito.when(mockCommandManager.validateCertTypeValue(Mockito.anyString())).thenReturn(true);

        final TrustDistributeCommand command = setupCommandOAM();
        Mockito.when(nscsInputNodeRetrievalUtility.getNodeReferenceList(command)).thenReturn(nodeReferenceList);
        Mockito.when(cacheHandler.insertJob((NscsCommandType) Mockito.any())).thenReturn(jobStatusRecord);
        final NscsCommandResponse response = testObj.process(command, mockCommandContext);
        Assert.assertNotNull("Response can't be null", response);
        Assert.assertEquals("Expecting workflow started successfully message.", TrustDistributeHandler.TRUST_DISTRIBUTION_EXECUTED + ". Perform 'secadm job get -j "
                + jobStatusRecord.getJobId().toString() + "' to get progress info." + TrustDistributeHandler.DEPRECATED_WARNING_MESSAGE, ((NscsMessageCommandResponse) response).getMessage());
    }

    @Test
    public void testProcessSomeNodesWithInvalidConfigParams() {
        Mockito.when(mockCommandManager.validateCertTypeValue(Mockito.anyString())).thenReturn(true);

        // Mockito.when(testObj.get)
        // final String fileData = getInputData(command, "file:");
        final TrustDistributeCommand command = setupCommandIPSEC();
        final NscsCommandResponse response = testObj.process(command, mockCommandContext);
        Assert.assertNotNull("Response can't be null", response);
    }

    @SuppressWarnings("serial")
    private TrustDistributeCommand setupCommandIPSEC() {
        final TrustDistributeCommand command = new TrustDistributeCommand();
        command.setCommandType(NscsCommandType.TRUST_DISTRIBUTE);
        final List<String> inputNodesList = Arrays.asList("node1", "node2", "node3", "node4", "node5", "MeContext=node6", "NetworkElement=node7");
        // final String inputNodesList = "ERBS_CN_001,ERBS_CN_002";
        // final byte[] INPUT_FILE_CONTENT = convertFileToByteArray("src/test/resources/SampleInputFileForCertificateIssueIPSEC.xml");
        final Map<String, Object> commandMap = new HashMap<String, Object>() {
            {
                {
                    put(TrustDistributeCommand.NODE_LIST_PROPERTY, inputNodesList);
                    put(TrustDistributeCommand.CERT_TYPE_PROPERTY, IPSEC);
                }
            }
        };
        command.setProperties(commandMap);
        return command;
    }

    @SuppressWarnings("serial")
    private TrustDistributeCommand setupCommandIPSEC_inputFile() {
        final TrustDistributeCommand command = new TrustDistributeCommand();
        command.setCommandType(NscsCommandType.TRUST_DISTRIBUTE);
        final List<String> inputNodesList = Arrays.asList("node1", "node2", "node3", "node4", "node5", "MeContext=node6", "NetworkElement=node7");

        final byte[] INPUT_FILE_CONTENT = convertFileToByteArray("src/test/resources/" + FILE_NAME);
        final Map<String, Object> commandMap = new HashMap<String, Object>() {
            {
                {
                    put(TrustDistributeCommand.CERT_TYPE_PROPERTY, IPSEC);
                    put(TrustDistributeCommand.NODE_LIST_FILE_PROPERTY, FILE_TXT + FILE_NAME);
                    put(TrustDistributeCommand.COMMAND_TYPE_PROPERTY, NscsCommandType.TRUST_DISTRIBUTE);
                    put(TrustDistributeCommand.NODE_LIST_PROPERTY, inputNodesList);
                    put(FILENAME_PROPERTY, FILE_NAME);
                    put(FILE_TXT, INPUT_FILE_CONTENT);
                }
            }
        };
        command.setProperties(commandMap);
        return command;
    }

    @SuppressWarnings("serial")
    private TrustDistributeCommand setupCommandOAM() {
        final TrustDistributeCommand command = new TrustDistributeCommand();
        command.setCommandType(NscsCommandType.TRUST_DISTRIBUTE);
        final List<String> inputNodesList = Arrays.asList("node1", "node2", "node3", "node4", "node5", "MeContext=node6", "NetworkElement=node7");
        // final String inputNodesList = "ERBS_CN_001,ERBS_CN_002";
        // final byte[] INPUT_FILE_CONTENT = convertFileToByteArray("src/test/resources/SampleInputFileForCertificateIssueIPSEC.xml");
        final Map<String, Object> commandMap = new HashMap<String, Object>() {
            {
                {
                    put(TrustDistributeCommand.NODE_LIST_PROPERTY, inputNodesList);
                    put(TrustDistributeCommand.CERT_TYPE_PROPERTY, OAM);
                }
            }
        };
        command.setProperties(commandMap);
        return command;
    }

    /**
     * @return
     */
    @SuppressWarnings("serial")
    private TrustDistributeCommand setupCommandWithInvalidCertType() {

        final TrustDistributeCommand command = new TrustDistributeCommand();
        command.setCommandType(NscsCommandType.TRUST_DISTRIBUTE);
        final List<String> inputNodesList = Arrays.asList("node1", "node2", "node3", "node4", "node5", "MeContext=node6", "NetworkElement=node7");
        // final String inputNodesList = "ERBS_CN_001,ERBS_CN_002";
        // final byte[] INPUT_FILE_CONTENT = convertFileToByteArray("src/test/resources/SampleInputFileForCertificateIssueIPSEC.xml");
        final Map<String, Object> commandMap = new HashMap<String, Object>() {
            {
                {
                    put(TrustDistributeCommand.NODE_LIST_PROPERTY, inputNodesList);
                    put(TrustDistributeCommand.CERT_TYPE_PROPERTY, INVALID_CERT_TYPE);
                }
            }
        };
        command.setProperties(commandMap);
        return command;
    }

    private byte[] convertFileToByteArray(final String fileLocation) {
        final File file = new File(fileLocation);
        FileInputStream fileInputStream = null;

        final byte[] fileToBeParsed = new byte[(int) file.length()];

        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(fileToBeParsed);
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    // As this is JUnit, we are not logging the proper error.
                    e.printStackTrace();
                }
            }
        }
        return fileToBeParsed;
    }

}
