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
package com.ericsson.oss.services.security.nscs.model;

import javax.ejb.Local;

@Local
public interface NscsModelManager {

    Object getTargetInfo(String targetCategory, String targetType, String targetModelIdentity);

    Object getModelInfo(String targetCategory, String targetType, String targetModelIdentity, String namespace, String type);

    Object getTargetPO(String fdn);
}
