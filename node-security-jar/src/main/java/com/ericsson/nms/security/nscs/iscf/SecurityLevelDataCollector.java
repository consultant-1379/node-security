/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */
package com.ericsson.nms.security.nscs.iscf;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.cert.CertificateEncodingException;
import java.util.List;

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo;
import com.ericsson.nms.security.nscs.cpp.model.TrustStoreInfo;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.iscf.dto.*;
import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.smrs.api.exception.SmrsDirectoryException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;
import java.security.NoSuchAlgorithmException;

import java.security.cert.CertificateException;

/**
 * Data collector for Security Level ISCF XML generation
 *
 * @author ealemca
 */
public class SecurityLevelDataCollector extends BaseDataCollector {

    /**
     * Fetch all required Security Level data from external services and
     * encapsulate in a {@link NodeAIData} object.
     *
     * @param wantedSecurityLevel
     * @param minimumSecurityLevel
     * @param fdn
     * @param logicalName
     * @param wantedEnrollmentMode
     * @param modelInfo
     * @param rbsIntegrityCode
     * @return NodeAIData
     * @throws SecurityLevelNotSupportedException
     * @throws
     * com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException
     * @throws java.net.UnknownHostException
     * @throws java.security.cert.CertificateEncodingException
     * @throws com.ericsson.nms.security.nscs.iscf.IscfEncryptionException
     * @throws java.io.UnsupportedEncodingException
     * @throws java.security.NoSuchAlgorithmException
     */
    public NodeAIData getNodeAIData(final SecurityLevel wantedSecurityLevel,
            final SecurityLevel minimumSecurityLevel, final String fdn,
            final String logicalName,
            final EnrollmentMode wantedEnrollmentMode,
            final NodeModelInformation modelInfo,
            final byte[] rbsIntegrityCode)
            throws SecurityLevelNotSupportedException,
            CppSecurityServiceException, SmrsDirectoryException,
            UnknownHostException, IscfEncryptionException,
            CertificateEncodingException, UnsupportedEncodingException, CertificateException, NoSuchAlgorithmException {

        log.info("Collecting auto integration data for node {}", fdn);
        final NodeAIData data = new NodeAIData();
        addBasicData(data, wantedSecurityLevel, minimumSecurityLevel, fdn,
                rbsIntegrityCode);
        addMetaData(data, logicalName);
        validateNodeAIData(data);
        log.debug("Fetching SCEP Enrollment info");
        //final ScepEnrollmentInfo scepInfo = cpp.generateSCEPEnrollmentInfo(fdn, wantedEnrollmentMode);
        final ScepEnrollmentInfo scepInfo
                = cpp.generateOamEnrollmentInfo(fdn, null, null, null, wantedEnrollmentMode, modelInfo);
        log.debug("Fetching trust store info");
        final TrustStoreInfo trustInfo
                = cpp.getTrustStoreForAP(TrustedCertCategory.CORBA_PEERS, scepInfo.getName(), modelInfo);
//                final TrustStoreInfo trustInfo = cpp.getTrustStoreInfo(TrustedCertCategory.CORBA_PEERS);

        addEnrollmentInfo(data, modelInfo.getNodeType(), scepInfo);
        addTrustStoreInfo(data, trustInfo, TrustedCertCategory.CORBA_PEERS);


        log.info("Collection Successful");
        return data;
    }

    /**
     * Validates gathered node auto-integration data
     *
     * @param data
     * @return isValid boolean indicating whether data is valid
     * @throws SecurityLevelNotSupportedException
     */
    @Override
    public boolean validateNodeAIData(final NodeAIData data)
            throws SecurityLevelNotSupportedException, InvalidNodeAIDataException {
        log.info("Validating gathered auto integration data for node {}",
                data.getFdn());
        final boolean isValid = validateCommonNodeAIData(data);
        if ((data.getWantedSecLevel() == null)
                || (data.getMinimumSecLevel() == null)) {
            throw new SecurityLevelNotSupportedException(
                    "Wanted Security Level and Minimum Security Level cannot be null");
        }
        if (data.getMinimumSecLevel().compareTo(SecurityLevel.LEVEL_NOT_SUPPORTED) >= 0) {
            throw new SecurityLevelNotSupportedException(
                    "Minimum Security Level invalid");
        }
        if (data.getWantedSecLevel().compareTo(SecurityLevel.LEVEL_NOT_SUPPORTED) >= 0) {
            throw new SecurityLevelNotSupportedException(
                    "Wanted Security Level invalid");
        }
        if (data.getWantedSecLevel().compareTo(SecurityLevel.LEVEL_3) == 0) {
            throw new SecurityLevelNotSupportedException(
                    "Security Level 3 not supported");
        }
        if (data.getWantedSecLevel().compareTo(data.getMinimumSecLevel()) < 0) {
            throw new SecurityLevelNotSupportedException(
                    "Minimum Security Level cannot be greater than Wanted Security Level");
        }

        log.info("Node auto integration data is valid");
        return isValid;
    }

    private void addBasicData(final NodeAIData data,
            final SecurityLevel wantedSecurityLevel,
            final SecurityLevel minimumSecurityLevel, final String fdn,
            final byte[] rbsIntegrityCode) {
        data.setWantedSecLevel(wantedSecurityLevel);
        data.setMinimumSecLevel(minimumSecurityLevel);
        data.setFdn(fdn);
        data.setRic(rbsIntegrityCode);
        data.setLogonServerAddress(config.getIscfLogonServerAddress());
    }

    private void addEnrollmentInfo(final NodeAIData data, final String nodeType,
            final ScepEnrollmentInfo scepInfo) throws IscfEncryptionException,
            UnsupportedEncodingException {

        final SecEnrollmentDataDto secDto = new SecEnrollmentDataDto();

        final EnrollmentDataDto enrollDto = new EnrollmentDataDto();
        enrollDto.setCAFingerprint(parseCaFingerPrint(scepInfo));
        enrollDto.setDataChallengePassword(getDataChallengePassword(data,
                scepInfo));
        enrollDto.setDistinguishedName(scepInfo.getDistinguishedName());
        String enrollmentServerURL = scepInfo.getServerURL();
        enrollmentServerURL = updateEnrollmentServerUrl(enrollmentServerURL, nodeType);
        enrollDto.setEnrollmentServerURL(enrollmentServerURL);
        //enrollDto.setKeyLength(new Integer(config.getEnrollmentKeyLength()));
        Integer keySize = Integer.parseInt(scepInfo.getKeySize());
        enrollDto.setKeyLength(keySize);
        Integer enrollmentProtocol = Integer.parseInt(scepInfo.getEnrollmentProtocol());
        enrollDto.setEnrollmentMode(enrollmentProtocol);
        enrollDto.setCertificateAuthorityDn(scepInfo.getCertificateAuthorityDn());
        enrollDto.setEnrollmentTimeLimit(IscfConstants.DEFAULT_ENROLLMENT_TIME_LIMIT);

        secDto.setRollbackTimeout(scepInfo.getRollbackTimeout());
        secDto.setEnrollmentData(enrollDto);

        data.setSecEnrollmentData(secDto);
    }

    private void addTrustStoreInfo(final NodeAIData data,
            final TrustStoreInfo trustInfo,
            final TrustedCertCategory trustedCertCategory)
            throws IscfEncryptionException, CertificateEncodingException, NoSuchAlgorithmException {
        log.debug("Fetching trusted certificates");
        final List<CertFileDto> certFileDtoList = data
                .getSecLevelCertFileDtos();
        addCertDtos(data, trustInfo, trustedCertCategory, certFileDtoList);
        for (CertSpec spec : trustInfo.getCertSpecs()) {
            data.getSecLevelCertSpecs().add(spec);
        }
    }

}
