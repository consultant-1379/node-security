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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.EnrollmentInfoFileCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException;
import com.ericsson.nms.security.nscs.api.exception.NetworkElementNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.EnrollmentRequestInfo;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.XmlOperatorUtils;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.enrollmentinfo.request.NodeDetailsUnMarshaller;
import com.ericsson.nms.security.nscs.enrollmentinfo.request.NodeDetailsValidator;
import com.ericsson.nms.security.nscs.enrollmentinfo.request.model.NodeDetails;
import com.ericsson.nms.security.nscs.enrollmentinfo.request.model.NodeDetailsList;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.EnrollmentInfoProvider;
import com.ericsson.nms.security.nscs.enrollmentinfo.service.EnrollmentInfoServiceException;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.CliUtil;
import com.ericsson.nms.security.nscs.util.FileUtil;
import com.ericsson.nms.security.nscs.utilities.XmlValidatorUtility;

/**
 * Test Class for GenerateEnrollmentInfoFileHandler.
 * 
 * @author tcsviku
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class GenerateEnrollmentInfoFileHandlerTest {

    @InjectMocks
    GenerateEnrollmentInfoFileHandler generateEnrollmentInfoFileHandler;

    @Mock
    CommandContext context;

    @Mock
    NscsLogger nscsLogger;

    @Mock
    XmlValidatorUtility xmlValidatorUtility;

    @Mock
    NscsCommandManager commandManager;

    @Mock
    CliUtil cliUtil;

    @Mock
    FileUtil fileUtil;

    @Mock
    NodeDetailsUnMarshaller nodeDetailsUnMarshaller;

    @Mock
    NodeDetailsValidator nodeDetailsValidator;

    @Mock
    NscsCapabilityModelService nscsCapabilityModelService;

    @Mock
    EnrollmentInfoProvider enrollmentInfoProvider;

    @Mock
    NodeModelInformation nodeModelInformation;

    @Mock
    NscsCMReaderService nscsCmReaderService;

    @Mock
    XmlOperatorUtils xmlOperatorUtils;

    @Mock
    EnrollmentRequestInfo enrollmentRequestInfo;

    private final String FILE_URI = "file:";
    private final String XSD_VALIDATOR_FILE_NAME = "NodeDetailsSchema.xsd";
    private final String CERT_TYPE = "IPSEC";
    private final String OTHER_CERT_TYPE = "OAM";
    private final String ENROLLMENT_MODE_TYPE = "CMPv2_VC";
    private final String ENTITY_PROFILE_NAME = "DUSGen2OAM_CHAIN_EP";
    private final String OTHER_ENTITY_PROFILE_NAME = "OTHER_DUSGen2OAM_CHAIN_EP";
    private final String KEY_SIZE = "RSA_2048";
    private final String COMMON_NAME = "commonName";
    private final String NODE_FDN = "VPP00001";
    private final String EMPTY_FILE_DATA = "";
    private final File XML_FILE = new File("src/test/resources/node.xml");
    private String fileData;
    private EnrollmentInfoFileCommand command = new EnrollmentInfoFileCommand();
    private Map<String, Object> properties = new HashMap<String, Object>();
    private NodeDetails nodeDetails = new NodeDetails();
    private NodeDetails nodeDetails1 = new NodeDetails();
    private List<NodeDetails> nodeDetailsList = new ArrayList<NodeDetails>();
    private NodeDetailsList nodeSpecificConfigurationsBatch = new NodeDetailsList();
    NscsMessageCommandResponse nscsCommandResponseExcepted = new NscsMessageCommandResponse();
    EnrollmentInfo enrollmentInfo = new EnrollmentInfo();
    VerboseEnrollmentInfo verboseEnrollmentInfo = new VerboseEnrollmentInfo();
    EnrollmentCmpConfig enrollmentCmpConfig = new EnrollmentCmpConfig();

    @Before
    public void setUp() throws IOException {
        properties.put("command", NscsCommandType.ENROLLMENT_INFO_FILE);
        properties.put("verbose", Boolean.TRUE);
        command.setProperties(properties);
        nodeDetails.setCertType(CERT_TYPE);
        nodeDetails.setEnrollmentMode(ENROLLMENT_MODE_TYPE);
        nodeDetails.setNodeFdn(NODE_FDN);
        nodeDetails1.setCertType(CERT_TYPE);
        nodeDetails1.setNodeFdn("VPP00002");
        nodeDetails1.setEntityProfileName(ENTITY_PROFILE_NAME);
        nodeDetails1.setKeySize(KEY_SIZE);
        nodeDetails1.setCommonName(COMMON_NAME);
        nodeDetails1.setEnrollmentMode(ENROLLMENT_MODE_TYPE);
        nodeDetailsList.add(nodeDetails);
        nodeDetailsList.add(nodeDetails1);
        nodeSpecificConfigurationsBatch.setList(nodeDetailsList);
        enrollmentCmpConfig.setAuthorityName("C=SE, O=ERICSSON, OU=BUCI_DUAC_NAM, CN=NE_OAM_CA");
        enrollmentCmpConfig.setAuthorityType("REGISTRATION_AUTHORITY");
        enrollmentCmpConfig.setCacerts("oamCmpCaTrustCategory");
        enrollmentCmpConfig.setTrustedCerts("oamTrustCategory");
        enrollmentCmpConfig.setEnrollmentServerGroupId("1");
        enrollmentCmpConfig.setEnrollmentServerId("1");
        enrollmentCmpConfig.setEnrollmentAuthority("NE_OAM_CA");
        enrollmentCmpConfig.setNodeCredentialId("oamNodeCredential");
        verboseEnrollmentInfo.setEnrollmentCmpConfig(enrollmentCmpConfig);
        enrollmentInfo.setVerboseEnrollmentInfo(verboseEnrollmentInfo);
        verboseEnrollmentInfo.setCertificateType("OAM");
        TrustCategories trustCategories = new TrustCategories();
        TrustCategory trustCategory = new TrustCategory();
        trustCategory.setName("1");
        List<TrustCategory> trustCategoryList = new ArrayList<>();
        trustCategoryList.add(trustCategory);
        trustCategories.setTrustCategory(trustCategoryList);
        verboseEnrollmentInfo.setTrustCategories(trustCategories);

        TrustedCertificates trustedCertificates = new TrustedCertificates();
        List<TrustedCertificate> trustedCertificateList = new ArrayList<>();
        TrustedCertificate trustedCertificate = new TrustedCertificate();
        trustedCertificate.setCafingerprint("51:DB:DC:81:07:4B:32:2F:11:DA:7A:C0:D0:84:96:21:B3:E2:19:71");
        trustedCertificate.setCaPem("capem");
        trustedCertificate.setCaSubjectName("Sample");
        trustedCertificate.setName("TrustedCertificate1");
        trustedCertificate.setTdpsUri("http://[2001:1b70:82a1:103::181]:8093/pki-ra-tdps/ca_entity/ENM_PKI_Root_CA/533c110561e1bbf4/active/ENM_PKI_Root_CA");

        CertificateRevocation certificateRevocation = new CertificateRevocation();
        certificateRevocation.setCrlUri("cdps_usi");
        certificateRevocation.setCrlName("ipv4_name");
        List<CertificateRevocation> certificateRevocationsLst = new ArrayList<>();
        certificateRevocationsLst.add(certificateRevocation);
        CertificateRevocations certificateRevocations = new CertificateRevocations();
        certificateRevocations.setCertificateRevocations(certificateRevocationsLst);
        trustedCertificate.setCrls(certificateRevocations);

        trustedCertificateList.add(trustedCertificate);
        trustedCertificates.setTrustedCertificate(trustedCertificateList);

        verboseEnrollmentInfo.setTrustedCertificates(trustedCertificates);
        fileData = getFileData(XML_FILE);
        nscsCommandResponseExcepted.setMessage("Security file generated successfully.");
    }

    @Test
    public void testProcess_FileDataNull() {

        Mockito.when(cliUtil.getCommandInputData(command, FILE_URI)).thenReturn(null);
        generateEnrollmentInfoFileHandler.process(command, context);
    }

    @Test
    public void testProcess_FileDataEmpty() {
        Mockito.when(cliUtil.getCommandInputData(command, FILE_URI)).thenReturn(EMPTY_FILE_DATA);
        generateEnrollmentInfoFileHandler.process(command, context);
    }

    @Test
    public void testProcess_NullNodeList() {

        NodeDetailsList nodeSpecificConfigurationsBatch2 = new NodeDetailsList();
        Mockito.when(cliUtil.getCommandInputData(command, FILE_URI)).thenReturn(fileData);
        Mockito.when(xmlValidatorUtility.validateXMLSchema(fileData, XSD_VALIDATOR_FILE_NAME)).thenReturn(true);
        Mockito.when(nodeDetailsUnMarshaller.buildNodeDetailsFromXmlContent(fileData)).thenReturn(nodeSpecificConfigurationsBatch2);
        generateEnrollmentInfoFileHandler.process(command, context);
    }

    @Test
    public void testProcess_EmptyNodeList() {

        NodeDetailsList nodeSpecificConfigurationsBatch2 = new NodeDetailsList();
        List<NodeDetails> nodeDetailsList1 = new ArrayList<NodeDetails>();
        nodeSpecificConfigurationsBatch2.setList(nodeDetailsList1);
        Mockito.when(cliUtil.getCommandInputData(command, FILE_URI)).thenReturn(fileData);
        Mockito.when(xmlValidatorUtility.validateXMLSchema(fileData, XSD_VALIDATOR_FILE_NAME)).thenReturn(true);
        Mockito.when(nodeDetailsUnMarshaller.buildNodeDetailsFromXmlContent(fileData)).thenReturn(nodeSpecificConfigurationsBatch2);
        generateEnrollmentInfoFileHandler.process(command, context);
    }

    @Test
    public void testProcess_WithDuplicate() {

        NodeDetailsList nodeSpecificConfigurationsBatch2 = new NodeDetailsList();
        List<NodeDetails> nodeDetailsList1 = new ArrayList<NodeDetails>();
        NodeDetails nodeDetails1 = new NodeDetails();
        nodeDetails1.setNodeFdn(NODE_FDN);
        nodeDetails1.setCertType(CERT_TYPE);
        nodeDetails1.setEntityProfileName(ENTITY_PROFILE_NAME);
        NodeDetails nodeDetails2 = new NodeDetails();
        nodeDetails2.setNodeFdn(NODE_FDN);
        nodeDetails2.setCertType(CERT_TYPE);
        nodeDetails2.setEntityProfileName(ENTITY_PROFILE_NAME);
        nodeDetailsList1.add(nodeDetails1);
        nodeDetailsList1.add(nodeDetails2);
        nodeSpecificConfigurationsBatch2.setList(nodeDetailsList1);
        Mockito.when(cliUtil.getCommandInputData(command, FILE_URI)).thenReturn(fileData);
        Mockito.when(xmlValidatorUtility.validateXMLSchema(fileData, XSD_VALIDATOR_FILE_NAME)).thenReturn(true);
        Mockito.when(nodeDetailsUnMarshaller.buildNodeDetailsFromXmlContent(fileData)).thenReturn(nodeSpecificConfigurationsBatch2);
        generateEnrollmentInfoFileHandler.process(command, context);
    }

    @Test
    public void testProcess_WithConflictingDuplicates() {

        NodeDetailsList nodeSpecificConfigurationsBatch2 = new NodeDetailsList();
        List<NodeDetails> nodeDetailsList1 = new ArrayList<NodeDetails>();
        NodeDetails nodeDetails1 = new NodeDetails();
        nodeDetails1.setNodeFdn(NODE_FDN);
        nodeDetails1.setCertType(CERT_TYPE);
        nodeDetails1.setEntityProfileName(ENTITY_PROFILE_NAME);
        NodeDetails nodeDetails2 = new NodeDetails();
        nodeDetails2.setNodeFdn(NODE_FDN);
        nodeDetails2.setCertType(CERT_TYPE);
        nodeDetails2.setEntityProfileName(OTHER_ENTITY_PROFILE_NAME);
        nodeDetailsList1.add(nodeDetails1);
        nodeDetailsList1.add(nodeDetails2);
        nodeSpecificConfigurationsBatch2.setList(nodeDetailsList1);
        Mockito.when(cliUtil.getCommandInputData(command, FILE_URI)).thenReturn(fileData);
        Mockito.when(xmlValidatorUtility.validateXMLSchema(fileData, XSD_VALIDATOR_FILE_NAME)).thenReturn(true);
        Mockito.when(nodeDetailsUnMarshaller.buildNodeDetailsFromXmlContent(fileData)).thenReturn(nodeSpecificConfigurationsBatch2);
        generateEnrollmentInfoFileHandler.process(command, context);
    }

    @Test
    public void testProcess_WithNonConflictingDuplicates() {

        NodeDetailsList nodeSpecificConfigurationsBatch2 = new NodeDetailsList();
        List<NodeDetails> nodeDetailsList1 = new ArrayList<NodeDetails>();
        NodeDetails nodeDetails1 = new NodeDetails();
        nodeDetails1.setNodeFdn(NODE_FDN);
        nodeDetails1.setCertType(CERT_TYPE);
        nodeDetails1.setEntityProfileName(ENTITY_PROFILE_NAME);
        NodeDetails nodeDetails2 = new NodeDetails();
        nodeDetails2.setNodeFdn(NODE_FDN);
        nodeDetails2.setCertType(OTHER_CERT_TYPE);
        nodeDetails2.setEntityProfileName(OTHER_ENTITY_PROFILE_NAME);
        nodeDetailsList1.add(nodeDetails1);
        nodeDetailsList1.add(nodeDetails2);
        nodeSpecificConfigurationsBatch2.setList(nodeDetailsList1);
        Mockito.when(cliUtil.getCommandInputData(command, FILE_URI)).thenReturn(fileData);
        Mockito.when(xmlValidatorUtility.validateXMLSchema(fileData, XSD_VALIDATOR_FILE_NAME)).thenReturn(true);
        Mockito.when(nodeDetailsUnMarshaller.buildNodeDetailsFromXmlContent(fileData)).thenReturn(nodeSpecificConfigurationsBatch2);
        generateEnrollmentInfoFileHandler.process(command, context);
    }

    @Test
    public void testProcess_InvalidNode() throws IOException, EnrollmentInfoServiceException {

        Mockito.when(cliUtil.getCommandInputData(command, FILE_URI)).thenReturn(fileData);
        Mockito.when(xmlValidatorUtility.validateXMLSchema(fileData, XSD_VALIDATOR_FILE_NAME)).thenReturn(true);
        Mockito.when(nodeDetailsUnMarshaller.buildNodeDetailsFromXmlContent(fileData)).thenReturn(nodeSpecificConfigurationsBatch);
        Mockito.doThrow(new NetworkElementNotfoundException()).when(nodeDetailsValidator).validate(Mockito.any());
        generateEnrollmentInfoFileHandler.process(command, context);
    }

    @Test
    public void testProcess_ValidNodeFailedEnrollment() throws IOException, EnrollmentInfoServiceException {

        Mockito.when(cliUtil.getCommandInputData(command, FILE_URI)).thenReturn(fileData);
        Mockito.when(xmlValidatorUtility.validateXMLSchema(fileData, XSD_VALIDATOR_FILE_NAME)).thenReturn(true);
        Mockito.when(nodeDetailsUnMarshaller.buildNodeDetailsFromXmlContent(fileData)).thenReturn(nodeSpecificConfigurationsBatch);
        Mockito.doThrow(new EnrollmentInfoServiceException("")).when(enrollmentInfoProvider).getEnrollmentInfo(Mockito.any(), Mockito.anyBoolean());
        generateEnrollmentInfoFileHandler.process(command, context);
    }

    @Test
    public void testProcess_SingleNodeJAXBException() throws IOException, EnrollmentInfoServiceException {

        NodeDetailsList nodeSpecificConfigurationsBatch2 = new NodeDetailsList();
        List<NodeDetails> nodeDetailsList2 = new ArrayList<NodeDetails>();
        NodeDetails nodeDetails2 = new NodeDetails();
        nodeDetails2.setNodeFdn(NODE_FDN);
        nodeDetails2.setCertType(CERT_TYPE);
        nodeDetails2.setEntityProfileName(ENTITY_PROFILE_NAME);
        nodeDetailsList2.add(nodeDetails2);
        nodeSpecificConfigurationsBatch2.setList(nodeDetailsList2);
        Mockito.when(cliUtil.getCommandInputData(command, FILE_URI)).thenReturn(fileData);
        Mockito.when(xmlValidatorUtility.validateXMLSchema(fileData, XSD_VALIDATOR_FILE_NAME)).thenReturn(true);
        Mockito.when(nodeDetailsUnMarshaller.buildNodeDetailsFromXmlContent(fileData)).thenReturn(nodeSpecificConfigurationsBatch2);
        Mockito.when(enrollmentInfoProvider.getEnrollmentInfo(Mockito.any(), Mockito.anyBoolean())).thenReturn(enrollmentInfo);
        EnrollmentRequestInfo enrollmentRequestInfo2 = new EnrollmentRequestInfo();
        enrollmentRequestInfo2.setNodeName(NODE_FDN);
        Mockito.when(nodeDetailsValidator.validate(nodeDetails2)).thenReturn(enrollmentRequestInfo2);
        generateEnrollmentInfoFileHandler.process(command, context);
    }

    @Test
    public void testProcess() throws IOException, EnrollmentInfoServiceException {

        Mockito.when(cliUtil.getCommandInputData(command, FILE_URI)).thenReturn(fileData);
        Mockito.when(xmlValidatorUtility.validateXMLSchema(fileData, XSD_VALIDATOR_FILE_NAME)).thenReturn(true);
        Mockito.when(nodeDetailsUnMarshaller.buildNodeDetailsFromXmlContent(fileData)).thenReturn(nodeSpecificConfigurationsBatch);
        Mockito.when(enrollmentInfoProvider.getEnrollmentInfo(Mockito.any(), Mockito.anyBoolean())).thenReturn(enrollmentInfo);
        EnrollmentRequestInfo enrollmentRequestInfo = new EnrollmentRequestInfo();
        enrollmentRequestInfo.setNodeName(NODE_FDN);
        EnrollmentRequestInfo enrollmentRequestInfo1 = new EnrollmentRequestInfo();
        enrollmentRequestInfo1.setNodeName(NODE_FDN);
        Mockito.when(nodeDetailsValidator.validate(nodeDetails)).thenReturn(enrollmentRequestInfo);
        Mockito.when(nodeDetailsValidator.validate(nodeDetails1)).thenReturn(enrollmentRequestInfo1);
        generateEnrollmentInfoFileHandler.process(command, context);
    }

    @Test
    public void testProcessForMultipleNodes() throws IOException, JAXBException, EnrollmentInfoServiceException {
        ByteArrayOutputStream bOutput = new ByteArrayOutputStream(12);
        bOutput.write("hello".getBytes());

        NodeDetails nodeDetails = new NodeDetails();

        NodeDetailsList nodeSpecificConfigurationsBatch2 = new NodeDetailsList();
        List<NodeDetails> nodeDetailsList2 = new ArrayList<NodeDetails>();
        properties.put("command", NscsCommandType.ENROLLMENT_INFO_FILE);
        command.setProperties(properties);
        nodeDetails.setCertType(CERT_TYPE);
        nodeDetails.setNodeFdn("VPP00001");
        nodeDetails.setSubjectAltName("SAN");
        nodeDetails.setSubjectAltNameType("IPV6");
        nodeDetails.setEnrollmentMode(ENROLLMENT_MODE_TYPE);
        nodeDetails1.setEntityProfileName(ENTITY_PROFILE_NAME);
        nodeDetails1.setKeySize(KEY_SIZE);
        nodeDetails1.setCommonName(COMMON_NAME);
        nodeDetails1.setSubjectAltName("SAN");
        nodeDetails1.setSubjectAltNameType("IPV6");
        nodeDetailsList2.add(nodeDetails);
        nodeDetailsList2.add(nodeDetails1);
        nodeSpecificConfigurationsBatch2.setList(nodeDetailsList2);
        Mockito.when(cliUtil.getCommandInputData(command, FILE_URI)).thenReturn(fileData);
        Mockito.when(xmlValidatorUtility.validateXMLSchema(fileData, XSD_VALIDATOR_FILE_NAME)).thenReturn(true);
        Mockito.when(nodeDetailsUnMarshaller.buildNodeDetailsFromXmlContent(fileData)).thenReturn(nodeSpecificConfigurationsBatch2);
        Mockito.when(enrollmentInfoProvider.getEnrollmentInfo(Mockito.any(), Mockito.anyBoolean())).thenReturn(enrollmentInfo);
        EnrollmentRequestInfo enrollmentRequestInfo = new EnrollmentRequestInfo();
        enrollmentRequestInfo.setNodeName(NODE_FDN);
        EnrollmentRequestInfo enrollmentRequestInfo1 = new EnrollmentRequestInfo();
        enrollmentRequestInfo1.setNodeName(NODE_FDN);
        Mockito.when(nodeDetailsValidator.validate(nodeDetails)).thenReturn(enrollmentRequestInfo);
        Mockito.when(nodeDetailsValidator.validate(nodeDetails1)).thenReturn(enrollmentRequestInfo1);
        Mockito.when(xmlOperatorUtils.convertObjectToXmlStream(enrollmentInfo, EnrollmentInfo.class)).thenReturn(bOutput);
        generateEnrollmentInfoFileHandler.process(command, context);
    }

    @Test
    public void testProcessForMultipleNodes_JAXBException() throws IOException, JAXBException, EnrollmentInfoServiceException {
        ByteArrayOutputStream bOutput = new ByteArrayOutputStream(12);
        bOutput.write("hello".getBytes());

        NodeDetails nodeDetails = new NodeDetails();

        NodeDetailsList nodeSpecificConfigurationsBatch2 = new NodeDetailsList();
        List<NodeDetails> nodeDetailsList2 = new ArrayList<NodeDetails>();
        properties.put("command", NscsCommandType.ENROLLMENT_INFO_FILE);
        command.setProperties(properties);
        nodeDetails.setCertType(CERT_TYPE);
        nodeDetails.setNodeFdn("VPP00001");
        nodeDetails.setSubjectAltName("SAN");
        nodeDetails.setSubjectAltNameType("IPV6");
        nodeDetails.setEnrollmentMode(ENROLLMENT_MODE_TYPE);
        nodeDetails1.setEntityProfileName(ENTITY_PROFILE_NAME);
        nodeDetails1.setKeySize(KEY_SIZE);
        nodeDetails1.setCommonName(COMMON_NAME);
        nodeDetails1.setSubjectAltName("SAN");
        nodeDetails1.setSubjectAltNameType("IPV6");
        nodeDetailsList2.add(nodeDetails);
        nodeDetailsList2.add(nodeDetails1);
        nodeSpecificConfigurationsBatch2.setList(nodeDetailsList2);
        Mockito.when(cliUtil.getCommandInputData(command, FILE_URI)).thenReturn(fileData);
        Mockito.when(xmlValidatorUtility.validateXMLSchema(fileData, XSD_VALIDATOR_FILE_NAME)).thenReturn(true);
        Mockito.when(nodeDetailsUnMarshaller.buildNodeDetailsFromXmlContent(fileData)).thenReturn(nodeSpecificConfigurationsBatch2);
        Mockito.when(enrollmentInfoProvider.getEnrollmentInfo(Mockito.any(), Mockito.anyBoolean())).thenReturn(enrollmentInfo);
        EnrollmentRequestInfo enrollmentRequestInfo = new EnrollmentRequestInfo();
        enrollmentRequestInfo.setNodeName(NODE_FDN);
        EnrollmentRequestInfo enrollmentRequestInfo1 = new EnrollmentRequestInfo();
        enrollmentRequestInfo1.setNodeName(NODE_FDN);
        Mockito.when(nodeDetailsValidator.validate(nodeDetails)).thenReturn(enrollmentRequestInfo);
        Mockito.when(nodeDetailsValidator.validate(nodeDetails1)).thenReturn(enrollmentRequestInfo1);
        Mockito.doThrow(new JAXBException("")).when(xmlOperatorUtils).convertObjectToXmlStream(enrollmentInfo, EnrollmentInfo.class);
        generateEnrollmentInfoFileHandler.process(command, context);
    }

    @Test
    public void testProcessForMultipleNodes_IOException() throws IOException, JAXBException, EnrollmentInfoServiceException {
        ByteArrayOutputStream bOutput = new ByteArrayOutputStream(12);
        bOutput.write("hello".getBytes());

        NodeDetails nodeDetails = new NodeDetails();

        NodeDetailsList nodeSpecificConfigurationsBatch2 = new NodeDetailsList();
        List<NodeDetails> nodeDetailsList2 = new ArrayList<NodeDetails>();
        properties.put("command", NscsCommandType.ENROLLMENT_INFO_FILE);
        command.setProperties(properties);
        nodeDetails.setCertType(CERT_TYPE);
        nodeDetails.setNodeFdn("VPP00001");
        nodeDetails.setSubjectAltName("SAN");
        nodeDetails.setSubjectAltNameType("IPV6");
        nodeDetails.setEnrollmentMode(ENROLLMENT_MODE_TYPE);
        nodeDetails1.setEntityProfileName(ENTITY_PROFILE_NAME);
        nodeDetails1.setKeySize(KEY_SIZE);
        nodeDetails1.setCommonName(COMMON_NAME);
        nodeDetails1.setSubjectAltName("SAN");
        nodeDetails1.setSubjectAltNameType("IPV6");
        nodeDetailsList2.add(nodeDetails);
        nodeDetailsList2.add(nodeDetails1);
        nodeSpecificConfigurationsBatch2.setList(nodeDetailsList2);
        Mockito.when(cliUtil.getCommandInputData(command, FILE_URI)).thenReturn(fileData);
        Mockito.when(xmlValidatorUtility.validateXMLSchema(fileData, XSD_VALIDATOR_FILE_NAME)).thenReturn(true);
        Mockito.when(nodeDetailsUnMarshaller.buildNodeDetailsFromXmlContent(fileData)).thenReturn(nodeSpecificConfigurationsBatch2);
        Mockito.when(enrollmentInfoProvider.getEnrollmentInfo(Mockito.any(), Mockito.anyBoolean())).thenReturn(enrollmentInfo);
        EnrollmentRequestInfo enrollmentRequestInfo = new EnrollmentRequestInfo();
        enrollmentRequestInfo.setNodeName(NODE_FDN);
        EnrollmentRequestInfo enrollmentRequestInfo1 = new EnrollmentRequestInfo();
        enrollmentRequestInfo1.setNodeName(NODE_FDN);
        Mockito.when(nodeDetailsValidator.validate(nodeDetails)).thenReturn(enrollmentRequestInfo);
        Mockito.when(nodeDetailsValidator.validate(nodeDetails1)).thenReturn(enrollmentRequestInfo1);
        Mockito.when(xmlOperatorUtils.convertObjectToXmlStream(enrollmentInfo, EnrollmentInfo.class)).thenReturn(bOutput);
        Mockito.doThrow(new IOException()).when(fileUtil).getArchiveFileBytes(Mockito.any(), Mockito.any());
        generateEnrollmentInfoFileHandler.process(command, context);
    }

    @Test
    public void testProcessForMultipleValidAndInvalidNodes() throws IOException, JAXBException, EnrollmentInfoServiceException {
        ByteArrayOutputStream bOutput = new ByteArrayOutputStream(12);
        bOutput.write("hello".getBytes());

        NodeDetails nodeDetails = new NodeDetails();

        NodeDetailsList nodeSpecificConfigurationsBatch2 = new NodeDetailsList();
        List<NodeDetails> nodeDetailsList2 = new ArrayList<NodeDetails>();
        properties.put("command", NscsCommandType.ENROLLMENT_INFO_FILE);
        command.setProperties(properties);
        nodeDetails.setCertType(CERT_TYPE);
        nodeDetails.setNodeFdn("VPP00001");
        nodeDetails.setSubjectAltName("SAN");
        nodeDetails.setSubjectAltNameType("IPV6");
        nodeDetails.setEnrollmentMode(ENROLLMENT_MODE_TYPE);
        nodeDetails1.setEntityProfileName(ENTITY_PROFILE_NAME);
        nodeDetails1.setKeySize(KEY_SIZE);
        nodeDetails1.setCommonName(COMMON_NAME);
        nodeDetails1.setSubjectAltName("SAN");
        nodeDetails1.setSubjectAltNameType("IPV6");
        nodeDetailsList2.add(nodeDetails);
        nodeDetailsList2.add(nodeDetails1);
        nodeSpecificConfigurationsBatch2.setList(nodeDetailsList2);
        Mockito.when(cliUtil.getCommandInputData(command, FILE_URI)).thenReturn(fileData);
        Mockito.when(xmlValidatorUtility.validateXMLSchema(fileData, XSD_VALIDATOR_FILE_NAME)).thenReturn(true);
        Mockito.when(nodeDetailsUnMarshaller.buildNodeDetailsFromXmlContent(fileData)).thenReturn(nodeSpecificConfigurationsBatch2);
        Mockito.when(enrollmentInfoProvider.getEnrollmentInfo(Mockito.any(), Mockito.anyBoolean())).thenReturn(enrollmentInfo);
        Mockito.when(xmlOperatorUtils.convertObjectToXmlStream(enrollmentInfo, EnrollmentInfo.class)).thenReturn(bOutput);
        EnrollmentRequestInfo enrollmentRequestInfo = new EnrollmentRequestInfo();
        enrollmentRequestInfo.setNodeName(NODE_FDN);
        Mockito.when(nodeDetailsValidator.validate(nodeDetails)).thenReturn(enrollmentRequestInfo);
        Mockito.doThrow(new InvalidNodeNameException()).when(nodeDetailsValidator).validate(nodeDetails1);
        Mockito.when(fileUtil.getArchiveFileBytes(Mockito.any(), Mockito.any())).thenReturn("response".getBytes());
        generateEnrollmentInfoFileHandler.process(command, context);
    }

    @Test
    public void testProcess_NetworkElementNotfoundException() throws IOException {

        Mockito.when(cliUtil.getCommandInputData(command, FILE_URI)).thenReturn(fileData);
        Mockito.when(xmlValidatorUtility.validateXMLSchema(fileData, XSD_VALIDATOR_FILE_NAME)).thenReturn(true);
        Mockito.when(nodeDetailsUnMarshaller.buildNodeDetailsFromXmlContent(fileData)).thenReturn(nodeSpecificConfigurationsBatch);
        Mockito.when(nscsCmReaderService.getNodeModelInformation(Mockito.anyString())).thenThrow(new NetworkElementNotfoundException());
        generateEnrollmentInfoFileHandler.process(command, context);
        Mockito.verify(nodeDetailsUnMarshaller).buildNodeDetailsFromXmlContent(fileData);
    }

    @Test
    public void testProcessForSingleNode() throws IOException, EnrollmentInfoServiceException {
        NodeDetails nodeDetails = new NodeDetails();

        NodeDetailsList nodeSpecificConfigurationsBatch2 = new NodeDetailsList();
        List<NodeDetails> nodeDetailsList1 = new ArrayList<NodeDetails>();
        List<NodeDetails> nodeDetailsList2 = new ArrayList<NodeDetails>();
        List<String> supportedEnrollmentModes = new ArrayList<>();
        supportedEnrollmentModes.add("CMPv2_INITIAL");
        supportedEnrollmentModes.add("CMPv2_VC");
        properties.put("command", NscsCommandType.ENROLLMENT_INFO_FILE);
        command.setProperties(properties);
        nodeDetails.setCertType(CERT_TYPE);
        nodeDetails.setNodeFdn("VPP00001");
        nodeDetails.setSubjectAltName("12.12.12.12");
        nodeDetails.setSubjectAltNameType("IPV4");
        nodeDetails.setEnrollmentMode(ENROLLMENT_MODE_TYPE);
        nodeDetails1.setEntityProfileName(ENTITY_PROFILE_NAME);
        nodeDetails1.setKeySize(KEY_SIZE);
        nodeDetails1.setCommonName(COMMON_NAME);
        nodeDetailsList1.add(nodeDetails);
        nodeDetailsList2.add(nodeDetails);
        nodeSpecificConfigurationsBatch2.setList(nodeDetailsList1);
        nodeSpecificConfigurationsBatch2.setList(nodeDetailsList1);

        enrollmentRequestInfo.setEntityProfile("Sample_EP");
        enrollmentRequestInfo.setKeySize("RSA_2048");
        System.out.println(nodeSpecificConfigurationsBatch2.getList().size());
        fileData = getFileData(XML_FILE);
        Mockito.when(nscsCapabilityModelService.getSupportedEnrollmentModes(nodeModelInformation)).thenReturn(supportedEnrollmentModes);
        Mockito.when(cliUtil.getCommandInputData(command, FILE_URI)).thenReturn(fileData);
        Mockito.when(xmlValidatorUtility.validateXMLSchema(fileData, XSD_VALIDATOR_FILE_NAME)).thenReturn(true);
        Mockito.when(nodeDetailsUnMarshaller.buildNodeDetailsFromXmlContent(fileData)).thenReturn(nodeSpecificConfigurationsBatch2);
        Mockito.when(enrollmentInfoProvider.getEnrollmentInfo(Mockito.any(), Mockito.anyBoolean())).thenReturn(enrollmentInfo);
        Mockito.when(nscsCmReaderService.getNodeModelInformation(Mockito.anyString())).thenReturn(nodeModelInformation);
        assertNotNull(enrollmentInfo.getVerboseEnrollmentInfo().getEnrollmentCmpConfig().getAuthorityName());
        assertNotNull(enrollmentInfo.getVerboseEnrollmentInfo().getEnrollmentCmpConfig().getAuthorityType());
        assertNotNull(enrollmentInfo.getVerboseEnrollmentInfo().getEnrollmentCmpConfig().getEnrollmentAuthority());
        assertNotNull(enrollmentInfo.getVerboseEnrollmentInfo().getEnrollmentCmpConfig().getEnrollmentServerGroupId());
        assertNotNull(enrollmentInfo.getVerboseEnrollmentInfo().getEnrollmentCmpConfig().getEnrollmentServerId());
        assertNotNull(enrollmentInfo.getVerboseEnrollmentInfo().getEnrollmentCmpConfig().getNodeCredentialId());
        assertNotNull(enrollmentInfo.getVerboseEnrollmentInfo().getEnrollmentCmpConfig().getCacerts());
        assertNotNull(enrollmentInfo.getVerboseEnrollmentInfo().getEnrollmentCmpConfig().getTrustedCerts());
        assertNotNull(enrollmentInfo.getVerboseEnrollmentInfo().getCertificateType());
        assertNotNull(enrollmentInfo.getVerboseEnrollmentInfo().getEnrollmentCmpConfig());
        assertNotNull(enrollmentInfo.getVerboseEnrollmentInfo().getTrustCategories());
        assertNotNull(enrollmentInfo.getVerboseEnrollmentInfo().getTrustedCertificates());
        assertNotNull(enrollmentInfo.getVerboseEnrollmentInfo().getTrustedCertificates().getTrustedCertificate().get(0).getCafingerprint());
        assertNotNull(enrollmentInfo.getVerboseEnrollmentInfo().getTrustedCertificates().getTrustedCertificate().get(0).getCaPem());
        assertNotNull(enrollmentInfo.getVerboseEnrollmentInfo().getTrustedCertificates().getTrustedCertificate().get(0).getCaSubjectName());
        assertNotNull(enrollmentInfo.getVerboseEnrollmentInfo().getTrustedCertificates().getTrustedCertificate().get(0).getName());
        assertNotNull(enrollmentInfo.getVerboseEnrollmentInfo().getTrustedCertificates().getTrustedCertificate().get(0).getTdpsUri());
        assertNotNull(enrollmentInfo.getVerboseEnrollmentInfo().getTrustedCertificates().getTrustedCertificate().get(0).getCrls());
        assertNotNull(enrollmentInfo.getVerboseEnrollmentInfo().getTrustCategories().getTrustCategory().get(0).getName());
        System.out.println(enrollmentInfo.getVerboseEnrollmentInfo().getTrustCategories().getTrustCategory().get(0).getCertificates());
        generateEnrollmentInfoFileHandler.process(command, context);
    }

    @Test
    public void testProcessForEmpty() throws IOException, EnrollmentInfoServiceException {
        NodeDetails nodeDetails = new NodeDetails();

        NodeDetailsList nodeSpecificConfigurationsBatch2 = new NodeDetailsList();
        List<NodeDetails> nodeDetailsList1 = new ArrayList<NodeDetails>();

        properties.put("command", NscsCommandType.ENROLLMENT_INFO_FILE);
        command.setProperties(properties);
        nodeDetails.setCertType(CERT_TYPE);
        nodeDetails.setNodeFdn("NSCS");
        nodeDetails.setEnrollmentMode(ENROLLMENT_MODE_TYPE);
        nodeDetailsList1.add(nodeDetails);
        nodeSpecificConfigurationsBatch2.setList(nodeDetailsList1);
        System.out.println(nodeSpecificConfigurationsBatch2.getList().size());
        fileData = getFileData(XML_FILE);

        Mockito.when(cliUtil.getCommandInputData(command, FILE_URI)).thenReturn(fileData);
        Mockito.when(xmlValidatorUtility.validateXMLSchema(fileData, XSD_VALIDATOR_FILE_NAME)).thenReturn(true);
        Mockito.when(nodeDetailsUnMarshaller.buildNodeDetailsFromXmlContent(fileData)).thenReturn(nodeSpecificConfigurationsBatch2);
        Mockito.when(enrollmentInfoProvider.getEnrollmentInfo(Mockito.any(), Mockito.anyBoolean())).thenReturn(enrollmentInfo);
        Mockito.when(nscsCmReaderService.getNodeModelInformation(Mockito.anyString())).thenReturn(nodeModelInformation);
        generateEnrollmentInfoFileHandler.process(command, context);
    }

    @Test
    public void testProcess_InvalidInputXMLFileException() throws IOException {

        Mockito.when(cliUtil.getCommandInputData(command, FILE_URI)).thenReturn(fileData);
        Mockito.when(xmlValidatorUtility.validateXMLSchema(fileData, XSD_VALIDATOR_FILE_NAME)).thenReturn(false);
        final NscsMessageCommandResponse nscsMessageCommandResponse = (NscsMessageCommandResponse) generateEnrollmentInfoFileHandler.process(command, context);
        assertTrue(nscsMessageCommandResponse.getMessage().contains(NscsErrorCodes.INVALID_INPUT_XML_FILE));
    }

    private String getFileData(File xmlFile) throws IOException {
        Reader fileReader = new FileReader(xmlFile);
        BufferedReader bufReader = new BufferedReader(fileReader);

        StringBuilder sb = new StringBuilder();
        String line = bufReader.readLine();
        while (line != null) {
            sb.append(line).append("\n");
            line = bufReader.readLine();
        }
        return sb.toString();
    }
}
