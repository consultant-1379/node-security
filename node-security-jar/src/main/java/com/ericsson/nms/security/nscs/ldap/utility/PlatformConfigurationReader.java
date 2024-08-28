/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.ldap.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

/**
 * This class is responsible for reading the platform properties file. This
 * class lazily reads the properties.
 * 
 * @author xsrirko
 * 
 */
@ApplicationScoped
public class PlatformConfigurationReader {

    @Inject
    private Logger log;

    private final Properties properties = new Properties();
    private boolean initializationSuccessful = false;
    private int attempts = 0;

    public PlatformConfigurationReader() {
    }

    public void initialize() {
        if (!initializationSuccessful) {
            synchronized (properties) {
                if (!initializationSuccessful) {
                    final String filename = System.getProperty(LdapConstants.CONFIGURATION_JAVA_PROPERTIES, LdapConstants.GLOBAL_PROPERTIES_PATH);
                    try (final FileInputStream stream = new FileInputStream(filename)) {
                        properties.load(stream);
                        initializationSuccessful = true;
                    } catch (IOException e) {
                        ++attempts;
                        log.error("Could not Load Platform Properties File {} for {} times", filename, attempts);
                    }
                }
            }
        }
    }

    public String getProperty(final String key) {
        initialize();
        return properties.getProperty(key);
    }
}
