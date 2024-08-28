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
package com.ericsson.nms.security.nscs.handler.command.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.CapabilityGetCommand;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelConstants;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.command.utility.CapabilityGetResponseBuilder;
import com.ericsson.nms.security.nscs.handler.command.utility.NscsCapabilityDetails;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;

@CommandType(NscsCommandType.CAPABILITY_GET)
@Local(CommandHandlerInterface.class)
public class CapabilityGetCommandHandler implements CommandHandler<CapabilityGetCommand>, CommandHandlerInterface {

    private static final String TARGET_MODEL_IDENTITY_NULL_AS_STRING = "<null>";

    @Inject
    private NscsLogger logger;

    @Inject
    private NscsModelServiceImpl nscsModelServiceImpl;

    @Override
    public NscsCommandResponse process(final CapabilityGetCommand command, final CommandContext context) {

        logger.commandHandlerStarted(command);

        try {
            final List<NscsCapabilityDetails> capabilities = buildRequestedCapabilities(command);
            final CapabilityGetResponseBuilder responseBuilder = new CapabilityGetResponseBuilder();
            responseBuilder.addHeader();
            responseBuilder.addAllCapabilitiesRows(capabilities);
            logger.commandHandlerFinishedWithSuccess(command, "Command successfully completed.");
            return responseBuilder.getResponse();
        } catch (final Exception e) {
            final String errorMsg = String.format("Command failed due to %s.", NscsLogger.stringifyException(e));
            logger.commandHandlerFinishedWithError(command, errorMsg);
            logger.error(errorMsg, e);
            return NscsCommandResponse.message(errorMsg);
        }
    }

    /**
     * Builds the capabilities requested from the given command.
     * 
     * @param command
     *            the received command.
     * @return the list of requested capabilities.
     */
    private List<NscsCapabilityDetails> buildRequestedCapabilities(final CapabilityGetCommand command) {
        final List<NscsCapabilityDetails> capabilities = new ArrayList<>();
        final Map<String, Map<String, Set<String>>> requestedTargets = getRequestedTargets(command);
        final Map<String, Set<String>> requestedCapabilities = getRequestedCapabilities(command);
        for (final Entry<String, Set<String>> entry : requestedCapabilities.entrySet()) {
            final String model = entry.getKey();
            final Set<String> capabilityNames = entry.getValue();
            for (final String capabilityName : capabilityNames) {
                final NscsCapabilityDetails capability = buildCapabilityDetails(model, capabilityName, requestedTargets);
                capabilities.add(capability);
            }
        }
        return capabilities;
    }

    /**
     * Builds the requested capability details for the given model and capability name for the given targets (in terms of target category, target type
     * and target model identities).
     * 
     * @param model
     *            the model.
     * @param capabilityName
     *            the capability name.
     * @param requestedTargets
     *            the collection of involved targets (a map with key the target category and value a map with key the target type and value a set of
     *            target model identities).
     * @return the requested capability details.
     */
    private NscsCapabilityDetails buildCapabilityDetails(final String model, final String capabilityName,
            final Map<String, Map<String, Set<String>>> requestedTargets) {

        final Object defaultValue = nscsModelServiceImpl.getDefaultValue(model, capabilityName);
        final NscsCapabilityDetails capability = new NscsCapabilityDetails(model, capabilityName, defaultValue);

        for (final Map.Entry<String, Map<String, Set<String>>> entry : requestedTargets.entrySet()) {
            final String targetCategory = entry.getKey();
            final Map<String, Set<String>> targetTypes = entry.getValue();
            for (final Map.Entry<String, Set<String>> targetTypeEntry : targetTypes.entrySet()) {
                final String targetType = targetTypeEntry.getKey();
                final Set<String> targetModelIdentities = targetTypeEntry.getValue();
                if (targetModelIdentities != null && !targetModelIdentities.isEmpty()) {
                    for (final String targetModelIdentity : targetModelIdentities) {
                        updateCapabilityDetails(capability, model, capabilityName, targetCategory, targetType, targetModelIdentity);
                    }
                } else {
                    updateCapabilityDetails(capability, model, capabilityName, targetCategory, targetType, null);
                }
            }
        }
        return capability;
    }

    /**
     * Updates the given capability details for the given model and capability name and for the given target category, target type and target model
     * identity.
     * 
     * @param capability
     *            the capability details to update.
     * @param model
     *            the model.
     * @param capabilityName
     *            the capability name.
     * @param targetCategory
     *            the target category.
     * @param targetType
     *            the target type.
     * @param targetModelIdentity
     *            the target model identity.
     */
    private void updateCapabilityDetails(final NscsCapabilityDetails capability, final String model, final String capabilityName,
            final String targetCategory, final String targetType, final String targetModelIdentity) {
        final Object value = nscsModelServiceImpl.getCapabilityValue(targetCategory, targetType, targetModelIdentity, model, capabilityName);
        final String notes = null;
        final String nodeType = String.format("%s:%s", targetCategory, targetType);
        capability.addValue(value, nodeType, targetModelIdentity, notes);
    }

    /**
     * Gets the collection of capabilities (in terms of model and capability name) requested from the given received command.
     * 
     * If the capability name specified in the command is null, all the capabilities of all models are requested capabilities.
     * 
     * @param command
     *            the received command.
     * @return the collection of capabilities (a map with key the model and value the set of capability names).
     */
    private Map<String, Set<String>> getRequestedCapabilities(final CapabilityGetCommand command) {
        final Map<String, Set<String>> modelsAndCapabilities = NscsCapabilityModelConstants.getCapabilityModels();
        final String capabilityName = command.getCapabilityName();
        if (capabilityName == null) {
            return modelsAndCapabilities;
        }

        final Map<String, Set<String>> capabilities = new HashMap<>();
        for (final Map.Entry<String, Set<String>> entry : modelsAndCapabilities.entrySet()) {
            final String model = entry.getKey();
            final Set<String> capabilityNames = entry.getValue();
            if (capabilityNames.contains(capabilityName)) {
                capabilities.put(model, Stream.of(capabilityName).collect(Collectors.toCollection(HashSet::new)));
            }
        }
        if (capabilities.isEmpty()) {
            final String errorMessage = String.format("Unsupported capability name [%s] for any models. Allowed values are: %s", capabilityName,
                    modelsAndCapabilities);
            throw new IllegalArgumentException(errorMessage);
        }
        return capabilities;
    }

    /**
     * Gets the collection of targets (in terms of target category, target type and target model identity) requested from the given received command.
     * 
     * If the target type specified in the command is null, all target model identities of all target types under all target categories are requested
     * targets.
     * 
     * @param command
     *            the received command.
     * @return the collection of involved targets (a map with key the target category and value a map with key the target type and value a set of
     *         target model identities).
     */
    private Map<String, Map<String, Set<String>>> getRequestedTargets(final CapabilityGetCommand command) {
        final String targetType = command.getNeType();
        if (targetType == null) {
            return getAllTargets(command);
        } else {
            return getTargetTypeTargets(targetType, command);
        }
    }

    /**
     * Gets the collection of all targets for the given command: all target model identities of all target types under all target categories.
     * 
     * @param command
     *            the received command.
     * @return the collection of all targets (a map with key the target category and value a map with key the target type and value a set of target
     *         model identities).
     */
    private Map<String, Map<String, Set<String>>> getAllTargets(final CapabilityGetCommand command) {
        final Map<String, Map<String, Set<String>>> targets = new HashMap<>();
        final Set<String> targetCategories = nscsModelServiceImpl.getTargetCategories();
        for (final String targetCategory : targetCategories) {
            final Set<String> targetTypes = nscsModelServiceImpl.getTargetTypes(targetCategory);
            final Map<String, Set<String>> targetTypeTargets = new HashMap<>();
            for (final String targetType : targetTypes) {
                final Set<String> targetCategoryAndTypeTargets = getTargetModelIdentities(targetCategory, targetType, command);
                targetTypeTargets.put(targetType, targetCategoryAndTypeTargets);
            }
            targets.put(targetCategory, targetTypeTargets);
        }
        return targets;
    }

    /**
     * Gets the collection of involved targets for the given not null target type and given command.
     * 
     * If the target category is null, all target categories which the given target type belongs to are used, otherwise it shall be a supported target
     * category.
     * 
     * The target type shall be not null and supported for the given target category or for at least one supported target category if target category
     * specified in the command is null.
     * 
     * @param targetType
     *            the not null target type.
     * @param command
     *            the received command.
     * @return the collection of involved targets (a map with key the target category and value a map with key the target type and value a set of
     *         target model identities).
     */
    private Map<String, Map<String, Set<String>>> getTargetTypeTargets(final String targetType, final CapabilityGetCommand command) {
        final String targetCategory = command.getTargetCategory();
        if (targetCategory == null) {
            return getAllTargetCategoriesAndTypeTargets(targetType, command);
        } else {
            return getTargetCategoryAndTypeTargets(targetCategory, targetType, command);
        }
    }

    /**
     * Gets the collection of targets (in term of target model identities) for the given not null and valid target type under the given not null and
     * valid target category and for the given command.
     * 
     * @param targetCategory
     *            the not null and valid target category.
     * @param targetType
     *            the not null and valid target type.
     * @param command
     *            the received command.
     * 
     * @return the collection of targets (in term of target model identities).
     */
    private Map<String, Map<String, Set<String>>> getTargetCategoryAndTypeTargets(final String targetCategory, final String targetType,
            final CapabilityGetCommand command) {
        final Map<String, Map<String, Set<String>>> targets = new HashMap<>();
        final Set<String> supportedTargetCategories = nscsModelServiceImpl.getTargetCategories();
        final boolean isSkipConsistencyCheck = command.isSkipConsistencyCheck();
        if (!isSkipConsistencyCheck) {
            if (supportedTargetCategories.contains(targetCategory)) {
                final Set<String> targetTypes = nscsModelServiceImpl.getTargetTypes(targetCategory);
                if (targetTypes.contains(targetType)) {
                    final Map<String, Set<String>> targetTypeTargets = new HashMap<>();
                    final Set<String> targetCategoryAndTypeTargets = getTargetModelIdentities(targetCategory, targetType, command);
                    targetTypeTargets.put(targetType, targetCategoryAndTypeTargets);
                    targets.put(targetCategory, targetTypeTargets);
                } else {
                    final String errorMessage = String.format("Unsupported target type [%s] for target category [%s]. Allowed values are: %s",
                            targetType, targetCategory, targetTypes);
                    throw new IllegalArgumentException(errorMessage);
                }
            } else {
                final String errorMessage = String.format("Unsupported target category [%s]. Allowed values are: %s", targetCategory,
                        supportedTargetCategories);
                throw new IllegalArgumentException(errorMessage);
            }
        } else {
            final Map<String, Set<String>> targetTypeTargets = new HashMap<>();
            final String targetModelIdentity = command.getOssModelIdentity();
            targetTypeTargets.put(targetType, Stream.of(targetModelIdentity).collect(Collectors.toCollection(HashSet::new)));
            targets.put(targetCategory, targetTypeTargets);
        }
        return targets;
    }

    /**
     * Gets the collection of involved targets for the given not null target type under all supported target categories which the given target type
     * belongs to and given command.
     * 
     * @param targetType
     *            the not null target type.
     * @param command
     *            the received command.
     * @return the collection of involved targets (a map with key the target category and value a map with key the target type and value a set of
     *         target model identities).
     */
    private Map<String, Map<String, Set<String>>> getAllTargetCategoriesAndTypeTargets(final String targetType, final CapabilityGetCommand command) {
        final Map<String, Map<String, Set<String>>> targets = new HashMap<>();
        final Set<String> supportedTargetCategories = nscsModelServiceImpl.getTargetCategories();
        for (final String supportedTargetCategory : supportedTargetCategories) {
            final Map<String, Set<String>> targetTypeTargets = new HashMap<>();
            final Set<String> targetTypes = nscsModelServiceImpl.getTargetTypes(supportedTargetCategory);
            if (targetTypes.contains(targetType)) {
                final Set<String> targetCategoryAndTypeTargets = getTargetModelIdentities(supportedTargetCategory, targetType, command);
                targetTypeTargets.put(targetType, targetCategoryAndTypeTargets);
                targets.put(supportedTargetCategory, targetTypeTargets);
            }
        }
        if (targets.isEmpty()) {
            final String errorMessage = String.format("Unsupported target type [%s] for any target categories.", targetType);
            throw new IllegalArgumentException(errorMessage);
        }
        return targets;
    }

    /**
     * Gets the collection of target model identities for the given not null and valid target type under the given not null and valid target category
     * and for the given command.
     * 
     * If the target model identity in the given command is null, all supported target model identities for the given target category and target type
     * are returned, otherwise it shall be a supported target model identity for the given target category and target type.
     * 
     * @param targetCategory
     *            the not null and valid target category.
     * @param targetType
     *            the not null and valid target type.
     * @param command
     *            the received command.
     * 
     * @return the collection of target model identities.
     */
    private Set<String> getTargetModelIdentities(final String targetCategory, final String targetType, final CapabilityGetCommand command) {
        final String targetModelIdentity = command.getOssModelIdentity();
        final Set<String> targetModelIdentities = nscsModelServiceImpl.getTargetModelIdentities(targetCategory, targetType);
        if (targetModelIdentity == null) {
            return targetModelIdentities;
        }

        final boolean isSkipConsistencyCheck = command.isSkipConsistencyCheck();
        final boolean isNullTargetModelIdentity = (TARGET_MODEL_IDENTITY_NULL_AS_STRING.equals(targetModelIdentity));
        if (isNullTargetModelIdentity || (targetModelIdentities != null && targetModelIdentities.contains(targetModelIdentity))
                || isSkipConsistencyCheck) {
            return Stream.of(isNullTargetModelIdentity ? null : targetModelIdentity).collect(Collectors.toCollection(HashSet::new));
        } else {
            final String errorMessage = String.format(
                    "Unsupported target model identity [%s] for target type [%s] and target category [%s]. Allowed values are: %s",
                    targetModelIdentity, targetType, targetCategory, targetModelIdentities);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private static boolean equalLists(final List<String> expected, final List<String> actual) {
        if (expected == actual) {
            return true;
        }
        if (expected == null || actual == null) {
            return false;
        }
        if (expected.size() != actual.size()) {
            return false;
        }
        final List<String> exp = new ArrayList<String>(expected);
        final List<String> act = new ArrayList<String>(actual);
        Collections.sort(exp);
        Collections.sort(act);
        return exp.equals(act);
    }

    private static boolean equalMapOfLists(final Map<String, List<String>> expected, final Map<String, List<String>> actual) {
        if (expected == actual) {
            return true;
        }
        if (expected == null || actual == null) {
            return false;
        }
        if (expected.size() != actual.size()) {
            return false;
        }
        if (!expected.keySet().equals(actual.keySet())) {
            return false;
        }
        for (final String key : expected.keySet()) {
            try {
                if (!equalLists(expected.get(key), actual.get(key))) {
                    return false;
                }
            } catch (final ClassCastException e) {
                return false;
            }
        }
        return true;
    }

    private static boolean equalMapOfMapOfString(final Map<String, Map<String, String>> expected, final Map<String, Map<String, String>> actual) {
        if (expected == actual) {
            return true;
        }
        if (expected == null || actual == null) {
            return false;
        }
        if (expected.size() != actual.size()) {
            return false;
        }
        if (!expected.keySet().equals(actual.keySet())) {
            return false;
        }
        for (final String key : expected.keySet()) {
            try {
                if (!expected.get(key).equals(actual.get(key))) {
                    return false;
                }
            } catch (final ClassCastException e) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public static boolean equalValue(final Object expected, final Object actual) {
        if (expected == actual) {
            return true;
        }
        if (expected == null || actual == null) {
            return false;
        }
        if (!expected.equals(actual)) {
            if (expected instanceof List<?>) {
                try {
                    if (!equalLists((List<String>) expected, (List<String>) actual)) {
                        return false;
                    }
                } catch (final ClassCastException e) {
                    return false;
                }
            } else if (expected instanceof Map<?, ?>) {
                try {
                    if (!equalMapOfLists((Map<String, List<String>>) expected, (Map<String, List<String>>) actual)) {
                        return false;
                    }
                } catch (final ClassCastException e) {
                    try {
                        if (!equalMapOfMapOfString((Map<String, Map<String, String>>) expected, (Map<String, Map<String, String>>) actual)) {
                            return false;
                        }
                    } catch (final ClassCastException e1) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        return true;
    }
}
