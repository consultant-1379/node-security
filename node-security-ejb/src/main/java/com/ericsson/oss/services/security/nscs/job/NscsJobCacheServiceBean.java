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

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.ericsson.oss.services.jobs.dto.JobDto;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;

@Stateless
public class NscsJobCacheServiceBean implements NscsJobCacheService {

    @Inject
    private NscsJobCacheHandler nscsJobCacheHandler;

    @Override
    public List<JobDto> getAllJobs() {
        return nscsJobCacheHandler.getAllJobs();
    }

    @Override
    public List<JobDto> getJobList(final List<UUID> uuids) {
        return nscsJobCacheHandler.getJobList(uuids);
    }

    @Override
    public JobDto abortJob(final UUID uuid) {
        return nscsJobCacheHandler.abortJob(uuid);
    }

}
