/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.validation.impl

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.types.CredentialsCommand
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException
import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.handler.CommandContext
import com.ericsson.nms.security.nscs.logger.NscsLogger

import spock.lang.Shared

class CredMandatoryParamsValidatorTest extends CdiSpecification {

    @ObjectUnderTest
    private CredMandatoryParamsValidator validator

    @MockedImplementation
    private NscsLogger nscsLogger

    @MockedImplementation
    private NscsCapabilityModelService nscsCapabilityModelService

    @MockedImplementation
    private CommandContext commandContext

    @Shared
    private NormalizableNodeReference normalizable = Mock(NormalizableNodeReference)

    def setup() {
        nscsCapabilityModelService.getExpectedCredentialsParams(normalizable) >> ["expected_1", "expected_2"]
        nscsCapabilityModelService.getUnexpectedCredentialsParams(normalizable) >> [
            "unexpected_1",
            "unexpected_2"
        ]
    }

    def 'object under test'() {
        expect:
        validator != null
    }

    def "validate credentials create with no valid node"() {
        given:
        CredentialsCommand command = new CredentialsCommand()
        commandContext.getValidNodes() >> []
        when:
        validator.validate(command, commandContext)
        then:
        noExceptionThrown()
    }

    def "validate credentials create with one valid node not supporting credentials"() {
        given:
        CredentialsCommand command = new CredentialsCommand()
        commandContext.getValidNodes() >> [normalizable]
        nscsCapabilityModelService.isCliCommandSupported(normalizable, NscsCapabilityModelService.CREDENTIALS_COMMAND) >> false
        when:
        validator.validate(command, commandContext)
        then:
        noExceptionThrown()
    }

    def "validate credentials create with one valid node not existent"() {
        given:
        CredentialsCommand command = new CredentialsCommand()
        commandContext.getValidNodes() >> [normalizable]
        nscsCapabilityModelService.isCliCommandSupported(normalizable, NscsCapabilityModelService.CREDENTIALS_COMMAND) >> {throw new NscsCapabilityModelException("error")}
        normalizable.hasNormalizedRef() >> false
        when:
        validator.validate(command, commandContext)
        then:
        noExceptionThrown()
    }

    def "validate credentials create with one valid node supporting credentials and no expected params"() {
        given:
        CredentialsCommand command = new CredentialsCommand()
        commandContext.getValidNodes() >> [normalizable]
        nscsCapabilityModelService.isCliCommandSupported(normalizable, NscsCapabilityModelService.CREDENTIALS_COMMAND) >> true
        when:
        validator.validate(command, commandContext)
        then:
        thrown(CommandSyntaxException.class)
    }

    def "validate credentials create with one valid node supporting credentials and unexpected params"() {
        given:
        CredentialsCommand command = new CredentialsCommand()
        command.setProperties(["unexpected_1" : "unexp_value_1"])
        commandContext.getValidNodes() >> [normalizable]
        nscsCapabilityModelService.isCliCommandSupported(normalizable, NscsCapabilityModelService.CREDENTIALS_COMMAND) >> true
        when:
        validator.validate(command, commandContext)
        then:
        thrown(CommandSyntaxException.class)
    }

    def "validate credentials create with one valid node supporting credentials and missing expected params"() {
        given:
        CredentialsCommand command = new CredentialsCommand()
        command.setProperties(["expected_1" : "exp_value_1"])
        commandContext.getValidNodes() >> [normalizable]
        nscsCapabilityModelService.isCliCommandSupported(normalizable, NscsCapabilityModelService.CREDENTIALS_COMMAND) >> true
        when:
        validator.validate(command, commandContext)
        then:
        thrown(CommandSyntaxException.class)
    }

    def "validate credentials create with one valid node supporting credentials and expected params"() {
        given:
        CredentialsCommand command = new CredentialsCommand()
        command.setProperties(["expected_1" : "exp_value_1", "expected_2" : "exp_value_2"])
        commandContext.getValidNodes() >> [normalizable]
        nscsCapabilityModelService.isCliCommandSupported(normalizable, NscsCapabilityModelService.CREDENTIALS_COMMAND) >> true
        when:
        validator.validate(command, commandContext)
        then:
        noExceptionThrown()
    }

    def "validate credentials create with one valid node supporting credentials and expected and unexpected params"() {
        given:
        CredentialsCommand command = new CredentialsCommand()
        command.setProperties(["expected_1" : "exp_value_1", "expected_2" : "exp_value_2", "unexpected_1" : "unexp_value_1"])
        commandContext.getValidNodes() >> [normalizable]
        nscsCapabilityModelService.isCliCommandSupported(normalizable, NscsCapabilityModelService.CREDENTIALS_COMMAND) >> true
        when:
        validator.validate(command, commandContext)
        then:
        thrown(CommandSyntaxException.class)
    }
}