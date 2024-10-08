//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.02.21 at 03:38:44 PM GMT 
//
package com.ericsson.nms.security.nscs.iscf.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}dataChallengePassword"/>
 *       &lt;/sequence>
 *       &lt;attribute name="distinguishedName" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="enrollmentServerURL" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="cAFingerprint" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="keyLength">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
 *             &lt;minInclusive value="0"/>
 *             &lt;maxInclusive value="1"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="enrollmentTimeLimit">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
 *             &lt;minInclusive value="120"/>
 *             &lt;maxInclusive value="1800"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "dataChallengePassword"
})
@XmlRootElement(name = "enrollmentData")
public class EnrollmentData {

    @XmlElement(required = true)
    protected ISCFEncryptedContent dataChallengePassword;
    @XmlAttribute(name = "distinguishedName", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String distinguishedName;
    @XmlAttribute(name = "enrollmentServerURL", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String enrollmentServerURL;
    @XmlAttribute(name = "cAFingerprint", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String caFingerprint;
    @XmlAttribute(name = "keyLength")
    protected Integer keyLength;
    @XmlAttribute(name = "enrollmentTimeLimit")
    protected Integer enrollmentTimeLimit;
    @XmlAttribute(name = "enrollmentMode", required = true)
    protected Integer enrollmentMode;
    @XmlAttribute(name = "certificateAuthorityDn")
    @XmlSchemaType(name = "anySimpleType")
    protected String certificateAuthorityDn;

    /**
     * Gets the value of the dataChallengePassword property.
     *
     * @return possible object is {@link ISCFEncryptedContent }
     *
     */
    public ISCFEncryptedContent getDataChallengePassword() {
        return dataChallengePassword;
    }

    /**
     * Sets the value of the dataChallengePassword property.
     *
     * @param value allowed object is {@link ISCFEncryptedContent }
     *
     */
    public void setDataChallengePassword(final ISCFEncryptedContent value) {
        this.dataChallengePassword = value;
    }

    /**
     * Gets the value of the distinguishedName property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getDistinguishedName() {
        return distinguishedName;
    }

    /**
     * Sets the value of the distinguishedName property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setDistinguishedName(final String value) {
        this.distinguishedName = value;
    }

    /**
     * Gets the value of the enrollmentServerURL property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getEnrollmentServerURL() {
        return enrollmentServerURL;
    }

    /**
     * Sets the value of the enrollmentServerURL property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setEnrollmentServerURL(final String value) {
        this.enrollmentServerURL = value;
    }

    /**
     * Gets the value of the caFingerprint property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getCAFingerprint() {
        return caFingerprint;
    }

    /**
     * Sets the value of the caFingerprint property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setCAFingerprint(final String value) {
        this.caFingerprint = value;
    }

    /**
     * Gets the value of the keyLength property.
     *
     * @return possible object is {@link Integer }
     *
     */
    public Integer getKeyLength() {
        return keyLength;
    }

    /**
     * Sets the value of the keyLength property.
     *
     * @param value allowed object is {@link Integer }
     *
     */
    public void setKeyLength(final Integer value) {
        this.keyLength = value;
    }

    /**
     * Gets the value of the enrollmentTimeLimit property.
     *
     * @return possible object is {@link Integer }
     *
     */
    public Integer getEnrollmentTimeLimit() {
        return enrollmentTimeLimit;
    }

    /**
     * Sets the value of the enrollmentTimeLimit property.
     *
     * @param value allowed object is {@link Integer }
     *
     */
    public void setEnrollmentTimeLimit(final Integer value) {
        this.enrollmentTimeLimit = value;
    }

    /**
     * Gets the value of the enrollmentMode property.
     *
     * @return possible object is {@link Integer }
     *
     */
    public Integer getEnrollmentMode() {
        return enrollmentMode;
    }

    /**
     * Sets the value of the enrollmentMode property.
     *
     * @param value allowed object is {@link Integer }
     *
     */
    public void setEnrollmentMode(Integer value) {
        this.enrollmentMode = value;
    }

    /**
     * Gets the value of the certificateAuthorityDn property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getCertificateAuthorityDn() {
        return certificateAuthorityDn;
    }

    /**
     * Sets the value of the certificateAuthorityDn property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setCertificateAuthorityDn(String value) {
        this.certificateAuthorityDn = value;
    }

}
