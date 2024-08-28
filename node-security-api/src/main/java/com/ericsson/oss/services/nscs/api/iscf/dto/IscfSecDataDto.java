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

import java.net.StandardProtocolFamily;

/**
 * ISCF Security Data DTO.
 */
public class IscfSecDataDto extends IscfBaseDto {

    private static final long serialVersionUID = 31690274464597115L;

    private NodeIdentifierDto nodeId;
    private SubjectAltNameParamDto subjectAltNameParam;
    private StandardProtocolFamily ipVersion;

    /**
     * @return the nodeId
     */
    public NodeIdentifierDto getNodeId() {
        return nodeId;
    }

    /**
     * @param nodeId
     *            the nodeId to set
     */
    public void setNodeId(final NodeIdentifierDto nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * @return the subjectAltNameParam
     */
    public SubjectAltNameParamDto getSubjectAltNameParam() {
        return subjectAltNameParam;
    }

    /**
     * @param subjectAltNameParam
     *            the subjectAltNameParam to set
     */
    public void setSubjectAltNameParam(final SubjectAltNameParamDto subjectAltNameParam) {
        this.subjectAltNameParam = subjectAltNameParam;
    }

    /**
     * @return the ipVersion
     */
    public StandardProtocolFamily getIpVersion() {
        return ipVersion;
    }

    /**
     * @param ipVersion
     *            the ipVersion to set
     */
    public void setIpVersion(final StandardProtocolFamily ipVersion) {
        this.ipVersion = ipVersion;
    }
}
