package com.ericsson.nms.security.nscs.api.exception;

/**
 * <p>Exception thrown by the NodeMustHaveNetworkElementSecurityMoValidator class
 * when one or more nodes in the command don't have NetworkElementSecurity MO
 * associated to it.</p>
 * Created by emaynes on 13/05/2014.
 */
public class NetworkElementSecurityNotfoundException extends NscsServiceException {

    private static final long serialVersionUID = -4989736491044542299L;

    public NetworkElementSecurityNotfoundException() {
        super(NscsErrorCodes.CREDENTIALS_DO_NOT_EXIST_FOR_THE_NODE_SPECIFIED);
        setSuggestedSolution(NscsErrorCodes.PLEASE_CREATE_CREDENTIALS_FOR_THE_NODE);
    }

    /**
     * @return ErrorType.NETWORK_ELEMENT_SECURITY_NOTFOUND_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.NETWORK_ELEMENT_SECURITY_NOTFOUND_ERROR;
    }
}
