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
package com.ericsson.nms.security.nscs.integration.jee.test.setup;

import java.io.File;
import java.util.Set;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.*;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.mock.*;
import java.util.HashSet;

public class IntegrationTestDeploymentFactory {

    private static final Logger log = LoggerFactory.getLogger(IntegrationTestDeploymentFactory.class);
    private static final File MANIFEST_MF_FILE = new File("src/test/resources/META-INF/MANIFEST.MF");
    private static final File BEANS_XML_FILE = new File("src/test/resources/META-INF/beans.xml");
    private static final String idmSfwkConfigPropertiesFilePath = "src/test/resources/SfwkConfigurationIdm.properties";
    private static final String smrsSfwkConfigPropertiesFilePath = "src/test/resources/SfwkConfigurationSmrs.properties";
    private static final String testSfwkConfigPropertiesFilePath = "src/test/resources/SfwkConfigurationNscsTest.properties";
    private static final File SFWK_IDM_FILE = new File(idmSfwkConfigPropertiesFilePath);
    private static final File SFWK_SMRS_FILE = new File(smrsSfwkConfigPropertiesFilePath);
    private static final File SFWK_TEST_FILE = new File(testSfwkConfigPropertiesFilePath);


    /**
     * Create deployment from given maven coordinates
     *
     * @param mavenCoordinates
     *            Maven coordinates in form of groupId:artifactId:type
     * @return Deployment archive represented by this maven artifact
     */
    public static EnterpriseArchive createEARDeploymentFromMavenCoordinates(final String mavenCoordinates) {
        log.debug("******Creating deployment {} for test******", mavenCoordinates);
        final File archiveFile = IntegrationTestDependencies.resolveArtifactWithoutDependencies(mavenCoordinates);
        if (archiveFile == null) {
            throw new IllegalStateException("Unable to resolve artifact " + mavenCoordinates);
        }
        final EnterpriseArchive ear = ShrinkWrap.createFromZipFile(EnterpriseArchive.class, archiveFile);

        log.debug("******Created from maven artifact with coordinates {} ******", mavenCoordinates);
        return ear;
    }

    /**
     * Create deployment from given maven coordinates
     *
     * @param mavenCoordinates
     *            Maven coordinates in form of groupId:artifactId:type
     * @return Deployment archive represented by this maven artifact
     */
    public static JavaArchive createJARDeploymentFromMavenCoordinates(final String mavenCoordinates) {
        log.debug("******Creating deployment {} for test******", mavenCoordinates);
        final File archiveFile = IntegrationTestDependencies.resolveArtifactWithoutDependencies(mavenCoordinates);
        if (archiveFile == null) {
            throw new IllegalStateException("Unable to resolve artifact " + mavenCoordinates);
        }
        final JavaArchive jar = ShrinkWrap.createFromZipFile(JavaArchive.class, archiveFile);

        log.debug("******Created from maven artifact with coordinates {} ******", mavenCoordinates);
        return jar;
    }

    /**
     * Create web archive
     *
     * @param name name
     * @return web archive
     */
    public static WebArchive createWarDeployment(final String name) {
        log.debug("******Creating war deployment {} for test******", name);
        final WebArchive war = ShrinkWrap.create(WebArchive.class, name);
        log.debug("******Created from maven artifact with coordinates {} ******..", name);
        return war;
    }

    public static Archive<?> createEarTestDeployment(final Set<Package> testPackagesToAdd, 
                                                     final Set<File> testResourcesToAdd,
                                                     final String name) {

        log.debug("******Creating ear test deployment {} for test******", name);
        final EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, name);
        final PomEquippedResolveStage resolver = getMavenResolver();

        ear.addAsModule(createModuleArchive(testPackagesToAdd, testResourcesToAdd));
        ear.addAsApplicationResource(BEANS_XML_FILE);
        ear.setManifest(MANIFEST_MF_FILE);
        ear.addAsResource(SFWK_TEST_FILE, "ServiceFrameworkConfiguration.properties");
        
        //(1) API dependencies
        ear.addAsLibraries(resolver.resolve(Dependencies.NODE_SECURITY_API).withTransitivity().asFile());
        ear.addAsLibraries(resolver.resolve(Dependencies.PKI_MANAGER_PROF_MAN_API).withTransitivity().asFile());
        ear.addAsLibraries(resolver.resolve(Dependencies.PKI_MANAGER_CERT_MAN_API).withTransitivity().asFile());
        ear.addAsLibraries(resolver.resolve(Dependencies.PKI_MANAGER_COMMON_MODEL).withTransitivity().asFile());
        ear.addAsLibraries(resolver.resolve(Dependencies.PKI_COMMON_MODEL).withTransitivity().asFile());
        ear.addAsLibraries(resolver.resolve(Dependencies.PKI_MANAGER_API).withTransitivity().asFile());
        ear.addAsLibraries(resolver.resolve(Dependencies.SMRS_SERVICE_API).withTransitivity().asFile());

        // To re-work mock-pki-manager repo uplift to eap7
        ear.addAsLibraries(resolver.resolve(Dependencies.MOCK_PKI_MANAGER_API).withTransitivity().asFile());

        //(2) Indirect dependencies
        // To re-work mock-pki-manager repo uplift to eap7
        ear.addAsLibraries(resolver.resolve(Dependencies.NODE_SECURITY_MODEL_JAR).withTransitivity().asFile());
        ear.addAsLibraries(resolver.resolve(Dependencies.NODE_SECURITY_JAR).withTransitivity().asFile());

        //(3) Libraries needed to perform some tests
        ear.addAsLibraries(resolver.resolve(Dependencies.REST_EASY).withTransitivity().asFile());
        ear.addAsLibraries(resolver.resolve(Dependencies.WFS_REMOTE_API).withTransitivity().asFile());

        //(5) Key management
        ear.addAsLibraries(resolver.resolve(Dependencies.KEY_GENERATOR_JAR).withTransitivity().asFile());
        ear.addAsLibraries(resolver.resolve(Dependencies.BCPROC_LIBRARY_JAR).withTransitivity().asFile());

        return ear;
    }

    /**
     * This is used to setup the module configuration
     *
     * @param testPackagesToAdd testPackagesToAdd
     * @param resourceFilesToAdd resourceFilesToAdd
     * @return Archive
     */
    protected static Archive<?> createModuleArchive(final Set<Package> testPackagesToAdd, 
                                                    final Set<File> resourceFilesToAdd) {
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "nscs-test-bean-lib.jar")
                .addAsResource("META-INF/beans.xml", "META-INF/beans.xml").addAsResource("META-INF/MANIFEST.MF", "META-INF/MANIFEST.MF")
                .addAsResource("ISCF.xsd", "ISCF.xsd");
        if ((resourceFilesToAdd != null) && !resourceFilesToAdd.isEmpty()) {
            for (final File resourceFile : resourceFilesToAdd) {
                archive.addAsResource(resourceFile, resourceFile.getName());
            }
        }
        if ((testPackagesToAdd != null) && !testPackagesToAdd.isEmpty()) {
            log.info("add packages");
            for (final Package p : testPackagesToAdd) {
                log.info("add packages {} ", p.getName());
                archive.addPackage(p);
            }
        }
        return archive;
    }

    public static Archive<?> createIdManagerDeployment() {
        log.info("Generating IdentityManagementService mocked service");
        final EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "identity-manager-mock-service-ear.ear");
        final PomEquippedResolveStage resolver = getMavenResolver();
        ear.setManifest("META-INF/MANIFEST.MF");

        ear.addAsLibraries(resolver.resolve(Dependencies.ID_MANAGER_SERVICE_API).withTransitivity().asFile());
        //Add IDM MOCK implementation
        final JavaArchive idManager = ShrinkWrap.create(JavaArchive.class, "identitymanagement-service.jar");
        idManager.addAsResource(SFWK_IDM_FILE, "ServiceFrameworkConfiguration.properties");
        idManager.addAsResource(BEANS_XML_FILE);
        idManager.addAsResource(MANIFEST_MF_FILE);
        //Add Mocked class
        idManager.addClass(IdentityManagementServiceBean.class.getName(), IdentityManagementServiceBean.class.getClassLoader());
        idManager.addClass(ComAAInfoImpl.class.getName(), ComAAInfoImpl.class.getClassLoader());
        ear.addAsModule(idManager);
        return ear;
    }

    public static Archive<?> createSmrsMockDeployment() {
        log.info("Generating SMRS mocked service");
        final EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "smrs-mock-service-ear.ear");
        final PomEquippedResolveStage resolver = getMavenResolver();
        ear.setManifest("META-INF/MANIFEST.MF");

        ear.addAsLibraries(resolver.resolve(Dependencies.SMRS_SERVICE_API).withTransitivity().asFile());
        //Add SMRS MOCK implementation
        final JavaArchive smrsManager = ShrinkWrap.create(JavaArchive.class, "smrs-service.jar");
        smrsManager.addAsResource(SFWK_SMRS_FILE, "ServiceFrameworkConfiguration.properties");
        smrsManager.addAsResource(BEANS_XML_FILE);
        smrsManager.addAsResource(MANIFEST_MF_FILE);
        //Add Mocked class
        smrsManager.addClass(SmrsServiceBeanMock.class.getName(), SmrsServiceBeanMock.class.getClassLoader());
        ear.addAsModule(smrsManager);
        return ear;
    }

    private static PomEquippedResolveStage getMavenResolver() {
        return Maven.resolver().loadPomFromFile("pom.xml");
    }

    public static Set<File> getTestResources(Set<String> resourcePaths) {
        final Set<File> resourceSet = new HashSet<>();
        if ((resourcePaths != null) && !resourcePaths.isEmpty()) {
            for (final String resourcePath : resourcePaths) {
                File[] files = new File(resourcePath).listFiles();
                for (final File file : files) {
                    if (file.isFile()) {
                        resourceSet.add(file);
                    }
                }
            }
        }
        return resourceSet;
    }

    public static String getMavenVersionFromSystemProps (final String mavenCoordinates, final String systemProp) {
        final String version = System.getProperty(systemProp);
        
        if (( version == null ) || version.isEmpty()) {
            throw new IllegalStateException("DPS version system property is not set");
        }

        return mavenCoordinates.replace(Dependencies.TAG_VERSION, version);
    }
}
