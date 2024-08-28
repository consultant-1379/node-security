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

/**
 * Models a capability
 */
public class NscsCapabilityDefinition implements Serializable {

    private static final long serialVersionUID = 7122467589894034193L;
    private String function;
    private String name;
    private Object defaultValue;

    public NscsCapabilityDefinition() {
        super();
    }

    /**
     * @param function
     *            the capability model name
     * @param name
     *            the capability name
     * @param defaultValue
     *            the capability default value
     */
    public NscsCapabilityDefinition(final String function, final String name, final Object defaultValue) {
        super();
        this.function = function;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    /**
     * @return the capability model name (function)
     */
    public String getFunction() {
        return function;
    }

    /**
     * @param function
     *            the capability model name (function) to set
     */
    public void setFunction(final String function) {
        this.function = function;
    }

    /**
     * @return the capability name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the capability name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the capability default value
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * @param defaultValue
     *            the capability default value to set
     */
    public void setDefaultValue(final Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + ((function == null) ? 0 : function.hashCode());
        return result;
    }

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
        final NscsCapabilityDefinition other = (NscsCapabilityDefinition) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (defaultValue == null) {
            if (other.defaultValue != null) {
                return false;
            }
        } else if (!defaultValue.equals(other.defaultValue)) {
            if (!ComparatorHelper.equalValue(defaultValue, other.defaultValue)) {
                return false;
            }
        }
        if (function == null) {
            if (other.function != null) {
                return false;
            }
        } else if (!function.equals(other.function)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "NscsCapabilityDefinition [function=" + function + ", name=" + name + ", defaultValue=" + defaultValue + "]";
    }

}
