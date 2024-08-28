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

import static org.mockito.Mockito.doReturn;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.security.auth.x500.X500Principal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.TrustRemoveCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.impl.TrustValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;
import com.ericsson.nms.security.nscs.utilities.XmlValidatorUtility;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.enums.JobGlobalStatusEnum;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

@RunWith(MockitoJUnitRunner.class)
public class TrustRemoveHandlerTest {

    private final String IPSEC = "IPSEC";
    private final String OAM = "OAM";
    private final String INVALID_CERT_TYPE = "OEM";
    private final String ISDN = "CN=ENM_Infrastructure_CA,O=ERICSSON,C=SE,OU=BUCI_DUAC_NAM";
    private final String SERIAL_NUM = "12345";
    private final String FILE_NAME = "testFileNewLine.txt";
    private final String FILE_TXT = "file:";
    private final String FILENAME_PROPERTY = "fileName";

    @Spy
    private final Logger logger = LoggerFactory.getLogger(TrustRemoveHandler.class);

    @Mock
    private NscsLogger nscslogger;

    @Mock
    private CommandContext mockCommandContext;

    @Mock
    private XmlValidatorUtility mockXmlUtility;

    @Mock
    private NscsCommandManager mockCommandManager;

    @Mock
    private NscsPkiEntitiesManagerIF nscsPkiManager;

    @Mock
    private NscsCMReaderService reader;

    @InjectMocks
    private TrustRemoveHandler testObj;

    @Mock
    private NscsJobCacheHandler cacheHandler;
    
    @Mock
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility;

    @Mock
    private TrustValidator trustValidator;

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
    }

    @Test
    public void testProcessAllValidNodesCertTypeIPSEC() {
        Mockito.when(mockCommandManager.validateCertTypeValue(Mockito.anyString())).thenReturn(true);

        final X509Certificate mockCert = Mockito.mock(X509Certificate.class);
        final X500Principal mock500Principal = new X500Principal(ISDN);
        doReturn(mock500Principal).when(mockCert).getIssuerX500Principal();

        final List<X509Certificate> certList = new ArrayList<>();
        final Map<String, List<X509Certificate>> trustsMap = new HashMap<>();
        certList.add(mockCert);
        trustsMap.put("key", certList);

        try {
            Mockito.when(nscsPkiManager.getCAsTrusts()).thenReturn(trustsMap);
        } catch (final NscsPkiEntitiesManagerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final List<String> inputList = Arrays.asList("node1", "node2", "node3", "node4", "node5", "MeContext=node6", "NetworkElement=node7");
        final TrustRemoveCommand command = setupCommandIPSEC(inputList);
        Mockito.when(cacheHandler.insertJob((NscsCommandType) Mockito.any())).thenReturn(jobStatusRecord);
        final NscsCommandResponse response = testObj.process(command, mockCommandContext);
        Assert.assertNotNull("Response can't be null", response);
        Assert.assertEquals("Expecting workflow started successfully message.",
                TrustRemoveHandler.TRUST_REMOVAL_EXECUTED + ". Perform 'secadm job get -j " + jobStatusRecord.getJobId().toString() + "' to get progress info." + TrustRemoveHandler.DEPRECATED_WARNING_MESSAGE,
                ((NscsMessageCommandResponse) response).getMessage());
    }

    @Test
    public void testProcessAllValidNodesCertTypeIPSEC_inputFile() {
        Mockito.when(mockCommandManager.validateCertTypeValue(Mockito.anyString())).thenReturn(true);
        final X509Certificate mockCert = Mockito.mock(X509Certificate.class);
        final X500Principal mock500Principal = new X500Principal(ISDN);
        doReturn(mock500Principal).when(mockCert).getIssuerX500Principal();

        final List<X509Certificate> certList = new ArrayList<>();
        final Map<String, List<X509Certificate>> trustsMap = new HashMap<>();
        certList.add(mockCert);
        trustsMap.put("key", certList);

        try {
            Mockito.when(nscsPkiManager.getCAsTrusts()).thenReturn(trustsMap);
        } catch (final NscsPkiEntitiesManagerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final TrustRemoveCommand command = setupCommandIPSEC_inputFile();
        Mockito.when(cacheHandler.insertJob((NscsCommandType) Mockito.any())).thenReturn(jobStatusRecord);
        final NscsCommandResponse response = testObj.process(command, mockCommandContext);
        Assert.assertNotNull("Response can't be null", response);
        Assert.assertEquals("Expecting workflow started successfully message.",
                TrustRemoveHandler.TRUST_REMOVAL_EXECUTED + ". Perform 'secadm job get -j " + jobStatusRecord.getJobId().toString() + "' to get progress info." + TrustRemoveHandler.DEPRECATED_WARNING_MESSAGE,
                ((NscsMessageCommandResponse) response).getMessage());
    }

    @Test
    public void testProcessAllValidNodesCertTypeOAM() {
        Mockito.when(mockCommandManager.validateCertTypeValue(Mockito.anyString())).thenReturn(true);

        final X509Certificate mockCert = Mockito.mock(X509Certificate.class);
        final X500Principal mock500Principal = new X500Principal(ISDN);
        doReturn(mock500Principal).when(mockCert).getIssuerX500Principal();

        final List<X509Certificate> certList = new ArrayList<>();
        final Map<String, List<X509Certificate>> trustsMap = new HashMap<>();
        certList.add(mockCert);
        trustsMap.put("key", certList);

        try {
            Mockito.when(nscsPkiManager.getCAsTrusts()).thenReturn(trustsMap);
        } catch (final NscsPkiEntitiesManagerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final TrustRemoveCommand command = setupCommandOAM();
        Mockito.when(cacheHandler.insertJob((NscsCommandType) Mockito.any())).thenReturn(jobStatusRecord);
        final NscsCommandResponse response = testObj.process(command, mockCommandContext);
        Assert.assertNotNull("Response can't be null", response);
        Assert.assertEquals("Expecting workflow started successfully message.",
                TrustRemoveHandler.TRUST_REMOVAL_EXECUTED + ". Perform 'secadm job get -j " + jobStatusRecord.getJobId().toString() + "' to get progress info." + TrustRemoveHandler.DEPRECATED_WARNING_MESSAGE,
                ((NscsMessageCommandResponse) response).getMessage());
    }

    @Test
    public void testProcessAllNodesCertTypeIPSEC() {
        Mockito.when(mockCommandManager.validateCertTypeValue(Mockito.anyString())).thenReturn(true);

        final X509Certificate mockCert = Mockito.mock(X509Certificate.class);
        final X500Principal mock500Principal = new X500Principal(ISDN);
        doReturn(mock500Principal).when(mockCert).getIssuerX500Principal();

        final List<X509Certificate> certList = new ArrayList<>();
        final Map<String, List<X509Certificate>> trustsMap = new HashMap<>();
        certList.add(mockCert);
        trustsMap.put("key", certList);

        try {
            Mockito.when(nscsPkiManager.getCAsTrusts()).thenReturn(trustsMap);
        } catch (final NscsPkiEntitiesManagerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final CmResponse mockCmResp = Mockito.mock(CmResponse.class);
        final CmObject mockCmObj = Mockito.mock(CmObject.class);
        final Collection<CmObject> cmObjColl = new ArrayList<>();
        cmObjColl.add(mockCmObj);
        doReturn(cmObjColl).when(mockCmResp).getCmObjects();
        doReturn("Node1").when(mockCmObj).getName();
        doReturn(1).when(mockCmResp).getStatusCode();

        Mockito.when(reader.getAllMos(Mockito.eq(Model.NETWORK_ELEMENT.type()), Mockito.eq(Model.NETWORK_ELEMENT.namespace())))
                .thenReturn(mockCmResp);

        final List<NodeReference> inputNodes = new ArrayList<>();
        final NodeReference noderef = new NodeRef("Node1");
        inputNodes.add(noderef);
        final Map<Entity, NodeReference> validEntityNodesMap = new HashMap<>();
        final Map<NodeReference, NscsServiceException> blockingErrors = new HashMap<>();
        final Map<String, String[]> nonBlockingErrors = new HashMap<>();
        final List<NodeReference> validNodesList = new ArrayList<NodeReference>();
        validNodesList.add(noderef);

        final TrustRemoveCommand command = setupCommandIPSECAllNodes();
        Mockito.when(cacheHandler.insertJob((NscsCommandType) Mockito.any())).thenReturn(jobStatusRecord);
        final NscsCommandResponse response = testObj.process(command, mockCommandContext);
        Assert.assertNotNull("Response can't be null", response);
        Assert.assertEquals("Expecting workflow started successfully message.",
                TrustRemoveHandler.TRUST_REMOVAL_EXECUTED + ". Perform 'secadm job get -j " + jobStatusRecord.getJobId().toString() + "' to get progress info." +TrustRemoveHandler.DEPRECATED_WARNING_MESSAGE,
                ((NscsMessageCommandResponse) response).getMessage());
    }

    @Test(expected = InvalidArgumentValueException.class)
    public void testProcessWithInvalidCertTypeValue() {
        final TrustRemoveCommand command = setupCommandWithInvalidCertType();
        Mockito.doThrow(InvalidArgumentValueException.class).when(trustValidator).validateCommandForCertTypeAndTrustCategory(Mockito.anyString(), Mockito.anyString());
        testObj.process(command, mockCommandContext);
    }

    @SuppressWarnings("serial")
    private TrustRemoveCommand setupCommandIPSECAllNodes() {
        final TrustRemoveCommand command = new TrustRemoveCommand();
        command.setCommandType(NscsCommandType.TRUST_REMOVE);
        final List<String> inputNodesList = new ArrayList<>();
        inputNodesList.add("all");
        final Map<String, Object> commandMap = new HashMap<String, Object>() {
            {
                {
                    put(TrustRemoveCommand.NODE_LIST_PROPERTY, inputNodesList);
                    put(TrustRemoveCommand.CERT_TYPE_PROPERTY, IPSEC);
                    put(TrustRemoveCommand.ISDN_PROPERTY, ISDN);
                    put(TrustRemoveCommand.SERIALNUMBER_PROPERTY, SERIAL_NUM);
                }
            }
        };
        command.setProperties(commandMap);
        return command;
    }

    @SuppressWarnings("serial")
    private TrustRemoveCommand setupCommandIPSEC(final List<String> inputList) {
        final TrustRemoveCommand command = new TrustRemoveCommand();
        command.setCommandType(NscsCommandType.TRUST_REMOVE);
        final List<String> inputNodesList = inputList;
        final Map<String, Object> commandMap = new HashMap<String, Object>() {
            {
                {
                    put(TrustRemoveCommand.NODE_LIST_PROPERTY, inputNodesList);
                    put(TrustRemoveCommand.CERT_TYPE_PROPERTY, IPSEC);
                    put(TrustRemoveCommand.ISDN_PROPERTY, ISDN);
                    put(TrustRemoveCommand.SERIALNUMBER_PROPERTY, SERIAL_NUM);
                }
            }
        };
        command.setProperties(commandMap);
        return command;
    }

    @SuppressWarnings("serial")
    private TrustRemoveCommand setupCommandIPSEC_inputFile() {
        final TrustRemoveCommand command = new TrustRemoveCommand();
        command.setCommandType(NscsCommandType.TRUST_REMOVE);
        final List<String> inputNodesList = Arrays.asList("node1", "node2", "node3", "node4", "node5", "MeContext=node6", "NetworkElement=node7");

        final byte[] INPUT_FILE_CONTENT = convertFileToByteArray("src/test/resources/" + FILE_NAME);
        final Map<String, Object> commandMap = new HashMap<String, Object>() {
            {
                {
                    put(TrustRemoveCommand.CERT_TYPE_PROPERTY, IPSEC);
                    put(TrustRemoveCommand.ISDN_PROPERTY, ISDN);
                    put(TrustRemoveCommand.SERIALNUMBER_PROPERTY, SERIAL_NUM);
                    put(TrustRemoveCommand.NODE_LIST_FILE_PROPERTY, FILE_TXT + FILE_NAME);
                    put(TrustRemoveCommand.NODE_LIST_PROPERTY, inputNodesList);
                    put(FILENAME_PROPERTY, FILE_NAME);
                    put(FILE_TXT, INPUT_FILE_CONTENT);
                }
            }
        };
        command.setProperties(commandMap);
        return command;
    }

    @SuppressWarnings("serial")
    private TrustRemoveCommand setupCommandOAM() {
        final TrustRemoveCommand command = new TrustRemoveCommand();
        command.setCommandType(NscsCommandType.TRUST_REMOVE);
        final List<String> inputNodesList = Arrays.asList("node1", "node2", "node3", "node4", "node5", "MeContext=node6", "NetworkElement=node7");
        final Map<String, Object> commandMap = new HashMap<String, Object>() {
            {
                {
                    put(TrustRemoveCommand.NODE_LIST_PROPERTY, inputNodesList);
                    put(TrustRemoveCommand.CERT_TYPE_PROPERTY, OAM);
                    put(TrustRemoveCommand.ISDN_PROPERTY, ISDN);
                    put(TrustRemoveCommand.SERIALNUMBER_PROPERTY, SERIAL_NUM);
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
    private TrustRemoveCommand setupCommandWithInvalidCertType() {

        final TrustRemoveCommand command = new TrustRemoveCommand();
        command.setCommandType(NscsCommandType.TRUST_REMOVE);
        final List<String> inputNodesList = Arrays.asList("node1", "node2", "node3", "node4", "node5", "MeContext=node6", "NetworkElement=node7");
        final Map<String, Object> commandMap = new HashMap<String, Object>() {
            {
                {
                    put(TrustRemoveCommand.NODE_LIST_PROPERTY, inputNodesList);
                    put(TrustRemoveCommand.CERT_TYPE_PROPERTY, INVALID_CERT_TYPE);
                    put(TrustRemoveCommand.ISDN_PROPERTY, ISDN);
                    put(TrustRemoveCommand.SERIALNUMBER_PROPERTY, SERIAL_NUM);
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
                } catch (final IOException e) {
                    // As this is JUnit, we are not logging the proper error.
                    e.printStackTrace();
                }
            }
        }
        return fileToBeParsed;
    }

}
