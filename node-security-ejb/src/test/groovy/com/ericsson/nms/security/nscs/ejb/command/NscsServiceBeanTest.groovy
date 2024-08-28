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
package com.ericsson.nms.security.nscs.ejb.command

import javax.enterprise.context.spi.CreationalContext
import javax.enterprise.inject.spi.Bean
import javax.enterprise.inject.spi.BeanManager
import javax.inject.Inject

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsCliCommand
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse.NscsCommandResponseType
import com.ericsson.nms.security.nscs.api.command.NscsCommandType
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand.NscsPropertyCommandInvoker
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException
import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException
import com.ericsson.nms.security.nscs.api.exception.NscsSecurityViolationException
import com.ericsson.nms.security.nscs.api.exception.TestWfsException
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.ejb.command.rbac.RbacAuthorizationManager
import com.ericsson.nms.security.nscs.handler.command.CommandHandler
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface
import com.ericsson.nms.security.nscs.handler.command.impl.ActivateFtpesHandler
import com.ericsson.nms.security.nscs.handler.command.impl.ActivateHttpsHandler
import com.ericsson.nms.security.nscs.handler.command.impl.CppInstallLaadHandler
import com.ericsson.nms.security.nscs.handler.command.impl.CppIpSecHandler
import com.ericsson.nms.security.nscs.handler.command.impl.CppSetSecurityLevelHandler
import com.ericsson.nms.security.nscs.handler.command.impl.DeactivateFtpesHandler
import com.ericsson.nms.security.nscs.handler.command.impl.DeactivateHttpsHandler
import com.ericsson.nms.security.nscs.handler.command.impl.DisableSsoHandler
import com.ericsson.nms.security.nscs.handler.command.impl.EnableSsoHandler
import com.ericsson.nms.security.nscs.handler.command.impl.GetNodeSpecificPasswordHandler
import com.ericsson.nms.security.nscs.handler.command.impl.GetSsoHandler
import com.ericsson.nms.security.nscs.handler.command.impl.SetEnrollmentModeHandler
import com.ericsson.nms.security.nscs.handler.command.impl.TestCommandHandler
import com.ericsson.nms.security.nscs.handler.validation.CommandValidator
import com.ericsson.nms.security.nscs.handler.validation.CommandValidatorInterface
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.nms.security.nscs.logger.NscsSystemRecorder
import com.ericsson.nms.security.nscs.parser.NscsCliCommandParser
import com.ericsson.nms.security.nscs.util.FileUtil
import com.ericsson.oss.itpf.sdk.security.accesscontrol.SecurityViolationException
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse
import com.ericsson.oss.services.dto.JobStatusRecord
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler

import spock.lang.Unroll

class NscsServiceBeanTest extends CdiSpecification {

    @ObjectUnderTest
    NscsServiceBean nscsServiceBean

    @Inject
    TestCommandHandler testCommandHandler

    @Inject
    ActivateFtpesHandler activateFtpesHandler

    @Inject
    DeactivateFtpesHandler deactivateFtpesHandler

    @Inject
    ActivateHttpsHandler activateHttpsHandler

    @Inject
    DeactivateHttpsHandler deactivateHttpsHandler

    @Inject
    CppInstallLaadHandler cppInstallLaadHandler

    @MockedImplementation
    FileUtil fileUtil

    @Inject
    CppIpSecHandler cppIpSecHandler

    @Inject
    CppSetSecurityLevelHandler cppSetSecurityLevelHandler

    @Inject
    EnableSsoHandler enableSsoHandler

    @Inject
    DisableSsoHandler disableSsoHandler

    @Inject
    GetSsoHandler getSsoHandler

    @Inject
    GetNodeSpecificPasswordHandler getNodeSpecificPasswordHandler

    @Inject
    SetEnrollmentModeHandler setEnrollmentModeHandler

    @MockedImplementation
    NscsCliCommandParser commandParser

    @MockedImplementation
    NscsPropertyCommand nscsPropertyCommand

    @MockedImplementation
    Bean<?> bean;

    @MockedImplementation
    BeanManager beanManager;

    @MockedImplementation
    CreationalContext creationalContext;

    @MockedImplementation
    NscsJobCacheHandler nscsJobCacheHandler

    @MockedImplementation
    CommandValidator commandValidator

    @MockedImplementation
    NscsCMReaderService nscsCMReaderService

    @MockedImplementation
    private NscsSystemRecorder nscsSystemRecorder

    @MockedImplementation
    private NscsLogger nscsLogger

    @MockedImplementation
    private RbacAuthorizationManager rbacAuthorizationManager;

    def "process command with syntax error"() {
        given:
        def NscsCommandType nscsCommandType = NscsCommandType.TEST_COMMAND
        def NscsCliCommand nscsCliCommand = new NscsCliCommand("secadm test", ["invalid" : 1])
        nscsPropertyCommand.getCommandType() >> nscsCommandType
        nscsPropertyCommand.getCommandInvokerValue() >> NscsPropertyCommandInvoker.CLI
        nscsPropertyCommand.getProperties() >> ["invalid" : 1]
        commandParser.parseCommand(nscsCliCommand) >> { throw new CommandSyntaxException() }
        when:
        def NscsCommandResponse response = nscsServiceBean.processCommand(nscsCliCommand)
        then:
        1 * nscsServiceBean.logger.commandStarted("secadm ", null)
        1 * nscsServiceBean.logger.commandFinishedWithError("secadm ", null, "Command syntax error")
        thrown(CommandSyntaxException.class)
    }

    def "process command with security violation"() {
        given:
        def NscsCommandType nscsCommandType = NscsCommandType.TEST_COMMAND
        def NscsCliCommand nscsCliCommand = new NscsCliCommand("secadm test", ["invalid" : 1])
        nscsPropertyCommand.getCommandType() >> nscsCommandType
        nscsPropertyCommand.getCommandInvokerValue() >> NscsPropertyCommandInvoker.CLI
        nscsPropertyCommand.getProperties() >> ["invalid" : 1]
        commandParser.parseCommand(nscsCliCommand) >> nscsPropertyCommand
        beanManager.getBeans(CommandHandlerInterface.class, _) >> [bean]
        beanManager.getBeans(_) >> [bean]
        beanManager.createCreationalContext(_) >> creationalContext
        beanManager.getReference(_,CommandHandlerInterface.class,_) >> getCommandHandler(nscsCommandType)
        beanManager.getReference(_,CommandValidatorInterface.class,_) >> commandValidator
        rbacAuthorizationManager.checkAuthorization(_ as NscsPropertyCommand) >> {throw new SecurityViolationException()}
        when:
        def NscsCommandResponse response = nscsServiceBean.processCommand(nscsCliCommand)
        then:
        1 * nscsServiceBean.logger.commandStarted("secadm ", nscsCliCommand)
        1 * nscsServiceBean.logger.commandFinishedWithError("secadm ", nscsCliCommand, "Security violation exception.")
        thrown(NscsSecurityViolationException.class)
    }

    @Unroll
    def "process command #commandType" () {
        given:
        def NscsCommandType nscsCommandType = commandType
        def NscsCliCommand nscsCliCommand = new NscsCliCommand("secadm", props)
        nscsPropertyCommand.getCommandType() >> nscsCommandType
        nscsPropertyCommand.getCommandInvokerValue() >> NscsPropertyCommandInvoker.CLI
        nscsPropertyCommand.getProperties() >> props
        commandParser.parseCommand(nscsCliCommand) >> nscsPropertyCommand
        beanManager.getBeans(CommandHandlerInterface.class, _) >> [bean]
        beanManager.getBeans(_) >> [bean]
        beanManager.createCreationalContext(_) >> creationalContext
        beanManager.getReference(_,CommandHandlerInterface.class,_) >> getCommandHandler(commandType)
        beanManager.getReference(_,CommandValidatorInterface.class,_) >> commandValidator
        def JobStatusRecord jobStatusRecord = new JobStatusRecord()
        jobStatusRecord.setJobId(UUID.randomUUID())
        nscsJobCacheHandler.insertJob(nscsCommandType) >> jobStatusRecord
        def CmResponse cmResponse = new CmResponse()
        cmResponse.setStatusCode(0)
        cmResponse.getCmObjects() >> Collections.emptyList()
        nscsCMReaderService.getMOAttribute(_, _, _, _) >> cmResponse
        when:
        def NscsCommandResponse response = nscsServiceBean.processCommand(nscsCliCommand)
        then:
        1 * nscsServiceBean.logger.commandStarted("secadm ", nscsCliCommand)
        1 * nscsServiceBean.logger.commandFinishedWithSuccess("secadm ", nscsCliCommand, "Any VALIDATION was successful")
        and:
        response != null
        and:
        response.getResponseType() == responseType
        where:
        commandType                      | props             || responseType
        NscsCommandType.TEST_COMMAND     | [:]               || NscsCommandResponseType.MESSAGE
        NscsCommandType.TEST_COMMAND     | ["workflows" : 1] || NscsCommandResponseType.MESSAGE
        NscsCommandType.FTPES_ACTIVATE   | [:]               || NscsCommandResponseType.MESSAGE
        NscsCommandType.FTPES_DEACTIVATE | [:]               || NscsCommandResponseType.MESSAGE
        NscsCommandType.HTTPS_ACTIVATE   | [:]               || NscsCommandResponseType.MESSAGE
        NscsCommandType.HTTPS_DEACTIVATE | [:]               || NscsCommandResponseType.MESSAGE
        NscsCommandType.CPP_INSTALL_LAAD | [:]               || NscsCommandResponseType.MESSAGE
        NscsCommandType.SSO_ENABLE       | [:]               || NscsCommandResponseType.MESSAGE
        NscsCommandType.SSO_DISABLE      | [:]               || NscsCommandResponseType.MESSAGE
        NscsCommandType.SSO_GET          | [:]               || NscsCommandResponseType.NAME_MULTIPLE_VALUE
        NscsCommandType.SET_ENROLLMENT   | [:]               || NscsCommandResponseType.MESSAGE
    }

    @Unroll
    def "process command #commandType with expected #exception" () {
        given:
        def NscsCommandType nscsCommandType = commandType
        def NscsCliCommand nscsCliCommand = new NscsCliCommand("secadm", [:])
        nscsPropertyCommand.getCommandType() >> nscsCommandType
        nscsPropertyCommand.getCommandInvokerValue() >> NscsPropertyCommandInvoker.CLI
        nscsPropertyCommand.getProperties() >> props
        commandParser.parseCommand(nscsCliCommand) >> nscsPropertyCommand
        beanManager.getBeans(CommandHandlerInterface.class, _) >> [bean]
        beanManager.getBeans(_) >> [bean]
        beanManager.createCreationalContext(_) >> creationalContext
        beanManager.getReference(_,CommandHandlerInterface.class,_) >> getCommandHandler(commandType)
        beanManager.getReference(_,CommandValidatorInterface.class,_) >> commandValidator
        def JobStatusRecord jobStatusRecord = new JobStatusRecord()
        jobStatusRecord.setJobId(UUID.randomUUID())
        nscsJobCacheHandler.insertJob(nscsCommandType) >> jobStatusRecord
        def CmResponse cmResponse = new CmResponse()
        cmResponse.setStatusCode(0)
        cmResponse.getCmObjects() >> Collections.emptyList()
        nscsCMReaderService.getMOAttribute(_, _, _, _) >> cmResponse
        when:
        def NscsCommandResponse response = nscsServiceBean.processCommand(nscsCliCommand)
        then:
        1 * nscsServiceBean.logger.commandStarted("secadm ", nscsCliCommand)
        1 * nscsServiceBean.logger.commandFinishedWithError("secadm ", nscsCliCommand, _ as String)
        thrown(exception)
        where:
        commandType                                | props               || exception
        NscsCommandType.CPP_SET_SL                 | [:]                 || CommandSyntaxException
        NscsCommandType.CPP_IPSEC                  | [:]                 || CommandSyntaxException
        NscsCommandType.GET_NODE_SPECIFIC_PASSWORD | [:]                 || NscsCapabilityModelException
        NscsCommandType.TEST_COMMAND               | ["workflows" : "a"] || TestWfsException
    }

    private CommandHandler<?> getCommandHandler (NscsCommandType nscsCommandType) {
        if (NscsCommandType.TEST_COMMAND.equals(nscsCommandType)) {
            return testCommandHandler
        } else if (NscsCommandType.FTPES_ACTIVATE.equals(nscsCommandType)) {
            return activateFtpesHandler
        } else if (NscsCommandType.FTPES_DEACTIVATE.equals(nscsCommandType)) {
            return deactivateFtpesHandler
        } else if (NscsCommandType.HTTPS_ACTIVATE.equals(nscsCommandType)) {
            return activateHttpsHandler
        } else if (NscsCommandType.HTTPS_DEACTIVATE.equals(nscsCommandType)) {
            return deactivateHttpsHandler
        } else if (NscsCommandType.CPP_INSTALL_LAAD.equals(nscsCommandType)) {
            return cppInstallLaadHandler
        } else if (NscsCommandType.CPP_IPSEC.equals(nscsCommandType)) {
            return cppIpSecHandler
        } else if (NscsCommandType.CPP_SET_SL.equals(nscsCommandType)) {
            return cppSetSecurityLevelHandler
        } else if (NscsCommandType.SSO_ENABLE.equals(nscsCommandType)) {
            return enableSsoHandler
        } else if (NscsCommandType.SSO_DISABLE.equals(nscsCommandType)) {
            return disableSsoHandler
        } else if (NscsCommandType.SSO_GET.equals(nscsCommandType)) {
            return getSsoHandler
        } else if (NscsCommandType.GET_NODE_SPECIFIC_PASSWORD.equals(nscsCommandType)) {
            return getNodeSpecificPasswordHandler
        } else if (NscsCommandType.SET_ENROLLMENT.equals(nscsCommandType)) {
            return setEnrollmentModeHandler
        } else {
            return null;
        }
    }
}