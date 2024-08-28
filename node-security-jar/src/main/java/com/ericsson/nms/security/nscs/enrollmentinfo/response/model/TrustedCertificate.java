/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.enrollmentinfo.response.model;

import java.io.Serializable;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "trustedCertificate")
public class TrustedCertificate implements Serializable {

    private static final long serialVersionUID = 3614078938023470961L;

    protected String name;

    protected String caSubjectName;

    protected String cafingerprint;

    protected String tdpsUri;

    protected String caPem;

    @XmlElement(required = true)
    protected CertificateRevocations crls;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the caSubjectName
     */
    public String getCaSubjectName() {
        return caSubjectName;
    }

    /**
     * @param caSubjectName
     *            The caSubjectName to set
     */
    public void setCaSubjectName(final String caSubjectName) {
        this.caSubjectName = caSubjectName;
    }

    /**
     * @return the cafingerprint
     */
    public String getCafingerprint() {
        return cafingerprint;
    }

    /**
     * @param cafingerprint
     *            The cafingerprint to set
     */
    public void setCafingerprint(final String cafingerprint) {
        this.cafingerprint = cafingerprint;
    }

    /**
     * @return the tdpsUri
     */
    public String getTdpsUri() {
        return tdpsUri;
    }

    /**
     * @param tdpsUri
     *            The tdpsUri to set.
     */
    public void setTdpsUri(final String tdpsUri) {
        this.tdpsUri = tdpsUri;
    }

    /**
     * @return the caPem
     */
    public String getCaPem() {
        return caPem;
    }

    /**
     * @param caPem
     *            The caPem to set
     */
    public void setCaPem(final String caPem) {
        this.caPem = caPem;
    }

    public CertificateRevocations getCrls() {
        return crls;
    }

    public void setCrls(CertificateRevocations crls) {
        this.crls = crls;
    }
}
