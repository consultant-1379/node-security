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
package com.ericsson.nms.security.nscs.api.enums;


/**
 * @author tcsviga
 *
 */
public enum ExternalServerLoglevel {

    EMERGENCY, ALERT, CRITICAL, ERROR, WARNING, NOTICE, INFO, DEBUG;

    @Override
    public String toString() {
        return this.name();
    }

    /**
     * @param logLevel input loglevel
     * @return Get the ExternalServerLoglevel from the String value
     * 
     */

    public static ExternalServerLoglevel getExternalServerLoglevel(final String logLevel) {
        ExternalServerLoglevel externalServerLoglevel = null;
        switch (logLevel) {
        case "EMERGENCY":
            externalServerLoglevel = ExternalServerLoglevel.EMERGENCY;
            break;
        case "ALERT":
            externalServerLoglevel = ExternalServerLoglevel.ALERT;
            break;

        case "CRITICAL":
            externalServerLoglevel = ExternalServerLoglevel.CRITICAL;
            break;
        case "ERROR":
            externalServerLoglevel = ExternalServerLoglevel.ERROR;
            break;
        case "WARNING":
            externalServerLoglevel = ExternalServerLoglevel.WARNING;
            break;
        case "NOTICE":
            externalServerLoglevel = ExternalServerLoglevel.NOTICE;
            break;
        case "INFO":
            externalServerLoglevel = ExternalServerLoglevel.INFO;
            break;
        case "DEBUG":
            externalServerLoglevel = ExternalServerLoglevel.DEBUG;
            break;
        default:
            break;
        }
        return externalServerLoglevel;
    }
}
