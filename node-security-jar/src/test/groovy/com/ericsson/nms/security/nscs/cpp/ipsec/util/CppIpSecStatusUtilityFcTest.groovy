package com.ericsson.nms.security.nscs.cpp.ipsec.util

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import org.mockito.Spy
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CppIpSecStatusUtilityFcTest extends CdiSpecification {

    @ObjectUnderTest
    CppIpSecStatusUtility cppIpSecStatusUtility

    @Spy
    private final Logger logger = LoggerFactory.getLogger(CppIpSecStatusUtility.class);

    def setup() {}

    @Override
    Object addAdditionalInjectionProperties(final InjectionProperties injectionProperties) {}

    def "getOmConfigurationType" () {
        given:
        cppIpSecStatusUtility.configurationInfo = new CppIpSecStatusUtility.ConfigurationInfo()
        cppIpSecStatusUtility.configurationInfo.setOmIpAccessHostEtId("omIpAccessHostEtId")
        when:
        String confType = cppIpSecStatusUtility.getOmConfigurationType()
        then:
        confType == CppIpSecStatusUtility.CONFIGURATION_1
    }

    def "getOmConfigurationType with configurationinfo null" () {
        given:
        cppIpSecStatusUtility.configurationInfo = null
        when:
        cppIpSecStatusUtility.getOmConfigurationType()
        then:
        def e = thrown(IllegalArgumentException.class)
        e.getMessage() == "configuration info is null"
    }
}
