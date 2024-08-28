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
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.nscs.cpp.model.*;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.iscf.dto.*;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

/**
 * Base class for gathering data needed for ISCF XML generation
 *
 * @author ealemca
 */
public abstract class BaseDataCollector {

    @Inject
    protected Logger log;

    @Inject
    protected IscfConfigurationBean config;

    @Inject
    protected IscfEncryptor iscfEncryptor;

    @Inject
    protected RicGenerator ricGenerator;

    @Inject
    protected CppSecurityService cpp;

    /**
     * Validates the portions of the data gathered that were passed by the client. This includes
     * checking for null values, whether the requested and minimum Security Levels and requested
     * IPSec areas are valid and supported by the implementation
     *
     * @param data The NodeAIData gathered from the client
     * @return boolean A true or false result on validation
     * @throws SecurityLevelNotSupportedException
     * @throws InvalidNodeAIDataException
     */
    public abstract boolean validateNodeAIData(final NodeAIData data)
            throws SecurityLevelNotSupportedException, InvalidNodeAIDataException;

    protected void addMetaData(final NodeAIData data, final String logicalName) {
        data.setLogicalName(logicalName);
    }

    protected ISCFEncryptedContentDto getDataChallengePassword(
            final NodeAIData data,
            final ScepEnrollmentInfo scepInfo
    ) throws IscfEncryptionException, UnsupportedEncodingException {

        final ISCFEncryptedContentDto dto = new ISCFEncryptedContentDto();
        
        byte[] passwordAsBytes = scepInfo.getChallengePassword().getBytes(IscfConstants.UTF8_CHARSET);

        final EncryptedContentDto eDto = getEncryptedContentDto(data, passwordAsBytes);
        dto.setEncryptedContent(eDto);

        return dto;
    }

    protected EncryptedContentDto getEncryptedContentDto(
            final NodeAIData data,
            final byte[] value
    ) throws IscfEncryptionException {

        final EncryptedContentDto eDto = new EncryptedContentDto();

        log.debug("Generating salt value for encryption");
        final byte[] salt = ricGenerator.generateSalt();
        final byte[] ric = data.getRic();
        log.debug("Encrypting byte array of length {}", value.length);
        final byte[] encryptedValue = iscfEncryptor.encrypt(value, ric, salt);

        final BigInteger big = BigInteger.valueOf(config.getCipherIterationCount());

        eDto.setValue(encryptedValue);
        eDto.setPBKDF2IterationCount(big);
        eDto.setPBKDF2Salt(salt);

        return eDto;
    }

    protected NodeAIData addCertDtos(
            final NodeAIData data,
            final TrustStoreInfo trustInfo,
            final TrustedCertCategory trustedCertCategory,
            final List<CertFileDto> certfileDtoList
    ) throws IscfEncryptionException, CertificateEncodingException, NoSuchAlgorithmException {

        for(CertSpec spec : trustInfo.getCertSpecs()) {
            final CertFileDto certDto = new CertFileDto();
            certDto.setCategory(trustedCertCategory.getIscfCategoryName());
            certDto.setCertFingerprint(parseCertFingerPrint(spec, trustInfo.getFingerPrintAlgorithm(), 
                    trustedCertCategory));
            certDto.setCertSerialNumber(spec.getSerial());
            final X509Certificate holder = spec.getCertHolder();
            final EncryptedContentDto eDto =
                    getEncryptedContentDto(data, holder.getEncoded());
            certDto.setEncryptedContent(eDto);
            certfileDtoList.add(certDto);
        }

        return data;
    }

    /**
     *
     * @param scepInfo
     * @return formatted CA certificate fingerprint
     */
    protected String parseCaFingerPrint(final ScepEnrollmentInfo scepInfo) {
        final byte[] fingerprintBytes = scepInfo.getServerCertFingerPrint();
        final String prefix = scepInfo.getFingerPrintAlgorithm().getDigestValuePrefix();
        return this.parseFingerPrint(fingerprintBytes, prefix);
    }

    /**
     *
     * @param spec
     * @param fingerprintAlgo
     * @param category
     * @return
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.cert.CertificateEncodingException
     */
    protected String parseCertFingerPrint(final CertSpec spec, DigestAlgorithm fingerprintAlgo,
            TrustedCertCategory category) throws NoSuchAlgorithmException, CertificateEncodingException {
        final CPPCertSpec cmCertSpec = new CPPCertSpec(spec, category, fingerprintAlgo, null);
        final byte[] fingerprintBytes = cmCertSpec.getFingerPrint();
        return parseFingerPrint(fingerprintBytes, fingerprintAlgo.getDigestValuePrefix());
    }

    protected String updateEnrollmentServerUrl(final String enrollmentServerUrl, final String nodeType) {
        String retServerUrl = enrollmentServerUrl;
        // Do nothing
        return retServerUrl;
    }

    protected boolean validateCommonNodeAIData(final NodeAIData data) throws InvalidNodeAIDataException {
        log.info("Validating common integration data for node");
        final Pattern alphaNumPattern = Pattern.compile("[a-zA-Z0-9]");

        if (data.getLogicalName() == null) {
            throw new InvalidNodeAIDataException("Logical Name cannot be null");
        }
        Matcher m = alphaNumPattern.matcher(data.getLogicalName());
        if (!m.find()) {
            throw new InvalidNodeAIDataException("Logical Name must contain an alphanumeric character");
        }
        return true;
    }

    private String parseFingerPrint(final byte[] fingerprint, final String prefix) {
        final String fingerPrintStr = CertSpec.bytesToHex(fingerprint);
        return prefix + fingerPrintStr;
    }

}
