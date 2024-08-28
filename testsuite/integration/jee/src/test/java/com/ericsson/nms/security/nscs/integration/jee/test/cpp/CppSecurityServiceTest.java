package com.ericsson.nms.security.nscs.integration.jee.test.cpp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.TrustCategoryType;
import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo;
import com.ericsson.nms.security.nscs.cpp.model.TrustStoreInfo;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParam;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityDataConstants;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityDataSetup;
import com.ericsson.nms.security.nscs.pki.NscsPkiUtils;
import com.ericsson.nms.security.nscs.utilities.NscsCommonValidator;
import com.ericsson.oss.itpf.security.pki.common.model.CAStatus;
import com.ericsson.oss.itpf.security.pki.common.model.CertificateAuthority;
import com.ericsson.oss.itpf.security.pki.common.model.EntityStatus;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.CAEntity;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.itpf.smrs.SmrsAccount;
import com.ericsson.oss.itpf.smrs.SmrsService;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

public class CppSecurityServiceTest implements CppSecurityServiceTests {

    protected final static NodeModelInformation CPP_NODE1_MODEL_INFO = new NodeModelInformation("E.1.63", ModelIdentifierType.MIM_VERSION, "ERBS");
    protected final static NodeModelInformation CPP_NODE1_MODEL_INFO_63 = new NodeModelInformation("E.1.63", ModelIdentifierType.MIM_VERSION, "ERBS");
    protected final static NodeReference TEST_NODE_REF = new NodeRef(NodeSecurityDataConstants.MECONTEXT_CPP_FDN1);
    protected final static NodeModelInformation CPP_RBS_13B_NODE_MODEL_INFO = new NodeModelInformation("13B-S2.1.100",
            ModelIdentifierType.OSS_IDENTIFIER, "RBS");

    //This is the BEAN we are testing
    @Inject
    CppSecurityService cppSecurity;

    @Inject
    SmrsService smrsService;

    @Inject
    NscsPkiEntitiesManagerIF nscsPkiManager;

    @Inject
    NodeSecurityDataSetup dataSetup;

    @Inject
    UserTransaction userTransaction;

    @Inject
    NscsCapabilityModelService nscsCapabilityModelService;

    @Inject
    NscsCMReaderService readerService;

    @Inject
    private Logger log;

    @Override
    public void prepareInitEnrollment() throws Exception {

        log.info("[NSCS_ARQ_CPP_SECURITY_SERVICE] Starting test prepareInitEnrollment >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        dataSetup.createNode(NodeSecurityDataConstants.CPP_NODE_NAME2, ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name(),
                NodeSecurityDataSetup.IpAddressVersion.IPv4);
        log.info("[NSCS_ARQ_CPP_SECURITY_SERVICE] DataSetup Completed... starting userTransaction");

        beginTransaction();

        // ScepEnrollmentInfo should contain all data needed for the initCertEnrollment MO action
        final ScepEnrollmentInfo enrollment = cppSecurity.generateOamEnrollmentInfo(
                NodeSecurityDataSetup.networkElementNameFromMeContextName(NodeSecurityDataConstants.CPP_NODE_NAME2), null, null, null,
                EnrollmentMode.SCEP, CPP_NODE1_MODEL_INFO);

        commitTransaction();

        assertNotNull("Null Enrollment Info ", enrollment);
        final String otp = enrollment.getChallengePassword();
        final String expKeySize = NscsPkiUtils.convertAlgorithmKeysToKeyLength(AlgorithmKeys.RSA_2048).toString();
        assertNotNull("Null OTP", otp);
        assertNotNull("Null enrollment server URL", enrollment.getServerURL());
        assertNotNull("Null enrollment EE DN", enrollment.getDistinguishedName());
        assertNotNull("Null enrollment finger print algorithm", enrollment.getFingerPrintAlgorithm());
        assertNotNull("Null enrollment CA finger print", enrollment.getServerCertFingerPrint());

        final URL enrollmentUrl = new URL(enrollment.getServerURL());
        final NscsCommonValidator ipValidator = NscsCommonValidator.getInstance();
        assertTrue("Enrollment URL [" + enrollment.getServerURL() + "]does not contain IPv4 address",
                ipValidator.isValidIPv4Address(enrollmentUrl.getHost()));
        assertEquals("Wrong enrollment protocol", EnrollmentMode.SCEP.getEnrollmentModeValue(), enrollment.getEnrollmentProtocol());
        assertEquals("Wrong fingerprint algorithm", nscsCapabilityModelService.getDefaultDigestAlgorithm(CPP_NODE1_MODEL_INFO),
                enrollment.getFingerPrintAlgorithm());
        assertEquals("Wrong enrollment key size", expKeySize, enrollment.getKeySize());

        final String entityName = enrollment.getName();

        //Make sure EE is created
        final Entity nodeEE = nscsPkiManager.getPkiEntity(entityName);
        assertNotNull("Null entity", nodeEE);
        assertEquals("Entity status is not NEW", EntityStatus.NEW, nodeEE.getEntityInfo().getStatus());
        assertEquals("Entity password does not match", otp, nscsPkiManager.getEntityOTP(nodeEE.getEntityInfo().getName()));

        beginTransaction();

        // Delete entity
        nscsPkiManager.deleteEntity(entityName);

        commitTransaction();

        dataSetup.cleanUp();

        log.info("[NSCS_ARQ_CPP_SECURITY_SERVICE] END test prepareInitEnrollment >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

    }

    @Override
    public void prepareInitEnrollmentKey2048() throws Exception {

        log.info("[NSCS_ARQ_CPP_SECURITY_SERVICE] Starting test prepareInitEnrollmentKey2048 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        dataSetup.createNode(NodeSecurityDataConstants.CPP_NODE_NAME1, ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name(),
                NodeSecurityDataSetup.IpAddressVersion.IPv4);

        beginTransaction();

        // ScepEnrollmentInfo should contain all data needed for the initCertEnrollment MO action
        final ScepEnrollmentInfo enrollment = cppSecurity.generateOamEnrollmentInfo(
                NodeSecurityDataSetup.networkElementNameFromMeContextName(NodeSecurityDataConstants.CPP_NODE_NAME1), null, null, null,
                EnrollmentMode.SCEP, CPP_NODE1_MODEL_INFO_63);
        // Delete entity
        nscsPkiManager.deleteEntity(enrollment.getName());

        commitTransaction();

        dataSetup.cleanUp();

        assertNotNull("Null Enrollment Info ", enrollment);
        final String expKeySize = NscsPkiUtils.convertAlgorithmKeysToKeyLength(AlgorithmKeys.RSA_2048).toString();
        assertNotNull("Null enrollment finger print algorithm", enrollment.getFingerPrintAlgorithm());
        assertNotNull("Null enrollment CA finger print", enrollment.getServerCertFingerPrint());

        assertEquals("Wrong fingerprint algorithm", nscsCapabilityModelService.getDefaultDigestAlgorithm(CPP_NODE1_MODEL_INFO_63),
                enrollment.getFingerPrintAlgorithm());
        assertEquals("Wrong enrollment key size", expKeySize, enrollment.getKeySize());

        log.info("[NSCS_ARQ_CPP_SECURITY_SERVICE] END test prepareInitEnrollmentKey2048 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    @Override
    public void prepareInitEnrollmentIPv6() throws Exception {

        log.info("[NSCS_ARQ_CPP_SECURITY_SERVICE] Starting test prepareInitEnrollmentIPv6 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        dataSetup.createNode(NodeSecurityDataConstants.CPP_NODE_NAME1, ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name(),
                NodeSecurityDataSetup.IpAddressVersion.IPv6);

        beginTransaction();

        // ScepEnrollmentInfo should contain all data needed for the initCertEnrollment MO action
        final ScepEnrollmentInfo enrollment = cppSecurity.generateOamEnrollmentInfo(
                NodeSecurityDataSetup.networkElementNameFromMeContextName(NodeSecurityDataConstants.CPP_NODE_NAME1), null, null, null,
                EnrollmentMode.SCEP, CPP_NODE1_MODEL_INFO_63);
        // Delete entity
        nscsPkiManager.deleteEntity(enrollment.getName());

        commitTransaction();

        dataSetup.cleanUp();

        assertNotNull("Null Enrollment Info ", enrollment);
        assertNotNull("Null enrollment server URL", enrollment.getServerURL());
        final URL enrollmentUrl = new URL(enrollment.getServerURL());
        String ipv6Addr = enrollmentUrl.getHost();
        assertTrue("Enrollment host [" + ipv6Addr + "] is not IPv6 address", ipv6Addr.startsWith("["));
        assertTrue("Enrollment host [" + ipv6Addr + "] is not IPv6 address", ipv6Addr.endsWith("]"));
        ipv6Addr = ipv6Addr.substring(1, ipv6Addr.length() - 1); // Remove []
        final NscsCommonValidator ipValidator = NscsCommonValidator.getInstance();
        assertTrue("Enrollment host [" + ipv6Addr + "] is not IPv6 address", ipValidator.isValidIPv6Address(ipv6Addr));

        log.info("[NSCS_ARQ_CPP_SECURITY_SERVICE] END test prepareInitEnrollmentIPv6 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    @Override
    public void prepareInstallCorbaTrust() throws Exception {

        log.info("[NSCS_ARQ_CPP_SECURITY_SERVICE] Starting test prepareInstallCorbaTrust >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        dataSetup.insertData();

        beginTransaction();

        final TrustStoreInfo trustInfo = cppSecurity.getTrustStoreForNode(TrustedCertCategory.CORBA_PEERS, TEST_NODE_REF, true);

        commitTransaction();

        dataSetup.cleanUp();

        assertNotNull(trustInfo);
        assertEquals(TrustedCertCategory.CORBA_PEERS, trustInfo.getCategory());

        final Set<CertSpec> certSpecs = trustInfo.getCertSpecs();
        assertNotNull(certSpecs);
        assertFalse("Empty trust list", certSpecs.isEmpty());

        // Check certificate fingerprint format prepared for MO action
        final MoParams tiMoParams = trustInfo.toMoParams();
        final Map<String, MoParam> moParamMap = tiMoParams.getParamMap();
        final List<MoParams> certSpecListMo = (List<MoParams>) (moParamMap.get("certSpecList").getParam());
        for (final MoParams moParams : certSpecListMo) {
            final MoParam fpMoParam = moParams.getParamMap().get("fingerprint");
            final String fingerprint = (String) (fpMoParam.getParam());
            assertTrue("Wrong fingerprint format: " + fingerprint, fingerprint.startsWith(DigestAlgorithm.SHA256.getDigestValuePrefix()));
            final String fileName = (String) (moParams.getParamMap().get("fileName").getParam());
            assertNotNull("Certificate file name can not be null", fileName);
            assertTrue("File name must contain SMRS path", fileName.contains("lran/certificates"));
        }
        final List<MoParams> accountInfoListMo = (List<MoParams>) (moParamMap.get("accountInfoList").getParam());
        for (final MoParams moParams : accountInfoListMo) {
            final String host = (String) (moParams.getParamMap().get("remoteHost").getParam());
            assertNotNull("SMRS host can not be null", host);
            final String user = (String) (moParams.getParamMap().get("userID").getParam());
            assertNotNull("SMRS user can not be null", user);
            final String passwd = (String) (moParams.getParamMap().get("password").getParam());
            assertNotNull("SMRS password can not be null", passwd);
        }

        //Make sure the files are stored in the SMRS
        final SmrsAccount smrsaccount = smrsService.getNodeSpecificAccount(nscsPkiManager.getSmrsAccountTypeForNscs(), "ERBS",
                TEST_NODE_REF.getName());
        final String homeDirectory = smrsaccount.getHomeDirectory();

        for (final CertSpec certSpec : certSpecs) {
            final String fileName = certSpec.getFileName();
            //When the node logs in to SMRS via SFTP it will be in the home directory
            //and it will look for the file specified in the CertSpec
            final String fileFullPath = homeDirectory + File.separator + fileName;
            final File file = new File(fileFullPath);
            assertTrue("File does not exists: " + fileFullPath, file.isFile());
        }

        log.info("[NSCS_ARQ_CPP_SECURITY_SERVICE] END test prepareInstallCorbaTrust >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    @Override
    public void prepareInstallCorbaTrustForAP() throws Exception {

        log.info("[NSCS_ARQ_CPP_SECURITY_SERVICE] Starting test prepareInstallCorbaTrustForAP >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        dataSetup.insertData();

        beginTransaction();

        //TrustStoreInfo should contain all data needed for the installTrustedCertificates MO action
        final ScepEnrollmentInfo enrollment = cppSecurity.generateOamEnrollmentInfo(NodeSecurityDataConstants.MECONTEXT_FDN2, null, null, null,
                EnrollmentMode.SCEP, CPP_NODE1_MODEL_INFO);
        assertNotNull("Null Enrollment Info ", enrollment);
        final TrustStoreInfo trustInfo = cppSecurity.getTrustStoreForAP(TrustedCertCategory.CORBA_PEERS, enrollment.getName(), CPP_NODE1_MODEL_INFO);
        nscsPkiManager.deleteEntity(enrollment.getName());

        commitTransaction();

        dataSetup.cleanUp();

        assertNotNull(trustInfo);
        assertEquals(TrustedCertCategory.CORBA_PEERS, trustInfo.getCategory());

        final Set<CertSpec> certSpecs = trustInfo.getCertSpecs();
        assertNotNull(certSpecs);
        assertFalse("Empty trust list", certSpecs.isEmpty());

        final MoParams tiMoParams = trustInfo.toMoParams();
        final List<MoParams> certSpecListMo = (List<MoParams>) (tiMoParams.getParamMap().get("certSpecList").getParam());
        for (final MoParams moParams : certSpecListMo) {
            final MoParam fpMoParam = moParams.getParamMap().get("fingerprint");
            final String fingerprint = (String) (fpMoParam.getParam());
            assertTrue("Wrong fingerprint format: " + fingerprint, fingerprint.startsWith(DigestAlgorithm.SHA256.getDigestValuePrefix()));
        }

        //Make sure the files are stored in the SMRS
        final SmrsAccount smrsaccount = smrsService.getNodeSpecificAccount(nscsPkiManager.getSmrsAccountTypeForNscs(), "ERBS",
                NodeSecurityDataConstants.CPP_NODE_NAME2);
        final String homeDirectory = smrsaccount.getHomeDirectory();

        for (final CertSpec certSpec : certSpecs) {
            final String fileName = certSpec.getFileName();
            //When the node logs in to SMRS via SFTP it will be in the home directory
            //and it will look for the file specified in the CertSpec
            final String fileFullPath = homeDirectory + File.separator + fileName;
            final File file = new File(fileFullPath);
            assertTrue("File does not exists: " + fileFullPath, file.isFile());
        }

        log.info("[NSCS_ARQ_CPP_SECURITY_SERVICE] END test prepareInstallCorbaTrustForAP >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    @Override
    public void prepareInstallCorbaTrustNotSupportedCat() throws Exception {

        log.info(
                "[NSCS_ARQ_CPP_SECURITY_SERVICE] Starting test prepareInstallCorbaTrustNotSupportedCat >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        dataSetup.createNode(NodeSecurityDataConstants.CPP_NODE_NAME1, ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name(),
                NodeSecurityDataSetup.IpAddressVersion.IPv4);

        beginTransaction();

        for (final TrustedCertCategory category : TrustedCertCategory.values()) {
            if (category.equals(TrustedCertCategory.CORBA_PEERS) || category.equals(TrustedCertCategory.IPSEC)
                    || category.equals(TrustedCertCategory.SYSLOG_SERVERS) || category.equals(TrustedCertCategory.LOCAL_AA_DB_FILE_SIGNERS)) {
                continue;
            } //This is the only supported category at the moment}
            try {
                cppSecurity.getTrustStoreForNode(category, TEST_NODE_REF, false);
                fail("Category should not be supported yet");
            } catch (final CppSecurityServiceException e) { /* ok */
            }
        }
        commitTransaction();

        dataSetup.cleanUp();
        log.info(
                "[NSCS_ARQ_CPP_SECURITY_SERVICE] END test prepareInstallCorbaTrustNotSupportedCat >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    @Override
    public void getTrustdistributionPointUrlIPv4() throws Exception {

        log.info("[NSCS_ARQ_CPP_SECURITY_SERVICE] Starting test getTrustdistributionPointUrlIPv4 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        dataSetup.createNode(NodeSecurityDataConstants.CPP_NODE_NAME1, ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name(),
                NodeSecurityDataSetup.IpAddressVersion.IPv4);
        final CAEntity caEntity = new CAEntity();
        caEntity.setType(EntityType.CA_ENTITY);
        final CertificateAuthority ca = new CertificateAuthority();
        ca.setName("NE_OAM_CA");
        ca.setStatus(CAStatus.NEW);
        caEntity.setCertificateAuthority(ca);

        beginTransaction();

        final NodeReference node = new NodeRef(NodeSecurityDataSetup.networkElementNameFromMeContextName(NodeSecurityDataConstants.CPP_NODE_NAME1));
        final NormalizableNodeReference normRef = readerService.getNormalizedNodeReference(node);
        final String tdpsStringUrl = cppSecurity.getTrustDistributionPointUrl(caEntity, normRef);

        commitTransaction();

        assertNotNull("Null TDPS URL", tdpsStringUrl);
        final URL tdpsUrl = new URL(tdpsStringUrl);

        final NscsCommonValidator ipValidator = NscsCommonValidator.getInstance();
        assertTrue("TDPS URL [" + tdpsStringUrl + "]does not contain IPv4 address", ipValidator.isValidIPv4Address(tdpsUrl.getHost()));
        dataSetup.cleanUp();
        log.info("[NSCS_ARQ_CPP_SECURITY_SERVICE] END test getTrustdistributionPointUrlIPv4 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    public void getTrustIPSECPico() throws Exception {

        log.info("[NSCS_ARQ_CPP_SECURITY_SERVICE] Starting test getTrustIPSECPico >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        dataSetup.createComEcimNode("pico1", ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name(), "1", "MSRBS_V1");

        final CAEntity caEntity = new CAEntity();
        caEntity.setType(EntityType.CA_ENTITY);
        final CertificateAuthority ca = new CertificateAuthority();
        ca.setName("NE_IPsec_CA");
        ca.setStatus(CAStatus.NEW);
        caEntity.setCertificateAuthority(ca);

        beginTransaction();

        final NodeReference node = new NodeRef(NodeSecurityDataSetup.networkElementNameFromMeContextName("pico1"));
        final NormalizableNodeReference normRef = readerService.getNormalizedNodeReference(node);
        final TrustStoreInfo trustStore = cppSecurity.getTrustStoreForNode(TrustedCertCategory.IPSEC, node, false, TrustCategoryType.IPSEC);

        commitTransaction();

        assertNotNull("Null trustStore", trustStore);
        dataSetup.cleanUp();
        log.info("[NSCS_ARQ_CPP_SECURITY_SERVICE] END test getTrustIPSECPico >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    @Override
    public void getTrustdistributionPointUrlIPv6() throws Exception {

        log.info("[NSCS_ARQ_CPP_SECURITY_SERVICE] Starting test getTrustdistributionPointUrlIPv6 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        dataSetup.createNode(NodeSecurityDataConstants.CPP_NODE_NAME1, ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name(),
                NodeSecurityDataSetup.IpAddressVersion.IPv6);
        final CAEntity caEntity = new CAEntity();
        caEntity.setType(EntityType.CA_ENTITY);
        final CertificateAuthority ca = new CertificateAuthority();
        ca.setName("NE_OAM_CA");
        ca.setStatus(CAStatus.NEW);
        caEntity.setCertificateAuthority(ca);

        beginTransaction();

        final NodeReference node = new NodeRef(NodeSecurityDataSetup.networkElementNameFromMeContextName(NodeSecurityDataConstants.CPP_NODE_NAME1));
        final NormalizableNodeReference normRef = readerService.getNormalizedNodeReference(node);
        final String tdpsStringUrl = cppSecurity.getTrustDistributionPointUrl(caEntity, normRef);

        commitTransaction();

        assertNotNull("Null TDPS URL", tdpsStringUrl);
        final URL tdpsUrl = new URL(tdpsStringUrl);

        String ipv6Addr = tdpsUrl.getHost();
        assertTrue("TDPS host [" + ipv6Addr + "] is not IPv6 address", ipv6Addr.startsWith("["));
        assertTrue("TDPS host [" + ipv6Addr + "] is not IPv6 address", ipv6Addr.endsWith("]"));
        ipv6Addr = ipv6Addr.substring(1, ipv6Addr.length() - 1); // Remove []
        final NscsCommonValidator ipValidator = NscsCommonValidator.getInstance();
        assertTrue("TDPS host [" + ipv6Addr + "] is not IPv6 address", ipValidator.isValidIPv6Address(ipv6Addr));
        dataSetup.cleanUp();
        log.info("[NSCS_ARQ_CPP_SECURITY_SERVICE] END test getTrustdistributionPointUrlIPv6 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    @Override
    public void prepareInitEnrollmentForRBS13B() throws Exception {

        log.info("[NSCS_ARQ_CPP_SECURITY_SERVICE] Starting test prepareInitEnrollmentForRBS13B >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        dataSetup.createNode(NodeSecurityDataConstants.NODE_NAME4, ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name(),
                NodeSecurityDataSetup.IpAddressVersion.IPv4);
        log.info("[NSCS_ARQ_CPP_SECURITY_SERVICE] DataSetup Completed... starting userTransaction");

        beginTransaction();

        // ScepEnrollmentInfo should contain all data needed for the initCertEnrollment MO action
        final ScepEnrollmentInfo enrollment = cppSecurity.generateOamEnrollmentInfo(
                NodeSecurityDataSetup.networkElementNameFromMeContextName(NodeSecurityDataConstants.NODE_NAME4), null, null, null,
                EnrollmentMode.SCEP, CPP_RBS_13B_NODE_MODEL_INFO);

        commitTransaction();

        assertNotNull("Null Enrollment Info ", enrollment);
        final String otp = enrollment.getChallengePassword();
        assertNotNull("Null OTP", otp);
        assertNotNull("Null enrollment server URL", enrollment.getServerURL());
        assertNotNull("Null enrollment EE DN", enrollment.getDistinguishedName());
        assertNotNull("Null enrollment finger print algorithm", enrollment.getFingerPrintAlgorithm());
        assertNotNull("Null enrollment CA finger print", enrollment.getServerCertFingerPrint());

        final URL enrollmentUrl = new URL(enrollment.getServerURL());
        final NscsCommonValidator ipValidator = NscsCommonValidator.getInstance();
        assertTrue("Enrollment URL [" + enrollment.getServerURL() + "]does not contain IPv4 address",
                ipValidator.isValidIPv4Address(enrollmentUrl.getHost()));
        assertEquals("Wrong enrollment protocol", EnrollmentMode.SCEP.getEnrollmentModeValue(), enrollment.getEnrollmentProtocol());
        assertEquals("Wrong fingerprint algorithm", nscsCapabilityModelService.getDefaultDigestAlgorithm(CPP_RBS_13B_NODE_MODEL_INFO),
                enrollment.getFingerPrintAlgorithm());

        //13B RBS node does not support KeySize and EnrollmentMode
        assertFalse(enrollment.isCertificateAuthorityDnSupported());
        assertFalse(enrollment.isKSandEMSupported());

        final String entityName = enrollment.getName();

        //Make sure EE is created
        final Entity nodeEE = nscsPkiManager.getPkiEntity(entityName);
        assertNotNull("Null entity", nodeEE);
        assertEquals("Entity status is not NEW", EntityStatus.NEW, nodeEE.getEntityInfo().getStatus());
        assertEquals("Entity password does not match", otp, nscsPkiManager.getEntityOTP(nodeEE.getEntityInfo().getName()));

        beginTransaction();

        // Delete entity
        nscsPkiManager.deleteEntity(entityName);

        commitTransaction();

        dataSetup.cleanUp();

        log.info("[NSCS_ARQ_CPP_SECURITY_SERVICE] END test prepareInitEnrollmentForRBS13B >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

    }

    private int getTransactionStatus() throws Exception {
        try {
            return userTransaction.getStatus();
        } catch (final SystemException e) {
            log.error("[NSCS_ARQ_CPP_SECURITY_SERVICE] transaction : status : FAILED", e);
            throw e;
        } catch (final Exception e) {
            log.error("[NSCS_ARQ_CPP_SECURITY_SERVICE] transaction : status : ERROR", e);
            throw e;
        }
    }

    private void beginTransaction() throws Exception {
        log.info("[NSCS_ARQ_CPP_SECURITY_SERVICE] transaction : begin : STARTED : status [{}]", getTransactionStatus());
        try {
            userTransaction.begin();
            log.info("[NSCS_ARQ_CPP_SECURITY_SERVICE] transaction : begin : SUCCESS : status [{}]", getTransactionStatus());
        } catch (final NotSupportedException | SystemException e) {
            log.error("[NSCS_ARQ_CPP_SECURITY_SERVICE] transaction : begin : FAILED", e);
            throw e;
        } catch (final Exception e) {
            log.error("[NSCS_ARQ_CPP_SECURITY_SERVICE] transaction : begin : ERROR", e);
            throw e;
        }
    }

    private void commitTransaction() throws Exception {
        log.info("[NSCS_ARQ_CPP_SECURITY_SERVICE] transaction : commit : STARTED : status [{}]", getTransactionStatus());
        try {
            userTransaction.commit();
            log.info("[NSCS_ARQ_CPP_SECURITY_SERVICE] transaction : commit : SUCCESS : status [{}]", getTransactionStatus());
        } catch (final SecurityException | IllegalStateException | RollbackException | HeuristicMixedException | HeuristicRollbackException
                | SystemException e) {
            log.error("[NSCS_ARQ_CPP_SECURITY_SERVICE] transaction : commit : FAILED", e);
            throw e;
        } catch (final Exception e) {
            log.error("[NSCS_ARQ_CPP_SECURITY_SERVICE] transaction : commit : ERROR", e);
            throw e;
        }
    }

}
