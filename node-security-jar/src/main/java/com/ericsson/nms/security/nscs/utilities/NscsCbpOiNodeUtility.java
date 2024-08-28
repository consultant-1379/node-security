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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.inject.Inject;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameEdiPartyType;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;

public class NscsCbpOiNodeUtility {

    private static final String SUBJECT_ALT_NAME_WILDCARD_VALUE = "?";

    @Inject
    private Logger logger;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    /**
     * Gets the list of names of the trust category MOs for the given node reference and trust category type.
     * 
     * @param normalizableNodeRef
     *            the node reference.
     * @param trustCategory
     *            the trust category type.
     * @return the list of names of the trust category MOs.
     */
    public List<String> getTrustCategoryNames(final NormalizableNodeReference normalizableNodeRef, final String trustCategory) {
        return Arrays.asList(getTrustCategoryName(normalizableNodeRef, trustCategory),
                getEnrollmentTrustCategoryName(normalizableNodeRef, trustCategory));
    }

    /**
     * Gets the name of the trust category MO for the given node reference and trust category type.
     * 
     * @param normalizableNodeRef
     *            the node reference.
     * @param trustCategory
     *            the trust category type.
     * @return the name of the trust category MO.
     */
    public String getTrustCategoryName(final NormalizableNodeReference normalizableNodeRef, final String trustCategory) {
        Map<String, String> moDefaultNames = nscsCapabilityModelService.getComEcimDefaultTrustCategoryIds(normalizableNodeRef);
        return moDefaultNames.get(trustCategory);
    }

    /**
     * Gets the name of the enrollment trust category MO for the given node reference and trust category.
     * 
     * @param normalizableNodeRef
     *            the node reference.
     * @param trustCategory
     *            the trust category.
     * @return the name of the enrollment trust category.
     */
    public String getEnrollmentTrustCategoryName(final NormalizableNodeReference normalizableNodeRef, final String trustCategory) {
        Map<String, String> moDefaultNames = nscsCapabilityModelService.getDefaultEnrollmentCaTrustCategoryId(normalizableNodeRef);
        return moDefaultNames.get(trustCategory);
    }

    /**
     * Gets the name of the node credential MO for the given node reference and certificate type.
     * 
     * @param normalizableNodeRef
     *            the node reference.
     * @param certificateType
     *            the certificate type.
     * @return the name of the node credential.
     */
    public String getNodeCredentialName(final NormalizableNodeReference normalizableNodeRef, final String certificateType) {
        Map<String, String> moDefaultNames = nscsCapabilityModelService.getComEcimDefaultNodeCredentialIds(normalizableNodeRef);
        return moDefaultNames.get(certificateType);
    }

    /**
     * Gets Certificate in x509 Format.
     *
     * @param certificates
     *            the input string with the certificate.
     * @return X509Certificate
     *            the certificate in x509 format or null.
     * @throws CertificateException
     *             Certificate parsing errors.
     */
    public X509Certificate convertToX509Cert(final String certificates) throws CertificateException {
        X509Certificate certificate = null;
        try {
            if (certificates != null && !certificates.trim().isEmpty()) {
                final List<X509Certificate> x509Certificates = generateX509Certificates(certificates);
                final Iterator<X509Certificate> it = x509Certificates.iterator();
                while (it.hasNext()) {
                    certificate = it.next();
                }
            }
        } catch (CertificateException e) {
            throw new CertificateException(e);
        }
        return certificate;
    }

    /**
     * Gets node credential X509 certificate from the given string as read from node.
     *
     * @param certificates
     *            the input string with the certificates.
     * @return the node credential X509 certificate or null.
     */
    public X509Certificate getNodeCredentialX509Certificate(final String certificates) {
        if (certificates == null || certificates.trim().isEmpty()) {
            logger.error("Invalid cert [{}]", certificates);
            return null;
        }
        X509Certificate certificate = null;
        try {
            final List<X509Certificate> x509Certificates = generateX509Certificates(certificates);
            final Iterator<X509Certificate> it = x509Certificates.iterator();
            if (it.hasNext()) {
                certificate = it.next();
            }
        } catch (final CertificateException e) {
            final String errorMsg = String.format("Parsing error for cert [%s]", certificates);
            logger.error(errorMsg, e);
        }
        return certificate;
    }

    /**
     * 
     * Gets a (possibly empty) ordered list of X.509 certificates from the given string as read from node.
     * 
     * If no certificates are present, an empty collection is returned.
     * 
     * @param certificates
     *            the input string with the certificates.
     * @return the ordered collection of X509 certificates or empty collection.
     * @throws CertificateException
     *             on parsing errors.
     */
    private List<X509Certificate> generateX509Certificates(final String certificates) throws CertificateException {
        if (certificates == null || certificates.trim().isEmpty()) {
            return Collections.emptyList();
        }

        final List<X509Certificate> x509Certificates = new LinkedList<>();
        try {
            final String certificateBegin = "-----BEGIN CERTIFICATE-----\n";
            final String certificateEnd = "\n-----END CERTIFICATE-----";
            final String pkcs7Cert = certificateBegin + certificates + certificateEnd;
            final InputStream targetPkcs7Stream = new ByteArrayInputStream(pkcs7Cert.getBytes(StandardCharsets.UTF_8));
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            final Collection<? extends Certificate> c = cf.generateCertificates(targetPkcs7Stream);
            final Iterator<? extends Certificate> i = c.iterator();
            while (i.hasNext()) {
                x509Certificates.add((X509Certificate) i.next());
            }
        } catch (final CertificateException e) {
            throw new CertificateException(e);
        }
        return x509Certificates;
    }

    /**
     * Gets subject DN from a given X509 certificate.
     * 
     * @param x509Certificate
     *            the X509 certificate.
     * @return the subject DN or null.
     */
    public String getSubject(final X509Certificate x509Certificate) {
        if (x509Certificate == null) {
            return null;
        }
        return x509Certificate.getSubjectX500Principal() != null ? x509Certificate.getSubjectX500Principal().getName() : null;
    }

    /**
     * Gets issuer DN from a given X509 certificate.
     * 
     * @param x509Certificate
     *            the X509 certificate.
     * @return the issuer DN or null.
     */
    public String getIssuer(final X509Certificate x509Certificate) {
        if (x509Certificate == null) {
            return null;
        }
        return x509Certificate.getIssuerX500Principal() != null ? x509Certificate.getIssuerX500Principal().getName() : null;
    }

    /**
     * Gets serial number from a given X509 certificate.
     * 
     * @param x509Certificate
     *            the X509 certificate.
     * @return the serial number or null.
     */
    public BigInteger getSerialNumber(final X509Certificate x509Certificate) {
        if (x509Certificate == null) {
            return null;
        }
        return x509Certificate.getSerialNumber();
    }

    /**
     * Gets the algorithm to be used when generating the asymmetric key from enrollment info.
     * 
     * @param enrollmentInfo
     *            the enrollment info.
     * @return the algorithm in node format or null if invalid key size in enrollment info.
     */
    public String getAlgorithmFromEnrollmentInfo(final ScepEnrollmentInfo enrollmentInfo) {
        
        String algorithm = null;
        if (enrollmentInfo == null) {
            logger.error("Illegal parameters : enrollmentInfo [{}]", enrollmentInfo);
        } else {
            final String keySize = enrollmentInfo.getKeySize();
            final CbpOiAlgorithm cbpOiAlgorithm = CbpOiAlgorithm.fromEnrollmentInfoKeySize(keySize);
            if (cbpOiAlgorithm != null) {
                algorithm = cbpOiAlgorithm.getNodeAlgorithm();
            } else {
                logger.error("Invalid key size [{}] in enrollment info", keySize);
            }
        }
        logger.debug("Returns algorithm [{}]", algorithm);
        return algorithm;
    }

    /**
     * Gets the subject alternative names from enrollment info.
     * 
     * The enrollment info contains just one subject alternative name while the node can have multiple SAN space-separated and the format of the
     * string for any SAN is <type>:<value>, where <type> is either 'IP' (for IP address) or 'DNS' (for FQDN). For 'IP' type the <value> is an IPv4
     * address in dotted decimal notation, or an IPv6 address in colon decimal notation. For 'DNS' type the <value> is an FQDN.
     * 
     * Examples: DNS:someserialnumber.ericsson.com IP:145.34.23.123 IP:2001:DB8:8:800:200C:417A
     * 
     * @param enrollmentInfo
     *            the enrollment info.
     * @return the subject alternative names string or null.
     */
    public String getSubjectAltNamesFromEnrollmentInfo(final ScepEnrollmentInfo enrollmentInfo) {

        String cbpOiSubjectAltName = null;
        if (enrollmentInfo == null) {
            logger.error("Illegal parameters : enrollmentInfo [{}]", enrollmentInfo);
        } else {
            cbpOiSubjectAltName = getSubjectAltNameFromFormatAndData(enrollmentInfo.getSubjectAltNameType(), enrollmentInfo.getSubjectAltName());
        }
        logger.debug("Returns cbpOiSubjectAltName [{}]", cbpOiSubjectAltName);
        return cbpOiSubjectAltName;
    }

    /**
     * Gets the subject alternative names as a string in node format from a list of subject alternative name parameters.
     * 
     * The node can have multiple SAN space-separated and the format of the string for any SAN is <type>:<value>, where <type> is either 'IP' (for IP
     * address) or 'DNS' (for FQDN). For 'IP' type the <value> is an IPv4 address in dotted decimal notation, or an IPv6 address in colon decimal
     * notation. For 'DNS' type the <value> is an FQDN.
     * 
     * Examples: DNS:someserialnumber.ericsson.com IP:145.34.23.123 IP:2001:DB8:8:800:200C:417A
     * 
     * @param subjectAlternativeNameParams
     *            the list of subject alternative name parameters.
     * @return the subject alternative names string or null.
     */
    public String getSubjectAltNamesFromParamsList(final List<SubjectAltNameParam> subjectAlternativeNameParams) {

        String cbpOiSubjectAltNames = null;
        if (subjectAlternativeNameParams == null || subjectAlternativeNameParams.isEmpty()) {
            logger.error("Illegal parameters : subjectAlternativeNameParams [{}]", subjectAlternativeNameParams);
        } else {
            final StringJoiner joiner = new StringJoiner(" ");
            for (final SubjectAltNameParam subjectAltNameParam : subjectAlternativeNameParams) {
                final String cbpOiSubjectAltName = getSubjectAltNameFromParams(subjectAltNameParam);
                if (cbpOiSubjectAltName != null) {
                    joiner.add(cbpOiSubjectAltName);
                } else {
                    logger.error("Null cbpOiSubjectAltName for subjectAltNameParam [{}]", subjectAltNameParam);
                }
            }
            cbpOiSubjectAltNames = joiner.toString();
        }
        logger.debug("Returns cbpOiSubjectAltNames [{}]", cbpOiSubjectAltNames);
        return cbpOiSubjectAltNames;
    }

    /**
     * Gets the subject alternative name as a string in node format from given subject alternative name parameters.
     * 
     * @param subjectAltNameParam
     *            the subject alternative name parameters.
     * @return the subject alternative name string or null.
     */
    public String getSubjectAltNameFromParams(final SubjectAltNameParam subjectAltNameParam) {

        String cbpOiSubjectAltName = null;
        if (subjectAltNameParam == null) {
            logger.error("Illegal parameters : subjectAltNameParam [{}]", subjectAltNameParam);
        } else {
            cbpOiSubjectAltName = getSubjectAltNameFromFormatAndData(subjectAltNameParam.getSubjectAltNameFormat(),
                    subjectAltNameParam.getSubjectAltNameData());
        }
        logger.debug("Returns cbpOiSubjectAltName [{}]", cbpOiSubjectAltName);
        return cbpOiSubjectAltName;
    }

    /**
     * Gets the subject alternative name as a string in node format from a given subject alternative name format and data.
     * 
     * The CBP-OI node can have multiple SAN space-separated and the format of the string for any SAN is <type>:<value>, where <type> is either 'IP'
     * (for IP address) or 'DNS' (for FQDN). For 'IP' type the <value> is an IPv4 address in dotted decimal notation, or an IPv6 address in colon
     * decimal notation. For 'DNS' type the <value> is an FQDN.
     * 
     * Examples: DNS:someserialnumber.ericsson.com IP:145.34.23.123 IP:2001:DB8:8:800:200C:417A
     * 
     * @param subjectAltNameFormat
     *            the subject alternative name format.
     * @param subjectAltNameData
     *            the subject alternative name data.
     * @return the subject alternative name string or null.
     */
    public String getSubjectAltNameFromFormatAndData(final SubjectAltNameFormat subjectAltNameFormat,
            final BaseSubjectAltNameDataType subjectAltNameData) {

        String cbpOiSubjectAltName = null;

        if (subjectAltNameFormat == null || subjectAltNameData == null) {
            logger.error("Illegal parameters : subjectAltNameFormat [{}] subjectAltNameData [{}]", subjectAltNameFormat, subjectAltNameData);
        } else {
            if (subjectAltNameData instanceof SubjectAltNameStringType) {
                cbpOiSubjectAltName = getSubjectAltNameFromFormatAndValue(subjectAltNameFormat, (SubjectAltNameStringType) subjectAltNameData);
            } else if (subjectAltNameData instanceof SubjectAltNameEdiPartyType) {
                logger.error("Not yet supported subjectAltNameData of type SubjectAltNameEdiPartyType");
            } else {
                logger.error("Unsupported subjectAltNameData type");
            }
            logger.debug("Returns cbpOiSubjectAltName [{}]", cbpOiSubjectAltName);
        }
        return cbpOiSubjectAltName;
    }

    /**
     * Gets the subject alternative name as a string in node format from a given subject alternative name format and value.
     * 
     * @param subjectAltNameFormat
     *            the subject alternative name format.
     * @param subjectAltNameValue
     *            the subject alternative name value.
     * @return the subject alternative name string or null.
     */
    private String getSubjectAltNameFromFormatAndValue(final SubjectAltNameFormat subjectAltNameFormat,
            final SubjectAltNameStringType subjectAltNameValue) {

        String cbpOiSubjectAltName = null;
        final String subjectAltNameValueString = subjectAltNameValue.getValue();
        if (!SUBJECT_ALT_NAME_WILDCARD_VALUE.equals(subjectAltNameValueString)) {
            switch (subjectAltNameFormat) {
            case FQDN:
                cbpOiSubjectAltName = getSubjectAltNameFromDns(subjectAltNameValueString);
                break;
            case IPV4:
                cbpOiSubjectAltName = getSubjectAltNameFromIPv4Address(subjectAltNameValueString);
                break;
            case IPV6:
                cbpOiSubjectAltName = getSubjectAltNameFromIPv6Address(subjectAltNameValueString);
                break;
            default:
                logger.error("Unsupported subjectAltNameFormat [{}]", subjectAltNameFormat);
                break;
            }
        } else {
            logger.error("Not yet supported subjectAltNameData wildcard [{}]", subjectAltNameValueString);
        }
        return cbpOiSubjectAltName;
    }

    /**
     * Gets the subject alternative name as a string in node format from a given FQDN/DNS value.
     * 
     * @param dns
     *            the DNS value.
     * @return the subject alternative name string or null.
     */
    private String getSubjectAltNameFromDns(final String dns) {

        String cbpOiSubjectAltName = null;
        if (dns != null) {
            cbpOiSubjectAltName = "DNS:" + dns;
        } else {
            logger.error("Invalid FQDN/DNS [{}]", dns);
        }
        return cbpOiSubjectAltName;
    }

    /**
     * Gets the subject alternative name as a string in node format from a given IPv4 address.
     * 
     * @param ipv4Address
     *            the IPv4 address.
     * @return the subject alternative name string or null.
     */
    private String getSubjectAltNameFromIPv4Address(final String ipv4Address) {

        String cbpOiSubjectAltName = null;
        if (NscsCommonValidator.getInstance().isValidIPv4Address(ipv4Address)) {
            cbpOiSubjectAltName = "IP:" + ipv4Address;
        } else {
            logger.error("Invalid IPv4 address [{}]", ipv4Address);
        }
        return cbpOiSubjectAltName;
    }

    /**
     * Gets the subject alternative name as a string in node format from a given IPv6 address.
     * 
     * @param ipv6Address
     *            the IPv6 address.
     * @return the subject alternative name string or null.
     */
    private String getSubjectAltNameFromIPv6Address(final String ipv6Address) {

        String cbpOiSubjectAltName = null;
        if (NscsCommonValidator.getInstance().isValidIPv6Address(ipv6Address)) {
            cbpOiSubjectAltName = "IP:" + ipv6Address;
        } else {
            logger.error("Invalid IPv6 address [{}]", ipv6Address);
        }
        return cbpOiSubjectAltName;
    }

     /* Get Certificate in base64 format.
     *
     * @param X509Certificate
     *            X509 Certificate .
     * @return the base64 certificate in String format.
     * @throws IOException
     */
    public String convertToBase64String(final X509Certificate x509Certificate) throws IOException {
        final StringWriter writer = new StringWriter();
        final JcaPEMWriter pemWriter = new JcaPEMWriter(writer);
        pemWriter.writeObject(x509Certificate);
        pemWriter.flush();
        pemWriter.close();
        return Base64.getEncoder().encodeToString(writer.toString().getBytes(StandardCharsets.UTF_8));
    }
}

