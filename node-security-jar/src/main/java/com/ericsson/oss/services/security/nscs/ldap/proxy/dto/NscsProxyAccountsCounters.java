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
import javax.xml.bind.annotation.XmlType;

/**
 * Auxiliary class to model global counters related to proxy accounts.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProxyAccountsCounters", propOrder = { "numOfProxyAccounts", "numOfRequestedProxyAccounts", "numOfLegacyProxyAccounts",
        "numOfRequestedLegacyProxyAccounts" })
public class NscsProxyAccountsCounters implements Serializable {

    private static final long serialVersionUID = -7720740376162028098L;

    @XmlElement
    private Integer numOfProxyAccounts;

    @XmlElement
    private Integer numOfRequestedProxyAccounts;

    @XmlElement
    private Integer numOfLegacyProxyAccounts;

    @XmlElement
    private Integer numOfRequestedLegacyProxyAccounts;

    /**
     * @return the numOfProxyAccounts
     */
    public Integer getNumOfProxyAccounts() {
        return numOfProxyAccounts;
    }

    /**
     * @param numOfProxyAccounts
     *            the numOfProxyAccounts to set
     */
    public void setNumOfProxyAccounts(final Integer numOfProxyAccounts) {
        this.numOfProxyAccounts = numOfProxyAccounts;
    }

    /**
     * @return the numOfRequestedProxyAccounts
     */
    public Integer getNumOfRequestedProxyAccounts() {
        return numOfRequestedProxyAccounts;
    }

    /**
     * @param numOfRequestedProxyAccounts
     *            the numOfRequestedProxyAccounts to set
     */
    public void setNumOfRequestedProxyAccounts(final Integer numOfRequestedProxyAccounts) {
        this.numOfRequestedProxyAccounts = numOfRequestedProxyAccounts;
    }

    /**
     * @return the numOfLegacyProxyAccounts
     */
    public Integer getNumOfLegacyProxyAccounts() {
        return numOfLegacyProxyAccounts;
    }

    /**
     * @param numOfLegacyProxyAccounts
     *            the numOfLegacyProxyAccounts to set
     */
    public void setNumOfLegacyProxyAccounts(final Integer numOfLegacyProxyAccounts) {
        this.numOfLegacyProxyAccounts = numOfLegacyProxyAccounts;
    }

    /**
     * @return the numOfRequestedLegacyProxyAccounts
     */
    public Integer getNumOfRequestedLegacyProxyAccounts() {
        return numOfRequestedLegacyProxyAccounts;
    }

    /**
     * @param numOfRequestedLegacyProxyAccounts
     *            the numOfRequestedLegacyProxyAccounts to set
     */
    public void setNumOfRequestedLegacyProxyAccounts(final Integer numOfRequestedLegacyProxyAccounts) {
        this.numOfRequestedLegacyProxyAccounts = numOfRequestedLegacyProxyAccounts;
    }

}
