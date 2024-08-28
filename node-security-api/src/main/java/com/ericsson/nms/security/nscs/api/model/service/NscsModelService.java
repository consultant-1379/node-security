package com.ericsson.nms.security.nscs.api.model.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException;
import com.ericsson.oss.itpf.sdk.core.annotation.EService;

/**
 * Provides remote access to NSCS Model Service
 */
@EService
@Remote
public interface NscsModelService {

    /**
     * Gets all supported target categories.
     *
     * @return the supported target categories
     */
    public List<String> getTargetCategories();

    /**
     * Gets all target types for the given target category.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @return all supported target types.
     */
    public List<String> getTargetTypes(final String targetCategory);

    /**
     * Gets the platform for the given target category and target type.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @return the platform. It may return null if the target type does not have a platform.
     * @throws NscsCapabilityModelException
     *             if the given combination of targetType and targetCategory is unknown.
     */
    public String getPlatform(final String targetCategory, final String targetType) throws NscsCapabilityModelException;

    /**
     * Gets the supported target model identities for the given target category and type.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @return the target model identities
     */
    public List<String> getTargetModelIdentities(final String targetCategory, final String targetType);

    /**
     * Gets the releases for the given target category, target type and target model identity.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return the releases
     * @throws NscsCapabilityModelException
     *             if targetModelIdentity does not exist.
     */
    public List<String> getReleases(final String targetCategory, final String targetType, final String targetModelIdentity)
            throws NscsCapabilityModelException;

    /**
     * Gets the target categories supporting the given target type.
     *
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @return the target categories
     * @throws NscsCapabilityModelException
     *             if target type is null.
     */
    public List<String> getTargetCategories(final String targetType) throws NscsCapabilityModelException;

    /**
     * Gets the root MO type for the given target type and target category.
     *
     * @param targetCategory
     *            the target category
     * @param targetType
     *            the target type
     * @return a map containing the type and namespace of the root MO type. It may return null if the targetType does not contain a root MO.
     *
     * @throws NscsCapabilityModelException
     *             if the given combination of targetType and targetCategory is unknown.
     */
    public Map<String, String> getRootMoType(final String targetCategory, final String targetType) throws NscsCapabilityModelException;

    /**
     * Gets if the keyLength and enrollmentMode attributes are defined as part of EnrollmentData Complex Data Type (CDT) of CPP MOM for the given
     * target category, target type and target model identity.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return true if attributes are defined as part of CDT of CPP MOM, false otherwise.
     */
    public boolean isKSandEMSupported(final String targetCategory, final String targetType, final String targetModelIdentity);

    /**
     * Gets if the certificateAuthorityDn attribute is defined as part of EnrollmentData Complex Data Type (CDT) of CPP MOM for the given target
     * category, target type and target model identity.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return true if attribute is defined as part of CDT of CPP MOM, false otherwise.
     */
    public boolean isCertificateAuthorityDnSupported(final String targetCategory, final String targetType, final String targetModelIdentity);

    /**
     * Returns if Mock Capability Model is currently used or not.
     *
     * @return true if Mock Capability Model is used, false otherwise.
     */
    public boolean isMockCapabilityModelUsed();

    /**
     * Gets all the NSCS capabilities.
     *
     * @return the a map (key is the function, value is the list of capability names for that function).
     */
    public Map<String, List<String>> getNscsCapabilities();

    /**
     * Gets generic target type details.
     *
     * @return target type details
     */
    public Map<String, Object> getTargetTypeDetails();

    /**
     * Gets target type details for the given target category.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @return target type details
     */
    public Map<String, Object> getTargetTypeDetails(final String targetCategory);

    /**
     * Gets target type details for the given target category and type.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @return target type details
     */
    public Map<String, Object> getTargetTypeDetails(final String targetCategory, final String targetType);

    /**
     * Gets target type version details for the given target category and type.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @return target type version details
     */
    public Map<String, Object> getTargetTypeVersionDetails(final String targetCategory, final String targetType);

    /**
     * Gets target type version details for the given target category and type and model identity.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity. For network elements, the target type is typically the NE type.
     * @return target type version details
     */
    public Map<String, Object> getTargetTypeVersionDetails(final String targetCategory, final String targetType, final String targetModelIdentity);

    /**
     * Gets the target model identity corresponding to a given MIM version for the given target category and target type.
     *
     * Note that this method is applicable to CPP nodes only since there is only one single MIM.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param mimVersion
     *            the reference MIM namespace.
     * @return the target model identity or null for not CPP nodes.
     * @throws NscsModelServiceException
     *             if input parameters are illegal
     * @throws IllegalArgumentException
     *             if the given combination of targetType and targetCategory is unknown.
     */
    public String getTargetModelIdentityFromMimVersion(final String targetCategory, final String targetType, final String mimVersion)
            throws NscsModelServiceException, IllegalArgumentException;

    /**
     * Gets the model info of the given Primary Type of the given reference MIM namespace for the given target category, target type and target model
     * identity.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @param refMimNamespace
     *            the reference MIM namespace. In case of CPP node types it is null since there is only one single MIM.
     * @param primaryType
     *            the Primary Type.
     * @return the model info of the primary type. The returned version will always be "*". It may return null if the primary type does not exist for
     *         the given target.
     */
    public NscsModelInfo getMimPrimaryTypeModelInfo(final String targetCategory, final String targetType, final String targetModelIdentity,
            final String refMimNamespace, final String primaryType);

    /**
     * Gets the most appropriate TMI (target model identity) for the given target category, target type, target model identity and model URN.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @param modelUrn
     *            only TMIs which contain a supported model adhering to this URN will be considered. The URN may include ModelMetaInformation.WILDCARD
     *            and ModelMetaInformation.ANY, which will be interpreted accordingly (e.g. /oss_capabilitysupport/targetCategory:targetType/NSCS/*)
     * @return the TMI, null may be returned if no TMI exists for this target type or, if no TMI exists for this target type with supported model(s)
     *         matching the given modelUrn.
     */
    public String getMostAppropriateTmiForTarget(final String targetCategory, final String targetType, final String targetModelIdentity,
            final String modelUrn);

    /**
     * Get the model info of the given list of model names of dps_primarytpe schema for the given target category, target type and target model
     * identity.
     * 
     * @param targetCategory
     *            the target category.
     * @param targetType
     *            the target type.
     * @param targetModelIdentity
     *            the target model identity.
     * @param models
     *            the list of model names.
     * @return the map of model info. For each element, the key is the model name and the value is the model info for that model name). All requested
     *         models shall exist to have a correct return value.
     */
    public Map<String, NscsModelInfo> getModelInfoList(final String targetCategory, final String targetType, final String targetModelIdentity,
            final String... models);

    /**
     * Get the model info of the latest version of the given normalized model. Note that this method is to be used ONLY for a model of dps_primarytype
     * schema whose namespace and version is independent of node type and version. Typically it shall be used to get model info of normalized model
     * (except for connectivity info model).
     *
     * @param model
     *            the model name.
     * @return the model info of the latest version of the normalized model.
     * @throws IllegalArgumentException
     *             if input parameters are invalid.
     * @throws NscsModelServiceException
     *             if model does not exist.
     */
    public NscsModelInfo getLatestVersionOfNormalizedModel(final String model) throws IllegalArgumentException, NscsModelServiceException;

    /**
     * Get the model info of the latest version of the given model.
     *
     * @param schema
     *            the schema name.
     * @param namespace
     *            the model namespace.
     * @param model
     *            the model name.
     * @return the model info of the latest version of the model.
     * @throws IllegalArgumentException
     *             if input parameters are invalid.
     * @throws NscsModelServiceException
     *             if model does not exist.
     */
    public NscsModelInfo getLatestVersionOfModel(final String schema, final String namespace, final String model)
            throws IllegalArgumentException, NscsModelServiceException;

    /**
     * Gets all available capabilities for the given function regardless of their target type.
     *
     * @param function
     *            the name of the oss_capability and oss_capabilitysupport models to consider.
     * @param capabilityName
     *            the name of the capability in the capability model.
     * @return a collection of Capability
     */
    public Collection<NscsCapability> getCapabilities(final String function, final String capabilityName);

    /**
     * Gets the name and value of all capabilities for the given target for a given version of capability model.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param function
     *            the name of the oss_capability and oss_capabilitysupport models to consider.
     * @param version
     *            the version of the oss_capabilitysupport model to consider.
     *
     * @return a map of capabilities (K is capability name, V is capability value).
     */
    public Map<String, Object> getCapabilities(String targetCategory, final String targetType, final String function, final String version);

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
    public Object getDefaultValue(final String function, final String capabilityName);

    /**
     * Gets the value of the given capability for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @param function
     *            the name of the oss_capability and oss_capabilitysupport models to consider.
     * @param capabilityName
     *            the name of the capability in the capability model
     * @return the value of the capability, defined in the capability support model or, a default value specified in the capability model, if no value
     *         has been defined in the capability support model for the given target type or, null if no value is specified for the given capability
     *         in the capability support model and no default value is specified for the given capability.
     */
    public Object getCapabilityValue(final String targetCategory, final String targetType, final String targetModelIdentity, final String function,
            final String capabilityName);

    /**
     * Gets the capabilitysupport version for the given function and target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @param function
     *            the name of the oss_capability and oss_capabilitysupport models to consider.
     * @return the capabilitysupport version
     */
    public String getCapabilitySupportModelVersion(final String targetCategory, final String targetType, final String targetModelIdentity,
            final String function);

    /**
     * Get the target PO for the given node (specified by name or FDN).
     *
     * @param node
     *            the node name or FDN.
     * @return the target PO.
     * @throws IllegalArgumentException
     *             if input parameters are invalid.
     */
    public NscsTargetPO getTargetPO(String node) throws IllegalArgumentException;

    /**
     * Get the model info for the given target category, target type, target model identity and model.
     * 
     * @param targetCategory
     *            the target category.
     * @param targetType
     *            the target type.
     * @param targetModelIdentity
     *            the target model identity.
     * @param model
     *            the model name.
     * @return the model info.
     * @throws IllegalArgumentException
     *             if input parameters are invalid
     * @throws NscsModelServiceException
     *             if model does not exist
     */
    public NscsModelInfo getModelInfo(final String targetCategory, final String targetType, final String targetModelIdentity, final String model);
}
