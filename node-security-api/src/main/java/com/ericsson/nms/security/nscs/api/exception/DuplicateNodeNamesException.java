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

public class DuplicateNodeNamesException extends NscsServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 755044771500522680L;

	  {{
	        setSuggestedSolution(NscsErrorCodes.PLEASE_REMOVE_DUPLICATES_FROM_NODE_LIST);
	    }}

	    public DuplicateNodeNamesException() {
	        super(NscsErrorCodes.DUPLICATE_NODE_NAMES);
	    }

        public DuplicateNodeNamesException(final String message) {
	        super(formatMessage(NscsErrorCodes.DUPLICATE_NODE_NAMES, message));
	    }

        public DuplicateNodeNamesException(final String message, final String messageDescription) {
                super(formatMessage(message, messageDescription));
          }
        
	    public DuplicateNodeNamesException(final String message, final Throwable cause) {
	        super(formatMessage(NscsErrorCodes.DUPLICATE_NODE_NAMES, message), cause);
	    }

	    public DuplicateNodeNamesException(final Throwable cause) {
	        super(NscsErrorCodes.DUPLICATE_NODE_NAMES, cause);
	    }

	    /**
	     * @return ErrorType.DUPLICATE_NODE_NAMES_ERROR
	     */
	    @Override
	    public ErrorType getErrorType() {
	        return ErrorType.DUPLICATE_NODE_NAMES_ERROR;
	    }

}
