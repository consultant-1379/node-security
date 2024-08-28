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
package com.ericsson.nms.security.nscs.handler.validation.impl;

import java.util.*;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.*;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.CommandValidator;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;

/**
 * Checks if all nodes in the given command has all of the properties that the CredentialsCommand needs.
 * 
 * @author egbobcs
 *
 */
public class KeyGeneratorUpdateMissingKeyPairValidator implements CommandValidator {

	@Inject
	private Logger logger;

	@Inject
	private NscsCMReaderService reader;


	/**
	 * Check if node has already key-pair generated
	 * 
	 * @param command
	 *            - expects to be a NscsNodeCommand
	 * @param context a CommandContext instance
	 * @throws CommandSyntaxException in case some of the parameters are missing
	 *             
	 */
	@Override
	public void validate(final NscsPropertyCommand command, final CommandContext context) throws NscsServiceException {

		logger.info("Starting " + this.getClass().getName());

		// e.g. secadm keygen update --nodelist aaa --algorithm-type-size RSA_2048

		if (command instanceof NscsPropertyCommand) {
			
			if (context.getValidNodes().size() > 0) {

				KeypairNotFoundException keypairMissingException = new KeypairNotFoundException();
				StringBuilder sb = new StringBuilder();

				final CmResponse response = reader.getMOAttribute(context.toNormalizedRef(context.getValidNodes()),
						Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.type(),
						Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.namespace(), 
						NetworkElementSecurity.ENM_SSH_PUBLIC_KEY);

				final Collection<CmObject> cmObjects = response.getCmObjects();

				logger.warn("response.getCmObjects().size() = {}", response.getCmObjects().size());
				
				if (cmObjects.isEmpty()) {
					logger.warn("keygen update - cmResponse is empty reading attribute {} !", NetworkElementSecurity.ENM_SSH_PUBLIC_KEY);
					throw new CouldNotReadMoAttributeException(NetworkElementSecurity.ENM_SSH_PUBLIC_KEY);
				} else if (context.getValidNodes().size() != cmObjects.size()) {
					logger.warn("keygen update - cmResponse size [{}] not equals to context.getValidNodes [{}]", cmObjects.size(), context.getValidNodes());
					throw new NormalizableNodesMismatchValidNodesException();
				} else {
					
					for (final CmObject cmObjIntfs : cmObjects) {
						
						NodeReference nodeRef = new NodeRef(cmObjIntfs.getFdn());

						logger.warn("cmObjIntfs fdn: " + cmObjIntfs.getFdn());

						String enmSshPublicKey = (String) cmObjIntfs.getAttributes().get(NetworkElementSecurity.ENM_SSH_PUBLIC_KEY);
						logger.warn("enmSshPublicKey: {}", enmSshPublicKey);
						if (enmSshPublicKey == null || (enmSshPublicKey != null && enmSshPublicKey.trim().isEmpty())) {
							sb.append(nodeRef.getName() + " ");
							context.setAsInvalidOrFailed(nodeRef, keypairMissingException);
						}
					}
				}

				if (sb.length() > 0) {
					String errmsg = "Public/Private key already generated for the following nodes " + sb.toString() + 
							". Please perform keygen update";
					logger.error(errmsg);
				}
			}

		} else {
			logger.error("Got an unexpected type of command. '{}' expecting NscsPropertyCommand", command.getClass().getSimpleName());
			throw new UnexpectedCommandTypeException();
		}

		logger.info(this.getClass().getName() + " done");
	}

}
