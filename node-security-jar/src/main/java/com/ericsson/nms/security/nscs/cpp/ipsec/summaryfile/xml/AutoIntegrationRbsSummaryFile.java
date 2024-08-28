//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.10.17 at 11:05:52 AM IST 
//


package com.ericsson.nms.security.nscs.cpp.ipsec.summaryfile.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="Format">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="revision" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="F"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ConfigurationFiles">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="siteBasicFilePath" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="ipForOamSettingFilePath" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="siteEquipmentFilePath" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="licensingKeyFilePath" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="upgradePackageFilePath" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="initialSecurityConfigurationFilePath" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="siteBasicFileHash" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="ipForOamSettingFileHash" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="siteEquipmentFileHash" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "AutoIntegrationRbsSummaryFile")
public class AutoIntegrationRbsSummaryFile {

    @XmlElement(name = "Format", required = true)
    protected AutoIntegrationRbsSummaryFile.Format format;
    @XmlElement(name = "ConfigurationFiles", required = true)
    protected AutoIntegrationRbsSummaryFile.ConfigurationFiles configurationFiles;

    /**
     * Gets the value of the format property.
     * 
     * @return
     *     possible object is
     *     {@link AutoIntegrationRbsSummaryFile.Format }
     *     
     */
    public AutoIntegrationRbsSummaryFile.Format getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     * 
     * @param value
     *     allowed object is
     *     {@link AutoIntegrationRbsSummaryFile.Format }
     *     
     */
    public void setFormat(AutoIntegrationRbsSummaryFile.Format value) {
        this.format = value;
    }

    /**
     * Gets the value of the configurationFiles property.
     * 
     * @return
     *     possible object is
     *     {@link AutoIntegrationRbsSummaryFile.ConfigurationFiles }
     *     
     */
    public AutoIntegrationRbsSummaryFile.ConfigurationFiles getConfigurationFiles() {
        return configurationFiles;
    }

    /**
     * Sets the value of the configurationFiles property.
     * 
     * @param value
     *     allowed object is
     *     {@link AutoIntegrationRbsSummaryFile.ConfigurationFiles }
     *     
     */
    public void setConfigurationFiles(AutoIntegrationRbsSummaryFile.ConfigurationFiles value) {
        this.configurationFiles = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="siteBasicFilePath" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="ipForOamSettingFilePath" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="siteEquipmentFilePath" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="licensingKeyFilePath" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="upgradePackageFilePath" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="initialSecurityConfigurationFilePath" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="siteBasicFileHash" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="ipForOamSettingFileHash" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="siteEquipmentFileHash" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ConfigurationFiles {

        @XmlAttribute(name = "siteBasicFilePath")
        protected String siteBasicFilePath;
        @XmlAttribute(name = "ipForOamSettingFilePath")
        protected String ipForOamSettingFilePath;
        @XmlAttribute(name = "siteEquipmentFilePath")
        protected String siteEquipmentFilePath;
        @XmlAttribute(name = "licensingKeyFilePath")
        protected String licensingKeyFilePath;
        @XmlAttribute(name = "upgradePackageFilePath")
        protected String upgradePackageFilePath;
        @XmlAttribute(name = "initialSecurityConfigurationFilePath")
        protected String initialSecurityConfigurationFilePath;
        @XmlAttribute(name = "siteBasicFileHash")
        protected String siteBasicFileHash;
        @XmlAttribute(name = "ipForOamSettingFileHash")
        protected String ipForOamSettingFileHash;
        @XmlAttribute(name = "siteEquipmentFileHash")
        protected String siteEquipmentFileHash;

        /**
         * Gets the value of the siteBasicFilePath property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSiteBasicFilePath() {
            return siteBasicFilePath;
        }

        /**
         * Sets the value of the siteBasicFilePath property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSiteBasicFilePath(String value) {
            this.siteBasicFilePath = value;
        }

        /**
         * Gets the value of the ipForOamSettingFilePath property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getIpForOamSettingFilePath() {
            return ipForOamSettingFilePath;
        }

        /**
         * Sets the value of the ipForOamSettingFilePath property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setIpForOamSettingFilePath(String value) {
            this.ipForOamSettingFilePath = value;
        }

        /**
         * Gets the value of the siteEquipmentFilePath property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSiteEquipmentFilePath() {
            return siteEquipmentFilePath;
        }

        /**
         * Sets the value of the siteEquipmentFilePath property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSiteEquipmentFilePath(String value) {
            this.siteEquipmentFilePath = value;
        }

        /**
         * Gets the value of the licensingKeyFilePath property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLicensingKeyFilePath() {
            return licensingKeyFilePath;
        }

        /**
         * Sets the value of the licensingKeyFilePath property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLicensingKeyFilePath(String value) {
            this.licensingKeyFilePath = value;
        }

        /**
         * Gets the value of the upgradePackageFilePath property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUpgradePackageFilePath() {
            return upgradePackageFilePath;
        }

        /**
         * Sets the value of the upgradePackageFilePath property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUpgradePackageFilePath(String value) {
            this.upgradePackageFilePath = value;
        }

        /**
         * Gets the value of the initialSecurityConfigurationFilePath property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getInitialSecurityConfigurationFilePath() {
            return initialSecurityConfigurationFilePath;
        }

        /**
         * Sets the value of the initialSecurityConfigurationFilePath property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setInitialSecurityConfigurationFilePath(String value) {
            this.initialSecurityConfigurationFilePath = value;
        }

        /**
         * Gets the value of the siteBasicFileHash property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSiteBasicFileHash() {
            return siteBasicFileHash;
        }

        /**
         * Sets the value of the siteBasicFileHash property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSiteBasicFileHash(String value) {
            this.siteBasicFileHash = value;
        }

        /**
         * Gets the value of the ipForOamSettingFileHash property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getIpForOamSettingFileHash() {
            return ipForOamSettingFileHash;
        }

        /**
         * Sets the value of the ipForOamSettingFileHash property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setIpForOamSettingFileHash(String value) {
            this.ipForOamSettingFileHash = value;
        }

        /**
         * Gets the value of the siteEquipmentFileHash property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSiteEquipmentFileHash() {
            return siteEquipmentFileHash;
        }

        /**
         * Sets the value of the siteEquipmentFileHash property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSiteEquipmentFileHash(String value) {
            this.siteEquipmentFileHash = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="revision" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;enumeration value="F"/>
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
    @XmlType(name = "")
    public static class Format {

        @XmlAttribute(name = "revision", required = true)
        protected String revision;

        /**
         * Gets the value of the revision property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRevision() {
            return revision;
        }

        /**
         * Sets the value of the revision property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRevision(String value) {
            this.revision = value;
        }

    }

}
