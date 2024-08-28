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

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.DataAccessException
import com.ericsson.nms.security.nscs.api.exception.DataAccessSystemException
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject
import com.ericsson.oss.itpf.datalayer.dps.stub.RuntimeConfigurableDps

import spock.lang.Unroll

class NscsDataPersistenceServiceImplTest extends CdiSpecification {

    @ObjectUnderTest
    NscsDataPersistenceServiceImpl nscsDataPersistenceServiceImpl

    private RuntimeConfigurableDps runtimeConfigurableDps
    private DataPersistenceService dataPersistenceService
    private ManagedObject parentMO

    def setup() {
        runtimeConfigurableDps = cdiInjectorRule.getService(RuntimeConfigurableDps.class)
        dataPersistenceService = runtimeConfigurableDps.build()
        parentMO = runtimeConfigurableDps.addManagedObject()
                .namespace("ParentNS")
                .type("Parent")
                .name("1")
                .version("1.0.0")
                .build()
    }

    def "create top MIB root without parent and without attributes"() {
        given:
        when:
        ManagedObject mo = nscsDataPersistenceServiceImpl.createMibRoot(null, "Child", "ChildNS", "1.1.0", "1", null)
        then:
        notThrown(Exception)
        and:
        mo != null
        and:
        mo.getName() == "1"
        and:
        mo.getType() == "Child"
        and:
        mo.getNamespace() == "ChildNS"
        and:
        mo.getVersion() == "1.1.0"
        and:
        mo.getFdn() == "Child=1"
        and:
        mo.getParent() == null
        and:
        mo.getAllAttributes().isEmpty()
    }

    def "create MIB root with parent and without attributes"() {
        given:
        when:
        ManagedObject mo = nscsDataPersistenceServiceImpl.createMibRoot("Parent=1", "Child", "ChildNS", "1.1.0", "1", null)
        then:
        notThrown(Exception)
        and:
        mo != null
        and:
        mo.getName() == "1"
        and:
        mo.getType() == "Child"
        and:
        mo.getNamespace() == "ChildNS"
        and:
        mo.getVersion() == "1.1.0"
        and:
        mo.getFdn() == "Parent=1,Child=1"
        and:
        mo.getParent() != null
        and:
        mo.getAllAttributes().isEmpty()
    }

    def "create MIB root with parent and with attributes"() {
        given:
        def attributes = [ "attr1" : "value1", "attr2" : "value2" ]
        when:
        ManagedObject mo = nscsDataPersistenceServiceImpl.createMibRoot("Parent=1", "Child", "ChildNS", "1.1.0", "1", attributes)
        then:
        notThrown(Exception)
        and:
        mo != null
        and:
        mo.getName() == "1"
        and:
        mo.getType() == "Child"
        and:
        mo.getNamespace() == "ChildNS"
        and:
        mo.getVersion() == "1.1.0"
        and:
        mo.getFdn() == "Parent=1,Child=1"
        and:
        mo.getParent() != null
        and:
        !mo.getAllAttributes().isEmpty()
        and:
        mo.getAttribute("attr1") == "value1"
        and:
        mo.getAttribute("attr2") == "value2"
    }

    def "create MIB root with not existing parent"() {
        given:
        def attributes = [ "attr1" : "value1", "attr2" : "value2" ]
        when:
        ManagedObject mo = nscsDataPersistenceServiceImpl.createMibRoot("Parent=2", "Child", "ChildNS", "1.1.0", "1", attributes)
        then:
        thrown(DataAccessException)
    }

    @Unroll
    def "create MIB root with invalid params"() {
        given:
        def attributes = [ "attr1" : "value1", "attr2" : "value2" ]
        when:
        ManagedObject mo = nscsDataPersistenceServiceImpl.createMibRoot("Parent=1", type, ns, version, name, attributes)
        then:
        thrown(expected)
        where:
        type    | ns        | version | name || expected
        null    | "ChildNS" | "1.1.0" | "1"  || DataAccessException
        "Child" | null      | "1.1.0" | "1"  || DataAccessException
        "Child" | "ChildNS" | null    | "1"  || DataAccessException
        "Child" | "ChildNS" | "1.1.0" | null || DataAccessException
    }

    def "create MO without attributes"() {
        given:
        when:
        ManagedObject mo = nscsDataPersistenceServiceImpl.createMo("Parent=1", "Child", "1", null)
        then:
        notThrown(Exception)
        and:
        mo != null
        and:
        mo.getName() == "1"
        and:
        mo.getType() == "Child"
        and:
        mo.getNamespace() == "ParentNS"
        and:
        mo.getVersion() == "1.0.0"
        and:
        mo.getFdn() == "Parent=1,Child=1"
        and:
        mo.getParent() != null
        and:
        mo.getAllAttributes().isEmpty()
    }

    def "create MO with attributes"() {
        given:
        def attributes = [ "attr1" : "value1", "attr2" : "value2" ]
        when:
        ManagedObject mo = nscsDataPersistenceServiceImpl.createMo("Parent=1", "Child", "1", attributes)
        then:
        notThrown(Exception)
        and:
        mo != null
        and:
        mo.getName() == "1"
        and:
        mo.getType() == "Child"
        and:
        mo.getNamespace() == "ParentNS"
        and:
        mo.getVersion() == "1.0.0"
        and:
        mo.getFdn() == "Parent=1,Child=1"
        and:
        mo.getParent() != null
        and:
        !mo.getAllAttributes().isEmpty()
        and:
        mo.getAttribute("attr1") == "value1"
        and:
        mo.getAttribute("attr2") == "value2"
    }

    def "create MO with null parent"() {
        given:
        def attributes = [ "attr1" : "value1", "attr2" : "value2" ]
        when:
        ManagedObject mo = nscsDataPersistenceServiceImpl.createMo(null, "Child", "1", attributes)
        then:
        thrown(DataAccessSystemException)
    }

    def "create MO with not existent parent"() {
        given:
        def attributes = [ "attr1" : "value1", "attr2" : "value2" ]
        when:
        ManagedObject mo = nscsDataPersistenceServiceImpl.createMo("Parent=2", "Child", "1", attributes)
        then:
        thrown(DataAccessException)
    }

    @Unroll
    def "create MO with invalid params"() {
        given:
        def attributes = [ "attr1" : "value1", "attr2" : "value2" ]
        when:
        ManagedObject mo = nscsDataPersistenceServiceImpl.createMo("Parent=1", type, name, attributes)
        then:
        thrown(expected)
        where:
        type    | name || expected
        null    | "1"  || DataAccessException
        "Child" | null || DataAccessException
    }

    @Unroll
    def "update MO"() {
        given:
        def attributes = [ "attr1" : "value1", "attr2" : "value2" ]
        ManagedObject mo = nscsDataPersistenceServiceImpl.createMo("Parent=1", "Child", "1", attributes)
        when:
        def newAttributes = [ "attr1" : value ]
        mo = nscsDataPersistenceServiceImpl.updateMo("Parent=1,Child=1", newAttributes)
        then:
        notThrown(Exception)
        and:
        mo != null
        and:
        mo.getName() == "1"
        and:
        mo.getType() == "Child"
        and:
        mo.getNamespace() == "ParentNS"
        and:
        mo.getVersion() == "1.0.0"
        and:
        mo.getFdn() == "Parent=1,Child=1"
        and:
        mo.getParent() != null
        and:
        !mo.getAllAttributes().isEmpty()
        and:
        mo.getAttribute("attr1") == expected
        and:
        mo.getAttribute("attr2") == "value2"
        where:
        value    || expected
        "value1" || "value1"
        null     || null
        "value2" || "value2"
    }

    def "delete child MO"() {
        given:
        def attributes = [ "attr1" : "value1", "attr2" : "value2" ]
        ManagedObject mo = nscsDataPersistenceServiceImpl.createMo("Parent=1", "Child", "1", attributes)
        when:
        def deletedPos = nscsDataPersistenceServiceImpl.deletePo(mo)
        then:
        notThrown(Exception)
        and:
        deletedPos == 1
    }

    def "delete parent MO"() {
        given:
        def attributes = [ "attr1" : "value1", "attr2" : "value2" ]
        ManagedObject mo = nscsDataPersistenceServiceImpl.createMo("Parent=1", "Child", "1", attributes)
        when:
        def deletedPos = nscsDataPersistenceServiceImpl.deletePo(parentMO)
        then:
        notThrown(Exception)
        and:
        deletedPos == 2
    }

    def "delete null PO"() {
        given:
        when:
        nscsDataPersistenceServiceImpl.deletePo(null)
        then:
        thrown(DataAccessSystemException)
    }

    def "delete not existent PO - delete twice the same PO"() {
        given:
        def attributes = [ "attr1" : "value1", "attr2" : "value2" ]
        ManagedObject mo = nscsDataPersistenceServiceImpl.createMo("Parent=1", "Child", "1", attributes)
        when:
        nscsDataPersistenceServiceImpl.deletePo(mo)
        nscsDataPersistenceServiceImpl.deletePo(mo)
        then:
        thrown(DataAccessException)
    }

    @Unroll
    def "update MO with no attributes"() {
        given:
        def attributes = [ "attr1" : "value1", "attr2" : "value2" ]
        ManagedObject mo = nscsDataPersistenceServiceImpl.createMo("Parent=1", "Child", "1", attributes)
        when:
        mo = nscsDataPersistenceServiceImpl.updateMo("Parent=1,Child=1", attrs)
        then:
        notThrown(Exception)
        and:
        mo != null
        and:
        mo.getName() == "1"
        and:
        mo.getType() == "Child"
        and:
        mo.getNamespace() == "ParentNS"
        and:
        mo.getVersion() == "1.0.0"
        and:
        mo.getFdn() == "Parent=1,Child=1"
        and:
        mo.getParent() != null
        and:
        !mo.getAllAttributes().isEmpty()
        and:
        mo.getAttribute("attr1") == expected
        and:
        mo.getAttribute("attr2") == "value2"
        where:
        attrs    || expected
        null     || "value1"
        [ : ]    || "value1"
    }

    def "update MO with null FDN"() {
        given:
        def attributes = [ "attr1" : "value1", "attr2" : "value2" ]
        when:
        ManagedObject mo = nscsDataPersistenceServiceImpl.updateMo(null, attributes)
        then:
        thrown(DataAccessSystemException)
    }

    def "update MO with not existent FDN"() {
        given:
        def attributes = [ "attr1" : "value1", "attr2" : "value2" ]
        when:
        ManagedObject mo = nscsDataPersistenceServiceImpl.updateMo("Parent=1,Child=1", attributes)
        then:
        thrown(DataAccessException)
    }

    def "get MO by FDN"() {
        given:
        def attributes = [ "attr1" : "value1", "attr2" : "value2" ]
        ManagedObject mo = nscsDataPersistenceServiceImpl.createMo("Parent=1", "Child", "1", attributes)
        when:
        mo = nscsDataPersistenceServiceImpl.getMoByFdn("Parent=1")
        then:
        notThrown(Exception)
        and:
        mo != null
        and:
        mo.getName() == "1"
        and:
        mo.getType() == "Parent"
        and:
        mo.getNamespace() == "ParentNS"
        and:
        mo.getVersion() == "1.0.0"
        and:
        mo.getFdn() == "Parent=1"
        and:
        mo.getParent() == null
        and:
        mo.getAllAttributes().isEmpty()
        and:
        mo.getChildrenSize() == 1
        and:
        mo.getChild("Child=1") != null
        and:
        mo.getChild("Child=1").getName() == "1"
        and:
        mo.getChild("Child=1").getType() == "Child"
        and:
        mo.getChild("Child=1").getNamespace() == "ParentNS"
        and:
        mo.getChild("Child=1").getVersion() == "1.0.0"
        and:
        mo.getChild("Child=1").getFdn() == "Parent=1,Child=1"
        and:
        mo.getChild("Child=1").getParent() != null
        and:
        !mo.getChild("Child=1").getAllAttributes().isEmpty()
        and:
        mo.getChild("Child=1").getChildrenSize() == 0
    }

    def "get MO by FDN for not existent MO"() {
        given:
        def attributes = [ "attr1" : "value1", "attr2" : "value2" ]
        ManagedObject mo = nscsDataPersistenceServiceImpl.createMo("Parent=1", "Child", "1", attributes)
        when:
        mo = nscsDataPersistenceServiceImpl.getMoByFdn("Parent=1,Child=2")
        then:
        notThrown(Exception)
        and:
        mo == null
    }

    def "get MO by FDN with null FDN"() {
        given:
        when:
        ManagedObject mo = nscsDataPersistenceServiceImpl.getMoByFdn(null)
        then:
        thrown(DataAccessSystemException)
    }

    def "get MO list by type"() {
        given:
        nscsDataPersistenceServiceImpl.createMo("Parent=1", "Child", "1", null)
        nscsDataPersistenceServiceImpl.createMo("Parent=1", "Child", "2", null)
        nscsDataPersistenceServiceImpl.createMo("Parent=1,Child=1", "GrandChild", "1", null)
        nscsDataPersistenceServiceImpl.createMo("Parent=1,Child=1", "GrandChild", "2", null)
        nscsDataPersistenceServiceImpl.createMo("Parent=1,Child=2", "GrandChild", "1", null)
        nscsDataPersistenceServiceImpl.createMo("Parent=1,Child=2", "GrandChild", "2", null)
        when:
        def childrenList = nscsDataPersistenceServiceImpl.getMoListByType("Parent=1", "Child", "ParentNS")
        and:
        def grandChildrenList = nscsDataPersistenceServiceImpl.getMoListByType("Parent=1", "GrandChild", "ParentNS")
        then:
        notThrown(Exception)
        and:
        childrenList != null && !childrenList.isEmpty()
        and:
        childrenList.size() == 2
        and:
        grandChildrenList != null && !grandChildrenList.isEmpty()
        and:
        grandChildrenList.size() == 4
    }

    @Unroll
    def "get MO list by type for invalid parent"() {
        given:
        when:
        def moList = nscsDataPersistenceServiceImpl.getMoListByType(parentFdn, "Child", "ParentNS")
        then:
        thrown(expected)
        where:
        parentFdn  || expected
        "Parent=2" || DataAccessException
        null       || DataAccessSystemException
    }

    @Unroll
    def "get MO list by type for not existing children"() {
        given:
        when:
        def moList = nscsDataPersistenceServiceImpl.getMoListByType("Parent=1", type, ns)
        then:
        notThrown(Exception)
        and:
        moList != null && moList.isEmpty()
        where:
        type       | ns         || size
        "NotChild" | "ParentNS" || 0
        "Child"    | "NotNS"    || 0
        "Parent"   | "ParentNS" || 0
    }

    @Unroll
    def "get MO list by type for invalid params"() {
        given:
        when:
        def moList = nscsDataPersistenceServiceImpl.getMoListByType("Parent=1", type, ns)
        then:
        thrown(expected)
        where:
        type    | ns         || expected
        null    | "ParentNS" || DataAccessException
        "Child" | null       || DataAccessException
    }

    def "get MO"() {
        given:
        nscsDataPersistenceServiceImpl.createMo("Parent=1", "Child", "1", null)
        nscsDataPersistenceServiceImpl.createMo("Parent=1", "Child", "2", null)
        nscsDataPersistenceServiceImpl.createMo("Parent=1,Child=1", "GrandChild", "1", null)
        nscsDataPersistenceServiceImpl.createMo("Parent=1,Child=1", "GrandChild", "2", null)
        nscsDataPersistenceServiceImpl.createMo("Parent=1,Child=2", "GrandChild", "1", null)
        nscsDataPersistenceServiceImpl.createMo("Parent=1,Child=2", "GrandChild", "2", null)
        when:
        def child = nscsDataPersistenceServiceImpl.getMo("Parent=1", "Child", "ParentNS", "2")
        and:
        def grandChild = nscsDataPersistenceServiceImpl.getMo("Parent=1,Child=2", "GrandChild", "ParentNS", "1")
        then:
        notThrown(Exception)
        and:
        child != null
        and:
        child.getFdn() == "Parent=1,Child=2"
        and:
        child.getName() == "2"
        and:
        child.getType() == "Child"
        and:
        child.getNamespace() == "ParentNS"
        and:
        grandChild != null
        and:
        grandChild.getFdn() == "Parent=1,Child=2,GrandChild=1"
        and:
        grandChild.getName() == "1"
        and:
        grandChild.getType() == "GrandChild"
        and:
        grandChild.getNamespace() == "ParentNS"
    }

    @Unroll
    def "get MO for invalid parent"() {
        given:
        when:
        def moList = nscsDataPersistenceServiceImpl.getMo(parentFdn, "Child", "ParentNS", "1")
        then:
        thrown(expected)
        where:
        parentFdn  || expected
        "Parent=2" || DataAccessException
        null       || DataAccessSystemException
    }

    @Unroll
    def "get MO for not existing children"() {
        given:
        nscsDataPersistenceServiceImpl.createMo("Parent=1", "Child", "1", null)
        nscsDataPersistenceServiceImpl.createMo("Parent=1", "Child", "2", null)
        nscsDataPersistenceServiceImpl.createMo("Parent=1,Child=1", "GrandChild", "1", null)
        nscsDataPersistenceServiceImpl.createMo("Parent=1,Child=1", "GrandChild", "2", null)
        nscsDataPersistenceServiceImpl.createMo("Parent=1,Child=2", "GrandChild", "1", null)
        nscsDataPersistenceServiceImpl.createMo("Parent=1,Child=2", "GrandChild", "2", null)
        when:
        def mo = nscsDataPersistenceServiceImpl.getMo("Parent=1", type, ns, name)
        then:
        notThrown(Exception)
        and:
        mo == expected
        where:
        type       | ns         | name || expected
        "NotChild" | "ParentNS" | "1"  || null
        "Child"    | "NotNS"    | "1"  || null
        "Parent"   | "ParentNS" | "1"  || null
        "Child"    | "ParentNS" | "3"  || null
    }

    @Unroll
    def "get MO for invalid params"() {
        given:
        when:
        def mo = nscsDataPersistenceServiceImpl.getMo("Parent=1", type, ns, name)
        then:
        thrown(expected)
        where:
        type    | ns         | name || expected
        null    | "ParentNS" | "1"  || DataAccessException
        "Child" | null       | "1"  || DataAccessException
        "Child" | "ParentNS" | null || DataAccessSystemException
    }

    @Unroll
    def "get MO list by type and with attribute #attr"() {
        given:
        def attr1 = ["attribute1": "value1"]
        def attr2 = ["attribute2": "value2"]
        def attr12 = ["attribute1": "value1", "attribute2": "value2"]
        nscsDataPersistenceServiceImpl.createMo("Parent=1", "Child", "1", null)
        nscsDataPersistenceServiceImpl.createMo("Parent=1", "Child", "2", null)
        nscsDataPersistenceServiceImpl.createMo("Parent=1,Child=1", "GrandChild", "1", attr1)
        nscsDataPersistenceServiceImpl.createMo("Parent=1,Child=1", "GrandChild", "2", attr12)
        nscsDataPersistenceServiceImpl.createMo("Parent=1,Child=1", "GrandChild", "3", [:])
        nscsDataPersistenceServiceImpl.createMo("Parent=1,Child=2", "GrandChild", "1", null)
        nscsDataPersistenceServiceImpl.createMo("Parent=1,Child=2", "GrandChild", "2", attr2)
        when:
        def mos = nscsDataPersistenceServiceImpl.getMoListByTypeWithAttribute("GrandChild", "ParentNS", attr)
        then:
        notThrown(Exception)
        and:
        mos.size() == expected
        where:
        attr         || expected
        "attribute1" || 2
        "attribute2" || 2
        "attribute3" || 0
        ""           || 0
    }

    def "get MO list by type and with null attribute"() {
        given:
        when:
        def mos = nscsDataPersistenceServiceImpl.getMoListByTypeWithAttribute("GrandChild", "ParentNS", null)
        then:
        thrown(DataAccessException.class)
    }
}
