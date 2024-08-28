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
package com.ericsson.nms.security.nscs.capabilitymodel.service;

import java.util.Collection;
import java.util.Map;

import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException;
import com.ericsson.oss.itpf.modeling.modelservice.typed.capabilities.Capability;

public interface NscsCapabilityModel {

    /**
     * Gets the list of target types for the given target category.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     *
     * @return the list of target types.
     * @throws NscsCapabilityModelException exception thrown for NscsCapabilityModel
     */
    public Collection<String> getTargetTypes(final String targetCategory) throws NscsCapabilityModelException;

    /**
     * Gets all available capabilities for the given capability model (function) regardless of their target type.
     *
     * @param function
     *            the name of the oss_capability and oss_capabilitysupport models to consider.
     * @param capabilityName
     *            the name of the capability in the capability model.
     * @return a collection of Capability
     */
    Collection<Capability> getCapabilities(final String function, final String capabilityName);

    /**
     * Gets the name and value of all capabilities for the given target for a given version of capability model.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type which is defined in the namespace of the oss_capabilitysupport model to consider. For oss_capabilitysupport models
     *            for network elements, the target type is typically the NE type.
     * @param function
     *            the name of the oss_capability and oss_capabilitysupport models to consider.
     * @param version
     *            the version of the oss_capabilitysupport model to consider.
     *
     * @return a map of capability's name to its value.
     */
    Map<String, Object> getCapabilities(final String targetCategory, final String targetType, final String function, final String version);

    /**
     * Gets the default value for a capability for a given capability model (function).
     *
     * @param function
     *            the name of the oss_capability model to consider.
     * @param capabilityName
     *            the name of the capability in the capability model.
     * @return a default value of the capability. Null may be returned if no default value has been defined, or if the default value is explicitly
     *         defined to be null.
     */
    Object getDefaultValue(final String function, final String capabilityName);

    /**
     * Gets the value of the given capability for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type which is defined in the namespace of the oss_capabilitysupport model to consider. For oss_capabilitysupport models
     *            for network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity. This shall be converted to the version of the oss_capabilitysupport model to consider (if any).
     * @param capabilityModelName
     *            The name of the oss_capability and oss_capabilitysupport models to consider.
     * @param capabilityName
     *            The name of the capability in the capability model.
     * @return the value of the capability, defined in the capability support model. Or a default value specified in the capability model, if no value
     *         has been defined in the capability support model for the given target type. Or null, if no value is specified for the given capability
     *         in the capability support model and no default value is specified for the given capability.
     */
    public Object getCapabilityValue(final String targetCategory, final String targetType, final String targetModelIdentity,
            final String capabilityModelName, final String capabilityName);

    /**
     * Gets target model identity associated with the given MIM version for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type which is defined in the namespace of the oss_capabilitysupport model to consider. For oss_capabilitysupport models
     *            for network elements, the target type is typically the NE type.
     * @param mimVersion
     *            the MIM version.
     *
     * @return the target model identity or null.
     */
    public String getTargetModelIdentityFromMimVersion(final String targetCategory, final String targetType, final String mimVersion);

    /**
     * Gets target model identity associated with the given Product Number for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type which is defined in the namespace of the oss_capabilitysupport model to consider. For oss_capabilitysupport models
     *            for network elements, the target type is typically the NE type.
     * @param productNumber
     *            the Product Number.
     *
     * @return the target model identity or null.
     */
    public String getTargetModelIdentityFromProductNumber(final String targetCategory, final String targetType, final String productNumber);
}