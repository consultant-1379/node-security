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
package com.ericsson.oss.services.security.nscs.nbi.logger;

import java.io.Serializable;

/**
 * Auxiliary class to manage an attribute value change.
 */
public class AttributeValueChange implements Serializable {

    private static final long serialVersionUID = 1066559248625882724L;

    private String attribute;
    private Serializable currValue;
    private Serializable oldValue;

    /**
     * Constructor using fields.
     * 
     * @param attribute
     *            the attribute name.
     * @param currValue
     *            the current new value.
     * @param oldValue
     *            the old value.
     */
    public AttributeValueChange(final String attribute, final Serializable currValue, final Serializable oldValue) {
        this.attribute = attribute;
        this.currValue = currValue;
        this.oldValue = oldValue;
    }

    /**
     * @return the attribute
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * @param attribute
     *            the attribute to set
     */
    public void setAttribute(final String attribute) {
        this.attribute = attribute;
    }

    /**
     * @return the currValue
     */
    public Serializable getCurrValue() {
        return currValue;
    }

    /**
     * @param currValue
     *            the currValue to set
     */
    public void setCurrValue(final Serializable currValue) {
        this.currValue = currValue;
    }

    /**
     * @return the oldValue
     */
    public Serializable getOldValue() {
        return oldValue;
    }

    /**
     * @param oldValue
     *            the oldValue to set
     */
    public void setOldValue(final Serializable oldValue) {
        this.oldValue = oldValue;
    }

}
