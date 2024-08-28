/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.privacy;


public interface PrivacyHandler {

    /**
     * Updates log-view object. If RncFeature.fetureState with RncFeatureId=AnonymizedEvenData has been received.
     *
     * @param nodeFdn
     *            the node fdn of involved node.
     * @param nodeName
     *            the node name of involved node.
     * @param attribute
     *            the attribute name
     * @param value
     *            the attribute new value
     */
    public void updateAnonymized(final String nodeFdn, final String nodeName, final String attribute, final Object value);

}
