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
import com.ericsson.nms.security.nscs.api.exception.*

import spock.lang.Unroll

/**
 * This class covers positive and negative scenario test cases to issue certificate on the given nodes for external ca.
 *
 * @author xkumkam
 *
 */
class CertificateIssueHandlerExtcaTest extends CertificateIssueSetupData {

    @ObjectUnderTest
    CertificateIssueHandler certificateIssueHandler

    def "object under test injection" () {
        expect:
        certificateIssueHandler != null
    }

    @Unroll("Initiate CertificateIssueHandler to issue certificate on node using nodeName #nodeName")
    def 'Issues Certificate for valid nodes'() {
        given: 'certType, extcaOption, filePath,nodeName'
        setcommand(certType,extcaOption)
        setFileData(filePath)
        setDataForNodeExists(nodeStatus, nodeName,filePath)
        when: 'execute Certificate Issue Handler process method'
        certificateIssueHandler.process(command, context)
        then: 'Assert process response'
        where:
        nodeStatus    | filePath                                                        | certType  | extcaOption | nodeName
        'validNode'   | 'src/test/resources/ISSUE_EXTCA/newEnrollmentFile.xml'          | 'IPSEC'   | 'extca'     | 'LTE01dg2ERBS00026'
        'invalidNode' | 'src/test/resources/ISSUE_EXTCA/invalidNodeExtCaEnrollment.xml' | 'IPSEC'   | 'extca'     | 'node1234'
    }

    @Unroll("Initiates CertificateIssueHandler to issue certificate incase of exceptions using certType or extcaOption  #certType #extcaOption ")
    def "Exception cases for performing enrollment on nodes"() {
        given: "filePath, ExpectedException"
        setcommand(certType,extcaOption)
        setFileData(filePath)
        setDataForNodeExists(nodeStatus, nodeName,filePath)
        when:"Execute Certificate Issue Handler process method"
        certificateIssueHandler.process(command, context)
        then:"Expected response are thrown as a response"
        def error = thrown(expectedException)
        where:
        certType | extcaOption | filePath                                                    | expectedException             | nodeStatus  | nodeName
        'IPSEC'  |   null      | 'src/test/resources/ISSUE_EXTCA/enrollmentOld.xml'          | InvalidInputXMLFileException  | 'validNode' |'LTE01dg2ERBS00026'
        'IPSEC'  |  'extca'    | 'src/test/resources/ISSUE_EXTCA/enrollmentOld.xml'          | InvalidInputXMLFileException  | 'validNode' |'LTE01dg2ERBS00026'
        'OAM'    |  'extca'    | 'src/test/resources/ISSUE_EXTCA/enrollmentOld.xml'          | InvalidArgumentValueException | 'validNode' |'LTE01dg2ERBS00026'
        'OAM'    |   null      | 'src/test/resources/ISSUE_EXTCA/newEnrollmentFile.xml'      | InvalidArgumentValueException | 'validNode' |'LTE01dg2ERBS00026'
        'OAM'    |  'extca'    | 'src/test/resources/ISSUE_EXTCA/newEnrollmentFile.xml'      | InvalidArgumentValueException | 'validNode' |'LTE01dg2ERBS00026'
        'IPSEC'  |   null      | 'src/test/resources/ISSUE_EXTCA/emptySubjectAltNameCPP.xml' | InvalidInputXMLFileException  | 'validNode' |'LTE01dg2ERBS00026'
    }
}
