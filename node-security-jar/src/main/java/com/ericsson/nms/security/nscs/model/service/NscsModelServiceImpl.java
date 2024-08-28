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
package com.ericsson.nms.security.nscs.model.service;

import static com.ericsson.nms.security.nscs.data.Model.NETWORK_ELEMENT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.MoTypeNotFoundException;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelInfo;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelServiceException;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelConstants;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.EnrollmentServer;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeActionParameterSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeActionSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeAttributeSpecification;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeSpecification;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.modelservice.ModelService;
import com.ericsson.oss.itpf.modeling.modelservice.exception.UnknownModelException;
import com.ericsson.oss.itpf.modeling.modelservice.exception.UnknownSchemaException;
import com.ericsson.oss.itpf.modeling.modelservice.meta.ModelMetaInformation;
import com.ericsson.oss.itpf.modeling.modelservice.typed.TypedModelAccess;
import com.ericsson.oss.itpf.modeling.modelservice.typed.capabilities.Capability;
import com.ericsson.oss.itpf.modeling.modelservice.typed.capabilities.CapabilityInformation;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.DataTypeSpecification;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.cdt.ComplexDataTypeSpecification;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.edt.EnumDataTypeSpecification;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.exception.MatchingClassNotFoundException;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.exception.UnknownElementException;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.MimMappedTo;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.Target;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeVersionInformation;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;

/**
 * Provides implementation to access Model Service.
 */
public class NscsModelServiceImpl {

    final static String CPP_PLATFORM = "CPP";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private ModelService modelService;

    /**
     * Gets all supported target categories.
     *
     * @return the supported target categories
     */
    public Set<String> getTargetCategories() {
        return getTargetTypeInformation().getTargetCategories();
    }

    /**
     * Gets all target types for the given target category.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @return all supported target types.
     */
    public Set<String> getTargetTypes(final String targetCategory) {
        return getTargetTypeInformation().getTargetTypes(targetCategory);
    }

    /**
     * Gets the connectivity info MO type for the given target category and target type.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @return the implied URN of the connectivity info MO type. The version in the returned URN will always be "*". It may return null if the node
     *         type does not contain a connectivity info MO type.
     * @throws IllegalArgumentException
     *             if the given combination of targetType and targetCategory is unknown.
     */
    public String getConnectivityInfoMoType(final String targetCategory, final String targetType) throws IllegalArgumentException {
        return getTargetTypeInformation().getConnectivityInfoMoType(targetCategory, targetType);
    }

    /**
     * Gets the platform for the given target category and target type.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @return the platform. It may return null if the target type does not have a platform.
     * @throws IllegalArgumentException
     *             if the given combination of targetType and targetCategory is unknown.
     */
    public String getPlatform(final String targetCategory, final String targetType) throws IllegalArgumentException {
        return getTargetTypeInformation().getPlatform(targetCategory, targetType);
    }

    /**
     * Gets if the platform is "CPP" for the given target category and target type.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @return true if the platform is "CPP", false otherwise.
     * @throws IllegalArgumentException
     *             if the given combination of targetType and targetCategory is unknown.
     */
    public boolean isCppPlatform(final String targetCategory, final String targetType) throws IllegalArgumentException {
        return CPP_PLATFORM.equals(getTargetTypeInformation().getPlatform(targetCategory, targetType));
    }

    /**
     * Gets the root MO type for the given target category and target type.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @return the implied URN of the root MO type. The version in the returned URN will always be "*". It may return null if the node type does not
     *         contain a root MO type.
     * @throws IllegalArgumentException
     *             if the given combination of targetType and targetCategory is unknown.
     */
    public String getRootMoType(final String targetCategory, final String targetType) throws IllegalArgumentException {
        return getTargetTypeInformation().getRootMoType(targetCategory, targetType);
    }

    /**
     * Gets the supported target model identities for the given target category and type.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @return the target model identities
     */
    public Set<String> getTargetModelIdentities(final String targetCategory, final String targetType) {
        return getTargetTypeVersionInformation(targetCategory, targetType).getTargetModelIdentities();
    }

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
     * @throws IllegalArgumentException
     *             if targetModelIdentity does not exist.
     */
    public Set<String> getReleases(final String targetCategory, final String targetType, final String targetModelIdentity)
            throws IllegalArgumentException {
        return getTargetTypeVersionInformation(targetCategory, targetType).getReleases(targetModelIdentity);
    }

    /**
     * Gets the target categories supporting the given target type.
     *
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @return the target categories
     * @throws IllegalArgumentException
     *             if target type is null.
     */
    public Collection<String> getTargetCategories(final String targetType) throws IllegalArgumentException {
        final TargetTypeInformation targetTypeInformation = getTargetTypeInformation();
        final Collection<String> targetCategories = new ArrayList<>();
        final Collection<String> supportedTargetCategories = getTargetCategories(targetTypeInformation);
        for (final String targetCategory : supportedTargetCategories) {
            final Collection<String> targetTypes = getTargetTypes(targetTypeInformation, targetCategory);
            if (targetTypes != null && targetTypes.contains(targetType)) {
                targetCategories.add(targetCategory);
            }
        }
        return targetCategories;
    }

    /**
     * Gets the model info of the connectivity info MO type for the given target category and target type.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @return the model info of the connectivity info MO type. The returned version will always be "*". It may return null if the involved node type
     *         does not contain a connectivity info MO type.
     * @throws IllegalArgumentException
     *             if the given combination of targetType and targetCategory is unknown.
     */
    public NscsModelInfo getConnectivityInfo(final String targetCategory, final String targetType) throws IllegalArgumentException {
        final String impliedUrn = getConnectivityInfoMoType(targetCategory, targetType);
        return fromImpliedUrn(impliedUrn);
    }

    /**
     * Gets the model info of the root MO type for the given target category and target type.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @return the model info of the root MO type. The returned version will always be "*". It may return null if the involved node type does not
     *         contain a root MO type.
     * @throws IllegalArgumentException
     *             if the given combination of targetType and targetCategory is unknown.
     */
    public NscsModelInfo getRootMoInfo(final String targetCategory, final String targetType) {
        final String impliedUrn = getRootMoType(targetCategory, targetType);
        return fromImpliedUrn(impliedUrn);
    }

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
            throws NscsModelServiceException, IllegalArgumentException {
        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] mimVersion [" + mimVersion + "]";
        nscsLogger.debug("get TargetModelIdentityFromMimVersion: starts for " + inputParams);
        if (targetCategory == null || targetType == null || mimVersion == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            nscsLogger.error("get TargetModelIdentityFromMimVersion: " + errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        String targetModelIdentity = null;
        try {
            final TargetTypeInformation targetTypeInformation = getTargetTypeInformation();
            final boolean isCpp = CPP_PLATFORM.equals(getPlatform(targetTypeInformation, targetCategory, targetType));
            if (isCpp) {
                final TargetTypeVersionInformation targetTypeVersionInformation = getTargetTypeVersionInformation(targetTypeInformation,
                        targetCategory, targetType);
                final Set<String> targetModelIdentities = targetTypeVersionInformation.getTargetModelIdentities();
                for (final String tmi : targetModelIdentities) {
                    final Collection<MimMappedTo> mims = targetTypeVersionInformation.getMimsMappedTo(tmi);
                    if (mims != null && !mims.isEmpty()) {
                        final MimMappedTo mim = mims.iterator().next();
                        if (mimVersion.equals(mim.getVersion())) {
                            nscsLogger.debug("get TargetModelIdentityFromMimVersion: found matching MIM [" + mim.toString() + "]");
                            targetModelIdentity = tmi;
                            break;
                        }
                    } else {
                        nscsLogger.error("get TargetModelIdentityFromMimVersion: wrong MIMs [" + mims + "] for targetCategory [" + targetCategory
                                + "] targetType [" + targetType + "] targetModelIdentity [" + tmi + "]");
                    }
                }
            } else {
                nscsLogger.info("get TargetModelIdentityFromMimVersion: not CPP node " + inputParams);
            }
        } catch (UnknownModelException | MatchingClassNotFoundException | UnknownSchemaException e) {
            nscsLogger.info("get TargetModelIdentityFromMimVersion: exception [" + e.getClass().getCanonicalName() + "] occurred [" + e.getMessage()
                    + "] for " + inputParams);
        }
        nscsLogger.debug("get TargetModelIdentityFromMimVersion: returns [" + targetModelIdentity + "]");
        return targetModelIdentity;
    }

    /**
     * Gets the model info of the given Primary Type of given reference MIM namespace for the given target category, target type and target model
     * identity.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @param refMimNamespace
     *            the reference MIM namespace. In case of CPP nodes it is null since there is only one single MIM. In case of ECIM nodes the
     *            implementation models are derived from reference models, and hence it can be different from the MIM namespace (different target
     *            types of ECIM family could have for a MIM different namespace but the reference namespace is common and unique for all). In case of
     *            EOI-YANG based nodes the namespace is conceptually the same as the reference, and hence the MIM reference namespace is null and the
     *            MIM namespace can be used instead (it is the same across all target types of EOI family).
     * @param primaryType
     *            the Primary Type.
     * @return the model info of the primary type. The returned version will always be "*". It may return null if the primary type does not exist for
     *         the given target.
     */
    public NscsModelInfo getMimPrimaryTypeModelInfo(final String targetCategory, final String targetType, final String targetModelIdentity,
            final String refMimNamespace, final String primaryType) {
        NscsModelInfo nscsModelInfo = null;
        try {
            final TargetTypeInformation targetTypeInformation = getTargetTypeInformation();
            final TargetTypeVersionInformation targetTypeVersionInformation = getTargetTypeVersionInformation(targetTypeInformation, targetCategory,
                    targetType);
            final Collection<MimMappedTo> mims = targetTypeVersionInformation.getMimsMappedTo(targetModelIdentity);
            if (!mims.isEmpty()) {
                final boolean isCpp = CPP_PLATFORM.equals(getPlatform(targetTypeInformation, targetCategory, targetType));
                final Iterator<MimMappedTo> it = mims.iterator();
                while (it.hasNext()) {
                    final MimMappedTo mim = it.next();
                    if (isRequiredMim(mim, refMimNamespace, isCpp)) {
                        final String namespace = mim.getNamespace();
                        final String version = mim.getVersion();
                        final String urn = "/" + SchemaConstants.DPS_PRIMARYTYPE + "/" + namespace + "/" + primaryType + "/" + version;
                        final PrimaryTypeSpecification ptSpec = getTypedModelAccess().getEModelSpecification(urn, PrimaryTypeSpecification.class);
                        if (ptSpec != null) {
                            final String impliedUrn = "//" + namespace + "/" + primaryType + "/" + version;
                            nscsModelInfo = fromImpliedUrn(impliedUrn);
                        } else {
                            nscsLogger.error("get MimModelInfo: null primary type spec for URN [" + urn + "] for targetCategory [" + targetCategory
                                    + "] targetType [" + targetType + "] targetModelIdentity [" + targetModelIdentity + "]");
                        }
                        break;
                    }
                }
            } else {
                nscsLogger.error("get MimModelInfo: empty MIMs for targetCategory [" + targetCategory + "] targetType [" + targetType
                        + "] targetModelIdentity [" + targetModelIdentity + "]");
            }
        } catch (UnknownModelException | MatchingClassNotFoundException | UnknownSchemaException e) {
            nscsLogger.info("get MimModelInfo: exception [" + e.getClass().getCanonicalName() + "] occurred [" + e.getMessage() + "]");
            nscsModelInfo = null;
        }
        return nscsModelInfo;
    }

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
            final String modelUrn) {

        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "] modelUrn [" + modelUrn + "]";
        nscsLogger.debug("get MostAppropriateTmiForTarget: starts for " + inputParams);

        String mostAppropriateTMI = null;
        try {
            final TargetTypeInformation targetTypeInformation = getTargetTypeInformation();
            final TargetTypeVersionInformation targetTypeVersionInformation = getTargetTypeVersionInformation(targetTypeInformation, targetCategory,
                    targetType);
            mostAppropriateTMI = targetTypeVersionInformation.getMostAppropriateTmiForTarget(targetModelIdentity, modelUrn);
        } catch (final IllegalArgumentException e) {
            nscsLogger.info("get MostAppropriateTmiForTarget: exception [" + e.getClass().getCanonicalName() + "] occurred [" + e.getMessage()
                    + "] for " + inputParams);
            mostAppropriateTMI = null;
        }
        nscsLogger.debug("get MostAppropriateTmiForTarget: returns [" + mostAppropriateTMI + "]");
        return mostAppropriateTMI;
    }

    /**
     * Get the model info of the given model of dps_primarytpe schema in the MIM of given reference namespace for the given target category, target
     * type and target model identity.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type
     * @param targetModelIdentity
     *            the target model identity
     * @param refMimNamespace
     *            the reference MIM namespace. In case of CPP nodes it is null since there is only one single MIM. In case of ECIM nodes the
     *            implementation models are derived from reference models, and hence it can be different from the MIM namespace (different target
     *            types of ECIM family could have for a MIM different namespace but the reference namespace is common and unique for all). In case of
     *            EOI-YANG based nodes the namespace is conceptually the same as the reference, and hence the MIM reference namespace is null and the
     *            MIM namespace can be used instead (it is the same across all target types of EOI family).
     * @param model
     *            the model name (the name of the primary type, the MO type).
     * @return the model info
     * @throws IllegalArgumentException
     *             if input parameters are invalid
     * @throws NscsModelServiceException
     *             if model does not exist
     */
    public NscsModelInfo getModelInfoWithRefMimNs(final String targetCategory, final String targetType, final String targetModelIdentity,
            final String refMimNamespace, final String model) {

        final String inputParams = String.format("targetCategory [%s] targetType [%s] targetModelIdentity [%s] refMimNs [%s] model [%s]",
                targetCategory, targetType, targetModelIdentity, refMimNamespace, model);
        if (targetCategory == null || targetCategory.isEmpty() || targetType == null || targetType.isEmpty() || targetModelIdentity == null
                || targetModelIdentity.isEmpty() || model == null || model.isEmpty()) {
            final String errorMsg = String.format("Illegal args : %s", inputParams);
            nscsLogger.error("get ModelInfoWithRefMimNs : {}", errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        final String momType = (String) getCapabilityValue(targetCategory, targetType, targetModelIdentity,
                NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL, NscsCapabilityModelConstants.NSCS_CAPABILITY_MOM_TYPE);
        nscsLogger.debug("get ModelInfoWithRefMimNs : MOM type [{}]", momType);
        if (NscsCapabilityModelConstants.NSCS_EOI_MOM.equals(momType) && refMimNamespace == null) {
            final String errorMsg = String.format("Illegal args : %s for momType %s", inputParams, momType);
            nscsLogger.error("get ModelInfoWithRefMimNs : {}", errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        nscsLogger.debug("get ModelInfoWithRefMimNs : starts for {}", inputParams);
        final TargetTypeVersionInformation targetTypeVersionInformation = getTargetTypeVersionInformation(targetCategory, targetType);
        final Collection<MimMappedTo> mims = targetTypeVersionInformation.getMimsMappedTo(targetModelIdentity);
        for (final MimMappedTo mimMappedTo : mims) {
            if (isRequiredMim(mimMappedTo, refMimNamespace, momType)) {
                nscsLogger.debug("get ModelInfoWithRefMimNs : found MIM ns [{}] ver [{}] refNs [{}]", mimMappedTo.getNamespace(),
                        mimMappedTo.getVersion(), mimMappedTo.getReferenceMimNamespace());
                final NscsModelInfo nscsModelInfo = getModelFromUrn(SchemaConstants.DPS_PRIMARYTYPE, mimMappedTo.getNamespace(), model,
                        mimMappedTo.getVersion());
                if (nscsModelInfo != null) {
                    nscsLogger.debug("get ModelInfoWithRefMimNs : returns model info [{}]", nscsModelInfo);
                    return nscsModelInfo;
                }
            }
        }
        final String errorMsg = String.format("Could not find ModelInfo for %s", inputParams);
        nscsLogger.error("get ModelInfoWithRefMimNs : {}", errorMsg);
        throw new NscsModelServiceException(errorMsg);
    }

    /**
     * Returns if the given MIM is the required one for the given reference MIM namespace and MOM type.
     * 
     * @param mimMappedTo
     *            the MIM.
     * @param refMimNamespace
     *            the reference MIM namespace. In case of CPP nodes it is null since there is only one single MIM. In case of ECIM nodes the
     *            implementation models are derived from reference models, and hence it can be different from the MIM namespace (different target
     *            types of ECIM family could have for a MIM different namespace but the reference namespace is common and unique for all). In case of
     *            EOI-YANG based nodes the namespace is conceptually the same as the reference, and hence the MIM reference namespace is null and the
     *            MIM namespace can be used instead (it is the same across all target types of EOI family).
     * @param momType
     *            the MOM type ("CPP", "ECIM", or "EOI").
     * @return true if MIM corresponds to given reference namespace for the given MOM type.
     */
    private boolean isRequiredMim(final MimMappedTo mimMappedTo, final String refMimNamespace, final String momType) {
        return NscsCapabilityModelConstants.NSCS_CPP_MOM.equals(momType)
                || (NscsCapabilityModelConstants.NSCS_ECIM_MOM.equals(momType)
                        && (refMimNamespace == null || refMimNamespace.equals(mimMappedTo.getReferenceMimNamespace())))
                || (NscsCapabilityModelConstants.NSCS_EOI_MOM.equals(momType) && refMimNamespace.equals(mimMappedTo.getNamespace()));
    }

    /**
     * Returns if the given MIM is the required one for the given reference MIM namespace.
     * 
     * @param mimMappedTo
     *            the MIM.
     * @param refMimNamespace
     *            the reference MIM namespace. In case of CPP nodes it is null since there is only one single MIM. In case of ECIM nodes the
     *            implementation models are derived from reference models, and hence it can be different from the MIM namespace (different target
     *            types of ECIM family could have for a MIM different namespace but the reference namespace is common and unique for all). In case of
     *            EOI-YANG based nodes the namespace is conceptually the same as the reference, and hence the MIM reference namespace is null and the
     *            MIM namespace can be used instead (it is the same across all target types of EOI family).
     * @param isCpp
     *            true if involved node has CPP platform, false otherwise.
     * @return
     */
    private boolean isRequiredMim(final MimMappedTo mimMappedTo, final String refMimNamespace, final boolean isCpp) {
        return isCpp || (mimMappedTo.getReferenceMimNamespace() != null ? refMimNamespace.equals(mimMappedTo.getReferenceMimNamespace())
                : refMimNamespace.equals(mimMappedTo.getNamespace()));
    }

    /**
     * Get the model info of the given model of dps_primarytpe schema for the given target category, target type and target model identity.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
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
    public NscsModelInfo getModelInfo(final String targetCategory, final String targetType, final String targetModelIdentity, final String model) {

        final String inputParams = String.format("targetCategory [%s] targetType [%s] targetModelIdentity [%s] model [%s]", targetCategory,
                targetType, targetModelIdentity, model);
        if (targetCategory == null || targetCategory.isEmpty() || targetType == null || targetType.isEmpty() || targetModelIdentity == null
                || targetModelIdentity.isEmpty() || model == null || model.isEmpty()) {
            final String errorMsg = String.format("Illegal args : %s", inputParams);
            nscsLogger.error("get ModelInfo : {}", errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        nscsLogger.debug("get ModelInfo : starts for {}", inputParams);
        final TargetTypeVersionInformation targetTypeVersionInformation = getTargetTypeVersionInformation(targetCategory, targetType);
        final Collection<MimMappedTo> mims = targetTypeVersionInformation.getMimsMappedTo(targetModelIdentity);
        for (final MimMappedTo mimMappedTo : mims) {
            final NscsModelInfo nscsModelInfo = getModelFromUrn(SchemaConstants.DPS_PRIMARYTYPE, mimMappedTo.getNamespace(), model,
                    mimMappedTo.getVersion());
            if (nscsModelInfo != null) {
                nscsLogger.debug("get ModelInfo : found matching MIM for {}", inputParams);
                nscsLogger.debug("get ModelInfo : returns model info [{}]", nscsModelInfo);
                return nscsModelInfo;
            }
        }
        final String errorMsg = String.format("Could not find namespace and version for %s", inputParams);
        nscsLogger.error("get ModelInfo : {}", errorMsg);
        throw new NscsModelServiceException(errorMsg);
    }

    /**
     * Get the model info of the given list of model names of dps_primarytpe schema for the given target category, target type and target model
     * identity.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type.
     * @param targetModelIdentity
     *            the target model identity.
     * @param models
     *            the list of model names.
     * @return the map of model info. For each element, the key is the model name and the value is the model info for that model name). All requested
     *         models shall exist to have a correct return value.
     * @throws IllegalArgumentException
     *             if input parameters are invalid
     * @throws NscsModelServiceException
     *             if at least one model does not exist
     */
    public Map<String, NscsModelInfo> getModelInfoList(final String targetCategory, final String targetType, final String targetModelIdentity,
            final String... models) {

        final String inputParams = String.format("targetCategory [%s] targetType [%s] targetModelIdentity [%s] models [%s]", targetCategory,
                targetType, targetModelIdentity, models);
        if (targetCategory == null || targetCategory.isEmpty() || targetType == null || targetType.isEmpty() || targetModelIdentity == null
                || targetModelIdentity.isEmpty() || models == null || models.length <= 0) {
            final String errorMsg = String.format("Illegal args : %s", inputParams);
            nscsLogger.error("get ModelInfoList : {}", errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        nscsLogger.debug("get ModelInfoList : starts for {}", inputParams);
        final TargetTypeVersionInformation targetTypeVersionInformation = getTargetTypeVersionInformation(targetCategory, targetType);
        final Map<String, NscsModelInfo> nscsModelInfos = new HashMap<String, NscsModelInfo>();
        final Collection<MimMappedTo> mims = targetTypeVersionInformation.getMimsMappedTo(targetModelIdentity);
        for (final String model : models) {
            if (model == null || model.isEmpty()) {
                final String errorMsg = String.format("Illegal args : invalid model in %s", inputParams);
                nscsLogger.error("get ModelInfoList : {}", errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }
            for (final MimMappedTo mimMappedTo : mims) {
                final NscsModelInfo nscsModelInfo = getModelFromUrn(SchemaConstants.DPS_PRIMARYTYPE, mimMappedTo.getNamespace(), model,
                        mimMappedTo.getVersion());
                if (nscsModelInfo != null) {
                    nscsLogger.debug("get ModelInfoList : found matching MIM for nodeType [{}] tMI [{}] model [{}]", targetType, targetModelIdentity,
                            model);
                    nscsLogger.debug("get ModelInfoList : adds model info [{}]", nscsModelInfo);
                    nscsModelInfos.put(model, nscsModelInfo);
                    break;
                }
            }
        }
        if (models.length == nscsModelInfos.size()) {
            nscsLogger.debug("get ModelInfoList : returns {}", nscsModelInfos);
            return nscsModelInfos;
        }
        final String errorMsg = String.format("Could not find all namespace and version for %s", inputParams);
        nscsLogger.error("get ModelInfoList : {}", errorMsg);
        throw new NscsModelServiceException(errorMsg);
    }

    /**
     * Get the model info of the given model.
     *
     * @param schema
     *            the schema name.
     * @param namespace
     *            the model namespace.
     * @param model
     *            the model name.
     * @param version
     *            the model version.
     * @return the model info of the model or null if model does not exist.
     */
    public NscsModelInfo getModelFromUrn(final String schema, final String namespace, final String model, final String version) {

        final String inputParams = String.format("schema [%s] ns [%s] model [%s] version [%s]", schema, namespace, model, version);
        nscsLogger.debug("get ModelFromUrn : starts for {}", inputParams);
        final Collection<ModelInfo> modelInfos = getModelMetaInformation().getModelsFromUrn(schema, namespace, model, version);
        nscsLogger.debug("get ModelFromUrn : found {} model infos {}", modelInfos.size(), modelInfos);
        final Iterator<ModelInfo> it = modelInfos.iterator();
        if (it.hasNext()) {
            final ModelInfo modelInfo = it.next();
            final NscsModelInfo nscsModelInfo = fromModelInfo(modelInfo);
            nscsLogger.debug("get ModelFromUrn : returns {}", nscsModelInfo);
            return nscsModelInfo;
        }
        nscsLogger.debug("get ModelFromUrn : model does not exist : returns null");
        return null;
    }

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
    public NscsModelInfo getLatestVersionOfNormalizedModel(final String model) throws IllegalArgumentException, NscsModelServiceException {
        final String inputParams = "model [" + model + "]";
        if (model == null || model.isEmpty()) {
            final String errorMsg = "Model name can't be null or empty.";
            nscsLogger.error("get LatestVersionOfNormalizedModel : " + errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        nscsLogger.debug("get LatestVersionOfNormalizedModel : starts for " + inputParams);
        final NscsModelInfo nscsModelInfo = getLatestVersionOfModel(SchemaConstants.DPS_PRIMARYTYPE, ModelMetaInformation.ANY, model);
        nscsLogger.debug("get LatestVersionOfNormalizedModel : returns : " + nscsModelInfo.toString());
        return nscsModelInfo;
    }

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
            throws IllegalArgumentException, NscsModelServiceException {
        final String inputParams = "schema [" + schema + "] ns [" + namespace + "] model [" + model + "]";
        if (schema == null || schema.isEmpty() || namespace == null || namespace.isEmpty() || model == null || model.isEmpty()) {
            final String errorMsg = "Illegal args : " + inputParams;
            nscsLogger.error("get LatestVersionOfModel : " + errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        nscsLogger.debug("get LatestVersionOfModel : starts for " + inputParams);
        ModelInfo modelInfo = null;
        try {
            modelInfo = getModelMetaInformation().getLatestVersionOfModel(schema, namespace, model);
        } catch (UnknownModelException | UnknownSchemaException e) {
            final String exceptionMessage = NscsLogger.stringifyException(e) + " while getting model info of " + inputParams;
            nscsLogger.debug("get LatestVersionOfModel : " + exceptionMessage);
            throw new NscsModelServiceException(exceptionMessage);
        }
        final NscsModelInfo nscsModelInfo = fromModelInfo(modelInfo);
        nscsLogger.debug("get LatestVersionOfModel : returns : " + nscsModelInfo.toString());
        return nscsModelInfo;
    }

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
    public boolean isModelDeployed(final String schema, final String namespace, final String model, final String version)
            throws IllegalArgumentException, NscsModelServiceException {
        final String inputParams = "schema [" + schema + "] ns [" + namespace + "] model [" + model + "] version [" + version + "]";
        if (schema == null || schema.isEmpty() || namespace == null || namespace.isEmpty() || model == null || model.isEmpty() || version == null
                || version.isEmpty()) {
            final String errorMsg = "Illegal args : " + inputParams;
            nscsLogger.error("is ModelDeployed : " + errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        nscsLogger.debug("is ModelDeployed : starts for " + inputParams);
        boolean isDeployed = false;
        try {
            isDeployed = getModelMetaInformation().isModelDeployed(new ModelInfo(schema, namespace, model, version));
        } catch (final Exception e) {
            final String exceptionMessage = NscsLogger.stringifyException(e) + " while getting if model is deployed for " + inputParams;
            nscsLogger.debug("is ModelDeployed : " + exceptionMessage);
            throw new NscsModelServiceException(exceptionMessage);
        }
        nscsLogger.debug("is ModelDeployed : returns : " + isDeployed);
        return isDeployed;
    }

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
    public boolean isKeyLengthAndEnrollmentModeDefinedInEnrollmentData(final String targetCategory, final String targetType,
            final String targetModelIdentity) {
        final TargetTypeInformation targetTypeInformation = getTargetTypeInformation();
        if (!CPP_PLATFORM.equals(getPlatform(targetTypeInformation, targetCategory, targetType))) {
            nscsLogger.info("is KeyLengthAndEnrollmentModeDefinedInEnrollmentData: returns false for not CPP targetCategory [" + targetCategory
                    + "] targetType [" + targetType + "] targetModelIdentity [" + targetModelIdentity + "]");
            return false;
        }
        return areCdtAttributesDefined(targetCategory, targetType, targetModelIdentity, null, "EnrollmentData", "keyLength", "enrollmentMode");
    }

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
    public boolean isCertificateAuthorityDnDefinedInEnrollmentData(final String targetCategory, final String targetType,
            final String targetModelIdentity) {
        final TargetTypeInformation targetTypeInformation = getTargetTypeInformation();
        if (!CPP_PLATFORM.equals(getPlatform(targetTypeInformation, targetCategory, targetType))) {
            nscsLogger.info("is CertificateAuthorityDnDefinedInEnrollmentData: returns false for not CPP targetCategory [" + targetCategory
                    + "] targetType [" + targetType + "] targetModelIdentity [" + targetModelIdentity + "]");
            return false;
        }
        return areCdtAttributesDefined(targetCategory, targetType, targetModelIdentity, null, "EnrollmentData", "certificateAuthorityDn");
    }

    /**
     * Gets if the given CDT attributes are defined as part of given Complex Data Type of given namespace for the given target category, target type
     * and target model identity.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @param refMimNamespace
     *            the reference MIM namespace. In case of CPP node types it is null since there is only one single MIM.
     * @param cdtType
     *            the Complex Data Type.
     * @param cdtAttributes
     *            the Complex Data Type attributes.
     * @return true if attributes are defined as part of given CDT, false otherwise.
     */
    public boolean areCdtAttributesDefined(final String targetCategory, final String targetType, final String targetModelIdentity,
            final String refMimNamespace, final String cdtType, final String... cdtAttributes) {
        nscsLogger.info(
                "are CdtAttributesDefined: starts for targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                        + targetModelIdentity + "] cdtType [" + cdtType + "] cdtAttributes [" + Arrays.toString(cdtAttributes) + "]");
        boolean isSupported = false;
        final TargetTypeInformation targetTypeInformation = getTargetTypeInformation();
        try {
            final TargetTypeVersionInformation targetTypeVersionInformation = getTargetTypeVersionInformation(targetTypeInformation, targetCategory,
                    targetType);
            final Collection<MimMappedTo> mims = targetTypeVersionInformation.getMimsMappedTo(targetModelIdentity);
            if (!mims.isEmpty()) {
                final boolean isCpp = CPP_PLATFORM.equals(getPlatform(targetTypeInformation, targetCategory, targetType));
                final Iterator<MimMappedTo> it = mims.iterator();
                while (it.hasNext()) {
                    final MimMappedTo mim = it.next();
                    if (isRequiredMim(mim, refMimNamespace, isCpp)) {
                        final String namespace = mim.getNamespace();
                        final String version = mim.getVersion();
                        final String modelUrn = "/" + SchemaConstants.OSS_CDT + "/" + namespace + "/" + cdtType + "/" + version;
                        final ComplexDataTypeSpecification cdtSpec = getTypedModelAccess().getEModelSpecification(modelUrn,
                                ComplexDataTypeSpecification.class);
                        for (final String attribute : cdtAttributes) {
                            cdtSpec.getAttributeSpecification(attribute);
                        }
                        isSupported = true;
                        break;
                    }
                }
            } else {
                nscsLogger.error("are CdtAttributesDefined: empty MIMs for targetCategory [" + targetCategory + "] targetType [" + targetType
                        + "] targetModelIdentity [" + targetModelIdentity + "]");
            }
        } catch (IllegalArgumentException | UnknownModelException | MatchingClassNotFoundException | UnknownSchemaException
                | UnknownElementException e) {
            nscsLogger.info("are CdtAttributesDefined: exception [" + e.getClass().getCanonicalName() + "] occurred [" + e.getMessage() + "]");
            isSupported = false;
        }
        nscsLogger.info("are CdtAttributesDefined: returns [" + isSupported + "]");
        return isSupported;
    }

    /**
     * Gets all available capabilities for the given function regardless of their target type.
     *
     * @param function
     *            the name of the oss_capability and oss_capabilitysupport models to consider.
     * @param capabilityName
     *            the name of the capability in the capability model.
     * @return a collection of Capability
     */
    public Collection<Capability> getCapabilities(final String function, final String capabilityName) {
        nscsLogger.debug("get Capabilities: starts for function [" + function + "] name [" + capabilityName + "]");
        return getCapabilityInformation().getCapabilities(function, capabilityName);
    }

    /**
     * Gets the name and value of all capabilities for the given target for a given version of capability model.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the NE type (the target type which is defined in the namespace of the oss_capabilitysupport model to consider: for
     *            oss_capabilitysupport models for network elements, the target type is typically the NE type).
     * @param function
     *            the name of the oss_capability and oss_capabilitysupport models to consider.
     * @param version
     *            the version of the oss_capabilitysupport model to consider.
     *
     * @return a map of capability's name to its value.
     */
    public Map<String, Object> getCapabilities(final String targetCategory, final String targetType, final String function, final String version) {
        final String inputParams = "targetCategory [" + targetCategory + "] targetType[" + targetType + "] function[" + function + "] version["
                + version + "]";
        nscsLogger.debug("get Capabilities: starts for " + inputParams);
        return getCapabilityInformation().getCapabilities(targetCategory, targetType, function, version);
    }

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
    public Object getDefaultValue(final String function, final String capabilityName) {
        final String inputParams = "function[" + function + "] capability[" + capabilityName + "]";
        nscsLogger.debug("get DefaultValue: starts for " + inputParams);
        return getCapabilityInformation().getDefaultValue(function, capabilityName);
    }

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
     *            the name of wanted capability model (the name of the oss_capability and oss_capabilitysupport models to consider).
     * @param capabilityName
     *            the name of the capability in the capability model
     * @return the value of the capability, defined in the capability support model or, a default value specified in the capability model, if no value
     *         has been defined in the capability support model for the given target type or, null if no value is specified for the given capability
     *         in the capability support model and no default value is specified for the given capability.
     */
    public Object getCapabilityValue(final String targetCategory, final String targetType, final String targetModelIdentity, final String function,
            final String capabilityName) {

        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "] function [" + function + "] capabilityName [" + capabilityName + "]";
        nscsLogger.debug("get CapabilityValue: starts for " + inputParams);
        final String capabilitySupportModelVersion = getCapabilitySupportModelVersion(targetCategory, targetType, targetModelIdentity, function);
        nscsLogger.debug("get CapabilityValue: capability support version[" + capabilitySupportModelVersion + "]");
        final Object value = getCapabilityInformation().getCapabilityValue(targetCategory, targetType, function, capabilityName,
                capabilitySupportModelVersion);
        nscsLogger.debug("get CapabilityValue: returns [" + value + "]");
        return value;
    }

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
     *            the name of wanted capability model (the name of the oss_capability and oss_capabilitysupport models to consider).
     * @return
     */
    public String getCapabilitySupportModelVersion(final String targetCategory, final String targetType, final String targetModelIdentity,
            final String function) {

        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "] function [" + function + "]";
        nscsLogger.debug("get CapabilitySupportModelVersion: starts for " + inputParams);

        String capabilitySupportVersion = NscsCapabilityModelConstants.NSCS_TARGET_TYPE_SPECIFIC_CAPABILITYSUPPORT_MODEL_VER;
        if (targetModelIdentity != null) {
            try {
                final TargetTypeVersionInformation targetTypeVersionInformation = getTargetTypeVersionInformation(targetCategory, targetType);

                final String targetTypeWithCategory = targetCategory + ":" + targetType;
                final String modelUrn = targetTypeVersionInformation.getSupportedModel(targetModelIdentity, SchemaConstants.OSS_CAPABILITYSUPPORT,
                        targetTypeWithCategory, function);
                capabilitySupportVersion = ModelInfo.fromUrn(modelUrn).getVersion().toString();
            } catch (final Exception e) {
                nscsLogger.debug("get CapabilitySupportModelVersion: exception [" + e.getClass().getCanonicalName() + "] occurred [" + e.getMessage()
                        + "] for " + inputParams);
                capabilitySupportVersion = NscsCapabilityModelConstants.NSCS_TARGET_TYPE_SPECIFIC_CAPABILITYSUPPORT_MODEL_VER;
            }
        }
        nscsLogger.debug("get CapabilitySupportModelVersion: returns [" + capabilitySupportVersion + "]");
        return capabilitySupportVersion;
    }

    /**
     * Gets generic target type details.
     *
     * @return target type details
     */
    public Map<String, Object> getTargetTypeDetails() {
        final TargetTypeInformation targetTypeInfo = getTargetTypeInformation();
        final Map<String, Object> targetInfo = new HashMap<>();
        targetInfo.put("Target categories", getTargetCategories(targetTypeInfo));
        return targetInfo;
    }

    /**
     * Gets target type details for the given target category.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @return target type details
     */
    public Map<String, Object> getTargetTypeDetails(final String targetCategory) {
        final TargetTypeInformation targetTypeInfo = getTargetTypeInformation();
        final Map<String, Object> targetInfo = new HashMap<>();
        targetInfo.put("Target types", getTargetTypes(targetTypeInfo, targetCategory));
        return targetInfo;
    }

    /**
     * Gets target type details for the given target category and type.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @return target type details
     */
    public Map<String, Object> getTargetTypeDetails(final String targetCategory, final String targetType) {
        final Map<String, Object> targetInfo = new HashMap<>();
        targetInfo.put("Target categories", getTargetCategories(targetType));
        return targetInfo;
    }

    /**
     * Gets target type version details for the given target category and type.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @return target type version details
     */
    public Map<String, Object> getTargetTypeVersionDetails(final String targetCategory, final String targetType) {
        final TargetTypeInformation targetTypeInformation = getTargetTypeInformation();
        final TargetTypeVersionInformation targetTypeVersionInformation = getTargetTypeVersionInformation(targetTypeInformation, targetCategory,
                targetType);
        final Map<String, Object> targetInfo = new HashMap<>();
        targetInfo.put("ConnectivityInfo MO type", targetTypeInformation.getConnectivityInfoMoType(targetCategory, targetType));
        targetInfo.put("Platform", targetTypeInformation.getPlatform(targetCategory, targetType));
        targetInfo.put("Root MO type", targetTypeInformation.getRootMoType(targetCategory, targetType));
        final Set<String> targetModelIdentities = targetTypeVersionInformation.getTargetModelIdentities();
        targetInfo.put("Target model identities", targetModelIdentities);
        return targetInfo;
    }

    /**
     * Gets target type version details for the given target category and type and model identity.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return target type version details
     */
    public Map<String, Object> getTargetTypeVersionDetails(final String targetCategory, final String targetType, final String targetModelIdentity) {
        final TargetTypeInformation targetTypeInfo = getTargetTypeInformation();
        final TargetTypeVersionInformation targetTypeVersionInfo = getTargetTypeVersionInformation(targetTypeInfo, targetCategory, targetType);
        final Map<String, Object> targetInfo = new HashMap<>();
        targetInfo.put("Releases", targetTypeVersionInfo.getReleases(targetModelIdentity));
        targetInfo.put("Supported models", targetTypeVersionInfo.getSupportedModels(targetModelIdentity));
        targetInfo.put("MIMs mapped to", targetTypeVersionInfo.getMimsMappedTo(targetModelIdentity));
        return targetInfo;
    }

    /**
     * Gets if the specified attribute exists in specified PrimaryType MO for the specified target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @param nameSpace
     *            the reference MIM namespace.
     * @param primaryType
     *            the Primary Type.
     * @param moAttribute
     *            the MO Attribute name.
     * @return true if specified attribute exists in specified MO, false otherwise.
     */
    public boolean isMoAttributeExists(final String targetCategory, final String targetType, final String targetModelIdentity, final String nameSpace,
            final String primaryType, final String moAttribute) {
        boolean isSupported = false;
        final NscsModelInfo nscsModelInfo = getMimPrimaryTypeModelInfo(targetCategory, targetType, targetModelIdentity, nameSpace, primaryType);
        if (nscsModelInfo != null) {
            final Collection<String> mimAttrs = nscsModelInfo.getMemberNames();
            for (final String mimattr : mimAttrs) {
                if (mimattr.equals(moAttribute)) {
                    isSupported = true;
                    break;
                }
            }
        }
        nscsLogger.info(
                "is MoAttributeExists: returns {} for targetCategory [{}], targetType [{}], targetModelIdentity [{}], nameSpace [{}], primaryType [{}] and moAttribute [{}]",
                isSupported, targetCategory, targetType, targetModelIdentity, nameSpace, primaryType, moAttribute);
        return isSupported;
    }

    /**
     * Check if given attribute is defined for the given PrimaryType MO of given version.
     * 
     * @param nameSpace
     *            the reference MIM namespace.
     * @param primaryType
     *            the Primary Type.
     * @param version
     *            the Primary Type MO version.
     * @param moAttribute
     *            the MO Attribute name.
     * @return true if the attribute is defined, false otherwise.
     */
    public Boolean isAttributeDefinedForPrimaryTypeMO(final String nameSpace, final String primaryType, final String version,
            final String moAttribute) {
        Boolean isAttributeDefined = false;
        // get from ModelService the ModelInfo of a PT of given namespace, type and version
        final NscsModelInfo nscsModelInfo = getModelFromUrn(SchemaConstants.DPS_PRIMARYTYPE, nameSpace, primaryType, version);
        if (nscsModelInfo != null) {
            final Collection<String> memberNames = nscsModelInfo.getMemberNames();
            if (memberNames != null && memberNames.contains(moAttribute)) {
                isAttributeDefined = true;
            }
        }
        return isAttributeDefined;
    }

    /**
     * Gets from Model Service the scoped MO type (possibly containing the $$ notation) for an unscoped MO type (not containing the $$ notation) under
     * a parent MO of given namespace, type and version for a target of given category, type and model identity.
     * 
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @param parentMoNamespace
     *            the parent MO namespace.
     * @param parentMoType
     *            the parent MO type.
     * @param parentMoVersion
     *            the parent MO version.
     * @param unscopedMoType
     *            the unscoped MO type.
     * @return the scoped MO type (possibly containing the $$ notation).
     */
    public String getScopedMoTypeFromUnscopedMoType(final String targetCategory, final String targetType, final String targetModelIdentity,
            final String parentMoNamespace, final String parentMoType, final String parentMoVersion, final String unscopedMoType) {

        final String inputParams = String.format(
                "unscopedMoType [%s] targetCategory [%s] targetType [%s] targetModelIdentity [%s] parentMoNamespace [%s] parentMoType [%s] parentMoVersion [%s]",
                unscopedMoType, targetCategory, targetType, targetModelIdentity, parentMoNamespace, parentMoType, parentMoVersion);
        nscsLogger.debug("get ScopedMoTypeFromUnscopedType : starts for {}", inputParams);

        final Optional<String> optionalScopedMoType = getHierarchicalPrimaryTypeSpecification(targetCategory, targetType, targetModelIdentity,
                parentMoNamespace, parentMoType, parentMoVersion).getAllChildTypes().stream()
                        .filter(hpts -> unscopedMoType.equals(hpts.getUnscopedType())).map(hpts -> hpts.getModelInfo().getName()).findAny();
        if (!optionalScopedMoType.isPresent()) {
            final String errorMessage = String.format("scoped MO type not found for %s", inputParams);
            nscsLogger.error("get ScopedMoTypeFromUnscopedType : {}", errorMessage);
            throw new MoTypeNotFoundException(errorMessage);
        }
        final String scopedMoType = optionalScopedMoType.get();
        nscsLogger.debug("get ScopedMoTypeFromUnscopedType : returns {} for {}", scopedMoType, inputParams);
        return scopedMoType;
    }

    public Collection<String> getSupportedAlgorithmAndKeySize() {

        nscsLogger.debug("get SupportedAlgorithmAndKeySize: starts");

        final String schema = SchemaConstants.DPS_PRIMARYTYPE;
        final String namespace = NETWORK_ELEMENT.securityFunction.networkElementSecurity.namespace();
        final String model = NETWORK_ELEMENT.securityFunction.networkElementSecurity.type();
        final ModelInfo modelInfo = getModelMetaInformation().getLatestVersionOfModel(schema, namespace, model);
        final PrimaryTypeSpecification primaryTypeSpecification = getTypedModelAccess().getEModelSpecification(modelInfo,
                PrimaryTypeSpecification.class);
        final String attributeName = NetworkElementSecurity.ALGORITHM_AND_KEY_SIZE;
        final PrimaryTypeAttributeSpecification primaryTypeAttributeSpecification = primaryTypeSpecification.getAttributeSpecification(attributeName);
        final DataTypeSpecification dataTypeSpecification = primaryTypeAttributeSpecification.getDataTypeSpecification();
        final ModelInfo referencedModelInfo = dataTypeSpecification.getReferencedDataType();
        final EnumDataTypeSpecification enumDataTypeSpecification = getTypedModelAccess().getEModelSpecification(referencedModelInfo,
                EnumDataTypeSpecification.class);
        final Collection<String> memberNames = enumDataTypeSpecification.getMemberNames();
        final Collection<Integer> memberValues = enumDataTypeSpecification.getMemberValues();

        nscsLogger.debug("get SupportedAlgorithmAndKeySize: returns member names [{}] member values [{}]", memberNames, memberValues);

        return memberNames;
    }

    /**
     * Gets from Model Service the hierarchical specification of a primary type of given namespace, type and version for a target of given category,
     * type and model identity.
     * 
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @param namespace
     *            the primary type namespace.
     * @param type
     *            the primary type type.
     * @param version
     *            the primary type version.
     * @return the hierarchical specification of the primary type.
     */
    private HierarchicalPrimaryTypeSpecification getHierarchicalPrimaryTypeSpecification(final String targetCategory, final String targetType,
            final String targetModelIdentity, final String namespace, final String type, final String version) {
        final Target target = new Target(targetCategory, targetType, null, targetModelIdentity);
        final ModelInfo modelInfo = new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, namespace, type, version);
        return getTypedModelAccess().getEModelSpecification(modelInfo, HierarchicalPrimaryTypeSpecification.class, target);
    }

    /**
     * Get from ModelService the model meta information.
     *
     * @return the model meta information.
     */
    private ModelMetaInformation getModelMetaInformation() {
        return modelService.getModelMetaInformation();
    }

    /**
     * Get from ModelService the typed model access.
     *
     * @return the typed model access.
     */
    private TypedModelAccess getTypedModelAccess() {
        return modelService.getTypedAccess();
    }

    /**
     * Get from ModelService the model information for the TargetTypeInformation Model Information class.
     *
     * @return the model information for the TargetTypeInformation class.
     */
    private TargetTypeInformation getTargetTypeInformation() {
        return getTypedModelAccess().getModelInformation(TargetTypeInformation.class);
    }

    /**
     * Gets all supported target categories.
     *
     * @param targetTypeInformation
     *            the model information for the TargetTypeInformation class
     * @return the supported target categories
     */
    private Collection<String> getTargetCategories(final TargetTypeInformation targetTypeInformation) {
        return targetTypeInformation.getTargetCategories();
    }

    /**
     * Gets all supported target types for the given target category.
     *
     * @param targetTypeInformation
     *            the model information for the TargetTypeInformation class
     * @param targetCategory
     *            the target category
     * @return all supported target types.
     */
    private Collection<String> getTargetTypes(final TargetTypeInformation targetTypeInformation, final String targetCategory) {
        return targetTypeInformation.getTargetTypes(targetCategory);
    }

    /**
     * Gets target type version information for the given target category and target type.
     *
     * @param targetTypeInformation
     *            the model information for the TargetTypeInformation class
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @return an instance of TargetVersionInformation for the given target category and target type.
     * @throws IllegalArgumentException
     *             if the given combination of targetType and targetCategory is unknown.
     */
    private TargetTypeVersionInformation getTargetTypeVersionInformation(final TargetTypeInformation targetTypeInformation,
            final String targetCategory, final String targetType) throws IllegalArgumentException {
        return targetTypeInformation.getTargetTypeVersionInformation(targetCategory, targetType);
    }

    /**
     * Gets target type version information for the given target category and target type.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @return an instance of TargetVersionInformation for the given target category and target type.
     * @throws IllegalArgumentException
     *             if the given combination of targetType and targetCategory is unknown.
     */
    private TargetTypeVersionInformation getTargetTypeVersionInformation(final String targetCategory, final String targetType)
            throws IllegalArgumentException {
        return getTargetTypeInformation().getTargetTypeVersionInformation(targetCategory, targetType);
    }

    /**
     * Get from ModelService the model information for the CapabilityInformation Model Information class.
     *
     * @return the model information for the CapabilityInformation class.
     */
    private CapabilityInformation getCapabilityInformation() {
        return getTypedModelAccess().getModelInformation(CapabilityInformation.class);
    }

    /**
     * Gets the platform for the given target category and target type.
     *
     * @param targetTypeInformation
     *            the model information for the TargetTypeInformation class
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type. * @return the platform. It may return null if the
     *            node type does not have a platform.
     * @return the platform. It may return null if the node type does not have a platform.
     * @throws IllegalArgumentException
     *             if the given combination of targetType and targetCategory is unknown.
     */
    private String getPlatform(final TargetTypeInformation targetTypeInformation, final String targetCategory, final String targetType)
            throws IllegalArgumentException {
        return targetTypeInformation.getPlatform(targetCategory, targetType);
    }

    /**
     * Gets the model info for the given implied URN of a Primary Type.
     *
     * @param impliedUrn
     *            the implied URN of a Primary Type.
     * @return the model info for the given implied URN. The returned version will always be "*". It may return null if the given URN does not exist.
     * @throws IllegalArgumentException
     *             if the supplied URN is malformed.
     */
    private NscsModelInfo fromImpliedUrn(final String impliedUrn) throws IllegalArgumentException {
        NscsModelInfo nscsModelInfo = null;
        if (impliedUrn != null) {
            final ModelInfo modelInfo = ModelInfo.fromImpliedUrn(impliedUrn, SchemaConstants.DPS_PRIMARYTYPE);
            nscsModelInfo = fromModelInfo(modelInfo);
        } else {
            nscsLogger.info("from ImpliedUrn : null implied URN.");
        }
        return nscsModelInfo;
    }

    /**
     * Converts the given ModelInfo of a Primary Type to an internal NscsModelInfo auxiliary class containing also info about attributes and actions
     * of involved Primary Type.
     *
     * @param modelInfo
     *            the Primary Type ModelInfo
     * @return the NscsModelInfo
     */
    private NscsModelInfo fromModelInfo(final ModelInfo modelInfo) {
        NscsModelInfo nscsModelInfo;
        final PrimaryTypeSpecification primaryTypeSpecification = getTypedModelAccess().getEModelSpecification(modelInfo,
                PrimaryTypeSpecification.class);
        final Map<String, Collection<String>> actions = new HashMap<>();
        for (final PrimaryTypeActionSpecification action : primaryTypeSpecification.getActionSpecifications()) {
            final Collection<String> params = new ArrayList<>();
            for (final PrimaryTypeActionParameterSpecification param : action.getParameters()) {
                params.add(param.getName());
            }
            actions.put(action.getName(), params);
        }
        nscsModelInfo = new NscsModelInfo(modelInfo.getSchema(), modelInfo.getNamespace(), modelInfo.getName(), modelInfo.getVersion().toString(),
                primaryTypeSpecification.getMemberNames(), actions);
        return nscsModelInfo;
    }

    /**
     * Check whether External CA enrollment is supported by given node specified by its node model information.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return true if the External CA enrollment is supported for the targeted node.
     */
    public boolean isExtCAOperationSupported(final String targetCategory, final String targetType, final String targetModelIdentity) {
        nscsLogger.debug("is ExtCAOperationSupported: starts for targetCategory [{}], targetType [{}], targetModelIdentity [{}] ", targetCategory,
                targetType, targetModelIdentity);
        Boolean isExtCAOperationSupported = false;
        if (isMoAttributeExists(targetCategory, targetType, targetModelIdentity, ModelDefinition.REF_MIM_NS_ECIM_CERTM, ModelDefinition.ENROLLMENT_SERVER_TYPE,
                EnrollmentServer.ENROLLMENT_INTERFACE)) {

            isExtCAOperationSupported = true;
        }
        return isExtCAOperationSupported;
    }

    /**
     * validates the node for NTP support by checking the NTP server related MO attributes
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @param namespace
     *           normalized namespaces
     * @param moName
     *           MO that needs to be validated
     * @param moAttribute
     *           attribute that exists under MO
     */
    public boolean isNtpOperationSupported(final String targetCategory, final String targetType, final String targetModelIdentity,
            final String namespace, final String moName, final String moAttribute) {
        nscsLogger.debug("is NtpOperationSupported: starts for targetCategory [{}], targetType [{}], targetModelIdentity [{}] ", targetCategory,
                targetType, targetModelIdentity);
        Boolean isSupported = false;
        if (isMoAttributeExists(targetCategory, targetType, targetModelIdentity, namespace, moName, moAttribute)) {
            isSupported = true;
        }
        return isSupported;
    }
}
