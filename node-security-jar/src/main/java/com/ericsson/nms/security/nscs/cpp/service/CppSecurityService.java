package com.ericsson.nms.security.nscs.cpp.service;

import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Set;
import java.net.StandardProtocolFamily;

import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.TrustCategoryType;
import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.EnrollmentRequestInfo;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo;
import com.ericsson.nms.security.nscs.cpp.model.SmrsAccountInfo;
import com.ericsson.nms.security.nscs.cpp.model.TrustStoreInfo;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.util.EnrollingInformation;
import com.ericsson.nms.security.nscs.util.NscsCbpOiTrustedEntityInfo;
import com.ericsson.nms.security.nscs.util.NscsTrustedEntityInfo;
import com.ericsson.nms.security.smrs.api.exception.SmrsDirectoryException;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.Certificate;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.CAEntity;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

/**
 * Service provides CPP specific security related methods related to security level activation.
 *
 * Methods to be added later: -generateCMPEntrollmentData once CPP supports CMPv2
 *
 * @author egbobcs
 *
 */
public interface CppSecurityService {

    public static final String ENROLLMENT_URL_ECIM_SUFFIX = "synch";

    /**
     * Generates OAM Enrollment Info for the specified node, needed for generating the ISCF file during Auto-Provisioning procedure.
     *
     * @param nodeFdn
     *            The FDN of involved node.
     * @param commonName
     *            (optional) the value of nodeSerialNumber, Can be null.
     * @param subjectAltName
     *            The Subject Alternative Name.
     * @param subjectAltNameFormat
     *            The format of Subject Alternative Name.
     * @param enrollmentMode
     *            The Enrollment Mode (SCEP or CMPV1_VC or CMPV1_INITIAL).
     * @param modelInfo
     *            The Model Information for the involved node.
     * @throws CppSecurityServiceException exception thrown for CppSecurityService
     * @return the com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo
     */
    ScepEnrollmentInfo generateOamEnrollmentInfo(String nodeFdn, String commonName, BaseSubjectAltNameDataType subjectAltName,
            SubjectAltNameFormat subjectAltNameFormat, EnrollmentMode enrollmentMode, NodeModelInformation modelInfo)
            throws CppSecurityServiceException;
    /**
     * Generates OAM Enrollment Info for the specified node, needed for generating the enrollment information.
     *
     * @param modelInfo
     *            The Model Information for the involved node.
     * @param EnrollmentRequestInfo
     *            The Enrollment Info Details.
     * @throws CppSecurityServiceException exception thrown for CppSecurityService
     * @return the com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo
     */
    ScepEnrollmentInfo generateOamEnrollmentInfo(NodeModelInformation modelInfo, EnrollmentRequestInfo enrollmentInfoDetails)
            throws CppSecurityServiceException;

    /**
     * Generates IPSec Enrollment Info for the specified node, needed for generating the ISCF file during Auto-Provisioning procedure.
     *
     * @param nodeFdn
     *            The FDN of involved node.
     * @param nodeSerialNumber
     *            (optional) the value of nodeSerialNumber, Can be null.
     * @param subjectAltName
     *            The Subject Alternative Name.
     * @param subjectAltNameFormat
     *            The format of Subject Alternative Name.
     * @param enrollmentMode
     *            The Enrollment Mode (SCEP or CMPV1_VC or CMPV1_INITIAL).
     * @param modelInfo
     *            The Model Information for the involved node.
     * @throws CppSecurityServiceException exception thrown for CppSecurityService
     * @return the com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo
     */
    ScepEnrollmentInfo generateIpsecEnrollmentInfo(String nodeFdn, String nodeSerialNumber, BaseSubjectAltNameDataType subjectAltName,
            SubjectAltNameFormat subjectAltNameFormat, EnrollmentMode enrollmentMode, NodeModelInformation modelInfo)
            throws CppSecurityServiceException;

    /**
     * Generates IPSec Enrollment Info for the specified node, with required IP version for enrollment server URI .
     * This API is needed for generating the ISCF file during Auto-Provisioning procedure.
     *
     * @param nodeFdn
     *            The FDN of involved node.
     * @param nodeSerialNumber
     *            (optional) the value of nodeSerialNumber, Can be null.
     * @param subjectAltName
     *            The Subject Alternative Name.
     * @param subjectAltNameFormat
     *            The format of Subject Alternative Name.
     * @param enrollmentMode
     *            The Enrollment Mode (SCEP or CMPV1_VC or CMPV1_INITIAL).
     * @param ipVersion
     *            The IP version (INET / INET6) to select the suitable enrollment server URI. If null, IP version is retrieved from  ConnectivityInformation
     * @param modelInfo
     *            The Model Information for the involved node.
     * @throws CppSecurityServiceException exception thrown for CppSecurityService
     * @return the com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo
     */
    ScepEnrollmentInfo generateIpsecEnrollmentInfo(String nodeFdn, String nodeSerialNumber, BaseSubjectAltNameDataType subjectAltName,
            SubjectAltNameFormat subjectAltNameFormat, EnrollmentMode enrollmentMode, StandardProtocolFamily ipVersion,
            NodeModelInformation modelInfo)
            throws CppSecurityServiceException;

    /**
     * Generates IPSec Enrollment Info, for the specified node, used by Workflow Task Handlers.
     *
     * @param nodeFdn
     *            The FDN of involved node.
     * @param subjectAltName
     *            The Subject Alternative Name value in AP syntax.
     * @param subjectAltNameFormat
     *            The Subject Alternative Name Format in AP syntax.
     *
     * @return ScepEnrollmentInfo containing all information needed to generate ISCF file for the node
     * @throws CppSecurityServiceException exception thrown for CppSecurityService
     *
     */
    ScepEnrollmentInfo generateIpsecEnrollmentInfo(String nodeFdn, BaseSubjectAltNameDataType subjectAltName,
            SubjectAltNameFormat subjectAltNameFormat) throws CppSecurityServiceException;

    /**
     * Gets the trust store with trust certificates relative to an Entity.
     *
     * @param category
     *            of the trust store. (CORBA, IpSec, etc)
     * @param nodeFdn  node Fdn
     * @param modelInfo
     *            Modeling information of node
     * @return TrustStoreInfo containing the CertSpecs (certificate information) and AccountInfos (users/passwords)
     * @throws CppSecurityServiceException CppSecurityServiceException
     * @throws SmrsDirectoryException SmrsDirectoryException
     * @throws java.security.cert.CertificateException CertificateException
     * @throws UnknownHostException UnknownHostException
     */
    TrustStoreInfo getTrustStoreForAP(final TrustedCertCategory category, final String nodeFdn, final NodeModelInformation modelInfo)
            throws CppSecurityServiceException, SmrsDirectoryException, CertificateException, UnknownHostException;

    /**
     * Gets the trust store with trust certificates relative to a node.
     *
     * @param category
     *            of the trust store. (CORBA, IpSec, etc)
     * @param nodeRef
     *            reference to the requested node
     * @param publishCertificates
     *            true if trusts must be published on SMRS
     * @return TrustStoreInfo containing the CertSpecs (certificate information) and AccountInfos (users/passwords)
     * @throws CppSecurityServiceException exception thrown for CppSecurityService
     * @throws SmrsDirectoryException exception thrown for CppSecurityService
     * @throws java.security.cert.CertificateException exception thrown for CppSecurityService
     * @throws UnknownHostException exception thrown for CppSecurityService
     */
    TrustStoreInfo getTrustStoreForNode(final TrustedCertCategory category, final NodeReference nodeRef, boolean publishCertificates)
            throws CppSecurityServiceException, SmrsDirectoryException, CertificateException, UnknownHostException;

    /**
     * Gets the trust store with trust certificates relative to a node.
     * 
     * @param category
     *            of the trust store. (CORBA, IpSec, etc)
     * @param nodeRef
     *            reference to the requested node
     * @return Set X509Certificate containing the trust certificates
     * @throws CppSecurityServiceException CppSecurityServiceException
     */
    Set<X509Certificate> getTrustCertificatesForNode(final TrustedCertCategory category, final NodeReference nodeRef)
            throws CppSecurityServiceException;

    /**
     * Gets the trust store with trust certificates relative to a node.
     *
     * @param category
     *            of the trust store. (CORBA, IpSec, etc)
     * @param caName
     *            the name of the trusted Certificate Authority to distribute
     * @param nodeRef
     *            reference to the requested node
     * @param publishCertificates
     *            true if trusts must be published on SMRS
     * @return TrustStoreInfo containing the CertSpecs (certificate information) and AccountInfos (users/passwords)
     * @throws CppSecurityServiceException exception thrown for CppSecurityService
     * @throws SmrsDirectoryException exception thrown for CppSecurityService
     * @throws java.security.cert.CertificateException exception thrown for CppSecurityService
     * @throws UnknownHostException exception thrown for CppSecurityService
     */
    TrustStoreInfo getTrustStoreForNodeWithCA(final TrustedCertCategory category, final String caName, final NodeReference nodeRef,
            boolean publishCertificates) throws CppSecurityServiceException, SmrsDirectoryException, CertificateException, UnknownHostException;

    /**
     * Gets the Digest Algorithm used to compute certificate fingerprint
     *
     * @param nodeFdn
     *            FDN of node
     * @return Digest Algorithm value (SHA1 SHA256 SHA512 MD5)
     */
    DigestAlgorithm getCertificateFingerprintAlgorithmForNode(final String nodeFdn);

    /**
     * Deletes any Entity objects in PKI created as a result of calling <code>generateSCEPEnrollmentInfo(String nodeFdn)</code> i.e. Corba
     *
     * @param fdn
     *            The fdn of the node that may have Entity objects associated with it
     * @throws CppSecurityServiceException
     *             CppSecurityServiceException
     */
    void cancelSCEPEnrollment(String fdn) throws CppSecurityServiceException;

    /**
     * Get {@link SmrsAccountInfo} for a node.
     *
     * @param nodeName
     *            node name.
     * @param neType
     *            type of Network Element for SMRS account
     * @return SRMS account information.
     */
    SmrsAccountInfo getSmrsAccountInfoForNode(final String nodeName, final String neType);

    /**
     * Get {@link SmrsAccountInfo} for the certificates of a node.
     *
     * @param neName
     *            Network Element name
     * @param neType
     *            type of Network Element for SMRS account
     * @return SRMS account information for certificates.
     * @throws CertificateException the CertificateException
     * @throws SmrsDirectoryException the SmrsDirectoryException
     */
    List<SmrsAccountInfo> getSmrsAccountInfoForCertificate(final String neName, final String neType)
            throws CertificateException, SmrsDirectoryException;

    /**
     * Delete {@link SmrsAccountInfo} for a node.
     *
     * @param nodeName
     *            node name.
     * @param targetType
     *            the target type of Network Element for SMRS account
     */
    void cancelSmrsAccountForNode(String nodeName, String targetType);

    /**
     * @param enrollInfo the enrollInfo
     * @return the ScepEnrollmentInfo
     * @throws CppSecurityServiceException the CppSecurityServiceException
     */
    ScepEnrollmentInfo generateEnrollmentInfo(EnrollingInformation enrollInfo) throws CppSecurityServiceException;

    /**
     * Get list of trusted certificates for given entity name.
     *
     * @param entityName
     *            the entity name.
     * @return the list of trusted certificates.
     * @throws CppSecurityServiceException the CppSecurityServiceException
     */
    List<Certificate> getTrustCertificates(final String entityName) throws CppSecurityServiceException;

    /**
     * @param issuerName the issuerName
     * @param serialNumber the serialNumber
     * @param reason the reason
     *
     * @throws CppSecurityServiceException
     *             Wraps all exception in internal exception
     */
    void revokeCertificateByIssuerName(String issuerName, String serialNumber, String reason) throws CppSecurityServiceException;

    /**
     * @param caEntity the caEntity
     * @param nodeRef the nodeRef
     * @return the trust distribution point url
     * @throws CppSecurityServiceException CppSecurityServiceException
     */
    String getTrustDistributionPointUrl(CAEntity caEntity, NormalizableNodeReference nodeRef) throws CppSecurityServiceException;

    /**
     * @param String the caEntityName
     * @param nodeName the nodeName
     * @return the trust distribution point url
     * @throws CppSecurityServiceException CppSecurityServiceException
     */
    String getTrustDistributionPointUrl(String caEntityName, String nodeName) throws CppSecurityServiceException;

    /**
     * Get entity profile name for the given trust category and the given node. If on PKI the node entity already exists, the name of its entity
     * profile is returned, if the node entity does not yet exist, the default entity profile as defined in capability model is returned.
     * 
     * @param category the category
     * @param nodeRef the nodeRef
     * @return the entity profile name
     * @throws CppSecurityServiceException the CppSecurityServiceException
     */
    public String getEntityProfileName(final TrustedCertCategory category, final NodeReference nodeRef) throws CppSecurityServiceException;

    /**
     * @param nodeFdn the nodeFdn
     * @return the ScepEnrollmentInfo
     * @throws CppSecurityServiceException the CppSecurityServiceException
     */
    ScepEnrollmentInfo generateOamEnrollmentInfo(String nodeFdn) throws CppSecurityServiceException;

    /**
     * enrollmentModeUpdate: traslate enrollmentMode from CMPv2_VC or CMPv2_INITIAL to CMPv2_UPDATE only if a NodeCertificate is installed on the
     * node. Only CPP.
     *
     * @param nodeFdn
     *            The FDN of involved node.
     * @param certificateType
     *            IPSEC or OAM.
     * @param entity
     *            the Entity retrieved from PKI
     *
     * @return EnrollmentMode
     */
    public EnrollmentMode enrollmentModeUpdate(final String nodeFdn, final String certificateType, final Entity entity);

    /**
     * enrollmentModeUpdate: traslate enrollmentMode from CMPv2_VC or CMPv2_INITIAL to CMPv2_UPDATE only if a NodeCertificate is installed on the
     * node. Only CPP.
     *
     * @param nodeFdn
     *            The FDN of involved node.
     * @param requiredEnrollment
     *            required Enrollment Mode
     * @param certificateType
     *            IPSEC or OAM.
     * @param entity
     *            the Entity retrieved from PKI
     *
     * @return EnrollmentMode
     */
    public EnrollmentMode enrollmentModeUpdate(final String nodeFdn, final EnrollmentMode requiredEnrollment, final String certificateType,
            Entity entity);

    public TrustStoreInfo getTrustStoreForNode(final TrustedCertCategory category, final NodeReference nodeRef, final boolean publishCertificates,
            final TrustCategoryType trustCategory) throws CppSecurityServiceException, SmrsDirectoryException, CertificateException, UnknownHostException;

    /**
     * Get from PKI the trusted CA info for the given CA name and the given type of TDPS URL (IPv4 or IPv6) to be used. Only the info referring to
     * active certificate of the trusted CA is returned.
     *
     * @param caName
     *            the CA name.
     * @param isIPv6Node
     *            true if node has IPv6 address, false if node has IPv4 address
     * @return the trusted CA info.
     * @throws CppSecurityServiceException the CppSecurityServiceException
     */
    public NscsTrustedEntityInfo getTrustedCAInfoByName(final String caName, final boolean isIPv6Node) throws CppSecurityServiceException;

    /**
     * Get from PKI the CbpOi trusted CA info for the given CA name . Only the info referring to
     * active certificate of the trusted CA is returned.
     *
     * @param caName
     *            the CA name.
     * @param trustCategoryName
     *            the Trust Category
     * @return the trusted CA info with the format needed for CbpOi platform.
     * @throws CppSecurityServiceException the CppSecurityServiceException
     */
    public NscsCbpOiTrustedEntityInfo getCbpOiTrustedCAInfoByName(final String caName) throws CppSecurityServiceException;

    /**
     * Get from PKI the CbpOi trusted CAs info for the given entity profile name . Only the info
     * referring to active certificates are returned. For an internal trusted CA with isChainRequired set to true, the info of the certificates
     * related to the chain of the active certificate of the internal CA itself are returned.
     *
     * @param entityProfileName
     *            the entity profile name.
     * @param trustCategoryName
     *            the Trust Category
     * @return the trusted CAs info.
     * @throws CppSecurityServiceException the CppSecurityServiceException
     */
    public Set<NscsCbpOiTrustedEntityInfo> getCbpOiTrustedCAsInfoByEntityProfileName(final String entityProfileName)
            throws CppSecurityServiceException;

    /**
     * Get from PKI the trusted CAs info for the given entity profile name and the given type of TDPS URL (IPv4 or IPv6) to be used. Only the info
     * referring to active certificates are returned. For an internal trusted CA with isChainRequired set to true, the info of the certificates
     * related to the chain of the active certificate of the internal CA itself are returned.
     *
     * @param entityProfileName
     *            the entity profile name.
     * @param isIPv6Node
     *            true if node has IPv6 address, false if node has IPv4 address
     * @return the trusted CAs info.
     * @throws CppSecurityServiceException the CppSecurityServiceException
     */
    public Set<NscsTrustedEntityInfo> getTrustedCAsInfoByEntityProfileName(final String entityProfileName, final boolean isIPv6Node)
            throws CppSecurityServiceException;

    /**
     * Check whether the node has been installed with a valid certificate issued by ENM CA or not.
     *
     * @param nodeFdn
     *            The FDN of involved node.
     * @param certificateType
     *            IPSEC or OAM.
     * @return true if node certificate is valid on PKI, false otherwise.
     * @throws CppSecurityServiceException the CppSecurityServiceException
     */
    boolean isNodeHasValidCertificate(final String nodeFdn, final String certificateType) throws CppSecurityServiceException;

    /**
     * Obtains the EnrollmentMode from NetworkElementSecurity MO by creating the MO if not exists
     *
     * @param enrollmentMode
     *            value to be updated on the NetworkElementSecurity MO
     * @param nodeFdn
     *            node Fdn to get the Existing NetworkElementSecurity
     * @param normRef
     *            normalizableNodeReference of the node for which NetworkElementSecurity is created
     * @return EnrollmentMode
     * @throws CppSecurityServiceException
     *             when error occurs while creating NetworkElementSecurity MO
     */
    public EnrollmentMode configureNESAndGetEnrollmentMode(EnrollmentMode enrollmentMode, final String nodeFdn, final NormalizableNodeReference normRef)
            throws CppSecurityServiceException;
}
