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

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType;
import com.ericsson.nms.security.nscs.api.iscf.IpsecArea;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo;
import com.ericsson.nms.security.nscs.cpp.model.TrustStoreInfo;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.iscf.dto.CertFileDto;
import com.ericsson.nms.security.nscs.iscf.dto.EnrollmentDataDto;
import com.ericsson.nms.security.nscs.iscf.dto.IpsecEnrollmentDataDto;
import com.ericsson.nms.security.nscs.iscf.dto.SecEnrollmentDataDto;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.smrs.api.exception.SmrsDirectoryException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Set;

/**
 * Data collector for IPSec and Security Level ISCF XML generation
 *
 * @author ealemca
 */
public class CombinedDataCollector extends BaseDataCollector {

    /**
     * Fetch all required data from external services and encapsulate in a
     * {@link NodeAIData} object containing all info needed for the Security
     * Level wanted and the IpsecArea wanted.
     *
     * @param fdn
     * @param logicalName
     * @param wantedSecurityLevel
     * @param minimumSecurityLevel
     * @param userLabel
     * @param subjectAltName
     * @param subjectAltNameFormat
     * @param wantedIpsecAreas
     * @param wantedEnrollmentMode
     * @param modelInfo
     * @param rbsIntegrityCode
     * @return NodeAIData
     * @throws SecurityLevelNotSupportedException
     * @throws com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException
     * @throws java.net.UnknownHostException
     * @throws java.security.cert.CertificateEncodingException
     * @throws com.ericsson.nms.security.nscs.iscf.IscfEncryptionException
     * @throws java.io.UnsupportedEncodingException
     * @throws java.security.NoSuchAlgorithmException
     */
    public NodeAIData getNodeAIData(
            final String fdn,
            final String logicalName,
            final SecurityLevel wantedSecurityLevel,
            final SecurityLevel minimumSecurityLevel,
            final String userLabel,
            final BaseSubjectAltNameDataType subjectAltName,
            final SubjectAltNameFormat subjectAltNameFormat,
            final Set<IpsecArea> wantedIpsecAreas,
			final EnrollmentMode wantedEnrollmentMode,
			final NodeModelInformation modelInfo,
            final byte[] rbsIntegrityCode
    )  throws SecurityLevelNotSupportedException,
            CppSecurityServiceException,
            SmrsDirectoryException,
            UnknownHostException,
            IscfEncryptionException,
            CertificateEncodingException,
            UnsupportedEncodingException,
            CertificateException,
            NoSuchAlgorithmException {
        log.info("Gathering auto integration data for node {}", fdn);
        final NodeAIData data = new NodeAIData();
        addBasicData(data, wantedSecurityLevel, minimumSecurityLevel, fdn, rbsIntegrityCode,
                userLabel, wantedIpsecAreas);
        addMetaData(data, logicalName);
        validateNodeAIData(data);

        log.debug("Fetching Security Level SCEP Enrollment info");
        final ScepEnrollmentInfo secLevelScepInfo = 
                    cpp.generateOamEnrollmentInfo(fdn, null, null, null, wantedEnrollmentMode, modelInfo);
        log.debug("Fetching Security Level trust store info");
        final TrustStoreInfo secLevelTrustInfo = cpp.getTrustStoreForAP(TrustedCertCategory.CORBA_PEERS, 
                secLevelScepInfo.getName(), modelInfo);
//       final TrustStoreInfo secLevelTrustInfo = cpp.getTrustStoreInfo(TrustedCertCategory.CORBA_PEERS);
        log.debug("Fetching IPSec SCEP Enrollment info");
        final ScepEnrollmentInfo ipsecScepInfo = 
                cpp.generateIpsecEnrollmentInfo(fdn, null, subjectAltName, 
                            subjectAltNameFormat, wantedEnrollmentMode, modelInfo);
        log.debug("Fetching IPSEC trust store info");
        final TrustStoreInfo ipsecTrustInfo = cpp.getTrustStoreForAP(TrustedCertCategory.IPSEC, 
                ipsecScepInfo.getName(), modelInfo);

        addSecLevelEnrollmentInfo(data, modelInfo.getNodeType(), secLevelScepInfo);
        addIpsecEnrollmentInfo(data, modelInfo.getNodeType(), ipsecScepInfo, 
                subjectAltName,subjectAltNameFormat);
        if (data.getSubjectAltName() == null) {
            throw new InvalidNodeAIDataException("Property \"subjectAltName\" cannot be null string");
        }
        addSecLevelTrustStoreInfo(data, secLevelTrustInfo, TrustedCertCategory.CORBA_PEERS);
        addIpsecTrustStoreInfo(data, ipsecTrustInfo, TrustedCertCategory.IPSEC);

        log.debug("Finished gathering auto integration data for node {}", fdn);
        return data;
    }

    @Override
    public boolean validateNodeAIData(final NodeAIData data)
            throws SecurityLevelNotSupportedException, InvalidNodeAIDataException {
        log.info("Validating gathered auto integration data for node {}", data.getFdn());
        final boolean isValid = validateCommonNodeAIData(data);

        // TORF-90405 : do not check User Label
//        if (data.getIpsecUserLabel() == null) {
//            throw new InvalidNodeAIDataException("User Label cannot be null");
//        }
        if (data.getIpsecUserLabel() != null) {
            if (data.getIpsecUserLabel().length() > config.getIpsecUserLabelMaxLength())
              throw new InvalidNodeAIDataException("User Label too long");
        }
        if (data.getWantedSecLevel() == null || data.getMinimumSecLevel() == null) {
            throw new SecurityLevelNotSupportedException("Wanted Security Level and Minimum Security Level cannot be null");
        }
        if (data.getMinimumSecLevel().compareTo(SecurityLevel.LEVEL_NOT_SUPPORTED) >= 0) {
            throw new SecurityLevelNotSupportedException("Minimum Security Level invalid");
        }
        if (data.getWantedSecLevel().compareTo(SecurityLevel.LEVEL_NOT_SUPPORTED) >= 0) {
            throw new SecurityLevelNotSupportedException("Wanted Security Level invalid");
        }
        if (data.getWantedSecLevel().compareTo(SecurityLevel.LEVEL_3) == 0) {
            throw new SecurityLevelNotSupportedException("Security Level 3 not supported");
        }
        if (data.getWantedSecLevel().compareTo(data.getMinimumSecLevel()) < 0) {
            throw new SecurityLevelNotSupportedException("Minimum Security Level cannot be greater than Wanted Security Level");
        }
//        if (data.getSubjectAltName() == null) {
//            throw new InvalidNodeAIDataException("Property \"subjectAltName\" cannot be null string");
//        }
        if(data.getIpsecAreas().isEmpty()) {
            throw new InvalidNodeAIDataException("At least one of IP Security area must be present");
        }

        log.info("Node auto integration data is valid returning");
        return isValid;
    }

    private void addBasicData(
            final NodeAIData data,
            final SecurityLevel wantedSecurityLevel,
            final SecurityLevel minimumSecurityLevel,
            final String fdn,
            final byte[] rbsIntegrityCode,
            final String userLabel,
            final Set<IpsecArea> wantedIpsecAreas
    ) {
        data.setWantedSecLevel(wantedSecurityLevel);
        data.setMinimumSecLevel(minimumSecurityLevel);
        data.setFdn(fdn);
        data.setRic(rbsIntegrityCode);
        data.setLogonServerAddress(config.getIscfLogonServerAddress());
        data.setIpsecUserLabel(userLabel);
        data.setIpsecCertExpirWarnTime(Integer.valueOf(config.getCertExpiryWarnTime()));
        for(IpsecArea ipsecArea: wantedIpsecAreas){
            data.getIpsecAreas().add(ipsecArea);
        }
    }

    private void addSecLevelEnrollmentInfo(
            final NodeAIData data, final String nodeType,
            final ScepEnrollmentInfo secLevelScepInfo
    ) throws IscfEncryptionException, UnsupportedEncodingException {

        final SecEnrollmentDataDto secDto = new SecEnrollmentDataDto();

        final EnrollmentDataDto enrollDto = new EnrollmentDataDto();
        enrollDto.setCAFingerprint(parseCaFingerPrint(secLevelScepInfo));
        enrollDto.setDataChallengePassword(getDataChallengePassword(data, secLevelScepInfo));
        enrollDto.setDistinguishedName(secLevelScepInfo.getDistinguishedName());
        String enrollmentServerURL = secLevelScepInfo.getServerURL();
        enrollmentServerURL = updateEnrollmentServerUrl(enrollmentServerURL, nodeType);
        enrollDto.setEnrollmentServerURL(enrollmentServerURL);
        enrollDto.setEnrollmentServerURL(secLevelScepInfo.getServerURL());
        Integer keySize = Integer.parseInt(secLevelScepInfo.getKeySize());
        enrollDto.setKeyLength(keySize);
        Integer enrollmentProtocol = Integer.parseInt(secLevelScepInfo.getEnrollmentProtocol());
        enrollDto.setEnrollmentMode(enrollmentProtocol);
        enrollDto.setCertificateAuthorityDn(CertDetails.getBcX500Name(secLevelScepInfo.getCertificateAuthorityDn()));
        enrollDto.setEnrollmentTimeLimit(IscfConstants.DEFAULT_ENROLLMENT_TIME_LIMIT);

        secDto.setRollbackTimeout(secLevelScepInfo.getRollbackTimeout());
        secDto.setEnrollmentData(enrollDto);

        data.setSecEnrollmentData(secDto);
    }

    private void addIpsecEnrollmentInfo(
            final NodeAIData data, final String nodeType, final ScepEnrollmentInfo ipsecScepInfo, 
            final BaseSubjectAltNameDataType subjectAltName,
            final SubjectAltNameFormat subjectAltNameFormat
    ) throws IscfEncryptionException, UnsupportedEncodingException {

        final IpsecEnrollmentDataDto secDto = new IpsecEnrollmentDataDto();

        final EnrollmentDataDto enrollDto = new EnrollmentDataDto();
        enrollDto.setCAFingerprint(parseCaFingerPrint(ipsecScepInfo));
        enrollDto.setDataChallengePassword(getDataChallengePassword(data, ipsecScepInfo));
        enrollDto.setDistinguishedName(ipsecScepInfo.getDistinguishedName());
        String enrollmentServerURL = ipsecScepInfo.getServerURL();
        enrollmentServerURL = updateEnrollmentServerUrl(enrollmentServerURL, nodeType);
        enrollDto.setEnrollmentServerURL(enrollmentServerURL);
        //enrollDto.setKeyLength(new Integer(config.getEnrollmentKeyLength()));
        Integer keySize = Integer.parseInt(ipsecScepInfo.getKeySize());
        enrollDto.setKeyLength(keySize);
        Integer enrollmentProtocol = Integer.parseInt(ipsecScepInfo.getEnrollmentProtocol());
        enrollDto.setEnrollmentMode(enrollmentProtocol);
        enrollDto.setCertificateAuthorityDn(CertDetails.getBcX500Name(ipsecScepInfo.getCertificateAuthorityDn()));
        enrollDto.setEnrollmentTimeLimit(IscfConstants.DEFAULT_ENROLLMENT_TIME_LIMIT);

        secDto.setSubjectAltNameFormat(data.getSubjectAltNameFormat());
        secDto.setEnrollmentData(enrollDto);

        data.setIpsecEnrollmentDataDto(secDto);

        if ((subjectAltName != null) && (subjectAltNameFormat != null)) {
            data.setSubjectAltName(subjectAltName);
            data.setSubjectAltNameFormat(subjectAltNameFormat);
        } else {
            data.setSubjectAltName(ipsecScepInfo.getSubjectAltName());
            data.setSubjectAltNameFormat(ipsecScepInfo.getSubjectAltNameType());
        }
    }

    private void addSecLevelTrustStoreInfo(
            final NodeAIData data,
            final TrustStoreInfo secLevelTrustInfo,
            final TrustedCertCategory cppTrustedCertCategory
    ) throws IscfEncryptionException, CertificateException, CertificateEncodingException, NoSuchAlgorithmException {
        log.debug("Fetching Security Level trusted certificates");
        final List<CertFileDto> certFileDtoList = data.getSecLevelCertFileDtos();
        addCertDtos(data, secLevelTrustInfo, cppTrustedCertCategory, certFileDtoList);
        for(CertSpec spec : secLevelTrustInfo.getCertSpecs()) {
            data.getSecLevelCertSpecs().add(spec);
        }
        log.debug("Fetching Security Level trusted certificates finished");
    }

    private void addIpsecTrustStoreInfo(
            final NodeAIData data,
            final TrustStoreInfo ipsecTrustInfo,
            final TrustedCertCategory trustedCertCategory
    ) throws IscfEncryptionException, CertificateEncodingException, NoSuchAlgorithmException {
        log.debug("Fetching IPSec trusted certificates");
        final List<CertFileDto> certFileDtoList = data.getIpsecCertFileDtos();
        addCertDtos(data, ipsecTrustInfo, trustedCertCategory, certFileDtoList);
        for(CertSpec spec : ipsecTrustInfo.getCertSpecs()) {
            data.getIpsecCertSpecs().add(spec);
        }
        log.debug("Fetching IPSec trusted certificates finished");
    }

}
