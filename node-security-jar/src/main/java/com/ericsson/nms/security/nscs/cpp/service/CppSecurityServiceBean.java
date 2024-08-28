package com.ericsson.nms.security.nscs.cpp.service;

import static com.ericsson.nms.security.nscs.data.Model.NETWORK_ELEMENT;

import java.io.IOException;
import java.math.BigInteger;
import java.net.StandardProtocolFamily;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.TrustCategoryType;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeTypeException;
import com.ericsson.nms.security.nscs.api.exception.InvalidVersionException;
import com.ericsson.nms.security.nscs.api.exception.IscfServiceException;
import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelInfo;
import com.ericsson.nms.security.nscs.api.pki.EnrollmentPartialInfos;
import com.ericsson.nms.security.nscs.api.pki.NodeEntityCategory;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiCertificateManager;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.api.pki.exception.NscsPkiCertificateManagerException;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.EnrollmentRequestInfo;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.SmrsUtils;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl;
import com.ericsson.nms.security.nscs.cpp.model.SmrsAccountInfo;
import com.ericsson.nms.security.nscs.cpp.model.TrustStoreInfo;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.moget.MOGetServiceFactory;
import com.ericsson.nms.security.nscs.data.moget.param.CertStateInfo;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.enrollmentinfo.utility.EnrollmentInfoConstants;
import com.ericsson.nms.security.nscs.handler.command.utility.PasswordHelper;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.nms.security.nscs.pki.NscsPkiEntitiesManagerJar;
import com.ericsson.nms.security.nscs.pki.NscsPkiUtils;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.util.EnrollingInformation;
import com.ericsson.nms.security.nscs.util.NscsCbpOiTrustedEntityInfo;
import com.ericsson.nms.security.nscs.util.NscsTrustedEntityInfo;
import com.ericsson.nms.security.nscs.utilities.CredentialsHelper;
import com.ericsson.nms.security.nscs.utilities.NscsCbpOiNodeUtility;
import com.ericsson.nms.security.nscs.utilities.NscsCommonValidator;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.smrs.api.exception.SmrsDirectoryException;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.recording.EventLevel;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.Certificate;
import com.ericsson.oss.itpf.security.pki.manager.model.EnrollmentInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.TDPSUrlInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.TrustedEntityInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.CAEntity;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.itpf.smrs.SmrsAccount;
import com.ericsson.oss.itpf.smrs.SmrsAddressRequest;
import com.ericsson.oss.itpf.smrs.SmrsService;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoAttributeException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

/**
 * Implementation of the CppSecurityService interface.
 *
 * @see com.ericsson.nms.security.nscs.cpp.service.CppSecurityService
 * @author egbobcs
 *
 */
@Stateless
public class CppSecurityServiceBean implements CppSecurityService {

    public final static String CPP_KS_AND_EM_SUPPORTED_MINIMUM_VERSION = "5.0.0";
    public final static String CPP_CERT_AUTH_DN_SUPPORTED_MINIMUM_VERSION = "5.1.200";
    public final static String CPP_KEY_2048_SUPPORTED_MINIMUM_VERSION = "E.1.50";
    public final static String ERBS_NODE_TYPE = "ERBS";
    private static final String DNS_NAME_REGEX = "^(?=.{1,255}$)(?!.*\\.{2})(((?:(?!-)[\\x21-\\x7E]{1,63}(?<!-|_)(?:\\.|$)){1,})*)$";

    private static final String DIGIT_ONLY_REGEX = "^(?=^.{1,255}$)(([0-9]{1,63}(?:\\.|$)){1,})(?!\\.)$";
    private static final String SAN_WILDCARD = "?";
    public static final String CERT_PATH_PATTERN = "/%s";

    @EServiceRef
    private SmrsService smrsService;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsCMWriterService writer;

    @EJB
    private NscsPkiEntitiesManagerIF nscsPkiManager;

    @Inject
    private NscsModelServiceImpl nscsModelServiceImpl;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    @EJB
    private NscsPkiCertificateManager nscsPkiCertificateManager;

    @Inject
    private MOGetServiceFactory moGetServiceFactory;

    @Inject
    private SmrsUtils smrsUtils;

    @Inject
    private Logger log;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    @Inject
    private NscsCbpOiNodeUtility nscsCbpOiNodeUtility;

    @Inject
    private PasswordHelper passwordHelper;

    @Inject
    private SystemRecorder systemRecorder;

    EnrollmentRequestInfo enrollmentRequestInfo = new EnrollmentRequestInfo();

    final Map<Object, Object> enrollmentEntityInfo = new HashMap<>();
    /**
     * @param enrollInfo
     *            : parameters needed to perform node enrollment
     * @return enrollment data retrieved from PKI
     */
    private ScepEnrollmentInfo generateNodeEnrollmentInfo(final EnrollingInformation enrollInfo) throws CppSecurityServiceException {

        validateEnrollingInformation(enrollInfo);

        final String nodeFdn = enrollInfo.getNodeFdn();
        final String entityProfileName = enrollInfo.getEntityProfileName();
        final BaseSubjectAltNameDataType subjectAltName = enrollInfo.getSubjectAltName();
        final SubjectAltNameFormat subjectAltNameFormat = enrollInfo.getSubjectAltNameFormat();
        EnrollmentMode enrollmentMode = enrollInfo.getEnrollmentMode();
        final NodeModelInformation modelInfo = enrollInfo.getModelInfo();
        final AlgorithmKeys algorithmKeys = enrollInfo.getKeySize();
        final NodeEntityCategory entityCategory = enrollInfo.getCategory();
        final String commonName = enrollInfo.getCommonName();
        final StandardProtocolFamily ipVersion = enrollInfo.getIpVersion();
        final Integer otpCount = enrollInfo.getOtpCount();
        final Integer otpValidityPeriodInMinutes = enrollInfo.getOtpValidityPeriodInMinutes();

        log.info("generateNodeEnrollmentInfo : node [{}] commonName[{}] entityProfileName [{}] ipVersion[{}]",
                nodeFdn, commonName, entityProfileName, ipVersion);

        // Check on Network Element Security
        final NodeReference node = new NodeRef(nodeFdn);
        final NormalizableNodeReference normRef = readerService.getNormalizedNodeReference(node);

        if (normRef == null) {
            final String errorMsg = "NormalizedNodeReference MO is null for node [" + nodeFdn + "]";
            log.error(errorMsg);
            throw new CppSecurityServiceException(errorMsg);
        }

        enrollmentMode = configureNESAndGetEnrollmentMode(enrollmentMode, nodeFdn, normRef);
        // Assign the entity name prefix from the normalized node name
        final String normNodeName = normRef.getName();

        log.info(
                "Invoking NscsPkiEntitiesManager : nodeName[{}], commonName[{}], entityProfileName[{}], subjectAltName[{}], algorithmKeys[{}], entityCategory[{}], modelInfo[{}], otpCount[{}], otpValidity[{}]",
                normNodeName, commonName, entityProfileName, subjectAltName, ((algorithmKeys == null) ? "null" : algorithmKeys.name()),
                ((entityCategory == null) ? "null" : entityCategory.name()), ((modelInfo == null) ? "null" : modelInfo.toString()), otpCount,
                otpValidityPeriodInMinutes);

        // Invoke PKI
        EnrollmentPartialInfos enrollmentPartialInfo;
        try {
            log.debug("Getting EnrollmentEntityInfo");
            enrollmentEntityInfo.put(EnrollmentInfoConstants.NODE_NAME, normNodeName);
            enrollmentEntityInfo.put(EnrollmentInfoConstants.COMMON_NAME, commonName);
            enrollmentEntityInfo.put(EnrollmentInfoConstants.ENROLLMENT_MODE, enrollmentMode);
            enrollmentEntityInfo.put(EnrollmentInfoConstants.ENTITY_PROFILE_NAME, entityProfileName);
            enrollmentEntityInfo.put(EnrollmentInfoConstants.SUBJECT_ALT_NAME, subjectAltName);
            enrollmentEntityInfo.put(EnrollmentInfoConstants.SUBJECT_ALT_NAME_FORMAT, subjectAltNameFormat);
            enrollmentEntityInfo.put(EnrollmentInfoConstants.ALGORITHM_KEYS, algorithmKeys);
            enrollmentEntityInfo.put(EnrollmentInfoConstants.ENTITY_CATEGORY, entityCategory);
            enrollmentEntityInfo.put(EnrollmentInfoConstants.MODEL_INFO, modelInfo);
            enrollmentEntityInfo.put(EnrollmentInfoConstants.OTP_COUNT, otpCount);
            enrollmentEntityInfo.put(EnrollmentInfoConstants.OTP_VALIDITY_PERIOD_IN_MINUTES, otpValidityPeriodInMinutes);
            enrollmentPartialInfo = nscsPkiManager.getEnrollmentEntityInfo(enrollmentEntityInfo);

            log.debug("Got EnrollmentEntityInfo: KeySize[{}], Entity[{}], EnrollmentInfo[{}]", enrollmentPartialInfo.getKeySize(),
                    NscsPkiEntitiesManagerJar.getEntityLog(enrollmentPartialInfo.getEndEntity()),
                    NscsPkiEntitiesManagerJar.getEnrollmentInfoLog(enrollmentPartialInfo.getEnrollmentServerInfo()));
        } catch (final NscsPkiEntitiesManagerException e) {
            final String errorMsg = String.format("Exception when invoking NscsPkiEntitiesManager getEnrollmentEntityInfo(), error: %s",
                    e.getMessage());
            log.error(errorMsg);
            throw new CppSecurityServiceException(e.getMessage());
        }

        // Retrieve enrollment server info
        final EnrollmentInfo enrollmentInfo = enrollmentPartialInfo.getEnrollmentServerInfo();
        log.debug("Fetched Enrollment info : [{}] ", NscsPkiEntitiesManagerJar.getEnrollmentInfoLog(enrollmentInfo));

        // Prepare scep enrollment
        final Entity ee = enrollmentPartialInfo.getEndEntity();
        log.info("Node is prepared for SCEP enrollment in PKI, fdn : [{}], Entity : [{}]", nodeFdn, NscsPkiEntitiesManagerJar.getEntityLog(ee));

        final KeyLength keyLength = NscsPkiUtils.convertAlgorithmKeysToKeyLength(enrollmentPartialInfo.getKeySize());

        ScepEnrollmentInfo scepInfo = null;

        try {
            log.info("init ScepEnrollmentInfoImpl");

            log.debug("Getting Digest Algorithm");
            final DigestAlgorithm digestAlgorithm = nscsCapabilityModelService.getDefaultDigestAlgorithm(modelInfo);
            log.debug(
                    "Digest Algorithm " + "- getDigestValuePrefix [{}], " + "- getEnmDigestAlgorithmValue [{}], "
                            + "- getStandardDigestAlgorithmValue [{}]",
                    digestAlgorithm.getDigestValuePrefix(), digestAlgorithm.getEnmDigestAlgorithmValue(),
                    digestAlgorithm.getStandardDigestAlgorithmValue());

            log.debug("Getting OTP from PKI");
            final String otp = ee.getEntityInfo().getOTP();

            final String caName = ee.getEntityInfo().getIssuer().getName();
            final Map<String, String> enrollmentCAAuthorizationModes = nscsCapabilityModelService.getEnrollmentCAAuthorizationModes(normRef);

            final X509Certificate pkiRootX509 = nscsPkiManager.findPkiRootCACertificate(caName);
            final X509Certificate caCertX509 = (enrollmentInfo == null) ? null : enrollmentInfo.getCaCertificate();
            final String enrollmentServerAddress = getEnrollmentServerAddress(enrollmentInfo, normRef, ipVersion);
            if (nscsCapabilityModelService.isEnrollmentRootCAFingerPrintSupported(enrollmentCAAuthorizationModes,
                    NodeEntityCategory.toCertType(entityCategory).toString())) {
                log.info("Setting rootCaCert if enrollment RootCA finger print is supported");
                scepInfo = new ScepEnrollmentInfoImpl(ee, enrollmentServerAddress, caCertX509,
                        digestAlgorithm, 0, otp, keyLength.toString(), enrollmentMode, pkiRootX509, null);
            } else if (nscsCapabilityModelService.isEnrollmentRootCACertificateSupported(enrollmentCAAuthorizationModes,
                    NodeEntityCategory.toCertType(entityCategory).toString())) {
                final String pkiRootCaName = nscsPkiManager.findPkiRootCAName(caName);
                log.info("Setting rootCaName if enrollment RootCA Certificate is supported : [{}]", pkiRootCaName);
                scepInfo = new ScepEnrollmentInfoImpl(ee, enrollmentServerAddress, caCertX509,
                        digestAlgorithm, 0, otp, keyLength.toString(), enrollmentMode, pkiRootX509, pkiRootCaName);

            } else if (nscsCapabilityModelService.isEnrollmentCACertificateSupported(enrollmentCAAuthorizationModes,
                    NodeEntityCategory.toCertType(entityCategory).toString())) {
                log.info("Setting caName if enrollment CA Certificate is supported : [{}]", caName);
                scepInfo = new ScepEnrollmentInfoImpl(ee, enrollmentServerAddress, caCertX509,
                        digestAlgorithm, 0, otp, keyLength.toString(), enrollmentMode, pkiRootX509, caName);

            }
        } catch (NoSuchAlgorithmException | CertificateEncodingException ex) {
            final String errorMsg = String.format("Before updating EnrollmentInfo: exception[%s] msg[%s]", ex.getClass(), ex.getMessage());
            log.error("{} : ex[{}]", errorMsg, ex);
            throw new CppSecurityServiceException(errorMsg);
        } catch (final IllegalArgumentException e) {
            log.error("Null ipaddress in the node connectivity information", e);
            throw new CppSecurityServiceException(e.getMessage());
        } catch (final Exception ex) {
            final String errorMsg = String.format("Before updating EnrollmentInfo: GENERIC exception[%s] msg[%s]", ex.getClass(), ex.getMessage());
            log.error("{} : ex[{}]", errorMsg, ex);
            throw new CppSecurityServiceException(errorMsg);
        }
        try {
            log.info("Updating EnrollmentInfo for fdn : [{}]", nodeFdn);
            updateEnrollmentInfo(enrollmentMode, modelInfo, scepInfo);
        } catch (final InvalidVersionException e) {
            final String errorMsg = String.format("InvalidVersionException for fdn %s, modelInfo %s error: %s", nodeFdn, modelInfo, e.getMessage());
            log.error(errorMsg);
            throw new CppSecurityServiceException(errorMsg);
        } catch (final InvalidNodeTypeException e) {
            final String errorMsg = String.format("InvalidNodeTypeException for fdn %s modelInfo %s error: %s", nodeFdn, modelInfo, e.getMessage());
            log.error(errorMsg);
            throw new CppSecurityServiceException(errorMsg);
        }
        log.info("ScepEnrollmentInfo is constructed [{}]", scepInfo);

        return scepInfo;

    }

    /**
     * Check if given node has a valid IPv6 address.
     *
     * @param nodeRef
     *            the node reference
     * @return true if valid IPv6 address, false otherwise.
     */
    private boolean hasNodeIPv6Address(final NormalizableNodeReference nodeRef) {
        boolean hasIPv6Address = false;
        try {
            hasIPv6Address = nscsNodeUtility.hasNodeIPv6Address(nodeRef);
        } catch (final IllegalArgumentException e) {
            log.error("Cannot determine if node [" + nodeRef.getFdn() + "] has IPv6 ipAddress");
            throw new IllegalArgumentException("Null ipaddress in the node connectivity information");
        }
        return hasIPv6Address;
    }

    private String getEnrollmentServerAddress(final EnrollmentInfo ei, final NormalizableNodeReference nodeRef, 
                                              final StandardProtocolFamily ipVersion) {
        String enrollmentServerAddress = null;
        try {
        if (ei != null) {
            if (ipVersion != null) {
                enrollmentServerAddress = (StandardProtocolFamily.INET6.equals(ipVersion) ? ei.getIpv6EnrollmentURL() : ei.getIpv4EnrollmentURL());
            }
            else if (hasNodeIPv6Address(nodeRef)) {
                enrollmentServerAddress = ei.getIpv6EnrollmentURL();
            } else {
                enrollmentServerAddress = ei.getIpv4EnrollmentURL();
            }
        }
        } catch (final IllegalArgumentException e) {
            log.info("Null ipaddress in the node connectivity information");
            throw new IllegalArgumentException(e);
        }
        return enrollmentServerAddress;
    }

    /**
     * @param normRef  normRef
     * @param oldEnrollmentMode oldEnrollmentMode
     * @param newEnrollmentMode newEnrollmentMode
     * @throws CppSecurityServiceException CppSecurityServiceException
     */
    private void setEnrollmentMode(final NormalizableNodeReference normRef, final EnrollmentMode oldEnrollmentMode,
                                   final EnrollmentMode newEnrollmentMode)
            throws CppSecurityServiceException {
        log.info("Enrollment Mode for node [{}], oldEnrollmentMode [{}], newEnrollmentMode [{}]", normRef.getFdn(), oldEnrollmentMode,
                newEnrollmentMode);
        boolean performSet = false;
        if (oldEnrollmentMode == null) {
            log.debug("oldEnrollmentMode is null");
            performSet = true;
        } else if (!oldEnrollmentMode.name().equals(newEnrollmentMode.name())) {
            log.debug("oldEnrollmentMode [{}], newEnrollmentMode [{}]", oldEnrollmentMode.name(), newEnrollmentMode.name());
            performSet = true;
        }
        if (performSet) {
            log.info("Setting new Enrollment Mode for node [{}], newEnrollmentMode [{}]", normRef.getFdn(), newEnrollmentMode.name());

            final String normalizedRefName = normRef.getNormalizedRef().getName();
            final String networkElementSecurityFdn = Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.withNames(normalizedRefName).fdn();

            final NscsCMWriterService.WriterSpecificationBuilder specification = writer.withSpecification();
            specification.setNotNullAttribute(NetworkElementSecurity.ENROLLMENT_MODE, newEnrollmentMode.toString());
            specification.setFdn(networkElementSecurityFdn);
            log.debug("Updating NetworkElementSecurity for normalizedRefName [{}], networkElementSecurityFdn[{}]", normalizedRefName,
                    networkElementSecurityFdn);
            try {
                specification.updateMO();
            } catch (final Exception e) {
                final String errorMsg = "Could not update MO attribute for node " + normRef.getFdn() + ": " + e.getMessage();
                log.error(errorMsg);
                throw new CppSecurityServiceException(errorMsg);
            }
            log.info("Updated NetworkElementSecurity for [{}]", networkElementSecurityFdn);
        } else {
            log.info("No need to update the Enrollment Mode for node [{}]", normRef.getFdn());
        }
    }

    /**
     * Create NetworkElementSecurity MO for the given node.
     *
     * @param nodeRef
     *            the node reference.
     */
    private void createNetworkElementSecurityMO(final NormalizableNodeReference nodeRef) {
        log.info("create NetworkElementSecurityMO : starts for {}", nodeRef);
        final String normalizedRefName = nodeRef.getNormalizedRef().getName();
        final String networkElementSecurityType = NETWORK_ELEMENT.securityFunction.networkElementSecurity.type();
        final NscsModelInfo nscsModelInfo = nscsModelServiceImpl.getLatestVersionOfNormalizedModel(networkElementSecurityType);
        final String networkElementSecurityNamespace = nscsModelInfo.getNamespace();
        final String networkElementSecurityVersion = nscsModelInfo.getVersion();
        log.info("Creating {} MO for node [{}] with ns [{}] and version [{}]",
                networkElementSecurityType, nodeRef, networkElementSecurityNamespace, networkElementSecurityVersion);
        final NscsCMWriterService.WriterSpecificationBuilder specification = writer.withSpecification(networkElementSecurityType,
                networkElementSecurityNamespace, networkElementSecurityVersion);
        specification.setParent(NETWORK_ELEMENT.securityFunction.withNames(normalizedRefName).fdn());
        /**
         * Starting from 18.02 some attributes (according to node type) are mandatory, so they are initialized to a dummy value
         */
        final List<String> expectedParams = nscsCapabilityModelService.getExpectedCredentialsParams(nodeRef);
        for (final String param : expectedParams) {
            final String attributeDbValue = CredentialsHelper.isPasswordParam(param)
                    ? passwordHelper.encryptEncode(CredentialsHelper.UNDEFINED_CREDENTIALS) : CredentialsHelper.UNDEFINED_CREDENTIALS;
            specification.setAttribute(CredentialsHelper.toAttribute(param), attributeDbValue);
        }
        specification.createMIBRoot();
        log.info("create NetworkElementSecurityMO : all done for {}", nodeRef);
    }

    private void validateEnrollingInformation(final EnrollingInformation enrollInfo) throws CppSecurityServiceException {
        String errorMsg = null, nodeFdn = null;
        final String noSpaceMsg = " must not contain blank characters";
        final Pattern alphaNumPattern = Pattern.compile("[a-zA-Z0-9]");
        if (enrollInfo == null) {
            errorMsg = "Null enrollment info";
        }
        if (errorMsg == null) {
            nodeFdn = enrollInfo.getNodeFdn();
            if (nodeFdn == null) {
                errorMsg = "Node FDN cannot be null";
            } else {
                if (nodeFdn.isEmpty()) {
                    errorMsg = "Node FDN cannot be empty";
                } else {
                    if (nodeFdn.contains(" ")) {
                        errorMsg = "Node FDN" + noSpaceMsg;
                    } else {
                        final Matcher m = alphaNumPattern.matcher(nodeFdn);
                        if (!m.find()) {
                            errorMsg = "Node FDN must contain an alphanumeric character";
                        }
                    }
                }
            }
        }
        if (errorMsg == null) {
            final NodeModelInformation modelInfo = enrollInfo.getModelInfo();
            if (modelInfo == null) {
                log.info("Building NodeModelInformation for node [{}]", nodeFdn);
                enrollInfo.setModelInfo(getNodeModelInformation(nodeFdn));
            }
            final String nodeType = enrollInfo.getModelInfo().getNodeType();
            if (nodeType == null) {
                errorMsg = "Node type cannot be null";
            } else {
                if (nodeType.isEmpty()) {
                    errorMsg = "Node type cannot be empty";
                } else {
                    final Matcher m = alphaNumPattern.matcher(nodeType);
                    if (nodeType.contains(" ")) {
                        errorMsg = "Node Type" + noSpaceMsg;
                    } else {
                        if (!m.find()) {
                            errorMsg = "Node Type must contain an alphanumeric character";
                        }
                    }
                }
            }
        }
        if (errorMsg == null) {
            final EnrollmentMode enrollmentMode = enrollInfo.getEnrollmentMode();
            if (enrollmentMode != null) {
                // Check if requested EnrollmentMode is supported by node
                if ((nscsCapabilityModelService.isEnrollmentModeSupported(enrollInfo.getModelInfo(), enrollmentMode)) != true) {
                    errorMsg = "Enrollment Mode not supported by node model: " + enrollmentMode;
                }
            }
        }
        if ((errorMsg == null) && (enrollInfo.getSubjectAltNameFormat() != null)
                && (!SubjectAltNameFormat.NONE.equals(enrollInfo.getSubjectAltNameFormat()))) {
            boolean isValidSan = true;
            final BaseSubjectAltNameDataType subjectAltNameValue = enrollInfo.getSubjectAltName();
            if ((subjectAltNameValue != null) && subjectAltNameValue.getClass().equals(SubjectAltNameStringType.class)) {
                final String subjectAltNameStringVal = ((SubjectAltNameStringType) subjectAltNameValue).getValue();
                if (!SAN_WILDCARD.equals(subjectAltNameStringVal)) {
                    if (SubjectAltNameFormat.FQDN.equals(enrollInfo.getSubjectAltNameFormat())) {
                        Pattern sanPattern = Pattern.compile(DNS_NAME_REGEX);
                        final Matcher dnsMatch = sanPattern.matcher(subjectAltNameStringVal);
                        sanPattern = Pattern.compile(DIGIT_ONLY_REGEX);
                        final Matcher digitMatch = sanPattern.matcher(subjectAltNameStringVal);
                        isValidSan = dnsMatch.matches() || digitMatch.matches();
                    }
                    if (SubjectAltNameFormat.IPV4.equals(enrollInfo.getSubjectAltNameFormat())) {
                        isValidSan = NscsCommonValidator.getInstance().isValidIPv4Address(subjectAltNameStringVal);
                    }
                    if (SubjectAltNameFormat.IPV6.equals(enrollInfo.getSubjectAltNameFormat())) {
                        isValidSan = NscsCommonValidator.getInstance().isValidIPv6Address(subjectAltNameStringVal);
                    }
                }
            }
            if (!isValidSan) {
                errorMsg = "Subject Alt Name has wrong format";
            }
        }
        if (errorMsg != null) {
            log.error(errorMsg);
            throw new CppSecurityServiceException(errorMsg);
        }
    }

    @Override
    public void revokeCertificateByIssuerName(final String issuerName, final String serialNumber, final String reason)
            throws CppSecurityServiceException {
        log.info("Revoking certificate for issuer [{}], serial number [{}], reason [{}] ", issuerName, serialNumber, reason);

        try {
            nscsPkiManager.revokeCertificateByIssuerName(issuerName, serialNumber, reason);
        } catch (final NscsPkiEntitiesManagerException e) {
            log.error("Exception while revoking certificate for issuer [{}], serial number [{}], reason [{}], exception [{}]", issuerName,
                    serialNumber, reason, e.getMessage());
            throw new CppSecurityServiceException(e.getMessage());
        }
    }

    @Override
    public String getTrustDistributionPointUrl(final CAEntity caEntity, final NormalizableNodeReference nodeRef) throws CppSecurityServiceException {
        log.info("Getting Trust Distribution point URL for entity [{}]", caEntity.getCertificateAuthority().getName());
        String tdpsUrl = null;
        TDPSUrlInfo tdpsUrlInfo = null;
        try {
            tdpsUrlInfo = nscsPkiManager.getTrustDistributionPointUrls(caEntity);
        } catch (final NscsPkiEntitiesManagerException e) {
            log.error("Exception while getting Trust Distribution point URLs for entity [{}]", caEntity.getCertificateAuthority().getName());
            throw new CppSecurityServiceException(e.getMessage());
        }
        if (tdpsUrlInfo != null) {
            if (hasNodeIPv6Address(nodeRef)) {
                tdpsUrl = tdpsUrlInfo.getIpv6Address();
            } else {
                tdpsUrl = tdpsUrlInfo.getIpv4Address();
            }
        }
        log.debug("Got Trust Distribution point URL [{}] by PKI for entity [{}]", tdpsUrl, caEntity.getCertificateAuthority().getName());
        return tdpsUrl;
    }

    @Override
    public String getTrustDistributionPointUrl(final String caName, final String nodeName) throws CppSecurityServiceException {
        log.info("Getting Trust Distribution point URL for caName [{}] nodeName [{}]", caName, nodeName);
        String tdpsUrl = null;
        final NodeReference node = new NodeRef(nodeName);
        final NormalizableNodeReference normRef = readerService.getNormalizedNodeReference(node);
        TrustedEntityInfo pkiTrustedCAInfo = null;
        try {
            pkiTrustedCAInfo = nscsPkiManager.getTrustedCAInfoByName(caName);
            log.debug("TrustedEntityInfo from PKI [{}]", pkiTrustedCAInfo);
            if (pkiTrustedCAInfo != null) {
               if (hasNodeIPv6Address(normRef)) {
                  tdpsUrl = pkiTrustedCAInfo.getIpv6TrustDistributionPointURL();
               } else {
                   tdpsUrl = pkiTrustedCAInfo.getIpv4TrustDistributionPointURL();
               }
            } else {
                log.error("Null TrustEntityInfo from PKI for caName [{}] node [{}]", caName, nodeName);
            }
        } catch (final Exception exception) {
            log.error("Exception occured while getting tdps url :{}", exception.getMessage());
            throw new IscfServiceException(exception.getMessage());
        }
        return tdpsUrl;
      }

    /**
     * Generates OAM enrollment info.
     *
     * This data is needed for the initCertEnrollment MO action and for generating the ISCF file for nodes supporting AP
     *
     * @param nodeFdn
     *            the value of nodeFdn
     * @param commonName
     *            (optional) the value of nodeSerialNumber, Can be null.
     * @param subjectAltName
     *            the value of subjectAltName
     * @param subjectAltNameFormat
     *            the value of subjectAltNameFormat
     * @param enrollmentMode
     *            the value of enrollmentMode
     * @param modelInfo
     *            the value of modelInfo
     * @return the com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo
     */
    @Override
    public ScepEnrollmentInfo generateOamEnrollmentInfo(final String nodeFdn, final String commonName,
            final BaseSubjectAltNameDataType subjectAltName, final SubjectAltNameFormat subjectAltNameFormat, final EnrollmentMode enrollmentMode,
            final NodeModelInformation modelInfo) throws CppSecurityServiceException {
        enrollmentRequestInfo.setCommonName(commonName);
        enrollmentRequestInfo.setEnrollmentMode(enrollmentMode);
        enrollmentRequestInfo.setNodeName(nodeFdn);
        SubjectAltNameParam subjectAltNameParam = null;
        if (subjectAltName != null && subjectAltNameFormat != null) {
            final SubjectAltNameStringType subjectAltNameString = new SubjectAltNameStringType(subjectAltName.toString());
            subjectAltNameParam = new SubjectAltNameParam(subjectAltNameFormat, subjectAltNameString);
        }
        enrollmentRequestInfo.setSubjectAltNameParam(subjectAltNameParam);
        return generateOamEnrollmentInfo(modelInfo, enrollmentRequestInfo);
    }

   /**
     * Generates OAM enrollment info.
     *
     * This data is needed for the initCertEnrollment MO action and for generating the ISCF file for nodes supporting AP
     *
     * @param modelInfo
     *            the value of modelInfo
     * @param enrollmentRequestInfo
     *            The EnrollmentInfo Details.
     * @return the com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo
     */
    @Override
    public ScepEnrollmentInfo generateOamEnrollmentInfo(final NodeModelInformation modelInfo, EnrollmentRequestInfo enrollmentRequestInfo) throws CppSecurityServiceException {
        log.info("Generate OAM Enrollment Info : node[{}]", enrollmentRequestInfo.getNodeName());
        String entityProfileName = null;
        String commonName = null;
        BaseSubjectAltNameDataType subjectAltName = null;
        SubjectAltNameFormat subjectAltNameFormat = null;
        EnrollmentMode enrollmentMode = null;
        AlgorithmKeys keyGenerationAlgorithm = null;
        String nodeFdn = null;
        if (enrollmentRequestInfo.getEntityProfile() == null || enrollmentRequestInfo.getEntityProfile().isEmpty()) {
            // TORF-196726: for AP, always set EP to default value
            entityProfileName = nscsCapabilityModelService.getDefaultEntityProfile(modelInfo, NodeEntityCategory.OAM);
        } else {
            entityProfileName = enrollmentRequestInfo.getEntityProfile();
        }

        if (enrollmentRequestInfo.getKeySize() == null || enrollmentRequestInfo.getKeySize().isEmpty()) {
            // TORF-346790: for AP, always set KeySize to default value
            keyGenerationAlgorithm = nscsCapabilityModelService.getDefaultAlgorithmKeys(modelInfo);
        } else {
            keyGenerationAlgorithm = AlgorithmKeys.valueOf(enrollmentRequestInfo.getKeySize());
        }
        if (enrollmentRequestInfo.getSubjectAltNameParam() != null) {
            subjectAltName = enrollmentRequestInfo.getSubjectAltNameParam().getSubjectAltNameData();
            subjectAltNameFormat = enrollmentRequestInfo.getSubjectAltNameParam().getSubjectAltNameFormat();
        }
        commonName = enrollmentRequestInfo.getCommonName();
        enrollmentMode = enrollmentRequestInfo.getEnrollmentMode();
        nodeFdn = enrollmentRequestInfo.getNodeName();
        final EnrollingInformation enrollInfo = new EnrollingInformation(nodeFdn, entityProfileName, enrollmentMode, keyGenerationAlgorithm,
                NodeEntityCategory.OAM, commonName);
        enrollInfo.setSubjectAltName(subjectAltName);
        enrollInfo.setSubjectAltNameFormat(subjectAltNameFormat);
        enrollInfo.setModelInfo(modelInfo);
        enrollInfo.setIpVersion(enrollmentRequestInfo.getIpVersion());
        enrollInfo.setOtpConfigurationParameters(enrollmentRequestInfo.getOtpConfigurationParameters());
        return generateNodeEnrollmentInfo(enrollInfo);
    }

    /**
     * Generates IPSEC enrollment info.
     *
     * This data is needed for the initCertEnrollment MO action and for generating the ISCF file for nodes supporting AP
     *
     * @param nodeFdn
     *            the value of nodeFdn
     * @param commonName
     *            (optional) the value of nodeSerialNumber, Can be null.
     * @param subjectAltName
     *            the value of subjectAltName
     * @param subjectAltNameFormat
     *            the value of subjectAltNameFormat
     * @param enrollmentMode
     *            the value of enrollmentMode
     * @param modelInfo
     *            the value of modelInfo
     * @return the com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo
     * @throws com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException
     */
    @Override
    public ScepEnrollmentInfo generateIpsecEnrollmentInfo(final String nodeFdn,
                                                          final String commonName,
                                                          final BaseSubjectAltNameDataType subjectAltName, final SubjectAltNameFormat subjectAltNameFormat,
                                                          final EnrollmentMode enrollmentMode,
                                                          final NodeModelInformation modelInfo)
            throws CppSecurityServiceException {
        return generateIpsecEnrollmentInfo(nodeFdn, commonName, subjectAltName, subjectAltNameFormat, enrollmentMode, null, modelInfo);
    }

    /**
     * Generates IPSEC enrollment info with required IP version for enrollment server URI.
     *
     * This data is needed for the initCertEnrollment MO action and for generating the ISCF file for nodes supporting AP
     *
     * @param nodeFdn
     *            the value of nodeFdn
     * @param commonName
     *            (optional) the value of nodeSerialNumber, Can be null.
     * @param subjectAltName
     *            the value of subjectAltName
     * @param subjectAltNameFormat
     *            the value of subjectAltNameFormat
     * @param enrollmentMode
     *            the value of enrollmentMode
     * @param ipVersion
     *            The IP version (INET / INET6) to select the suitable enrollment server URI. If null, IP version is retrieved from  ConnectivityInformation
     * @param modelInfo
     *            the value of modelInfo
     * @return the com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo
     */
    @Override
    public ScepEnrollmentInfo generateIpsecEnrollmentInfo(final String nodeFdn,
                                                          final String commonName,
                                                          final BaseSubjectAltNameDataType subjectAltName, final SubjectAltNameFormat subjectAltNameFormat,
                                                          final EnrollmentMode enrollmentMode,
                                                          final StandardProtocolFamily ipVersion,
                                                          final NodeModelInformation modelInfo)
            throws CppSecurityServiceException {

        log.info("Generate IPSec Enrollment Info : node[{}] serialNumber[{}] subAltName[{}] enrollmentMode[{}] ipVersion[{}] modelInfo[{}]", nodeFdn, commonName,
                subjectAltName, (enrollmentMode != null ? enrollmentMode.name() : enrollmentMode), (ipVersion != null ? ipVersion.toString() : "null"),
                (modelInfo != null ? modelInfo.toString() : modelInfo));

        // TORF-196726: for AP always set EP to default value
        final String entityProfileName = nscsCapabilityModelService.getDefaultEntityProfile(modelInfo, NodeEntityCategory.IPSEC);
        // TORF-346790: for AP, always set KeySize to default value
        final AlgorithmKeys keyGenerationAlgorithm =  nscsCapabilityModelService.getDefaultAlgorithmKeys(modelInfo);

        final EnrollingInformation enrollInfo = new EnrollingInformation(nodeFdn, entityProfileName, enrollmentMode, keyGenerationAlgorithm,
                NodeEntityCategory.IPSEC, commonName);
        enrollInfo.setSubjectAltName(subjectAltName);
        enrollInfo.setSubjectAltNameFormat(subjectAltNameFormat);
        enrollInfo.setModelInfo(modelInfo);
        enrollInfo.setIpVersion(ipVersion);
        return generateNodeEnrollmentInfo(enrollInfo);
    }

    /**
     * Generates SL2 enrollment info for WFS.
     */
    @Override
    public ScepEnrollmentInfo generateOamEnrollmentInfo(final String nodeFdn) throws CppSecurityServiceException {

        log.info("Generate SL2 Enrollment info for WFS : node[{}] ", nodeFdn);

        // Get Enrollment Mode and Node Model Info from MOs
        final NodeModelInformation modelInfo = getNodeModelInformation(nodeFdn);
        final EnrollmentMode enrollmentMode = getEnrollmentMode(nodeFdn);

        final EnrollingInformation enrollInfo = new EnrollingInformation(nodeFdn, "", enrollmentMode, null, NodeEntityCategory.OAM, null);
        enrollInfo.setSubjectAltName(null);
        enrollInfo.setSubjectAltNameFormat(SubjectAltNameFormat.NONE);
        enrollInfo.setModelInfo(modelInfo);
        return generateNodeEnrollmentInfo(enrollInfo);
    }

    /**
     * Generates IPSEC enrollment info for WFS.
     */
    @Override
    public ScepEnrollmentInfo generateIpsecEnrollmentInfo(final String nodeFdn, final BaseSubjectAltNameDataType subjectAltName,
                                                          final SubjectAltNameFormat subjectAltNameFormat)
            throws CppSecurityServiceException {

        log.info("Generate IPSec Enrollment info for WFS : node[{}] subAltName[{}]", nodeFdn, subjectAltName);

        // Get Enrollment Mode and Node Model Info from MOs
        final EnrollmentMode enrollmentMode = getEnrollmentMode(nodeFdn);
        final NodeModelInformation modelInfo = getNodeModelInformation(nodeFdn);

        final EnrollingInformation enrollInfo = new EnrollingInformation(nodeFdn, "", enrollmentMode, null, NodeEntityCategory.IPSEC, null);
        enrollInfo.setSubjectAltName(subjectAltName);
        enrollInfo.setSubjectAltNameFormat(subjectAltNameFormat);
        enrollInfo.setModelInfo(modelInfo);
        return generateNodeEnrollmentInfo(enrollInfo);
    }

    /**
     * Generates Enrollment info for WFS.
     *
     * @throws CppSecurityServiceException CppSecurityServiceException
     */
    @Override
    public ScepEnrollmentInfo generateEnrollmentInfo(final EnrollingInformation enrollInfo) throws CppSecurityServiceException {
        log.info("Generating Enrollment info : node [{}] entityProfileName [{}] category [{}]", enrollInfo.getNodeFdn(),
                enrollInfo.getEntityProfileName(), enrollInfo.getCategory().name());

        return generateNodeEnrollmentInfo(enrollInfo);

    }

    @Override
    public List<Certificate> getTrustCertificates(final String entityName) throws CppSecurityServiceException {
        List<Certificate> trustedCertificates = new ArrayList<>();
        log.info("Get trust certificates for entity[{}]", entityName);
        try {
            trustedCertificates = nscsPkiManager.getTrustCertificates(entityName);
        } catch (final NscsPkiEntitiesManagerException e) {
            final String errorMessage = String.format("Caught exception[%s] msg[%s] while getting trusted certificates for entity[%s]",
                    e.getClass().getName(), e.getMessage(), entityName);
            log.error(errorMessage);
            throw new CppSecurityServiceException(errorMessage);
        }
        if (trustedCertificates != null) {
            log.info("trustedCertificates found [{}]", trustedCertificates.size());
        } else {
            log.info("trustedCertificates null ");
        }
        return trustedCertificates;
    }

    /**
     * Gets trust store info for the specified node and (optionally) publish certificates on SMRS
     *
     * @param category category
     * @param nodeRef nodeRef
     * @param publishCertificates
     *            if true publishes certificates to SMRS
     * @return TrustStoreInfo containing set of trust certificates
     * @throws java.net.UnknownHostException UnknownHostException
     */
    @Override
    public TrustStoreInfo getTrustStoreForNode(final TrustedCertCategory category, final NodeReference nodeRef, final boolean publishCertificates)
            throws CppSecurityServiceException, SmrsDirectoryException, CertificateException, UnknownHostException {

        log.info("getTrustStoreForNode, category[{}] , node[{}]", (category != null ? category.name() : category),
                (nodeRef != null) ? nodeRef.getFdn() : nodeRef);
        if (nodeRef == null) {
            throw new CppSecurityServiceException("getTrustStoreForNode : null node reference");
        }
        final NodeModelInformation nodeModelInfo = getNodeModelInformation(nodeRef.getFdn());
        final DigestAlgorithm fingerprintAlgo = nscsCapabilityModelService.getDefaultDigestAlgorithm(nodeModelInfo);
        final Set<CertSpec> toBeInstalledCerts = getTrustedCAsForNode(category, nodeRef);
        final String nodeType = (nodeModelInfo != null) ? nodeModelInfo.getNodeType() : null;
        return getTrustStoreInfo(category, toBeInstalledCerts, nodeRef.getName(), nodeType, fingerprintAlgo, publishCertificates);
    }

    @Override
    public TrustStoreInfo getTrustStoreForNode(final TrustedCertCategory category, final NodeReference nodeRef, final boolean publishCertificates,
                                               final TrustCategoryType trustCategory)
            throws CppSecurityServiceException, CertificateException, UnknownHostException {

        log.info("getTrustStoreForNode, category[{}] , node[{}]", (category != null ? category.name() : category),
                (nodeRef != null) ? nodeRef.getFdn() : nodeRef);
        if (nodeRef == null) {
            throw new CppSecurityServiceException("getTrustStoreForNode : null node reference");
        }
        final NodeModelInformation nodeModelInfo = getNodeModelInformation(nodeRef.getFdn());
        if (nodeModelInfo == null) {
            throw new CppSecurityServiceException("getTrustStoreForNode : null node model info");
        }
        final DigestAlgorithm fingerprintAlgo = nscsCapabilityModelService.getDefaultDigestAlgorithm(nodeModelInfo);
        final Set<CertSpec> toBeInstalledCerts = getTrustedCAsForNode(category, nodeRef);
        log.info("The certs to be installed are : {}", toBeInstalledCerts);
        final CertStateInfo trustCertificateInfo = moGetServiceFactory.getTrustCertificateStateInfo(nodeRef, trustCategory.name());
        if (trustCertificateInfo.isNotAvailable()) {

            throw new MissingMoAttributeException();
        } else {
            final List<CertDetails> installedTrustedCerts = new ArrayList<CertDetails>();
            if (TrustCategoryType.OAM.equals(trustCategory) || TrustCategoryType.LAAD.equals(trustCategory)) {
                for (final CertDetails certDetail : trustCertificateInfo.getCertificates()) {
                    if (category.toString().equals(certDetail.getCategory().toString())) {
                        installedTrustedCerts.add(certDetail);
                    }
                }
            } else {
                installedTrustedCerts.addAll(trustCertificateInfo.getCertificates());
            }
            final Iterator<CertSpec> iterator = toBeInstalledCerts.iterator();
            while (iterator.hasNext()) {
                final CertSpec certSpec = iterator.next();
                log.info("The cert is : {}", certSpec);
                for (final CertDetails certDetail : installedTrustedCerts) {
                    if (certDetail.isEqual(certSpec)) {
                        iterator.remove();
                    }
                }
            }

            return getTrustStoreInfo(category, toBeInstalledCerts, nodeRef.getName(), nodeModelInfo.getNodeType(), fingerprintAlgo,
                    publishCertificates);
        }
    }

    @Override
    public Set<X509Certificate> getTrustCertificatesForNode(final TrustedCertCategory category, final NodeReference nodeRef)
            throws CppSecurityServiceException {
        log.info("getTrustCertificatesForNode, category[{}] , node[{}]", (category != null ? category.name() : category),
                (nodeRef != null) ? nodeRef.getFdn() : nodeRef);
        if (nodeRef == null) {
            throw new CppSecurityServiceException("getTrustCertificatesForNode : null node reference");
        }
        final Set<X509Certificate> trustCertificates = new HashSet<>();
        final Set<CertSpec> certSpecs = getTrustedCAsForNode(category, nodeRef);
        for (final CertSpec certSpec : certSpecs) {
            trustCertificates.add(certSpec.getCertHolder());
        }
        return trustCertificates;
    }

    /**
     * Gets trust store info for the specified node and (optionally) publish certificates on SMRS
     *
     * @param category category
     * @param caName caName
     * @param nodeRef nodeRef
     * @param publishCertificates
     *            if true publishes certificates to SMRS
     * @return TrustStoreInfo containing set of trust certificates
     * @throws java.net.UnknownHostException UnknownHostException
     */
    @Override
    public TrustStoreInfo getTrustStoreForNodeWithCA(final TrustedCertCategory category, final String caName, final NodeReference nodeRef,
                                                     final boolean publishCertificates)
            throws CppSecurityServiceException, SmrsDirectoryException, CertificateException, UnknownHostException {

        log.info("getTrustStoreForNodeWithCA, category is [{}] , node is [{}]", (category != null ? category.name() : null),
                (nodeRef != null) ? nodeRef.getFdn() : null);
        if (nodeRef == null) {
            throw new CppSecurityServiceException("getTrustStoreForNode : null node reference");
        }

        final NodeModelInformation nodeModelInfo = getNodeModelInformation(nodeRef.getFdn());
        if (nodeModelInfo == null) {
            throw new CppSecurityServiceException("getTrustStoreForNodeWithCA : null node model info");
        }

        final DigestAlgorithm fingerprintAlgo = nscsCapabilityModelService.getDefaultDigestAlgorithm(nodeModelInfo);

        final Set<CertSpec> certSpecs = new HashSet<>();
        List<X509Certificate> certs = null;
        try {
            certs = nscsPkiManager.getCATrusts(caName);
        } catch (final NscsPkiEntitiesManagerException e) {
            log.error("getCATrusts for " + caName + "returned exception " + e);
            throw new CppSecurityServiceException("getTrustStoreForNodeWithCA: " + e);
        }
        if ((certs != null) && !certs.isEmpty()) {
            for (final X509Certificate cert : certs) {
                final CertSpec certSpec = new CertSpec(cert);
                certSpecs.add(certSpec);
            }
        }
        return getTrustStoreInfo(category, certSpecs, nodeRef.getName(), nodeModelInfo.getNodeType(), fingerprintAlgo, publishCertificates);
    }

    /**
     * Gets the trust store info for AutoProvisioning and publish trust certificates
     *
     * @param entityName entityName
     * @param category category
     * @param modelInfo modelInfo
     * @throws java.net.UnknownHostException UnknownHostException
     */
    @Override
    public TrustStoreInfo getTrustStoreForAP(final TrustedCertCategory category, final String entityName, final NodeModelInformation modelInfo)
            throws CppSecurityServiceException, SmrsDirectoryException, CertificateException, UnknownHostException {

        log.info("getTrustStoreForAP for category[{}], entity[{}], NodeType[{}]", category, entityName, modelInfo.getNodeType());

        final Entity ent = retrieveEntityIfExists(entityName);
        if (ent == null) {
            final String err = "Entity with entity name " + entityName + " should exists";
            log.error(err);
            throw new CppSecurityServiceException(err);
        }
        final DigestAlgorithm fingerprintAlgo = nscsCapabilityModelService.getDefaultDigestAlgorithm(modelInfo);
        Set<CertSpec> trustCertificates = null;
        try {
            trustCertificates = nscsPkiManager.getTrustCertificatesFromProfile(ent.getEntityProfile().getName());
        } catch (final NscsPkiEntitiesManagerException ex) {
            final String errorMsg = "Error in get TrustedCertificates " + ex.getMessage();
            log.error(errorMsg);
            throw new CppSecurityServiceException(errorMsg);
        }
        return getTrustStoreInfo(category, trustCertificates, getNodeNameFromEntity(entityName), modelInfo.getNodeType(), fingerprintAlgo, true);
    }

    /**
     * Gets the Digest Algorithm used to compute certificate fingerprint
     *
     * @param nodeFdn
     *            FDN of node
     * @return Digest Algorithm value (SHA1 SHA256 SHA512 MD5)
     */
    @Override
    public DigestAlgorithm getCertificateFingerprintAlgorithmForNode(final String nodeFdn) {
        final NodeModelInformation nodeModelInfo = getNodeModelInformation(nodeFdn);
        return nscsCapabilityModelService.getDefaultDigestAlgorithm(nodeModelInfo);
    }

    @Override
    public void cancelSCEPEnrollment(final String fdn) throws CppSecurityServiceException {

        log.info("Cancelling SCEP Enrollment info for node[{}]", fdn);
        String errorMsg = null;
        String entityName = null;

        if ((fdn != null) && !fdn.isEmpty()) {
            // Check if parameter is already an entity name
            if (!fdn.contains("=")
                    && (fdn.endsWith("-" + NodeEntityCategory.OAM.toString()) || fdn.endsWith("-" + NodeEntityCategory.IPSEC.toString()))) {
                try {
                    nscsPkiCertificateManager.revokeEntityCertificates(fdn);
                    nscsPkiManager.deleteEntity(fdn);
                } catch (final NscsPkiCertificateManagerException nscsPkiCertificateManagerException) {
                    errorMsg = String.format("cancelSCEPEnrollment() : error in revokeEntityCertificates()");
                } catch (final NscsPkiEntitiesManagerException ex) {
                    errorMsg = String.format("cancelSCEPEnrollment() : error in deleteEntity()");
                }
            } else {
                // Parameter is node name or FDN
                try {
                    entityName = NscsPkiUtils.getEntityNameFromFdn(NodeEntityCategory.OAM, fdn);
                    nscsPkiCertificateManager.revokeEntityCertificates(entityName);
                    nscsPkiManager.deleteEntity(entityName);
                } catch (final NscsPkiCertificateManagerException nscsPkiCertificateManagerException) {
                    errorMsg = String.format("cancelSCEPEnrollment() : error in revokeEntityCertificates() for OAM enrollment");
                } catch (final NscsPkiEntitiesManagerException ex) {
                    errorMsg = String.format("cancelSCEPEnrollment() : error in OAM deleteEntity()");
                }
                try {
                    entityName = NscsPkiUtils.getEntityNameFromFdn(NodeEntityCategory.IPSEC, fdn);
                    nscsPkiCertificateManager.revokeEntityCertificates(entityName);
                    nscsPkiManager.deleteEntity(entityName);
                } catch (final NscsPkiCertificateManagerException nscsPkiCertificateManagerException) {
                    errorMsg = String.format("cancelSCEPEnrollment() : error in revokeEntityCertificates() for IPSEC enrollment");
                } catch (final NscsPkiEntitiesManagerException ex) {
                    errorMsg = String.format("cancelSCEPEnrollment() : error in IPSEC deleteEntity()");
                }
            }
            final String targetType = readerService.getTargetType(fdn);
            if (targetType != null) {
                final String nodeName = nscsNodeUtility.getNodeNameFromFdn(fdn);
                cancelSmrsAccountForNode(nodeName, targetType);
            }
        }
        if (errorMsg != null) {
            log.error(errorMsg);
            throw new CppSecurityServiceException(errorMsg);
        }
    }

    @Override
    public void cancelSmrsAccountForNode(final String nodeName, final String targetType) {
        log.info("Cancel SMRS Account for node [{}] of type [{}]",nodeName , targetType);

        if ((nodeName != null) && (targetType != null)) {
            final SmrsAccount account = smrsService.getNodeSpecificAccount(nscsPkiManager.getSmrsAccountTypeForNscs(), targetType, nodeName);
            smrsService.deleteSmrsAccount(account);
        }
    }

    @Override
    public List<SmrsAccountInfo> getSmrsAccountInfoForCertificate(final String neName, final String neType)
            throws SmrsDirectoryException, CertificateException {

        log.info("Get SMRS Account Info for Certificate");

        final List<SmrsAccountInfo> accounts = new ArrayList<>();
        // Temporary
        final SmrsAccount account = smrsService.getNodeSpecificAccount(nscsPkiManager.getSmrsAccountTypeForNscs(), neType, neName);
        log.info("Certificate SMRS account is registered : [{}] ", account);
        if (account.getUserName().startsWith("mm-cert")) {
            final StringBuilder logParam = new StringBuilder("UserName=");
            final String encodedHiddenWord = Base64.getEncoder().encodeToString(account.getPassword().getBytes(StandardCharsets.UTF_8));
            logParam.append(account.getUserName()).append("  HiddenWord=").append(encodedHiddenWord);
            systemRecorder.recordEvent("[TORF480878] SMRS getNodeSpecificAccount ", EventLevel.COARSE, "Parameter Values : " + logParam.toString(), "node-security", "");
        }
        final SmrsAddressRequest smrsAddReq = new SmrsAddressRequest();
        smrsAddReq.setAccountType(nscsPkiManager.getSmrsAccountTypeForNscs());
        smrsAddReq.setNeName(neName);
        smrsAddReq.setNeType(neType);
        log.debug("Invoking getFileServerAddress() with [{}]", smrsAddReq);
        final String addresses = smrsService.getFileServerAddress(smrsAddReq);
        log.debug("getFileServerAddress() return [{}]", addresses);

        final char[] smrsPassword = account.getPassword().toCharArray();

        final NodeReference node = new NodeRef(neName);
        final NormalizableNodeReference normRef = readerService.getNormalizableNodeReference(node);

        final boolean evoc = nscsCapabilityModelService.isEvoc(normRef);

        if (addresses != null) {
            for (final String address : addresses.split(",")) {
                SmrsAccountInfo accountInfo = null;
                if (evoc) {
               accountInfo = new SmrsAccountInfo(account.getUserName(), smrsPassword, address, account.getHomeDirectory(),
                            String.format(CERT_PATH_PATTERN, account.getRelativePath()));
                } else {
                    accountInfo = new SmrsAccountInfo(account.getUserName(), smrsPassword, address, account.getHomeDirectory(),
                            account.getRelativePath());
                }
                accounts.add(accountInfo);
            }
        }
        return accounts;
    }

    @Override
    public SmrsAccountInfo getSmrsAccountInfoForNode(final String nodeName, final String neType) {

        log.info("Get SMRS Account Info for node[{}]", nodeName);

        final SmrsAddressRequest smrsAddReq = new SmrsAddressRequest();
        smrsAddReq.setAccountType(nscsPkiManager.getSmrsAccountTypeForNscs());
        smrsAddReq.setNeType(neType);
        smrsAddReq.setNeName(nodeName);
        final String address = getSmrsIpAddress(smrsAddReq);

        log.info(" address[{}]", address);
        final SmrsAccount smrsAccount = smrsService.getNodeSpecificAccount(nscsPkiManager.getSmrsAccountTypeForNscs(), neType, nodeName);
        // TODO Remove this workaround once SMRS M2M user for node is available
        // by default
        //ensureValidUserExists(smrsAccount);
        log.debug("SmrsAccount1 [{}] -> [{}]", smrsAccount.getHomeDirectory(), smrsAccount.getAccountType());
        final char[] smrsPassword = smrsAccount.getPassword().toCharArray();
        return new SmrsAccountInfo(smrsAccount.getUserName(), smrsPassword, address, smrsAccount.getHomeDirectory(), smrsAccount.getRelativePath());

    }

    @Override
    public String getEntityProfileName(final TrustedCertCategory category, final NodeReference nodeRef) throws CppSecurityServiceException {

        final String inputParams = "category [" + category.name() + "] node [" + (nodeRef == null ? nodeRef : nodeRef.getFdn()) + "]";

        log.debug("get EntityProfileName : starts for {}", inputParams);

        if (nodeRef == null) {
            final String errorMsg = "Null node reference for {}" + inputParams;
            log.error("get EntityProfileName : {}", errorMsg);
            throw new CppSecurityServiceException(errorMsg);
        }
        NodeEntityCategory nodeCategory;
        try {
            nodeCategory = NscsPkiUtils.convertTrustCategoryToNodeCategory(category);
        } catch (final IllegalArgumentException e) {
            final String errorMsg = NscsLogger.stringifyException(e) + " during conversion to Node Entity Category of " + inputParams;
            log.error("get EntityProfileName : {}", errorMsg);
            throw new CppSecurityServiceException(errorMsg);
        }

        String entityProfileName = null;

        /*
         * Get entity profile from PKI Entity Manager
         */
        final String entityName = NscsPkiUtils.getEntityNameFromFdn(nodeCategory, nodeRef.getName());
        log.debug("get EntityProfileName : entity name [{}]", entityName);
        final Entity entity = retrieveEntityIfExists(entityName);
        if (entity != null) {
            /*
             * Entity for node already existent in PKI
             */
            log.debug("get EntityProfileName : from PKI : already existent entity [{}]", entityName);
            entityProfileName = entity.getEntityProfile().getName();
        } else {
            /*
             * Entity for node not yet existent in PKI: get default entity profile from capability model
             */
            log.debug("get EntityProfileName : from PKI : not yet existent entity [{}]", entityName);
            final NodeModelInformation nodeModelInfo = readerService.getNodeModelInformation(nodeRef.getFdn());
            log.debug("get EntityProfileName : reading default from capability model : nodeModelInfo [{}]", nodeModelInfo);
            entityProfileName = nscsCapabilityModelService.getDefaultEntityProfile(nodeModelInfo, nodeCategory);
        }
        log.debug("get EntityProfileName : returns entity profile name [{}]", entityProfileName);
        return entityProfileName;
    }

    /**
     * @param entityName entityName
     * @return the entity
     * @throws CppSecurityServiceException CppSecurityServiceException
     */
    private Entity retrieveEntityIfExists(final String entityName) throws CppSecurityServiceException {

        Entity ret = null;
        try {
            if (!nscsPkiManager.isEntityNameAvailable(entityName, EntityType.ENTITY)) {
                ret = nscsPkiManager.getPkiEntity(entityName);
                log.debug("Entity [{}] found", entityName);
            } else {
                log.debug("Entity [{}] not found", entityName);
            }
        } catch (final NscsPkiEntitiesManagerException ex) {
            final String err = "Exception in getPkiEntity: " + ex;
            log.error(err);
            throw new CppSecurityServiceException(err);
        }
        return ret;
    }

    /**
     * SMRS address from SMRS
     */
    Set<String> fetchSmrsAddresses(final String neType, final String neName) {
        final SmrsAddressRequest smrsAddReq = new SmrsAddressRequest();
        smrsAddReq.setAccountType(nscsPkiManager.getSmrsAccountTypeForNscs());
        smrsAddReq.setNeType(neType);
        final String addresses = smrsService.getFileServerAddress(smrsAddReq);
        log.debug("SMRS address is fetched from SMRS [{}]", addresses);
        final Set<String> setAddresses = new HashSet<String>(Arrays.asList(addresses.split(",")));
        return setAddresses;
    }

    /**

    /**
     * @param enrollmentMode enrollmentMode
     * @param modelInfo modelInfo
     * @param scepInfo scepInfo
     */
    private void updateEnrollmentInfo(final EnrollmentMode enrollmentMode, final NodeModelInformation modelInfo, final ScepEnrollmentInfo scepInfo)
            throws InvalidVersionException, InvalidNodeTypeException {

        log.debug("EnrollmentMode: [{}], NodeModelInformation: [{}], ScepEnrollmentInfo: [{}]", enrollmentMode, modelInfo, scepInfo);

        if (scepInfo != null) {
            if (modelInfo != null) {
                scepInfo.setKSandEMSupported(nscsCapabilityModelService.isKSandEMSupported(modelInfo));
                scepInfo.setCertificateAuthorityDnSupported(nscsCapabilityModelService.isCertificateAuthorityDnSupported(modelInfo));
                log.warn("Please set the key size based on ScepEnrollmentInfo!!!");

                String enrollmentServerUrl = scepInfo.getServerURL();
                if (nscsCapabilityModelService.isSynchronousEnrollmentSupported(modelInfo)
                        && (!(enrollmentServerUrl.endsWith(ENROLLMENT_URL_ECIM_SUFFIX)))) {
                    log.debug("update EnrollmentInfo: changing enrollmentServerUrl[{}]", enrollmentServerUrl);
                    enrollmentServerUrl += ("/" + ENROLLMENT_URL_ECIM_SUFFIX);
                    scepInfo.setServerURL(enrollmentServerUrl);
                    log.debug("update EnrollmentInfo: changed enrollmentServerUrl[{}]", scepInfo.getServerURL());
                }
            } else {
                log.info("updateEnrollmentInfo: NULL modelInfo!!!");
            }

            if (enrollmentMode != null) {
                // Valid if the value to be written in ISCF XML file is
                // node-independent!
                scepInfo.setEnrollmentProtocol(enrollmentMode.getEnrollmentModeValue());
            }
        }
    }

    private EnrollmentMode getEnrollmentMode(final String nodeFdn) {

        log.info("Getting Enrollment Mode for [{}]", nodeFdn);

        return nscsNodeUtility.getEnrollmentMode(nodeFdn);
    }

    /**
     *
     * @param nodeFdn node Fdn
     * @return the EnrollmentMode
     */
    private EnrollmentMode getEnrollmentModeFromNES(final String nodeFdn) {

        log.info("Getting Enrollment Mode from NES for [{}]", nodeFdn);

        return nscsNodeUtility.getEnrollmentModeFromNES(nodeFdn);
    }

    private NodeModelInformation getNodeModelInformation(final String nodeFdn) {

        log.info("Getting Node Model Information for [{}] from NscsCMReaderService", nodeFdn);

        final NodeModelInformation nodeModelInfo = readerService.getNodeModelInformation(nodeFdn);

        if (nodeModelInfo != null) {
            log.info("Got Node Model Information for [{}], nodeModelInfo [{}]", nodeFdn, nodeModelInfo.toString());
        }

        return nodeModelInfo;
    }

    private boolean isNetworkElementSecurityMOPresent(final NormalizableNodeReference nodeRef) {

        log.debug("Checking if NetworkElementSecurity MO exists for [{}]", nodeRef.getFdn());

        boolean isCreated = false;

        final String normalizedRefName = nodeRef.getNormalizedRef().getName();

        if (readerService.exists(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.withNames(normalizedRefName).fdn())) {
            isCreated = true;
        }
        log.debug("isNetworkElementSecurityMOPresent returns [{}] for node [{}], name [{}]", isCreated, nodeRef, normalizedRefName);

        return isCreated;
    }

    private Set<CertSpec> getTrustedCAsForNode(final TrustedCertCategory trustCategory, final NodeReference nodeRef) throws CppSecurityServiceException {

        log.info("getTrustedCAsForNode, category[{}] , node[{}]", (trustCategory != null ? trustCategory.name() : trustCategory),(nodeRef != null) ? nodeRef.getFdn() : nodeRef);
        if (nodeRef == null) {
            throw new CppSecurityServiceException("getTrustStoreForNode : null node reference");
        }
        NodeEntityCategory nodeCategory;
        try {
            nodeCategory = NscsPkiUtils.convertTrustCategoryToNodeCategory(trustCategory);
        } catch (final IllegalArgumentException exc) {
            log.error("TrustedCertCategory not supported: [{}]", trustCategory);
            throw new CppSecurityServiceException("Trusted certificate category : " + trustCategory + " not supported");
        }
        final NodeModelInformation nodeModelInfo = getNodeModelInformation(nodeRef.getFdn());
        final String entityProfile;

        if (nodeCategory.toString().equals(TrustCategoryType.LAAD.toString())) {
            entityProfile = nscsCapabilityModelService.getDefaultEntityProfile(nodeModelInfo, nodeCategory);
        } else {
            final String entityName = NscsPkiUtils.getEntityNameFromFdn(nodeCategory, nodeRef.getName());
            final Entity ent = retrieveEntityIfExists(entityName);

            if (ent != null) {
                entityProfile = ent.getEntityProfile().getName();
            } else {
                // Get default entity profile from Capability Model
                entityProfile = nscsCapabilityModelService.getDefaultEntityProfile(nodeModelInfo, nodeCategory);
            }
        }

        Set<CertSpec> trustedCertificates = null;
        try {
            trustedCertificates = nscsPkiManager.getTrustCertificatesFromProfile(entityProfile);
        } catch (final NscsPkiEntitiesManagerException ex) {
            final String errorMsg = "Error in get TrustedCertificates " + ex.getMessage();
            log.error(errorMsg);
            throw new CppSecurityServiceException(errorMsg);
        }
        return trustedCertificates;
    }

    /**
     * Gets the trust store info given category and list of trust certificates; optionally publishes certificates on SMRS
     *
     * This data is needed for installTrustedCertificates MO action
     *
     * @param category category
     * @param certSpecs certSpecs
     * @param neName neMane
     * @param neType neType
     * @param fingerprintAlgorithm  fingerprintAlgorithm
     * @param publishTrustsOnSmrs publishTrustsOnSmrs
     * @throws java.net.UnknownHostException UnknownHostException
     */
    private TrustStoreInfo getTrustStoreInfo(final TrustedCertCategory category, final Set<CertSpec> certSpecs, final String neName,
                                             final String neType, final DigestAlgorithm fingerprintAlgorithm, final boolean publishTrustsOnSmrs)
                  throws SmrsDirectoryException, CertificateException {

        log.info("getTrustStoreInfo() :  node[{}] , type[{}] , publish[{}], trusts[{}]", neName, neType, publishTrustsOnSmrs, certSpecs.size());

        List<SmrsAccountInfo> addressInfo = null;
        if (publishTrustsOnSmrs) {
            // Get SMRS accounts
            addressInfo = this.getSmrsAccountInfoForCertificate(neName, neType);
            log.info("getSmrsAccountInfoForCertificate() returns  [{}] SmrsAccountInfo", addressInfo.size());
            try {
                publishCertificatesSmrs(certSpecs, addressInfo);
            } catch (final CertificateException ex) {
                log.debug("CertificateException in publishCertificatesSmrs");
            }
        }

        final TrustStoreInfo trustStoreInfo = new TrustStoreInfo(category, certSpecs, addressInfo, fingerprintAlgorithm);
        log.debug("TrustStoreInfo is constructed to call MO action: [{}]", trustStoreInfo.toString());
        return trustStoreInfo;
    }

    private void publishCertificatesSmrs(final Set<CertSpec> certSpecs, final List<SmrsAccountInfo> accountList) throws CertificateException {
        log.info("publishCertificatesSmrs()");

        // Publish certificates to each directory
        log.debug("Publishing [{}] certificates to [{}] SMRS locations", certSpecs.size(), accountList.size());
        for (final CertSpec certSpec : certSpecs) {
            try {
                uploadCertificateToSmrs(certSpec, accountList);
            } catch (final IOException ex) {
                log.error("Failed to publish certificate [" + certSpec.getCertHolder().getSerialNumber() + "]", ex);
                throw new CertificateException();
            }
        }
        log.debug("Total number of [{}] certificates are published.", certSpecs.size());
    }

    private void uploadCertificateToSmrs(final CertSpec certSpec, final List<SmrsAccountInfo> smrsAccountList)
            throws CertificateException, IOException {
        log.info("uploadCertificateToSmrs([" + certSpec.getCertHolder().getSerialNumber() + "])");
        final String certFileName = certSpec.getFileName();
        final byte[] certBytes = certSpec.getCertHolder().getEncoded();

        for (final SmrsAccountInfo smrsAccount : smrsAccountList) {
            final String settingUri = smrsUtils.uploadFileToSmrs(smrsAccount, certFileName, certBytes);
            log.info("Uploaded file to SMRS: {}", settingUri);
        }
    }

    private String getNodeNameFromEntity(final String entityName) {
        String nodeName;
        if (entityName.endsWith("-" + NodeEntityCategory.OAM.toString())) {
            nodeName = entityName.substring(0, entityName.indexOf("-" + NodeEntityCategory.OAM.toString(), 0));
        } else {
            if (entityName.endsWith("-" + NodeEntityCategory.IPSEC.toString())) {
                nodeName = entityName.substring(0, entityName.indexOf("-" + NodeEntityCategory.IPSEC.toString(), 0));
            } else {
                nodeName = entityName;
            }
        }
        return nodeName;
    }

    public enum KeyLength {

        RSA1024("0"), RSA2048("1"), RSA3072("2"), RSA4096("3"), ECDSA160("4"), ECDSA224("5"), ECDSA256("6"), ECDSA384("7"), ECDSA512("8"), ECDSA521("9");

        private final String keyLength;

        private KeyLength(final String keyLength) {
            this.keyLength = keyLength;
        }

        /**
         * @param value value
         * @return Get the KeyLength from a String value
         */
        public static KeyLength getKeySizeFromValue(final String value) {
            KeyLength retValue = KeyLength.RSA2048;

            for (final KeyLength kl : KeyLength.values()) {
                if (kl.toString().equals(value)) {
                    retValue = kl;
                    break;
                }
            }
            return retValue;
        }

        @Override
        public String toString() {
            return this.keyLength;
        }
    }

    @Override
    public EnrollmentMode enrollmentModeUpdate(final String nodeFdn, final String certificateType, final Entity entity) {

        // Get Enrollment Mode and Node Model Info from MOs
        final EnrollmentMode enrollmentMode = getEnrollmentMode(nodeFdn);

        log.info("enrollmentModeUpdate : node[{}] certType[{}]", nodeFdn, certificateType);

        return enrollmentModeUpdate(nodeFdn, enrollmentMode, certificateType, entity);
    }

    @Override
    public EnrollmentMode enrollmentModeUpdate(final String nodeFdn, final EnrollmentMode requiredEnrollment, final String certificateType,
                                               Entity entity) {

        EnrollmentMode enrollmentMode = requiredEnrollment;

        log.info("enrollmentModeUpdate with enrollmentMode [{}] : node[{}] certType[{}]", enrollmentMode, nodeFdn, certificateType);

        if (EnrollmentMode.CMPv2_VC.equals(enrollmentMode) || EnrollmentMode.CMPv2_INITIAL.equals(enrollmentMode)) {
            try {
                final NodeReference nodeRef = new NodeRef(nodeFdn);
                final NormalizableNodeReference normNode = readerService.getNormalizableNodeReference(nodeRef);

                // retrieve the nodeCertificate on Mirrored model.
                final CertStateInfo certificateInfo = moGetServiceFactory.getCertificateIssueStateInfo(normNode, certificateType);

                if (certificateInfo.isInvalid()) {
                    log.info("No Valid Certificate on Node nodeRef[{}]", nodeRef);
                } else {
                    log.debug("Valid Certificate on Node nodeRef[{}]", nodeRef);

                    boolean isKURfeasible = false;

                    // Check if on PKI there is a valid certificate
                    final String entityName = NscsPkiUtils.getEntityNameFromFdn(NodeEntityCategory.valueOf(certificateType), nodeFdn);
                    if (entity == null) {
                        entity = retrieveEntityIfExists(entityName);
                    }
                    if (entity != null) {
                        final Certificate activeCert = entity.getEntityInfo().getActiveCertificate();
                        if (activeCert != null) {
                            String activeIssuer = null;
                            // PKI always returns serial number in hexadecimal
                            // format!
                            final BigInteger activeCertificateSN = CertDetails
                                    .convertHexadecimalSerialNumberToDecimalFormat(activeCert.getSerialNumber());
                            log.debug("activeCertificate PKI serial number is [{}] : converted [{}]", activeCert.getSerialNumber(),
                                    activeCertificateSN);
                            if (activeCert.getIssuer() == null) {
                                log.warn("Can't find active certificate issuer for entity [{}]", entity);
                            }
                            activeIssuer = activeCert.getIssuer() == null ? "" : activeCert.getIssuer().getSubject().toASN1String();
                            log.debug("activeCertificate issuer: [{}]", activeIssuer);
                            log.debug("activeCertificate issuer name: [{}]", activeCert.getIssuer().getName());

                            final Iterator<CertDetails> itr = certificateInfo.getCertificates().iterator();
                            while (itr.hasNext()) {
                                final CertDetails currentNodeCertDetails = itr.next();

                                log.debug("currentNodeCertDetails serial number is: [{}]", currentNodeCertDetails.getSerial());
                                log.debug("currentNodeCertDetails issuer: [{}]", currentNodeCertDetails.getIssuer());

                                if (CertDetails.matchesDN(activeIssuer, currentNodeCertDetails.getIssuer())
                                        && activeCertificateSN.equals(currentNodeCertDetails.getSerial())) {
                                    isKURfeasible = true;
                                    break;
                                }
                            }

                        }
                    }

                    if (isKURfeasible) {
                        log.info("Generate Enrollment with CMPv2_UPDATE : node[{}] certType[{}] ", nodeFdn, certificateType);
                        enrollmentMode = EnrollmentMode.CMPv2_UPDATE;
                    } else {
                        log.info("isKURfeasible false : node[{}] certType[{}] ", nodeFdn, certificateType);
                    }
                }

            } catch (final NullPointerException | CppSecurityServiceException exception) {
                log.info("Catch Expection: Generate Enrollment with enrollmentMode : node[{}] certType[{}]", enrollmentMode, certificateType);
            }
        }

        log.info("Return enrollmentModeUpdate with enrollmentMode [{}] node[{}] certType[{}]", enrollmentMode, nodeFdn, certificateType);

        return enrollmentMode;
    }

    @Override
    public NscsTrustedEntityInfo getTrustedCAInfoByName(final String caName, final boolean isIPv6Node) throws CppSecurityServiceException {
        final String inputParams = "CA name [" + caName + "] isIPv6 [" + isIPv6Node + "]";
        log.debug("get TrustedCAInfoByName : starts for {}", inputParams);
        if (caName == null) {
            final String errorMsg = "Invalid CA name in : " + inputParams;
            log.error("get TrustedCAInfoByName : {}", errorMsg);
            throw new CppSecurityServiceException(errorMsg);
        }
        TrustedEntityInfo pkiTrustedCAInfo = null;
        try {
            pkiTrustedCAInfo = nscsPkiManager.getTrustedCAInfoByName(caName);
        } catch (final NscsPkiEntitiesManagerException e) {
            final String errorMsg = NscsLogger.stringifyException(e) + " from PKI while getting trusted CA active cert info for [" + caName + "]";
            log.error("get TrustedCAInfoByName : " + errorMsg);
            throw new CppSecurityServiceException(errorMsg);
        }
        final NscsTrustedEntityInfo nscsTrustedCAInfo = new NscsTrustedEntityInfo(pkiTrustedCAInfo, isIPv6Node);

        if(log.isDebugEnabled()) {
            log.debug("get TrustedCAInfoByName : returns {}", nscsTrustedCAInfo.stringify());
        }
        return nscsTrustedCAInfo;
    }

    @Override
    public NscsCbpOiTrustedEntityInfo getCbpOiTrustedCAInfoByName(final String caName)
                                            throws CppSecurityServiceException {
        NscsCbpOiTrustedEntityInfo nscsCbpOiTrustedEntityInfo = null;
        NscsTrustedEntityInfo nscsTrustedCAInfo = getTrustedCAInfoByName(caName, false);
        List<X509Certificate> certs;
        try {
            certs = nscsPkiManager.getCATrusts(caName);
        } catch (final NscsPkiEntitiesManagerException e) {
            log.error("getCATrusts for [{]} returned exception [{}]",caName ,e);
            throw new CppSecurityServiceException("getCbpOiTrustedCAInfoByName: " + e);
        }
        if ((nscsTrustedCAInfo != null) && (certs != null) && (!certs.isEmpty())) {
            final X509Certificate trustCertificate = certs.get(0);
            final String base64PemCert;
            try {
                base64PemCert = nscsCbpOiNodeUtility.convertToBase64String(trustCertificate);
            } catch (IOException ex) {
                log.error("convertToBase64String throws exception [{}]", ex.getMessage());
                throw new CppSecurityServiceException("getCbpOiTrustedCAInfoByName: " + ex);
            }
            nscsCbpOiTrustedEntityInfo = new NscsCbpOiTrustedEntityInfo(nscsTrustedCAInfo.getName(),
                                            nscsTrustedCAInfo.getSerialNumber(), trustCertificate.getIssuerDN().toString(),
                                            base64PemCert);
        }
        return nscsCbpOiTrustedEntityInfo;
    }

    @Override
    public Set<NscsCbpOiTrustedEntityInfo> getCbpOiTrustedCAsInfoByEntityProfileName(final String entityProfileName)
                                        throws CppSecurityServiceException {

        log.debug("get getCbpOiTrustedCAsInfoByEntityProfileName : starts for entity profile name [{}]", entityProfileName);

        final Set<NscsCbpOiTrustedEntityInfo> nscsTrustedCAsInfo = new HashSet<>();
        Set<TrustedEntityInfo> pkiTrustedCAsInfo = null;
        try {
            pkiTrustedCAsInfo = nscsPkiManager.getTrustedCAsInfoByEntityProfileName(entityProfileName);
        } catch (final NscsPkiEntitiesManagerException e) {
            final String errorMsg = NscsLogger.stringifyException(e)
                    + " from PKI while getting trusted CAs active cert info for entity profile name [" + entityProfileName + "]";
            log.error("get getCbpOiTrustedCAsInfoByEntityProfileName : {}", errorMsg);
            throw new CppSecurityServiceException(errorMsg);
        }
        final Iterator<TrustedEntityInfo> itPkiTrustedCAsInfo = pkiTrustedCAsInfo.iterator();
        while (itPkiTrustedCAsInfo.hasNext()) {
            final TrustedEntityInfo pkiTrustedCAInfo = itPkiTrustedCAsInfo.next();
            final NscsCbpOiTrustedEntityInfo cbpoiTrustedEntityInfo =
                        getCbpOiTrustedCAInfoByName(pkiTrustedCAInfo.getEntityName());
            if (cbpoiTrustedEntityInfo != null) {
                nscsTrustedCAsInfo.add(cbpoiTrustedEntityInfo);
            }
        }
        if (nscsTrustedCAsInfo.isEmpty()) {
            final String errorMsg = "No trusted CAs active cert info from PKI for entity profile name [" + entityProfileName + "]";
            log.error("get getCbpOiTrustedCAsInfoByEntityProfileName : {}", errorMsg);
            throw new CppSecurityServiceException(errorMsg);
        }

        log.debug("get getCbpOiTrustedCAsInfoByEntityProfileName : returns {} trusted CAs active cert info", nscsTrustedCAsInfo.size());
        return nscsTrustedCAsInfo;
    }

    @Override
    public Set<NscsTrustedEntityInfo> getTrustedCAsInfoByEntityProfileName(final String entityProfileName, final boolean isIPv6Node)
            throws CppSecurityServiceException {

        final String inputParams = "entity profile name [" + entityProfileName + "] isIPv6 [" + isIPv6Node + "]";
        log.debug("get getTrustedCAsInfoByEntityProfileName : starts for {}", inputParams);

        final Set<NscsTrustedEntityInfo> nscsTrustedCAsInfo = new HashSet<NscsTrustedEntityInfo>();
        Set<TrustedEntityInfo> pkiTrustedCAsInfo = null;
        try {
            pkiTrustedCAsInfo = nscsPkiManager.getTrustedCAsInfoByEntityProfileName(entityProfileName);
        } catch (final NscsPkiEntitiesManagerException e) {
            final String errorMsg = NscsLogger.stringifyException(e)
                    + " from PKI while getting trusted CAs active cert info for entity profile name [" + entityProfileName + "]";
            log.error("get getTrustedCAsInfoByEntityProfileName : " + errorMsg);
            throw new CppSecurityServiceException(errorMsg);
        }
        final Iterator<TrustedEntityInfo> itPkiTrustedCAsInfo = pkiTrustedCAsInfo.iterator();
        while (itPkiTrustedCAsInfo.hasNext()) {
            final TrustedEntityInfo pkiTrustedCAInfo = itPkiTrustedCAsInfo.next();
            final NscsTrustedEntityInfo nscsTrustedCAInfo = new NscsTrustedEntityInfo(pkiTrustedCAInfo, isIPv6Node);
            nscsTrustedCAsInfo.add(nscsTrustedCAInfo);
        }
        if (nscsTrustedCAsInfo.isEmpty()) {
            final String errorMsg = "No trusted CAs active cert info from PKI for entity profile name [" + entityProfileName + "]";
            log.error("get getTrustedCAsInfoByEntityProfileName : {}", errorMsg);
            throw new CppSecurityServiceException(errorMsg);
        }

        log.debug("get getTrustedCAsInfoByEntityProfileName : returns {} trusted CAs active cert info", nscsTrustedCAsInfo.size());
        return nscsTrustedCAsInfo;
    }

    @Override
    public boolean isNodeHasValidCertificate(final String nodeFdn, final String certificateType) throws CppSecurityServiceException {
        final NodeReference nodeRef = new NodeRef(nodeFdn);
        final NormalizableNodeReference normNode = readerService.getNormalizableNodeReference(nodeRef);
        if (normNode == null) {
            final String errorMsg = "NormalizedNodeReference MO is null for node [" + nodeFdn + "]";
            log.error(errorMsg);
            throw new CppSecurityServiceException(errorMsg);
        }
        final CertStateInfo certificateInfo = moGetServiceFactory.getCertificateIssueStateInfo(normNode, certificateType);
        boolean nodeHasValidCert = false;
        if (!certificateInfo.isInvalid()) {
            final String entityName = NscsPkiUtils.getEntityNameFromFdn(NodeEntityCategory.valueOf(certificateType), nodeFdn);
            final String nodeCertSerialNo = certificateInfo.getCertificates().get(0).getSerial().toString();
            final String nodeCertificateIssuer = certificateInfo.getCertificates().get(0).getIssuer();
            log.info("Node has a certificate with serial number {} issued by {}. Need to validate from PKI", nodeCertSerialNo, nodeCertificateIssuer);
            nodeHasValidCert = nscsPkiCertificateManager.isNodeHasValidCertificate(entityName, nodeCertSerialNo, nodeCertificateIssuer);
        }
        return nodeHasValidCert;
    }

    @Override
    public EnrollmentMode configureNESAndGetEnrollmentMode(final EnrollmentMode enrollmentMode, final String nodeFdn, final NormalizableNodeReference normRef)
            throws CppSecurityServiceException {
        EnrollmentMode oldEnrollmentMode = null;
        EnrollmentMode enrollmentModeToBeSet = enrollmentMode;
        if (!this.isNetworkElementSecurityMOPresent(normRef)) {
            log.info("NetworkElementSecurity MO is missing, it will be created for node [{}]", normRef.getFdn());
            try {
                this.createNetworkElementSecurityMO(normRef);
            } catch (final Exception e) {
                final String errorMsg = "Exception when creating new NetworkElementSecurity MO for node " + normRef.getFdn() + ": " + e.getMessage();
                log.error(errorMsg);
                throw new CppSecurityServiceException(errorMsg);
            }

            // Check on Enrollment Mode
            if (enrollmentMode == null) {
                // Read default from Capability Model...
                log.info("Reading default value for EnrollmentMode from Capability Model for node [{}]", normRef.getFdn());
                final String enrollmentModeStr = nscsCapabilityModelService.getDefaultEnrollmentMode(normRef);
                enrollmentModeToBeSet = EnrollmentMode.valueOf(enrollmentModeStr);
            }
        } else {
            log.info("NetworkElementSecurity MO is existing for node [{}]", normRef.getFdn());

            // Check on EnrollmentMode
            oldEnrollmentMode = getEnrollmentModeFromNES(nodeFdn);
            log.info("Reading old value for EnrollmentMode from Network Element Security for node [{}] Enrollment Mode [{}]", normRef.getFdn(),
                    oldEnrollmentMode.name());
            if (enrollmentMode == null) {
                // Since no data is passed to the method, the old value will
                // be used. This is a trick to NOT perform any set.
                log.info("Since no data is passed to the method, the old value [{}] will be used. This is a trick to NOT perform any set.",
                        oldEnrollmentMode.name());
                enrollmentModeToBeSet = oldEnrollmentMode;
            }
        }
        // Set Enrollment mode on Network Element Security MO
        this.setEnrollmentMode(normRef, oldEnrollmentMode, enrollmentModeToBeSet);
        return enrollmentModeToBeSet;
    }
    private String getSmrsIpAddress(final SmrsAddressRequest smrsAddReq){
        final String ipAddress = smrsService.getFileServerAddress(smrsAddReq);
        if (ipAddress.length() - ipAddress.replace(":", "").length() > 2){
            return "["+ipAddress+"]";
        }
        return ipAddress;
    }
}
