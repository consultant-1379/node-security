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
package com.ericsson.oss.services.security.nscs.instrumentation;

import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.instrumentation.NscsInstrumentationService;
import com.ericsson.nms.security.nscs.api.instrumentation.NscsMonitoredEntityStats;
import com.ericsson.nms.security.nscs.api.instrumentation.NscsMonitoredEntityTypes;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowNames;

/**
 * Utility class wrapping the metrics logic used in the instrumentation.
 */
@ApplicationScoped
public class NscsInstrumentationServiceImpl implements NscsInstrumentationService {

    /**
     * Dynamic counters
     */
    private AtomicInteger numOfPendingWorkflows = new AtomicInteger(0);
    private AtomicInteger numOfRunningWorkflows = new AtomicInteger(0);

    /**
     * Global workflows counters
     */
    private AtomicInteger numOfSuccessfulWorkflows = new AtomicInteger(0);
    private AtomicInteger numOfFailedWorkflows = new AtomicInteger(0);
    private AtomicInteger numOfErroredWorkflows = new AtomicInteger(0);
    private AtomicInteger numOfTimedOutWorkflows = new AtomicInteger(0);

    /**
     * Monitored entities statistics.
     * <p>
     * A monitored entity can be: all workflow instances of a specific type, all remote invocations of a specific service.
     */
    private EnumMap<NscsMonitoredEntityTypes, NscsMonitoredEntityStats> monitoredEntities = new EnumMap<>(NscsMonitoredEntityTypes.class);

    @Inject
    private Logger logger;

    @PostConstruct
    void onServiceStart() {
        logger.info("Starting Instrumentation Service Implementation");
    }

    @PreDestroy
    void onServiceStop() {
        logger.info("Stopping Instrumentation Service Implementation");
    }

    @Override
    public long getNumOfRunningWorkflows() {
        return this.numOfRunningWorkflows.get();
    }

    @Override
    public void setNumOfRunningWorkflows(final int runningWf) {
        this.numOfRunningWorkflows.set(runningWf);
    }

    @Override
    public long getNumOfPendingWorkflows() {
        return this.numOfPendingWorkflows.get();
    }

    @Override
    public void setNumOfPendingWorkflows(final int pendingWf) {
        this.numOfPendingWorkflows.set(pendingWf);
    }

    @Override
    public long getNumOfSuccessfulWorkflows() {
        return this.numOfSuccessfulWorkflows.get();
    }

    @Override
    public long getNumOfFailedWorkflows() {
        return this.numOfFailedWorkflows.get();
    }

    @Override
    public long getNumOfErroredWorkflows() {
        return this.numOfErroredWorkflows.get();
    }

    @Override
    public long getNumOfTimedOutWorkflows() {
        return this.numOfTimedOutWorkflows.get();
    }

    @Override
    public void updateSuccessfulIscfServiceInvocations() {
        updateSuccessful(NscsMonitoredEntityTypes.ISCF, 0L);
    }

    @Override
    public void updateFailedIscfServiceInvocations() {
        updateFailed(NscsMonitoredEntityTypes.ISCF);
    }

    @Override
    public long getNumOfInstances(final NscsMonitoredEntityTypes monitoredEntityType) {
        final NscsMonitoredEntityStats stats = getMonitoredEntityStats(monitoredEntityType);
        return stats != null ? stats.getSuccessful() + stats.getFailed() + stats.getErrored() + stats.getTimedOut() : 0;
    }

    @Override
    public long getNumOfUnsuccessfulInstances(final NscsMonitoredEntityTypes monitoredEntityType) {
        final NscsMonitoredEntityStats stats = getMonitoredEntityStats(monitoredEntityType);
        return stats != null ? stats.getFailed() + stats.getErrored() + stats.getTimedOut() : 0;
    }

    @Override
    public long getNumOfSuccessfulInstances(final NscsMonitoredEntityTypes monitoredEntityType) {
        final NscsMonitoredEntityStats stats = getMonitoredEntityStats(monitoredEntityType);
        return stats != null ? stats.getSuccessful() : 0;
    }

    @Override
    public long getAverageDurationOfSuccessfulInstances(final NscsMonitoredEntityTypes monitoredEntityType) {
        final NscsMonitoredEntityStats stats = getMonitoredEntityStats(monitoredEntityType);
        final Integer successful = stats != null ? stats.getSuccessful() : 0;
        return successful != 0 ? stats.getTotalDurationOfSuccessful() / successful : 0L;
    }

    @Override
    public void updateSuccessfulWorkflow(final String workflowName, final Long duration) {
        incrementNumOfSuccessfulWorkflows();
        final NscsMonitoredEntityTypes monitoredEntityType = getMonitoredEntityTypeFromWorkflowName(workflowName);
        updateSuccessful(monitoredEntityType, duration);
    }

    @Override
    public long getNumOfFailedInstances(final NscsMonitoredEntityTypes monitoredEntityType) {
        final NscsMonitoredEntityStats stats = getMonitoredEntityStats(monitoredEntityType);
        return stats != null ? stats.getFailed() : 0;
    }

    @Override
    public void updateFailedWorkflow(final String workflowName) {
        incrementNumOfFailedWorkflows();
        final NscsMonitoredEntityTypes monitoredEntityType = getMonitoredEntityTypeFromWorkflowName(workflowName);
        updateFailed(monitoredEntityType);
    }

    @Override
    public long getNumOfErroredInstances(final NscsMonitoredEntityTypes monitoredEntityType) {
        final NscsMonitoredEntityStats stats = getMonitoredEntityStats(monitoredEntityType);
        return stats != null ? stats.getErrored() : 0;
    }

    @Override
    public void updateErroredWorkflow(final String workflowName) {
        incrementNumOfErroredWorkflows();
        final NscsMonitoredEntityTypes monitoredEntityType = getMonitoredEntityTypeFromWorkflowName(workflowName);
        updateErrored(monitoredEntityType);
    }

    @Override
    public long getNumOfTimedOutInstances(final NscsMonitoredEntityTypes monitoredEntityType) {
        final NscsMonitoredEntityStats stats = getMonitoredEntityStats(monitoredEntityType);
        return stats != null ? stats.getTimedOut() : 0;
    }

    @Override
    public void updateTimedOutWorkflow(final String workflowName) {
        incrementNumOfTimedOutWorkflows();
        final NscsMonitoredEntityTypes monitoredEntityType = getMonitoredEntityTypeFromWorkflowName(workflowName);
        updateTimedOut(monitoredEntityType);
    }

    /**
     * Increments the number of successful workflows
     */
    private void incrementNumOfSuccessfulWorkflows() {
        this.numOfSuccessfulWorkflows.incrementAndGet();
    }

    /**
     * Increments the number of failed workflows
     */
    private void incrementNumOfFailedWorkflows() {
        this.numOfFailedWorkflows.incrementAndGet();
    }

    /**
     * Increments the number of errored workflows
     */
    private void incrementNumOfErroredWorkflows() {
        this.numOfErroredWorkflows.incrementAndGet();
    }

    /**
     * Increments the number of timed-out workflows
     */
    private void incrementNumOfTimedOutWorkflows() {
        this.numOfTimedOutWorkflows.incrementAndGet();
    }

    /**
     * Updates statistics for successful monitored entity of given type and duration.
     *
     * @param monitoredEntityType the monitored entity type
     * @param duration            the duration (in millis)
     */
    private synchronized void updateSuccessful(final NscsMonitoredEntityTypes monitoredEntityType, final Long duration) {
        NscsMonitoredEntityStats stats = monitoredEntities.get(monitoredEntityType);
        if (stats != null) {
            stats.updateSuccessful(duration);
        } else {
            stats = new NscsMonitoredEntityStats(1, 0, 0, 0, duration);
        }
        monitoredEntities.put(monitoredEntityType, stats);
    }

    /**
     * Updates statistics for failed monitored entity of given type.
     *
     * @param monitoredEntityType the monitored entity type
     */
    private synchronized void updateFailed(final NscsMonitoredEntityTypes monitoredEntityType) {
        NscsMonitoredEntityStats stats = monitoredEntities.get(monitoredEntityType);
        if (stats != null) {
            stats.updateFailed();
        } else {
            stats = new NscsMonitoredEntityStats(0, 1, 0, 0, 0L);
        }
        monitoredEntities.put(monitoredEntityType, stats);
    }

    /**
     * Updates statistics for errored monitored entity of given type.
     *
     * @param monitoredEntityType the monitored entity type
     */
    private synchronized void updateErrored(final NscsMonitoredEntityTypes monitoredEntityType) {
        NscsMonitoredEntityStats stats = monitoredEntities.get(monitoredEntityType);
        if (stats != null) {
            stats.updateErrored();
        } else {
            stats = new NscsMonitoredEntityStats(0, 0, 1, 0, 0L);
        }
        monitoredEntities.put(monitoredEntityType, stats);
    }

    /**
     * Updates statistics for timed-out monitored entity of given type.
     *
     * @param monitoredEntityType the monitored entity type
     */
    private synchronized void updateTimedOut(final NscsMonitoredEntityTypes monitoredEntityType) {
        NscsMonitoredEntityStats stats = monitoredEntities.get(monitoredEntityType);
        if (stats != null) {
            stats.updateTimedOut();
        } else {
            stats = new NscsMonitoredEntityStats(0, 0, 0, 1, 0L);
        }
        monitoredEntities.put(monitoredEntityType, stats);
    }

    /**
     * Gets statistics of instances of given monitored entity type.
     *
     * @param monitoredEntityType the monitored entity type
     * @return the statistics of the instances or null if no instance of given type
     */
    private synchronized NscsMonitoredEntityStats getMonitoredEntityStats(final NscsMonitoredEntityTypes monitoredEntityType) {
        return monitoredEntities.get(monitoredEntityType);
    }

    /**
     * Converts the given workflow name to the correspondent monitored entity type.
     *
     * @param workflowName the workflow name
     * @return the monitored entity type or UNDEFINED if unknown workflow name.
     */
    private NscsMonitoredEntityTypes getMonitoredEntityTypeFromWorkflowName(final String workflowName) {
        NscsMonitoredEntityTypes monitoredEntityType = NscsMonitoredEntityTypes.UNDEFINED;
        if (workflowName != null && !workflowName.isEmpty()) {
            final WorkflowNames wfName = WorkflowNames.getWorkflowName(workflowName);
            if (wfName != null) {
                switch (wfName) {
                    case WORKFLOW_SSHKeyGeneration:
                        monitoredEntityType = NscsMonitoredEntityTypes.SSH_KEY;
                        break;
                    case WORKFLOW_CPPActivateSL2:
                        monitoredEntityType = NscsMonitoredEntityTypes.CPP_SL2_ACTIVATE;
                        break;
                    case WORKFLOW_CPPDeactivateSL2:
                        monitoredEntityType = NscsMonitoredEntityTypes.CPP_SL2_DEACTIVATE;
                        break;
                    case WORKFLOW_CPPActivateIpSec:
                        monitoredEntityType = NscsMonitoredEntityTypes.CPP_IPSEC_ACTIVATE;
                        break;
                    case WORKFLOW_CPPDeactivateIpSec:
                        monitoredEntityType = NscsMonitoredEntityTypes.CPP_IPSEC_DEACTIVATE;
                        break;
                    case WORKFLOW_CPPInstallCertificatesIpSec:
                    case WORKFLOW_CPPIssueCertificate:
                    case WORKFLOW_CPPIssueReissueCertificate_IpSec:
                        monitoredEntityType = NscsMonitoredEntityTypes.CPP_CERTIFICATE_ENROLLMENT;
                        break;
                    case WORKFLOW_COMECIM_ComIssueCert:
                        monitoredEntityType = NscsMonitoredEntityTypes.ECIM_CERTIFICATE_ENROLLMENT;
                        break;
                    case WORKFLOW_CBPOI_START_ONLINE_ENROLLMENT:
                        monitoredEntityType = NscsMonitoredEntityTypes.EOI_CERTIFICATE_ENROLLMENT;
                        break;
                    case WORKFLOW_CPPIssueTrustCert:
                    case WORKFLOW_CPPIssueTrustCertIpSec:
                        monitoredEntityType = NscsMonitoredEntityTypes.CPP_TRUST_DISTRIBUTE;
                        break;
                    case WORKFLOW_COMECIM_ComIssueTrustCert:
                        monitoredEntityType = NscsMonitoredEntityTypes.ECIM_TRUST_DISTRIBUTE;
                        break;
                    case WORKFLOW_CBPOI_INSTALL_TRUST_CERTS:
                        monitoredEntityType = NscsMonitoredEntityTypes.EOI_TRUST_DISTRIBUTE;
                        break;
                    case WORKFLOW_CPPRemoveTrustOAM:
                    case WORKFLOW_CPPRemoveTrustIPSEC:
                        monitoredEntityType = NscsMonitoredEntityTypes.CPP_TRUST_REMOVE;
                        break;
                    case WORKFLOW_COMECIMRemoveTrust:
                        monitoredEntityType = NscsMonitoredEntityTypes.ECIM_TRUST_REMOVE;
                        break;
                    case WORKFLOW_CBPOI_REMOVE_TRUST:
                        monitoredEntityType = NscsMonitoredEntityTypes.EOI_TRUST_REMOVE;
                        break;
                    case WORKFLOW_COMECIM_CONFIGURE_LDAP:
                        monitoredEntityType = NscsMonitoredEntityTypes.ECIM_LDAP_CONFIGURE;
                        break;
                    case WORKFLOW_CBPOI_CONFIGURE_LDAP:
                        monitoredEntityType = NscsMonitoredEntityTypes.EOI_LDAP_CONFIGURE;
                        break;
                    case WORKFLOW_CPP_ENABLE_OR_DISABLE_CRLCHECK:
                        monitoredEntityType = NscsMonitoredEntityTypes.CPP_CRLCHECK;
                        break;
                    case WORKFLOW_COMECIM_ENABLE_OR_DISABLE_CRLCHECK:
                        monitoredEntityType = NscsMonitoredEntityTypes.ECIM_CRLCHECK;
                        break;
                    case WORKFLOW_CPP_ON_DEMAND_DOWNLOAD_CRL:
                        monitoredEntityType = NscsMonitoredEntityTypes.CPP_ON_DEMAND_CRL_DOWNLOAD;
                        break;
                    case WORKFLOW_COMECIM_ON_DEMAND_DOWNLOAD_CRL:
                        monitoredEntityType = NscsMonitoredEntityTypes.ECIM_ON_DEMAND_CRL_DOWNLOAD;
                        break;
                    case WORKFLOW_SET_CIPHERS:
                        monitoredEntityType = NscsMonitoredEntityTypes.SET_CIPHERS;
                        break;
                    case WORKFLOW_CPPACTIVATERTSEL:
                        monitoredEntityType = NscsMonitoredEntityTypes.CPP_RTSEL_ACTIVATE;
                        break;
                    case WORKFLOW_CPPDEACTIVATERTSEL:
                        monitoredEntityType = NscsMonitoredEntityTypes.CPP_RTSEL_DEACTIVATE;
                        break;
                    case WORKFLOW_CPP_RTSEL_DELETE_SERVER:
                        monitoredEntityType = NscsMonitoredEntityTypes.CPP_RTSEL_DELETE;
                        break;
                    case WORKFLOW_CPP_ACTIVATE_HTTPS:
                        monitoredEntityType = NscsMonitoredEntityTypes.CPP_HTTPS_ACTIVATE;
                        break;
                    case WORKFLOW_CPP_DEACTIVATE_HTTPS:
                        monitoredEntityType = NscsMonitoredEntityTypes.CPP_HTTPS_DEACTIVATE;
                        break;
                    case WORKFLOW_CPP_GET_HTTPS:
                        monitoredEntityType = NscsMonitoredEntityTypes.CPP_HTTPS_GET;
                        break;
                    case WORKFLOW_COM_ACTIVATE_FTPES:
                        monitoredEntityType = NscsMonitoredEntityTypes.ECIM_FTPES_ACTIVATE;
                        break;
                    case WORKFLOW_COM_DEACTIVATE_FTPES:
                        monitoredEntityType = NscsMonitoredEntityTypes.ECIM_FTPES_DEACTIVATE;
                        break;
                    case WORKFLOW_CPP_LAAD_FILES_DISTRIBUTION:
                        monitoredEntityType = NscsMonitoredEntityTypes.CPP_LAAD_DISTRIBUTE;
                        break;
                    case WORKFLOW_CPP_NTP_CONFIGURE:
                        monitoredEntityType = NscsMonitoredEntityTypes.CPP_NTP_CONFIGURE;
                        break;
                    case WORKFLOW_CPP_NTP_REMOVE:
                        monitoredEntityType = NscsMonitoredEntityTypes.CPP_NTP_REMOVE;
                        break;
                    case WORKFLOW_COMECIM_NTP_REMOVE:
                        monitoredEntityType = NscsMonitoredEntityTypes.COMECIM_NTP_REMOVE;
                        break;
                    default:
                        logger.error("Unknown workflow name [{}]", workflowName);
                        break;
                }
            }
        }
        return monitoredEntityType;
    }

}
