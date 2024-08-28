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
 * REST ISCF XML COMBO DTO.
 */
public class IscfXmlComboDto extends IscfXmlBaseDto {

    private static final long serialVersionUID = 6393681007296061303L;

    private IscfXmlOamParamsDto oamParams;
    private IscfXmlIpsecParamsDto ipsecParams;

    /**
     * @return the oamParams
     */
    public IscfXmlOamParamsDto getOamParams() {
        return oamParams;
    }

    /**
     * @param oamParams
     *            the oamParams to set
     */
    public void setOamParams(final IscfXmlOamParamsDto oamParams) {
        this.oamParams = oamParams;
    }

    /**
     * @return the ipsecParams
     */
    public IscfXmlIpsecParamsDto getIpsecParams() {
        return ipsecParams;
    }

    /**
     * @param ipsecParams
     *            the ipsecParams to set
     */
    public void setIpsecParams(final IscfXmlIpsecParamsDto ipsecParams) {
        this.ipsecParams = ipsecParams;
    }
}
