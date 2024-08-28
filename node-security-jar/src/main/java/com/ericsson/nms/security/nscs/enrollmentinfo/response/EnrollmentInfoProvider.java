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
package com.ericsson.nms.security.nscs.enrollmentinfo.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.EnrollmentServerData;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.SecurityDataContainer;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse.TrustedCertificateData;
import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.EnrollmentRequestInfo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.CertificateRevocation;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.CertificateRevocations;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.Certificates;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.EnrollmentCmpConfig;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.EnrollmentInfo;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.TrustCategories;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.TrustCategory;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.TrustedCertificate;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.TrustedCertificates;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.TrustedCertificatesFingerPrints;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.VerboseEnrollmentInfo;
import com.ericsson.nms.security.nscs.enrollmentinfo.service.EnrollmentInfoService;
import com.ericsson.nms.security.nscs.enrollmentinfo.service.EnrollmentInfoServiceException;
import com.ericsson.nms.security.nscs.util.CertDetails;


/**
 * Provides EnrollmentInfo for the Node.
 *
 * @author tcsmave
 *
 */
public class EnrollmentInfoProvider {

    @Inject
    private EnrollmentInfoService enrollmentInfoService;

    @Inject
    private NscsCMReaderService nscsCMReaderService;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Inject
    private Logger logger;

    /**
     * This method gets the Enrollment info for the given node and provides response.
     *
     * @param enrollmentRequestInfo
     *            The details of enrollment info fields like EP, key size and common name.
     * @param verbose
     *            The verbose value
     * @return EnrollmentInfo Object
     * @throws EnrollmentInfoServiceException enrollmentInfoServiceException
     */
    public EnrollmentInfo getEnrollmentInfo(final EnrollmentRequestInfo enrollmentRequestInfo, final boolean verbose) throws EnrollmentInfoServiceException {

        final NodeModelInformation nodeModelInformation = nscsCMReaderService.getNodeModelInformation(enrollmentRequestInfo.getNodeIdentifier().getFdn());

        // This method is only invoked by the generateenrollmentinfo command handler.
        // Currently only OAM is supported, so this hard-coded generation of OAM security data is acceptable.
        final SecurityDataResponse response = enrollmentInfoService.generateSecurityDataOam(nodeModelInformation, enrollmentRequestInfo);
        final SecurityDataContainer securityDataContainer = response.getSecurityDataContainers().get(0);
        final List<TrustedCertificateData> trustedCertificates = response.getTrustedCertificateData();
        if (!verbose) {
            return prepareEnrollmentInfoObject(securityDataContainer, trustedCertificates);
        } else {
            return prepareVerboseEnrollmentInfoObject(securityDataContainer, trustedCertificates,
                    enrollmentRequestInfo.getCertType(), nodeModelInformation);
        }
    }

    /**
     * Prepare the non verbose enrollment info, containing only the general (non verbose) section, according to the info retrieved from PKI.
     * 
     * @param securityDataContainer
     *            the security data retrieved from PKI.
     * @param trustedCertificates
     *            the list of trusted certificates retrieved from PKI.
     * @return the enrollment info.
     */
    private EnrollmentInfo prepareEnrollmentInfoObject(final SecurityDataContainer securityDataContainer, final List<TrustedCertificateData> trustedCertificates) {
        final EnrollmentInfo enrollmentInfo = new EnrollmentInfo();
        enrollmentInfo.setSubjectName(securityDataContainer.getNodeCredentials().getSubjectName());
        enrollmentInfo.setKeyInfo(securityDataContainer.getNodeCredentials().getKeyInfo());
        enrollmentInfo.setEnrollmentCaFingerprint(securityDataContainer.getNodeCredentials().getEnrollmentAuthority().getEnrollmentCaFingerprint());
        enrollmentInfo.setIssuerCA(CertDetails.getBcX500Name(securityDataContainer.getNodeCredentials().getEnrollmentAuthority().getEnrollmentAuthorityName()));
        enrollmentInfo.setChallengePassword(securityDataContainer.getNodeCredentials().getChallengePassword());

        final List<EnrollmentServerData> listOfEnrollmentServerData = securityDataContainer.getNodeCredentials().getEnrollmentServerGroup().getEnrollmentServers();
        for (final EnrollmentServerData enrollmentServerData : listOfEnrollmentServerData) {
            enrollmentInfo.setUrl(enrollmentServerData.getUri());
        }
        if (trustedCertificates != null && !trustedCertificates.isEmpty()) {
            final TrustedCertificatesFingerPrints trustedCertificatesFingerPrints = new TrustedCertificatesFingerPrints();
            final List<String> caFingerPrints = new ArrayList<>();

            for (final TrustedCertificateData trustedCertificateData : trustedCertificates) {
                caFingerPrints.add(trustedCertificateData.getTrustedCertificateFingerPrint());
            }
            trustedCertificatesFingerPrints.setCertificateFingerPrint(caFingerPrints);
            enrollmentInfo.setTrustedCertificateFingerPrints(trustedCertificatesFingerPrints);
        }
        return enrollmentInfo;
    }

    /**
     * Prepare the verbose enrollment info, containing both the general section and the verbose section, according to the info retrieved from PKI.
     * 
     * @param securityDataContainer
     *            the security data retrieved from PKI.
     * @param trustedCertificates
     *            the list of trusted certificates retrieved from PKI.
     * @param certificateType
     *            the certificate type (domain)
     * @param nodeModelInformation
     *            the node model information.
     * @return the enrollment info.
     */
    private EnrollmentInfo prepareVerboseEnrollmentInfoObject(final SecurityDataContainer securityDataContainer,
                                                              final List<TrustedCertificateData> trustedCertificates,
                                                              final String certificateType,
                                                              final NodeModelInformation nodeModelInformation) {

        final EnrollmentInfo enrollmentInfo = prepareEnrollmentInfoObject(securityDataContainer, trustedCertificates);

        VerboseEnrollmentInfo verboseEnrollmentInfo = new VerboseEnrollmentInfo();
        verboseEnrollmentInfo.setCertificateType(certificateType);
        String cbpOiDefaultTrustCategoryId = null;
        Map<String, String> cbpOiDefaultTrustCategoryValue = nscsCapabilityModelService.getDefaultEnrollmentCaTrustCategoryId(nodeModelInformation);
        if (cbpOiDefaultTrustCategoryValue != null && !cbpOiDefaultTrustCategoryValue.isEmpty()) {
            cbpOiDefaultTrustCategoryId = cbpOiDefaultTrustCategoryValue.get(certificateType);
        }
        Map<String, String> comEcimDefaultTrustCategoryIds = nscsCapabilityModelService.getComEcimDefaultTrustCategoryIds(nodeModelInformation);
        final String oamTrustCategoryId = comEcimDefaultTrustCategoryIds.get(certificateType);
        final Map<String, String> caNameMap = getCaNameMap(trustedCertificates);
        TrustCategories trustCategories = new TrustCategories();
        TrustCategory oamTrustCategory = getOamTrustCategory(trustedCertificates, oamTrustCategoryId, caNameMap);
        List<TrustCategory> trustCategoryList = new ArrayList<>();
        trustCategoryList.add(oamTrustCategory);
        if (cbpOiDefaultTrustCategoryId != null && !cbpOiDefaultTrustCategoryId.isEmpty()) {
            final String enrollmentCaCertificateFingerPrint = securityDataContainer.getNodeCredentials().getEnrollmentAuthority().getEnrollmentCaFingerprint();
            final String enrollmentCaCertificateFdn = securityDataContainer.getNodeCredentials().getEnrollmentAuthority().getEnrollmentCaCertificate();
            TrustCategory oamCmpCaTrustcategory = getOamCmpCaTrustCategory(trustedCertificates, cbpOiDefaultTrustCategoryId,
                    enrollmentCaCertificateFingerPrint, enrollmentCaCertificateFdn, caNameMap);
            trustCategoryList.add(oamCmpCaTrustcategory);
        }
        EnrollmentCmpConfig enrollmentCmpConfig = prepareEnrollmentCmpConfig(securityDataContainer, cbpOiDefaultTrustCategoryId, oamTrustCategoryId);
        verboseEnrollmentInfo.setEnrollmentCmpConfig(enrollmentCmpConfig);

        TrustedCertificates trustedCertificatesXml = getTrustedCertificates(trustedCertificates, caNameMap);
        verboseEnrollmentInfo.setTrustedCertificates(trustedCertificatesXml);
        trustCategories.setTrustCategory(trustCategoryList);
        verboseEnrollmentInfo.setTrustCategories(trustCategories);

        enrollmentInfo.setVerboseEnrollmentInfo(verboseEnrollmentInfo);
        return enrollmentInfo;
    }

    /**
     * Prepare the enrollment trust category section given the trust category name, the fingerprint and certificate of enrollment CA, the list of its
     * trusted certificates and the mapping from trusted certificate fingerprint to trusted certificate name.
     * 
     * @param trustedCertificates
     *            the list of trusted certificates retrieved from PKI.
     * @param cbpOiDefaultTrustCategoryId
     *            the trust category name.
     * @param enrollmentCaCertificateFingerPrint
     *            the enrollment CA certificate fingerprint.
     * @param enrollmentCaCertificateFdn
     *            the enrollment CA certificate FDN.
     * @param caNameMap
     *            mapping from trusted certificate fingerprint to trusted certificate name.
     * @return the enrollment trust category section.
     */
    private TrustCategory getOamCmpCaTrustCategory(final List<TrustedCertificateData> trustedCertificates, final String cbpOiDefaultTrustCategoryId,
            final String enrollmentCaCertificateFingerPrint, final String enrollmentCaCertificateFdn, final Map<String, String> caNameMap) {
        TrustCategory oamTrustcategory = new TrustCategory();
        oamTrustcategory.setName(cbpOiDefaultTrustCategoryId);
        Certificates oamCertificate = new Certificates();
        List<String> oamCertificateName = new ArrayList<>();
        if (trustedCertificates != null && !trustedCertificates.isEmpty()) {
            if (enrollmentCaCertificateFingerPrint != null && !enrollmentCaCertificateFingerPrint.isEmpty()) {
                oamCertificateName.add(caNameMap.get(enrollmentCaCertificateFingerPrint));
            } else if (enrollmentCaCertificateFdn != null && !enrollmentCaCertificateFdn.isEmpty()) {
                oamCertificateName.add(getEnrollmentCaNameByCaCertificate(trustedCertificates, enrollmentCaCertificateFdn, caNameMap));
            }
            oamCertificate.setCertificate(oamCertificateName);
        }
        oamTrustcategory.setCertificates(oamCertificate);
        return oamTrustcategory;
    }

    /**
     * Get the enrollment CA name by its enrollment CA certificate FDN.
     * 
     * @param trustedCertificates
     *            the list of trusted certificates retrieved from PKI.
     * @param enrollmentCaCertificateFdn
     *            the enrollment CA certificate FDN.
     * @param caNameMap
     *            mapping from trusted certificate fingerprint to trusted certificate name.
     * @return
     */
    private String getEnrollmentCaNameByCaCertificate(final List<TrustedCertificateData> trustedCertificates, final String enrollmentCaCertificateFdn,
            final Map<String, String> caNameMap) {
        String enrollmentCaName = null;
        for (final TrustedCertificateData trustedCertificateData : trustedCertificates) {
            if (trustedCertificateData.getTrustedCertificateFdn().equals(enrollmentCaCertificateFdn)) {
                enrollmentCaName = caNameMap.get(trustedCertificateData.getTrustedCertificateFingerPrint());
                break;
            }
        }
        return enrollmentCaName;
    }

    /**
     * Prepare the trust category section given the trust category name and the list of its trusted certificates.
     * 
     * @param trustedCertificates
     *            the trusted certificates belonging to the trust category.
     * @param oamTrustCategoryId
     *            the trust category name.
     * @param caNameMap
     *            mapping from trusted certificate fingerprint to trusted certificate name.
     * @return the trust category section.
     */
    private TrustCategory getOamTrustCategory(List<TrustedCertificateData> trustedCertificates, final String oamTrustCategoryId,
            final Map<String, String> caNameMap) {
        TrustCategory trustcategory = new TrustCategory();
        trustcategory.setName(oamTrustCategoryId);
        Certificates certificates = new Certificates();
        List<String> certificateNames = new ArrayList<>();
        if (trustedCertificates != null && !trustedCertificates.isEmpty()) {
            for (final TrustedCertificateData trustedCertificateData : trustedCertificates) {
                certificateNames.add(caNameMap.get(trustedCertificateData.getTrustedCertificateFingerPrint()));
            }
            certificates.setCertificate(certificateNames);
        }
        trustcategory.setCertificates(certificates);
        return trustcategory;
    }

    /**
     * Prepare the trusted certificates section given the list of trusted certificates.
     * 
     * @param trustedCertificates
     *            the trusted certificates retrieved from PKI.
     * @param caNameMap
     *            mapping from trusted certificate fingerprint to trusted certificate name.
     * @return the trusted certificates section.
     */
    private TrustedCertificates getTrustedCertificates(final List<TrustedCertificateData> trustedCertificates,
                                                       final Map<String, String> caNameMap) {
        TrustedCertificates trustedCertificatesXml = new TrustedCertificates();
        List<TrustedCertificate> trustedCertificateList = new ArrayList<>();

        if (trustedCertificates != null && !trustedCertificates.isEmpty()) {
            for (final TrustedCertificateData trustedCertificateData : trustedCertificates) {
                TrustedCertificate trustedCertificate = new TrustedCertificate();
                trustedCertificate.setName(caNameMap.get(trustedCertificateData.getTrustedCertificateFingerPrint()));
                trustedCertificate.setCafingerprint(trustedCertificateData.getTrustedCertificateFingerPrint());
                trustedCertificate.setCaSubjectName(trustedCertificateData.getCaSubjectName());
                trustedCertificate.setTdpsUri(trustedCertificateData.getTdpsUri());
                trustedCertificate.setCaPem(trustedCertificateData.getCaPem());

                trustedCertificate.setCrls(getCrlsUri(trustedCertificateData, caNameMap));
                trustedCertificateList.add(trustedCertificate);
            }
        }
        trustedCertificatesXml.setTrustedCertificate(trustedCertificateList);

        return trustedCertificatesXml;
    }

    /**
     * Prepare the CRL section for the given trusted certificate.
     * 
     * @param trustedCertificateData
     *            the trusted certificate retrieved from PKI.
     * @param caNameMap
     *            mapping from trusted certificate fingerprint to trusted certificate name.
     * @return the trusted certificates section.
     */
    private CertificateRevocations getCrlsUri (final TrustedCertificateData trustedCertificateData,
                                                    final Map<String, String> caNameMap) {

        CertificateRevocations certificateRevocations = new CertificateRevocations();
        List<CertificateRevocation> crls = new ArrayList<>();

        logger.info("crlsUri : [{}]", trustedCertificateData.getCrlsUri());
        for (String crlUri : trustedCertificateData.getCrlsUri()) {
            CertificateRevocation certificateRevocation = new CertificateRevocation();
            certificateRevocation.setCrlName(caNameMap.get(trustedCertificateData.getTrustedCertificateFingerPrint()));
            certificateRevocation.setCrlUri(crlUri);
            crls.add(certificateRevocation);
            logger.info("crl uri in xml: name=[{}], uri=[{}]", crls.get(0).getCrlName(), crls.get(0).getCrlUri());
        }
        certificateRevocations.setCertificateRevocations(crls);
        return certificateRevocations;
    }

    private EnrollmentCmpConfig prepareEnrollmentCmpConfig(final SecurityDataContainer securityDataContainer,
            final String cbpOiDefaultTrustCategoryId, final String oamTrustCategoryId) {
        EnrollmentCmpConfig enrollmentCmpConfig = new EnrollmentCmpConfig();
        enrollmentCmpConfig.setNodeCredentialId(securityDataContainer.getNodeCredentials().getNodeCredentialId());
        enrollmentCmpConfig.setEnrollmentServerGroupId(securityDataContainer.getNodeCredentials().getEnrollmentServerGroup()
                .getEnrollmentServerGroupId());
        final List<EnrollmentServerData> listOfEnrollmentServerDatas = securityDataContainer.getNodeCredentials().getEnrollmentServerGroup()
                .getEnrollmentServers();
        for (final EnrollmentServerData enrollmentServerData : listOfEnrollmentServerDatas) {
            enrollmentCmpConfig.setEnrollmentServerId(enrollmentServerData.getEnrollmentServerId());
        }

        enrollmentCmpConfig.setEnrollmentAuthority(securityDataContainer.getNodeCredentials().getEnrollmentAuthority().getEnrollmentAuthorityName());
        enrollmentCmpConfig.setAuthorityType("REGISTRATION_AUTHORITY");
        if (cbpOiDefaultTrustCategoryId != null) {
            enrollmentCmpConfig.setCacerts(cbpOiDefaultTrustCategoryId);
        }
        enrollmentCmpConfig.setTrustedCerts(oamTrustCategoryId);
        return enrollmentCmpConfig;
    }

    /**
     * Get a CA name map (key is CA DN, value is a unique CA name). For each CA, the CA name is the CA common name followed by a "-<index>" suffix.
     * The <index> is set to 1 and incremented until the map already contains a value with same common name and <index>.
     * 
     * @param trustedCertificates
     *            the trusted certificate (CA) list.
     * @return the CA name map.
     */
    private Map<String, String> getCaNameMap(final List<TrustedCertificateData> trustedCertificates) {
        final Map<String, String> caNameMap = new HashMap<>();
        if (trustedCertificates != null) {
            final List<String> usedCaNames = new ArrayList<>();
            for (final TrustedCertificateData trustedCertificate : trustedCertificates) {
                final String caOrderedDn = trustedCertificate.getCaName();
                final String caCN = getCACommonName(caOrderedDn);
                final String caFingerprint = trustedCertificate.getTrustedCertificateFingerPrint();
                int index = 1;
                String caName;
                do {
                    caName = String.format("%s-%s", caCN, index);
                    index++;
                } while (usedCaNames.contains(caName));
                usedCaNames.add(caName);
                caNameMap.put(caFingerprint, caName);
            }
        }
        return caNameMap;
    }

    /**
     * Get CA common name from a given CA distinguished name.
     * 
     * @param caDn
     *            the CA distinguished name.
     * @return the CA common name.
     */
    private String getCACommonName(final String caDn) {
        return new CertSpec().getCNfromDN(caDn);
    }

}
