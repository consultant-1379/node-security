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
package com.ericsson.nms.security.nscs.integration.jee.test.command;

/**
 * This interface is for read crlCheck status on g2 nodes for OAM or IPSEC
 *
 */
public interface CrlCheckReadTest {

    void testReadCrlCheck_WithEmptyFile() throws Exception;

    void testReadCrlCheck_WithWrongFileContent() throws Exception;

    void testReadCrlCheck_WithMulitpleNodes() throws Exception;

    void testReadCrlCheck_NodeDoesNotExist() throws Exception;

    void testReadCrlCheck_TrustCategoryDoesNotExist() throws Exception;

    void testReadCrlCheck_InvalidCertificateType() throws Exception;

    void testReadCrlCheck_NodeNotInSynch() throws Exception;

}
