/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
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
import java.util.Objects;

public class NscsCbpOiTrustedEntityInfo extends NscsTrustedEntityInfo {

    private static final long serialVersionUID = 7062961965340311870L;

    /**
     * The trusted certificate in Base64 PEM format.
     */
    private String base64PemCertificate;

    /**
     * The OAM trust category name relative to  the trusted certificate (oamTrustCategory / oamCmpCaTrustCategory)
     */
    private String trustCategoryName;

    /**
     * The OAM trust category FDN relative to the trusted certificate
     */
    private String trustCategoryFdn;

    /**
     *  TRUE if certificate has been installed on EOI trust store
     */
    private Boolean installed;

    public NscsCbpOiTrustedEntityInfo(final String name, final BigInteger serialNumber,
                            final String issuer, final String pemCertificate) {
        super(name, serialNumber, issuer, "");
        this.base64PemCertificate = pemCertificate;
        this.trustCategoryName = "";
        this.trustCategoryFdn = "";
        this.installed = false;
    }

    public NscsCbpOiTrustedEntityInfo(final NscsCbpOiTrustedEntityInfo nscsCbpOiTrustedEntityInfo) {
        super(nscsCbpOiTrustedEntityInfo.getName(), nscsCbpOiTrustedEntityInfo.getSerialNumber(), nscsCbpOiTrustedEntityInfo.getIssuer(),
                nscsCbpOiTrustedEntityInfo.getTdpsUrl());
        this.base64PemCertificate = nscsCbpOiTrustedEntityInfo.getBase64PemCertificate();
        this.trustCategoryName = nscsCbpOiTrustedEntityInfo.getTrustCategoryName();
        this.trustCategoryFdn = nscsCbpOiTrustedEntityInfo.getTrustCategoryFdn();
        this.installed = nscsCbpOiTrustedEntityInfo.getInstalled();
    }

    public String getBase64PemCertificate() {
        return base64PemCertificate;
    }

    public void setBase64PemCertificate(String base64PemCertificate) {
        this.base64PemCertificate = base64PemCertificate;
    }

    public String getTrustCategoryName() {
        return trustCategoryName;
    }

    public void setTrustCategoryName(final String trustCategoryName) {
        this.trustCategoryName = trustCategoryName;
    }

    public String getTrustCategoryFdn() {
        return trustCategoryFdn;
    }

    public void setTrustCategoryFdn(final String trustCategoryFdn) {
        this.trustCategoryFdn = trustCategoryFdn;
    }

    public Boolean getInstalled() {
        return installed;
    }

    public void setInstalled(Boolean installed) {
        this.installed = installed;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.base64PemCertificate);
        hash = 23 * hash + Objects.hashCode(this.trustCategoryName);
        hash = 23 * hash + Objects.hashCode(this.installed);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }
        final NscsCbpOiTrustedEntityInfo other = (NscsCbpOiTrustedEntityInfo) obj;
        if (!Objects.equals(this.base64PemCertificate, other.base64PemCertificate)) {
            return false;
        }
        if (!Objects.equals(this.trustCategoryName, other.trustCategoryName)) {
            return false;
        }
        return Objects.equals(this.installed, other.installed);
    }

    @Override
    public String stringify() {
        final StringBuilder sb = new StringBuilder(super.stringify());
        sb.append(" Trust Category name [").append(this.trustCategoryName).append("]");
        sb.append(" Trust Category FDN [").append(this.trustCategoryFdn).append("]");
        sb.append(" PEM Certificate [").append(this.base64PemCertificate.substring(0, 
                    Math.min(this.base64PemCertificate.length(), 50))).append(" ...]");
        return sb.toString();
    }
}
