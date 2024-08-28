/*------------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2017
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.command.types;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;

public class CapabilityGetCommand extends NscsPropertyCommand {

    private static final long serialVersionUID = -930872603106113829L;

    public static final String TARGET_CATEGORY_PROPERTY = "targetcategory";
    public static final String NE_TYPE_PROPERTY = "netype";
    public static final String OSS_MODEL_IDENTITY_PROPERTY = "ossmodelidentity";
    public static final String CAPABILITY_NAME_PROPERTY = "capabilityname";
    public static final String SKIP_CONSISTENCY_CHECK_PROPERTY = "skipconsistencycheck";
    public static final String ALL_NE_TYPES_VALUE = "*";

    /**
     * This method will return the wanted target category or null if ALL or if property is missing.
     *
     * @return the target category or null if ALL or missing
     */
    public String getTargetCategory() {
        if (!isAllNeTypes() && hasProperty(TARGET_CATEGORY_PROPERTY)) {
            return (String) getValue(TARGET_CATEGORY_PROPERTY);
        }
        return null;
    }

    /**
     * This method will return the wanted node type or null if ALL or if property is missing.
     *
     * @return the node type or null if ALL or missing
     */
    public String getNeType() {
        if (!isAllNeTypes() && hasProperty(NE_TYPE_PROPERTY)) {
            return (String) getValue(NE_TYPE_PROPERTY);
        }
        return null;
    }

    /**
     * This method will return the wanted OSS model identity or null if ALL or if property is missing.
     *
     * @return the OSS model identity or null if ALL or missing
     */
    public String getOssModelIdentity() {
        if (!isAllNeTypes() && hasProperty(OSS_MODEL_IDENTITY_PROPERTY)) {
            return (String) getValue(OSS_MODEL_IDENTITY_PROPERTY);
        }
        return null;
    }

    /**
     * This method will return the wanted capability name or null if property is missing.
     *
     * @return the capability name or null if missing
     */
    public String getCapabilityName() {
        if (hasProperty(CAPABILITY_NAME_PROPERTY)) {
            return (String) getValue(CAPABILITY_NAME_PROPERTY);
        }
        return null;
    }

    /**
     * This method will return if a capability skip consistency check is required.
     *
     * @return true if capability skip consistency check is required, false otherwise.
     */
    public boolean isSkipConsistencyCheck() {
        return hasProperty(SKIP_CONSISTENCY_CHECK_PROPERTY);
    }

    private boolean isAllNeTypes() {
        return ALL_NE_TYPES_VALUE.equals(getValue(NE_TYPE_PROPERTY));
    }
}
