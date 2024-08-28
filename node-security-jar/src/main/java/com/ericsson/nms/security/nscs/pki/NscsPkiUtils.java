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
package com.ericsson.nms.security.nscs.pki;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NodeEntityCategory;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceBean;
import com.ericsson.nms.security.nscs.utilities.Constants;
import com.ericsson.nms.security.nscs.utilities.NscsCommonValidator;
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo;
import com.ericsson.oss.itpf.security.pki.common.model.Subject;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.AbstractSubjectAltNameFieldValue;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltName;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameField;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameFieldType;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameString;
import com.ericsson.oss.itpf.security.pki.manager.model.EnrollmentType;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

/**
 * Utility class for PKI management
 *
 * @author emaborz
 *
 */
/**
 * @author enmadmin
 *
 */
public abstract class NscsPkiUtils {

    private static Logger logger = LoggerFactory.getLogger(NscsPkiUtils.class);

    /**
     * Translation map for Enrollment Mode from NSCS to PKI format
     */
    private static final Map<EnrollmentMode, EnrollmentType> emMap;
    static {
        final Map<EnrollmentMode, EnrollmentType> map = new HashMap<EnrollmentMode, EnrollmentType>();
        map.put(EnrollmentMode.SCEP, EnrollmentType.scep);
        map.put(EnrollmentMode.CMPv2_VC, EnrollmentType.cmp);
        map.put(EnrollmentMode.CMPv2_INITIAL, EnrollmentType.cmp);
        //		map.put(EnrollmentMode.CMPv2_VC, EnrollmentType.CMPV2_VC);
        //		map.put(EnrollmentMode.CMPv2_INITIAL, EnrollmentType.CMPV2_IAK);
        map.put(EnrollmentMode.ONLINE_SCEP, EnrollmentType.scep);
        emMap = Collections.unmodifiableMap(map);
    }

    /**
     * Translation map for Enrollment Mode from NSCS to PKI format
     */
    private static final Map<SubjectAltNameFormat, SubjectAltNameFieldType> subjectAltNameMap;
    static {
        final Map<SubjectAltNameFormat, SubjectAltNameFieldType> map = new HashMap<SubjectAltNameFormat, SubjectAltNameFieldType>();
        map.put(SubjectAltNameFormat.FQDN, SubjectAltNameFieldType.DNS_NAME);
        map.put(SubjectAltNameFormat.IPV4, SubjectAltNameFieldType.IP_ADDRESS);
        map.put(SubjectAltNameFormat.IPV6, SubjectAltNameFieldType.IP_ADDRESS);
        map.put(SubjectAltNameFormat.RFC822_NAME, SubjectAltNameFieldType.RFC822_NAME);
        map.put(SubjectAltNameFormat.NONE, SubjectAltNameFieldType.OTHER_NAME);
        subjectAltNameMap = Collections.unmodifiableMap(map);
    }

    private static final Map<AlgorithmKeys, CppSecurityServiceBean.KeyLength> keyLengthMap;
    static {
        final Map<AlgorithmKeys, CppSecurityServiceBean.KeyLength> klMap = new HashMap<>();
        klMap.put(AlgorithmKeys.RSA_1024, CppSecurityServiceBean.KeyLength.RSA1024);
        klMap.put(AlgorithmKeys.RSA_2048, CppSecurityServiceBean.KeyLength.RSA2048);
        klMap.put(AlgorithmKeys.RSA_3072, CppSecurityServiceBean.KeyLength.RSA3072);
        klMap.put(AlgorithmKeys.RSA_4096, CppSecurityServiceBean.KeyLength.RSA4096);
        klMap.put(AlgorithmKeys.ECDSA_160, CppSecurityServiceBean.KeyLength.ECDSA160);
        klMap.put(AlgorithmKeys.ECDSA_224, CppSecurityServiceBean.KeyLength.ECDSA224);
        klMap.put(AlgorithmKeys.ECDSA_256, CppSecurityServiceBean.KeyLength.ECDSA256);
        klMap.put(AlgorithmKeys.ECDSA_384, CppSecurityServiceBean.KeyLength.ECDSA384);
        klMap.put(AlgorithmKeys.ECDSA_512, CppSecurityServiceBean.KeyLength.ECDSA512);
        klMap.put(AlgorithmKeys.ECDSA_521, CppSecurityServiceBean.KeyLength.ECDSA521);
        keyLengthMap = Collections.unmodifiableMap(klMap);
    }

    private static final Map<CppSecurityServiceBean.KeyLength, AlgorithmKeys> algoKeysMap;
    static {
        final Map<CppSecurityServiceBean.KeyLength, AlgorithmKeys> akMap = new HashMap<>();
        akMap.put(CppSecurityServiceBean.KeyLength.RSA1024, AlgorithmKeys.RSA_1024);
        akMap.put(CppSecurityServiceBean.KeyLength.RSA2048, AlgorithmKeys.RSA_2048);
        akMap.put(CppSecurityServiceBean.KeyLength.RSA3072, AlgorithmKeys.RSA_3072);
        akMap.put(CppSecurityServiceBean.KeyLength.RSA4096, AlgorithmKeys.RSA_4096);
        akMap.put(CppSecurityServiceBean.KeyLength.ECDSA160, AlgorithmKeys.ECDSA_160);
        akMap.put(CppSecurityServiceBean.KeyLength.ECDSA224, AlgorithmKeys.ECDSA_224);
        akMap.put(CppSecurityServiceBean.KeyLength.ECDSA256, AlgorithmKeys.ECDSA_256);
        akMap.put(CppSecurityServiceBean.KeyLength.ECDSA384, AlgorithmKeys.ECDSA_384);
        akMap.put(CppSecurityServiceBean.KeyLength.ECDSA512, AlgorithmKeys.ECDSA_512);
        akMap.put(CppSecurityServiceBean.KeyLength.ECDSA521, AlgorithmKeys.ECDSA_521);
        algoKeysMap = Collections.unmodifiableMap(akMap);
    }

    /**
     * Translate given Enrollment Mode from NSCS to PKI format
     *
     * @param enrollmentMode
     *            the Enrollment Mode in NSCS format
     * @return Enrollment Mode in PKI format or null if translation fails
     */
    public static EnrollmentType convertEnrollmentModeToPkiFormat(
            final EnrollmentMode enrollmentMode) {
        if (enrollmentMode != null)
            return emMap.get(enrollmentMode);
        return null;
    }

    /**
     * Translate given SubjectAltNameFormat from NSCS to PKI format
     * @param subjectAltNameFormat the SubjectAltNameFormat in NSCS format
     * @return SubjectAltNameFieldType in PKI format or null if translation fails
     */
    public static SubjectAltNameFieldType convertSubjectAltNameFormatToPkiFormat(final SubjectAltNameFormat subjectAltNameFormat) {
        return subjectAltNameMap.get(subjectAltNameFormat);
    }

    public static AbstractSubjectAltNameFieldValue convertSubjectAltNameValueToPkiFormat(
            final SubjectAltNameFormat subjectAltNameFormat,
            final BaseSubjectAltNameDataType subjectAltNameDataType) {
        AbstractSubjectAltNameFieldValue subjectAltNameValueType = null;

        if ((subjectAltNameFormat != null) && (subjectAltNameDataType != null)) {
            switch (subjectAltNameFormat) {
                case IPV4:
                case IPV6:
                case RFC822_NAME:
                case FQDN:
                    if (subjectAltNameDataType.getClass().equals(SubjectAltNameStringType.class)) {
                        final SubjectAltNameString subjectAltNameString = new SubjectAltNameString();
                        final SubjectAltNameStringType subjectAltNameStringType
                                = (SubjectAltNameStringType) subjectAltNameDataType;
                        subjectAltNameString.setValue(subjectAltNameStringType.getValue());
                        subjectAltNameValueType = subjectAltNameString;
                    }
                    break;
                case NONE:
                default:
                    break;
            }
        }
        return subjectAltNameValueType;
    }

    public static BaseSubjectAltNameDataType convertSubjectAltNameValueToNscsFormat(
        final SubjectAltNameFieldType subjectAltNameType,
        final AbstractSubjectAltNameFieldValue subjectAltNameValType) {
        BaseSubjectAltNameDataType subjectAltNameDataType = null;

        if ((subjectAltNameType != null) && (subjectAltNameValType != null)) {
            switch (subjectAltNameType) {
                case RFC822_NAME:
                case OTHER_NAME:
                case DNS_NAME:
                case IP_ADDRESS:
                    if (subjectAltNameValType.getClass().equals(SubjectAltNameString.class)) {
                        final SubjectAltNameString subjectAltNameString = (SubjectAltNameString)subjectAltNameValType;
                        final SubjectAltNameStringType subjectAltNameStringType =
                                new SubjectAltNameStringType(subjectAltNameString.getValue());
                        subjectAltNameDataType = subjectAltNameStringType;
                    }
                    break;
                default:
                    break;
            }

        }
        return subjectAltNameDataType;
    }

    /**
     * Translate given SubjectAltNameFieldType from PKI to NSCS format
     * @param subjectAltNameFieldType it contains the value of SAN type in PKI format
     * @param subjectAltNameDataType it contains the value of SAN in NSCS format
     * @return SubjectAltNameFormat in NSCS format or default value if translation fails
     */
    public static SubjectAltNameFormat convertSubjectAltNameFieldTypeToNscsFormat(
            final SubjectAltNameFieldType subjectAltNameFieldType,
            final BaseSubjectAltNameDataType subjectAltNameDataType) {
        SubjectAltNameFormat subjectAltNameFormat = SubjectAltNameFormat.NONE;
        switch(subjectAltNameFieldType) {
            case DNS_NAME:
                subjectAltNameFormat = SubjectAltNameFormat.FQDN;
                break;
            case RFC822_NAME:
                subjectAltNameFormat = SubjectAltNameFormat.RFC822_NAME;
                break;
            case IP_ADDRESS:
                if (subjectAltNameDataType.getClass().equals(SubjectAltNameStringType.class)) {
                    final String subjectAltNameValue = subjectAltNameDataType.toString();
                    if (NscsCommonValidator.getInstance().isValidIPv6Address(subjectAltNameValue)) {
                        subjectAltNameFormat = SubjectAltNameFormat.IPV6;
                    } else if (NscsCommonValidator.getInstance().isValidIPv4Address(subjectAltNameValue)) {
                        subjectAltNameFormat = SubjectAltNameFormat.IPV4;
                    } else {
                        logger.error("The subjectAltNameValue format [{}] is different from the expected one.", subjectAltNameValue);
                    }
                } else {
                    logger.warn("The subjectAltNameDataType [{}] is not supported yet.", subjectAltNameDataType);
                }
                break;
            default:
                break;
        }

        return subjectAltNameFormat;
    }

    /**
     * @param algorithmName (may be null or empty, RSA will be applied)
     * @param keySize
     * @return AlgorithmKeys in NSCS format, depending on algorithmName and keySize. Return null if no match
     */
    public static AlgorithmKeys convertKeySizetoNscsFormat(final String algorithmName, final int keySize) {
        AlgorithmKeys algorithmKeys = null;
        String algorithmNameToFind = "";

        if(algorithmName == null || algorithmName.isEmpty()) {
            algorithmNameToFind = "RSA";
        }
        else {
            algorithmNameToFind = algorithmName;
        }
        for (final AlgorithmKeys algo : AlgorithmKeys.values()) {
            if (algo.getAlgorithm().equals(algorithmNameToFind.toUpperCase()) && algo.getKeySize() == keySize) {
                algorithmKeys = algo;
                break;
            }
        }

        return algorithmKeys;
    }

    /**
     * This method returns result of digest value as byte array. Digest value is
     * generated by using specified algorithm on encoded data
     *
     * @param algorithm
     *            name of the algorithm used to generate MessageDigest
     *
     * @param encodedData
     *            for which need to do MessageDigest
     *
     * @return the array of bytes for the resulting hash value
     *
     * @throws NoSuchAlgorithmException
     *             if no Provider supports a MessageDigestSpi implementation for
     *             the specified algorithm.
     */
    public static byte[] generateMessageDigest(final DigestAlgorithm algorithm,
            final byte[] encodedData) throws NoSuchAlgorithmException {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(algorithm.getStandardDigestAlgorithmValue());
            messageDigest.update(encodedData);
        } catch (final NoSuchAlgorithmException e) {
            //			LOGGER.error("Caught exception while getting Message Digest", e);
            throw new NoSuchAlgorithmException(e.getMessage());
        }
        return messageDigest.digest();

    }

    public static CppSecurityServiceBean.KeyLength convertAlgorithmKeysToKeyLength(final AlgorithmKeys algo) {
        CppSecurityServiceBean.KeyLength keyLength = null;
        if (algo != null)
            keyLength = keyLengthMap.get(algo);
        return (keyLength != null) ? keyLength : CppSecurityServiceBean.KeyLength.RSA1024;
    }

    public static AlgorithmKeys convertKeyLengthToAlgorithmKeys(final CppSecurityServiceBean.KeyLength keyLen) {
        AlgorithmKeys algo = null;
        if (keyLen != null)
            algo = algoKeysMap.get(keyLen);
        return (algo != null) ? algo : AlgorithmKeys.RSA_1024;
    }

    public static NodeEntityCategory convertTrustCategoryToNodeCategory(final TrustedCertCategory trustCategory) {
        switch (trustCategory) {
            case CORBA_PEERS:
            case SYSLOG_SERVERS:
            case LOCAL_AA_DB_FILE_SIGNERS:
                return NodeEntityCategory.OAM;
            case IPSEC:
                return NodeEntityCategory.IPSEC;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static NodeEntityCategory convertCertificateTypeToNodeCategory(final CertificateType certType)
            throws IllegalArgumentException {
        switch (certType) {
            case OAM:
                return NodeEntityCategory.OAM;
            case IPSEC:
                return NodeEntityCategory.IPSEC;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static String getEntityNameFromFdn(final NodeEntityCategory category, 
            final String nodeFdn) {
        String entityName = null;
        if (nodeFdn != null) {
            final String suffix = "-" + category.toString();
            if (nodeFdn.endsWith(suffix) && !nodeFdn.contains("="))
                entityName = nodeFdn;  // Already an entity name
            else {
                final NodeReference nodeRef = new NodeRef(nodeFdn);
                entityName = nodeRef.getName() + suffix;
            }
        }
        return entityName;
    }

    /**
     * This method creates an Entity object used for Nscs operation and not to be persisted in PKI
     *
     * @param subjectName
     *            subjectName to be used for entity creation
     * @param subjectAltName
     *            subjectAltName to be used for entity creation
     * @param subjectAltNameType
     *            subjectAltNameType to be used for entity creation
     * @return Entity Object
     */
    public static Entity createEntity(final String subjectName, final String subjectAltName, final String subjectAltNameType) {

        final Entity entity = new Entity();

        final EntityInfo entityInfo = new EntityInfo();

        final Subject subject = new Subject().fromASN1String(subjectName);
        entityInfo.setSubject(subject);

        final SubjectAltNameField subjectAltNameField = new SubjectAltNameField();
        final SubjectAltNameString subjectAltNameValue = new SubjectAltNameString();
        subjectAltNameValue.setValue(subjectAltName);

        switch (subjectAltNameType) {
        case "IPV6":
        case "IPV4":
            subjectAltNameField.setType(SubjectAltNameFieldType.fromName("IP_ADDRESS"));
            break;
        case "FQDN":
            subjectAltNameField.setType(SubjectAltNameFieldType.fromName("DNS_NAME"));
            break;
        case "RFC822_NAME":
            subjectAltNameField.setType(SubjectAltNameFieldType.fromName("RFC822_NAME"));
            break;
        default:
            subjectAltNameField.setType(SubjectAltNameFieldType.fromName(subjectAltNameType));
            break;
        }

        subjectAltNameField.setValue(subjectAltNameValue);
        final List<SubjectAltNameField> subjectAltNameFieldList = new ArrayList<>();
        subjectAltNameFieldList.add(subjectAltNameField);

        final SubjectAltName subjAltName = new SubjectAltName();
        subjAltName.setSubjectAltNameFields(subjectAltNameFieldList);
        entityInfo.setSubjectAltName(subjAltName);

        entity.setEntityInfo(entityInfo);
        return entity;
    }

    /**
     * This method convert deprecated ECDSA_XXX Key Names new ECDSA_SECP_XXX_R1 format
     *
     * @param nodeCredentialKeyInfo
     *            keyInfo Name
     * @return ECDSA SECP Format Key Algorithm name
     */
    public static String convertAlgorithmNamesToNodeSupportedFormat(String nodeCredentialKeyInfo) {

        if (nodeCredentialKeyInfo != null) {
            if (nodeCredentialKeyInfo.equals(Constants.ECDSA_256)) {
                return Constants.ECDSA_SECP_256_R1;
            } else if (nodeCredentialKeyInfo.equals(Constants.ECDSA_384)) {
                return Constants.ECDSA_SECP_384_R1;
            } else if (nodeCredentialKeyInfo.equals(Constants.ECDSA_521)) {
                return Constants.ECDSA_SECP_521_R1;
            }

        }
        return nodeCredentialKeyInfo;
    }
}
