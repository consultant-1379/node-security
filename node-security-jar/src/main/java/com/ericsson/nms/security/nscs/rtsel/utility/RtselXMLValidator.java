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
package com.ericsson.nms.security.nscs.rtsel.utility;

import static com.ericsson.nms.security.nscs.rtsel.utility.RtselConstants.COLON;
import static com.ericsson.nms.security.nscs.rtsel.utility.RtselConstants.DOT;
import static com.ericsson.nms.security.nscs.rtsel.utility.RtselConstants.EMPTY_STRING;
import static com.ericsson.nms.security.nscs.rtsel.utility.RtselConstants.LT_SQUARE_BRACE;
import static com.ericsson.nms.security.nscs.rtsel.utility.RtselConstants.RT_SQUARE_BRACE;
import static com.ericsson.nms.security.nscs.rtsel.utility.RtselConstants.RT_SQUARE_BRACE_COLON;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.enums.ExternalServerLoglevel;
import com.ericsson.nms.security.nscs.api.enums.ExternalServerProtocol;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NscsCommonValidator;

/**
 * This class defines the methods to validate the xml.
 * 
 * @author tcsviga
 *
 */
public class RtselXMLValidator {

    @Inject
    private NscsLogger nscsLogger;

    /**
     * This method will validate external server protocol from the input xml file
     * 
     * @param protocol
     *            external server protocol
     * @throws InvalidArgumentValueException exception thrown for RtselXMLValidator
     */
    public void validateExternalServerProtocol(final String protocol) throws InvalidArgumentValueException {
        final ExternalServerProtocol externalServerProtocol = ExternalServerProtocol.getExternalServerProtocol(protocol);
        if (externalServerProtocol == null) {
            final String errorMessage = String.format(NscsErrorCodes.INVALID_EXTERNAL_SERVER_PROTOCOL, NscsErrorCodes.SUPPORTED_PROTOCOLS + java.util.Arrays.asList(ExternalServerProtocol.values()));
            throw new InvalidArgumentValueException(errorMessage);
        }
    }

    /**
     * This method will validate external server log level from the input xml file
     * 
     * @param logLevel
     *            external server logLevel
     * @throws InvalidArgumentValueException exception thrown for RtselXMLValidator
     */
    public void validateExternalServerLogLevel(final String logLevel) throws InvalidArgumentValueException {
        final ExternalServerLoglevel externalServerLoglevel = ExternalServerLoglevel.getExternalServerLoglevel(logLevel);
        if (externalServerLoglevel == null) {
            final String errorMessage = String.format(NscsErrorCodes.INVALID_EXTERNAL_SERVER_LOGLEVEL, NscsErrorCodes.SUPPORTED_LOG_LEVELS + java.util.Arrays.asList(ExternalServerLoglevel.values()));
            throw new InvalidArgumentValueException(errorMessage);
        }
    }

    /**
     * This method will validate external server address from the input xml file
     * 
     * @param address
     *            external server address
     * @throws InvalidArgumentValueException exception thrown for RtselXMLValidator
     */
    public void validateExternalServerAddress(final String address) throws InvalidArgumentValueException {
        if (address.isEmpty()) {
            final String errorMessage = String.format(NscsErrorCodes.INVALID_EXTERNAL_SERVER_ADDRESS);
            throw new InvalidArgumentValueException(errorMessage);
        } else {
            if (!NscsCommonValidator.getInstance().isValidDomainHostname(address)) {
                validateIpAddress(address);
            }
        }
    }

    /**
     * This method will validate connAttemptTimeOut from the input xml file
     *
     * @param connAttemptTimeOut
     *            connAttemptTimeOut information
     * @throws InvalidArgumentValueException exception thrown for RtselXMLValidator
     */
    public void validateConnectionTimeOut(final int connAttemptTimeOut) throws InvalidArgumentValueException {

        if (connAttemptTimeOut < RtselConstants.MIN_CONN_TIME_OUT || connAttemptTimeOut > RtselConstants.MAX_CONN_TIME_OUT) {
            final String errorMessage = String.format(NscsErrorCodes.INVALID_CONN_ATTEMPT_TIME_OUT);
            throw new InvalidArgumentValueException(errorMessage);
        }
    }

    private void validateIpAddress(String address) {
        nscsLogger.debug("The given address is {}", address);
        try {
            if (!address.contains(DOT) && address.contains(RT_SQUARE_BRACE_COLON) && address.contains(LT_SQUARE_BRACE)) {
                address = address.split(RT_SQUARE_BRACE_COLON)[0].replace(LT_SQUARE_BRACE, EMPTY_STRING);

            } else if (address.contains(DOT) && address.contains(COLON)) {
                address = address.split(COLON)[0];
            }

            final Object res = InetAddress.getByName(address);
            if (address.endsWith(RT_SQUARE_BRACE) || !(res instanceof Inet4Address || res instanceof Inet6Address)) {
                throw new InvalidArgumentValueException(NscsErrorCodes.INVALID_EXTERNAL_SERVER_ADDRESS);
            }
        } catch (final UnknownHostException ex) {
            nscsLogger.error("Address is not a valid IPv4 or IPv6 type :{}", ex);
            throw new InvalidArgumentValueException(NscsErrorCodes.INVALID_EXTERNAL_SERVER_ADDRESS);
        }
    }
}
