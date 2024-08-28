/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.integration.jee.test.command;

public interface CppOnDemandCrlDownloadTest {

	void testDownloadCrl_WithWrongFileContent_Failure() throws Exception;

	void testDownloadCrl_CPPInvalidNodesWithFile_Failure() throws Exception;

	void testDownloadCrl_CPPNodeDoesNotExist_Failure() throws Exception;

	void testDownloadCrl_SecurityDoesNotExist_Failure() throws Exception;

	void testDownloadCrl_CPPNodeNotInSynch_Failure() throws Exception;

	void testDownloadCrl_WithMulitpleCPPNodes_Failure() throws Exception;

	void testDownloadCrl_CPPNodeDoesNotExist_PartialSuccess() throws Exception;

	void testDownloadCrl_CPPNodeNotInSynch_PartialSuccess() throws Exception;

	void testDownloadCrl_FileWithMulitpleCPPNodes_Success() throws Exception;

	void testDownloadCrl_SingleCPPNodeWithOutFile_Success() throws Exception;

	void testDownloadCrl_MulitpleCPPNodesWithOutFile_Success() throws Exception;

}
