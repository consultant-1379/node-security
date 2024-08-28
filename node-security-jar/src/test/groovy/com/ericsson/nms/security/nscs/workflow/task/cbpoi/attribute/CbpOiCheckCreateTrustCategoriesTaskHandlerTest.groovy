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

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.security.nscs.api.exception.DataAccessException
import com.ericsson.nms.security.nscs.util.NscsCbpOiTrustedEntityInfo
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiCheckCreateTrustCategoriesTask
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys
import com.ericsson.oss.services.security.nscs.utils.CbpOiNodeDataSetup

import spock.lang.Shared

class CbpOiCheckCreateTrustCategoriesTaskHandlerTest extends CbpOiNodeDataSetup {

    @ObjectUnderTest
    CbpOiCheckCreateTrustCategoriesTaskHandler checkCreateTrustCategoriesTaskHandler

    static def nodeName = "cloud257-vdu"
    static def enrollmentCaName = "NE_OAM_CA"
    static def enmOamCaName = "ENM_OAM_CA"
    static def rootCaName = "ENM_Pki_Root_CA"
    static def rootCaSubjectDN = "O=Ericsson, CN=" + rootCaName
    static def infrastructureCaName = "ENM_Infrastructure_CA"
    static def infrastructureCaSubjectDN = "O=Ericsson, CN=" + infrastructureCaName
    static def oamTrustCategoryName = "oamTrustCategory"
    static def oamCmpCaTrustCategoryName = "oamCmpCaTrustCategory"
    static def truststoreFdn = "ManagedElement=" + nodeName + ",truststore=1,"
    static def certificatesMoType = "certificates"

    @Shared
    def oamCaTrustedEntityInfo = new NscsCbpOiTrustedEntityInfo(enrollmentCaName, 123, rootCaSubjectDN, "NE_OAM_CA PEM Certificate")

    @Shared
    def oamTrustedEntityInfo = new NscsCbpOiTrustedEntityInfo(enmOamCaName, 456, infrastructureCaSubjectDN, "ENM_OAM_CA PEM Certificate")

    @Shared
    def task = new CbpOiCheckCreateTrustCategoriesTask(nodeName)

    def setup() {
        oamCaTrustedEntityInfo.setTrustCategoryName(oamCmpCaTrustCategoryName)
        oamTrustedEntityInfo.setTrustCategoryName(oamTrustCategoryName)
    }

    def 'When handler receives Trusted Entities, and truststore is not present under MeContext and ManagedElement, then all Trust Categories are created'() {
        given: 'handler receives list with 2 Trusted Entities belonging to different Trust Categories'
        def trustedEntitiesInfoStr = NscsObjectSerializer.writeObject([
            oamTrustedEntityInfo,
            oamCaTrustedEntityInfo
        ])
        def outputParams = [(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString()) : trustedEntitiesInfoStr]
        and: "task for node"
        task.setOutputParams(outputParams)

        and: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)

        when: 'task handler is invoked'
        def result = checkCreateTrustCategoriesTaskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);

        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserialized task result should be not null"
        deserializedTaskResult != null
        and: "task result should be PASSED"
        deserializedTaskResult.getResult() == "PASSED"
        and: 'truststore is created on node'
        def truststoreFdn = 'MeContext='+nodeName+',ManagedElement='+nodeName+',truststore=1'
        findMoByFdn(truststoreFdn) != null
        and: 'both Trust Categories are created on node'
        def oamTrustCategoryFdn = truststoreFdn+',certificates='+oamTrustCategoryName
        findMoByFdn(oamTrustCategoryFdn) != null
        def oamCmpCaTrustCategoryFdn = truststoreFdn+',certificates='+oamCmpCaTrustCategoryName
        findMoByFdn(oamCmpCaTrustCategoryFdn) != null
    }

    def 'When handler receives no Trusted Entities, and truststore is not present under MeContext and ManagedElement, then truststore is created and no Trust Categories are created'() {
        given: 'handler receives list with no Trusted Entities'
        def trustedEntitiesInfoStr = NscsObjectSerializer.writeObject([])
        def outputParams = [(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString()) : trustedEntitiesInfoStr]
        and: "task for node"
        task.setOutputParams(outputParams)

        and: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)

        when: 'task handler is invoked'
        def result = checkCreateTrustCategoriesTaskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);

        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserialized task result should be not null"
        deserializedTaskResult != null
        and: "task result should be PASSED"
        deserializedTaskResult.getResult() == "PASSED"
        and: 'truststore is created on node'
        def truststoreFdn = 'MeContext='+nodeName+',ManagedElement='+nodeName+',truststore=1'
        findMoByFdn(truststoreFdn) != null
        and: 'no Trust Categories are created on node'
        def oamTrustCategoryFdn = truststoreFdn+',certificates='+oamTrustCategoryName
        findMoByFdn(oamTrustCategoryFdn) == null
        def oamCmpCaTrustCategoryFdn = truststoreFdn+',certificates='+oamCmpCaTrustCategoryName
        findMoByFdn(oamCmpCaTrustCategoryFdn) == null
    }

    def 'When handler receives Trusted Entities, and truststore is not present under ManagedElement, then all Trust Categories are created'() {
        given: 'handler receives list with 2 Trusted Entities belonging to different Trust Categories'
        def trustedEntitiesInfoStr = NscsObjectSerializer.writeObject([
            oamTrustedEntityInfo,
            oamCaTrustedEntityInfo
        ])
        def outputParams = [(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString()) : trustedEntitiesInfoStr]
        and: "task for node"
        task.setOutputParams(outputParams)

        and: "node created with ManagedElement"
        createNodeWithManagedElement(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)

        when: 'task handler is invoked'
        def result = checkCreateTrustCategoriesTaskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);

        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserialized task result should be not null"
        deserializedTaskResult != null
        and: "task result should be PASSED"
        deserializedTaskResult.getResult() == "PASSED"
        and: 'truststore is created on node'
        def truststoreFdn = 'ManagedElement='+nodeName+',truststore=1'
        findMoByFdn(truststoreFdn) != null
        and: 'both Trust Categories are created on node'
        def oamTrustCategoryFdn = truststoreFdn+',certificates='+oamTrustCategoryName
        findMoByFdn(oamTrustCategoryFdn) != null
        def oamCmpCaTrustCategoryFdn = truststoreFdn+',certificates='+oamCmpCaTrustCategoryName
        findMoByFdn(oamCmpCaTrustCategoryFdn) != null
    }

    def 'When handler receives Trusted Entities, and truststore is not present under MeContext for node supporting ManagedElement as node root MO, then all Trust Categories are created'() {
        given: 'handler receives list with 2 Trusted Entities belonging to different Trust Categories'
        def trustedEntitiesInfoStr = NscsObjectSerializer.writeObject([
            oamTrustedEntityInfo,
            oamCaTrustedEntityInfo
        ])
        def outputParams = [(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString()) : trustedEntitiesInfoStr]
        and: "task for node"
        task.setOutputParams(outputParams)

        and: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)

        when: 'task handler is invoked'
        def result = checkCreateTrustCategoriesTaskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);

        then: "DataAccessException exception should be thrown"
        thrown(DataAccessException)
    }

    def 'When handler receives Trusted Entities, and truststore is not present under MeContext for node not supporting ManagedElement as node root MO, then all Trust Categories are created'() {
        given: 'handler receives list with 2 Trusted Entities belonging to different Trust Categories'
        def trustedEntitiesInfoStr = NscsObjectSerializer.writeObject([
            oamTrustedEntityInfo,
            oamCaTrustedEntityInfo
        ])
        def outputParams = [(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString()) : trustedEntitiesInfoStr]
        and: "task for node"
        task.setOutputParams(outputParams)

        and: "node created with MeContext"
        createNodeWithMeContext(SHARED_CNF_TARGET_TYPE, SHARED_CNF_TARGET_MODEL_IDENTITY, nodeName)

        when: 'task handler is invoked'
        def result = checkCreateTrustCategoriesTaskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);

        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserialized task result should be not null"
        deserializedTaskResult != null
        and: "task result should be PASSED"
        deserializedTaskResult.getResult() == "PASSED"
        and: 'truststore is created on node'
        def truststoreFdn = 'MeContext='+nodeName+',truststore=1'
        findMoByFdn(truststoreFdn) != null
        and: 'both Trust Categories are created on node'
        def oamTrustCategoryFdn = truststoreFdn+',certificates='+oamTrustCategoryName
        findMoByFdn(oamTrustCategoryFdn) != null
        def oamCmpCaTrustCategoryFdn = truststoreFdn+',certificates='+oamCmpCaTrustCategoryName
        findMoByFdn(oamCmpCaTrustCategoryFdn) != null
    }

    def 'When handler receives Trusted Entities, and truststore is present under MeContext and ManagedElement, then all Trust Categories are created'() {
        given: 'handler receives list with 2 Trusted Entities belonging to different Trust Categories'
        def trustedEntitiesInfoStr = NscsObjectSerializer.writeObject([
            oamTrustedEntityInfo,
            oamCaTrustedEntityInfo
        ])
        def outputParams = [(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString()) : trustedEntitiesInfoStr]
        and: "task for node"
        task.setOutputParams(outputParams)

        and: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: 'with truststore under ManagedElement'
        createTruststoreUnderManagedElement()

        when: 'task handler is invoked'
        def result = checkCreateTrustCategoriesTaskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);

        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserialized task result should be not null"
        deserializedTaskResult != null
        and: "task result should be PASSED"
        deserializedTaskResult.getResult() == "PASSED"
        and: 'truststore is still present on node'
        def truststoreFdn = 'MeContext='+nodeName+',ManagedElement='+nodeName+',truststore=1'
        findMoByFdn(truststoreFdn) != null
        and: 'both Trust Categories are created on node'
        def oamTrustCategoryFdn = truststoreFdn+',certificates='+oamTrustCategoryName
        findMoByFdn(oamTrustCategoryFdn) != null
        def oamCmpCaTrustCategoryFdn = truststoreFdn+',certificates='+oamCmpCaTrustCategoryName
        findMoByFdn(oamCmpCaTrustCategoryFdn) != null
    }

    def 'When handler receives Trusted Entities, and truststore is present under ManagedElement, then all Trust Categories are created'() {
        given: 'handler receives list with 2 Trusted Entities belonging to different Trust Categories'
        def trustedEntitiesInfoStr = NscsObjectSerializer.writeObject([
            oamTrustedEntityInfo,
            oamCaTrustedEntityInfo
        ])
        def outputParams = [(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString()) : trustedEntitiesInfoStr]
        and: "task for node"
        task.setOutputParams(outputParams)

        and: "node created with ManagedElement"
        createNodeWithManagedElement(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: 'with truststore under ManagedElement'
        createTruststoreUnderManagedElement()

        when: 'task handler is invoked'
        def result = checkCreateTrustCategoriesTaskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);

        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserialized task result should be not null"
        deserializedTaskResult != null
        and: "task result should be PASSED"
        deserializedTaskResult.getResult() == "PASSED"
        and: 'truststore is created on node'
        def truststoreFdn = 'ManagedElement='+nodeName+',truststore=1'
        findMoByFdn(truststoreFdn) != null
        and: 'both Trust Categories are created on node'
        def oamTrustCategoryFdn = truststoreFdn+',certificates='+oamTrustCategoryName
        findMoByFdn(oamTrustCategoryFdn) != null
        def oamCmpCaTrustCategoryFdn = truststoreFdn+',certificates='+oamCmpCaTrustCategoryName
        findMoByFdn(oamCmpCaTrustCategoryFdn) != null
    }

    def 'When handler receives Trusted Entities, and truststore is present under MeContext, then all Trust Categories are created'() {
        given: 'handler receives list with 2 Trusted Entities belonging to different Trust Categories'
        def trustedEntitiesInfoStr = NscsObjectSerializer.writeObject([
            oamTrustedEntityInfo,
            oamCaTrustedEntityInfo
        ])
        def outputParams = [(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString()) : trustedEntitiesInfoStr]
        and: "task for node"
        task.setOutputParams(outputParams)

        and: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: 'with truststore under MeContext'
        createTruststoreUnderMeContext()

        when: 'task handler is invoked'
        def result = checkCreateTrustCategoriesTaskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);

        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserialized task result should be not null"
        deserializedTaskResult != null
        and: "task result should be PASSED"
        deserializedTaskResult.getResult() == "PASSED"
        and: 'truststore is created on node'
        def truststoreFdn = 'MeContext='+nodeName+',truststore=1'
        findMoByFdn(truststoreFdn) != null
        and: 'both Trust Categories are created on node'
        def oamTrustCategoryFdn = truststoreFdn+',certificates='+oamTrustCategoryName
        findMoByFdn(oamTrustCategoryFdn) != null
        def oamCmpCaTrustCategoryFdn = truststoreFdn+',certificates='+oamCmpCaTrustCategoryName
        findMoByFdn(oamCmpCaTrustCategoryFdn) != null
    }

    def 'When handler receives Trusted Entities, and all Trust Categories are already installed, then no object is created'() {
        given: 'handler receives list with 2 Trusted Entities belonging to different Trust Categories'
        def trustedEntitiesInfoStr = NscsObjectSerializer.writeObject([
            oamTrustedEntityInfo,
            oamCaTrustedEntityInfo
        ])
        def outputParams = [(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString()) : trustedEntitiesInfoStr]
        and: "task for node"
        task.setOutputParams(outputParams)

        and: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: 'with truststore under ManagedElement'
        createTruststoreUnderManagedElement()
        and: 'with trust categories under truststore'
        createCertificatesUnderTruststore(oamTrustCategoryName)
        createCertificatesUnderTruststore(oamCmpCaTrustCategoryName)

        when: 'task handler is invoked'
        def result = checkCreateTrustCategoriesTaskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);

        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserialized task result should be not null"
        deserializedTaskResult != null
        and: "task result should be PASSED"
        deserializedTaskResult.getResult() == "PASSED"
        and: 'truststore is still present on node'
        def truststoreFdn = 'MeContext='+nodeName+',ManagedElement='+nodeName+',truststore=1'
        findMoByFdn(truststoreFdn) != null
        and: 'both Trust Categories are still present on node'
        def oamTrustCategoryFdn = truststoreFdn+',certificates='+oamTrustCategoryName
        findMoByFdn(oamTrustCategoryFdn) != null
        def oamCmpCaTrustCategoryFdn = truststoreFdn+',certificates='+oamCmpCaTrustCategoryName
        findMoByFdn(oamCmpCaTrustCategoryFdn) != null
    }

    def 'when no output params are received, then exception is thrown' () {
        given: 'no output params are received'
        task.setOutputParams(null)
        when: 'task handler is invoked'
        checkCreateTrustCategoriesTaskHandler.processTask(task)
        then: 'exception is thrown'
        thrown(WorkflowTaskException)
    }
}

