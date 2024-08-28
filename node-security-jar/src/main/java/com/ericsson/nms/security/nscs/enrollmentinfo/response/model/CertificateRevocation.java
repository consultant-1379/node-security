/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.enrollmentinfo.response.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "crl")
public class CertificateRevocation implements Serializable  {

    private static final long serialVersionUID = 3614078938023470961L;

    protected String crlName;
    protected String crlUri;

    public String getCrlName() {
        return crlName;
    }

    public void setCrlName(String crlName) {
        this.crlName = crlName;
    }

    public String getCrlUri() {
        return crlUri;
    }

    public void setCrlUri(String crlUri) {
        this.crlUri = crlUri;
    }
}
