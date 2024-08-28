/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */
package com.ericsson.nms.security.nscs.ejb.iscf;

import java.util.EnumSet;
import java.util.Set;
import java.net.StandardProtocolFamily;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import com.ericsson.nms.security.nscs.interceptor.EjbLoggerInterceptor;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.IscfService;
import com.ericsson.nms.security.nscs.api.cpp.level.CPPSecurityLevel;
import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.exception.IscfServiceException;
import com.ericsson.nms.security.nscs.api.instrumentation.NscsInstrumentationService;
import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType;
import com.ericsson.nms.security.nscs.api.iscf.IpsecArea;
import com.ericsson.nms.security.nscs.api.iscf.IscfResponse;
import com.ericsson.nms.security.nscs.api.iscf.NodeIdentifier;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType;
import com.ericsson.nms.security.nscs.iscf.CombinedIscfGenerator;
import com.ericsson.nms.security.nscs.iscf.IpsecIscfGenerator;
import com.ericsson.nms.security.nscs.iscf.IscfCancelHandler;
import com.ericsson.nms.security.nscs.iscf.IscfGeneratorFactory;
import com.ericsson.nms.security.nscs.iscf.IscfServiceValidators;
import com.ericsson.nms.security.nscs.iscf.SecurityDataCollector;
import com.ericsson.nms.security.nscs.iscf.SecurityLevelIscfGenerator;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;

/**
 * Main implementation of IscfService responsible for generating and returning valid ISCF XML content, an RBS Integrity Code and a Security
 * Configuration Checksum
 *
 * @author ealemca
 */
@Stateless
@Interceptors({ EjbLoggerInterceptor.class })
public class IscfServiceBean implements IscfService {

    @Inject
    private Logger logger;

    @Inject
    IscfServiceValidators iscfServiceValidators;

    @Inject
    IscfGeneratorFactory generatorFactory;

    @Inject
    IscfCancelHandler cancelHandler;

    @Inject
    NscsInstrumentationService nscsInstrumentationService;

    @Inject
    private SecurityDataCollector securityDataCollector;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    @Inject
    private NscsContextService nscsContextService;

    @Override
    public IscfResponse generate(final String logicalName, final String nodeFdn, final CPPSecurityLevel wantedSecLevel,
            final CPPSecurityLevel minimumSecLevel) {
        if (logger.isInfoEnabled()) {
            logger.info("[OLD] Generating ISCF for Security Level only: Wanted Level {}, Minimum Level {}", wantedSecLevel, minimumSecLevel);
        }
        final NodeModelInformation modelInfo = new NodeModelInformation("E.1.239", ModelIdentifierType.MIM_VERSION, "ERBS");

        IscfResponse response;
        try {
            response = generate(logicalName, nodeFdn, wantedSecLevel.getNewSecurityLevel(), minimumSecLevel.getNewSecurityLevel(),
                    EnrollmentMode.SCEP, modelInfo);
        } catch (final Exception e) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Exception: " + e.getMessage());
            throw e;
        }
        nscsInstrumentationService.updateSuccessfulIscfServiceInvocations();

        return response;
    }

    @Override
    public IscfResponse generate(final String logicalName, final String nodeFdn, final String ipsecUserLabel, final String ipsecSubjectAltName,
            final SubjectAltNameFormat subjectAltNameFormat, final Set<IpsecArea> wantedIpsecAreas) {
        if (logger.isInfoEnabled()) {
            logger.info("[OLD] Generating ISCF for IPSec only: IPSec areas {}", wantedIpsecAreas);
        }
        final BaseSubjectAltNameDataType subjectAltNameData = new SubjectAltNameStringType(ipsecSubjectAltName);
        final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(subjectAltNameFormat, subjectAltNameData);
        final NodeModelInformation modelInfo = new NodeModelInformation("E.1.239", ModelIdentifierType.MIM_VERSION, "ERBS");

        IscfResponse response;
        try {
            response = generate(logicalName, nodeFdn, ipsecUserLabel, subjectAltNameParam, wantedIpsecAreas, EnrollmentMode.SCEP, modelInfo);
        } catch (final Exception e) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Exception: " + e.getMessage());
            throw e;
        }
        nscsInstrumentationService.updateSuccessfulIscfServiceInvocations();

        return response;
    }

    @Override
    public IscfResponse generate(final String logicalName, final String nodeFdn, final CPPSecurityLevel wantedSecLevel,
            final CPPSecurityLevel minimumSecLevel, final String ipsecUserLabel, final String ipsecSubjectAltName,
            final SubjectAltNameFormat subjectAltNameFormat, final Set<IpsecArea> wantedIpSecAreas) {
        if (logger.isInfoEnabled()) {
            logger.info("[OLD] Generating ISCF for Security Level and IPSec: Wanted Level {}, Minimum Level {}, IPSec areas {}", wantedSecLevel,
                    minimumSecLevel, wantedIpSecAreas);
        }
        final BaseSubjectAltNameDataType subjectAltNameData = new SubjectAltNameStringType(ipsecSubjectAltName);
        final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(subjectAltNameFormat, subjectAltNameData);
        final NodeModelInformation modelInfo = new NodeModelInformation("E.1.239", ModelIdentifierType.MIM_VERSION, "ERBS");

        IscfResponse response;
        try {
            response = generate(logicalName, nodeFdn, wantedSecLevel.getNewSecurityLevel(), minimumSecLevel.getNewSecurityLevel(), ipsecUserLabel,
                    subjectAltNameParam, wantedIpSecAreas, EnrollmentMode.SCEP, modelInfo);
        } catch (final Exception e) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Exception: " + e.getMessage());
            throw e;
        }
        nscsInstrumentationService.updateSuccessfulIscfServiceInvocations();

        return response;
    }

    @Override
    public IscfResponse generate(final String logicalName, final String nodeFdn, final SecurityLevel wantedSecLevel,
            final SecurityLevel minimumSecLevel, final EnrollmentMode wantedEnrollmentMode, final NodeModelInformation modelInfo) {
        if (logger.isInfoEnabled()) {
            logger.info("Generating ISCF for O&M: Logical Name [{}], Node FDN [{}]", logicalName, nodeFdn);
            logger.info("Generating ISCF for O&M: Wanted Level [{}], Minimum Level [{}]", wantedSecLevel, minimumSecLevel);
            logger.info("Generating ISCF for O&M: Enrollment Mode [{}]", wantedEnrollmentMode);
            logger.info("Generating ISCF for O&M: Model Info [{}]", (modelInfo != null ? modelInfo.toString() : modelInfo));
        }

        nscsContextService.setInputNodeNameContextValue(nodeFdn);
        // Validate input parameters
        try {
            iscfServiceValidators.validateGenerateOam(logicalName, nodeFdn, wantedSecLevel, minimumSecLevel, wantedEnrollmentMode, modelInfo);
        } catch (final Exception eValidator) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Validation Exception: " + eValidator.getMessage());
            throw eValidator;
        }

        final SecurityLevelIscfGenerator generator = generatorFactory.getSecLevelGenerator(logicalName, nodeFdn, wantedSecLevel, minimumSecLevel,
                wantedEnrollmentMode, modelInfo);

        IscfResponse response;
        try {
            response = generator.generate();
        } catch (final Exception e) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Exception: " + e.getMessage());
            throw e;
        }
        nscsInstrumentationService.updateSuccessfulIscfServiceInvocations();

        return response;
    }

    @Override
    public IscfResponse generate(final String logicalName, final String nodeFdn, final String ipsecUserLabel,
            final SubjectAltNameParam ipsecSubjectAltName, final Set<IpsecArea> wantedIpsecAreas, final EnrollmentMode wantedEnrollmentMode,
            final NodeModelInformation modelInfo) {
        if (logger.isInfoEnabled()) {
            logger.info("Generating ISCF for IPSec: Logical Name [{}], Node FDN [{}]", logicalName, nodeFdn);
            logger.info("Generating ISCF for IPSec: IPSec areas [{}]", wantedIpsecAreas);
            logger.info("Generating ISCF for IPSec: Enrollment Mode [{}]", wantedEnrollmentMode);
            logger.info("Generating ISCF for IPSec: Model Info [{}]", (modelInfo != null ? modelInfo.toString() : modelInfo));
        }

        nscsContextService.setInputNodeNameContextValue(nodeFdn);
        // Validate input parameters
        try {
            iscfServiceValidators.validateGenerateIpsec(logicalName, nodeFdn, ipsecUserLabel, ipsecSubjectAltName, wantedIpsecAreas,
                    wantedEnrollmentMode, modelInfo);
        } catch (final Exception eValidator) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Validation Exception: " + eValidator.getMessage());
            throw eValidator;
        }

        final IpsecIscfGenerator generator = generatorFactory.getIpsecGenerator(logicalName, nodeFdn, ipsecUserLabel, ipsecSubjectAltName,
                wantedIpsecAreas, wantedEnrollmentMode, modelInfo);

        IscfResponse response;
        try {
            response = generator.generate();
        } catch (final Exception e) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Exception: " + e.getMessage());
            throw e;
        }
        nscsInstrumentationService.updateSuccessfulIscfServiceInvocations();

        return response;
    }

    @Override
    public IscfResponse generate(final String logicalName, final String nodeFdn, final SecurityLevel wantedSecLevel,
            final SecurityLevel minimumSecLevel, final String ipsecUserLabel, final SubjectAltNameParam ipsecSubjectAltName,
            final Set<IpsecArea> wantedIpSecAreas, final EnrollmentMode wantedEnrollmentMode, final NodeModelInformation modelInfo) {
        if (logger.isInfoEnabled()) {
            logger.info("Generating ISCF for COMBO: Logical Name [{}], Node FDN [{}]", logicalName, nodeFdn);
            logger.info("Generating ISCF for COMBO: Wanted Level [{}], Minimum Level [{}], IPSec areas [{}]", wantedSecLevel, minimumSecLevel,
                    wantedIpSecAreas);
            logger.info("Generating ISCF for COMBO: Enrollment Mode [{}]", wantedEnrollmentMode);
            logger.info("Generating ISCF for COMBO: Model Info [{}]", (modelInfo != null ? modelInfo.toString() : modelInfo));
        }

        nscsContextService.setInputNodeNameContextValue(nodeFdn);
        // Validate input parameters
        try {
            iscfServiceValidators.validateGenerateCombo(logicalName, nodeFdn, wantedSecLevel, minimumSecLevel, ipsecUserLabel, ipsecSubjectAltName,
                    wantedIpSecAreas, wantedEnrollmentMode, modelInfo);
        } catch (final Exception eValidator) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Validation Exception: " + eValidator.getMessage());
            throw eValidator;
        }

        final CombinedIscfGenerator generator = generatorFactory.getCombinedGenerator(logicalName, nodeFdn, wantedSecLevel, minimumSecLevel,
                ipsecUserLabel, ipsecSubjectAltName, wantedIpSecAreas, wantedEnrollmentMode, modelInfo);

        IscfResponse response;
        try {
            response = generator.generate();
        } catch (final Exception e) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Exception: " + e.getMessage());
            throw e;
        }
        nscsInstrumentationService.updateSuccessfulIscfServiceInvocations();

        return response;
    }

    @Override
    public void cancel(final String fdn) {
        nscsContextService.setInputNodeNameContextValue(fdn);
        if (logger.isInfoEnabled()) {
            logger.info("Cancelling ISCF info for node {}", fdn);
        }
        try {
            cancelHandler.cancel(fdn);
        } catch (final Exception e) {
            logger.error("ISCF Exception cancelling ScepEnrollment for [{}], message: ", fdn, e.getMessage());
            throw new IscfServiceException("Cancelling ISCF failed: " + e.getMessage());
        }
    }

    @Override
    public SecurityDataResponse generateSecurityDataOam(final String nodeFdn, final EnrollmentMode wantedEnrollmentMode,
            final NodeModelInformation modelInfo) {
        if (logger.isInfoEnabled()) {
            logger.info("Generate OAM Security Data for: node [{}] , enrollmentMode [{}] , nodeModelInformation [{}]", nodeFdn, wantedEnrollmentMode,
                    modelInfo);
        }

        nscsContextService.setInputNodeNameContextValue(nodeFdn);
        // Validate input parameters
        try {
            iscfServiceValidators.validateGenerateSecurityDataOam(nodeFdn, wantedEnrollmentMode, modelInfo);
        } catch (final Exception eValidator) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Validation Exception: " + eValidator.getMessage());
            throw eValidator;
        }
        String nodeName;
        try {
            nodeName = nscsNodeUtility.getNodeNameFromFdn(nodeFdn);
        } catch (final UnexpectedErrorException ex) {
            throw new IscfServiceException("Generate Security Data failed: " + ex.getMessage());
        }

        // Check if generation is compatible with already existing entities
        //        checkEntityCompatibility(EnumSet.of(CertificateType.OAM), nodeName);

        SecurityDataResponse secDataResp;
        try {
            secDataResp = securityDataCollector.getSecurityDataResponse(EnumSet.of(CertificateType.OAM), nodeName, null, null, wantedEnrollmentMode,
                    modelInfo);
        } catch (final Exception e) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Exception: " + e.getMessage());
            throw e;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("getSecurityDataResponse return: \nSecurityDataResponse [{}]", secDataResp);
        }
        nscsInstrumentationService.updateSuccessfulIscfServiceInvocations();

        return secDataResp;
    }

    @Override
    public SecurityDataResponse generateSecurityDataIpsec(final String nodeFdn, final SubjectAltNameParam ipsecSubjectAltName,
            final EnrollmentMode wantedEnrollmentMode, final NodeModelInformation modelInfo) {
        if (logger.isInfoEnabled()) {
            logger.info("Generate IPSEC Security Data for: node [{}] , SAN [{}] , enrollmentMode [{}] , nodeModelInformation [{}]", nodeFdn,
                    ipsecSubjectAltName, wantedEnrollmentMode, modelInfo);
        }

        nscsContextService.setInputNodeNameContextValue(nodeFdn);
        // Validate input parameters
        try {
            iscfServiceValidators.validateGenerateSecurityDataIpsec(nodeFdn, ipsecSubjectAltName, wantedEnrollmentMode, modelInfo);
        } catch (final Exception eValidator) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Validation Exception: " + eValidator.getMessage());
            throw eValidator;
        }
        String nodeName;
        try {
            nodeName = nscsNodeUtility.getNodeNameFromFdn(nodeFdn);
        } catch (final UnexpectedErrorException ex) {
            throw new IscfServiceException("Generate Security Data failed: " + ex.getMessage());
        }

        // Check if generation is compatible with already existing entities
        //        checkEntityCompatibility(EnumSet.of(CertificateType.IPSEC), nodeName);

        SecurityDataResponse secDataResp;
        try {
            secDataResp = securityDataCollector.getSecurityDataResponse(EnumSet.of(CertificateType.IPSEC), nodeName, null, ipsecSubjectAltName,
                    wantedEnrollmentMode, modelInfo);
        } catch (final Exception e) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Exception: " + e.getMessage());
            throw e;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("getSecurityDataResponse return: \nSecurityDataResponse [{}]", secDataResp);
        }
        nscsInstrumentationService.updateSuccessfulIscfServiceInvocations();

        return secDataResp;
    }

    @Override
    public SecurityDataResponse generateSecurityDataCombo(final String nodeFdn, final SubjectAltNameParam ipsecSubjectAltName,
            final EnrollmentMode wantedEnrollmentMode, final NodeModelInformation modelInfo) {
        if (logger.isInfoEnabled()) {
            logger.info("Generate COMBO Security Data for: node [{}] , SAN [{}] , enrollmentMode [{}] , nodeModelInformation [{}]", nodeFdn,
                    ipsecSubjectAltName, wantedEnrollmentMode, modelInfo);
        }

        nscsContextService.setInputNodeNameContextValue(nodeFdn);
        // Validate input parameters
        try {
            iscfServiceValidators.validateGenerateSecurityDataCombo(nodeFdn, ipsecSubjectAltName, wantedEnrollmentMode, modelInfo);
        } catch (final Exception eValidator) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Validation Exception: " + eValidator.getMessage());
            throw eValidator;
        }
        String nodeName;
        try {
            nodeName = nscsNodeUtility.getNodeNameFromFdn(nodeFdn);
        } catch (final UnexpectedErrorException ex) {
            throw new IscfServiceException("Generate Security Data failed: " + ex.getMessage());
        }

        // Check if generation is compatible with already existing entities
        //        checkEntityCompatibility(EnumSet.allOf(CertificateType.class), nodeName);

        SecurityDataResponse secDataResp;
        try {
            secDataResp = securityDataCollector.getSecurityDataResponse(EnumSet.allOf(CertificateType.class), nodeName, null, ipsecSubjectAltName,
                    wantedEnrollmentMode, modelInfo);
        } catch (final Exception e) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Exception: " + e.getMessage());
            throw e;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("generateSecurityDataCombo return: \nSecurityDataResponse [{}]", secDataResp);
        }
        nscsInstrumentationService.updateSuccessfulIscfServiceInvocations();

        return secDataResp;
    }

    @Override
    public SecurityDataResponse generateSecurityDataOam(final NodeIdentifier nodeId, final EnrollmentMode wantedEnrollmentMode,
            final NodeModelInformation modelInfo) {
        if (logger.isInfoEnabled()) {
            logger.info("Generate OAM Security Data for: node [{}] , enrollmentMode [{}] , nodeModelInformation [{}]", nodeId, wantedEnrollmentMode,
                    modelInfo);
        }

        nscsContextService.setInputNodeNameContextValue(nodeId.getFdn());
        // Validate input parameters
        try {
            iscfServiceValidators.validateGenerateSecurityDataOam(nodeId, wantedEnrollmentMode, modelInfo);
        } catch (final Exception eValidator) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Validation Exception: " + eValidator.getMessage());
            throw eValidator;
        }

        String nodeName;
        try {
            nodeName = nscsNodeUtility.getNodeNameFromFdn(nodeId.getFdn());
        } catch (final UnexpectedErrorException ex) {
            throw new IscfServiceException("Generate Security Data failed: " + ex.getMessage());
        }
        // Check if generation is compatible with already existing entities
        //        checkEntityCompatibility(EnumSet.of(CertificateType.OAM), nodeName);

        SecurityDataResponse secDataResp;
        try {
            secDataResp = securityDataCollector.getSecurityDataResponse(EnumSet.of(CertificateType.OAM), nodeName, nodeId.getSerialNumber(), null,
                    wantedEnrollmentMode, modelInfo);
        } catch (final Exception e) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Exception: " + e.getMessage());
            throw e;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("getSecurityDataResponse return: \nSecurityDataResponse [{}]", secDataResp);
        }
        nscsInstrumentationService.updateSuccessfulIscfServiceInvocations();

        return secDataResp;
    }

    @Override
    public SecurityDataResponse generateSecurityDataIpsec(final NodeIdentifier nodeId, final SubjectAltNameParam ipsecSubjectAltName,
            final EnrollmentMode wantedEnrollmentMode, final NodeModelInformation modelInfo) {
        if (logger.isInfoEnabled()) {
            logger.info("Generate IPSEC Security Data for: node [{}] , SAN [{}] , enrollmentMode [{}] , nodeModelInformation [{}]", nodeId,
                    (ipsecSubjectAltName == null) ? ipsecSubjectAltName
                            : ipsecSubjectAltName.getSubjectAltNameFormat() + " , " + ipsecSubjectAltName.getSubjectAltNameData(),
                    wantedEnrollmentMode, modelInfo);
        }

        nscsContextService.setInputNodeNameContextValue(nodeId.getFdn());
        // Validate input parameters
        try {
            iscfServiceValidators.validateGenerateSecurityDataIpsec(nodeId, ipsecSubjectAltName, wantedEnrollmentMode, modelInfo);
        } catch (final Exception eValidator) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Validation Exception: " + eValidator.getMessage());
            throw eValidator;
        }
        String nodeName;
        try {
            nodeName = nscsNodeUtility.getNodeNameFromFdn(nodeId.getFdn());
        } catch (final UnexpectedErrorException ex) {
            throw new IscfServiceException("Generate Security Data failed: " + ex.getMessage());
        }
        // Check if generation is compatible with already existing entities
        //        checkEntityCompatibility(EnumSet.of(CertificateType.IPSEC), nodeName);

        SecurityDataResponse secDataResp;
        try {
            secDataResp = securityDataCollector.getSecurityDataResponse(EnumSet.of(CertificateType.IPSEC), nodeName, nodeId.getSerialNumber(),
                    ipsecSubjectAltName, wantedEnrollmentMode, modelInfo);
        } catch (final Exception e) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Exception: " + e.getMessage());
            throw e;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("getSecurityDataResponse return: \nSecurityDataResponse [{}]", secDataResp);
        }
        nscsInstrumentationService.updateSuccessfulIscfServiceInvocations();

        return secDataResp;
    }

    @Override
    public SecurityDataResponse generateSecurityDataCombo(final NodeIdentifier nodeId, final SubjectAltNameParam ipsecSubjectAltName,
            final EnrollmentMode wantedEnrollmentMode, final NodeModelInformation modelInfo) {
        if (logger.isInfoEnabled()) {
            logger.info("Generate COMBO Security Data for: node [{}] , SAN [{}] , enrollmentMode [{}] , nodeModelInformation [{}]", nodeId,
                    ipsecSubjectAltName, wantedEnrollmentMode, modelInfo);
        }

        nscsContextService.setInputNodeNameContextValue(nodeId.getFdn());
        try {
            iscfServiceValidators.validateGenerateSecurityDataCombo(nodeId, ipsecSubjectAltName, wantedEnrollmentMode, modelInfo);
        } catch (final Exception eValidator) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Validation Exception: " + eValidator.getMessage());
            throw eValidator;
        }
        String nodeName;
        try {
            nodeName = nscsNodeUtility.getNodeNameFromFdn(nodeId.getFdn());
        } catch (final UnexpectedErrorException ex) {
            throw new IscfServiceException("Generate Security Data failed: " + ex.getMessage());
        }

        // Check if generation is compatible with already existing entities
        //        checkEntityCompatibility(EnumSet.allOf(CertificateType.class), nodeName);

        SecurityDataResponse secDataResp;
        try {
            secDataResp = securityDataCollector.getSecurityDataResponse(EnumSet.allOf(CertificateType.class), nodeName, nodeId.getSerialNumber(),
                    ipsecSubjectAltName, wantedEnrollmentMode, modelInfo);
        } catch (final Exception e) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Exception: " + e.getMessage());
            throw e;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("generateSecurityDataCombo return: \nSecurityDataResponse [{}]", secDataResp);
        }
        nscsInstrumentationService.updateSuccessfulIscfServiceInvocations();

        return secDataResp;
    }

    @Override
    public SecurityDataResponse generateSecurityDataOam(final NodeIdentifier nodeId, final SubjectAltNameParam subjectAltName,
            final EnrollmentMode wantedEnrollmentMode, final NodeModelInformation modelInfo) {
        if (logger.isInfoEnabled()) {
            logger.info("Generate OAM Security Data for: node [{}] , SAN [{}] , enrollmentMode [{}] , nodeModelInformation [{}]", nodeId,
                    (subjectAltName == null) ? subjectAltName
                            : subjectAltName.getSubjectAltNameFormat() + " , " + subjectAltName.getSubjectAltNameData(),
                    wantedEnrollmentMode, modelInfo);
        }
        SecurityDataResponse secDataResp = null;

        nscsContextService.setInputNodeNameContextValue(nodeId.getFdn());
        try {
            iscfServiceValidators.validateGenerateSecurityDataOam(nodeId, subjectAltName, wantedEnrollmentMode, modelInfo);
        } catch (final Exception eValidator) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Validation Exception: " + eValidator.getMessage());
            throw eValidator;
        }

        String nodeName;
        try {
            nodeName = nscsNodeUtility.getNodeNameFromFdn(nodeId.getFdn());
        } catch (final UnexpectedErrorException ex) {
            throw new IscfServiceException("Generate Security Data failed: " + ex.getMessage());
        }
        try {
            secDataResp = securityDataCollector.getSecurityDataResponse(EnumSet.of(CertificateType.OAM), nodeName, nodeId.getSerialNumber(),
                    subjectAltName, wantedEnrollmentMode, modelInfo);
        } catch (final Exception e) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Exception: " + e.getMessage());
            throw e;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("getSecurityDataResponse return: \nSecurityDataResponse [{}]", secDataResp);
        }
        nscsInstrumentationService.updateSuccessfulIscfServiceInvocations();

        return secDataResp;
    }

    @Override
    public SecurityDataResponse generateSecurityDataOam(final NodeIdentifier nodeId, final EnrollmentMode wantedEnrollmentMode,
            final NodeModelInformation modelInfo, StandardProtocolFamily ipVersion) {
        if (logger.isInfoEnabled()) {
            logger.info("Generate OAM Security Data for: node [{}] , enrollmentMode [{}] , nodeModelInformation [{}] , ipVersion [{}]",
                    nodeId, wantedEnrollmentMode, modelInfo, ipVersion);
        }

        nscsContextService.setInputNodeNameContextValue(nodeId.getFdn());
        // Validate input parameters
        try {
            iscfServiceValidators.validateGenerateSecurityDataOam(nodeId, wantedEnrollmentMode, modelInfo);
        } catch (final Exception eValidator) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Validation Exception: [{}]", eValidator.getMessage());
            throw eValidator;
        }

        String nodeName;
        try {
            nodeName = nscsNodeUtility.getNodeNameFromFdn(nodeId.getFdn());
        } catch (final UnexpectedErrorException ex) {
            throw new IscfServiceException("Generate Security Data failed: " + ex);
        }

        SecurityDataResponse secDataResp;
        try {
            secDataResp = securityDataCollector.getSecurityDataResponse(EnumSet.of(CertificateType.OAM), nodeName, nodeId.getSerialNumber(), null,
                    wantedEnrollmentMode, modelInfo, ipVersion);
        } catch (final Exception e) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Exception: [{}]", e.getMessage());
            throw e;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("getSecurityDataResponse return: \nSecurityDataResponse [{}]", secDataResp);
        }
        nscsInstrumentationService.updateSuccessfulIscfServiceInvocations();

        return secDataResp;
    }

    @Override
    public SecurityDataResponse generateSecurityDataOam(NodeIdentifier nodeId, SubjectAltNameParam subjectAltName,
            EnrollmentMode wantedEnrollmentMode, NodeModelInformation modelInfo, StandardProtocolFamily ipVersion) {
        if (logger.isInfoEnabled()) {
            logger.info("Generate OAM Security Data for: node [{}] , SAN [{}] , enrollmentMode [{}] , nodeModelInformation [{}] , ipVersion [{}]",
                    nodeId, (subjectAltName == null) ? subjectAltName
                            : (subjectAltName.getSubjectAltNameFormat() + " , " + subjectAltName.getSubjectAltNameData()),
                    wantedEnrollmentMode, modelInfo, ipVersion);
        }
        SecurityDataResponse secDataResp = null;

        nscsContextService.setInputNodeNameContextValue(nodeId.getFdn());
        try {
            iscfServiceValidators.validateGenerateSecurityDataOam(nodeId, subjectAltName, wantedEnrollmentMode, modelInfo);
        } catch (final Exception eValidator) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Validation Exception: [{}]", eValidator.getMessage());
            throw eValidator;
        }

        String nodeName;
        try {
            nodeName = nscsNodeUtility.getNodeNameFromFdn(nodeId.getFdn());
        } catch (final UnexpectedErrorException ex) {
            throw new IscfServiceException("Generate Security Data failed: " + ex);
        }
        try {
            secDataResp = securityDataCollector.getSecurityDataResponse(EnumSet.of(CertificateType.OAM), nodeName, nodeId.getSerialNumber(),
                    subjectAltName, wantedEnrollmentMode, modelInfo, ipVersion);
        } catch (final Exception e) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Exception: [{}]", e.getMessage());
            throw e;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("getSecurityDataResponse return: \nSecurityDataResponse [{}]", secDataResp);
        }
        nscsInstrumentationService.updateSuccessfulIscfServiceInvocations();

        return secDataResp;
    }

    @Override
    public SecurityDataResponse generateSecurityDataIpsec(NodeIdentifier nodeId, SubjectAltNameParam ipsecSubjectAltName,
            EnrollmentMode wantedEnrollmentMode, NodeModelInformation modelInfo, StandardProtocolFamily ipVersion) {
        if (logger.isInfoEnabled()) {
            logger.info("Generate IPSEC Security Data for: node [{}] , SAN [{}] , enrollmentMode [{}] , nodeModelInformation [{}] , ipVersion [{}]",
                    nodeId, (ipsecSubjectAltName == null) ? ipsecSubjectAltName
                            : (ipsecSubjectAltName.getSubjectAltNameFormat() + " , " + ipsecSubjectAltName.getSubjectAltNameData()),
                    wantedEnrollmentMode, modelInfo, ipVersion);
        }

        nscsContextService.setInputNodeNameContextValue(nodeId.getFdn());
        // Validate input parameters
        try {
            iscfServiceValidators.validateGenerateSecurityDataIpsec(nodeId, ipsecSubjectAltName, wantedEnrollmentMode, modelInfo);
        } catch (final Exception eValidator) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Validation Exception: [{}]", eValidator.getMessage());
            throw eValidator;
        }
        String nodeName;
        try {
            nodeName = nscsNodeUtility.getNodeNameFromFdn(nodeId.getFdn());
        } catch (final UnexpectedErrorException ex) {
            throw new IscfServiceException("Generate Security Data failed: " + ex);
        }

        SecurityDataResponse secDataResp;
        try {
            secDataResp = securityDataCollector.getSecurityDataResponse(EnumSet.of(CertificateType.IPSEC), nodeName, nodeId.getSerialNumber(),
                    ipsecSubjectAltName, wantedEnrollmentMode, modelInfo, ipVersion);
        } catch (final Exception e) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Exception: [{}]", e.getMessage());
            throw e;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("getSecurityDataResponse return: \nSecurityDataResponse [{}]", secDataResp);
        }
        nscsInstrumentationService.updateSuccessfulIscfServiceInvocations();

        return secDataResp;
    }

    @Override
    public SecurityDataResponse generateSecurityDataCombo(NodeIdentifier nodeId, SubjectAltNameParam ipsecSubjectAltName,
            EnrollmentMode wantedEnrollmentMode, NodeModelInformation modelInfo, StandardProtocolFamily ipVersion) {
        if (logger.isInfoEnabled()) {
            logger.info("Generate COMBO Security Data for: node [{}] , SAN [{}] , enrollmentMode [{}] , nodeModelInformation [{}] , ipVersion [{}]",
                    nodeId, (ipsecSubjectAltName == null) ? ipsecSubjectAltName
                            : (ipsecSubjectAltName.getSubjectAltNameFormat() + " , " + ipsecSubjectAltName.getSubjectAltNameData()),
                    wantedEnrollmentMode, modelInfo, ipVersion);
        }

        nscsContextService.setInputNodeNameContextValue(nodeId.getFdn());
        try {
            iscfServiceValidators.validateGenerateSecurityDataCombo(nodeId, ipsecSubjectAltName, wantedEnrollmentMode, modelInfo);
        } catch (final Exception eValidator) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Validation Exception: [{}]", eValidator.getMessage());
            throw eValidator;
        }
        String nodeName;
        try {
            nodeName = nscsNodeUtility.getNodeNameFromFdn(nodeId.getFdn());
        } catch (final UnexpectedErrorException ex) {
            throw new IscfServiceException("Generate Security Data failed: " + ex);
        }

        SecurityDataResponse secDataResp;
        try {
            secDataResp = securityDataCollector.getSecurityDataResponse(EnumSet.allOf(CertificateType.class), nodeName, nodeId.getSerialNumber(),
                    ipsecSubjectAltName, wantedEnrollmentMode, modelInfo, ipVersion);
        } catch (final Exception e) {
            nscsInstrumentationService.updateFailedIscfServiceInvocations();
            logger.error("ISCF Exception: [{}]", e.getMessage());
            throw e;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("generateSecurityDataCombo return: \nSecurityDataResponse [{}]", secDataResp);
        }
        nscsInstrumentationService.updateSuccessfulIscfServiceInvocations();

        return secDataResp;
    }
}
