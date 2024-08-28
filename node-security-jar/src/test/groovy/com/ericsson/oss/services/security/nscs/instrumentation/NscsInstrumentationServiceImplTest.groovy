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
package com.ericsson.oss.services.security.nscs.instrumentation

import org.junit.Assert
import org.junit.Before

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.instrumentation.NscsMonitoredEntityTypes
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowNames

import spock.lang.Unroll

class NscsInstrumentationServiceImplTest extends CdiSpecification {

    @ObjectUnderTest
    private NscsInstrumentationServiceImpl nscsInstrumentationService

    private final static Long WF_DURATION = 50000L
    private final static String TOTAL = "T_"
    private final static String SUCCESSFUL = "S_"
    private final static String AVERAGE = "A_"
    private final static String UNSUCCESSFUL = "U_"
    private final static String FAILED = "F_"
    private final static String ERROR = "E_"
    private final static String TIMEOUT = "O_"
    private final static int NUM_OF_RUNNING = 75
    private final static int NUM_OF_PENDING = 25

    def beforeTotalSuccessful = null
    def beforeTotalFailed = null
    def beforeTotalErrored = null
    def beforeTotalTimedOut = null
    def beforeRunning = null
    def beforePending = null
    final Map<String, Object> beforeMonitoredEntities = new HashMap<String, Object>()

    final static List worflowData = [["sTiLlUnKnOwN", NscsMonitoredEntityTypes.UNDEFINED],
                                     [WorkflowNames.WORKFLOW_SSHKeyGeneration.getWorkflowName(), NscsMonitoredEntityTypes.SSH_KEY],
                                     [WorkflowNames.WORKFLOW_CPPActivateSL2.getWorkflowName(), NscsMonitoredEntityTypes.CPP_SL2_ACTIVATE],
                                     [WorkflowNames.WORKFLOW_CPPDeactivateSL2.getWorkflowName(), NscsMonitoredEntityTypes.CPP_SL2_DEACTIVATE],
                                     [WorkflowNames.WORKFLOW_CPPActivateIpSec.getWorkflowName(), NscsMonitoredEntityTypes.CPP_IPSEC_ACTIVATE],
                                     [WorkflowNames.WORKFLOW_CPPDeactivateIpSec.getWorkflowName(), NscsMonitoredEntityTypes.CPP_IPSEC_DEACTIVATE],
                                     [WorkflowNames.WORKFLOW_CPPInstallCertificatesIpSec.getWorkflowName(), NscsMonitoredEntityTypes.CPP_CERTIFICATE_ENROLLMENT],
                                     [WorkflowNames.WORKFLOW_SSHKeyGeneration.getWorkflowName(), NscsMonitoredEntityTypes.SSH_KEY],
                                     [WorkflowNames.WORKFLOW_CPPIssueCertificate.getWorkflowName(), NscsMonitoredEntityTypes.CPP_CERTIFICATE_ENROLLMENT],
                                     [WorkflowNames.WORKFLOW_CPPIssueReissueCertificate_IpSec.getWorkflowName(), NscsMonitoredEntityTypes.CPP_CERTIFICATE_ENROLLMENT],
                                     [WorkflowNames.WORKFLOW_CPPIssueTrustCert.getWorkflowName(), NscsMonitoredEntityTypes.CPP_TRUST_DISTRIBUTE],
                                     [WorkflowNames.WORKFLOW_CPPIssueTrustCertIpSec.getWorkflowName(), NscsMonitoredEntityTypes.CPP_TRUST_DISTRIBUTE],
                                     [WorkflowNames.WORKFLOW_COMECIM_ComIssueTrustCert.getWorkflowName(), NscsMonitoredEntityTypes.ECIM_TRUST_DISTRIBUTE],
                                     [WorkflowNames.WORKFLOW_CBPOI_INSTALL_TRUST_CERTS.getWorkflowName(), NscsMonitoredEntityTypes.EOI_TRUST_DISTRIBUTE],
                                     [WorkflowNames.WORKFLOW_COMECIM_ComIssueCert.getWorkflowName(), NscsMonitoredEntityTypes.ECIM_CERTIFICATE_ENROLLMENT],
                                     [WorkflowNames.WORKFLOW_CBPOI_START_ONLINE_ENROLLMENT.getWorkflowName(), NscsMonitoredEntityTypes.EOI_CERTIFICATE_ENROLLMENT],
                                     [WorkflowNames.WORKFLOW_CPPRemoveTrustOAM.getWorkflowName(), NscsMonitoredEntityTypes.CPP_TRUST_REMOVE],
                                     [WorkflowNames.WORKFLOW_CPPRemoveTrustIPSEC.getWorkflowName(), NscsMonitoredEntityTypes.CPP_TRUST_REMOVE],
                                     [WorkflowNames.WORKFLOW_COMECIMRemoveTrust.getWorkflowName(), NscsMonitoredEntityTypes.ECIM_TRUST_REMOVE],
                                     [WorkflowNames.WORKFLOW_CBPOI_REMOVE_TRUST.getWorkflowName(), NscsMonitoredEntityTypes.EOI_TRUST_REMOVE],
                                     [WorkflowNames.WORKFLOW_COMECIM_ENABLE_OR_DISABLE_CRLCHECK.getWorkflowName(), NscsMonitoredEntityTypes.ECIM_CRLCHECK],
                                     [WorkflowNames.WORKFLOW_CPP_ENABLE_OR_DISABLE_CRLCHECK.getWorkflowName(), NscsMonitoredEntityTypes.CPP_CRLCHECK],
                                     [WorkflowNames.WORKFLOW_COMECIM_ON_DEMAND_DOWNLOAD_CRL.getWorkflowName(), NscsMonitoredEntityTypes.ECIM_ON_DEMAND_CRL_DOWNLOAD],
                                     [WorkflowNames.WORKFLOW_CPP_ON_DEMAND_DOWNLOAD_CRL.getWorkflowName(), NscsMonitoredEntityTypes.CPP_ON_DEMAND_CRL_DOWNLOAD],
                                     [WorkflowNames.WORKFLOW_SET_CIPHERS.getWorkflowName(), NscsMonitoredEntityTypes.SET_CIPHERS],
                                     [WorkflowNames.WORKFLOW_CPPACTIVATERTSEL.getWorkflowName(), NscsMonitoredEntityTypes.CPP_RTSEL_ACTIVATE],
                                     [WorkflowNames.WORKFLOW_CPPDEACTIVATERTSEL.getWorkflowName(), NscsMonitoredEntityTypes.CPP_RTSEL_DEACTIVATE],
                                     [WorkflowNames.WORKFLOW_CPP_RTSEL_DELETE_SERVER.getWorkflowName(), NscsMonitoredEntityTypes.CPP_RTSEL_DELETE],
                                     [WorkflowNames.WORKFLOW_CPP_ACTIVATE_HTTPS.getWorkflowName(), NscsMonitoredEntityTypes.CPP_HTTPS_ACTIVATE],
                                     [WorkflowNames.WORKFLOW_CPP_DEACTIVATE_HTTPS.getWorkflowName(), NscsMonitoredEntityTypes.CPP_HTTPS_DEACTIVATE],
                                     [WorkflowNames.WORKFLOW_CPP_GET_HTTPS.getWorkflowName(), NscsMonitoredEntityTypes.CPP_HTTPS_GET],
                                     [WorkflowNames.WORKFLOW_COM_ACTIVATE_FTPES.getWorkflowName(), NscsMonitoredEntityTypes.ECIM_FTPES_ACTIVATE],
                                     [WorkflowNames.WORKFLOW_COM_DEACTIVATE_FTPES.getWorkflowName(), NscsMonitoredEntityTypes.ECIM_FTPES_DEACTIVATE],
                                     [WorkflowNames.WORKFLOW_CPP_LAAD_FILES_DISTRIBUTION.getWorkflowName(), NscsMonitoredEntityTypes.CPP_LAAD_DISTRIBUTE],
                                     [WorkflowNames.WORKFLOW_CPP_NTP_CONFIGURE.getWorkflowName(), NscsMonitoredEntityTypes.CPP_NTP_CONFIGURE],
                                     [WorkflowNames.WORKFLOW_CPP_NTP_REMOVE.getWorkflowName(), NscsMonitoredEntityTypes.CPP_NTP_REMOVE],
                                     [WorkflowNames.WORKFLOW_COMECIM_CONFIGURE_LDAP.getWorkflowName(), NscsMonitoredEntityTypes.ECIM_LDAP_CONFIGURE],
                                     [WorkflowNames.WORKFLOW_CBPOI_CONFIGURE_LDAP.getWorkflowName(), NscsMonitoredEntityTypes.EOI_LDAP_CONFIGURE],
                                     [WorkflowNames.WORKFLOW_COMECIM_NTP_REMOVE.getWorkflowName(), NscsMonitoredEntityTypes.COMECIM_NTP_REMOVE]]

    @Before
    void before() {
        beforeTotalSuccessful = nscsInstrumentationService.getNumOfSuccessfulWorkflows()
        beforeTotalFailed = nscsInstrumentationService.getNumOfFailedWorkflows()
        beforeTotalErrored = nscsInstrumentationService.getNumOfErroredWorkflows()
        beforeTotalTimedOut = nscsInstrumentationService.getNumOfTimedOutWorkflows()
        beforeRunning = nscsInstrumentationService.getNumOfRunningWorkflows()
        beforePending = nscsInstrumentationService.getNumOfPendingWorkflows()

        for (NscsMonitoredEntityTypes monitoredEntityType : NscsMonitoredEntityTypes.values()) {
            beforeMonitoredEntities.put(TOTAL + monitoredEntityType.name(), nscsInstrumentationService.getNumOfInstances(monitoredEntityType))
            beforeMonitoredEntities.put(SUCCESSFUL + monitoredEntityType.name(), nscsInstrumentationService.getNumOfSuccessfulInstances(monitoredEntityType))
            beforeMonitoredEntities.put(AVERAGE + monitoredEntityType.name(), nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(monitoredEntityType))
            beforeMonitoredEntities.put(UNSUCCESSFUL + monitoredEntityType.name(), nscsInstrumentationService.getNumOfUnsuccessfulInstances(monitoredEntityType))
            beforeMonitoredEntities.put(FAILED + monitoredEntityType.name(), nscsInstrumentationService.getNumOfFailedInstances(monitoredEntityType))
            beforeMonitoredEntities.put(ERROR + monitoredEntityType.name(), nscsInstrumentationService.getNumOfErroredInstances(monitoredEntityType))
            beforeMonitoredEntities.put(TIMEOUT + monitoredEntityType.name(), nscsInstrumentationService.getNumOfTimedOutInstances(monitoredEntityType))
        }
    }

    def 'Given an update of running and pending workflows when reading the NSCS metrics then expected values are returned'() {
        given:
            nscsInstrumentationService.setNumOfRunningWorkflows(NUM_OF_RUNNING)
            nscsInstrumentationService.setNumOfPendingWorkflows(NUM_OF_PENDING)

        expect:
            Assert.assertEquals("Wrong total successful", nscsInstrumentationService.getNumOfSuccessfulWorkflows(), beforeTotalSuccessful)
            Assert.assertEquals("Wrong total failed", nscsInstrumentationService.getNumOfFailedWorkflows(), beforeTotalFailed)
            Assert.assertEquals("Wrong total error", nscsInstrumentationService.getNumOfErroredWorkflows(), beforeTotalErrored)
            Assert.assertEquals("Wrong total timed-out", nscsInstrumentationService.getNumOfTimedOutWorkflows(), beforeTotalTimedOut)
            Assert.assertEquals("Wrong running", nscsInstrumentationService.getNumOfRunningWorkflows(), beforeRunning + NUM_OF_RUNNING)
            Assert.assertEquals("Wrong pending", nscsInstrumentationService.getNumOfPendingWorkflows(), beforePending + NUM_OF_PENDING)
            for (NscsMonitoredEntityTypes type : NscsMonitoredEntityTypes.values()) {
                Assert.assertEquals("Wrong total instances", nscsInstrumentationService.getNumOfInstances(type), beforeMonitoredEntities.get(TOTAL + type.name()))
                Assert.assertEquals("Wrong successful instances", nscsInstrumentationService.getNumOfSuccessfulInstances(type), beforeMonitoredEntities.get(SUCCESSFUL + type.name()))
                Assert.assertEquals("Wrong average instances", nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(type), beforeMonitoredEntities.get(AVERAGE + type.name()))
                Assert.assertEquals("Wrong unsuccessful instances", nscsInstrumentationService.getNumOfUnsuccessfulInstances(type), beforeMonitoredEntities.get(UNSUCCESSFUL + type.name()))
                Assert.assertEquals("Wrong failed instances", nscsInstrumentationService.getNumOfFailedInstances(type), beforeMonitoredEntities.get(FAILED + type.name()))
                Assert.assertEquals("Wrong error instances", nscsInstrumentationService.getNumOfErroredInstances(type), beforeMonitoredEntities.get(ERROR + type.name()))
                Assert.assertEquals("Wrong timeout instances", nscsInstrumentationService.getNumOfTimedOutInstances(type), beforeMonitoredEntities.get(TIMEOUT + type.name()))
            }
    }

    def 'Given an ISCF service invocation ended with success when reading the NSCS metrics then expected values are returned'() {
        given:
            nscsInstrumentationService.updateSuccessfulIscfServiceInvocations()

        expect:
            Assert.assertEquals("Wrong total successful", nscsInstrumentationService.getNumOfSuccessfulWorkflows(), beforeTotalSuccessful)
            Assert.assertEquals("Wrong total failed", nscsInstrumentationService.getNumOfFailedWorkflows(), beforeTotalFailed)
            Assert.assertEquals("Wrong total error", nscsInstrumentationService.getNumOfErroredWorkflows(), beforeTotalErrored)
            Assert.assertEquals("Wrong total timed-out", nscsInstrumentationService.getNumOfTimedOutWorkflows(), beforeTotalTimedOut)
            Assert.assertEquals("Wrong running", nscsInstrumentationService.getNumOfRunningWorkflows(), beforeRunning)
            Assert.assertEquals("Wrong pending", nscsInstrumentationService.getNumOfPendingWorkflows(), beforePending)
            for (NscsMonitoredEntityTypes type : NscsMonitoredEntityTypes.values()) {
                if (type == NscsMonitoredEntityTypes.ISCF) {
                    Assert.assertEquals("Wrong total instances", nscsInstrumentationService.getNumOfInstances(type), beforeMonitoredEntities.get(TOTAL + type.name()) + 1)
                    Assert.assertEquals("Wrong successful instances", nscsInstrumentationService.getNumOfSuccessfulInstances(type), beforeMonitoredEntities.get(SUCCESSFUL + type.name()) + 1)
                } else {
                    Assert.assertEquals("Wrong total instances", nscsInstrumentationService.getNumOfInstances(type), beforeMonitoredEntities.get(TOTAL + type.name()))
                    Assert.assertEquals("Wrong successful instances", nscsInstrumentationService.getNumOfSuccessfulInstances(type), beforeMonitoredEntities.get(SUCCESSFUL + type.name()))
                }
                Assert.assertEquals("Wrong average instances", nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(type), beforeMonitoredEntities.get(AVERAGE + type.name()))
                Assert.assertEquals("Wrong unsuccessful instances", nscsInstrumentationService.getNumOfUnsuccessfulInstances(type), beforeMonitoredEntities.get(UNSUCCESSFUL + type.name()))
                Assert.assertEquals("Wrong failed instances", nscsInstrumentationService.getNumOfFailedInstances(type), beforeMonitoredEntities.get(FAILED + type.name()))
                Assert.assertEquals("Wrong error instances", nscsInstrumentationService.getNumOfErroredInstances(type), beforeMonitoredEntities.get(ERROR + type.name()))
                Assert.assertEquals("Wrong timeout instances", nscsInstrumentationService.getNumOfTimedOutInstances(type), beforeMonitoredEntities.get(TIMEOUT + type.name()))
            }
    }

    def 'Given an ISCF service invocation ended with failure when reading the NSCS metrics then expected values are returned'() {
        given:
            nscsInstrumentationService.updateFailedIscfServiceInvocations()

        expect:
            Assert.assertEquals("Wrong total successful", nscsInstrumentationService.getNumOfSuccessfulWorkflows(), beforeTotalSuccessful)
            Assert.assertEquals("Wrong total failed", nscsInstrumentationService.getNumOfFailedWorkflows(), beforeTotalFailed)
            Assert.assertEquals("Wrong total error", nscsInstrumentationService.getNumOfErroredWorkflows(), beforeTotalErrored)
            Assert.assertEquals("Wrong total timed-out", nscsInstrumentationService.getNumOfTimedOutWorkflows(), beforeTotalTimedOut)
            Assert.assertEquals("Wrong running", nscsInstrumentationService.getNumOfRunningWorkflows(), beforeRunning)
            Assert.assertEquals("Wrong pending", nscsInstrumentationService.getNumOfPendingWorkflows(), beforePending)
            for (NscsMonitoredEntityTypes type : NscsMonitoredEntityTypes.values()) {
                Assert.assertEquals("Wrong successful instances", nscsInstrumentationService.getNumOfSuccessfulInstances(type), beforeMonitoredEntities.get(SUCCESSFUL + type.name()))
                Assert.assertEquals("Wrong average instances", nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(type), beforeMonitoredEntities.get(AVERAGE + type.name()))
                if (type == NscsMonitoredEntityTypes.ISCF) {
                    Assert.assertEquals("Wrong total instances", nscsInstrumentationService.getNumOfInstances(type), beforeMonitoredEntities.get(TOTAL + type.name()) + 1)
                    Assert.assertEquals("Wrong unsuccessful instances", nscsInstrumentationService.getNumOfUnsuccessfulInstances(type), beforeMonitoredEntities.get(UNSUCCESSFUL + type.name()) + 1)
                    Assert.assertEquals("Wrong failed instances", nscsInstrumentationService.getNumOfFailedInstances(type), beforeMonitoredEntities.get(FAILED + type.name()) + 1)
                } else {
                    Assert.assertEquals("Wrong total instances", nscsInstrumentationService.getNumOfInstances(type), beforeMonitoredEntities.get(TOTAL + type.name()))
                    Assert.assertEquals("Wrong unsuccessful instances", nscsInstrumentationService.getNumOfUnsuccessfulInstances(type), beforeMonitoredEntities.get(UNSUCCESSFUL + type.name()))
                    Assert.assertEquals("Wrong failed instances", nscsInstrumentationService.getNumOfFailedInstances(type), beforeMonitoredEntities.get(FAILED + type.name()))
                }
                Assert.assertEquals("Wrong error instances", nscsInstrumentationService.getNumOfErroredInstances(type), beforeMonitoredEntities.get(ERROR + type.name()))
                Assert.assertEquals("Wrong timeout instances", nscsInstrumentationService.getNumOfTimedOutInstances(type), beforeMonitoredEntities.get(TIMEOUT + type.name()))
            }
    }

    @Unroll
    def 'Given a #workflowName workflow ended with success when reading the NSCS metrics then expected values are returned'(String workflowName, NscsMonitoredEntityTypes monitoredEntityType) {
        given:
            nscsInstrumentationService.updateSuccessfulWorkflow(workflowName, WF_DURATION)

        expect:
            Assert.assertEquals("Wrong total successful", nscsInstrumentationService.getNumOfSuccessfulWorkflows(), beforeTotalSuccessful + 1)
            Assert.assertEquals("Wrong total failed", nscsInstrumentationService.getNumOfFailedWorkflows(), beforeTotalFailed)
            Assert.assertEquals("Wrong total error", nscsInstrumentationService.getNumOfErroredWorkflows(), beforeTotalErrored)
            Assert.assertEquals("Wrong total timed-out", nscsInstrumentationService.getNumOfTimedOutWorkflows(), beforeTotalTimedOut)
            Assert.assertEquals("Wrong running", nscsInstrumentationService.getNumOfRunningWorkflows(), beforeRunning)
            Assert.assertEquals("Wrong pending", nscsInstrumentationService.getNumOfPendingWorkflows(), beforePending)
            for (NscsMonitoredEntityTypes type : NscsMonitoredEntityTypes.values()) {
                def beforeSuccessful = beforeMonitoredEntities.get(SUCCESSFUL + type.name())
                def beforeAverage = beforeMonitoredEntities.get(AVERAGE + type.name())
                def afterSuccessful = nscsInstrumentationService.getNumOfSuccessfulInstances(type)
                def afterAverage = nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(type)
                if (type == monitoredEntityType) {
                    Assert.assertEquals("Wrong total instances", nscsInstrumentationService.getNumOfInstances(type), beforeMonitoredEntities.get(TOTAL + type.name()) + 1)
                    Assert.assertEquals("Wrong successful instances", afterSuccessful, beforeSuccessful + 1)
                    Assert.assertEquals("Wrong average instances", (double)afterAverage, (double)((beforeSuccessful * beforeAverage) + WF_DURATION) / afterSuccessful, 0)
                } else {
                    Assert.assertEquals("Wrong total instances", nscsInstrumentationService.getNumOfInstances(type), beforeMonitoredEntities.get(TOTAL + type.name()))
                    Assert.assertEquals("Wrong successful instances", afterSuccessful, beforeSuccessful)
                    Assert.assertEquals("Wrong average instances", afterAverage, beforeAverage)
                }
                Assert.assertEquals("Wrong unsuccessful instances", nscsInstrumentationService.getNumOfUnsuccessfulInstances(type), beforeMonitoredEntities.get(UNSUCCESSFUL + type.name()))
                Assert.assertEquals("Wrong failed instances", nscsInstrumentationService.getNumOfFailedInstances(type), beforeMonitoredEntities.get(FAILED + type.name()))
                Assert.assertEquals("Wrong error instances", nscsInstrumentationService.getNumOfErroredInstances(type), beforeMonitoredEntities.get(ERROR + type.name()))
                Assert.assertEquals("Wrong timeout instances", nscsInstrumentationService.getNumOfTimedOutInstances(type), beforeMonitoredEntities.get(TIMEOUT + type.name()))
            }

        where:
            [workflowName, monitoredEntityType] << worflowData
    }

    @Unroll
    def 'Given a #workflowName workflow ended with failure when reading the NSCS metrics then expected values are returned'(String workflowName, NscsMonitoredEntityTypes monitoredEntityType) {
        given:
            nscsInstrumentationService.updateFailedWorkflow(workflowName)

        expect:
            Assert.assertEquals("Wrong total successful", nscsInstrumentationService.getNumOfSuccessfulWorkflows(), beforeTotalSuccessful)
            Assert.assertEquals("Wrong total failed", nscsInstrumentationService.getNumOfFailedWorkflows(), beforeTotalFailed + 1)
            Assert.assertEquals("Wrong total error", nscsInstrumentationService.getNumOfErroredWorkflows(), beforeTotalErrored)
            Assert.assertEquals("Wrong total timed-out", nscsInstrumentationService.getNumOfTimedOutWorkflows(), beforeTotalTimedOut)
            Assert.assertEquals("Wrong running", nscsInstrumentationService.getNumOfRunningWorkflows(), beforeRunning)
            Assert.assertEquals("Wrong pending", nscsInstrumentationService.getNumOfPendingWorkflows(), beforePending)
            for (NscsMonitoredEntityTypes type : NscsMonitoredEntityTypes.values()) {
                Assert.assertEquals("Wrong successful instances", nscsInstrumentationService.getNumOfSuccessfulInstances(type), beforeMonitoredEntities.get(SUCCESSFUL + type.name()))
                Assert.assertEquals("Wrong average instances", nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(type), beforeMonitoredEntities.get(AVERAGE + type.name()))
                if (type == monitoredEntityType) {
                    Assert.assertEquals("Wrong total instances", nscsInstrumentationService.getNumOfInstances(type), beforeMonitoredEntities.get(TOTAL + type.name()) + 1)
                    Assert.assertEquals("Wrong unsuccessful instances", nscsInstrumentationService.getNumOfUnsuccessfulInstances(type), beforeMonitoredEntities.get(UNSUCCESSFUL + type.name()) + 1)
                    Assert.assertEquals("Wrong failed instances", nscsInstrumentationService.getNumOfFailedInstances(type), beforeMonitoredEntities.get(FAILED + type.name()) + 1)
                } else {
                    Assert.assertEquals("Wrong total instances", nscsInstrumentationService.getNumOfInstances(type), beforeMonitoredEntities.get(TOTAL + type.name()))
                    Assert.assertEquals("Wrong unsuccessful instances", nscsInstrumentationService.getNumOfUnsuccessfulInstances(type), beforeMonitoredEntities.get(UNSUCCESSFUL + type.name()))
                    Assert.assertEquals("Wrong failed instances", nscsInstrumentationService.getNumOfFailedInstances(type), beforeMonitoredEntities.get(FAILED + type.name()))
                }
                Assert.assertEquals("Wrong error instances", nscsInstrumentationService.getNumOfErroredInstances(type), beforeMonitoredEntities.get(ERROR + type.name()))
                Assert.assertEquals("Wrong timeout instances", nscsInstrumentationService.getNumOfTimedOutInstances(type), beforeMonitoredEntities.get(TIMEOUT + type.name()))
            }

        where:
            [workflowName, monitoredEntityType] << worflowData
    }

    @Unroll
    def 'Given a #workflowName workflow ended with error when reading the NSCS metrics then expected values are returned'(String workflowName, NscsMonitoredEntityTypes monitoredEntityType) {
        given:
            nscsInstrumentationService.updateErroredWorkflow(workflowName)

        expect:
            Assert.assertEquals("Wrong total successful", nscsInstrumentationService.getNumOfSuccessfulWorkflows(), beforeTotalSuccessful)
            Assert.assertEquals("Wrong total failed", nscsInstrumentationService.getNumOfFailedWorkflows(), beforeTotalFailed)
            Assert.assertEquals("Wrong total error", nscsInstrumentationService.getNumOfErroredWorkflows(), beforeTotalErrored + 1)
            Assert.assertEquals("Wrong total timed-out", nscsInstrumentationService.getNumOfTimedOutWorkflows(), beforeTotalTimedOut)
            Assert.assertEquals("Wrong running", nscsInstrumentationService.getNumOfRunningWorkflows(), beforeRunning)
            Assert.assertEquals("Wrong pending", nscsInstrumentationService.getNumOfPendingWorkflows(), beforePending)
            for (NscsMonitoredEntityTypes type : NscsMonitoredEntityTypes.values()) {
                Assert.assertEquals("Wrong successful instances", nscsInstrumentationService.getNumOfSuccessfulInstances(type), beforeMonitoredEntities.get(SUCCESSFUL + type.name()))
                Assert.assertEquals("Wrong average instances", nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(type), beforeMonitoredEntities.get(AVERAGE + type.name()))
                Assert.assertEquals("Wrong failed instances", nscsInstrumentationService.getNumOfFailedInstances(type), beforeMonitoredEntities.get(FAILED + type.name()))
                if (type == monitoredEntityType) {
                    Assert.assertEquals("Wrong total instances", nscsInstrumentationService.getNumOfInstances(type), beforeMonitoredEntities.get(TOTAL + type.name()) + 1)
                    Assert.assertEquals("Wrong unsuccessful instances", nscsInstrumentationService.getNumOfUnsuccessfulInstances(type), beforeMonitoredEntities.get(UNSUCCESSFUL + type.name()) + 1)
                    Assert.assertEquals("Wrong error instances", nscsInstrumentationService.getNumOfErroredInstances(type), beforeMonitoredEntities.get(ERROR + type.name()) + 1)
                } else {
                    Assert.assertEquals("Wrong total instances", nscsInstrumentationService.getNumOfInstances(type), beforeMonitoredEntities.get(TOTAL + type.name()))
                    Assert.assertEquals("Wrong unsuccessful instances", nscsInstrumentationService.getNumOfUnsuccessfulInstances(type), beforeMonitoredEntities.get(UNSUCCESSFUL + type.name()))
                    Assert.assertEquals("Wrong error instances", nscsInstrumentationService.getNumOfErroredInstances(type), beforeMonitoredEntities.get(ERROR + type.name()))
                }
                Assert.assertEquals("Wrong timeout instances", nscsInstrumentationService.getNumOfTimedOutInstances(type), beforeMonitoredEntities.get(TIMEOUT + type.name()))
            }

        where:
            [workflowName, monitoredEntityType] << worflowData
    }

    @Unroll
    def 'Given a #workflowName workflow ended with timeout when reading the NSCS metrics then expected values are returned'(String workflowName, NscsMonitoredEntityTypes monitoredEntityType) {
        given:
            nscsInstrumentationService.updateTimedOutWorkflow(workflowName)

        expect:
            Assert.assertEquals("Wrong total successful", nscsInstrumentationService.getNumOfSuccessfulWorkflows(), beforeTotalSuccessful)
            Assert.assertEquals("Wrong total failed", nscsInstrumentationService.getNumOfFailedWorkflows(), beforeTotalFailed)
            Assert.assertEquals("Wrong total error", nscsInstrumentationService.getNumOfErroredWorkflows(), beforeTotalErrored)
            Assert.assertEquals("Wrong total timed-out", nscsInstrumentationService.getNumOfTimedOutWorkflows(), beforeTotalTimedOut + 1)
            Assert.assertEquals("Wrong running", nscsInstrumentationService.getNumOfRunningWorkflows(), beforeRunning)
            Assert.assertEquals("Wrong pending", nscsInstrumentationService.getNumOfPendingWorkflows(), beforePending)
            for (NscsMonitoredEntityTypes type : NscsMonitoredEntityTypes.values()) {
                Assert.assertEquals("Wrong successful instances", nscsInstrumentationService.getNumOfSuccessfulInstances(type), beforeMonitoredEntities.get(SUCCESSFUL + type.name()))
                Assert.assertEquals("Wrong average instances", nscsInstrumentationService.getAverageDurationOfSuccessfulInstances(type), beforeMonitoredEntities.get(AVERAGE + type.name()))
                Assert.assertEquals("Wrong failed instances", nscsInstrumentationService.getNumOfFailedInstances(type), beforeMonitoredEntities.get(FAILED + type.name()))
                Assert.assertEquals("Wrong error instances", nscsInstrumentationService.getNumOfErroredInstances(type), beforeMonitoredEntities.get(ERROR + type.name()))
                if (type == monitoredEntityType) {
                    Assert.assertEquals("Wrong total instances", nscsInstrumentationService.getNumOfInstances(type), beforeMonitoredEntities.get(TOTAL + type.name()) + 1)
                    Assert.assertEquals("Wrong unsuccessful instances", nscsInstrumentationService.getNumOfUnsuccessfulInstances(type), beforeMonitoredEntities.get(UNSUCCESSFUL + type.name()) + 1)
                    Assert.assertEquals("Wrong timeout instances", nscsInstrumentationService.getNumOfTimedOutInstances(type), beforeMonitoredEntities.get(TIMEOUT + type.name()) + 1)
                } else {
                    Assert.assertEquals("Wrong total instances", nscsInstrumentationService.getNumOfInstances(type), beforeMonitoredEntities.get(TOTAL + type.name()))
                    Assert.assertEquals("Wrong unsuccessful instances", nscsInstrumentationService.getNumOfUnsuccessfulInstances(type), beforeMonitoredEntities.get(UNSUCCESSFUL + type.name()))
                    Assert.assertEquals("Wrong timeout instances", nscsInstrumentationService.getNumOfTimedOutInstances(type), beforeMonitoredEntities.get(TIMEOUT + type.name()))
                }
            }

        where:
            [workflowName, monitoredEntityType] << worflowData
    }
}
