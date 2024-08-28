/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
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
 * This interface is to test Download CRL on Demand.
 *
 */
public interface OnDemandCrlDownloadTest {

    void testOnDemandCrlDownload_NodeDoesNotExist_PartialSuccess() throws Exception;

    void testOnDemandCrlDownload_NodeNotInSynch_PartialSuccess() throws Exception;

    void testOnDemandCrlDownload_DuplicateNodes_Success() throws Exception;

    void testOnDemandCrlDownload_WithWrongFileContent_Failure() throws Exception;

    void testOnDemandCrlDownload_InvalidNodesWithFile_Failure() throws Exception;

    void testOnDemandCrlDownload_NodeDoesNotExist_Failure() throws Exception;

    void testOnDemandCrlDownload_NodeNotInSynch_Failure() throws Exception;

    void testOnDemandCrlDownload_SingleNodeWithOutFile_Success() throws Exception;

    void testOnDemandCrlDownload_FileWithMulitpleNodes_Success() throws Exception;

    void testOnDemandCrlDownload_MulitpleNodesWithOutFile_Success() throws Exception;
    
    void testOnDemandCrlDownload_WithMulitpleNodes_Failure() throws Exception;
    
}
