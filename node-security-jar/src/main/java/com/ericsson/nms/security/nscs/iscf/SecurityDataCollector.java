/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.iscf;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.net.StandardProtocolFamily;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.bouncycastle.asn1.x500.X500Name;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.exception.IscfServiceException;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.EnrollmentAuthorityData;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.EnrollmentServerData;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.EnrollmentServerGroupData;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.NodeCredentialData;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.SecurityDataContainer;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.TrustCategoryData;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.TrustedCertificateData;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiCertificateManager;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.EnrollmentRequestInfo;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.EnrollmentServer.EnrollmentProtocol;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.pki.NscsPkiUtils;
import com.ericsson.nms.security.nscs.util.NscsTrustedEntityInfo;
import com.ericsson.nms.security.nscs.utilities.ComEcimMoNaming;
import com.ericsson.nms.security.nscs.utilities.NscsCbpOiNodeUtility;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.CertificateStatus;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;

public class SecurityDataCollector {

    @Inject
    private Logger log;

    @Inject
    private CppSecurityService cppSecServ;

    @EJB
    private NscsPkiEntitiesManagerIF nscsPkiManager;

    @EJB
    private NscsPkiCertificateManager nscsPkiCertificateManager;

    @Inject
    private NscsCapabilityModelService capabilityModel;

    @Inject
    private ComEcimMoNaming comEcimMoNaming;

    @Inject
    private NscsCbpOiNodeUtility nscsCbpOiNodeUtility;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    public SecurityDataResponse getSecurityDataResponse(final Set<CertificateType> certTypeSet, final String nodeFdn, final String nodeSerialNumber,
            final SubjectAltNameParam subjectAltName, final EnrollmentMode wantedEnrollmentMode, final NodeModelInformation modelInfo) {
        return getSecurityDataResponse(certTypeSet, nodeFdn, nodeSerialNumber, subjectAltName,
             wantedEnrollmentMode, modelInfo, null);
    }

    public SecurityDataResponse getSecurityDataResponse(final Set<CertificateType> certTypeSet,
            final String nodeFdn, final String nodeSerialNumber, final SubjectAltNameParam subjectAltName,
            final EnrollmentMode wantedEnrollmentMode, final NodeModelInformation modelInfo, final StandardProtocolFamily ipVersion) {
        EnrollmentRequestInfo enrollmentRequestInfo = new EnrollmentRequestInfo();
        enrollmentRequestInfo.setCommonName(nodeSerialNumber);
        enrollmentRequestInfo.setSubjectAltNameParam(subjectAltName);
        enrollmentRequestInfo.setEnrollmentMode(wantedEnrollmentMode);
        enrollmentRequestInfo.setNodeName(nodeFdn);
        enrollmentRequestInfo.setIpVersion(ipVersion);

        return getSecurityDataResponse(certTypeSet, modelInfo, enrollmentRequestInfo);
    }

    /**
     * Get security data for the given set of certificate types for the given node model info with the given enrollment request info.
     * 
     * @param certTypeSet
     *            the set of certificate types.
     * @param modelInfo
     *            the node model info.
     * @param enrollmentRequestInfo
     *            the enrollment request info.
     * @return the security data.
     */
    public SecurityDataResponse getSecurityDataResponse(final Set<CertificateType> certTypeSet, final NodeModelInformation modelInfo,
            final EnrollmentRequestInfo enrollmentRequestInfo) {
        if (certTypeSet == null) {
            return null;
        }
        final CertificateType[] certTypes = new CertificateType[certTypeSet.size()];
        if (certTypes.length > 1) {
            certTypes[0] = CertificateType.OAM; // OAM always first
            certTypes[1] = CertificateType.IPSEC;
        } else {
            certTypes[0] = certTypeSet.iterator().next();
        }
        final NodeModelInformation validModelInfo = validateNodeModelInformation(modelInfo);

        // 1) Get EnrollmentInfo for each cert type (OAM/IPSEC)
        final Map<CertificateType, ScepEnrollmentInfo> scepInfoMap = new EnumMap<>(CertificateType.class);
        for (final CertificateType certType : certTypes) {
            final ScepEnrollmentInfo scepInfo = getScepEnrollmentInfo(certType, validModelInfo, enrollmentRequestInfo);
            if (scepInfo != null) {
                scepInfoMap.put(certType, scepInfo);
            }
        }
        // 2) Get global ordered list of trusted certificates
        List<TrustedCertificateData> trustDataGlobal;
	boolean isIpv6;
	if (enrollmentRequestInfo.getIpVersion() != null) {
	    isIpv6 = StandardProtocolFamily.INET6.equals(enrollmentRequestInfo.getIpVersion());
	} else {
	    isIpv6 = nscsNodeUtility.isNodeIpv6(enrollmentRequestInfo.getNodeName());
	}
        try {
            trustDataGlobal = this.getTrustedCertificatesGlobalList(certTypes, enrollmentRequestInfo.getNodeName(), scepInfoMap, isIpv6);
        } catch (final Exception exception) {
            String errmsg = "Error while preparing getTrustedCertificatesGlobalList";
            log.error("Error :[{}] exception :[{}]", errmsg, exception.getMessage());
            throw new IscfServiceException(errmsg);
        }
        final List<SecurityDataContainer> secDataContainerList = new ArrayList<>();
        String entityName;
        for (final CertificateType certType : certTypes) {
            final ScepEnrollmentInfo scepInfo = scepInfoMap.get(certType);
            if (scepInfo == null) {
                continue;
            }

            // 3)  Get NodeCredentialData for each cert type (OAM/IPSEC)
            final String enrollmentCaCertificateDn = getEnrollmentCaCertificateDn(modelInfo, trustDataGlobal, certType, scepInfo);
            final NodeCredentialData nodeCredData = getNodeCredentialData(certType, scepInfo, enrollmentRequestInfo.getEnrollmentMode(),
                    enrollmentCaCertificateDn, validModelInfo,enrollmentRequestInfo.getNodeName());
            try {
            // 4)  Get TrustCategoryData for each cert type
            final TrustCategoryData trustData = getTrustCategoryData(certType, enrollmentRequestInfo.getNodeName(), trustDataGlobal, validModelInfo, scepInfo, isIpv6);

            // 5)  Create Security Data Container for each cert type
            secDataContainerList.add(new SecurityDataContainer(certType, nodeCredData, trustData));

            // 6) Revoke End Entity certificates
            entityName = getEntityNameFromNodeName(certType, enrollmentRequestInfo.getNodeName());

                nscsPkiCertificateManager.revokeEntityCertificates(entityName);
            } catch (final Exception e) {
                log.error("Error in revoking certificates : [{}]", e.getMessage());
            }
        }

        // 7) Create SecurityDataResponse object
        return new SecurityDataResponse(secDataContainerList, trustDataGlobal);
    }

    public boolean isNodeEntityCreated(final CertificateType certType, final String nodeName) throws NscsPkiEntitiesManagerException {
        return !(nscsPkiManager.isEntityNameAvailable(getEntityNameFromNodeName(certType, nodeName), EntityType.ENTITY));
    }

    /**
     * Get enrollment info for the given certificate type for the given node model info with the given enrollment request info.
     * 
     * @param certType
     *            the certificate type.
     * @param modelInfo
     *            the node model info.
     * @param enrollmentRequestInfo
     *            the enrollment request info.
     * @return the enrollment info.
     */
    private ScepEnrollmentInfo getScepEnrollmentInfo(final CertificateType certType, final NodeModelInformation modelInfo,
            final EnrollmentRequestInfo enrollmentRequestInfo) {

        ScepEnrollmentInfo scepInfo = null;
        String errmsg;
        if (CertificateType.OAM.equals(certType)) {
            try {
                scepInfo = cppSecServ.generateOamEnrollmentInfo(modelInfo, enrollmentRequestInfo);
            } catch (final CppSecurityServiceException ex) {
                errmsg = "Error in generate OAM enrollment info";
                log.error("{}: [{}]", errmsg, NscsLogger.stringifyException(ex));
                throw new IscfServiceException(errmsg);
            }
        } else {
            if (CertificateType.IPSEC.equals(certType)) {
                scepInfo = generateIpSecEnrollmentInfo(enrollmentRequestInfo, modelInfo);
            } else {
                errmsg = "Certificate type not supported";
                log.error("{}: [{}]", errmsg, certType);
                throw new IscfServiceException(errmsg);
            }
        }
        return scepInfo;
    }

    private ScepEnrollmentInfo generateIpSecEnrollmentInfo(final EnrollmentRequestInfo enrollmentRequestInfo, final NodeModelInformation modelInfo) {
        String errmsg;
        try {
        SubjectAltNameParam subjectAltNameParam = enrollmentRequestInfo.getSubjectAltNameParam();
        return cppSecServ.generateIpsecEnrollmentInfo(enrollmentRequestInfo.getNodeName(), enrollmentRequestInfo.getCommonName(),
                subjectAltNameParam != null ? enrollmentRequestInfo.getSubjectAltNameParam().getSubjectAltNameData() : null,
                subjectAltNameParam != null ? enrollmentRequestInfo.getSubjectAltNameParam().getSubjectAltNameFormat(): null,
                enrollmentRequestInfo.getEnrollmentMode(), enrollmentRequestInfo.getIpVersion(), modelInfo);
        } catch (final CppSecurityServiceException ex) {
            errmsg = "Error in generate IPSec ennrollment info";
            log.error("{}: [{}]", errmsg, ex.getMessage());
            throw new IscfServiceException(errmsg);
        }
    }

    private String getTrustCertDnFromFingerprint(final List<TrustedCertificateData> trustedCertDataList, final String caFingerprint) {
        String trustCertDn = null;
        if ((trustedCertDataList != null) && (caFingerprint != null) && !trustedCertDataList.isEmpty()) {
            for (final TrustedCertificateData trustedCertData : trustedCertDataList) {
                if ((trustedCertData != null) && caFingerprint.equals(trustedCertData.getTrustedCertificateFingerPrint())) {
                    trustCertDn = trustedCertData.getTrustedCertificateFdn();
                    break;
                }
            }
        }
        return trustCertDn;
    }

    private String getEnrollmentCaCertificateDn(final NodeModelInformation modelInfo, final List<TrustedCertificateData> trustDataList,
            final CertificateType certType, final ScepEnrollmentInfo scepInfo) {
        String enrollmentCaCertificateDn = null;
        final Map<String, String> enrollmentCAAuthorizationModes = capabilityModel.getEnrollmentCAAuthorizationModes(modelInfo);
        if (capabilityModel.isEnrollmentCACertificateSupported(enrollmentCAAuthorizationModes, certType.toString())) {
            enrollmentCaCertificateDn = getTrustCertDnFromFingerprint(trustDataList, CertSpec.bytesToHex(scepInfo.getServerCertFingerPrint()));
        } else {
            if (capabilityModel.isEnrollmentRootCACertificateSupported(enrollmentCAAuthorizationModes, certType.toString())) {
                enrollmentCaCertificateDn = getTrustCertDnFromFingerprint(trustDataList, CertSpec.bytesToHex(scepInfo.getPkiRootCertFingerPrint()));
            }
        }
        return enrollmentCaCertificateDn;
    }

    private NodeCredentialData getNodeCredentialData(final CertificateType certType, final ScepEnrollmentInfo scepInfo,
            final EnrollmentMode wantedEnrollmentMode, final String enrollmentCaCertificateDn, final NodeModelInformation modelInfo, final String nodeName) {
        log.info("getNodeCredentialData: CertificateType[{}]", certType);

        NodeCredentialData nodeCredentialData = null;
        if (scepInfo != null) {
            //Get the challenge password from scepInfo object and set only if the enrollment mode is CMPv2_Initial(IAK based enrollment)
            //agreed with AP a null is sent otherwise.
            String challengePassword = null;
            if (EnrollmentMode.CMPv2_INITIAL.equals(scepInfo.getEnrollmentMode())) {
                challengePassword = scepInfo.getChallengePassword();
            }
            final String enrollmentAuthorityFdn = getX500NameDn(scepInfo.getCertificateAuthorityDn());
            String enrollmentServerDataEnrollmentAuthority = null;
            if (!capabilityModel.isConfiguredSubjectNameUsedForEnrollment(modelInfo)) {
                // Only for picoRBS nodes
                enrollmentServerDataEnrollmentAuthority = enrollmentAuthorityFdn;
            }
            // Get EnrollmentServerData
            final EnrollmentServerData enrollmentServerData = new EnrollmentServerData(getEnrollmentServerId(wantedEnrollmentMode, modelInfo),
                    scepInfo.getServerURL(), ModelDefinition.EnrollmentServer.EnrollmentProtocol.fromEnrollmentMode(wantedEnrollmentMode).name(),
                    enrollmentServerDataEnrollmentAuthority);
            final EnrollmentServerGroupData enrollmentServerGroup = new EnrollmentServerGroupData(getEnrollmentServerGroupId(certType, modelInfo));
            enrollmentServerGroup.addEnrollmentServer(enrollmentServerData);

            // Get EnrollmentAuthorityData
            final Map<String, String> enrollmentCAAuthorizationModes = capabilityModel.getEnrollmentCAAuthorizationModes(modelInfo);

            String formattedRootCaFingerPrint = null;
            if (capabilityModel.isEnrollmentRootCAFingerPrintSupported(enrollmentCAAuthorizationModes, certType.toString())) {
                formattedRootCaFingerPrint = CertSpec.bytesToHex(scepInfo.getPkiRootCertFingerPrint());
            }

            final EnrollmentAuthorityData enrollmentAuthorityData = new EnrollmentAuthorityData(getEnrollmentAuthorityId(certType, modelInfo),
                    formattedRootCaFingerPrint, enrollmentCaCertificateDn,
                    ModelDefinition.EnrollmentAuthority.AuthorityType.REGISTRATION_AUTHORITY.name(), enrollmentAuthorityFdn);
            final String nodeCredentialKeyInfo = nscsNodeUtility.getNodeCredentialKeyInfo(nodeName, scepInfo.getKeySize());
            nodeCredentialData = new NodeCredentialData(getNodeCredentialId(certType, modelInfo), getX500NameDn(scepInfo.getDistinguishedName()),
                    nodeCredentialKeyInfo, enrollmentServerGroup, enrollmentAuthorityData, challengePassword);

        }
        return nodeCredentialData;
    }

    private List<NscsTrustedEntityInfo> getActiveTrustCertificatesForNode(final String entityProfileName, final boolean isIpv6) {
        Set<NscsTrustedEntityInfo> trustedEntitiesInfo = null;

        // Get list of trusted certificates for certType from PKI
        log.info("getTrustedCertificatesGlobalList: get trust certificates from PKI");
        try {
            trustedEntitiesInfo = cppSecServ.getTrustedCAsInfoByEntityProfileName(entityProfileName, isIpv6);
        } catch (final CppSecurityServiceException ex) {
            final String errmsg = "Error in getting NscsTrustedEntityInfo details";
            log.error("{}: [{}]", errmsg, ex.getMessage());
            throw new IscfServiceException(errmsg);
        }
        final List<NscsTrustedEntityInfo> trustCertsActive = new ArrayList<>();
        if ((trustedEntitiesInfo != null) && (!trustedEntitiesInfo.isEmpty())) {
            log.info("getTrustedCertificatesGlobalList: got {} NscsTrustedEntityInfo from PKI", trustedEntitiesInfo.size());
            for (final NscsTrustedEntityInfo trustedEntity : trustedEntitiesInfo) {
                if (CertificateStatus.ACTIVE.equals(trustedEntity.getCertificateStatus())) {
                    trustCertsActive.add(trustedEntity);
                }
            }
        }
        return trustCertsActive;
    }

    private List<TrustedCertificateData> getTrustedCertificatesGlobalList(final CertificateType[] certTypeArray, final String nodeName,
            final Map<CertificateType, ScepEnrollmentInfo> scepInfoMap, final boolean isIpv6){
        DigestAlgorithm trustCertFingerprintAlgorithm = DigestAlgorithm.SHA1;
        log.info("getTrustedCertificatesGlobalList: CertificateTypes[{}] , node [{}]", certTypeArray, nodeName);
        final List<TrustedCertificateData> trustedCertificatesDataList = new ArrayList<>();

        if (certTypeArray == null) {
            return trustedCertificatesDataList;
        }
        final EnumMap<CertificateType, List<NscsTrustedEntityInfo>> nscsTrustedEntityInfoTypeMap = new EnumMap<>(CertificateType.class);
        for (final CertificateType certType : certTypeArray) {
            final ScepEnrollmentInfo scepInfo = scepInfoMap.get(certType);

            if (scepInfo.getFingerPrintAlgorithm() != null) {
                trustCertFingerprintAlgorithm = scepInfo.getFingerPrintAlgorithm();
            }
            final String entityProfileName = scepInfo.getEntity().getEntityProfile().getName();
            final List<NscsTrustedEntityInfo> trustCertsActive = getActiveTrustCertificatesForNode(entityProfileName, isIpv6);
            if ((trustCertsActive != null) && !trustCertsActive.isEmpty()) {
                nscsTrustedEntityInfoTypeMap.put(certType, trustCertsActive);
            }
        }

        Integer index = 0;
        final Set<String> certFingerPrintSet = new HashSet<>();
        TrustedCertificateData trustedCertData;
        for (final CertificateType certType : certTypeArray) {
            final List<NscsTrustedEntityInfo> nscsTrustedEntityInfoList = nscsTrustedEntityInfoTypeMap.get(certType);
            if ((nscsTrustedEntityInfoList != null) && (!nscsTrustedEntityInfoList.isEmpty())) {
                for (final NscsTrustedEntityInfo nscsTrustedEntity : nscsTrustedEntityInfoList) {
                    // Add root CA certificates first
                    if (isRootCaCertificate(nscsTrustedEntity)) {
                        trustedCertData = getTrustedCertificateData(nscsTrustedEntity, index + 1, trustCertFingerprintAlgorithm);
                        if ((trustedCertData != null) && certFingerPrintSet.add(trustedCertData.getTrustedCertificateFingerPrint())) {
                            // Trusted certificate not present in list
                            trustedCertificatesDataList.add(index, trustedCertData);
                            index++;
                            log.info("getTrustedCertificatesGlobalList: adding trust data for index [{}] with FDN [{}]", index,
                                    trustedCertData.getTrustedCertificateFdn());
                        }
                    }
                }
                for (final NscsTrustedEntityInfo nscsTrustedEntity : nscsTrustedEntityInfoList) {
                    // Add non-root trust certificates
                    trustedCertData = getTrustedCertificateData(nscsTrustedEntity, index + 1, trustCertFingerprintAlgorithm);
                    if ((trustedCertData != null) && certFingerPrintSet.add(trustedCertData.getTrustedCertificateFingerPrint())) {
                        // Trusted certificate not present in list
                        trustedCertificatesDataList.add(index, trustedCertData);
                        index++;
                        log.info("getTrustedCertificatesGlobalList: adding trust data for index [{}] with FDN [{}]", index,
                                trustedCertData.getTrustedCertificateFdn());
                    }
                }
            }
        }
        return trustedCertificatesDataList;
    }

    private boolean isRootCaCertificate(final NscsTrustedEntityInfo nscsTrustedEntity) {
        boolean isRoot = false;
        if ((nscsTrustedEntity != null) && (nscsTrustedEntity.getX509Certificate() != null)) {
            isRoot = nscsTrustedEntity.getX509Certificate().getIssuerDN().equals(nscsTrustedEntity.getX509Certificate().getSubjectDN());
        }
        return isRoot;
    }

    private TrustedCertificateData getTrustedCertificateData(final NscsTrustedEntityInfo trustedEntity, final Integer trustedCertFdnIndex,
            final DigestAlgorithm trustFingerprintAlgorithm) {
        TrustedCertificateData trustedCertData = null;
        String base64Cert = null;
        String tdpsUrl = null;
        try {
            base64Cert = nscsCbpOiNodeUtility.convertToBase64String(trustedEntity.getX509Certificate());
        } catch (final Exception exception) {
            final String errMsg = "Error while converting into base 64 format";
            log.error("Exception occurred :[{}]", exception.getMessage());
            throw new IscfServiceException(errMsg);
        }
        if (trustedEntity.getX509Certificate() != null) {
            // Get fingerprint of trusted certificate
            byte[] x509CertBytes = null;
            byte[] fingerPrint = null;
            log.info("getTrustedCertificateData: getEncoded() for x509 certificate");
            try {
                x509CertBytes = trustedEntity.getX509Certificate().getEncoded();
            } catch (final CertificateEncodingException ex) {
                final String errmsg = "Error in trust certificate decoding";
                log.error("{}: [{}]", errmsg, ex.getMessage());
                throw new IscfServiceException(errmsg);
            }
            if ((x509CertBytes != null) && (trustFingerprintAlgorithm != null)) {
                log.debug("getTrustedCertificateData: generate trust fingerprint");
                try {
                    fingerPrint = NscsPkiUtils.generateMessageDigest(trustFingerprintAlgorithm, x509CertBytes);
                } catch (final NoSuchAlgorithmException ex) {
                    final String errmsg = "Error in generate trust certificate fingerprint";
                    log.error("{}: [{}]", errmsg, ex.getMessage());
                    throw new IscfServiceException(errmsg);
                }
            }
            tdpsUrl = trustedEntity.getTdpsUrl();
            if (fingerPrint != null) {
                // Store FDN of trustedCertificate object
                String trustedCertFdn = Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM.trustedCertificate
                        .withNames(null, null, null, null, trustedCertFdnIndex.toString()).fdn();
                log.info("getTrustedCertificateData: trustedCertFdn={}", trustedCertFdn);
                trustedCertData = new TrustedCertificateData(trustedCertFdn, CertSpec.bytesToHex(fingerPrint),
                        getX500NameDn(trustedEntity.getX509Certificate().getSubjectDN().getName()),
                        trustedEntity.getX509Certificate().getSubjectDN().getName(), tdpsUrl, base64Cert,
                        getX500NameDn(trustedEntity.getX509Certificate().getIssuerDN().getName()));
                trustedCertData.setCrlsUri(trustedEntity.getCrlsUri());
            }
        }
        return trustedCertData;
    }

    private int getTrustedCertificateIndexFromFdn(final String fdn) {
        int index = 0;
        if ((fdn != null) && (fdn.length() > 0)) {
            final String[] fdnSplit = fdn.split("=");
            index = Integer.valueOf(fdnSplit[fdnSplit.length - 1]);
        }
        return index;
    }

    private TrustCategoryData getTrustCategoryData(final CertificateType certType, final String nodeName,
            final List<TrustedCertificateData> trustGlobalList, final NodeModelInformation modelInfo, final ScepEnrollmentInfo scepInfo, final boolean isIpv6) {
        log.info("getTrustCategoryData: CertificateType[{}] , node [{}]", certType, nodeName);
        Set<NscsTrustedEntityInfo> trustCerts = null;
        final List<String> trustFdnList = new ArrayList<>();
        final TrustCategoryData emptyTrustData = new TrustCategoryData(getTrustCategoryId(certType, modelInfo), trustFdnList);
        if (trustGlobalList == null) {
            return emptyTrustData;
        }
        // Get list of trusted certificates from PKI
        log.info("getTrustCategoryData: get trust certificates from PKI");
        try {
            trustCerts = cppSecServ.getTrustedCAsInfoByEntityProfileName(scepInfo.getEntity().getEntityProfile().getName(), isIpv6);
        } catch (final CppSecurityServiceException ex) {
            final String errmsg = "Error in get trust certificates";
            log.error("{}: [{}]", errmsg, ex.getMessage());
            throw new IscfServiceException(errmsg);
        }
        if ((trustCerts == null) || (trustCerts.isEmpty())) {
            return emptyTrustData;
        }
        final Set<String> trustFdnSet = new HashSet<>();
        log.info("getTrustCategoryData: got {} trust certificates from PKI", trustCerts.size());
        for (final NscsTrustedEntityInfo trustCert : trustCerts) {
            final TrustedCertificateData tcd = getTrustedCertificateData(trustCert, 1, scepInfo.getFingerPrintAlgorithm());
            String certIssuerName = "";
            if ((trustCert.getIssuer() != null)) {
                certIssuerName = trustCert.getIssuer();
            }
            String subject = "";
            if (trustCert.getX509Certificate().getSubjectDN().toString() != null) {
                subject = trustCert.getX509Certificate().getSubjectDN().toString();
            }
            log.info("getTrustCategoryData: trustCert from PKI: Subject[{}] , IssuerDN[{}] , SN [{}]", subject, certIssuerName,
                    trustCert.getSerialNumber());
            if (tcd != null) {
                for (final TrustedCertificateData tcdGlobal : trustGlobalList) {
                    if (tcdGlobal.getTrustedCertificateFingerPrint().equals(tcd.getTrustedCertificateFingerPrint())) {
                        final String fdnNew = tcdGlobal.getTrustedCertificateFdn();
                        log.info("getTrustCategoryData: fdnNew={}", fdnNew);
                        if (trustFdnSet.add(fdnNew)) {
                            // fdnNew not already present in trustFdnList
                            int listIndex = -1;
                            // Arrange FDN list in increasing order
                            for (final String fdn : trustFdnList) {
                                if (getTrustedCertificateIndexFromFdn(fdn) > getTrustedCertificateIndexFromFdn(fdnNew)) {
                                    listIndex = trustFdnList.indexOf(fdn);
                                    break;
                                }
                            }
                            log.debug("getTrustCategoryData: adding index fdnNew[{}] to trustFdnList", fdnNew);
                            if (listIndex >= 0) {
                                final List<String> fdnSublist = new ArrayList<>(trustFdnList.subList(listIndex, trustFdnList.size()));
                                trustFdnList.removeAll(fdnSublist);
                                trustFdnList.add(fdnNew);
                                trustFdnList.addAll(fdnSublist);
                            } else {
                                trustFdnList.add(fdnNew);
                            }
                        }
                        break;
                    }
                }
            }
        }
        return new TrustCategoryData(getTrustCategoryId(certType, modelInfo), trustFdnList);
    }

    private String getEnrollmentServerId(final EnrollmentMode enrolMode, final NodeModelInformation modelInfo) {
        final Mo mo = Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM.enrollmentServerGroup.enrollmentServer;
        final String param = EnrollmentProtocol.fromEnrollmentMode(enrolMode).name();
        log.debug("get EnrollmentServerId: invoked for mo [{}] param [{}]", mo, param);
        return comEcimMoNaming.getDefaultName(mo.type(), param, modelInfo);
    }

    private String getEnrollmentServerGroupId(final CertificateType certType, final NodeModelInformation modelInfo) {
        final Mo mo = Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM.enrollmentServerGroup;
        final String param = certType.name();
        log.debug("get EnrollmentServerGroupId: invoked for mo [{}] param [{}]", mo, param);
        return comEcimMoNaming.getDefaultName(mo.type(), param, modelInfo);
    }

    private String getEnrollmentAuthorityId(final CertificateType certType, final NodeModelInformation modelInfo) {
        final Mo mo = Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM.enrollmentAuthority;
        final String param = certType.name();
        log.debug("get EnrollmentAuthorityId: invoked for mo [{}] param [{}]", mo, param);
        return comEcimMoNaming.getDefaultName(mo.type(), param, modelInfo);
    }

    private String getNodeCredentialId(final CertificateType certType, final NodeModelInformation modelInfo) {
        final Mo mo = Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM.nodeCredential;
        final String param = certType.name();
        log.debug("get NodeCredentialId: invoked for mo [{}] param [{}]", mo, param);
        return comEcimMoNaming.getDefaultName(mo.type(), param, modelInfo);
    }

    private String getTrustCategoryId(final CertificateType certType, final NodeModelInformation modelInfo) {
        final Mo mo = Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM.trustCategory;
        final String param = certType.name();
        log.debug("get TrustCategoryId: invoked for mo [{}] param [{}]", mo, param);
        return comEcimMoNaming.getDefaultName(mo.type(), param, modelInfo);
    }

    private String getEntityNameFromNodeName(final CertificateType certType, final String nodeName) {
        return NscsPkiUtils.getEntityNameFromFdn(NscsPkiUtils.convertCertificateTypeToNodeCategory(certType), nodeName);
    }

    private NodeModelInformation validateNodeModelInformation(final NodeModelInformation nodeInfo) {
        final String targetType = (nodeInfo != null ? nodeInfo.getNodeType() : null);
        if (targetType == null) {
            final String errorMessage = "NodeModelInformation contains invalid node type";
            log.error(errorMessage);
            throw new IscfServiceException(errorMessage);
        }
        return nodeInfo;
    }

    private String getX500NameDn(String dn) {
        final X500Name x500Name = new X500Name(dn);
        return x500Name.toString();
    }
}
