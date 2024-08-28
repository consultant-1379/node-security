package com.ericsson.nms.security.nscs.api;

import javax.ejb.Remote;

import com.ericsson.nms.security.nscs.api.command.*;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.oss.itpf.sdk.core.annotation.EService;

/**
 * The main interface to do any checks/validations or processing of commands
 * 
 * @author eabdsin
 * 
 */
@EService
@Remote
public interface NscsService {

    /**
     * The following method is the main starting point for Nscs service.
     * 
     * @param commandObject
     *            - the commandObject
     * @return NscsCommandResponse
     * @throws NscsServiceException
     *              - the exception throws by NscsService
     */
    NscsCommandResponse processCommand(NscsCliCommand commandObject) throws NscsServiceException;

    /**
     * This method accepts a NscsPropertCommand and works the same way as the other one.
     * 
     * @param commandObject
     *            - the commandObject
     * @return - NscsCommandResponse
     * @throws   NscsServiceException
     *              - the exception throws by NscsService
     */
    NscsCommandResponse processCommand(NscsPropertyCommand commandObject) throws NscsServiceException;
}
