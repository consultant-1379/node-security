/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.CertificateIssueCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.InvalidFileContentException;
import com.ericsson.nms.security.nscs.api.exception.InvalidInputXMLFileException;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.EnrollmentDetails;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.NodeEnrollmentDetails;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.Nodes;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.Nodes.Node;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.XmlOperatorUtils;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.node.certificate.validator.CertificateIssueValidator;
import com.ericsson.nms.security.nscs.util.CliUtil;
import com.ericsson.nms.security.nscs.utilities.XMLUnMarshallerUtility;
import com.ericsson.nms.security.nscs.utilities.XmlValidatorUtility;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.enums.JobGlobalStatusEnum;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

@RunWith(MockitoJUnitRunner.class)
public class CertificateIssueHandlerTest {

    private static final byte[] EMPTY_BYTE_ARRAY = "".getBytes(Charset.forName("UTF-8"));
    private final String EMPTY_STRING = "";
    private final String IPSEC = "IPSEC";
    private final String OAM = "OAM";
    private final String INVALID_CERT_TYPE = "OEM";
    //private final String FILE_XML = "file:abc.xml";
    private final String FILE_XML = "file:";
    private final String ENROLLMENT_FILE = "<EnrollmentDetails><Nodes><Node><NodeFdn>LTE04dg2ERBS00011</NodeFdn><EnrollmentMode>CMPv2_VC</EnrollmentMode><SubjectAltName>192.168.100.119</SubjectAltName><SubjectAltNameType>IP_ADDRESS</SubjectAltNameType><KeySize>RSA_2048</KeySize></Node></Nodes></EnrollmentDetails>";

    @Mock
    private NscsLogger logger;

    @Mock
    private CommandContext mockCommandContext;

    @Mock
    private XmlValidatorUtility mockXmlUtility;

    @Mock
    private NscsCommandManager mockCommandManager;

    @Mock
    private List<String> myErrListString;

    @Mock
    private NscsJobCacheHandler cacheHandler;

    @Mock
    private CertificateIssueValidator certificateIssueValidator;
    
    @Mock
    private CliUtil cliUtil;

    @Mock
    private XmlOperatorUtils xmlOperatorUtils;

    @Mock
    private XMLUnMarshallerUtility xmlUnMarshallerUtility;

    @Mock
    private EnrollmentDetails enrollDetails;
    
    @InjectMocks
    private CertificateIssueHandler testObj;

    @Mock
    private NscsContextService nscsContextService;

    JobStatusRecord jobStatusRecord;
    
    private List<NodeEnrollmentDetails> nodeEnrollmentDetailsList = null;

    @Before
    public void setup() {
        jobStatusRecord = new JobStatusRecord();
        UUID jobId = UUID.randomUUID();
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.PENDING);
        jobStatusRecord.setUserId("user");
        jobStatusRecord.setJobId(jobId);
        nodeEnrollmentDetailsList = new ArrayList<NodeEnrollmentDetails>();
        NodeEnrollmentDetails nodeEnrollmentDetails = new NodeEnrollmentDetails();
        final Nodes nodes = new Nodes();
        Node node = new Node();
        node.setNodeFdn("LTE04dg2ERBS00011");
        List<Nodes.Node> nodeList = new ArrayList<Nodes.Node>();
        nodeList.add(node);
        nodeEnrollmentDetails.setNodes(nodes);
        nodeEnrollmentDetailsList.add(nodeEnrollmentDetails);
    }

    @Test
    public void testProcessAllValidNodesCertTypeIPSEC() {
        Mockito.when(mockCommandManager.validateCertTypeValue(Mockito.anyString())).thenReturn(true);
        Mockito.when(mockXmlUtility.validateXMLSchema(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

        final CertificateIssueCommand command = setupCommandIPSEC();
        Mockito.when(cliUtil.getCommandInputData(Mockito.any(CertificateIssueCommand.class), Mockito.anyString())).thenReturn(ENROLLMENT_FILE);
        Mockito.when(xmlOperatorUtils.transformXmlSchema(Mockito.anyString(), Mockito.anyString())).thenReturn(ENROLLMENT_FILE);
        Mockito.when(enrollDetails.getNodeEnrollmentDetails()).thenReturn(nodeEnrollmentDetailsList);
        Mockito.when(xmlUnMarshallerUtility.xMLUnmarshaller(ENROLLMENT_FILE, EnrollmentDetails.class)).thenReturn(enrollDetails);

        //set the command type
        jobStatusRecord.setCommandId(command.getCommandType().name());

        Mockito.when(cacheHandler.insertJob((NscsCommandType) Mockito.any())).thenReturn(jobStatusRecord);
        final NscsCommandResponse response = testObj.process(command, mockCommandContext);
        Assert.assertNotNull("Response can't be null", response);
        Assert.assertEquals("Expecting workflow started successfully message.",
                CertificateIssueHandler.CERTIFICATE_ISSUE_EXECUTED + ". Perform 'secadm job get -j " + jobStatusRecord.getJobId().toString() + "' to get progress info.",
                ((NscsMessageCommandResponse) response).getMessage());
    }

    @Test
    public void testProcessAllValidNodesCertTypeOAM() {
        Mockito.when(mockCommandManager.validateCertTypeValue(Mockito.anyString())).thenReturn(true);
        Mockito.when(mockXmlUtility.validateXMLSchema(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(cliUtil.getCommandInputData(Mockito.any(CertificateIssueCommand.class), Mockito.anyString())).thenReturn(ENROLLMENT_FILE);
        Mockito.when(xmlOperatorUtils.transformXmlSchema(Mockito.anyString(), Mockito.anyString())).thenReturn(ENROLLMENT_FILE);
        Mockito.when(enrollDetails.getNodeEnrollmentDetails()).thenReturn(nodeEnrollmentDetailsList);
        Mockito.when(xmlUnMarshallerUtility.xMLUnmarshaller(ENROLLMENT_FILE, EnrollmentDetails.class)).thenReturn(enrollDetails);

        //set the command type
        final CertificateIssueCommand command = setupCommandOAM();
        jobStatusRecord.setCommandId(command.getCommandType().name());
        Mockito.when(cacheHandler.insertJob(Matchers.any(NscsCommandType.class))).thenReturn(jobStatusRecord);

        final NscsCommandResponse response = testObj.process(command, mockCommandContext);
        Assert.assertNotNull("Response can't be null", response);
        Assert.assertEquals("Expecting workflow started successfully message.",
                CertificateIssueHandler.CERTIFICATE_ISSUE_EXECUTED + ". Perform 'secadm job get -j " + jobStatusRecord.getJobId().toString() + "' to get progress info.",
                ((NscsMessageCommandResponse) response).getMessage());
    }

    @Test
    public void testProcessSomeNodesWithInvalidConfigParams() {
        Mockito.when(mockCommandManager.validateCertTypeValue(Mockito.anyString())).thenReturn(true);
        Mockito.when(mockXmlUtility.validateXMLSchema(Mockito.anyString(), Mockito.anyString())).thenReturn(true);


        final CertificateIssueCommand command = setupCommandIPSEC();
        jobStatusRecord.setCommandId(command.getCommandType().name());

        Mockito.when(cacheHandler.insertJob(Matchers.any(NscsCommandType.class))).thenReturn(jobStatusRecord);
        Mockito.when(cliUtil.getCommandInputData(Mockito.any(CertificateIssueCommand.class), Mockito.anyString())).thenReturn(ENROLLMENT_FILE);
        Mockito.when(xmlOperatorUtils.transformXmlSchema(Mockito.anyString(), Mockito.anyString())).thenReturn(ENROLLMENT_FILE);
        Mockito.when(enrollDetails.getNodeEnrollmentDetails()).thenReturn(nodeEnrollmentDetailsList);
        Mockito.when(xmlUnMarshallerUtility.xMLUnmarshaller(ENROLLMENT_FILE, EnrollmentDetails.class)).thenReturn(enrollDetails);

        final NscsCommandResponse response = testObj.process(command, mockCommandContext);
        Assert.assertNotNull("Response can't be null", response);
        Assert.assertEquals("Expecting workflow started successfully message.",
                CertificateIssueHandler.CERTIFICATE_ISSUE_EXECUTED + ". Perform 'secadm job get -j " + jobStatusRecord.getJobId().toString() + "' to get progress info.",
                ((NscsMessageCommandResponse) response).getMessage());
    }

    @Test(expected = InvalidArgumentValueException.class)
    public void testProcessWithInvalidCertTypeValue() {
        final CertificateIssueCommand command = setupCommandWithInvalidCertType();
        jobStatusRecord.setCommandId(command.getCommandType().name());

        Mockito.when(cacheHandler.insertJob(Matchers.any(NscsCommandType.class))).thenReturn(jobStatusRecord);
        testObj.process(command, mockCommandContext);
    }

    @Test(expected = InvalidFileContentException.class)
    public void testProcessWithInvalidXML() {
        Mockito.when(mockCommandManager.validateCertTypeValue(Mockito.anyString())).thenReturn(true);
        final CertificateIssueCommand command = setupCommandWithInvalidXML();
        jobStatusRecord.setCommandId(command.getCommandType().name());

        Mockito.when(cacheHandler.insertJob(Matchers.any(NscsCommandType.class))).thenReturn(jobStatusRecord);
        testObj.process(command, mockCommandContext);
    }

    @Test(expected = InvalidInputXMLFileException.class)
    public void testProcessWithInvalidData() {
        Mockito.when(mockCommandManager.validateCertTypeValue(Mockito.anyString())).thenReturn(true);
        Mockito.when(mockXmlUtility.validateXMLSchema(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
        String path = "src/test/resources/SampleInvalidInputFileForCertificateIssue.xml";
        final CertificateIssueCommand command = setupCommandWithInvalidData(path);
        jobStatusRecord.setCommandId(command.getCommandType().name());
        Mockito.when(cliUtil.getCommandInputData(Mockito.any(CertificateIssueCommand.class), Mockito.anyString())).thenReturn(ENROLLMENT_FILE);
        Mockito.when(xmlOperatorUtils.transformXmlSchema(Mockito.anyString(), Mockito.anyString())).thenReturn(ENROLLMENT_FILE);
        Mockito.when(enrollDetails.getNodeEnrollmentDetails()).thenReturn(nodeEnrollmentDetailsList);
        Mockito.when(xmlUnMarshallerUtility.xMLUnmarshaller(ENROLLMENT_FILE, EnrollmentDetails.class)).thenReturn(enrollDetails);


        Mockito.when(cacheHandler.insertJob(Matchers.any(NscsCommandType.class))).thenReturn(jobStatusRecord);
        testObj.process(command, mockCommandContext);
    }

    @SuppressWarnings("serial")
    private CertificateIssueCommand setupCommandIPSEC() {
        final CertificateIssueCommand command = new CertificateIssueCommand();
        command.setCommandType(NscsCommandType.CERTIFICATE_ISSUE);
        final byte[] INPUT_FILE_CONTENT = convertFileToByteArray("src/test/resources/SampleInputFileForCertificateIssueIPSEC.xml");
        final Map<String, Object> commandMap = new HashMap<String, Object>() {
            {
                {
                    put(CertificateIssueCommand.XML_FILE_PROPERTY, FILE_XML);
                    put(CertificateIssueCommand.CERT_TYPE_PROPERTY, IPSEC);
                    put(FILE_XML, INPUT_FILE_CONTENT);
                }
            }
        };
        command.setProperties(commandMap);
        return command;
    }

    @SuppressWarnings("serial")
    private CertificateIssueCommand setupCommandWithCommonName() {
        final CertificateIssueCommand command = new CertificateIssueCommand();
        command.setCommandType(NscsCommandType.CERTIFICATE_ISSUE);
        final byte[] INPUT_FILE_CONTENT = convertFileToByteArray("src/test/resources/SampleInputFileForCertificateIssueWithCommonName.xml");
        final Map<String, Object> commandMap = new HashMap<String, Object>() {
            {
                {
                    put(CertificateIssueCommand.XML_FILE_PROPERTY, FILE_XML);
                    put(CertificateIssueCommand.CERT_TYPE_PROPERTY, IPSEC);
                    put(FILE_XML, INPUT_FILE_CONTENT);
                }
            }
        };
        command.setProperties(commandMap);
        return command;
    }

    @SuppressWarnings("serial")
    private CertificateIssueCommand setupCommandOAM() {
        final CertificateIssueCommand command = new CertificateIssueCommand();
        command.setCommandType(NscsCommandType.CERTIFICATE_ISSUE);
        final byte[] INPUT_FILE_CONTENT = convertFileToByteArray("src/test/resources/SampleInputFileForCertificateIssueIPSEC.xml");
        final Map<String, Object> commandMap = new HashMap<String, Object>() {
            {
                {
                    put(CertificateIssueCommand.XML_FILE_PROPERTY, FILE_XML);
                    put(CertificateIssueCommand.CERT_TYPE_PROPERTY, OAM);
                    put(FILE_XML, INPUT_FILE_CONTENT);
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
    private CertificateIssueCommand setupCommandWithInvalidCertType() {

        final CertificateIssueCommand command = new CertificateIssueCommand();
        command.setCommandType(NscsCommandType.CERTIFICATE_ISSUE);
        final byte[] INPUT_FILE_CONTENT = convertFileToByteArray("src/test/resources/SampleInputFileForCertificateIssueIPSEC.xml");
        final Map<String, Object> commandMap = new HashMap<String, Object>() {
            {
                {
                    put(CertificateIssueCommand.XML_FILE_PROPERTY, FILE_XML);
                    put(CertificateIssueCommand.CERT_TYPE_PROPERTY, INVALID_CERT_TYPE);
                    put(FILE_XML, INPUT_FILE_CONTENT);
                }
            }
        };
        command.setProperties(commandMap);
        return command;
    }

    @SuppressWarnings("serial")
    private CertificateIssueCommand setupCommandWithInvalidData(String path) {
        final CertificateIssueCommand command = new CertificateIssueCommand();
        command.setCommandType(NscsCommandType.CERTIFICATE_ISSUE);
        final byte[] INPUT_FILE_CONTENT = convertFileToByteArray(path);
        final Map<String, Object> commandMap = new HashMap<String, Object>() {

            {
                {
                    put(CertificateIssueCommand.XML_FILE_PROPERTY, FILE_XML);
                    put(CertificateIssueCommand.CERT_TYPE_PROPERTY, EMPTY_STRING);
                    put(FILE_XML, INPUT_FILE_CONTENT);
                }
            }
        };
        command.setProperties(commandMap);
        return command;
    }

    @SuppressWarnings("serial")
    private CertificateIssueCommand setupCommandWithIconsistentInputNodeDataWithCertType() {
        final CertificateIssueCommand command = new CertificateIssueCommand();
        command.setCommandType(NscsCommandType.CERTIFICATE_ISSUE);
        final byte[] INPUT_FILE_CONTENT = convertFileToByteArray("src/test/resources/SampleInputFileMissingAltNameAndTypeForCertificateIssue.xml");
        final Map<String, Object> commandMap = new HashMap<String, Object>() {

            {
                {
                    put(CertificateIssueCommand.XML_FILE_PROPERTY, FILE_XML);
                    put(CertificateIssueCommand.CERT_TYPE_PROPERTY, IPSEC);
                    put(FILE_XML, INPUT_FILE_CONTENT);
                }
            }
        };
        command.setProperties(commandMap);
        return command;
    }

    @SuppressWarnings("serial")
    private CertificateIssueCommand setupCommandWithInvalidXML() {
        final CertificateIssueCommand command = new CertificateIssueCommand();
        command.setCommandType(NscsCommandType.CERTIFICATE_ISSUE);
        //final byte[] INPUT_FILE_CONTENT = convertFileToByteArray("src/test/resources/SampleInlidInputFile.xml");
        //final byte[] INPUT_FILE_CONTENT = new byte[0];
        final byte[] INPUT_FILE_CONTENT = EMPTY_BYTE_ARRAY;
        final Map<String, Object> commandMap = new HashMap<String, Object>() {

            {
                {
                    put(CertificateIssueCommand.XML_FILE_PROPERTY, FILE_XML);
                    put(CertificateIssueCommand.CERT_TYPE_PROPERTY, IPSEC);
                    put(FILE_XML, INPUT_FILE_CONTENT);
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

    @Test
    public void testProcessAllValidNodesWithCommonName() {
        Mockito.when(mockCommandManager.validateCertTypeValue(Mockito.anyString())).thenReturn(true);
        Mockito.when(mockXmlUtility.validateXMLSchema(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

        final CertificateIssueCommand command = setupCommandWithCommonName();
        jobStatusRecord.setCommandId(command.getCommandType().name());
        Mockito.when(cliUtil.getCommandInputData(Mockito.any(CertificateIssueCommand.class), Mockito.anyString())).thenReturn(ENROLLMENT_FILE);
        Mockito.when(xmlOperatorUtils.transformXmlSchema(Mockito.anyString(), Mockito.anyString())).thenReturn(ENROLLMENT_FILE);
        Mockito.when(enrollDetails.getNodeEnrollmentDetails()).thenReturn(nodeEnrollmentDetailsList);
        Mockito.when(xmlUnMarshallerUtility.xMLUnmarshaller(ENROLLMENT_FILE, EnrollmentDetails.class)).thenReturn(enrollDetails);

        Mockito.when(cacheHandler.insertJob(Matchers.any(NscsCommandType.class))).thenReturn(jobStatusRecord);

        final NscsCommandResponse response = testObj.process(command, mockCommandContext);
        Assert.assertNotNull("Response can't be null", response);
        Assert.assertEquals("Expecting workflow started successfully message.",
                CertificateIssueHandler.CERTIFICATE_ISSUE_EXECUTED + ". Perform 'secadm job get -j " + jobStatusRecord.getJobId().toString() + "' to get progress info.",
                ((NscsMessageCommandResponse) response).getMessage());
    }
}
