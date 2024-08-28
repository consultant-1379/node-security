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
package com.ericsson.nms.security.nscs.api.model.service;

import java.io.Serializable;

public class NscsTargetPO implements Serializable {
    private static final long serialVersionUID = -2666008042941052118L;
    private String category;
    private String type;
    private String name;
    private String modelIdentity;

    /**
     * @param category the category
     * @param type the type
     * @param name the name
     * @param modelIdentity the modelIdentity
     */
    public NscsTargetPO(final String category, final String type, final String name, final String modelIdentity) {
        super();
        this.category = category;
        this.type = type;
        this.name = name;
        this.modelIdentity = modelIdentity;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category
     *            the category to set
     */
    public void setCategory(final String category) {
        this.category = category;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the modelIdentity
     */
    public String getModelIdentity() {
        return modelIdentity;
    }

    /**
     * @param modelIdentity
     *            the modelIdentity to set
     */
    public void setModelIdentity(final String modelIdentity) {
        this.modelIdentity = modelIdentity;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((modelIdentity == null) ? 0 : modelIdentity.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        final NscsTargetPO other = (NscsTargetPO) obj;
        if (category == null) {
            if (other.category != null) {
                return false;
            }
        } else if (!category.equals(other.category)) {
            return false;
        }
        if (modelIdentity == null) {
            if (other.modelIdentity != null) {
                return false;
            }
        } else if (!modelIdentity.equals(other.modelIdentity)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

}
