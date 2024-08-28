package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Throw when a LAAD file installation process has failed</p>
 * Created by emaynes on 02/05/2014.
 */
public class LaadFileInstallationException extends NscsServiceException {

    private static final long serialVersionUID = -1981099350472020058L;

    public LaadFileInstallationException() {
        super(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR);
    }

    {{
        setSuggestedSolution(NscsErrorCodes.SUGGESTED_SOLUTION_CONSULT_ERROR_LOGS);
    }}

    public LaadFileInstallationException(final String message) {
        super(formatMessage(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, message));
    }

    public LaadFileInstallationException(final String message, final Throwable cause) {
        super(formatMessage(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, message), cause);
    }

    public LaadFileInstallationException(final Throwable cause) {
        super(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR, cause);
    }

    /**
     * @return ErrorType.LAAD_FILE_INSTALLATION_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.LAAD_FILE_INSTALLATION_ERROR;
    }
}
