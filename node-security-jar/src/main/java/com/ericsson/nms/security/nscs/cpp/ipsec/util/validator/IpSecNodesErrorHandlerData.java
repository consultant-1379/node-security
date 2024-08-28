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
package com.ericsson.nms.security.nscs.cpp.ipsec.util.validator;

import com.ericsson.nms.security.nscs.api.rest.IpSecValidityErrorCode;

public class IpSecNodesErrorHandlerData {

    private IpSecValidityErrorCode errorCode;
    private String errorMessage;

    public void setErrorCode(IpSecValidityErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public IpSecValidityErrorCode getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public IpSecNodesErrorHandlerData(IpSecValidityErrorCode errorCode, String errorMessage) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public IpSecNodesErrorHandlerData() {

    }

}
