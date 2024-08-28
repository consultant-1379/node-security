/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.exception;

public class PlatformSpecificConfigurationProviderException extends NscsServiceException {
    private static final long serialVersionUID = -3164459083651080028L;

    public static final String LDAP_BASE_DN_INVALID = "LDAP base_dn is not set due to error in accessing global.properties file";

    public PlatformSpecificConfigurationProviderException() {
        super(NscsErrorCodes.PLATFORM_CONFIGURATION_UNAVAILABLE);
    }

    public PlatformSpecificConfigurationProviderException(final String message) {
        super(formatMessage(NscsErrorCodes.PLATFORM_CONFIGURATION_UNAVAILABLE, message));
    }

    @Override
    public ErrorType getErrorType() {
        return ErrorType.PLATFORM_CONFIGURATION_UNAVAILABLE;
    }

}
