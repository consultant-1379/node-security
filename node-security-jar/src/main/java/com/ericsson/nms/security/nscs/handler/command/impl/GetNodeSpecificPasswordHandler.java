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

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.types.GetNodeSpecificPasswordCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.ldap.control.IdentityManagementProxy;
import com.ericsson.nms.security.nscs.logger.NscsLogger;

/**
 * <p>
 * Getting password for requested push node type.
 * </p>
 *
 *
 */
@CommandType(NscsCommandType.GET_NODE_SPECIFIC_PASSWORD)
@Local(CommandHandlerInterface.class)
public class GetNodeSpecificPasswordHandler implements CommandHandler<GetNodeSpecificPasswordCommand>, CommandHandlerInterface {

    public static final String USER_PASSWRD = "Password";
    public static final String UNDEFINED_PASSWRD = "Undefined";
    public static final String GENERATED_SUCCESS_MESSAGE = "Password generated successfully";
    public static final String UNSUPPORTED_NODE_TYPE = "ERROR:Requester value %s does not have get password support";

    @Inject
    private NscsCapabilityModelService capabilityModel;

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private IdentityManagementProxy identityManagementProxy;

    /**
     *
     * @param command
     *            get nodetype specific password command 
     * @param context
     *            a CommandContext instance
     * @return NscsCommandResponse instance with the password for push type nodes.
     * @throws NscsServiceException
     */
    @Override
    public NscsCommandResponse process(final GetNodeSpecificPasswordCommand command, final CommandContext context) throws NscsServiceException {
        nscsLogger.commandHandlerStarted(command);
        final String neType = command.getNeType();
        final NodeModelInformation nodeModelInfo = new NodeModelInformation(null, null, neType);

        final String pushM2MUser = capabilityModel.getPushM2MUser(nodeModelInfo);
        if (pushM2MUser == null) {
            final String errmsg = String.format(UNSUPPORTED_NODE_TYPE, neType);
            nscsLogger.commandHandlerFinishedWithError(command, errmsg);
            throw new InvalidArgumentValueException(errmsg);
        }

        final char[] userPassword = fetchPassword(pushM2MUser);
        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(0);
        if (userPassword != null) {
            nscsLogger.commandHandlerFinishedWithSuccess(command, GENERATED_SUCCESS_MESSAGE);
            response.add(USER_PASSWRD, new String[] {});
            response.add(String.valueOf(userPassword), new String[] {});
        } else {
            nscsLogger.commandHandlerFinishedWithError(command, UNDEFINED_PASSWRD);
            response.add(USER_PASSWRD, new String[] {});
            response.add(UNDEFINED_PASSWRD, new String[] {});
        }
        return response;
    }

    private char[] fetchPassword(final String name) {
        return identityManagementProxy.getM2MPassword(name);
    }
}