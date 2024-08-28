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

class TrustRemoveTest extends TrustSetupData {


    @ObjectUnderTest
    TrustRemoveHandler trustRemoveHandler

    private static final String IPSEC = "IPSEC";
    private static final String OAM = "OAM";
    private static final String LAAD = "LAAD";
    private static final String INVALID_CERT_TYPE = "OEM";

    def "object under test injection" () {
        expect:
        trustRemoveHandler != null
    }

    @Unroll("Initiates Trust Removal of OAM/IPSEC/LAAD on given nodes with issuerDn")
    def "Trusted Certificates of OAM/IPSEC/LAAD will be removed from the nodes with issuerDn"() {
        given: "trustCategory, responseMessage"

        setTrustRemoveCommandData(trustCategory)
        setupJobStatusRecord()
        setNodes()
        setTrustCertificates()

        when: "execute Trust Remove Handler process method"
        NscsCommandResponse response = trustRemoveHandler.process(trustRemoveCommand, context)

        then:
        assert response.message(responseMessage)
        where:
        trustCategory                  |  responseMessage
        IPSEC                          | TrustRemoveHandler.TRUST_REMOVAL_EXECUTED
        OAM                            | TrustRemoveHandler.TRUST_REMOVAL_EXECUTED
        LAAD                           | TrustRemoveHandler.TRUST_REMOVAL_EXECUTED
    }

    @Unroll("Initiates Trust removal of OAM/IPSEC/LAAD on given nodes partial success case with issuerDn")
    def "Trusted Certificates of OAM/IPSEC/LAAD will be removed from the nodes partial success case with issuerDn"() {
        given: "trustCategory, responseMessage"

        setTrustRemoveCommandDataForPartialSuccessCase(trustCategory)
        setupJobStatusRecord()
        setNodes()
        setTrustCertificates()

        when: "execute Trust Remove Handler process method"
        NscsCommandResponse response = trustRemoveHandler.process(trustRemoveCommand, context)

        then:
        assert response.message(responseMessage)
        where:
        trustCategory                  |  responseMessage
        IPSEC                          | TrustRemoveHandler.TRUST_REMOVAL_EXECUTED_DYN_ISSUE
        OAM                            | TrustRemoveHandler.TRUST_REMOVAL_EXECUTED_DYN_ISSUE
        LAAD                           | TrustRemoveHandler.TRUST_REMOVAL_EXECUTED_DYN_ISSUE
    }

    @Unroll("Initiates Trust removal of OAM/IPSEC/LAAD on given nodes with caName")
    def "Trusted Certificates of OAM/IPSEC/LAAD will be removed from the nodes with caName"() {
        given: "trustCategory, ExpectedException"

        setTrustRemoveCommandDataWithCaName(trustCategory)
        setupJobStatusRecord()
        setNodes()
        setTrustCertificates()

        when: "execute Trust Remove Handler process method"
        NscsCommandResponse response = trustRemoveHandler.process(trustRemoveCommand, context)

        then:
        def error = thrown(expectedException)
        where:
        trustCategory                          | expectedException
        OAM                                    | InvalidArgumentValueException
        IPSEC                                  | InvalidArgumentValueException
        LAAD                                   | InvalidArgumentValueException
    }

    @Unroll("Initiates Trust Removal of OAM/IPSEC/LAAD on given invalid nodes")
    def "Trusted Certificates of OAM/IPSEC/LAAD will be removed for the invalid nodes"() {
        given: "trustCategory, responseMessage"

        setTrustRemoveCommandData(trustCategory)
        setupJobStatusRecord()
        setTrustCertificates()

        when: "execute Trust Remove Handler process method"
        NscsCommandResponse response = trustRemoveHandler.process(trustRemoveCommand, context)

        then:
        assert response.message(responseMessage)
        where:
        trustCategory                  |  responseMessage
        IPSEC                          | TrustRemoveHandler.TRUST_REMOVAL_NOT_EXECUTED
        OAM                            | TrustRemoveHandler.TRUST_REMOVAL_NOT_EXECUTED
        LAAD                           | TrustRemoveHandler.TRUST_REMOVAL_NOT_EXECUTED
    }

    @Unroll("Initiates Trust Removal with invalid certtype")
    def "Removal of trusted certificates with invalid cettype"() {
        given: "trustCategory, ExpectedException"

        setTrustRemoveCommandDataForCertType(trustCategory)

        when: "execute Trust Remove Handler process method"
        NscsCommandResponse response = trustRemoveHandler.process(trustRemoveCommand, context)

        then:
        def error = thrown(expectedException)
        where:
        trustCategory                        | expectedException
        'IPSEC123'                           | InvalidArgumentValueException
        LAAD                                 | InvalidArgumentValueException
    }

    @Unroll("Initiates Trust Removal with invalid trustCategory")
    def "Removal of trusted certificates with invalid trustCategory"() {
        given: "trustCategory, ExpectedException"

        setTrustRemoveCommandData(trustCategory)

        when: "execute Trust Remove Handler process method"
        NscsCommandResponse response = trustRemoveHandler.process(trustRemoveCommand, context)

        then:
        def error = thrown(expectedException)
        where:
        trustCategory                          | expectedException
        'IPSEC123'                             | InvalidArgumentValueException
    }
}
