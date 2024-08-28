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

import com.ericsson.oss.itpf.sdk.security.accesscontrol.annotation.Authorize;
import com.ericsson.oss.services.jobs.dto.JobDto;
import com.ericsson.oss.services.security.nscs.interceptor.NscsRecordedCommand;
import com.ericsson.oss.services.security.nscs.interceptor.NscsSecurityViolationHandled;
import com.ericsson.oss.services.security.nscs.jobs.JobDtoHelper;

@Stateless
public class NscsJobCacheManagerBean implements NscsJobCacheManager {

    private static final String RESOURCE = "nodesec_job";
    private static final String READ = "read";
    private static final String PATCH = "patch";

    @Inject
    private NscsJobCacheService nscsJobCacheService;

    @Override
    @Authorize(resource = RESOURCE, action = READ)
    @NscsSecurityViolationHandled
    @NscsRecordedCommand
    public List<JobDto> getAllJobs() {
        return nscsJobCacheService.getAllJobs();
    }

    @Override
    @Authorize(resource = RESOURCE, action = READ)
    @NscsSecurityViolationHandled
    @NscsRecordedCommand
    public List<JobDto> getJobList(final String ids) {
        final List<UUID> uuids = JobDtoHelper.fromUuidListDto(ids);
        return nscsJobCacheService.getJobList(uuids);
    }

    @Override
    @Authorize(resource = RESOURCE, action = PATCH)
    @NscsSecurityViolationHandled
    @NscsRecordedCommand
    public JobDto abortJob(final String uuidStr) {
        final UUID uuid = JobDtoHelper.fromUuidDto(uuidStr);
        return nscsJobCacheService.abortJob(uuid);
    }
}