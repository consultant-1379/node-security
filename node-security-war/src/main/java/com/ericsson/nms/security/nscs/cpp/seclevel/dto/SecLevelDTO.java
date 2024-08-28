/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016

 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.cpp.seclevel.dto;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * Security level change request.
 * </p>
 * <p>
 * This is a DTO object which has parameter nodeNames as list and SecurityLevel as Enum 
 * </p>
 */

public class SecLevelDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<String> nodeNames;
    private String wantedSecLevel;

    /**
     * @return the nodeNames
     */
    public List<String> getNodeNames() {
        return nodeNames;
    }

    /**
     * @param nodeNames
     *            the nodeNames to set
     */
    public void setNodeNames(List<String> nodeNames) {
        this.nodeNames = nodeNames;
    }

    /**
     * @return the wantedSecLevel
     */
    public String getWantedSecLevel() {
        return wantedSecLevel;
    }

    /**
     * @param wantedSecLevel the wantedSecLevel to set
     */
    public void setWantedSecLevel(String wantedSecLevel) {
        this.wantedSecLevel = wantedSecLevel;
    }

}
