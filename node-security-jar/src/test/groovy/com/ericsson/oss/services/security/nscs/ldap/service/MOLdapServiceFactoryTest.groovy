package com.ericsson.oss.services.security.nscs.ldap.service

import javax.enterprise.context.spi.CreationalContext
import javax.enterprise.inject.spi.Bean
import javax.enterprise.inject.spi.BeanManager
import javax.inject.Inject

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeTypeException
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.CommonLdapConfigurationTask
import com.ericsson.oss.services.security.nscs.ldap.service.impl.CbpOiMOLdapServiceImpl
import com.ericsson.oss.services.security.nscs.ldap.service.impl.ComEcimMOLdapServiceImpl
import com.ericsson.oss.services.security.nscs.ldap.service.impl.CppMOLdapServiceImpl

class MOLdapServiceFactoryTest extends CdiSpecification {

    @ObjectUnderTest
    private MOLdapServiceFactory mOLdapServiceFactory

    @MockedImplementation
    private Bean<?> bean

    @MockedImplementation
    private Bean<?> bean1

    @MockedImplementation
    private BeanManager beanManager

    @MockedImplementation
    private CreationalContext creationalContext

    @MockedImplementation
    private NscsCapabilityModelService capabilityService

    @MockedImplementation
    private MOLdapService moLdapService

    @Inject
    private CppMOLdapServiceImpl cppMOLdapServiceImpl

    @Inject
    private ComEcimMOLdapServiceImpl comEcimMOLdapServiceImpl

    @Inject
    private CbpOiMOLdapServiceImpl cbpOiMOLdapServiceImpl

    private nodeName = "NODENAME"
    private CommonLdapConfigurationTask task
    private NormalizableNodeReference normalizable = mock(NormalizableNodeReference)

    def setup() {
        task = new CommonLdapConfigurationTask(nodeName)
    }

    def 'object under test'() {
        expect:
        mOLdapServiceFactory != null
    }

    def 'validate ldap configuration'() {
        given:
        capabilityService.getMomType(normalizable) >> "platform"
        and:
        moLdapService.validateLdapConfiguration(task, normalizable) >> { return }
        and:
        beanManager.getBeans(MOLdapService.class, _) >> [bean]
        beanManager.createCreationalContext(_) >> creationalContext
        beanManager.getReference(_,MOLdapService.class,_) >> moLdapService
        when:
        mOLdapServiceFactory.validateLdapConfiguration(task, normalizable)
        then:
        noExceptionThrown()
    }

    def 'validate ldap configuration with wrong platform'() {
        given:
        capabilityService.getMomType(normalizable) >> { throw new Exception() }
        when:
        mOLdapServiceFactory.validateLdapConfiguration(task, normalizable)
        then:
        thrown(InvalidNodeTypeException.class)
    }

    def 'validate ldap configuration with no implementations'() {
        given:
        capabilityService.getMomType(normalizable) >> "platform"
        and:
        beanManager.getBeans(MOLdapService.class, _) >> []
        beanManager.createCreationalContext(_) >> creationalContext
        when:
        mOLdapServiceFactory.validateLdapConfiguration(task, normalizable)
        then:
        thrown(InvalidNodeTypeException.class)
    }

    def 'ldap configure'() {
        given:
        capabilityService.getMomType(normalizable) >> "platform"
        and:
        moLdapService.ldapConfigure(task, normalizable) >> { return }
        and:
        beanManager.getBeans(MOLdapService.class, _) >> [bean]
        beanManager.createCreationalContext(_) >> creationalContext
        beanManager.getReference(_,MOLdapService.class,_) >> moLdapService
        when:
        mOLdapServiceFactory.ldapConfigure(task, normalizable)
        then:
        noExceptionThrown()
    }
}
