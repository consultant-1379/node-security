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
package com.ericsson.oss.services.nscs.workflow.tasks.ejb

import java.security.cert.CertificateException

import javax.enterprise.context.spi.CreationalContext
import javax.enterprise.inject.spi.Bean
import javax.enterprise.inject.spi.BeanManager
import javax.inject.Inject
import javax.validation.Validator

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.CACertSftpPublisher
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException
import com.ericsson.nms.security.nscs.handler.validation.impl.NtpValidator
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface
import com.ericsson.nms.security.nscs.workflow.task.comecim.attribute.ComEcimCheckEnrollmentProtocolTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.comecim.attribute.ComEcimCheckIsExternalCATaskHandler
import com.ericsson.nms.security.nscs.workflow.task.comecim.attribute.ComEcimDeleteNtpSecPolicyAndNtpServerMoTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.comecim.attribute.ComEcimLdapCommonConfigTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.comecim.attribute.ComEcimNodeSpecificLdapConfigTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.comecim.attribute.ComEcimNtpConfigureDetailsTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.comecim.attribute.ComEcimPrepareExternalCaEnrollmentInfomationTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.comecim.attribute.ComEcimPrepareInstallTrustedCertTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.comecim.attribute.ComEcimPrepareOnDemandCrlDownloadActionTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.comecim.attribute.ComEcimPrepareStartOnlineEnrollmentTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.comecim.attribute.PrepareExternalCATrustedEntityInfoTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.comecim.attribute.PrepareInternalCATrustedEntityInfoTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.comecim.moaction.ComEcimActivateFtpesOnNodeTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.comecim.moaction.ComEcimDeactivateFtpesOnNodeTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.comecim.moaction.ComEcimRemoveTrustTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.comecim.node.validation.ComEcimValidateNodeForNtpRemoveTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.cpp.attribute.CheckNodeSyncStatusTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.cpp.attribute.CheckTrustedOAMAlreadyInstalledTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.cpp.attribute.CppCheckAndRemoveNTPServerTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.cpp.attribute.CppCheckKeysRemovedTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.cpp.attribute.CppCheckNtpKeysInstalledTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.cpp.attribute.CppConfigureNtpServerTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.cpp.attribute.CppGetNtpKeyDataTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.cpp.attribute.CppInstallNtpKeysTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.cpp.attribute.CppRemoveNtpKeyDataMappingTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.cpp.attribute.GetLaadFilesTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.cpp.attribute.NtpConfigureFailureStatusSenderTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.cpp.attribute.ReadLaadInstallationFailureTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.cpp.attribute.TestCheckSomethingTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.cpp.attribute.ValidateNodeForNtpRemoveTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.cpp.moaction.CancelInstallLocalAADatabaseTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.cpp.moaction.InstallLocalAADatabaseTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.cpp.moaction.RemoveNtpKeysTaskHandlers
import com.ericsson.nms.security.nscs.workflow.task.cpp.moaction.RemoveTrustNewIPSECTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.cpp.moaction.RemoveTrustOAMTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.cpp.moaction.RtselDeleteServerTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.cpp.moaction.TestDoSomethingTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.cpp.ssh.GetHttpsStatusOverCliTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.log.LogErrorTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.log.LogFailureTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.log.LogSuccessTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.log.LogTimeoutTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.node.attribute.CheckMoActionProgressTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.node.attribute.PerformMoActionTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.pki.RevokeNodeCertificateTaskHandler
import com.ericsson.nms.security.nscs.workflow.task.proto.ProtoTaskHandler
import com.ericsson.oss.itpf.sdk.security.accesscontrol.classic.EAccessControlBean
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType

import spock.lang.Unroll

class WorkflowTaskServiceBeanTest extends CdiSpecification {

    @MockedImplementation
    EAccessControlBean eAccessControl

    @ObjectUnderTest
    WorkflowTaskServiceBean workflowTaskServiceBean

    // Actions
    @Inject
    CancelInstallLocalAADatabaseTaskHandler cancelInstallLocalAADatabaseTaskHandler

    @Inject
    ComEcimActivateFtpesOnNodeTaskHandler comEcimActivateFtpesOnNodeTaskHandler

    @Inject
    ComEcimDeactivateFtpesOnNodeTaskHandler comEcimDeactivateFtpesOnNodeTaskHandler

    @Inject
    GetHttpsStatusOverCliTaskHandler getHttpsStatusOverCliTaskHandler

    @Inject
    InstallLocalAADatabaseTaskHandler installLocalAADatabaseTaskHandler

    @Inject
    LogErrorTaskHandler logErrorTaskHandler

    @Inject
    LogFailureTaskHandler logFailureTaskHandler

    @Inject
    LogSuccessTaskHandler logSuccessTaskHandler

    @Inject
    LogTimeoutTaskHandler logTimeoutTaskHandler

    @Inject
    RtselDeleteServerTaskHandler rtselDeleteServerTaskHandler

    @Inject
    TestDoSomethingTaskHandler testDoSomethingTaskHandler

    @Inject
    ProtoTaskHandler protoTaskHandler

    // Queries
    @Inject
    CheckMoActionProgressTaskHandler checkMoActionProgressTaskHandler

    @Inject
    CheckNodeSyncStatusTaskHandler checkNodeSyncStatusTaskHandler

    @Inject
    CheckTrustedOAMAlreadyInstalledTaskHandler checkTrustedOAMAlreadyInstalledTaskHandler

    @Inject
    ComEcimCheckEnrollmentProtocolTaskHandler comEcimCheckEnrollmentProtocolTaskHandler

    @Inject
    ComEcimCheckIsExternalCATaskHandler comEcimCheckIsExternalCATaskHandler

    @Inject
    ComEcimDeleteNtpSecPolicyAndNtpServerMoTaskHandler comEcimDeleteNtpSecPolicyAndNtpServerMoTaskHandler

    @Inject
    ComEcimLdapCommonConfigTaskHandler comEcimLdapCommonConfigTaskHandler

    @Inject
    ComEcimNodeSpecificLdapConfigTaskHandler comEcimNodeSpecificLdapConfigTaskHandler

    @Inject
    ComEcimNtpConfigureDetailsTaskHandler comEcimNtpConfigureDetailsTaskHandler

    @Inject
    ComEcimPrepareExternalCaEnrollmentInfomationTaskHandler comEcimPrepareExternalCaEnrollmentInfomationTaskHandler

    @Inject
    ComEcimPrepareInstallTrustedCertTaskHandler comEcimPrepareInstallTrustedCertTaskHandler

    @Inject
    ComEcimPrepareOnDemandCrlDownloadActionTaskHandler comEcimPrepareOnDemandCrlDownloadActionTaskHandler

    @Inject
    ComEcimPrepareStartOnlineEnrollmentTaskHandler comEcimPrepareStartOnlineEnrollmentTaskHandler

    @Inject
    ComEcimRemoveTrustTaskHandler comEcimRemoveTrustTaskHandler

    @Inject
    ComEcimValidateNodeForNtpRemoveTaskHandler comEcimValidateNodeForNtpRemoveTaskHandler

    @Inject
    CppCheckAndRemoveNTPServerTaskHandler cppCheckAndRemoveNTPServerTaskHandler

    @Inject
    CppCheckKeysRemovedTaskHandler cppCheckKeysRemovedTaskHandler

    @Inject
    CppCheckNtpKeysInstalledTaskHandler cppCheckNtpKeysInstalledTaskHandler

    @MockedImplementation
    NtpValidator ntpValidator

    @Inject
    CppConfigureNtpServerTaskHandler cppConfigureNtpServerTaskHandler

    @Inject
    CppGetNtpKeyDataTaskHandler cppGetNtpKeyDataTaskHandler

    @Inject
    CppInstallNtpKeysTaskHandler cppInstallNtpKeysTaskHandler

    @Inject
    CppRemoveNtpKeyDataMappingTaskHandler cppRemoveNtpKeyDataMappingTaskHandler

    @Inject
    GetLaadFilesTaskHandler getLaadFilesTaskHandler

    @Inject
    NtpConfigureFailureStatusSenderTaskHandler ntpConfigureFailureStatusSenderTaskHandler

    @Inject
    PerformMoActionTaskHandler performMoActionTaskHandler

    @Inject
    PrepareExternalCATrustedEntityInfoTaskHandler prepareExternalCATrustedEntityInfoTaskHandler

    @Inject
    PrepareInternalCATrustedEntityInfoTaskHandler prepareInternalCATrustedEntityInfoTaskHandler

    @Inject
    ReadLaadInstallationFailureTaskHandler readLaadInstallationFailureTaskHandler

    @Inject
    RemoveNtpKeysTaskHandlers removeNtpKeysTaskHandlers

    @Inject
    RemoveTrustNewIPSECTaskHandler removeTrustNewIPSECTaskHandler

    @Inject
    RemoveTrustOAMTaskHandler removeTrustOAMTaskHandler

    @Inject
    RevokeNodeCertificateTaskHandler revokeNodeCertificateTaskHandler

    @Inject
    TestCheckSomethingTaskHandler testCheckSomethingTaskHandler

    @Inject
    ValidateNodeForNtpRemoveTaskHandler validateNodeForNtpRemoveTaskHandler

    @MockedImplementation
    Bean<?> bean;

    @MockedImplementation
    BeanManager beanManager;

    @MockedImplementation
    CreationalContext creationalContext;

    @MockedImplementation
    Validator validator

    @MockedImplementation
    CACertSftpPublisher caCertSftpPublisher

    @Unroll
    def "process action task #taskType with expected #exception" () {
        given:
        def fdn = "MeContext=NODE"
        def WorkflowActionTask task = new WorkflowActionTask(taskType, fdn)
        beanManager.getBeans(WFTaskHandlerInterface.class, _) >> [bean]
        beanManager.getBeans(_) >> [bean]
        beanManager.createCreationalContext(_) >> creationalContext
        beanManager.getReference(_,WFTaskHandlerInterface.class,_) >> getActionTaskHandler(taskType)
        validator.validate(_) >> []
        when:
        workflowTaskServiceBean.processTask(task)
        then:
        thrown(exception)
        where:
        taskType                                                    || exception
        WorkflowTaskType.CPP_CANCEL_INSTALL_LAAD_FAILURE            || IllegalStateException
        WorkflowTaskType.COM_ACTIVATE_FTPES                         || IllegalStateException
        WorkflowTaskType.COM_DEACTIVATE_FTPES                       || IllegalStateException
        WorkflowTaskType.GET_HTTPS_STATUS_CLI                       || IllegalStateException
        WorkflowTaskType.CPP_INSTALL_LAAD_ACTION                    || IllegalStateException
        WorkflowTaskType.LOG_ERROR                                  || IllegalStateException
        WorkflowTaskType.LOG_FAILURE                                || IllegalStateException
        WorkflowTaskType.LOG_SUCCESS                                || IllegalStateException
        WorkflowTaskType.LOG_TIMEOUT                                || IllegalStateException
        WorkflowTaskType.RTSEL_DELETE_SERVER                        || IllegalStateException
        WorkflowTaskType.TEST_DO_SOMETHING                          || IllegalStateException
        WorkflowTaskType.PROTOTYPE_TASK                             || IllegalStateException
    }

    def "process action task ProtoTask with CertificateException" () {
        given:
        def fdn = "MeContext=NODE"
        def WorkflowActionTask task = new WorkflowActionTask(WorkflowTaskType.PROTOTYPE_TASK, fdn)
        beanManager.getBeans(WFTaskHandlerInterface.class, _) >> [bean]
        beanManager.getBeans(_) >> [bean]
        beanManager.createCreationalContext(_) >> creationalContext
        beanManager.getReference(_,WFTaskHandlerInterface.class,_) >> protoTaskHandler
        validator.validate(_) >> []
        caCertSftpPublisher.publishCertificates(_, _) >> {throw new CertificateException()}
        when:
        workflowTaskServiceBean.processTask(task)
        then:
        thrown(IllegalStateException)
    }

    def "process action task ProtoTask with NscsPkiEntitiesManagerException" () {
        given:
        def fdn = "MeContext=NODE"
        def WorkflowActionTask task = new WorkflowActionTask(WorkflowTaskType.PROTOTYPE_TASK, fdn)
        beanManager.getBeans(WFTaskHandlerInterface.class, _) >> [bean]
        beanManager.getBeans(_) >> [bean]
        beanManager.createCreationalContext(_) >> creationalContext
        beanManager.getReference(_,WFTaskHandlerInterface.class,_) >> protoTaskHandler
        validator.validate(_) >> []
        caCertSftpPublisher.publishCertificates(_, _) >> {throw new NscsPkiEntitiesManagerException()}
        when:
        workflowTaskServiceBean.processTask(task)
        then:
        thrown(IllegalStateException)
    }

    @Unroll
    def "process query task with expected #exception" () {
        given:
        def fdn = "MeContext=NODE"
        def WorkflowQueryTask task = new WorkflowQueryTask(taskType, fdn)
        beanManager.getBeans(WFTaskHandlerInterface.class, _) >> [bean]
        beanManager.getBeans(_) >> [bean]
        beanManager.createCreationalContext(_) >> creationalContext
        beanManager.getReference(_,WFTaskHandlerInterface.class,_) >> getQueryTaskHandler(taskType)
        validator.validate(_) >> []
        when:
        def result = workflowTaskServiceBean.processTask(task)
        then:
        thrown(exception)
        where:
        taskType                                                          || exception
        WorkflowTaskType.CHECK_MO_ACTION_PROGRESS                         || IllegalStateException
        WorkflowTaskType.CHECK_NODE_SYNC_STATUS                           || IllegalStateException
        WorkflowTaskType.CPP_CHECK_TRUSTED_OAM_ALREADY_INSTALLED          || IllegalStateException
        WorkflowTaskType.COM_ECIM_CHECK_ENROLLMENT_PROTOCOL               || IllegalStateException
        WorkflowTaskType.COM_ECIM_CHECK_IS_EXTERNAL_CA                    || IllegalStateException
        WorkflowTaskType.COM_ECIM_REMOVE_NTP_SEC_POLICY_AND_NTP_SERVER_MO || IllegalStateException
        WorkflowTaskType.COM_ECIM_GET_LDAP_COMMON_CONFIG                  || IllegalStateException
        WorkflowTaskType.COM_ECIM_GET_LDAP_NODE_CONFIG                    || IllegalStateException
        WorkflowTaskType.COM_ECIM_NTP_CONFIGURE                           || IllegalStateException
        WorkflowTaskType.COM_ECIM_PREPARE_EXT_CA_ENROLLMENT_INFO          || IllegalStateException
        WorkflowTaskType.COM_ECIM_PREPARE_INSTALL_TRUSTED_CERT            || IllegalStateException
        WorkflowTaskType.COM_ECIM_PREPARE_ON_DEMAND_CRL_DOWNLOAD          || IllegalStateException
        WorkflowTaskType.COM_ECIM_PREPARE_START_ONLINE_ENROLLMENT         || IllegalStateException
        WorkflowTaskType.COM_ECIM_VALIDATE_NODE_FOR_NTP_REMOVE            || IllegalStateException
        WorkflowTaskType.CPP_CHECK_AND_REMOVE_NTP_SERVER                  || IllegalStateException
        WorkflowTaskType.CPP_CHECK_KEYS_REMOVED                           || IllegalStateException
        WorkflowTaskType.CPP_CHECK_NTP_KEYS_INSTALLED                     || IllegalStateException
        WorkflowTaskType.CPP_CONFIGURE_NTP_SERVER                         || IllegalStateException
        WorkflowTaskType.CPP_GET_NTP_KEY_DATA                             || IllegalStateException
        WorkflowTaskType.CPP_INSTALL_NTP_KEYS                             || IllegalStateException
        WorkflowTaskType.CPP_REMOVE_NTP_KEY_DATA_MAPPING                  || IllegalStateException
        WorkflowTaskType.NTP_CONFIGURE_FAILURE_STATUS_SENDER              || IllegalStateException
        WorkflowTaskType.PERFORM_MO_ACTION                                || IllegalStateException
        WorkflowTaskType.PREPARE_EXTERNAL_CA_TRUSTED_ENTITY_INFO          || IllegalStateException
        WorkflowTaskType.PREPARE_INTERNAL_CA_TRUSTED_ENTITY_INFO          || IllegalStateException
        WorkflowTaskType.CPP_READ_LAAD_INSTALLATION_FAILURE               || IllegalStateException
        WorkflowTaskType.REMOVE_NTP_KEYS                                  || IllegalStateException
        WorkflowTaskType.CPP_REMOVE_TRUST_NEW_IPSEC                       || IllegalStateException
        WorkflowTaskType.CPP_REMOVE_TRUST_OAM                             || IllegalStateException
        WorkflowTaskType.REVOKE_NODE_CERTIFICATE                          || IllegalStateException
        WorkflowTaskType.TEST_CHECK_SOMETHING                             || IllegalStateException
        WorkflowTaskType.VALIDATE_NODE_FOR_NTP_REMOVE                     || IllegalStateException
        WorkflowTaskType.CPP_GET_LAAD_FILES                               || IllegalStateException
    }

    private WFActionTaskHandler<?> getActionTaskHandler (WorkflowTaskType taskType) {
        if (WorkflowTaskType.CPP_CANCEL_INSTALL_LAAD_FAILURE.equals(taskType)) {
            return cancelInstallLocalAADatabaseTaskHandler
        } else if (WorkflowTaskType.COM_ACTIVATE_FTPES.equals(taskType)) {
            return comEcimActivateFtpesOnNodeTaskHandler
        } else if (WorkflowTaskType.COM_DEACTIVATE_FTPES.equals(taskType)) {
            return comEcimDeactivateFtpesOnNodeTaskHandler
        } else if (WorkflowTaskType.GET_HTTPS_STATUS_CLI.equals(taskType)) {
            return getHttpsStatusOverCliTaskHandler
        } else if (WorkflowTaskType.CPP_INSTALL_LAAD_ACTION.equals(taskType)) {
            return installLocalAADatabaseTaskHandler
        } else if (WorkflowTaskType.LOG_ERROR.equals(taskType)) {
            return logErrorTaskHandler
        } else if (WorkflowTaskType.LOG_FAILURE.equals(taskType)) {
            return logFailureTaskHandler
        } else if (WorkflowTaskType.LOG_SUCCESS.equals(taskType)) {
            return logSuccessTaskHandler
        } else if (WorkflowTaskType.LOG_TIMEOUT.equals(taskType)) {
            return logTimeoutTaskHandler
        } else if (WorkflowTaskType.RTSEL_DELETE_SERVER.equals(taskType)) {
            return rtselDeleteServerTaskHandler
        } else if (WorkflowTaskType.TEST_DO_SOMETHING.equals(taskType)) {
            return testDoSomethingTaskHandler
        } else if (WorkflowTaskType.PROTOTYPE_TASK.equals(taskType)) {
            return protoTaskHandler
        } else {
            return null;
        }
    }

    private WFQueryTaskHandler<?> getQueryTaskHandler (WorkflowTaskType taskType) {
        if (WorkflowTaskType.CHECK_MO_ACTION_PROGRESS.equals(taskType)) {
            return checkMoActionProgressTaskHandler
        } else if (WorkflowTaskType.CHECK_NODE_SYNC_STATUS.equals(taskType)) {
            return checkNodeSyncStatusTaskHandler
        } else if (WorkflowTaskType.CPP_CHECK_TRUSTED_OAM_ALREADY_INSTALLED.equals(taskType)) {
            return checkTrustedOAMAlreadyInstalledTaskHandler
        } else if (WorkflowTaskType.COM_ECIM_CHECK_ENROLLMENT_PROTOCOL.equals(taskType)) {
            return comEcimCheckEnrollmentProtocolTaskHandler
        } else if (WorkflowTaskType.COM_ECIM_CHECK_IS_EXTERNAL_CA.equals(taskType)) {
            return comEcimCheckIsExternalCATaskHandler
        } else if (WorkflowTaskType.COM_ECIM_REMOVE_NTP_SEC_POLICY_AND_NTP_SERVER_MO.equals(taskType)) {
            return comEcimDeleteNtpSecPolicyAndNtpServerMoTaskHandler
        } else if (WorkflowTaskType.COM_ECIM_GET_LDAP_COMMON_CONFIG.equals(taskType)) {
            return comEcimLdapCommonConfigTaskHandler
        } else if (WorkflowTaskType.COM_ECIM_GET_LDAP_NODE_CONFIG.equals(taskType)) {
            return comEcimNodeSpecificLdapConfigTaskHandler
        } else if (WorkflowTaskType.COM_ECIM_NTP_CONFIGURE.equals(taskType)) {
            return comEcimNtpConfigureDetailsTaskHandler
        } else if (WorkflowTaskType.COM_ECIM_PREPARE_EXT_CA_ENROLLMENT_INFO.equals(taskType)) {
            return comEcimPrepareExternalCaEnrollmentInfomationTaskHandler
        } else if (WorkflowTaskType.COM_ECIM_PREPARE_INSTALL_TRUSTED_CERT.equals(taskType)) {
            return comEcimPrepareInstallTrustedCertTaskHandler
        } else if (WorkflowTaskType.COM_ECIM_PREPARE_ON_DEMAND_CRL_DOWNLOAD.equals(taskType)) {
            return comEcimPrepareOnDemandCrlDownloadActionTaskHandler
        } else if (WorkflowTaskType.COM_ECIM_PREPARE_START_ONLINE_ENROLLMENT.equals(taskType)) {
            return comEcimPrepareStartOnlineEnrollmentTaskHandler
        } else if (WorkflowTaskType.COM_ECIM_REMOVE_TRUST.equals(taskType)) {
            return comEcimRemoveTrustTaskHandler
        } else if (WorkflowTaskType.COM_ECIM_VALIDATE_NODE_FOR_NTP_REMOVE.equals(taskType)) {
            return comEcimValidateNodeForNtpRemoveTaskHandler
        } else if (WorkflowTaskType.CPP_CHECK_AND_REMOVE_NTP_SERVER.equals(taskType)) {
            return cppCheckAndRemoveNTPServerTaskHandler
        } else if (WorkflowTaskType.CPP_CHECK_KEYS_REMOVED.equals(taskType)) {
            return cppCheckKeysRemovedTaskHandler
        } else if (WorkflowTaskType.CPP_CHECK_NTP_KEYS_INSTALLED.equals(taskType)) {
            return cppCheckNtpKeysInstalledTaskHandler
        } else if (WorkflowTaskType.CPP_CONFIGURE_NTP_SERVER.equals(taskType)) {
            return cppConfigureNtpServerTaskHandler
        } else if (WorkflowTaskType.CPP_GET_NTP_KEY_DATA.equals(taskType)) {
            return cppGetNtpKeyDataTaskHandler
        } else if (WorkflowTaskType.CPP_INSTALL_NTP_KEYS.equals(taskType)) {
            return cppInstallNtpKeysTaskHandler
        } else if (WorkflowTaskType.CPP_REMOVE_NTP_KEY_DATA_MAPPING.equals(taskType)) {
            return cppRemoveNtpKeyDataMappingTaskHandler
        } else if (WorkflowTaskType.NTP_CONFIGURE_FAILURE_STATUS_SENDER.equals(taskType)) {
            return ntpConfigureFailureStatusSenderTaskHandler
        } else if (WorkflowTaskType.PERFORM_MO_ACTION.equals(taskType)) {
            return performMoActionTaskHandler
        } else if (WorkflowTaskType.PREPARE_EXTERNAL_CA_TRUSTED_ENTITY_INFO.equals(taskType)) {
            return prepareExternalCATrustedEntityInfoTaskHandler
        } else if (WorkflowTaskType.PREPARE_INTERNAL_CA_TRUSTED_ENTITY_INFO.equals(taskType)) {
            return prepareInternalCATrustedEntityInfoTaskHandler
        } else if (WorkflowTaskType.CPP_READ_LAAD_INSTALLATION_FAILURE.equals(taskType)) {
            return readLaadInstallationFailureTaskHandler
        } else if (WorkflowTaskType.REMOVE_NTP_KEYS.equals(taskType)) {
            return removeNtpKeysTaskHandlers
        } else if (WorkflowTaskType.CPP_REMOVE_TRUST_NEW_IPSEC.equals(taskType)) {
            return removeTrustNewIPSECTaskHandler
        } else if (WorkflowTaskType.CPP_REMOVE_TRUST_OAM.equals(taskType)) {
            return removeTrustOAMTaskHandler
        } else if (WorkflowTaskType.REVOKE_NODE_CERTIFICATE.equals(taskType)) {
            return revokeNodeCertificateTaskHandler
        } else if (WorkflowTaskType.TEST_CHECK_SOMETHING.equals(taskType)) {
            return testCheckSomethingTaskHandler
        } else if (WorkflowTaskType.VALIDATE_NODE_FOR_NTP_REMOVE.equals(taskType)) {
            return validateNodeForNtpRemoveTaskHandler
        } else if (WorkflowTaskType.CPP_GET_LAAD_FILES.equals(taskType)) {
            return getLaadFilesTaskHandler
        } else {
            return null;
        }
    }
}
