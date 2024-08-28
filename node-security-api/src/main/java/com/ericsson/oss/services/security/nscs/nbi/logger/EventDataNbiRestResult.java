/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2024
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.nbi.logger;

/**
 * NBI REST result as reported in SFWK event data.
 */
public enum EventDataNbiRestResult {
    SUCCESS,
    ERROR;

    /**
     * Return the REST identifier as string used in SFWK event data.
     * 
     * @return the REST identifier.
     */
    public String toEventData() {
        return this.name();
    }
}
