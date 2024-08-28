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
package com.ericsson.nms.security.nscs.capabilitymodel.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NodeEntityCategory;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CiphersConstants;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;

/**
 * Auxiliary class to provide access to information stored in Capability Model.
 *
 * @author emaborz
 */
public class NscsCapabilityModelService {

    public static final String CREDENTIALS_COMMAND = "credentials";
    public static final String SSHKEY_COMMAND = "sshkey";
    public static final String SNMP_COMMAND = "snmp";
    public static final String ENROLLMENT_COMMAND = "enrollment";
    public static final String CERTIFICATE_COMMAND = "certificate";
    public static final String TRUST_COMMAND = "trust";
    public static final String SECURITYLEVEL_COMMAND = "securitylevel";
    public static final String LDAP_COMMAND = "ldap";
    public static final String CRLCHECK_COMMAND = "crlcheck";
    public static final String CRLDOWNLOAD_COMMAND = "crldownload";
    public static final String CIPHERS_COMMAND = "ciphers";
    public static final String RTSEL_COMMAND = "rtsel";
    public static final String HTTPS_COMMAND = "https";
    public static final String FTPES_COMMAND = "ftpes";
    public static final String LAAD_COMMAND = "laad";
    public static final String RNC8300 = "RNC_8300";
    public static final String RNC = "RNC";
    public static final String MONAMESPACE = "RNC_NODE_MODEL";
    public static final String MOTYPE = "RncFunction";
    public static final String RNCTYPE = "rncType";
    public static final String NTP = "ntp";

    @Inject
    private Logger logger;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private NscsModelServiceImpl nscsModelServiceImpl;

    @Inject
    private BeanManager beanManager;

    /**
     * Gets from Model Service if the given target type is supported by NSCS.
     *
     * Note that the target types supported by NSCS can be a subset of the target types supported by the whole ENM project.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     *
     * @return true if target type is supported, false otherwise.
     * @throws NscsCapabilityModelException
     *             if the capability is undefined.
     */
    public boolean isTargetTypeSupported(final String targetCategory, final String targetType) {
        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "]";
        logger.debug("is TargetTypeSupported: starts for {}", inputParams);

        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is TargetTypeSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        boolean isSupported = false;
        final List<String> supportedTargetTypes = getTargetTypes(targetCategory);
        if (supportedTargetTypes != null && supportedTargetTypes.contains(targetType)) {
            isSupported = true;
        } else {
            logger.error(
                    "is TargetTypeSupported: targetType [{}] unsupported: supported target types are [{}]", targetType, supportedTargetTypes);
        }
        logger.debug("is TargetTypeSupported: returns [{}]", isSupported);
        return isSupported;
    }

    /**
     * Gets from Model Service the list of target types for the given target category.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     *
     * @return the list of target types.
     * @throws NscsCapabilityModelException
     *             if the capability is undefined.
     */
    public List<String> getTargetTypes(final String targetCategory) {
        Collection<String> targetTypes = null;
        final Bean<?> bean = getCapabilityModelBeanForOption(useMockCapabilityModel());
        final CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
        try {
            final NscsCapabilityModel nscsCapabilityModel = (NscsCapabilityModel) beanManager.getReference(bean, NscsCapabilityModel.class,
                    creationalContext);
            if (nscsCapabilityModel != null) {
                targetTypes = nscsCapabilityModel.getTargetTypes(targetCategory);
            }
        } finally {
            creationalContext.release();
        }
        return new ArrayList<>(targetTypes);
    }

    /**
     * Gets from Capability Model if the given command is supported for the given node reference.
     *
     * @param normNodeRef
     *            The node reference.
     * @param command
     *            The command.
     * @return true if the given command is supported, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public boolean isCliCommandSupported(final NormalizableNodeReference normNodeRef, final String command) {
        final String inputParams = "normNodeRef [" + normNodeRef + "] command [" + command + "]";
        logger.debug("is CliCommandSupported: starts for {}", inputParams);
        if (normNodeRef == null || command == null || command.isEmpty()) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is CliCommandSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        return isCliCommandSupported(targetCategory, targetType, targetModelIdentity, command);
    }

    /**
     * Gets from Capability Model if the given command is supported for the given node model info.
     *
     * @param nodeModelInfo
     *            The node model info.
     * @param command
     *            The command.
     * @return true if the given command is supported, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public boolean isCliCommandSupported(final NodeModelInformation nodeModelInfo, final String command) {
        final String inputParams = "nodeModelInfo [" + nodeModelInfo + "] command [" + command + "]";
        logger.debug("is CliCommandSupported: starts for {}", inputParams);
        if (nodeModelInfo == null || command.isEmpty()) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is CliCommandSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String targetCategory = getTargetCategoryFromNodeModelInfo(nodeModelInfo);
        final String targetType = getTargetTypeFromNodeModelInfo(nodeModelInfo);
        final String targetModelIdentity = getTargetModelIdentityFromNodeModelInformation(nodeModelInfo);
        return isCliCommandSupported(targetCategory, targetType, targetModelIdentity, command);
    }

    /**
     * Gets from Capability Model if the given command is supported for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @param command
     *            the command.
     * @return true if the given command is supported, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    private boolean isCliCommandSupported(final String targetCategory, final String targetType, final String targetModelIdentity,
            final String command) {
        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "] command [" + command + "]";
        logger.debug("is CliCommandSupported: starts for {}", inputParams);
        if (targetCategory == null || targetType == null || command == null || command.isEmpty()) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is CliCommandSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        boolean isSupported = false;
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_UNSUPPORTED_COMMANDS;
        @SuppressWarnings("unchecked")
        final List<String> unsupportedCommands = (List<String>) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function,
                capabilityName);
        logger.debug("is CliCommandSupported: unsupportedCommands: {}", unsupportedCommands);
        if (unsupportedCommands != null) {
            if (!unsupportedCommands.contains(command)) {
                isSupported = true;
            } else {
                logger.error("is CliCommandSupported: UNSUPPORTED for {}", inputParams);
            }
        } else {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("is CliCommandSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("is CliCommandSupported: returns [{}]", isSupported);
        return isSupported;
    }

    /**
     * Gets the expected parameters for 'secadm credentials create/update' commands for the given node reference.
     *
     * @param normNodeRef
     *            The node reference.
     * @return the expected parameters for 'secadm credentials create/update' commands.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public List<String> getExpectedCredentialsParams(final NormalizableNodeReference normNodeRef) {
        final String inputParams = "normNodeRef [" + normNodeRef + "]";
        logger.debug("get ExpectedCredentialsParams: starts for {}", inputParams);

        if (normNodeRef == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get ExpectedCredentialsParams: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        return getExpectedCredentialsParams(targetCategory, targetType, targetModelIdentity);
    }

    /**
     * Gets from Capability Model the expected parameters for 'secadm credentials create/update' commands for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return the expected parameters for 'secadm credentials create/update' commands.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    private List<String> getExpectedCredentialsParams(final String targetCategory, final String targetType, final String targetModelIdentity)
    {

        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("get ExpectedCredentialsParams: starts for {}", inputParams);

        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get ExpectedCredentialsParams: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_CREDS_PARAMS;
        @SuppressWarnings("unchecked")
        final Map<String, List<String>> credentialsParams = (Map<String, List<String>>) getCapabilityValue(targetCategory, targetType,
                targetModelIdentity, function, capabilityName);
        final List<String> expected = credentialsParams != null
                ? credentialsParams.get(NscsCapabilityModelConstants.NSCS_CAPABILITY_PARAMS_EXPECTED_ATTRIBUTE) : null;
        logger.debug("get ExpectedCredentialsParams: returns [{}]", expected );
        return expected;
    }

    /**
     * Gets the unexpected parameters for 'secadm credentials create/update' commands for the given node reference.
     *
     * @param normNodeRef
     *            The node reference.
     * @return the unexpected parameters for 'secadm credentials create/update' commands.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public List<String> getUnexpectedCredentialsParams(final NormalizableNodeReference normNodeRef) {
        final String inputParams = "normNodeRef [" + normNodeRef + "]";
        logger.debug("get UnexpectedCredentialsParams: starts for {}", inputParams);

        if (normNodeRef == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get UnexpectedCredentialsParams: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        return getUnexpectedCredentialsParams(targetCategory, targetType, targetModelIdentity);
    }

    /**
     * Gets from Capability Model the unexpected parameters for 'secadm credentials create/update' commands for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return the unexpected parameters for 'secadm credentials create/update' commands.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    private List<String> getUnexpectedCredentialsParams(final String targetCategory, final String targetType, final String targetModelIdentity)
    {

        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("get UnexpectedCredentialsParams: starts for {}", inputParams);

        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get UnexpectedCredentialsParams: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_CREDS_PARAMS;
        @SuppressWarnings("unchecked")
        final Map<String, List<String>> credentialsParams = (Map<String, List<String>>) getCapabilityValue(targetCategory, targetType,
                targetModelIdentity, function, capabilityName);
        final List<String> unexpected = credentialsParams != null
                ? credentialsParams.get(NscsCapabilityModelConstants.NSCS_CAPABILITY_PARAMS_UNEXPECTED_ATTRIBUTE) : null;
        logger.debug("get UnexpectedCredentialsParams: returns [{}]", unexpected);
        return unexpected;
    }

    /**
     * Checks if the given enrollment mode is supported by the given node model info.
     *
     * @param nodeModelInfo
     *            the node model info
     * @param enrollmentMode
     *            the enrollment mode
     * @return true if enrollment mode is supported, false otherwise
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal.
     */
    public boolean isEnrollmentModeSupported(final NodeModelInformation nodeModelInfo, final EnrollmentMode enrollmentMode)
    {
        final String inputParams = "nodeModelInfo [" + nodeModelInfo + "] enrollmentMode [" + enrollmentMode + "]";
        logger.debug("is EnrollmentModeSupported: starts for {}", inputParams);
        if (nodeModelInfo == null || enrollmentMode == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is EnrollmentModeSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String targetCategory = getTargetCategoryFromNodeModelInfo(nodeModelInfo);
        final String targetType = getTargetTypeFromNodeModelInfo(nodeModelInfo);
        final String targetModelIdentity = getTargetModelIdentityFromNodeModelInformation(nodeModelInfo);
        final String targetParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        boolean isSupported = false;
        final List<String> supportedEnrollmentModes = getSupportedEnrollmentModes(targetCategory, targetType, targetModelIdentity);
        if (supportedEnrollmentModes != null && supportedEnrollmentModes.contains(enrollmentMode.name())) {
            isSupported = true;
        } else {

            logger.error("is EnrollmentModeSupported: enrollmentMode [{}] is unsupported for [{}]", enrollmentMode, targetParams);
        }
        logger.debug("is EnrollmentModeSupported: returns [{}]", isSupported);
        return isSupported;
    }

    /**
     * Gets the supported enrollment modes for the given node reference.
     *
     * @param normNodeRef
     *            The node reference.
     * @return the supported enrollment modes.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public List<String> getSupportedEnrollmentModes(final NormalizableNodeReference normNodeRef) {
        final String inputParams = "normNodeRef [" + normNodeRef + "]";
        logger.debug("get SupportedEnrollmentModes: starts for {}", inputParams);

        if (normNodeRef == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get SupportedEnrollmentModes: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        final List<String> supported = getSupportedEnrollmentModes(targetCategory, targetType, targetModelIdentity);
        logger.debug("get SupportedEnrollmentModes: returns [{}]", supported);
        return supported;
    }

    /**
     * Gets from Capability Model the supported enrollment modes for the given Node Model Information.
     *
     * @param nodeModelInfo
     *            the Node Model Info.
     * @return the supported enrollment modes.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public List<String> getSupportedEnrollmentModes(final NodeModelInformation nodeModelInfo) {
        if (nodeModelInfo == null) {
            final String errorMsg = "null Node Model Info";
            logger.error("get SupportedEnrollmentModes: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String targetCategory = getTargetCategoryFromNodeModelInfo(nodeModelInfo);
        final String targetType = getTargetTypeFromNodeModelInfo(nodeModelInfo);
        final String ossModelIdentity = getTargetModelIdentityFromNodeModelInformation(nodeModelInfo);
        return getSupportedEnrollmentModes(targetCategory, targetType, ossModelIdentity);
    }

    /**
     * Gets from Capability Model the supported enrollment modes for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return the supported enrollment modes.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    private List<String> getSupportedEnrollmentModes(final String targetCategory, final String targetType, final String targetModelIdentity)
    {

        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("get SupportedEnrollmentModes: starts for {}", inputParams);

        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get SupportedEnrollmentModes: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_SUPPORTED_ENROLLMENT_MODES;
        @SuppressWarnings("unchecked")
        final List<String> supportedEnrollmentModes = (List<String>) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function,
                capabilityName);
        if (supportedEnrollmentModes == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("get SupportedEnrollmentModes: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("get SupportedEnrollmentModes: returns [{}]", supportedEnrollmentModes);
        return supportedEnrollmentModes;
    }

    /**
     * Gets the default enrollment mode for the given node reference.
     *
     * @param normNodeRef
     *            The node reference.
     * @return the default enrollment mode.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public String getDefaultEnrollmentMode(final NormalizableNodeReference normNodeRef) {

        final String inputParams = "normNodeRef [" + normNodeRef + "]";
        logger.debug("get DefaultEnrollmentMode: starts for {}", inputParams);

        if (normNodeRef == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get DefaultEnrollmentMode: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        final String defaultEnrollmentMode = getDefaultEnrollmentMode(targetCategory, targetType, targetModelIdentity);
        logger.debug("get DefaultEnrollmentMode: returns [{}]", defaultEnrollmentMode);
        return defaultEnrollmentMode;
    }

    /**
     * Gets from Capability Model the default enrollment mode for the given node model info.
     *
     * @param nodeModelInfo
     *            The node model info.
     * @return the default enrollment mode.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined
     */
    public String getDefaultEnrollmentMode(final NodeModelInformation nodeModelInfo) {

        logger.debug("get DefaultEnrollmentMode: starts for [{}]", nodeModelInfo);
        if (nodeModelInfo == null) {
            final String errorMsg = "null Node Model Info";
            logger.error("get DefaultEnrollmentMode: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String targetCategory = getTargetCategoryFromNodeModelInfo(nodeModelInfo);
        final String targetType = getTargetTypeFromNodeModelInfo(nodeModelInfo);
        final String targetModelIdentity = getTargetModelIdentityFromNodeModelInformation(nodeModelInfo);
        return getDefaultEnrollmentMode(targetCategory, targetType, targetModelIdentity);
    }

    /**
     * Gets from Capability Model the default enrollment mode for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return the default enrollment mode.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    private String getDefaultEnrollmentMode(final String targetCategory, final String targetType, final String targetModelIdentity)
    {

        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("get DefaultEnrollmentMode: starts for {}", inputParams);

        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get DefaultEnrollmentMode: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_ENROLLMENT_MODE;
        final String defaultEnrollmentMode = (String) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function, capabilityName);
        if (defaultEnrollmentMode == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("get DefaultEnrollmentMode: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("get DefaultEnrollmentMode: returns [{}]", defaultEnrollmentMode);
        return defaultEnrollmentMode;
    }

    /**
     * Gets from Capability Model the default entity profile name for the given node model info and the given entity category (IPSEC or OAM).
     *
     * @param nodeModelInfo
     *            the node model information
     * @param entityCategory
     *            the entity category
     * @return the default entity profile name.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public String getDefaultEntityProfile(final NodeModelInformation nodeModelInfo, final NodeEntityCategory entityCategory)
    {
        final String inputParams = "modelInfo [" + nodeModelInfo + "] entityCategory [" + entityCategory + "]";
        logger.debug("get EntityProfileFromNodeModel: starts for {}", inputParams);
        if (nodeModelInfo == null || entityCategory == null) {
            final String errorMsg = "invalid input parameters " + inputParams;
            logger.error("get EntityProfileFromNodeModel: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String targetCategory = getTargetCategoryFromNodeModelInfo(nodeModelInfo);
        final String targetType = getTargetTypeFromNodeModelInfo(nodeModelInfo);
        final String targetModelIdentity = getTargetModelIdentityFromNodeModelInformation(nodeModelInfo);
        final String defaultEntityProfileName = getDefaultEntityProfile(targetCategory, targetType, targetModelIdentity, entityCategory);
        logger.debug("get EntityProfileFromNodeModel: returns [{}]", defaultEntityProfileName);
        return defaultEntityProfileName;
    }

    /**
     * Gets from Capability Model the default entity profile name for the given target and the given entity category (IPSEC or OAM).
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @param entityCategory
     *            the entity category
     * @return the default entity profile name
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    private String getDefaultEntityProfile(final String targetCategory, final String targetType, final String targetModelIdentity,
            final NodeEntityCategory entityCategory) {
        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "] entityCategory [" + entityCategory + "]";
        logger.debug("get EntityProfileFromNodeModel: starts for {}", inputParams);
        if (targetCategory == null || targetType == null || entityCategory == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get DefaultEnrollmentMode: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        String defaultEntityProfileName = null;
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_ENTITY_PROFILES;
        @SuppressWarnings("unchecked")
        final Map<String, String> defaultEntityProfiles = (Map<String, String>) getCapabilityValue(targetCategory, targetType, targetModelIdentity,
                function, capabilityName);
        if (defaultEntityProfiles != null) {
            defaultEntityProfileName = defaultEntityProfiles.get(entityCategory.name());
        }
        logger.debug("get EntityProfileFromNodeModel: returns [{}]", defaultEntityProfileName);
        return defaultEntityProfileName;
    }

    /**
     * Gets from Capability Model the issue/reissue certificate workflow name for the given node reference and the given certificate type (IPSEC or
     * OAM).
     *
     * @param nodeRef
     *            the node reference
     * @param certType
     *            the certificate type (IPSEC or OAM)
     * @return the name of the issue/reissue certificate workflow
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or capability is undefined
     */
    public String getIssueOrReissueCertWf(final NodeReference nodeRef, final String certType) {

        final String inputParams = "nodeRef [" + nodeRef + "] certType [" + certType + "]";
        logger.debug("get IssueOrReissueCertWf: starts for {}", inputParams);

        final NormalizableNodeReference normNodeRef = getNormalizableNodeReferenceFromNodeReference(nodeRef);
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeReference = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeReference);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        final String targetParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        String workflowName = null;
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_ISSUE_CERT_WORKFLOWS;
        @SuppressWarnings("unchecked")
        final Map<String, String> workflows = (Map<String, String>) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function,
                capabilityName);
        if (workflows != null) {
            workflowName = workflows.get(certType);
        } else {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + targetParams;
            logger.error("get IssueOrReissueCertWf: {}", errorMsg);
        }
        logger.debug("get IssueOrReissueCertWf: returns [{}]", workflowName);
        return workflowName;
    }

    /**
     * Gets from Capability Model the trust distribute workflow name for the given node reference and the given trust category type (IPSEC,OAM and LAAD).
     *
     * @param nodeRef
     *            the node reference
     * @param trustCategory
     *            the trust category type (IPSEC,OAM and LAAD)
     * @return the name of the trust distribute workflow
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or capability is undefined
     */
    public String getTrustDistributeWf(final NodeReference nodeRef, final String trustCategory) {

        final String inputParams = "nodeRef [" + nodeRef + "] trustCategory [" + trustCategory + "]";
        logger.debug("get TrustDistributeWf: starts for {}", inputParams);

        final NormalizableNodeReference normNodeRef = getNormalizableNodeReferenceFromNodeReference(nodeRef);
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeReference = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeReference);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        final String targetParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        String workflowName = null;
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_TRUST_DISTR_WORKFLOWS;
        @SuppressWarnings("unchecked")
        final Map<String, String> workflows = (Map<String, String>) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function,
                capabilityName);
        if (workflows != null) {
            workflowName = workflows.get(trustCategory);
        } else {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + targetParams;
            logger.error("get TrustDistributeWf: {}", errorMsg);
        }
        logger.debug("get TrustDistributeWf: returns [{}]", workflowName);
        return workflowName;
    }

    /**
     * Gets from Capability Model the trust remove workflow name for the given node reference and the given certificate type (IPSEC or OAM).
     * 
     * @param nodeRef
     *            the node reference
     * @param trustcategory
     *            the trust category (IPSEC,OAM and LAAD)
     * @return the name of the trust remove workflow
     */
    public String getTrustRemoveWf(final NodeReference nodeRef, final String trustcategory){

        final String inputParams = "nodeRef [" + nodeRef + "] trustcategory [" + trustcategory + "]";
        logger.debug("get TrustRemoveWf: starts for {}", inputParams);

        final NormalizableNodeReference normNodeRef = getNormalizableNodeReferenceFromNodeReference(nodeRef);
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeReference = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeReference);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        final String targetParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        String workflowName = null;
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_TRUST_REMOVE_WORKFLOWS;
        @SuppressWarnings("unchecked")
        final Map<String, String> workflows = (Map<String, String>) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function,
                capabilityName);
        if (workflows != null) {
            workflowName = workflows.get(trustcategory);
        } else {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + targetParams;
            logger.error("get TrustRemoveWf: {}", errorMsg);
        }
        logger.debug("get TrustRemoveWf: returns [{}]", workflowName);
        return workflowName;
    }

    /**
     * Gets from Capability Model if the certificate management is supported for the given node reference.
     *
     * @param normNodeRef
     *            the node reference.
     * @return true if the certificate management is supported, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public boolean isCertificateManagementSupported(final NormalizableNodeReference normNodeRef) {

        final String inputParams = "normNodeRef [" + normNodeRef + "]";
        logger.debug("is CertificateManagementSupported: starts for {}", inputParams);

        if (normNodeRef == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is CertificateManagementSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        boolean isSupported = false;
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        isSupported = isCertificateManagementSupported(targetCategory, targetType, targetModelIdentity);
        logger.debug("is CertificateManagementSupported: returns [{}]", isSupported);
        return isSupported;
    }

    /**
     * Gets from Capability Model if the certificate management is supported for the given node model info.
     *
     * @param nodeModelInfo
     *            The node model info.
     * @return true if the certificate management is supported, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public boolean isCertificateManagementSupported(final NodeModelInformation nodeModelInfo) {
        final String inputParams = "nodeModelInfo [" + nodeModelInfo + "]";
        logger.debug("is CertificateManagementSupported: starts for {}", inputParams);
        if (nodeModelInfo == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is CertificateManagementSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String targetCategory = getTargetCategoryFromNodeModelInfo(nodeModelInfo);
        final String targetType = getTargetTypeFromNodeModelInfo(nodeModelInfo);
        final String targetModelIdentity = getTargetModelIdentityFromNodeModelInformation(nodeModelInfo);
        return isCertificateManagementSupported(targetCategory, targetType, targetModelIdentity);
    }

    /**
     * Gets from Capability Model if the certificate management is supported for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return true if the certificate management is supported, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    private boolean isCertificateManagementSupported(final String targetCategory, final String targetType, final String targetModelIdentity)
    {

        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("is CertificateManagementSupported: starts for {}", inputParams);

        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is CertificateManagementSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_CERT_MANAGEMENT_SUPPORTED;
        final Boolean isSupported = (Boolean) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function, capabilityName);
        if (isSupported == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("is CertificateManagementSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("is CertificateManagementSupported: returns [{}]", isSupported);
        return isSupported;
    }

    /**
     * Gets from Capability Model if the given certificate type is supported for the given node reference.
     *
     * @param normNodeRef
     *            The node reference.
     * @param certType
     *            The certificate type.
     * @return true if the given certificate type is supported, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public boolean isCertTypeSupported(final NormalizableNodeReference normNodeRef, final String certType) {
        final String inputParams = "normNodeRef [" + normNodeRef + "] certType [" + certType + "]";
        logger.debug("is CertTypeSupported: starts for {}", inputParams);
        if (normNodeRef == null || certType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is CertTypeSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        boolean isSupported = false;
        final List<String> supportedCertTypes = getSupportedCertTypes(targetCategory, targetType, targetModelIdentity);
        if (supportedCertTypes != null && supportedCertTypes.contains(certType)) {
            isSupported = true;
        } else {
            logger.error("is CertTypeSupported: unsupported for {}", inputParams);
        }
        logger.debug("is CertTypeSupported: returns [{}]", isSupported);
        return isSupported;
    }

    /**
     * Gets from Capability Model the supported certificate types for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return the supported certificate types.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    private List<String> getSupportedCertTypes(final String targetCategory, final String targetType, final String targetModelIdentity)
    {

        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("get SupportedCertTypes: starts for {}", inputParams);

        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get SupportedCertTypes: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_SUPPORTED_CERT_TYPES;
        @SuppressWarnings("unchecked")
        final List<String> supportedCertTypes = (List<String>) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function,
                capabilityName);
        if (supportedCertTypes == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("get SupportedCertTypes: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("get SupportedCertTypes: returns [{}]", supportedCertTypes);
        return supportedCertTypes;
    }

    /**
     * Gets from Capability Model the default AlgorithmKeys for the given node model info.
     *
     * @param nodeModelInfo
     *            The node model info.
     * @return the default AlgorithmKeys.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined
     */
    public AlgorithmKeys getDefaultAlgorithmKeys(final NodeModelInformation nodeModelInfo) {

        logger.debug("get DefaultAlgorithmKeys: starts for [{}]", nodeModelInfo);
        if (nodeModelInfo == null) {
            final String errorMsg = "null Node Model Info";
            logger.error("get DefaultAlgorithmKeys: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String targetCategory = getTargetCategoryFromNodeModelInfo(nodeModelInfo);
        final String targetType = getTargetTypeFromNodeModelInfo(nodeModelInfo);
        final String targetModelIdentity = getTargetModelIdentityFromNodeModelInformation(nodeModelInfo);
        final String defaultKeyAlgorithm = getDefaultAlgorithmKeys(targetCategory, targetType, targetModelIdentity);
        AlgorithmKeys defaultAlgorithmKey = null;
        try {
            defaultAlgorithmKey = AlgorithmKeys.valueOf(defaultKeyAlgorithm);
        } catch (final Exception e) {
            final String errorMsg = "wrong key algorithm value [" + defaultAlgorithmKey + "]";
            logger.error("get DefaultAlgorithmKeys: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("get DefaultAlgorithmKeys: returns [{}]", defaultAlgorithmKey);
        return defaultAlgorithmKey;
    }

    /**
     * Gets from Capability Model the default algorithm keys for the given node reference.
     *
     * @param normNodeRef
     *            The node reference.
     * @return the default algorithm keys.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined
     */
    public String getDefaultAlgorithmKeys(final NormalizableNodeReference normNodeRef) {
        final String inputParams = "normNodeRef [" + normNodeRef + "]";
        logger.debug("get DefaultAlgorithmKeys: starts for {}", inputParams);
        if (normNodeRef == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get DefaultAlgorithmKeys: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        final String defaultAlgorithmKeys = getDefaultAlgorithmKeys(targetCategory, targetType, targetModelIdentity);
        logger.debug("get DefaultAlgorithmKeys: returns [{}]", defaultAlgorithmKeys);
        return defaultAlgorithmKeys;
    }

    /**
     * Gets from Capability Model the default key size and key generation algorithm for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return the default key size and key generation algorithm
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    private String getDefaultAlgorithmKeys(final String targetCategory, final String targetType, final String targetModelIdentity)
    {
        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("get DefaultAlgorithmKeys: starts for {}", inputParams);
        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get DefaultAlgorithmKeys: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_KEY_ALGORITHM;
        final String defaultKeyAlgorithm = (String) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function, capabilityName);
        if (defaultKeyAlgorithm == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("get DefaultAlgorithmKeys: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("get DefaultAlgorithmKeys: returns [{}]", defaultKeyAlgorithm);
        return defaultKeyAlgorithm;
    }

    /**
     * Checks if EnrollmentMode and KeySize are supported for the given node model information.
     *
     * @param nodeModelInfo
     *            the node model information.
     * @return true if the node model info supports the KeySize and EnrollmentMode, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public boolean isKSandEMSupported(final NodeModelInformation nodeModelInfo) {

        logger.debug("is KSandEMSupported: starts for nodeModelInfo [{}]", nodeModelInfo);
        if (nodeModelInfo == null) {
            final String errorMsg = "invalid parameters: nodeModelInfo [" + nodeModelInfo + "]";
            logger.error("is KSandEMSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String targetCategory = getTargetCategoryFromNodeModelInfo(nodeModelInfo);
        final String targetType = getTargetTypeFromNodeModelInfo(nodeModelInfo);
        final String targetModelIdentity = getTargetModelIdentityFromNodeModelInformation(nodeModelInfo);
        return isKSandEMSupported(targetCategory, targetType, targetModelIdentity);
    }

    /**
     * Gets from Model Service if the keyLength and enrollmentMode attributes are defined as part of EnrollmentData Complex Data Type (CDT) of CPP MOM
     * for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return true if attributes are defined as part of CDT of CPP MOM, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public boolean isKSandEMSupported(final String targetCategory, final String targetType, final String targetModelIdentity)
    {
        return nscsModelServiceImpl.isKeyLengthAndEnrollmentModeDefinedInEnrollmentData(targetCategory, targetType, targetModelIdentity);

    }

    /**
     * Checks if certificateAuthorityDN is supported for the given node model information.
     *
     * @param nodeModelInfo
     *            the node model information.
     * @return true if the node model information supports the certificateAuthorityDN, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public boolean isCertificateAuthorityDnSupported(final NodeModelInformation nodeModelInfo) {

        logger.debug("is CertificateAuthorityDnSupported: starts for nodeModelInfo[{}]", nodeModelInfo);
        if (nodeModelInfo == null) {
            final String errorMsg = "null nodeModelInfo";
            logger.error("is CertificateAuthorityDnSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String targetCategory = getTargetCategoryFromNodeModelInfo(nodeModelInfo);
        final String targetType = getTargetTypeFromNodeModelInfo(nodeModelInfo);
        final String targetModelIdentity = getTargetModelIdentityFromNodeModelInformation(nodeModelInfo);
        final boolean isSupported = nscsModelServiceImpl.isCertificateAuthorityDnDefinedInEnrollmentData(targetCategory, targetType,
                targetModelIdentity);
        logger.debug("is CertificateAuthorityDnSupported: returns [{}]", isSupported);
        return isSupported;
    }

    /**
     * Gets from Model Service if the certificateAuthorityDn attribute is defined as part of EnrollmentData Complex Data Type (CDT) of CPP MOM for the
     * given target category, target type and target model identity.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return true if attribute is defined as part of CDT of CPP MOM, false otherwise.
     */
    public boolean isCertificateAuthorityDnSupported(final String targetCategory, final String targetType, final String targetModelIdentity)
    {
        return nscsModelServiceImpl.isCertificateAuthorityDnDefinedInEnrollmentData(targetCategory, targetType, targetModelIdentity);
    }

    /**
     * Gets from Capability Model the default fingerprint (digest) algorithm for the given node model information.
     *
     * @param nodeModelInfo
     *            the node model information.
     * @return the default fingerprint (digest) algorithm used to compute the certificates fingerprint.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public DigestAlgorithm getDefaultDigestAlgorithm(final NodeModelInformation nodeModelInfo) {

        logger.debug("get DefaultDigestAlgorithm: starts for [{}]", nodeModelInfo);
        if (nodeModelInfo == null) {
            final String errorMsg = "null Node Model Info";
            logger.error("get DefaultDigestAlgorithm: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String targetCategory = getTargetCategoryFromNodeModelInfo(nodeModelInfo);
        final String targetType = getTargetTypeFromNodeModelInfo(nodeModelInfo);
        final String targetModelIdentity = getTargetModelIdentityFromNodeModelInformation(nodeModelInfo);
        final String defaultFingerprintAlgorithm = getDefaultDigestAlgorithm(targetCategory, targetType, targetModelIdentity);
        DigestAlgorithm defaultDigestAlgorithm = null;
        try {
            defaultDigestAlgorithm = DigestAlgorithm.valueOf(defaultFingerprintAlgorithm);
        } catch (final Exception e) {
            final String errorMsg = "wrong digest algorithm value [" + defaultFingerprintAlgorithm + "]";
            logger.error("get DefaultDigestAlgorithm: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("get DefaultDigestAlgorithm: returns [{}]", defaultDigestAlgorithm);
        return defaultDigestAlgorithm;
    }

    /**
     * Gets from Capability Model the default fingerprint (digest) algorithm for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return the default fingerprint algorithm value.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    private String getDefaultDigestAlgorithm(final String targetCategory, final String targetType, final String targetModelIdentity)
    {

        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("get DefaultDigestAlgorithm: starts for {}", inputParams);

        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get DefaultDigestAlgorithm: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_FINGERPRINT_ALGORITHM;
        final String defaultFingerprintAlgorithm = (String) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function,
                capabilityName);
        if (defaultFingerprintAlgorithm == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("get DefaultDigestAlgorithm: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("get DefaultDigestAlgorithm: returns [{}]", defaultFingerprintAlgorithm);
        return defaultFingerprintAlgorithm;
    }

    /**
     * Gets from Capability Model if the synchronous enrollment is supported for the given node model info.
     *
     * @param nodeModelInfo
     *            the node model information.
     * @return true if the synchronous enrollment is supported support, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public boolean isSynchronousEnrollmentSupported(final NodeModelInformation nodeModelInfo) {
        logger.debug("is SynchronousEnrollmentSupported: starts for [{}]", nodeModelInfo);
        if (nodeModelInfo == null) {
            final String errorMsg = "null Node Model Info";
            logger.error("is SynchronousEnrollmentSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String targetCategory = getTargetCategoryFromNodeModelInfo(nodeModelInfo);
        final String targetType = getTargetTypeFromNodeModelInfo(nodeModelInfo);
        final String targetModelIdentity = getTargetModelIdentityFromNodeModelInformation(nodeModelInfo);
        final boolean isSupported = isSynchronousEnrollmentSupported(targetCategory, targetType, targetModelIdentity);
        logger.debug("is SynchronousEnrollmentSupported: returns [{}]", isSupported);
        return isSupported;
    }

    /**
     * Gets from Capability Model if the synchronous enrollment is supported for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return true if the synchronous enrollment is supported, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    private boolean isSynchronousEnrollmentSupported(final String targetCategory, final String targetType, final String targetModelIdentity)
    {

        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("is SynchronousEnrollmentSupported: starts for {}", inputParams);

        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is SynchronousEnrollmentSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_SYNC_ENROLLMENT_SUPPORTED;
        final Boolean isSupported = (Boolean) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function, capabilityName);
        if (isSupported == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("is SynchronousEnrollmentSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("is SynchronousEnrollmentSupported: returns [{}]", isSupported);
        return isSupported;
    }

    /**
     * Gets from Model Service the node root Mo (if it exists) for the given node reference.
     *
     * The Mo is an internal class representing an actual MO in terms of type, namespace, name of attributes, hierarchy... It is not the actual MO!!!
     *
     * @param normNodeRef
     *            the node reference
     * @return the node root Mo or null if not existent
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal.
     */
    public Mo getMirrorRootMo(final NormalizableNodeReference normNodeRef) {
        final String inputParams = "normNodeRef [" + normNodeRef + "]";
        logger.debug("get MirrorRootMo: starts for {}", inputParams);
        if (normNodeRef == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get MirrorRootMo: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
        }
        final String mirrorRootFdn = normNodeRef.getFdn();
        return getMirrorRootMo(targetCategory, targetType, mirrorRootFdn);
    }

    /**
     * Gets from Model Service the node root Mo (if it exists) for the given target.
     *
     * The Mo is an internal class representing an actual MO in terms of type, namespace, name of attributes, hierarchy... It is not the actual MO!!!
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param fdn
     *            the FDN of the mirrored top MO
     * @return the node root Mo or null if not existent
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or any exception occurs.
     */
    private Mo getMirrorRootMo(final String targetCategory, final String targetType, final String fdn) {
        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] fdn [" + fdn + "]";
        logger.debug("get MirrorRootMo: starts for {}", inputParams);
        if (targetCategory == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get MirrorRootMo: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        Mo mo = null;

        try {
            if (targetType != null) {
                final Map<String, String> mirrorRootMoInfo = getRootMoType(targetCategory, targetType);
                if (mirrorRootMoInfo != null) {
                    final String mirrorRootNamespace = mirrorRootMoInfo
                            .get(NscsCapabilityModelConstants.NSCS_CAPABILITY_MIRROR_ROOT_MO_INFO_NS_ATTRIBUTE);
                    if (mirrorRootNamespace != null) {
                        logger.debug("get MirrorRootMo: namespace [{}]", mirrorRootNamespace);
                        mo = Model.getMirrorRoot(fdn, mirrorRootNamespace);
                    } else {
                        logger.error("get MirrorRootMo: null namespace for {}", inputParams);
                    }
                } else {
                    logger.error("get MirrorRootMo: null MO Info for {}", inputParams);
                }
            } else {
                logger.error("get MirrorRootMo: null targetType for {}", inputParams);
            }
        } catch (final Exception e) {
            logger.error("get MirrorRootMo: exception [" + e.getClass().getCanonicalName() + "] msg[" + e.getMessage() + "] for " + inputParams);
            throw new NscsCapabilityModelException(e.getMessage());
        }
        logger.debug("get MirrorRootMo: returns [" + (mo != null ? mo.toString() : mo) + "]");
        return mo;
    }

    /**
     * Get from Model Service the root MO type for the given target category and type.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @return a map with type and namespace of root MO or null.
     * @throws NscsCapabilityModelException
     *             if the given combination of targetType and targetCategory is unknown.
     */
    public Map<String, String> getRootMoType(final String targetCategory, final String targetType) {

        logger.debug("get RootMoType : starts for targetCategory [{}] targetType [{}]", targetCategory, targetType);

        Map<String, String> rootMoInfo = null;
        String impliedUrn = null;
        try {
            impliedUrn = nscsModelServiceImpl.getRootMoType(targetCategory, targetType);
        } catch (final IllegalArgumentException e) {
            final String errorMsg = e.getClass().getCanonicalName() + " exception getting root MO type for targetCategory [" + targetCategory
                    + "] targetType [" + targetType + "]";
            throw new NscsCapabilityModelException(errorMsg);
        }
        if (impliedUrn != null) {
            final ModelInfo modelInfo = ModelInfo.fromImpliedUrn(impliedUrn, SchemaConstants.DPS_PRIMARYTYPE);
            rootMoInfo = new HashMap<String, String>();
            rootMoInfo.put(NscsCapabilityModelConstants.NSCS_CAPABILITY_MIRROR_ROOT_MO_INFO_TYPE_ATTRIBUTE, modelInfo.getName());
            rootMoInfo.put(NscsCapabilityModelConstants.NSCS_CAPABILITY_MIRROR_ROOT_MO_INFO_NS_ATTRIBUTE, modelInfo.getNamespace());
        }
        return rootMoInfo;
    }

    /**
     * Gets from Capability Model if the given security level is supported for the given node reference.
     *
     * @param normNodeRef
     *            The node reference.
     * @param securityLevel
     *            The security level.
     * @return true if security level is supported, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public boolean isSecurityLevelSupported(final NormalizableNodeReference normNodeRef, final String securityLevel)
    {
        final String inputParams = "normNodeRef [" + normNodeRef + "] securitylevel [" + securityLevel + "]";
        logger.debug("is SecurityLevelSupported: starts for {}", inputParams);

        if (normNodeRef == null || securityLevel == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is SecurityLevelSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final List<String> supportedSecurityLevels = getSupportedSecurityLevels(normNodeRef);
        boolean isSupported = false;
        if (supportedSecurityLevels != null && supportedSecurityLevels.contains(securityLevel)) {
            isSupported = true;
        }
        logger.debug("is SecurityLevelSupported: returns [{}]", isSupported);
        return isSupported;
    }

    /**
     * Gets the supported security levels for the given node reference.
     *
     * @param normNodeRef
     *            The node reference.
     * @return the supported security levels.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public List<String> getSupportedSecurityLevels(final NormalizableNodeReference normNodeRef) {
        final String inputParams = "normNodeRef [" + normNodeRef + "]";
        logger.debug("get SupportedSecurityLevels: starts for {}", inputParams);

        if (normNodeRef == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get SupportedSecurityLevels: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        return getSupportedSecurityLevels(targetCategory, targetType, targetModelIdentity);
    }

    /**
     * Gets from Capability Model the supported security levels for the given Node Model Information.
     *
     * @param nodeModelInfo
     *            the Node Model Info.
     * @return the supported security levels.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public List<String> getSupportedSecurityLevels(final NodeModelInformation nodeModelInfo) {
        final String inputParams = "nodeModelInfo [" + nodeModelInfo + "]";
        logger.debug("get SupportedSecurityLevels: starts for {}", inputParams);
        if (nodeModelInfo == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get SupportedSecurityLevels: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String targetCategory = getTargetCategoryFromNodeModelInfo(nodeModelInfo);
        final String targetType = getTargetTypeFromNodeModelInfo(nodeModelInfo);
        final String targetModelIdentity = getTargetModelIdentityFromNodeModelInformation(nodeModelInfo);
        return getSupportedSecurityLevels(targetCategory, targetType, targetModelIdentity);
    }

    /**
     * Gets from Capability Model the supported security levels for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return the supported security levels.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    private List<String> getSupportedSecurityLevels(final String targetCategory, final String targetType, final String targetModelIdentity)
    {
        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("get SupportedSecurityLevels: starts for {}", inputParams);
        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get SupportedEnrollmentModes: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_SUPPORTED_SECURITY_LEVELS;
        @SuppressWarnings("unchecked")
        final List<String> supported = (List<String>) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function, capabilityName);
        if (supported == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("get SupportedSecurityLevels: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("get SupportedSecurityLevels: returns [{}]", supported);
        return supported;
    }

    /**
     * Gets from Capability Model the MOM type ("ECIM", "CPP") for the given node reference.
     *
     * This value is used, for example, as qualifier to select the implementation of the MO Get Service for the given node.
     *
     * @param nodeRef
     *            the node reference
     * @return the MOM type.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public String getMomType(final NodeReference nodeRef) {
        final String inputParams = "nodeRef [" + nodeRef + "]";
        logger.debug("get MomType: starts for {}", inputParams);
        if (nodeRef == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get MomType: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final NormalizableNodeReference normNodeRef = getNormalizableNodeReferenceFromNodeReference(nodeRef);
        String targetCategory = null;
        String targetType = null;
        String targetModelIdentity = null;
        if (normNodeRef != null) {
            targetCategory = normNodeRef.getTargetCategory();
            targetType = normNodeRef.getNeType();
            targetModelIdentity = normNodeRef.getOssModelIdentity();
            if (targetCategory == null || targetType == null) {
                final NodeReference nodeReference = new NodeRef(normNodeRef.getFdn());
                final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeReference);
                targetCategory = normalized.getTargetCategory();
                targetType = normalized.getNeType();
                targetModelIdentity = normalized.getOssModelIdentity();
            }
        } else {
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        return getMomType(targetCategory, targetType, targetModelIdentity);
    }

    /**
     * Gets from Capability Model the MOM type ("ECIM", "CPP") for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return the MOM type.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    private String getMomType(final String targetCategory, final String targetType, final String targetModelIdentity)
    {

        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("get MomType: starts for {}", inputParams);

        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get MomType: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_MOM_TYPE;
        final String momType = (String) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function, capabilityName);
        if (momType == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("get MomType: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("get MomType: returns [{}]", momType);
        return momType;
    }

    /**
     * Gets from Capability Model if the node certificate subject name is used by the node during its enrollment procedure or if an "hard-coded"
     * subject name is used instead for the given node reference.
     *
     * @param normNodeRef
     *            the node reference.
     * @return true if the node certificate subject name is used, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public boolean isConfiguredSubjectNameUsedForEnrollment(final NormalizableNodeReference normNodeRef) {

        final String inputParams = "normNodeRef [" + normNodeRef + "]";
        logger.debug("is ConfiguredSubjectNameUsedForEnrollment: starts for {}", inputParams);

        if (normNodeRef == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is ConfiguredSubjectNameUsedForEnrollment: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        return isConfiguredSubjectNameUsedForEnrollment(targetCategory, targetType, targetModelIdentity);
    }

    /**
     * Gets from Capability Model if the node certificate subject name is used by the node during its enrollment procedure or if an "hard-coded"
     * subject name is used instead for the given node model info.
     *
     * @param nodeModelInfo
     *            The node model info.
     * @return true if the node certificate subject name is used, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public boolean isConfiguredSubjectNameUsedForEnrollment(final NodeModelInformation nodeModelInfo) {
        final String inputParams = "nodeModelInfo [" + nodeModelInfo + "]";
        logger.debug("is ConfiguredSubjectNameUsedForEnrollment: starts for {}", inputParams);
        if (nodeModelInfo == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is ConfiguredSubjectNameUsedForEnrollment: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String targetCategory = getTargetCategoryFromNodeModelInfo(nodeModelInfo);
        final String targetType = getTargetTypeFromNodeModelInfo(nodeModelInfo);
        final String targetModelIdentity = getTargetModelIdentityFromNodeModelInformation(nodeModelInfo);
        return isConfiguredSubjectNameUsedForEnrollment(targetCategory, targetType, targetModelIdentity);
    }

    /**
     * Gets from Capability Model if the node certificate subject name is used by the node during its enrollment procedure or if an "hard-coded"
     * subject name is used instead for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return true if the node certificate subject name is used, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    private boolean isConfiguredSubjectNameUsedForEnrollment(final String targetCategory, final String targetType, final String targetModelIdentity)
    {

        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("is ConfiguredSubjectNameUsedForEnrollment: starts for {}", inputParams);

        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is ConfiguredSubjectNameUsedForEnrollment: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_CONF_SUBJECT_NAME_USED_FOR_ENROLL;
        final Boolean isUsed = (Boolean) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function, capabilityName);
        if (isUsed == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("is ConfiguredSubjectNameUsedForEnrollment: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("is ConfiguredSubjectNameUsedForEnrollment: returns [{}]", isUsed);
        return isUsed;
    }

    /**
     * Gets from Capability Model if the Ikev2PolicyProfile MO is supported for the given node reference.
     *
     * @param normNodeRef
     *            the node reference.
     * @return true if the Ikev2PolicyProfile MO is supported, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public boolean isIkev2PolicyProfileSupported(final NormalizableNodeReference normNodeRef) {

        final String inputParams = "normNodeRef [" + normNodeRef + "]";
        logger.debug("is Ikev2PolicyProfileSupported: starts for {}", inputParams);

        if (normNodeRef == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is Ikev2PolicyProfileSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        return isIkev2PolicyProfileSupported(targetCategory, targetType, targetModelIdentity);
    }

    /**
     * Gets from Capability Model if the Ikev2PolicyProfile MO is supported for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return true if the Ikev2PolicyProfile MO is supported, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    private boolean isIkev2PolicyProfileSupported(final String targetCategory, final String targetType, final String targetModelIdentity)
    {

        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("is Ikev2PolicyProfileSupported: starts for {}", inputParams);

        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is Ikev2PolicyProfileSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_IKEV2_POLICY_PROFILE_SUPPORTED;
        final Boolean isUsed = (Boolean) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function, capabilityName);
        if (isUsed == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("is Ikev2PolicyProfileSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("is Ikev2PolicyProfileSupported: returns [{}]", isUsed);
        return isUsed;
    }

    /**
     * Gets from Capability Model if the ldapApplicationUser is supported for the given node reference.
     *
     * @param normNodeRef
     *            the node reference.
     * @return true if ldapApplicationUser is supported, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public boolean isLdapCommonUserSupported(final NormalizableNodeReference normNodeRef) {

        final String inputParams = "normNodeRef [" + normNodeRef + "]";
        logger.debug("is LdapCommonUserSupported: starts for {}", inputParams);

        if (normNodeRef == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is LdapCommonUserSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        boolean isSupported = false;
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        isSupported = isLdapCommonUserSupported(targetCategory, targetType, targetModelIdentity);
        logger.debug("is LdapCommonUserSupported: returns [{}]", isSupported);
        return isSupported;
    }

    /**
     * Gets from Capability Model if the ldapApplicationUser is supported for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return true if ldapApplicationUser is supported, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    private boolean isLdapCommonUserSupported(final String targetCategory, final String targetType, final String targetModelIdentity)
    {

        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("is LdapCommonUserSupported: starts for {}", inputParams);

        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is LdapCommonUserSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_LDAP_COMMON_USER_SUPPORTED;
        final Boolean isSupported = (Boolean) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function, capabilityName);
        if (isSupported == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("is LdapCommonUserSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("is LdapCommonUserSupported: returns [{}]", isSupported);
        return isSupported;
    }

    /**
     * Gets from Capability Model if the enrollment CA fingerprint is used as enrollment CA authorization mode during the enrollment of given certificate type.
     *
     * @param enrollmentCAAuthorizationModes
     *            the enrollment CA authorization modes
     * @param certType
     *            the certificate type (“OAM” or “IPSEC”).
     * @return true if enrollment CA fingerprint is used as enrollment CA authorization mode, false otherwise.
     * @throws NscsCapabilityModelException
     *             if no value can be retrieved by Capability Model.
     */
    public boolean isEnrollmentCAFingerPrintSupported(final Map<String, String> enrollmentCAAuthorizationModes, final String certType)
    {
        boolean isSupported = false;
        if (enrollmentCAAuthorizationModes != null) {
            final String enrollmentCAAuthorizationMode = enrollmentCAAuthorizationModes.get(certType);
            isSupported = enrollmentCAAuthorizationMode.equals("ENROLLMENT_CA_FINGERPRINT");
        }
        return isSupported;
    }

    /**
     * Gets from Capability Model if the enrollment ROOT CA fingerprint is used as enrollment CA authorization mode during the enrollment of given certificate type.
     *
     * @param enrollmentCAAuthorizationModes
     *            the enrollment CA authorization modes
     * @param certType
     *            the certificate type (“OAM” or “IPSEC”).
     * @return true if enrollment ROOT CA fingerprint is used as enrollment CA authorization mode, false otherwise.
     * @throws NscsCapabilityModelException
     *             if no value can be retrieved by Capability Model.
     */
    public boolean isEnrollmentRootCAFingerPrintSupported(final Map<String, String> enrollmentCAAuthorizationModes, final String certType)
    {
        boolean isSupported = false;
        if (enrollmentCAAuthorizationModes != null) {
            final String enrollmentRootCAAuthorizationMode = enrollmentCAAuthorizationModes.get(certType);
            isSupported = enrollmentRootCAAuthorizationMode.equals("ENROLLMENT_ROOT_CA_FINGERPRINT");
        }
        return isSupported;
    }

    /**
     * Gets from Capability Model if the enrollment CA certificate is used as enrollment CA authorization mode during the enrollment of given certificate type.
     *
     * @param enrollmentCAAuthorizationModes
     *            the enrollment CA authorization modes
     * @param certType
     *            the certificate type (“OAM” or “IPSEC”).
     * @return true if enrollment CA certificate is used as enrollment CA authorization mode, false otherwise.
     * @throws NscsCapabilityModelException
     *             if no value can be retrieved by Capability Model.
     */
    public boolean isEnrollmentCACertificateSupported(final Map<String, String> enrollmentCAAuthorizationModes, final String certType)
    {
        boolean isSupported = false;
        if (enrollmentCAAuthorizationModes != null) {
            final String enrollmentCAAuthorizationMode = enrollmentCAAuthorizationModes.get(certType);
            isSupported = enrollmentCAAuthorizationMode.equals("ENROLLMENT_CA_CERTIFICATE");
        }
        return isSupported;
    }

    /**
     * Gets from Capability Model if the enrollment ROOT CA certificate is used as enrollment CA authorization mode during the enrollment of given certificate type.
     *
     * @param enrollmentCAAuthorizationModes
     *            the enrollment CA authorization modes
     * @param certType
     *            the certificate type (“OAM” or “IPSEC”).
     * @return true if enrollment ROOT CA certificate is used as enrollment CA authorization mode, false otherwise.
     * @throws NscsCapabilityModelException
     *             if no value can be retrieved by Capability Model.
     */
    public boolean isEnrollmentRootCACertificateSupported(final Map<String, String> enrollmentCAAuthorizationModes, final String certType)
    {
        boolean isSupported = false;
        if (enrollmentCAAuthorizationModes != null) {
            final String enrollmentRootCAAuthorizationMode = enrollmentCAAuthorizationModes.get(certType);
            isSupported = enrollmentRootCAAuthorizationMode.equals("ENROLLMENT_ROOT_CA_CERTIFICATE");
        }
        return isSupported;
    }

    /**
     * Gets from Capability Model the supported enrollment CA authorization modes for the given node model info.
     *
     * @param nodeModelInfo
     *            The node model info.
     * @return the supported enrollment CA authorization modes.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined
     */
    public Map<String, String> getEnrollmentCAAuthorizationModes(final NodeModelInformation nodeModelInfo) {

        logger.debug("get EnrollmentCAAuthorizationModes: starts for [{}]", nodeModelInfo);
        if (nodeModelInfo == null) {
            final String errorMsg = "null Node Model Info";
            logger.error("get EnrollmentCAAuthorizationModes: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String targetCategory = getTargetCategoryFromNodeModelInfo(nodeModelInfo);
        final String targetType = getTargetTypeFromNodeModelInfo(nodeModelInfo);
        final String targetModelIdentity = getTargetModelIdentityFromNodeModelInformation(nodeModelInfo);
        final Map<String, String> enrollmentCAAuthorizationModes = getEnrollmentCAAuthorizationModes(targetCategory, targetType, targetModelIdentity);
        logger.debug("get EnrollmentCAAuthorizationModes: returns [{}]", enrollmentCAAuthorizationModes);
        return enrollmentCAAuthorizationModes;
    }

    /**
     * Gets from Capability Model the supported enrollment CA authorization modes for the given node reference.
     *
     * @param normNodeRef
     *            The node reference.
     * @return the supported enrollment CA authorization modes.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined
     */
    public Map<String, String> getEnrollmentCAAuthorizationModes(final NormalizableNodeReference normNodeRef) {
        final String inputParams = "normNodeRef [" + normNodeRef + "]";
        logger.debug("get EnrollmentCAAuthorizationModes: starts for {}", inputParams);
        if (normNodeRef == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get EnrollmentCAAuthorizationModes: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        final Map<String, String> enrollmentCAAuthorizationModes = getEnrollmentCAAuthorizationModes(targetCategory, targetType, targetModelIdentity);
        logger.debug("get EnrollmentCAAuthorizationModes: returns [{}]", enrollmentCAAuthorizationModes);
        return enrollmentCAAuthorizationModes;
    }

    /**
     * Gets from Capability Model the supported enrollment CA authorization modes for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return the supported enrollment CA authorization modes.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public Map<String, String> getEnrollmentCAAuthorizationModes(final String targetCategory, final String targetType,
            final String targetModelIdentity) {
        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("get EnrollmentCAAuthorizationModes: starts for {}", inputParams);
        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get EnrollmentCAAuthorizationModes: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_ENROLLMENT_CA_AUTHORIZATION_MODES;
        @SuppressWarnings("unchecked")
        final Map<String, String> enrollmentCAAuthorizationModes = (Map<String, String>) getCapabilityValue(targetCategory, targetType,
                targetModelIdentity, function, capabilityName);
        if (enrollmentCAAuthorizationModes == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("get EnrollmentCAAuthorizationModes: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("get EnrollmentCAAuthorizationModes: returns [{}]", enrollmentCAAuthorizationModes);
        return enrollmentCAAuthorizationModes;
    }

    /**
     * Gets from Capability Model if the deprecated enrollmentAuthority attribute of EnrollmentServer MO is used for the given node reference.
     *
     * @param normNodeRef
     *            the node reference.
     * @return true if the deprecated enrollmentAuthority attribute of EnrollmentServer MO is used, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public boolean isDeprecatedEnrollmentAuthorityUsed(final NormalizableNodeReference normNodeRef) {

        final String inputParams = "normNodeRef [" + normNodeRef + "]";
        logger.debug("is DeprecatedEnrollmentAuthorityUsed: starts for {}", inputParams);

        if (normNodeRef == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is DeprecatedEnrollmentAuthorityUsed: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        boolean isSupported = false;
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        isSupported = isDeprecatedEnrollmentAuthorityUsed(targetCategory, targetType, targetModelIdentity);
        logger.debug("is DeprecatedEnrollmentAuthorityUsed: returns [{}]", isSupported);
        return isSupported;
    }

    /**
     * Gets from Capability Model if the deprecated enrollmentAuthority attribute of EnrollmentServer MO is used for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return true if the deprecated enrollmentAuthority attribute of EnrollmentServer MO is used, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    private boolean isDeprecatedEnrollmentAuthorityUsed(final String targetCategory, final String targetType, final String targetModelIdentity)
    {

        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("is DeprecatedEnrollmentAuthorityUsed: starts for {}", inputParams);

        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is DeprecatedEnrollmentAuthorityUsed: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_DEPRECATED_ENROLLMENT_AUTHORITY_USED;
        final Boolean isSupported = (Boolean) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function, capabilityName);
        if (isSupported == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("is DeprecatedEnrollmentAuthorityUsed: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("is DeprecatedEnrollmentAuthorityUsed: returns [{}]", isSupported);
        return isSupported;
    }

    /**
     * Gets from Capability Model if the deprecated authorityType attribute of EnrollmentAuthority MO is supported for the given node reference.
     *
     * Some node types (AXE nodes) completely hide such attribute and neither allow read access to it.
     *
     * @param normNodeRef
     *            the node reference.
     * @return true if the deprecated authorityType attribute of EnrollmentAuthority MO is supported, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public boolean isDeprecatedAuthorityTypeSupported(final NormalizableNodeReference normNodeRef) {

        final String inputParams = "normNodeRef [" + normNodeRef + "]";
        logger.debug("is DeprecatedAuthorityTypeSupported: starts for {}", inputParams);

        if (normNodeRef == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is DeprecatedAuthorityTypeSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        boolean isSupported = false;
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        isSupported = isDeprecatedAuthorityTypeSupported(targetCategory, targetType, targetModelIdentity);
        logger.debug("is DeprecatedAuthorityTypeSupported: returns [{}]", isSupported);
        return isSupported;
    }

    /**
     * Gets from Capability Model if the deprecated authorityType attribute of EnrollmentAuthority MO is supported for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return true if the deprecated authorityType attribute of EnrollmentAuthority MO is supported, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    private boolean isDeprecatedAuthorityTypeSupported(final String targetCategory, final String targetType, final String targetModelIdentity)
    {

        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("is DeprecatedAuthorityTypeSupported: starts for {}", inputParams);

        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is DeprecatedAuthorityTypeSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_DEPRECATED_AUTHORITY_TYPE_SUPPORTED;
        final Boolean isSupported = (Boolean) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function, capabilityName);
        if (isSupported == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("is DeprecatedAuthorityTypeSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("is DeprecatedAuthorityTypeSupported: returns [{}]", isSupported);
        return isSupported;
    }

    /**
     * Gets from Capability Model the default IDs of NodeCredential MO for all supported certificate types for the given node reference.
     *
     * @param normNodeRef
     *            The node reference.
     * @return the default IDs of NodeCredential MO for all supported certificate types.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined
     */
    public Map<String, String> getComEcimDefaultNodeCredentialIds(final NormalizableNodeReference normNodeRef) {
        final String inputParams = "normNodeRef [" + normNodeRef + "]";
        logger.debug("get ComEcimDefaultNodeCredentialIds: starts for {}", inputParams);
        if (normNodeRef == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get ComEcimDefaultNodeCredentialIds: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        return getComEcimDefaultNodeCredentialIds(targetCategory, targetType, targetModelIdentity);
    }

    /**
     * Gets from Capability Model the default IDs of NodeCredential MO for all supported certificate types for the given node model info.
     *
     * @param nodeModelInfo
     *            The node model info.
     * @return the default IDs of NodeCredential MO for all supported certificate types.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined
     */
    public Map<String, String> getComEcimDefaultNodeCredentialIds(final NodeModelInformation nodeModelInfo) {
        final String inputParams = "nodeModelInfo [" + nodeModelInfo + "]";
        logger.debug("get ComEcimDefaultNodeCredentialIds: starts for {}", inputParams);
        if (nodeModelInfo == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get ComEcimDefaultNodeCredentialIds: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String targetCategory = getTargetCategoryFromNodeModelInfo(nodeModelInfo);
        final String targetType = getTargetTypeFromNodeModelInfo(nodeModelInfo);
        final String targetModelIdentity = getTargetModelIdentityFromNodeModelInformation(nodeModelInfo);
        return getComEcimDefaultNodeCredentialIds(targetCategory, targetType, targetModelIdentity);
    }

    /**
     * Gets from Capability Model the default IDs of NodeCredential MO for all supported certificate types for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return the default IDs of NodeCredential MO for all supported certificate types or null.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal.
     */
    public Map<String, String> getComEcimDefaultNodeCredentialIds(final String targetCategory, final String targetType,
            final String targetModelIdentity) {
        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("get ComEcimDefaultNodeCredentialIds: starts for {}", inputParams);
        if (targetCategory == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get ComEcimDefaultNodeCredentialIds: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_COM_ECIM_DEFAULT_NODE_CREDENTIAL_ID;
        @SuppressWarnings("unchecked")
        final Map<String, String> defaultNodeCredentialIds = (Map<String, String>) getCapabilityValue(targetCategory, targetType, targetModelIdentity,
                function, capabilityName);
        logger.debug("get ComEcimDefaultNodeCredentialIds: returns [{}]", defaultNodeCredentialIds);
        return defaultNodeCredentialIds;
    }

    /**
     * Gets from Capability Model the default IDs of TrustCategory MO for all supported certificate types for the given node reference.
     *
     * @param normNodeRef
     *            The node reference.
     * @return the default IDs of TrustCategory MO for all supported certificate types.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined
     */
    public Map<String, String> getComEcimDefaultTrustCategoryIds(final NormalizableNodeReference normNodeRef) {
        final String inputParams = "normNodeRef [" + normNodeRef + "]";
        logger.debug("get ComEcimDefaultTrustCategoryIds: starts for {}", inputParams);
        if (normNodeRef == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get ComEcimDefaultTrustCategoryIds: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        return getComEcimDefaultTrustCategoryIds(targetCategory, targetType, targetModelIdentity);
    }

    /**
     * Gets from Capability Model the default IDs of TrustCategory MO for all supported certificate types for the given node model info.
     *
     * @param nodeModelInfo
     *            The node model info.
     * @return the default IDs of TrustCategory MO for all supported certificate types.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined
     */
    public Map<String, String> getComEcimDefaultTrustCategoryIds(final NodeModelInformation nodeModelInfo) {
        final String inputParams = "nodeModelInfo [" + nodeModelInfo + "]";
        logger.debug("get ComEcimDefaultTrustCategoryIds: starts for {}", inputParams);
        if (nodeModelInfo == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get ComEcimDefaultTrustCategoryIds: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String targetCategory = getTargetCategoryFromNodeModelInfo(nodeModelInfo);
        final String targetType = getTargetTypeFromNodeModelInfo(nodeModelInfo);
        final String targetModelIdentity = getTargetModelIdentityFromNodeModelInformation(nodeModelInfo);
        return getComEcimDefaultTrustCategoryIds(targetCategory, targetType, targetModelIdentity);
    }

    /**
     * Gets from Capability Model the default IDs of TrustCategory MO for all supported certificate types for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return the default IDs of TrustCategory MO for all supported certificate types or null.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal.
     */
    public Map<String, String> getComEcimDefaultTrustCategoryIds(final String targetCategory, final String targetType,
            final String targetModelIdentity) {
        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("get ComEcimDefaultTrustCategoryIds: starts for {}", inputParams);
        if (targetCategory == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get ComEcimDefaultTrustCategoryIds: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_COM_ECIM_DEFAULT_TRUST_CATEGORY_ID;
        @SuppressWarnings("unchecked")
        final Map<String, String> defaultTrustCategoryIds = (Map<String, String>) getCapabilityValue(targetCategory, targetType, targetModelIdentity,
                function, capabilityName);
        logger.debug("get ComEcimDefaultTrustCategoryIds: returns [{}]", defaultTrustCategoryIds);
        return defaultTrustCategoryIds;
    }

    /**
     * Gets from Capability Model the CRL check workflow name for the given node reference and the given certificate type (IPSEC or OAM).
     *
     * @param nodeRef
     *            the node reference
     * @param certType
     *            the certificate type (IPSEC or OAM)
     * @return the name of the CRL check workflow
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or capability is undefined
     */
    public String getCrlCheckWf(final NodeReference nodeRef, final String certType) {

        final String inputParams = "nodeRef [" + nodeRef + "] certType [" + certType + "]";
        logger.debug("get CrlCheckWf: starts for {}", inputParams);

        final NormalizableNodeReference normNodeRef = getNormalizableNodeReferenceFromNodeReference(nodeRef);
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeReference = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeReference);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        final String targetParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        String workflowName = null;
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CRL_CHECK_WORKFLOWS;
        @SuppressWarnings("unchecked")
        final Map<String, String> workflows = (Map<String, String>) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function,
                capabilityName);
        if (workflows != null) {
            workflowName = workflows.get(certType);
        } else {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + targetParams;
            logger.error("get CrlCheckWf: {}", errorMsg);
        }
        logger.debug("get CrlCheckWf: returns [{}]", workflowName);
        return workflowName;
    }

    /**
     * Checks if the given certificate type for crlCheck enable or disable is supported for the given node reference.
     *
     * @param normNodeRef
     *            The node reference.
     * @param certType
     *            the certificate type
     * @return true if the given certificate type is supported, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the supported certificate types cannot be retrieved.
     */
    public boolean isCertTypeSupportedforCrlCheck(final NormalizableNodeReference normNodeRef, final String certType)
    {

        final String inputParams = "normNodeRef [" + normNodeRef + "] certType [" + certType + "]";
        logger.debug("is CertTypeSupportedforCrlCheck: starts for {}", inputParams);

        if (normNodeRef == null || certType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is CertTypeSupportedforCrlCheck: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        boolean isSupported = false;
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        final List<String> supportedCertTypes = getCrlCheckSupportedCertTypes(targetCategory, targetType, targetModelIdentity);
        if (supportedCertTypes != null && supportedCertTypes.contains(certType)) {
            isSupported = true;
        } else {
            logger.error("is CertTypeSupportedforCrlCheck: certType [{}] is unsupported for {}: supported ones {}"
                    ,certType, inputParams, supportedCertTypes);
        }
        logger.debug("is CertTypeSupportedforCrlCheck: returns [{}]", isSupported);
        return isSupported;
    }

    /**
     * Gets from Capability Model valid certificate types for crl check enable or disable for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return the crl check supported certificate types
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    private List<String> getCrlCheckSupportedCertTypes(final String targetCategory, final String targetType, final String targetModelIdentity)
    {

        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("is CrlCheckSupportedCertTypes: starts for {}", inputParams);
        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is CrlCheckSupportedCertTypes: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_CRL_CHECK_SUPPORTED_CERT_TYPES;
        @SuppressWarnings("unchecked")
        final List<String> supportedCertTypes = (List<String>) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function,
                capabilityName);
        if (supportedCertTypes == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("get CrlCheckSupportedCertTypes: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("get CrlCheckSupportedCertTypes: returns {}", supportedCertTypes);
        return supportedCertTypes;
    }

    /**
     * Gets from Capability Model the default initial OTP (One Time Password) count for the given node model info.
     *
     * @param nodeModelInfo
     *            The node model info.
     * @return the default initial OTP count.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined
     */
    public String getDefaultInitialOtpCount(final NodeModelInformation nodeModelInfo) {

        logger.debug("get DefaultInitialOtpCount: starts for [{}]", nodeModelInfo);
        if (nodeModelInfo == null) {
            final String errorMsg = "null Node Model Info";
            logger.error("get DefaultInitialOtpCount: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String targetCategory = getTargetCategoryFromNodeModelInfo(nodeModelInfo);
        final String targetType = getTargetTypeFromNodeModelInfo(nodeModelInfo);
        final String targetModelIdentity = getTargetModelIdentityFromNodeModelInformation(nodeModelInfo);
        final String defaultInitialOtpCount = getDefaultInitialOtpCount(targetCategory, targetType, targetModelIdentity);
        logger.debug("get DefaultInitialOtpCount: returns [{}]", defaultInitialOtpCount);
        return defaultInitialOtpCount;
    }

    /**
     * Gets from Capability Model the default initial OTP (One Time Password) count for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return the default initial OTP count.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined
     */
    private String getDefaultInitialOtpCount(final String targetCategory, final String targetType, final String targetModelIdentity)
    {
        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("get DefaultInitialOtpCount: starts for {}", inputParams);
        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get DefaultInitialOtpCount: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_INITIAL_OTP_COUNT;
        final String defaultInitialOtpCount = (String) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function, capabilityName);
        if (defaultInitialOtpCount == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("get DefaultInitialOtpCount: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("get DefaultInitialOtpCount: returns [{}]", defaultInitialOtpCount);
        return defaultInitialOtpCount;
    }

    /**
     * Gets from Capability Model the on demand CRL download workflow name for the given node reference.
     *
     * @param nodeRef
     *            the node reference
     * @return the name of the CRL check workflow
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or capability is undefined
     */
    public String getOnDemandCrlDownloadWf(final NodeReference nodeRef) {

        final String inputParams = "nodeRef [" + nodeRef + "]";
        logger.debug("get OnDemandCrlDownloadWf: starts for {}", inputParams);

        final NormalizableNodeReference normNodeRef = getNormalizableNodeReferenceFromNodeReference(nodeRef);
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeReference = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeReference);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        final String targetParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_ON_DEMAND_CRL_DOWNLOAD_WORKFLOW;
        final String workflowName = (String) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function, capabilityName);
        if (workflowName == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + targetParams;
            logger.error("get OnDemandCrlDownloadWf: {}", errorMsg);
        }
        logger.debug("get OnDemandCrlDownloadWf: returns [{}]", workflowName);
        return workflowName;
    }

    /**
     * This method will validate Node Family for crl Download MO Validation validation will be done only for CPP family Nodes
     *
     * @param normNodeRef
     *            the Normalizable Node Reference
     * @return if NeTypeSupportedForCrlDownloadMoValidation
     */
    public boolean isNeTypeSupportedForCrlDownloadMoValidation(final NormalizableNodeReference normNodeRef) {
        if (NscsCapabilityModelConstants.NSCS_CPP_MOM.equals(getMomType(normNodeRef))) {
            return true;
        }
        return false;
    }

    /**
     * Gets from Capability Model the supported protocol types for ciphers command for the given node reference.
     *
     * @param normNodeRef
     *            The node reference.
     * @return the supported protocol types for ciphers command.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public List<String> getSupportedCipherProtocolTypes(final NormalizableNodeReference normNodeRef) {
        final String inputParams = "normNodeRef [" + normNodeRef + "]";
        logger.debug("get SupportedCipherProtocolTypes: starts for {}", inputParams);
        if (normNodeRef == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get SupportedCipherProtocolTypes: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        final List<String> supportedProtocols = getSupportedCipherProtocolTypes(targetCategory, targetType, targetModelIdentity);
        logger.debug("get SupportedCipherProtocolTypes: returns [{}]", supportedProtocols);
        return supportedProtocols;
    }

    /**
     * Gets from Capability Model the supported protocol types for ciphers command for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return the supported protocol types for ciphers command.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    private List<String> getSupportedCipherProtocolTypes(final String targetCategory, final String targetType, final String targetModelIdentity)
    {
        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("get SupportedCipherProtocolTypes: starts for {}", inputParams);
        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get SupportedCipherProtocolTypes: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_SUPPORTED_CIPHER_PROTOCOL_TYPES;
        @SuppressWarnings("unchecked")
        final List<String> supportedCapabilityProtocols = (List<String>) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function,
                capabilityName);
        if (supportedCapabilityProtocols == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("get SupportedCipherProtocolTypes: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final List<String> supportedProtocols = new ArrayList<String>();
        for (final String supportedCapabilityProtocol : supportedCapabilityProtocols) {
            supportedProtocols.add(convertCiphersProtocolTypes(supportedCapabilityProtocol));
        }
        logger.debug("get SupportedCipherProtocolTypes: returns [{}]", supportedProtocols);
        return supportedProtocols;
    }

    /**
     * Gets from Capability Model the ciphers MO attributes for the supported protocol types for the given node reference.
     *
     * @param normNodeRef
     *            The node reference.
     * @return the ciphers MO attributes.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public Map<String, Map<String, String>> getCipherMoAttributes(final NormalizableNodeReference normNodeRef) {
        final String inputParams = "normNodeRef [" + normNodeRef + "]";
        logger.debug("get CipherMoAttributes: starts for {}", inputParams);
        if (normNodeRef == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get CipherMoAttributes: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        final Map<String, Map<String, String>> attrs = getCipherMoAttributes(targetCategory, targetType, targetModelIdentity);
        logger.debug("get CipherMoAttributes: returns [{}]", attrs);
        return attrs;
    }

    /**
     * Gets from Capability Model the ciphers MO attributes for the supported protocol types for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return the ciphers MO attributes.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public Map<String, Map<String, String>> getCipherMoAttributes(final String targetCategory, final String targetType,
            final String targetModelIdentity) {
        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("get CipherMoAttributes: starts for {}", inputParams);
        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get CipherMoAttributes: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHER_MO_ATTRIBUTES;
        @SuppressWarnings("unchecked")
        final Map<String, Map<String, String>> capabilityAttrs = (Map<String, Map<String, String>>) getCapabilityValue(targetCategory, targetType,
                targetModelIdentity, function, capabilityName);
        if (capabilityAttrs == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("get CipherMoAttributes: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final Map<String, Map<String, String>> attrs = new HashMap<String, Map<String, String>>();
        for (final String capabilityProtocol : capabilityAttrs.keySet()) {
            attrs.put(convertCiphersProtocolTypes(capabilityProtocol), capabilityAttrs.get(capabilityProtocol));
        }
        logger.debug("get CipherMoAttributes: returns [{}]", attrs);
        return attrs;
    }

    /**
     * Gets from Capability Model if the empty value is supported for ciphers for the given node reference.
     *
     * @param normNodeRef
     *            the node reference.
     * @return true if if the empty value is supported for ciphers, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public boolean isEmptyValueSupportedForCiphers(final NormalizableNodeReference normNodeRef) {

        final String inputParams = "normNodeRef [" + normNodeRef + "]";
        logger.debug("is EmptyValueSupportedForCiphers: starts for {}", inputParams);

        if (normNodeRef == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is EmptyValueSupportedForCiphers: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        boolean isSupported = false;
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        isSupported = isEmptyValueSupportedForCiphers(targetCategory, targetType, targetModelIdentity);
        logger.debug("is EmptyValueSupportedForCiphers: returns [{}]", isSupported);
        return isSupported;
    }

    /**
     * Gets from Capability Model if the empty value is supported for ciphers for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return true if if the empty value is supported for ciphers, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    private boolean isEmptyValueSupportedForCiphers(final String targetCategory, final String targetType, final String targetModelIdentity)
    {

        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("is EmptyValueSupportedForCiphers: starts for {}", inputParams);

        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is EmptyValueSupportedForCiphers: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_EMPTY_CIPHER_SUPPORTED;
        final Boolean isSupported = (Boolean) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function, capabilityName);
        if (isSupported == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("is EmptyValueSupportedForCiphers: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("is EmptyValueSupportedForCiphers: returns [{}]", isSupported);
        return isSupported;
    }

    /**
     * Checks if Ciphers Configuration is supported for the given node model information.
     *
     * @param nodeModelInfo
     *            the node model information
     * @return true if the ciphers configuration is supported, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public boolean isCiphersConfigurationSupported(final NodeModelInformation nodeModelInfo) {
        return isCliCommandSupported(nodeModelInfo, CIPHERS_COMMAND);
    }

    /**
     * Checks if CRL Check Command is supported for the given node model information.
     *
     * @param nodeModelInfo
     *            the node model information
     * @return true if the CRL Check Command configuration is supported, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public boolean isCrlCheckCommandSupported(final NodeModelInformation nodeModelInfo) {
        return isCliCommandSupported(nodeModelInfo, CRLCHECK_COMMAND);
    }

    /**
     * Checks if CRL Download Command is supported for the given node model information.
     *
     * @param nodeModelInfo
     *            the node model information
     * @return true if the CRL Download Command configuration is supported, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public boolean isCrlDownloadCommandSupported(final NodeModelInformation nodeModelInfo) {
        return isCliCommandSupported(nodeModelInfo, CRLDOWNLOAD_COMMAND);
    }

    /**
     * Returns if Mock Capability Model is currently used or not.
     *
     * @return true if Mock Capability Model is used, false otherwise.
     */
    public boolean useMockCapabilityModel() {
        final boolean useMock = NscsCapabilityModelProperties.useMockCapabilityModel();
        logger.debug("use MockCapabilityModel: returns [{}]", useMock);
        return useMock;
    }

    /**
     * Get the default value of the given capability.
     *
     * @param capabilityModelName
     *            The name of the oss_capability and oss_capabilitysupport models to consider.
     * @param capabilityName
     *            The name of the capability in the capability model.
     * @return the default value of the capability.
     */
    public Object getDefaultValue(final String capabilityModelName, final String capabilityName) {
        Object capabilityValue = null;
        final Bean<?> bean = getCapabilityModelBeanForOption(useMockCapabilityModel());
        final CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
        try {
            final NscsCapabilityModel nscsCapabilityModel = (NscsCapabilityModel) beanManager.getReference(bean, NscsCapabilityModel.class,
                    creationalContext);
            if (nscsCapabilityModel != null) {
                capabilityValue = nscsCapabilityModel.getDefaultValue(capabilityModelName, capabilityName);
            }
        } finally {
            creationalContext.release();
        }
        return capabilityValue;
    }

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
     * @param function
     *            The name of the oss_capability and oss_capabilitysupport models to consider.
     * @param capabilityName
     *            The name of the capability in the capability model.
     * @return the value of the capability, defined in the capability support model. Or a default value specified in the capability model, if no value
     *         has been defined in the capability support model for the given target type. Or null, if no value is specified for the given capability
     *         in the capability support model and no default value is specified for the given capability.
     */
    public Object getCapabilityValue(final String targetCategory, final String targetType, final String targetModelIdentity, final String function,
            final String capabilityName) {
        Object capabilityValue = null;
        final Bean<?> bean = getCapabilityModelBeanForOption(useMockCapabilityModel());
        final CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
        try {
            final NscsCapabilityModel nscsCapabilityModel = (NscsCapabilityModel) beanManager.getReference(bean, NscsCapabilityModel.class,
                    creationalContext);
            if (nscsCapabilityModel != null) {
                capabilityValue = nscsCapabilityModel.getCapabilityValue(targetCategory, targetType, targetModelIdentity, function, capabilityName);
            }
        } finally {
            creationalContext.release();
        }
        return capabilityValue;
    }

    /**
     * Gets the target model identity associated with the given MIM version for the given target.
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
    public String getTargetModelIdentityFromMimVersion(final String targetCategory, final String targetType, final String mimVersion) {
        String targetModelIdentity = null;
        final Bean<?> bean = getCapabilityModelBeanForOption(useMockCapabilityModel());
        final CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
        try {
            final NscsCapabilityModel nscsCapabilityModel = (NscsCapabilityModel) beanManager.getReference(bean, NscsCapabilityModel.class,
                    creationalContext);
            if (nscsCapabilityModel != null) {
                targetModelIdentity = nscsCapabilityModel.getTargetModelIdentityFromMimVersion(targetCategory, targetType, mimVersion);
            }
        } finally {
            creationalContext.release();
        }
        return targetModelIdentity;
    }

    /**
     * Gets the target model identity associated with the given Product Number for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type which is defined in the namespace of the oss_capabilitysupport model to consider. For oss_capabilitysupport models
     *            for network elements, the target type is typically the NE type.
     * @param productNumber
     *            the Product Number.
     * @return the target model identity or null.
     */
    public String getTargetModelIdentityFromProductNumber(final String targetCategory, final String targetType, final String productNumber) {
        String targetModelIdentity = null;
        final Bean<?> bean = getCapabilityModelBeanForOption(useMockCapabilityModel());
        final CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
        try {
            final NscsCapabilityModel nscsCapabilityModel = (NscsCapabilityModel) beanManager.getReference(bean, NscsCapabilityModel.class,
                    creationalContext);
            if (nscsCapabilityModel != null) {
                targetModelIdentity = nscsCapabilityModel.getTargetModelIdentityFromProductNumber(targetCategory, targetType, productNumber);
            }
        } finally {
            creationalContext.release();
        }
        return targetModelIdentity;
    }

    /**
     * Gets the target category for the given node model information.
     * 
     * Note that NodeModelInformation does not contain target category information so it is intended for TargetTypeInformation.CATEGORY_NODE only.
     *
     * @param nodeModelInfo
     *            the node model information
     * @return the target type
     * @throws NscsCapabilityModelException
     *             if input parameters are invalid.
     */
    private String getTargetCategoryFromNodeModelInfo(final NodeModelInformation nodeModelInfo) {
        logger.debug("get TargetCategoryFromNodeModelInfo: starts for nodeModelInfo [{}]", nodeModelInfo);
        if (nodeModelInfo == null || nodeModelInfo.getNodeType() == null) {
            final String errorMsg = String.format("invalid parameters: nodeModelInfo [%s]", nodeModelInfo);
            logger.error("get TargetCategoryFromNodeModelInfo: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String targetCategory = TargetTypeInformation.CATEGORY_NODE;
        logger.debug("get TargetCategoryFromNodeModelInfo: returns [{}]", targetCategory);
        return targetCategory;
    }

    /**
     * Gets the target type for the given node model information.
     *
     * @param nodeModelInfo
     *            the node model information
     * @return the target type
     * @throws NscsCapabilityModelException
     *             if input parameters are invalid.
     */
    private String getTargetTypeFromNodeModelInfo(final NodeModelInformation nodeModelInfo) {
        logger.debug("get TargetTypeFromNodeModelInfo: starts for nodeModelInfo [{}]", nodeModelInfo);
        if (nodeModelInfo == null || nodeModelInfo.getNodeType() == null) {
            final String errorMsg = "invalid parameters: nodeModelInfo [" + nodeModelInfo + "]";
            logger.error("get TargetTypeFromNodeModelInfo: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String targetType = nodeModelInfo.getNodeType();
        logger.debug("get TargetTypeFromNodeModelInfo: returns [{}]", targetType);
        return targetType;
    }

    /**
     * Get the target model identity (TMI) from the given node model information.
     *
     * Note that NodeModelInformation does not contain target category information so it is intended for TargetTypeInformation.CATEGORY_NODE only.
     *
     * @param nodeModelInfo
     *            the node model information.
     * @return the target model identity or null if it has not been defined.
     */
    private String getTargetModelIdentityFromNodeModelInformation(final NodeModelInformation nodeModelInfo) {
        logger.debug("get TargetModelIdentityFromNodeModelInformation: starts for nodeModelInfo [{}]", nodeModelInfo);
        String targetModelIdentity = null;
        if (nodeModelInfo != null) {
            final String targetCategory = getTargetCategoryFromNodeModelInfo(nodeModelInfo);
            final String targetType = getTargetTypeFromNodeModelInfo(nodeModelInfo);
            final ModelIdentifierType modelIdentifierType = nodeModelInfo.getModelIdentifierType();
            final String modelIdentifier = nodeModelInfo.getModelIdentifier();
            if (modelIdentifierType != null && modelIdentifier != null && targetType != null) {
                switch (modelIdentifierType) {
                case OSS_IDENTIFIER:
                    targetModelIdentity = modelIdentifier;
                    break;
                case MIM_VERSION:
                    targetModelIdentity = getTargetModelIdentityFromMimVersion(targetCategory, targetType, modelIdentifier);
                    break;
                case PRODUCT_NUMBER:
                    targetModelIdentity = getTargetModelIdentityFromProductNumber(targetCategory, targetType, modelIdentifier);
                    break;
                case UNKNOWN:
                    logger.error("get TargetModelIdentityFromNodeModelInformation: UNKNOWN type for nodeModelInfo [{}]", nodeModelInfo);
                    break;
                default:
                    logger.error("get TargetModelIdentityFromNodeModelInformation: wrong type for nodeModelInfo [{}]", nodeModelInfo);
                    break;
                }
                if (targetModelIdentity == null) {
                    logger.warn("get TargetModelIdentityFromNodeModelInformation: got null target model identity for nodeModelInfo [{}]"
                            , nodeModelInfo);
                } else {
                    logger.debug("get TargetModelIdentityFromNodeModelInformation: returns [{}]", targetModelIdentity);
                }
            } else {
                logger.error("get TargetModelIdentityFromNodeModelInformation: invalid nodeModelInfo [{}]", nodeModelInfo);
            }
        } else {
            logger.error("get TargetModelIdentityFromNodeModelInformation: invalid null nodeModelInfo [{}]", nodeModelInfo);
        }
        return targetModelIdentity;
    }

    /**
     * Gets the NormalizableNodeReference from the given NodeReference.
     *
     * @param nodeRef
     *            the NodeReference
     * @return the NormalizableNodeReference
     * @throws NscsCapabilityModelException
     *                  if input parameters are illegal or the capability is undefined.
     */
    private NormalizableNodeReference getNormalizableNodeReferenceFromNodeReference(final NodeReference nodeRef) {

        logger.debug("Get NormalizableNodeReference: starts for NodeReference [{}]", nodeRef);

        if (nodeRef == null) {
            final String errorMsg = "null nodeRef";
            logger.error("get NormalizableNodeReference: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }

        NormalizableNodeReference normNodeRef = null;
        if (nodeRef instanceof NormalizableNodeReference) {
            normNodeRef = (NormalizableNodeReference) nodeRef;
        } else {
            normNodeRef = reader.getNormalizableNodeReference(nodeRef);
        }

        logger.debug("Get NormalizableNodeReference: returns [{}]", normNodeRef);
        return normNodeRef;
    }

    /**
     * Converts the given ciphers protocol type from Capability Model format to command format.
     *
     * @param capabilityProtocolType
     *            the protocol type in Capability Model format
     * @return the protocol type in command format.
     * @throws NscsCapabilityModelException
     *             if invalid value in capability model.
     */
    private String convertCiphersProtocolTypes(final String capabilityProtocolType) {
        String commandProtocolType = null;
        if (capabilityProtocolType != null) {
            switch (capabilityProtocolType) {
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_PROTOCOL_TYPE_SSH: {
                commandProtocolType = CiphersConstants.PROTOCOL_TYPE_SSH;
            }
                break;
            case NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_PROTOCOL_TYPE_TLS: {
                commandProtocolType = CiphersConstants.PROTOCOL_TYPE_TLS;
            }
                break;
            default:
                break;
            }
        }
        if (commandProtocolType == null) {
            final String errorMsg = "invalid capability model value [" + capabilityProtocolType + "] for ciphers protocol type";
            logger.error("convert CiphersProtocolTypes: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        return commandProtocolType;
    }

    /**
     * Get the instance of capability model service for the given qualifier.
     *
     * @param isCapabilityModelMock
     *            the qualifier (true if mock is to be used, false if real capability model is to be used)
     * @return the bean instance or null (on error or if no instance or more than one instance found).
     */
    @SuppressWarnings({ "unchecked" })
    private Bean<?> getCapabilityModelBeanForOption(final boolean isCapabilityModelMock) {
        try {
            final Set<Bean<?>> beans = beanManager.getBeans(NscsCapabilityModel.class, new NscsCapabilityModelTypeQualifier(isCapabilityModelMock));
            if (beans.size() == 1) {
                final Bean<NscsCapabilityModel> bean = (Bean<NscsCapabilityModel>) beans.iterator().next();
                final String msg = "Found capability model [" + bean != null ? bean.getClass().getCanonicalName()
                        : bean + "] registered for option isMock [" + isCapabilityModelMock + "]";
                logger.debug(msg);
                return bean;
            } else if (beans.size() < 1) {
                final String msg = "No capability model registered for option isMock [" + isCapabilityModelMock + "]";
                logger.error(msg);
            } else {
                final String msg = "Multiple capability model registered for option isMock [" + isCapabilityModelMock + "]";
                logger.error(msg);
            }
        } catch (final Exception e) {
            final String msg = "Internal Error retrieving capability model registered for option isMock [" + isCapabilityModelMock + "]";
            logger.error(msg);
        }
        return null;
    }

    /**
     * Auxiliary class modeling the capability model implementation qualifier.
     */
    private class NscsCapabilityModelTypeQualifier extends AnnotationLiteral<NscsCapabilityModelType> implements NscsCapabilityModelType {

        private static final long serialVersionUID = 3112432601092125236L;
        private final boolean isCapabilityModelMock;

        public NscsCapabilityModelTypeQualifier(final boolean isCapabilityModelMock) {
            this.isCapabilityModelMock = isCapabilityModelMock;
        }

        @Override
        public boolean isCapabilityModelMock() {
            return this.isCapabilityModelMock;
        }
    }

    /**
     * Get default password Hash Algorithm for the given node model info from Capability Model.
     * @param normNodeRef the normNodeRef
     * @return the default password hash algo
     */
    public String getDefaultPasswordHashAlgorithm(final NormalizableNodeReference normNodeRef) {
        final String logMsg = "get DefaultPasswordHashAlgorithm:";
        final String errorMsg = "null Normalizable Node Reference Info";
        final String SHA512_CRYPT_HASH_ALGORITHM = "SHA-512-CRYPT";

        logger.debug("{} starts for [{}]", logMsg, normNodeRef);
        if (normNodeRef == null) {
            logger.error("Error in {} {}", logMsg, errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        final String defaultPasswordHashAlgorithm = isEvoc(normNodeRef)
                ? SHA512_CRYPT_HASH_ALGORITHM
                : getDefaultPasswordHashAlgorithm(targetCategory, targetType, targetModelIdentity);
        logger.debug("{} returns [{}]", logMsg, defaultPasswordHashAlgorithm);
        return defaultPasswordHashAlgorithm;
    }

    private String getDefaultPasswordHashAlgorithm(final String targetCategory, final String targetType, final String targetModelIdentity) {
        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity [" + targetModelIdentity + "]";
        final String logMsg = "get Default Password Hash Algorithm:";

        logger.debug("{} starts for {}", logMsg, inputParams);
        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("Error found in {} {}", logMsg, errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_PASSPHRASE_HASH_ALGORITHM;
        final String defaultPasswordHashAlgorithm = (String) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function, capabilityName);
        if (defaultPasswordHashAlgorithm == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("Error from {} {}", logMsg, errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("{} returns {}", logMsg, defaultPasswordHashAlgorithm);
        return defaultPasswordHashAlgorithm;
    }
    
    /**
     * Gets from Capability Model if the given trust category type is supported for the given node reference.
     * 
     * @param normNodeRef
     *            The node reference.
     * @param trustCategory
     *            The trust category type.
     * @return true if the given trust category type is supported, false otherwise.
     * 
     */
    public boolean isTrustCategoryTypeSupported(final NormalizableNodeReference normNodeRef, final String trustCategory) {
        final String inputParams = "normNodeRef [" + normNodeRef + "] trustCategoryType [" + trustCategory + "]";
        logger.debug("is TrustCategoryTypeSupported: starts for {}", inputParams);
        if (normNodeRef == null || trustCategory == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is TrustCategoryTypeSupported: {} ", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        boolean isSupported = false;
        final List<String> supportedTrustCategoryTypes = getSupportedTrustCategoryTypes(targetCategory, targetType, targetModelIdentity);
        if (supportedTrustCategoryTypes.contains(trustCategory)) {
            isSupported = true;
        } else {
            logger.error("is TrustCategoryTypeSupported: unsupported for {}", inputParams);
        }
        logger.debug("is TrustCategoryTypeSupported: returns [{}]", isSupported);
        return isSupported;
    }

    /**
     * Gets from Capability Model the supported trust category types for the given target.
     * 
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return the supported trust category types.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    private List<String> getSupportedTrustCategoryTypes(final String targetCategory, final String targetType, final String targetModelIdentity) {

        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("get SupportedTrustCategoryTypes: starts for {}", inputParams);

        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get SupportedTrustCategoryTypes: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_SUPPORTED_TRUST_CATEGORIES;
        @SuppressWarnings("unchecked")
        final List<String> supportedCertTypes = (List<String>) getCapabilityValue(targetCategory, targetType,
                targetModelIdentity, function, capabilityName);
        if (supportedCertTypes == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("get SupportedTrustCategoryTypes: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("get SupportedTrustCategoryTypes: returns [{}]", supportedCertTypes);
        return supportedCertTypes;
    }

    /**
     * Gets from Capability Model the push M2M user name for the given node reference
     *
     * @param nodeModelInfo
     *            The node model info.
     * @return the push M2M user name.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public String getPushM2MUser(final NodeModelInformation nodeModelInfo) {
        final String inputParams = "nodeModelInfo [" + nodeModelInfo + "]";
        logger.debug("getPushM2MUser: starts for {}", inputParams);
        if (nodeModelInfo == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("getPushM2MUser: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String targetCategory = getTargetCategoryFromNodeModelInfo(nodeModelInfo);
        final String targetType = getTargetTypeFromNodeModelInfo(nodeModelInfo);
        // sending targetModelIdentity as null to avoid ERROR log in this scenario.
        final String targetModelIdentity = null;
        return getSupportedPushM2MUser(targetCategory, targetType, targetModelIdentity);
    }

    /**
     * Checks whether given node is Evo8300. The behaviour of EVo8300 is different to RNC nodes for some use cases. Since the node type of Evo8300 is same as RNC it is not possible to differentiate
     * between Evo and RNC nodes. For this reason this hard coded check is used to identify the Evo8300 node, when there is a behavior change with RNC node.
     *
     * @param normRef
     *            the node reference.
     * @return true if the node type EVO8300, false otherwise.
     */
    public boolean isEvoc(final NormalizableNodeReference normRef) {
        if (normRef == null) {
            logger.error("isEvoc: null Normalizable Node Reference Info");
            return false;
        }
        String rncType = "";
        Collection<CmObject> cmResponse = null;
        boolean attributexists = false;
        attributexists = rncAttributeExists(normRef);
        if (attributexists) {
            cmResponse = reader.getMOAttribute(normRef, MOTYPE, MONAMESPACE, RNCTYPE).getCmObjects();
            logger.debug(" isEvoc: cmresponse returned for node : {}", cmResponse);
            if (cmResponse == null) {
                logger.error("isEvoc: no object returned for node {} mo {} ns {} attr {}", normRef, MOTYPE, MONAMESPACE, RNCTYPE);
                return false;
            }
            try {
                final Iterator<CmObject> cmObjectIterator = cmResponse.iterator();
                while (cmObjectIterator.hasNext()) {
                    final Map<String, Object> attributes = cmObjectIterator.next().getAttributes();
                    rncType = attributes.get(RNCTYPE).toString();
                    logger.debug("isEvoc: rncType {} returned for node {} mo {} ns {} attr {}", rncType, normRef, MOTYPE, MONAMESPACE, RNCTYPE);
                    if (rncType.equals(RNC8300)) {
                        return true;
                    }
                }
            } catch (final NullPointerException exception) {
                logger.error("isEvoc: no attributes returned for node {} mo {} ns {} attr {}", normRef, MOTYPE, MONAMESPACE, RNCTYPE);
            }
        }
        return false;
    }

    /**
     * Gets from Capability Model the push M2M user name for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return the push M2M user name.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    private String getSupportedPushM2MUser(final String targetCategory, final String targetType, final String targetModelIdentity) {

        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("getSupportedPushM2MUser: starts for {}", inputParams);

        if (targetType == null || targetCategory == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("getSupportedPushM2MUser: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_PUSH_M2M_USER;
        final String pushM2MUser = (String) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function, capabilityName);
        logger.debug("getSupportedPushM2MUser: returns [{}]", pushM2MUser);
        return pushM2MUser;
    }

    private boolean rncAttributeExists(final NormalizableNodeReference normNodeRef) {
        final String targetCategory = normNodeRef.getTargetCategory();
        final String targetType = normNodeRef.getNeType();
        final String targetModelIdentity = normNodeRef.getOssModelIdentity();
        logger.info(" Name space for the MO : {} and  Type for Mo  is {}", MONAMESPACE, MOTYPE);
        return nscsModelServiceImpl.isMoAttributeExists(targetCategory, targetType, targetModelIdentity, MONAMESPACE, MOTYPE, RNCTYPE);
    }

    /**
     * Get Ntp Remove Workflow for the given node from Capability Model.
     *
     * @param normNodeRef
     *            the normNodeRef
     * @return The NTP Remove workflow name for the given node type.
     */
    public String getNtpRemoveWorkflow(final NormalizableNodeReference normNodeRef) {
        final String logMsg = "get TrustedNtpRemoveWorkflow:";
        final String errorMsg = "null Normalizable Node Reference Info";

        logger.debug("{} starts for [{}]", logMsg, normNodeRef);
        if (normNodeRef == null) {
            logger.error("Error in {} {}", logMsg, errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        final String trustedNtpRemoveWorkflow = getNtpRemoveWorkflow(targetCategory, targetType, targetModelIdentity);
        logger.debug("{} returns [{}]", logMsg, trustedNtpRemoveWorkflow);
        return trustedNtpRemoveWorkflow;
    }

    private String getNtpRemoveWorkflow(final String targetCategory, final String targetType, final String targetModelIdentity) {
        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity [" + targetModelIdentity + "]";
        final String logMsg = "get Trusted Ntp Remove Workflow name";

        logger.debug("{} starts for {}", logMsg, inputParams);
        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("Error found in {} {}", logMsg, errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_NTP_REMOVE_WORKFLOW;
        final String ntpRemoveWorkflow = (String) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function, capabilityName);
        if (ntpRemoveWorkflow == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("Error occured during {} : {}", logMsg, errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("{} returns {}", logMsg, ntpRemoveWorkflow);
        return ntpRemoveWorkflow;
    }

    /**
     * Get Ntp Configure Workflow for the given node model info from Capability Model.
     *
     * @param normNodeRef
     *            the normNodeRef
     * @return the Ntp configure workflow
     */
    public String getNtpConfigureWorkflow(final NormalizableNodeReference normNodeRef) {
        final String logMsg = "get NtpConfigureWorkflow:";
        final String errorMsg = "null Normalizable Node Reference Info";

        logger.debug("{} starts for [{}]", logMsg, normNodeRef);
        if (normNodeRef == null) {
            logger.error("Error in {} {}", logMsg, errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        final String ntpConfigureWorkflow = getNtpConfigureWorkflow(targetCategory, targetType, targetModelIdentity);
        logger.debug("{} returns [{}]", logMsg, ntpConfigureWorkflow);
        return ntpConfigureWorkflow;
    }

    private String getNtpConfigureWorkflow(final String targetCategory, final String targetType, final String targetModelIdentity) {
        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        final String logMsg = "get Ntp Configuration Workflow:";

        logger.debug("{} starts for {}", logMsg, inputParams);
        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("Error found in {} {}", logMsg, errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_NTP_CONFIGURE_WORKFLOW;
        final String ntpConfigureWorkflow = (String) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function, capabilityName);
        if (ntpConfigureWorkflow == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("Error from {} {}", logMsg, errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("{} returns {}", logMsg, ntpConfigureWorkflow);
        return ntpConfigureWorkflow;
    }

    /**
     * Gets from Capability Model if the ssh privatekey import is supported for the given node reference.
     *
     * @param normNodeRef
     *            the node reference.
     * @return true if targetType supports ssh privatekey import, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    public boolean isNodeSshPrivateKeyImportSupported(final NormalizableNodeReference normNodeRef) {

        final String inputParams = "normNodeRef [" + normNodeRef + "]";
        logger.debug("is NodeSshPrivateKeyImportSupported: starts for {}", inputParams);

        if (normNodeRef == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is NodeSshPrivateKeyImportSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        boolean isSupported = false;
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        isSupported = isNodeSshPrivateKeyImportSupported(targetCategory, targetType, targetModelIdentity);
        logger.debug("is NodeSshPrivateKeyImportSupported: returns [{}]", isSupported);
        return isSupported;
    }

    /**
     * Gets from Capability Model if the ssh privatekey import is supported for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return true if targetType supports ssh privatekey import, false otherwise.
     * @throws NscsCapabilityModelException
     *             if input parameters are illegal or the capability is undefined.
     */
    private boolean isNodeSshPrivateKeyImportSupported(final String targetCategory, final String targetType, final String targetModelIdentity) {

        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("is NodeSshPrivateKeyImportSupported: starts for {}", inputParams);

        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("is NodeSshPrivateKeyImportSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_IS_NODE_SSH_PRIVATEKEY_IMPORT_SUPPORTED;
        final Boolean isSupported = (Boolean) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function, capabilityName);
        if (isSupported == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("is NodeSshPrivateKeyImportSupported: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("is NodeSshPrivateKeyImportSupported: returns [{}]", isSupported);
        return isSupported;
    }

    /**
     * Get Ldap Configure Workflow for the given node model info from Capability Model.
     *
     * @param normNodeRef
     *            the normNodeRef
     * @return the Ldap configure workflow
     */
    public String getLdapConfigureWorkflow(final NormalizableNodeReference normNodeRef) {
        final String logMsg = "get LdapConfigureWorkflow:";
        final String errorMsg = "null Normalizable Node Reference Info";

        logger.debug("{} starts for [{}]", logMsg, normNodeRef);
        if (normNodeRef == null) {
            logger.error("Error in {} {}", logMsg, errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        final String ldapConfigureWorkflow = getLdapConfigureWorkflow(targetCategory, targetType, targetModelIdentity);
        logger.debug("{} returns [{}]", logMsg, ldapConfigureWorkflow);
        return ldapConfigureWorkflow;
    }

    private String getLdapConfigureWorkflow(final String targetCategory, final String targetType, final String targetModelIdentity) {
        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        final String logMsg = "get Ldap Configuration Workflow:";

        logger.debug("{} starts for {}", logMsg, inputParams);
        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("Error found in {} {}", logMsg, errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_CONFIGURE_LDAP_WORKFLOW;
        final String ldapConfigureWorkflow = (String) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function, capabilityName);
        if (ldapConfigureWorkflow == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("Error from {} {}", logMsg, errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("{} returns {}", logMsg, ldapConfigureWorkflow);
        return ldapConfigureWorkflow;
    }

    /**
     * Get Ldap MO name for the given node model info from Capability Model.
     *
     * @param normNodeRef
     *            the normNodeRef
     * @return the Ldap configure MO name
     */
    public String getLdapMoName(final NormalizableNodeReference normNodeRef) {
        final String logMsg = "get LdapMoName:";
        final String errorMsg = "null Normalizable Node Reference Info";

        logger.debug("{} starts for [{}]", logMsg, normNodeRef);
        if (normNodeRef == null) {
            logger.error("Error in {} {}", logMsg, errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        final String ldapMoName = getLdapMoName(targetCategory, targetType, targetModelIdentity);
        logger.debug("{} returns [{}]", logMsg, ldapMoName);
        return ldapMoName;
    }

    private String getLdapMoName(final String targetCategory, final String targetType, final String targetModelIdentity) {
        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        final String logMsg = "get Ldap MO Name:";

        logger.debug("{} starts for {}", logMsg, inputParams);
        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("Error found in {} {}", logMsg, errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_LDAP_MO_NAME;
        final String ldapMoName = (String) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function, capabilityName);
        if (ldapMoName == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("Error from {} {}", logMsg, errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("{} returns {}", logMsg, ldapMoName);
        return ldapMoName;
    }

    /**
     * Get Default OTP Validity period in minutes for the given node model from Capability Model.
     *
     * @param nodeModelInfo
     *            the NodeModelInformation.
     * @return the default OTP validity period in minutes
     */
    public String getDefaultOtpValidityPeriodInMinutes(final NodeModelInformation nodeModelInfo) {
        final String logMsg = "get Default Otp Validity Period InMinutes:";
        final String errorMsg = "null Normalizable Node Reference Info";

        logger.debug("{} starts for [{}]", logMsg, nodeModelInfo);
        if (nodeModelInfo == null) {
            logger.error("Error in {} {}", logMsg, errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String targetCategory = getTargetCategoryFromNodeModelInfo(nodeModelInfo);
        final String targetType = getTargetTypeFromNodeModelInfo(nodeModelInfo);
        final String targetModelIdentity = getTargetModelIdentityFromNodeModelInformation(nodeModelInfo);
        final String defaultOtpValidityPeriod = getDefaultOtpValidityPeriodInMinutes(targetCategory, targetType, targetModelIdentity);
        logger.debug("{} returns [{}]", logMsg, defaultOtpValidityPeriod);
        return defaultOtpValidityPeriod;
    }

    /**
     * Get Default OTP Validity period in minutes for the given node model from Capability Model.
     *
     * @param normNodeRef
     *            the normNodeRef
     * @return the default OTP validity period in minutes
     */
    public String getDefaultOtpValidityPeriodInMinutes(final NormalizableNodeReference normNodeRef) {
        final String logMsg = "get Default Otp Validity Period InMinutes:";
        final String errorMsg = "null Normalizable Node Reference Info";

        logger.debug("{} starts for [{}]", logMsg, normNodeRef);
        if (normNodeRef == null) {
            logger.error("Error in {} {}", logMsg, errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        final String defaultOtpValidityPeriod = getDefaultOtpValidityPeriodInMinutes(targetCategory, targetType, targetModelIdentity);
        logger.debug("{} returns [{}]", logMsg, defaultOtpValidityPeriod);
        return defaultOtpValidityPeriod;
    }

    private String getDefaultOtpValidityPeriodInMinutes(final String targetCategory, final String targetType, final String targetModelIdentity) {
        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        final String logMsg = "get Default Otp Validity Period In Minutes:";

        logger.debug("{} starts for {}", logMsg, inputParams);
        if (targetCategory == null || targetType == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("Error found in {} {}", logMsg, errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityName = NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_OTP_VALIDITY_PERIOD_IN_MINUTES;
        final String defaultOtpValidityPeriod = (String) getCapabilityValue(targetCategory, targetType, targetModelIdentity, function, capabilityName);
        if (defaultOtpValidityPeriod == null) {
            final String errorMsg = "undefined capability [" + capabilityName + "] in function [" + function + "] for " + inputParams;
            logger.error("Error from {} {}", logMsg, errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        logger.debug("{} returns {}", logMsg, defaultOtpValidityPeriod);
        return defaultOtpValidityPeriod;
    }

    /**
     * Get Default Enrollment CA TrustCategory Id for the given node model info from Capability
     * Model.
     *
     * @param nodeModelInfo
     *            The Node Model Information
     *
     * @return defaultEnrollmentCaTrustCategoryId the default IDs of Certificates MO for all
     *         supported certificate types.
     *
     */
    public Map<String, String> getDefaultEnrollmentCaTrustCategoryId(final NodeModelInformation nodeModelInfo) {
        final String inputParams = "nodeModelInfo [" + nodeModelInfo + "]";
        logger.debug("get CbpOiDefaultTrustCategoryIds: starts for {}", inputParams);
        if (nodeModelInfo == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get CbpOiDefaultTrustCategoryIds: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }

        final String targetCategory = getTargetCategoryFromNodeModelInfo(nodeModelInfo);
        final String targetType = getTargetTypeFromNodeModelInfo(nodeModelInfo);
        final String targetModelIdentity = getTargetModelIdentityFromNodeModelInformation(nodeModelInfo);
        return getDefaultEnrollmentCaTrustCategoryId(targetCategory, targetType, targetModelIdentity);
    }

    /**
     * Get Default Enrollment CA TrustCategory Id for the given node reference info from Capability
     * Model.
     *
     * @param normNodeRef
     *            The node reference.
     *
     * @return defaultEnrollmentCaTrustCategoryId the default IDs of Certificates MO for all
     *         supported certificate types.
     *
     */
    public Map<String, String> getDefaultEnrollmentCaTrustCategoryId(final NormalizableNodeReference normNodeRef) {
        final String inputParams = "normNodeRef [" + normNodeRef + "]";
        logger.debug("get CbpOiDefaultTrustCategoryIds: starts for {}", inputParams);
        if (normNodeRef == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get CbpOiDefaultTrustCategoryIds: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        String targetCategory = normNodeRef.getTargetCategory();
        String targetType = normNodeRef.getNeType();
        String targetModelIdentity = normNodeRef.getOssModelIdentity();
        if (targetCategory == null || targetType == null) {
            final NodeReference nodeRef = new NodeRef(normNodeRef.getFdn());
            final NormalizableNodeReference normalized = reader.getNormalizedNodeReference(nodeRef);
            targetCategory = normalized.getTargetCategory();
            targetType = normalized.getNeType();
            targetModelIdentity = normalized.getOssModelIdentity();
        }
        return getDefaultEnrollmentCaTrustCategoryId(targetCategory, targetType, targetModelIdentity);
    }

    /**
     * Gets from Capability Model the default IDs of TrustCategory MO for all supported certificate
     * types for the given target.
     *
     * @param targetCategory
     *            the target category. For network elements, the target category is typically
     *            TargetTypeInformation.CATEGORY_NODE.
     * @param targetType
     *            the target type. For network elements, the target type is typically the NE type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return the default IDs of TrustCategory MO for all supported certificate types or null.
     *
     */
    private Map<String, String> getDefaultEnrollmentCaTrustCategoryId(final String targetCategory, final String targetType,
            final String targetModelIdentity) {
        final String inputParams = "targetCategory [" + targetCategory + "] targetType [" + targetType + "] targetModelIdentity ["
                + targetModelIdentity + "]";
        logger.debug("get CbpOiDefaultTrustCategoryIds: starts for {}", inputParams);
        if (targetCategory == null) {
            final String errorMsg = "invalid parameters " + inputParams;
            logger.error("get CbpOiDefaultTrustCategoryIds: {}", errorMsg);
            throw new NscsCapabilityModelException(errorMsg);
        }
        final String function = NscsCapabilityModelConstants.NSCS_CAPABILITY_MODEL;
        final String capabilityNameOamCmpCa = NscsCapabilityModelConstants.NSCS_CAPABILITY_DEFAULT_ENROLL_CA_TRUST_CATEGORY_ID;
        @SuppressWarnings("unchecked")
        final Map<String, String> defaultCmpCaTrustCategoryIds = (Map<String, String>) getCapabilityValue(targetCategory, targetType,
                targetModelIdentity, function, capabilityNameOamCmpCa);
        logger.debug("get CbpOiDefaultTrustCategoryIds: returns [{}]", defaultCmpCaTrustCategoryIds);
        return defaultCmpCaTrustCategoryIds;
    }
}

