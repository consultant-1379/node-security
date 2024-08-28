/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.cpp.level;

import javax.ejb.Local;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.oss.itpf.sdk.core.annotation.EService;

@EService
@Local
public interface CppIpSecService {

    void updateSummaryFileHashAttributeOfMo(final NodeReference nodeRef, final String summaryFileHash);

}
