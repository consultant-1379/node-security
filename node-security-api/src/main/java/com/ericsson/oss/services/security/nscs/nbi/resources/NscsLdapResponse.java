/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2024
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.nbi.resources;

import java.io.Serializable;

public class NscsLdapResponse implements Serializable {

    private static final long serialVersionUID = -198702569851042897L;

    private String tlsPort;
    private String ldapsPort;
    private String ldapIpAddress;
    private String fallbackLdapIpAddress;
    private String bindDn;
    private String bindPassword;
    private String baseDn;

    public NscsLdapResponse() {
        super();
    }

    /**
     * @return the tlsPort
     */
    public String getTlsPort() {
        return tlsPort;
    }

    /**
     * @param tlsPort
     *            the tlsPort to set
     */
    public void setTlsPort(final String tlsPort) {
        this.tlsPort = tlsPort;
    }

    /**
     * @return the ldapsPort
     */
    public String getLdapsPort() {
        return ldapsPort;
    }

    /**
     * @param ldapsPort
     *            the ldapsPort to set
     */
    public void setLdapsPort(final String ldapsPort) {
        this.ldapsPort = ldapsPort;
    }

    /**
     * @return the ldapIpAddress
     */
    public String getLdapIpAddress() {
        return ldapIpAddress;
    }

    /**
     * @param ldapIpAddress
     *            the ldapIpAddress to set
     */
    public void setLdapIpAddress(final String ldapIpAddress) {
        this.ldapIpAddress = ldapIpAddress;
    }

    /**
     * @return the fallbackLdapIpAddress
     */
    public String getFallbackLdapIpAddress() {
        return fallbackLdapIpAddress;
    }

    /**
     * @param fallbackLdapIpAddress
     *            the fallbackLdapIpAddress to set
     */
    public void setFallbackLdapIpAddress(final String fallbackLdapIpAddress) {
        this.fallbackLdapIpAddress = fallbackLdapIpAddress;
    }

    /**
     * @return the bindDn
     */
    public String getBindDn() {
        return bindDn;
    }

    /**
     * @param bindDn
     *            the bindDn to set
     */
    public void setBindDn(final String bindDn) {
        this.bindDn = bindDn;
    }

    /**
     * @return the bindPassword
     */
    public String getBindPassword() {
        return bindPassword;
    }

    /**
     * @param bindPassword
     *            the bindPassword to set
     */
    public void setBindPassword(final String bindPassword) {
        this.bindPassword = bindPassword;
    }

    /**
     * @return the baseDn
     */
    public String getBaseDn() {
        return baseDn;
    }

    /**
     * @param baseDn
     *            the baseDn to set
     */
    public void setBaseDn(final String baseDn) {
        this.baseDn = baseDn;
    }

}
