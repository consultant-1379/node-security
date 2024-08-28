/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.security.nscs.workflow.task.comecim.node.validation

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.NodeDoesNotExistException
import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.InvalidNodeException
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.validation.NodeValidationFtpesTask
import spock.lang.Shared

class NodeValidationFtpesTaskHandlerTest extends CdiSpecification {

    @ObjectUnderTest
    private NodeValidationFtpesTaskHandler taskHandler

    @MockedImplementation
    private NscsLogger nscsLogger;

    @MockedImplementation
    private NodeValidatorUtility nodeValidator

    @Shared
    private String expectedSuccessResult

    @Shared
    private NodeValidationFtpesTask task


    private static final String NODE_NAME = "ERBS_001"
    private static final String FDN = "MeContext=" + NODE_NAME



    def setupSpec(){
        task = new NodeValidationFtpesTask(FDN)
        expectedSuccessResult = "Validation of the node " +FDN+ " finished with Success."
    }

    def "When everything is correct logger should finished with success"(){
        given:
        nodeValidator.validateNodeForFtpes(task.getNode()) >> {}

        when:
        taskHandler.processTask(task)

        then:
        1 * nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task,expectedSuccessResult)
    }

    def "When node does not exist task should throw InvalidNodeException"(){
        given:
        nodeValidator.validateNodeForFtpes(task.getNode()) >> {throw new NodeDoesNotExistException()}

        when:
        taskHandler.processTask(task)

        then:
        thrown InvalidNodeException
    }

    def "When there is no normalized reference task should throw InvalidNodeException"(){
        given:
        nodeValidator.validateNodeForFtpes(task.getNode()) >> {throw new InvalidNodeException()}

        when:
        taskHandler.processTask(task)

        then:
        thrown InvalidNodeException
    }

    def "When node does not support ftpes commands task should throw UnsupportedNodeTypeException"(){
        given:
        nodeValidator.validateNodeForFtpes(task.getNode()) >> {throw new UnsupportedNodeTypeException()}

        when:
        taskHandler.processTask(task)

        then:
        thrown InvalidNodeException
    }


}