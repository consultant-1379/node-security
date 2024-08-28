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
package com.ericsson.nms.security.nscs.certificate.issue.input.xml;

import java.net.StandardProtocolFamily;
import java.util.Objects;

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.iscf.NodeIdentifier;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam;
import com.ericsson.oss.services.security.nscs.command.enrollmentinfo.OtpConfigurationParameters;

/**
 * EnrollmentRequestInfo holds the inputs of enrollment information fields.
 *
 * @author xkihari
 */
public class EnrollmentRequestInfo {

    private static final String LINE_SHIFT = "    ";

    private NodeIdentifier nodeIdentifier;
    private String certType;
    private String entityProfile;
    private String keySize;
    private String commonName;
    private EnrollmentMode enrollmentMode;
    private SubjectAltNameParam subjectAltNameParam;
    private String nodeName;
    private StandardProtocolFamily ipVersion;
    private OtpConfigurationParameters otpConfigurationParameters;

    /**
     * @return the nodeName
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * @param nodeName
     *           the nodeName to set
     */
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public EnrollmentRequestInfo() {
        super();
    }

    /**
     * @return the nodeIdentifier
     */
    public NodeIdentifier getNodeIdentifier() {
        return nodeIdentifier;
    }

    /**
     * @param nodeIdentifier
     *        the nodeIdentifier to set
     */
    public void setNodeIdentifier(final NodeIdentifier nodeIdentifier) {
        this.nodeIdentifier = nodeIdentifier;
    }

    /**
     * @return the certType
     */
    public String getCertType() {
        return certType;
    }

    /**
     * @param certType
     *        The certType to set
     */
    public void setCertType(final String certType) {
        this.certType = certType;
    }

    /**
     * @return the enrollmentMode
     */
    public EnrollmentMode getEnrollmentMode() {
        return enrollmentMode;
    }

    /**
     * @param enrollmentMode
     *        the enrollmentMode to set
     */
    public void setEnrollmentMode(final EnrollmentMode enrollmentMode) {
        this.enrollmentMode = enrollmentMode;
    }

    /**
     * @return the subjectAltNameParam
     */
    public SubjectAltNameParam getSubjectAltNameParam() {
        return subjectAltNameParam;
    }

    /**
     * @param subjectAltNameParam
     *        the subjectAltNameParam to set
     */
    public void setSubjectAltNameParam(final SubjectAltNameParam subjectAltNameParam) {
        this.subjectAltNameParam = subjectAltNameParam;
    }

    /**
     * @return the entityProfile
     */
    public String getEntityProfile() {
        return entityProfile;
    }

    /**
     * @param entityProfile
     *        the entityProfile to set
     */
    public void setEntityProfile(final String entityProfile) {
        this.entityProfile = entityProfile;
    }

    /**
     * @return the keySize
     */
    public String getKeySize() {
        return keySize;
    }

    /**
     * @param keySize
     *        the keySize to set
     */
    public void setKeySize(final String keySize) {
        this.keySize = keySize;
    }

    /**
     * @return the commonName
     */
    public String getCommonName() {
        return commonName;
    }

    /**
     * @param commonName
     *        the commonName to set
     */
    public void setCommonName(final String commonName) {
        this.commonName = commonName;
    }

    public void setIpVersion(final StandardProtocolFamily ipVersion) {
        this.ipVersion = ipVersion;
    }

    public StandardProtocolFamily getIpVersion() {
        return ipVersion;
    }

    /**
     * @return the otpConfigurationParameters
     */
    public OtpConfigurationParameters getOtpConfigurationParameters() {
        return otpConfigurationParameters;
    }

    /**
     * @param otpConfigurationParameters
     *            the otpConfigurationParameters to set
     */
    public void setOtpConfigurationParameters(final OtpConfigurationParameters otpConfigurationParameters) {
        this.otpConfigurationParameters = otpConfigurationParameters;
    }

    @Override
    public String toString() {
        String ret = LINE_SHIFT + "NodeIdentifier: " + nodeIdentifier + "\n";
        ret += LINE_SHIFT + "Certificate Type: " + certType + "\n";
        ret += LINE_SHIFT + "Entity Profile: " + entityProfile + "\n";
        ret += LINE_SHIFT + "KeySize: " + keySize + "\n";
        ret += LINE_SHIFT + "Common Name: " + commonName + "\n";
        ret += LINE_SHIFT + "Enrollment Mode: " + enrollmentMode + "\n";
        ret += LINE_SHIFT + "SubjectAltName: " + subjectAltNameParam + "\n";
        ret += LINE_SHIFT + "NodeName: " + nodeName + "\n";
        ret += LINE_SHIFT + "IP version: " + ((ipVersion == null) ? "null" : ipVersion.toString()) + "\n";
        ret += LINE_SHIFT + "OTP configuration parameters: " + ((otpConfigurationParameters == null) ? "null" : otpConfigurationParameters.toString())
                + "\n";
        return ret;
    }

    @Override
    public int hashCode() {
        return Objects.hash(certType, commonName, enrollmentMode, entityProfile, ipVersion, keySize, nodeIdentifier, nodeName,
                otpConfigurationParameters, subjectAltNameParam);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        EnrollmentRequestInfo other = (EnrollmentRequestInfo) obj;
        return Objects.equals(certType, other.certType) && Objects.equals(commonName, other.commonName) && enrollmentMode == other.enrollmentMode
                && Objects.equals(entityProfile, other.entityProfile) && ipVersion == other.ipVersion && Objects.equals(keySize, other.keySize)
                && Objects.equals(nodeIdentifier, other.nodeIdentifier) && Objects.equals(nodeName, other.nodeName)
                && Objects.equals(otpConfigurationParameters, other.otpConfigurationParameters)
                && equalsSubjectAltNameParam(other);
    }

    private boolean equalsSubjectAltNameParam(final EnrollmentRequestInfo other) {
        if (this.subjectAltNameParam == other.subjectAltNameParam) {
            return true;
        }
        if (this.subjectAltNameParam == null) {
            return false;
        }
        if (other.subjectAltNameParam == null) {
            return false;
        }
        if (!this.subjectAltNameParam.getSubjectAltNameFormat().equals(other.subjectAltNameParam.getSubjectAltNameFormat())) {
            return false;
        }
        return this.subjectAltNameParam.getSubjectAltNameData().equals(other.subjectAltNameParam.getSubjectAltNameData());
    }

}
