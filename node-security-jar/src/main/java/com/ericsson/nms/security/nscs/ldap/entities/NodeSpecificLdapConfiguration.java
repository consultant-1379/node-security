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
package com.ericsson.nms.security.nscs.ldap.entities;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.ericsson.nms.security.nscs.api.exception.DuplicateNodeNamesException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;

/**
 * <p>
 * This class acts as both XML Object and also DTO for holding the LDAP
 * Configurations.
 * 
 * The following schema fragment specifies the XSD Schema of this class.
 * 
 * <pre>
 *      &lt;complexType name="NodeType">
 *          &lt;sequence>
 *                 &lt;element name="NodeFdn" type="xs:string"/>
 *                 &lt;element name="tlsMode" type="tlsModeType"/>
 *                 &lt;element name="userLabel" type="xs:string"/>
 *                 &lt;element name="useTls" type="xs:boolean"/>
 *          &lt;/sequence>
 *       &lt;/complexType>
 * </pre>
 * 
 */
@XmlRootElement(name = "Node")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Node", propOrder = { "nodeFdn", "tlsMode", "userLabel", "useTls" })
public class NodeSpecificLdapConfiguration implements Serializable {

    private static final long serialVersionUID = 4979641580956471563L;

    @XmlElement(required = true)
    private String nodeFdn;

    private String tlsMode;

    private String userLabel;

    private Boolean useTls;

    @XmlTransient
    private String baseDN;

    @XmlTransient
    private String bindDN;

    @XmlTransient
    private String bindPassword;

    @XmlTransient
    private String primaryLdapServerIPAddress;

    @XmlTransient
    private String secondaryLdapServerIPAddress;

    @XmlTransient
    private Integer ldapServerPort;

    public String getNodeFdn() {
        return nodeFdn;
    }

    public void setNodeFdn(String nodeFdn) {
        this.nodeFdn = nodeFdn;
    }

    public String getTlsMode() {
        return tlsMode;
    }

    public void setTlsMode(String tlsMode) {
        this.tlsMode = tlsMode;
    }

    public String getUserLabel() {
        return userLabel;
    }

    public void setUserLabel(String userLabel) {
        this.userLabel = userLabel;
    }

    public Boolean getUseTls() {
        return useTls;
    }

    public void setUseTls(Boolean useTls) {
        this.useTls = useTls;
    }

    public String getBaseDN() {
        return baseDN;
    }

    public void setBaseDN(String baseDN) {
        this.baseDN = baseDN;
    }

    public String getBindDN() {
        return bindDN;
    }

    public void setBindDN(String bindDN) {
        this.bindDN = bindDN;
    }

    public String getBindPassword() {
        return bindPassword;
    }

    public void setBindPassword(String bindPassword) {
        this.bindPassword = bindPassword;
    }

    public String getPrimaryLdapServerIPAddress() {
        return primaryLdapServerIPAddress;
    }

    public void setPrimaryLdapServerIPAddress(String primaryLdapServerIPAddress) {
        this.primaryLdapServerIPAddress = primaryLdapServerIPAddress;
    }

    public String getSecondaryLdapServerIPAddress() {
        return secondaryLdapServerIPAddress;
    }

    public void setSecondaryLdapServerIPAddress(String secondaryLdapServerIPAddress) {
        this.secondaryLdapServerIPAddress = secondaryLdapServerIPAddress;
    }

    public Integer getLdapServerPort() {
        return ldapServerPort;
    }

    public void setLdapServerPort(Integer ldapServerPort) {
        this.ldapServerPort = ldapServerPort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NodeSpecificLdapConfiguration that = (NodeSpecificLdapConfiguration) o;

        if (!nodeFdn.equals(that.nodeFdn)) {
            return false;
        }
        if (!tlsMode.equals(that.tlsMode)) {
            return false;
        }
        if (!userLabel.equals(that.userLabel)) {
            return false;
        }
        if (!useTls.equals(that.useTls)) {
            return false;
        }
        if (!baseDN.equals(that.baseDN)) {
            return false;
        }
        if (!bindDN.equals(that.bindDN)) {
            return false;
        }
        if (!bindPassword.equals(that.bindPassword)) {
            return false;
        }
        if (!primaryLdapServerIPAddress.equals(that.primaryLdapServerIPAddress)) {
            return false;
        }
        if (!secondaryLdapServerIPAddress.equals(that.secondaryLdapServerIPAddress)) {
            return false;
        }
        return ldapServerPort.equals(that.ldapServerPort);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nodeFdn == null) ? 0 : nodeFdn.hashCode());
        result = prime * result + ((tlsMode == null) ? 0 : tlsMode.hashCode());
        result = prime * result + ((userLabel == null) ? 0 : userLabel.hashCode());
        result = prime * result + ((useTls == null) ? 0 : useTls.hashCode());

        result = prime * result + ((baseDN == null) ? 0 : baseDN.hashCode());
        result = prime * result + ((bindDN == null) ? 0 : bindDN.hashCode());
        result = prime * result + ((bindPassword == null) ? 0 : bindPassword.hashCode());
        result = prime * result + ((primaryLdapServerIPAddress == null) ? 0 : primaryLdapServerIPAddress.hashCode());
        result = prime * result + ((secondaryLdapServerIPAddress == null) ? 0 : secondaryLdapServerIPAddress.hashCode());
        result = prime * result + ((ldapServerPort == null) ? 0 : ldapServerPort.hashCode());
        return result;
    }

    /**
     * Compare the given object for the user-defined LDAP parameters only.
     * 
     * The given object is equal if all the user-defined LDAP parameters are equal.
     * 
     * If the user-defined parameters are not equal and the node name or FDN refers to the same node (conflicting duplicates), a
     * {@link DuplicateNodeNamesException} is thrown.
     * 
     * @param o
     *            the object to compare.
     * @return 0 if the user-defined LDAP parameters are the same.
     * @throws {@link
     *             DuplicateNodeNamesException} if the user-defined parameters are not equal and the node name or FDN refers to the same node
     *             (conflicting duplicates).
     */
    public int compareUserDefinedLdapParams(final NodeSpecificLdapConfiguration o) {
        if (this.equalsUserDefinedLdapParams(o)) {
            return 0;
        }
        if (o == null) {
            return 1;
        }
        final NodeReference nodeReference = new NodeRef(nodeFdn);
        final NodeReference otherNodeReference = new NodeRef(o.nodeFdn);
        if (nodeReference.equals(otherNodeReference)) {
            final String errorMsg = String.format("Conflicting duplicates for node %s", nodeReference.getName());
            throw new DuplicateNodeNamesException(errorMsg);
        }
        return nodeFdn.compareTo(o.nodeFdn);
    }

    private boolean equalsUserDefinedLdapParams(final Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NodeSpecificLdapConfiguration that = (NodeSpecificLdapConfiguration) o;

        final NodeReference nodeReference = new NodeRef(nodeFdn);
        final NodeReference otherNodeReference = new NodeRef(that.nodeFdn);
        if (!nodeReference.equals(otherNodeReference)) {
            return false;
        }
        if (!tlsMode.equals(that.tlsMode)) {
            return false;
        }
        if (!userLabel.equals(that.userLabel)) {
            return false;
        }
        return useTls.equals(that.useTls);
    }
}
