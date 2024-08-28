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
 * This interface is for enable or disable crlCheck attribute on g2 nodes for OAM or IPSEC
 *
 */
public interface ComEcimCrlCheckEnableOrDisableTest {

    void testEnableCrlCheck_NodeDoesNotExist_PartialSuccess() throws Exception;

    void testEnableCrlCheck_NodeNotInSynch_PartialSuccess() throws Exception;

    void testEnableCrlCheck_DuplicateNodes_Failure() throws Exception;

    void testEnableCrlCheck_WithWrongFileContent_Failure() throws Exception;

    void testEnableCrlCheck_InvalidNodesWithFile_Failure() throws Exception;

    void testEnableCrlCheck_WithMulitpleNodes_Failure() throws Exception;

    void testEnableCrlCheck_NodeDoesNotExist_Failure() throws Exception;

    void testEnableCrlCheck_TrustCategoryDoesNotExist_Failure() throws Exception;

    void testEnableCrlCheck_InvalidCertificateType_Failure() throws Exception;

    void testEnableCrlCheck_NodeNotInSynch_Failure() throws Exception;

    void testEnableCrlCheck_SingleNodeWithOutFile_Success() throws Exception;

    void testEnableCrlCheck_FileWithMulitpleNodes_Success() throws Exception;

    void testEnableCrlCheck_MulitpleNodesWithOutFile_Success() throws Exception;

    void testDisableCrlCheck_SingleNodeWithOutFile_Success() throws Exception;

    void testDisableCrlCheck_FileWithMulitpleNodes_Success() throws Exception;

    void testDisableCrlCheck_MulitpleNodesWithOutFile_Success() throws Exception;

    void testDisableCrlCheck_NodeDoesNotExist_Failure() throws Exception;

    void testDisableCrlCheck_TrustCategoryDoesNotExist_Failure() throws Exception;

    void testDisableCrlCheck_InvalidCertificateType_Failure() throws Exception;

    void testDisableCrlCheck_NodeNotInSynch_Failure() throws Exception;

    void testDisableCrlCheck_NodeDoesNotExist_PartialSuccess() throws Exception;

    void testDisableCrlCheck_NodeNotInSynch_PartialSuccess() throws Exception;

    void testDisableCrlCheck_DuplicateNodes_Failure() throws Exception;

    void testDisableCrlCheck_WithWrongFileContent_Failure() throws Exception;

    void testDisableCrlCheck_InvalidNodesWithFile_Failure() throws Exception;

    void testDisableCrlCheck_WithMulitpleNodes_Failure() throws Exception;

}
