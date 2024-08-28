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
package com.ericsson.nms.security.nscs.util;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x500.X500Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.nscs.handler.command.utility.NscsNameMultipleValueResponseBuilder;
import com.ericsson.nms.security.nscs.utilities.Constants;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.OamTrustCategory;


/**
 * Utility class that holds a small set of properties for comparison between certs.
 */
public class CertDetails {

    private static final Logger logger = LoggerFactory.getLogger(CertDetails.class);
    private final String subject;
    private BigInteger serial;
    private final String issuer;
    private OamTrustCategory category;
    private final static Map<String, String> subjectOidMap = new HashMap<String, String>();

    protected static final String CERTIFICATE_SERIAL = "serialNumber";
    protected static final String CERTIFICATE_ISSUER = "issuer";
    protected static final String CERTIFICATE_SUBJECT = "subject";
    protected static final String CERTFICATE_CATEGORY="category";

    static {
        subjectOidMap.put("DN", "2.5.4.46");
        subjectOidMap.put("DNQ", "2.5.4.46");
        subjectOidMap.put("DNQUALIFIER", "2.5.4.46");
        subjectOidMap.put("SN", "2.5.4.4");
        subjectOidMap.put("SURNAME", "2.5.4.4");
        subjectOidMap.put("T", "2.5.4.12");
        subjectOidMap.put("TITLE", "2.5.4.12");
        subjectOidMap.put("GIVENNAME", "2.5.4.42");
        subjectOidMap.put("GN", "2.5.4.42");

    }

    private final static Map<String, String> bouncyCastleSubjectOidMap = new HashMap<String, String>();

    static {
        bouncyCastleSubjectOidMap.put("DN", "2.5.4.46");
        bouncyCastleSubjectOidMap.put("DNQ", "2.5.4.46");
        bouncyCastleSubjectOidMap.put("DNQUALIFIER", "2.5.4.46");
        /**
         * SN for old implementation of BouncyCastle is serialNumber, SN for standard is SURNAME! Due to accepted limitation, surname cannot be used in PKI, so SN, if present in subject name shall
         * always be interpreted as serialNumber (coming from PKI)
         */
        bouncyCastleSubjectOidMap.put("SN", "2.5.4.5");
        bouncyCastleSubjectOidMap.put("T", "2.5.4.12");
        bouncyCastleSubjectOidMap.put("TITLE", "2.5.4.12");
        bouncyCastleSubjectOidMap.put("GIVENNAME", "2.5.4.42");
        bouncyCastleSubjectOidMap.put("GN", "2.5.4.42");
    }

    public CertDetails(final java.security.cert.X509Certificate cert) {
        this.subject = cert.getSubjectDN().toString();
        // TODO
        // Denigrated method
        // this.subject = cert.getSubjectX500Principal().getName();
        this.issuer = cert.getIssuerDN().toString();
        this.serial = cert.getSerialNumber();
    }

    public CertDetails(final java.security.cert.X509Certificate cert, final OamTrustCategory category) {
        this.subject = cert.getSubjectDN().toString();
        // TODO
        // Denigrated method
        // this.subject = cert.getSubjectX500Principal().getName();
        this.issuer = cert.getIssuerDN().toString();
        this.serial = cert.getSerialNumber();
        this.setCategory(category);
    }

    public CertDetails(final Map<String, Object> certStr) {
        this.subject = (String) certStr.get(CERTIFICATE_SUBJECT);
        this.issuer = (String) certStr.get(CERTIFICATE_ISSUER);
        final String sn = (String) certStr.get(CERTIFICATE_SERIAL);
        this.serial = convertSerialNumberToDecimalFormat(sn);
        if(certStr.get(CERTFICATE_CATEGORY)!=null && certStr.get(CERTFICATE_CATEGORY) != NscsNameMultipleValueResponseBuilder.NOT_APPLICABLE){
        	this.setCategory(OamTrustCategory.fromName((String)certStr.get(CERTFICATE_CATEGORY)));
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((issuer == null) ? 0 : issuer.hashCode());
        result = prime * result + ((serial == null) ? 0 : serial.hashCode());
        result = prime * result + ((subject == null) ? 0 : subject.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CertDetails)) {
            return false;
        }
        final CertDetails other = (CertDetails) obj;
        if (issuer == null) {
            if (other.issuer != null) {
                return false;
            }
        } else if (!matchesDN(issuer, other.issuer) && !matchesDN(getJavaDN(issuer), getJavaDN(other.issuer))) {
            return false;
        }
        if (serial == null) {
            if (other.serial != null) {
                return false;
            }
        } else if (!serial.equals(other.serial)) {
            return false;
        }
        if (subject == null) {
            if (other.subject != null) {
                return false;
            }
        } else if (!matchesDN(subject, other.subject) && !matchesDN(getJavaDN(subject), getJavaDN(other.subject))) {
            return false;
        }
        return true;
    }

    public boolean isEqual(final CertSpec obj) {

        if (obj == null) {
            return false;
        }
        final X509Certificate other = obj.getCertHolder();
        if (issuer == null) {
            if (other.getIssuerDN() != null) {
                return false;
            }
        } else if (!matchesDN(getJavaDN(issuer), other.getIssuerDN().toString())) {
            return false;
        }
        if (serial == null) {
            if (other.getSerialNumber() != null) {
                return false;
            }
        } else if (!serial.equals(other.getSerialNumber())) {
            return false;
        }
        if (subject == null) {
            if (other.getSubjectDN().toString() != null) {
                return false;
            }
        } else if (!matchesDN(getJavaDN(subject), other.getSubjectDN().toString())) {
            return false;
        }
        return true;
    }

    /**
     * Check whether the object is invalid or not.
     *
     * @return
     */
    public boolean isInvalid() {
        if (this.issuer == null || this.issuer.isEmpty()) {
            return true;
        }
        if (this.serial == null) {
            return true;
        }
        if (this.subject == null || this.subject.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Check whether the object is unavailable or not.
     *
     * @return
     */
    public boolean isNotAvailable() {
        if (this.issuer == null || this.issuer.isEmpty()) {
            return true;
        }
        if (this.serial == null) {
            return true;
        }
        if (this.subject == null || this.subject.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Check if two DN as {@link String} matches. When a DN is converted to {@link String} we are not sure of the sequence of the 'pieces' (like CN, O, OU, etc). This method ignores this sequence
     * checks if this two DN as {@link String} are the same.
     *
     * @param dn1
     *            first DN as {@link String}.
     * @param dn2
     *            second DN as {@link String}.
     * @return if both matches ignoring the sequence of the RDN in the {@link String}.
     */
    public static boolean matchesDN(final String dn1, final String dn2) {
        if (dn1 == null || dn2 == null) {
            throw new IllegalArgumentException(String.format("Invalid DN : [%s] : [%s]", dn1, dn2));
        }
        try {
            String nameDn1 = getOidDN(dn1);
            String nameDn2 = getOidDN(dn2);
            final List<Rdn> rdn1 = new LdapName(nameDn1).getRdns();
            final List<Rdn> rdn2 = new LdapName(nameDn2).getRdns();
            if (rdn1.size() != rdn2.size()) {
                return false;
            }
            return rdn1.containsAll(rdn2);
        } catch (InvalidNameException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Check if first "partial" DN as {@link String} partially matches the second "full" DN as {@link String}. When a DN is converted to {@link String} we are not sure of the sequence of the 'pieces'
     * (like CN, O, OU, etc). This method ignores this sequence and checks if all fields of the first "partial" DN are contained in the second "full" DN with the same value.
     *
     * @param partialDn
     *            first "partial" DN as {@link String}.
     * @param fullDn
     *            second "full" DN as {@link String}.
     * @return if first DN is "contained" in the second DN ignoring the sequence of the RDN in the {@link String}.
     */
    public static boolean partiallyMatchesDN(final String partialDn, final String fullDn) {
        if (partialDn == null || partialDn.isEmpty() || fullDn == null) {
            throw new IllegalArgumentException("Invalid DN : [" + partialDn + "] : [" + fullDn + "]");
        }
        try {
            String namePartialDn = getOidDN(partialDn);
            String nameFullDn = getOidDN(fullDn);
            final List<Rdn> partialRdns = new LdapName(namePartialDn).getRdns();
            final List<Rdn> fullRdns = new LdapName(nameFullDn).getRdns();
            return fullRdns.containsAll(partialRdns);
        } catch (InvalidNameException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Check if two Serial Numbers (SN) as {@link String} match. A SN can have following formats: decimal (e.g. "1851389729377754674"), hexadecimal (e.g. "19b17526585e1e32"), hexadecimal with 0x
     * prefix (e.g. "0x19b17526585e1e32"), hexadecimal using colon as delimiter (e.g. "19:b1:75:26:58:5e:1e:32").
     *
     * @param sn1
     *            first SN as {@link String}.
     * @param sn2
     *            second SN as {@link String}.
     * @return if they match.
     */
    public static boolean matchesSN(final String sn1, final String sn2) {
        BigInteger serial1 = convertSerialNumberToDecimalFormat(sn1);
        BigInteger serial2 = convertSerialNumberToDecimalFormat(sn2);
        if (serial1 == null || serial2 == null) {
            throw new IllegalArgumentException(String.format("Invalid SN : [%s] : [%s]", sn1, sn2));
        }
        return serial1.equals(serial2);
    }

    /**
     * Convert a Serial Number (SN) as {@link String} to a {@link BigInteger}. A SN can have following formats: decimal (e.g. "1851389729377754674"), hexadecimal (e.g. "19b17526585e1e32"), hexadecimal
     * with 0x prefix (e.g. "0x19b17526585e1e32"), hexadecimal using colon as delimiter (e.g. "19:b1:75:26:58:5e:1e:32"). The attempt to convert to decimal format occurs before the attempt to convert
     * to hexadecimal format, so, if the SN is a string with all digits [0..9], it is converted to a decimal even if it were actually an hexadecimal! If all conversions should fail, null is returned.
     *
     * @param sn
     * @return
     */
    public static BigInteger convertSerialNumberToDecimalFormat(final String sn) {
        if (sn != null && !sn.isEmpty()) {
            try {
                // Try decimal format
                return new BigInteger(sn);
            } catch (Exception e) {
                String hexSn = sn.replaceFirst("0[xX]", "").replaceAll(":", "");
                try {
                    // Try hexadecimal format
                    return new BigInteger(hexSn, 16);
                } catch (Exception e1) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Convert a Serial Number (SN) as {@link String} to a {@link BigInteger}. The SN is assumed to have following formats: hexadecimal (e.g. "19b17526585e1e32"), hexadecimal with 0x prefix (e.g.
     * "0x19b17526585e1e32"), hexadecimal using colon as delimiter (e.g. "19:b1:75:26:58:5e:1e:32"). If all conversions should fail, null is returned.
     *
     * @param sn
     * @return
     */
    public static BigInteger convertHexadecimalSerialNumberToDecimalFormat(final String sn) {
        if (sn != null && !sn.isEmpty()) {
            String hexSn = sn.replaceFirst("0x", "").replaceAll(":", "");
            try {
                // Try hexadecimal format
                return new BigInteger(hexSn, 16);
            } catch (Exception e1) {
            }
        }
        return null;
    }

    /**
     * Method to align certificate DN field type with RFC
     * @param distinguishedName
     * @return finalDistinguishedName
     */
    public static String alignNodeCertDNFieldNamesWithRFC(final String distinguishedName) {
       String finalDistinguishedName = distinguishedName;
       if (finalDistinguishedName != null) {
            final Pattern dcPattern = Pattern.compile("(?i)"+Constants.DOMAINCOMPONENT+"\\s*=");
            finalDistinguishedName = dcPattern.matcher(finalDistinguishedName).replaceAll(Constants.DC+"=");
            if (!distinguishedName.equals(finalDistinguishedName)) {
                logger.warn("Node provided cert subject/issuer DN field type  is not aligned with RFC. Hence converting  from {} to {} ",
                 distinguishedName, finalDistinguishedName );
            }
        }
        return finalDistinguishedName;
    }

    /**
     * Method to  match the certificate DNs fields after aligning the DN field types with RFC
     * @param dn1
     *            first DN as {@link String}.
     * @param dn2
     *            second DN as {@link String}.
     * @return if both matches ignoring the sequence of the RDN in the {@link String}.
     */
    public static boolean matchesNotAlignedToRfcDN(final String dn1, final String dn2) {
        final String normalizedDn1 = alignNodeCertDNFieldNamesWithRFC(dn1);
        final String normalizedDn2 = alignNodeCertDNFieldNamesWithRFC(dn2);
        return matchesDN(normalizedDn1, normalizedDn2);
    }

    /**
     * Method to  partially match the certificate DNs fields after aligning the DN field types with RFC
     * @param dn1
     *            first DN as {@link String}.
     * @param dn2
     *            second DN as {@link String}.
     * @return if first DN is "contained" in the second DN ignoring the sequence of the RDN in the {@link String}.
     */

    public static boolean partiallyMatchesNotAlignedToRfcDN(final String dn1, final String dn2) {
        final String normalizedDn1 = alignNodeCertDNFieldNamesWithRFC(dn1);
        final String normalizedDn2 = alignNodeCertDNFieldNamesWithRFC(dn2);
        return partiallyMatchesDN(normalizedDn1, normalizedDn2);
    }

    /**
     * @return the serial
     */
    public BigInteger getSerial() {
        return serial;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @return the issuer
     */
    public String getIssuer() {
        return issuer;
    }

    private String getJavaDN(final String subjectDN) {
        return new X500Principal(subjectDN, subjectOidMap).toString();
    }

    private static String getOidDN(final String subjectDN) {
        String x500PrincipalDn = new X500Principal(subjectDN, bouncyCastleSubjectOidMap).getName(X500Principal.RFC2253, bouncyCastleSubjectOidMap);
        return x500PrincipalDn;
    }

	/**
	 * @return the category
	 */
	public OamTrustCategory getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(final OamTrustCategory category) {
		this.category = category;
	}

    /**
     * Get the BouncyCastle X500 name for the given distinguished name (DN).
     * 
     * All unnecessary whitespace is removed, all escaped characters are maintained.
     * 
     * Before uplift to BouncyCastle 1.67 there was a known limitation about the SN attribute type: SN for BouncyCastle was serialNumber, SN for
     * standard is SURNAME! Due to accepted limitation, surname could not be used in PKI, so SN, if present in subject name was always interpreted as
     * serialNumber.
     * 
     * After uplift to BouncyCastle 1.67 such limitation has been removed so this method can be used both for DN coming from PKI and coming from node.
     * 
     * @param dn
     *            the DN string.
     * @return a formatted X500 name according to BouncyCastle format or null if an error occurs
     */
    public static String getBcX500Name(final String dn) {
        if (dn == null) {
            return null;
        }
        String bcX500Name = null;
        try {
            final X500Name x500Name = new X500Name(dn);
            bcX500Name = x500Name.toString();
        } catch (final Exception e) {
            final String errorMessage = String.format("Exception while converting [%s] to BC X500 name", dn);
            logger.error(errorMessage, e);
            return null;
        }
        return bcX500Name;
    }
}

