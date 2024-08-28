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
package com.ericsson.nms.security.nscs.api.rest;

import java.io.Serializable;

public class IpSecConfigInvalidElement implements Serializable {

    private static final long serialVersionUID = -811835946266711013L;
    private String elementName;
    private String errorMessage;
    private IpSecValidityErrorCode errorCode;

    public IpSecValidityErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(IpSecValidityErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(final String elementName) {
        this.elementName = elementName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(final String errorMessages) {
        this.errorMessage = errorMessages;
    }

    public IpSecConfigInvalidElement() {
        this.elementName = "";
        this.errorMessage = "";
        this.errorCode = IpSecValidityErrorCode.UNKNOWN;
    }

    public IpSecConfigInvalidElement(final String elementName, final String errorMessages, final IpSecValidityErrorCode errorCode) {
        super();
        this.elementName = elementName;
        this.errorMessage = errorMessages;
        this.errorCode = errorCode;
    }
}