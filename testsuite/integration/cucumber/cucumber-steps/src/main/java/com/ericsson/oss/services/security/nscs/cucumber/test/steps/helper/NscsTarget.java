/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2018
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.cucumber.test.steps.helper;

import java.io.Serializable;
import java.util.List;

public class NscsTarget implements Serializable {

    private static final long serialVersionUID = -6845005289263958368L;
    private String targetCategory;
    private String targetType;
    private List<String> targetModelIdentities;

    public NscsTarget() {
        super();
    }

    /**
     * @param targetCategory
     * @param targetType
     * @param targetModelIdentities
     */
    public NscsTarget(final String targetCategory, final String targetType, final List<String> targetModelIdentities) {
        super();
        this.targetCategory = targetCategory;
        this.targetType = targetType;
        this.targetModelIdentities = targetModelIdentities;
    }

    /**
     * @return the targetCategory
     */
    public String getTargetCategory() {
        return targetCategory;
    }

    /**
     * @param targetCategory
     *            the targetCategory to set
     */
    public void setTargetCategory(final String targetCategory) {
        this.targetCategory = targetCategory;
    }

    /**
     * @return the targetType
     */
    public String getTargetType() {
        return targetType;
    }

    /**
     * @param targetType
     *            the targetType to set
     */
    public void setTargetType(final String targetType) {
        this.targetType = targetType;
    }

    /**
     * @return the targetModelIdentities
     */
    public List<String> getTargetModelIdentities() {
        return targetModelIdentities;
    }

    /**
     * @param targetModelIdentities
     *            the targetModelIdentities to set
     */
    public void setTargetModelIdentities(final List<String> targetModelIdentities) {
        this.targetModelIdentities = targetModelIdentities;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((targetCategory == null) ? 0 : targetCategory.hashCode());
        result = prime * result + ((targetModelIdentities == null) ? 0 : targetModelIdentities.hashCode());
        result = prime * result + ((targetType == null) ? 0 : targetType.hashCode());
        return result;
    }

    /**
     * Checks if this target equals or is contained in the given other target. "Contained" is to be intended as: same target category, same target
     * type and all target model identities of this target contained in the given other target ones. If target model identities of this target are
     * null or empty, they are to be considered as "contained".
     *
     * @param obj
     *            the other target
     * @return true if equal to or contained in
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NscsTarget other = (NscsTarget) obj;
        if (targetCategory == null) {
            if (other.targetCategory != null) {
                return false;
            }
        } else if (!targetCategory.equals(other.targetCategory)) {
            return false;
        }
        if (targetType == null) {
            if (other.targetType != null) {
                return false;
            }
        } else if (!targetType.equals(other.targetType)) {
            return false;
        }
        if (targetModelIdentities != null && !targetModelIdentities.isEmpty()) {
            if (other.targetModelIdentities == null) {
                return false;
            } else if (!other.targetModelIdentities.containsAll(targetModelIdentities)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "NscsTarget [targetCategory=" + targetCategory + ", targetType=" + targetType + ", targetModelIdentities=" + targetModelIdentities
                + "]";
    }

}
