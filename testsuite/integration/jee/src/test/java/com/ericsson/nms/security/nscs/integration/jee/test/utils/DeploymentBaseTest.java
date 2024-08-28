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
package com.ericsson.nms.security.nscs.integration.jee.test.utils;

import java.util.HashSet;
import java.util.Set;

import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;

import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.moget.MOGetServiceFactory;
import com.ericsson.nms.security.nscs.ejb.pkiwrap.NscsPkiEntitiesManager;
import com.ericsson.nms.security.nscs.ejb.pkiwrap.cache.PkiCachedCallsImpl;
import com.ericsson.nms.security.nscs.integration.jee.test.cache.NodeSecurityReplicatedCacheSortingTests;
import com.ericsson.nms.security.nscs.integration.jee.test.command.CreateCredentialsTests;
import com.ericsson.nms.security.nscs.integration.jee.test.command.ciphersconfig.SetCiphersTest;
import com.ericsson.nms.security.nscs.integration.jee.test.cpp.CppSecurityServiceTests;
import com.ericsson.nms.security.nscs.integration.jee.test.events.FMEventTests;
import com.ericsson.nms.security.nscs.integration.jee.test.gim.TestEcimPwdFileManager;
import com.ericsson.nms.security.nscs.integration.jee.test.gim.TestEcimUserRemote;
import com.ericsson.nms.security.nscs.integration.jee.test.ipsec.IpSecWorkflowTest;
import com.ericsson.nms.security.nscs.integration.jee.test.iscf.ISCFGeneratorTests;
import com.ericsson.nms.security.nscs.integration.jee.test.moaction.MOActionTest;
import com.ericsson.nms.security.nscs.integration.jee.test.producer.EServiceProducer;
import com.ericsson.nms.security.nscs.integration.jee.test.rest.RestHelper;
import com.ericsson.nms.security.nscs.integration.jee.test.rest.RestTests;
import com.ericsson.nms.security.nscs.integration.jee.test.setup.Dependencies;
import com.ericsson.nms.security.nscs.integration.jee.test.setup.IntegrationTestDeploymentFactory;
import com.ericsson.nms.security.nscs.integration.jee.test.ssh.SSHHandlerServiceIntegrationTest;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityCPPNodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityCiscoAsr9000NodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityCiscoAsr900NodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityJuniperMxNodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityMiniLinkIndoorNodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityRadioNodesDataSetup;
import com.ericsson.nms.security.nscs.integration.jee.test.wftasks.MoActionWithParamTasksTests;
import com.ericsson.nms.security.nscs.integration.jee.test.workflow.WorkflowTests;
import com.ericsson.nms.security.nscs.test.pki.TestPkiCACertificateManagementService;
import com.ericsson.nms.security.nscs.test.pki.TestPkiConfigurationManagementService;
import com.ericsson.nms.security.nscs.test.pki.TestPkiEntityCertificateManagementService;
import com.ericsson.nms.security.nscs.test.pki.TestPkiEntityManagementServiceImpl;
import com.ericsson.nms.security.nscs.test.pki.TestPkiProfileManagementServiceImpl;
import com.ericsson.oss.services.nscs.jobs.NscsJobCacheHandlerImpl;
import com.ericsson.oss.services.nscs.model.service.NscsModelServiceBean;

@ArquillianSuiteDeployment
public class DeploymentBaseTest {

    @Deployment(name = "neo4j-jca-rar", managed = true, testable = false, order = 1)
    public static Archive<?> deployRARService() {
        String mavenCoordinates = IntegrationTestDeploymentFactory.getMavenVersionFromSystemProps(Dependencies.NEO4J_JCA_RAR, Dependencies.VERSION_NEO4J_JCA_RAR);
        return IntegrationTestDeploymentFactory.createEARDeploymentFromMavenCoordinates(mavenCoordinates);
    }

    @Deployment(name = "dps-ear-eap7", managed = true, testable = false, order = 1)
    public static Archive<?> createADeployableDPSEAR() {
        String mavenCoordinates = IntegrationTestDeploymentFactory.getMavenVersionFromSystemProps(Dependencies.DPS_EAR_EAP7, Dependencies.VERSION_DPS);
        return IntegrationTestDeploymentFactory.createEARDeploymentFromMavenCoordinates(mavenCoordinates);
    }

    @Deployment(name = "dps-neo4j-ear-eap7", managed = true, testable = false, order = 1)
    public static Archive<?> createADeployableDPSNEO4JEAR() {
        String mavenCoordinates = IntegrationTestDeploymentFactory.getMavenVersionFromSystemProps(Dependencies.DPS_NEO4J_EAR_EAP7, Dependencies.VERSION_DPS);
        return IntegrationTestDeploymentFactory.createEARDeploymentFromMavenCoordinates(mavenCoordinates);
    }

    @Deployment(name = "dps-testsuite-mock-data-access-delegate-ejb", managed = true, testable = false, order = 1)
    public static Archive<?> createADeployableDPSTESTSUITEMOCKEJB() {
        String mavenCoordinates = IntegrationTestDeploymentFactory.getMavenVersionFromSystemProps(Dependencies.DPS_TESTSUITE_MOCK_EJB, Dependencies.VERSION_DPS);
        return IntegrationTestDeploymentFactory.createEARDeploymentFromMavenCoordinates(mavenCoordinates);
    }

    @Deployment(name = "node-security-ear", managed = true, testable = false, order = 2)
    public static Archive<?> deployEARService() {
        return IntegrationTestDeploymentFactory.createEARDeploymentFromMavenCoordinates(Dependencies.NODE_SECURITY_EAR);
    }

    @Deployment(name = "pib-ear", testable = false, order = 3)
    public static Archive<?> createADeployablePIBEAR() {
        return IntegrationTestDeploymentFactory.createEARDeploymentFromMavenCoordinates(Dependencies.PIB_EAR);
    }

    @Deployment(name = "script-engine-ear", managed = false, testable = false, order = 4)
    public static Archive<?> createADeployableScriptEngineEAR() {
        return IntegrationTestDeploymentFactory.createEARDeploymentFromMavenCoordinates(Dependencies.SCRIPT_ENGINE_EAR);
    }

    @Deployment(name = "cm-writer-ear", managed = false, testable = false, order = 5)
    public static Archive<?> createADeployableCMWriterEAR() {
        return IntegrationTestDeploymentFactory.createEARDeploymentFromMavenCoordinates(Dependencies.CM_WRITER_EAR);
    }

    @Deployment(name = "cm-reader-ear", managed = false, testable = false, order = 6)
    public static Archive<?> createADeployableCMReaderEAR() {
        return IntegrationTestDeploymentFactory.createEARDeploymentFromMavenCoordinates(Dependencies.CM_READER_EAR);
    }

    @Deployment(name = "wfs-ear", testable = false, order = 7)
    public static Archive<?> createWfsArchive() {
        return IntegrationTestDeploymentFactory.createEARDeploymentFromMavenCoordinates(Dependencies.WFS_EAR);
    }

    @Deployment(name = "security-workflows", managed = false, testable = false, order = 8)
    public static Archive<?> createSecurityWorkflowsWar() {
        return IntegrationTestDeploymentFactory.createEARDeploymentFromMavenCoordinates(Dependencies.SECURITY_WORKFLOWS_WAR);
    }

    @Deployment(name = "mock-pki-manager-ear", testable = false, order = 9)
    public static Archive<?> createADeployableMockPKIManagerEAR() {
        return IntegrationTestDeploymentFactory.createEARDeploymentFromMavenCoordinates(Dependencies.MOCK_PKI_MANAGER_EAR);
    }

    @Deployment(name = "identity-manager-mock-service-ear", testable = false, order = 10)
    public static Archive<?> createADeployableIdentityManagementEAR() {
        return IntegrationTestDeploymentFactory.createIdManagerDeployment();
    }

    @Deployment(name = "smrs-mock-service-ear", testable = false, order = 11)
    public static Archive<?> createADeployableSmrsEAR() {
        return IntegrationTestDeploymentFactory.createSmrsMockDeployment();
    }

    @Deployment(name = "nscs-test-ear.ear", order = 12)
    public static Archive<?> createWarWithIntegrationTest() {
        final Set<Package> testPackages = new HashSet<>();

        // Common from integration test
        //testPackages.add(DeploymentBaseTest.class.getPackage());
        testPackages.add(IntegrationTest.class.getPackage());
        testPackages.add(EServiceProducer.class.getPackage());
        testPackages.add(RestHelper.class.getPackage());
        // Common Node data
        testPackages.add(NodeSecurityDataSetup.class.getPackage());
        testPackages.add(NodeSecurityRadioNodesDataSetup.class.getPackage());
        testPackages.add(NodeSecurityCPPNodesDataSetup.class.getPackage());
        testPackages.add(NodeSecurityMiniLinkIndoorNodesDataSetup.class.getPackage());
        testPackages.add(NodeSecurityCiscoAsr9000NodesDataSetup.class.getPackage());
        testPackages.add(NodeSecurityCiscoAsr900NodesDataSetup.class.getPackage());
        testPackages.add(NodeSecurityJuniperMxNodesDataSetup.class.getPackage());

        // Common from node-security-ejb
        testPackages.add(NscsJobCacheHandlerImpl.class.getPackage());
        testPackages.add(NscsPkiEntitiesManager.class.getPackage());
        testPackages.add(PkiCachedCallsImpl.class.getPackage());
        testPackages.add(MOGetServiceFactory.class.getPackage());
        testPackages.add(NscsCapabilityModelService.class.getPackage());
        testPackages.add(NscsModelServiceBean.class.getPackage());

        // PKI mock classes
        testPackages.add(TestPkiEntityManagementServiceImpl.class.getPackage());
        testPackages.add(TestPkiProfileManagementServiceImpl.class.getPackage());
        testPackages.add(TestPkiCACertificateManagementService.class.getPackage());
        testPackages.add(TestPkiConfigurationManagementService.class.getPackage());
        testPackages.add(TestPkiEntityCertificateManagementService.class.getPackage());

        // GIM mock classes
        testPackages.add(TestEcimUserRemote.class.getPackage());
        testPackages.add(TestEcimPwdFileManager.class.getPackage());

        // WF
        testPackages.add(WorkflowTests.class.getPackage());

        // Create Creds
        testPackages.add(CreateCredentialsTests.class.getPackage());

        // CppSercurityServiceBean
        testPackages.add(CppSecurityServiceTests.class.getPackage());

        // ISCFGeneratorBean
        testPackages.add(ISCFGeneratorTests.class.getPackage());

        // Events
        testPackages.add(FMEventTests.class.getPackage());

        // REST test
        testPackages.add(RestTests.class.getPackage());

        // MOACtion test
        testPackages.add(MOActionTest.class.getPackage());

        // WFTaskTests
        testPackages.add(MoActionWithParamTasksTests.class.getPackage());

        //cache
        testPackages.add(NodeSecurityReplicatedCacheSortingTests.class.getPackage());

        //ipsec
        testPackages.add(IpSecWorkflowTest.class.getPackage());

        //ssh
        testPackages.add(SSHHandlerServiceIntegrationTest.class.getPackage());


        // CiphersConfigurations
        testPackages.add(SetCiphersTest.class.getPackage());

        // Get resources for tests
        final Set<String> resourcePaths = new HashSet<>();
        resourcePaths.add(Dependencies.CIPHERS_RESOURCE_PATH);
        resourcePaths.add(Dependencies.CRL_RESOURCE_PATH);
        resourcePaths.add(Dependencies.LDAP_RESOURCE_PATH);
        resourcePaths.add(Dependencies.ISSUE_RESOURCE_PATH);

        return IntegrationTestDeploymentFactory.createEarTestDeployment(testPackages,
                IntegrationTestDeploymentFactory.getTestResources(resourcePaths), "nscs-test-ear.ear");
    }

}
