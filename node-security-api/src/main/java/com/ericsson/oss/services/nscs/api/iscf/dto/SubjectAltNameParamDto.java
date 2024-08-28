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
package com.ericsson.oss.services.nscs.api.iscf.dto;

import java.io.Serializable;

import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;

public class SubjectAltNameParamDto implements Serializable {

    private static final long serialVersionUID = 7352766178633795029L;

    private SubjectAltNameFormat subjectAltNameFormat = null;
    // for simple string name
    private String value = null;
    // for ediPartyName
    private String nameAssigner = null;
    private String partyName = null;

    public SubjectAltNameParamDto() {
    }

    /**
     * Constructor for simple string name.
     * 
     * @param subjectAltNameFormat
     *            the subject alternative name format.
     * @param value
     *            the simple string value of subject alternative name.
     */
    public SubjectAltNameParamDto(final SubjectAltNameFormat subjectAltNameFormat, final String value) {
        this.subjectAltNameFormat = subjectAltNameFormat;
        this.value = value;
        this.nameAssigner = null;
        this.partyName = null;
    }

    /**
     * Constructor for EDI party name.
     * 
     * @param nameAssigner
     *            the name assigner.
     * @param partyName
     *            the part name.
     */
    public SubjectAltNameParamDto(final String nameAssigner, final String partyName) {
        this.subjectAltNameFormat = SubjectAltNameFormat.FQDN;
        this.value = null;
        this.nameAssigner = nameAssigner;
        this.partyName = partyName;
    }

    /**
     * @return the subjectAltNameFormat
     */
    public SubjectAltNameFormat getSubjectAltNameFormat() {
        return subjectAltNameFormat;
    }

    /**
     * @param subjectAltNameFormat
     *            the subjectAltNameFormat to set
     */
    public void setSubjectAltNameFormat(final SubjectAltNameFormat subjectAltNameFormat) {
        this.subjectAltNameFormat = subjectAltNameFormat;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * @return the nameAssigner
     */
    public String getNameAssigner() {
        return nameAssigner;
    }

    /**
     * @param nameAssigner
     *            the nameAssigner to set
     */
    public void setNameAssigner(final String nameAssigner) {
        this.nameAssigner = nameAssigner;
    }

    /**
     * @return the partyName
     */
    public String getPartyName() {
        return partyName;
    }

    /**
     * @param partyName
     *            the partyName to set
     */
    public void setPartyName(final String partyName) {
        this.partyName = partyName;
    }
}
