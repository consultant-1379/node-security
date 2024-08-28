package com.ericsson.nms.security.nscs.utilities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.exception.DataAccessException;
import com.ericsson.nms.security.nscs.api.exception.DataAccessSystemException;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.EnrollmentServer.EnrollmentProtocol;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;

/**
 * Auxiliary class to determine the name of COM/ECIM MOs.
 *
 */
public class ComEcimMoNaming {

    @Inject
    private Logger logger;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    /**
     * Map containing association between specific MO type and the default name of its instances according to given certificate type.
     */
    static final Map<String, Map<String, String>> theDefaultMoNamesByCertType = new HashMap<>();

    static {
        // CertificateType-dependent names
        final String oam = CertificateType.OAM.name();
        final String ipsec = CertificateType.IPSEC.name();
        final Map<String, String> enrollmentAuthorityNames = new HashMap<String, String>();
        enrollmentAuthorityNames.put(oam, "1");
        enrollmentAuthorityNames.put(ipsec, "2");
        final Map<String, String> enrollmentServerGroupNames = new HashMap<String, String>();
        enrollmentServerGroupNames.put(oam, "1");
        enrollmentServerGroupNames.put(ipsec, "2");

        theDefaultMoNamesByCertType.put(ModelDefinition.ENROLLMENT_AUTHORITY_TYPE, enrollmentAuthorityNames);
        theDefaultMoNamesByCertType.put(ModelDefinition.ENROLLMENT_SERVER_GROUP_TYPE, enrollmentServerGroupNames);
    }

    /**
     * Map containing association between specific MO type and the default name of its instances according to enrollment protocol.
     */
    static final Map<String, Map<String, String>> theDefaultMoNamesByEnrollmentProtocol = new HashMap<String, Map<String, String>>();

    static {
        // EnrollmentProtocol-dependent names
        final String cmp = EnrollmentProtocol.CMP.name();
        final String scep = EnrollmentProtocol.SCEP.name();
        final Map<String, String> enrollmentServerNames = new HashMap<String, String>();
        enrollmentServerNames.put(cmp, "1");
        enrollmentServerNames.put(scep, "2");

        theDefaultMoNamesByEnrollmentProtocol.put(ModelDefinition.ENROLLMENT_SERVER_TYPE, enrollmentServerNames);
    }

    /**
     * Returns the default name of an MO of given type for the given certificate type or enrollment protocol for the given node reference.
     *
     * @param moType
     *            the MO type.
     * @param param
     *            the certificate type (OAM or IPSEC) or enrollment protocol (CMP or SCEP).
     * @param normNodeRef
     *            the node reference or null for MO types whose name is independent of node type.
     * @return the default MO name.
     * @throws IllegalArgumentException
     *             if invalid input parameters are passed
     */
    public String getDefaultName(final String moType, final String param, final NormalizableNodeReference normNodeRef)
            throws IllegalArgumentException {

        final String inputParams = "moType [" + moType + "] param [" + param + "] normNodeRef [" + normNodeRef + "]";

        String moName = null;
        String illegalArgumentMessage = null;

        if (moType != null && param != null) {
            final Map<String, String> moNames = getDefaultNames(moType, normNodeRef);
            if (moNames != null) {
                moName = moNames.get(param);
                if (moName == null) {
                    illegalArgumentMessage = "Invalid param in [" + inputParams + "]";
                }
            } else {
                illegalArgumentMessage = "Invalid MO type in [" + inputParams + "]";
            }
        } else {
            illegalArgumentMessage = "Null input params in [" + inputParams + "]";
        }

        if (illegalArgumentMessage != null) {
            throw new IllegalArgumentException(illegalArgumentMessage);
        }

        return moName;
    }

    /**
     * Returns the default name of an MO of given type for the given certificate type or enrollment protocol for the given node model info.
     *
     * @param moType
     *            the MO type.
     * @param param
     *            the certificate type (OAM or IPSEC) or enrollment protocol (CMP or SCEP).
     * @param modelInfo
     *            the node node model info or null for MO types whose name is independent of node type.
     * @return the default MO name.
     * @throws IllegalArgumentException
     *             if invalid input parameters are passed
     */
    public String getDefaultName(final String moType, final String param, final NodeModelInformation modelInfo) throws IllegalArgumentException {

        final String inputParams = "moType [" + moType + "] param [" + param + "] modelInfo [" + modelInfo + "]";

        String moName = null;
        String illegalArgumentMessage = null;

        if (moType != null && param != null) {
            final Map<String, String> moNames = getDefaultNames(moType, modelInfo);
            if (moNames != null) {
                moName = moNames.get(param);
                if (moName == null) {
                    illegalArgumentMessage = "Invalid param in [" + inputParams + "]";
                }
            } else {
                illegalArgumentMessage = "Invalid MO type in [" + inputParams + "]";
            }
        } else {
            illegalArgumentMessage = "Null input params in [" + inputParams + "]";
        }

        if (illegalArgumentMessage != null) {
            throw new IllegalArgumentException(illegalArgumentMessage);
        }

        return moName;
    }

    /**
     * Return the fixed name of an MO of given type.
     *
     * @param moType
     *            the MO type.
     * @return the fixed MO name
     * @throws IllegalArgumentException
     *             if invalid input parameters are passed
     */
    public static String getName(final String moType) throws IllegalArgumentException {

        final String inputParams = "moType [" + moType + "]";

        String moName = null;
        String illegalArgumentMessage = null;

        if (moType != null) {
            switch (moType) {
            case ModelDefinition.SEC_M_TYPE:
                // break intentionally omitted
            case ModelDefinition.CERT_M_TYPE:
                // break intentionally omitted
            case ModelDefinition.SYS_M_TYPE:
                // break intentionally omitted
            case ModelDefinition.NETCONF_TLS_TYPE:
                // break intentionally omitted
            case ModelDefinition.TRANSPORT_TYPE:
                // break intentionally omitted
            case ModelDefinition.IKEV2_POLICY_PROFILE_TYPE:
                final Integer numericValue = new Integer(1);
                moName = numericValue.toString();
                break;
            default:
                illegalArgumentMessage = "Invalid MO type in [" + inputParams + "]";
                break;
            }
        } else {
            illegalArgumentMessage = "Null input params in [" + inputParams + "]";
        }

        if (illegalArgumentMessage != null) {
            throw new IllegalArgumentException(illegalArgumentMessage);
        }

        return moName;
    }

    /**
     * Return the "first" available name of an MO of given type and namespace under a given parent. The possible default values for the given MO type
     * and given NE type are NOT used.
     *
     * The namespace, passed as parameter, is actually not meaningful since it is used only in the get operation of already existent MOs and the get
     * doesn't use such parameter. It is left for future possible cases.
     *
     * Each MO type can have its own naming policy: the basic one is to assign the first unused, different from any default values, positive, not null
     * integer.
     *
     * @param moType
     *            the MO type.
     * @param moNamespace
     *            the MO namespace.
     * @param parentFdn
     *            the FDN of the parent MO.
     * @return the MO name
     * @throws IllegalArgumentException
     *             if invalid input parameters are passed
     * @throws UnexpectedErrorException
     *             if something fails with valid input parameters
     */
    public String getFirstAvailableName(final String moType, final String moNamespace, final String parentFdn)
            throws IllegalArgumentException, UnexpectedErrorException {

        final String inputParams = "moType [" + moType + "] moNs [" + moNamespace + "] parentFdn [" + parentFdn + "]";

        logger.debug("get FirstAvailableName: start for " + inputParams);

        String moName = null;
        final String moNamePrefix = null;
        final String moNameSuffix = null;
        String illegalArgumentMessage = null;
        String unexpectedErrorMessage = null;

        if (moType != null && parentFdn != null && !parentFdn.isEmpty()) {
            switch (moType) {
            case ModelDefinition.NODE_CREDENTIAL_TYPE:
                // break intentionally omitted
            case ModelDefinition.ENROLLMENT_SERVER_GROUP_TYPE:
                // break intentionally omitted
            case ModelDefinition.ENROLLMENT_AUTHORITY_TYPE:
                // break intentionally omitted
            case ModelDefinition.TRUST_CATEGORY_TYPE:
                // break intentionally omitted
            case ModelDefinition.ENROLLMENT_SERVER_TYPE:
                // moNameSuffix = "";
                // moNamePrefix = "";
                // if ((certificateType != null) &&
                // (!certificateType.isEmpty())) {
                // moNamePrefix = getDefaultName(mo, certificateType, neType);
                // // Check if name prefix ends with digit
                // if
                // (Character.isDigit(moNamePrefix.charAt(moNamePrefix.length()
                // - 1))) {
                // moNamePrefix = null;
                // }
                // }
                break;
            default:
                illegalArgumentMessage = "Unsupported moType in [" + inputParams + "]";
                break;
            }
        } else {
            illegalArgumentMessage = "Null input params in [" + inputParams + "]";
        }

        if (illegalArgumentMessage != null) {
            logger.error("get FirstAvailableName: " + illegalArgumentMessage);
            throw new IllegalArgumentException(illegalArgumentMessage);
        }

        Integer numericValue = new Integer(1);
        String currentName = null;
        try {
            final CmResponse cmResponse = reader.getMos(parentFdn, moType, moNamespace);
            if (cmResponse != null && cmResponse.getCmObjects() != null) {
                boolean isAvailableNameFound = false;
                while (!isAvailableNameFound && unexpectedErrorMessage == null) {
                    currentName = buildMoName(moNamePrefix, numericValue.toString(), moNameSuffix);
                    boolean isNameFound = false;
                    if (!isDefaultName(currentName, moType)) {
                        final Iterator<CmObject> it = cmResponse.getCmObjects().iterator();
                        while (it.hasNext()) {
                            final CmObject cmObject = it.next();
                            if (currentName.equals(cmObject.getName())) {
                                isNameFound = true;
                                break;
                            }
                        }
                    } else {
                        // Default values shall be skipped as if they were in use
                        isNameFound = true;
                    }
                    if (isNameFound) {
                        numericValue++;
                    } else {
                        isAvailableNameFound = true;
                        moName = currentName;
                    }
                }
            } else {
                unexpectedErrorMessage = "get FirstAvailableMoName: null response while getting MOs for " + inputParams;
            }
        } catch (DataAccessSystemException | DataAccessException e) {
            unexpectedErrorMessage = "get FirstAvailableMoName: exc [" + e.getClass().getName() + "] msg [" + e.getMessage()
                    + "] while getting MOs for " + inputParams;
        }
        moName = currentName;

        if (unexpectedErrorMessage != null) {
            logger.error(unexpectedErrorMessage);
            throw new UnexpectedErrorException(unexpectedErrorMessage);
        }

        logger.debug("get FirstAvailableMoName: return MO name [" + moName + "]");

        return moName;
    }

    /**
     * Returns the default MO names of the given MO for the given node reference.
     *
     * @param moType
     *            the MO type.
     * @param normNodeRef
     *            the node reference.
     * @return the default MO names or null if type is invalid.
     */
    private Map<String, String> getDefaultNames(final String moType, final NormalizableNodeReference normNodeRef) {

        Map<String, String> moNames = null;

        if (moType != null) {
            switch (moType) {
            case ModelDefinition.NODE_CREDENTIAL_TYPE:
                moNames = nscsCapabilityModelService.getComEcimDefaultNodeCredentialIds(normNodeRef);
                break;
            case ModelDefinition.TRUST_CATEGORY_TYPE:
                moNames = nscsCapabilityModelService.getComEcimDefaultTrustCategoryIds(normNodeRef);
                break;
            case ModelDefinition.ENROLLMENT_SERVER_GROUP_TYPE:
                // break intentionally omitted
            case ModelDefinition.ENROLLMENT_AUTHORITY_TYPE:
                moNames = theDefaultMoNamesByCertType.get(moType);
                break;
            case ModelDefinition.ENROLLMENT_SERVER_TYPE:
                moNames = theDefaultMoNamesByEnrollmentProtocol.get(moType);
                break;
            default:
                break;
            }
        }

        return moNames;
    }

    /**
     * Returns the default MO names of the given MO for the given node model info.
     *
     * @param moType
     *            the MO type.
     * @param modelInfo
     *            the node model info.
     * @return the default MO names or null if type is invalid.
     */
    private Map<String, String> getDefaultNames(final String moType, final NodeModelInformation modelInfo) {

        Map<String, String> moNames = null;

        if (moType != null) {
            switch (moType) {
            case ModelDefinition.NODE_CREDENTIAL_TYPE:
                moNames = nscsCapabilityModelService.getComEcimDefaultNodeCredentialIds(modelInfo);
                break;
            case ModelDefinition.TRUST_CATEGORY_TYPE:
                moNames = nscsCapabilityModelService.getComEcimDefaultTrustCategoryIds(modelInfo);
                break;
            case ModelDefinition.ENROLLMENT_SERVER_GROUP_TYPE:
                // break intentionally omitted
            case ModelDefinition.ENROLLMENT_AUTHORITY_TYPE:
                moNames = theDefaultMoNamesByCertType.get(moType);
                break;
            case ModelDefinition.ENROLLMENT_SERVER_TYPE:
                moNames = theDefaultMoNamesByEnrollmentProtocol.get(moType);
                break;
            default:
                break;
            }
        }

        return moNames;
    }

    /**
     * Check if given MO name is a possible default name for an MO of given type.
     *
     * @param moName
     *            the MO name.
     * @param moType
     *            the MO type.
     * @return true if it is a possible default MO name, false otherwise
     */
    private boolean isDefaultName(final String moName, final String moType) {

        boolean isDefaultName = false;

        if (moType != null && moName != null) {
            for (final String targetType : nscsCapabilityModelService.getTargetTypes(TargetTypeInformation.CATEGORY_NODE)) {
                final NodeModelInformation modelInfo = new NodeModelInformation(null, null, targetType);
                final Map<String, String> moNames = getDefaultNames(moType, modelInfo);
                if (moNames != null) {
                    if (moNames.containsValue(moName)) {
                        isDefaultName = true;
                        break;
                    }
                }
            }
        }
        return isDefaultName;
    }

    /**
     * Return an MO name concatenating the given prefix, name and suffix.
     *
     * Each null parameter is skipped.
     *
     * @param prefix
     * @param name
     * @param suffix
     * @return
     */
    private static String buildMoName(final String prefix, final String name, final String suffix) {
        final StringBuilder sb = new StringBuilder();
        if (prefix != null) {
            sb.append(prefix).append("_");
        }
        if (name != null) {
            sb.append(name);
        }
        if (suffix != null) {
            sb.append("_").append(suffix);
        }
        return sb.toString();
    }

}
