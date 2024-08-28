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
package com.ericsson.nms.security.nscs.utilities;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

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

import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException;
import com.ericsson.nms.security.nscs.api.exception.InvalidSubjAltNameXmlException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotCertifiableException;
import com.ericsson.nms.security.nscs.api.exception.SubjAltNameTypeNotSupportedXmlException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedCertificateTypeException;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.enrollmentinfo.request.NodeDetailsValidator;
import com.ericsson.nms.security.nscs.enrollmentinfo.request.model.NodeDetails;

/**
 * Test Class for NodeDetailsValidator.
 *
 * @author tcsviku
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class NodeDetailsValidatorTest {

    @Spy
    private Logger logger = LoggerFactory.getLogger(NodeValidatorUtility.class);

    @InjectMocks
    private NodeDetailsValidator nodeDetailsValidator;

    @Mock
    private NscsCMReaderService mockReaderService;

    @Mock
    private NormalizableNodeReference mockNormalizedNodeReference;

    @Mock
    private NodeValidatorUtility nodeValidatorUtility;

    @Mock
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Mock
    private NodeModelInformation nodeModelInformation;

    private NodeDetails nodeDetails = new NodeDetails();
    private final String nodeName = "Node123";
    private final NodeReference nodeRef = new NodeRef(nodeName);
    private static final String ENROLLMENT_MODE_TYPE = "CMPv2_VC";
    private static final String NODE_FDN = "VPP00001";
    private static final String CERT_TYPE_OAM = "OAM";
    private List<String> certtypes = new ArrayList<String>();
    private List<String> supportedEnrollmentModes = new ArrayList<>();

    /**
     * Method to setup initial test data.
     *
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        supportedEnrollmentModes.add(ENROLLMENT_MODE_TYPE);
        nodeDetails.setCertType(CERT_TYPE_OAM);
        nodeDetails.setEnrollmentMode(ENROLLMENT_MODE_TYPE);
        nodeDetails.setNodeFdn(NODE_FDN);
        certtypes.add(CERT_TYPE_OAM);

    }

    @Test
    public void testValidate() {
        Mockito.when(mockReaderService.getNormalizedNodeReference(Mockito.any(NodeReference.class))).thenReturn(mockNormalizedNodeReference);
        Mockito.when(mockReaderService.getNodeModelInformation(Mockito.anyString())).thenReturn(nodeModelInformation);
        Mockito.when(nodeModelInformation.getNodeType()).thenReturn("vRC");
        Mockito.when(nodeValidatorUtility.isNodeExists(nodeRef)).thenReturn(true);
        Mockito.when(nodeValidatorUtility.isCertificateSupportedForNode(mockNormalizedNodeReference)).thenReturn(true);
        Mockito.when(nodeValidatorUtility.isCertificateTypeSupported(mockNormalizedNodeReference, CERT_TYPE_OAM)).thenReturn(true);
        Mockito.when(nscsCapabilityModelService.getSupportedEnrollmentModes(mockNormalizedNodeReference)).thenReturn(supportedEnrollmentModes);
        assertNotNull(nodeDetailsValidator.validate(nodeDetails));
    }

    @Test
    public void testValidateWithValidSubjectAlternativeName() {
        Mockito.when(mockReaderService.getNormalizedNodeReference(Mockito.any(NodeReference.class))).thenReturn(mockNormalizedNodeReference);
        Mockito.when(mockReaderService.getNodeModelInformation(Mockito.anyString())).thenReturn(nodeModelInformation);
        Mockito.when(nodeModelInformation.getNodeType()).thenReturn("vRC");
        Mockito.when(nodeValidatorUtility.isNodeExists(nodeRef)).thenReturn(true);
        Mockito.when(nodeValidatorUtility.isCertificateSupportedForNode(mockNormalizedNodeReference)).thenReturn(true);
        Mockito.when(nodeValidatorUtility.isCertificateTypeSupported(mockNormalizedNodeReference, CERT_TYPE_OAM)).thenReturn(true);
        Mockito.when(nscsCapabilityModelService.getSupportedEnrollmentModes(mockNormalizedNodeReference)).thenReturn(supportedEnrollmentModes);
        nodeDetails.setSubjectAltName("12.12.12.12");
        nodeDetails.setSubjectAltNameType("IPV4");
        assertNotNull(nodeDetailsValidator.validate(nodeDetails));
    }

    @Test
    public void testValidateWithValidSubjectAlternativeNameWildcard() {
        Mockito.when(mockReaderService.getNormalizedNodeReference(Mockito.any(NodeReference.class))).thenReturn(mockNormalizedNodeReference);
        Mockito.when(mockReaderService.getNodeModelInformation(Mockito.anyString())).thenReturn(nodeModelInformation);
        Mockito.when(nodeModelInformation.getNodeType()).thenReturn("vRC");
        Mockito.when(nodeValidatorUtility.isNodeExists(nodeRef)).thenReturn(true);
        Mockito.when(nodeValidatorUtility.isCertificateSupportedForNode(mockNormalizedNodeReference)).thenReturn(true);
        Mockito.when(nodeValidatorUtility.isCertificateTypeSupported(mockNormalizedNodeReference, CERT_TYPE_OAM)).thenReturn(true);
        Mockito.when(nscsCapabilityModelService.getSupportedEnrollmentModes(mockNormalizedNodeReference)).thenReturn(supportedEnrollmentModes);
        nodeDetails.setSubjectAltName("?");
        nodeDetails.setSubjectAltNameType("IPV4");
        assertNotNull(nodeDetailsValidator.validate(nodeDetails));
    }

    @Test
    public void testValidateWithValidIPv6SubjectAlternativeName() {
        Mockito.when(mockReaderService.getNormalizedNodeReference(Mockito.any(NodeReference.class))).thenReturn(mockNormalizedNodeReference);
        Mockito.when(mockReaderService.getNodeModelInformation(Mockito.anyString())).thenReturn(nodeModelInformation);
        Mockito.when(nodeModelInformation.getNodeType()).thenReturn("vRC");
        Mockito.when(nodeValidatorUtility.isNodeExists(nodeRef)).thenReturn(true);
        Mockito.when(nodeValidatorUtility.isCertificateSupportedForNode(mockNormalizedNodeReference)).thenReturn(true);
        Mockito.when(nodeValidatorUtility.isCertificateTypeSupported(mockNormalizedNodeReference, CERT_TYPE_OAM)).thenReturn(true);
        Mockito.when(nscsCapabilityModelService.getSupportedEnrollmentModes(mockNormalizedNodeReference)).thenReturn(supportedEnrollmentModes);
        nodeDetails.setSubjectAltName("2001:cdba:0000:0000:0000:0000:3257:9652");
        nodeDetails.setSubjectAltNameType("IPV6");
        assertNotNull(nodeDetailsValidator.validate(nodeDetails));
    }

    @Test
    public void testValidateWithValidIPv6SubjectAlternativeNameWildcard() {
        Mockito.when(mockReaderService.getNormalizedNodeReference(Mockito.any(NodeReference.class))).thenReturn(mockNormalizedNodeReference);
        Mockito.when(mockReaderService.getNodeModelInformation(Mockito.anyString())).thenReturn(nodeModelInformation);
        Mockito.when(nodeModelInformation.getNodeType()).thenReturn("vRC");
        Mockito.when(nodeValidatorUtility.isNodeExists(nodeRef)).thenReturn(true);
        Mockito.when(nodeValidatorUtility.isCertificateSupportedForNode(mockNormalizedNodeReference)).thenReturn(true);
        Mockito.when(nodeValidatorUtility.isCertificateTypeSupported(mockNormalizedNodeReference, CERT_TYPE_OAM)).thenReturn(true);
        Mockito.when(nscsCapabilityModelService.getSupportedEnrollmentModes(mockNormalizedNodeReference)).thenReturn(supportedEnrollmentModes);
        nodeDetails.setSubjectAltName("?");
        nodeDetails.setSubjectAltNameType("IPV6");
        assertNotNull(nodeDetailsValidator.validate(nodeDetails));
    }

    @Test(expected = SubjAltNameTypeNotSupportedXmlException.class)
    public void testValidateWithInvalidSubjectAlternativeNameType() {
        Mockito.when(mockReaderService.getNormalizedNodeReference(Mockito.any(NodeReference.class))).thenReturn(mockNormalizedNodeReference);
        Mockito.when(mockReaderService.getNodeModelInformation(Mockito.anyString())).thenReturn(nodeModelInformation);
        Mockito.when(nodeModelInformation.getNodeType()).thenReturn("vRC");
        Mockito.when(nodeValidatorUtility.isNodeExists(nodeRef)).thenReturn(true);
        Mockito.when(nodeValidatorUtility.isCertificateSupportedForNode(mockNormalizedNodeReference)).thenReturn(true);
        Mockito.when(nodeValidatorUtility.isCertificateTypeSupported(mockNormalizedNodeReference, CERT_TYPE_OAM)).thenReturn(true);
        Mockito.when(nscsCapabilityModelService.getSupportedEnrollmentModes(mockNormalizedNodeReference)).thenReturn(supportedEnrollmentModes);
        nodeDetails.setSubjectAltName("12.12.12.12");
        nodeDetails.setSubjectAltNameType("IP");
        nodeDetailsValidator.validate(nodeDetails);
    }

    @Test(expected = InvalidSubjAltNameXmlException.class)
    public void testValidateWithInvalidSubjectAlternativeName() {
        Mockito.when(mockReaderService.getNormalizedNodeReference(Mockito.any(NodeReference.class))).thenReturn(mockNormalizedNodeReference);
        Mockito.when(mockReaderService.getNodeModelInformation(Mockito.anyString())).thenReturn(nodeModelInformation);
        Mockito.when(nodeModelInformation.getNodeType()).thenReturn("vRC");
        Mockito.when(nodeValidatorUtility.isNodeExists(nodeRef)).thenReturn(true);
        Mockito.when(nodeValidatorUtility.isCertificateSupportedForNode(mockNormalizedNodeReference)).thenReturn(true);
        Mockito.when(nodeValidatorUtility.isCertificateTypeSupported(mockNormalizedNodeReference, CERT_TYPE_OAM)).thenReturn(true);
        Mockito.when(nscsCapabilityModelService.getSupportedEnrollmentModes(mockNormalizedNodeReference)).thenReturn(supportedEnrollmentModes);
        nodeDetails.setSubjectAltName("fqdn");
        nodeDetails.setSubjectAltNameType("IPV4");
        nodeDetailsValidator.validate(nodeDetails);
    }

    @Test(expected = InvalidSubjAltNameXmlException.class)
    public void testValidateWithInvalidIPv6SubjectAlternativeName() {
        Mockito.when(mockReaderService.getNormalizedNodeReference(Mockito.any(NodeReference.class))).thenReturn(mockNormalizedNodeReference);
        Mockito.when(mockReaderService.getNodeModelInformation(Mockito.anyString())).thenReturn(nodeModelInformation);
        Mockito.when(nodeModelInformation.getNodeType()).thenReturn("vRC");
        Mockito.when(nodeValidatorUtility.isNodeExists(nodeRef)).thenReturn(true);
        Mockito.when(nodeValidatorUtility.isCertificateSupportedForNode(mockNormalizedNodeReference)).thenReturn(true);
        Mockito.when(nodeValidatorUtility.isCertificateTypeSupported(mockNormalizedNodeReference, CERT_TYPE_OAM)).thenReturn(true);
        Mockito.when(nscsCapabilityModelService.getSupportedEnrollmentModes(mockNormalizedNodeReference)).thenReturn(supportedEnrollmentModes);
        nodeDetails.setSubjectAltName("fqdn");
        nodeDetails.setSubjectAltNameType("IPV6");
        nodeDetailsValidator.validate(nodeDetails);
    }

    @Test(expected = InvalidNodeNameException.class)
    public void testValidate_NetworkElementNotfoundException() {
        Mockito.when(mockReaderService.getNormalizedNodeReference(Mockito.any(NodeReference.class))).thenReturn(null);
        Mockito.when(mockReaderService.getNodeModelInformation(Mockito.anyString())).thenReturn(nodeModelInformation);
        Mockito.when(nodeModelInformation.getNodeType()).thenReturn(CERT_TYPE_OAM);
        nodeDetailsValidator.validate(nodeDetails);
    }

    @Test(expected = NodeNotCertifiableException.class)
    public void testValidate_NodeNotCertifiableException() {
        Mockito.when(mockReaderService.getNormalizedNodeReference(Mockito.any(NodeReference.class))).thenReturn(mockNormalizedNodeReference);
        Mockito.when(mockReaderService.getNodeModelInformation(Mockito.anyString())).thenReturn(nodeModelInformation);
        Mockito.when(nodeModelInformation.getNodeType()).thenReturn(CERT_TYPE_OAM);
        Mockito.when(mockNormalizedNodeReference.getNormalizedRef()).thenReturn(nodeRef);
        Mockito.when(nodeValidatorUtility.isNodeExists(nodeRef)).thenReturn(true);
        Mockito.when(nodeValidatorUtility.isCertificateSupportedForNode(mockNormalizedNodeReference)).thenReturn(false);
        nodeDetailsValidator.validate(nodeDetails);
    }

    @Test(expected = UnsupportedCertificateTypeException.class)
    public void testValidate_UnsupportedCertificateTypeException() {
        Mockito.when(mockReaderService.getNormalizedNodeReference(Mockito.any(NodeReference.class))).thenReturn(mockNormalizedNodeReference);
        Mockito.when(mockReaderService.getNodeModelInformation(Mockito.anyString())).thenReturn(nodeModelInformation);
        Mockito.when(nodeModelInformation.getNodeType()).thenReturn(CERT_TYPE_OAM);
        Mockito.when(mockNormalizedNodeReference.getNormalizedRef()).thenReturn(nodeRef);
        Mockito.when(nodeValidatorUtility.isNodeExists(nodeRef)).thenReturn(true);
        Mockito.when(nodeValidatorUtility.isCertificateSupportedForNode(mockNormalizedNodeReference)).thenReturn(true);
        Mockito.when(nodeValidatorUtility.isCertificateTypeSupported(mockNormalizedNodeReference, CERT_TYPE_OAM)).thenReturn(false);
        nodeDetailsValidator.validate(nodeDetails);
    }
}