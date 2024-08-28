/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2021
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NscsPacketVersionUtil {

    private static Logger logger = LoggerFactory.getLogger(NscsPacketVersionUtil.class);

    private NscsPacketVersionUtil() {
        throw new IllegalStateException("NscsPacketVersionUtil class");
    }

    /**
     * Dumps packet version containing the given class specified by its full class name.
     * 
     * @param fullClassName
     *            the full class name.
     */
    public static void dumpPacketVersion(final String fullClassName) {
        try {
            final Class<?> target = Class.forName(fullClassName);
            final String specVersion = target.getPackage().getSpecificationVersion();
            final String implVersion = target.getPackage().getImplementationVersion();
            logger.info("Full class name : {}, specification version : {}, impl version: {}", fullClassName, specVersion, implVersion);
        } catch (final ClassNotFoundException e) {
            final String errorMsg = String.format("Full class name : %s not found", fullClassName);
            logger.error(errorMsg, e);
        }
    }

}
