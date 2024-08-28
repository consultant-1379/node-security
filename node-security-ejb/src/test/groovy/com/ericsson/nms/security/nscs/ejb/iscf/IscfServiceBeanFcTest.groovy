/*
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */
package com.ericsson.nms.security.nscs.ejb.iscf

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.enums.CertificateType
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode
import com.ericsson.nms.security.nscs.api.exception.IscfServiceException
import com.ericsson.nms.security.nscs.api.iscf.NodeIdentifier
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException
import com.ericsson.nms.security.nscs.iscf.IscfServiceValidators
import com.ericsson.nms.security.nscs.iscf.SecurityDataCollector
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException
import org.slf4j.Logger
import spock.lang.Unroll

class IscfServiceBeanFcTest extends CdiSpecification {

    @ObjectUnderTest
    IscfServiceBean iscfService

    @MockedImplementation
    SecurityDataCollector securityDataCollector

    @MockedImplementation
    NscsNodeUtility nscsNodeUtility

    @MockedImplementation
    NscsCapabilityModelService nscsCapabilityModelService;

    @MockedImplementation
    IscfServiceValidators iscfServiceValidators;

    @MockedImplementation
    Logger logger

    static def MSRBS_NODE_TYPE = "RadioNode"
    static def MSRBS_NODE_FDN = "RadioNode01"
    static def nodeSerialNumber = "1234"
    def nodeId = new NodeIdentifier(MSRBS_NODE_FDN, nodeSerialNumber)
    def MSRBS_MODEL_INFO = new NodeModelInformation(null, null, MSRBS_NODE_TYPE)
    static def subjectAltNameString = new SubjectAltNameStringType("127.0.0.1")
    static def subjectAltNameParam = new SubjectAltNameParam(SubjectAltNameFormat.IPV4, subjectAltNameString)
    def wantedEnrollmentMode = EnrollmentMode.CMPv2_VC

    def setup() {
        nscsCapabilityModelService.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, MSRBS_NODE_TYPE) >> true
        nscsCapabilityModelService.isCertificateManagementSupported(MSRBS_MODEL_INFO) >> true
    }

    def 'When security data OAM are generated for a Radio Node, then SecurityDataCollector is invoked with proper parameters' () {
        given: 'a Radio Node'
        nscsNodeUtility.getNodeNameFromFdn(MSRBS_NODE_FDN) >> MSRBS_NODE_FDN
        logger.isDebugEnabled() >> true
        when: 'security data for OAM are generated'
        iscfService.generateSecurityDataOam(nodeId, wantedEnrollmentMode, MSRBS_MODEL_INFO, StandardProtocolFamily.INET)
        then: 'SecurityDataCollector is invoked with correct parameters'
        1 * securityDataCollector.getSecurityDataResponse(EnumSet.of(CertificateType.OAM), MSRBS_NODE_FDN, nodeSerialNumber, null,
                wantedEnrollmentMode, MSRBS_MODEL_INFO, StandardProtocolFamily.INET)
        and: 'response is logged at debug level'
        1 * logger.debug(_ as String, _)
    }

    def 'When security data OAM are generated with SAN, then SecurityDataCollector is invoked with correct parameters' () {
        given: 'a Radio Node'
        nscsNodeUtility.getNodeNameFromFdn(MSRBS_NODE_FDN) >> MSRBS_NODE_FDN
        logger.isDebugEnabled() >> true
        when: 'security data for OAM with SAN are generated'
        iscfService.generateSecurityDataOam(nodeId, subjectAltNameParam, wantedEnrollmentMode, MSRBS_MODEL_INFO, StandardProtocolFamily.INET)
        then: 'SecurityDataCollector is invoked with correct parameters'
        1 * securityDataCollector.getSecurityDataResponse(EnumSet.of(CertificateType.OAM), MSRBS_NODE_FDN, nodeSerialNumber, subjectAltNameParam,
                wantedEnrollmentMode, MSRBS_MODEL_INFO, StandardProtocolFamily.INET)
        and: 'response is logged at debug level'
        1 * logger.debug(_ as String, _)
    }

    def 'When security data IPSec are generated for a Radio Node, then SecurityDataCollector is invoked with correct parameters' () {
        given: 'a Radio Node'
        nscsNodeUtility.getNodeNameFromFdn(MSRBS_NODE_FDN) >> MSRBS_NODE_FDN
        logger.isDebugEnabled() >> true
        when: 'security data for IPSec are generated'
        iscfService.generateSecurityDataIpsec(nodeId, subjectAltNameParam, wantedEnrollmentMode, MSRBS_MODEL_INFO, StandardProtocolFamily.INET)
        then: 'SecurityDataCollector is invoked with correct parameters'
        1 * securityDataCollector.getSecurityDataResponse(EnumSet.of(CertificateType.IPSEC), MSRBS_NODE_FDN, nodeSerialNumber, subjectAltNameParam,
                wantedEnrollmentMode, MSRBS_MODEL_INFO, StandardProtocolFamily.INET)
        and: 'response is logged at debug level'
        1 * logger.debug(_ as String, _)
    }

    def 'When security data combined are generated for a Radio Node, then SecurityDataCollector is invoked with correct parameters' () {
        given: 'a Radio Node'
        nscsNodeUtility.getNodeNameFromFdn(MSRBS_NODE_FDN) >> MSRBS_NODE_FDN
        logger.isDebugEnabled() >> true
        when: 'security data for combined are generated'
        iscfService.generateSecurityDataCombo(nodeId, subjectAltNameParam, wantedEnrollmentMode, MSRBS_MODEL_INFO, StandardProtocolFamily.INET)
        then: 'SecurityDataCollector is invoked with correct parameters'
        1 * securityDataCollector.getSecurityDataResponse(EnumSet.allOf(CertificateType), MSRBS_NODE_FDN, nodeSerialNumber, subjectAltNameParam,
                wantedEnrollmentMode, MSRBS_MODEL_INFO, StandardProtocolFamily.INET)
        and: 'response is logged at debug level'
        1 * logger.debug(_ as String, _)
    }

    def 'When security data OAM are generated and input data are invalid, then exception is thrown' () {
        given: 'input data for Radio Node are not valid'
        iscfServiceValidators.validateGenerateSecurityDataOam(nodeId, wantedEnrollmentMode, MSRBS_MODEL_INFO) >> {throw new IscfServiceException()}
        logger.isInfoEnabled() >> true
        when: 'security data for OAM are generated'
        iscfService.generateSecurityDataOam(nodeId, wantedEnrollmentMode, MSRBS_MODEL_INFO, StandardProtocolFamily.INET)
        then: 'IscfServiceException is thrown'
        thrown(IscfServiceException.class)
        and: 'parameters are logged at info level'
        1 * logger.info(_ as String, _, _, _, _)
    }

    @Unroll
    def 'When security data OAM are generated with SAN and input data are invalid, then exception is thrown' () {
        given: 'input data for Radio Node are not valid'
        iscfServiceValidators.validateGenerateSecurityDataOam(nodeId, subjectAltNameParamTest, wantedEnrollmentMode, MSRBS_MODEL_INFO) >> {throw new IscfServiceException()}
        logger.isInfoEnabled() >> true
        when: 'security data for OAM with SAN are generated'
        iscfService.generateSecurityDataOam(nodeId, subjectAltNameParamTest, wantedEnrollmentMode, MSRBS_MODEL_INFO, StandardProtocolFamily.INET)
        then: 'IscfServiceException is thrown'
        thrown(IscfServiceException.class)
        and: 'parameters are logged at info level'
        1 * logger.info(_ as String, _, _, _, _, _)
        where:
        subjectAltNameParamTest << [subjectAltNameParam, null]
    }

    @Unroll
    def 'When security data IPSec are generated for a Radio Node and input data are invalid, then exception is thrown' () {
        given: 'input data for Radio Node are not valid'
        iscfServiceValidators.validateGenerateSecurityDataIpsec(nodeId, subjectAltNameParamTest, wantedEnrollmentMode, MSRBS_MODEL_INFO) >> {throw new IscfServiceException()}
        logger.isInfoEnabled() >> true
        when: 'security data for IPSec are generated'
        iscfService.generateSecurityDataIpsec(nodeId, subjectAltNameParamTest, wantedEnrollmentMode, MSRBS_MODEL_INFO, StandardProtocolFamily.INET)
        then: 'IscfServiceException is thrown'
        thrown(IscfServiceException.class)
        and: 'parameters are logged at info level'
        1 * logger.info(_ as String, _, _, _, _, _)
        where:
        subjectAltNameParamTest << [subjectAltNameParam, null]
    }

    @Unroll
    def 'When security data combined are generated for a Radio Node and input data are invalid, then exception is thrown' () {
        given: 'input data for Radio Node are not valid'
        iscfServiceValidators.validateGenerateSecurityDataCombo(nodeId, subjectAltNameParamTest, wantedEnrollmentMode, MSRBS_MODEL_INFO) >> {throw new IscfServiceException()}
        logger.isInfoEnabled() >> true
        when: 'security data combined are generated'
        iscfService.generateSecurityDataCombo(nodeId, subjectAltNameParamTest, wantedEnrollmentMode, MSRBS_MODEL_INFO, StandardProtocolFamily.INET)
        then: 'IscfServiceException is thrown'
        thrown(IscfServiceException.class)
        and: 'parameters are logged at info level'
        1 * logger.info(_ as String, _, _, _, _, _)
        where:
        subjectAltNameParamTest << [subjectAltNameParam, null]
    }

    def 'When security data OAM are generated and cannot get node name, then exception is thrown' () {
        given: 'a Radio Node'
        nscsNodeUtility.getNodeNameFromFdn(MSRBS_NODE_FDN) >> {throw new UnexpectedErrorException()}
        when: 'security data for OAM are generated'
        iscfService.generateSecurityDataOam(nodeId, wantedEnrollmentMode, MSRBS_MODEL_INFO, StandardProtocolFamily.INET)
        then: 'IscfServiceException is thrown'
        thrown(IscfServiceException.class)
    }

    def 'When security data OAM are generated with SAN and cannot get node name, then exception is thrown' () {
        given: 'a Radio Node'
        nscsNodeUtility.getNodeNameFromFdn(MSRBS_NODE_FDN) >> {throw new UnexpectedErrorException()}
        when: 'security data for OAM with SAN are generated'
        iscfService.generateSecurityDataOam(nodeId, subjectAltNameParam, wantedEnrollmentMode, MSRBS_MODEL_INFO, StandardProtocolFamily.INET)
        then: 'IscfServiceException is thrown'
        thrown(IscfServiceException.class)
    }

    def 'When security data IPSec are generated and cannot get node name, then exception is thrown' () {
        given: 'a Radio Node'
        nscsNodeUtility.getNodeNameFromFdn(MSRBS_NODE_FDN) >> {throw new UnexpectedErrorException()}
        when: 'security data for IPSec are generated'
        iscfService.generateSecurityDataIpsec(nodeId, subjectAltNameParam, wantedEnrollmentMode, MSRBS_MODEL_INFO, StandardProtocolFamily.INET)
        then: 'IscfServiceException is thrown'
        thrown(IscfServiceException.class)
    }

    def 'When security data combined are generated and cannot get node name, then exception is thrown' () {
        given: 'a Radio Node'
        nscsNodeUtility.getNodeNameFromFdn(MSRBS_NODE_FDN) >> {throw new UnexpectedErrorException()}
        when: 'security data combined are generated'
        iscfService.generateSecurityDataCombo(nodeId, subjectAltNameParam, wantedEnrollmentMode, MSRBS_MODEL_INFO, StandardProtocolFamily.INET)
        then: 'IscfServiceException is thrown'
        thrown(IscfServiceException.class)
    }

    def 'When security data OAM are generated and cannot get data response, then exception is thrown' () {
        given: 'a Radio Node'
        nscsNodeUtility.getNodeNameFromFdn(MSRBS_NODE_FDN) >> MSRBS_NODE_FDN
        and: 'getSecurityDataResponse throws exception'
        securityDataCollector.getSecurityDataResponse(EnumSet.of(CertificateType.OAM), MSRBS_NODE_FDN, nodeSerialNumber, null,
                wantedEnrollmentMode, MSRBS_MODEL_INFO, StandardProtocolFamily.INET) >>
                {throw new CppSecurityServiceException("Exception when invoking NscsPkiEntitiesManager")}
        when: 'security data for OAM are generated'
        iscfService.generateSecurityDataOam(nodeId, wantedEnrollmentMode, MSRBS_MODEL_INFO, StandardProtocolFamily.INET)
        then: 'same exception is thrown'
        thrown(CppSecurityServiceException.class)
    }

    def 'When security data OAM are generated with SAN and cannot get data response, then exception is thrown' () {
        given: 'a Radio Node'
        nscsNodeUtility.getNodeNameFromFdn(MSRBS_NODE_FDN) >> MSRBS_NODE_FDN
        and: 'getSecurityDataResponse throws exception'
        securityDataCollector.getSecurityDataResponse(EnumSet.of(CertificateType.OAM), MSRBS_NODE_FDN, nodeSerialNumber,
                subjectAltNameParam, wantedEnrollmentMode, MSRBS_MODEL_INFO, StandardProtocolFamily.INET) >>
                {throw new CppSecurityServiceException("Exception when invoking NscsPkiEntitiesManager")}
        when: 'security data for OAM with SAN are generated'
        iscfService.generateSecurityDataOam(nodeId, subjectAltNameParam, wantedEnrollmentMode, MSRBS_MODEL_INFO, StandardProtocolFamily.INET)
        then: 'same exception is thrown'
        thrown(CppSecurityServiceException.class)
    }

    def 'When security data IPSec are generated and cannot get data response, then exception is thrown' () {
        given: 'a Radio Node'
        nscsNodeUtility.getNodeNameFromFdn(MSRBS_NODE_FDN) >> MSRBS_NODE_FDN
        and: 'getSecurityDataResponse throws exception'
        securityDataCollector.getSecurityDataResponse(EnumSet.of(CertificateType.IPSEC), MSRBS_NODE_FDN, nodeSerialNumber,
                subjectAltNameParam, wantedEnrollmentMode, MSRBS_MODEL_INFO, StandardProtocolFamily.INET) >>
                {throw new CppSecurityServiceException("Exception when invoking NscsPkiEntitiesManager")}
        when: 'security data for IPSec are generated'
        iscfService.generateSecurityDataIpsec(nodeId, subjectAltNameParam, wantedEnrollmentMode, MSRBS_MODEL_INFO, StandardProtocolFamily.INET)
        then: 'same exception is thrown'
        thrown(CppSecurityServiceException.class)
    }

    def 'When security data combined are generated and cannot get data  response, then exception is thrown' () {
        given: 'a Radio Node'
        nscsNodeUtility.getNodeNameFromFdn(MSRBS_NODE_FDN) >> MSRBS_NODE_FDN
        and: 'getSecurityDataResponse throws exception'
        securityDataCollector.getSecurityDataResponse(EnumSet.allOf(CertificateType), MSRBS_NODE_FDN, nodeSerialNumber,
                subjectAltNameParam, wantedEnrollmentMode, MSRBS_MODEL_INFO, StandardProtocolFamily.INET) >>
                {throw new CppSecurityServiceException("Exception when invoking NscsPkiEntitiesManager")}
        when: 'security data combined are generated'
        iscfService.generateSecurityDataCombo(nodeId, subjectAltNameParam, wantedEnrollmentMode, MSRBS_MODEL_INFO, StandardProtocolFamily.INET)
        then: 'same exception is thrown'
        thrown(CppSecurityServiceException.class)
    }

}
