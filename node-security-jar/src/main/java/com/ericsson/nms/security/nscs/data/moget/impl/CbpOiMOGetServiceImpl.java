/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.data.moget.impl;

import java.math.BigInteger;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionState;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter;
import com.ericsson.nms.security.nscs.data.moget.MOGetService;
import com.ericsson.nms.security.nscs.data.moget.MOGetServiceType;
import com.ericsson.nms.security.nscs.data.moget.param.CertStateInfo;
import com.ericsson.nms.security.nscs.data.moget.param.NtpServer;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.command.utility.NscsNameMultipleValueResponseBuilder;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.util.ExtendedCertDetails;
import com.ericsson.nms.security.nscs.utilities.CbpOiAlgorithm;
import com.ericsson.nms.security.nscs.utilities.CbpOiMoNaming;
import com.ericsson.nms.security.nscs.utilities.NscsCbpOiNodeUtility;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.services.security.nscs.dps.NscsDpsUtils;

@MOGetServiceType(moGetServiceType = "EOI")
public class CbpOiMOGetServiceImpl implements MOGetService {

    @Inject
    private Logger logger;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private NscsCbpOiNodeUtility nscsCbpOiNodeUtility;

    @Inject
    private NscsDpsUtils nscsDpsUtils;

    public static final String NOT_AVAILABLE = NscsNameMultipleValueResponseBuilder.NOT_AVAILABLE;
    public static final String EMPTY_FIELD = NscsNameMultipleValueResponseBuilder.EMPTY_STRING;

    @Override
    public CertStateInfo getCertificateIssueStateInfo(final NodeReference nodeRef, final String certType) {
        logger.debug("get CbpOi CertificateIssueStateInfo for nodeRef [{}] and certType [{}]", nodeRef, certType);
        if (nodeRef == null || certType == null || certType.isEmpty()) {
            logger.error("get CbpOi CertificateIssueStateInfo : wrong params : nodeRef [{}] and certType [{}]", nodeRef, certType);
            return null;
        }

        String nodeName;
        String serialNumber;
        String issuer;
        String subjectName;
        String subjectAltName;

        final CertStateInfo notAvailableCertStateInfo = new CertStateInfo(nodeRef.getFdn());

        final NormalizableNodeReference node = reader.getNormalizableNodeReference(nodeRef);

        final String nodeCredentialName = nscsCbpOiNodeUtility.getNodeCredentialName(node, certType);

        logger.debug("Getting nodeCredential name [{}]", nodeCredentialName);
        final ManagedObject asymmetricKeyMO = nscsDpsUtils.getAsymmetricKeyMO(node, nodeCredentialName);
        if (asymmetricKeyMO == null) {
            final String errorMessage = String.format("asymmetric-key MO with name [%s] not found for nodeRef [%s]", nodeCredentialName, nodeRef);
            logger.error("get CbpOi CertificateIssueStateInfo failed: {}", errorMessage);
            return notAvailableCertStateInfo;
        }
        final ManagedObject asymmetricKeyCertificatesMO = nscsDpsUtils.getChildMo(asymmetricKeyMO, node, ModelDefinition.KEYSTORE_CERTIFICATES_TYPE,
                CbpOiMoNaming.getName(ModelDefinition.KEYSTORE_CERTIFICATES_TYPE));
        if (asymmetricKeyCertificatesMO == null) {
            final String errorMessage = String.format("asymmetric-key certificates MO under asymmetric-key with name [%s] not found for nodeRef [%s]",
                    nodeCredentialName, nodeRef);
            logger.error("get CbpOi CertificateIssueStateInfo failed: {}", errorMessage);
            return notAvailableCertStateInfo;
        }
        final ManagedObject asymmetricKeyCertificateMO = nscsDpsUtils.getChildMo(asymmetricKeyCertificatesMO, node,
                ModelDefinition.KEYSTORE_CERTIFICATE_TYPE, nodeCredentialName);
        if (asymmetricKeyCertificateMO == null) {
            final String errorMessage = String.format("asymmetric-key certificate MO with name [%s] not found for nodeRef [%s]", nodeCredentialName,
                    nodeRef);
            logger.error("get CbpOi CertificateIssueStateInfo failed: {}", errorMessage);
            return notAvailableCertStateInfo;
        }

        String certIssuer = null;
        String certSerialNumber = null;
        String certSubjectName = null;
        String certSubjectAltName = null;

        // Extract NodeCredential certificate
        final String cert = asymmetricKeyCertificateMO.getAttribute(ModelDefinition.KEYSTORE_CERTIFICATE_CERT_ATTR);
        final X509Certificate x509Certificate = nscsCbpOiNodeUtility.getNodeCredentialX509Certificate(cert);
        if (x509Certificate != null) {
            certIssuer = nscsCbpOiNodeUtility.getIssuer(x509Certificate);
            final BigInteger sn = nscsCbpOiNodeUtility.getSerialNumber(x509Certificate);
            certSerialNumber = sn != null ? sn.toString() : null;
            certSubjectName = nscsCbpOiNodeUtility.getSubject(x509Certificate);
            certSubjectAltName = NOT_AVAILABLE;
        } else {
            logger.error("get CbpOi CertificateIssueStateInfo : failed parsing of cert [{}]", cert);
        }

        issuer = certIssuer != null ? certIssuer : NOT_AVAILABLE;
        serialNumber = certSerialNumber != null ? certSerialNumber : EMPTY_FIELD;
        subjectName = certSubjectName != null ? certSubjectName : NOT_AVAILABLE;
        subjectAltName = certSubjectAltName != null ? certSubjectAltName : NOT_AVAILABLE;
        logger.debug("get CbpOi CertificateIssueStateInfo : issuer [{}] serialNumber [{}] subject [{}] subjectAltName [{}]", issuer, serialNumber,
                subjectName, subjectAltName);

        nodeName = node.getName();
        if (node.getNormalizedRef() != null) {
            nodeName = node.getNormalizedRef().getFdn();
        }
        logger.debug("get CbpOi CertificateIssueStateInfo : node name [{}]", nodeName);

        return new CertStateInfo(nodeName, null, null, ExtendedCertDetails.certDetailsFactory(issuer, serialNumber, subjectName, subjectAltName));
    }

    @Override
    public CertStateInfo getTrustCertificateStateInfo(final NodeReference nodeRef, final String trustCategory) {
        logger.debug("get CbpOi TrustCertificateStateInfo for nodeRef [{}] and trustCategory [{}]", nodeRef, trustCategory);
        if (nodeRef == null || trustCategory == null || trustCategory.isEmpty()) {
            logger.error("get CbpOi TrustCertificateStateInfo : wrong params : nodeRef [{}] and trustCategory [{}]", nodeRef, trustCategory);
            return null;
        }

        String nodeName;
        String trustCertInstallState = NOT_AVAILABLE;
        String trustCertInstallErrMsg = NOT_AVAILABLE;

        final CertStateInfo notAvailableTrustCertStateInfo = new CertStateInfo(nodeRef.getFdn());

        final NormalizableNodeReference node = reader.getNormalizableNodeReference(nodeRef);

        final String trustCategoryName = nscsCbpOiNodeUtility.getTrustCategoryName(node, trustCategory);

        logger.debug("Getting trustCategory name [{}]", trustCategoryName);
        final ManagedObject certificatesMO = nscsDpsUtils.getCertificatesMO(node, trustCategoryName);
        if (certificatesMO == null) {
            final String errorMessage = String.format("certificates MO with name [%s] not found for nodeRef [%s]", trustCategoryName, nodeRef);
            logger.error("get CbpOi TrustCertificateStateInfo failed: {}", errorMessage);
            return notAvailableTrustCertStateInfo;
        }
        final List<ManagedObject> certificateMOs = nscsDpsUtils.getChildMos(certificatesMO, node, ModelDefinition.TRUSTSTORE_CERTIFICATE_TYPE);
        if (certificateMOs.isEmpty()) {
            final String errorMessage = String.format("certificate MOs not found under [%s] for nodeRef [%s]", certificatesMO.getFdn(),
                    nodeRef);
            logger.error("get CbpOi TrustCertificateStateInfo failed: {}", errorMessage);
            return notAvailableTrustCertStateInfo;
        }
        final List<CertDetails> trustedCertificates = getTrustedCertificates(certificateMOs);

        nodeName = node.getName();
        if (node.getNormalizedRef() != null) {
            nodeName = node.getNormalizedRef().getFdn();
        }
        logger.debug("get CbpOi TrustCertificateStateInfo : node name [{}]", nodeName);
        return new CertStateInfo(nodeName, trustCertInstallState, trustCertInstallErrMsg, trustedCertificates);
    }

    /**
     * Gets the list of certificate details for the given list of certificates MOs.
     * 
     * @param certificateMOs
     *            the list of certificate MOs.
     * @return the list of certificate details.
     */
    private List<CertDetails> getTrustedCertificates(final List<ManagedObject> certificateMOs) {
        final List<CertDetails> trustedCertificates = new ArrayList<>();
        for (final ManagedObject certificateMO : certificateMOs) {
            String certIssuer = null;
            String certSerialNumber = null;
            String certSubjectName = null;
            String certSubjectAltName = null;
            final String cert = certificateMO.getAttribute(ModelDefinition.TRUSTSTORE_CERTIFICATE_CERT_ATTR);
            try {
                final X509Certificate x509Certificate = nscsCbpOiNodeUtility.convertToX509Cert(cert);
                certIssuer = x509Certificate.getIssuerDN().getName();
                certSerialNumber = x509Certificate.getSerialNumber().toString();
                certSubjectName = x509Certificate.getSubjectDN().getName();
                certSubjectAltName = NOT_AVAILABLE;
            } catch (final CertificateException e) {
                logger.error("get CbpOi TrustedCertificates : error occurred while converting into x509 format", e);
            }
            getTrustedCertificateInfo(certIssuer, certSerialNumber, certSubjectName, certSubjectAltName, trustedCertificates);
        }
        return trustedCertificates;
    }

    private void getTrustedCertificateInfo(String certIssuer, String certSerialNumber, String certSubjectName, String certSubjectAltName,
            final List<CertDetails> trustedCertificates) {
        certIssuer = certIssuer != null ? certIssuer : NOT_AVAILABLE;
        certSerialNumber = certSerialNumber != null ? certSerialNumber : EMPTY_FIELD;
        certSubjectName = certSubjectName != null ? certSubjectName : NOT_AVAILABLE;
        certSubjectAltName = certSubjectAltName != null ? certSubjectAltName : NOT_AVAILABLE;
        logger.debug("get CbpOi TrustedCertificates : found issuer[{}] serialNumber[{}] subject[{}]", certIssuer, certSerialNumber, certSubjectName);

        final CertDetails trustedCertificateInfo = ExtendedCertDetails.certDetailsFactory(certIssuer, certSerialNumber, certSubjectName,
                certSubjectAltName);
        trustedCertificates.add(trustedCertificateInfo);
    }

    @Override
    public String getSecurityLevel(final NormalizableNodeReference nodeRef, final String syncstatus) {
        return null;
    }

    @Override
    public String getIpsecConfig(final NormalizableNodeReference normNode, final String syncstatus) {
        return null;
    }

    @Override
    public MoActionState getMoActionState(final String moFdn, final MoActionWithoutParameter action) {
        return null;
    }

    @Override
    public MoActionState getMoActionState(final String moFdn, final MoActionWithParameter action) {
        return null;
    }

    @Override
    public String getCrlCheckStatus(final NormalizableNodeReference normNode, final String certType) {
        return null;
    }

    @Override
    public boolean validateNodeForCrlCheckMO(final NormalizableNodeReference normNode, final String certType) {
        return false;
    }

    @Override
    public List<NtpServer> listNtpServerDetails(final NormalizableNodeReference normNode) {
        return Collections.emptyList();
    }

    @Override
    public boolean validateNodeForNtp(final NormalizableNodeReference nodeRef) {
        return false;
    }

    @Override
    public String getNodeSupportedFormatOfKeyAlgorithm(NodeReference nodeRef, String keySize) {
        return CbpOiAlgorithm.fromEnrollmentInfoKeySize(keySize).getNodeAlgorithm();
    }

}
