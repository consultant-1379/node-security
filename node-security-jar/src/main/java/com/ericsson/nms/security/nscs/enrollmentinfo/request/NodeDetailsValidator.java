/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.enrollmentinfo.request;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException;
import com.ericsson.nms.security.nscs.api.exception.InvalidSubjAltNameXmlException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotCertifiableException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.SubjAltNameTypeNotSupportedXmlException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedCertificateTypeException;
import com.ericsson.nms.security.nscs.api.iscf.NodeIdentifier;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.EnrollmentRequestInfo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.enrollmentinfo.request.model.NodeDetails;
import com.ericsson.nms.security.nscs.enrollmentinfo.utility.EnrollmentInfoConstants;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.nms.security.nscs.utilities.NscsCommonValidator;
import com.ericsson.oss.services.security.nscs.command.enrollmentinfo.OtpConfigurationParameters;

/**
 * Utility service responsible for validating the Node Details provided by the user.
 *
 * @author tcsmave
 *
 */
public class NodeDetailsValidator {

    private static final String SAN_WILDCARD = "?";

    @Inject
    private Logger logger;

    @Inject
    private NscsCMReaderService nscsCmReaderService;

    @Inject
    private NodeValidatorUtility nodeValidatorUtility;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    /**
     * Validates the user provided node details. If valid, converts to internal NSCS enrollment request info format. If invalid, an exception is
     * thrown.
     * 
     * @param nodeDetails
     *            input node details provided by user.
     * @return the internal NSCS enrollment request info format if user provided node details are valid.
     */
    public EnrollmentRequestInfo validate(final NodeDetails nodeDetails) {

        logger.debug("Starts for nodeDetails [{}].", nodeDetails);

        final NormalizableNodeReference normNode = validateNode(nodeDetails);

        final String certType = validateCertType(nodeDetails, normNode);

        return validateNodeDetails(normNode, nodeDetails, certType);
    }

    /**
     * Validates the given NBI node details for the given node. If valid, converts to internal NSCS enrollment request info format. If invalid, an
     * exception is thrown.
     * 
     * @param normNode
     *            the normalized node reference.
     * @param nodeDetails
     *            input node details provided by user.
     * @return the internal NSCS enrollment request info format if user provided node details are valid.
     */
    public EnrollmentRequestInfo validateNbiRequest(final NormalizableNodeReference normNode, final NodeDetails nodeDetails) {

        logger.debug("Starts for node [{}] and nodeDetails [{}].", normNode, nodeDetails);

        final String domainName = validateDomain(nodeDetails);

        return validateNodeDetails(normNode, nodeDetails, domainName);
    }

    /**
     * Validate the given domain.
     * 
     * @param nodeDetails
     *            the node details.
     * @return the domain.
     */
    private String validateDomain(final NodeDetails nodeDetails) {
        final String domainName = nodeDetails.getCertType();
        validateDomainName(domainName);
        return domainName;
    }

    /**
     * Validate the given domain name.
     * 
     * @param domainName
     *            the domain name.
     * @throws {@link
     *             InvalidArgumentValueException} if domain name is not valid.
     */
    public void validateDomainName(final String domainName) {
        if (!EnrollmentInfoConstants.OAM.equals(domainName)) {
            final String errorMessage = String.format("Domain [%s] not supported.", domainName);
            logger.error(errorMessage);
            throw new InvalidArgumentValueException(errorMessage);
        }
    }

    /**
     * Validates the given node details for the given node and domain. If valid, converts to internal NSCS enrollment request info format. If invalid,
     * an exception is thrown.
     * 
     * @param normNode
     *            the normalized node reference.
     * @param nodeDetails
     *            the node details.
     * @param domainName
     *            the domain.
     * @return the internal NSCS enrollment request info format if given node details are valid.
     */
    private EnrollmentRequestInfo validateNodeDetails(final NormalizableNodeReference normNode, final NodeDetails nodeDetails,
            final String domainName) {

        final String keySize = validateKeySize(nodeDetails);

        final EnrollmentMode enrollmentMode = validateEnrollmentMode(nodeDetails, normNode);

        final SubjectAltNameParam subjectAltNameParam = validateSubjectAltNameParams(nodeDetails);

        final OtpConfigurationParameters otpConfigurationParameters = validateOtpConfigurationParams(nodeDetails);

        final EnrollmentRequestInfo enrollmentRequestInfo = new EnrollmentRequestInfo();
        enrollmentRequestInfo.setNodeIdentifier(new NodeIdentifier(nodeDetails.getNodeFdn(), null));
        enrollmentRequestInfo.setNodeName(normNode.getName());
        enrollmentRequestInfo.setCertType(domainName);
        enrollmentRequestInfo.setEntityProfile(nodeDetails.getEntityProfileName());
        enrollmentRequestInfo.setKeySize(keySize);
        enrollmentRequestInfo.setCommonName(nodeDetails.getCommonName());
        enrollmentRequestInfo.setEnrollmentMode(enrollmentMode);
        enrollmentRequestInfo.setSubjectAltNameParam(subjectAltNameParam);
        enrollmentRequestInfo.setIpVersion(nodeDetails.getIpVersion());
        enrollmentRequestInfo.setOtpConfigurationParameters(otpConfigurationParameters);
        return enrollmentRequestInfo;
    }

    /**
     * Validate the node.
     * 
     * @param nodeDetails
     *            the node details.
     * @return if valid, the normalized node reference.
     * @throws {@link
     *             InvalidArgumentValueException} if nodeFdn is an invalid node name or FDN.
     * @throws {@link
     *             InvalidNodeNameException} if NetworkElement MO does not exist.
     * @throws {@link
     *             NodeNotCertifiableException} if node does not support certificate management.
     */
    private NormalizableNodeReference validateNode(final NodeDetails nodeDetails) {
        final String nodeNameOrFdn = nodeDetails.getNodeFdn();
        final NodeReference nodeReference = new NodeRef(nodeNameOrFdn);
        final NormalizableNodeReference normNode = nscsCmReaderService.getNormalizedNodeReference(nodeReference);
        if (normNode == null) {
            logger.error("{} for node [{}].", NscsErrorCodes.THE_NETWORK_ELEMENT_SPECIFIED_DOES_NOT_EXIST, nodeNameOrFdn);
            throw new InvalidNodeNameException();
        }
        if (!nodeValidatorUtility.isCertificateSupportedForNode(normNode)) {
            logger.error("Node [{}] does not support certificate management.", nodeNameOrFdn);
            throw new NodeNotCertifiableException();
        }
        return normNode;
    }

    /**
     * Validate the certificate type for the given normalized node reference. If not specified in user provided node details, OAM is returned.
     * 
     * Current implementation only supports OAM.
     * 
     * @param nodeDetails
     *            the node details
     * @param normNode
     *            the normalized node reference.
     * @return if valid and supported, the certificate type.
     * @throws {@link
     *             InvalidArgumentValueException} if certificate type is invalid.
     * @throws {@link
     *             UnsupportedCertificateTypeException} if node does not support certificate type.
     */
    private String validateCertType(final NodeDetails nodeDetails, final NormalizableNodeReference normNode) {
        String certType = EnrollmentInfoConstants.OAM;
        if (nodeDetails.getCertType() != null) {
            certType = nodeDetails.getCertType().trim();
            if (!certType.equals(EnrollmentInfoConstants.OAM)) {
                final String errorMessage = String.format("Certificate type [%s] not supported.", certType);
                logger.error(errorMessage);
                throw new InvalidArgumentValueException(errorMessage);
            }
        }
        if (!nodeValidatorUtility.isCertificateTypeSupported(normNode, certType)) {
            logger.error("{} [{}] for node [{}]", NscsErrorCodes.UNSUPPORTED_CERTIFICATE_TYPE, certType, nodeDetails.getNodeFdn());
            throw new UnsupportedCertificateTypeException(certType);
        }
        return certType;
    }

    /**
     * Validate the key size and algorithm for the given node details.
     * 
     * @param nodeDetails
     *            the node details.
     * @return the key size and algorithm.
     * @throws {@link
     *             InvalidArgumentValueException} if key size is invalid.
     */
    private String validateKeySize(final NodeDetails nodeDetails) {
        final String keySize = nodeDetails.getKeySize() != null ? nodeDetails.getKeySize().trim() : null;
        if (keySize != null) {
            try {
                AlgorithmKeys.valueOf(keySize);
            } catch (final IllegalArgumentException e) {
                final String errorMessage = String.format("Key size [%s] is invalid.", keySize);
                logger.error(errorMessage);
                throw new InvalidArgumentValueException(errorMessage);
            }
        }
        return keySize;
    }

    /**
     * Validate the enrollment mode for the given normalized node reference. If not specified in the user provided node details, the default
     * enrollment mode for the given node is returned.
     * 
     * @param nodeDetails
     *            the node details.
     * @param normNode
     *            the normalized node reference.
     * @return if valid, the enrollment mode.
     * @throws {@link
     *             InvalidArgumentValueException} if enrollment mode is not supported by the node.
     */
    private EnrollmentMode validateEnrollmentMode(final NodeDetails nodeDetails, final NormalizableNodeReference normNode) {
        String enrollmentType = nodeDetails.getEnrollmentMode() != null ? nodeDetails.getEnrollmentMode().trim() : null;
        if (enrollmentType != null) {
            final List<String> supportedEnrollmentModes = nscsCapabilityModelService.getSupportedEnrollmentModes(normNode);
            if (!supportedEnrollmentModes.contains(enrollmentType)) {
                final String errorMessage = String.format("%s%s", NscsErrorCodes.UNSUPPORTED_ENROLLMENT_MODE, supportedEnrollmentModes);
                logger.error("Enrollment mode [{}] for node [{}] : {}", enrollmentType, nodeDetails.getNodeFdn(), errorMessage);
                throw new InvalidArgumentValueException(errorMessage);
            }
        } else {
            enrollmentType = nscsCapabilityModelService.getDefaultEnrollmentMode(normNode);
        }
        return EnrollmentMode.valueOf(enrollmentType);
    }

    /**
     * Validate the subject alternative name.
     * 
     * Current implementation ONLY supports IP Address formats.
     * 
     * @param nodeDetails
     *            the node details.
     * @return if valid, the subject alternative name parameter.
     * @throws {@link
     *             InvalidArgumentValueException} if subject alternative name type is not null and subject alternative name is null or if subject
     *             alternative name type is null and subject alternative name is not null.
     */
    private SubjectAltNameParam validateSubjectAltNameParams(final NodeDetails nodeDetails) {

        SubjectAltNameParam subjectAltNameParam = null;

        final String subjectAltNameType = nodeDetails.getSubjectAltNameType() != null ? nodeDetails.getSubjectAltNameType().trim() : null;
        final String subjectAltName = nodeDetails.getSubjectAltName() != null ? nodeDetails.getSubjectAltName().trim() : null;
        if (subjectAltNameType != null) {
            if (subjectAltName == null) {
                final String errorMessage = "Null SubjectAlternativeNameValue for not null SubjectAlternativeNameType";
                logger.error("{} [{}]", errorMessage, subjectAltNameType);
                throw new InvalidArgumentValueException(errorMessage);
            }
            final SubjectAltNameFormat subjectAltNameFormat = validateSubjectAltNameType(subjectAltNameType);
            final SubjectAltNameStringType subjectAltNameStringType = validateSubjectAltName(subjectAltName, subjectAltNameFormat);
            subjectAltNameParam = new SubjectAltNameParam(subjectAltNameFormat, subjectAltNameStringType);
        } else {
            if (subjectAltName != null) {
                final String errorMessage = "Null SubjectAlternativeNameType for not null SubjectAlternativeNameValue";
                logger.error("{} [{}]", errorMessage, subjectAltName);
                throw new InvalidArgumentValueException(errorMessage);
            }
        }

        return subjectAltNameParam;
    }

    /**
     * Validate the subject alternative name type.
     * 
     * Current implementation ONLY supports IP Address formats.
     * 
     * @param subjectAltNameType
     *            the subject alternative name type.
     * @return if valid, the subject alternative name format.
     * @throws {@link
     *             SubjAltNameTypeNotSupportedXmlException} if subject alternative name format is not supported.
     */
    private SubjectAltNameFormat validateSubjectAltNameType(final String subjectAltNameType) {
        SubjectAltNameFormat subjectAltNameFormat = null;
        if (subjectAltNameType != null) {
            switch (subjectAltNameType) {
            case "IPV4":
                subjectAltNameFormat = SubjectAltNameFormat.IPV4;
                break;
            case "IPV6":
                subjectAltNameFormat = SubjectAltNameFormat.IPV6;
                break;
            case "RFC822_NAME":
                subjectAltNameFormat = SubjectAltNameFormat.RFC822_NAME;
                break;
            case "DNS_NAME":
                subjectAltNameFormat = SubjectAltNameFormat.FQDN;
                break;

            default:
                break;
            }
        }
        if (subjectAltNameFormat == null) {
            logger.error("SubjectAltNameType [{}] not supported", subjectAltNameType);
            throw new SubjAltNameTypeNotSupportedXmlException();
        }
        return subjectAltNameFormat;
    }

    /**
     * Validate the subject alternative name according to the given subject alternative name format.
     * 
     * Current implementation ONLY supports IP Address formats.
     * 
     * @param subjectAltName
     *            the subject alternative name.
     * @param subjectAltNameFormat
     *            the subject alternative name format.
     * @return if valid, the subject alternative name value as string.
     * @throws {@link
     *             InvalidSubjAltNameXmlException} if subject alternative name is invalid.
     */
    private SubjectAltNameStringType validateSubjectAltName(final String subjectAltName, final SubjectAltNameFormat subjectAltNameFormat) {
        SubjectAltNameStringType subjectAltNameStringType = null;
        if (SAN_WILDCARD.equals(subjectAltName)){
            subjectAltNameStringType = new SubjectAltNameStringType(subjectAltName);
            return subjectAltNameStringType;
        }

        switch (subjectAltNameFormat) {
        case IPV4:
            if (NscsCommonValidator.getInstance().isValidIPv4Address(subjectAltName)) {
                subjectAltNameStringType = new SubjectAltNameStringType(subjectAltName);
            }
            break;
        case IPV6:
            if (NscsCommonValidator.getInstance().isValidIPv6Address(subjectAltName)) {
                subjectAltNameStringType = new SubjectAltNameStringType(subjectAltName);
            }
            break;
        case RFC822_NAME:
            if (NscsCommonValidator.getInstance().isValidRFC822Name(subjectAltName)) {
                subjectAltNameStringType = new SubjectAltNameStringType(subjectAltName);
            }
            break;
        case FQDN:
            if (NscsCommonValidator.getInstance().isValidDNSName(subjectAltName)) {
                subjectAltNameStringType = new SubjectAltNameStringType(subjectAltName);
            }
            break;

        default:
            break;
        }

        if (subjectAltNameStringType == null) {
            logger.error("SubjectAltName [{}] invalid for SubjectAltNameFormat [{}]", subjectAltName, subjectAltNameFormat);
            throw new InvalidSubjAltNameXmlException();
        }
        return subjectAltNameStringType;
    }

    /**
     * Validate OTP configuration parameters.
     * 
     * @param nodeDetails
     *            the node details.
     * @return if valid, the OTP configuration parameters.
     * @throws {@link
     *             InvalidArgumentValueException} if OTP count or OTP validity period are out of allowed ranges.
     */
    private OtpConfigurationParameters validateOtpConfigurationParams(NodeDetails nodeDetails) {
        final Integer otpCount = nodeDetails.getOtpCount();
        final Integer otpValidityPeriodInMinutes = nodeDetails.getOtpValidityPeriodInMinutes();
        if (otpCount != null && otpCount < EnrollmentInfoConstants.MIN_OTP_COUNT) {
            final String errorMessage = "OTP count is invalid";
            logger.error("{} [{}]", errorMessage, otpCount);
            throw new InvalidArgumentValueException(errorMessage);
        }
        if (otpValidityPeriodInMinutes != null && !EnrollmentInfoConstants.NEVER_EXPIRING_OTP_VALIDITY_PERIOD.equals(otpValidityPeriodInMinutes)
                && (otpValidityPeriodInMinutes < EnrollmentInfoConstants.MIN_OTP_VALIDITY_PERIOD_IN_MINUTES
                        || otpValidityPeriodInMinutes > EnrollmentInfoConstants.MAX_OTP_VALIDITY_PERIOD_IN_MINUTES)) {
            final String errorMessage = "OTP validity period in minutes is invalid";
            logger.error("{} [{}]", errorMessage, otpValidityPeriodInMinutes);
            throw new InvalidArgumentValueException(errorMessage);
        }
        return new OtpConfigurationParameters(otpCount, otpValidityPeriodInMinutes);
    }
}
