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
package com.ericsson.nms.security.nscs.workflow.task.node.attribute;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelConstants;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.ldap.utility.PlatformConfigurationReader;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.ssh.SSHKeyGenConstants;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.security.cryptography.CryptographyService;
import com.ericsson.oss.itpf.security.keymanagement.KeyGenerator;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.ConfigureModeledSshKeyTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;
import com.ericsson.oss.services.security.nscs.dps.NscsDpsUtils;

/**
 * <p>
 * Task handler for WorkflowTaskType.CONFIGURE_MODELED_SSH_KEY. Configure the modeled SSH key on a node.
 * </p>
 */
@WFTaskType(WorkflowTaskType.CONFIGURE_MODELED_SSH_KEY)
@Local(WFTaskHandlerInterface.class)
public class ConfigureModeledSshKeyTaskHandler implements WFQueryTaskHandler<ConfigureModeledSshKeyTask>, WFTaskHandlerInterface {

    private static final String CREATED_ON_NODE = "CREATED_ON_NODE";
    private static final String UPDATED_ON_NODE = "UPDATED_ON_NODE";
    private static final String DELETED_ON_NODE = "DELETED_ON_NODE";
    private static final String NO_KEY_TO_BE_DELETED_ON_NODE = "NO_KEY_TO_BE_DELETED_ON_NODE";
    private static final String NOT_DELETED_ON_NOT_IN_SYNC_NODE = "NOT_DELETED_ON_NOT_IN_SYNC_NODE";
    private static final String SUCCESSFULLY_UPDATED_MO_CURRENT_OLD_ATTRS = "Successfully updated MO [{}] current [{}] old [{}]";
    private static final String SUCCESSFULLY_CREATED_MO_WITH_ATTRS = "Successfully created MO [{}] with attrs [{}]";
    private static final String NODE_NOT_SYNCHRONIZED = "Node not synchronized";
    private static final String EMPTY_OR_NULL_SECURE_USER_NAME = "Empty or null secure user name";
    private static final String MISSING_MO_OF_TYPE_AND_NAME_FORMAT = "Missing MO of type [%s] and name [%s]";
    private static final String MISSING_MO_OF_TYPE_FORMAT = "Missing MO of type [%s]";
    private static final String UNSUPPORTED_MOM_TYPE_FORMAT = "Unsupported MOM type [%s]";
    private static final String FAILED_KEYS_FORMAT_FORMAT = "Failed keys format with msg [%s]";
    private static final String WRONG_ALGORITHM_KEYS_FORMAT = "Wrong AlgorithmKeys value [%s]";

    private static final String CREATED_BY_ENM_COMMENT = "Created by ENM.";

    private boolean isNodeSynchronized;
    private String secureUserName;
    private String authorizedKeyName;
    private String publicSSHKey = null;
    private String encryptedPrivateSSHKey = null;
    private String result;

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsDpsUtils nscsDpsUtils;

    @Inject
    private PlatformConfigurationReader platformConfigurationReader;

    @Inject
    private CryptographyService cryptographyService;

    @Override
    public String processTask(final ConfigureModeledSshKeyTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        String taskResult;
        try {
            final NormalizableNodeReference normalizableNodeRef = readerService.getNormalizableNodeReference(task.getNode());
            nscsLogger.info(task, "From task : mirrorRootFdn [{}] targetType [{}] targetModelIdentity [{}]", normalizableNodeRef.getFdn(),
                    normalizableNodeRef.getNeType(), normalizableNodeRef.getOssModelIdentity());

            processConfigureModeledSshKey(task, normalizableNodeRef);
            taskResult = serializeResult(task);
        } catch (final WorkflowTaskException e) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, e.getMessage());
            throw e;
        } catch (final Exception e) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, NscsLogger.stringifyException(e));
            throw e;
        }

        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "Successfully completed", getResult());
        return taskResult;
    }

    /**
     * Process the configuration of a modeled SSH key for the given task and the given node.
     * 
     * @param task
     *            the task.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @throws {@link
     *             WorkflowTaskException} if MOM type is not supported.
     */
    private void processConfigureModeledSshKey(final ConfigureModeledSshKeyTask task, final NormalizableNodeReference normalizableNodeRef) {

        validateConfigureModeledSshKey(task, normalizableNodeRef);

        if (NscsCapabilityModelConstants.NSCS_EOI_MOM.equals(task.getMomType())) {
            cbpOiConfigureModeledSshKey(task, normalizableNodeRef);
        } else {
            final String errorMessage = String.format(UNSUPPORTED_MOM_TYPE_FORMAT, task.getMomType());
            nscsLogger.error(task, errorMessage);
            throw new WorkflowTaskException(errorMessage);
        }
    }

    /**
     * Validate the configuration of a modeled SSH key for the given task and the given node.
     * 
     * @param task
     *            the task.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @throws {@link
     *             WorkflowTaskException} if validation fails.
     */
    private void validateConfigureModeledSshKey(final ConfigureModeledSshKeyTask task, final NormalizableNodeReference normalizableNodeRef) {

        final ManagedObject networkElementMO = nscsDpsUtils.getNormalizedRootMo(normalizableNodeRef);
        final ManagedObject cmFunctionMO = nscsDpsUtils.getOnlyChildMo(networkElementMO, Model.NETWORK_ELEMENT.cmFunction.type());
        final String syncStatus = cmFunctionMO.getAttribute(ModelDefinition.CmFunction.SYNC_STATUS);
        final boolean isSync = (ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name().equalsIgnoreCase(syncStatus));
        setNodeSynchronized(isSync);
        if (!SSHKeyGenConstants.SSH_KEY_TO_BE_DELETED.equals(task.getSshkeyOperation()) && !isNodeSynchronized()) {
            nscsLogger.error(task, "{} [{}]", NODE_NOT_SYNCHRONIZED, normalizableNodeRef.getName());
            throw new WorkflowTaskException(NODE_NOT_SYNCHRONIZED);
        }
        final ManagedObject networkElementSecurityMO = nscsDpsUtils.getNetworkElementSecurityMO(networkElementMO);
        if (networkElementSecurityMO != null) {
            final String username = networkElementSecurityMO.getAttribute(ModelDefinition.NetworkElementSecurity.SECURE_USER_NAME);
            setSecureUserName(username);
            if (getSecureUserName() == null || getSecureUserName().isEmpty()) {
                nscsLogger.error(task, "{} for node [{}]", EMPTY_OR_NULL_SECURE_USER_NAME, normalizableNodeRef.getName());
                throw new WorkflowTaskException(EMPTY_OR_NULL_SECURE_USER_NAME);
            }
        } else {
            final String errorMessage = String.format(MISSING_MO_OF_TYPE_FORMAT,
                    Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.type());
            nscsLogger.error(task, "{} for node [{}]", errorMessage, normalizableNodeRef.getName());
            throw new WorkflowTaskException(errorMessage);
        }
    }

    /**
     * Configure a modeled SSH key for the given task on the given CBP-OI node.
     * 
     * @param task
     *            the task.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     */
    private void cbpOiConfigureModeledSshKey(final ConfigureModeledSshKeyTask task, final NormalizableNodeReference normalizableNodeRef) {
        if (isNodeSynchronized()) {
            final ManagedObject userMO = nscsDpsUtils.getUserMO(normalizableNodeRef, getSecureUserName());
            if (userMO == null) {
                final String errorMessage = String.format(MISSING_MO_OF_TYPE_AND_NAME_FORMAT, ModelDefinition.USER_TYPE, getSecureUserName());
                nscsLogger.error(task, "{} for node [{}]", errorMessage, normalizableNodeRef.getFdn());
                throw new WorkflowTaskException(errorMessage);
            }
            final String keyName = platformConfigurationReader.getProperty(SSHKeyGenConstants.UI_PRES_SERVER_KEY);
            setAuthorizedKeyName(keyName);
            final ManagedObject authorizedKeyMO = nscsDpsUtils.getChildMo(userMO, normalizableNodeRef, ModelDefinition.AUTHORIZED_KEY_TYPE,
                    getAuthorizedKeyName());
            if (SSHKeyGenConstants.SSH_KEY_TO_BE_DELETED.equals(task.getSshkeyOperation())) {
                cbpOiDeleteModeledSshKey(task, normalizableNodeRef, authorizedKeyMO);
            } else {
                cbpOiCreateOrUpdateModeledSshKey(task, normalizableNodeRef, userMO, authorizedKeyMO);
            }
        } else {
            nscsLogger.info(task, "Node {} not synchronized", normalizableNodeRef.getName());
            setResult(NOT_DELETED_ON_NOT_IN_SYNC_NODE);
        }
    }

    /**
     * Create or update a modeled SSH key for the given task on the given CBP-OI node.
     * 
     * @param task
     *            the task.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param userMO
     *            the user MO.
     * @param authorizedKeyMO
     *            the authorized-key MO.
     */
    private void cbpOiCreateOrUpdateModeledSshKey(final ConfigureModeledSshKeyTask task, final NormalizableNodeReference normalizableNodeRef,
            final ManagedObject userMO, final ManagedObject authorizedKeyMO) {
        AlgorithmKeys algorithmKey;
        try {
            algorithmKey = AlgorithmKeys.valueOf(task.getAlgorithm());
        } catch (final Exception e) {
            final String errorMessage = String.format(WRONG_ALGORITHM_KEYS_FORMAT, task.getAlgorithm());
            nscsLogger.error(task, e, errorMessage);
            throw new WorkflowTaskException(errorMessage);
        }
        nscsLogger.info("Generating KeyPair for algorithm {} key size {}", algorithmKey.getAlgorithm(), algorithmKey.getKeySize());
        final KeyPair kp = KeyGenerator.getKeyPair(algorithmKey.getAlgorithm(), algorithmKey.getKeySize());
        nscsLogger.info("Before KeyGenerator toRFC4253Format() toPEMFormat() ");
        String publicKey;
        String privateKey;
        try {
            publicKey = KeyGenerator.toRFC4253Format(kp.getPublic());
            setPublicSSHKey(publicKey);
            privateKey = KeyGenerator.toPEMFormat(kp.getPrivate());
        } catch (final IOException | IllegalArgumentException e) {
            final String errorMessage = String.format(FAILED_KEYS_FORMAT_FORMAT, e.getMessage());
            nscsLogger.error(task, e, errorMessage);
            throw new WorkflowTaskException(errorMessage);
        }
        nscsLogger.info("Before encryptEncode");
        final String encryptedPrivateKey = encryptEncode(privateKey);
        setEncryptedPrivateSSHKey(encryptedPrivateKey);
        createOrUpdateAuthorizedKeyMO(task, normalizableNodeRef, userMO, authorizedKeyMO, publicKey, algorithmKey);
    }

    /**
     * Create or update authorized-key MO for the given task on the given CBP-OI node.
     * 
     * @param task
     *            the task.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param userMO
     *            the parent user MO.
     * @param authorizedKeyMO
     *            the authorized-key MO.
     * @param publicSSHKey
     *            the public SSH key.
     * @param algorithmKey
     *            the algorithm and key size.
     */
    private void createOrUpdateAuthorizedKeyMO(final ConfigureModeledSshKeyTask task, final NormalizableNodeReference normalizableNodeRef,
            final ManagedObject userMO, final ManagedObject authorizedKeyMO, final String publicSSHKey, final AlgorithmKeys algorithmKey) {
        if (authorizedKeyMO == null) {
            createAuthorizedKeyMO(task, normalizableNodeRef, userMO, publicSSHKey, algorithmKey);
        } else {
            updateAuthorizedKeyMO(task, authorizedKeyMO, publicSSHKey, algorithmKey);
        }
    }

    /**
     * Create authorized-key MO for the given task on the given CBP-OI node.
     * 
     * @param task
     *            the task.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param userMO
     *            the parent user MO.
     * @param publicSSHKey
     *            the public SSH key.
     * @param algorithmKey
     *            the algorithm and key size.
     */
    private void createAuthorizedKeyMO(final ConfigureModeledSshKeyTask task, final NormalizableNodeReference normalizableNodeRef,
            final ManagedObject userMO, final String publicSSHKey, final AlgorithmKeys algorithmKey) {
        final Map<String, Object> authorizedKeyAttributes = new HashMap<>();
        authorizedKeyAttributes.put(ModelDefinition.AUTHORIZED_KEY_NAME_ATTR, getAuthorizedKeyName());
        authorizedKeyAttributes.put(ModelDefinition.AUTHORIZED_KEY_COMMENT_ATTR, CREATED_BY_ENM_COMMENT);
        authorizedKeyAttributes.put(ModelDefinition.AUTHORIZED_KEY_ALGORITHM_ATTR, getAuthorizedKeyAlgorithm(algorithmKey));
        authorizedKeyAttributes.put(ModelDefinition.AUTHORIZED_KEY_KEY_DATA_ATTR, publicSSHKey);
        final String moType = ModelDefinition.AUTHORIZED_KEY_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_SYSTEM_NS;
        final ManagedObject authorizedKeyMO = nscsDpsUtils.createChildMo(userMO, normalizableNodeRef, refMimNs, moType, getAuthorizedKeyName(),
                authorizedKeyAttributes);
        setResult(CREATED_ON_NODE);
        nscsLogger.info(task, SUCCESSFULLY_CREATED_MO_WITH_ATTRS, authorizedKeyMO.getFdn(), authorizedKeyAttributes);
    }

    /**
     * Update authorized-key MO for the given task on the given CBP-OI node.
     * 
     * @param task
     *            the task.
     * @param authorizedKeyMO
     *            the authorized-key MO.
     * @param publicSSHKey
     *            the public SSH key.
     * @param algorithmKey
     *            the algorithm and key size.
     */
    private void updateAuthorizedKeyMO(final ConfigureModeledSshKeyTask task, final ManagedObject authorizedKeyMO, final String publicSSHKey,
            final AlgorithmKeys algorithmKey) {
        final Map<String, Object> oldAuthorizedKeyAttributes = authorizedKeyMO
                .getAttributes(Arrays.asList(ModelDefinition.AUTHORIZED_KEY_ALGORITHM_ATTR, ModelDefinition.AUTHORIZED_KEY_KEY_DATA_ATTR));
        final Map<String, Object> authorizedKeyAttributes = new HashMap<>();
        authorizedKeyAttributes.put(ModelDefinition.AUTHORIZED_KEY_ALGORITHM_ATTR, getAuthorizedKeyAlgorithm(algorithmKey));
        authorizedKeyAttributes.put(ModelDefinition.AUTHORIZED_KEY_KEY_DATA_ATTR, publicSSHKey);
        nscsDpsUtils.updateMo(authorizedKeyMO, authorizedKeyAttributes);
        setResult(UPDATED_ON_NODE);
        nscsLogger.info(task, SUCCESSFULLY_UPDATED_MO_CURRENT_OLD_ATTRS, authorizedKeyMO.getFdn(), authorizedKeyAttributes,
                oldAuthorizedKeyAttributes);
    }

    /**
     * Convert algorithm and key size to authorized-key algorithm.
     * 
     * Valid values are the values in the IANA 'Secure Shell (SSH) Protocol Parameters' registry, Public Key Algorithm Names.
     * 
     * Constraint: only support ssh-rsa, ssh-dss, dsa, rsa algorithm.
     * 
     * @param algorithmKey
     *            the algorithm and key size.
     * @return the authorized-key algorithm.
     * @throws {@link
     *             IllegalArgumentException} if algorithm is unknown or not supported.
     */
    private String getAuthorizedKeyAlgorithm(final AlgorithmKeys algorithmKey) {
        if ("RSA".equals(algorithmKey.getAlgorithm())) {
            return "ssh-rsa";
        } else if ("DSA".equals(algorithmKey.getAlgorithm())) {
            return "ssh-dss";
        } else {
            throw new IllegalArgumentException("Unknown public key encoding: " + algorithmKey.getAlgorithm());
        }
    }

    /**
     * Delete a modeled SSH key for the given task on the given CBP-OI node with given authorize-key MO.
     * 
     * @param task
     *            the task.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param authorizedKeyMO
     *            the authorize-key MO.
     */
    private void cbpOiDeleteModeledSshKey(final ConfigureModeledSshKeyTask task, final NormalizableNodeReference normalizableNodeRef,
            final ManagedObject authorizedKeyMO) {
        if (authorizedKeyMO != null) {
            final int deletedMOs = nscsDpsUtils.deleteMo(authorizedKeyMO);
            setResult(DELETED_ON_NODE);
            nscsLogger.info(task, "deleted {} MOs for hierarchy of authorized-key MO name {} for node {}", deletedMOs, getAuthorizedKeyName(),
                    normalizableNodeRef.getName());
        } else {
            setResult(NO_KEY_TO_BE_DELETED_ON_NODE);
            nscsLogger.info(task, "No authorized-key MO of name {} for node {}", getAuthorizedKeyName(), normalizableNodeRef.getName());
        }
    }

    /**
     * Encode base64 the given byte array.
     * 
     * @param bytes
     *            the byte array.
     * @return the base64-encoded byte array.
     */
    private static String encode(final byte[] bytes) {
        return DatatypeConverter.printBase64Binary(bytes);
    }

    /**
     * Encrypt the given string using the Cryptography Service.
     * 
     * @param text
     *            the string to encrypt.
     * @return the encrypted string as byte array.
     */
    private byte[] encrypt(final String text) {
        return cryptographyService.encrypt(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Encrypt the given string using the Cryptography Service and encode base64 the resulting byte array.
     * 
     * @param text
     *            the string.
     * @return the base64-encoded encrypted string.
     */
    private String encryptEncode(final String text) {
        if (text == null) {
            return null;
        }
        return encode(encrypt(text));
    }

    /**
     * @return the isNodeSynchronized
     */
    private boolean isNodeSynchronized() {
        return isNodeSynchronized;
    }

    /**
     * @param isNodeSynchronized
     *            the isNodeSynchronized to set
     */
    private void setNodeSynchronized(final boolean isNodeSynchronized) {
        this.isNodeSynchronized = isNodeSynchronized;
    }

    /**
     * @return the secureUserName
     */
    private String getSecureUserName() {
        return secureUserName;
    }

    /**
     * @param secureUserName
     *            the secureUserName to set
     */
    private void setSecureUserName(final String secureUserName) {
        this.secureUserName = secureUserName;
    }

    /**
     * @return the authorizedKeyName
     */
    private String getAuthorizedKeyName() {
        return authorizedKeyName;
    }

    /**
     * @param authorizedKeyName
     *            the authorizedKeyName to set
     */
    private void setAuthorizedKeyName(final String authorizedKeyName) {
        this.authorizedKeyName = authorizedKeyName;
    }

    /**
     * @return the publicSSHKey
     */
    public String getPublicSSHKey() {
        return publicSSHKey;
    }

    /**
     * @param publicSSHKey
     *            the publicSSHKey to set
     */
    public void setPublicSSHKey(final String publicSSHKey) {
        this.publicSSHKey = publicSSHKey;
    }

    /**
     * @return the encryptedPrivateSSHKey
     */
    public String getEncryptedPrivateSSHKey() {
        return encryptedPrivateSSHKey;
    }

    /**
     * @param encryptedPrivateSSHKey
     *            the encryptedPrivateSSHKey to set
     */
    public void setEncryptedPrivateSSHKey(final String encryptedPrivateSSHKey) {
        this.encryptedPrivateSSHKey = encryptedPrivateSSHKey;
    }

    /**
     * @return the result
     */
    private String getResult() {
        return result;
    }

    /**
     * @param result
     *            the result to set
     */
    private void setResult(final String result) {
        this.result = result;
    }

    /**
     * Serialize the result and outputParams to be passed to next task handlers.
     * 
     * @param task
     *            the task.
     *
     * @return the encoded result of the task handler.
     */
    private String serializeResult(final ConfigureModeledSshKeyTask task) {
        String encodedTaskResult = null;
        final Map<String, Serializable> outputParams = new HashMap<>();
        outputParams.put(WorkflowOutputParameterKeys.PUBLIC_SSH_KEY.toString(), getPublicSSHKey());
        outputParams.put(WorkflowOutputParameterKeys.ENCRYPTED_PRIVATE_SSH_KEY.toString(), getEncryptedPrivateSSHKey());
        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(getResult(), outputParams);
        try {
            encodedTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessage = String.format("%s while serializing output params", NscsLogger.stringifyException(e));
            nscsLogger.error(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        return encodedTaskResult;
    }

}
