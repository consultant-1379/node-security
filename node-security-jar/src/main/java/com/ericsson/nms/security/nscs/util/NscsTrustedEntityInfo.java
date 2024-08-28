package com.ericsson.nms.security.nscs.util;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import com.ericsson.oss.itpf.security.pki.common.model.certificate.CertificateStatus;
import com.ericsson.oss.itpf.security.pki.manager.model.TrustedEntityInfo;
import com.ericsson.oss.services.security.nscs.util.NscsStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Auxiliary class containing info about a trusted entity as used by NSCS.
 */
public class NscsTrustedEntityInfo implements Serializable {

    private static final long serialVersionUID = 2539989979562757893L;
    private static final Logger logger = LoggerFactory.getLogger(NscsTrustedEntityInfo.class);
    /**
     * The trusted entity name.
     */
    private String name;
    /**
     * The serial number (integer format) of the active certificate of the trusted entity.
     */
    private BigInteger serialNumber;
    /**
     * The issuer DN of the active certificate of the trusted entity.
     */
    private String issuer;
    /**
     * The TDPS (Trust Distribution Point Service) URL of the active certificate of the trusted entity.
     */
    private String tdpsUrl;
    /**
     * The X509 format of Certificate.
     */
    private X509Certificate x509Certificate;
    /**
     * The Certificate Status of Certificate.
     */
    private CertificateStatus certificateStatus;

    private List<String> crlsUri = new ArrayList<>();

    public NscsTrustedEntityInfo(final String name, final BigInteger serialNumber, final String issuer, final String tdpsUrl,
            final X509Certificate x509Certificate, final CertificateStatus certificateStatus) {
        super();
        this.name = name;
        this.serialNumber = serialNumber;
        this.issuer = issuer;
        this.tdpsUrl = tdpsUrl;
        this.certificateStatus = certificateStatus;
        this.x509Certificate = x509Certificate;
    }

    public NscsTrustedEntityInfo(final String name, final BigInteger serialNumber, final String issuer, final String tdpsUrl) {
        super();
        this.name = name;
        this.serialNumber = serialNumber;
        this.issuer = issuer;
        this.tdpsUrl = tdpsUrl;
    }

    /**
     * @return the x509Certificate
     */
    public X509Certificate getX509Certificate() {
        return x509Certificate;
    }

    /**
     * @param x509Certificate
     *            the x509Certificate to set
     */
    public void setX509Certificate(final X509Certificate x509Certificate) {
        this.x509Certificate = x509Certificate;
    }

    /**
     * @return the certificateStatus
     */
    public CertificateStatus getCertificateStatus() {
        return certificateStatus;
    }

    /**
     * @param certificateStatus
     *            the certificateStatus to set
     */
    public void setCertificateStatus(final CertificateStatus certificateStatus) {
        this.certificateStatus = certificateStatus;
    }

    public NscsTrustedEntityInfo(final TrustedEntityInfo pkiTrustedEntityInfo, final boolean isIPv6) {
        super();
        this.name = pkiTrustedEntityInfo.getEntityName();
        // PKI always returns serial number in hexadecimal format!
        this.serialNumber = CertDetails.convertHexadecimalSerialNumberToDecimalFormat(pkiTrustedEntityInfo.getCertificateSerialNumber());
        this.issuer = pkiTrustedEntityInfo.getIssuerFullDN();
        this.tdpsUrl = (isIPv6 ? pkiTrustedEntityInfo.getIpv6TrustDistributionPointURL() : pkiTrustedEntityInfo.getIpv4TrustDistributionPointURL());
        this.certificateStatus = pkiTrustedEntityInfo.getCertificateStatus();
        this.x509Certificate = pkiTrustedEntityInfo.getX509Certificate();

        buildCrlsUri(pkiTrustedEntityInfo, isIPv6);
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public BigInteger getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(final BigInteger serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(final String issuer) {
        this.issuer = issuer;
    }

    public String getTdpsUrl() {
        return tdpsUrl;
    }

    public void setTdpsUrl(final String tdpsUrl) {
        this.tdpsUrl = tdpsUrl;
    }

    public List<String> getCrlsUri() {
        return new ArrayList<>(crlsUri);
    }

    public void setCrlsUri(List<String> crlsUri) {
        this.crlsUri = new ArrayList<>(crlsUri);
    }

    private void buildCrlsUri (final TrustedEntityInfo pkiTrustedEntityInfo,
                               final boolean isIPv6) {

        /* remove spaces from input string null safe */
        String crlDnsUri = NscsStringUtils.trim(pkiTrustedEntityInfo.getDnsCrlDistributionPointURL());
        String crlIpv4Uri = NscsStringUtils.trim(pkiTrustedEntityInfo.getIpv4CrlDistributionPointURL());
        String crlIpv6Uri = NscsStringUtils.trim(pkiTrustedEntityInfo.getIpv6CrlDistributionPointURL());
        logger.info("crl Uri from pki : crlDnsUri=[{}], crlIpv4Uri=[{}], crlIpv6Uri=[{}]", crlDnsUri, crlIpv4Uri, crlIpv6Uri);

        String crlUri = (isIPv6) ? crlIpv6Uri : crlIpv4Uri;

        if (NscsStringUtils.isNotBlank(crlUri)) {
            crlsUri.add(crlUri);
        } else if (NscsStringUtils.isNotBlank(crlDnsUri)) {
            crlsUri.add(crlDnsUri);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = prime * result + ((this.serialNumber == null) ? 0 : this.serialNumber.hashCode());
        result = prime * result + ((this.issuer == null) ? 0 : this.issuer.hashCode());
        result = prime * result + ((this.tdpsUrl == null) ? 0 : this.tdpsUrl.hashCode());
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
        if (!(obj instanceof NscsTrustedEntityInfo)) {
            return false;
        }
        final NscsTrustedEntityInfo other = (NscsTrustedEntityInfo) obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        if (this.issuer == null) {
            if (other.issuer != null) {
                return false;
            }
        } else if (!this.issuer.equals(other.issuer)) {
            return false;
        }
        if (this.serialNumber == null) {
            if (other.serialNumber != null) {
                return false;
            }
        } else if (!this.serialNumber.equals(other.serialNumber)) {
            return false;
        }
        if (this.tdpsUrl == null) {
            if (other.tdpsUrl != null) {
                return false;
            }
        } else if (!this.tdpsUrl.equals(other.tdpsUrl)) {
            return false;
        }
        return true;
    }

    public String stringify() {
        final StringBuilder sb = new StringBuilder("Trusted Entity :");
        sb.append(" name [" + this.name + "]");
        sb.append(" SN [" + this.serialNumber + "]");
        sb.append(" issuer [" + this.issuer + "]");
        sb.append(" tdpsUrl [" + this.tdpsUrl + "]");
        sb.append("crls Uri [" + this.crlsUri.toString() + "]");
        return sb.toString();
    }

}
