/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2022
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.ldap.proxy.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Auxiliary class to model a proxy account.
 */
@XmlRootElement(name = "proxyAccount")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProxyAccount", propOrder = { "dn", "adminStatus", "createDate", "lastLoginDate" })
public class NscsProxyAccount implements Serializable {

    private static final long serialVersionUID = 4455461638731132319L;

    @XmlElement(required = true)
    private String dn;
    @XmlElement(required = false)
    private String adminStatus;
    @XmlElement(required = false)
    private String createDate;
    @XmlElement(required = false)
    private String lastLoginDate;

    /**
     * @return the dn
     */
    public String getDn() {
        return dn;
    }

    /**
     * @param dn
     *            the dn to set
     */
    public void setDn(final String dn) {
        this.dn = dn;
    }

    /**
     * @return the adminStatus
     */
    public String getAdminStatus() {
        return adminStatus;
    }

    /**
     * @param adminStatus
     *            the adminStatus to set
     */
    public void setAdminStatus(final String adminStatus) {
        this.adminStatus = adminStatus;
    }

    /**
     * @return the createDate
     */
    public String getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate
     *            the createDate to set
     */
    public void setCreateDate(final String createDate) {
        this.createDate = createDate;
    }

    /**
     * @return the lastLoginDate
     */
    public String getLastLoginDate() {
        return lastLoginDate;
    }

    /**
     * @param lastLoginDate
     *            the lastLoginDate to set
     */
    public void setLastLoginDate(final String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

}
