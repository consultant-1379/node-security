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

import javax.inject.Inject

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.types.GetCredentialsCommand
import com.ericsson.nms.security.nscs.handler.CommandContext

class GetCredentialsMandatoryParamsValidatorTest extends CdiSpecification {

    @ObjectUnderTest
    private GetCredentialsMandatoryParamsValidator validator

    @Inject
    private CommandContext commandContext

    def 'object under test'() {
        expect:
        validator != null
    }

    def 'validate command with unknown property'() {
        given:
        GetCredentialsCommand command = new GetCredentialsCommand()
        command.setProperties(["unknown_key" : "unknown_value"])
        when:
        validator.validate(command, commandContext)
        then:
        noExceptionThrown()
    }
}
