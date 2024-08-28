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
package com.ericsson.nms.security.nscs.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.exception.DataAccessException;
import com.ericsson.nms.security.nscs.api.exception.DataAccessSystemException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelInfo;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CmFunction;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComConnectivityInformation;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moget.MOGetServiceFactory;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.command.utility.NscsNameMultipleValueResponseBuilder;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

/**
 * Utility class for handling nodes
 *
 */
public class NscsNodeUtility {
    private static final String DNS_NAME = "DNS_NAME";
    private static final String IP_ADDRESS = "IP_ADDRESS";
    private static final String FQDN = "FQDN";
    private static final String IPV4 = "IPV4";
    private static final String IPV6 = "IPV6";

    @Inject
    private Logger logger;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private NscsModelServiceImpl nscsModelServiceImpl;

    @Inject
    private NscsCapabilityModelService capabilityService;

    @Inject
    private MOGetServiceFactory moGetServiceFactory;

    /**
     * Read EnrollmentMode from NetworkElementSecurity (NES) of the given node specified by its FDN and, if this is not set, return the default value
     * stored in Capability Model.
     *
     * @param nodeFdn
     * @return
     */
    public EnrollmentMode getEnrollmentMode(final String nodeFdn) {
        EnrollmentMode enrollmentMode = EnrollmentMode.NOT_SUPPORTED;
        if (nodeFdn != null && !nodeFdn.isEmpty()) {
            final NodeReference nodeRef = new NodeRef(nodeFdn);
            final NormalizableNodeReference normalizable = reader.getNormalizableNodeReference(nodeRef);
            if (normalizable != null) {
                enrollmentMode = getEnrollmentMode(normalizable);
            } else {
                logger.warn("NormalizableNodeReference null for node [{}]", nodeRef);
            }
        }
        return enrollmentMode;
    }

    /**
     * Read EnrollmentMode from NetworkElementSecurity (NES) of the given node specified by its normalizable node reference, and, if this is not set,
     * return the default value stored in Capability Model.
     *
     * @param normalizable
     * @return
     */
    public EnrollmentMode getEnrollmentMode(final NormalizableNodeReference normalizable) {
        EnrollmentMode enrollmentMode = EnrollmentMode.NOT_SUPPORTED;
        if (normalizable != null) {
            enrollmentMode = getEnrollmentMode(null, normalizable, true);
        } else {
            logger.warn("NormalizableNodeReference null ");
        }
        return enrollmentMode;
    }

    /**
     * Return EnrollmentMode for given input string value. If the value is not specified, try to read EnrollmentMode from NetworkElementSecurity (NES)
     * of the given node specified by its FDN and, if this is not set, return the default value stored in Capability Model.
     *
     * @param nodeFdn
     * @return
     */
    public EnrollmentMode getEnrollmentMode(final String inputEnrollmentModeString, final String nodeFdn) {
        NormalizableNodeReference normalizable = null;
        if (nodeFdn != null && !nodeFdn.isEmpty()) {
            final NodeReference nodeRef = new NodeRef(nodeFdn);
            normalizable = reader.getNormalizableNodeReference(nodeRef);
        }
        return getEnrollmentMode(inputEnrollmentModeString, normalizable);
    }

    /**
     * Return EnrollmentMode for given input string value. If the value is not specified, try to read EnrollmentMode from NetworkElementSecurity (NES)
     * of the given node specified by its normalizable node reference, and, if this is not set, return the default value stored in Capability Model.
     *
     * @param normalizable
     * @return
     */
    public EnrollmentMode getEnrollmentMode(final String inputEnrollmentModeString, final NormalizableNodeReference normalizable) {
        return getEnrollmentMode(inputEnrollmentModeString, normalizable, true);
    }

    /**
     * Read EnrollmentMode from NetworkElementSecurity (NES) of the given node specified by its FDN.
     *
     * @param nodeFdn
     * @return
     */
    public EnrollmentMode getEnrollmentModeFromNES(final String nodeFdn) {
        EnrollmentMode enrollmentMode = EnrollmentMode.NOT_SUPPORTED;
        if (nodeFdn != null && !nodeFdn.isEmpty()) {
            final NodeReference nodeRef = new NodeRef(nodeFdn);
            final NormalizableNodeReference normalizable = reader.getNormalizableNodeReference(nodeRef);
            if (normalizable != null) {
                enrollmentMode = getEnrollmentModeFromNES(normalizable);
            }
        }
        return enrollmentMode;
    }

    /**
     * Read EnrollmentMode from NetworkElementSecurity (NES) of the given node specified by its normalizable node reference.
     *
     * @param normalizable
     * @return
     */
    public EnrollmentMode getEnrollmentModeFromNES(final NormalizableNodeReference normalizable) {
        return getEnrollmentMode(null, normalizable, false);
    }

    /**
     * Return EnrollmentMode for given input string value. If the value is not specified, try to read EnrollmentMode from NetworkElementSecurity (NES)
     * of the given node and, if this not set and the default value is allowed, return the default value stored in Capability Model.
     *
     * @param inputEnrollmentModeString
     * @param normalizable
     * @param isDefaultAllowed
     * @return
     */
    public EnrollmentMode getEnrollmentMode(final String inputEnrollmentModeString, final NormalizableNodeReference normalizable,
            final boolean isDefaultAllowed) {

        EnrollmentMode enrollmentMode = EnrollmentMode.NOT_SUPPORTED;
        String enrollmentModeAsString = null;
        if (inputEnrollmentModeString == null || inputEnrollmentModeString.isEmpty()) {
            if (normalizable != null) {
                final String normalizedRefName = normalizable.getNormalizedRef().getName();
                logger.debug("Reading EnrollmentMode as optional parameter from NES for [{}]", normalizedRefName);
                if (reader.exists(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.withNames(normalizedRefName).fdn())) {
                    final CmResponse cmResponse = reader.getMOAttribute(normalizable.getNormalizedRef().getFdn(),
                            Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.type(),
                            Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.namespace(), NetworkElementSecurity.ENROLLMENT_MODE);
                    final Collection<CmObject> cmObjs = cmResponse.getCmObjects();
                    if (cmResponse == null || cmObjs == null || cmObjs.isEmpty() || cmObjs.size() > 1) {
                        logger.info("Error reading attribute [{}] from NormalizedModel for [{}]...going to read from Capability model",
                                NetworkElementSecurity.ENROLLMENT_MODE, normalizable.getNormalizedRef().getFdn());
                    } else {
                        enrollmentModeAsString = (String) cmObjs.iterator().next().getAttributes().get(NetworkElementSecurity.ENROLLMENT_MODE);
                        enrollmentMode = EnrollmentMode.valueOf(enrollmentModeAsString);
                    }
                }
                if ((enrollmentMode == null || EnrollmentMode.NOT_SUPPORTED.equals(enrollmentMode)) && (isDefaultAllowed == true)) {
                    logger.info("Reading default EnrollmentMode from Capability model for node [{}]", normalizedRefName);
                    enrollmentModeAsString = capabilityService.getDefaultEnrollmentMode(normalizable);
                    enrollmentMode = EnrollmentMode.valueOf(enrollmentModeAsString);
                }
            }
        } else {
            // enrollmentMode =
            // EnrollmentMode.getEnrollmentModeFromValue(inputEnrollmentModeString);
            enrollmentMode = EnrollmentMode.valueOf(inputEnrollmentModeString);
        }
        logger.debug("return EnrollmentMode [{}]", enrollmentMode);
        return enrollmentMode;
    }

    /**
     * Convert the name of given TrustedCertCategory to the name of correspondent CertificateType.
     *
     * @param trustedCertCategory
     *            the TrustedCertCategory name.
     * @return the CertificateType name.
     * @throws UnexpectedErrorException
     */
    public static String getCertificateTypeFromTrustedCertCategory(final String trustedCertCategory) throws UnexpectedErrorException {
        String certificateType = null;
        String errorMessage = null;
        try {
            final TrustedCertCategory trustedCertCategoryEnum = TrustedCertCategory.valueOf(trustedCertCategory);
            switch (trustedCertCategoryEnum) {
            case CORBA_PEERS:
                certificateType = CertificateType.OAM.name();
                break;
            case IPSEC:
                certificateType = CertificateType.IPSEC.name();
                break;
            case AA_SERVERS:
                // break intentionally omitted
            case ENROLLMENT_SERVERS:
                // break intentionally omitted
            case ERICSSON_1:
                // break intentionally omitted
            case LOCAL_AA_DB_FILE_SIGNERS:
                // break intentionally omitted
            case SYSLOG_SERVERS:
                // break intentionally omitted
            default:
                errorMessage = String.format("getCertificateTypeFromTrustedCertCategory: wrong value for trustedCertCategory[%s]",
                        trustedCertCategoryEnum);
                break;
            }
        } catch (final Exception e) {
            errorMessage = String.format("getCertificateTypeFromTrustedCertCategory: valueOf failed for trustedCertCategory[%s]",
                    trustedCertCategory);
        }

        if (errorMessage != null) {
            throw new UnexpectedErrorException(errorMessage);
        }

        return certificateType;
    }

    /**
     * Return AlgorithmKeys for given input value. If the value is not specified return the default value stored in Capability Model.
     *
     * @param inputAlgorithmKeysString
     * @param normalizable
     * @return
     */
    public AlgorithmKeys getAlgorithmKeys(final String inputAlgorithmKeysString, final NormalizableNodeReference normalizable) {

        AlgorithmKeys algorithmKeys = null;
        String algorithmKeysAsString = null;
        if (inputAlgorithmKeysString == null || inputAlgorithmKeysString.isEmpty()) {
            logger.debug("Reading default AlgorithmKeys from Capability model for node [{}]", normalizable);
            algorithmKeysAsString = capabilityService.getDefaultAlgorithmKeys(normalizable);
            try {
                algorithmKeys = AlgorithmKeys.valueOf(algorithmKeysAsString);
            } catch (final Exception e) {
                logger.error("Wrong default AlgorithmKeys value[{}] read from Capability Model for [{}]", algorithmKeysAsString, normalizable);
            }
        } else {
            algorithmKeys = AlgorithmKeys.valueOf(inputAlgorithmKeysString);
        }
        return algorithmKeys;
    }

    public String getNodeNameFromFdn(final String nodeFdnOrName) throws UnexpectedErrorException {
        final NodeReference node = new NodeRef(nodeFdnOrName);
        final NormalizableNodeReference normRef = reader.getNormalizedNodeReference(node);
        String nodeName = nodeFdnOrName;

        if (normRef == null) {
            if (Model.ME_CONTEXT.isPresent(nodeFdnOrName) || Model.NETWORK_ELEMENT.isPresent(nodeFdnOrName)) {
                final String errorMsg = "MeContext/NetworkElement in node FDN [" + nodeFdnOrName + "] , but no normalized node reference";
                //                log.error(errorMsg);
                throw new UnexpectedErrorException(errorMsg);
            }
        } else {
            nodeName = normRef.getName();
        }
        return nodeName;
    }

    /**
     * Get from connectivity information MO the IP address of the given node.
     *
     * @param nodeRef
     *            the node reference
     * @return the node IP address or null on error.
     */
    public String getNodeIpAddress(final NormalizableNodeReference nodeRef) {

        String nodeIpAddress = null;

        if (nodeRef != null) {
            String fdn = null;
            if (NscsCMReaderService.isNormalizedNodeReference(nodeRef)) {
                fdn = nodeRef.getFdn();
            } else if (NscsCMReaderService.isMirrorNodeReference(nodeRef)) {
                fdn = nodeRef.getNormalizedRef().getFdn();
            } else {
                logger.error("get NodeIpAddress : invalid node reference [" + nodeRef + "]");
            }
            logger.debug("get NodeIpAddress : got normalized FDN [" + fdn + "]");

            final String nodeType = nodeRef.getNeType();
            final String targetCategory = nodeRef.getTargetCategory();
            logger.debug("get NodeIpAddress : got target category [{}] node type [{}]", targetCategory, nodeType);
            final NscsModelInfo modelInfo = nscsModelServiceImpl.getConnectivityInfo(targetCategory, nodeType);
            final String type = modelInfo.getName();
            final String namespace = modelInfo.getNamespace();
            final String ipAddressAttribute = ComConnectivityInformation.IPADDRESS;

            final String readParams = "parent [" + fdn + "] type [" + type + "] namespace [" + namespace + "] attribute [" + ipAddressAttribute + "]";
            logger.debug("get NodeIpAddress : reading from DPS : " + readParams);
            final CmResponse cmResponse = reader.getMOAttribute(fdn, type, namespace, ipAddressAttribute);
            if (cmResponse == null || cmResponse.getCmObjects() == null || cmResponse.getCmObjects().isEmpty()) {
                logger.error("get NodeIpAddress : No objects returned from DPS while reading " + readParams);
            } else {
                final Collection<CmObject> cmObjs = cmResponse.getCmObjects();
                if (cmObjs.size() > 1) {
                    logger.error("get NodeIpAddress : Too many objects [" + cmObjs.size() + "] returned from DPS while reading " + readParams);
                } else {
                    nodeIpAddress = (String) cmObjs.iterator().next().getAttributes().get(ipAddressAttribute);
                }
            }
        } else {
            logger.error("get NodeIpAddress : NULL node reference.");
        }
        logger.debug("get NodeIpAddress : returns [" + nodeIpAddress + "]");
        return nodeIpAddress;
    }

    /**
     * Check if given node has a valid IPv6 address.
     *
     * @param nodeRef
     *            the node reference
     * @return true if valid IPv6 address, false otherwise.
     * @throws IllegalArgumentException
     */
    public boolean hasNodeIPv6Address(final NormalizableNodeReference nodeRef) throws IllegalArgumentException {
        boolean isIPv6 = false;
        try {
            isIPv6 = isIPv6Address(getNodeIpAddress(nodeRef));
        } catch(final IllegalArgumentException e) {
            logger.error("IP address is null or empty");
            throw new IllegalArgumentException("IP address is null or empty", e);
        }
        return isIPv6;
    }

    /**
     * Check if given IP address is a valid IPv6 address.
     *
     * @param ipAddress
     *            the IP address.
     * @return true if valid IPv6 address, false otherwise.
     * @throws IllegalArgumentException
     */
    public static boolean isIPv6Address(final String ipAddress) throws IllegalArgumentException {
        boolean isIPv6 = false;
        if (ipAddress == null || ipAddress.isEmpty() || ipAddress.equalsIgnoreCase("null")) {
            throw new IllegalArgumentException("is IPv6Address : NULL IP address.");
        } else {
            isIPv6 = NscsCommonValidator.getInstance().isValidIPv6Address(ipAddress);
        }
        return isIPv6;
    }

    /**
     * Returns if the given node reference supports Security Level.
     *
     * @param nodeRef
     *            the node reference
     * @return true if OAM/IPSEC Security Level is supported, false otherwise.
     */
    public boolean isSecurityLevelSupported(final NormalizableNodeReference nodeRef) {
        boolean isSupported = false;
        if (nodeRef != null) {
            final String unsupportedSecurityLevel = "LEVEL_NOT_SUPPORTED";
            isSupported = !capabilityService.isSecurityLevelSupported(nodeRef, unsupportedSecurityLevel);
        } else {
            logger.error("is SecurityLevelSupported : NULL node reference.");
        }
        logger.debug("is SecurityLevelSupported : returns [" + isSupported + "]");
        return isSupported;
    }

    /**
     * Get OAM Security Level of the given normalizable node according to the given synchronization status.
     *
     * @param nodeRef
     *            the node reference
     * @param syncStatus
     *            the synchronization status
     * @return the OAM Security Level or NOT_SUPPORTED if node does not support Security Level or UNKNOWN if node is not SYNCHRONIZED or null on
     *         error.
     */
    public String getSecurityLevel(final NormalizableNodeReference nodeRef, final String syncStatus) {

        String nodeSecurityLevel = null;

        if (nodeRef != null) {
            if (isSecurityLevelSupported(nodeRef)) {
                if (CmFunction.SyncStatusValue.SYNCHRONIZED.name().equals(syncStatus)) {
                    nodeSecurityLevel = moGetServiceFactory.getSecurityLevel(nodeRef, syncStatus);
                } else {
                    nodeSecurityLevel = NscsNameMultipleValueResponseBuilder.UNKNOWN;
                }
            } else {
                nodeSecurityLevel = NscsNameMultipleValueResponseBuilder.LEVEL_NOT_SUPPORTED;
            }
        } else {
            logger.error("get SecurityLevel : NULL node reference.");
        }
        logger.debug("get SecurityLevel : returns [" + nodeSecurityLevel + "]");
        return nodeSecurityLevel;
    }

    /**
     * Get IPSEC Security Level of the given normalizable node according to the given synchronization status.
     *
     * @param nodeRef
     *            the node reference
     * @param syncStatus
     *            the synchronization status
     * @return the IPSEC Security Level or NOT_SUPPORTED if node does not support Security Level or UNKNOWN if node is not SYNCHRONIZED or null on
     *         error.
     */
    public String getIpsecConfig(final NormalizableNodeReference nodeRef, final String syncStatus) {

        String nodeIpsecConfig = null;

        if (nodeRef != null) {
            if (isSecurityLevelSupported(nodeRef)) {
                if (CmFunction.SyncStatusValue.SYNCHRONIZED.name().equals(syncStatus)) {
                    nodeIpsecConfig = moGetServiceFactory.getIpsecConfig(nodeRef, syncStatus);
                } else {
                    nodeIpsecConfig = NscsNameMultipleValueResponseBuilder.UNKNOWN;
                }
            } else {
                nodeIpsecConfig = NscsNameMultipleValueResponseBuilder.IPSEC_NOT_SUPPORTED;
            }
        } else {
            logger.error("get IpsecConfig : NULL node reference.");
        }
        logger.debug("get IpsecConfig : returns [" + nodeIpsecConfig + "]");
        return nodeIpsecConfig;
    }

    /**
     * Get FDN of a single instance MO of given Mo under a root MO of given FDN.
     *
     * @param mirrorRootFdn
     *            the root MO FDN
     * @param mo
     *            the requested Mo
     * @return the FDN or null if MO doesn't exist
     * @throws IllegalArgumentException
     *             if input parameters are illegal
     */
    public String getSingleInstanceMoFdn(final String mirrorRootFdn, final Mo mo) {
        logger.debug("get SingleInstanceMoFdn for mo[{}] under rootFdn[{}]", mo, mirrorRootFdn);
        if (mo == null) {
            logger.error("get SingleInstanceMoFdn: illegal arguments: mo[{}], rootFdn[{}]", mo, mirrorRootFdn);
            throw new IllegalArgumentException();
        }
        final String moFdn = getSingleInstanceMoFdn(mirrorRootFdn, mo.type(), mo.namespace());
        logger.debug("get SingleInstanceMoFdn return[{}]", moFdn);
        return moFdn;
    }

    /**
     * Get FDN of a single instance MO of given Mo under a root MO of given FDN. If MO exists, its requested attributes are returned too.
     *
     * @param mirrorRootFdn
     *            the root MO FDN
     * @param mo
     *            the requested Mo
     * @param attributes
     *            the attributes of existing MO to be returned or null if attributes are not required.
     * @param requestedAttrs
     *            The list of requested attributes. If the attributes parameter is not null, this parameter shall be not null since if requested
     *            attributes are not specified only attributes modeled as 'FROM_PERSISTENCE' would be returned!
     * @return the FDN or null if MO doesn't exist
     * @throws IllegalArgumentException
     *             if input parameters are illegal
     */
    public String getSingleInstanceMoFdn(final String mirrorRootFdn, final Mo mo, final Map<String, Object> attributes,
            final String... requestedAttrs) {
        logger.debug("get SingleInstanceMoFdn for mo[{}] and attrs [{}] and requestedAttrs [{}] under rootFdn[{}]", mo, attributes, requestedAttrs,
                mirrorRootFdn);
        if (mo == null || (attributes != null && requestedAttrs == null)) {
            logger.error("get SingleInstanceMoFdn: illegal arguments: mo[{}], attrs [{}], requestedAttrs[{}], rootFdn[{}]", mo, attributes,
                    requestedAttrs, mirrorRootFdn);
            throw new IllegalArgumentException();
        }
        final String moFdn = getSingleInstanceMoFdn(mirrorRootFdn, mo.type(), mo.namespace(), attributes, requestedAttrs);
        logger.debug("get SingleInstanceMoFdn return[{}]", moFdn);
        return moFdn;
    }

    /**
     * Get FDN of a single instance MO of given namespace and type under a root MO of given FDN.
     *
     * @param mirrorRootFdn
     *            the root MO FDN
     * @param moName
     *            the requested MO type
     * @param moName
     *            the requested MO namespace
     * @return the FDN or null if MO doesn't exist
     */
    public String getSingleInstanceMoFdn(final String mirrorRootFdn, final String moName, final String moNameSpace) {
        logger.debug(" Get SingleInstanceMoFdn for moName[{}], moNameSpace[{}] and rootFdn[{}]", moName, moNameSpace, mirrorRootFdn);
        String moFdn = null;
        try {
            final CmResponse cmResponse = reader.getMos(mirrorRootFdn, moName, moNameSpace);
            if (cmResponse != null && cmResponse.getCmObjects() != null && cmResponse.getCmObjects().size() == 1) {
                final CmObject cmObject = cmResponse.getCmObjects().iterator().next();
                moFdn = cmObject.getFdn();
            } else {
                logger.error("Get SingleInstanceMoFdn failed for moName[{}], moNameSpace[{}] under rootFdn[{}]", moName, moNameSpace, mirrorRootFdn);
            }
        } catch (DataAccessSystemException | DataAccessException e) {
            logger.error("Caught exception[{}] msg [{}] while getting SingleInstanceMoFdn for moName[{}], moNameSpace[{}] under rootFdn[{}]",
                    e.getClass().getName(), e.getMessage(), moName, moNameSpace, mirrorRootFdn);
        }
        logger.debug("Get SingleInstanceMoFdn return[{}]", moFdn);
        return moFdn;
    }

    /**
     * Get FDN of a single instance MO of given type and namespace under a root MO of given FDN. If MO exists, its requested attributes are returned
     * too.
     *
     * @param mirrorRootFdn
     *            the root MO FDN
     * @param moName
     *            the requested MO type
     * @param moNameSpace
     *            the requested MO namespace
     * @param attributes
     *            the attributes of existing MO to be returned or null if attributes are not required.
     * @param requestedAttrs
     *            The list of requested attributes. If the attributes parameter is not null, this parameter shall be not null since if requested
     *            attributes are not specified only attributes modeled as 'FROM_PERSISTENCE' would be returned!
     * @return the FDN or null if MO doesn't exist
     * @throws IllegalArgumentException
     *             if input parameters are illegal
     */
    public String getSingleInstanceMoFdn(final String mirrorRootFdn, final String moName, final String moNameSpace,
            final Map<String, Object> attributes, final String... requestedAttrs) {
        logger.debug("Get SingleInstanceMoFdn for moName[{}], moNameSpace[{}], attrs[{}], requestedAttrs[{}] under rootFdn[{}]", moName, moNameSpace,
                attributes, requestedAttrs, mirrorRootFdn);
        if (attributes != null && requestedAttrs == null) {
            logger.error("Get SingleInstanceMoFdn: illegal arguments: moName[{}], moNameSpace[{}], attrs [{}], requestedAttrs[{}], rootFdn[{}]",
                    moName, moNameSpace, attributes, requestedAttrs, mirrorRootFdn);
            throw new IllegalArgumentException();
        }
        String moFdn = null;
        try {
            final CmResponse cmResponse = reader.getMos(mirrorRootFdn, moName, moNameSpace, requestedAttrs);
            if (cmResponse != null && cmResponse.getCmObjects() != null && cmResponse.getCmObjects().size() == 1) {
                final CmObject cmObject = cmResponse.getCmObjects().iterator().next();
                moFdn = cmObject.getFdn();
                if (attributes != null) {
                    attributes.putAll(cmObject.getAttributes());
                }
                logger.debug("Got moFdn[{}] and attributes[{}] for moName[{}], moNameSpace[{}] under rootFdn[{}]", moFdn, attributes, moName,
                        moNameSpace, mirrorRootFdn);
            } else {
                logger.error("Get SingleInstanceMoFdn failed for moName[{}], moNameSpace[{}] under rootFdn[{}]", moName, moNameSpace, mirrorRootFdn);
            }
        } catch (DataAccessSystemException | DataAccessException e) {
            logger.error("Caught exception[{}] msg [{}] while getting SingleInstanceMoFdn for moName[{}], moNameSpace[{}] under rootFdn[{}]",
                    e.getClass().getName(), e.getMessage(), moName, moNameSpace, mirrorRootFdn);
        }
        logger.debug("Get SingleInstanceMoFdn return[{}]", moFdn);
        return moFdn;
    }

    /**
     * This method prepares the subjectAltName in a format as required to be set on NodeCredential MO
     *
     * @param subjectAltNameValue
     *            subjectAltName value
     * @param subjectAltNameType
     *            subjectAltNameType
     * @return subjectAltName
     */
    public String prepareNodeCredentialSubjectAltName(final String subjectAltNameValue, final String subjectAltNameType) {

        String subjectAltName = null;
        switch (subjectAltNameType) {
        case FQDN:
        case DNS_NAME:
            subjectAltName = "DNS:" + subjectAltNameValue;
            break;

        case IPV6:
            // The ECIM model for the SAN attribute indicates that it supports in the case of IP_ADDRESS the type
            // as prefix IP:. In reality, for SAN of type IPv6, the presence of the IP: prefix causes a Constraint
            // Violation error due to a REGex. So since the model also accepts the non-presence of the prefix type
            // we for IPv6 implemented it this way without the IP: prefix.
            subjectAltName = subjectAltNameValue;
            break;

        case IPV4:
        case IP_ADDRESS:
            subjectAltName = "IP:" + subjectAltNameValue;
            break;

        default:
            subjectAltName = subjectAltNameValue;
            break;
        }
        return subjectAltName;
    }

    /**
     * This method will convert a string into a list of strings using the delimiter comma ','.
     *
     * @param str
     *          String object.
     * @return List of strings
     */
    public List<String> convertStringToList(final String str) {
        List<String> list = new ArrayList<>();
        if (str != null && !str.isEmpty()) {
            String[] words = str.replaceAll(Constants.REMOVE_UNWANTED_SQUARE_BRACKETS_REGEX, "").split(Constants.COMMA_DELIMITER);
            for (String word : words) {
                if (!list.contains(word.trim())) {
                    list.add(word.trim());
                }
            }
            return list;
        }
        return list;
    }

    /**
     * This method will check whether node has Ipv6 Address or not.
     *
     * @param nodeName
     *            the node name.
     * @return true or false the boolean value true if Ipv6 and false if not Ipv6
     */
    public boolean isNodeIpv6(final String nodeName) {
        NodeReference node = new NodeRef(nodeName);
        NormalizableNodeReference normRef = reader.getNormalizedNodeReference(node);
        return hasNodeIPv6Address(normRef);
    }

    /**
     * Convert the given keySize from the NSCS normalized value to the node supported value for the given node.
     *
     * @param nodeName
     *            the node name.
     *
     * @param keySize
     *            the NSCS normalized key size value
     *
     * @return the node supported key size value
     */
    public String getNodeCredentialKeyInfo(final String nodeName, final String keySize) {
        NodeReference nodeRef = new NodeRef(nodeName);
        NormalizableNodeReference normRef = reader.getNormalizedNodeReference(nodeRef);
        return moGetServiceFactory.getNodeCredentialKeyInfo(normRef, keySize);
    }
}
