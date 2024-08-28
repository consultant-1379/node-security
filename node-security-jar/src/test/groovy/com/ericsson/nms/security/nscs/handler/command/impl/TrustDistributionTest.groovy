/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.command.impl

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.security.nscs.api.command.*
import com.ericsson.nms.security.nscs.api.exception.*

import spock.lang.Unroll

class TrustDistributionTest extends TrustSetupData {

    @ObjectUnderTest
    TrustDistributeHandler trustDistributeHandler

    def "object under test injection" () {
        expect:
        trustDistributeHandler != null
    }

    @Unroll("Initiates Trust Distribution of OAM/IPSEC/LAAD on given nodes")
    def "Trusted Certificates of OAM/IPSEC/LAAD will distributes to the nodes"() {
        given: "trustCategory, responseMessage"

        setCommandData(trustCategory)
        setupJobStatusRecord()
        setNodes()

        when: "execute Trust Distribute Handler process method"
        NscsCommandResponse response = trustDistributeHandler.process(command, context)

        then:
        assert response.message(responseMessage)
        where:
        trustCategory | responseMessage
        'IPSEC'       | TrustDistributeHandler.TRUST_DISTRIBUTION_EXECUTED
        'OAM'         | TrustDistributeHandler.TRUST_DISTRIBUTION_EXECUTED
        'LAAD'        | TrustDistributeHandler.TRUST_DISTRIBUTION_EXECUTED
    }

    @Unroll("Initiates Trust Distribution of OAM/IPSEC/LAAD on given nodes partial success case")
    def "Trusted Certificates of OAM/IPSEC/LAAD will distributes to the nodes partial success case"() {
        given: "trustCategory, responseMessage"

        setCommandDataForPartialSuccess(trustCategory)
        setupJobStatusRecord()
        setNodes()

        when: "execute Trust Distribute Handler process method"
        NscsCommandResponse response = trustDistributeHandler.process(command, context)

        then:
        assert response.message(responseMessage)
        where:
        trustCategory | responseMessage
        'IPSEC'       | TrustDistributeHandler.TRUST_DISTRIBUTION_EXECUTED_DYN_ISSUE
        'OAM'         | TrustDistributeHandler.TRUST_DISTRIBUTION_EXECUTED_DYN_ISSUE
        'LAAD'        | TrustDistributeHandler.TRUST_DISTRIBUTION_EXECUTED_DYN_ISSUE
    }

    @Unroll("Initiates Trust Distribution of OAM/IPSEC/LAAD on given invalid nodes")
    def "Trusted Certificates of OAM/IPSEC/LAAD will distributes to the invalid nodes"() {
        given: "trustCategory, responseMessage"

        setCommandData(trustCategory)
        setupJobStatusRecord()

        when: "execute Trust Distribute Handler process method"
        NscsCommandResponse response = trustDistributeHandler.process(command, context)

        then:
        assert response.message(responseMessage)
        where:
        trustCategory | responseMessage
        'IPSEC'       | TrustDistributeHandler.TRUST_DISTRIBUTION_NOT_EXECUTED
        'OAM'         | TrustDistributeHandler.TRUST_DISTRIBUTION_NOT_EXECUTED
        'LAAD'        | TrustDistributeHandler.TRUST_DISTRIBUTION_NOT_EXECUTED
    }

    @Unroll("Initiates Trust Distribution of OAM/IPSEC/LAAD on given nodes with Ca and trustcategory")
    def "Trusted Certificates of OAM/IPSEC/LAAD will distributes to the nodes with Ca and trustcategory"() {
        given: "trustCategory, responseMessage"

        setCommandDataWithNodeAndCA(trustCategory,"ENM_OAM_CA")
        setupJobStatusRecord()
        setValidNodes()

        when: "execute Trust Distribute Handler process method"
        NscsCommandResponse response = trustDistributeHandler.process(command, context)

        then:
        assert response.message(responseMessage)
        where:
        trustCategory | responseMessage
        'IPSEC'       | TrustDistributeHandler.TRUST_DISTRIBUTION_EXECUTED
        'OAM'         | TrustDistributeHandler.TRUST_DISTRIBUTION_EXECUTED
        'LAAD'        | TrustDistributeHandler.TRUST_DISTRIBUTION_EXECUTED
    }

    @Unroll("Initiates Trust Distribution with valid Ca node names from entity is null")
    def "Trusted Certificates will distributes on with valid Ca node names from entity is null"() {
        given: "trustCategory, ExpectedException"

        setCommandDataWithOnlyCA(trustCategory,null)
        setupJobStatusRecord()
        setValidNodes()
        setEntityList(null)

        when: "execute Trust Distribute Handler process method"
        NscsCommandResponse response = trustDistributeHandler.process(command, context)

        then:
        def error = thrown(expectedException)
        where:
        trustCategory | expectedException
        'IPSEC'       | EntitiesWithValidCategoryForNodesNotFound
    }

    @Unroll("Initiates Trust Distribution with valid Ca when EntitiesWithValidCategoryForNodesNotFound")
    def "Trusted Certificates of OAM will distributes on with valid Ca when EntitiesWithValidCategoryForNodesNotFound"() {
        given: "trustCategory, ExpectedException"

        setCommandDataWithOnlyCA(trustCategory,"ENM_OAM_CA")
        setupJobStatusRecord()
        setValidNodes()

        when: "execute Trust Distribute Handler process method"
        NscsCommandResponse response = trustDistributeHandler.process(command, context)

        then:
        def error = thrown(expectedException)
        where:
        trustCategory | expectedException
        'OAM'         | EntitiesWithValidCategoryForNodesNotFound
        'LAAD'        | CommandSyntaxException
    }

    @Unroll("Initiates Trust Distribution with valid Ca")
    def "Trusted Certificates will distributes on with valid Ca"() {
        given: "trustCategory, responseMessage"

        setCommandDataWithOnlyCA(trustCategory,"ENM_OAM_CA")
        setupJobStatusRecord()
        setValidNodesForEntity()
        setEntityList("ENM_OAM_CA")

        when: "execute Trust Distribute Handler process method"
        NscsCommandResponse response = trustDistributeHandler.process(command, context)

        then:
        assert response.message(responseMessage)
        where:
        trustCategory | responseMessage
        'OAM'         | TrustDistributeHandler.TRUST_DISTRIBUTION_EXECUTED
    }
}
