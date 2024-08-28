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
package com.ericsson.oss.services.security.nscs.job;

import java.util.List;
import java.util.UUID;

import javax.ejb.Local;

import com.ericsson.oss.services.jobs.dto.JobDto;

@Local
public interface NscsJobCacheService {

    List<JobDto> getAllJobs();

    List<JobDto> getJobList(List<UUID> uuids);

    JobDto abortJob(UUID uuid);
}
