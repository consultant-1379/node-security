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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.CiphersConfigCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CiphersConfigurationUtil;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CiphersResponseBuilderFactory;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.validation.ciphersconfig.GetCiphersValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;

/**
 * Initiates the process to get TLS or SSH ciphers on the nodes
 *
 * @author xkumkam
 */
@CommandType(NscsCommandType.GET_CIPHERS)
@Local(CommandHandlerInterface.class)
public class GetCiphersHandler implements CommandHandler<CiphersConfigCommand>, CommandHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private CiphersResponseBuilderFactory ciphersProtocolManagerFactory;

    @Inject
    private GetCiphersValidator getCiphersValidator;

    @Inject
    private CiphersConfigurationUtil ciphersConfigurationUtil;

    @Override
    public NscsCommandResponse process(final CiphersConfigCommand command, final CommandContext context) throws NscsServiceException {
        final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();

        nscsLogger.commandHandlerStarted(command);

        if (!ciphersConfigurationUtil.getValidProtocolTypesForCiphersConfiguration().contains(command.getProtocolProperty())) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Invalid argument given for protocol. Accepted arguments are ");
            ciphersConfigurationUtil.getValidProtocolTypesForCiphersConfiguration().forEach(sb::append);
            final String errorMessage = sb.toString();
            nscsLogger.error(errorMessage);
            nscsLogger.commandHandlerFinishedWithError(command, errorMessage);
            throw new InvalidArgumentValueException(NscsErrorCodes.CIPHERS_CONFIG_INVALID_ARGUMENT_VALUE, errorMessage)
                    .setSuggestedSolution(NscsErrorCodes.REFER_TO_ONLINE_HELP_FOR_SYNTAX);
        }

        final List<NodeReference> validNodesList = new ArrayList<>();
        getCiphersValidator.validateNodes(command, validNodesList, invalidNodesErrorMap);
        try {
            final NscsCommandResponse nscsCommandResponse = ciphersProtocolManagerFactory.getCiphersmanager(command.getProtocolProperty())
                    .buildGetCiphersResponse(validNodesList, invalidNodesErrorMap);
            nscsLogger.commandHandlerFinishedWithSuccess(command, "Command executed successfully");
            return nscsCommandResponse;
        } catch (final IOException exception) {
            nscsLogger.error(NscsErrorCodes.THIS_IS_AN_UNEXPECTED_SYSTEM_ERROR + exception.getMessage());
            nscsLogger.commandHandlerFinishedWithError(command, exception.getMessage());
            return NscsCommandResponse.message(NscsErrorCodes.THIS_IS_AN_UNEXPECTED_SYSTEM_ERROR);
        }
    }
}
