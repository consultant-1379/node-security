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
 * REST ISCF XML OAM DTO.
 */
public class IscfXmlOamDto extends IscfXmlBaseDto {

    private static final long serialVersionUID = 6393681007296061303L;

    private IscfXmlOamParamsDto params;

    /**
     * @return the params
     */
    public IscfXmlOamParamsDto getParams() {
        return params;
    }

    /**
     * @param params
     *            the params to set
     */
    public void setParams(final IscfXmlOamParamsDto params) {
        this.params = params;
    }
}
