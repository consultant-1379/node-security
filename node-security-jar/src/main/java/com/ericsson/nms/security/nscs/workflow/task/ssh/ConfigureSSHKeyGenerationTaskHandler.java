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
package com.ericsson.nms.security.nscs.workflow.task.ssh;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.*;

import javax.ejb.Local;
import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.*;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.ssh.SSHKeyGenCommand;
import com.ericsson.nms.security.nscs.utilities.MoAttributeHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.eventbus.model.EventSender;
import com.ericsson.oss.itpf.sdk.eventbus.model.annotation.Modeled;
import com.ericsson.oss.itpf.sdk.recording.ErrorSeverity;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.itpf.security.cryptography.CryptographyService;
import com.ericsson.oss.itpf.security.keymanagement.KeyGenerator;
import com.ericsson.oss.mediation.sec.model.SSHkeyManagementJob;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.ssh.ConfigureSSHKeyGenerationTask;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.services.security.nscs.util.NscsStringUtils;

import static com.ericsson.nms.security.nscs.ssh.SSHKeyGenConstants.*;

/**
 * <p>
 * Task handler for WorkflowTaskType.SSH_KEY_GENERATION
 * </p>
 * <p>
 * Generate the SSH keys for the node invoking the KeyManagement Service
 * </p>
 * 
 * @author elucbot.
 */
@WFTaskType(WorkflowTaskType.SSH_KEY_GENERATION)
@Local(WFTaskHandlerInterface.class)
public class ConfigureSSHKeyGenerationTaskHandler implements WFActionTaskHandler<ConfigureSSHKeyGenerationTask>, WFTaskHandlerInterface  {
	private static final ModelDefinition.NetworkElementSecurity nesMO = Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity;
	private static final String ENM_SSH_PRIVATE_KEY = NetworkElementSecurity.ENM_SSH_PRIVATE_KEY;
	private static final String ENM_SSH_PUBLIC_KEY = NetworkElementSecurity.ENM_SSH_PUBLIC_KEY;
	private static final String ALGORITHM_AND_KEY_SIZE = NetworkElementSecurity.ALGORITHM_AND_KEY_SIZE;

	private static final long SSHKEY_DELAY_TIME = 1000; //in msec

	//SSH command to send
	private String sshCommandToExecute;
	private String publicSSHKey;
	private String encryptedPrivateSSHKey;
	private String algorithmKeyAndSize;

	boolean skipMediationLayer = false;

	private static final  Map<String, String> sshKeyCommandConversion = new HashMap<>();
	static {
		sshKeyCommandConversion.put(SSH_KEY_TO_BE_CREATED, SSHKeyGenCommand.SSH_KEY_CREATE.toString());
		sshKeyCommandConversion.put(SSH_KEY_TO_BE_UPDATED, SSHKeyGenCommand.SSH_KEY_UPDATE.toString());
		sshKeyCommandConversion.put(SSH_KEY_TO_BE_DELETED, SSHKeyGenCommand.SSH_KEY_DELETE.toString());
	}

	@Inject
	private NscsLogger nscsLogger;
	
    @Inject
    private SystemRecorder systemRecorder;

	@Inject
	private MoAttributeHandler moAttributeHandler;

	@Inject
	private NscsCMReaderService readerService;

	@Inject
	private CryptographyService cryptographyService;

	@Inject
	@Modeled
	private EventSender<SSHkeyManagementJob> commandJobSender;

	@EServiceRef
	ConfigureSSHKeyTimerInterface configureSSHKeyTimer;

	@Override
	public void processTask(final ConfigureSSHKeyGenerationTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);
        
        String errorMsg;
		final String nodeFdn = task.getNodeFdn();
		final NodeReference node = task.getNode();
		final String sshkeyOperation = task.getSshkeyOperation();
		final AlgorithmKeys algorithmKey = task.getAlgorithm();

		//MO normalized model
		String networkElementSecurityFdn;
		
		if(node == null) {
			errorMsg = String.format("Null NodeReference for node fdn %s ", nodeFdn);
			nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMsg);
	        systemRecorder.recordSecurityEvent("Node Security Service - Configuring SSH keys", "Node is null fdn["+ nodeFdn+"] : Configuring SSH Keys on Node '" + task.getNodeFdn()
	                + "'", "", "NETWORK.INITIAL_NODE_ACCESS", ErrorSeverity.ERROR, "IN-PROGRESS");

			throw new UnexpectedErrorException(errorMsg);
		}
		
		if(algorithmKey == null) {
			errorMsg = String.format("Null AlgorithmKeys for NetworkElementSecurity MO %s ", node);
			nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMsg);
	        systemRecorder.recordSecurityEvent("Node Security Service - Configuring SSH keys", "Algorithm is null fdn["+ nodeFdn+"] : Configuring SSH Keys on Node '" + task.getNodeFdn()
	                + "'", "", "NETWORK.INITIAL_NODE_ACCESS", ErrorSeverity.ERROR, "IN-PROGRESS");
	        throw new UnexpectedErrorException(errorMsg);
		}
		
		final String nodeName = node.getName();		
		nscsLogger.info("Got fdn "+nodeFdn+", nodeName "+nodeName+", Algorithm "+algorithmKey+", getAlgorithm "+algorithmKey.getAlgorithm()+", getKeySize "+algorithmKey.getKeySize()+", sshkeyOperation "+sshkeyOperation);

		SSHkeyManagementJob sshKeyManagementJob;
		try {
			nscsLogger.info("Before readerService.getNormalizedNodeReference for node "+node.getFdn());
			final NormalizableNodeReference normRef = readerService.getNormalizedNodeReference(node);

			if(normRef == null) {
				errorMsg = String.format("Fetched null NormalizedNodeReference ref for NetworkElementSecurity MO %s", node);
				nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMsg);
				systemRecorder.recordSecurityEvent("Node Security Service - Configuring SSH keys", "null NormalizedNodeReference ref fdn["+ nodeFdn+"] : Configuring SSH Keys on Node '" + task.getNodeFdn()
						+ "'", "", "NETWORK.INITIAL_NODE_ACCESS", ErrorSeverity.ERROR, "IN-PROGRESS");
				throw new UnexpectedErrorException(errorMsg);
			}

			String normalizedRefName = normRef.getName();
			networkElementSecurityFdn = Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.withNames(normalizedRefName).fdn();
			nscsLogger.info("Updating NetworkElementSecurity for normalizedRefName "+normalizedRefName+", networkElementSecurityFdn"+ networkElementSecurityFdn);

			switch (sshkeyOperation) {
				case SSH_KEY_TO_BE_DELETED :
					buildSshKeyToDelete(nodeFdn);
					break;
				case SSH_KEY_TO_BE_CREATED:
				case SSH_KEY_TO_BE_UPDATED:
					buildSshKeyToCreateOrUpdate(nodeFdn, sshkeyOperation, algorithmKey);
					break;
				default:
					errorMsg = String.format("SSH KEY: invalid operation for node %s", node);
					nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMsg);
					systemRecorder.recordSecurityEvent("Node Security Service - Configuring SSH keys", "SSH KEY invalid operation fdn["+ nodeFdn+"] : Configuring SSH Keys on Node '" + task.getNodeFdn()
							+ "'", "", "NETWORK.INITIAL_NODE_ACCESS", ErrorSeverity.ERROR, "IN-PROGRESS");
					throw new UnexpectedErrorException(errorMsg);
			}

			sshKeyManagementJob = new SSHkeyManagementJob(
					networkElementSecurityFdn,
					String.format("%s-%s", task.getNode().getName(), UUID.randomUUID()),
					sshCommandToExecute,
					publicSSHKey,
					encryptedPrivateSSHKey,
					algorithmKeyAndSize);

		} catch (final Exception e) {
			nscsLogger.workFlowTaskHandlerFinishedWithError(task,"Could not update MO attribute! "+e.getMessage());
	        systemRecorder.recordSecurityEvent("Node Security Service - Configuring SSH keys", "Could not update MO attribute fdn["+ nodeFdn+"] : Configuring SSH Keys on Node '" + task.getNodeFdn()
	                + "'", "", "NETWORK.INITIAL_NODE_ACCESS", ErrorSeverity.ERROR, "IN-PROGRESS");
	        throw new UnexpectedErrorException(e);
		}

		// Send SSHCommandJob
		nscsLogger.info("Prepared sshCommandJob "+sshCommandToExecute+" for node "+nodeName+", nodeAddress "+networkElementSecurityFdn);

		try {
			if(!skipMediationLayer) {
				commandJobSender.send(sshKeyManagementJob);
				nscsLogger.debug("Command "+sshKeyManagementJob+" sent to the node "+task.getNodeFdn());
			} else {
				configureSSHKeyTimer.startSSHKeyTimer(SSHKEY_DELAY_TIME,
						new ConfigureSSHKeyTimerDto(node.getName(),networkElementSecurityFdn, sshKeyCommandConversion.get(sshkeyOperation)));
				nscsLogger.info("Skip sshCommandJob " + sshCommandToExecute + " for node " + nodeName);
			}
		} catch (Exception e) {
			nscsLogger.workFlowTaskHandlerFinishedWithError(task,"Error when sending SSH Command Job! Command: "+sshCommandToExecute+", networkElementSecurityFdn: "+networkElementSecurityFdn+", Exception: "+e.getMessage());
			systemRecorder.recordSecurityEvent("Node Security Service - Configuring SSH keys", "Error when sending SSH Command Job fdn["+ nodeFdn+"] : Configuring SSH Keys on Node '" + task.getNodeFdn()
	                + "'", "", "NETWORK.INITIAL_NODE_ACCESS", ErrorSeverity.ERROR, "COMPLETED");
	        throw new UnexpectedErrorException(e);
		}

		nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "Process completed");
	}

	private static String encode(final byte[] bytes) {
		return DatatypeConverter.printBase64Binary(bytes);
	}

	private byte[] encrypt(final String text) {
		return cryptographyService.encrypt(text
				.getBytes(StandardCharsets.UTF_8));
	}

	private String encryptEncode(final String text) {
		if (text == null) {
			return null;
		}
		return encode(encrypt(text));
	}

	/**
	 * buildSshKeyToDelete: fill all attributes needed to perform sshkey delete
	 * there are three cases:
	 * 1. if both attributes NES.enmSshPublicKey and NES.enmSshPrivateKey are null/empty then
	 *    the mediation layer is skipped and workflow shoudl finished without errors (no activities on the node)
	 * 2. if one of the attributes NES.enmSshPublicKey or NES.enmSshPrivateKey has been set to Invalid_Key
	 *    then a dummy couple of private/public key with proper format is created, the mediation flow is called.
	 *    The mediation flow is executed and should finish by setting NES.enmSshPublicKey = empty
	 *    and NES.enmSshPrivateKey = empty
	 * @param nodeFdn: the fdn of the node
	 */
	private void buildSshKeyToDelete(final String nodeFdn){

		nscsLogger.debug("Getting key/algorithm size from NES");

		publicSSHKey = moAttributeHandler.getMOAttributeValue(nodeFdn, nesMO.type(), nesMO.namespace(), ENM_SSH_PUBLIC_KEY);
		encryptedPrivateSSHKey = moAttributeHandler.getMOAttributeValue(nodeFdn, nesMO.type(), nesMO.namespace(), ENM_SSH_PRIVATE_KEY);
		algorithmKeyAndSize = moAttributeHandler.getMOAttributeValue(nodeFdn, nesMO.type(), nesMO.namespace(), ALGORITHM_AND_KEY_SIZE);
		sshCommandToExecute = sshKeyCommandConversion.get(SSH_KEY_TO_BE_DELETED);

		if (NscsStringUtils.isEmpty(publicSSHKey) && NscsStringUtils.isEmpty(encryptedPrivateSSHKey)) {
			skipMediationLayer = true;
			nscsLogger.info("Skip delete cmd for node {}, keys are null or empty", nodeFdn);
		} else if (isKeyEmptyOrInvalid(publicSSHKey) || isKeyEmptyOrInvalid(encryptedPrivateSSHKey)){
			createDummyKeys(nodeFdn);
			nscsLogger.info("Format invalid key for delete cmd for node={}", nodeFdn);
		}
	}

	private void buildSshKeyToCreateOrUpdate(final String nodeFdn, final String sshkeyOperation,
											 final AlgorithmKeys algorithmKey) throws IOException {

		nscsLogger.info("Generating KeyPair");
		KeyPair kp = KeyGenerator.getKeyPair(algorithmKey.getAlgorithm(), algorithmKey.getKeySize());

		nscsLogger.info("Before KeyGenerator toSecShFormat() toPEMFormat() ");
		publicSSHKey = KeyGenerator.toSecShFormat(kp.getPublic(), nodeFdn);
		String privateSSHKey = KeyGenerator.toPEMFormat(kp.getPrivate());
		algorithmKeyAndSize = algorithmKey.toString();
		nscsLogger.info("Before encryptEncode");
		encryptedPrivateSSHKey = encryptEncode(privateSSHKey);

		sshCommandToExecute = sshKeyCommandConversion.get(sshkeyOperation);
	}

	private void createDummyKeys(final String nodeFdn){
		publicSSHKey = "ssh-rsa " + DatatypeConverter.printBase64Binary(SSH_KEY_INVALID.getBytes(StandardCharsets.UTF_8))
				+ " " +  nodeFdn;
		encryptedPrivateSSHKey = encryptEncode(SSH_KEY_INVALID);
	}

	private boolean isKeyEmptyOrInvalid (String key) {
		return NscsStringUtils.isEmpty(key) || key.equals(SSH_KEY_INVALID);
	}
}
