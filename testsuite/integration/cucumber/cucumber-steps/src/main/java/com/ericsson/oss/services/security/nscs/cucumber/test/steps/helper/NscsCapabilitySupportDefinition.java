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

public class NscsCapabilitySupportDefinition extends NscsCapabilityDefinition {

    private static final long serialVersionUID = 6749587752963676946L;
    private NscsTargetParameter targetParameter;

    public NscsCapabilitySupportDefinition() {
        super();
    }

    /**
     *
     * @param function
     * @param name
     * @param defaultValue
     * @param targetCategory
     * @param targetType
     * @param targetModelIdentity
     * @param value
     */
    public NscsCapabilitySupportDefinition(final String function, final String name, final Object defaultValue, final String targetCategory,
            final String targetType, final List<String> targetModelIdentities, final Object value) {
        super(function, name, defaultValue);
        this.targetParameter = new NscsTargetParameter(targetCategory, targetType, targetModelIdentities, value);
    }

    /**
     * @return the targetParameter
     */
    public NscsTargetParameter getTargetParameter() {
        return targetParameter;
    }

    /**
     * @param targetParameter
     *            the targetParameter to set
     */
    public void setTargetParameter(final NscsTargetParameter targetParameter) {
        this.targetParameter = targetParameter;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((targetParameter == null) ? 0 : targetParameter.hashCode());
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
        final NscsCapabilitySupportDefinition other = (NscsCapabilitySupportDefinition) obj;
        if (targetParameter == null) {
            if (other.targetParameter != null) {
                return false;
            }
        } else if (!targetParameter.equals(other.targetParameter)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "NscsCapabilitySupportDefinition [" + super.toString() + " targetParameter=" + targetParameter.toString() + "]";
    }

}
