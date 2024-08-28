/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.ntp.model;

import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;

/**
 * NTP Key Data to install Ntp Keys on the Node
 *
 * @author xkihari
 */

public class NtpKeyData {

    private int id;

    private String key;

    private DigestAlgorithm digestAlgorithm;

    private String nodeFdn;

    /**
     * @return the digestAlgorithm
     */
    public DigestAlgorithm getDigestAlgorithm() {
        return digestAlgorithm;
    }

    /**
     * @param digestAlgorithm
     *            the digestAlgorithm to set
     */
    public void setDigestAlgorithm(final DigestAlgorithm digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm;
    }


    /**
     * NtpKeyData default constructor
     */
    public NtpKeyData() {
        super();
    }

    /**
     * @param id
     * @param key
     * @param nodeFdn
     * @param digestAlgorithm
     */
    public NtpKeyData(final int id, final String key, final String nodeFdn, final DigestAlgorithm digestAlgorithm) {
        super();
        this.id = id;
        this.key = key;
        this.digestAlgorithm = digestAlgorithm;
        this.nodeFdn = nodeFdn;
    }

    /**
     * @param id
     * @param key
     * @param digestAlgorithm
     */
    public NtpKeyData(final int id, final String key, final DigestAlgorithm digestAlgorithm) {
        super();
        this.id = id;
        this.key = key;
        this.digestAlgorithm = digestAlgorithm;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(final int id) {
        this.id = id;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key
     *            the key to set
     */
    public void setKey(final String key) {
        this.key = key;
    }


    /**
     * @return the nodeFDN
     */
    public String getNodeFdn() {
        return nodeFdn;
    }

    /**
     * @param nodeFDN
     *            the nodeFDN to set
     */
    public void setNodeFdn(final String nodeFdn) {
        this.nodeFdn = nodeFdn;
    }

    /*
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((digestAlgorithm == null) ? 0 : digestAlgorithm.hashCode());
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + id;
        result = prime * result + ((nodeFdn == null) ? 0 : nodeFdn.hashCode());
        return result;
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        final NtpKeyData other = (NtpKeyData) obj;
        if (digestAlgorithm != other.digestAlgorithm){
            return false;
        }
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key)){
            return false;
        }
        if (id != other.id){
            return false;
        }
        if (nodeFdn == null) {
            if (other.nodeFdn != null){
                return false;
            }
        } else if (!nodeFdn.equals(other.nodeFdn)){
            return false;
        }
        return true;
    }

    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "NtpKeyData [id=" + id + ", key=" + key + ", digestAlgorithm=" + digestAlgorithm + ", nodeFdn=" + nodeFdn + "]";
    }
}