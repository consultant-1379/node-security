/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2021
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.instrumentation

import static org.junit.Assert.assertEquals

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.instrumentation.NscsMonitoredEntityTypes

class NscsInstrumentationBeanTest extends CdiSpecification {

    @ObjectUnderTest
    NscsInstrumentationBean nscsInstrumentationBean

    def "on service start" () {
        given:
        when:
        nscsInstrumentationBean.onServiceStart()
        then:
        noExceptionThrown()
    }

    def "on service stop" () {
        given:
        when:
        nscsInstrumentationBean.onServiceStop()
        then:
        noExceptionThrown()
    }

    def "get number of failed Workflows in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedWorkflows()
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredWorkflows()
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutWorkflows()
        and:
        assertEquals("wrong number of failed workflows", 0L, num)
    }

    def "get number of removed Workflows in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfRemovedWorkflows()
        then:
        assertEquals("wrong number of removed workflows", 0L, num)
    }

    def "get number of successful distributed Trusted Certificates in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulDistributedTrustedCertificates()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_TRUST_DISTRIBUTE)
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_TRUST_DISTRIBUTE)
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.EOI_TRUST_DISTRIBUTE)
        and:
        assertEquals("wrong number of successful distributed trusted certificates workflows", 0L, num)
    }

    def "get number of failed Trusted Certificates in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedDistributedTrustedCertificates()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfUnsuccessfulInstances(NscsMonitoredEntityTypes.CPP_TRUST_DISTRIBUTE)
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfUnsuccessfulInstances(NscsMonitoredEntityTypes.ECIM_TRUST_DISTRIBUTE)
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfUnsuccessfulInstances(NscsMonitoredEntityTypes.EOI_TRUST_DISTRIBUTE)
        and:
        assertEquals("wrong number of failed distributed trusted certificates workflows", 0L, num)
    }

    def "get number of successful generated SSH keys in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulGeneratedSSHKeys()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.SSH_KEY)
        and:
        assertEquals("wrong number of successful generated SSH keys", 0L, num)
    }

    def "get number of failed generated SSH keys in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedGeneratedSSHKeys()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfUnsuccessfulInstances(NscsMonitoredEntityTypes.SSH_KEY)
        and:
        assertEquals("wrong number of failed generated SSH keys", 0L, num)
    }

    def "get number of successful enrolled node certificates in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulEnrolledCertificates()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_CERTIFICATE_ENROLLMENT)
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_CERTIFICATE_ENROLLMENT)
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.EOI_CERTIFICATE_ENROLLMENT)
        and:
        assertEquals("wrong number of successful enrolled node certificates", 0L, num)
    }

    def "get number of failed enrolled node certificates in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedEnrolledCertificates()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfUnsuccessfulInstances(NscsMonitoredEntityTypes.CPP_CERTIFICATE_ENROLLMENT)
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfUnsuccessfulInstances(NscsMonitoredEntityTypes.ECIM_CERTIFICATE_ENROLLMENT)
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfUnsuccessfulInstances(NscsMonitoredEntityTypes.EOI_CERTIFICATE_ENROLLMENT)
        and:
        assertEquals("wrong number of failed enrolled node certificates", 0L, num)
    }

    def "get number of successful generated ISCF file in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccesfulGeneratedISCFFiles()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.ISCF);
        and:
        assertEquals("wrong number of successful generated ISCF file", 0L, num)
    }

    def "get number of failed generated ISCF file in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedGeneratedISCFFiles()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfUnsuccessfulInstances(NscsMonitoredEntityTypes.ISCF);
        and:
        assertEquals("wrong number of failed generated ISCF file", 0L, num)
    }

    def "get number of failed SL2 cert enrollment" () {
        given:
        when:
        def num = nscsInstrumentationBean.getSL2InitCertEnrollmentFailures()
        then:
        assertEquals("wrong number of failed SL2 cert enrollments", 0L, num)
    }

    def "get number of failed SL2 trust distribute" () {
        given:
        when:
        def num = nscsInstrumentationBean.getSL2InstallTrustedCertificateFailures()
        then:
        assertEquals("wrong number of failed SL2 trust distribute", 0L, num)
    }

    def "get number of failed SL2 activate" () {
        given:
        when:
        def num = nscsInstrumentationBean.getActivateSL2Failures()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfUnsuccessfulInstances(NscsMonitoredEntityTypes.CPP_SL2_ACTIVATE);
        and:
        assertEquals("wrong number of failed SL2 activate", 0L, num)
    }

    def "get number of SL2 activate" () {
        given:
        when:
        def num = nscsInstrumentationBean.getActivateSL2Invocations()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfInstances(NscsMonitoredEntityTypes.CPP_SL2_ACTIVATE);
        and:
        assertEquals("wrong number of SL2 activate", 0L, num)
    }

    def "get number of failed SL2 deactivate" () {
        given:
        when:
        def num = nscsInstrumentationBean.getDeActivateSL2Failures()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfUnsuccessfulInstances(NscsMonitoredEntityTypes.CPP_SL2_DEACTIVATE);
        and:
        assertEquals("wrong number of failed SL2 deactivate", 0L, num)
    }

    def "get number of SL2 deactivate" () {
        given:
        when:
        def num = nscsInstrumentationBean.getDeActivateSL2Invocations()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfInstances(NscsMonitoredEntityTypes.CPP_SL2_DEACTIVATE);
        and:
        assertEquals("wrong number of SL2 deactivate", 0L, num)
    }

    def "get number of failed IpSec cert enrollment" () {
        given:
        when:
        def num = nscsInstrumentationBean.getIpsecInitCertEnrollmentFailures()
        then:
        assertEquals("wrong number of failed IpSec cert enrollments", 0L, num)
    }

    def "get number of failed IpSec trust distribute" () {
        given:
        when:
        def num = nscsInstrumentationBean.getIpsecInstallTrustedCertificateFailures()
        then:
        assertEquals("wrong number of failed IpSec trust distribute", 0L, num)
    }

    def "get number of failed IpSec activate" () {
        given:
        when:
        def num = nscsInstrumentationBean.getActivateIpsecFailures()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfUnsuccessfulInstances(NscsMonitoredEntityTypes.CPP_IPSEC_ACTIVATE);
        and:
        assertEquals("wrong number of failed IpSec activate", 0L, num)
    }

    def "get number of IpSec activate" () {
        given:
        when:
        def num = nscsInstrumentationBean.getActivateIpsecInvocations()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfInstances(NscsMonitoredEntityTypes.CPP_IPSEC_ACTIVATE);
        and:
        assertEquals("wrong number of IpSec activate", 0L, num)
    }

    def "get number of failed IpSec deactivate" () {
        given:
        when:
        def num = nscsInstrumentationBean.getDeActivateIpsecFailures()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfUnsuccessfulInstances(NscsMonitoredEntityTypes.CPP_IPSEC_DEACTIVATE);
        and:
        assertEquals("wrong number of failed IpSec deactivate", 0L, num)
    }

    def "get number of IpSec deactivate" () {
        given:
        when:
        def num = nscsInstrumentationBean.getDeActivateIpsecInvocations()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfInstances(NscsMonitoredEntityTypes.CPP_IPSEC_DEACTIVATE);
        and:
        assertEquals("wrong number of IpSec deactivate", 0L, num)
    }

    def "get number of successful workflows in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulWorkflows()
        and:
        assertEquals("wrong number of successful workflows", 0L, num)
    }

    def "get number of pending workflows in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfPendingWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfPendingWorkflows()
        and:
        assertEquals("wrong number of pending workflows", 0L, num)
    }

    def "get number of running workflows in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfRunningWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfRunningWorkflows()
        and:
        assertEquals("wrong number of running workflows", 0L, num)
    }

    def "get number of errored workflows in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredWorkflows()
        and:
        assertEquals("wrong number of errored workflows", 0L, num)
    }

    def "get number of timed-out workflows in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutWorkflows()
        and:
        assertEquals("wrong number of timed-out workflows", 0L, num)
    }

    def "get number of successful ISCF service invocations in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulIscfInvocations()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.ISCF)
        and:
        assertEquals("wrong number of successful ISCF invocations", 0L, num)
    }

    def "get number of failed ISCF service invocations in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedIscfInvocations()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.ISCF)
        and:
        assertEquals("wrong number of failed ISCF invocations", 0L, num)
    }

    def "get number of successful SSH keys generation workflows in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulSSHKeyGenerationWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.SSH_KEY)
        and:
        assertEquals("wrong number of successful SSH keys generation", 0L, num)
    }

    def "get average duration of successful SSH keys generation workflows in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulSSHKeyGenerationWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.SSH_KEY)
        and:
        assertEquals("wrong average duration of successful SSH keys generation", 0L, num)
    }

    def "get number of failed SSH keys generation workflows in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedSSHKeyGenerationWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.SSH_KEY)
        and:
        assertEquals("wrong number of failed SSH keys generation", 0L, num)
    }

    def "get number of errored SSH keys generation workflows in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredSSHKeyGenerationWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.SSH_KEY)
        and:
        assertEquals("wrong number of errored SSH keys generation", 0L, num)
    }

    def "get number of timed-out SSH keys generation workflows in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutSSHKeyGenerationWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.SSH_KEY)
        and:
        assertEquals("wrong number of timed-out SSH keys generation", 0L, num)
    }

    def "get number of successful SL2 activate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulCppSL2ActivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_SL2_ACTIVATE)
        and:
        assertEquals("wrong number of successful SL2 activations", 0L, num)
    }

    def "get average duration of successful SL2 activate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulCppSL2ActivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_SL2_ACTIVATE)
        and:
        assertEquals("wrong average duration of successful SL2 activations", 0L, num)
    }

    def "get number of failed SL2 activate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedCppSL2ActivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_SL2_ACTIVATE)
        and:
        assertEquals("wrong number of failed SL2 activations", 0L, num)
    }

    def "get number of errored SL2 activate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredCppSL2ActivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_SL2_ACTIVATE)
        and:
        assertEquals("wrong number of errored SL2 activations", 0L, num)
    }

    def "get number of timed-out SL2 activate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutCppSL2ActivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_SL2_ACTIVATE)
        and:
        assertEquals("wrong number of timed-out SL2 activations", 0L, num)
    }

    def "get number of successful SL2 deactivate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulCppSL2DeactivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_SL2_DEACTIVATE)
        and:
        assertEquals("wrong number of successful SL2 deactivations", 0L, num)
    }

    def "get average duration of successful SL2 deactivate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulCppSL2DeactivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_SL2_DEACTIVATE)
        and:
        assertEquals("wrong average duration of successful SL2 deactivations", 0L, num)
    }

    def "get number of failed SL2 deactivate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedCppSL2DeactivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_SL2_DEACTIVATE)
        and:
        assertEquals("wrong number of failed SL2 deactivations", 0L, num)
    }

    def "get number of errored SL2 deactivate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredCppSL2DeactivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_SL2_DEACTIVATE)
        and:
        assertEquals("wrong number of errored SL2 deactivations", 0L, num)
    }

    def "get number of timed-out SL2 deactivate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutCppSL2DeactivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_SL2_DEACTIVATE)
        and:
        assertEquals("wrong number of timed-out SL2 deactivations", 0L, num)
    }

    def "get number of successful IpSec activate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulCppIpSecActivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_IPSEC_ACTIVATE)
        and:
        assertEquals("wrong number of successful IpSec activations", 0L, num)
    }

    def "get average duration of successful IpSec activate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulCppIpSecActivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_IPSEC_ACTIVATE)
        and:
        assertEquals("wrong average duration of successful IpSec activations", 0L, num)
    }

    def "get number of failed IpSec activate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedCppIpSecActivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_IPSEC_ACTIVATE)
        and:
        assertEquals("wrong number of failed IpSec activations", 0L, num)
    }

    def "get number of errored IpSec activate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredCppIpSecActivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_IPSEC_ACTIVATE)
        and:
        assertEquals("wrong number of errored IpSec activations", 0L, num)
    }

    def "get number of timed-out IpSec activate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutCppIpSecActivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_IPSEC_ACTIVATE)
        and:
        assertEquals("wrong number of timed-out IpSec activations", 0L, num)
    }

    def "get number of successful IpSec deactivate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulCppIpSecDeactivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_IPSEC_DEACTIVATE)
        and:
        assertEquals("wrong number of successful IpSec deactivations", 0L, num)
    }

    def "get average duration of successful IpSec deactivate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulCppIpSecDeactivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_IPSEC_DEACTIVATE)
        and:
        assertEquals("wrong average duration of successful IpSec deactivations", 0L, num)
    }

    def "get number of failed IpSec deactivate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedCppIpSecDeactivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_IPSEC_DEACTIVATE)
        and:
        assertEquals("wrong number of failed IpSec deactivations", 0L, num)
    }

    def "get number of errored IpSec deactivate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredCppIpSecDeactivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_IPSEC_DEACTIVATE)
        and:
        assertEquals("wrong number of errored IpSec deactivations", 0L, num)
    }

    def "get number of timed-out IpSec deactivate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutCppIpSecDeactivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_IPSEC_DEACTIVATE)
        and:
        assertEquals("wrong number of timed-out IpSec deactivations", 0L, num)
    }

    def "get number of successful certificate enrollment workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulCppCertificateEnrollmentWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_CERTIFICATE_ENROLLMENT)
        and:
        assertEquals("wrong number of successful CPP certificate enrollment", 0L, num)
    }

    def "get average duration of successful certificate enrollment workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulCppCertificateEnrollmentWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_CERTIFICATE_ENROLLMENT)
        and:
        assertEquals("wrong average duration of successful CPP certificate enrollment", 0L, num)
    }

    def "get number of failed certificate enrollment workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedCppCertificateEnrollmentWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_CERTIFICATE_ENROLLMENT)
        and:
        assertEquals("wrong number of failed CPP certificate enrollment", 0L, num)
    }

    def "get number of errored certificate enrollment workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredCppCertificateEnrollmentWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_CERTIFICATE_ENROLLMENT)
        and:
        assertEquals("wrong number of errored CPP certificate enrollment", 0L, num)
    }

    def "get number of timed-out certificate enrollment workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutCppCertificateEnrollmentWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_CERTIFICATE_ENROLLMENT)
        and:
        assertEquals("wrong number of timed-out CPP certificate enrollment", 0L, num)
    }

    def "get number of successful certificate enrollment workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulEcimCertificateEnrollmentWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_CERTIFICATE_ENROLLMENT)
        and:
        assertEquals("wrong number of successful ECIM certificate enrollment", 0L, num)
    }

    def "get average duration of successful certificate enrollment workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulEcimCertificateEnrollmentWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_CERTIFICATE_ENROLLMENT)
        and:
        assertEquals("wrong average duration of successful ECIM certificate enrollment", 0L, num)
    }

    def "get number of failed certificate enrollment workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedEcimCertificateEnrollmentWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.ECIM_CERTIFICATE_ENROLLMENT)
        and:
        assertEquals("wrong number of failed ECIM certificate enrollment", 0L, num)
    }

    def "get number of errored certificate enrollment workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredEcimCertificateEnrollmentWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.ECIM_CERTIFICATE_ENROLLMENT)
        and:
        assertEquals("wrong number of errored ECIM certificate enrollment", 0L, num)
    }

    def "get number of timed-out certificate enrollment workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutEcimCertificateEnrollmentWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.ECIM_CERTIFICATE_ENROLLMENT)
        and:
        assertEquals("wrong number of timed-out ECIM certificate enrollment", 0L, num)
    }

    def "get number of successful certificate enrollment workflows for EOI nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulEoiCertificateEnrollmentWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.EOI_CERTIFICATE_ENROLLMENT)
        and:
        assertEquals("wrong number of successful EOI certificate enrollment", 0L, num)
    }

    def "get average duration of successful certificate enrollment workflows for EOI nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulEoiCertificateEnrollmentWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.EOI_CERTIFICATE_ENROLLMENT)
        and:
        assertEquals("wrong average duration of successful EOI certificate enrollment", 0L, num)
    }

    def "get number of failed certificate enrollment workflows for EOI nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedEoiCertificateEnrollmentWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.EOI_CERTIFICATE_ENROLLMENT)
        and:
        assertEquals("wrong number of failed EOI certificate enrollment", 0L, num)
    }

    def "get number of errored certificate enrollment workflows for EOI nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredEoiCertificateEnrollmentWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.EOI_CERTIFICATE_ENROLLMENT)
        and:
        assertEquals("wrong number of errored EOI certificate enrollment", 0L, num)
    }

    def "get number of timed-out certificate enrollment workflows for EOI nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutEoiCertificateEnrollmentWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.EOI_CERTIFICATE_ENROLLMENT)
        and:
        assertEquals("wrong number of timed-out EOI certificate enrollment", 0L, num)
    }

    def "get number of successful trust distribute workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulCppTrustDistributeWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_TRUST_DISTRIBUTE)
        and:
        assertEquals("wrong number of successful CPP trust distribute", 0L, num)
    }

    def "get average duration of successful trust distribute workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulCppTrustDistributeWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_TRUST_DISTRIBUTE)
        and:
        assertEquals("wrong average duration of successful CPP trust distribute", 0L, num)
    }

    def "get number of failed trust distribute workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedCppTrustDistributeWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_TRUST_DISTRIBUTE)
        and:
        assertEquals("wrong number of failed CPP trust distribute", 0L, num)
    }

    def "get number of errored trust distribute workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredCppTrustDistributeWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_TRUST_DISTRIBUTE)
        and:
        assertEquals("wrong number of errored CPP trust distribute", 0L, num)
    }

    def "get number of timed-out trust distribute workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutCppTrustDistributeWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_TRUST_DISTRIBUTE)
        and:
        assertEquals("wrong number of timed-out CPP trust distribute", 0L, num)
    }

    def "get number of successful trust distribute workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulEcimTrustDistributeWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_TRUST_DISTRIBUTE)
        and:
        assertEquals("wrong number of successful ECIM trust distribute", 0L, num)
    }

    def "get average duration of successful trust distribute workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulEcimTrustDistributeWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_TRUST_DISTRIBUTE)
        and:
        assertEquals("wrong average duration of successful ECIM trust distribute", 0L, num)
    }

    def "get number of failed trust distribute workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedEcimTrustDistributeWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.ECIM_TRUST_DISTRIBUTE)
        and:
        assertEquals("wrong number of failed ECIM trust distribute", 0L, num)
    }

    def "get number of errored trust distribute workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredEcimTrustDistributeWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.ECIM_TRUST_DISTRIBUTE)
        and:
        assertEquals("wrong number of errored ECIM trust distribute", 0L, num)
    }

    def "get number of timed-out trust distribute workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutEcimTrustDistributeWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.ECIM_TRUST_DISTRIBUTE)
        and:
        assertEquals("wrong number of timed-out ECIM trust distribute", 0L, num)
    }

    def "get number of successful trust distribute workflows for EOI nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulEoiTrustDistributeWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.EOI_TRUST_DISTRIBUTE)
        and:
        assertEquals("wrong number of successful EOI trust distribute", 0L, num)
    }

    def "get average duration of successful trust distribute workflows for EOI nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulEoiTrustDistributeWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.EOI_TRUST_DISTRIBUTE)
        and:
        assertEquals("wrong average duration of successful EOI trust distribute", 0L, num)
    }

    def "get number of failed trust distribute workflows for EOI nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedEoiTrustDistributeWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.EOI_TRUST_DISTRIBUTE)
        and:
        assertEquals("wrong number of failed EOI trust distribute", 0L, num)
    }

    def "get number of errored trust distribute workflows for EOI nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredEoiTrustDistributeWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.EOI_TRUST_DISTRIBUTE)
        and:
        assertEquals("wrong number of errored EOI trust distribute", 0L, num)
    }

    def "get number of timed-out trust distribute workflows for EOI nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutEoiTrustDistributeWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.EOI_TRUST_DISTRIBUTE)
        and:
        assertEquals("wrong number of timed-out EOI trust distribute", 0L, num)
    }

    def "get number of successful trust remove workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulCppTrustRemoveWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_TRUST_REMOVE)
        and:
        assertEquals("wrong number of successful CPP trust remove", 0L, num)
    }

    def "get average duration of successful trust remove workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulCppTrustRemoveWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_TRUST_REMOVE)
        and:
        assertEquals("wrong average duration of successful CPP trust remove", 0L, num)
    }

    def "get number of failed trust remove workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedCppTrustRemoveWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_TRUST_REMOVE)
        and:
        assertEquals("wrong number of failed CPP trust remove", 0L, num)
    }

    def "get number of errored trust remove workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredCppTrustRemoveWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_TRUST_REMOVE)
        and:
        assertEquals("wrong number of errored CPP trust remove", 0L, num)
    }

    def "get number of timed-out trust remove workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutCppTrustRemoveWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_TRUST_REMOVE)
        and:
        assertEquals("wrong number of timed-out CPP trust remove", 0L, num)
    }

    def "get number of successful trust remove workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulEcimTrustRemoveWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_TRUST_REMOVE)
        and:
        assertEquals("wrong number of successful ECIM trust remove", 0L, num)
    }

    def "get average duration of successful trust remove workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulEcimTrustRemoveWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_TRUST_REMOVE)
        and:
        assertEquals("wrong average duration of successful ECIM trust remove", 0L, num)
    }

    def "get number of failed trust remove workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedEcimTrustRemoveWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.ECIM_TRUST_REMOVE)
        and:
        assertEquals("wrong number of failed ECIM trust remove", 0L, num)
    }

    def "get number of errored trust remove workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredEcimTrustRemoveWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.ECIM_TRUST_REMOVE)
        and:
        assertEquals("wrong number of errored ECIM trust remove", 0L, num)
    }

    def "get number of timed-out trust remove workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutEcimTrustRemoveWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.ECIM_TRUST_REMOVE)
        and:
        assertEquals("wrong number of timed-out ECIM trust remove", 0L, num)
    }

    def "get number of successful trust remove workflows for EOI nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulEoiTrustRemoveWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.EOI_TRUST_REMOVE)
        and:
        assertEquals("wrong number of successful EOI trust remove", 0L, num)
    }

    def "get average duration of successful trust remove workflows for EOI nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulEoiTrustRemoveWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.EOI_TRUST_REMOVE)
        and:
        assertEquals("wrong average duration of successful EOI trust remove", 0L, num)
    }

    def "get number of failed trust remove workflows for EOI nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedEoiTrustRemoveWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.EOI_TRUST_REMOVE)
        and:
        assertEquals("wrong number of failed EOI trust remove", 0L, num)
    }

    def "get number of errored trust remove workflows for EOI nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredEoiTrustRemoveWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.EOI_TRUST_REMOVE)
        and:
        assertEquals("wrong number of errored EOI trust remove", 0L, num)
    }

    def "get number of timed-out trust remove workflows for EOI nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutEoiTrustRemoveWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.EOI_TRUST_REMOVE)
        and:
        assertEquals("wrong number of timed-out EOI trust remove", 0L, num)
    }

    def "get number of successful LDAP configure workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulEcimLdapConfigureWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_LDAP_CONFIGURE)
        and:
        assertEquals("wrong number of successful ECIM LDAP configure", 0L, num)
    }

    def "get average duration of successful LDAP configure workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulEcimLdapConfigureWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_LDAP_CONFIGURE)
        and:
        assertEquals("wrong average duration of successful ECIM LDAP configure", 0L, num)
    }

    def "get number of failed LDAP configure workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedEcimLdapConfigureWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.ECIM_LDAP_CONFIGURE)
        and:
        assertEquals("wrong number of failed ECIM LDAP configure", 0L, num)
    }

    def "get number of errored LDAP configure workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredEcimLdapConfigureWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.ECIM_LDAP_CONFIGURE)
        and:
        assertEquals("wrong number of errored ECIM LDAP configure", 0L, num)
    }

    def "get number of timed-out LDAP configure workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutEcimLdapConfigureWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.ECIM_LDAP_CONFIGURE)
        and:
        assertEquals("wrong number of timed-out ECIM LDAP configure", 0L, num)
    }

    def "get number of successful LDAP configure workflows for EOI nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulEoiLdapConfigureWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.EOI_LDAP_CONFIGURE)
        and:
        assertEquals("wrong number of successful EOI LDAP configure", 0L, num)
    }

    def "get average duration of successful LDAP configure workflows for EOI nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulEoiLdapConfigureWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.EOI_LDAP_CONFIGURE)
        and:
        assertEquals("wrong average duration of successful EOI LDAP configure", 0L, num)
    }

    def "get number of failed LDAP configure workflows for EOI nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedEoiLdapConfigureWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.EOI_LDAP_CONFIGURE)
        and:
        assertEquals("wrong number of failed EOI LDAP configure", 0L, num)
    }

    def "get number of errored LDAP configure workflows for EOI nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredEoiLdapConfigureWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.EOI_LDAP_CONFIGURE)
        and:
        assertEquals("wrong number of errored EOI LDAP configure", 0L, num)
    }

    def "get number of timed-out LDAP configure workflows for EOI nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutEoiLdapConfigureWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.EOI_LDAP_CONFIGURE)
        and:
        assertEquals("wrong number of timed-out EOI LDAP configure", 0L, num)
    }

    def "get number of successful CRL check workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulCppCRLCheckWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_CRLCHECK)
        and:
        assertEquals("wrong number of successful CPP CRL check", 0L, num)
    }

    def "get average duration of successful CRL check workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulCppCRLCheckWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_CRLCHECK)
        and:
        assertEquals("wrong average duration of successful CPP CRL check", 0L, num)
    }

    def "get number of failed CRL check workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedCppCRLCheckWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_CRLCHECK)
        and:
        assertEquals("wrong number of failed CPP CRL check", 0L, num)
    }

    def "get number of errored CRL check workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredCppCRLCheckWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_CRLCHECK)
        and:
        assertEquals("wrong number of errored CPP CRL check", 0L, num)
    }

    def "get number of timed-out CRL check workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutCppCRLCheckWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_CRLCHECK)
        and:
        assertEquals("wrong number of timed-out CPP CRL check", 0L, num)
    }

    def "get number of successful CRL check workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulEcimCRLCheckWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_CRLCHECK)
        and:
        assertEquals("wrong number of successful ECIM CRL check", 0L, num)
    }

    def "get average duration of successful CRL check workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulEcimCRLCheckWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_CRLCHECK)
        and:
        assertEquals("wrong average duration of successful ECIM CRL check", 0L, num)
    }

    def "get number of failed CRL check workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedEcimCRLCheckWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.ECIM_CRLCHECK)
        and:
        assertEquals("wrong number of failed ECIM CRL check", 0L, num)
    }

    def "get number of errored CRL check workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredEcimCRLCheckWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.ECIM_CRLCHECK)
        and:
        assertEquals("wrong number of errored ECIM CRL check", 0L, num)
    }

    def "get number of timed-out CRL check workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutEcimCRLCheckWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.ECIM_CRLCHECK)
        and:
        assertEquals("wrong number of timed-out ECIM CRL check", 0L, num)
    }

    def "get number of successful on-demand CRL download workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulCppOnDemandCRLDownloadWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_ON_DEMAND_CRL_DOWNLOAD)
        and:
        assertEquals("wrong number of successful CPP on-demand CRL download", 0L, num)
    }

    def "get average duration of successful on-demand CRL download workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulCppOnDemandCRLDownloadWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_ON_DEMAND_CRL_DOWNLOAD)
        and:
        assertEquals("wrong average duration of successful CPP on-demand CRL download", 0L, num)
    }

    def "get number of failed on-demand CRL download workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedCppOnDemandCRLDownloadWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_ON_DEMAND_CRL_DOWNLOAD)
        and:
        assertEquals("wrong number of failed CPP on-demand CRL download", 0L, num)
    }

    def "get number of errored on-demand CRL download workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredCppOnDemandCRLDownloadWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_ON_DEMAND_CRL_DOWNLOAD)
        and:
        assertEquals("wrong number of errored CPP on-demand CRL download", 0L, num)
    }

    def "get number of timed-out on-demand CRL download workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutCppOnDemandCRLDownloadWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_ON_DEMAND_CRL_DOWNLOAD)
        and:
        assertEquals("wrong number of timed-out CPP on-demand CRL download", 0L, num)
    }

    def "get number of successful on-demand CRL download workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulEcimOnDemandCRLDownloadWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_ON_DEMAND_CRL_DOWNLOAD)
        and:
        assertEquals("wrong number of successful ECIM on-demand CRL download", 0L, num)
    }

    def "get average duration of successful on-demand CRL download workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulEcimOnDemandCRLDownloadWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_ON_DEMAND_CRL_DOWNLOAD)
        and:
        assertEquals("wrong average duration of successful ECIM on-demand CRL download", 0L, num)
    }

    def "get number of failed on-demand CRL download workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedEcimOnDemandCRLDownloadWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.ECIM_ON_DEMAND_CRL_DOWNLOAD)
        and:
        assertEquals("wrong number of failed ECIM on-demand CRL download", 0L, num)
    }

    def "get number of errored on-demand CRL download workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredEcimOnDemandCRLDownloadWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.ECIM_ON_DEMAND_CRL_DOWNLOAD)
        and:
        assertEquals("wrong number of errored ECIM on-demand CRL download", 0L, num)
    }

    def "get number of timed-out on-demand CRL download workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutEcimOnDemandCRLDownloadWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.ECIM_ON_DEMAND_CRL_DOWNLOAD)
        and:
        assertEquals("wrong number of timed-out ECIM on-demand CRL download", 0L, num)
    }

    def "get number of successful set ciphers workflows in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulSetCiphersWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.SET_CIPHERS)
        and:
        assertEquals("wrong number of successful set ciphers", 0L, num)
    }

    def "get average duration of successful set ciphers workflows in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulSetCiphersWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.SET_CIPHERS)
        and:
        assertEquals("wrong average duration of successful set ciphers", 0L, num)
    }

    def "get number of failed set ciphers workflows in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedSetCiphersWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.SET_CIPHERS)
        and:
        assertEquals("wrong number of failed set ciphers", 0L, num)
    }

    def "get number of errored set ciphers workflows in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredSetCiphersWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.SET_CIPHERS)
        and:
        assertEquals("wrong number of errored set ciphers", 0L, num)
    }

    def "get number of timed-out set ciphers workflows in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutSetCiphersWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.SET_CIPHERS)
        and:
        assertEquals("wrong number of timed-out set ciphers", 0L, num)
    }

    def "get number of successful RTSEL activate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulCppRTSELActivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_RTSEL_ACTIVATE)
        and:
        assertEquals("wrong number of successful RTSEL activations", 0L, num)
    }

    def "get average duration of successful RTSEL activate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulCppRTSELActivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_RTSEL_ACTIVATE)
        and:
        assertEquals("wrong average duration of successful RTSEL activations", 0L, num)
    }

    def "get number of failed RTSEL activate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedCppRTSELActivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_RTSEL_ACTIVATE)
        and:
        assertEquals("wrong number of failed RTSEL activations", 0L, num)
    }

    def "get number of errored RTSEL activate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredCppRTSELActivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_RTSEL_ACTIVATE)
        and:
        assertEquals("wrong number of errored RTSEL activations", 0L, num)
    }

    def "get number of timed-out RTSEL activate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutCppRTSELActivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_RTSEL_ACTIVATE)
        and:
        assertEquals("wrong number of timed-out RTSEL activations", 0L, num)
    }

    def "get number of successful RTSEL deactivate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulCppRTSELDeactivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_RTSEL_DEACTIVATE)
        and:
        assertEquals("wrong number of successful RTSEL deactivations", 0L, num)
    }

    def "get average duration of successful RTSEL deactivate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulCppRTSELDeactivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_RTSEL_DEACTIVATE)
        and:
        assertEquals("wrong average duration of successful RTSEL deactivations", 0L, num)
    }

    def "get number of failed RTSEL deactivate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedCppRTSELDeactivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_RTSEL_DEACTIVATE)
        and:
        assertEquals("wrong number of failed RTSEL deactivations", 0L, num)
    }

    def "get number of errored RTSEL deactivate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredCppRTSELDeactivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_RTSEL_DEACTIVATE)
        and:
        assertEquals("wrong number of errored RTSEL deactivations", 0L, num)
    }

    def "get number of timed-out RTSEL deactivate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutCppRTSELDeactivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_RTSEL_DEACTIVATE)
        and:
        assertEquals("wrong number of timed-out RTSEL deactivations", 0L, num)
    }

    def "get number of successful RTSEL delete workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulCppRTSELDeleteWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_RTSEL_DELETE)
        and:
        assertEquals("wrong number of successful RTSEL delete", 0L, num)
    }

    def "get average duration of successful RTSEL delete workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulCppRTSELDeleteWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_RTSEL_DELETE)
        and:
        assertEquals("wrong average duration of successful RTSEL delete", 0L, num)
    }

    def "get number of failed RTSEL delete workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedCppRTSELDeleteWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_RTSEL_DELETE)
        and:
        assertEquals("wrong number of failed RTSEL delete", 0L, num)
    }

    def "get number of errored RTSEL delete workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredCppRTSELDeleteWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_RTSEL_DELETE)
        and:
        assertEquals("wrong number of errored RTSEL delete", 0L, num)
    }

    def "get number of timed-out RTSEL delete workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutCppRTSELDeleteWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_RTSEL_DELETE)
        and:
        assertEquals("wrong number of timed-out RTSEL delete", 0L, num)
    }

    def "get number of successful HTTPS activate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulCppHTTPSActivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_HTTPS_ACTIVATE)
        and:
        assertEquals("wrong number of successful HTTPS activations", 0L, num)
    }

    def "get average duration of successful HTTPS activate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulCppHTTPSActivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_HTTPS_ACTIVATE)
        and:
        assertEquals("wrong average duration of successful HTTPS activations", 0L, num)
    }

    def "get number of failed HTTPS activate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedCppHTTPSActivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_HTTPS_ACTIVATE)
        and:
        assertEquals("wrong number of failed HTTPS activations", 0L, num)
    }

    def "get number of errored HTTPS activate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredCppHTTPSActivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_HTTPS_ACTIVATE)
        and:
        assertEquals("wrong number of errored HTTPS activations", 0L, num)
    }

    def "get number of timed-out HTTPS activate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutCppHTTPSActivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_HTTPS_ACTIVATE)
        and:
        assertEquals("wrong number of timed-out HTTPS activations", 0L, num)
    }

    def "get number of successful HTTPS deactivate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulCppHTTPSDeactivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_HTTPS_DEACTIVATE)
        and:
        assertEquals("wrong number of successful HTTPS deactivations", 0L, num)
    }

    def "get average duration of successful HTTPS deactivate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulCppHTTPSDeactivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_HTTPS_DEACTIVATE)
        and:
        assertEquals("wrong average duration of successful HTTPS deactivations", 0L, num)
    }

    def "get number of failed HTTPS deactivate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedCppHTTPSDeactivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_HTTPS_DEACTIVATE)
        and:
        assertEquals("wrong number of failed HTTPS deactivations", 0L, num)
    }

    def "get number of errored HTTPS deactivate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredCppHTTPSDeactivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_HTTPS_DEACTIVATE)
        and:
        assertEquals("wrong number of errored HTTPS deactivations", 0L, num)
    }

    def "get number of timed-out HTTPS deactivate workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutCppHTTPSDeactivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_HTTPS_DEACTIVATE)
        and:
        assertEquals("wrong number of timed-out HTTPS deactivations", 0L, num)
    }

    def "get number of successful HTTPS get workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulCppHTTPSGetWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_HTTPS_GET)
        and:
        assertEquals("wrong number of successful HTTPS get", 0L, num)
    }

    def "get average duration of successful HTTPS get workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulCppHTTPSGetWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_HTTPS_GET)
        and:
        assertEquals("wrong average duration of successful HTTPS get", 0L, num)
    }

    def "get number of failed HTTPS get workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedCppHTTPSGetWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_HTTPS_GET)
        and:
        assertEquals("wrong number of failed HTTPS get", 0L, num)
    }

    def "get number of errored HTTPS get workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredCppHTTPSGetWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_HTTPS_GET)
        and:
        assertEquals("wrong number of errored HTTPS get", 0L, num)
    }

    def "get number of timed-out HTTPS get workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutCppHTTPSGetWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_HTTPS_GET)
        and:
        assertEquals("wrong number of timed-out HTTPS get", 0L, num)
    }

    def "get number of successful FTPES activate workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulEcimFTPESActivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_FTPES_ACTIVATE)
        and:
        assertEquals("wrong number of successful FTPES activations", 0L, num)
    }

    def "get average duration of successful FTPES activate workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulEcimFTPESActivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_FTPES_ACTIVATE)
        and:
        assertEquals("wrong average duration of successful FTPES activations", 0L, num)
    }

    def "get number of failed FTPES activate workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedEcimFTPESActivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.ECIM_FTPES_ACTIVATE)
        and:
        assertEquals("wrong number of failed FTPES activations", 0L, num)
    }

    def "get number of errored FTPES activate workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredEcimFTPESActivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.ECIM_FTPES_ACTIVATE)
        and:
        assertEquals("wrong number of errored FTPES activations", 0L, num)
    }

    def "get number of timed-out FTPES activate workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutEcimFTPESActivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.ECIM_FTPES_ACTIVATE)
        and:
        assertEquals("wrong number of timed-out FTPES activations", 0L, num)
    }

    def "get number of successful FTPES deactivate workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulEcimFTPESDeactivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_FTPES_DEACTIVATE)
        and:
        assertEquals("wrong number of successful FTPES deactivations", 0L, num)
    }

    def "get average duration of successful FTPES deactivate workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulEcimFTPESDeactivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.ECIM_FTPES_DEACTIVATE)
        and:
        assertEquals("wrong average duration of successful FTPES deactivations", 0L, num)
    }

    def "get number of failed FTPES deactivate workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedEcimFTPESDeactivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.ECIM_FTPES_DEACTIVATE)
        and:
        assertEquals("wrong number of failed FTPES deactivations", 0L, num)
    }

    def "get number of errored FTPES deactivate workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredEcimFTPESDeactivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.ECIM_FTPES_DEACTIVATE)
        and:
        assertEquals("wrong number of errored FTPES deactivations", 0L, num)
    }

    def "get number of timed-out FTPES deactivate workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutEcimFTPESDeactivateWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.ECIM_FTPES_DEACTIVATE)
        and:
        assertEquals("wrong number of timed-out FTPES deactivations", 0L, num)
    }

    def "get number of successful LAAD distribute workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulCppLaadDistributeWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_LAAD_DISTRIBUTE)
        and:
        assertEquals("wrong number of successful CPP LAAD distribute", 0L, num)
    }

    def "get average duration of successful LAAD distribute workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulCppLaadDistributeWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_LAAD_DISTRIBUTE)
        and:
        assertEquals("wrong average duration of successful CPP LAAD distribute", 0L, num)
    }

    def "get number of failed LAAD distribute workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedCppLaadDistributeWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_LAAD_DISTRIBUTE)
        and:
        assertEquals("wrong number of failed CPP LAAD distribute", 0L, num)
    }

    def "get number of errored LAAD distribute workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredCppLaadDistributeWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_LAAD_DISTRIBUTE)
        and:
        assertEquals("wrong number of errored CPP LAAD distribute", 0L, num)
    }

    def "get number of timed-out LAAD distribute workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutCppLaadDistributeWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_LAAD_DISTRIBUTE)
        and:
        assertEquals("wrong number of timed-out CPP LAAD distribute", 0L, num)
    }

    def "get number of successful NTP configure workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulCppNtpConfigureWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_NTP_CONFIGURE)
        and:
        assertEquals("wrong number of successful CPP NTP configure", 0L, num)
    }

    def "get average duration of successful NTP configure workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulCppNtpConfigureWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_NTP_CONFIGURE)
        and:
        assertEquals("wrong average duration of successful CPP NTP configure", 0L, num)
    }

    def "get number of failed NTP configure workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedCppNtpConfigureWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_NTP_CONFIGURE)
        and:
        assertEquals("wrong number of failed CPP NTP configure", 0L, num)
    }

    def "get number of errored NTP configure workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredCppNtpConfigureWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_NTP_CONFIGURE)
        and:
        assertEquals("wrong number of errored CPP NTP configure", 0L, num)
    }

    def "get number of timed-out NTP configure workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutCppNtpConfigureWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_NTP_CONFIGURE)
        and:
        assertEquals("wrong number of timed-out CPP NTP configure", 0L, num)
    }

    def "get number of successful NTP remove workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulCppNtpRemoveWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_NTP_REMOVE)
        and:
        assertEquals("wrong number of successful CPP NTP remove", 0L, num)
    }

    def "get average duration of successful NTP remove workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulCppNtpRemoveWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.CPP_NTP_REMOVE)
        and:
        assertEquals("wrong average duration of successful CPP NTP remove", 0L, num)
    }

    def "get number of failed NTP remove workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedCppNtpRemoveWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.CPP_NTP_REMOVE)
        and:
        assertEquals("wrong number of failed CPP NTP remove", 0L, num)
    }

    def "get number of errored NTP remove workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredCppNtpRemoveWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.CPP_NTP_REMOVE)
        and:
        assertEquals("wrong number of errored CPP NTP remove", 0L, num)
    }

    def "get number of timed-out NTP remove workflows for CPP nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutCppNtpRemoveWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.CPP_NTP_REMOVE)
        and:
        assertEquals("wrong number of timed-out CPP NTP remove", 0L, num)
    }

    def "get number of successful NTP remove workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfSuccessfulComNtpRemoveWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfSuccessfulInstances(NscsMonitoredEntityTypes.COMECIM_NTP_REMOVE)
        and:
        assertEquals("wrong number of successful ECIM NTP remove", 0L, num)
    }

    def "get average duration of successful NTP remove workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getAverageDurationOfSuccessfulComNtpRemoveWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(NscsMonitoredEntityTypes.COMECIM_NTP_REMOVE)
        and:
        assertEquals("wrong average duration of successful ECIM NTP remove", 0L, num)
    }

    def "get number of failed NTP remove workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfFailedComNtpRemoveWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfFailedInstances(NscsMonitoredEntityTypes.COMECIM_NTP_REMOVE)
        and:
        assertEquals("wrong number of failed ECIM NTP remove", 0L, num)
    }

    def "get number of errored NTP remove workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfErroredComNtpRemoveWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfErroredInstances(NscsMonitoredEntityTypes.COMECIM_NTP_REMOVE)
        and:
        assertEquals("wrong number of errored ECIM NTP remove", 0L, num)
    }

    def "get number of timed-out NTP remove workflows for ECIM nodes in NSCS" () {
        given:
        when:
        def num = nscsInstrumentationBean.getNumOfTimedOutComNtpRemoveWorkflows()
        then:
        1 * nscsInstrumentationBean.nscsInstrumentationService.getNumOfTimedOutInstances(NscsMonitoredEntityTypes.COMECIM_NTP_REMOVE)
        and:
        assertEquals("wrong number of timed-out ECIM NTP remove", 0L, num)
    }
}
