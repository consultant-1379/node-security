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

/**
 * Base ISCF XML DTO.
 */
public class IscfXmlBaseDto extends IscfBaseDto {

    private static final long serialVersionUID = 4356461603626115644L;

    private String logicalName;
    private String nodeFdn;

    /**
     * @return the logicalName
     */
    public String getLogicalName() {
        return logicalName;
    }

    /**
     * @param logicalName
     *            the logicalName to set
     */
    public void setLogicalName(final String logicalName) {
        this.logicalName = logicalName;
    }

    /**
     * @return the nodeFdn
     */
    public String getNodeFdn() {
        return nodeFdn;
    }

    /**
     * @param nodeFdn
     *            the nodeFdn to set
     */
    public void setNodeFdn(final String nodeFdn) {
        this.nodeFdn = nodeFdn;
    }
}
