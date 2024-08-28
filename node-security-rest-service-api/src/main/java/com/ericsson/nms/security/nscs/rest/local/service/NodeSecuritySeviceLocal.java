/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016

 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.rest.local.service;

import java.util.List;

import javax.ejb.Local;

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.model.SecurityLevelSwitchStatus;
import com.ericsson.oss.itpf.sdk.core.annotation.EService;

/**
 * 
 * <ul>
 * <li>This interface is for rest service of node security. It supports the following operations
 * <li>1. Change security level
 * </ul>
 * 
 */

@EService
@Local
public interface NodeSecuritySeviceLocal {

    /**
     * <ul>
     * <li>Method to initiate the change security level request
     * 
     * @param The
     *            parameters of this method accepts i.e., two parameters which is nodeList, secLevel
     * @return a JSON which contains success or failure messages of the security level initiation
     *         </ul>
     */

    List<SecurityLevelSwitchStatus> changeSecurityLevel(List<String> nodeNames, SecurityLevel wantedSecLevel);

}
