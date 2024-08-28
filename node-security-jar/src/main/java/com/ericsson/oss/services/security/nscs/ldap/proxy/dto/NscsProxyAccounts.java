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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Auxiliary class to model a list of proxy accounts.
 */
@XmlRootElement(name = "proxyAccounts")
@XmlAccessorType(XmlAccessType.FIELD)
public class NscsProxyAccounts implements Serializable {

    private static final long serialVersionUID = 3220198348429171993L;

    @XmlElement(name = "proxyAccount")
    private List<NscsProxyAccount> proxyAccounts;

    /**
     * @return the proxyAccounts
     */
    public List<NscsProxyAccount> getProxyAccounts() {
        if (proxyAccounts == null) {
            proxyAccounts = new ArrayList<>();
        }
        return new ArrayList<>(this.proxyAccounts);
    }

    /**
     * @param proxyAccounts
     *            the proxyAccounts to set
     */
    public void setProxyAccounts(final List<NscsProxyAccount> proxyAccounts) {
        if (proxyAccounts != null) {
            List<NscsProxyAccount> localProxyAccounts = new ArrayList<>(proxyAccounts);
            this.proxyAccounts = Collections.unmodifiableList(localProxyAccounts);
        }
    }

}
