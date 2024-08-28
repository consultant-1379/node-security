package com.ericsson.nms.security.nscs.api.exception;

import javax.ejb.ApplicationException;

import com.ericsson.nms.security.nscs.api.exception.NscsServiceException.ErrorType;

/**
 * Exception for CredentialService api.
 */
@ApplicationException(rollback = false)
public class CredentialServiceException extends RuntimeException {

	private static final long serialVersionUID = 2945770435852399415L;

	private String suggestedSolution = NscsErrorCodes.SUGGESTED_SOLUTION_CONSULT_ERROR_LOGS;

    public static final int ERROR_CODE_START_INT = 10000;
    private int errorCode;
    private ErrorType errorType;
    
    public CredentialServiceException() {
    }

    public CredentialServiceException(final String message) {
        super(message);
    }

    public CredentialServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CredentialServiceException(final Throwable cause) {
        super(cause);
    }

    /**
     * @return error code of the exception
     */
    public int getErrorCode() {
        return ERROR_CODE_START_INT + this.errorCode;
    }
    
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
    
    /**
     * Each subclass of NscsServiceException has it's own ErrorType
     * 
     * @return the Error type
     */
    public ErrorType getErrorType() {
    	return this.errorType;
    }
    
	public void setErrorType(ErrorType errorType) {
		this.errorType= errorType;	
	}

    /**
     * Gets the proposed solution for this error.
     *
     * @return String with the proposed solution or empty String.
     */
    public String getSuggestedSolution() {
        return suggestedSolution;
    }

    /**
     * Sets the proposed solution for this exception.
     *
     * @param suggestedSolution String of the proposed solution
     * @return this instance
     */
    public CredentialServiceException setSuggestedSolution(final String suggestedSolution) {
        this.suggestedSolution = suggestedSolution == null ? "" : suggestedSolution;
        return this;
    }

    /**
     * <p>Convenience method to set proposed solution.</p>
     * <p>Internally this method call String.format(suggestedSolution, args) </p>
     *
     * @param suggestedSolution Suggested solution message. You can use any valid String.format placeholder.
     * @param args arguments so be placed at the placeholders.
     * @return this instance
     */
    public CredentialServiceException setSuggestedSolution(final String suggestedSolution, final Object ... args) {
        return setSuggestedSolution(String.format(suggestedSolution, args));
    }

    /**
     * Convenience method to subclasses so the can easily create messages with the format 'message part1 : message part2'
     * 
     * @param part1 the part1
     * @param part2 the part2
     * @return formatted message
     */
    protected static String formatMessage(final String part1, final String part2) {
        return String.format("%s : %s", part1, part2);
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof CredentialServiceException)) { return false; }

        final CredentialServiceException that = (CredentialServiceException) o;

        if (!(getErrorCode() == that.getErrorCode())){ return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return getErrorType().hashCode();
    }

}
