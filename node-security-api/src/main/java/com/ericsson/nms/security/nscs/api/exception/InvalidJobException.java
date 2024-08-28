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

public class InvalidJobException extends NscsServiceException {

	private static final long serialVersionUID = -8008299008955693201L;
	
	  {
	        {
	            setSuggestedSolution(NscsErrorCodes.PLEASE_PROVIDE_VALID_JOB_ID);
	        }
	    }

	/**
     * Creates a new instance of exception
     * without any detail message.
	 */
//	public InvalidJobException() {
//		 super(NscsErrorCodes.INVALID_JOB_EXCEPTION_MESSAGE);
//	}

	/**
     * Constructs an instance of exception
     * with the specified detail message.
     *
	 * @param message The detail message
	 */
	public InvalidJobException(String message) {
		 super(message);
	}

	 public InvalidJobException(final String message, final Throwable cause) {
	        super(message, cause);
	    }

	    /**
	     * Constructs a new LdapConfigurationException exception with the specified
	     * cause
	     *
		 */
//	    public InvalidJobException(final Throwable cause) {
//	        super(NscsErrorCodes.INVALID_JOB_EXCEPTION_MESSAGE, cause);
//	    }

	@Override
	public ErrorType getErrorType() {
		// TODO Auto-generated method stub
		return ErrorType.INVALID_JOB;
	}

}
