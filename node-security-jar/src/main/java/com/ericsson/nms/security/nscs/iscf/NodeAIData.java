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

import java.util.*;

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType;
import com.ericsson.nms.security.nscs.api.iscf.IpsecArea;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.nscs.iscf.dto.*;

/**
 * Class encapsulating data needed for generating ISCF XML data
 *
 * @author ealemca
 */
public class NodeAIData {

    private String fdn;
    private String logicalName;
    private SecurityLevel wantedSecLevel;
    private SecurityLevel minimumSecLevel;
    private String logonServerAddress;
    private List<CertFileDto> secLevelCertFileDtos;
    private SecEnrollmentDataDto enrollmentDto;
    private Set<IpsecArea> ipsecAreas;
    private byte[] ric;
    private Set<CertSpec> secLevelCertSpecs;
    private Set<CertSpec> ipsecCertSpecs;
    private BaseSubjectAltNameDataType subjectAltName;
    private SubjectAltNameFormat subjectAltNameFormat;
    private String ipsecUserLabel;
    private Integer ipsecCertExpirWarnTime;
    private IpsecEnrollmentDataDto ipsecEnrollmentDataDto;
    private List<CertFileDto> ipsecCertFileDtos;

    /**
     * Get the value of the distinguished name of the node undergoing Auto Integration
     *
     * @return fdn the distinguished name of the node
     */
    public String getFdn() {
        return fdn;
    }

    /**
     * Set the value of the distinguished name of the node undergoing Auto Integration
     *
     * @param fdn the distinguished name of the node
     */
    public void setFdn(final String fdn) {
        this.fdn = fdn;
    }

    /**
     * Get the value of the logical name
     *
     * @return logicalName the logical name
     */
    public String getLogicalName() {
        return logicalName;
    }

    /**
     * Set the value of the logical name
     *
     * @param logicalName the logical name
     */
    public void setLogicalName(final String logicalName) {
        this.logicalName = logicalName;
    }

    /**
     * Get the value of the minimum security level to be set by the end of
     * Auto Integration
     *
     * @return minimumSecLevel the minimum security level for this Auto Integration process
     */
    public SecurityLevel getMinimumSecLevel() {
        return minimumSecLevel;
    }

    /**
     * Set the value of the minimum security level to be set by the end of
     * Auto Integration
     *
     * @param minimumSecLevel the minimum security level this node can be left in if
     *                        auto integration fails
     */
    public void setMinimumSecLevel(final SecurityLevel minimumSecLevel) {
        this.minimumSecLevel = minimumSecLevel;
    }

    /**
     * Get the value of the desired security level to be set by the end of
     * Auto Integration
     *
     * @return wantedSecLevel the desired security level for this Auto Integration process
     */
    public SecurityLevel getWantedSecLevel() {
        return wantedSecLevel;
    }

    /**
     * Set the value of the desired security level to be set by the end of
     * Auto Integration
     *
     * @param wantedSecLevel the desired security level for this node
     */
    public void setWantedSecLevel(final SecurityLevel wantedSecLevel) {
        this.wantedSecLevel = wantedSecLevel;
    }

    /**
     * Get the value of the URL of the enrollment server
     *
     * @return logonServerAddress the enrollment server URL
     */
    public String getLogonServerAddress() {
        return logonServerAddress;
    }

    /**
     * Set the value of the URL of the enrollment server
     *
     * @param logonServerAddress the URL of the enrollment server
     */
    public void setLogonServerAddress(final String logonServerAddress) {
        this.logonServerAddress = logonServerAddress;
    }

    /**
     * Get the list of certificate file data transfer objects representing the
     * trusted certificates used during Auto Integration
     *
     * @return secLevelCertFileDtos the list of certificate file data transfer objects
     */
    public List<CertFileDto> getSecLevelCertFileDtos() {
        if (secLevelCertFileDtos == null) {
            secLevelCertFileDtos = new ArrayList<>();
        }
        return this.secLevelCertFileDtos;
    }

    /**
     * Get the value of the data transfer object representing the
     * Enrollment Server information
     *
     * @return enrollmentDto the value of the data transfer object representing the
     * Enrollment Server information
     */
    public SecEnrollmentDataDto getEnrollmentDto() {
        return enrollmentDto;
    }

    /**
     * Set the value of the data transfer object representing the
     * Enrollment Server information
     *
     * @param enrollmentDto the data transfer object representing the Enrollment
     *                      Server details
     */
    public void setSecEnrollmentData(final SecEnrollmentDataDto enrollmentDto) {
        this.enrollmentDto = enrollmentDto;
    }

    /**
     * Get the value of the RBS Integrity Code
     *
     * @return ric the value of ric
     */
    public byte[] getRic() {
        return ric;
    }

    /**
     * Set the value of the RBS Integrity Code
     *
     * @param ric new value of ric
     */
    public void setRic(final byte[] ric) {
        this.ric = ric;
    }

    /**
     * Set the value of the IPSEC user label
     *
     * @param ipsecUserLabel the new IPSEC user label
     */
    public void setIpsecUserLabel(final String ipsecUserLabel) {
        this.ipsecUserLabel = ipsecUserLabel;
    }

    /**
     * Set the value of the IPSEC certificate expiry warn time
     *
     * @param ipsecCertExpirWarnTime the new IPSEC certificate expiry warn time
     */
    public void setIpsecCertExpirWarnTime(final Integer ipsecCertExpirWarnTime) {
        this.ipsecCertExpirWarnTime = ipsecCertExpirWarnTime;
    }

    /**
     * Get the value of the IPSEC certificate expiry warn time
     *
     * @return ipsecCertExpirWarnTime the IPSEC certificate expiry warn time
     */
    public Integer getIpsecCertExpirWarnTime() {
        return ipsecCertExpirWarnTime;
    }

    /**
     * Get the value of the IPSEC user label
     *
     * @return ipsecUserLabel the IPSEC user label
     */
    public String getIpsecUserLabel() {
        return ipsecUserLabel;
    }

    /**
     * Get the data transfer object representing the IPSEC Enrollment Server
     * details
     *
     * @return ipsecEnrollmentDataDto the data transfer object representing the IPSEC Enrollment Server
     * details
     */
    public IpsecEnrollmentDataDto getIpsecEnrollmentDataDto() {
        return ipsecEnrollmentDataDto;
    }

    /**
     * Set the data transfer object representing the IPSEC Enrollment Server
     * details
     *
     * @param ipsecEnrollmentDataDto the new data transfer object representing the
     *                               IPSEC Enrollment Server details
     */
    public void setIpsecEnrollmentDataDto(final IpsecEnrollmentDataDto ipsecEnrollmentDataDto) {
        this.ipsecEnrollmentDataDto = ipsecEnrollmentDataDto;
    }

    /**
     * Get the list of data transfer object representing the list of IPSEC certificates
     *
     * @return ipsecCertFileDtos the list data transfer object representing the list of IPSEC certificates
     */
    public List<CertFileDto> getIpsecCertFileDtos() {
        if (ipsecCertFileDtos == null) {
            ipsecCertFileDtos = new ArrayList<>();
        }
        return this.ipsecCertFileDtos;
    }

    /**
     * Get the list of Security Level certificate holders
     *
     * @return secLevelCertSpecs the list of Security Level certificate holders
     */
    public Set<CertSpec> getSecLevelCertSpecs() {
        if(secLevelCertSpecs == null) {
            secLevelCertSpecs = new HashSet<>();
        }
        return this.secLevelCertSpecs;
    }

    /**
     * Get the list of Security Level certificate holders
     *
     * @return secLevelCertSpecs the list of Security Level certificate holders
     */
    public Set<CertSpec> getIpsecCertSpecs() {
        if(ipsecCertSpecs == null) {
            ipsecCertSpecs = new HashSet<>();
        }
        return this.ipsecCertSpecs;
    }

    /**
     * Get the value of the type representing the required IPSEC area
     *
     * @return ipsecArea the value of the type representing the required IPSEC area
     */
    public Set<IpsecArea> getIpsecAreas() {
        if(ipsecAreas == null) {
            ipsecAreas = new HashSet<>();
        }
        return this.ipsecAreas;
    }

    /**
     * Get the 'subjectAltName'
     *
     * @return subjectAltName the value of subjectAltName
     */
    public BaseSubjectAltNameDataType getSubjectAltName() {
        return subjectAltName;
    }

    /**
     * Set the 'subjectAltName'
     *
     * @param subjectAltName the new value of subjectAltName
     */
    public void setSubjectAltName(final BaseSubjectAltNameDataType subjectAltName) {
        this.subjectAltName = subjectAltName;
    }


    /**
     * get the subjectAltNameFormat
     *
     * @return subjectAltNameFormat
     */
    public SubjectAltNameFormat getSubjectAltNameFormat() {
        return subjectAltNameFormat;
    }

    /**
     * set the subjectAltNameFormat
     *
     * @param subjectAltNameFormat
     */
    public void setSubjectAltNameFormat(final SubjectAltNameFormat subjectAltNameFormat) {
        this.subjectAltNameFormat = subjectAltNameFormat;
    }

}
