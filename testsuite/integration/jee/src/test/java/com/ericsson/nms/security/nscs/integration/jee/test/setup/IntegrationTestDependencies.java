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

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegrationTestDependencies {

    private static final Logger log = LoggerFactory.getLogger(IntegrationTestDependencies.class);
    /**
     * Maven resolver that will try to resolve dependencies using pom.xml of the project where this class is located.
     *
     * @return MavenDependencyResolver
     */
    public static PomEquippedResolveStage getMavenResolver() {
        return Maven.resolver().loadPomFromFile("pom.xml");
    }

    /**
     * Resolve artifacts without dependencies
     * 
     * @param artifactCoordinates
     * @return
     */
    public static File resolveArtifactWithoutDependencies(final String artifactCoordinates) {

        final File[] artifacts = getMavenResolver()
                .addDependencies(MavenDependencies.createDependency(artifactCoordinates, ScopeType.COMPILE, false))
                .resolve().withTransitivity().asFile();

        if (artifacts == null) {
            throw new IllegalStateException("Artifact with coordinates " + artifactCoordinates + " was not resolved");
        }
        if (artifacts.length != 1) {
            log.warn("Resolved more then one artifact with coordinates: {}", artifactCoordinates);
        }
        return artifacts[0];
    }

}
