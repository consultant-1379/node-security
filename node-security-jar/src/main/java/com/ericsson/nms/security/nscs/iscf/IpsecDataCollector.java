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
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Set;

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType;
import com.ericsson.nms.security.nscs.api.iscf.IpsecArea;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo;
import com.ericsson.nms.security.nscs.cpp.model.TrustStoreInfo;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.iscf.dto.*;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.smrs.api.exception.SmrsDirectoryException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

import java.security.NoSuchAlgorithmException;

/**
 * Data collector for IPSec ISCF XML generation
 *
 * @author ealemca
 */
public class IpsecDataCollector extends BaseDataCollector {

    /**
     * Fetch all required data from external services and encapsulate in a
     * {@link NodeAIData} object containing all info needed for the Security
     * Level wanted and the IpsecArea wanted.
     *
     * @param fdn
     * @param logicalName
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
            InvalidNodeAIDataException,
            NoSuchAlgorithmException {

        log.info("Gathering auto integration data for node {}", fdn);
        final NodeAIData data = new NodeAIData();
        addBasicData(data, fdn, rbsIntegrityCode, userLabel, wantedIpsecAreas);
        addMetaData(data, logicalName);
        validateNodeAIData(data);
        log.info("Fetching SCEP Enrollment info");

        final ScepEnrollmentInfo scepInfo = 
                cpp.generateIpsecEnrollmentInfo(fdn, null, subjectAltName, subjectAltNameFormat, 
                        wantedEnrollmentMode, modelInfo);
        log.debug("Fetching trust store info");
        final TrustStoreInfo trustInfo = cpp.getTrustStoreForAP(TrustedCertCategory.IPSEC, 
                scepInfo.getName(), modelInfo);
//        final TrustStoreInfo trustInfo = cpp.getTrustStoreInfo(TrustedCertCategory.IPSEC);

        addEnrollmentInfo(data, modelInfo.getNodeType(), scepInfo, subjectAltName,subjectAltNameFormat);
        addTrustStoreInfo(data, trustInfo, TrustedCertCategory.IPSEC);
        if (data.getSubjectAltName() == null) {
            log.warn("Property 'subjectAltName' cannot be null string");
            throw new InvalidNodeAIDataException("Property \"subjectAltName\" cannot be null string");
        }

        log.debug("Finished gathering auto integration data for node {}", fdn);
        return data;
    }

    @Override
    public boolean validateNodeAIData(final NodeAIData data)
            throws InvalidNodeAIDataException {
//        final Pattern alphaNumPattern = Pattern.compile("[a-zA-Z0-9]");
        log.info("Validating gathered auto integration data for node {}", data.getFdn());
        validateCommonNodeAIData(data);
        // TORF-90405 : do not check User Label
//        if (data.getIpsecUserLabel() == null) {
//            throw new InvalidNodeAIDataException("User Label cannot be null");
//        }
//        Matcher m = alphaNumPattern.matcher(data.getIpsecUserLabel());
//        if (!m.find()) {
//            throw new InvalidNodeAIDataException("User Label must contain an alphanumeric character");
//        }
        if (data.getIpsecUserLabel() != null) {
            if (data.getIpsecUserLabel().length() > config.getIpsecUserLabelMaxLength())
              throw new InvalidNodeAIDataException("User Label too long");
        }
        if (data.getIpsecAreas().isEmpty()) {
            log.warn("At least one of IP Security area must be present");
            throw new InvalidNodeAIDataException("At least one of IP Security area must be present");
        }
//        if (data.getSubjectAltName() == null) {
//            log.warn("Property 'subjectAltName' cannot be null string");
//            throw new InvalidNodeAIDataException("Property \"subjectAltName\" cannot be null string");
//        }
        return true;
    }

    private void addBasicData(
            final NodeAIData data,
            final String fdn,
            final byte[] rbsIntegrityCode,
            final String userLabel,
            final Set<IpsecArea> wantedIpsecAreas
    ) {
        data.setFdn(fdn);
        data.setRic(rbsIntegrityCode);
        data.setIpsecUserLabel(userLabel);
        data.setIpsecCertExpirWarnTime(Integer.valueOf(config.getCertExpiryWarnTime()));
        data.setLogonServerAddress(config.getIscfLogonServerAddress());
        for(IpsecArea ipsecArea: wantedIpsecAreas){
            data.getIpsecAreas().add(ipsecArea);
        }
    }

    private void addEnrollmentInfo(final NodeAIData data, final String nodeType,
            final ScepEnrollmentInfo scepInfo, final BaseSubjectAltNameDataType subjectAltName,
            final SubjectAltNameFormat subjectAltNameFormat)
            throws IscfEncryptionException, UnsupportedEncodingException {

        final IpsecEnrollmentDataDto ipsecDto = new IpsecEnrollmentDataDto();

        final EnrollmentDataDto enrollDto = new EnrollmentDataDto();
        enrollDto.setCAFingerprint(parseCaFingerPrint(scepInfo));
        enrollDto.setDataChallengePassword(getDataChallengePassword(data, scepInfo));
        enrollDto.setDistinguishedName(scepInfo.getDistinguishedName());
        String enrollmentServerURL = scepInfo.getServerURL();
        enrollmentServerURL = updateEnrollmentServerUrl(enrollmentServerURL, nodeType);
        enrollDto.setEnrollmentServerURL(enrollmentServerURL);
        //enrollDto.setKeyLength(new Integer(config.getEnrollmentKeyLength()));
        Integer keySize = Integer.parseInt(scepInfo.getKeySize());
        enrollDto.setKeyLength(keySize);
        Integer enrollmentProtocol = Integer.parseInt(scepInfo.getEnrollmentProtocol());
        enrollDto.setEnrollmentMode(enrollmentProtocol);
        enrollDto.setCertificateAuthorityDn(CertDetails.getBcX500Name(scepInfo.getCertificateAuthorityDn()));
        enrollDto.setEnrollmentTimeLimit(IscfConstants.DEFAULT_ENROLLMENT_TIME_LIMIT);

        ipsecDto.setSubjectAltNameFormat(data.getSubjectAltNameFormat());
        ipsecDto.setRollbackTimeout(scepInfo.getRollbackTimeout());
        ipsecDto.setEnrollmentData(enrollDto);

        data.setIpsecEnrollmentDataDto(ipsecDto);

        if ((subjectAltName != null) && (subjectAltNameFormat != null)) {
            data.setSubjectAltName(subjectAltName);
            data.setSubjectAltNameFormat(subjectAltNameFormat);
        } else {
            data.setSubjectAltName(scepInfo.getSubjectAltName());
            data.setSubjectAltNameFormat(scepInfo.getSubjectAltNameType());
        }
    }

    private void addTrustStoreInfo(
            final NodeAIData data,
            final TrustStoreInfo trustInfo,
            final TrustedCertCategory trustedCertCategory
    ) throws IscfEncryptionException, CertificateEncodingException, NoSuchAlgorithmException {
        log.debug("Fetching trusted certificates");
        final List<CertFileDto> certFileDtoList = data.getIpsecCertFileDtos();
        addCertDtos(data, trustInfo, trustedCertCategory, certFileDtoList);
        for(CertSpec spec : trustInfo.getCertSpecs()) {
            data.getIpsecCertSpecs().add(spec);
        }
    }
    
}
