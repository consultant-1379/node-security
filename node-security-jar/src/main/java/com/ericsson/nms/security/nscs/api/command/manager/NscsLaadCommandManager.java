/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2018
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.command.manager;

import java.util.List;

import com.ericsson.oss.services.dto.JobStatusRecord;

/**
 * This interface manages the workflow for all LAAD Operations.
 *
 * @author tcsgoja
 *
 */
public interface NscsLaadCommandManager {

    /**
     * Executes the LaadFilesDistributeWorkFlow for the valid nodes.
     *
     * @param nodeFdnList
     *            the list of all node Fdns
     * @param jobStatusRecord
     *            the laad jobStatusRecord
     *
     * @author xkihari
     */

    void executeLaadFilesDistributeWorkFlow(final List<String> nodeFdnList, final JobStatusRecord jobStatusRecord);

}
