/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.itpf.sdk.security.accesscontrol.classic;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.*;

/* Implementation of EAccessControl interface for testing.
 */
public class EAccessControlBypassAuthnImpl extends EAccessControlImpl implements EAccessControl {
    private static final Logger logger = LoggerFactory.getLogger(EAccessControlBypassAuthnImpl.class);

    @Override
    public ESecuritySubject getAuthUserSubject() throws SecurityViolationException {
        logger.warn("************************************************************");
        logger.warn("EAccessControlBypassAuthnImpl IS NOT FOR PRODUCTION USE.");
        logger.warn("EAccessControlBypassAuthnImpl: getAuthUserSubject called.");
        logger.warn("************************************************************");

        // get userid from currentAuthUser file in tmpDir
        final String tmpDir = System.getProperty("java.io.tmpdir");
        final String useridFile = String.format("%s/currentAuthUser", tmpDir);

        String toruser;
        try {
            toruser = new String(Files.readAllBytes(Paths.get(useridFile)));
        } catch (final IOException ioe) {
            logger.error("EAccessControlBypassAuthnImpl: Error reading {}, Details: {}", useridFile, ioe.getMessage());
            toruser = "ioerror";
        }
        logger.info("getAuthUserSubject: toruser is <{}>", toruser);
        return new ESecuritySubject(toruser);
    }
}