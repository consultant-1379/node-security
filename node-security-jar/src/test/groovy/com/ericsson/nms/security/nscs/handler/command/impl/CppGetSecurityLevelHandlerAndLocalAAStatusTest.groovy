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

import javax.inject.Inject

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse
import com.ericsson.nms.security.nscs.api.command.types.CppSecurityLevelCommand
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes
import com.ericsson.nms.security.nscs.cpp.seclevel.util.CppGetSecurityLevelConstants
import com.ericsson.nms.security.nscs.handler.validation.impl.CppGetSecurityLevelValidator
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.nms.security.nscs.utilities.Constants
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility
import com.ericsson.oss.services.dto.JobStatusRecord

import spock.lang.Shared
import spock.lang.Unroll

class CppGetSecurityLevelHandlerAndLocalAAStatusTest extends CPPGetSecurityLevelSetupData {

    @ObjectUnderTest
    CppGetSecurityLevelHandler cppGetSecurityLevelHandler

    @Inject
    CppSecurityLevelCommand command

    @Inject
    JobStatusRecord jobStatusRecord

    @MockedImplementation
    NscsLogger nscsLogger

    @Inject
    private CppGetSecurityLevelValidator cppGetSecurityLevelValidator

    @Inject
    NscsNodeUtility nscsNodeUtility

    @Shared
    private String[] securityLevelHeader

    @Shared
    private String[][] resultSecurityLevelOneLocalAAStatusActivated

    @Shared
    private String[][] resultSecurityLevelTwoLocalAAStatusDeactivated

    @Shared
    private String[][] resultSecurityLevelEmptyLocalAAStatusActivated

    @Shared
    private String[][] resultSecurityLevelEmptyLocalAAStatusNA

    @Shared
    private String[][] resultSecurityLevelNodeDoesNotExistException

    final String namespace = "OSS_NE_CM_DEF"
    final int poId = 1111
    final String cmFunction = "CmFunction"

    def setupSpec() {

        securityLevelHeader = [
            CppGetSecurityLevelConstants.NODE_SECURITY_LEVEL_HEADER,
            CppGetSecurityLevelConstants.LOCAL_AA_MODE,
            CppGetSecurityLevelConstants.ERROR_DETAILS_HEADER,
            CppGetSecurityLevelConstants.SUGGESTED_SOLUTION
        ]

        resultSecurityLevelOneLocalAAStatusActivated = [
            securityLevelHeader,
            [
                "level 1",
                CppGetSecurityLevelConstants.ACTIVATED,
                CppGetSecurityLevelConstants.NA,
                CppGetSecurityLevelConstants.NA]
        ]
        resultSecurityLevelTwoLocalAAStatusDeactivated = [
            securityLevelHeader,
            [
                "level 2",
                CppGetSecurityLevelConstants.DEACTIVATED,
                CppGetSecurityLevelConstants.NA,
                CppGetSecurityLevelConstants.NA]
        ]

        resultSecurityLevelEmptyLocalAAStatusActivated = [
            securityLevelHeader,
            [
                Constants.ERROR,
                CppGetSecurityLevelConstants.ACTIVATED,
                CppGetSecurityLevelConstants.NODE_SECURITY_LEVEL_ERROR_DETAILS_MESSAGE,
                CppGetSecurityLevelConstants.NODE_SECURITY_LEVEL_SUGGESTED_SOLUTION ]
        ]

        resultSecurityLevelEmptyLocalAAStatusNA = [
            securityLevelHeader,
            [
                Constants.ERROR,
                CppGetSecurityLevelConstants.NA,
                CppGetSecurityLevelConstants.LOCAL_AA_MODE_DETAILS_MSG + "\n",
                CppGetSecurityLevelConstants.LOCAL_AA_MODE_SUGGESTED_SOLUTION + "\n"]
        ]
        resultSecurityLevelNodeDoesNotExistException = [
            securityLevelHeader,
            [
                Constants.ERROR,
                Constants.ERROR,
                NscsErrorCodes.THE_NODE_SPECIFIED_DOES_NOT_EXIST,
                NscsErrorCodes.SPECIFY_A_VALID_NODE
            ]
        ]
    }

    def "object under test injection" () {
        expect:
        cppGetSecurityLevelHandler != null
    }

    @Unroll("Read SecurityLevel on the node #nodeName #nodeStatus #securityLevel #localAAStatus")
    def 'Status of SecurityLevel Feature on node'() {
        slGetCommandData(nodeStatus, nodeName)
        setDataForManagedObject(nodeName)
        setDataForNodeExists(nodeStatus, nodeName, nodeType, securityLevel, localAAStatus, isCliCmdSupported)
        when: 'Get SecurityLevel feature status on node '
        def output = cppGetSecurityLevelHandler.process(command, context)
        then: 'Response contains status information of SecurityLevel feature on the given node'

        if(output.isNameMultipleValueResponseType()){
            def response = output.getAdditionalInformation()
            responseVerification(output,expectedResults)
        }else{
            def response = output.getMessage()
            assert response.contains(expectedResults)
        }

        where:'Assert status on node'
        nodeName           | nodeType   | nodeStatus          |securityLevel      | localAAStatus                                | expectedResults                                    | isCliCmdSupported
        'LTE102RNC00001'   | 'RNC'      | 'validNode'         | 'level 1'         | CppGetSecurityLevelConstants.LOCALAADATABASE | resultSecurityLevelOneLocalAAStatusActivated       | true
        'LTE102RNC00002'   | 'RNC'      | 'validNode'         | 'level 2'         | CppGetSecurityLevelConstants.NODE_PASSPHRASE | resultSecurityLevelTwoLocalAAStatusDeactivated     | true
        'LTE102RNC00004'   | 'RNC'      | 'validNode'         | 'undefined'       | CppGetSecurityLevelConstants.LOCALAADATABASE | resultSecurityLevelEmptyLocalAAStatusActivated     | true
        'LTE102ERBS00005'  | 'ERBS'     | 'validNode'         | 'undefined'       | CppGetSecurityLevelConstants.NA              | resultSecurityLevelEmptyLocalAAStatusNA            | true
        'LTE102RNC00009'   | 'RNC'      | 'normNodeNull'      | 'level 1'         | CppGetSecurityLevelConstants.LOCALAADATABASE | resultSecurityLevelNodeDoesNotExistException       | true
    }

    def responseVerification(NscsNameMultipleValueCommandResponse response, String[][] expectedResults) {
        Iterator iterator = response.iterator()
        while (iterator.hasNext()) {
            NscsNameMultipleValueCommandResponse.Entry entry = (NscsNameMultipleValueCommandResponse.Entry) iterator.next()
            assert expectedResults.contains(entry.getValues())
        }
    }
}
