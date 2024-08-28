/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.cpp.model;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceBean;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.pki.NscsPkiEntitiesManagerJar;
import com.ericsson.nms.security.nscs.pki.NscsPkiUtils;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.AbstractSubjectAltNameFieldValue;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltName;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameField;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameFieldType;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;

/**
 * Implementation of the interface ScepEnrollmentInfo wrapping the Entity and
 * EnrollmentInfo objects from PKI.
 *
 *
 * @see com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo
 * @see
 * <a href="https://cpp-mom.rnd.ki.sw.ericsson.se/cpp_c14/momdoc/CPP-LSV127-gen9-complete_vs_LSV125/EnrollmentData.html">CPP
 * MOM</a>
 * @author egbobcs
 *
 */
public class ScepEnrollmentInfoImpl implements ScepEnrollmentInfo, Serializable {

    private static final long serialVersionUID = 3L;
    private static Logger logger = LoggerFactory.getLogger(ScepEnrollmentInfoImpl.class);
    public static final String DEFAULT_KEY_SIZE = CppSecurityServiceBean.KeyLength.RSA2048.toString();
    public static final String DEFAULT_ENROLLMENT_PROTOCOL = EnrollmentMode.SCEP.getEnrollmentModeValue();

    private final Entity ee;
    private String enrollmentProtocol = null;
    private String enrollmentServerUrl = null;
    private String keySize = null;
    private EnrollmentMode enrollmentMode = null;
    private String certificateAuthorityDn = null;
    private String enrollmentCaName = null;
    private byte[] cAFingerPrint = null;
    private String pkiRootCertificateAuthorityDn = null;
    private byte[] pkiRootCAFingerPrint = null;
    private DigestAlgorithm fingerPrintAlgorithm = null;
    private String challengePassword = null;

    private boolean isKSandEMSupported = true;
    private boolean isCertificateAuthorityDnSupported = false;

    private int rollbackTimeOut = 0;

    /**
     * Constructor which sets all the fields according to the input parameters
     *
     * @param ee the Entity
     * @param enrollmentUrl
     * @param caCert
     * @param digestAlgorithm
     * @param rollbackTimeOut
     * @param oneTimePassword
     * @param keySize the value of keySize
     * @param enrollmentMode
     * @param pkiRootCaCert
     * @param enrollmentCaName
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.cert.CertificateEncodingException
     */
    public ScepEnrollmentInfoImpl(final Entity ee, final String enrollmentUrl, 
            final X509Certificate caCert, final DigestAlgorithm digestAlgorithm, 
            final int rollbackTimeOut, final String oneTimePassword, final String keySize,
            final EnrollmentMode enrollmentMode, final X509Certificate pkiRootCaCert, final String enrollmentCaName)
            throws NoSuchAlgorithmException, CertificateEncodingException {
        this.ee = ee;
        this.rollbackTimeOut = rollbackTimeOut;
        this.challengePassword = oneTimePassword;
        this.fingerPrintAlgorithm = digestAlgorithm;
        this.keySize = keySize;
        this.enrollmentMode = enrollmentMode;
        this.enrollmentServerUrl = enrollmentUrl;
        if ((caCert != null) && (digestAlgorithm != null)) {
            this.certificateAuthorityDn = caCert.getSubjectX500Principal().toString();
            this.cAFingerPrint = NscsPkiUtils.generateMessageDigest(digestAlgorithm, caCert.getEncoded());
            }
        if ((pkiRootCaCert != null) && (digestAlgorithm != null)) {
            this.pkiRootCertificateAuthorityDn = pkiRootCaCert.getSubjectX500Principal().toString();
            this.pkiRootCAFingerPrint = NscsPkiUtils.generateMessageDigest(digestAlgorithm, pkiRootCaCert.getEncoded());
        }
        if(enrollmentCaName != null){
            this.enrollmentCaName = enrollmentCaName;
        }
    }

    @Override
    public String getDistinguishedName() {
        return ee.getEntityInfo().getSubject().toASN1String();
    }

    @Override
    public String getName() {
        return ee.getEntityInfo().getName();
    }

    @Override
    public String getChallengePassword() {
        return challengePassword;
    }

    @Override
    public String getServerURL() {
        return this.enrollmentServerUrl;
    }

    @Override
    public void setServerURL(String serverUrl) {
    	this.enrollmentServerUrl = serverUrl;
    }

    @Override
    public byte[] getServerCertFingerPrint() {
        return cAFingerPrint;
    }

    @Override
    public void setServerCertFingerPrint(final byte[] fp) {
        cAFingerPrint = fp;
    }
    
    @Override
    public byte[] getPkiRootCertFingerPrint() {
        return pkiRootCAFingerPrint;
    }

    @Override
    public void setPkiRootCertFingerPrint(final byte[] fp) {
    	pkiRootCAFingerPrint = fp;
    }
    
    @Override
    public String getEnrollmentCaName() {
        return enrollmentCaName;
    }

    @Override
    public void setEnrollmentCaName(final String caName) {
        enrollmentCaName = caName;
    }

    @Override
    public DigestAlgorithm getFingerPrintAlgorithm() {
        return this.fingerPrintAlgorithm;
    }

    @Override
    public int getRollbackTimeout() {
        return rollbackTimeOut;
    }

    @Override
    public String toString() {
//        String caFingerPrint = new String(this.getServerCertFingerPrint());
        String ret = "ScepEnrollmentInfoImpl\n";
        ret += NscsPkiEntitiesManagerJar.getEntityLog(ee) + "\n";
        ret += "EnrollmentURL: " + enrollmentServerUrl + "\n";
        ret += "OTP: ";
        if ((challengePassword != null) && (!challengePassword.isEmpty())) {
            ret += "*****";
        }
        ret += "\nDistinguishedName: " + this.getDistinguishedName() + "\n";
        ret += "rollbackTimeout: " + rollbackTimeOut + "\n";
        ret += "enrollmentProtocol: " + this.enrollmentProtocol + "\n";
        ret += "keySize: " + this.keySize + "\n";
        ret += "certificateAuthorityDn: " + this.certificateAuthorityDn + "\n";
        //      ret += "serverCertFingerPrint(): " + caFingerPrint + "\n";
        ret += "isKSandEMSupported: " + this.isKSandEMSupported + "\n";
        ret += "isCertificateAuthorityDnSupported: " + this.isCertificateAuthorityDnSupported + "\n";
        return ret;
    }

    /**
     * Returns the MoParams representation of the supplied values.<br />
     * Check CPP reference
     * <code>void initCertEnrollment ( EnrollmentData data );</code>
     *
     * @return 
     * @see <a
     *      href="https://cpp-mom.rnd.ki.sw.ericsson.se/cpp_c14/momdoc/CPP-LSV127-gen9-complete_vs_LSV125/EnrollmentData.html">CPP
     * MOM</a>
     */
    public MoParams toMoParams() {
        // TODO To be invoked with correct node type
        return toMoParams(getServerCertFingerPrint(), getChallengePassword(),
                getDistinguishedName(), getEnrollmentProtocol(),
                getServerURL(), getKeySize(), getRollbackTimeout(),
                isKSandEMSupported(), isCertificateAuthorityDnSupported(),
                getCertificateAuthorityDn(), getFingerPrintAlgorithm());
    }

    /**
     * Returns the MoParams representation of the supplied values. <br/>
     * Check CPP reference
     * <code>void initCertEnrollment (IpSecEnrollmentData data);</code>
     *
     * @see http
     * ://cpistore.internal.ericsson.com/alexserv?li=en/lzn7840900/2r17a
     * &fn=50_1553-hsc10550_1uen.pj1.html&ac=linkext
     */
    public MoParams toIpSecMoParams() {
        return toIpSecMoParams(getServerCertFingerPrint(),
                getChallengePassword(), getDistinguishedName(),
                getEnrollmentProtocol(), getServerURL(), getKeySize(),
                getRollbackTimeout(), isKSandEMSupported(), getSubjectAltNameForMOParams(),
                getSubjectAltNameTypeForMOParams(), isCertificateAuthorityDnSupported(),
                getCertificateAuthorityDn(), getFingerPrintAlgorithm() );
    }

    /**
     * Returns the MoIpSecParams representation of the object's values.
     *
     * @see
     * <a href="http://cpistore.internal.ericsson.com/alexserv?ID=18771&fn=15554-EN_LZN7850001_2-V1Uen.G.130.html">CPP_MOM</a>
     */
    public static MoParams toIpSecMoParams(final byte[] caFingerPrint, final String challengePassword, final String distinguishedName,
            final String enrollmentMode, final String enrollmentServerURL, final String keyLength,
            final int rollbackTimeOut, final boolean isKSandEMSupported, final String subAltName, final String subAltNameType,
            final boolean isCertificateAuthorityDnSupported, final String certificateAuthorityDn, final DigestAlgorithm fingerPrintAlgo) {
        final MoParams params = new MoParams();
        final MoParams data = new MoParams();
        data.addParam("caFingerPrint", formatFingerprint(caFingerPrint, fingerPrintAlgo));
        data.addParam("challengePassword", challengePassword, false);
        data.addParam("distinguishedName", distinguishedName);
        data.addParam("keyLength", keyLength);
        if(isKSandEMSupported) {
        	data.addParam("enrollmentMode", EnrollmentMode.getEnrollmentModeFromValue(enrollmentMode).name());
        }
        data.addParam("enrollmentServerURL", enrollmentServerURL);
        // TODO hardcoded value. They will be implemented from user input on TORF-30091
        data.addParam("subjectAltName", subAltName);
        data.addParam("subjectAltNameType", subAltNameType);
        if(isCertificateAuthorityDnSupported) {
        	data.addParam("certificateAuthorityDn", certificateAuthorityDn);
        }
        params.addParam("enrollmentData", data);
        return params;
    }

    /**
     * Returns the MoParams representation of the object's values.
     *
     * @see
     * <a href="https://cpp-mom.rnd.ki.sw.ericsson.se/cpp_c14/momdoc/CPP-LSV127-gen9-complete_vs_LSV125/EnrollmentData.html">CPP
     * MOM</a>
     */
    public static MoParams toMoParams(final byte[] caFingerPrint, final String challengePassword, final String distinguishedName,
            final String enrollmentMode, final String enrollmentServerURL, final String keyLength, final int rollbackTimeOut, final boolean isKSandEMSupported,
            final boolean isCertificateAuthorityDnSupported, final String certificateAuthorityDn, final DigestAlgorithm fingerPrintAlgo) {
        final MoParams params = new MoParams();
        final MoParams data = new MoParams();
        data.addParam("caFingerPrint", formatFingerprint(caFingerPrint, fingerPrintAlgo));
        data.addParam("challengePassword", challengePassword, false);
        data.addParam("distinguishedName", distinguishedName);
        if (isKSandEMSupported) {
            data.addParam("enrollmentMode", EnrollmentMode.getEnrollmentModeFromValue(enrollmentMode).name());
        }
        data.addParam("enrollmentServerURL", enrollmentServerURL);
        if (isKSandEMSupported) {
            data.addParam("keyLength", keyLength);
        }
        if (isCertificateAuthorityDnSupported) {
            data.addParam("certificateAuthorityDn", certificateAuthorityDn);
        }

        data.addParam("rollbackTimeOut", String.valueOf(rollbackTimeOut));
        params.addParam("data", data);
        return params;
    }

    @Override
    public void setRollbackTimeout(final int timeout) {
        this.rollbackTimeOut = timeout;

    }

    @Override
    public String getEnrollmentProtocol() {
        if (this.enrollmentProtocol != null) {
            return this.enrollmentProtocol;
        }
        return DEFAULT_ENROLLMENT_PROTOCOL;
    }

    @Override
    public void setEnrollmentProtocol(final String value) {
        this.enrollmentProtocol = value;
    }

    @Override
    public String getKeySize() {
        if (this.keySize != null) {
            return this.keySize;
        }
        return DEFAULT_KEY_SIZE;
    }

    @Override
    public void setKeySize(final String value) {
        this.keySize = value;
    }
    
    @Override
    public void setEnrollmentMode(final EnrollmentMode enrollmentMode) {
    	this.enrollmentMode = enrollmentMode;
    }
    
    @Override
    public EnrollmentMode getEnrollmentMode() {
    	return this.enrollmentMode;
    }

    @Override
    public String getCertificateAuthorityDn() {
        return this.certificateAuthorityDn;
    }

    @Override
    public void setCertificateAuthorityDn(final String value) {
        this.certificateAuthorityDn = value;
    }
    
    @Override
    public String getPkiRootCertificateAuthorityDn() {
        return this.pkiRootCertificateAuthorityDn;
    }

    @Override
    public void setPkiRootCertificateAuthorityDn(String value) {
        this.pkiRootCertificateAuthorityDn = value;
    }

    protected static String formatFingerprint(final byte[] fingerprint, final DigestAlgorithm fingerPrintAlgo) {
        return fingerPrintAlgo.getDigestValuePrefix() + CertSpec.bytesToHex(fingerprint);
    }

    @Override
    public boolean isKSandEMSupported() {
        return isKSandEMSupported;
    }

    @Override
    public void setKSandEMSupported(final boolean isSupported) {
        this.isKSandEMSupported = isSupported;
    }

    @Override
    public boolean isCertificateAuthorityDnSupported() {
        return isCertificateAuthorityDnSupported;
    }

    @Override
    public void setCertificateAuthorityDnSupported(final boolean isCertificateAuthorityDnSupported) {
        this.isCertificateAuthorityDnSupported = isCertificateAuthorityDnSupported;
    }

    @Override
    public BaseSubjectAltNameDataType getSubjectAltName() {
        BaseSubjectAltNameDataType retValue = new SubjectAltNameStringType("");
        final AbstractSubjectAltNameFieldValue subjectAltNameValPki = getSubjectAltNameValuePki();
        final SubjectAltNameFieldType subjectAltNameTypePki = getSubjectAltNameTypePki();
        if ((subjectAltNameTypePki != null) && (subjectAltNameValPki != null)) {
            retValue = NscsPkiUtils.convertSubjectAltNameValueToNscsFormat(
                    subjectAltNameTypePki, subjectAltNameValPki);
        }
        System.out.println("XXXXXXXXXX getSubjectAltName() " + retValue);

        return retValue;
    }

    @Override
    public SubjectAltNameFormat getSubjectAltNameType() {
        SubjectAltNameFormat retValue = SubjectAltNameFormat.NONE;
        final SubjectAltNameFieldType subjectAltNameTypePki = getSubjectAltNameTypePki();
        final BaseSubjectAltNameDataType subjectAltNameDataType = this.getSubjectAltName();
        if (subjectAltNameTypePki != null)
            retValue = NscsPkiUtils.convertSubjectAltNameFieldTypeToNscsFormat(subjectAltNameTypePki, subjectAltNameDataType);
        logger.debug("Inputs subjectAltNameTypePki [{}] and subjectAltNameDataType [{}] returned value: {}",
                subjectAltNameTypePki, subjectAltNameDataType, retValue);
        return retValue;
    }

    private String getSubjectAltNameTypeForMOParams() {
       return getSubjectAltNameType().name();
    }

    public String getSubjectAltNameForMOParams() {
        String retValue = "";
        final AbstractSubjectAltNameFieldValue subjectAltNameVal = getSubjectAltNameValuePki();
        if  (subjectAltNameVal != null)
            retValue = subjectAltNameVal.toString();
        System.out.println("XXXXXXXXXX getSubjectAltName() " + retValue);

        return retValue;
    }

    private AbstractSubjectAltNameFieldValue getSubjectAltNameValuePki() {
        AbstractSubjectAltNameFieldValue subjectAltNameVal = null;
        if (this.ee != null) {
            final SubjectAltName subjectAltNameValues = this.ee.getEntityInfo().getSubjectAltName();
            if (subjectAltNameValues != null) {
                List<SubjectAltNameField> sanvList = subjectAltNameValues.getSubjectAltNameFields();
                subjectAltNameVal = sanvList.iterator().next().getValue();
            }
        }
        return subjectAltNameVal;
    }

    private SubjectAltNameFieldType getSubjectAltNameTypePki() {
        SubjectAltNameFieldType subjectAltNameType = null;
        if (this.ee != null) {
            final SubjectAltName subjectAltNameValues = this.ee.getEntityInfo().getSubjectAltName();
            if (subjectAltNameValues != null) {
                List<SubjectAltNameField> sanvList = subjectAltNameValues.getSubjectAltNameFields();
                if ((sanvList != null) && (!sanvList.isEmpty())) {
                    subjectAltNameType = sanvList.iterator().next().getType();
                }
            }
        }
        return subjectAltNameType;
    }

	@Override
	public Entity getEntity() {
		return this.ee;
	}
}
