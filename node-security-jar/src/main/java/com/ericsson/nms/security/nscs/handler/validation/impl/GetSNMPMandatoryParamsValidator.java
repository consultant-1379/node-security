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
package com.ericsson.nms.security.nscs.handler.validation.impl;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.CommandValidator;

/**
 * Checks if all nodes in the given command has all of the properties that the GetSNMP Command needs.
 *
 * @author DespicableUs
 *
 */
public class GetSNMPMandatoryParamsValidator implements CommandValidator {

    final public static String PLAIN_TEXT = "plaintext";

    protected static final Set<String> expectedGetSNMPParams = new HashSet<>();

    protected static final Set<String> optionalGetSNMPParams = new HashSet<>();
    static {
        optionalGetSNMPParams.add(PLAIN_TEXT);
    }

    @Inject
    private Logger logger;

    @Override
    public void validate(final NscsPropertyCommand command, final CommandContext context) throws NscsServiceException {

        logger.debug("Starting GetSNMPParamsValidator with command type: {}", command.getCommandType());

        if (!validateKeys(command, expectedGetSNMPParams, optionalGetSNMPParams)) {
            logger.error("Got an unexpected get SNMP parameters '{}' ", command.getProperties().keySet().toString());
            throw new CommandSyntaxException();
        }

        logger.debug("Command validated");
    }

    /**
     * Validates the secadm snmp get command.
     * 
     * Starting from 23.11 (TORF-659913), removed all strict checks on the presence of only expected and optional keys in the command properties.
     * 
     * All checks on the command options are performed by g4 parser and any extra command property is, in any case, not managed by the command
     * handler.
     * 
     * This change is needed for the Compact Audit Logging (CAL) since extra properties are added to contain the CAL parameters.
     * 
     * @param command
     *            the command.
     * @param expectedKeys
     *            the expected property keys.
     * @param optionalKeys
     *            the optional property keys.
     * @return true if valid.
     */
    protected boolean validateKeys(final NscsPropertyCommand command, final Set<String> expectedKeys, final Set<String> optionalKeys) {
        final Set<String> actualAttributeKeys = new HashSet<>(command.getProperties().keySet());
        logger.debug("SNMP get validator: actual keys {}, expected keys {}, optional keys {}", actualAttributeKeys, expectedKeys, optionalKeys);
        return true;
    }

}
