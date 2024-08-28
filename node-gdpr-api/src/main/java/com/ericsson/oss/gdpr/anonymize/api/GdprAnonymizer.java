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
package com.ericsson.oss.gdpr.anonymize.api;

import javax.ejb.Remote;

import com.ericsson.oss.itpf.sdk.core.annotation.EService;

/**
 * GdprAnonymizer interface invoked by Remote Ejbs.
 * No @Authorize annotation is necessary
 */
@EService
@Remote
public interface GdprAnonymizer {
    /**
     * Anonymize input data
     *
     * @param s clear data to be anonymized
     * @return anonymized input data
     */
    String gdprBuildAnonymization(final String s);

    /**
     * Anonymize input data
     * @param s  clear data to be anonymized
     * @param salt user pattern to be combined with input data before anonymizing
     * @return anonymized input data + salt
     */
    String gdprBuildAnonymization(final String s, final String salt);
}