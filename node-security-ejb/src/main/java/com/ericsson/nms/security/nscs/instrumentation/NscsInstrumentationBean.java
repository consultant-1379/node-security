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
package com.ericsson.nms.security.nscs.instrumentation;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.instrumentation.NscsInstrumentationService;
import com.ericsson.nms.security.nscs.api.instrumentation.NscsMonitoredEntityTypes;
import com.ericsson.oss.itpf.sdk.instrument.annotation.InstrumentedBean;
import com.ericsson.oss.itpf.sdk.instrument.annotation.MonitoredAttribute;
import com.ericsson.oss.itpf.sdk.instrument.annotation.MonitoredAttribute.Category;
import com.ericsson.oss.itpf.sdk.instrument.annotation.MonitoredAttribute.CollectionType;
import com.ericsson.oss.itpf.sdk.instrument.annotation.MonitoredAttribute.Interval;
import com.ericsson.oss.itpf.sdk.instrument.annotation.MonitoredAttribute.Units;
import com.ericsson.oss.itpf.sdk.instrument.annotation.MonitoredAttribute.Visibility;

/**
 *
 * @author elucbot
 *
 */
@ApplicationScoped
@InstrumentedBean(description = "Records metrics as part of NSCS service", displayName = "NSCS Instrumentation Bean for Metrics")
public class NscsInstrumentationBean {

    @Inject
    private NscsInstrumentationService nscsInstrumentationService;

    @Inject
    private Logger logger;

    @PostConstruct
    void onServiceStart() {
        logger.info("Starting Instrumentation Bean service");
    }

    @PreDestroy
    void onServiceStop() {
        logger.info("Stopping Instrumentation Bean service");
    }

    /**
     * The number of failed workflows in NSCS. TO_BE_REWORKED currently returns the number of unsuccessful workflows
     *
     * @return int
     */
    @MonitoredAttribute(displayName = "The number of failed Workflows in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedWorkflows() {
        return nscsInstrumentationService.getNumOfFailedWorkflows() + nscsInstrumentationService.getNumOfErroredWorkflows()
                + nscsInstrumentationService.getNumOfTimedOutWorkflows();
    }

    @MonitoredAttribute(displayName = "The number of running Workflows in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getNumOfRunningWorkflows() {
        return nscsInstrumentationService.getNumOfRunningWorkflows();
    }

    @MonitoredAttribute(displayName = "The number of pending Workflows in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getNumOfPendingWorkflows() {
        return nscsInstrumentationService.getNumOfPendingWorkflows();
    }

    @MonitoredAttribute(displayName = "The number of successful workflows in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulWorkflows();
    }

    /**
     * TODO: Is it necessary? The number of NSCS Workflows removed from this map
     *
     * @return int
     */
    @MonitoredAttribute(displayName = "The number of removed Workflows in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.UTILIZATION, interval = Interval.FIVE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfRemovedWorkflows() {
        return 0;
    }

    /**
     * The number of successful distributed Trusted Certificates TO_BE_REMOVED
     *
     * @return int
     */
    @MonitoredAttribute(displayName = "The number of successful distributed Trusted Certificates in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulDistributedTrustedCertificates() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_TRUST_DISTRIBUTE)
                + nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_TRUST_DISTRIBUTE)
                + nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.EOI_TRUST_DISTRIBUTE);
    }

    /**
     * The number of failed distributed Trusted Certificates TO_BE_REMOVED
     *
     * @return int
     */
    @MonitoredAttribute(displayName = "The number of failed Trusted Certificates in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedDistributedTrustedCertificates() {
        return nscsInstrumentationService.getNumOfUnsuccessfulInstances(NscsMonitoredEntityTypes.CPP_TRUST_DISTRIBUTE)
                + nscsInstrumentationService.getNumOfUnsuccessfulInstances(NscsMonitoredEntityTypes.ECIM_TRUST_DISTRIBUTE)
                + nscsInstrumentationService.getNumOfUnsuccessfulInstances(NscsMonitoredEntityTypes.EOI_TRUST_DISTRIBUTE);
    }

    /**
     * The number of successful generated SSH keys in NSCS. TO_BE_REMOVED
     *
     * @return int
     */
    @MonitoredAttribute(displayName = "The number of successful generated SSH keys in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulGeneratedSSHKeys() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.SSH_KEY);
    }

    /**
     * The number of failed generated SSH keys in NSCS. TO_BE_REMOVED
     *
     * @return int
     */
    @MonitoredAttribute(displayName = "The number of failed generated SSH keys in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedGeneratedSSHKeys() {
        return nscsInstrumentationService.getNumOfUnsuccessfulInstances(NscsMonitoredEntityTypes.SSH_KEY);
    }

    /**
     * The number of successful enrolled node certificates in NSCS. TO_BE_REMOVED
     *
     * @return int
     */
    @MonitoredAttribute(displayName = "The number of successful enrolled node certificates in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulEnrolledCertificates() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_CERTIFICATE_ENROLLMENT)
                + nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_CERTIFICATE_ENROLLMENT)
                + nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.EOI_CERTIFICATE_ENROLLMENT);
    }

    /**
     * The number of failed enrolled node certificates in NSCS. TO_BE_REMOVED
     *
     * @return int
     */
    @MonitoredAttribute(displayName = "The number of failed enrolled node certificates in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedEnrolledCertificates() {
        return nscsInstrumentationService.getNumOfUnsuccessfulInstances(NscsMonitoredEntityTypes.CPP_CERTIFICATE_ENROLLMENT)
                + nscsInstrumentationService.getNumOfUnsuccessfulInstances(NscsMonitoredEntityTypes.ECIM_CERTIFICATE_ENROLLMENT)
                + nscsInstrumentationService.getNumOfUnsuccessfulInstances(NscsMonitoredEntityTypes.EOI_CERTIFICATE_ENROLLMENT);
    }

    /**
     * The number of successful generated ISCF files in NSCS. TO_BE_REMOVED
     *
     * @return int
     */
    @MonitoredAttribute(displayName = "The number of successful generated ISCF file in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccesfulGeneratedISCFFiles() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.ISCF);
    }

    /**
     * The number of failed generated ISCF files in NSCS. TO_BE_REMOVED
     *
     * @return int
     */
    @MonitoredAttribute(displayName = "The number of failed generated ISCF file in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.UTILIZATION, interval = Interval.FIVE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedGeneratedISCFFiles() {
        return nscsInstrumentationService.getNumOfUnsuccessfulInstances(NscsMonitoredEntityTypes.ISCF);
    }

    /**
     * Get the number of failed SL2 Init Cert Enrollment calls in NSCS TO_BE_REMOVED
     *
     * @return the sl2InitCertEnrollmentFailures
     */
    @MonitoredAttribute(category = Category.PERFORMANCE, collectionType = CollectionType.TRENDSUP, interval = Interval.FIVE_MIN, units = Units.NONE, visibility = Visibility.INTERNAL, displayName = "Number of failed SL2 Init Cert Enrollment calls in NSCS")
    public long getSL2InitCertEnrollmentFailures() {
        return 0;
    }

    /**
     * Get the number of failed SL2 Install Trusted Certificates calls in NSCS TO_BE_REMOVED
     *
     * @return the sl2InstallTrustedCertificateFailures
     */
    @MonitoredAttribute(category = Category.PERFORMANCE, collectionType = CollectionType.TRENDSUP, interval = Interval.FIVE_MIN, units = Units.NONE, visibility = Visibility.INTERNAL, displayName = "Number of failed SL2 Install Trusted Certificates calls in NSCS")
    public long getSL2InstallTrustedCertificateFailures() {
        return 0;
    }

    /**
     * Get the number of failed SL2 Activate Corba Security calls in NSCS TO_BE_REMOVED
     *
     * @return the activateSL2Failures
     */
    @MonitoredAttribute(category = Category.PERFORMANCE, collectionType = CollectionType.TRENDSUP, interval = Interval.FIVE_MIN, units = Units.NONE, visibility = Visibility.INTERNAL, displayName = "Number of failed SL2 Activate Corba Security calls in NSCS")
    public long getActivateSL2Failures() {
        return nscsInstrumentationService.getNumOfUnsuccessfulInstances(NscsMonitoredEntityTypes.CPP_SL2_ACTIVATE);
    }

    /**
     * Get the number of total SL2 activation invocations in NSCS TO_BE_REMOVED
     *
     * @return the activateSL2Invocations
     */
    @MonitoredAttribute(category = Category.PERFORMANCE, collectionType = CollectionType.TRENDSUP, interval = Interval.FIVE_MIN, units = Units.NONE, visibility = Visibility.INTERNAL, displayName = "Number of total SL2 activation invocations in NSCS")
    public long getActivateSL2Invocations() {
        return nscsInstrumentationService.getNumOfInstances(NscsMonitoredEntityTypes.CPP_SL2_ACTIVATE);
    }

    /**
     * Get the number of total SL2 de-activation failures in NSCS TO_BE_REMOVED
     *
     * @return the deActivateSL2Failures
     */
    @MonitoredAttribute(category = Category.PERFORMANCE, collectionType = CollectionType.TRENDSUP, interval = Interval.FIVE_MIN, units = Units.NONE, visibility = Visibility.INTERNAL, displayName = "Number of total SL2 de-activation failures in NSCS")
    public long getDeActivateSL2Failures() {
        return nscsInstrumentationService.getNumOfUnsuccessfulInstances(NscsMonitoredEntityTypes.CPP_SL2_DEACTIVATE);
    }

    /**
     * Get the number of total SL2 de-activation invocations in NSCS TO_BE_REMOVED
     *
     * @return the deActivateSL2Invocations
     */
    @MonitoredAttribute(category = Category.PERFORMANCE, collectionType = CollectionType.TRENDSUP, interval = Interval.FIVE_MIN, units = Units.NONE, visibility = Visibility.INTERNAL, displayName = "Number of total SL2 de-activation invocations in NSCS")
    public long getDeActivateSL2Invocations() {
        return nscsInstrumentationService.getNumOfInstances(NscsMonitoredEntityTypes.CPP_SL2_DEACTIVATE);
    }

    /**
     * Get the number of failed IPSEC Init Cert Enrollment calls in NSCS TO_BE_REMOVED
     *
     * @return the ipsecInitCertEnrollmentFailures
     */
    @MonitoredAttribute(category = Category.PERFORMANCE, collectionType = CollectionType.TRENDSUP, interval = Interval.FIVE_MIN, units = Units.NONE, visibility = Visibility.INTERNAL, displayName = "Number of failed IPSEC Init Cert Enrollment calls in NSCS")
    public long getIpsecInitCertEnrollmentFailures() {
        return 0;
    }

    /**
     * Get the number of failed IPSEC Install Trusted Certificates calls in NSCS TO_BE_REMOVED
     *
     * @return the ipsecInstallTrustedCertificateFailures
     */
    @MonitoredAttribute(category = Category.PERFORMANCE, collectionType = CollectionType.TRENDSUP, interval = Interval.FIVE_MIN, units = Units.NONE, visibility = Visibility.INTERNAL, displayName = "Number of failed IPSEC Install Trusted Certificates calls in NSCS")
    public long getIpsecInstallTrustedCertificateFailures() {
        return 0;
    }

    /**
     * Get the number of failed IPSEC Activate Corba Security calls in NSCS TO_BE_REMOVED
     *
     * @return the activateIpsecFailures
     */
    @MonitoredAttribute(category = Category.PERFORMANCE, collectionType = CollectionType.TRENDSUP, interval = Interval.FIVE_MIN, units = Units.NONE, visibility = Visibility.INTERNAL, displayName = "Number of failed IPSEC Activate Corba Security calls in NSCS")
    public long getActivateIpsecFailures() {
        return nscsInstrumentationService.getNumOfUnsuccessfulInstances(NscsMonitoredEntityTypes.CPP_IPSEC_ACTIVATE);
    }

    /**
     * Get the total number of IPSEC activation calls in NSCS TO_BE_REMOVED
     *
     * @return the activateIpsecInvocations
     */
    @MonitoredAttribute(category = Category.PERFORMANCE, collectionType = CollectionType.TRENDSUP, interval = Interval.FIVE_MIN, units = Units.NONE, visibility = Visibility.INTERNAL, displayName = "Number of total IPSEC activation calls in NSCS")
    public long getActivateIpsecInvocations() {
        return nscsInstrumentationService.getNumOfInstances(NscsMonitoredEntityTypes.CPP_IPSEC_ACTIVATE);
    }

    /**
     * Get the number of failed IPSEC de-activation calls in NSCS TO_BE_REMOVED
     *
     * @return the deActivateIpsecFailures
     */
    @MonitoredAttribute(category = Category.PERFORMANCE, collectionType = CollectionType.TRENDSUP, interval = Interval.FIVE_MIN, units = Units.NONE, visibility = Visibility.INTERNAL, displayName = "Number of failed IPSEC de-activation calls in NSCS")
    public long getDeActivateIpsecFailures() {
        return nscsInstrumentationService.getNumOfUnsuccessfulInstances(NscsMonitoredEntityTypes.CPP_IPSEC_DEACTIVATE);
    }

    /**
     * Get the total number of IPSEC de-activation calls in NSCS TO_BE_REMOVED
     *
     * @return the deActivateIpsecInvocations
     */
    @MonitoredAttribute(category = Category.PERFORMANCE, collectionType = CollectionType.TRENDSUP, interval = Interval.FIVE_MIN, units = Units.NONE, visibility = Visibility.INTERNAL, displayName = "Number of total IPSEC de-activation calls in NSCS")
    public long getDeActivateIpsecInvocations() {
        return nscsInstrumentationService.getNumOfInstances(NscsMonitoredEntityTypes.CPP_IPSEC_DEACTIVATE);
    }

    // ##########################

    @MonitoredAttribute(displayName = "The number of errored workflows in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredWorkflows() {
        return nscsInstrumentationService.getNumOfErroredWorkflows();
    }

    @MonitoredAttribute(displayName = "The number of timed-out workflows in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutWorkflows();
    }

    @MonitoredAttribute(displayName = "The number of successful ISCF service invocations in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulIscfInvocations() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.ISCF);
    }

    @MonitoredAttribute(displayName = "The number of failed ISCF service invocations in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedIscfInvocations() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.ISCF);
    }

    @MonitoredAttribute(displayName = "The number of successful SSH keys generation workflows in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulSSHKeyGenerationWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.SSH_KEY);
    }

    @MonitoredAttribute(displayName = "The average duration of successful SSH keys generation workflows in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulSSHKeyGenerationWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.SSH_KEY);
    }

    @MonitoredAttribute(displayName = "The number of failed SSH keys generation workflows in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedSSHKeyGenerationWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.SSH_KEY);
    }

    @MonitoredAttribute(displayName = "The number of errored SSH keys generation workflows in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredSSHKeyGenerationWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.SSH_KEY);
    }

    @MonitoredAttribute(displayName = "The number of timed-out SSH keys generation workflows in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutSSHKeyGenerationWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.SSH_KEY);
    }

    @MonitoredAttribute(displayName = "The number of successful SL2 activate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulCppSL2ActivateWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_SL2_ACTIVATE);
    }

    /**
     * The average duration of successful SL2 activate workflows for CPP nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful SL2 activate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulCppSL2ActivateWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_SL2_ACTIVATE);
    }

    /**
     * The number of failed SL2 activate workflows for CPP nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed SL2 activate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedCppSL2ActivateWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_SL2_ACTIVATE);
    }

    /**
     * The number of errored SL2 activate workflows for CPP nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored SL2 activate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredCppSL2ActivateWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_SL2_ACTIVATE);
    }

    /**
     * The number of timed-out SL2 activate workflows for CPP nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out SL2 activate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutCppSL2ActivateWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_SL2_ACTIVATE);
    }

    /**
     * The number of successful SL2 deactivate workflows for CPP nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful SL2 deactivate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulCppSL2DeactivateWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_SL2_DEACTIVATE);
    }

    /**
     * The average duration of successful SL2 deactivate workflows for CPP nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful SL2 deactivate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulCppSL2DeactivateWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_SL2_DEACTIVATE);
    }

    /**
     * The number of failed SL2 deactivate workflows for CPP nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed SL2 deactivate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedCppSL2DeactivateWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_SL2_DEACTIVATE);
    }

    /**
     * The number of errored SL2 deactivate workflows for CPP nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored SL2 deactivate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredCppSL2DeactivateWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_SL2_DEACTIVATE);
    }

    /**
     * The number of timed-out SL2 deactivate workflows for CPP nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out SL2 deactivate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutCppSL2DeactivateWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_SL2_DEACTIVATE);
    }

    /**
     * The number of successful IpSec activate workflows for CPP nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful IpSec activate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulCppIpSecActivateWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_IPSEC_ACTIVATE);
    }

    /**
     * The average duration of successful IpSec activate workflows for CPP nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful IpSec activate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulCppIpSecActivateWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_IPSEC_ACTIVATE);
    }

    /**
     * The number of failed IpSec activate workflows for CPP nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed IpSec activate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedCppIpSecActivateWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_IPSEC_ACTIVATE);
    }

    /**
     * The number of errored IpSec activate workflows for CPP nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored IpSec activate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredCppIpSecActivateWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_IPSEC_ACTIVATE);
    }

    /**
     * The number of timed-out IpSec activate workflows for CPP nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out IpSec activate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutCppIpSecActivateWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_IPSEC_ACTIVATE);
    }

    /**
     * The number of successful IpSec deactivate workflows for CPP nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful IpSec deactivate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulCppIpSecDeactivateWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_IPSEC_DEACTIVATE);
    }

    /**
     * The average duration of successful IpSec deactivate workflows for CPP nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful IpSec deactivate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulCppIpSecDeactivateWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_IPSEC_DEACTIVATE);
    }

    /**
     * The number of failed IpSec deactivate workflows for CPP nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed IpSec deactivate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedCppIpSecDeactivateWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_IPSEC_DEACTIVATE);
    }

    /**
     * The number of errored IpSec deactivate workflows for CPP nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored IpSec deactivate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredCppIpSecDeactivateWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_IPSEC_DEACTIVATE);
    }

    /**
     * The number of timed-out IpSec deactivate workflows for CPP nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out IpSec deactivate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutCppIpSecDeactivateWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_IPSEC_DEACTIVATE);
    }

    /**
     * The number of successful certificate enrollment workflows for CPP nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful certificate enrollment workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulCppCertificateEnrollmentWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_CERTIFICATE_ENROLLMENT);
    }

    /**
     * The average duration of successful certificate enrollment workflows for CPP nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful certificate enrollment workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulCppCertificateEnrollmentWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_CERTIFICATE_ENROLLMENT);
    }

    /**
     * The number of failed certificate enrollment workflows for CPP nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed certificate enrollment workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedCppCertificateEnrollmentWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_CERTIFICATE_ENROLLMENT);
    }

    /**
     * The number of errored certificate enrollment workflows for CPP nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored certificate enrollment workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredCppCertificateEnrollmentWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_CERTIFICATE_ENROLLMENT);
    }

    /**
     * The number of timed-out certificate enrollment workflows for CPP nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out certificate enrollment workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutCppCertificateEnrollmentWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_CERTIFICATE_ENROLLMENT);
    }

    /**
     * The number of successful certificate enrollment workflows for ECIM nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful certificate enrollment workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulEcimCertificateEnrollmentWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_CERTIFICATE_ENROLLMENT);
    }

    /**
     * The average duration of successful certificate enrollment workflows for ECIM nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful certificate enrollment workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulEcimCertificateEnrollmentWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_CERTIFICATE_ENROLLMENT);
    }

    /**
     * The number of failed certificate enrollment workflows for ECIM nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed certificate enrollment workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedEcimCertificateEnrollmentWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.ECIM_CERTIFICATE_ENROLLMENT);
    }

    /**
     * The number of errored certificate enrollment workflows for ECIM nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored certificate enrollment workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredEcimCertificateEnrollmentWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.ECIM_CERTIFICATE_ENROLLMENT);
    }

    /**
     * The number of timed-out certificate enrollment workflows for ECIM nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out certificate enrollment workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutEcimCertificateEnrollmentWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.ECIM_CERTIFICATE_ENROLLMENT);
    }

    /**
     * The number of successful certificate enrollment workflows for EOI nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful certificate enrollment workflows for EOI nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulEoiCertificateEnrollmentWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.EOI_CERTIFICATE_ENROLLMENT);
    }

    /**
     * The average duration of successful certificate enrollment workflows for EOI nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful certificate enrollment workflows for EOI nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulEoiCertificateEnrollmentWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.EOI_CERTIFICATE_ENROLLMENT);
    }

    /**
     * The number of failed certificate enrollment workflows for EOI nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed certificate enrollment workflows for EOI nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedEoiCertificateEnrollmentWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.EOI_CERTIFICATE_ENROLLMENT);
    }

    /**
     * The number of errored certificate enrollment workflows for EOI nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored certificate enrollment workflows for EOI nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredEoiCertificateEnrollmentWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.EOI_CERTIFICATE_ENROLLMENT);
    }

    /**
     * The number of timed-out certificate enrollment workflows for EOI nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out certificate enrollment workflows for EOI nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutEoiCertificateEnrollmentWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.EOI_CERTIFICATE_ENROLLMENT);
    }

    /**
     * The number of successful trust distribute workflows for CPP nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful trust distribute workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulCppTrustDistributeWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_TRUST_DISTRIBUTE);
    }

    /**
     * The average duration of successful trust distribute workflows for CPP nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful trust distribute workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulCppTrustDistributeWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_TRUST_DISTRIBUTE);
    }

    /**
     * The number of failed trust distribute workflows for CPP nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed trust distribute workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedCppTrustDistributeWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_TRUST_DISTRIBUTE);
    }

    /**
     * The number of errored trust distribute workflows for CPP nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored trust distribute workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredCppTrustDistributeWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_TRUST_DISTRIBUTE);
    }

    /**
     * The number of timed-out trust distribute workflows for CPP nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out trust distribute workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutCppTrustDistributeWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_TRUST_DISTRIBUTE);
    }

    /**
     * The number of successful trust distribute workflows for ECIM nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful trust distribute workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulEcimTrustDistributeWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_TRUST_DISTRIBUTE);
    }

    /**
     * The average duration of successful trust distribute workflows for ECIM nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful trust distribute workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulEcimTrustDistributeWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_TRUST_DISTRIBUTE);
    }

    /**
     * The number of failed trust distribute workflows for ECIM nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed trust distribute workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedEcimTrustDistributeWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.ECIM_TRUST_DISTRIBUTE);
    }

    /**
     * The number of errored trust distribute workflows for ECIM nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored trust distribute workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredEcimTrustDistributeWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.ECIM_TRUST_DISTRIBUTE);
    }

    /**
     * The number of timed-out trust distribute workflows for ECIM nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out trust distribute workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutEcimTrustDistributeWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.ECIM_TRUST_DISTRIBUTE);
    }

    /**
     * The number of successful trust distribute workflows for EOI nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful trust distribute workflows for EOI nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulEoiTrustDistributeWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.EOI_TRUST_DISTRIBUTE);
    }

    /**
     * The average duration of successful trust distribute workflows for EOI nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful trust distribute workflows for EOI nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulEoiTrustDistributeWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.EOI_TRUST_DISTRIBUTE);
    }

    /**
     * The number of failed trust distribute workflows for EOI nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed trust distribute workflows for EOI nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedEoiTrustDistributeWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.EOI_TRUST_DISTRIBUTE);
    }

    /**
     * The number of errored trust distribute workflows for EOI nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored trust distribute workflows for EOI nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredEoiTrustDistributeWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.EOI_TRUST_DISTRIBUTE);
    }

    /**
     * The number of timed-out trust distribute workflows for EOI nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out trust distribute workflows for EOI nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutEoiTrustDistributeWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.EOI_TRUST_DISTRIBUTE);
    }

    /**
     * The number of successful trust remove workflows for CPP nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful trust remove workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulCppTrustRemoveWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_TRUST_REMOVE);
    }

    /**
     * The average duration of successful trust remove workflows for CPP nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful trust remove workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulCppTrustRemoveWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_TRUST_REMOVE);
    }

    /**
     * The number of failed trust remove workflows for CPP nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed trust remove workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedCppTrustRemoveWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_TRUST_REMOVE);
    }

    /**
     * The number of errored trust remove workflows for CPP nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored trust remove workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredCppTrustRemoveWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_TRUST_REMOVE);
    }

    /**
     * The number of timed-out trust remove workflows for CPP nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out trust remove workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutCppTrustRemoveWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_TRUST_REMOVE);
    }

    /**
     * The number of successful trust remove workflows for ECIM nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful trust remove workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulEcimTrustRemoveWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_TRUST_REMOVE);
    }

    /**
     * The average duration of successful trust remove workflows for ECIM nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful trust remove workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulEcimTrustRemoveWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_TRUST_REMOVE);
    }

    /**
     * The number of failed trust remove workflows for ECIM nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed trust remove workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedEcimTrustRemoveWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.ECIM_TRUST_REMOVE);
    }

    /**
     * The number of errored trust remove workflows for ECIM nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored trust remove workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredEcimTrustRemoveWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.ECIM_TRUST_REMOVE);
    }

    /**
     * The number of timed-out trust remove workflows for ECIM nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out trust remove workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutEcimTrustRemoveWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.ECIM_TRUST_REMOVE);
    }

    /**
     * The number of successful trust remove workflows for EOI nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful trust remove workflows for EOI nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulEoiTrustRemoveWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.EOI_TRUST_REMOVE);
    }

    /**
     * The average duration of successful trust remove workflows for EOI nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful trust remove workflows for EOI nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulEoiTrustRemoveWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.EOI_TRUST_REMOVE);
    }

    /**
     * The number of failed trust remove workflows for EOI nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed trust remove workflows for EOI nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedEoiTrustRemoveWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.EOI_TRUST_REMOVE);
    }

    /**
     * The number of errored trust remove workflows for EOI nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored trust remove workflows for EOI nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredEoiTrustRemoveWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.EOI_TRUST_REMOVE);
    }

    /**
     * The number of timed-out trust remove workflows for EOI nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out trust remove workflows for EOI nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutEoiTrustRemoveWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.EOI_TRUST_REMOVE);
    }

    /**
     * The number of successful LDAP configure workflows for ECIM nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful LDAP configure workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulEcimLdapConfigureWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_LDAP_CONFIGURE);
    }

    /**
     * The average duration of successful LDAP configure workflows for ECIM nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful LDAP configure workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulEcimLdapConfigureWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_LDAP_CONFIGURE);
    }

    /**
     * The number of failed LDAP configure workflows for ECIM nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed LDAP configure workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedEcimLdapConfigureWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.ECIM_LDAP_CONFIGURE);
    }

    /**
     * The number of errored LDAP configure workflows for ECIM nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored LDAP configure workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredEcimLdapConfigureWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.ECIM_LDAP_CONFIGURE);
    }

    /**
     * The number of timed-out LDAP configure workflows for ECIM nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out LDAP configure workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutEcimLdapConfigureWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.ECIM_LDAP_CONFIGURE);
    }

    /**
     * The number of successful LDAP configure workflows for EOI nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful LDAP configure workflows for EOI nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulEoiLdapConfigureWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.EOI_LDAP_CONFIGURE);
    }

    /**
     * The average duration of successful LDAP configure workflows for EOI nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful LDAP configure workflows for EOI nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulEoiLdapConfigureWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.EOI_LDAP_CONFIGURE);
    }

    /**
     * The number of failed LDAP configure workflows for EOI nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed LDAP configure workflows for EOI nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedEoiLdapConfigureWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.EOI_LDAP_CONFIGURE);
    }

    /**
     * The number of errored LDAP configure workflows for EOI nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored LDAP configure workflows for EOI nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredEoiLdapConfigureWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.EOI_LDAP_CONFIGURE);
    }

    /**
     * The number of timed-out LDAP configure workflows for EOI nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out LDAP configure workflows for EOI nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutEoiLdapConfigureWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.EOI_LDAP_CONFIGURE);
    }

    /**
     * The number of successful CRL check workflows for CPP nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful CRL check workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulCppCRLCheckWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_CRLCHECK);
    }

    /**
     * The average duration of successful CRL check workflows for CPP nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful CRL check workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulCppCRLCheckWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_CRLCHECK);
    }

    /**
     * The number of failed CRL check workflows for CPP nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed CRL check workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedCppCRLCheckWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_CRLCHECK);
    }

    /**
     * The number of errored CRL check workflows for CPP nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored CRL check workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredCppCRLCheckWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_CRLCHECK);
    }

    /**
     * The number of timed-out CRL check workflows for CPP nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out CRL check workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutCppCRLCheckWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_CRLCHECK);
    }

    /**
     * The number of successful CRL check workflows for ECIM nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful CRL check workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulEcimCRLCheckWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_CRLCHECK);
    }

    /**
     * The average duration of successful CRL check workflows for ECIM nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful CRL check workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulEcimCRLCheckWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_CRLCHECK);
    }

    /**
     * The number of failed CRL check workflows for ECIM nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed CRL check workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedEcimCRLCheckWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.ECIM_CRLCHECK);
    }

    /**
     * The number of errored CRL check workflows for ECIM nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored CRL check workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredEcimCRLCheckWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.ECIM_CRLCHECK);
    }

    /**
     * The number of timed-out CRL check workflows for ECIM nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out CRL check workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutEcimCRLCheckWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.ECIM_CRLCHECK);
    }

    /**
     * The number of successful on-demand CRL download workflows for CPP nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful on-demand CRL download workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulCppOnDemandCRLDownloadWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_ON_DEMAND_CRL_DOWNLOAD);
    }

    /**
     * The average duration of successful on-demand CRL download workflows for CPP nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful on-demand CRL download workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulCppOnDemandCRLDownloadWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_ON_DEMAND_CRL_DOWNLOAD);
    }

    /**
     * The number of failed on-demand CRL download workflows for CPP nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed on-demand CRL download workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedCppOnDemandCRLDownloadWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_ON_DEMAND_CRL_DOWNLOAD);
    }

    /**
     * The number of errored on-demand CRL download workflows for CPP nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored on-demand CRL download workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredCppOnDemandCRLDownloadWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_ON_DEMAND_CRL_DOWNLOAD);
    }

    /**
     * The number of timed-out on-demand CRL download workflows for CPP nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out on-demand CRL download workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutCppOnDemandCRLDownloadWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_ON_DEMAND_CRL_DOWNLOAD);
    }

    /**
     * The number of successful on-demand CRL download workflows for ECIM nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful on-demand CRL download workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulEcimOnDemandCRLDownloadWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_ON_DEMAND_CRL_DOWNLOAD);
    }

    /**
     * The average duration of successful on-demand CRL download workflows for ECIM nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful on-demand CRL download workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulEcimOnDemandCRLDownloadWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_ON_DEMAND_CRL_DOWNLOAD);
    }

    /**
     * The number of failed on-demand CRL download workflows for ECIM nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed on-demand CRL download workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedEcimOnDemandCRLDownloadWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.ECIM_ON_DEMAND_CRL_DOWNLOAD);
    }

    /**
     * The number of errored on-demand CRL download workflows for ECIM nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored on-demand CRL download workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredEcimOnDemandCRLDownloadWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.ECIM_ON_DEMAND_CRL_DOWNLOAD);
    }

    /**
     * The number of timed-out on-demand CRL download workflows for ECIM nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out on-demand CRL download workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutEcimOnDemandCRLDownloadWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.ECIM_ON_DEMAND_CRL_DOWNLOAD);
    }

    /**
     * The number of successful set ciphers workflows in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful set ciphers workflows in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulSetCiphersWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.SET_CIPHERS);
    }

    /**
     * The average duration of successful set ciphers workflows in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful set ciphers workflows in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulSetCiphersWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.SET_CIPHERS);
    }

    /**
     * The number of failed set ciphers workflows in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed set ciphers workflows in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedSetCiphersWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.SET_CIPHERS);
    }

    /**
     * The number of errored set ciphers workflows in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored set ciphers workflows in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredSetCiphersWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.SET_CIPHERS);
    }

    /**
     * The number of timed-out set ciphers workflows in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out set ciphers workflows in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutSetCiphersWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.SET_CIPHERS);
    }

    /**
     * The number of successful RTSEL activate workflows for CPP nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful RTSEL activate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulCppRTSELActivateWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_RTSEL_ACTIVATE);
    }

    /**
     * The average duration of successful RTSEL activate workflows for CPP nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful RTSEL activate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulCppRTSELActivateWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_RTSEL_ACTIVATE);
    }

    /**
     * The number of failed RTSEL activate workflows for CPP nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed RTSEL activate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedCppRTSELActivateWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_RTSEL_ACTIVATE);
    }

    /**
     * The number of errored RTSEL activate workflows for CPP nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored RTSEL activate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredCppRTSELActivateWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_RTSEL_ACTIVATE);
    }

    /**
     * The number of timed-out RTSEL activate workflows for CPP nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out RTSEL activate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutCppRTSELActivateWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_RTSEL_ACTIVATE);
    }

    /**
     * The number of successful RTSEL deactivate workflows for CPP nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful RTSEL deactivate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulCppRTSELDeactivateWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_RTSEL_DEACTIVATE);
    }

    /**
     * The average duration of successful RTSEL deactivate workflows for CPP nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful RTSEL deactivate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulCppRTSELDeactivateWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_RTSEL_DEACTIVATE);
    }

    /**
     * The number of failed RTSEL deactivate workflows for CPP nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed RTSEL deactivate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedCppRTSELDeactivateWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_RTSEL_DEACTIVATE);
    }

    /**
     * The number of errored RTSEL deactivate workflows for CPP nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored RTSEL deactivate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredCppRTSELDeactivateWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_RTSEL_DEACTIVATE);
    }

    /**
     * The number of timed-out RTSEL deactivate workflows for CPP nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out RTSEL deactivate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutCppRTSELDeactivateWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_RTSEL_DEACTIVATE);
    }

    /**
     * The number of successful RTSEL delete workflows for CPP nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful RTSEL delete workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulCppRTSELDeleteWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_RTSEL_DELETE);
    }

    /**
     * The average duration of successful RTSEL delete workflows for CPP nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful RTSEL delete workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulCppRTSELDeleteWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_RTSEL_DELETE);
    }

    /**
     * The number of failed RTSEL delete workflows for CPP nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed RTSEL delete workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedCppRTSELDeleteWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_RTSEL_DELETE);
    }

    /**
     * The number of errored RTSEL delete workflows for CPP nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored RTSEL delete workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredCppRTSELDeleteWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_RTSEL_DELETE);
    }

    /**
     * The number of timed-out RTSEL delete workflows for CPP nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out RTSEL delete workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutCppRTSELDeleteWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_RTSEL_DELETE);
    }

    /**
     * The number of successful HTTPS activate workflows for CPP nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful HTTPS activate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulCppHTTPSActivateWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_HTTPS_ACTIVATE);
    }

    /**
     * The average duration of successful HTTPS activate workflows for CPP nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful HTTPS activate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulCppHTTPSActivateWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_HTTPS_ACTIVATE);
    }

    /**
     * The number of failed HTTPS activate workflows for CPP nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed HTTPS activate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedCppHTTPSActivateWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_HTTPS_ACTIVATE);
    }

    /**
     * The number of errored HTTPS activate workflows for CPP nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored HTTPS activate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredCppHTTPSActivateWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_HTTPS_ACTIVATE);
    }

    /**
     * The number of timed-out HTTPS activate workflows for CPP nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out HTTPS activate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutCppHTTPSActivateWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_HTTPS_ACTIVATE);
    }

    /**
     * The number of successful HTTPS deactivate workflows for CPP nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful HTTPS deactivate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulCppHTTPSDeactivateWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_HTTPS_DEACTIVATE);
    }

    /**
     * The average duration of successful HTTPS deactivate workflows for CPP nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful HTTPS deactivate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulCppHTTPSDeactivateWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_HTTPS_DEACTIVATE);
    }

    /**
     * The number of failed HTTPS deactivate workflows for CPP nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed HTTPS deactivate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedCppHTTPSDeactivateWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_HTTPS_DEACTIVATE);
    }

    /**
     * The number of errored HTTPS deactivate workflows for CPP nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored HTTPS deactivate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredCppHTTPSDeactivateWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_HTTPS_DEACTIVATE);
    }

    /**
     * The number of timed-out HTTPS deactivate workflows for CPP nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out HTTPS deactivate workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutCppHTTPSDeactivateWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_HTTPS_DEACTIVATE);
    }

    /**
     * The number of successful HTTPS get workflows for CPP nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful HTTPS get workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulCppHTTPSGetWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_HTTPS_GET);
    }

    /**
     * The average duration of successful HTTPS get workflows for CPP nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful HTTPS get workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulCppHTTPSGetWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_HTTPS_GET);
    }

    /**
     * The number of failed HTTPS get workflows for CPP nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed HTTPS get workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedCppHTTPSGetWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_HTTPS_GET);
    }

    /**
     * The number of errored HTTPS get workflows for CPP nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored HTTPS get workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredCppHTTPSGetWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_HTTPS_GET);
    }

    /**
     * The number of timed-out HTTPS get workflows for CPP nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out HTTPS get workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutCppHTTPSGetWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_HTTPS_GET);
    }

    /**
     * The number of successful FTPES activate workflows for ECIM nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful FTPES activate workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulEcimFTPESActivateWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_FTPES_ACTIVATE);
    }

    /**
     * The average duration of successful FTPES activate workflows for ECIM nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful FTPES activate workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulEcimFTPESActivateWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_FTPES_ACTIVATE);
    }

    /**
     * The number of failed FTPES activate workflows for ECIM nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed FTPES activate workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedEcimFTPESActivateWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.ECIM_FTPES_ACTIVATE);
    }

    /**
     * The number of errored FTPES activate workflows for ECIM nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored FTPES activate workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredEcimFTPESActivateWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.ECIM_FTPES_ACTIVATE);
    }

    /**
     * The number of timed-out FTPES activate workflows for ECIM nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out FTPES activate workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutEcimFTPESActivateWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.ECIM_FTPES_ACTIVATE);
    }

    /**
     * The number of successful FTPES deactivate workflows for ECIM nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful FTPES deactivate workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulEcimFTPESDeactivateWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_FTPES_DEACTIVATE);
    }

    /**
     * The average duration of successful FTPES deactivate workflows for ECIM nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful FTPES deactivate workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulEcimFTPESDeactivateWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_FTPES_DEACTIVATE);
    }

    /**
     * The number of failed FTPES deactivate workflows for ECIM nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed FTPES deactivate workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedEcimFTPESDeactivateWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.ECIM_FTPES_DEACTIVATE);
    }

    /**
     * The number of errored FTPES deactivate workflows for ECIM nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored FTPES deactivate workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredEcimFTPESDeactivateWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.ECIM_FTPES_DEACTIVATE);
    }

    /**
     * The number of timed-out FTPES deactivate workflows for ECIM nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out FTPES deactivate workflows for ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutEcimFTPESDeactivateWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.ECIM_FTPES_DEACTIVATE);
    }

    /**
     * The number of successful LAAD distribute workflows for CPP nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful LAAD distribute workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulCppLaadDistributeWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_LAAD_DISTRIBUTE);
    }

    /**
     * The average duration of successful LAAD distribute workflows for CPP nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful LAAD distribute workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulCppLaadDistributeWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_LAAD_DISTRIBUTE);
    }

    /**
     * The number of failed LAAD distribute workflows for CPP nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed LAAD distribute workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedCppLaadDistributeWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_LAAD_DISTRIBUTE);
    }

    /**
     * The number of errored LAAD distribute workflows for CPP nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored LAAD distribute workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredCppLaadDistributeWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_LAAD_DISTRIBUTE);
    }

    /**
     * The number of timed-out LAAD distribute workflows for CPP nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out LAAD distribute workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutCppLaadDistributeWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_LAAD_DISTRIBUTE);
    }

    /**
     * The number of successful NTP configure workflows for CPP nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful NTP configure workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulCppNtpConfigureWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_NTP_CONFIGURE);
    }

    /**
     * The average duration of successful NTP configure workflows for CPP nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful NTP configure workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulCppNtpConfigureWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_NTP_CONFIGURE);
    }

    /**
     * The number of failed NTP configure workflows for CPP nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed NTP configure workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedCppNtpConfigureWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_NTP_CONFIGURE);
    }

    /**
     * The number of errored NTP configure workflows for CPP nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored NTP configure workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredCppNtpConfigureWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_NTP_CONFIGURE);
    }

    /**
     * The number of timed-out NTP configure workflows for CPP nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out NTP configure workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutCppNtpConfigureWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_NTP_CONFIGURE);
    }

    /**
     * The number of successful NTP remove workflows for CPP nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful NTP remove workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulCppNtpRemoveWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_NTP_REMOVE);
    }

    /**
     * The average duration of successful NTP remove workflows for CPP nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful NTP remove workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulCppNtpRemoveWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_NTP_REMOVE);
    }

    /**
     * The number of failed NTP remove workflows for CPP nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed NTP remove workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedCppNtpRemoveWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_NTP_REMOVE);
    }

    /**
     * The number of errored NTP remove workflows for CPP nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored NTP remove workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredCppNtpRemoveWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_NTP_REMOVE);
    }

    /**
     * The number of timed-out NTP remove workflows for CPP nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out NTP remove workflows for CPP nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutCppNtpRemoveWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_NTP_REMOVE);
    }

    /**
     * The number of successful NTP remove workflows for COM ECIM nodes in NSCS.
     *
     * @return number of successful workflows
     */
    @MonitoredAttribute(displayName = "The number of successful NTP remove workflows for COM ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfSuccessfulComNtpRemoveWorkflows() {
        return nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.COMECIM_NTP_REMOVE);
    }

    /**
     * The average duration of successful NTP remove workflows for COM ECIM nodes in NSCS.
     *
     * @return average duration of successful workflows
     */
    @MonitoredAttribute(displayName = "The average duration of successful NTP remove workflows for COM ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.DYNAMIC)
    public long getAverageDurationOfSuccessfulComNtpRemoveWorkflows() {
        return nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.COMECIM_NTP_REMOVE);
    }

    /**
     * The number of failed NTP remove workflows for COM ECIM nodes in NSCS.
     *
     * @return number of failed workflows
     */
    @MonitoredAttribute(displayName = "The number of failed NTP remove workflows for COM ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfFailedComNtpRemoveWorkflows() {
        return nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.COMECIM_NTP_REMOVE);
    }

    /**
     * The number of errored NTP remove workflows for COM ECIM nodes in NSCS.
     *
     * @return number of errored workflows
     */
    @MonitoredAttribute(displayName = "The number of errored NTP remove workflows for COM ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfErroredComNtpRemoveWorkflows() {
        return nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.COMECIM_NTP_REMOVE);
    }

    /**
     * The number of timed-out NTP remove workflows for COM ECIM nodes in NSCS.
     *
     * @return number of timed-out workflows
     */
    @MonitoredAttribute(displayName = "The number of timed-out NTP remove workflows for COM ECIM nodes in NSCS", visibility = Visibility.ALL, units = Units.NONE, category = Category.PERFORMANCE, interval = Interval.ONE_MIN, collectionType = CollectionType.TRENDSUP)
    public long getNumOfTimedOutComNtpRemoveWorkflows() {
        return nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.COMECIM_NTP_REMOVE);
    }

}
