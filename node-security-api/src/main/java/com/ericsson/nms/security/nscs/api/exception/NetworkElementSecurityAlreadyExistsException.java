package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by the Create credentials command handler class
 * when one or more nodes in the command already have NetworkElementSecurity MO
 * associated to it.</p>
 * Created by emaynes on 13/05/2014.
 */
public class NetworkElementSecurityAlreadyExistsException extends NscsServiceException {

    private static final long serialVersionUID = -4989736491044542299L;

    public NetworkElementSecurityAlreadyExistsException() {
        super(NscsErrorCodes.CREDENTIALS_ALREADY_EXIST_FOR_THE_NODE_SPECIFIED);
        setSuggestedSolution(NscsErrorCodes.PLEASE_SPECIFY_NODES_WITHOUT_EXISTING_CREDENTIALS_DEFINED);
    }

    /**
     * @return ErrorType.NETWORK_ELEMENT_SECURITY_ALREADY_EXISTS_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.NETWORK_ELEMENT_SECURITY_ALREADY_EXISTS_ERROR;
    }
}
