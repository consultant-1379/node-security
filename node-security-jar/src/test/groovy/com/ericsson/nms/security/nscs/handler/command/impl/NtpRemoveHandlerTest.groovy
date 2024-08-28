package com.ericsson.nms.security.nscs.handler.command.impl

import javax.inject.Inject

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException
import com.ericsson.nms.security.nscs.api.exception.InvalidInputXMLFileException
import com.ericsson.nms.security.nscs.util.NtpConstants
import com.ericsson.oss.services.dto.JobStatusRecord
import com.ericsson.oss.services.enums.JobGlobalStatusEnum
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler
import com.ericsson.oss.services.topologyCollectionsService.exception.rest.EmptyFileException

import spock.lang.Shared
import spock.lang.Unroll



class NtpRemoveHandlerTest extends NtpRemoveSetupData{

    @ObjectUnderTest
    NtpRemoveHandler ntpRemoveHandler

    @Inject
    NscsJobCacheHandler nscsJobCacheHandler

    @Shared
    private JobStatusRecord jobStatusRecord

    @Shared
    private String allValidNodesMessage

    @Shared
    private String partialValidNodesMessage

    @Shared
    private String allInValidNodesMessage

    def "object under test injection" () {
        expect:
        ntpRemoveHandler != null
    }

    def setupSpec() {

        UUID jobId = UUID.randomUUID()
        jobStatusRecord = new JobStatusRecord()
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.PENDING)
        jobStatusRecord.setUserId("user")
        jobStatusRecord.setJobId(jobId)

        allValidNodesMessage = NtpConstants.NTP_REMOVE_EXECUTED + ". Perform 'secadm job get -j " + jobStatusRecord.getJobId().toString() + "' to get progress information.";
        partialValidNodesMessage = String.format(NtpConstants.NTP_REMOVE_PARTIALLY_EXECUTED, jobStatusRecord.getJobId().toString());
        allInValidNodesMessage = NtpConstants.NTP_REMOVE_NOT_EXECUTED;
    }


    @Unroll("Initiate NtpRemoveHandler to remove ntp key detials on node using nodeName and KeyIdList #keyid,#nodename")
    def 'Remove Ntp Key Details for valid nodes for single node case'() {
        given: 'validNode,keyid,nodename,responseMessage'
        setCommandData(keyid,nodename)
        setDataForManagedObject(nodename)
        setDataForNodeExists(nodeStatus, nodename, true,true)
        nscsJobCacheHandler.insertJob(_) >> jobStatusRecord
        when: 'execute Ntp Remove Handler process method'
        NscsCommandResponse response=ntpRemoveHandler.process(command, context)

        then:
        assert response.message(responseMessage)

        where:
        nodeStatus                |        keyid       |    nodename           |          responseMessage
        'validNode'               |        [1, 2]| 'LTE01ERBS00026'      |    allValidNodesMessage
        'validNode'               |        ['all']| 'LTE01ERBS00027'      |    allValidNodesMessage
        'validNode'               |        ['invalid']| 'LTE01ERBS00028'      |    allValidNodesMessage
        'normNodeNull'            |        [1, 2]| 'LTE01ERBS00001'      |    NtpConstants.NTP_REMOVE_NOT_EXECUTED
        'isNodeSynchronized'      |        [1, 2]| 'LTE01ERBS00004'      |    NtpConstants.NTP_REMOVE_NOT_EXECUTED
        'InvalidNode'             |        [1, 2]| 'node1234'            |    NtpConstants.NTP_REMOVE_NOT_EXECUTED
    }

    @Unroll("Initiate NtpRemoveHandler to remove ntp key detials on node using valid xmlFlie")
    def 'Remove Ntp Key Details for valid nodes for valid xml case'() {

        given:'filePath,responseMessage'
        setCommandDataForxmlFile(filePath)
        fileUtil.isValidFileExtension(_,_) >> true
        setDataForManagedObject('LTE01ERBS00001')
        setDataForNodeExists('validNode','LTE01ERBS00001', true,true)
        setDataForNodeExists('InvalidNode','node1234', true,true)
        nscsJobCacheHandler.insertJob(_) >> jobStatusRecord
        when: 'execute Ntp Remove Handler process method'
        NscsCommandResponse response=ntpRemoveHandler.process(command, context)

        then:
        assert response.message(responseMessage)

        where:
        filePath                                        |          responseMessage
        'src/test/resources/TrustedNtp/ntpremove.xml'                  |     partialValidNodesMessage
    }

    @Unroll("Initiate NtpRemoveHandler to remove ntp key detials on node using invalid xmlFlie")
    def 'Remove Ntp Key Details for valid nodes for invalid xml case'() {

        given:'filePath,responseMessage'
        setCommandDataForInvalidxmlFile(filePath)
        when: 'execute Ntp Remove Handler process method'
        NscsCommandResponse response=ntpRemoveHandler.process(command, context)

        then:
        def assertion = thrown(exception)

        where:
        filePath                                        |          exception
        'src/test/resources/TrustedNtp/ntpremove.xml'          |  InvalidArgumentValueException
    }

    @Unroll("Initiate NtpRemoveHandler to remove ntp key detials on node using invalid xmlFlie other cases")
    def 'Remove Ntp Key Details for valid nodes for invalid xml case other cases'() {

        given:'filePath,responseMessage'
        setCommandDataForInvalidxmlFile(filePath)
        fileUtil.isValidFileExtension(_,_)>> true

        when: 'execute Ntp Remove Handler process method'
        NscsCommandResponse response=ntpRemoveHandler.process(command, context)

        then:
        def assertion = thrown(exception)

        where:
        filePath                                             |          exception
        'src/test/resources/TrustedNtp/ntpremoveempty.xml'          |  EmptyFileException
        'src/test/resources/TrustedNtp/ntpremoveinvalid.xml'        |  InvalidInputXMLFileException
        'src/test/resources/TrustedNtp/ntpremovefile.xml'           |  EmptyFileException
    }
}
