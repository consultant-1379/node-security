/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2019
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.command.types;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * Auxiliary class for the secadm job get command.
 */
public class JobGetCommand extends NscsNodeCommand {

    private static final long serialVersionUID = 3088320921340455454L;

    public static final String JOB_NUMBER_PROPERTY = "joblist";
    public static final String JOB_FILTER_PROPERTY = "wf";
    public static final String ALL_JOBS_VALUE = "*";
    private static final String JOB_SUMMARY_PROPERTY = "summary";

    /**
     * Returns the wanted job list
     *
     * @return the job list
     */
    public List<String> getJobList() {
        List<String> jobs = null;
        if (isAllJobs()) {
            jobs = Collections.emptyList();
        } else if (hasProperty(JOB_NUMBER_PROPERTY)) {
            jobs = (List<String>) getValue(JOB_NUMBER_PROPERTY);
        }

        return jobs;
    }

    private boolean isAllJobs() {
        return ALL_JOBS_VALUE.equals(getValue(JOB_NUMBER_PROPERTY));
    }

    public List<String> getWfFilters() {

        final List<String> filters = (List<String>) getValue(JOB_FILTER_PROPERTY);

        if (filters != null) {

            final ListIterator<String> iterator = filters.listIterator();

            while (iterator.hasNext()) {
                iterator.set(iterator.next().toUpperCase());
            }
        }

        return filters;
    }

    /**
     * Returns if summary is requested
     * 
     * @return true if summary is requested
     */
    public boolean isSummary() {
        return hasProperty(JOB_SUMMARY_PROPERTY);
    }
}
