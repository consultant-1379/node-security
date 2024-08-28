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
package com.ericsson.nms.security.nscs.handler.command.impl;

import javax.ejb.EJB
import javax.inject.Inject
import javax.xml.bind.*

import org.apache.commons.collections.map.HashedMap
import org.codehaus.groovy.ast.stmt.SwitchStatement

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.*
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsCommandType
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand.NscsPropertyCommandInvoker
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager
import com.ericsson.nms.security.nscs.api.command.types.TrustDistributeCommand
import com.ericsson.nms.security.nscs.data.*
import com.ericsson.nms.security.nscs.data.ModelDefinition.*
import com.ericsson.nms.security.nscs.enrollmentinfo.utility.FileConstants
import com.ericsson.nms.security.nscs.handler.CommandContext
import com.ericsson.nms.security.nscs.manager.*
import com.ericsson.nms.security.nscs.utilities.*
import com.ericsson.oss.services.dto.JobStatusRecord
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler
import com.ericsson.oss.services.enums.JobGlobalStatusEnum

/**
 * This class prepares setup data to distribute external ca trusted certificates
 *
 *  @author xkumkam
 *
 */

public class ExternalCATrustDistributionSetupData extends CdiSpecification{

    @Inject
    NscsJobCacheHandler nscsJobCacheHandler

    @MockedImplementation
    CommandContext context

    @Inject
    JobStatusRecord jobStatusRecord

    @Inject
    TrustDistributeCommand command

    @Inject
    TestSetupInitializer testSetupInitializer

    @Inject
    XmlValidatorUtility xmlValidatorUtility

    @Inject
    XMLUnMarshallerUtility xmlUnMarshallerUtility

    final protected Map<String, Object> commandMap = new HashMap<String, Object>()

    /**
     * Customize the injection provider
     *
     * */
    @Override
    public Object addAdditionalInjectionProperties(final InjectionProperties injectionProperties) {
        injectionProperties.autoLocateFrom('com.ericsson.nms.security.nscs.handler.command.impl')
        injectionProperties.autoLocateFrom('com.ericsson.nms.security.nscs.manager')
    }


    def setupJobStatusRecord(){
        UUID jobId = UUID.randomUUID()
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.PENDING)
        jobStatusRecord.setUserId("user")
        jobStatusRecord.setJobId(jobId)
        nscsJobCacheHandler.insertJob(_) >> jobStatusRecord
    }

    def setCommandData(final String filePath, final String certType) {
        def fileContent = testSetupInitializer.getFileContent(filePath)
        commandMap.put(FileConstants.FILE_URI, fileContent)
        commandMap.put("certtype", certType)
        commandMap.put("extca", null)
        commandMap.put("xmlfile", fileContent)
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandType(NscsCommandType.TRUST_DISTRIBUTE)
        command.setCommandInvokerValue(commandInvokerValue)
        command.setProperties(commandMap)
    }

    def setTrustCategoryCommandData(final String filePath, final String trustCategory) {
        def fileContent = testSetupInitializer.getFileContent(filePath)
        commandMap.put(FileConstants.FILE_URI, fileContent)
        commandMap.put("trustcategory", trustCategory)
        commandMap.put("extca", null)
        commandMap.put("xmlfile", fileContent)
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandType(NscsCommandType.TRUST_DISTRIBUTE)
        command.setCommandInvokerValue(commandInvokerValue)
        command.setProperties(commandMap)
    }
    
    def setExtCaCommandErrorData(final String filePath, final String trustCategory) {
        def fileContent = testSetupInitializer.getFileContent(filePath)
        commandMap.put(FileConstants.FILE_URI, fileContent)
        commandMap.put("trustcategory", trustCategory)
        commandMap.put("extca", null)
        NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI
        command.setCommandType(NscsCommandType.TRUST_DISTRIBUTE)
        command.setCommandInvokerValue(commandInvokerValue)
        command.setProperties(commandMap)
    }
}
