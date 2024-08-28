/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.exception

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException.ErrorType

import spock.lang.Unroll

class MoTypeNotFoundExceptionTest extends CdiSpecification {

    def "no-args constructor" () {
        given: "an MO type not found exception"
        def moTypeNotFoundException = new MoTypeNotFoundException();
        expect: "MO type not found exception should not be null"
        moTypeNotFoundException != null
        and:
        moTypeNotFoundException.getErrorCode() == 10129
        and:
        moTypeNotFoundException.getErrorType() == ErrorType.MO_TYPE_NOT_FOUND
        and:
        moTypeNotFoundException.getLocalizedMessage() == 'MO type not found'
        and:
        moTypeNotFoundException.getMessage() == 'MO type not found'
        and:
        moTypeNotFoundException.getCause() == null
        and:
        moTypeNotFoundException.getSuggestedSolution() == "An error occurred while executing the command on the system. Consult the error and command logs for more information."
    }

    @Unroll
    def "message constructor" () {
        given: "an MO type not found exception"
        def moTypeNotFoundException = new MoTypeNotFoundException(message);
        expect: "MO type not found exception should not be null"
        moTypeNotFoundException != null
        and:
        moTypeNotFoundException.getErrorCode() == 10129
        and:
        moTypeNotFoundException.getErrorType() == ErrorType.MO_TYPE_NOT_FOUND
        and:
        moTypeNotFoundException.getLocalizedMessage() == 'MO type not found'+' : ' + message
        and:
        moTypeNotFoundException.getMessage() == 'MO type not found'+' : ' + message
        and:
        moTypeNotFoundException.getCause() == null
        and:
        moTypeNotFoundException.getSuggestedSolution() == "An error occurred while executing the command on the system. Consult the error and command logs for more information."
        where:
        message << [null, '', 'my message']
    }
}
