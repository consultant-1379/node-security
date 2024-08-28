/*------------------------------------------------------------------------------
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

package com.ericsson.nms.security.nscs.handler.ciphersconfig.utility

import static org.junit.Assert.*

import java.nio.charset.StandardCharsets

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse.NscsCommandResponseType
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.util.FileUtil

import spock.lang.Shared

abstract class AbstractCiphersProtocolManagerSpec extends CdiSpecification {
    @Shared
    private byte[] content

    @ImplementationInstance
    FileUtil fileUtil = [
        createDeletableDownloadFileIdentifier : { final byte[] fileContents, final String fileName, final String contentType ->
            content = fileContents
            return "fileIdentifier"
        }
    ] as FileUtil

    @ImplementationInstance
    NormalizableNodeReference normNode = [
        getFdn : {
            -> return "NetworkElement=1"
        }
    ] as NormalizableNodeReference

    @ImplementationInstance
    NscsCMReaderService nscsCmReaderService = [
        getNormalizableNodeReference : { final NodeReference node ->
            return normNode
        }
    ] as NscsCMReaderService

    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {
        injectionProperties.autoLocateFrom('com.ericsson.nms.security.nscs.util')
    }

    final String nodeName = "LTE01dg2ERBS0001"
    private Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>()
    private List<NodeReference> nodeList = new ArrayList<>()

    def 'object under test'() {
        expect:
        getObjectUnderTest() != null
    }

    def 'test buildGetCiphersResponse for SingleNode'() {
        given:
        final NodeReference nodeRef = new NodeRef(nodeName)
        nodeList.add(nodeRef)
        final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>()
        def expected = new ArrayList<>()
        expected.addAll(getExpectedHeader())
        expected.addAll(getExpectedOk())
        NscsCommandResponse commandResponse = null
        when:
        try {
            commandResponse = getObjectUnderTest().buildGetCiphersResponse(nodeList, invalidNodesErrorMap)
        } catch (final IOException e) {
            fail("testBuildCiphersResponseForSingleNode test case failed due to IOException")
        }
        then:
        assertNotNull(commandResponse)
        assertTrue(commandResponse.isNameMultipleValueResponseType())
        assert commandResponse instanceof NscsNameMultipleValueCommandResponse
        checkSingleNodeResponse(commandResponse, expected)
    }

    def 'test buildGetCiphersResponse for SingleNode on InvalidNodes'() {
        given:
        final NodeReference nodeRef = new NodeRef(nodeName)
        invalidNodesErrorMap.put(nodeRef, new NodeNotSynchronizedException())
        NscsCommandResponse commandResponse = null
        def expected = new ArrayList<>()
        expected.addAll(getExpectedHeader())
        expected.addAll(getExpectedFail())
        when:
        try {
            commandResponse = getObjectUnderTest().buildGetCiphersResponse(nodeList, invalidNodesErrorMap)
        } catch (final IOException e) {
            fail("testBuildCiphersResponseForSingleNode_InvalidNodes test case failed due to IOException")
        }
        then:
        assertNotNull(commandResponse)
        assertTrue(commandResponse.isNameMultipleValueResponseType())
        assert commandResponse instanceof NscsNameMultipleValueCommandResponse
        checkSingleNodeResponse(commandResponse, expected)
    }

    def 'test buildGetCiphersResponse for MultipleNodes'() {
        given:
        final NodeReference nodeRef = new NodeRef(nodeName)
        nodeList.add(nodeRef)
        invalidNodesErrorMap.put(nodeRef, new NodeNotSynchronizedException())
        def expected = new ArrayList<>()
        expected.addAll(getExpectedHeader())
        expected.addAll(getExpectedOk())
        expected.addAll(getExpectedFail())
        NscsCommandResponse commandResponse = null
        content = new byte[0]
        when:
        try {
            commandResponse = getObjectUnderTest().buildGetCiphersResponse(nodeList, invalidNodesErrorMap)
        } catch (final IOException e) {
            fail("testBbuildCiphersResponseForMultipleNodes test case failed due to IOException")
        }
        then:
        assertNotNull(commandResponse)
        assertFalse(commandResponse.isNameMultipleValueResponseType())
        assertTrue(NscsCommandResponseType.DOWNLOAD_REQ_MESSAGE == commandResponse.getResponseType())
        assert commandResponse instanceof NscsNameMultipleValueCommandResponse
        checkMultipleNodeResponse(content, expected)
    }

    protected abstract List<List<String>> getExpectedHeader()
    protected abstract List<List<String>> getExpectedOk()
    protected abstract List<List<String>> getExpectedFail()
    protected abstract CiphersProtocolManager getObjectUnderTest()

    void checkSingleNodeResponse(commandResponse, expected) {
        assert commandResponse.size() == expected.size()
        commandResponse.eachWithIndex { entry, id ->
            assert entry.name == expected[id][0]
            entry.values.eachWithIndex { col, idx ->
                assert col == expected[id][idx+1]
            }
        }
    }

    void checkMultipleNodeResponse(content, expected) {
        def strContent = new String(content, StandardCharsets.UTF_8)
        def rows = strContent.split(System.lineSeparator())
        assert rows.length == expected.size()
        rows.eachWithIndex { r, rowId ->
            def fields = r.split(",")
            fields.eachWithIndex { f, fieldId ->
                assert f.trim() == expected[rowId][fieldId]
            }
        }
    }
}
