/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.util;

import java.net.StandardProtocolFamily;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.pki.NodeEntityCategory;
import com.ericsson.oss.services.security.nscs.command.enrollmentinfo.OtpConfigurationParameters;

public class EnrollingInformation {

    private final String nodeFdn;
    private final String entityProfileName;
    private BaseSubjectAltNameDataType subjectAltName;
    private SubjectAltNameFormat subjectAltNameFormat;
    private final EnrollmentMode enrollmentMode;
    private NodeModelInformation modelInfo;
    private final AlgorithmKeys keySize;
    private final NodeEntityCategory category;
    private final String commonName;
    private StandardProtocolFamily ipVersion;
    private OtpConfigurationParameters otpConfigurationParameters;

    /**
     * Constructor with all final attributes only, the missing not final attributes shall be configured with the related setters. This to avoid
     * SonarQube issue due to excessive number of parameters in constructors.
     * 
     * @param nodeFdn
     *            the node FDN.
     * @param entityProfileName
     *            the entity profile name.
     * @param enrollmentMode
     *            the enrollment mode.
     * @param keySize
     *            the algorithm and key size.
     * @param category
     *            the node entity category.
     * @param commonName
     *            the common name.
     */
    public EnrollingInformation(final String nodeFdn, final String entityProfileName, final EnrollmentMode enrollmentMode,
            final AlgorithmKeys keySize, final NodeEntityCategory category, final String commonName) {
        super();
        this.nodeFdn = nodeFdn;
        this.entityProfileName = entityProfileName;
        this.enrollmentMode = enrollmentMode;
        this.keySize = keySize;
        this.category = category;
        this.commonName = commonName;
        this.subjectAltName = null;
        this.subjectAltNameFormat = null;
        this.modelInfo = null;
        this.ipVersion = null;
        this.otpConfigurationParameters = null;
    }

    /**
     * @return the nodeFdn
     */
    public String getNodeFdn() {
        return nodeFdn;
    }

    /**
     * @return the entityProfileName
     */
    public String getEntityProfileName() {
        return entityProfileName;
    }

    /**
     * @return the subjectAltName
     */
    public BaseSubjectAltNameDataType getSubjectAltName() {
        return subjectAltName;
    }

    /**
     * @param subjectAltName
     *            the subjectAltName to set.
     */
    public void setSubjectAltName(final BaseSubjectAltNameDataType subjectAltName) {
        this.subjectAltName = subjectAltName;
    }

    /**
     * @return the subjectAltNameFormat
     */
    public SubjectAltNameFormat getSubjectAltNameFormat() {
        return subjectAltNameFormat;
    }

    /**
     * @param subjectAltNameFormat
     *            the subjectAltNameFormat to set.
     */
    public void setSubjectAltNameFormat(final SubjectAltNameFormat subjectAltNameFormat) {
        this.subjectAltNameFormat = subjectAltNameFormat;
    }

    /**
     * @return the enrollmentMode
     */
    public EnrollmentMode getEnrollmentMode() {
        return enrollmentMode;
    }

    /**
     * @return the modelInfo
     */
    public NodeModelInformation getModelInfo() {
        return modelInfo;
    }

    /**
     * @param modelInformation
     *            the modelInformation to set
     */
    public void setModelInfo(final NodeModelInformation modelInformation) {
        this.modelInfo = modelInformation;
    }

    /**
     * @return the keySize
     */
    public AlgorithmKeys getKeySize() {
        return keySize;
    }

    /**
     * @return the category
     */
    public NodeEntityCategory getCategory() {
        return category;
    }

    /**
     * @return the commonName
     */
    public String getCommonName() {
        return commonName;
    }

    /**
     * @return the ipVersion
     */
    public StandardProtocolFamily getIpVersion() {
        return ipVersion;
    }

    /**
     * @param ipVersion
     *            the ipVersion to set
     */
    public void setIpVersion(StandardProtocolFamily ipVersion) {
        this.ipVersion = ipVersion;
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

    /**
     * Get OTP count.
     * 
     * @return the OTP count.
     */
    public Integer getOtpCount() {
        return (this.getOtpConfigurationParameters() == null || this.getOtpConfigurationParameters().getOtpCount() == null) ? null
                : this.getOtpConfigurationParameters().getOtpCount();
    }

    /**
     * Get OTP validity period in minutes.
     * 
     * @return the OTP validity period in minutes.
     */
    public Integer getOtpValidityPeriodInMinutes() {
        return (this.getOtpConfigurationParameters() == null || this.getOtpConfigurationParameters().getOtpValidityPeriodInMinutes() == null) ? null
                : this.getOtpConfigurationParameters().getOtpValidityPeriodInMinutes();
    }

    @Override
    public String toString() {
        final String dataFormat = "nodeFdn [%s], " + "entityProfileName [%s], " + "subjectAltName [%s], " + "subjectAltNameFormat [%s], "
                + "enrollmentMode [%s], " + "modelInfo [%s], " + "keySize [%s], " + "category [%s], " + "commonName [%s]" + "ipVersion [%s]"
                + "otpCount [%s]" + "otpValidity [%s]";

        return String.format(dataFormat, this.getNodeFdn(), this.getEntityProfileName(), this.getSubjectAltName(),
                (this.getSubjectAltNameFormat() == null) ? "null" : this.getSubjectAltNameFormat().name(),
                (this.getEnrollmentMode() == null) ? "null" : this.getEnrollmentMode().name(),
                (this.getModelInfo() == null) ? "null" : this.getModelInfo().toString(),
                (this.getKeySize() == null) ? "null" : this.getKeySize().name(), (this.getCategory() == null) ? "null" : this.getCategory().name(),
                (this.getCommonName() == null) ? "null" : this.getCommonName(), (this.getIpVersion() == null) ? "null" : this.getIpVersion(),
                getOtpCount(), getOtpValidityPeriodInMinutes());
    }

}
