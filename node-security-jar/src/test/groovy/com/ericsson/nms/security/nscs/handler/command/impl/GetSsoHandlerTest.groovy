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

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.types.SsoCommand
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.handler.CommandContext
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.nms.security.nscs.utilities.MoAttributeHandler
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility

class GetSsoHandlerTest extends CdiSpecification {
    private static final String NODE_NAME = "CORE19MLTN001"
    private static final String FDN = "MeContext=" + NODE_NAME
    private static final NormalizableNodeReference nodeReference = MockUtils.createNormalizableNodeRef(NODE_NAME)
    private static final String[][] resultSuccessResponse = [["SSO Status", "Error Message", "Suggested solution"], ["ON", "N/A", "N/A"]]
    final String[][] resultCouldNotReadMoAttributeException = [
        ["SSO Status", "Error Message", "Suggested solution"],
        ["N/A", "Unsupported Node Type", "Please check Online Help for correct syntax."
        ]
    ]
    final String[][] nodeDoesNotExistResponse = [
        ["SSO Status", "Error Message", "Suggested solution"],
        [
            "N/A",
            "The NetworkElement specified does not exist",
            "Please specify a valid NetworkElement that exists in the system."
        ]
    ]
    final String[][] ssoNotSupportedResponse = [
        ["SSO Status", "Error Message", "Suggested solution"],
        [
            "N/A",
            "SSO is not supported for one or more node types",
            "Please check Online Help for the list of supported nodes."
        ]
    ]
    final static String SSO = "SSO"

    @ObjectUnderTest
    private GetSsoHandler statusHandler

    @MockedImplementation
    private NscsLogger nscsLogger

    @MockedImplementation
    private SsoCommand command

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
}


