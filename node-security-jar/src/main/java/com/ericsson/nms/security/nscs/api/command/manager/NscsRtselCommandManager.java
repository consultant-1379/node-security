/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
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

import com.ericsson.nms.security.nscs.rtsel.utility.RtselJobInfo;
import com.ericsson.nms.security.nscs.rtsel.utility.RtselDeleteServerDetails;
import com.ericsson.oss.services.dto.JobStatusRecord;

/**
 * This interface is having the workflow methods for all RTSEL CommandHandlers.
 * 
 * @author xchowja
 *
 */
public interface NscsRtselCommandManager {

    /**
     * @param rtselJobInfoList
     *            is the list of RtselJobInfo values
     * @param jobStatusRecord
     *            the Rtsel jobStatusRecord.
     * @return
     */
    void executeActivateRtselWfs(final List<RtselJobInfo> rtselJobInfoList, final JobStatusRecord jobStatusRecord);

    /**
     * @param nodeFdnList
     *            the list of all node Fdns
     * @param jobStatusRecord
     *            the Rtsel jobStatusRecord.
     */
    void executeDeActivateRtselWfs(final List<String> nodeFdnList, final JobStatusRecord jobStatusRecord);

    /**
    * @param rtselDeleteServerJobInfoList
    * @param jobStatusRecord
    */
    void executeRtselDeleteServerWfs(final List<RtselDeleteServerDetails> rtselDeleteServerDetailsList, final JobStatusRecord jobStatusRecord);


}
