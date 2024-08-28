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

public interface CommandSecurityLevelsTests {

    //GET
    void testCmdSlNscsService__CPP_GET_SL_Single_NODE() throws Exception;

    void testCmdSlNscsService__CPP_GET_SL_ALL() throws Exception;

    void testCmdSlNscsService__CPP_GET_SL_2() throws Exception;

    void testCmdSlCommandCppSetSL2_NE_not_synced_and_non_existing() throws Exception;

    void testCmdSlNscsService__CPP_GET_SL_ALL_Negative_Syntax_invalid() throws Exception;

    void testCmdSlNscsService__CPP_GET_SL_ALL_Negative_Nodes_invalid() throws Exception;

    void testCmdSlNscsService__CPP_GET_SL_ALL_Negative_Nodes_DoesNot_Exist() throws Exception;

    void testCmdSlNscsService__CPP_GET_SL_Single_NODE_Invalid_User() throws Exception;

    //SET
    void testCmdSlNscsService__CPP_SET_SL_2_NegativeSyntaxError() throws Exception;

    void testCmdSlCommandCppSetSL2_NE_already_atSL2() throws Exception;

    void testCmdSlCommandCppSetSL2_NE_in_progress() throws Exception;

    void testCmdSlCommandCppSetSL2_NE_in_progress_MultipleNodes() throws Exception;

    void testCmdSlCommandCppSetSL1_NE_in_progress_Deactivate() throws Exception;

    //SET negative
    void testCmdSlCommandCppSetSL2_NE_not_synced() throws Exception;

    //Events
    void testCmdSlNscsService__SecurityMO_Changed_Events() throws Exception;

}
