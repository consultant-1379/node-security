/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.model.service;

/**
 * Auxiliary class to map Capability class from Model Service.
 *
 * @author emaborz
 *
 */
public class NscsCapability {
    private String function;
    private String name;
    private String targetCategory;
    private String targetType;
    private String version;
    private Object value;

    /**
     * @param function the function
     * @param name  the name
     * @param targetCategory the targetCategory
     * @param targetType the targetType
     * @param version the version
     * @param value the version
     */
    public NscsCapability(final String function, final String name, final String targetCategory, final String targetType, final String version,
            final Object value) {
        super();
        this.function = function;
        this.name = name;
        this.targetCategory = targetCategory;
        this.targetType = targetType;
        this.version = version;
        this.value = value;
    }

    /**
     * Get the function of this Capability which corresponds to the name of the oss_capabilitysupport model.
     *
     * @return the function
     */
    public String getFunction() {
        return function;
    }

    /**
     * Get the name of this Capability.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the target category of this Capability which is defined in the namespace of the oss_capabilitysupport model.
     *
     * @return the targetCategory
     */
    public String getTargetCategory() {
        return targetCategory;
    }

    /**
     * Get the target type of this Capability which is defined in the namespace of the oss_capabilitysupport model.
     *
     * @return the targetType
     */
    public String getTargetType() {
        return targetType;
    }

    /**
     * Get the version of this Capability which corresponds to the version of the oss_capabilitysupport model.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Get the value of this Capability.
     *
     * @return the value
     */
    public Object getValue() {
        return value;
    }
}
