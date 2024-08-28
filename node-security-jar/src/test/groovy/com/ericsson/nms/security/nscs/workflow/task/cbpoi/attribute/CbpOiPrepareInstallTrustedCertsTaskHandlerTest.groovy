/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.security.nscs.workflow.task.cbpoi.attribute

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.nms.security.nscs.util.NscsCbpOiTrustedEntityInfo
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiPrepareInstallTrustedCertsTask
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys

import spock.lang.Shared

class CbpOiPrepareInstallTrustedCertsTaskHandlerTest extends CdiSpecification {

    @ObjectUnderTest
    CbpOiPrepareInstallTrustedCertsTaskHandler prepareInstallTrustedCertsTaskHandler

    @MockedImplementation
    NscsLogger nscsLogger

    @MockedImplementation
    NscsCMReaderService readerService;

    @MockedImplementation
    NscsCapabilityModelService nscsCapabilityModelService;

    @MockedImplementation
    NscsNodeUtility nscsNodeUtility;

    @MockedImplementation
    NormalizableNodeReference normalizableNodeRef

    static def nodeName = "cloud257-vdu"
    static def enrollmentCaName = "NE_OAM_CA"
    static def enmOamCaName = "ENM_OAM_CA"
    static def rootCaName = "ENM_Pki_Root_CA"
    static def rootCaSubjectDN = "O=Ericsson, CN=" + rootCaName
    static def infrastructureCaName = "ENM_Infrastructure_CA"
    static def infrastructureCaSubjectDN = "O=Ericsson, CN=" + infrastructureCaName
    static def truststoreFdn = "ManagedElement=" + nodeName + ",truststore=1,"
    static def serializeCertificatesLogMessage = "Installing certificates"

    @Shared
    def oamCaTrustedEntityInfo = new NscsCbpOiTrustedEntityInfo(enrollmentCaName, 123, rootCaSubjectDN, "NE_OAM_CA PEM Certificate")

    @Shared
    def oamTrustedEntityInfo = new NscsCbpOiTrustedEntityInfo(enmOamCaName, 456, infrastructureCaSubjectDN, "ENM_OAM_CA PEM Certificate")

    @Shared
    def task = new CbpOiPrepareInstallTrustedCertsTask()

    def setup() {
        NodeReference nodeRef = Mock()
        nodeRef.getName() >> "cloud257-vdu"
        nodeRef.getName() >> nodeName
        readerService.getNormalizableNodeReference(_) >> normalizableNodeRef
        normalizableNodeRef.getFdn()>> nodeName
        task.setNode(nodeRef)
    }

    def 'When handler receives Trusted Entities, then MoParams are prepared for following MoAction task'() {
        given: 'handler receives list with 2 Trusted Entities belonging to different Trust Categories'
        def trustedEntitiesInfoStr = NscsObjectSerializer.writeObject([
            oamTrustedEntityInfo,
            oamCaTrustedEntityInfo
        ])
        def outputParams = [(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString()): trustedEntitiesInfoStr]
        task.setOutputParams(outputParams)
        nscsNodeUtility.getSingleInstanceMoFdn(normalizableNodeRef.getFdn(), _) >> truststoreFdn

        when: 'task handler is invoked'
        prepareInstallTrustedCertsTaskHandler.processTask(task)

        then: 'MoParams are prepared with data from both trusted certificates'
        1 * nscsLogger.info(task , _ as String, _ as List<String>) >> {
            String logMessage = it[1]
            def certificateNamesList = (it[2][0])
            assert logMessage.contains(serializeCertificatesLogMessage)
            assert certificateNamesList instanceof List<String>
            assert 2 == certificateNamesList.size()
            assert certificateNamesList.get(0) == enmOamCaName
            assert certificateNamesList.get(1) == enrollmentCaName
        }
    }

    def 'when no output params are received, then exception is thrown' () {
        given: 'no output params are received'
        task.setOutputParams(null)
        when: 'task handler is invoked'
        prepareInstallTrustedCertsTaskHandler.processTask(task)
        then: 'exception is thrown'
        thrown(WorkflowTaskException)
    }
}

