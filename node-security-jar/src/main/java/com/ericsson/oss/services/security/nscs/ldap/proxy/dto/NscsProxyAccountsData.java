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
 * Auxiliary class to model proxy accounts data.
 */
@XmlRootElement(name = "proxyAccountsData")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "proxyAccountsCounters", "proxyAccounts" })
public class NscsProxyAccountsData implements Serializable {

    private static final long serialVersionUID = 6784691911004319751L;

    @XmlElement(name = "proxyAccountsCounters")
    private NscsProxyAccountsCounters proxyAccountsCounters;
    @XmlElement(name = "proxyAccounts")
    private NscsProxyAccounts proxyAccounts;

    /**
     * @return the proxyAccountsCounters
     */
    public NscsProxyAccountsCounters getProxyAccountsCounters() {
        return proxyAccountsCounters;
    }

    /**
     * @param proxyAccountsCounters
     *            the proxyAccountsCounters to set
     */
    public void setProxyAccountsCounters(final NscsProxyAccountsCounters proxyAccountsCounters) {
        this.proxyAccountsCounters = proxyAccountsCounters;
    }

    /**
     * @return the proxyAccounts
     */
    public NscsProxyAccounts getProxyAccounts() {
        return proxyAccounts;
    }

    /**
     * @param proxyAccounts
     *            the proxyAccounts to set
     */
    public void setProxyAccounts(final NscsProxyAccounts proxyAccounts) {
        this.proxyAccounts = proxyAccounts;
    }
}
