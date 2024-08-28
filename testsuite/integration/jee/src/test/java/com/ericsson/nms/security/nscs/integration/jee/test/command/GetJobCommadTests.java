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
package com.ericsson.nms.security.nscs.integration.jee.test.command;

public interface GetJobCommadTests {

    /**
     * Get all jobs command when no jobs are present in cache
     *
     * @throws Exception
     *
     */
    void getAllJobsCommand_EmptyJobList() throws Exception;

    /**
     * Get single job id using short version, when no jobs are present in cache
     *
     * @throws Exception
     *
     */
    void getSingleJobCommand_ShortVersion_NotExistingJob() throws Exception;

    /**
     * Get single job id using long version, when no jobs are present in cache
     */
    void getSingleJobCommand_LongVersion_NotExistingJob() throws Exception;

    /**
     * Get list of job ids when no jobs are present in cache
     *
     * @throws Exception
     *
     */
    void jobGetListOfJobsCommand_NotExistingJobs() throws Exception;

    /**
     * Get existing job id using short version
     */
    void getSingleJobCommand_ShortVersion_ExistingJob() throws Exception;

    /**
     * Get existing job id using long version
     */
    void getSingleJobCommand_LongVersion_ExistingJob() throws Exception;

    /**
     * Get existing job id using long version, expected completed status
     */
    void getSingleJobCommand_LongVersion_ExistingCompletedJob() throws Exception;

    /**
     * Get info for job id in malformed format
     */
    void getSingleJobCommand_InvalidJobIdFormat() throws Exception;

    /**
     * Get all command when many job ids are present
     */
    void getAllJobsCommand_NotEmptyJobList() throws Exception;

    /**
     * Get existing job ids command when many job ids are present
     */
    void getListOfJobsCommand_NotEmptyJobList() throws Exception;

    /**
     * @throws Exception
     */
    void getSingleJobCommand_FilteredJob() throws Exception;

}
