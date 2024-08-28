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

public interface CredentialsTests {

    //Positive
    void updateCredentialsOneNode() throws Exception;
    void updateCredentialsOneNodeOneParam() throws Exception;
    void getCredentialsSecureShow() throws Exception;
    void getCredentialsSecureHide() throws Exception;
    void getCredentialsSecureDefaultHide() throws Exception;
    void getCredentialsRootHide() throws Exception;
    void getCredentialsRootShow() throws Exception;
    void getCredentialsRootDefaultHide() throws Exception;
    void getCredentialsNormalDefaultHide() throws Exception;
    //Negative
    void updateCredentialsNoParam() throws Exception;
    void updateCredentialsDuplicateParam() throws Exception;
    void updateCredentialsMissingMO() throws Exception;
    void getCredentialsNormalShow() throws Exception;
    void getCredentialsNormalHide() throws Exception;
    void getCredentialsSecureHideMissingNodes() throws Exception;
    void getCredentialsRootHideMissingNodes() throws Exception;
    void getCredentialsNormalHideMissingNodes() throws Exception;
    void getCredentialsNormalHideNotExistingNodes() throws Exception;
    void getCredentialsSGSNWithNormalUserTypeShow() throws Exception;
    void getCredentialsSGSNWithRootUserTypeShow() throws Exception;
    void getCredentialsSGSNWithNormalUserTypeHide() throws Exception;
    void getCredentialsSGSNWithRootUserTypeHide() throws Exception;
    void getCredentialsWithoutUserTypeShow() throws Exception;
    void getCredentialsWithoutUserTypeAndPlainText() throws Exception;
    void getCredentialsWithoutUserTypeHide() throws Exception;
    void getCredentialsSGSNWithSecureUserTypeHide() throws Exception;
    void getCredentialsSGSNWithSecureUserTypeShow() throws Exception;
}
