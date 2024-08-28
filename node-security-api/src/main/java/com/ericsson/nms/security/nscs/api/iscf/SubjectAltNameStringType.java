/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.api.iscf;

import java.io.Serializable;

/**
 *
 * @author enmadmin
 */
//@XmlAccessorType(XmlAccessType.FIELD)
//@XmlType(name = "SubjectAltNameStringType", propOrder = {"value"})
//@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE, creatorVisibility = Visibility.NONE)
public class SubjectAltNameStringType extends BaseSubjectAltNameDataType implements Serializable {
    
    private static final long serialVersionUID = 5178638568471955549L;

//    @XmlElement(name = "StringValue")
    protected String value;

    public SubjectAltNameStringType(String initVal) {
        value = initVal;
    }
    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(final String value) {
        this.value = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return (null == value) ? "" : value;
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
        final SubjectAltNameStringType other = (SubjectAltNameStringType) obj;
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

}
