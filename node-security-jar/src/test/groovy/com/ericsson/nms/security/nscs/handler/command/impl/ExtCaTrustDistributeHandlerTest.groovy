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
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse
import com.ericsson.nms.security.nscs.api.exception.*

import spock.lang.Unroll


class ExtCaTrustDistributeHandlerTest extends ExternalCATrustDistributionSetupData {

    @ObjectUnderTest
    TrustDistributeHandler trustDistributeHandler

    def "object under test injection" () {
        expect:
        trustDistributeHandler != null
    }

    @Unroll("Initiates External CA Trust Distribution on given nodes using an xml file with IPSEC cert type")
    def "External ca trusted Certificates will distributes to the nodes with IPSEC cert type"() {
        given: "certType, filePath, responseMessage"

        setCommandData(filePath,certType)
        setupJobStatusRecord()

        when: "execute Trust Distribute Handler process method"
        NscsCommandResponse response = trustDistributeHandler.process(command, context)

        then:
        assert response.message(responseMessage)
        where:
        certType | filePath                                             |  responseMessage
        'IPSEC'  | 'src/test/resources/EXTCA_TRUST/ValidExtCaTrust.xml' | 'Successfully started a job for trust distribution to nodes'
    }

    @Unroll("Initiates External CA Trust Distribution on given nodes using an xml file with OAM cert type")
    def "External ca trusted Certificates will distributes to the nodes with OAM certType"() {
        given: "certType, filePath, responseMessage"

        setCommandData(filePath,certType)

        when: "execute Trust Distribute Handler process method"
        NscsCommandResponse response = trustDistributeHandler.process(command, context)

        then:
        def error = thrown(expectedException)
        where:
        certType | filePath                                             | expectedException
        'OAM'    | 'src/test/resources/EXTCA_TRUST/ValidExtCaTrust.xml' | InvalidArgumentValueException
    }

    @Unroll("Initiates External CA Trust Distribution on given nodes using an xml file with IPSEC trust category type")
    def "External ca trusted Certificates will distributes to the nodes with IPSEC trust category type"() {
        given: "trustCategory, filePath, responseMessage"

        setTrustCategoryCommandData(filePath,trustCategory)
        setupJobStatusRecord()

        when: "execute Trust Distribute Handler process method"
        NscsCommandResponse response = trustDistributeHandler.process(command, context)

        then:
        assert response.message(responseMessage)
        where:
        trustCategory | filePath                                             | responseMessage
        'IPSEC'       | 'src/test/resources/EXTCA_TRUST/ValidExtCaTrust.xml' | 'Successfully started a job for trust distribution to nodes'
    }

    @Unroll("Initiates External CA Trust Distribution on given nodes using an xml file with OAM trust category type")
    def "External ca trusted Certificates will distributes to the nodes with OAM certType with trust category type"() {
        given: "trustCategory, filePath, responseMessage"

        setTrustCategoryCommandData(filePath,trustCategory)

        when: "execute Trust Distribute Handler process method"
        NscsCommandResponse response = trustDistributeHandler.process(command, context)

        then:
        def error = thrown(expectedException)
        where:
        trustCategory | filePath                                             | expectedException
        'OAM'         | 'src/test/resources/EXTCA_TRUST/ValidExtCaTrust.xml' | InvalidArgumentValueException
    }

    @Unroll("Initiates External CA Trust Distribution on given nodes using an xml file with CommandSyntaxException")
    def "External ca trusted Certificates will distributes to the nodes with CommandSyntaxException"() {
        given: "trustCategory, filePath, responseMessage"

        setExtCaCommandErrorData(filePath,trustCategory)

        when: "execute Trust Distribute Handler process method"
        NscsCommandResponse response = trustDistributeHandler.process(command, context)

        then:
        def error = thrown(expectedException)
        where:
        trustCategory | filePath                                             | expectedException
        'OAM'         | 'src/test/resources/EXTCA_TRUST/ValidExtCaTrust.xml' | CommandSyntaxException
    }
}
