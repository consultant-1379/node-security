/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.validation.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.command.types.SnmpAuthpriv;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedCommandTypeException;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.CommandValidator;
import com.ericsson.oss.services.security.nscs.command.util.SnmpCommandHelper;

/**
 *
 * @author ebarmos, emelant
 */
public class SnmpAuthprivParamsValidator implements CommandValidator {

    @Inject
    private Logger logger;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Override
    public void validate(final NscsPropertyCommand command, final CommandContext context) throws NscsServiceException {
        logger.debug("Starting SnmpAuthprivParamsValidator with command type: {}", command.getCommandType());

        if (command instanceof NscsPropertyCommand) {
            for (final NormalizableNodeReference node : context.getValidNodes()) {
                logger.debug("Updating Snmp Authpriv param for Node : {}", node);

                if (missingAttributes(command, node)) {
                    logger.error("Command is not valid, missing attributes");
                    throw new CommandSyntaxException(new String("missing attributes"));
                }

                // Check Mandatory auth_algo and priv_algo
                final SnmpAuthpriv snmpAuthprivCommand = (SnmpAuthpriv) command;

                if (snmpAuthprivCommand.getProperties().keySet().contains(SnmpAuthpriv.AUTH_ALGO_PARAM)
                        && !SnmpAuthpriv.getAuthProtocolList().contains(snmpAuthprivCommand.getAuthAlgo())) {
                    final String errmsg = String.format("" + "Invalid argument for parameter %s",
                            SnmpAuthpriv.AUTH_ALGO_PARAM + ". Accepted arguments are " + SnmpAuthpriv.getAuthProtocolList());
                    logger.error(errmsg);
                    throw new InvalidArgumentValueException(errmsg);
                }

                if (snmpAuthprivCommand.getProperties().keySet().contains(SnmpAuthpriv.PRIV_ALGO_PARAM)
                        && !SnmpAuthpriv.getPrivProtocolList().contains(snmpAuthprivCommand.getPrivAlgo())) {
                    final String errmsg = String.format("" + "Invalid argument for parameter %s",
                            SnmpAuthpriv.PRIV_ALGO_PARAM + ". Accepted arguments are " + SnmpAuthpriv.getPrivProtocolList());
                    logger.error(errmsg);
                    throw new InvalidArgumentValueException(errmsg);
                }

            }
        } else {
            logger.error("Got an unexpected type of command. '{}' expecting NscsPropertyCommand", command.getClass().getSimpleName());
            throw new UnexpectedCommandTypeException();
        }

        logger.debug("Command validated");
    }

    private boolean missingAttributes(final NscsPropertyCommand command, final NormalizableNodeReference normNodeRef) {

        final Set<String> actualAttributeKeys = new HashSet<>(command.getProperties().keySet());

        boolean isResult = true;
        final boolean isValidCommand = nscsCapabilityModelService.isCliCommandSupported(normNodeRef, NscsCapabilityModelService.SNMP_COMMAND);

        if (isValidCommand) {
            final List<String> expectedAttributeKeys = SnmpCommandHelper.getExpectedSnmpAuthprivParams();
            actualAttributeKeys.retainAll(expectedAttributeKeys);
            isResult = (actualAttributeKeys.size() < expectedAttributeKeys.size());
        }

        return isResult;
    }

}
