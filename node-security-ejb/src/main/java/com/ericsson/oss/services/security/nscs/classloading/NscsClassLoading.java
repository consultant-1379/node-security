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

import javax.ejb.Local;

/**
 * Interface to test NSCS class loading.
 */
@Local
public interface NscsClassLoading {

    /**
     * Returns the version of package containing the given class.
     * 
     * @param fullclassname
     *            the full class name.
     * @return the version.
     */
    public String getPackageVersion(final String fullclassname);

}
