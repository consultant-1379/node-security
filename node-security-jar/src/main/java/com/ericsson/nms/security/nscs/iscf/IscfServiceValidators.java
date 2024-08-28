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
package com.ericsson.nms.security.nscs.iscf;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeProductDataException;
import com.ericsson.nms.security.nscs.api.exception.InvalidVersionException;
import com.ericsson.nms.security.nscs.api.exception.IscfServiceException;
import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType;
import com.ericsson.nms.security.nscs.api.iscf.IpsecArea;
import com.ericsson.nms.security.nscs.api.iscf.NodeIdentifier;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameEdiPartyType;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.util.MimVersion;
import com.ericsson.nms.security.nscs.util.NodeProductData;
import com.ericsson.nms.security.nscs.utilities.NscsCommonValidator;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation;

/**
 * Auxiliary class for validators of IscfService interface.
 *
 */
//@Stateless
public class IscfServiceValidators {

    @Inject
    private Logger logger;

    @Inject
    private NscsCapabilityModelService capabilityService;

    private static final String subjectAltNameWildcardValue = "?";

    /**
     * Validate OAM generate.
     *
     * @param logicalName
     * @param nodeFdn
     * @param wantedSecLevel
     * @param minimumSecLevel
     * @param wantedEnrollmentMode
     * @param modelInfo
     * @throws IscfServiceException
     */
    public void validateGenerateOam(final String logicalName, final String nodeFdn, final SecurityLevel wantedSecLevel,
            final SecurityLevel minimumSecLevel, final EnrollmentMode wantedEnrollmentMode, final NodeModelInformation modelInfo)
            throws IscfServiceException {

        validateLogicalName(logicalName);
        validateFdn(nodeFdn);
        validateNodeModelInformation(modelInfo);
        validateSecurityLevels(modelInfo, wantedSecLevel, minimumSecLevel);
        validateEnrollmentMode(wantedEnrollmentMode, modelInfo);
    }

    /**
     * Validate Security Data OAM generate.
     *
     * @param nodeFdn
     * @param wantedEnrollmentMode
     * @param modelInfo
     * @throws IscfServiceException
     */
    public void validateGenerateSecurityDataOam(final String nodeFdn, final EnrollmentMode wantedEnrollmentMode, final NodeModelInformation modelInfo)
            throws IscfServiceException {

        validateFdn(nodeFdn);
        validateNodeModelInformation(modelInfo);
        validateEnrollmentMode(wantedEnrollmentMode, modelInfo);
    }

    /**
     * Validate Security Data OAM generate.
     *
     * @param nodeId
     * @param wantedEnrollmentMode
     * @param modelInfo
     * @throws IscfServiceException
     */
    public void validateGenerateSecurityDataOam(final NodeIdentifier nodeId, final EnrollmentMode wantedEnrollmentMode,
            final NodeModelInformation modelInfo) throws IscfServiceException {

        validateNodeIdentifier(nodeId);
        validateNodeModelInformation(modelInfo);
        validateEnrollmentMode(wantedEnrollmentMode, modelInfo);
    }

    /**
     * Validate Security Data OAM generate.
     *
     * @param NodeIdentifier
     *            The identifier information of the node undergoing auto-integration
     * @param SubjectAltNameParam
     *            The Subject Alternative Name
     * @param EnrollmentMode
     *            The desired enrollment mode for the node
     * @param NodeModelInformation
     *            The Node Model Information
     * @throws IscfServiceException
     *
     */

    public void validateGenerateSecurityDataOam(final NodeIdentifier nodeId, final SubjectAltNameParam subjectAltNameParam,
            final EnrollmentMode wantedEnrollmentMode, final NodeModelInformation modelInfo) throws IscfServiceException {

        validateNodeIdentifier(nodeId);
        validateNodeModelInformation(modelInfo);
        if (subjectAltNameParam != null) {
            validateSubjectAltNameParam(subjectAltNameParam.getSubjectAltNameData(), subjectAltNameParam.getSubjectAltNameFormat(),
                    nodeId.getSerialNumber(), modelInfo);
        }

        validateEnrollmentMode(wantedEnrollmentMode, modelInfo);
    }

    /**
     * Validate IPSEC generate.
     *
     * @param logicalName
     * @param nodeFdn
     * @param ipsecUserLabel
     * @param subjectAltNameParam
     * @param wantedIpSecAreas
     * @param wantedEnrollmentMode
     * @param modelInfo
     * @throws IscfServiceException
     */
    public void validateGenerateIpsec(final String logicalName, final String nodeFdn, final String ipsecUserLabel,
            final SubjectAltNameParam subjectAltNameParam, final Set<IpsecArea> wantedIpSecAreas, final EnrollmentMode wantedEnrollmentMode,
            final NodeModelInformation modelInfo) throws IscfServiceException {

        validateLogicalName(logicalName);
        validateFdn(nodeFdn);
        validateIpSecUserLabel(ipsecUserLabel);
        validateNodeModelInformation(modelInfo);
        validateSubjectAltNameParam(subjectAltNameParam.getSubjectAltNameData(), subjectAltNameParam.getSubjectAltNameFormat(), null, modelInfo);
        validateIpSecAreas(wantedIpSecAreas);
        validateEnrollmentMode(wantedEnrollmentMode, modelInfo);
    }

    /**
     * Validate IPSEC Security Data generator.
     *
     * @param nodeFdn
     * @param subjectAltNameParam
     * @param wantedEnrollmentMode
     * @param modelInfo
     * @throws IscfServiceException
     */
    public void validateGenerateSecurityDataIpsec(final String nodeFdn, final SubjectAltNameParam subjectAltNameParam,
            final EnrollmentMode wantedEnrollmentMode, final NodeModelInformation modelInfo) throws IscfServiceException {

        validateFdn(nodeFdn);
        validateNodeModelInformation(modelInfo);
        validateSubjectAltNameParam(subjectAltNameParam.getSubjectAltNameData(), subjectAltNameParam.getSubjectAltNameFormat(), null, modelInfo);
        validateEnrollmentMode(wantedEnrollmentMode, modelInfo);
    }

    /**
     * Validate IPSEC Security Data generator.
     *
     * @param nodeId
     * @param subjectAltNameParam
     * @param wantedEnrollmentMode
     * @param modelInfo
     * @throws IscfServiceException
     */
    public void validateGenerateSecurityDataIpsec(final NodeIdentifier nodeId, final SubjectAltNameParam subjectAltNameParam,
            final EnrollmentMode wantedEnrollmentMode, final NodeModelInformation modelInfo) throws IscfServiceException {

        validateNodeIdentifier(nodeId);
        validateNodeModelInformation(modelInfo);
        validateSubjectAltNameParam(subjectAltNameParam.getSubjectAltNameData(), subjectAltNameParam.getSubjectAltNameFormat(),
                nodeId.getSerialNumber(), modelInfo);
        validateEnrollmentMode(wantedEnrollmentMode, modelInfo);
    }

    /**
     * Validate COMBO generate.
     *
     * @param logicalName
     * @param nodeFdn
     * @param wantedSecLevel
     * @param minimumSecLevel
     * @param ipsecUserLabel
     * @param subjectAltNameParam
     * @param wantedIpSecAreas
     * @param wantedEnrollmentMode
     * @param modelInfo
     * @throws IscfServiceException
     */
    public void validateGenerateCombo(final String logicalName, final String nodeFdn, final SecurityLevel wantedSecLevel,
            final SecurityLevel minimumSecLevel, final String ipsecUserLabel, final SubjectAltNameParam subjectAltNameParam,
            final Set<IpsecArea> wantedIpSecAreas, final EnrollmentMode wantedEnrollmentMode, final NodeModelInformation modelInfo)
            throws IscfServiceException {

        validateLogicalName(logicalName);
        validateFdn(nodeFdn);
        validateIpSecUserLabel(ipsecUserLabel);
        validateNodeModelInformation(modelInfo);
        validateSubjectAltNameParam(subjectAltNameParam.getSubjectAltNameData(), subjectAltNameParam.getSubjectAltNameFormat(), null, modelInfo);
        validateIpSecAreas(wantedIpSecAreas);
        validateSecurityLevels(modelInfo, wantedSecLevel, minimumSecLevel);
        validateEnrollmentMode(wantedEnrollmentMode, modelInfo);
    }

    /**
     * Validate COMBO Security Data generator.
     *
     * @param nodeFdn
     * @param subjectAltNameParam
     * @param wantedEnrollmentMode
     * @param modelInfo
     * @throws IscfServiceException
     */
    public void validateGenerateSecurityDataCombo(final String nodeFdn, final SubjectAltNameParam subjectAltNameParam,
            final EnrollmentMode wantedEnrollmentMode, final NodeModelInformation modelInfo) throws IscfServiceException {

        validateFdn(nodeFdn);
        validateNodeModelInformation(modelInfo);
        validateSubjectAltNameParam(subjectAltNameParam.getSubjectAltNameData(), subjectAltNameParam.getSubjectAltNameFormat(), null, modelInfo);
        validateEnrollmentMode(wantedEnrollmentMode, modelInfo);
    }

    /**
     * Validate COMBO Security Data generator.
     *
     * @param nodeId
     * @param subjectAltNameParam
     * @param wantedEnrollmentMode
     * @param modelInfo
     * @throws IscfServiceException
     */
    public void validateGenerateSecurityDataCombo(final NodeIdentifier nodeId, final SubjectAltNameParam subjectAltNameParam,
            final EnrollmentMode wantedEnrollmentMode, final NodeModelInformation modelInfo) throws IscfServiceException {

        validateNodeIdentifier(nodeId);
        validateNodeModelInformation(modelInfo);
        validateSubjectAltNameParam(subjectAltNameParam.getSubjectAltNameData(), subjectAltNameParam.getSubjectAltNameFormat(),
                nodeId.getSerialNumber(), modelInfo);
        validateEnrollmentMode(wantedEnrollmentMode, modelInfo);
    }

    /**
     * Validate the given FDN. An exception is thrown on validation error.
     *
     * @param fdn
     *            the FDN.
     * @throws IscfServiceException
     */
    private void validateFdn(final String fdn) throws IscfServiceException {
        String errorMsg = null;
        if (fdn == null) {
            errorMsg = String.format("Wrong FDN[%s]", fdn);
        } else if (fdn.isEmpty()) {
            errorMsg = String.format("Wrong FDN[]");
        }

        if (errorMsg != null) {
            logger.error("validate Fdn: {}", errorMsg);
            throw new IscfServiceException(errorMsg);
        }
    }

    /**
     * Validate the given Node Identifier. An exception is thrown on validation error.
     *
     * @param nodeId
     *            the NodeIdentifier.
     * @throws IscfServiceException
     */
    private void validateNodeIdentifier(final NodeIdentifier nodeId) throws IscfServiceException {
        String errorMsg = null;
        if (nodeId == null) {
            errorMsg = String.format("Wrong NodeIdentifier[%s]", nodeId);
        } else {
            validateFdn(nodeId.getFdn());
        }

        if (errorMsg != null) {
            logger.error("validate NodeIdentifier: {}", errorMsg);
            throw new IscfServiceException(errorMsg);
        }
    }

    /**
     * Validate the given logical name. An exception is thrown on validation error.
     *
     * @param logicalName
     *            the logical name.
     * @throws IscfServiceException
     */
    private void validateLogicalName(final String logicalName) throws IscfServiceException {
        String errorMsg = null;
        if (logicalName == null) {
            errorMsg = String.format("Wrong logical name[%s]", logicalName);
        } else if (logicalName.isEmpty()) {
            errorMsg = String.format("Wrong logical name[]");
        }

        if (errorMsg != null) {
            logger.error("validate LogicalName: {}", errorMsg);
            throw new IscfServiceException(errorMsg);
        }
    }

    /**
     * Validate the given node model information. An exception is thrown on validation error.
     *
     * @param modelInfo
     *            the node model information
     * @throws IscfServiceException
     */
    private void validateNodeModelInformation(final NodeModelInformation modelInfo) throws IscfServiceException {
        String errorMsg = null;
        if (modelInfo == null) {
            errorMsg = "Wrong Node Model Information[" + modelInfo + "]";
        } else {
            final String nodeType = modelInfo.getNodeType();
            if (nodeType == null || nodeType.isEmpty()) {
                errorMsg = "Wrong node type[" + nodeType + "]";
            } else {
                if (!capabilityService.isTargetTypeSupported(TargetTypeInformation.CATEGORY_NODE, nodeType)) {
                    errorMsg = "node type[" + nodeType + "] not supported";
                } else {
                    if (!capabilityService.isCertificateManagementSupported(modelInfo)) {
                        errorMsg = "node type[" + nodeType + "] not supporting certificate management";
                    }
                }
            }

            if (errorMsg == null) {
                // Validate model identifier
                final ModelIdentifierType modelIdType = modelInfo.getModelIdentifierType();
                final String modelId = modelInfo.getModelIdentifier();
                if (ModelIdentifierType.UNKNOWN.equals(modelIdType)) {
                    errorMsg = String.format("Wrong model identifier type[%s]", modelIdType.name());
                } else {
                    if (modelId == null) {
                        logger.info("model identifier is Null");
                    } else if (modelId.isEmpty()) {
                        logger.info("model identifier is Empty");
                    } else {
                        if (ModelIdentifierType.PRODUCT_NUMBER.equals(modelIdType)) {
                            try {
                                @SuppressWarnings("unused")
                                final NodeProductData nodeProductData = new NodeProductData(modelId);
                            } catch (final InvalidNodeProductDataException e) {
                                errorMsg = String.format("Wrong Node Product Data[%s]", modelId);
                            }
                        } else if (ModelIdentifierType.MIM_VERSION.equals(modelIdType)) {
                            try {
                                @SuppressWarnings("unused")
                                final MimVersion mimVersion = new MimVersion(modelId);
                            } catch (final InvalidVersionException e) {
                                errorMsg = String.format("Wrong MIM version[%s]", modelId);
                            }
                        } else if (ModelIdentifierType.OSS_IDENTIFIER.equals(modelIdType)) {
                            logger.info("OSS_IDENTIFIER : {}", modelId);
                        }
                    }
                }
            }
        }

        if (errorMsg != null) {
            logger.error("validateNodeModelInformation: {}", errorMsg);
            throw new IscfServiceException(errorMsg);
        }
    }

    /**
     * Validate the given wanted and minimum security levels for the given node model information. An exception is thrown on validation error.
     *
     * @param modelInfo
     *            the node model information.
     * @param wanted
     *            the wanted security level.
     * @param minimum
     *            the minimum security level.
     * @throws IscfServiceException
     */
    private void validateSecurityLevels(final NodeModelInformation modelInfo, final SecurityLevel wanted, final SecurityLevel minimum)
            throws IscfServiceException {
        String errorMsg = null;
        if (modelInfo == null) {
            errorMsg = String.format("Wrong Node Model Information[null]");
        } else {
            if (wanted == null) {
                errorMsg = String.format("Wrong Wanted Security Level[null]");
            }
            if (minimum == null) {
                errorMsg = String.format("%sWrong Minimum Security Level[null]", (errorMsg != null ? errorMsg + " and " : ""));
            }
        }

        if (errorMsg == null) {
            final List<String> supportedSecurityLevels = capabilityService.getSupportedSecurityLevels(modelInfo);
            if (supportedSecurityLevels != null) {
                if (!supportedSecurityLevels.contains(wanted.name())) {
                    errorMsg = String.format("Wanted Security Level[%s]", wanted.name());
                }
                if (!supportedSecurityLevels.contains(minimum.name())) {
                    errorMsg = String.format("%sMinimum Security Level[%s]", (errorMsg != null ? errorMsg + " and " : ""), minimum.name());
                }
                if (errorMsg != null) {
                    errorMsg = String.format("%s not supported for Node Model Information[%s]", errorMsg, modelInfo);
                } else {
                    if (wanted.compareTo(minimum) < 0) {
                        errorMsg = String.format("Minimum Security Level[%s] cannot be greater than Wanted Security Level[%s]", minimum.name(),
                                minimum.name());
                    }
                }
            } else {
                errorMsg = String.format("Get supported SL for Node Model Information[%s] failed", modelInfo);
            }
        }

        if (errorMsg != null) {
            logger.error("validate SecurityLevels: {}", errorMsg);
            throw new IscfServiceException(errorMsg);
        }
    }

    /**
     * Validate the given enrollment mode for the given NE type. An exception is thrown on validation error.
     *
     * @param enrollmentMode
     *            the wanted enrollment mode.
     * @param modelInfo
     * @throws IscfServiceException
     */
    private void validateEnrollmentMode(final EnrollmentMode enrollmentMode, final NodeModelInformation modelInfo) throws IscfServiceException {
        String errorMsg = null;

        if (modelInfo == null) {
            errorMsg = String.format("Wrong Node Model Information[" + modelInfo + "]");
        } else if (enrollmentMode == null) {
            errorMsg = "Wrong Enrollment Mode[" + enrollmentMode + "]";
        } else {
            final List<String> supportedEnrollmentModes = capabilityService.getSupportedEnrollmentModes(modelInfo);
            if (supportedEnrollmentModes != null) {
                if (!supportedEnrollmentModes.contains(enrollmentMode.name())) {
                    errorMsg = "Wanted Enrollment Mode[" + enrollmentMode.name() + "] not supported for NE type[" + modelInfo.getNodeType() + "]";
                }
            }
        }

        if (errorMsg != null) {
            logger.error("validateEnrollmentMode: " + errorMsg);
            throw new IscfServiceException(errorMsg);
        }
    }

    /**
     * Validate the given IPSEC user label. An exception is thrown on validation error.
     *
     * @param ipsecUserLabel
     *            the IPSEC user label.
     * @throws IscfServiceException
     */
    private void validateIpSecUserLabel(final String ipsecUserLabel) throws IscfServiceException {
    }

    /**
     * Validate the given IPSEC areas. An exception is thrown on validation error.
     *
     * @param ipsecAreas
     *            the IPSEC areas.
     * @throws IscfServiceException
     */
    private void validateIpSecAreas(final Set<IpsecArea> ipsecAreas) throws IscfServiceException {
        String errorMsg = null;
        if (ipsecAreas == null) {
            errorMsg = String.format("Wrong IpSec Areas[%s]", ipsecAreas);
        } else if (ipsecAreas.isEmpty()) {
            errorMsg = String.format("Wrong IpSec Areas[]");
        }

        if (errorMsg != null) {
            logger.error("validateIpSecAreas: {}", errorMsg);
            throw new IscfServiceException(errorMsg);
        }
    }

    /**
     * Validate the given SubjectAltName data and format parameters. An exception is thrown on validation error.
     *
     * @param subjectAltNameData
     *            the SubjectAltName data parameter.
     * @param subjectAltNameFormat
     *            the SubjectAltName format parameter.
     * @param nodeSerialNumber
     *            the node serial number (used by pico)
     * @param nodeModelInfo
     *            the node model info
     * @throws IscfServiceException
     */
    private void validateSubjectAltNameParam(final BaseSubjectAltNameDataType subjectAltNameData, final SubjectAltNameFormat subjectAltNameFormat,
            final String nodeSerialNumber, final NodeModelInformation nodeModelInfo) throws IscfServiceException {
        String errorMsg = null;
        final boolean isPicoRbsNode = !capabilityService.isConfiguredSubjectNameUsedForEnrollment(nodeModelInfo);
        if (isPicoRbsNode) {
            if ((nodeSerialNumber != null) && !nodeSerialNumber.isEmpty() && ((subjectAltNameFormat == null) || (subjectAltNameData == null))) {
                // For picoRBS nodes, if serial number is specified, subjectAltName can be null
                return;
            }
        }
        if ((subjectAltNameFormat == null) && (subjectAltNameData != null)) {
            errorMsg = String.format("Wrong SubjectAltNameFormat[null]");
        } else {
            if ((subjectAltNameData == null) && (subjectAltNameFormat != null)) {
                errorMsg = String.format("Wrong SubjectAltNameData[null]");
            } else {
                if ((subjectAltNameFormat != null) && (subjectAltNameData != null)) {
                    if (SubjectAltNameFormat.NONE.equals(subjectAltNameFormat)) {
                        errorMsg = String.format("%sWrong SubjectAltNameFormat[%s]", (errorMsg != null ? " and " : ""), subjectAltNameFormat);
                    } else {
                        String subjectAltNameDataString = null;
                        if (subjectAltNameData instanceof SubjectAltNameStringType) {
                            subjectAltNameDataString = ((SubjectAltNameStringType) subjectAltNameData).getValue();
                        }

                        // If subjectAltName value is a wildcard, no need of other checks
                        if (!subjectAltNameWildcardValue.equals(subjectAltNameDataString)) {
                            if ((SubjectAltNameFormat.IPV4.equals(subjectAltNameFormat))
                                    || (SubjectAltNameFormat.IPV6.equals(subjectAltNameFormat))) {
                                // Value must be not null instance of SubjectAltNameStringType
                                if (subjectAltNameDataString == null) {
                                    errorMsg = String.format("Wrong subjectAltNameData[null]");
                                } else {
                                    if (SubjectAltNameFormat.IPV4.equals(subjectAltNameFormat)) {
                                        if (!NscsCommonValidator.getInstance().isValidIPv4Address(subjectAltNameDataString)) {
                                            errorMsg = String.format("subjectAltNameData is not valid IPv4 address");
                                        }
                                    } else {
                                        if (!NscsCommonValidator.getInstance().isValidIPv6Address(subjectAltNameDataString)) {
                                            errorMsg = String.format("subjectAltNameData is not valid IPv6 address");
                                        }
                                    }
                                }
                            } else { // SubjectAltNameFormat.FQDN

                                if (subjectAltNameDataString != null) {
                                    if (subjectAltNameDataString.isEmpty()) {
                                        errorMsg = String.format("Wrong subjectAltName[]");
                                    }
                                } else if (subjectAltNameData instanceof SubjectAltNameEdiPartyType) {
                                    final String nameAssigner = ((SubjectAltNameEdiPartyType) subjectAltNameData).getNameAssigner();
                                    final String partyName = ((SubjectAltNameEdiPartyType) subjectAltNameData).getPartyName();
                                    if (nameAssigner == null) {
                                        errorMsg = String.format("Wrong nameAssigner[%s]", nameAssigner);
                                    } else if (nameAssigner.isEmpty()) {
                                        errorMsg = String.format("Wrong nameAssigner[]");
                                    }
                                    if (partyName == null) {
                                        errorMsg = String.format("%sWrong partyName[%s]", (errorMsg != null ? " and " : ""), partyName);
                                    } else if (partyName.isEmpty()) {
                                        errorMsg = String.format("%sWrong partyName[]", (errorMsg != null ? " and " : ""));
                                    }
                                } else {
                                    errorMsg = String.format("Unsupported SubjectAltNameData class");
                                }
                            }
                        }
                    }
                } else {
                    errorMsg = String.format("Wrong subjectAltName[null]");
                }
            }
        }

        if (errorMsg != null) {
            logger.error("validate SubjectAltNameParam: {}", errorMsg);
            throw new IscfServiceException(errorMsg);
        }
    }
}
