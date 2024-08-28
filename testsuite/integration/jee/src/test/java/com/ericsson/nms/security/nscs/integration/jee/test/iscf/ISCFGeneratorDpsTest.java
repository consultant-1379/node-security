/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package com.ericsson.nms.security.nscs.integration.jee.test.iscf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.iscf.IpsecArea;
import com.ericsson.nms.security.nscs.api.iscf.IscfResponse;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NodeEntityCategory;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.EnrollmentServer.EnrollmentProtocol;
import com.ericsson.nms.security.nscs.integration.jee.test.producer.EServiceProducer;
import com.ericsson.nms.security.nscs.integration.jee.test.rest.RestHelper;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityDataSetup;
import com.ericsson.nms.security.nscs.pki.NscsPkiUtils;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.Certificate;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.AbstractSubjectAltNameFieldValue;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltName;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameFieldType;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameString;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.EntityProfile;

public class ISCFGeneratorDpsTest implements ISCFGeneratorTests {

    private final static String NODE_NAME = "MYNODE";
    private final static String CPP_NODE_TYPE = "ERBS";
    private final static String CPP_MIM_VERSION = "E.1.63";
    private final static String MSRBS_NODE_TYPE = "RadioNode";
    private final static NodeModelInformation CPP_MODEL_INFO = new NodeModelInformation(CPP_MIM_VERSION, ModelIdentifierType.MIM_VERSION,
            CPP_NODE_TYPE);
    private final static NodeModelInformation MSRBS_MODEL_INFO = new NodeModelInformation("CXP123/45-R67", ModelIdentifierType.PRODUCT_NUMBER,
            MSRBS_NODE_TYPE);

    @Inject
    EServiceProducer eserviceHolder;

    @Inject
    NscsPkiEntitiesManagerIF nscsPkiManager;

    @Inject
    NodeSecurityDataSetup dataSetup;

    @Inject
    private Logger logger;

    @Override
    public void testIscfGeneratorReturnsByteArray() throws Exception {

        logger.info("-----------testIscfGeneratorReturnsByteArray starts--------------");

        dataSetup.deleteAllNodes();
        dataSetup.createNode(NODE_NAME, ModelDefinition.CmFunction.SyncStatusValue.UNSYNCHRONIZED.name(),
                NodeSecurityDataSetup.IpAddressVersion.IPv4);

        final IscfResponse iscfResponse = eserviceHolder.getIscfService().generate("IllogicalName",
                NodeSecurityDataSetup.networkElementNameFromMeContextName(NODE_NAME), SecurityLevel.LEVEL_2, SecurityLevel.LEVEL_1,
                EnrollmentMode.SCEP, CPP_MODEL_INFO);
        final byte[] xmlOutput = iscfResponse.getIscfContent();
        logger.info("XML output from ISCFGenerator: {}", new String(xmlOutput, "UTF-8"));
        assertNotNull("byte[] returned from ISCFGenerator is null", xmlOutput);
        assertTrue("byte[] returned from ISCFGenerator is of length 0", xmlOutput.length > 0);

        dataSetup.deleteAllNodes();

        logger.info("-----------testIscfGeneratorReturnsByteArray ends--------------");
    }

    @Override
    public void testIscfCancelSecLevelAndIpsec() throws Exception {

        logger.info("-----------testIscfCancelSecLevelAndIpsec starts--------------");

        dataSetup.deleteAllNodes();
        dataSetup.createNode(NODE_NAME, ModelDefinition.CmFunction.SyncStatusValue.UNSYNCHRONIZED.name(),
                NodeSecurityDataSetup.IpAddressVersion.IPv4);

        final Set<IpsecArea> ipsecAreas = new HashSet<>();
        ipsecAreas.add(IpsecArea.OM);
        ipsecAreas.add(IpsecArea.TRANSPORT);
        final String nodeFdn = "NetworkElement=" + NodeSecurityDataSetup.networkElementNameFromMeContextName(NODE_NAME);
        final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(SubjectAltNameFormat.IPV4, new SubjectAltNameStringType("127.0.0.1"));

        final NodeReference nodeRef = new NodeRef(nodeFdn);
        //1.    generate Iscf files, Corba and Ipsec, for nodeFdn
        final IscfResponse iscfResponse = eserviceHolder.getIscfService().generate("AnotherIllogicalName", nodeRef.getName(), SecurityLevel.LEVEL_2,
                SecurityLevel.LEVEL_1, "Label", subjectAltNameParam, ipsecAreas, EnrollmentMode.SCEP, CPP_MODEL_INFO);
        final byte[] xmlOutput = iscfResponse.getIscfContent();
        logger.info("XML output from ISCFGenerator: {}", new String(xmlOutput, "UTF-8"));
        assertNotNull("byte[] returned from ISCFGenerator is null", xmlOutput);
        assertTrue("byte[] returned from ISCFGenerator is of length 0", xmlOutput.length > 0);

        //2.    Get the corresponding EndEntities
        Entity corbaEE = null, ipsecEE = null;
        try {
            corbaEE = nscsPkiManager.getPkiEntity(NscsPkiUtils.getEntityNameFromFdn(NodeEntityCategory.OAM, nodeFdn));
        } catch (final NscsPkiEntitiesManagerException ex) {
            fail("Corba EE not found");
        }
        try {
            ipsecEE = nscsPkiManager.getPkiEntity(NscsPkiUtils.getEntityNameFromFdn(NodeEntityCategory.IPSEC, nodeFdn));
        } catch (final NscsPkiEntitiesManagerException ex) {
            fail("IPSec EE not found");
        }
        assertNotNull("Corba EE is null", corbaEE);
        assertNotNull("IPSec EE is null", ipsecEE);

        //3.    Call Iscf.cancel() to remove the end entities
        eserviceHolder.getIscfService().cancel(nodeFdn);

        //4.    Verify EndEntitied removed be calling getEndEntity() on each again.
        Entity corbaEEDeleted = null;
        Entity ipsecEEDeleted = null;
        try {
            corbaEEDeleted = nscsPkiManager.getPkiEntity(corbaEE.getEntityInfo().getName());
        } catch (final NscsPkiEntitiesManagerException e) {
            if (e.getMessage().contains("Entity does not exist")) {
                logger.info("EndEntities were removed");
            } else {
                throw e;
            }
        }
        try {
            ipsecEEDeleted = nscsPkiManager.getPkiEntity(ipsecEE.getEntityInfo().getName());
        } catch (final NscsPkiEntitiesManagerException e) {
            if (e.getMessage().contains("Entity does not exist")) {
                logger.info("EndEntities were removed");
            } else {
                throw e;
            }
        }

        assertNull("Corba EE is not null", corbaEEDeleted);
        assertNull("IPSec EE is not null", ipsecEEDeleted);

        dataSetup.deleteAllNodes();

        logger.info("-----------testIscfCancelSecLevelAndIpsec ends--------------");
    }

    @Override
    public void testIscfCancelIpsecFqdn() throws Exception {

        logger.info("-----------testIscfCancelIpsecFqdn starts--------------");

        dataSetup.deleteAllNodes();
        dataSetup.createNode(NODE_NAME, ModelDefinition.CmFunction.SyncStatusValue.UNSYNCHRONIZED.name(),
                NodeSecurityDataSetup.IpAddressVersion.IPv4);

        final Set<IpsecArea> ipsecAreas = new HashSet<>();
        ipsecAreas.add(IpsecArea.OM);
        ipsecAreas.add(IpsecArea.TRANSPORT);
        final String nodeFdn = "NetworkElement=" + NodeSecurityDataSetup.networkElementNameFromMeContextName(NODE_NAME);
        final String dnsName = "eiffel004.lmera.ericsson.se";
        final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(SubjectAltNameFormat.FQDN, new SubjectAltNameStringType(dnsName));

        final NodeReference nodeRef = new NodeRef(nodeFdn);
        //1.    generate Iscf files, Corba and Ipsec, for nodeFdn
        final IscfResponse iscfResponse = eserviceHolder.getIscfService().generate("AnotherIllogicalName", nodeRef.getName(), "Label",
                subjectAltNameParam, ipsecAreas, EnrollmentMode.SCEP, CPP_MODEL_INFO);
        final byte[] xmlOutput = iscfResponse.getIscfContent();
        logger.info("XML output from ISCFGenerator: {}", new String(xmlOutput, "UTF-8"));
        assertNotNull("byte[] returned from ISCFGenerator is null", xmlOutput);
        assertTrue("byte[] returned from ISCFGenerator is of length 0", xmlOutput.length > 0);

        //2.    Get the corresponding EndEntities
        Entity ipsecEE = null;
        try {
            ipsecEE = nscsPkiManager.getPkiEntity(NscsPkiUtils.getEntityNameFromFdn(NodeEntityCategory.IPSEC, nodeFdn));
        } catch (final NscsPkiEntitiesManagerException ex) {
            fail("IPSec EE not found");
        }
        assertNotNull("IPSec EE is null", ipsecEE);
        final SubjectAltName subjectAltName = ipsecEE.getEntityInfo().getSubjectAltName();
        assertEquals(1, subjectAltName.getSubjectAltNameFields().size());
        assertEquals(SubjectAltNameFieldType.DNS_NAME, subjectAltName.getSubjectAltNameFields().get(0).getType());
        final AbstractSubjectAltNameFieldValue subjectAltNameFieldValue = subjectAltName.getSubjectAltNameFields().get(0).getValue();
        assertTrue(subjectAltNameFieldValue instanceof SubjectAltNameString);
        final SubjectAltNameString subjectAltNameStringValue = (SubjectAltNameString) subjectAltNameFieldValue;
        assertEquals(dnsName, subjectAltNameStringValue.getValue());

        //3.    Call Iscf.cancel() to remove the end entities
        eserviceHolder.getIscfService().cancel(nodeFdn);

        //4.    Verify EndEntity removed be calling getEndEntity() on each again.
        Entity ipsecEEDeleted = null;
        try {
            ipsecEEDeleted = nscsPkiManager.getPkiEntity(ipsecEE.getEntityInfo().getName());
        } catch (final NscsPkiEntitiesManagerException e) {
            if (e.getMessage().contains("Entity does not exist")) {
                logger.info("EndEntities were removed");
            } else {
                throw e;
            }
        }

        assertNull("IPSec EE is not null", ipsecEEDeleted);

        dataSetup.deleteAllNodes();

        logger.info("-----------testIscfCancelIpsecFqdn ends--------------");
    }

    @Override
    public void testIscfSecurityDataGeneratorOam() throws Exception {

        logger.info("-----------testIscfSecurityDataGeneratorOam starts--------------");

        dataSetup.deleteAllNodes();
        dataSetup.createNode(NODE_NAME, ModelDefinition.CmFunction.SyncStatusValue.UNSYNCHRONIZED.name(),
                NodeSecurityDataSetup.IpAddressVersion.IPv4);

        final String nodeFdn = NodeSecurityDataSetup.networkElementNameFromMeContextName(NODE_NAME);
        final SecurityDataResponse securityDataResponse = eserviceHolder.getIscfService().generateSecurityDataOam(nodeFdn, EnrollmentMode.CMPv2_VC,
                MSRBS_MODEL_INFO);
        Entity corbaEE = null;
        try {
            corbaEE = nscsPkiManager.getPkiEntity(NscsPkiUtils.getEntityNameFromFdn(NodeEntityCategory.OAM, nodeFdn));
        } catch (final NscsPkiEntitiesManagerException ex) {
            fail("Corba EE not found");
        }
        assertNotNull("Corba EE is null", corbaEE);

        assertEquals(1, securityDataResponse.getSecurityDataContainers().size());

        final SecurityDataResponse.SecurityDataContainer oamCont = securityDataResponse.getSecurityDataContainers().get(0);

        assertEquals(CertificateType.OAM, oamCont.getTrustCategoryType());
        assertEquals(corbaEE.getEntityInfo().getSubject().toASN1String(), oamCont.getNodeCredentials().getSubjectName());
        assertEquals(NscsPkiUtils.convertAlgorithmNamesToNodeSupportedFormat(AlgorithmKeys.RSA_2048.toString()),
                oamCont.getNodeCredentials().getKeyInfo());
        assertEquals(1, oamCont.getNodeCredentials().getEnrollmentServerGroup().getEnrollmentServers().size());
        assertEquals(EnrollmentProtocol.CMP.name(),
                oamCont.getNodeCredentials().getEnrollmentServerGroup().getEnrollmentServers().get(0).getProtocol());

        final List<Certificate> oamTrustList = nscsPkiManager
                .getTrustCertificates(NscsPkiUtils.getEntityNameFromFdn(NodeEntityCategory.OAM, nodeFdn));
        assertEquals(oamTrustList.size(), oamCont.getTrustCategories().getTrustedCertificateFdns().size());

        //3.    Call Iscf.cancel() to remove the end entities
        eserviceHolder.getIscfService().cancel(nodeFdn);

        //4.    Verify EndEntitied removed be calling getEndEntity() on each again.
        Entity corbaEEDeleted = null;
        try {
            corbaEEDeleted = nscsPkiManager.getPkiEntity(corbaEE.getEntityInfo().getName());
        } catch (final NscsPkiEntitiesManagerException e) {
            if (e.getMessage().contains("Entity does not exist")) {
                logger.info("EndEntities were removed");
            } else {
                throw e;
            }
        }
        assertNull("Corba EE is not null", corbaEEDeleted);

        dataSetup.deleteAllNodes();

        logger.info("-----------testIscfSecurityDataGeneratorOam ends--------------");
    }

    @Override
    public void testIscfSecurityDataGeneratorCombo() throws Exception {

        logger.info("-----------testIscfSecurityDataGeneratorCombo starts--------------");

        dataSetup.deleteAllNodes();
        dataSetup.createNode(NODE_NAME, ModelDefinition.CmFunction.SyncStatusValue.UNSYNCHRONIZED.name(),
                NodeSecurityDataSetup.IpAddressVersion.IPv4);

        final String nodeFdn = NodeSecurityDataSetup.networkElementNameFromMeContextName(NODE_NAME);
        final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(SubjectAltNameFormat.IPV4, new SubjectAltNameStringType("127.0.0.1"));

        final SecurityDataResponse securityDataResponse = eserviceHolder.getIscfService().generateSecurityDataCombo(nodeFdn, subjectAltNameParam,
                EnrollmentMode.CMPv2_VC, MSRBS_MODEL_INFO);
        Entity corbaEE = null, ipsecEE = null;
        try {
            corbaEE = nscsPkiManager.getPkiEntity(NscsPkiUtils.getEntityNameFromFdn(NodeEntityCategory.OAM, nodeFdn));
        } catch (final NscsPkiEntitiesManagerException ex) {
            fail("Corba EE not found");
        }
        try {
            ipsecEE = nscsPkiManager.getPkiEntity(NscsPkiUtils.getEntityNameFromFdn(NodeEntityCategory.IPSEC, nodeFdn));
        } catch (final NscsPkiEntitiesManagerException ex) {
            fail("IPSec EE not found");
        }
        assertNotNull("Corba EE is null", corbaEE);
        assertNotNull("IPSec EE is null", ipsecEE);

        assertEquals(2, securityDataResponse.getSecurityDataContainers().size());

        final SecurityDataResponse.SecurityDataContainer oamCont = securityDataResponse.getSecurityDataContainers().get(0);
        final SecurityDataResponse.SecurityDataContainer ipsecCont = securityDataResponse.getSecurityDataContainers().get(1);

        assertEquals("Certificate type of first container must be OAM", CertificateType.OAM, oamCont.getTrustCategoryType());
        assertEquals(corbaEE.getEntityInfo().getSubject().toASN1String(), oamCont.getNodeCredentials().getSubjectName());
        assertEquals(NscsPkiUtils.convertAlgorithmNamesToNodeSupportedFormat(AlgorithmKeys.RSA_2048.toString()),
                oamCont.getNodeCredentials().getKeyInfo());
        assertEquals(1, oamCont.getNodeCredentials().getEnrollmentServerGroup().getEnrollmentServers().size());
        assertEquals(EnrollmentProtocol.CMP.name(),
                oamCont.getNodeCredentials().getEnrollmentServerGroup().getEnrollmentServers().get(0).getProtocol());

        assertEquals("Certificate type of second container must be IPSEC", CertificateType.IPSEC, ipsecCont.getTrustCategoryType());
        assertEquals(ipsecEE.getEntityInfo().getSubject().toASN1String(), ipsecCont.getNodeCredentials().getSubjectName());
        assertEquals(NscsPkiUtils.convertAlgorithmNamesToNodeSupportedFormat(AlgorithmKeys.RSA_2048.toString()),
                ipsecCont.getNodeCredentials().getKeyInfo());
        assertEquals(1, ipsecCont.getNodeCredentials().getEnrollmentServerGroup().getEnrollmentServers().size());
        assertEquals(EnrollmentProtocol.CMP.name(),
                ipsecCont.getNodeCredentials().getEnrollmentServerGroup().getEnrollmentServers().get(0).getProtocol());

        final List<Certificate> oamTrustList = nscsPkiManager
                .getTrustCertificates(NscsPkiUtils.getEntityNameFromFdn(NodeEntityCategory.OAM, nodeFdn));
        final List<Certificate> ipsecTrustList = nscsPkiManager
                .getTrustCertificates(NscsPkiUtils.getEntityNameFromFdn(NodeEntityCategory.IPSEC, nodeFdn));
        assertEquals(oamTrustList.size(), oamCont.getTrustCategories().getTrustedCertificateFdns().size());
        assertEquals(ipsecTrustList.size(), ipsecCont.getTrustCategories().getTrustedCertificateFdns().size());

        //3.    Call Iscf.cancel() to remove the end entities
        eserviceHolder.getIscfService().cancel(nodeFdn);

        //4.    Verify EndEntitied removed be calling getEndEntity() on each again.
        Entity corbaEEDeleted = null;
        Entity ipsecEEDeleted = null;
        try {
            corbaEEDeleted = nscsPkiManager.getPkiEntity(corbaEE.getEntityInfo().getName());
        } catch (final NscsPkiEntitiesManagerException e) {
            if (e.getMessage().contains("Entity does not exist")) {
                logger.info("EndEntities were removed");
            } else {
                throw e;
            }
        }
        try {
            ipsecEEDeleted = nscsPkiManager.getPkiEntity(ipsecEE.getEntityInfo().getName());
        } catch (final NscsPkiEntitiesManagerException e) {
            if (e.getMessage().contains("Entity does not exist")) {
                logger.info("EndEntities were removed");
            } else {
                throw e;
            }
        }

        assertNull("Corba EE is not null", corbaEEDeleted);
        assertNull("IPSec EE is not null", ipsecEEDeleted);

        dataSetup.deleteAllNodes();

        logger.info("-----------testIscfSecurityDataGeneratorCombo ends--------------");
    }

    @Override
    public void testIscfNodeModernizationOAM() throws Exception {

        logger.info("-----------testIscfNodeModernizationOAM starts--------------");

        dataSetup.deleteAllNodes();
        dataSetup.createNode(NODE_NAME, ModelDefinition.CmFunction.SyncStatusValue.UNSYNCHRONIZED.name(),
                NodeSecurityDataSetup.IpAddressVersion.IPv4);

        final String nodeFdn = "NetworkElement=" + NodeSecurityDataSetup.networkElementNameFromMeContextName(NODE_NAME);
        final NodeReference nodeRef = new NodeRef(nodeFdn);
        //    generate Iscf files, Corba for nodeFdn
        final IscfResponse iscfResponse = eserviceHolder.getIscfService().generate("LogicalName", nodeRef.getName(), SecurityLevel.LEVEL_2,
                SecurityLevel.LEVEL_1, EnrollmentMode.SCEP, CPP_MODEL_INFO);
        final byte[] xmlOutput = iscfResponse.getIscfContent();
        logger.info("XML output from ISCFGenerator: {}", new String(xmlOutput, "UTF-8"));
        assertNotNull("byte[] returned from ISCFGenerator is null", xmlOutput);
        assertTrue("byte[] returned from ISCFGenerator is of length 0", xmlOutput.length > 0);

        //    Get the corresponding EndEntitiy
        Entity corbaEE = null;
        try {
            corbaEE = nscsPkiManager.getPkiEntity(NscsPkiUtils.getEntityNameFromFdn(NodeEntityCategory.OAM, nodeFdn));
        } catch (final NscsPkiEntitiesManagerException ex) {
            fail("Corba EE not found");
        }
        assertNotNull("Corba EE for CPP node is null", corbaEE);

        // Get Entity profile name
        EntityProfile entityProfile = corbaEE.getEntityProfile();
        assertNotNull("Entity Profile is null", entityProfile);
        final String cppEntityProfileName = entityProfile.getName();
        assertNotNull(cppEntityProfileName);

        // Modernize node by generating AP security data for type MSRBS_V2
        final SecurityDataResponse securityDataResponse = eserviceHolder.getIscfService().generateSecurityDataOam(nodeRef.getName(),
                EnrollmentMode.CMPv2_VC, MSRBS_MODEL_INFO);
        assertEquals(1, securityDataResponse.getSecurityDataContainers().size());
        try {
            corbaEE = nscsPkiManager.getPkiEntity(NscsPkiUtils.getEntityNameFromFdn(NodeEntityCategory.OAM, nodeFdn));
        } catch (final NscsPkiEntitiesManagerException ex) {
            fail("Corba EE not found");
        }
        assertNotNull("Corba EE for MSRBS_V2 node is null", corbaEE);

        // Get Entity profile name
        entityProfile = corbaEE.getEntityProfile();
        assertNotNull("Entity Profile is null", entityProfile);
        final String msrbsEntityProfileName = entityProfile.getName();
        assertNotNull(msrbsEntityProfileName);

        assertTrue("OAM Entity profile not updated after node modernization", !msrbsEntityProfileName.equals(cppEntityProfileName));

        //    Call Iscf.cancel() to remove the end entities
        eserviceHolder.getIscfService().cancel(nodeFdn);

        dataSetup.deleteAllNodes();

        logger.info("-----------testIscfNodeModernizationOAM ends--------------");
    }

    @Override
    public void testIscfNodeModernizationCombo() throws Exception {

        logger.info("-----------testIscfNodeModernizationCombo starts--------------");

        dataSetup.deleteAllNodes();
        dataSetup.createNode(NODE_NAME, ModelDefinition.CmFunction.SyncStatusValue.UNSYNCHRONIZED.name(),
                NodeSecurityDataSetup.IpAddressVersion.IPv4);

        final String nodeFdn = "NetworkElement=" + NodeSecurityDataSetup.networkElementNameFromMeContextName(NODE_NAME);
        final NodeReference nodeRef = new NodeRef(nodeFdn);

        final Set<IpsecArea> ipsecAreas = new HashSet<>();
        ipsecAreas.add(IpsecArea.OM);
        ipsecAreas.add(IpsecArea.TRANSPORT);
        final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(SubjectAltNameFormat.IPV4, new SubjectAltNameStringType("127.0.0.1"));

        //    generate Iscf files, Corba for nodeFdn
        final IscfResponse iscfResponse = eserviceHolder.getIscfService().generate("LogicalName", nodeRef.getName(), SecurityLevel.LEVEL_2,
                SecurityLevel.LEVEL_1, "Label", subjectAltNameParam, ipsecAreas, EnrollmentMode.SCEP, CPP_MODEL_INFO);
        final byte[] xmlOutput = iscfResponse.getIscfContent();
        logger.info("XML output from ISCFGenerator: {}", new String(xmlOutput, "UTF-8"));
        assertNotNull("byte[] returned from ISCFGenerator is null", xmlOutput);
        assertTrue("byte[] returned from ISCFGenerator is of length 0", xmlOutput.length > 0);

        //    Get the corresponding EndEntities
        Entity corbaEE = null;
        try {
            corbaEE = nscsPkiManager.getPkiEntity(NscsPkiUtils.getEntityNameFromFdn(NodeEntityCategory.OAM, nodeFdn));
        } catch (final NscsPkiEntitiesManagerException ex) {
            fail("Corba EE not found");
        }
        assertNotNull("Corba EE for CPP node is null", corbaEE);

        Entity ipsecEE = null;
        try {
            ipsecEE = nscsPkiManager.getPkiEntity(NscsPkiUtils.getEntityNameFromFdn(NodeEntityCategory.IPSEC, nodeFdn));
        } catch (final NscsPkiEntitiesManagerException ex) {
            fail("IPSEC EE not found");
        }
        assertNotNull("IPSEC EE for CPP node is null", ipsecEE);

        // Get Entity profile names
        EntityProfile entityProfile = corbaEE.getEntityProfile();
        assertNotNull(entityProfile);
        final String cppOamEntityProfileName = entityProfile.getName();
        assertNotNull(cppOamEntityProfileName);

        entityProfile = ipsecEE.getEntityProfile();
        assertNotNull(entityProfile);
        final String cppIpsecEntityProfileName = entityProfile.getName();
        assertNotNull(cppIpsecEntityProfileName);

        // Modernize node by generating AP security data for type MSRBS_V2
        final SecurityDataResponse securityDataResponse = eserviceHolder.getIscfService().generateSecurityDataCombo(nodeRef.getName(),
                subjectAltNameParam, EnrollmentMode.CMPv2_VC, MSRBS_MODEL_INFO);
        assertEquals(1, securityDataResponse.getSecurityDataContainers().size());
        try {
            corbaEE = nscsPkiManager.getPkiEntity(NscsPkiUtils.getEntityNameFromFdn(NodeEntityCategory.OAM, nodeFdn));
        } catch (final NscsPkiEntitiesManagerException ex) {
            fail("Corba EE not found");
        }
        assertNotNull("Corba EE for MSRBS_V2 node is null", corbaEE);

        try {
            ipsecEE = nscsPkiManager.getPkiEntity(NscsPkiUtils.getEntityNameFromFdn(NodeEntityCategory.IPSEC, nodeFdn));
        } catch (final NscsPkiEntitiesManagerException ex) {
            fail("IPSEC EE not found");
        }
        assertNotNull("IPSEC EE for CPP node is null", ipsecEE);

        // Get Entity profile names
        entityProfile = corbaEE.getEntityProfile();
        assertNotNull(entityProfile);
        final String msrbsOamEntityProfileName = entityProfile.getName();
        assertNotNull(msrbsOamEntityProfileName);

        entityProfile = ipsecEE.getEntityProfile();
        assertNotNull(entityProfile);
        final String msrbsIpsecEntityProfileName = entityProfile.getName();
        assertNotNull(msrbsIpsecEntityProfileName);

        assertTrue("OAM Entity profile not updated after node modernization", !cppOamEntityProfileName.equals(msrbsOamEntityProfileName));
        assertTrue("IPSEC Entity profile not updated after node modernization", !cppIpsecEntityProfileName.equals(msrbsIpsecEntityProfileName));

        //    Call Iscf.cancel() to remove the end entities
        eserviceHolder.getIscfService().cancel(nodeFdn);

        dataSetup.deleteAllNodes();

        logger.info("-----------testIscfNodeModernizationCombo ends--------------");
    }

    @Override
    public void testRestIscfGeneratorRestInterface() throws Exception {

        logger.info("-----------testRestIscfGeneratorRestInterface starts--------------");

        dataSetup.deleteAllNodes();
        dataSetup.createNode(NODE_NAME, ModelDefinition.CmFunction.SyncStatusValue.UNSYNCHRONIZED.name(),
                NodeSecurityDataSetup.IpAddressVersion.IPv4);

        final HttpResponse response = restGenerateIscf("LEVEL_2", "LEVEL_1", NodeSecurityDataSetup.networkElementNameFromMeContextName(NODE_NAME),
                "TEST_RIC", "SCEP", "E.1.63", "ERBS");
        assertEquals("Status code should be 200 OK", 200, response.getStatusLine().getStatusCode());
        final String responseContent = EntityUtils.toString(response.getEntity(), "UTF-8");
        logger.info("XML output from ISCFGenerator (RestResource): {}", responseContent);
        assertTrue("Response should start with <?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>",
                responseContent.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"));
        assertTrue("Response should contain <secConfData>", responseContent.contains("<secConfData"));
        assertTrue("Response should contain </secConfData>", responseContent.contains("</secConfData>"));

        dataSetup.deleteAllNodes();

        logger.info("-----------testRestIscfGeneratorRestInterface ends--------------");
    }

    @Override
    public void testRestIscfGeneratorRestInterfaceIpsecTrafficAndOam() throws Exception {

        logger.info("-----------testRestIscfGeneratorRestInterfaceIpsecTrafficAndOam starts--------------");

        dataSetup.deleteAllNodes();
        dataSetup.createNode(NODE_NAME, ModelDefinition.CmFunction.SyncStatusValue.UNSYNCHRONIZED.name(),
                NodeSecurityDataSetup.IpAddressVersion.IPv4);

        final HttpResponse response = restGenerateIscfIpsec(NodeSecurityDataSetup.networkElementNameFromMeContextName(NODE_NAME), "IllogicalName",
                "192.0.1.2", "IPV4", "traffic", "om", "SCEP", "E.1.63", "ERBS");
        assertEquals("Status code should be 200 OK", 200, response.getStatusLine().getStatusCode());
        final String responseContent = EntityUtils.toString(response.getEntity(), "UTF-8");
        logger.info("XML output from ISCFGenerator (RestResource): {}", responseContent);
        assertTrue("Response should start with <?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>",
                responseContent.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"));
        assertTrue("Response should contain <secConfData>", responseContent.contains("<secConfData"));
        assertTrue("Response should contain </secConfData>", responseContent.contains("</secConfData>"));

        dataSetup.deleteAllNodes();

        logger.info("-----------testRestIscfGeneratorRestInterfaceIpsecTrafficAndOam ends--------------");
    }

    @Override
    public void testRestIscfGeneratorRestInterfaceShouldThrowErrorInvalidLevels() throws Exception {

        logger.info("-----------testRestIscfGeneratorRestInterfaceShouldThrowErrorInvalidLevels starts--------------");

        final HttpResponse response = restGenerateIscf("LEVEL_1", "LEVEL_2", "MYNODE", "IllogicalName", "SCEP", "E.1.49", "ERBS");
        assertEquals("Status code should be 500 INTERNAL_SERVER_ERROR", 500, response.getStatusLine().getStatusCode());

        logger.info("-----------testRestIscfGeneratorRestInterfaceShouldThrowErrorInvalidLevels ends--------------");
    }

    @Override
    public void testRestIscfGeneratorRestInterfaceShouldThrowErrorNullLevels() throws Exception {

        logger.info("-----------testRestIscfGeneratorRestInterfaceShouldThrowErrorNullLevels starts--------------");

        final HttpResponse response = restGenerateIscf(null, "LEVEL_2", "MYNODE1", "IllogicalName", "SCEP", "E.1.49", "ERBS");
        assertEquals("Status code should be 500 INTERNAL_SERVER_ERROR", 500, response.getStatusLine().getStatusCode());

        logger.info("-----------testRestIscfGeneratorRestInterfaceShouldThrowErrorNullLevels ends--------------");
    }

    @Override
    public void testRestIscfGeneratorRestInterfaceCombined() throws Exception {

        logger.info("-----------testRestIscfGeneratorRestInterfaceCombined starts--------------");

        final HttpResponse response = restGenerateIscfCombined("NODE4", "IllogicalName", "LEVEL_2", "LEVEL_1", "192.0.1.2", "IPV4", "traffic", "om",
                "SCEP", "E.1.49", "ERBS");
        assertEquals("Status code should be 200 OK", 200, response.getStatusLine().getStatusCode());
        final String responseContent = EntityUtils.toString(response.getEntity(), "UTF-8");
        logger.info("XML output from ISCFGenerator (RestResource): {}", responseContent);
        assertTrue("Response should start with <?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>",
                responseContent.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"));
        assertTrue("Response should contain <secConfData>", responseContent.contains("<secConfData"));
        assertTrue("Response should contain </secConfData>", responseContent.contains("</secConfData>"));

        logger.info("-----------testRestIscfGeneratorRestInterfaceCombined ends--------------");
    }

    private HttpResponse restGenerateIscf(final String wantedSecurityLevel, final String minimumSecurityLevel, final String nodeName,
            final String logicalName, final String wantedEnrollmentMode, final String mimVersion, final String nodeType) throws Exception {
        final String url = String.format(
                "http://%s:8080/node-security/test/iscf/seclevel?wanted=%s&minimum=%s&node=%s&logical=%s&mode=%s&version=%s&nodeType=%s",
                RestHelper.getLocalHostAddr(), wantedSecurityLevel, minimumSecurityLevel, nodeName, logicalName, wantedEnrollmentMode, mimVersion,
                nodeType);
        System.out.println("------------------" + url);
        final HttpGet httpget = new HttpGet(new URL(url).toExternalForm());
        final HttpClient httpclient = HttpClientBuilder.create().build();
        final HttpResponse response = httpclient.execute(httpget);
        return response;
    }

    private HttpResponse restGenerateIscfIpsec(final String nodeName, final String logicalName, final String subjectAltName,
            final String subjectAltNameType, final String ipsecArea1, final String ipsecArea2, final String wantedEnrollmentMode,
            final String mimVersion, final String nodeType

    ) throws Exception {
        final String url = String.format(
                "http://%s:8080/node-security/test/iscf/ipsec?node=%s&logical=%s&subjectAltName=%s&subjectAltNameType=%s&ipsecAreas=%s&ipsecAreas=%s&mode=%s&version=%s&nodeType=%s",
                RestHelper.getLocalHostAddr(), nodeName, logicalName, subjectAltName, subjectAltNameType, ipsecArea1, ipsecArea2,
                wantedEnrollmentMode, mimVersion, nodeType);
        System.out.println("------------------" + url);
        final HttpGet httpget = new HttpGet(new URL(url).toExternalForm());
        final HttpClient httpclient = HttpClientBuilder.create().build();
        final HttpResponse response = httpclient.execute(httpget);
        return response;
    }

    private HttpResponse restGenerateIscfCombined(final String nodeName, final String logicalName, final String wantedSecurityLevel,
            final String minimumSecurityLevel, final String subjectAltName, final String subjectAltNameType, final String ipsecArea1,
            final String ipsecArea2, final String wantedEnrollmentMode, final String mimVersion, final String nodeType

    ) throws Exception {
        final String url = String.format(
                "http://%s:8080/node-security/test/iscf/combined?node=%s&logical=%s&wanted=%s&minimum=%s&subjectAltName=%s&subjectAltNameType=%s&ipsecAreas=%s&ipsecAreas=%s&mode=%s&version=%s&nodeType=%s",
                RestHelper.getLocalHostAddr(), nodeName, logicalName, wantedSecurityLevel, minimumSecurityLevel, subjectAltName, subjectAltNameType,
                ipsecArea1, ipsecArea2, wantedEnrollmentMode, mimVersion, nodeType);
        System.out.println("------------------" + url);
        final HttpGet httpget = new HttpGet(new URL(url).toExternalForm());
        final HttpClient httpclient = HttpClientBuilder.create().build();
        final HttpResponse response = httpclient.execute(httpget);
        return response;
    }

}
