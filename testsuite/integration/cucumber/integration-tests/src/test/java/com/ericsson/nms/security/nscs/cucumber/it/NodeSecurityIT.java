/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.cucumber.it;

import java.io.IOException;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.cucumber.helper.EServiceProducer;
import com.ericsson.nms.security.nscs.cucumber.helper.NodeSecurityCredentialsSetup;
import com.ericsson.nms.security.nscs.cucumber.helper.laad.PasswordHelper;
import com.ericsson.nms.security.nscs.cucumber.setup.Dependencies;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.oss.cucumber.arquillian.api.GluePackages;
import com.ericsson.oss.cucumber.arquillian.runtime.ArquillianCucumberBlast;
import com.ericsson.oss.services.security.nscs.cucumber.test.steps.helper.NscsCapabilityDefinition;
import com.ericsson.oss.services.test.deployment.Deployments;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.runtime.arquillian.api.Features;

@RunWith(ArquillianCucumberBlast.class)
@Features({ "ISCF.feature", "SNMPv3.feature", "Credentials.feature", "Targets.feature", "Capabilities.feature", "NotCapabilities.feature",
        "LaadDistribution.feature", "TrustDistributionForLaad.feature", "ExternalCATrustDistribution.feature",
        "ExternalCACertificateEnrollment.feature", "ExternalCACertificateReissue.feature", "Gdpr.feature", "GdprRest.feature", "NtpConfigure.feature",
        "NtpList.feature", "NtpRemove.feature", "SshkeyImport.feature" })
@GluePackages({ "com.ericsson.nms.security.nscs.cucumber.steps", "com.ericsson.oss.services.test.step",
        "com.ericsson.oss.services.security.nscs.cucumber.test.steps" })
public class NodeSecurityIT {

    @Inject
    private NodeSecurityCredentialsSetup nodeSecurityCredentialsSetup;

    private static final Logger logger = LoggerFactory.getLogger(NodeSecurityIT.class);

    private static final String[] deploymentDependencies = { Dependencies.SCRIPT_ENGINE_EDITOR_SPI_JAR, Dependencies.NODE_SECURITY_API,
            Dependencies.PKI_MANAGER_PROF_MAN_API, Dependencies.PKI_MANAGER_CERT_MAN_API, Dependencies.PKI_MANAGER_COMMON_MODEL,
            Dependencies.PKI_MANAGER_CONF_MAN_API, Dependencies.PKI_MANAGER_CRL_MAN_API, Dependencies.PKI_COMMON_MODEL, Dependencies.PKI_MANAGER_API,
            Dependencies.MOCK_PKI_MANAGER_API, Dependencies.SMRS_SERVICE_API, Dependencies.NODE_SECURITY_MODEL_JAR, Dependencies.WFS_REMOTE_API,
            Dependencies.WFS_API, Dependencies.REST_EASY, Dependencies.SDK_DIST, Dependencies.KEY_GENERATOR_JAR, Dependencies.BCPROC_LIBRARY_JAR,
            Dependencies.CORE_MEDIATION_API, Dependencies.FM_MODEL_JAR, Dependencies.RS_API_JAR, Dependencies.NODE_GDPR_API };

    private static final Class<?>[] jarClasses = { NodeSecurityIT.class };

    private static final Package[] packages = { EServiceProducer.class.getPackage(), ModelDefinition.class.getPackage(),
            NscsCapabilityDefinition.class.getPackage(), PasswordHelper.class.getPackage() };

    private static final String[] resourceFiles = { "src/test/resources/ServiceFrameworkConfiguration.properties",
            "src/test/resources/data/Capabilities/NscsTargetDefinitions.json", "src/test/resources/data/Capabilities/NscsCapabilityDefinitions.json",
            "src/test/resources/data/Capabilities/NscsCapabilitySupportDefinitions.json", "src/test/resources/data/Capabilities/RootMoInfo.json",
            "src/test/resources/data/Capabilities/IsKSandEMSupported.json",
            "src/test/resources/data/Capabilities/IsCertificateAuthorityDnSupported.json", "src/test/resources/data/externalca/trust.xml",
            "src/test/resources/data/externalca/internalCaEnrollment.xml", "src/test/resources/data/externalca/externalCaEnrollment.xml",
            "src/test/resources/data/externalca/externalCaEnrollmentNoTrust.xml", "src/test/resources/data/externalca/externalCaEnrollmentMultipleNodes.xml",
            "src/test/resources/data/externalca/trustMultipleNodes.xml", "src/test/resources/data/Sshkey/NodeSshPrivateKeyValidFile.txt" };
    private static final String[] infoResourceFiles = { "src/test/resources/jboss-deployment-structure.xml" };

    /**
     * Deploy war archive with our taste case. Here we are using standard arquillian testing approach, where we deploy war containing our test case(s)
     *
     * @return Web archive with our test case.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Deployment(testable = true)
    public static Archive<?> createTestableWarModuleDeployment() throws IOException, InterruptedException {
        logger.info("<------- createTestableWarModuleDeployment ------->");

        final Deployments.CustomModuleInfo cmi = new Deployments.CustomModuleInfo();
        return Deployments.createTestableModule(
                cmi.name("NscsCucumberTest").jarClasses(jarClasses).packages(packages).artifactsWithoutDep(deploymentDependencies)
                        .resourceFiles(resourceFiles).infoResourceFiles(infoResourceFiles).manifestDependencies("org.slf4j"));

    }

    @Before
    public void beforeTest() {
        logger.info("************** before ***********");
    }

    @After
    public void afterTest() throws Exception {
        logger.info("************** after***********");
        nodeSecurityCredentialsSetup.deleteNodes("OSS_NE_DEF", "NetworkElement");
        nodeSecurityCredentialsSetup.deleteNodes("OSS_NE_DEF", "NetworkFunctionVirtualizationOrchestrator");
    }

}