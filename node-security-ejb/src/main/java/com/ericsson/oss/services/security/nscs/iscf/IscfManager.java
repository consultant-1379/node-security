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
package com.ericsson.oss.services.security.nscs.iscf;

import javax.ejb.Local;

import com.ericsson.nms.security.nscs.api.iscf.IscfResponse;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse;
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfSecDataDto;
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfXmlComboDto;
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfXmlIpsecDto;
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfXmlOamDto;

@Local
public interface IscfManager {

    IscfResponse generateXmlOam(IscfXmlOamDto dto);

    IscfResponse generateXmlIpsec(IscfXmlIpsecDto dto);

    IscfResponse generateXmlCombo(IscfXmlComboDto dto);

    String cancel(String node);

    SecurityDataResponse generateSecurityDataOam(IscfSecDataDto dto);

    SecurityDataResponse generateSecurityDataIpsec(IscfSecDataDto dto);

    SecurityDataResponse generateSecurityDataCombo(IscfSecDataDto dto);
}
