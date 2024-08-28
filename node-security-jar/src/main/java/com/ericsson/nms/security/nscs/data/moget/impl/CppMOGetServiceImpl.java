package com.ericsson.nms.security.nscs.data.moget.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.enums.TrustCategoryType;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NtpOperationNotSupportedException;
import com.ericsson.nms.security.nscs.api.exception.SecurityMODoesNotExistException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.CppIpSecStatusUtility;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.IpSecNodeValidatorUtility;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceBean.KeyLength;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CppManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec.IpSecCertEnrollStateValue;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec.IpSecCertInfo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Security;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Security.CertEnrollStateValue;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Security.NodeCertInfo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TimeSetting;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionState;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter;
import com.ericsson.nms.security.nscs.data.moget.MOGetService;
import com.ericsson.nms.security.nscs.data.moget.MOGetServiceType;
import com.ericsson.nms.security.nscs.data.moget.param.CertStateInfo;
import com.ericsson.nms.security.nscs.data.moget.param.NtpServer;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.command.utility.NscsNameMultipleValueResponseBuilder;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.nms.security.nscs.pki.NscsPkiUtils;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.util.ExtendedCertDetails;
import com.ericsson.nms.security.nscs.util.NtpConstants;
import com.ericsson.nms.security.nscs.utilities.NSCSCppNodeUtility;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

@MOGetServiceType(moGetServiceType = "CPP")
public class CppMOGetServiceImpl implements MOGetService {

    public static final String CERT_ENROLL_STATE = "certEnrollState";
    public static final String CERT_ENROLL_ERROR_MSG = "certEnrollErrorMsg";
    public static final String CERTIFICATE = "certificate";

    public static final String TRUST_CERT_INSTALL_STATE = "trustCertInstState";
    public static final String TRUST_CERT_INSTALL_ERROR_MSG = "trustCertInstErrorMsg";
    public static final String INSTALLED_TRUSTED_CERTIFICATES = "installedTrustedCertificates";
    public static final String SERIAL_NUMBER = "serialNumber";
    public static final String ISSUER = "issuer";
    public static final String SUBJECT = "subject";
    public static final String SUBJECT_ALT_NAME = "subjectAltName";
    public static final String EMPTY_FIELD = NscsNameMultipleValueResponseBuilder.EMPTY_STRING;
    public static final String ACTIVATED = "ACTIVATED";
    public static final String DISABLED = "DISABLED";
    public static final String CATEGORY = "category";

    @Inject
    private Logger logger;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    IpSecNodeValidatorUtility ipsecvalidator;

    @Inject
    CppIpSecStatusUtility ipSecStatusUtility;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Inject
    private NSCSCppNodeUtility nscsCppNodeUtility;

    @Inject
    private NodeValidatorUtility nodeValidatorUtility;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsModelServiceImpl nscsModelServiceImpl;

    @Override
    public CertStateInfo getCertificateIssueStateInfo(final NodeReference nodeRef, final String certType) {

        logger.debug("get Cpp CertificateIssueStateInfo for nodeRef[{}] and certType[{}]", nodeRef, certType);
        if (nodeRef == null || certType == null || certType.isEmpty()) {
            logger.error("get Cpp CertificateIssueStateInfo : wrong params : nodeRef[{}] and certType[{}]", nodeRef, certType);
            return null;
        }

        final NormalizableNodeReference node = reader.getNormalizableNodeReference(nodeRef);

        String certEnrollState = EMPTY_FIELD;
        String certEnrollErrMsg = EMPTY_FIELD;
        String serialNumber = EMPTY_FIELD;
        String issuer = EMPTY_FIELD;
        String subjectName = EMPTY_FIELD;
        String subjectAltName = EMPTY_FIELD;

        final CmResponse cmResponseCertEnrollState = reader.getMOAttribute(node, getMoObjectForCertType(certType).type(), getMoObjectForCertType(certType).namespace(),
                getMoAttributeForCertType(node, certType, CERT_ENROLL_STATE));

        if (cmResponseCertEnrollState == null || cmResponseCertEnrollState.getCmObjects() == null || cmResponseCertEnrollState.getCmObjects().isEmpty()) {
            logger.debug("No object returned for certEnrollState for nodeRef[{}] and certType[{}]", nodeRef, certType);
            certEnrollState = NscsNameMultipleValueResponseBuilder.NOT_AVAILABLE;
        } else {
            for (final CmObject cmObject : cmResponseCertEnrollState.getCmObjects()) {
                try {
                    certEnrollState = (String) cmObject.getAttributes().get(getMoAttributeForCertType(node, certType, CERT_ENROLL_STATE));
                    logger.debug("certEnrollState for node {} is {}", node.getName(), certEnrollState);
                } catch (final NullPointerException exception) {
                    logger.error("No attribute details returned for nodeRef[{}] and certType[{}]", nodeRef, certType);
                }
            }

            if (certType.equals(CertificateType.OAM.toString())) {
                certEnrollState = normalizeEnrollState(certEnrollState);
                logger.debug("After conversion certEnrollState for node {} is {}", node.getName(), certEnrollState);
            }

            if (certEnrollState.equals(CertEnrollStateValue.ERROR.toString())) {

                final CmResponse cmResponseCertEnrollErrMsg = reader.getMOAttribute(node, getMoObjectForCertType(certType).type(), getMoObjectForCertType(certType).namespace(),
                        getMoAttributeForCertType(node, certType, CERT_ENROLL_ERROR_MSG));

                if (cmResponseCertEnrollErrMsg.getCmObjects().isEmpty()) {
                    logger.debug("No object returned for certEnrollErrMsg for nodeRef[{}] and certType[{}]", nodeRef, certType);
                    certEnrollErrMsg = NscsNameMultipleValueResponseBuilder.NOT_AVAILABLE;
                }
                for (final CmObject cmObject : cmResponseCertEnrollErrMsg.getCmObjects()) {
                    try {
                        certEnrollErrMsg = (String) cmObject.getAttributes().get(getMoAttributeForCertType(node, certType, CERT_ENROLL_ERROR_MSG));
                        logger.debug("certEnrollErrorMsg for node {} is {}", node.getName(), certEnrollErrMsg);

                        certEnrollErrMsg = Pattern.compile(", <<.*>>").matcher(certEnrollErrMsg).replaceAll("");
                        logger.debug("After conversion certEnrollErrorMsg for node {} is {}", node.getName(), certEnrollErrMsg);

                    } catch (final NullPointerException exception) {
                        logger.error("No attribute details returned for nodeRef[{}] and certType[{}]", nodeRef, certType);
                    }
                }
            } else {
                certEnrollErrMsg = NscsNameMultipleValueResponseBuilder.NOT_APPLICABLE;
            }
        }

        final CmResponse cmResponseCertificate = reader.getMOAttribute(node, getMoObjectForCertType(certType).type(), getMoObjectForCertType(certType).namespace(),
                getMoAttributeForCertType(node, certType, CERTIFICATE));
        if (cmResponseCertificate == null || cmResponseCertificate.getCmObjects() == null || cmResponseCertificate.getCmObjects().isEmpty()) {
            logger.debug("No object returned for certificate for nodeRef[{}] and certType[{}]", nodeRef, certType);
            serialNumber = NscsNameMultipleValueResponseBuilder.NOT_AVAILABLE;
            issuer = EMPTY_FIELD;
            subjectName = NscsNameMultipleValueResponseBuilder.NOT_AVAILABLE;
            subjectAltName = certType.equals(CertificateType.IPSEC.toString()) ? NscsNameMultipleValueResponseBuilder.NOT_AVAILABLE : NscsNameMultipleValueResponseBuilder.NOT_APPLICABLE;
        } else {
            for (final CmObject cmObject : cmResponseCertificate.getCmObjects()) {
                try {
                    @SuppressWarnings("unchecked")
                    final Map<String, Object> certInfo = (Map<String, Object>) cmObject.getAttributes().get(getMoAttributeForCertType(node, certType, CERTIFICATE));
                    logger.debug(getMoAttributeForCertType(node, certType, CERTIFICATE));
                    logger.debug("Attributes for node {} are {}", node.getName(), cmObject.getAttributes());
                    logger.debug("certInfo for node {} is {}", node.getName(), certInfo);

                    serialNumber = (String) certInfo.get(getMoAttributeForCertType(node, certType, SERIAL_NUMBER));
                    logger.debug("serialNumber for node {} in cmResponse is {}", node.getName(), serialNumber);
                    if (serialNumber.isEmpty()) {
                        serialNumber = EMPTY_FIELD;
                    }
                    logger.debug("serialNumber for node {} is {}", node.getName(), serialNumber);

                    issuer = (String) certInfo.get(getMoAttributeForCertType(node, certType, ISSUER));
                    logger.debug("issuer for node {} in cmResponse is {}", node.getName(), issuer);
                    if (issuer.isEmpty()) {
                        issuer = NscsNameMultipleValueResponseBuilder.NOT_AVAILABLE;
                    }
                    logger.debug("issuer for node {} is {}", node.getName(), issuer);

                    subjectName = (String) certInfo.get(getMoAttributeForCertType(node, certType, SUBJECT));
                    logger.debug("subject for node {} in cmResponse is {}", node.getName(), subjectName);
                    if (subjectName.isEmpty()) {
                        subjectName = NscsNameMultipleValueResponseBuilder.NOT_AVAILABLE;
                    }
                    logger.debug("subject for node {} is {}", node.getName(), subjectName);

                    if (certType.equals(CertificateType.IPSEC.toString())) {
                        subjectAltName = (String) certInfo.get(getMoAttributeForCertType(node, certType, SUBJECT_ALT_NAME));
                        if (subjectAltName.isEmpty()) {
                            subjectAltName = NscsNameMultipleValueResponseBuilder.NOT_AVAILABLE;
                        }
                    } else {
                        subjectAltName = NscsNameMultipleValueResponseBuilder.NOT_APPLICABLE;
                    }
                    logger.debug("subjectAltName for node {} is {}", node.getName(), subjectAltName);
                } catch (final NullPointerException exception) {
                    logger.error("No attribute details returned!");
                }
            }
        }

        String nodeName = node.getName();
        if (node.getNormalizedRef() != null) {
            nodeName = node.getNormalizedRef().getFdn();
        }

        return new CertStateInfo(nodeName, certEnrollState, certEnrollErrMsg, ExtendedCertDetails.certDetailsFactory(issuer, serialNumber, subjectName, subjectAltName));
    }

    @Override
    public CertStateInfo getTrustCertificateStateInfo(final NodeReference nodeRef, final String trustCategory) {

        final NormalizableNodeReference node = reader.getNormalizableNodeReference(nodeRef);

        String trustCertInstallState = EMPTY_FIELD;
        String trustCertInstallErrMsg = EMPTY_FIELD;

        final CmResponse cmResponseTrustCertInstallState = reader.getMOAttribute(node, getMoObjectForTrustCategory(trustCategory).type(), getMoObjectForTrustCategory(trustCategory).namespace(),
                getMoAttributeForTrustCategory(node, trustCategory, TRUST_CERT_INSTALL_STATE));

        if (cmResponseTrustCertInstallState == null || cmResponseTrustCertInstallState.getCmObjects() == null || cmResponseTrustCertInstallState.getCmObjects().isEmpty()) {
            logger.debug("No object returned for TrustCertInstallState for nodeRef[{}] and certType[{}]", nodeRef, trustCategory);
            trustCertInstallState = NscsNameMultipleValueResponseBuilder.NOT_AVAILABLE;
        } else {
            for (final CmObject cmObject : cmResponseTrustCertInstallState.getCmObjects()) {
                trustCertInstallState = getTrustCertInstallState(trustCertInstallState, node, trustCategory, cmObject);
            }

            if (trustCertInstallState.equals(CertEnrollStateValue.ERROR.toString()) && trustCategory.equals(TrustCategoryType.IPSEC.toString())) {
                trustCertInstallErrMsg = getTrustCertInstallErrMsg(trustCertInstallErrMsg, trustCategory, node);
            } else {
                trustCertInstallErrMsg = NscsNameMultipleValueResponseBuilder.NOT_APPLICABLE;
            }
        }

        String nodeName = node.getName();
        if (node.getNormalizedRef() != null) {
            nodeName = node.getNormalizedRef().getFdn();
        }

        return new CertStateInfo(nodeName, trustCertInstallState, trustCertInstallErrMsg, getTrustedCertificates(trustCategory, node));
    }

    private String getTrustCertInstallState(String trustCertInstallState, final NormalizableNodeReference node, String trustCategory, final CmObject cmObject) {
        try {
            final Object obj = cmObject.getAttributes().get(getMoAttributeForTrustCategory(node, trustCategory, TRUST_CERT_INSTALL_STATE));
            if (obj instanceof Boolean) {
                trustCertInstallState = normalizeTrustedCertInstallState(((Boolean) obj).toString());
                logger.debug("After conversion trustCertInstallState for node {} is {}", node.getName(), trustCertInstallState);
            } else {
                trustCertInstallState = (String) obj;
                logger.debug("trustCertInstallState for node {} is {}", node.getName(), trustCertInstallState);
            }

        } catch (final NullPointerException exception) {
            logger.error("No attribute details returned for node[{}] and certType[{}]", node, trustCategory);
        }
        return trustCertInstallState;
    }

    private List<CertDetails> getTrustedCertificates(final String trustCategory, final NormalizableNodeReference node) {
        final List<CertDetails> trustedCertificates = new ArrayList<>();
        final CmResponse cmResponseinstalledTrustedCertificates = reader.getMOAttribute(node, getMoObjectForTrustCategory(trustCategory).type(),
                getMoObjectForTrustCategory(trustCategory).namespace(), getMoAttributeForTrustCategory(node, trustCategory, INSTALLED_TRUSTED_CERTIFICATES));

        if (cmResponseinstalledTrustedCertificates == null || cmResponseinstalledTrustedCertificates.getCmObjects() == null || cmResponseinstalledTrustedCertificates.getCmObjects().isEmpty()) {
            logger.debug("No object returned for installedTrustedCertificates for node[{}] and trustCategory Type[{}]", node, trustCategory);
        } else {
            for (final CmObject cmObject : cmResponseinstalledTrustedCertificates.getCmObjects()) {

                try {
                    @SuppressWarnings("unchecked")
                    final List<Map<String, Object>> certInfos = (List<Map<String, Object>>) cmObject.getAttributes().get(
                            getMoAttributeForTrustCategory(node, trustCategory, INSTALLED_TRUSTED_CERTIFICATES));
                    logger.debug(getMoAttributeForTrustCategory(node, trustCategory, INSTALLED_TRUSTED_CERTIFICATES));
                    logger.debug("cmObject.getAttributes() are {}", cmObject.getAttributes());
                    logger.debug("cmObject is {}", cmObject);
                    logger.debug("certInfo for node {} is {}", node.getName(), certInfos);
                    if (certInfos == null || certInfos.isEmpty()) {
                        logger.debug("CertInfo is empty!!");
                    } else {
                        addCertDetails(trustedCertificates, trustCategory, node, certInfos);
                    }
                } catch (final NullPointerException exception) {
                    logger.error("No attribute details returned!");
                }
            }
        }

        return trustedCertificates;
    }

    private List<CertDetails> addCertDetails(final List<CertDetails> trustedCertificates, final String trustCategory, final NormalizableNodeReference node, final List<Map<String, Object>> certInfos) {

        for (final Map<String, Object> certInfo : certInfos) {
            String subjectAltName = TrustCategoryType.IPSEC.toString().equals(trustCategory) ? NscsNameMultipleValueResponseBuilder.NOT_AVAILABLE : NscsNameMultipleValueResponseBuilder.NOT_APPLICABLE;
            String certCategory = NscsNameMultipleValueResponseBuilder.NOT_APPLICABLE;


            final String serialNumber = (String) certInfo.get(getMoAttributeForTrustCategory(node, trustCategory, SERIAL_NUMBER));
            logger.debug("serialNumber for node {} is {}", node.getName(), serialNumber);
            final String issuer = (String) certInfo.get(getMoAttributeForTrustCategory(node, trustCategory, ISSUER));
            logger.debug("issuer for node {} is {}", node.getName(), issuer);
            final String subject = (String) certInfo.get(getMoAttributeForTrustCategory(node, trustCategory, SUBJECT));

            logger.debug("subject for node {} is {}", node.getName(), subject);

            CertDetails certDetails = null;
            if (TrustCategoryType.IPSEC.toString().equals(trustCategory)) {
                subjectAltName = (String) certInfo.get(getMoAttributeForTrustCategory(node, trustCategory, SUBJECT_ALT_NAME));
                logger.debug("subject for node {} is {}", node.getName(), subject);
                certDetails = ExtendedCertDetails.certDetailsFactory(issuer, serialNumber, subject, subjectAltName, certCategory);
                trustedCertificates.add(certDetails);
            } else if (TrustCategoryType.OAM.toString().equals(trustCategory)) {
                certCategory = (String) certInfo.get(getMoAttributeForTrustCategory(node, trustCategory, CATEGORY));
                logger.debug("category for node in string {} is {}", node.getName(), certCategory);
                if (!certCategory.equals(TrustedCertCategory.LOCAL_AA_DB_FILE_SIGNERS.toString())) {
                    certDetails = ExtendedCertDetails.certDetailsFactory(issuer, serialNumber, subject, subjectAltName, certCategory);
                    trustedCertificates.add(certDetails);
                }
            } else if (TrustCategoryType.LAAD.toString().equals(trustCategory)) {
                certCategory = (String) certInfo.get(getMoAttributeForTrustCategory(node, trustCategory, CATEGORY));
                logger.debug("category for node in string {} is {}", node.getName(), certCategory);
                if (certCategory.equals(TrustedCertCategory.LOCAL_AA_DB_FILE_SIGNERS.toString())) {
                    certDetails = ExtendedCertDetails.certDetailsFactory(issuer, serialNumber, subject, subjectAltName, certCategory);
                    trustedCertificates.add(certDetails);
                }
            }
        }

        logger.debug("Final trusted certs {} size : {}" , trustedCertificates, trustedCertificates.size());
        return trustedCertificates;

    }

    public String getTrustCertInstallErrMsg(String trustCertInstallErrMsg, final String trustCategory, final NormalizableNodeReference node) {
        final CmResponse cmResponseTrustCertInstallErrMsg = reader.getMOAttribute(node, getMoObjectForTrustCategory(trustCategory).type(), getMoObjectForTrustCategory(trustCategory).namespace(),
                getMoAttributeForTrustCategory(node, trustCategory, TRUST_CERT_INSTALL_ERROR_MSG));
        if (cmResponseTrustCertInstallErrMsg == null || cmResponseTrustCertInstallErrMsg.getCmObjects() == null || cmResponseTrustCertInstallErrMsg.getCmObjects().isEmpty()) {
            logger.debug("No object returned for trustCertInstallErrMsg for node[{}] and certType[{}]", node, trustCategory);
            trustCertInstallErrMsg = NscsNameMultipleValueResponseBuilder.NOT_AVAILABLE;
        }
        try {
            if (cmResponseTrustCertInstallErrMsg != null) {
                for (final CmObject cmObject : cmResponseTrustCertInstallErrMsg.getCmObjects()) {
                    trustCertInstallErrMsg = (String) cmObject.getAttributes().get(getMoAttributeForTrustCategory(node, trustCategory, TRUST_CERT_INSTALL_ERROR_MSG));
                    logger.debug("trustCertInstallErrMsg for node {} is {}", node.getName(), trustCertInstallErrMsg);

                    trustCertInstallErrMsg = Pattern.compile(", <<.*>>").matcher(trustCertInstallErrMsg).replaceAll("");
                    logger.debug("After conversion trustCertInstallErrMsg for node {} is {}", node.getName(), trustCertInstallErrMsg);

                    if (trustCertInstallErrMsg.isEmpty()) {
                        trustCertInstallErrMsg = NscsNameMultipleValueResponseBuilder.NOT_AVAILABLE;
                    }
                }
            }

        } catch (final NullPointerException exception) {
            logger.error("No attribute details returned for node[{}] and certType[{}]", node, trustCategory);
        }

        return trustCertInstallErrMsg;
    }

    /**
     * 
     * @param certType
     * @return
     */
    private Mo getMoObjectForCertType(final String certType) {
        Mo wantedMo;
        if (certType.equals(CertificateType.IPSEC.toString())) {
            wantedMo = Model.ME_CONTEXT.managedElement.ipSystem.ipSec;
        } else {
            wantedMo = Model.ME_CONTEXT.managedElement.systemFunctions.security;
        }

        return wantedMo;
    }

    /**
     * 
     * @param trustCategory
     * @return
     */
    private Mo getMoObjectForTrustCategory(final String trustCategory) {

        Mo wantedMo;

        if (trustCategory.equals(TrustCategoryType.IPSEC.toString())) {
            wantedMo = Model.ME_CONTEXT.managedElement.ipSystem.ipSec;
        } else {
            wantedMo = Model.ME_CONTEXT.managedElement.systemFunctions.security;
        }
        return wantedMo;
    }

    /**
     * 
     * @param node
     * @param certType
     * @param wantedAttribute
     * @return
     */
    private String getMoAttributeForCertType(final NormalizableNodeReference node, final String certType, final String wantedAttribute) {

        final String errString = "";

        if (certType.equals(CertificateType.IPSEC.toString())) {

            switch (wantedAttribute) {
            case CERT_ENROLL_STATE:
                return IpSec.CERT_ENROLL_STATE;
            case CERT_ENROLL_ERROR_MSG:
                return IpSec.CERT_ENROLL_ERROR_MSG;
            case CERTIFICATE:
                return IpSec.CERTIFICATE;
            case TRUST_CERT_INSTALL_STATE:
                return IpSec.TRUSTED_CERT_INST_STATE;
            case TRUST_CERT_INSTALL_ERROR_MSG:
                return IpSec.TRUSTED_CERT_INST_ERROR_MSG;
            case INSTALLED_TRUSTED_CERTIFICATES:
                return IpSec.INSTALLED_TRUSTED_CERTIFICATES;
            case ISSUER:
                return IpSecCertInfo.ISSUER;
            case SERIAL_NUMBER:
                return IpSecCertInfo.SERIAL_NUMBER;
            case SUBJECT:
                return IpSecCertInfo.SUBJECT;
            case SUBJECT_ALT_NAME:
                return IpSecCertInfo.SUBJECT_ALT_NAME;
            default:
                logger.error(NscsErrorCodes.INVALID_WANTED_MO, wantedAttribute, node, certType);
                return errString;
            }
        } else if (certType.equals(CertificateType.OAM.toString())) {
            switch (wantedAttribute) {
            case CERT_ENROLL_STATE:
                return Security.CERT_ENROLL_STATE;
            case CERT_ENROLL_ERROR_MSG:
                return Security.CERT_ENROLL_ERROR_MSG;
            case CERTIFICATE:
                return Security.NODE_CERTIFICATE;
            case TRUST_CERT_INSTALL_STATE:
                return Security.TRUSTED_CERTIFICATE_INSTALLATION_FAILURE;
            case INSTALLED_TRUSTED_CERTIFICATES:
                return Security.INSTALLED_TRUSTED_CERTIFICATES;
            case ISSUER:
                return NodeCertInfo.ISSUER;
            case SERIAL_NUMBER:
                return NodeCertInfo.SERIAL_NUMBER;
            case SUBJECT:
                return NodeCertInfo.SUBJECT;
            case CATEGORY:
                return NodeCertInfo.CATEGORY;
            default:
                logger.error(NscsErrorCodes.INVALID_WANTED_MO, wantedAttribute, node, certType);
                return errString;
            }
        } else {
            logger.error("Invalid Certificate type {} for node {}", certType, node);
            return errString;
        }
    }

    /**
     * 
     * @param node
     * @param trustCategory
     * @param wantedAttribute
     * @return
     */
    private String getMoAttributeForTrustCategory(final NormalizableNodeReference node, final String trustCategory, final String wantedAttribute) {

        final String errString = "";

        if (trustCategory.equals(TrustCategoryType.IPSEC.toString())) {

            switch (wantedAttribute) {
            case CERT_ENROLL_STATE:
                return IpSec.CERT_ENROLL_STATE;
            case CERT_ENROLL_ERROR_MSG:
                return IpSec.CERT_ENROLL_ERROR_MSG;
            case CERTIFICATE:
                return IpSec.CERTIFICATE;
            case TRUST_CERT_INSTALL_STATE:
                return IpSec.TRUSTED_CERT_INST_STATE;
            case TRUST_CERT_INSTALL_ERROR_MSG:
                return IpSec.TRUSTED_CERT_INST_ERROR_MSG;
            case INSTALLED_TRUSTED_CERTIFICATES:
                return IpSec.INSTALLED_TRUSTED_CERTIFICATES;
            case ISSUER:
                return IpSecCertInfo.ISSUER;
            case SERIAL_NUMBER:
                return IpSecCertInfo.SERIAL_NUMBER;
            case SUBJECT:
                return IpSecCertInfo.SUBJECT;
            case SUBJECT_ALT_NAME:
                return IpSecCertInfo.SUBJECT_ALT_NAME;
            default:
                logger.error(NscsErrorCodes.INVALID_WANTED_MO, wantedAttribute, node, trustCategory);
                return errString;
            }
        } else if (trustCategory.equals(TrustCategoryType.OAM.toString()) || trustCategory.equals(TrustCategoryType.LAAD.toString())) {
            switch (wantedAttribute) {
            case CERT_ENROLL_STATE:
                return Security.CERT_ENROLL_STATE;
            case CERT_ENROLL_ERROR_MSG:
                return Security.CERT_ENROLL_ERROR_MSG;
            case CERTIFICATE:
                return Security.NODE_CERTIFICATE;
            case TRUST_CERT_INSTALL_STATE:
                return Security.TRUSTED_CERTIFICATE_INSTALLATION_FAILURE;
            case INSTALLED_TRUSTED_CERTIFICATES:
                return Security.INSTALLED_TRUSTED_CERTIFICATES;
            case ISSUER:
                return NodeCertInfo.ISSUER;
            case SERIAL_NUMBER:
                return NodeCertInfo.SERIAL_NUMBER;
            case SUBJECT:
                return NodeCertInfo.SUBJECT;
            case CATEGORY:
                return NodeCertInfo.CATEGORY;
            default:
                logger.error(NscsErrorCodes.INVALID_WANTED_MO, wantedAttribute, node, trustCategory);
                return errString;
            }
        } else {
            logger.error("Invalid trustCategory type {} for node {}", trustCategory, node);
            return errString;
        }
    }

    /**
     * 
     * @param bTrustCertInstallState
     * @return
     */
    private String normalizeTrustedCertInstallState(final String bTrustCertInstallState) {

        return bTrustCertInstallState.equalsIgnoreCase("true") ? IpSecCertEnrollStateValue.ERROR.toString() : IpSecCertEnrollStateValue.IDLE.toString();
    }

    /**
     *
     * @param certEnrollState
     * @return
     */
    private String normalizeEnrollState(String certEnrollState) {

        if (certEnrollState.equals(CertEnrollStateValue.PREPARING_REQUEST.toString()) || certEnrollState.equals(CertEnrollStateValue.POLLING.toString())
                || certEnrollState.equals(CertEnrollStateValue.NEW_CREDS_AWAIT_CONF.toString())) {
            certEnrollState = IpSecCertEnrollStateValue.ONGOING.toString();
        }
        return certEnrollState;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ericsson.nms.security.nscs.data.moget.MOGetService#getSecurityLevel( com.ericsson.nms.security.nscs.data.nodereference. NormalizableNodeReference, java.lang.String)
     */
    @Override
    public String getSecurityLevel(final NormalizableNodeReference nodeRef, final String syncstatus) {

        final NodeReference normNodeRef = nodeRef.getNormalizableRef();
        String securityLevel = NscsNameMultipleValueResponseBuilder.UNKNOWN;

        if (normNodeRef != null) {

            if (!syncstatus.equals(NscsNameMultipleValueResponseBuilder.UNSYNCHRONIZED)) {

                final CmResponse cmresponse = reader.getMOAttribute(normNodeRef, Model.ME_CONTEXT.managedElement.systemFunctions.security.type(),
                        Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace(), Security.OPERATIONAL_SECURITY_LEVEL);

                if (cmresponse == null || cmresponse.getCmObjects() == null || cmresponse.getCmObjects().isEmpty()) {
                    logger.error("No cmObjects returned from the DPS");

                } else {

                    final CmObject cmObject = cmresponse.getCmObjects().iterator().next();

                    if (cmObject == null || cmObject.getAttributes() == null) {
                        logger.error("No cmObjects returned from the DPS for fdn [{}]", normNodeRef.getFdn());
                    } else {
                        securityLevel = (String) cmObject.getAttributes().get(Security.OPERATIONAL_SECURITY_LEVEL);
                    }

                    if (securityLevel == null) {
                        logger.error("Got NULL SecurityLevel for fdn [{}]", normNodeRef.getFdn());
                    }
                }
            }

        } else {
            logger.error("Got NULL NormalizedNodeReference for fdn [{}]", nodeRef.getFdn());
        }

        return securityLevel;
    }

    @Override
    public String getIpsecConfig(final NormalizableNodeReference normNode, final String syncstatus) {

        String result = NscsNameMultipleValueResponseBuilder.UNKNOWN;

        final NodeReference nodeRef = normNode.getNormalizableRef();

        if (nodeRef != null) {

            final NormalizableNodeReference normNodeRef = reader.getNormalizableNodeReference(nodeRef);

            // if(ipsecvalidator.isNodeSynchronized(normNodeRef)){
            if (!NscsNameMultipleValueResponseBuilder.UNSYNCHRONIZED.equals(syncstatus)) {

                if (ipsecvalidator.isNodeHasIpSecMO(normNodeRef)) {

                    final String featureState = ipSecStatusUtility.getIpSecFeatureState(normNodeRef);
                    ipSecStatusUtility.setConfigurationRequirement(true);

                    if (!ipSecStatusUtility.isOMActivated(normNodeRef, featureState)) {
                        result = DISABLED;
                    } else {
                        final boolean isIpSecTrafficActivated = ipSecStatusUtility.isTrafficActivated(normNodeRef, featureState);
                        logger.debug("Traffic is activated for node [{}] : [{}]", normNodeRef.getFdn(), isIpSecTrafficActivated);
                        result = ipSecStatusUtility.getConfigurationInfo().getOmConfigurationType();
                        if (result.isEmpty()) {
                            result = DISABLED;
                            logger.debug("Data is empty. Setting IPSEC config to [{}] for node [{}]", result, normNodeRef.getFdn());
                        }
                    }
                } else {
                    logger.debug("Node has not IpSec MO, fdn [{}] ", normNode.getFdn());
                }
            } else {
                logger.debug("Node not is sync, fdn [{}] ", normNode.getFdn());
            }
        } else {
            logger.debug("Null NormalizableRef for fdn [{}]", normNode.getFdn());
        }
        logger.debug("IpSec Config value for node [{}] : [{}]", normNode.getFdn(), result);

        return result;
    }

    @Override
    public MoActionState getMoActionState(final String moFdn, final MoActionWithoutParameter action) {
        final String inputParams = "FDN [" + moFdn + "] actionWithoutParam [" + action.getAction() + "]";
        logger.error("get MoActionState for {}: NOT YET IMPLEMENTED FOR CPP NODES!", inputParams);
        return null;
    }

    @Override
    public MoActionState getMoActionState(final String moFdn, final MoActionWithParameter action) {
        final String inputParams = "FDN [" + moFdn + "] actionWithParam [" + action.getAction() + "]";
        logger.error("get MoActionState for {}: NOT YET IMPLEMENTED FOR CPP NODES!", inputParams);
        return null;
    }

    /*
     * To get certRevStatusCheck MO Attribute Value from Security MO on Node
     */
    @Override
    public String getCrlCheckStatus(final NormalizableNodeReference normNode, final String certType) {
        logger.info("Start of CppMOGetServiceImpl::getCrlCheckStatus method: normNodeFdn[{}]", normNode.getFdn());

        final Mo rootMo = nscsCapabilityModelService.getMirrorRootMo(normNode);
        final String securityFdn = nscsCppNodeUtility.getSecurityFdn(normNode.getFdn(), rootMo);
        final MoObject MoObject = reader.getMoObjectByFdn(securityFdn);
        final String cRLCheckStatus = MoObject.getAttribute(Security.CERT_REV_STATUS_CHECK).toString();
        logger.info("cRLCheckStatus in getCRLCheckstatus method: [{}]", cRLCheckStatus);

        logger.info("End of CppMOGetServiceImpl::getCrlCheckStatus method ");

        return cRLCheckStatus;
    }

    /*
     * To validate Security MO Existance for CPP Node
     */
    @Override
    public boolean validateNodeForCrlCheckMO(final NormalizableNodeReference normNode, final String certType) throws SecurityMODoesNotExistException {

        logger.info("Start of CppMOGetServiceImpl::validateNodeForCrlCheckMO method: normNodeFdn[{}],certType[{}]", normNode.getFdn(), certType);

        if (!nodeValidatorUtility.isSecurityMOExists(normNode)) {
            logger.error("Node [{}] doesn't have Security MO.", normNode.getFdn());
            throw new SecurityMODoesNotExistException();
        }

        return true;
    }

    @Override
    public List<NtpServer> listNtpServerDetails(final NormalizableNodeReference normNode) {

        logger.info("Start of CppMOGetServiceImpl::listNtpKeyIds method: normNodeFdn[{}]", normNode.getFdn());

        final String mirrorRootFdn = normNode.getFdn();
        String timeSettingFdn = null;
        CmResponse ntpServerResponse = null;
        final Map<String, Object> keyIdAttributes = new HashMap<>();
        final Mo mo = nscsCapabilityModelService.getMirrorRootMo(normNode);
        List<NtpServer> ntpServersList = new ArrayList<>();
        final Mo ntpTimeSettingMo = ((CppManagedElement) mo).systemFunctions.timeSetting;
        final Mo ntpServerMo = ((CppManagedElement) mo).systemFunctions.timeSetting.ntpServer;

        final String[] requestedAttrsInstalledNtpKeyIds = { ModelDefinition.TimeSetting.INSTALLED_NTP_KEY_IDS };
        final String[] requestedAttrsNtpServer = { ModelDefinition.NtpServer.SERVICE_STATUS, ModelDefinition.NtpServer.NTP_KEY_ID, ModelDefinition.NtpServer.USER_LABEL,
                ModelDefinition.NtpServer.NTP_SERVER_ID, ModelDefinition.NtpServer.SERVER_ADDRESS };

        if (nodeValidatorUtility.isMoAttributeExists(normNode, ntpTimeSettingMo, NtpConstants.INSTALLED_NTP_KEY_IDS)) {
            timeSettingFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, ntpTimeSettingMo, keyIdAttributes, requestedAttrsInstalledNtpKeyIds);
        }
        if ((timeSettingFdn != null && !timeSettingFdn.isEmpty()) && nodeValidatorUtility.isMoAttributeExists(normNode, ntpServerMo, NtpConstants.SERVICE_STATUS)) {
            ntpServerResponse = readerService.getMos(mirrorRootFdn, ntpServerMo.type(), ntpServerMo.namespace(), requestedAttrsNtpServer);
        }
        List<String> installedNtpKeyIds = getKeyIdsList(keyIdAttributes.get(ModelDefinition.TimeSetting.INSTALLED_NTP_KEY_IDS));
        logger.info("installedNtpKeyIds..............: [{}]", installedNtpKeyIds);

        if ((ntpServerResponse != null && ntpServerResponse.getCmObjects() != null) && (!ntpServerResponse.getCmObjects().isEmpty())) {
            for (final CmObject ntpServerCmObj : ntpServerResponse.getCmObjects()) {
                final String ntpKeyId = String.valueOf(ntpServerCmObj.getAttributes().get(ModelDefinition.NtpServer.NTP_KEY_ID));
                ntpServersList.add(buildNtpServer(ntpKeyId, ntpServerCmObj));
            }
        }

        if (installedNtpKeyIds != null && !installedNtpKeyIds.isEmpty()) {
            logger.info("Build list of key ID's with no associated Ntpserver Mo.....ntpServersList: [{}], installedNtpKeyIds: [{}]",ntpServersList, installedNtpKeyIds);
            buildKeyIdList(installedNtpKeyIds, ntpServersList);
        } else {
            CmObject ntpServerCmObj = null;
            ntpServersList.add(buildNtpServer(NtpConstants.NA, ntpServerCmObj));
        }

        logger.debug("list of NtpServer KeyIds: [{}]", ntpServersList);

        return ntpServersList;
    }

    private void buildKeyIdList(final List<String> installedNtpKeyIds, final List<NtpServer> ntpServersList){
        for (String installedNtpKeyId : installedNtpKeyIds) {
            CmObject ntpServerCmObj = null;
            if (installedNtpKeyId.equalsIgnoreCase("") && ntpServersList.isEmpty()) {
                installedNtpKeyId = NtpConstants.NA;
                ntpServersList.add(buildNtpServer(installedNtpKeyId, ntpServerCmObj));
            } else {
                if (!isAssociatedNTPServerFound(ntpServersList, installedNtpKeyId) && ! installedNtpKeyId.equalsIgnoreCase("")) {
                    ntpServersList.add(buildNtpServer(installedNtpKeyId, ntpServerCmObj));
                }
            }
        }
    }

    private NtpServer buildNtpServer(final String ntpKeyId, final CmObject ntpServerCmObj) {
        NtpServer ntpServer = new NtpServer();
        final String serviceStatus = (ntpServerCmObj != null && ntpServerCmObj.getAttributes() != null) ? (String) ntpServerCmObj.getAttributes().get(ModelDefinition.NtpServer.SERVICE_STATUS) : NtpConstants.NA;
        final String ntpServerId = (ntpServerCmObj != null && ntpServerCmObj.getAttributes() != null) ? (String) ntpServerCmObj.getAttributes().get(ModelDefinition.NtpServer.NTP_SERVER_ID) : NtpConstants.NA;
        final String userLabel = (ntpServerCmObj != null && ntpServerCmObj.getAttributes() != null) ? (String) ntpServerCmObj.getAttributes().get(ModelDefinition.NtpServer.USER_LABEL) : NtpConstants.NA;
        final String serverAddress = (ntpServerCmObj != null && ntpServerCmObj.getAttributes() != null) ? (String) ntpServerCmObj.getAttributes().get(ModelDefinition.NtpServer.SERVER_ADDRESS) : NtpConstants.NA;
        ntpServer.setKeyId(ntpKeyId);
        ntpServer.setUserLabel(userLabel);
        ntpServer.setNtpServerId(ntpServerId);
        ntpServer.setServiceStatus(serviceStatus);
        ntpServer.setServerAddress(serverAddress);
        return ntpServer;
    }

    private boolean isAssociatedNTPServerFound(final List<NtpServer> ntpServersList, final String installedNtpKeyId){
        boolean isFound = false;
        if(ntpServersList != null && ! ntpServersList.isEmpty() && ! installedNtpKeyId.equalsIgnoreCase("")){
            for (final NtpServer ntpServer : ntpServersList) {
                if (ntpServer.getKeyId().equals(installedNtpKeyId)) {
                    isFound = true;
                    break;
                }
            }
        }
      return isFound;
    }

    private List<String> getKeyIdsList(final Object obj) {
        if (obj != null) {
            return (nscsNodeUtility.convertStringToList(obj.toString()));
        }
        return new ArrayList<>();
    }

    @Override
    public boolean validateNodeForNtp(NormalizableNodeReference nodeRef) {

        logger.info("Start of CppMOGetServiceImpl::validateNodeForNtp method: normNodeFdn[{}]", nodeRef.getFdn());
        final String targetCategory = nodeRef.getTargetCategory();
        final String targetType = nodeRef.getNeType();
        final String targetModelIdentity = nodeRef.getOssModelIdentity();
        final String NAMESPACE = ModelDefinition.CPP_MED_NS;
        final String TIMESETTING_MO = "TimeSetting";
        final String MO_ATTRIBUTE = TimeSetting.INSTALLED_NTP_KEY_IDS;
        boolean valid = false;
        valid = nscsModelServiceImpl.isNtpOperationSupported(targetCategory, targetType, targetModelIdentity, NAMESPACE, TIMESETTING_MO, MO_ATTRIBUTE);
        if (!valid) {
            logger.error("Node [{}] doesn't have installedNtpKeyIds attribute under TimeSetting MO.", nodeRef.getFdn());
            throw new NtpOperationNotSupportedException(NtpConstants.INSTALLED_NTP_KEYIDS_DOESNOT_EXISTS);
        }
        return valid;
    }

    @Override
    public String getNodeSupportedFormatOfKeyAlgorithm(NodeReference nodeRef, String keySize) {
        return NscsPkiUtils.convertAlgorithmNamesToNodeSupportedFormat(
                NscsPkiUtils.convertKeyLengthToAlgorithmKeys(KeyLength.getKeySizeFromValue(keySize)).name());
    }
}
