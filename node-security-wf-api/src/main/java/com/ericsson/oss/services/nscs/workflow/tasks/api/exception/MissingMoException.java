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
package com.ericsson.oss.services.nscs.workflow.tasks.api.exception;

/**
 * Exception thrown by workflow task handlers when an MO can't be found in the
 * target node.
 * 
 * @author emaborz
 * 
 */
public class MissingMoException extends WorkflowTaskException {

	private static final long serialVersionUID = 3033178470323451344L;

	public MissingMoException() {
	}

	/**
	 * @deprecated Use {@link #MissingMoException(String,String)} instead. It
	 *             does not use any more the 'namespace' parameter.
	 */
	public MissingMoException(final String nodeName, final String moType, final String moNamespace) {
		this(nodeName, moType);
	}

	public MissingMoException(final String nodeName, final String moType) {
		super("Can't get MO of type [" + moType + "] for node [" + nodeName + "].");
	}

	public MissingMoException(final String message) {
		super(message);
	}

	public MissingMoException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public MissingMoException(final Throwable cause) {
		super(cause);
	}

	public MissingMoException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
