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
package com.ericsson.nms.security.nscs.api.instrumentation;

import javax.ejb.Local;

/**
 *
 * This interface contains the getters and setters to access the NSCS instrumentation bean
 *
 * The implementation for these methods is available in the NscsInstrumentationServiceImpl
 *
 */
@Local
public interface NscsInstrumentationService {

    /**
     * Returns the number of running workflows.
     *
     * @return the number of running workflows
     */
    long getNumOfRunningWorkflows();

    /**
     * Sets the number of running workflows.
     *
     * @param runningWf
     *            the number of running workflows
     */
    void setNumOfRunningWorkflows(final int runningWf);

    /**
     * Returns the number of pending workflows.
     *
     * @return the number of pending workflows
     */
    long getNumOfPendingWorkflows();

    /**
     * Sets the number of pending workflow
     *
     * @param pendingWf
     *            the number of pending workflows
     */
    void setNumOfPendingWorkflows(final int pendingWf);

    /**
     * Returns the number of workflows finished with success.
     *
     * @return the number of workflows finished with success
     */
    long getNumOfSuccessfulWorkflows();

    /**
     * Returns the number of workflows finished with failure.
     *
     * @return the number of workflows finished with failure
     */
    long getNumOfFailedWorkflows();

    /**
     * Returns the number of workflows finished with error.
     *
     * @return the number of workflows finished with error
     */
    long getNumOfErroredWorkflows();

    /**
     * Returns the number of workflows finished with timeout.
     *
     * @return the number of workflows finished with timeout
     */
    long getNumOfTimedOutWorkflows();

    /**
     * Updates statistics for successful ISCF service invocations.
     */
    void updateSuccessfulIscfServiceInvocations();

    /**
     * Updates statistics for unsuccessful ISCF service invocations.
     */
    void updateFailedIscfServiceInvocations();

    /**
     * Gets total number of successful or failed or errored or timed-out instances of given monitored entity type.
     *
     * @param monitoredEntityType
     *            the monitored entity type
     * @return the total number of instances or 0 if no instance of given type
     */
    long getNumOfInstances(final NscsMonitoredEntityTypes monitoredEntityType);

    /**
     * Gets total number of unsuccessful (failed or errored or timed-out) instances of given monitored entity type.
     *
     * @param monitoredEntityType
     *            the monitored entity type
     * @return the total number of unsuccessful instances or 0 if no instance of given type
     */
    long getNumOfUnsuccessfulInstances(final NscsMonitoredEntityTypes monitoredEntityType);

    /**
     * Gets number of successful instances of given monitored entity type.
     *
     * @param monitoredEntityType
     *            the monitored entity type
     * @return the number of successful instances or 0 if no instance of given type
     */
    long getNumOfSuccessfulInstances(final NscsMonitoredEntityTypes monitoredEntityType);

    /**
     * Gets average duration of successful instances of given monitored entity type.
     *
     * @param monitoredEntityType
     *            the monitored entity type
     * @return the number of successful instances or 0 if no instance of given type
     */
    long getAverageDurationOfSuccessfulInstances(final NscsMonitoredEntityTypes monitoredEntityType);

    /**
     * Updates statistics for workflows finished with success of given name and duration.
     *
     * @param workflowName
     *            the workflow name
     * @param duration
     *            the duration (in millis) of the workflow
     */
    void updateSuccessfulWorkflow(final String workflowName, final Long duration);

    /**
     * Gets number of failed instances of given monitored entity type.
     *
     * @param monitoredEntityType
     *            the monitored entity type
     * @return the number of failed instances or 0 if no instance of given type
     */
    long getNumOfFailedInstances(final NscsMonitoredEntityTypes monitoredEntityType);

    /**
     * Updates statistics for workflows finished with failure of given name.
     *
     * @param workflowName
     *            the workflow name
     */
    void updateFailedWorkflow(final String workflowName);

    /**
     * Gets number of errored instances of given monitored entity type.
     *
     * @param monitoredEntityType
     *            the monitored entity type
     * @return the number of errored instances or 0 if no instance of given type
     */
    long getNumOfErroredInstances(final NscsMonitoredEntityTypes monitoredEntityType);

    /**
     * Updates statistics for workflows finished with error of given name.
     *
     * @param workflowName
     *            the workflow name
     */
    void updateErroredWorkflow(final String workflowName);

    /**
     * Gets number of timed-out instances of given monitored entity type.
     *
     * @param monitoredEntityType
     *            the monitored entity type
     * @return the number of timed-out instances or 0 if no instance of given type
     */
    long getNumOfTimedOutInstances(final NscsMonitoredEntityTypes monitoredEntityType);

    /**
     * Updates statistics for workflows finished with timeout of given name.
     *
     * @param workflowName
     *            the workflow name
     */
    void updateTimedOutWorkflow(final String workflowName);
}
