/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.iscf;

import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SecurityDataResponse implements Serializable {

    private static final long serialVersionUID = -8673346157785479392L;

    private final List<SecurityDataContainer> securityDataContainers;

    private final List<TrustedCertificateData> trustedCertificateData;

    private static final String LINE_SHIFT = "    ";

    public SecurityDataResponse(List<SecurityDataContainer> securityDataContainers,
            List<TrustedCertificateData> trustedCertificateData) {
        this.securityDataContainers = new ArrayList<>(securityDataContainers);
        this.trustedCertificateData = new ArrayList<>(trustedCertificateData);
    }

    /**
     * Get the ordered list of security data putting OAM first and IPSec second
     * in list if ipsec request.
     *
     * @return ordered list of SecurityDataContainer
     */
    public List<SecurityDataContainer> getSecurityDataContainers() {
        return new ArrayList<>(this.securityDataContainers);
    }

    /**
     * Get an ordered list of TrustedCertificateData objects These certs will be
     * installed in order given in this list.
     *
     * @return ordered list of TrustedCertificateData objects
     */
    public List<TrustedCertificateData> getTrustedCertificateData() {
        return new ArrayList<>(this.trustedCertificateData);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("\n\n");
        if ((securityDataContainers != null) && (!securityDataContainers.isEmpty())) {
            for (final SecurityDataContainer sdc : securityDataContainers) {
                builder.append(sdc.toString()).append("\n");
            }
        }
        if ((trustedCertificateData != null) && (!trustedCertificateData.isEmpty())) {
            builder.append("TrustedCertificateData:\n");
            for (final TrustedCertificateData tcd : trustedCertificateData) {
                builder.append(tcd.toString()).append("\n");
            }
        }
        return builder.toString();
    }

    public static final class SecurityDataContainer implements Serializable {

        private static final long serialVersionUID = 3067460821796857331L;

        private final CertificateType certificateType;
        private final NodeCredentialData nodeCredentials;
        private final TrustCategoryData trustCategories;

        public SecurityDataContainer(final CertificateType certificateType,
                final NodeCredentialData nodeCredentials, final TrustCategoryData trustCategories) {
            this.certificateType = certificateType;
            this.nodeCredentials = nodeCredentials;
            this.trustCategories = trustCategories;
        }

        public CertificateType getTrustCategoryType() {
            return certificateType;
        }

        public NodeCredentialData getNodeCredentials() {
            return nodeCredentials;
        }

        public TrustCategoryData getTrustCategories() {
            return trustCategories;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("SecurityDataContainer: CertificateType=").append(certificateType).append("\n");
            if (nodeCredentials != null) {
                builder.append(nodeCredentials.toString()).append("\n");
            }
            if (trustCategories != null) {
                builder.append(trustCategories.toString()).append("\n");
            }
            return builder.toString();
        }
    }

    public static final class EnrollmentServerData implements Serializable {

        private static final long serialVersionUID = -9163573314796137231L;

        private final String enrollmentServerId; // “1” for CMP protocol, “2” for SCEP protocol
        private final String uri;
        private final String protocol;
        private final String enrollmentAuthority; // Can be null if value not available/applicable for some node types

        public EnrollmentServerData(final String enrollmentServerId, final String uri,
                final String protocol, final String enrolAuthority) {
            this.enrollmentServerId = enrollmentServerId;
            this.uri = uri;
            this.protocol = protocol;
            this.enrollmentAuthority = enrolAuthority;
        }

        public String getEnrollmentServerId() {
            return enrollmentServerId;
        }

        public String getUri() {
            return uri;
        }

        public String getProtocol() {
            return protocol;
        }

        public String getEnrollmentAuthority() {
            return enrollmentAuthority;
        }

        @Override
        public String toString() {
            String ret = LINE_SHIFT + "EnrollmentServerData: EnrollmentServerId=" + enrollmentServerId + "\n";
            ret += LINE_SHIFT + LINE_SHIFT + "URI: " + uri + "\n";
            ret += LINE_SHIFT + LINE_SHIFT + "Protocol: " + protocol + "\n";
            ret += LINE_SHIFT + LINE_SHIFT + "EnrollmentAuthority: " + enrollmentAuthority + "\n";
            return ret;
        }
    }

    public static final class EnrollmentAuthorityData implements Serializable {

        private static final long serialVersionUID = -7277963501508612857L;

        private final String enrollmentAuthorityId;
        private final String enrollmentCaFingerprint;
        private final String enrollmentCaCertificate;
        private final String authorityType;
        private final String enrollmentAuthorityName; // DN

        public EnrollmentAuthorityData(final String enrollmentAuthorityId, final String enrollmentCaFingerprint,
                final String enrollmentCaCertificate, final String authorityType, final String enrollmentAuthorityName) {
            this.enrollmentAuthorityId = enrollmentAuthorityId;
            this.enrollmentCaFingerprint = enrollmentCaFingerprint;
            this.enrollmentCaCertificate = enrollmentCaCertificate;
            this.authorityType = authorityType;
            this.enrollmentAuthorityName = enrollmentAuthorityName;
        }

        public String getEnrollmentAuthorityId() {
            return enrollmentAuthorityId;
        }

        public String getEnrollmentCaFingerprint() {
            return enrollmentCaFingerprint;
        }

        public String getEnrollmentCaCertificate() {
            return enrollmentCaCertificate;
        }

        public String getAuthorityType() {
            return authorityType;
        }

        public String getEnrollmentAuthorityName() {
            return enrollmentAuthorityName;
        }

        @Override
        public String toString() {
            String ret = LINE_SHIFT + "AuthorityId: " + enrollmentAuthorityId + "\n";
            ret += LINE_SHIFT + "Enrollment CA Fingerprint: " + enrollmentCaFingerprint + "\n";
            ret += LINE_SHIFT + "Enrollment CA Certificate DN: " + enrollmentCaCertificate + "\n";
            ret += LINE_SHIFT + "AuthorityType: " + authorityType + "\n";
            ret += LINE_SHIFT + "AuthorityName: " + enrollmentAuthorityName + "\n";
            return ret;
        }
    }

    public static final class EnrollmentServerGroupData implements Serializable {

        private static final long serialVersionUID = 7635329733113004259L;

        private final String enrollmentServerGroupId;  // “1” or oamEnrollmentServerGroup for 16B – up to 
        private final List<EnrollmentServerData> enrollmentServers;

        public EnrollmentServerGroupData(final String enrollmentServerGroupId,
                final List<EnrollmentServerData> enrollmentServers) {
            this.enrollmentServerGroupId = enrollmentServerGroupId;
            this.enrollmentServers = new ArrayList<>(enrollmentServers);
        }

        public EnrollmentServerGroupData(final String enrollmentServerGroupId) {
            this.enrollmentServerGroupId = enrollmentServerGroupId;
            this.enrollmentServers = new ArrayList<>();
        }

        public void addEnrollmentServer(EnrollmentServerData enrollmentServer) {
            if (enrollmentServer != null) {
                this.enrollmentServers.add(enrollmentServer);
            }
        }

        public String getEnrollmentServerGroupId() {
            return enrollmentServerGroupId;
        }

        public List<EnrollmentServerData> getEnrollmentServers() {
            return new ArrayList<>(enrollmentServers);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(LINE_SHIFT).append("EnrollmentServerGroupId: ").append(enrollmentServerGroupId).append("\n");
            if ((enrollmentServers != null) && (!enrollmentServers.isEmpty())) {
                for (EnrollmentServerData esd : enrollmentServers) {
                    builder.append(esd.toString());
                }
            }
            return builder.toString();
        }
    }

    public static final class TrustedCertificateData implements Serializable {

        private static final long serialVersionUID = -1883934569100547183L;

        private final String trustedCertificateFdn;  // complete FDN of trusted cert with ManagedElement=1,xxx
        private final String trustedCertificateFingerPrint;
        private final String caSubjectName;
        private final String caName;
        private final String tdpsUri;
        private final String caPem;
        private final String caIssuerDn;

        private List<String> crlsUri;

        public TrustedCertificateData(final String fdn, final String fingerPrint, final String caSubjectName, final String caName, final String tdpsUri, final String caPem, final String caIssuerDn) {
            this.trustedCertificateFdn = fdn;
            this.trustedCertificateFingerPrint = fingerPrint;
            this.caSubjectName = caSubjectName;
            this.caName = caName;
            this.tdpsUri = tdpsUri;
            this.caPem = caPem;
            this.caIssuerDn = caIssuerDn;
            this.crlsUri = null;
        }

        /**
         * @return the caIssuerDn
         */
        public String getCaIssuerDn() {
            return caIssuerDn;
        }

        /**
         * @return the caPem
         */
        public String getCaPem() {
            return caPem;
        }

        /**
         * @return the caSubjectName
         */
        public String getCaSubjectName() {
            return caSubjectName;
        }

        /**
         * @return the caName
         */
        public String getCaName() {
            return caName;
        }

        /**
         * @return the tdpsUri
         */
        public String getTdpsUri() {
            return tdpsUri;
        }

        public final String getTrustedCertificateFdn() {
            return trustedCertificateFdn;
        }

        public final String getTrustedCertificateFingerPrint() {
            return trustedCertificateFingerPrint;
        }

        public List<String> getCrlsUri() {
            return new ArrayList<>(crlsUri);
        }

        public void setCrlsUri(List<String> crlsUri) {
            this.crlsUri = new ArrayList<>(crlsUri);
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 47 * hash + Objects.hashCode(this.trustedCertificateFdn);
            hash = 47 * hash + Objects.hashCode(this.trustedCertificateFingerPrint);
            hash = 47 * hash + Objects.hashCode(this.caSubjectName);
            hash = 47 * hash + Objects.hashCode(this.caIssuerDn);
            hash = 47 * hash + Objects.hashCode(this.caName);
            hash = 47 * hash + Objects.hashCode(this.tdpsUri);
            hash = 47 * hash + Objects.hashCode(this.caPem);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TrustedCertificateData other = (TrustedCertificateData) obj;
            if (!Objects.equals(this.trustedCertificateFdn, other.trustedCertificateFdn)) {
                return false;
            }
            if (!Objects.equals(this.trustedCertificateFingerPrint, other.trustedCertificateFingerPrint)) {
                return false;
            }
            if (caSubjectName == null) {
                if (other.caSubjectName != null) {
                    return false;
                }
            } else if (!caSubjectName.equals(other.caSubjectName)) {
                return false;
            }

            if (caName == null) {
                if (other.caName != null) {
                    return false;
                }
            } else if (!caName.equals(other.caName)) {
                return false;
            }
            return equalsForVerboseValues(other);
        }

        private boolean equalsForVerboseValues(final TrustedCertificateData other) {
            if (tdpsUri == null) {
                if (other.tdpsUri != null) {
                    return false;
                }
            } else if (!tdpsUri.equals(other.tdpsUri)) {
                return false;
            }
            if (caPem == null) {
                if (other.caPem != null) {
                    return false;
                }
            } else if (!caPem.equals(other.caPem)) {
                return false;
            }
            if (caIssuerDn == null) {
                if (other.caIssuerDn != null) {
                    return false;
                }
            } else if (!caIssuerDn.equals(other.caIssuerDn)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            String ret = LINE_SHIFT;
            if ((trustedCertificateFdn != null) && (!trustedCertificateFdn.isEmpty())) {
                ret += "CertificateFdn: " + trustedCertificateFdn + "\n";
            }
            if ((trustedCertificateFingerPrint != null) && (!trustedCertificateFingerPrint.isEmpty())) {
                ret += LINE_SHIFT + "CertificateFingerPrint: " + trustedCertificateFingerPrint + "\n";
            }
            if ((caSubjectName != null) && (!caSubjectName.isEmpty())) {
                ret += LINE_SHIFT + "CaSubjectName: " + caSubjectName + "\n";
            }
            if ((caName != null) && (!caName.isEmpty())) {
                ret += LINE_SHIFT + "CaName: " + caName + "\n";
            }
            if ((tdpsUri != null) && (!tdpsUri.isEmpty())) {
                ret += LINE_SHIFT + "TdpsUri: " + tdpsUri + "\n";
            }
            if ((caPem != null) && (!caPem.isEmpty())) {
                ret += LINE_SHIFT + "CaPem: " + caPem + "\n";
            }
            if ((caIssuerDn != null) && (!caIssuerDn.isEmpty())) {
                ret += LINE_SHIFT + "caIssuerDn: " + caIssuerDn + "\n";
            }

            return ret;
        }
    }

    public static final class TrustCategoryData implements Serializable {

        private static final long serialVersionUID = 183311487034061109L;

        private final String trustCategoryId;
        private final List<String> trustedCertificateFdns;

        public TrustCategoryData(final String trustCategoryId,
                final List<String> trustedCertificateFdns) {
            this.trustCategoryId = trustCategoryId;
            this.trustedCertificateFdns = new ArrayList<>(trustedCertificateFdns);
        }

        public String getTrustCategoryId() {
            return trustCategoryId;
        }

        /**
         * Get a list of certificate FDNs associated with the TrustCategory (oam
         * or ipsec)
         *
         * @return Return a list of certificate FDNs
         */
        public List<String> getTrustedCertificateFdns() {
            return new ArrayList<>(trustedCertificateFdns);
        }

        @Override
        public String toString() {
            String ret = LINE_SHIFT + "TrustCategoryData: trustCategoryId=" + trustCategoryId + "\n";
            ret = trustedCertificateFdns.stream().map(fdn -> LINE_SHIFT + LINE_SHIFT + fdn + "\n").reduce(ret, String::concat);
            return ret;
        }
    }

    public static final class NodeCredentialData implements Serializable {

        private static final long serialVersionUID = -2156628358026390542L;

        private final String nodeCredentialId; // oamNodeCredential or ipsecNodeCredential for oam and ipsec respectively
        private final String subjectName;
        private final String keyInfo;
        private final EnrollmentServerGroupData enrollmentServerGroup;
        private final EnrollmentAuthorityData enrollmentAuthority;
        private final String challengePassword;

        public NodeCredentialData(final String nodeCredentialId, final String subjectName,
                final String keyInfo, final EnrollmentServerGroupData enrollmentServerGroup,
                final EnrollmentAuthorityData enrollmentAuthority, final String challengePassword) {
            this.nodeCredentialId = nodeCredentialId;
            this.subjectName = subjectName;
            this.keyInfo = keyInfo;
            this.enrollmentServerGroup = enrollmentServerGroup;
            this.enrollmentAuthority = enrollmentAuthority;
            this.challengePassword = challengePassword;
        }

        public String getNodeCredentialId() {
            return nodeCredentialId;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public String getKeyInfo() {
            return keyInfo;
        }

        public EnrollmentServerGroupData getEnrollmentServerGroup() {
            return enrollmentServerGroup;
        }

        public EnrollmentAuthorityData getEnrollmentAuthority() {
            return enrollmentAuthority;
        }

        public String getChallengePassword() {
            return challengePassword;
        }

        @Override
        public String toString() {
            String ret = LINE_SHIFT + "NodeCredentialId: " + nodeCredentialId + "\n";
            ret += LINE_SHIFT + "SubjectName: " + subjectName + "\n";
            ret += LINE_SHIFT + "KeyInfo: " + keyInfo + "\n";
            if (enrollmentServerGroup != null) {
                ret += enrollmentServerGroup.toString();
            }
            if (enrollmentAuthority != null) {
                ret += enrollmentAuthority.toString();
            }
            ret += LINE_SHIFT + "ChallengePassword: ";
            if ((challengePassword != null) && (!challengePassword.isEmpty())) {
                ret += "*****";
            }
            ret += '\n';
            return ret;
        }
    }

}
