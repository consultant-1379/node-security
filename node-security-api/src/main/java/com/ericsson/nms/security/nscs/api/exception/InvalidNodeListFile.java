package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception throw by the command parser when a command passes a file
 * with a list of node names but the file could not be parsed properly,
 * probably it is a wrong format.</p>
 * Created by emaynes on 22/05/2014.
 */
public class InvalidNodeListFile extends NscsServiceException {

    public InvalidNodeListFile() {
        super(NscsErrorCodes.THE_CONTENTS_OF_THE_FILE_PROVIDED_ARE_NOT_IN_THE_CORRECT_FORMAT);
    }

    {{
        setSuggestedSolution(NscsErrorCodes.PLEASE_SEE_THE_ONLINE_HELP_FOR_THE_CORRECT_FORMAT_OF_THE_CONTENTS_OF_THE_FILE);
    }}

    public InvalidNodeListFile(final String message) {
        super(message);
    }

    public InvalidNodeListFile(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InvalidNodeListFile(final Throwable cause) {
        super(NscsErrorCodes.THE_CONTENTS_OF_THE_FILE_PROVIDED_ARE_NOT_IN_THE_CORRECT_FORMAT, cause);
    }

    /**
     * @return ErrorType.INVALID_NODE_FILE_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.INVALID_NODE_FILE_ERROR;
    }
}
