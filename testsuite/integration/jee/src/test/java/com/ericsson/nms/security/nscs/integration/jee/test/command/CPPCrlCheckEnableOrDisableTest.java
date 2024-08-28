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
 * This interface is for enable or disable crlCheck attribute on g1 nodes for both OAM and IPSEC
 *
 */
public interface CPPCrlCheckEnableOrDisableTest {

    void testEnableCrlCheck_CPPWithWrongFileContent_Failure() throws Exception;

    void testEnableCrlCheck_CPPInvalidNodesWithFile_Failure() throws Exception;

    void testEnableCrlCheck_CPPNodeDoesNotExist_Failure() throws Exception;

    void testEnableCrlCheck_CPPInvalidCertificateType_Failure() throws Exception;
    
    void testEnableCrlCheck_SecurityDoesNotExist_Failure() throws Exception;
    
    void testEnableCrlCheck_CPPNodeNotInSynch_Failure() throws Exception;
    
    void testEnableCrlCheck_CPPDuplicateNodes_Failure() throws Exception;
    
    void testEnableCrlCheck_WithMulitpleCPPNodes_Failure() throws Exception;
    
    void testEnableCrlCheck_CPPNodeDoesNotExist_PartialSuccess() throws Exception;
    
    void testEnableCrlCheck_CPPNodeNotInSynch_PartialSuccess() throws Exception;
    
    void testEnableCrlCheck_FileWithMulitpleCPPNodes_Success() throws Exception;
    
    void testEnableCrlCheck_SingleCPPNodeWithOutFile_Success() throws Exception;
    
    void testEnableCrlCheck_MulitpleCPPNodesWithOutFile_Success() throws Exception;
    
    void testDisableCrlCheck_CPPWithWrongFileContent_Failure() throws Exception;

    void testDisableCrlCheck_CPPInvalidNodesWithFile_Failure() throws Exception;

    void testDisableCrlCheck_CPPNodeDoesNotExist_Failure() throws Exception;

    void testDisableCrlCheck_CPPInvalidCertificateType_Failure() throws Exception;
    
    void testDisableCrlCheck_SecurityDoesNotExist_Failure() throws Exception;
    
    void testDisableCrlCheck_CPPNodeNotInSynch_Failure() throws Exception;
    
    void testDisableCrlCheck_CPPDuplicateNodes_Failure() throws Exception;
    
    void testDisableCrlCheck_WithMulitpleCPPNodes_Failure() throws Exception;
    
    void testDisableCrlCheck_CPPNodeDoesNotExist_PartialSuccess() throws Exception;
    
    void testDisableCrlCheck_CPPNodeNotInSynch_PartialSuccess() throws Exception;
    
    void testDisableCrlCheck_FileWithMulitpleCPPNodes_Success() throws Exception;
    
    void testDisableCrlCheck_SingleCPPNodeWithOutFile_Success() throws Exception;
    
    void testDisableCrlCheck_MulitpleCPPNodesWithOutFile_Success() throws Exception;    

}
