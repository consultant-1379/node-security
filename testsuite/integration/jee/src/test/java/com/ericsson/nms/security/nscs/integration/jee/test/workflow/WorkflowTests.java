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
package com.ericsson.nms.security.nscs.integration.jee.test.workflow;

/**
 * Interface defining integration tests for instantiating workflows
 */
public interface WorkflowTests {

    void testWfEngineStartWorkflowProto() throws Exception;

    void testWfEngineStartWorkflowCPPActivateSL2() throws Exception;

    void testWfEngineStartWorkflowCPPDeactivateSL2() throws Exception;

    void testRestWorkflowStartWorkflowForOneNode() throws Exception;

    void testRestWorkflowStartWorkflowForMultipleNodes() throws Exception;

    void testRestWorkflowStartNonExistingWorkflow() throws Exception;

    void testRestWorkflowStartActivateSL2Workflow() throws Exception;

    void testWfEngineStartWorkflowCPPActivateSL2_Negative() throws Exception;

    void testWfEngineStartWorkflowSSHKeyGeneration() throws Exception;

    void testWfEngineStartWorkflowCPPReissueCertificate() throws Exception;

    void testWfEngineStartWorkflowCPPIssueCertificate() throws Exception;

    void testWfEngineStartWorkflowRevokeNodeCertificate() throws Exception;

    void testRestWorkflowStartCPPIssueWorkflow() throws Exception;

    void testRestWorkflowStartCPPReissueWorkflow() throws Exception;

    void testRestWorkflowStartSSHKeyGenerationWorkflow_Create() throws Exception;

    void testRestWorkflowStartSSHKeyGenerationWorkflow_Update() throws Exception;

    void testWfEngineStartWorkflowCPPIssueTrustCert() throws Exception;

    void testRestWorkflowWorkflowDebug_getwfStatus() throws Exception;

    void testRestWorkflowWorkflowDebug_getwfStatusWithListOfnodes() throws Exception;

    void testRestWorkflowWorkflowDebug_getwfstats() throws Exception;

    void testRestWorkflowWorkflowDebug_getwffinalstatus() throws Exception;

    void testRestWorkflowWorkflowDebug_resetwfinstances() throws Exception;

    void testWfEngineStartWorkflowCOMIssueTrustCert() throws Exception;

    void testWfEngineStartWorkflowCOMIssueCert() throws Exception;

    void testRestWorkflowWorkflowDebug_getwffinalstatus_Failed() throws Exception;

    void testRestWorkflowWorkflowDebug_getwffinalstatus_Success() throws Exception;

    void testRestWorkflowWorkflowDebug_getwffinalstatus_Timeout() throws Exception;

    void testWfEngineStartWorkflowCPPRemoveTrustOAM() throws Exception;

    void testWfEngineStartWorkflowCPPRemoveTrustNewIPSEC() throws Exception;

    void testWfEngineStartWorkflowCOMRemoveTrust() throws Exception;

}
