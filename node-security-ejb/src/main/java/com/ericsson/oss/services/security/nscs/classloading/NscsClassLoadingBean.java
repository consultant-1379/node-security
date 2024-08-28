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
package com.ericsson.oss.services.security.nscs.classloading;

import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class NscsClassLoadingBean implements NscsClassLoading {

    private Logger logger = LoggerFactory.getLogger(NscsClassLoadingBean.class);

    @Override
    public String getPackageVersion(final String fullclassname) {
        final StringBuilder strb = new StringBuilder();
        try {
            final Class<?> target = Class.forName(fullclassname);
            strb.append("Specification version: ").append(target.getPackage().getSpecificationVersion()).append("\n");
            strb.append("Implementation version: ").append(target.getPackage().getImplementationVersion()).append("\n");
        } catch (final Exception e) {
            logger.error("Caught exception", e);
            strb.append("Exception: ").append(e.getClass().getSimpleName()).append(" Caused by: ").append(e.getCause());
        }
        return strb.toString();
    }

}
