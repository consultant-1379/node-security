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

package com.ericsson.nms.security.nscs.handler.command.impl

import org.mockito.Mock;

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse
import com.ericsson.nms.security.nscs.api.command.types.FtpesCommand
import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.handler.CommandContext
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.nms.security.nscs.utilities.MoAttributeHandler
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;

class GetFtpesStatusHandlerTest extends CdiSpecification {
    private static final String NODE_NAME = "ERBS_001"
    private static final String FDN = "MeContext=" + NODE_NAME
    private static final NormalizableNodeReference nodeReference = MockUtils.createNormalizableNodeRef(NODE_NAME)
    private static final String[][] resultSuccessResponse = [
        [
            "FTPES Status",
            "Error Message",
            "Suggested solution"
        ],
        ["ON", "N/A", "N/A"]
    ]
    final String[][] resultCouldNotReadMoAttributeException = [
        [
            "FTPES Status",
            "Error Message",
            "Suggested solution"
        ],
        [
            "N/A",
            "Unsupported Node Type",
            "Please check Online Help for correct syntax."
        ]
    ]
    final static String FTPES = "FTPES"
    final static String message = "Cannot find normalized reference"

    @ObjectUnderTest
    private GetFtpesStatusHandler statusHandler

    @MockedImplementation
    private NscsLogger nscsLogger

    @MockedImplementation
    private FtpesCommand command

    @MockedImplementation
    private NodeValidatorUtility nodeValidatorUtility

    @MockedImplementation
    private NscsCMReaderService reader

    @MockedImplementation
    private MoAttributeHandler moAttributeHandler

    @MockedImplementation
    private CommandContext commandContext

    @MockedImplementation
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility

    private List<NodeReference> nodes = new ArrayList<>()

    def setup() {
        nodes.add(new NodeRef(FDN))
        nscsInputNodeRetrievalUtility.getNodeReferenceList(command) >> { return nodes }
        reader.getNormalizableNodeReference(new NodeRef(FDN)) >> { return nodeReference }
    }

    def "When everything is correct task should return success response"() {
        given:
        moAttributeHandler.getMOAttributeValue(FDN, !null, !null, !null) >> {return FTPES}

        when:
        NscsNameMultipleValueCommandResponse response = statusHandler.process(command, commandContext)

        then:
        responsevVerification(response, resultSuccessResponse)

    }

    def "When node doesn't support ftpes response should contains correct information"() {
        given:
        nodeValidatorUtility.validateNodeForFtpes(nodeReference) >> { throw new UnsupportedNodeTypeException() }

        when:
        NscsNameMultipleValueCommandResponse response = statusHandler.process(command, commandContext)

        then:
        responsevVerification(response, resultCouldNotReadMoAttributeException)

    }

    def "When couldn't find normalized reference response should contains proper information"() {
        given:
        nodeValidatorUtility.validateNodeForFtpes(nodeReference) >> { throw new Exception(message) }
        when:
        NscsMessageCommandResponse response = statusHandler.process(command, commandContext)

        then:
        assert response.getMessage().equals(message)
    }

    private void responsevVerification(NscsNameMultipleValueCommandResponse response, String[][] expectedResults) {
        Iterator iterator = response.iterator()
        while (iterator.hasNext()) {
            NscsNameMultipleValueCommandResponse.Entry entry = (NscsNameMultipleValueCommandResponse.Entry) iterator.next()
            assert expectedResults.contains(entry.getValues())
        }
    }


}
