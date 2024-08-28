/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.command.impl;

import javax.ejb.Local;
import javax.inject.Inject;
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.types.KeyGeneratorCommand;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.validation.UseValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.*;

import static com.ericsson.nms.security.nscs.api.command.NscsCommandType.CREATE_SSH_KEY;

@CommandType(CREATE_SSH_KEY)
@Local(CommandHandlerInterface.class)
@UseValidator({ KeyGeneratorCreateMissingAttributesValidator.class,
		KeyGeneratorSupportedNodeTypeValidator.class,
		KeyGeneratorSupportedAlgorithmValidator.class,
		StarIsNotAllowedValidator.class,
		NoDuplNodeNamesAllowedValidator.class
})
public class CreateKeyGeneratorHandler implements CommandHandler<KeyGeneratorCommand>, CommandHandlerInterface {
	@Inject
	private SSHKeyCommandHandlerHelper sshKeyCommandHandlerHelper;

	@Override
	public NscsCommandResponse process(KeyGeneratorCommand command, CommandContext context){
		return sshKeyCommandHandlerHelper.processSshKey(command, context, SSHKeyCommandHandlerHelper.SSHKeyConfigurationMode.CREATE_SSH_KEY);
	}

}
