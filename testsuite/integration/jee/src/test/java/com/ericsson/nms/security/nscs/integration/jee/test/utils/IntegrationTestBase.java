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
package com.ericsson.nms.security.nscs.integration.jee.test.utils;

import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Arquillian.class)
public class IntegrationTestBase {

    private static final Logger log = LoggerFactory.getLogger(IntegrationTestBase.class);
    private String testStr = "Dummy test from IntegrationTest";

    @ArquillianResource
    private ContainerController controller;

    @ArquillianResource
    private Deployer deployer;

//    @ArquillianResource
//    Deployer deployer;
//
//    @Inject
//    ContextService ctxService;
//
//    private static final String XTorUser = "X-Tor-UserID";
//    private static final String XTorUserValue = "Administrator";
//    private static boolean deploymentComplete = false;
//    private static final String propertiesFileName = "jboss.properties";
//    private static final String arquillianDeployProperty = "arquillian.deploy";
    
//    @Before
//    public void setUp() {
//        log.info("Arquillian Set Up starting ....");
//        if (!deploymentComplete) {
//            log.info("Arquillian Deployment starting ....");
//            if (deployArtifacts()) {
//                log.info("Arquillian Deploy Artifact starting ....");
//                // Complete  deployment for artifacts annotated with (managed=false). Not for  docker  tests !
//                deployer.deploy("pib-ear");
//                deployer.deploy("script-engine-ear");
//                deployer.deploy("mock-pki-manager-ear");
//                deployer.deploy("wfs-ear");
//                deployer.deploy("security-workflows");
//                deployer.deploy("cm-writer-ear");
//                deployer.deploy("cm-reader-ear");
//                deployer.deploy("identity-manager-mock-service-ear");
//                deployer.deploy("smrs-mock-service-ear");
//                log.info(".... Arquillian Deploy Artifact finished ");
//            }
//            deploymentComplete = true;
//            log.info(".... Arquillian Deployment finished ");
//        }
//        ctxService.setContextValue(XTorUser, XTorUserValue);
//        log.info(".... Arquillian Set Up finished ");
//    }

//    protected InputStream getConfigurationAsStream(final String fileName) {
//        Resource fileResource = Resources.getFileSystemResource(fileName);
//
//        if ((fileResource == null) || !fileResource.exists()) {
//            return null;
//        }
//        return fileResource.getInputStream();
//    }


//    private boolean deployArtifacts() {
//        Properties props;
//        try {
//            props = PropertiesReader.getConfigProperties();
//        } catch (PropertiesFileNotFoundException exc) {
//            return false;    // Deploy artifacts disabled
//        }
//        return ((props != null) &&
//                ("true".equalsIgnoreCase(props.getProperty(arquillianDeployProperty, "false"))));
//    }

}
