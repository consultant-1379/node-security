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
package com.ericsson.nms.security.nscs.model.service

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.security.nscs.api.model.service.NscsModelInfo
import com.ericsson.nms.security.nscs.api.model.service.NscsModelServiceException
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation
import com.ericsson.oss.services.security.nscs.utils.NscsCdiSpecification

import spock.lang.Unroll

class NscsModelServiceImplTest extends NscsCdiSpecification {

    @ObjectUnderTest
    NscsModelServiceImpl nscsModelServiceImpl

    @Unroll
    def "get MIM primary type model info with reference MIM namespace"() {
        given:
        when:
        NscsModelInfo nscsModelInfo = nscsModelServiceImpl.getMimPrimaryTypeModelInfo(TargetTypeInformation.CATEGORY_NODE, targetType, targetModelIdentity, refMimNamespace, model)
        then:
        noExceptionThrown()
        where:
        targetType  | targetModelIdentity | refMimNamespace                             | model
        "vDU"       | "0.5.1"             | "urn:ietf:params:xml:ns:yang:ietf-keystore" | "certificates"
        "RadioNode" | "20.Q2-R4A24"       | "ECIM_CertM"                                | "NodeCredential"
        "RadioNode" | "20.Q3-R13A40"      | "ECIM_CertM"                                | "NodeCredential"
        "ERBS"      | "20.Q1-J.4.555"     | null                                        | "Security"
    }

    @Unroll
    def "get model info with reference MIM namespace"() {
        given:
        when:
        NscsModelInfo nscsModelInfo = nscsModelServiceImpl.getModelInfoWithRefMimNs(TargetTypeInformation.CATEGORY_NODE, targetType, targetModelIdentity, refMimNamespace, model)
        then:
        nscsModelInfo != null
        and:
        nscsModelInfo.getName() == name
        and:
        nscsModelInfo.getNamespace() == ns
        and:
        nscsModelInfo.getVersion() == version
        where:
        targetType  | targetModelIdentity | refMimNamespace                             | model                || name                 | ns                                          | version
        "vDU"       | "0.5.1"             | "urn:ietf:params:xml:ns:yang:ietf-keystore" | "certificates"       || "certificates"       | "urn:ietf:params:xml:ns:yang:ietf-keystore" | "2019.11.20"
        "RadioNode" | "20.Q2-R4A24"       | null                                        | "Ikev2PolicyProfile" || "Ikev2PolicyProfile" | "RtnIkev2PolicyProfile"                     | "1.14.0"
        "RadioNode" | "20.Q3-R13A40"      | null                                        | "Ikev2PolicyProfile" || "Ikev2PolicyProfile" | "RtnIkev2PolicyProfile"                     | "1.15.1"
        "RadioNode" | "20.Q2-R4A24"       | "ECIM_CertM"                                | "NodeCredential"     || "NodeCredential"     | "RcsCertM"                                  | "3.0.5"
        "RadioNode" | "20.Q3-R13A40"      | "ECIM_CertM"                                | "NodeCredential"     || "NodeCredential"     | "RcsCertM"                                  | "3.0.5"
        "ERBS"      | "20.Q1-J.4.555"     | null                                        | "Security"           || "Security"           | "ERBS_NODE_MODEL"                           | "10.4.555"
    }

    @Unroll
    def "get model info with reference MIM namespace throwing exception"() {
        given:
        when:
        def NscsModelInfo nscsModelInfo = nscsModelServiceImpl.getModelInfoWithRefMimNs(TargetTypeInformation.CATEGORY_NODE, targetType, targetModelIdentity, refMimNamespace, model)
        then:
        thrown(expected)
        where:
        targetType  | targetModelIdentity | refMimNamespace                             | model          || expected
        null        | "0.5.1"             | "urn:ietf:params:xml:ns:yang:ietf-keystore" | "certificates" || IllegalArgumentException
        "vDU"       | null                | "urn:ietf:params:xml:ns:yang:ietf-keystore" | "certificates" || IllegalArgumentException
        "vDU"       | "0.5.1"             | null                                        | "certificates" || IllegalArgumentException
        "vDU"       | "0.5.1"             | "urn:ietf:params:xml:ns:yang:ietf-keystore" | null           || IllegalArgumentException
        "vDU"       | "0.5.1"             | "unknown"                                   | "certificates" || NscsModelServiceException
        "vDU"       | "0.5.1"             | "urn:ietf:params:xml:ns:yang:ietf-keystore" | "unknown"      || NscsModelServiceException
    }

    @Unroll
    def "get model info"() {
        given:
        when:
        def NscsModelInfo nscsModelInfo = nscsModelServiceImpl.getModelInfo(TargetTypeInformation.CATEGORY_NODE, nodeType, tMI, model)
        then:
        nscsModelInfo != null
        and:
        nscsModelInfo.getName() == name
        and:
        nscsModelInfo.getNamespace() == ns
        and:
        nscsModelInfo.getVersion() == version
        where:
        nodeType    | tMI            | model                || name                 | ns                      | version
        "RadioNode" | "20.Q2-R4A24"  | "Ikev2PolicyProfile" || "Ikev2PolicyProfile" | "RtnIkev2PolicyProfile" | "1.14.0"
        "RadioNode" | "20.Q3-R13A40" | "Ikev2PolicyProfile" || "Ikev2PolicyProfile" | "RtnIkev2PolicyProfile" | "1.15.1"
        "RadioNode" | "20.Q2-R4A24"  | "NodeCredential"     || "NodeCredential"     | "RcsCertM"              | "3.0.5"
        "RadioNode" | "20.Q3-R13A40" | "NodeCredential"     || "NodeCredential"     | "RcsCertM"              | "3.0.5"
    }

    @Unroll
    def "get model info illegal argument"() {
        given:
        when:
        def NscsModelInfo nscsModelInfo = nscsModelServiceImpl.getModelInfo(TargetTypeInformation.CATEGORY_NODE, nodeType, tMI, model)
        then:
        thrown(expected)
        where:
        nodeType    | tMI            | model                || expected
        "RadioNode" | "WrongTMI"     | "Ikev2PolicyProfile" || IllegalArgumentException
        null        | "20.Q2-R4A24"  | "Ikev2PolicyProfile" || IllegalArgumentException
        ""          | "20.Q2-R4A24"  | "Ikev2PolicyProfile" || IllegalArgumentException
        "RadioNode" | null           | "Ikev2PolicyProfile" || IllegalArgumentException
        "RadioNode" | ""             | "Ikev2PolicyProfile" || IllegalArgumentException
        "RadioNode" | "20.Q2-R4A24"  | null                 || IllegalArgumentException
        "RadioNode" | "20.Q2-R4A24"  | ""                   || IllegalArgumentException
        "WrongType" | "20.Q2-R4A24"  | "Ikev2PolicyProfile" || IllegalArgumentException
        "RadioNode" | "20.Q2-R4A24"  | "WrongModel"         || NscsModelServiceException
    }

    @Unroll
    def "get model info list for one model"() {
        given:
        when:
        def Map<String, NscsModelInfo> nscsModelInfo = nscsModelServiceImpl.getModelInfoList(TargetTypeInformation.CATEGORY_NODE, nodeType, tMI, model)
        then:
        nscsModelInfo != null
        and:
        nscsModelInfo.size() == 1
        and:
        nscsModelInfo.get(model).getName() == name
        and:
        nscsModelInfo.get(model).getNamespace() == ns
        and:
        nscsModelInfo.get(model).getVersion() == version
        where:
        nodeType    | tMI            | model                || name                 | ns                      | version
        "RadioNode" | "20.Q2-R4A24"  | "Ikev2PolicyProfile" || "Ikev2PolicyProfile" | "RtnIkev2PolicyProfile" | "1.14.0"
        "RadioNode" | "20.Q3-R13A40" | "Ikev2PolicyProfile" || "Ikev2PolicyProfile" | "RtnIkev2PolicyProfile" | "1.15.1"
        "RadioNode" | "20.Q2-R4A24"  | "NodeCredential"     || "NodeCredential"     | "RcsCertM"              | "3.0.5"
        "RadioNode" | "20.Q3-R13A40" | "NodeCredential"     || "NodeCredential"     | "RcsCertM"              | "3.0.5"
    }

    @Unroll
    def "get model info list for two models"() {
        given:
        def String[] models = [model1, model2]
        when:
        def Map<String, NscsModelInfo> nscsModelInfo = nscsModelServiceImpl.getModelInfoList(TargetTypeInformation.CATEGORY_NODE, nodeType, tMI, models)
        then:
        nscsModelInfo != null
        and:
        nscsModelInfo.size() == 2
        and:
        nscsModelInfo.get(model1).getName() == name1
        and:
        nscsModelInfo.get(model1).getNamespace() == ns1
        and:
        nscsModelInfo.get(model1).getVersion() == version1
        and:
        nscsModelInfo.get(model2).getName() == name2
        and:
        nscsModelInfo.get(model2).getNamespace() == ns2
        and:
        nscsModelInfo.get(model2).getVersion() == version2
        where:
        nodeType    | tMI            | model1               | model2               || name1                 | ns1                      | version1 | name2                 | ns2                      | version2
        "RadioNode" | "20.Q2-R4A24"  | "Ikev2PolicyProfile" | "NodeCredential"     || "Ikev2PolicyProfile"  | "RtnIkev2PolicyProfile"  | "1.14.0" | "NodeCredential"      | "RcsCertM"               | "3.0.5"
        "RadioNode" | "20.Q3-R13A40" | "Ikev2PolicyProfile" | "NodeCredential"     || "Ikev2PolicyProfile"  | "RtnIkev2PolicyProfile"  | "1.15.1" | "NodeCredential"      | "RcsCertM"               | "3.0.5"
        "RadioNode" | "20.Q2-R4A24"  | "NodeCredential"     | "Ikev2PolicyProfile" || "NodeCredential"      | "RcsCertM"               | "3.0.5"  | "Ikev2PolicyProfile"  | "RtnIkev2PolicyProfile"  | "1.14.0"
        "RadioNode" | "20.Q3-R13A40" | "NodeCredential"     | "Ikev2PolicyProfile" || "NodeCredential"      | "RcsCertM"               | "3.0.5"  | "Ikev2PolicyProfile"  | "RtnIkev2PolicyProfile"  | "1.15.1"
    }

    @Unroll
    def "get model info list for one model with illegal argument exception"() {
        given:
        when:
        def Map<String, NscsModelInfo> nscsModelInfo = nscsModelServiceImpl.getModelInfoList(TargetTypeInformation.CATEGORY_NODE, nodeType, tMI, model)
        then:
        thrown(expected)
        where:
        nodeType    | tMI            | model                || expected
        "RadioNode" | "WrongTMI"     | "Ikev2PolicyProfile" || IllegalArgumentException
        null        | "20.Q2-R4A24"  | "Ikev2PolicyProfile" || IllegalArgumentException
        ""          | "20.Q2-R4A24"  | "Ikev2PolicyProfile" || IllegalArgumentException
        "RadioNode" | null           | "Ikev2PolicyProfile" || IllegalArgumentException
        "RadioNode" | ""             | "Ikev2PolicyProfile" || IllegalArgumentException
        "RadioNode" | "20.Q2-R4A24"  | null                 || IllegalArgumentException
        "RadioNode" | "20.Q2-R4A24"  | ""                   || IllegalArgumentException
        "Wrong"     | "20.Q2-R4A24"  | "Ikev2PolicyProfile" || IllegalArgumentException
        "RadioNode" | "20.Q2-R4A24"  | "WrongModel"         || NscsModelServiceException
    }

    @Unroll
    def "get model info list for two models with illegal argument exception"() {
        given:
        def String[] models = [model1, model2]
        when:
        def Map<String, NscsModelInfo> nscsModelInfo = nscsModelServiceImpl.getModelInfoList(TargetTypeInformation.CATEGORY_NODE, nodeType, tMI, models)
        then:
        thrown(expected)
        where:
        nodeType    | tMI            | model1               | model2               || expected
        "RadioNode" | "20.Q2-R4A24"  | null                 | "Ikev2PolicyProfile" || IllegalArgumentException
        "RadioNode" | "20.Q3-R13A40" | ""                   | "Ikev2PolicyProfile" || IllegalArgumentException
        "RadioNode" | "20.Q2-R4A24"  | "Ikev2PolicyProfile" | null                 || IllegalArgumentException
        "RadioNode" | "20.Q3-R13A40" | "Ikev2PolicyProfile" | ""                   || IllegalArgumentException
    }

    def "get model info list for no models with illegal argument exception"() {
        given:
        def String[] models = []
        when:
        def Map<String, NscsModelInfo> nscsModelInfo = nscsModelServiceImpl.getModelInfoList(TargetTypeInformation.CATEGORY_NODE, "RadioNode", "20.Q2-R4A24", models)
        then:
        thrown(IllegalArgumentException)
    }

    def 'get supported algorithm and key size'() {
        given:
        when:
        def supported = nscsModelServiceImpl.getSupportedAlgorithmAndKeySize()
        then:
        supported.contains("RSA_1024")
        supported.contains("RSA_2048")
        supported.contains("RSA_4096")
    }

    @Unroll
    def 'is proxyAccountDn attribute defined in NetworkElementSecurity version #version'() {
        given:
        def ns = "OSS_NE_SEC_DEF"
        when:
        def isDefined = nscsModelServiceImpl.isAttributeDefinedForPrimaryTypeMO(ns, "NetworkElementSecurity", version, "proxyAccountDn")
        then:
        isDefined == isdefined
        where:
        version || isdefined
        "5.0.0" || false
        "5.1.0" || true
    }
}
