/*-----------------------------------------------------------------------------
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
package com.ericsson.oss.services.security.nscs.dps

import javax.inject.Inject

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.security.nscs.api.exception.DataAccessException
import com.ericsson.nms.security.nscs.api.exception.DataAccessSystemException
import com.ericsson.nms.security.nscs.api.exception.MoTypeNotFoundException
import com.ericsson.nms.security.nscs.api.exception.TooManyChildMosException
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject
import com.ericsson.oss.services.security.nscs.utils.CbpOiNodeDataSetup

import spock.lang.Unroll

class NscsDpsUtilsTest extends CbpOiNodeDataSetup {

    @ObjectUnderTest
    NscsDpsUtils nscsDpsUtils

    @Inject
    NscsCMReaderService readerService

    private nodeName = "vDU00001"

    def setup() {
        NscsCMReaderService.deploymentEnv = NscsCMReaderService.DeploymentEnvironment.PRODUCTION
    }

    def 'object under test injection' () {
        expect:
        nscsDpsUtils != null
    }

    @Unroll
    def 'create node hierarchy top MO with MeContext and ManagedElement #toptype #topns' () {
        given: 'node created with MeContext'
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: 'with ManagedElement under MeContext'
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: 'normalizable node reference is set'
        def NodeReference nodeReference = new NodeRef(nodeName)
        def NormalizableNodeReference normalizableNodeReference = readerService.getNormalizableNodeReference(nodeReference)
        when: '#toptype is created'
        String moType = toptype
        String refMimNamespace = topns
        String moName = "1"
        def mo = nscsDpsUtils.createNodeHierarchyTopMo(normalizableNodeReference, refMimNamespace, moType, moName)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: 'created MO is not null'
        mo != null
        and: 'MO has been actually created'
        def moFdn = 'MeContext='+nodeName+',ManagedElement='+nodeName+','+toptype+'=1'
        nscsDpsUtils.getMoByFdn(moFdn) != null
        where:
        toptype << [
            "keystore",
            "truststore",
            "system"
        ]
        topns << [
            "urn:ietf:params:xml:ns:yang:ietf-keystore",
            "urn:ietf:params:xml:ns:yang:ietf-truststore",
            "urn:ietf:params:xml:ns:yang:ietf-system"
        ]
    }

    @Unroll
    def 'create node hierarchy top MO with ManagedElement #toptype #topns' () {
        given: 'node created with ManagedElement'
        createNodeWithManagedElement(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: 'normalizable node reference is set'
        def NodeReference nodeReference = new NodeRef(nodeName)
        def NormalizableNodeReference normalizableNodeReference = readerService.getNormalizableNodeReference(nodeReference)
        when: '#toptype is created'
        String moType = toptype
        String refMimNamespace = topns
        String moName = "1"
        def mo = nscsDpsUtils.createNodeHierarchyTopMo(normalizableNodeReference, refMimNamespace, moType, moName)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: 'created MO is not null'
        mo != null
        and: 'MO has been actually created'
        def moFdn = 'ManagedElement='+nodeName+','+toptype+'=1'
        nscsDpsUtils.getMoByFdn(moFdn) != null
        where:
        toptype << [
            "keystore",
            "truststore",
            "system"
        ]
        topns << [
            "urn:ietf:params:xml:ns:yang:ietf-keystore",
            "urn:ietf:params:xml:ns:yang:ietf-truststore",
            "urn:ietf:params:xml:ns:yang:ietf-system"
        ]
    }

    @Unroll
    def 'create node hierarchy top MO with MeContext #toptype #topns for node supporting ManagedElement as node root' () {
        given: 'node created with MeContext'
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: 'normalizable node reference is set'
        def NodeReference nodeReference = new NodeRef(nodeName)
        def NormalizableNodeReference normalizableNodeReference = readerService.getNormalizableNodeReference(nodeReference)
        when: '#toptype is created'
        String moType = toptype
        String refMimNamespace = topns
        String moName = "1"
        def mo = nscsDpsUtils.createNodeHierarchyTopMo(normalizableNodeReference, refMimNamespace, moType, moName)
        then: "DataAccessException exception should be thrown"
        thrown(DataAccessException)
        where:
        toptype << [
            "keystore",
            "truststore",
            "system"
        ]
        topns << [
            "urn:ietf:params:xml:ns:yang:ietf-keystore",
            "urn:ietf:params:xml:ns:yang:ietf-truststore",
            "urn:ietf:params:xml:ns:yang:ietf-system"
        ]
    }

    @Unroll
    def 'create node hierarchy top MO with MeContext #toptype #topns for node not supporting ManagedElement as node root' () {
        given: 'node created with MeContext'
        createNodeWithMeContext(SHARED_CNF_TARGET_TYPE, SHARED_CNF_TARGET_MODEL_IDENTITY, nodeName)
        and: 'normalizable node reference is set'
        def NodeReference nodeReference = new NodeRef(nodeName)
        def NormalizableNodeReference normalizableNodeReference = readerService.getNormalizableNodeReference(nodeReference)
        when: '#toptype is created'
        String moType = toptype
        String refMimNamespace = topns
        String moName = "1"
        def mo = nscsDpsUtils.createNodeHierarchyTopMo(normalizableNodeReference, refMimNamespace, moType, moName)
        then: "No exception should be thrown"
        noExceptionThrown()
        and: 'created MO is not null'
        mo != null
        and: 'MO has been actually created'
        def moFdn = 'MeContext='+nodeName+','+toptype+'=1'
        nscsDpsUtils.getMoByFdn(moFdn) != null
        where:
        toptype << [
            "keystore",
            "truststore",
            "system"
        ]
        topns << [
            "urn:ietf:params:xml:ns:yang:ietf-keystore",
            "urn:ietf:params:xml:ns:yang:ietf-truststore",
            "urn:ietf:params:xml:ns:yang:ietf-system"
        ]
    }

    @Unroll
    def 'create node hierarchy top MO without MeContext and without ManagedElement #toptype #topns' () {
        given: 'normalizable node reference is set'
        def NodeReference nodeReference = new NodeRef(nodeName)
        def NormalizableNodeReference normalizableNodeReference = readerService.getNormalizableNodeReference(nodeReference)
        when: '#toptype is created'
        String moType = toptype
        String refMimNamespace = topns
        String moName = "1"
        def mo = nscsDpsUtils.createNodeHierarchyTopMo(normalizableNodeReference, refMimNamespace, moType, moName)
        then: "DataAccessSystemException exception should be thrown"
        thrown(DataAccessSystemException)
        where:
        toptype << [
            "keystore",
            "truststore",
            "system"
        ]
        topns << [
            "urn:ietf:params:xml:ns:yang:ietf-keystore",
            "urn:ietf:params:xml:ns:yang:ietf-truststore",
            "urn:ietf:params:xml:ns:yang:ietf-system"
        ]
    }

    def "update MO with null MO"() {
        given:
        when:
        nscsDpsUtils.updateMo(null, null)
        then:
        thrown(DataAccessSystemException)
    }

    @Unroll
    def "update MO without attributes"() {
        given:
        ManagedObject mo = mock(ManagedObject)
        when:
        nscsDpsUtils.updateMo(mo, attrs)
        then:
        notThrown(Exception)
        where:
        attrs || expected
        null  || false
        [ : ] || false
    }

    def "update MO with exception"() {
        given:
        def attributes = [ "attr1" : "value1", "attr2" : "value2" ]
        ManagedObject mo = mock(ManagedObject)
        mo.setAttributes(_) >> {
            throw new DataAccessException("error")
        }
        when:
        nscsDpsUtils.updateMo(mo, attributes)
        then:
        thrown(DataAccessException)
    }

    def "delete MO"() {
        given:
        ManagedObject mo = mock(ManagedObject)
        when:
        nscsDpsUtils.deleteMo(mo)
        then:
        notThrown(Exception)
    }

    def "delete null MO"() {
        given:
        when:
        nscsDpsUtils.deleteMo(null)
        then:
        thrown(DataAccessSystemException)
    }

    def "get MO by FDN with null FDN"() {
        given:
        when:
        ManagedObject mo = nscsDpsUtils.getMoByFdn(null)
        then:
        thrown(DataAccessSystemException)
    }

    def "get node hierarchy MO with invalid node reference"() {
        given:
        def NormalizableNodeReference normalizableNodeReference = Mock()
        when:
        nscsDpsUtils.getNodeHierarchyTopMo(normalizableNodeReference, "urn:ietf:params:xml:ns:yang:ietf-keystore", "asymmetric-key", "oamNodeCredential")
        then:
        thrown(DataAccessSystemException)
    }

    def 'get node root MO MO with ManagedElement from normalizable' () {
        given: 'node created with ManagedElement'
        createNodeWithManagedElement(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: 'normalizable node reference is set'
        def NodeReference nodeReference = new NodeRef(nodeName)
        def NormalizableNodeReference normalizableNodeReference = readerService.getNormalizableNodeReference(nodeReference)
        when:
        def mo = nscsDpsUtils.getNodeRootMo(normalizableNodeReference)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: 'MO is not null'
        mo != null
    }

    def 'get node root MO with ManagedElement from normalized' () {
        given: 'node created with ManagedElement'
        createNodeWithManagedElement(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: 'normalized node reference is set'
        def NodeReference nodeReference = new NodeRef(nodeName)
        def NormalizableNodeReference normalizedNodeReference = readerService.getNormalizedNodeReference(nodeReference)
        when:
        def mo = nscsDpsUtils.getNodeRootMo(normalizedNodeReference)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: 'MO is not null'
        mo != null
    }

    def "get node root MO with invalid node reference"() {
        given:
        def NormalizableNodeReference normalizableNodeReference = Mock()
        when:
        ManagedObject mo = nscsDpsUtils.getNodeRootMo(normalizableNodeReference)
        then:
        thrown(DataAccessSystemException)
    }

    def "get only MO with null mirror root FDN"() {
        given:
        def NormalizableNodeReference normalizableNodeReference = Mock()
        normalizableNodeReference.getName() >> nodeName
        normalizableNodeReference.getFdn() >> null
        normalizableNodeReference.getNeType() >> RADIONODE_TARGET_TYPE
        normalizableNodeReference.getOssModelIdentity() >> RADIONODE_TARGET_MODEL_IDENTITY
        when:
        nscsDpsUtils.getOnlyMO(normalizableNodeReference, "ECIM_LDAP_Authentication", "Ldap")
        then:
        thrown(DataAccessSystemException)
    }

    @Unroll
    def "get valid existing child MO by name"() {
        given:
        ManagedObject childMo = mock(ManagedObject)
        childMo.getType() >> type
        childMo.getNamespace() >> ns
        childMo.getName() >> name
        ManagedObject parentMo = mock(ManagedObject)
        parentMo.getNamespace() >> "urn:ietf:params:xml:ns:yang:ietf-keystore"
        parentMo.getType() >> "keystore"
        parentMo.getVersion() >> "2019.11.20"
        parentMo.getChildrenSize() >> 1
        parentMo.getChildren() >> [childMo]
        def NormalizableNodeReference normalizableNodeReference = Mock()
        normalizableNodeReference.getNeType() >> "vDU"
        normalizableNodeReference.getOssModelIdentity() >> "0.5.1"
        when:
        def mo = nscsDpsUtils.getChildMo(parentMo, normalizableNodeReference, "asymmetric-keys", "1")
        then:
        notThrown(Exception)
        and:
        mo != null
        where:
        type              | ns                                          | name || expected
        "asymmetric-keys" | "urn:ietf:params:xml:ns:yang:ietf-keystore" | "1"  || true
    }

    @Unroll
    def "get valid child not existing MO by name"() {
        given:
        ManagedObject childMo = mock(ManagedObject)
        childMo.getType() >> type
        childMo.getNamespace() >> ns
        childMo.getName() >> name
        ManagedObject parentMo = mock(ManagedObject)
        parentMo.getNamespace() >> "urn:ietf:params:xml:ns:yang:ietf-keystore"
        parentMo.getType() >> "keystore"
        parentMo.getVersion() >> "2019.11.20"
        parentMo.getChildrenSize() >> 1
        parentMo.getChildren() >> [childMo]
        def NormalizableNodeReference normalizableNodeReference = Mock()
        normalizableNodeReference.getNeType() >> "vDU"
        normalizableNodeReference.getOssModelIdentity() >> "0.5.1"
        when:
        def mo = nscsDpsUtils.getChildMo(parentMo, normalizableNodeReference, "asymmetric-keys", "1")
        then:
        notThrown(Exception)
        and:
        mo == null
        where:
        type                 | ns                                                     | name || expected
        "asymmetric-keys"    | "urn:ietf:params:xml:ns:yang:ietf-keystore"            | "2"  || false
        "keystore\$\$cmp"    | "urn:rdns:com:ericsson:oammodel:ericsson-keystore-ext" | "1"  || false
    }

    def "get invalid child MO by name"() {
        given:
        ManagedObject childMo = mock(ManagedObject)
        childMo.getType() >> "asymmetric-keys"
        childMo.getNamespace() >> "urn:ietf:params:xml:ns:yang:ietf-keystore"
        childMo.getName() >> "1"
        ManagedObject parentMo = mock(ManagedObject)
        parentMo.getNamespace() >> "urn:ietf:params:xml:ns:yang:ietf-keystore"
        parentMo.getType() >> "keystore"
        parentMo.getVersion() >> "2019.11.20"
        parentMo.getChildrenSize() >> 1
        parentMo.getChildren() >> [childMo]
        def NormalizableNodeReference normalizableNodeReference = Mock()
        normalizableNodeReference.getNeType() >> "vDU"
        normalizableNodeReference.getOssModelIdentity() >> "0.5.1"
        when:
        def mo = nscsDpsUtils.getChildMo(parentMo, normalizableNodeReference, "invalid-type", "1")
        then:
        thrown(MoTypeNotFoundException)
    }

    def "get valid not existent only child MO with children of other type"() {
        given:
        ManagedObject firstChildMo = mock(ManagedObject)
        firstChildMo.getType() >> "asymmetric-keys"
        firstChildMo.getName() >> "1"
        ManagedObject secondChildMo = mock(ManagedObject)
        secondChildMo.getType() >> "asymmetric-keys"
        secondChildMo.getName() >> "2"
        ManagedObject parentMo = mock(ManagedObject)
        parentMo.getNamespace() >> "urn:ietf:params:xml:ns:yang:ietf-keystore"
        parentMo.getType() >> "keystore"
        parentMo.getVersion() >> "2019.11.20"
        parentMo.getChildrenSize() >> 2
        parentMo.getChildren() >> [firstChildMo, secondChildMo]
        def NormalizableNodeReference normalizableNodeReference = Mock()
        normalizableNodeReference.getNeType() >> "vDU"
        normalizableNodeReference.getOssModelIdentity() >> "0.5.1"
        when:
        def ManagedObject mo = nscsDpsUtils.getOnlyChildMo(parentMo, normalizableNodeReference, "cmp")
        then:
        notThrown(Exception)
        and:
        mo == null
    }

    def "get invalid only child MO"() {
        given:
        ManagedObject childMo = mock(ManagedObject)
        childMo.getType() >> "asymmetric-keys"
        childMo.getNamespace() >> "urn:ietf:params:xml:ns:yang:ietf-keystore"
        childMo.getName() >> "1"
        ManagedObject parentMo = mock(ManagedObject)
        parentMo.getNamespace() >> "urn:ietf:params:xml:ns:yang:ietf-keystore"
        parentMo.getType() >> "keystore"
        parentMo.getVersion() >> "2019.11.20"
        parentMo.getChildrenSize() >> 1
        parentMo.getChildren() >> [childMo]
        def NormalizableNodeReference normalizableNodeReference = Mock()
        normalizableNodeReference.getNeType() >> "vDU"
        normalizableNodeReference.getOssModelIdentity() >> "0.5.1"
        when:
        def ManagedObject mo = nscsDpsUtils.getOnlyChildMo(parentMo, normalizableNodeReference, "invalid-type")
        then:
        thrown(MoTypeNotFoundException)
    }

    def "get only child MO throwing exception with many children of requested type"() {
        given:
        ManagedObject firstChildMo = mock(ManagedObject)
        firstChildMo.getType() >> "asymmetric-keys"
        firstChildMo.getName() >> "1"
        ManagedObject secondChildMo = mock(ManagedObject)
        secondChildMo.getType() >> "asymmetric-keys"
        secondChildMo.getName() >> "2"
        ManagedObject parentMo = mock(ManagedObject)
        parentMo.getNamespace() >> "urn:ietf:params:xml:ns:yang:ietf-keystore"
        parentMo.getType() >> "keystore"
        parentMo.getVersion() >> "2019.11.20"
        parentMo.getChildrenSize() >> 2
        parentMo.getChildren() >> [firstChildMo, secondChildMo]
        def NormalizableNodeReference normalizableNodeReference = Mock()
        normalizableNodeReference.getNeType() >> "vDU"
        normalizableNodeReference.getOssModelIdentity() >> "0.5.1"
        when:
        def ManagedObject mo = nscsDpsUtils.getOnlyChildMo(parentMo, normalizableNodeReference, "asymmetric-keys")
        then:
        thrown(TooManyChildMosException)
    }

    def "get only child MO returning null with no children"() {
        given:
        ManagedObject parentMo = mock(ManagedObject)
        parentMo.getNamespace() >> "urn:ietf:params:xml:ns:yang:ietf-keystore"
        parentMo.getType() >> "keystore"
        parentMo.getVersion() >> "2019.11.20"
        parentMo.getChildrenSize() >> 0
        def NormalizableNodeReference normalizableNodeReference = Mock()
        normalizableNodeReference.getNeType() >> "vDU"
        normalizableNodeReference.getOssModelIdentity() >> "0.5.1"
        when:
        def ManagedObject mo = nscsDpsUtils.getOnlyChildMo(parentMo, normalizableNodeReference, "asymmetric-keys")
        then:
        notThrown(Exception)
        and:
        mo == null
    }

    @Unroll
    def "get child MOs returning empty list"() {
        given:
        ManagedObject firstChildMo = mock(ManagedObject)
        firstChildMo.getType() >> type
        firstChildMo.getName() >> "1"
        ManagedObject secondChildMo = mock(ManagedObject)
        secondChildMo.getType() >> type
        secondChildMo.getName() >> "2"
        ManagedObject parentMo = mock(ManagedObject)
        parentMo.getNamespace() >> "urn:ietf:params:xml:ns:yang:ietf-keystore"
        parentMo.getType() >> "keystore"
        parentMo.getVersion() >> "2019.11.20"
        parentMo.getChildrenSize() >> 2
        parentMo.getChildren() >> [firstChildMo, secondChildMo]
        def NormalizableNodeReference normalizableNodeReference = Mock()
        normalizableNodeReference.getNeType() >> "vDU"
        normalizableNodeReference.getOssModelIdentity() >> "0.5.1"
        when:
        def List<ManagedObject> mos = nscsDpsUtils.getChildMos(parentMo, normalizableNodeReference, "cmp")
        then:
        notThrown(Exception)
        and:
        mos != null
        and:
        mos.isEmpty() == true
        where:
        type << ["asymmetric-keys"]
    }

    def 'get normalized root MO for node with MeContext and ManagedElement from normalizable' () {
        given: 'node created with MeContext'
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: 'with ManagedElement under MeContext'
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: 'normalizable node reference is set'
        def NodeReference nodeReference = new NodeRef(nodeName)
        def NormalizableNodeReference normalizableNodeReference = readerService.getNormalizableNodeReference(nodeReference)
        when: 'get normalized root MO'
        def mo = nscsDpsUtils.getNormalizedRootMo(normalizableNodeReference)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: 'normalized root MO is not null'
        mo != null
    }

    def 'get normalized root MO for node with MeContext and ManagedElement from normalized' () {
        given: 'node created with MeContext'
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: 'with ManagedElement under MeContext'
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: 'normalized node reference is set'
        def NodeReference nodeReference = new NodeRef(nodeName)
        def NormalizableNodeReference normalizedNodeReference = readerService.getNormalizedNodeReference(nodeReference)
        when: 'get normalized root MO'
        def mo = nscsDpsUtils.getNormalizedRootMo(normalizedNodeReference)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: 'normalized root MO is not null'
        mo != null
    }

    def 'get normalized root MO for node with MeContext and ManagedElement from invalid node reference' () {
        given: 'node created with MeContext'
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: 'with ManagedElement under MeContext'
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: 'normalizable node reference is set'
        def NormalizableNodeReference normalizableNodeReference = Mock()
        when: 'get normalized root MO'
        def mo = nscsDpsUtils.getNormalizedRootMo(normalizableNodeReference)
        then: "DataAccessSystemException should be thrown"
        thrown(DataAccessSystemException.class)
    }

    def "get only child MO returning more than one child"() {
        given:
        ManagedObject firstChildMo = mock(ManagedObject)
        firstChildMo.getType() >> "child-type"
        firstChildMo.getName() >> "1"
        ManagedObject secondChildMo = mock(ManagedObject)
        secondChildMo.getType() >> "child-type"
        secondChildMo.getName() >> "2"
        ManagedObject parentMo = mock(ManagedObject)
        parentMo.getNamespace() >> "ns"
        parentMo.getType() >> "parent-type"
        parentMo.getVersion() >> "1.2.3"
        parentMo.getChildrenSize() >> 2
        parentMo.getChildren() >> [firstChildMo, secondChildMo]
        when:
        def List<ManagedObject> mos = nscsDpsUtils.getOnlyChildMo(parentMo, "child-type")
        then:
        thrown(TooManyChildMosException)
    }
}
