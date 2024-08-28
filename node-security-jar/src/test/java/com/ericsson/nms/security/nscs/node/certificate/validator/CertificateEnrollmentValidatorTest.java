/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.node.certificate.validator;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.data.*;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CmFunction;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;

@RunWith(MockitoJUnitRunner.class)
public class CertificateEnrollmentValidatorTest {

    @InjectMocks
    CertificateEnrollmentValidator testObj;

    @Mock
    NscsLogger logger;

    @Mock
    private NscsCMReaderService readerServiceMock;

    @Mock
    private NodeReference nodeReferenceMock;

    @Mock
    private NormalizableNodeReference normalizedNodeReferenceMock;

    @Mock
    private CmResponse cmResponseMock;

    @Mock
    private CmObject cmObjectMock;

    @Mock
    private Map<String, Object> attributeMapMock;

    @Mock
    private NodeValidatorUtility nodeValidatorUtilityMock;

    @Mock
    private NscsPkiEntitiesManagerIF nscsPkiManagerMock;

    private final String nodeName = "Node123";
    private final String IPSEC = "IPSEC";
    private final String OAM = "OAM";
    private final NodeReference nodeRef = new NodeRef(nodeName);

    @Test(expected = InvalidNodeNameException.class)
    public void testValidateNodeIssue_InvalidNodeNameException() throws UnassociatedNetworkElementException, InvalidNodeNameException,
            NetworkElementNotfoundException, NodeNotCertifiableException, UnsupportedCertificateTypeException, SecurityFunctionMoNotfoundException,
            NodeNotSynchronizedException, InvalidInputNodeListException, InvalidEntityProfileNameXmlException,
            InvalidEntityProfileNameDefaultXmlException, AlgorithmKeySizeNotSupportedXmlException, SubjAltNameSubjAltNameTypeEmptyXmlException,
            SubjAltNameTypeNotSupportedXmlException, InvalidSubjAltNameXmlException, NscsPkiEntitiesManagerException {
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(null);
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(false);
        testObj.validate(nodeRef, IPSEC, false);
    }

    @Test
    public void testValidateNodeIssueOam() throws UnassociatedNetworkElementException, InvalidNodeNameException, NetworkElementNotfoundException,
            NodeNotCertifiableException, UnsupportedCertificateTypeException, SecurityFunctionMoNotfoundException, NodeNotSynchronizedException,
            InvalidInputNodeListException, InvalidEntityProfileNameXmlException, InvalidEntityProfileNameDefaultXmlException,
            AlgorithmKeySizeNotSupportedXmlException, SubjAltNameSubjAltNameTypeEmptyXmlException, SubjAltNameTypeNotSupportedXmlException,
            InvalidSubjAltNameXmlException, NscsPkiEntitiesManagerException {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn())))
                .thenReturn(true);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        synchronizationMock();
        when(nodeValidatorUtilityMock.isNodeExists(nodeRef)).thenReturn(true);
        when(nodeValidatorUtilityMock.isCertificateSupportedForNode(normalizedNodeReferenceMock)).thenReturn(true);
        when(nodeValidatorUtilityMock.isCertificateTypeSupported(Mockito.any(NormalizableNodeReference.class),Mockito.anyString())).thenReturn(true);
        when(nodeValidatorUtilityMock.hasNodeSecurityFunctionMO(Mockito.any(NodeReference.class))).thenReturn(true);
        when(nodeValidatorUtilityMock.isNodeSynchronized(normalizedNodeReferenceMock)).thenReturn(true);
        testObj.validate(nodeRef, OAM, false);
    }

    @Test(expected = UnassociatedNetworkElementException.class)
    public void testValidateNodeIssue_UnassociatedNetworkElementException() throws UnassociatedNetworkElementException, InvalidNodeNameException,
            NetworkElementNotfoundException, NodeNotCertifiableException, UnsupportedCertificateTypeException, SecurityFunctionMoNotfoundException,
            NodeNotSynchronizedException, InvalidInputNodeListException, InvalidEntityProfileNameXmlException,
            InvalidEntityProfileNameDefaultXmlException, AlgorithmKeySizeNotSupportedXmlException, SubjAltNameSubjAltNameTypeEmptyXmlException,
            SubjAltNameTypeNotSupportedXmlException, InvalidSubjAltNameXmlException, NscsPkiEntitiesManagerException {
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(null);
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        testObj.validate(nodeRef, IPSEC, false);
    }

    @Test(expected = NetworkElementNotfoundException.class)
    public void testValidateNodeIssue_NotExistingNode() throws UnassociatedNetworkElementException, InvalidNodeNameException,
            NetworkElementNotfoundException, NodeNotCertifiableException, UnsupportedCertificateTypeException, SecurityFunctionMoNotfoundException,
            NodeNotSynchronizedException, InvalidInputNodeListException, InvalidEntityProfileNameXmlException,
            InvalidEntityProfileNameDefaultXmlException, AlgorithmKeySizeNotSupportedXmlException, SubjAltNameSubjAltNameTypeEmptyXmlException,
            SubjAltNameTypeNotSupportedXmlException, InvalidSubjAltNameXmlException, NscsPkiEntitiesManagerException {
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(false);
        testObj.validate(nodeRef, IPSEC, false);
    }

    @Test(expected = NodeNotCertifiableException.class)
    public void testValidateConfigParamsForNodeIssue_NotCertifiable() throws UnassociatedNetworkElementException, InvalidNodeNameException,
            NetworkElementNotfoundException, NodeNotCertifiableException, UnsupportedCertificateTypeException, SecurityFunctionMoNotfoundException,
            NodeNotSynchronizedException, InvalidInputNodeListException, InvalidEntityProfileNameXmlException,
            InvalidEntityProfileNameDefaultXmlException, AlgorithmKeySizeNotSupportedXmlException, SubjAltNameSubjAltNameTypeEmptyXmlException,
            SubjAltNameTypeNotSupportedXmlException, InvalidSubjAltNameXmlException, NscsPkiEntitiesManagerException {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        synchronizationMock();
        when(nodeValidatorUtilityMock.isNodeExists(nodeRef)).thenReturn(true);
        testObj.validate(nodeRef, IPSEC, false);
    }

    @Test(expected = UnsupportedCertificateTypeException.class)
    public void testValidateNodeIssue_UnsupportedCertType() throws UnassociatedNetworkElementException, InvalidNodeNameException,
            NetworkElementNotfoundException, NodeNotCertifiableException, UnsupportedCertificateTypeException, SecurityFunctionMoNotfoundException,
            NodeNotSynchronizedException, InvalidInputNodeListException, InvalidEntityProfileNameXmlException,
            InvalidEntityProfileNameDefaultXmlException, AlgorithmKeySizeNotSupportedXmlException, SubjAltNameSubjAltNameTypeEmptyXmlException,
            SubjAltNameTypeNotSupportedXmlException, InvalidSubjAltNameXmlException, NscsPkiEntitiesManagerException {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        synchronizationMock();
        when(nodeValidatorUtilityMock.isNodeExists(nodeRef)).thenReturn(true);
        when(nodeValidatorUtilityMock.isCertificateSupportedForNode(normalizedNodeReferenceMock)).thenReturn(true);
        testObj.validate(nodeRef, IPSEC, false);
    }

    @Test(expected = NodeNotSynchronizedException.class)
    public void testValidateDynamicParamsForNodeIssue_NotSynch() throws UnassociatedNetworkElementException, InvalidNodeNameException,
            NetworkElementNotfoundException, NodeNotCertifiableException, UnsupportedCertificateTypeException, SecurityFunctionMoNotfoundException,
            NodeNotSynchronizedException, InvalidInputNodeListException, InvalidEntityProfileNameXmlException,
            InvalidEntityProfileNameDefaultXmlException, AlgorithmKeySizeNotSupportedXmlException, SubjAltNameSubjAltNameTypeEmptyXmlException,
            SubjAltNameTypeNotSupportedXmlException, InvalidSubjAltNameXmlException, NscsPkiEntitiesManagerException {
        when(readerServiceMock.exists(matches(nodeRef.getFdn()))).thenReturn(true);
        when(readerServiceMock.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(normalizedNodeReferenceMock);
        when(normalizedNodeReferenceMock.getNormalizedRef()).thenReturn(nodeRef);
        when(readerServiceMock.getMOAttribute(any(NormalizableNodeReference.class),
                matches(Model.NETWORK_ELEMENT.cmFunction.type()), matches(Model.NETWORK_ELEMENT.cmFunction.namespace()),
                matches(CmFunction.SYNC_STATUS))).thenReturn(cmResponseMock);
        when(cmResponseMock.getCmObjects()).thenReturn(Arrays.asList(cmObjectMock));
        when(cmObjectMock.getAttributes()).thenReturn(attributeMapMock);
        when(attributeMapMock.get(matches(CmFunction.SYNC_STATUS)))
                .thenReturn(ModelDefinition.CmFunction.SyncStatusValue.PENDING.name());
        when(readerServiceMock.exists(matches(Model.NETWORK_ELEMENT.securityFunction.withNames(nodeRef.getName()).fdn())))
                .thenReturn(true);
       // when(normalizedNodeReferenceMock.getNeType()).thenReturn(NeType.ERBS);
        when(nodeValidatorUtilityMock.isNodeExists(nodeRef)).thenReturn(true);
        when(nodeValidatorUtilityMock.isCertificateSupportedForNode(normalizedNodeReferenceMock)).thenReturn(true);
        when(nodeValidatorUtilityMock.isCertificateTypeSupported(Mockito.any(NormalizableNodeReference.class),Mockito.anyString())).thenReturn(true);
        when(nodeValidatorUtilityMock.hasNodeSecurityFunctionMO(Mockito.any(NodeReference.class))).thenReturn(true);
        testObj.validate(nodeRef, OAM, false);
    }

    private void synchronizationMock() {
        when(readerServiceMock.getMOAttribute(any(NormalizableNodeReference.class),
                matches(Model.NETWORK_ELEMENT.cmFunction.type()), matches(Model.NETWORK_ELEMENT.cmFunction.namespace()),
                matches(CmFunction.SYNC_STATUS))).thenReturn(cmResponseMock);
        when(cmResponseMock.getCmObjects()).thenReturn(Arrays.asList(cmObjectMock));
        when(cmObjectMock.getAttributes()).thenReturn(attributeMapMock);
        when(attributeMapMock.get(matches(CmFunction.SYNC_STATUS)))
                .thenReturn(CmFunction.SyncStatusValue.SYNCHRONIZED.name());
    }

}
