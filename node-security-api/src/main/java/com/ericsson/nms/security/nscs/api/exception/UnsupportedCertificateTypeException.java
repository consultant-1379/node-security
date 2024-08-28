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
package com.ericsson.nms.security.nscs.api.exception;


/**
 * <p>
 * Exception thrown by the command validation methods in case an unsupported
 * certificate type condition is detected.
 * </p>
 */
public class UnsupportedCertificateTypeException extends NscsServiceException {

	private static final long serialVersionUID = -5874818666840186674L;

    {{
        setSuggestedSolution(NscsErrorCodes.CHECK_OLH_FOR_SUPPORTED_CERT_TYPES);
    }}

    public UnsupportedCertificateTypeException() {
        super(NscsErrorCodes.UNSUPPORTED_CERTIFICATE_TYPE);
    }

    public UnsupportedCertificateTypeException(final String message) {
        super(formatMessage(NscsErrorCodes.UNSUPPORTED_CERTIFICATE_TYPE, message));
    }

    public UnsupportedCertificateTypeException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.UNSUPPORTED_CERTIFICATE_TYPE, message), cause);
    }

    public UnsupportedCertificateTypeException(final Throwable cause) {
        super(NscsErrorCodes.UNSUPPORTED_CERTIFICATE_TYPE, cause);
    }

    /**
     * Gets the error type
     *
     * @return ErrorType.UNSUPPORTED_CERTIFICATE_TYPE
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.UNSUPPORTED_CERTIFICATE_TYPE;
    }

}
