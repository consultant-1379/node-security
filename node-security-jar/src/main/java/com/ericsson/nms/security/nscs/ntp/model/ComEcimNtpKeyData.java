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
 * NTP Key Data to configure Ntp Keys on the Node
 *
 * @author xkihari
 */
public class ComEcimNtpKeyData {

    private long id;

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
    public ComEcimNtpKeyData() {
        super();
    }

    /**
     * @param keyId
     *            Ntp key id is a serial number generating in ntp service which is configured on node
     * @param key
     *            Ntp key generated in itservice which is configured on node
     * @param nodeFdn
     *            NTP Mo FDN
     * @param digestAlgorithm
     *            algorithm used for key generation i.e., MD5
     */
    public ComEcimNtpKeyData(final long keyId, final String key, final String nodeFdn, final DigestAlgorithm digestAlgorithm) {
        super();
        this.id = keyId;
        this.key = key;
        this.digestAlgorithm = digestAlgorithm;
        this.nodeFdn = nodeFdn;
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

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(final long id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "NtpKeyData [keyId=" + id + ", key=" + key + ", digestAlgorithm=" + digestAlgorithm + ", nodeFdn=" + nodeFdn + "]";
    }
}
