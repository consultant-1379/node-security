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
package com.ericsson.nms.security.nscs.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

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

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.cert.issue.CertIssueWfParams;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManagerProcessor;
import com.ericsson.nms.security.nscs.api.command.types.CertificateIssueCommand;
import com.ericsson.nms.security.nscs.api.command.types.CiphersConfigCommand;
import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.exception.CrlCheckEnableOrDisableWfException;
import com.ericsson.nms.security.nscs.api.exception.InvalidInputXMLFileException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.OnDemandCrlDownloadWfException;
import com.ericsson.nms.security.nscs.api.exception.UnSupportedNodeReleaseVersionException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedCertificateTypeException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.api.scheduler.WorkflowSchedulerInterface;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.ExternalCAEnrollmentInfo;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.NodeEnrollmentDetails;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.Nodes;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.Nodes.Node;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.enrollmentinfo.utility.TrustedCAInformation;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.NodeCiphers;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CipherJobInfo;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CiphersConstants;
import com.ericsson.nms.security.nscs.ldap.entities.NodeSpecificLdapConfiguration;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.trust.distribution.input.xml.Nodes.NodeTrustInfo;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameFieldType;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.enums.JobGlobalStatusEnum;
import com.ericsson.oss.services.enums.WfStatusEnum;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowNames;

@RunWith(MockitoJUnitRunner.class)
public class NscsCommandManagerBeanTest {

    private static final String USER = "user";
    private static final String WF_ID = "1234";
    private static final String NODE_NAME = "LTE03ERBS00003";
    private static final String IPSEC = "IPSEC";
    private static final String OAM = "OAM";
    private static final String INVALID_CERT_TYPE = "OEM";
    private static final String FILE_XML = "file:";

    @Spy
    private final Logger logger = LoggerFactory.getLogger(NscsCommandManagerBeanTest.class);

    @Mock
    private NodeValidatorUtility validatorUtility;

    @Mock
    private NscsPkiEntitiesManagerIF pkiManager;

    @Mock
    private NscsCapabilityModelService capabilityModel;

    @Mock
    private WorkflowHandler wfHandler;

    @Mock
    private WorkflowSchedulerInterface workflowScheduler;

    @Mock
    private NscsCommandManagerProcessor nscsCommandManagerProcessor;

    @InjectMocks
    private NscsCommandManagerBean testObj;

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    NormalizableNodeReference normNode;

    @Mock
    NormalizableNodeReference normalizableNodeReference;

    @Mock
    NormalizableNodeReference normalizableReference;

    @Mock
    private NscsCMReaderService reader;

    final String testFdnName = "Test_node_001";
    final String testFdnName1 = "Test_node_002";
    final String testSubjAltName = "10.0.0.1";
    final String testSubjAltNameIPv6 = "2001:1b70:82a1:103::64:19b";
    final String testSubjAltNameEmpty = "";
    final String testSubjAltNameType = "ip_address";
    final String testSubjectAltNameTypeIPv6 = SubjectAltNameFieldType.IP_ADDRESS.name();
    final String testEnrollmentMode = "scep";
    final String testEnrollmentModeIpv6 = EnrollmentMode.SCEP.name();
    final String testKeysize = "rsa_1024";
    final String testKeysizeIPv6 = AlgorithmKeys.RSA_1024.toString();
    final List<Node> inputList = new LinkedList<Node>();
    List<NodeReference> inputNodeRefList = new LinkedList<NodeReference>();
    List<NodeTrustInfo> inputTrustNodeList = new LinkedList<>();
    List<NodeSpecificLdapConfiguration> nodeSpecificLdapConfigurations = new LinkedList<>();
    List<NodeReference> nodeReferenceList = new LinkedList<>();
    List<NodeReference> validNodesList;
    List<TrustedCAInformation> trustedCAInformationlist;
    Map<NodeReference, NscsServiceException> invalidNodesErrorMap;
    Map<String, String[]> invalidDynamicNodesMap;
    String certType;
    WfResult result;
    JobStatusRecord jobStatusRecord;
    private CiphersConfigCommand ciphersConfigCommand;
    private Map<String, Object> commandMap;

    @Before
    public void setup() {
        final Node testNode = new Node(testFdnName);
        testNode.setSubjectAltName(testSubjAltName);
        testNode.setSubjectAltNameType(testSubjAltNameType);
        testNode.setEnrollmentMode(testEnrollmentMode);
        testNode.setKeySize(testKeysize);
        inputList.add(testNode);
        inputNodeRefList = NodeRef.from(testFdnName);
        final NodeTrustInfo node = new NodeTrustInfo();
        node.setNodeFdn(testFdnName);
        inputTrustNodeList.add(node);
        trustedCAInformationlist = new ArrayList<>();
        final TrustedCAInformation trustedCAInfo = new TrustedCAInformation();
        trustedCAInfo.setValidNodes(inputTrustNodeList);
        trustedCAInformationlist.add(trustedCAInfo);
        result = new WfResult();
        result.setWfId(WF_ID);
        result.setNodeName(NODE_NAME);
        result.setStatus(WfStatusEnum.RUNNING);
        result.setMessage("test");
        result.setWfWakeId(UUID.randomUUID());

        jobStatusRecord = new JobStatusRecord();
        final UUID jobId = UUID.randomUUID();
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.PENDING);
        jobStatusRecord.setUserId(USER);
        jobStatusRecord.setJobId(jobId);
        ciphersConfigCommand = new CiphersConfigCommand();
        commandMap = new HashMap<>();
        validNodesList = new ArrayList<>();

        nodeReferenceList = NodeRef.from(testFdnName, testFdnName1);
    }

    @Test
    public void validateCertTypeValue_PosTest_IPSEC() {
        assertTrue(testObj.validateCertTypeValue(IPSEC));
    }

    @Test
    public void validateCertTypeValue_PosTest_OAM() {
        assertTrue(testObj.validateCertTypeValue(OAM));
    }

    @Test
    public void validateCertTypeValue_NegTest_INVALID() {
        assertFalse(testObj.validateCertTypeValue(INVALID_CERT_TYPE));
    }

    @Test
    public void isInputNodeListValidForCommand_IPSEC_NegTest_EnrollmentOutOfRange() {

        final String path = "src/test/resources/SampleInputFileEnrollmentOutOfRangeForCertificateIssue.xml";
        final Nodes myNodes = createNodesFromInputFile(path);
        final List<Node> myInputNodeList = myNodes.getNode();
        final List<String> myErrorMsg = new ArrayList<>();

        doReturn(normNode).when(reader).getNormalizableNodeReference(Mockito.any(NodeReference.class));
        doReturn("15B-U.4.90").when(normNode).getOssModelIdentity();
        doReturn("ERBS").when(normNode).getNeType();

        final CertificateIssueCommand command = setupCommandIPSEC_missingParams(path);
        assertFalse("Must be false when Enrollment Mode is out of range.", testObj.isEnrollmentModeSupportedForNodeList(myInputNodeList, myErrorMsg));
    }

    @Test
    public void isInputNodeListValidForCommand_OAM_NegTest_EnrollmentOutOfRange() {

        final String path = "src/test/resources/SampleInputFileEnrollmentOutOfRangeForCertificateIssue.xml";
        final Nodes myNodes = createNodesFromInputFile(path);
        final List<Node> myInputNodeList = myNodes.getNode();
        final List<String> myErrorMsg = new ArrayList<>();

        doReturn(normNode).when(reader).getNormalizableNodeReference(Mockito.any(NodeReference.class));
        doReturn("15B-U.4.90").when(normNode).getOssModelIdentity();
        doReturn("ERBS").when(normNode).getNeType();

        assertFalse("Must be false when Enrollment mode is out of range.", testObj.isEnrollmentModeSupportedForNodeList(myInputNodeList, myErrorMsg));
    }

    //@Test(expected = CertificateIssueWfException.class)
    @Test
    public void executeCertificateIssue_IPSEC_Test() throws NscsPkiEntitiesManagerException {

        Mockito.when(capabilityModel.getIssueOrReissueCertWf(new NodeRef(testFdnName), IPSEC)).thenReturn(
                WorkflowNames.WORKFLOW_CPPIssueReissueCertificate_IpSec.toString());
        //        Mockito.when(wfHandler.startWorkflowInstance(Mockito.any(NodeReference.class), Mockito.anyString(), Mockito.anyMap())).thenThrow(CertificateIssueWfException.class);
        Mockito.when(pkiManager.isEntityNameAvailable(Mockito.anyString(), Mockito.any(EntityType.class))).thenReturn(true);

        Mockito.when(nscsCommandManagerProcessor.executeCertificateIssueSingleWf(Mockito.any(ExternalCAEnrollmentInfo.class), Mockito.any(Node.class),
                Mockito.any(CertIssueWfParams.class), Mockito.any(boolean.class), Mockito.anyString(), Mockito.any(JobStatusRecord.class),
                Mockito.anyInt())).thenReturn(result);
        final List<NodeEnrollmentDetails> nodeEnrollmentDetailsList = new ArrayList<NodeEnrollmentDetails>();
        testObj.executeCertificateIssueWfs(nodeEnrollmentDetailsList, IPSEC, jobStatusRecord);
    }

    //@Test(expected = CertificateIssueWfException.class)
    @Test
    public void executeCertificateIssue_IPSEC_SAN_IPv6_Test() throws NscsPkiEntitiesManagerException {

        Mockito.when(capabilityModel.getIssueOrReissueCertWf(new NodeRef(testFdnName), IPSEC)).thenReturn(
                WorkflowNames.WORKFLOW_CPPIssueReissueCertificate_IpSec.toString());
        //        Mockito.when(wfHandler.startWorkflowInstance(Mockito.any(NodeReference.class), Mockito.anyString(), Mockito.anyMap())).thenThrow(CertificateIssueWfException.class);
        Mockito.when(pkiManager.isEntityNameAvailable(Mockito.anyString(), Mockito.any(EntityType.class))).thenReturn(true);

        Mockito.when(nscsCommandManagerProcessor.executeCertificateIssueSingleWf(Mockito.any(ExternalCAEnrollmentInfo.class), Mockito.any(Node.class),
                Mockito.any(CertIssueWfParams.class), Mockito.any(boolean.class), Mockito.anyString(), Mockito.any(JobStatusRecord.class),
                Mockito.anyInt())).thenReturn(result);

        final List<NodeEnrollmentDetails> nodeEnrollmentDetailsList = new ArrayList<NodeEnrollmentDetails>();
        NodeEnrollmentDetails nodeEnrollmentDetails = new NodeEnrollmentDetails();
        final Nodes nodes = new Nodes();
        List<Nodes.Node> nodeList = new ArrayList<Nodes.Node>();
        final Node testNodeIpv6 = inputList.get(0);
        testNodeIpv6.setSubjectAltName(testSubjAltNameIPv6);
        testNodeIpv6.setSubjectAltNameType(testSubjectAltNameTypeIPv6);
        testNodeIpv6.setEnrollmentMode(testEnrollmentModeIpv6);
        testNodeIpv6.setKeySize(testKeysizeIPv6);
        nodeList.add(testNodeIpv6);
        nodes.setNode(nodeList);
        nodeEnrollmentDetails.setNodes(nodes);
        nodeEnrollmentDetailsList.add(nodeEnrollmentDetails);

        testObj.executeCertificateIssueWfs(nodeEnrollmentDetailsList, IPSEC, jobStatusRecord);
    }

    //@Test(expected = CertificateIssueWfException.class)
    @Test
    public void executeCertificateIssue_IPSEC_SAN_IPv4_Test() throws NscsPkiEntitiesManagerException {

        Mockito.when(capabilityModel.getIssueOrReissueCertWf(new NodeRef(testFdnName), IPSEC)).thenReturn(
                WorkflowNames.WORKFLOW_CPPIssueReissueCertificate_IpSec.toString());
        //        Mockito.when(wfHandler.startWorkflowInstance(Mockito.any(NodeReference.class), Mockito.anyString(), Mockito.anyMap())).thenThrow(CertificateIssueWfException.class);
        Mockito.when(pkiManager.isEntityNameAvailable(Mockito.anyString(), Mockito.any(EntityType.class))).thenReturn(true);

        Mockito.when(nscsCommandManagerProcessor.executeCertificateIssueSingleWf(Mockito.any(ExternalCAEnrollmentInfo.class), Mockito.any(Node.class),
                Mockito.any(CertIssueWfParams.class), Mockito.any(boolean.class), Mockito.anyString(), Mockito.any(JobStatusRecord.class),
                Mockito.anyInt())).thenReturn(result);

        final List<NodeEnrollmentDetails> nodeEnrollmentDetailsList = new ArrayList<NodeEnrollmentDetails>();
        NodeEnrollmentDetails nodeEnrollmentDetails = new NodeEnrollmentDetails();
        final Nodes nodes = new Nodes();
        List<Nodes.Node> nodeList = new ArrayList<Nodes.Node>();
        final Node testNodeIpv6 = inputList.get(0);
        testNodeIpv6.setSubjectAltNameType(testSubjectAltNameTypeIPv6);
        testNodeIpv6.setEnrollmentMode(testEnrollmentModeIpv6);
        testNodeIpv6.setKeySize(testKeysizeIPv6);
        nodeList.add(testNodeIpv6);
        nodes.setNode(nodeList);
        nodeEnrollmentDetails.setNodes(nodes);
        nodeEnrollmentDetailsList.add(nodeEnrollmentDetails);

        testObj.executeCertificateIssueWfs(nodeEnrollmentDetailsList, IPSEC, jobStatusRecord);
    }

    //@Test(expected = CertificateIssueWfException.class)
    @Test
    public void executeCertificateIssue_IPSEC_SAN_Empty_Test() throws NscsPkiEntitiesManagerException {

        Mockito.when(capabilityModel.getIssueOrReissueCertWf(new NodeRef(testFdnName), IPSEC)).thenReturn(
                WorkflowNames.WORKFLOW_CPPIssueReissueCertificate_IpSec.toString());
        //        Mockito.when(wfHandler.startWorkflowInstance(Mockito.any(NodeReference.class), Mockito.anyString(), Mockito.anyMap())).thenThrow(CertificateIssueWfException.class);
        Mockito.when(pkiManager.isEntityNameAvailable(Mockito.anyString(), Mockito.any(EntityType.class))).thenReturn(true);

        Mockito.when(nscsCommandManagerProcessor.executeCertificateIssueSingleWf(Mockito.any(ExternalCAEnrollmentInfo.class), Mockito.any(Node.class),
                Mockito.any(CertIssueWfParams.class), Mockito.any(boolean.class), Mockito.anyString(), Mockito.any(JobStatusRecord.class),
                Mockito.anyInt())).thenReturn(result);

        final List<NodeEnrollmentDetails> nodeEnrollmentDetailsList = new ArrayList<NodeEnrollmentDetails>();
        NodeEnrollmentDetails nodeEnrollmentDetails = new NodeEnrollmentDetails();
        final Nodes nodes = new Nodes();
        List<Nodes.Node> nodeList = new ArrayList<Nodes.Node>();
        final Node testNodeIpv6 = inputList.get(0);
        testNodeIpv6.setSubjectAltName(testSubjAltNameEmpty);
        testNodeIpv6.setSubjectAltNameType(testSubjectAltNameTypeIPv6);
        testNodeIpv6.setEnrollmentMode(testEnrollmentModeIpv6);
        testNodeIpv6.setKeySize(testKeysizeIPv6);
        nodeList.add(testNodeIpv6);
        nodes.setNode(nodeList);
        nodeEnrollmentDetails.setNodes(nodes);
        nodeEnrollmentDetailsList.add(nodeEnrollmentDetails);

        testObj.executeCertificateIssueWfs(nodeEnrollmentDetailsList, IPSEC, jobStatusRecord);
    }

    //@Test(expected = CertificateIssueWfException.class)
    @Test
    public void executeCertificateIssue_OAM_Test() throws NscsPkiEntitiesManagerException {

        Mockito.when(capabilityModel.getIssueOrReissueCertWf( NodeRef.from(testFdnName).get(0), OAM)).thenReturn(WorkflowNames.WORKFLOW_CPPIssueCertificate.toString());
        //        Mockito.when(wfHandler.startWorkflowInstance(Mockito.any(NodeReference.class), Mockito.anyString(), Mockito.anyMap())).thenThrow(CertificateIssueWfException.class);
        Mockito.when(pkiManager.isEntityNameAvailable(Mockito.anyString(), Mockito.any(EntityType.class))).thenReturn(true);

        Mockito.when(nscsCommandManagerProcessor.executeCertificateIssueSingleWf(Mockito.any(ExternalCAEnrollmentInfo.class), Mockito.any(Node.class),
                Mockito.any(CertIssueWfParams.class), Mockito.any(boolean.class), Mockito.anyString(), Mockito.any(JobStatusRecord.class),
                Mockito.anyInt())).thenReturn(result);
        final List<NodeEnrollmentDetails> nodeEnrollmentDetailsList = new ArrayList<NodeEnrollmentDetails>();
        testObj.executeCertificateIssueWfs(nodeEnrollmentDetailsList, OAM, jobStatusRecord);
    }

    @Test
    public void executeTrustDistributeWfs_IPSEC_Test() {

        final String certType = "IPSEC";
        final String caName = "";

        Mockito.when(capabilityModel.getTrustDistributeWf(new NodeRef(testFdnName), certType))
                .thenReturn(WorkflowNames.WORKFLOW_CPPIssueTrustCertIpSec.toString());

        testObj.executeTrustDistributeWfs(certType, caName, jobStatusRecord, trustedCAInformationlist);
    }

    @Test
    public void executeTrustDistributeWfs_OAM_Test() {

        final String certType = "OAM";
        final String caName = "";

        Mockito.when(capabilityModel.getTrustDistributeWf(new NodeRef(testFdnName), certType))
                .thenReturn(WorkflowNames.WORKFLOW_CPPIssueTrustCert.toString());

        testObj.executeTrustDistributeWfs(certType, caName, jobStatusRecord, trustedCAInformationlist);
    }

    @Test
    public void executeTrustRemoveWfs_IPSEC_Test() {

        final String certType = "IPSEC";
        final String issuerDn = "ERICSSON";
        final String serialNumber = "12345";

        Mockito.when(capabilityModel.getTrustRemoveWf(new NodeRef(testFdnName), certType))
                .thenReturn(WorkflowNames.WORKFLOW_CPPRemoveTrustIPSEC.toString());

        testObj.executeTrustRemoveWfs(inputNodeRefList, issuerDn, serialNumber, certType, jobStatusRecord);
    }

    @Test
    public void executeTrustRemoveWfs_OAM_Test() {

        final String certType = "OAM";
        final String issuerDn = "ERICSSON";
        final String serialNumber = "12345";

        Mockito.when(capabilityModel.getTrustRemoveWf(new NodeRef(testFdnName), certType))
                .thenReturn(WorkflowNames.WORKFLOW_CPPRemoveTrustOAM.toString());

        testObj.executeTrustRemoveWfs(inputNodeRefList, issuerDn, serialNumber, certType, jobStatusRecord);
    }

    private Nodes createNodesFromInputFile(final String filePath) {
        Nodes nodes = null;
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(Nodes.class);
            Unmarshaller jaxbUnmarshaller = null;
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            nodes = (Nodes) jaxbUnmarshaller.unmarshal(new File(filePath));
        } catch (final JAXBException e) {
            // If there is any JAXB conversion error, we are throwing an invalid
            // XML exception.
            // It should not happen as we have already validated input with XSD.
            logger.error("Invalid input XML file. Unmarshalling of XML failed.");
            throw new InvalidInputXMLFileException();
        }
        return nodes;
    }

    @SuppressWarnings("serial")
    private CertificateIssueCommand setupCommandIPSEC_missingParams(final String path) {
        final CertificateIssueCommand command = new CertificateIssueCommand();
        command.setCommandType(NscsCommandType.CERTIFICATE_ISSUE);
        final byte[] INPUT_FILE_CONTENT = convertFileToByteArray(path);
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
    private CertificateIssueCommand setupCommandIPSEC(final String path) {
        final CertificateIssueCommand command = new CertificateIssueCommand();
        command.setCommandType(NscsCommandType.CERTIFICATE_ISSUE);
        final byte[] INPUT_FILE_CONTENT = convertFileToByteArray(path);
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
    private CertificateIssueCommand setupCommandOAM(final String path) {
        final CertificateIssueCommand command = new CertificateIssueCommand();
        command.setCommandType(NscsCommandType.CERTIFICATE_ISSUE);
        final byte[] INPUT_FILE_CONTENT = convertFileToByteArray(path);
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

    @Test
    public void toEnrollmentMode_Test() {
        String enrollmentMode = "SCEP";
        assertEquals(enrollmentMode, testObj.toEnrollmentMode(enrollmentMode));

        enrollmentMode = "scep";
        assertEquals("", testObj.toEnrollmentMode(enrollmentMode));

        enrollmentMode = "CMPv2_VC";
        assertEquals(enrollmentMode, testObj.toEnrollmentMode(enrollmentMode));

        enrollmentMode = "cmpv2_VC";
        assertEquals("", testObj.toEnrollmentMode(enrollmentMode));

        enrollmentMode = "CMPv2_INITIAL";
        assertEquals(enrollmentMode, testObj.toEnrollmentMode(enrollmentMode));

        enrollmentMode = "cmpv2_INITIAL";
        assertEquals("", testObj.toEnrollmentMode(enrollmentMode));

        enrollmentMode = "CMPv2_UPDATE";
        assertEquals(enrollmentMode, testObj.toEnrollmentMode(enrollmentMode));

        enrollmentMode = "cmpv2_UPDATE";
        assertEquals("", testObj.toEnrollmentMode(enrollmentMode));

        enrollmentMode = "MANUAL";
        assertEquals(enrollmentMode, testObj.toEnrollmentMode(enrollmentMode));

        enrollmentMode = "manual";
        assertEquals("", testObj.toEnrollmentMode(enrollmentMode));

        enrollmentMode = "OFFLINE_PKCS12";
        assertEquals(enrollmentMode, testObj.toEnrollmentMode(enrollmentMode));

        enrollmentMode = "offLINE_PKCS12";
        assertEquals("", testObj.toEnrollmentMode(enrollmentMode));

        enrollmentMode = "OFFLINE_CSR";
        assertEquals(enrollmentMode, testObj.toEnrollmentMode(enrollmentMode));

        enrollmentMode = "OFFILINE_CSR";
        assertEquals("", testObj.toEnrollmentMode(enrollmentMode));

        enrollmentMode = "offILINE_CSR";
        assertEquals("", testObj.toEnrollmentMode(enrollmentMode));

        enrollmentMode = "ONLINE_SCEP";
        assertEquals(enrollmentMode, testObj.toEnrollmentMode(enrollmentMode));

        enrollmentMode = "onLINE_SCEP";
        assertEquals("", testObj.toEnrollmentMode(enrollmentMode));

        enrollmentMode = "NOT_SUPPORTED";
        assertEquals(enrollmentMode, testObj.toEnrollmentMode(enrollmentMode));

        enrollmentMode = "NOT_supported";
        assertEquals("", testObj.toEnrollmentMode(enrollmentMode));

        enrollmentMode = "";
        assertEquals("", testObj.toEnrollmentMode(enrollmentMode));

        enrollmentMode = null;
        assertEquals("", testObj.toEnrollmentMode(enrollmentMode));
    }

    @Test
    public void toAlgorithmKeySize_Test() {
        String keysize = "DSA_1024";
        assertEquals(keysize, testObj.toAlgorithmKeySize(keysize));

        keysize = "dsa_1024";
        assertEquals("", testObj.toAlgorithmKeySize(keysize));

        keysize = "RSA_1024";
        assertEquals(keysize, testObj.toAlgorithmKeySize(keysize));

        keysize = "rsa_1024";
        assertEquals("", testObj.toAlgorithmKeySize(keysize));

        keysize = "RSA_2048";
        assertEquals(keysize, testObj.toAlgorithmKeySize(keysize));

        keysize = "rsa_2048";
        assertEquals("", testObj.toAlgorithmKeySize(keysize));

        keysize = "RSA_4096";
        assertEquals(keysize, testObj.toAlgorithmKeySize(keysize));

        keysize = "rsa_4096";
        assertEquals("", testObj.toAlgorithmKeySize(keysize));

        keysize = "RSA_8192";
        assertEquals(keysize, testObj.toAlgorithmKeySize(keysize));

        keysize = "rsa_8192";
        assertEquals("", testObj.toAlgorithmKeySize(keysize));

        keysize = "RSA_16384";
        assertEquals(keysize, testObj.toAlgorithmKeySize(keysize));

        keysize = "rsa_16384";
        assertEquals("", testObj.toAlgorithmKeySize(keysize));

        keysize = "";
        assertEquals("", testObj.toAlgorithmKeySize(keysize));

        keysize = null;
        assertEquals("", testObj.toAlgorithmKeySize(keysize));
    }

    @Test
    public void toSubjectAltNameFieldType_Test() {
        String subjectAltNameType = "DNS_NAME";
        assertEquals(SubjectAltNameFieldType.DNS_NAME, testObj.toSubjectAltNameFieldType(subjectAltNameType));

        subjectAltNameType = "dns_name";
        assertEquals(SubjectAltNameFieldType.OTHER_NAME, testObj.toSubjectAltNameFieldType(subjectAltNameType));

        subjectAltNameType = "IP_ADDRESS";
        assertEquals(SubjectAltNameFieldType.IP_ADDRESS, testObj.toSubjectAltNameFieldType(subjectAltNameType));

        subjectAltNameType = "ip_address";
        assertEquals(SubjectAltNameFieldType.OTHER_NAME, testObj.toSubjectAltNameFieldType(subjectAltNameType));

        subjectAltNameType = "";
        assertEquals(SubjectAltNameFieldType.OTHER_NAME, testObj.toSubjectAltNameFieldType(subjectAltNameType));

        subjectAltNameType = null;
        assertEquals(SubjectAltNameFieldType.OTHER_NAME, testObj.toSubjectAltNameFieldType(subjectAltNameType));
    }

    @Test
    public void testValidateNodesForCrlCheck() {

        assertTrue(testObj.validateNodesForCrlCheck(inputNodeRefList, IPSEC, validNodesList, invalidNodesErrorMap, invalidDynamicNodesMap, false));
    }

    @Test
    public void testValidateNodesForCrlCheck_UnsupportedCertificateTypeException() {
        certType = "SECCC";
        invalidNodesErrorMap = new HashMap<NodeReference, NscsServiceException>();
        Mockito.doThrow(new UnsupportedCertificateTypeException()).when(validatorUtility).validateNodeForCrlCheck(Mockito.anyString(),
                Mockito.any(NodeReference.class), Mockito.anyString(), Mockito.anyBoolean());
        assertFalse(
                testObj.validateNodesForCrlCheck(inputNodeRefList, certType, validNodesList, invalidNodesErrorMap, invalidDynamicNodesMap, false));
    }

    @Test
    public void testValidateNodesForCrlCheck_NodeNotSynchronizedException() {
        invalidNodesErrorMap = new HashMap<NodeReference, NscsServiceException>();
        invalidDynamicNodesMap = new HashMap<String, String[]>();
        Mockito.doThrow(new NodeNotSynchronizedException()).when(validatorUtility).validateNodeForCrlCheck(Mockito.anyString(),
                Mockito.any(NodeReference.class), Mockito.anyString(), Mockito.anyBoolean());
        assertFalse(
                testObj.validateNodesForCrlCheck(inputNodeRefList, certType, validNodesList, invalidNodesErrorMap, invalidDynamicNodesMap, false));
    }

    @Test(expected = CrlCheckEnableOrDisableWfException.class)
    public void executeCrlCheckWfs_Test() {
        final String certType = "IPSEC";
        final String CRL_CHECK_STATUS = "ACTIVATED";
        Mockito.when(capabilityModel.getTrustRemoveWf(new NodeRef(testFdnName), certType))
                .thenReturn(WorkflowNames.WORKFLOW_CPPRemoveTrustIPSEC.toString());
        Mockito.when(nscsCommandManagerProcessor.executeCrlCheckWfs(Mockito.any(NodeReference.class), Mockito.anyString(), Mockito.anyString(),
                Mockito.any(JobStatusRecord.class), Mockito.anyInt())).thenThrow(CrlCheckEnableOrDisableWfException.class);
        testObj.executeCrlCheckWfs(inputNodeRefList, certType, CRL_CHECK_STATUS, jobStatusRecord);
    }

    @Test
    public void testValidateNodesForOnDemandCrlDownload() {

        assertTrue(testObj.validateNodesForOnDemandCrlDownload(inputNodeRefList, validNodesList, invalidNodesErrorMap, invalidDynamicNodesMap));
    }

    @Test
    public void testValidateNodesForOnDemandCrlDownload_UnsupportedNodeTypeException() {
        invalidNodesErrorMap = new HashMap<NodeReference, NscsServiceException>();
        Mockito.doThrow(new UnsupportedNodeTypeException()).when(validatorUtility).validateNodeForOnDemandCrlDownload(Mockito.anyString(),
                Mockito.any(NodeReference.class));
        assertFalse(testObj.validateNodesForOnDemandCrlDownload(inputNodeRefList, validNodesList, invalidNodesErrorMap, invalidDynamicNodesMap));
    }

    @Test
    public void testValidateNodesForOnDemandCrlDownload_UnSupportedNodeReleaseVersionException() {
        invalidNodesErrorMap = new HashMap<NodeReference, NscsServiceException>();
        Mockito.doThrow(new UnSupportedNodeReleaseVersionException()).when(validatorUtility).validateNodeForOnDemandCrlDownload(Mockito.anyString(),
                Mockito.any(NodeReference.class));
        assertFalse(testObj.validateNodesForOnDemandCrlDownload(inputNodeRefList, validNodesList, invalidNodesErrorMap, invalidDynamicNodesMap));
    }

    @Test
    public void testValidateNodesForOnDemandCrlDownload_NodeNotSynchronizedException() {
        invalidNodesErrorMap = new HashMap<NodeReference, NscsServiceException>();
        invalidDynamicNodesMap = new HashMap<String, String[]>();
        Mockito.doThrow(new NodeNotSynchronizedException()).when(validatorUtility).validateNodeForOnDemandCrlDownload(Mockito.anyString(),
                Mockito.any(NodeReference.class));
        assertFalse(testObj.validateNodesForOnDemandCrlDownload(inputNodeRefList, validNodesList, invalidNodesErrorMap, invalidDynamicNodesMap));
    }

    @Test(expected = OnDemandCrlDownloadWfException.class)
    public void executeOnDemandCrlDownloadWfs_OnDemandCrlDownloadWfException() {
        Mockito.when(nscsCommandManagerProcessor.executeCrlDownload(Mockito.any(NodeReference.class), Mockito.anyMap(),
                Mockito.any(JobStatusRecord.class), Mockito.anyInt())).thenThrow(OnDemandCrlDownloadWfException.class);
        testObj.executeOnDemandCrlDownloadWfs(inputNodeRefList, jobStatusRecord);
    }

    @Test
    public void executeOnDemandCrlDownloadWfs_Test() {
        Mockito.when(nscsCommandManagerProcessor.executeCrlDownload(Mockito.any(NodeReference.class), Mockito.anyMap(),
                Mockito.any(JobStatusRecord.class), Mockito.anyInt())).thenReturn(new WfResult());
        testObj.executeOnDemandCrlDownloadWfs(inputNodeRefList, jobStatusRecord);
        //        Mockito.verify(nscsLogger.workFlowStarted(WorkflowNames.WORKFLOW_COMECIM_ON_DEMAND_DOWNLOAD_CRL.getWorkflowName(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()),times(1));
    }

    /**
     * test method for {@link NscsCommandManagerBean#executeSetCiphersWfs()} for Ssh
     */
    @Test
    public void testExecuteSetCiphersWfs_For_Ssh() {
        commandMap.put(CiphersConfigCommand.PROTOCOL_PROPERTY, CiphersConstants.PROTOCOL_TYPE_SSH);
        commandMap.put(CiphersConfigCommand.MACS_PROPERTY, "[hmac-sha1,hmac-sha2-256]");
        commandMap.put(CiphersConfigCommand.KEX_PROPERTY, "[[diffie-hellman-group1-sha1,diffiehellman-group14-sha1]");
        commandMap.put(CiphersConfigCommand.ENCRYPT_ALGOS_PROPERTY, "[3des-cbc,aes128-ctr]");
        commandMap.put(CiphersConfigCommand.NODE_LIST_PROPERTY, testFdnName);
        Mockito.when(nscsCommandManagerProcessor.executeSetCiphersSingleWf(Mockito.any(NodeReference.class), Mockito.any(NodeCiphers.class),
                Mockito.any(JobStatusRecord.class), Mockito.anyInt())).thenReturn(result);
        final List<CipherJobInfo> list = new ArrayList<CipherJobInfo>();
        list.add(new CipherJobInfo(inputNodeRefList, new NodeCiphers()));
        testObj.executeSetCiphersWfs(list, jobStatusRecord);
        Mockito.verify(nscsCommandManagerProcessor, Mockito.times(1)).insertWorkflowBatch(Mockito.anyMap());
    }

    /**
     * test method for executeSetCiphersSingleWf for Tls
     */
    @Test
    public void testExecuteSetCiphersWfs_For_Tls() {
        commandMap.put(CiphersConfigCommand.PROTOCOL_PROPERTY, CiphersConstants.PROTOCOL_TYPE_TLS);
        commandMap.put(CiphersConfigCommand.CIPHER_FILTER_PROPERTY, "[ALL:-aRSA:-3DES:SHA256]");
        commandMap.put(CiphersConfigCommand.NODE_LIST_PROPERTY, testFdnName);
        Mockito.when(nscsCommandManagerProcessor.executeSetCiphersSingleWf(Mockito.any(NodeReference.class), Mockito.any(NodeCiphers.class),
                Mockito.any(JobStatusRecord.class), Mockito.anyInt())).thenReturn(result);
        final List<CipherJobInfo> list = new ArrayList<CipherJobInfo>();
        list.add(new CipherJobInfo(inputNodeRefList, new NodeCiphers()));
        testObj.executeSetCiphersWfs(list, jobStatusRecord);
        Mockito.verify(nscsCommandManagerProcessor, Mockito.times(1)).insertWorkflowBatch(Mockito.anyMap());
    }

    @Test
    public void testExecuteActivateHttpsWfs() {

        Mockito.when(nscsCommandManagerProcessor.executeActivateHttpsWfs(Mockito.any(NodeReference.class), Mockito.any(CertIssueWfParams.class),
                Mockito.anyBoolean(), Mockito.anyString(), Mockito.any(JobStatusRecord.class), Mockito.anyInt())).thenReturn(result);

        testObj.executeActivateHttpsWfs(nodeReferenceList, jobStatusRecord);

        Mockito.verify(nscsCommandManagerProcessor, Mockito.times(1)).insertWorkflowBatch(Mockito.anyMap());
    }

    @Test
    public void testExecuteDeactivateHttpsWfs() {

        Mockito.when(nscsCommandManagerProcessor.executeDeactivateHttpsWfs(Mockito.any(NodeReference.class), Mockito.any(JobStatusRecord.class),
                Mockito.anyInt())).thenReturn(result);

        testObj.executeDeactivateHttpsWfs(nodeReferenceList, jobStatusRecord);
        Mockito.verify(nscsCommandManagerProcessor, Mockito.times(1)).insertWorkflowBatch(Mockito.anyMap());
    }

    @Test
    public void testExecuteGetHttpsStatusWfs() {

        Mockito.when(nscsCommandManagerProcessor.executeGetHttpsStatusWfs(Mockito.any(NodeReference.class), Mockito.any(JobStatusRecord.class),
                Mockito.anyInt())).thenReturn(result);
        testObj.executeGetHttpsStatusWfs(nodeReferenceList, jobStatusRecord);

        final Map<UUID, WfResult> resultMap = new HashMap<>();
        resultMap.put(result.getWfWakeId(), result);
        resultMap.put(result.getWfWakeId(), result);

        verify(nscsCommandManagerProcessor).insertWorkflowBatch(resultMap);
    }

    @Test
    public void testExecuteConfigureLdapWfs() {
        NodeSpecificLdapConfiguration nodeSpecificLdapConfiguration = new NodeSpecificLdapConfiguration();
        nodeSpecificLdapConfigurations.add(nodeSpecificLdapConfiguration);
        Mockito.when(nscsCommandManagerProcessor.executeConfigureLdapWfs(Mockito.any(NodeSpecificLdapConfiguration.class),
                Mockito.any(JobStatusRecord.class), Mockito.anyInt())).thenReturn(result);
        testObj.executeConfigureLdapWfs(nodeSpecificLdapConfigurations, jobStatusRecord);

        final Map<UUID, WfResult> resultMap = new HashMap<>();
        resultMap.put(result.getWfWakeId(), result);
        resultMap.put(result.getWfWakeId(), result);

        verify(nscsCommandManagerProcessor).insertWorkflowBatch(resultMap);
    }

    @Test
    public void testExecuteReconfigureLdapWfs() {
        NodeSpecificLdapConfiguration nodeSpecificLdapConfiguration = new NodeSpecificLdapConfiguration();
        nodeSpecificLdapConfigurations.add(nodeSpecificLdapConfiguration);
        Mockito.when(nscsCommandManagerProcessor.executeConfigureLdapWfs(Mockito.any(NodeSpecificLdapConfiguration.class),
                Mockito.any(JobStatusRecord.class), Mockito.anyInt())).thenReturn(result);
        testObj.executeReconfigureLdapWfs(nodeSpecificLdapConfigurations, jobStatusRecord);

        final Map<UUID, WfResult> resultMap = new HashMap<>();
        resultMap.put(result.getWfWakeId(), result);
        resultMap.put(result.getWfWakeId(), result);

        verify(nscsCommandManagerProcessor).insertWorkflowBatch(resultMap);
    }

    @Test
    public void testExecuteRenewLdapWfs() {
        NodeSpecificLdapConfiguration nodeSpecificLdapConfiguration = new NodeSpecificLdapConfiguration();
        nodeSpecificLdapConfigurations.add(nodeSpecificLdapConfiguration);
        Mockito.when(nscsCommandManagerProcessor.executeLdapWf(Mockito.any(NodeSpecificLdapConfiguration.class), Mockito.any(JobStatusRecord.class),
                Mockito.anyInt(), Mockito.anyBoolean())).thenReturn(result);
        testObj.executeRenewLdapWfs(nodeSpecificLdapConfigurations, jobStatusRecord);

        final Map<UUID, WfResult> resultMap = new HashMap<>();
        resultMap.put(result.getWfWakeId(), result);
        resultMap.put(result.getWfWakeId(), result);

        verify(nscsCommandManagerProcessor).insertWorkflowBatch(resultMap);
    }

    @Test
    public void testExecuteTestWfsNullResult() {
        Mockito.when(
                nscsCommandManagerProcessor.executeTestSingleWf(Mockito.any(NodeRef.class), Mockito.any(JobStatusRecord.class), Mockito.anyInt()))
                .thenReturn(null);
        testObj.executeTestWfs(1, jobStatusRecord);
        final Map<UUID, WfResult> resultMap = new HashMap<>();
        verify(nscsCommandManagerProcessor).insertWorkflowBatch(resultMap);
    }

    @Test
    public void testExecuteTestWfsWithResult() {
        Mockito.when(
                nscsCommandManagerProcessor.executeTestSingleWf(Mockito.any(NodeRef.class), Mockito.any(JobStatusRecord.class), Mockito.anyInt()))
                .thenReturn(result);
        testObj.executeTestWfs(1, jobStatusRecord);
        final Map<UUID, WfResult> resultMap = new HashMap<>();
        resultMap.put(result.getWfWakeId(), result);
        verify(nscsCommandManagerProcessor).insertWorkflowBatch(resultMap);
    }
}
