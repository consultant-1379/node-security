/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2022
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.rest.response;

import java.io.Serializable;

public class ErrorResponse implements Serializable {

    private static final long serialVersionUID = 7238930615559063877L;

    private String userMessage;
    private String internalErrorCode;
    private String developerMessage;
    private String errorData;

    /**
     * @return the userMessage
     */
    public String getUserMessage() {
        return userMessage;
    }

    /**
     * @param userMessage
     *            the userMessage to set
     */
    public void setUserMessage(final String userMessage) {
        this.userMessage = userMessage;
    }

    /**
     * @return the internalErrorCode
     */
    public String getInternalErrorCode() {
        return internalErrorCode;
    }

    /**
     * @param internalErrorCode
     *            the internalErrorCode to set
     */
    public void setInternalErrorCode(final String internalErrorCode) {
        this.internalErrorCode = internalErrorCode;
    }

    /**
     * @return the developerMessage
     */
    public String getDeveloperMessage() {
        return developerMessage;
    }

    /**
     * @param developerMessage
     *            the developerMessage to set
     */
    public void setDeveloperMessage(final String developerMessage) {
        this.developerMessage = developerMessage;
    }

    /**
     * @return the errorData
     */
    public String getErrorData() {
        return errorData;
    }

    /**
     * @param errorData
     *            the errorData to set
     */
    public void setErrorData(final String errorData) {
        this.errorData = errorData;
    }
}
