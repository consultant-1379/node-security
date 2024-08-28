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

public class PropertiesFileNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -4194554115909728754L;

	public PropertiesFileNotFoundException() {
	}

	public PropertiesFileNotFoundException(final String message) {
		super(message);
	}

	public PropertiesFileNotFoundException(final String message,
			final Throwable cause) {
		super(message, cause);
	}

	public PropertiesFileNotFoundException(final Throwable cause) {
		super(cause);
	}

}
