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
package com.ericsson.nms.security.nscs.util;

import java.io.Serializable;
import java.util.List;

/**
 * This class is used to prepare node name and key data values for output parameter.
 *
 * @author tcsviku
 *
 */
public class NtpKeyIdData implements Serializable {

    private static final long serialVersionUID = 1204389398694846177L;

    private String nodeName;

    private List<Integer> keyIdList;

    /**
     * @param name
     * @param keyIdList
     */
    public NtpKeyIdData(final String name, final List<Integer> keyIdList) {
        super();
        this.nodeName = name;
        this.keyIdList = keyIdList;
    }

    /**
     * Default constructor
     */
    public NtpKeyIdData() {
        super();

    }

    /**
     * @return the nodeName
     */
    public String getName() {
        return nodeName;
    }

    /**
     * @param nodeName
     *            the name of the node to set
     */
    public void setName(final String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * @return the keyIdList
     */
    public List<Integer> getKeyIdList() {
        return keyIdList;
    }

    /**
     * @param keyIdList
     *            the keyIdList to set
     */
    public void setKeyIdList(final List<Integer> keyIdList) {
        this.keyIdList = keyIdList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((keyIdList == null) ? 0 : keyIdList.hashCode());
        result = prime * result + ((nodeName == null) ? 0 : nodeName.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NtpKeyIdData other = (NtpKeyIdData) obj;
        if (keyIdList == null) {
            if (other.keyIdList != null)
                return false;
        } else if (!keyIdList.equals(other.keyIdList))
            return false;
        if (nodeName == null) {
            if (other.nodeName != null)
                return false;
        } else if (!nodeName.equals(other.nodeName))
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "NtpKeyIdData [name=" + nodeName + ", keyIdList=" + keyIdList + "]";
    }

}
