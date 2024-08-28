/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.gdpr.anonymize;

import com.ericsson.oss.itpf.sdk.core.annotation.EService;

import javax.ejb.Local;

/**
 * Interface invoked by REST interface.
 * It's wrapper of GdprAnonymizer, introduced to easily manage @Authorize annotation
 */
@EService
@Local
public interface GdprAnonymizerDelegate {
    /**
     *
     * @param s input data to anonymize
     * @return anonymized data
     */
    String gdprBuildAnonymization(final String s);

    /**
     *
     * @param s input data to anonymize
     * @param salt salt set by client
     * @return anonymized data
     */
    String gdprBuildAnonymization(final String s, final String salt);
}
