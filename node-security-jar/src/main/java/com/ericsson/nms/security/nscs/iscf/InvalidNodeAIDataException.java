package com.ericsson.nms.security.nscs.iscf;

/**
 * User: emacgma
 * Date: 19/06/14
 * Time: 13:47
 */
public class InvalidNodeAIDataException extends RuntimeException {

	private static final long serialVersionUID = -3941334030783098563L;

	public InvalidNodeAIDataException(final String message) {
        super(message);
    }
}
