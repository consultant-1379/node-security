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
package com.ericsson.nms.security.nscs.api.exception.wrapper

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException.ErrorType
import com.ericsson.nms.security.nscs.api.model.NodeReference

class MultiErrorNodeExceptionTest extends CdiSpecification {

    private NodeReference nodeRef = Mock(NodeReference.class)
    private NscsServiceException nscsServiceException = Mock(NscsServiceException.class)

    def "no-args constructor" () {
        given:
        def exception = new MultiErrorNodeException();
        expect:
        exception != null
        and:
        exception.getErrorCode() == 10010
        and:
        exception.getErrorType() == ErrorType.MULTIPLE_ERRORS
        and:
        exception.getLocalizedMessage() == 'There are issues with more than one of the nodes specified'
        and:
        exception.getMessage() == 'There are issues with more than one of the nodes specified'
        and:
        exception.getCause() == null
        and:
        exception.getSuggestedSolution() == 'Please check suggested solution for each node and re-run command when these issues are addressed. Alternatively, omit these nodes from the list and re-run the command'
        and:
        exception.getErrorsSize() == 0
    }

    def "exception with errors" () {
        given:
        def exception = new MultiErrorNodeException();
        when:
        exception.addException(nodeRef, nscsServiceException)
        then:
        exception != null
        and:
        exception.getErrorCode() == 10010
        and:
        exception.getErrorType() == ErrorType.MULTIPLE_ERRORS
        and:
        exception.getLocalizedMessage() == 'There are issues with more than one of the nodes specified'
        and:
        exception.getMessage() == 'There are issues with more than one of the nodes specified'
        and:
        exception.getCause() == null
        and:
        exception.getSuggestedSolution() == 'Please check suggested solution for each node and re-run command when these issues are addressed. Alternatively, omit these nodes from the list and re-run the command'
        and:
        exception.getErrorsSize() == 1
    }
}
