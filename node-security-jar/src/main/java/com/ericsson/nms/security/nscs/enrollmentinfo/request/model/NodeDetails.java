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
package com.ericsson.nms.security.nscs.enrollmentinfo.request.model;

import java.io.Serializable;
import java.net.StandardProtocolFamily;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.ericsson.nms.security.nscs.api.exception.DuplicateNodeNamesException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.enrollmentinfo.utility.EnrollmentInfoConstants;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 *      &lt;complexType name="NodeType" minOccurs="0" maxOccurs="unbounded">
 *          &lt;sequence>
 *                 &lt;element name="nodeFdn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1"
 *                                 nillable="false"/>
 *                 &lt;element name="certType" type="{http://www.w3.org/2001/XMLSchema}string"  minOccurs="0"
 *                                 maxOccurs="1"/>
 *                 &lt;element name="entityProfileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 *                 &lt;element name="keySize" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 *                 &lt;element name="commonName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 *                 &lt;element name="enrollmentMode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 *                 &lt;element name="subjectAltName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 *                 &lt;element name="subjectAltNameType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 *                 &lt;element name="ipVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 *                 &lt;element name="otpCount" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1"/>
 *                 &lt;element name="otpValidityPeriodInMinutes" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1"/>
 *          &lt;/sequence>
 *       &lt;/complexType>
 * </pre>
 * 
 * @author tcsmave
 * 
 */
@XmlRootElement(name = "Node")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Node", propOrder = { "nodeFdn", "certType", "entityProfileName", "keySize", "commonName", "enrollmentMode", "subjectAltName",
        "subjectAltNameType", "ipVersion", "otpCount", "otpValidityPeriodInMinutes" })
public class NodeDetails implements Serializable {

    private static final long serialVersionUID = 3520535139605199308L;

    @XmlElement(required = true)
    private String nodeFdn;

    private String certType;

    private String entityProfileName;

    private String keySize;

    private String commonName;

    private String enrollmentMode;

    private String subjectAltName;

    private String subjectAltNameType;

    private StandardProtocolFamily ipVersion;

    private Integer otpCount;

    private Integer otpValidityPeriodInMinutes;

    public StandardProtocolFamily getIpVersion() {
        return ipVersion;
    }

    public void setIpVersion(final StandardProtocolFamily ipVersion) {
        this.ipVersion = ipVersion;
    }
    public String getNodeFdn() {
        return nodeFdn;
    }

    public void setNodeFdn(final String nodeFdn) {
        this.nodeFdn = nodeFdn;
    }

    public String getEntityProfileName() {
        return entityProfileName;
    }

    public String getCertType() {
        return certType;
    }

    public void setCertType(final String certType) {
        this.certType = certType;
    }

    public void setEntityProfileName(final String entityProfileName) {
        this.entityProfileName = entityProfileName;
    }

    public String getEnrollmentMode() {
        return enrollmentMode;
    }

    public void setEnrollmentMode(final String enrollmentMode) {
        this.enrollmentMode = enrollmentMode;
    }

    public String getSubjectAltName() {
        return subjectAltName;
    }

    public void setSubjectAltName(String value) {
        this.subjectAltName = value;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(final String commonName) {
        this.commonName = commonName;
    }

    public String getSubjectAltNameType() {
        return subjectAltNameType;
    }

    public void setSubjectAltNameType(String value) {
        this.subjectAltNameType = value;
    }

    public String getKeySize() {
        return keySize;
    }

    public void setKeySize(final String keySize) {
        this.keySize = keySize;
    }

    /**
     * @return the otpCount
     */
    public Integer getOtpCount() {
        return otpCount;
    }

    /**
     * @param otpCount
     *            the otpCount to set
     */
    public void setOtpCount(final Integer otpCount) {
        this.otpCount = otpCount;
    }

    /**
     * @return the otpValidityPeriodInMinutes
     */
    public Integer getOtpValidityPeriodInMinutes() {
        return otpValidityPeriodInMinutes;
    }

    /**
     * @param otpValidityPeriodInMinutes
     *            the otpValidityPeriodInMinutes to set
     */
    public void setOtpValidityPeriodInMinutes(final Integer otpValidityPeriodInMinutes) {
        this.otpValidityPeriodInMinutes = otpValidityPeriodInMinutes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((certType == null) ? 0 : certType.hashCode());
        result = prime * result + ((entityProfileName == null) ? 0 : entityProfileName.hashCode());
        result = prime * result + ((keySize == null) ? 0 : keySize.hashCode());
        result = prime * result + ((commonName == null) ? 0 : commonName.hashCode());
        result = prime * result + ((enrollmentMode == null) ? 0 : enrollmentMode.hashCode());
        result = prime * result + ((nodeFdn == null) ? 0 : nodeFdn.hashCode());
        result = prime * result + ((subjectAltName == null) ? 0 : subjectAltName.hashCode());
        result = prime * result + ((subjectAltNameType == null) ? 0 : subjectAltNameType.hashCode());
        result = prime * result + ((ipVersion == null) ? 0 : ipVersion.hashCode());
        result = prime * result + ((otpCount == null) ? 0 : otpCount.hashCode());
        result = prime * result + ((otpValidityPeriodInMinutes == null) ? 0 : otpValidityPeriodInMinutes.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NodeDetails other = (NodeDetails) obj;
        if (!equalsNodeFdn(other)) {
            return false;
        }
        if (!equalsCertType(other)) {
            return false;
        }
        if (entityProfileName == null) {
            if (other.entityProfileName != null)
                return false;
        } else if (!entityProfileName.equals(other.entityProfileName))
            return false;
        if (keySize == null) {
            if (other.keySize != null)
                return false;
        } else if (!keySize.equals(other.keySize))
            return false;
        if (commonName == null) {
            if (other.commonName != null)
                return false;
        } else if (!commonName.equals(other.commonName))
            return false;
        if (enrollmentMode == null) {
            if (other.enrollmentMode != null)
                return false;
        } else if (!enrollmentMode.equals(other.enrollmentMode))
            return false;
        if (!equalsSubjectAltNameParams(other)) {
            return false;
        }
        if (ipVersion == null) {
            if (other.ipVersion != null)
                return false;
        } else if (!ipVersion.equals(other.ipVersion))
            return false;
        return equalsOtpParams(other);
    }

    /**
     * Compare the given object from a referred end entity point of view.
     * 
     * If the given object is not null and not equal and if the node name or FDN refers to the same node and the certificate type is the same
     * (conflicting duplicates), a {@link DuplicateNodeNamesException} is thrown because they refer to the same end entity but with different
     * parameters.
     * 
     * @param o
     *            the object to compare.
     * @return 0 if the object is the same.
     * @throws {@link
     *             DuplicateNodeNamesException} if the object is not null and not equal and the node name or FDN refers to the same node and the
     *             certificate type is the same (conflicting duplicates).
     */
    public int compareEndEntity(final NodeDetails o) {
        if (this.equals(o)) {
            return 0;
        }
        if (o == null) {
            return 1;
        }
        if (equalsNodeFdn(o) && equalsCertType(o)) {
            final String errorMsg = String.format("Conflicting duplicates for node %s, cert type %s", nodeFdn, certType);
            throw new DuplicateNodeNamesException(errorMsg);
        }
        final int compareNodeFdn = nodeFdn.compareTo(o.nodeFdn);
        if (compareNodeFdn != 0) {
            return compareNodeFdn;
        }
        return compareCertType(o);
    }

    /**
     * Check if nodeFdn attribute refers to the same node.
     * 
     * The attribute can contain both node FDN (e.g. NetworkElement=<nodename>, VirtualNetworkFunction=<nodename>, ...) and node name (e.g.
     * <nodename>, intended as NetworkElement=<nodename>).
     * 
     * @param other
     *            the other not null object.
     * @return true if the nodeFdn attributes refer to the same node, false otherwise.
     */
    private boolean equalsNodeFdn(final NodeDetails other) {
        if (nodeFdn == null) {
            if (other.nodeFdn != null) {
                return false;
            }
        } else if (other.nodeFdn == null) {
            return false;
        } else if (!nodeFdn.equals(other.nodeFdn)) {
            final NodeReference nodeReference = new NodeRef(nodeFdn);
            final NodeReference otherNodeReference = new NodeRef(other.nodeFdn);
            if (!nodeReference.equals(otherNodeReference)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if certType attribute refers to the same certificate type.
     * 
     * The attribute can contain both certificate type (e.g. OAM, IPSEC, ...) and null (intended as OAM).
     * 
     * @param other
     *            the other not null object.
     * @return true if the certType attributes refer to the same certificate type, false otherwise.
     */
    private boolean equalsCertType(final NodeDetails other) {
        final String thisCertType = certType != null ? certType : EnrollmentInfoConstants.OAM;
        final String otherCertType = other.certType != null ? other.certType : EnrollmentInfoConstants.OAM;
        return thisCertType.equals(otherCertType);
    }

    /**
     * Compare the given object from a certificate type point of view.
     * 
     * The certType attribute can contain both certificate type (e.g. OAM, IPSEC, ...) and null (intended as OAM).
     * 
     * @param other
     *            the other not null object.
     * @return 0 if the certType attributes refer to the same certificate type, false otherwise.
     */
    private int compareCertType(final NodeDetails other) {
        final String thisCertType = certType != null ? certType : EnrollmentInfoConstants.OAM;
        final String otherCertType = other.certType != null ? other.certType : EnrollmentInfoConstants.OAM;
        return thisCertType.compareTo(otherCertType);
    }

    /**
     * Check if SAN configuration parameters are equal.
     * 
     * @param other
     *            the other not null object.
     * @return true if SAN parameters are equal, false otherwise.
     */
    private boolean equalsSubjectAltNameParams(NodeDetails other) {
        if (subjectAltName == null) {
            if (other.subjectAltName != null)
                return false;
        } else if (!subjectAltName.equals(other.subjectAltName))
            return false;
        if (subjectAltNameType == null) {
            if (other.subjectAltNameType != null)
                return false;
        } else if (!subjectAltNameType.equals(other.subjectAltNameType))
            return false;
        return true;
    }

    /**
     * Check if OTP configuration parameters are equal.
     * 
     * @param other
     *            the other not null object.
     * @return true if OTP parameters are equal, false otherwise.
     */
    private boolean equalsOtpParams(final NodeDetails other) {
        if (otpCount == null) {
            if (other.otpCount != null) {
                return false;
            }
        } else if (!otpCount.equals(other.otpCount)) {
            return false;
        }
        if (otpValidityPeriodInMinutes == null) {
            if (other.otpValidityPeriodInMinutes != null) {
                return false;
            }
        } else if (!otpValidityPeriodInMinutes.equals(other.otpValidityPeriodInMinutes)) {
            return false;
        }
        return true;
    }
}
