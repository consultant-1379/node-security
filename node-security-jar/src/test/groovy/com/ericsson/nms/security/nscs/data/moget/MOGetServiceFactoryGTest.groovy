/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.data.moget

import javax.enterprise.context.spi.CreationalContext
import javax.enterprise.inject.spi.Bean
import javax.enterprise.inject.spi.BeanManager
import javax.inject.Inject

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.moget.impl.CbpOiMOGetServiceImpl
import com.ericsson.nms.security.nscs.data.moget.impl.ComEcimMOGetServiceImpl
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference

import spock.lang.Unroll

class MOGetServiceFactoryGTest extends CdiSpecification {

    @ObjectUnderTest
    private MOGetServiceFactory mOGetServiceFactory

    @Inject
    ComEcimMOGetServiceImpl comEcimMOGetServiceImpl;

    @Inject
    CbpOiMOGetServiceImpl cbpOiMOGetServiceImpl;

    @MockedImplementation
    private NscsCMReaderService reader

    @MockedImplementation
    private NscsCapabilityModelService capabilityService;

    @MockedImplementation
    private Bean<?> bean;

    @MockedImplementation
    private BeanManager beanManager;

    @MockedImplementation
    private CreationalContext creationalContext;

    def "object under test injection" () {
        expect:
        mOGetServiceFactory != null
    }

    @Unroll
    def "GetNodeCredentialKeyInfo for radio node success case" () {
        given:
        String RADIO_NODE_NAME = "LTE01dg2ERBS00001";
        NodeReference radioNodeRef = new NodeRef(RADIO_NODE_NAME);
        NormalizableNodeReference nodeRef = reader.getNormalizableNodeReference(radioNodeRef);
        capabilityService.getMomType(_)>> "ECIM"
        final Set<Bean<?>> beans = new HashSet<Bean<?>>();
        beans.add(bean);
        beanManager.getBeans(_,_) >> beans;
        beanManager.createCreationalContext(_) >> creationalContext;
        beanManager.getReference(_,_,_) >> comEcimMOGetServiceImpl;
        when:
        String keyAlogorithm = mOGetServiceFactory.getNodeCredentialKeyInfo(nodeRef, keySize);
        then:
        assert keyAlogorithm.equals(certType)
        where:
        certType    | keySize
        "RSA_2048"  |  "1"
        "RSA_4096"  |  "3"
        "RSA_3072"  |  "2"
    }

    @Unroll
    def "GetNodeCredentialKeyInfo for vdu node success case" () {
        given:
        String VDU_NODE_NAME = "VDU00001";
        NodeReference radioNodeRef = new NodeRef(VDU_NODE_NAME);
        NormalizableNodeReference nodeRef = reader.getNormalizableNodeReference(radioNodeRef);
        capabilityService.getMomType(_)>> "EOI"
        final Set<Bean<?>> beans = new HashSet<Bean<?>>();
        beans.add(bean);
        beanManager.getBeans(_,_) >> beans;
        beanManager.createCreationalContext(_) >> creationalContext;
        beanManager.getReference(_,_,_) >> cbpOiMOGetServiceImpl;
        when:
        String keyAlogorithm = mOGetServiceFactory.getNodeCredentialKeyInfo(nodeRef, keySize);
        then:
        assert keyAlogorithm.equals(certType)
        where:
        certType    | keySize
        "rsa2048"  |  "1"
        "rsa4096"  |  "3"
        "rsa3072"  |  "2"
    }
}
