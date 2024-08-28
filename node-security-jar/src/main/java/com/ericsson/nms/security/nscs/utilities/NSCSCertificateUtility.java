/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
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
import java.math.BigInteger;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.ericsson.nms.security.nscs.data.ModelDefinition.CertificateContent;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TrustedCertificate;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.CAEntity;

/**
 * Utility class to handle certificate
 *
 */
public class NSCSCertificateUtility {

    private static final String TRUSTED_CERT_CERTIFICATE_CONTENT = TrustedCertificate.CERTIFICATE_CONTENT;
    private static final String CERTIFICATE_CONTENT_ISSUER = CertificateContent.ISSUER;
    private static final String CERTIFICATE_CONTENT_SERIAL_NUMBER = CertificateContent.SERIAL_NUMBER;

    /**
     * Extracts some properties from a list of {@link Map}.
     * 
     * @param certs
     *            certs properties inside {@link Map}.
     * @return details from all the certs.
     */
    public List<CertDetails> extractDetailsFromMap(final List<Map<String, Object>> certs) {
        final List<CertDetails> details = new ArrayList<>();
        if (certs != null) {
            for (final Map<String, Object> cert : certs) {
                details.add(new CertDetails(cert));
            }
        }
        return details;
    }

    /**
     * Gets from the given map the value of the given reserved-by MO attribute.
     * 
     * @param entry
     *            the map of values.
     * @param reservedByMoAttribute
     *            the name of the reserved-by MO attribute.
     * @return the value of the reserved-by MO attribute.
     */
    @SuppressWarnings("unchecked")
    public static List<String> getTrustedCertificateReservedByMoAttributeValue(final Map<String, Object> entry, final String reservedByMoAttribute) {

        List<String> reservedByMoAttributeValue = null;

        if (entry != null && !entry.isEmpty()) {
            reservedByMoAttributeValue = (List<String>) entry.get(reservedByMoAttribute);
        }
        return reservedByMoAttributeValue;
    }

    /**
     * @param entry
     * @return
     */
    @SuppressWarnings("unchecked")
    public static final BigInteger getTrustedCertificateSerialNumber(final Map<String, Object> entry) {

        BigInteger serialNumber = null;

        if (entry != null && !entry.isEmpty()) {
            Map<String, Object> certificateContent = (Map<String, Object>) entry.get(TRUSTED_CERT_CERTIFICATE_CONTENT);
            if (certificateContent != null && !certificateContent.isEmpty()) {
                String certSerialNumber = (String) certificateContent.get(CERTIFICATE_CONTENT_SERIAL_NUMBER);
                if (certSerialNumber != null && !certSerialNumber.isEmpty()) {
                    serialNumber = CertDetails.convertSerialNumberToDecimalFormat(certSerialNumber);
                }
            }
        }
        return serialNumber;
    }

    /**
     * @param entry
     * @return
     */
    @SuppressWarnings("unchecked")
    public static final String getTrustedCertificateIssuer(final Map<String, Object> entry) {

        String issuer = null;

        if (entry != null && !entry.isEmpty()) {
            Map<String, Object> certificateContent = (Map<String, Object>) entry.get(TRUSTED_CERT_CERTIFICATE_CONTENT);
            if (certificateContent != null && !certificateContent.isEmpty()) {
                issuer = (String) certificateContent.get(CERTIFICATE_CONTENT_ISSUER);
            }
        }
        return issuer;
    }

    /**
     * 
     * @param caEntity
     * @return
     */
    public static BigInteger getSerialNumber(final CAEntity caEntity) {

        BigInteger serialNumber = null;

        if (caEntity != null && caEntity.getCertificateAuthority() != null && caEntity.getCertificateAuthority().getActiveCertificate() != null) {
            String pkiSerialNumber = caEntity.getCertificateAuthority().getActiveCertificate().getSerialNumber();
            // PKI always returns serial number in hexadecimal format!
            serialNumber = CertDetails.convertHexadecimalSerialNumberToDecimalFormat(pkiSerialNumber);
        }

        return serialNumber;
    }

    /**
     * 
     * @param caEntity
     * @return
     */
    public static final String getIssuer(final CAEntity caEntity) {

        String issuer = null;

        if ((caEntity != null) && (caEntity.getCertificateAuthority() != null)) {
            if (caEntity.getCertificateAuthority().getIssuer() == null) {
                // MS9: check active certificate issuer
                String activeCertificateIssuer = null;
                if (caEntity.getCertificateAuthority().getActiveCertificate() != null) {
                    if (caEntity.getCertificateAuthority().getActiveCertificate().getIssuer() != null) {
                        if (caEntity.getCertificateAuthority().getActiveCertificate().getIssuer().getSubject() != null) {
                            activeCertificateIssuer = caEntity.getCertificateAuthority().getActiveCertificate().getIssuer().getSubject()
                                    .toASN1String();
                        }
                    }
                }
                if ((activeCertificateIssuer != null) && !activeCertificateIssuer.isEmpty()) {
                    issuer = activeCertificateIssuer;
                } else {
                    issuer = caEntity.getCertificateAuthority().getSubject().toASN1String(); // self-signed CA
                }
            } else {
                // NOT self-signed CA
                issuer = caEntity.getCertificateAuthority().getIssuer().getSubject().toASN1String();
            }
        }
        return issuer;
    }

    /**
     * @param certificateString
     *            certificate in base64 string
     * @return X509Certificate
     * @throws CertificateException
     *             thrown when improper certificate string provided which cannot be converted to X509Certificate
     */
    public static final X509Certificate prepareX509Certificate(final String certificateString) throws CertificateException {
        byte[] certBytes = DatatypeConverter.parseBase64Binary(certificateString);

        return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(certBytes));
    }
}
