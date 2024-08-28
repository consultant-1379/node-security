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

import java.util.List;

public class NscsTargetParameter extends NscsTarget {

    private static final long serialVersionUID = -8952998075417685165L;
    private Object value;

    public NscsTargetParameter() {
        super();
    }

    /**
     * @param targetCategory
     * @param targetType
     * @param targetModelIdentities
     * @param value
     */
    public NscsTargetParameter(final String targetCategory, final String targetType, final List<String> targetModelIdentities, final Object value) {
        super(targetCategory, targetType, targetModelIdentities);
        this.value = value;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(final Object value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NscsTargetParameter other = (NscsTargetParameter) obj;
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            if (!ComparatorHelper.equalValue(value, other.value)) {
                return false;
            }
        }
        return true;
    }

}
