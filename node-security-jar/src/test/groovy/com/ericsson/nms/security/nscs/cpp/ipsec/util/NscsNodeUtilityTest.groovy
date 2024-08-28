package com.ericsson.nms.security.nscs.cpp.ipsec.util

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.data.moget.MOGetServiceFactory
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility

import spock.lang.Unroll

import com.ericsson.nms.security.nscs.handler.command.impl.MockUtils
import com.ericsson.nms.security.nscs.logger.NscsLogger

class NscsNodeUtilityTest extends CdiSpecification {

    @ObjectUnderTest
    NscsNodeUtility nscsNodeUtility;

    @MockedImplementation
    private MOGetServiceFactory moGetServiceFactory;

    @Unroll
    def "Verify IP address type based on node ref# input"() {
	given:
	when: 'get IP address type based on node data'
	nscsNodeUtility.hasNodeIPv6Address(input);
	then: 'IllegalArgumentException is thrown when node data is null'
	thrown(IllegalArgumentException)
	where:
	input   | expected
	null    | null
    }

    @Unroll
    def "get NodeCredentialKeyInfo based on the node type"() {
        given:
        moGetServiceFactory.getNodeCredentialKeyInfo(_, _) >> "RSA_2048"
        when:
        String keyAlgorithm = nscsNodeUtility.getNodeCredentialKeyInfo("LTE01dg2ERBS0001", "1")
        then:
        assert(keyAlgorithm.equals("RSA_2048"))
    }
}
