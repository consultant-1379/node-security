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
package com.ericsson.nms.security.nscs.integration.jee.test.utils;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.integration.jee.test.cache.NodeSecurityReplicatedCacheSortingTestsImpl;
import com.ericsson.nms.security.nscs.integration.jee.test.command.CPPCrlCheckEnableOrDisableTestImpl;
import com.ericsson.nms.security.nscs.integration.jee.test.command.ComEcimCrlCheckEnableOrDisableTestImpl;
import com.ericsson.nms.security.nscs.integration.jee.test.command.CppOnDemandCrlDownloadTest;
import com.ericsson.nms.security.nscs.integration.jee.test.command.CreateCredentialsCommandIntegrationTest;
import com.ericsson.nms.security.nscs.integration.jee.test.command.CredentialsTest;
import com.ericsson.nms.security.nscs.integration.jee.test.command.CrlCheckReadTestImpl;
import com.ericsson.nms.security.nscs.integration.jee.test.command.GetJobCommadTests;
import com.ericsson.nms.security.nscs.integration.jee.test.command.JobIdManagementTests;
import com.ericsson.nms.security.nscs.integration.jee.test.command.LdapConfigurationTest;
import com.ericsson.nms.security.nscs.integration.jee.test.command.NscsServiceIntegrationTest;
import com.ericsson.nms.security.nscs.integration.jee.test.command.OnDemandCrlDownloadTestImpl;
import com.ericsson.nms.security.nscs.integration.jee.test.command.SecurityProtocol;
import com.ericsson.nms.security.nscs.integration.jee.test.command.SecurityProtocolChangeTests;
import com.ericsson.nms.security.nscs.integration.jee.test.command.SnmpAuthTest;
import com.ericsson.nms.security.nscs.integration.jee.test.cpp.CppSecurityServiceTest;
import com.ericsson.nms.security.nscs.integration.jee.test.events.EventTest;
import com.ericsson.nms.security.nscs.integration.jee.test.iscf.ISCFGeneratorDpsTest;
import com.ericsson.nms.security.nscs.integration.jee.test.moaction.MOActionTest;
import com.ericsson.nms.security.nscs.integration.jee.test.producer.EServiceProducer;
import com.ericsson.nms.security.nscs.integration.jee.test.rest.RestTest;
import com.ericsson.nms.security.nscs.integration.jee.test.wftasks.MoActionWithParamTasksTests;
import com.ericsson.nms.security.nscs.integration.jee.test.workflow.WorkflowEngineTest;
import com.ericsson.nms.security.nscs.integration.jee.test.workflow.WorkflowHandlerTest;

/**
 * Test batches :
 * Generic          --> testGeneric*
 * Rest             --> testRest*
 *                         +--> testRestSmrs*
 *                         +--> testRestNodes*
 *                         +--> testRestMoAction*
 *                         +--> testRestWorkflow*
 *                         +--> testRestPIB*
 *                         +--> testRestJob*
 *                         +--> testRestIscf*
 * Credential       --> testCred*
 * Workflow engine  --> testWfEngine*
 * SL2              --> testCmdSl*
 * PKI              --> testPki*
 * ISCF             --> testIscf*
 * FM_events        --> testEvent*
 * MO action        --> testMo*
 *                         +--> testMoAction*
 *                         +--> testMoTask*
 * Workflow handler --> testWfHandler*
 * Ldap             --> testLdap*
 * Snmp AuthPriv    --> testSnmp*
 * Jobs             --> testJob*
 * Crl Check        --> testCrlCheck*
 * OnDemaandCrl     --> testOnDemandCrl*
 * Replicate Cache  --> testReplicatedCache*
 * Download Crl     --> testDownloadCrl*
 * Ftpes            --> testHttps*
 */

@RunWith(Arquillian.class)
@Stateless
public class IntegrationTest extends IntegrationTestBase {

    private static final Logger log = LoggerFactory.getLogger(IntegrationTest.class);

    @Inject
    private EServiceProducer eserviceHolder;

    /*
     * GENERIC TESTS
     */
    @Test
    @InSequence(1)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testGenericDummy() {
        log.info(">>>>>>>>>>> testGenericDummy starts >>>>>>>>>>>");
        log.info("<<<<<<<<<<< testGenericDummy ends   <<<<<<<<<<<");
    }

    @Test
    @InSequence(2)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testGenericEServiceRefNotNull() throws Exception {

        log.info(">>>>>>>>>>> testGenericEServiceRefNotNull starts >>>>>>>>>>>");

        assertNotNull("SmrsService @EService ref should not be null.", this.eserviceHolder.getSmrsService());
        assertNotNull("WorkflowHandler @EService ref should not be null.", this.eserviceHolder.getWorkflowHandler());

        assertNotNull("IscfService @EService ref should not be null.", this.eserviceHolder.getIscfService());
        assertNotNull("ProfileManagementService @EService ref should not be null.", this.eserviceHolder.getProfileManagementService());
        assertNotNull("EntityManagementService @EService ref should not be null.", this.eserviceHolder.getEntityManagementService());
        assertNotNull("CertificateManagementService @EService ref should not be null.", this.eserviceHolder.getCertificateManagementService());
        assertNotNull("PKIConfigurationManagementService @EService ref should not be null.", this.eserviceHolder.getPkiConfigurationManagementService());

        log.info("<<<<<<<<<<< testGenericEServiceRefNotNull ends   <<<<<<<<<<<");
    }

    /*
     * REST TESTS
     */
    @Inject
    private RestTest restTests;

    @Test
    @InSequence(90)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestSmrsAccount() throws Exception {
        restTests.testRestSmrsAccount();
    }

    @Test
    @InSequence(91)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestSmrsAddress() throws Exception {
        restTests.testRestSmrsAddress();
    }

    @Test
    @InSequence(92)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestSmrsDeleteAccount() throws Exception {
        restTests.testRestSmrsDeleteAccount();
    }

    @Test
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(93)
    public void testRestNodesVerifyInvalidIpsec_JsonParseExceptionMapper() throws Exception {
        restTests.testRestNodesVerifyInvalidIpsec_JsonParseExceptionMapper();
    }

    @Test
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(94)
    public void testRestNodesVerifyInvalidIpsec_UnrecognizedPropertyExceptionMapper() throws Exception {
        restTests.testRestNodesVerifyInvalidIpsec_UnrecognizedPropertyExceptionMapper();
    }

    @Test
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(95)
    public void testRestNodesVerifyInvalidIpsec_JsonMappingExceptionMapper() throws Exception {
        restTests.testRestNodesVerifyInvalidIpsec_JsonMappingExceptionMapper();
    }

    /*
     * ERROR [com.ericsson.oss.services.nscs.nodes.UnrecognizedPropertyExceptionMapper] This is an invalid request. Attribute [node] The field is not
     * recognized by the system. Exception: Unrecognized field "node" (class com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes), not marked as
     * ignorable (one known property: "Node"]) at [Source: (io.undertow.servlet.spec.ServletInputStreamImpl); line: 1, column: 11] (through reference
     * chain: com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes["node"])
     */
    @Test
    @Ignore("UnrecognizedPropertyExceptionMapper: backend IPSEC method uses Nodes instead of NodesDTO")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(96)
    public void testRestNodesVerifyInvalidIpsec() throws Exception {
        restTests.testRestNodesVerifyInvalidIpsec();
    }

    @Test
    @Ignore("UnrecognizedPropertyExceptionMapper: backend IPSEC method uses Nodes instead of NodesDTO")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(97)
    public void testRestNodesVerifyIpsec() throws Exception {
        restTests.testRestNodesVerifyIpsec();
    }

    @Test
    @Ignore("UnrecognizedPropertyExceptionMapper: backend IPSEC method uses Nodes instead of NodesDTO")
    @OperateOnDeployment("nscs-test-ear.ear")
    @InSequence(98)
    public void testRestNodesIpsec() throws Exception {
        restTests.testRestNodesIpsec();
    }

    /*
     * Test passed but on commitTransaction:
     * 
     * INFO [com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityDataSetup] (default task-1) [NSCS_ARQ_DATA_SETUP] transaction
     * : commit : STARTED : status [0]
     * 
     * INFO [org.jboss.jca.core.connectionmanager.listener.TxConnectionListener] (default task-1) IJ000311: Throwable from unregister connection:
     * java.lang.IllegalStateException: IJ000152: Trying to return an unknown connection:
     * com.ericsson.oss.itpf.datalayer.dps.neo4j.jca.Neo4jConnectionImpl@3a164307
     */
    @Test
    @InSequence(99)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestNodesCount() throws Exception {
        restTests.testRestNodesCount();
    }

    @Test
    @InSequence(100)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestNodesList() throws Exception {
        restTests.testRestNodesList();

    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmWriterService in EAP7")
    @InSequence(802)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestMoActionWithoutParam() throws Exception {
        restTests.testRestMoActionWithoutParam();

    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmWriterService in EAP7")
    @InSequence(803)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestMoActionInitCertEnrollment() throws Exception {
        restTests.testRestMoActionInitCertEnrollment();

    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmWriterService in EAP7")
    @InSequence(804)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestMoActionInstallTrustedCertificates() throws Exception {
        restTests.testRestMoActionInstallTrustedCertificates();
    }

    //WORKFLOW RELATED TESTS
    @Inject
    private WorkflowEngineTest workflowEngineTest;

    @Test
    @InSequence(101)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfEngineStartWorkflowProto() throws Exception {
        workflowEngineTest.testWfEngineStartWorkflowProto();
    }

    @Test
    @InSequence(102)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestWorkflowStartWorkflowForOneNode() throws Exception {
        workflowEngineTest.testRestWorkflowStartWorkflowForOneNode();
    }

    @Test
    @InSequence(103)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestWorkflowStartWorkflowForMultipleNodes() throws Exception {
        workflowEngineTest.testRestWorkflowStartWorkflowForMultipleNodes();
    }

    @Test
    @InSequence(104)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestWorkflowStartNonExistingWorkflow() throws Exception {
        workflowEngineTest.testRestWorkflowStartNonExistingWorkflow();
    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(105)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfEngineStartWorkflowCPPActivateSL2() throws Exception {
        workflowEngineTest.testWfEngineStartWorkflowCPPActivateSL2();
    }

    @Test
    @InSequence(106)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestWorkflowStartActivateSL2Workflow() throws Exception {
        workflowEngineTest.testRestWorkflowStartActivateSL2Workflow();
    }
    
    //CREATE CRED TESTS
    @Inject
    private CreateCredentialsCommandIntegrationTest createCredTest;

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(201)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredCppCreateCredentialsWith2ValidNodes() throws Exception {
        createCredTest.cppCreateCredentialsWith2ValidNodes();
    }

    @Test
    //@Ignore // Passed
    @InSequence(202)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredCppCreateCredentialsWith2ValidNodesTwice() throws Exception {
        createCredTest.cppCreateCredentialsWith2ValidNodesTwice();
    }

    @Test
    //@Ignore // Passed sometmes blocked
    @InSequence(203)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredCppCreateCredentialsWithInvalidNodeTest() throws Exception {
        createCredTest.cppCreateCredentialsWithInvalidNodeTest();
    }

    @Test
    //@Ignore // Passed
    @InSequence(204)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredCppCreateCredentialsWithStarTest() throws Exception {
        createCredTest.cppCreateCredentialsWithStarTest();
    }

    @Test
    //@Ignore // Passed
    @InSequence(205)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredCppCreateCredentialsWithMissingAttributeTest() throws Exception {
        createCredTest.cppCreateCredentialsWithMissingAttributeTest();
    }

    @Test
    @Ignore("TORF-38537 - command targetgroup add not supported in 14B")
    @InSequence(206)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredAddTargetGroupsWhenSecurityMODoesNotExist() throws Exception {
        createCredTest.testAddTargetGroupsWhenSecurityMODoesNotExist();
    }

    @Test
    @Ignore("TORF-38537 - command targetgroup add not supported in 14B")
    @InSequence(207)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredAddTargetGroupsWhenAllSecurityMOsExist() throws Exception {
        createCredTest.testAddTargetGroupsWhenAllSecurityMOsExist();
    }

    @Test
    @Ignore("TORF-38537 - command targetgroup add not supported in 14B")
    @InSequence(208)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredAddTargetGroupsWhenSomeSecurityMOsExist() throws Exception {
        createCredTest.testAddTargetGroupsWhenSomeSecurityMOsExist();
    }

    @Test
    @Ignore("TORF-38537 - command targetgroup add not supported in 14B")
    @InSequence(209)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredAddTargetGroupsWhenTargetGroupsAlreadySet() throws Exception {
        createCredTest.testAddTargetGroupsWhenTargetGroupsAlreadySet();
    }

    @Test
    @Ignore("TORF-38537 - command targetgroup add not supported in 14B")
    @InSequence(301)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredAddTargetGroupsWhenTargetGroupsNotAlreadySet() throws Exception {
        createCredTest.testAddTargetGroupsWhenTargetGroupsNotAlreadySet();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(302)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredCppCreateCredentialsWith2ValidNetworkElementNodes() throws Exception {
        createCredTest.cppCreateCredentialsWith2ValidNetworkElementNodes();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(303)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredMiniLinkIndoorCreateCredentials() throws Exception {
        createCredTest.testMiniLinkIndoorCreateCredentials();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(305)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredCiscoAsr9000CreateCredentials() throws Exception {
        createCredTest.testCiscoAsr9000CreateCredentials();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(306)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredCiscoAsr900CreateCredentials() throws Exception {
        createCredTest.testCiscoAsr900CreateCredentials();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(307)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredJuniperMxCreateCredentials() throws Exception {
        createCredTest.testJuniperMxCreateCredentials();
    }

    //SECURITY LEVELS TESTS
    @Inject
    private NscsServiceIntegrationTest commandSLtest;

    @Test
    //@Ignore("Using EAccessControlBypassAllImpl which returns 'yes' for any user in any role")
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(401)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCmdSlNscsService__CPP_GET_SL_Single_NODE_Invalid_User() throws Exception {
        commandSLtest.testCmdSlNscsService__CPP_GET_SL_Single_NODE_Invalid_User();
    }

    @Test
    //@Ignore("Not supported in 14B release")
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(402)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCmdSlNscsService__CPP_GET_SL_Single_NODE() throws Exception {
        commandSLtest.testCmdSlNscsService__CPP_GET_SL_Single_NODE();
    }

    @Test
    //@Ignore("Not supported in 14B release")
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(403)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCmdSlNscsService__CPP_GET_SL_ALL() throws Exception {
        commandSLtest.testCmdSlNscsService__CPP_GET_SL_ALL();
    }

    @Test
    //@Ignore("Not supported in 14B release")
    @InSequence(404)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCmdSlNscsService__CPP_SET_SL_2_NegativeSyntaxError() throws Exception {
        commandSLtest.testCmdSlNscsService__CPP_SET_SL_2_NegativeSyntaxError();
    }

    @Test
    //@Ignore // To Be investigated
    @InSequence(405)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCmdSlNscsService__CPP_GET_SL_ALL_Negative_Syntax_invalid() throws Exception {
        commandSLtest.testCmdSlNscsService__CPP_GET_SL_ALL_Negative_Syntax_invalid();
    }

    @Test
    //@Ignore("Not supported in 14B release")
    @InSequence(406)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCmdSlNscsService__CPP_GET_SL_ALL_Negative_Nodes_invalid() throws Exception {
        commandSLtest.testCmdSlNscsService__CPP_GET_SL_ALL_Negative_Nodes_invalid();
    }

    @Test
    //@Ignore // To Be investigated
    @InSequence(407)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCmdSlNscsService__SecurityMO_Changed_Events() throws Exception {
        commandSLtest.testCmdSlNscsService__SecurityMO_Changed_Events();
    }

    @Test
    //@Ignore("Not supported in 14B release")
    @Ignore("CommandSyntaxException: --nodelist option no more supported for secadm securitylevel set command")
    @InSequence(408)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCmdSlCommandCppSetSL2_NE_not_synced() throws Exception {
        commandSLtest.testCmdSlCommandCppSetSL2_NE_not_synced();
    }

    @Test
    //@Ignore("Not supported in 14B release")
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(409)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCmdSlNscsService__CPP_GET_SL_2() throws Exception {
        commandSLtest.testCmdSlNscsService__CPP_GET_SL_2();
    }

    @Test
    //@Ignore("Not supported in 14B release")
    @Ignore("CommandSyntaxException: --nodelist option no more supported for secadm securitylevel set command")
    @InSequence(410)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCmdSlCommandCppSetSL2_NE_not_synced_and_non_existing() throws Exception {
        commandSLtest.testCmdSlCommandCppSetSL2_NE_not_synced_and_non_existing();
    }

    @Test
    //@Ignore("Not supported in 14B release")
    @Ignore("CommandSyntaxException: --nodelist option no more supported for secadm securitylevel set command")
    @InSequence(412)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCmdSlCommandCppSetSL2_NE_already_atSL2() throws Exception {
        commandSLtest.testCmdSlCommandCppSetSL2_NE_already_atSL2();
    }

    @Test
    //@Ignore("Not supported in 14B release")
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(413)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCmdSlNscsService__CPP_GET_SL_ALL_Negative_Nodes_DoesNot_Exist() throws Exception {
        commandSLtest.testCmdSlNscsService__CPP_GET_SL_ALL_Negative_Nodes_DoesNot_Exist();
    }

    @Test
    //@Ignore("Not supported in 14B release")
    @Ignore("CommandSyntaxException: --nodelist option no more supported for secadm securitylevel set command")
    @InSequence(414)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCmdSlCommandCppSetSL2_NE_in_progress() throws Exception {
        commandSLtest.testCmdSlCommandCppSetSL2_NE_in_progress();
    }

    @Test
    //@Ignore("Not supported in 14B release")
    @Ignore("CommandSyntaxException: --nodelist option no more supported for secadm securitylevel set command")
    @InSequence(415)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCmdSlCommandCppSetSL2_NE_in_progress_MultipleNodes() throws Exception {
        commandSLtest.testCmdSlCommandCppSetSL2_NE_in_progress_MultipleNodes();
    }

    @Test
    //@Ignore("Not supported in 14B release")
    @Ignore("CommandSyntaxException: --nodelist option no more supported for secadm securitylevel set command")
    @InSequence(415)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCmdSlCommandCppSetSL1_NE_in_progress_Deactivate() throws Exception {
        commandSLtest.testCmdSlCommandCppSetSL1_NE_in_progress_Deactivate();
    }

    //NS and PKI INTERGRATION TESTS
    @Inject
    private CppSecurityServiceTest cppSecServTest;

    @Test
    @Ignore("Test to be removed since it uses the node-security-jar embedded in nscs-test-ear.ear")
    @InSequence(501)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testPkiCppPrepareInitEnrollment() throws Exception {
        cppSecServTest.prepareInitEnrollment();
    }

    @Test
    @Ignore("Test to be removed since it uses the node-security-jar embedded in nscs-test-ear.ear")
    @InSequence(502)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testPkiCppPrepareInitEnrollmentKey2048() throws Exception {
        cppSecServTest.prepareInitEnrollmentKey2048();
    }

    @Test
    @Ignore("Test to be removed since it uses the node-security-jar embedded in nscs-test-ear.ear")
    @InSequence(503)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testPkiCppPrepareInitEnrollmentIPv6() throws Exception {
        cppSecServTest.prepareInitEnrollmentIPv6();
    }

    @Test
    @Ignore("Test to be removed since it uses the node-security-jar embedded in nscs-test-ear.ear")
    @InSequence(504)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testPkiCppPrepareInstallCorbaTrust() throws Exception {
        cppSecServTest.prepareInstallCorbaTrust();
    }

    @Test
    @Ignore("Test to be removed since it uses the node-security-jar embedded in nscs-test-ear.ear")
    @InSequence(505)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testPkiCppPrepareInstallCorbaTrustNotSupportedCat() throws Exception {
        cppSecServTest.prepareInstallCorbaTrustNotSupportedCat();
    }

    @Test
    @Ignore("Test to be removed since it uses the node-security-jar embedded in nscs-test-ear.ear")
    @InSequence(506)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testPkiCppPrepareInstallCorbaTrustForAP() throws Exception {
        cppSecServTest.prepareInstallCorbaTrustForAP();
    }

    @Test
    @Ignore("Test to be removed since it uses the node-security-jar embedded in nscs-test-ear.ear")
    @InSequence(507)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testPkiCppGetTrustdistributionPointUrlIPv4() throws Exception {
        cppSecServTest.getTrustdistributionPointUrlIPv4();
    }

    @Test
    @Ignore("Test to be removed since it uses the node-security-jar embedded in nscs-test-ear.ear")
    @InSequence(508)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testPkiCppGetTrustdistributionPointUrlIPv6() throws Exception {
        cppSecServTest.getTrustdistributionPointUrlIPv6();
    }

    //ISCF Generator tests
    @Inject
    private ISCFGeneratorDpsTest iscfTest;

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(601)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testIscfGeneratorReturnsByteArray() throws Exception {
        iscfTest.testIscfGeneratorReturnsByteArray();
    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(602)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestIscfGeneratorRestInterface() throws Exception {
        iscfTest.testRestIscfGeneratorRestInterface();
    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(603)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestIscfGeneratorRestInterfaceIpsecTrafficAndOam() throws Exception {
        iscfTest.testRestIscfGeneratorRestInterfaceIpsecTrafficAndOam();
    }

    @Test
    @InSequence(604)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestIscfGeneratorRestInterfaceShouldThrowErrorInvalidLevels() throws Exception {
        iscfTest.testRestIscfGeneratorRestInterfaceShouldThrowErrorInvalidLevels();
    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(605)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestIscfGeneratorRestInterfaceShouldThrowErrorNullLevels() throws Exception {
        iscfTest.testRestIscfGeneratorRestInterfaceShouldThrowErrorNullLevels();
    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(606)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testIscfCancelSecLevelAndIpsec() throws Exception {
        iscfTest.testIscfCancelSecLevelAndIpsec();
    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(607)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testIscfCancelIpsecFqdn() throws Exception {
        iscfTest.testIscfCancelIpsecFqdn();
    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(608)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestIscfGeneratorRestInterfaceCombined() throws Exception {
        iscfTest.testRestIscfGeneratorRestInterfaceCombined();
    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(609)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testIscfSecurityDataGeneratorOam() throws Exception {
        iscfTest.testIscfSecurityDataGeneratorOam();
    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(610)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testIscfSecurityDataGeneratorCombo() throws Exception {
        iscfTest.testIscfSecurityDataGeneratorCombo();
    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(611)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testIscfNodeModernizationOAM() throws Exception {
        iscfTest.testIscfNodeModernizationOAM();
    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(612)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testIscfNodeModernizationCombo() throws Exception {
        iscfTest.testIscfNodeModernizationCombo();
    }

    //FM Alarm and Event tests
    @Inject
    private EventTest eventTest;

    @Test
    //@Ignore // To Be investigated
    @InSequence(701)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testEventNscsService__FMAlarm_Events() throws Exception {
        eventTest.testEventNscsService__FMAlarm_Events();
    }

    @Inject
    private MOActionTest moTest;

    @Test
    @Ignore("Test to be removed since it uses the node-security-jar embedded in nscs-test-ear.ear")
    @InSequence(901)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testMoActionWithoutParameter() throws Exception {
        moTest.moActionWithoutParameter();
    }

    @Test
    @Ignore("Test to be removed since it uses the node-security-jar embedded in nscs-test-ear.ear")
    @InSequence(902)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testMoActionInitCertEnrollment() throws Exception {
        moTest.moActionInitCertEnrollment();
    }

    @Test
    @Ignore("Test to be removed since it uses the node-security-jar embedded in nscs-test-ear.ear")
    @InSequence(903)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testMoActionNonExistingNode() throws Exception {
        moTest.moActionNonExistingNode();
    }

    @Test
    @Ignore("Test to be removed since it uses the node-security-jar embedded in nscs-test-ear.ear")
    @InSequence(904)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testMoActionNonValidParams() throws Exception {
        moTest.moActionNonValidParams();
    }

    @Test
    @Ignore("Test to be removed since it uses the node-security-jar embedded in nscs-test-ear.ear")
    @InSequence(905)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testMoActionInstallTrustedCertificatesCorba() throws Exception {
        moTest.moActionInstallTrustedCertificatesCorba();
    }

    @Test
    @Ignore("Test to be removed since it uses the node-security-jar embedded in nscs-test-ear.ear")
    @InSequence(906)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testMoActionAdaptSecurityLevel() throws Exception {
        moTest.moActionAdaptSecurityLevel();
    }

    @Inject
    MoActionWithParamTasksTests moTask;

    @Test
    @InSequence(1001)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testMoTaskInitCertEnrollmentTask() throws Exception {
        moTask.testMoTaskInitCertEnrollmentTask();
    }

    @Test
    @InSequence(1002)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testMoTaskInstallTrustedCertificatesTask() throws Exception {
        moTask.testMoTaskInstallTrustedCertificatesTask();
    }

    @Inject
    private WorkflowHandlerTest workflowHandlerTest;

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(1003)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerReadFileTransferClientModeTaskHandler() throws Exception {
        workflowHandlerTest.testWfHandlerReadFileTransferClientModeTaskHandler();
    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(1004)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerReadCertEnrollStateTaskHandler() throws Exception {
        workflowHandlerTest.testWfHandlerReadCertEnrollStateTaskHandler();
    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(1005)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerReadTrustedCertificateInstallationFailureTaskHandler() throws Exception {
        workflowHandlerTest.testWfHandlerReadTrustedCertificateInstallationFailureTaskHandler();
    }

    @Test
    @InSequence(1006)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerCancelCertEnrollmentTaskHandler() throws Exception {
        workflowHandlerTest.testWfHandlerCancelCertEnrollmentTaskHandler();
    }

    @Test
    @InSequence(1007)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerClearInstallTrustFlagsTaskHandler() throws Exception {
        workflowHandlerTest.testWfHandlerClearInstallTrustFlagsTaskHandler();
    }

    @Test
    @InSequence(1008)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfEngineStartWorkflowCPPActivateSL2_Negative() throws Exception {
        workflowEngineTest.testWfEngineStartWorkflowCPPActivateSL2_Negative();
    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(1010)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfEngineStartWorkflowCPPDeactivateSL2() throws Exception {
        workflowEngineTest.testWfEngineStartWorkflowCPPDeactivateSL2();
    }

    @Test
    @Ignore("JdbcSQLException: Table WORKFLOWPROGRESSEVENT not found")
    @InSequence(1011)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerHasWorkflowInstanceInProgress() throws Exception {
        workflowHandlerTest.testWfHandlerHasWorkflowInstanceInProgress();
    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(1012)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerHasWorkflowInstanceInProgress_Positive() throws Exception {
        workflowHandlerTest.testWfHandlerHasWorkflowInstanceInProgress_Positive();
    }

    @Test
    @InSequence(1013)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerConfigureSSHKeyGenerationTaskHandlerTest() throws Exception {
        workflowHandlerTest.testWfHandlerConfigureSSHKeyGenerationTaskHandlerTest();
    }

    @Test
    @InSequence(1014)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfEngineStartWorkflowSSHKeyGeneration() throws Exception {
        workflowEngineTest.testWfEngineStartWorkflowSSHKeyGeneration();
    }

    @Test
    @InSequence(1015)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerRevokeNodeCertificateTaskHandlerTest() throws Exception {
        workflowHandlerTest.testWfHandlerRevokeNodeCertificateTaskHandlerTest();
    }

    @Test
    @InSequence(1016)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfEngineStartWorkflowCPPIssueCertificate() throws Exception {
        workflowEngineTest.testWfEngineStartWorkflowCPPIssueCertificate();
    }

    @Test
    @InSequence(1017)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfEngineStartWorkflowCPPReissueCertificate() throws Exception {
        workflowEngineTest.testWfEngineStartWorkflowCPPReissueCertificate();
    }

    @Test
    @InSequence(1018)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfEngineStartWorkflowRevokeNodeCertificate() throws Exception {
        workflowEngineTest.testWfEngineStartWorkflowRevokeNodeCertificate();
    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(1019)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerIssueInitCertEnrollmentIpSecTaskHandlerTest() throws Exception {
        workflowHandlerTest.testWfHandlerIssueInitCertEnrollmentIpSecTaskHandlerTest();
    }

    @Test
    @InSequence(1020)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestWorkflowStartCPPIssueWorkflow() throws Exception {
        workflowEngineTest.testRestWorkflowStartCPPIssueWorkflow();
    }

    @Test
    @InSequence(1021)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestWorkflowStartCPPReissueWorkflow() throws Exception {
        workflowEngineTest.testRestWorkflowStartCPPReissueWorkflow();
    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(1022)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerIssueInitCertEnrollmentTaskHandlerTest() throws Exception {
        workflowHandlerTest.testWfHandlerIssueInitCertEnrollmentTaskHandlerTest();
    }

    @Test
    @InSequence(1024)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestWorkflowStartSSHKeyGenerationWorkflow_Create() throws Exception {
        workflowEngineTest.testRestWorkflowStartSSHKeyGenerationWorkflow_Create();
    }

    @Test
    @InSequence(1025)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestWorkflowStartSSHKeyGenerationWorkflow_Update() throws Exception {
        workflowEngineTest.testRestWorkflowStartSSHKeyGenerationWorkflow_Update();
    }

    @Test
    @InSequence(1026)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerIssueInitTrustedCertEnrollmentTaskHandler() throws Exception {
        workflowHandlerTest.testWfHandlerIssueInitTrustedCertEnrollmentTaskHandler();
    }

    @Test
    @InSequence(1027)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfEngineStartWorkflowCPPIssueTrustCert() throws Exception {
        workflowEngineTest.testWfEngineStartWorkflowCPPIssueTrustCert();
    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(1028)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerIssueInitTrustedCertIpSecEnrollmentTaskHandler() throws Exception {
        workflowHandlerTest.testWfHandlerIssueInitTrustedCertIpSecEnrollmentTaskHandler();
    }

    @Test
    @InSequence(1029)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestWorkflowWorkflowDebug_getwfStatus() throws Exception {
        workflowEngineTest.testRestWorkflowWorkflowDebug_getwfStatus();
    }

    @Test
    @Ignore("JdbcSQLException: Table WORKFLOWPROGRESSEVENT not found")
    @InSequence(1030)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestWorkflowWorkflowDebug_getwfStatusWithListOfnodes() throws Exception {
        workflowEngineTest.testRestWorkflowWorkflowDebug_getwfStatusWithListOfnodes();
    }

    @Test
    @InSequence(1031)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestWorkflowWorkflowDebug_getwfstats() throws Exception {
        workflowEngineTest.testRestWorkflowWorkflowDebug_getwfstats();
    }

    @Test
    @InSequence(1032)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestWorkflowWorkflowDebug_getwffinalstatus() throws Exception {
        workflowEngineTest.testRestWorkflowWorkflowDebug_getwffinalstatus();
    }

    @Test
    @InSequence(1033)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestWorkflowWorkflowDebug_resetwfinstances() throws Exception {
        workflowEngineTest.testRestWorkflowWorkflowDebug_resetwfinstances();
    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(1034)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerCheckTrustedOAMAlreadyInstalledTaskHandler() throws Exception {
        workflowHandlerTest.testWfHandlerCheckTrustedOAMAlreadyInstalledTaskHandler();
    }

    @Test
    @InSequence(1035)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerComEcimCheckTrustedAlreadyInstalledTaskHandler() throws Exception {
        workflowHandlerTest.testWfHandlerComEcimCheckTrustedAlreadyInstalledTaskHandler();
    }

    @Test
    @InSequence(1039)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerComEcimCheckTrustedCategoryTaskHandler() throws Exception {
        workflowHandlerTest.testWfHandlerComEcimCheckTrustedCategoryTaskHandler();
    }

    @Test
    @InSequence(1041)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfEngineStartWorkflowCOMIssueTrustCert() throws Exception {
        workflowEngineTest.testWfEngineStartWorkflowCOMIssueTrustCert();
    }

    @Test
    @InSequence(1042)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfEngineStartWorkflowCOMIssueCert() throws Exception {
        workflowEngineTest.testWfEngineStartWorkflowCOMIssueCert();
    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(1043)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerComEcimCheckEnrollmentProtocolTaskHandler() throws Exception {
        workflowHandlerTest.testWfHandlerComEcimCheckEnrollmentProtocolTaskHandler();
    }

    @Test
    @InSequence(1044)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerComEcimCheckNodeCredentialTaskHandler() throws Exception {
        workflowHandlerTest.testWfHandlerComEcimCheckNodeCredentialTaskHandler();
    }

    @Test
    @InSequence(1048)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestWorkflowWorkflowDebug_getwffinalstatus_Success() throws Exception {
        workflowEngineTest.testRestWorkflowWorkflowDebug_getwffinalstatus_Success();
    }

    @Test
    @InSequence(1049)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestWorkflowWorkflowDebug_getwffinalstatus_Failed() throws Exception {
        workflowEngineTest.testRestWorkflowWorkflowDebug_getwffinalstatus_Failed();
    }

    @Test
    @InSequence(1050)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestWorkflowWorkflowDebug_getwffinalstatus_Timeout() throws Exception {
        workflowEngineTest.testRestWorkflowWorkflowDebug_getwffinalstatus_Timeout();
    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(1051)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerRemoveTrustNewIPSECTaskHandler() throws Exception {
        workflowHandlerTest.testWfHandlerRemoveTrustNewIPSECTaskHandler();
    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(1052)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerRemoveTrustOAMTaskHandler() throws Exception {
        workflowHandlerTest.testWfHandlerRemoveTrustOAMTaskHandler();
    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(1053)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerComEcimRemoveTrustTaskHandler() throws Exception {
        workflowHandlerTest.testWfHandlerComEcimRemoveTrustTaskHandler();
    }

    @Test
    @InSequence(1054)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfEngineStartWorkflowCPPRemoveTrustOAM() throws Exception {
        workflowEngineTest.testWfEngineStartWorkflowCPPRemoveTrustOAM();
    }

    @Test
    @InSequence(1055)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfEngineStartWorkflowCPPRemoveTrustNewIPSEC() throws Exception {
        workflowEngineTest.testWfEngineStartWorkflowCPPRemoveTrustNewIPSEC();
    }

    @Test
    @InSequence(1056)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfEngineStartWorkflowCOMRemoveTrust() throws Exception {
        workflowEngineTest.testWfEngineStartWorkflowCOMRemoveTrust();
    }

    @Inject
    private CredentialsTest credentials;

    @Test
    //@Ignore // Passed
    @InSequence(1101)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredUpdateCredentialsOneNode() throws Exception {
        credentials.updateCredentialsOneNode();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1102)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredUpdateCredentialsOneNodeOneParam() throws Exception {
        credentials.updateCredentialsOneNodeOneParam();
    }

    @Test
    @Ignore("InvalidNodeNameException: The NetworkElement specified does not exist")
    @InSequence(1103)
    @OperateOnDeployment("nscs-test-ear.ear")
    // credential update without params now is permitted at g4 level.
    public void testCredUpdateCredentialsNoParam() throws Exception {
        credentials.updateCredentialsNoParam();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1104)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredUpdateCredentialsDuplicateParam() throws Exception {
        credentials.updateCredentialsDuplicateParam();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1105)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredUpdateCredentialsMissingMO() throws Exception {
        credentials.updateCredentialsMissingMO();
    }

    @Inject
    private LdapConfigurationTest ldapConfigurationTest;

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1201)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testLdapValidConfigurationTest() throws IOException {
        ldapConfigurationTest.ldapValidConfigurationTest();

    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1202)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testLdapConfigurationPartialInvalidNodeTest() throws IOException {
        ldapConfigurationTest.ldapConfigurationPartialInvalidNodeTest();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1203)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testLdapConfigurationAllInvalidNodeTest() throws IOException {
        ldapConfigurationTest.ldapConfigurationAllInvalidNodeTest();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1204)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testLdapValidReConfigurationTest() throws IOException {
        ldapConfigurationTest.ldapValidReConfigurationTest();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1205)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testLdapReConfigurationPartialInvalidNodeTest() throws IOException {
        ldapConfigurationTest.ldapReConfigurationPartialInvalidNodeTest();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1206)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testLdapReConfigurationAllInvalidNodeTest() throws IOException {
        ldapConfigurationTest.ldapReConfigurationAllInvalidNodeTest();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1207)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testLdapInvalidConfigurationTest() throws IOException {
        ldapConfigurationTest.ldapInvalidConfigurationTest();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1208)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testLdapInvalidReConfigurationTest() throws IOException {
        ldapConfigurationTest.ldapInvalidReConfigurationTest();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1209)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testLdapConfigurationManualTest() {
        ldapConfigurationTest.ldapConfigurationManualTest();
    }

    @Inject
    private SnmpAuthTest snmpAuth;

    @Test
    //@Ignore // Passed
    @InSequence(1210)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthpriv() throws Exception {
        snmpAuth.snmpAuthpriv();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1211)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthnopriv() throws Exception {
        snmpAuth.snmpAuthnopriv();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1212)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthMiniLinkIndoorSnmpAuthPriv() throws Exception {
        snmpAuth.miniLinkIndoorSnmpAuthPriv();

    }

    @Test
    //@Ignore // Passed
    @InSequence(1213)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthMiniLinkIndoorSnmpAuthNoPriv() throws Exception {
        snmpAuth.miniLinkIndoorSnmpAuthNoPriv();

    }

    @Test
    //@Ignore // Passed
    @InSequence(1214)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredGetCredentialsSecureShow() throws Exception {
        credentials.getCredentialsSecureShow();

    }

    @Test
    //@Ignore // Passed
    @InSequence(1215)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredGetCredentialsSecureHide() throws Exception {
        credentials.getCredentialsSecureHide();

    }

    @Test
    //@Ignore // Passed
    @InSequence(1216)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredGetCredentialsSecureDefaultHide() throws Exception {
        credentials.getCredentialsSecureDefaultHide();

    }

    @Test
    //@Ignore // Passed
    @InSequence(1217)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredGetCredentialsRootHide() throws Exception {
        credentials.getCredentialsRootHide();

    }

    @Test
    //@Ignore // Passed
    @InSequence(1218)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredGetCredentialsRootShow() throws Exception {
        credentials.getCredentialsRootShow();

    }

    @Test
    //@Ignore // Passed
    @InSequence(1219)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredGetCredentialsRootDefaultHide() throws Exception {
        credentials.getCredentialsRootDefaultHide();

    }

    @Test
    //@Ignore // Passed
    @InSequence(1220)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredGetCredentialsNormalDefaultHide() throws Exception {
        credentials.getCredentialsNormalDefaultHide();

    }

    @Test
    //@Ignore // Passed
    @InSequence(1221)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredGetCredentialsNormalShow() throws Exception {
        credentials.getCredentialsNormalShow();

    }

    @Test
    //@Ignore // Passed
    @InSequence(1222)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredGetCredentialsNormalHide() throws Exception {
        credentials.getCredentialsNormalHide();

    }

    @Test
    //@Ignore // Passed
    @InSequence(1223)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredGetCredentialsSecureHideMissingNodes() throws Exception {
        credentials.getCredentialsSecureHideMissingNodes();

    }

    @Test
    //@Ignore // Passed
    @InSequence(1224)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredGetCredentialsRootHideMissingNodes() throws Exception {
        credentials.getCredentialsRootHideMissingNodes();

    }

    @Test
    //@Ignore // Passed
    @InSequence(1225)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredGetCredentialsNormalHideMissingNodes() throws Exception {
        credentials.getCredentialsNormalHideMissingNodes();

    }

    @Test
    //@Ignore // Passed
    @InSequence(1226)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredGetCredentialsNormalHideNotExistingNodes() throws Exception {
        credentials.getCredentialsNormalHideNotExistingNodes();

    }

    @Test
    //@Ignore // Passed
    @InSequence(1227)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredGetCredentialsSGSNWithNormalUserTypeShow() throws Exception {
        credentials.getCredentialsSGSNWithNormalUserTypeShow();

    }

    @Test
    //@Ignore // Passed
    @InSequence(1228)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredGetCredentialsSGSNWithRootUserTypeShow() throws Exception {
        credentials.getCredentialsSGSNWithRootUserTypeShow();

    }

    @Test
    //@Ignore // Passed
    @InSequence(1229)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredGetCredentialsSGSNWithNormalUserTypeHide() throws Exception {
        credentials.getCredentialsSGSNWithNormalUserTypeHide();

    }

    @Test
    //@Ignore // Passed
    @InSequence(1230)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredGetCredentialsSGSNWithRootUserTypeHide() throws Exception {
        credentials.getCredentialsSGSNWithRootUserTypeHide();

    }

    @Test
    //@Ignore // Passed
    @InSequence(1231)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredGetCredentialsWithoutUserTypeShow() throws Exception {
        credentials.getCredentialsWithoutUserTypeShow();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1232)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredGetCredentialsWithoutUserTypeAndPlainText() throws Exception {
        credentials.getCredentialsWithoutUserTypeAndPlainText();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1233)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredGetCredentialsWithoutUserTypeHide() throws Exception {
        credentials.getCredentialsWithoutUserTypeHide();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1234)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredGetCredentialsSGSNWithSecureUserTypeHide() throws Exception {
        credentials.getCredentialsSGSNWithSecureUserTypeHide();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1235)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredGetCredentialsSGSNWithSecureUserTypeShow() throws Exception {
        credentials.getCredentialsSGSNWithSecureUserTypeShow();
    }

    @Inject
    private GetJobCommadTests getJobCommadTests;

    @Test
    //@Ignore // Passed
    @InSequence(1236)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testJobGetAllJobsCommand_EmptyJobList() throws Exception {
        getJobCommadTests.getAllJobsCommand_EmptyJobList();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1237)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testJobGetSingleJobCommand_ShortVersion_NotExistingJob() throws Exception {
        getJobCommadTests.getSingleJobCommand_ShortVersion_NotExistingJob();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1238)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testJobGetListOfJobsCommand_NotExistingJobs() throws Exception {
        getJobCommadTests.jobGetListOfJobsCommand_NotExistingJobs();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1239)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testJobGetSingleJobCommand_ShortVersion_ExistingJob() throws Exception {
        getJobCommadTests.getSingleJobCommand_ShortVersion_ExistingJob();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1240)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testJobGetSingleJobCommand_LongVersion_ExistingJob() throws Exception {
        getJobCommadTests.getSingleJobCommand_LongVersion_ExistingJob();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1241)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testJobGetSingleJobCommand_LongVersion_NotExistingJob() throws Exception {
        getJobCommadTests.getSingleJobCommand_LongVersion_NotExistingJob();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1242)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testJobGetSingleJobCommand_LongVersion_ExistingCompletedJob() throws Exception {
        getJobCommadTests.getSingleJobCommand_LongVersion_ExistingCompletedJob();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1243)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testJobGetSingleJobCommand_InvalidJobIdFormat() throws Exception {
        getJobCommadTests.getSingleJobCommand_InvalidJobIdFormat();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1244)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testJobGetAllJobsCommand_NotEmptyJobList() throws Exception {
        getJobCommadTests.getAllJobsCommand_NotEmptyJobList();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1245)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testJobGetListOfJobsCommand_NotEmptyJobList() throws Exception {
        getJobCommadTests.getListOfJobsCommand_NotEmptyJobList();
    }

    @Test
    @InSequence(1246)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestJobGetPendingWorkflowsTest() throws Exception {
        restTests.testRestJobGetPendingWorkflowsTest();
    }

    @Test
    @InSequence(1247)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestJobCheckNoRunningWFbyNodeNameTest() throws Exception {
        restTests.testRestJobCheckNoRunningWFbyNodeNameTest();
    }

    @Test
    @InSequence(1248)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestJobGetRunningWorkflowCountTest() throws Exception {
        restTests.testRestJobGetRunningWorkflowCountTest();
    }

    @Test
    @InSequence(1249)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestPIBModelRestResource_neCertAutoRenewalTimer() throws Exception {
        restTests.testRestPIBModelRestResource_neCertAutoRenewalTimer();
    }

    @Test
    @InSequence(1250)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestPIBModelRestResource_neCertAutoRenewalEnabled() throws Exception {
        restTests.testRestPIBModelRestResource_neCertAutoRenewalEnabled();
    }

    @Test
    @InSequence(1251)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestPIBModelRestResource_neCertAutoRenewalMax() throws Exception {
        restTests.testRestPIBModelRestResource_neCertAutoRenewalMax();
    }

    @Test
    @InSequence(1252)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestPIBModelRestResource_wfCongestionThreshold() throws Exception {
        restTests.testRestPIBModelRestResource_wfCongestionThreshold();
    }

    @Test
    @InSequence(1253)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestPIBModelRestResource_BadRequest() throws Exception {
        restTests.testRestPIBModelRestResource_BadRequest();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1254)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpCiscoAsr9000SnmpAuthPriv() throws Exception {
        snmpAuth.ciscoAsr9000SnmpAuthPriv();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1255)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpCiscoAsr9000SnmpAuthNoPriv() throws Exception {
        snmpAuth.ciscoAsr9000SnmpAuthNoPriv();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1256)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpCiscoAsr900SnmpAuthPriv() throws Exception {
        snmpAuth.ciscoAsr900SnmpAuthPriv();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1257)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpCiscoAsr900SnmpAuthNoPriv() throws Exception {
        snmpAuth.ciscoAsr900SnmpAuthNoPriv();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1258)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpJuniperMxSnmpAuthPriv() throws Exception {
        snmpAuth.juniperMxSnmpAuthPriv();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1259)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpJuniperMxSnmpAuthNoPriv() throws Exception {
        snmpAuth.juniperMxSnmpAuthNoPriv();
    }

    @Inject
    private JobIdManagementTests jobManagmentTest;

    @Test
    //@Ignore // Passed
    @InSequence(1260)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testJobCertIssueRest_sync() throws Exception {
        jobManagmentTest.certIssueRest_sync();

    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1261)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testJobCertIssueRest_notSync() throws Exception {
        jobManagmentTest.certIssueRest_notSync();

    }

    @Test
    @InSequence(1262)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testRestJobEvictionTest() throws Exception {
        restTests.testRestJobEvictionTest();
    }

    @Inject
    private ComEcimCrlCheckEnableOrDisableTestImpl crlCheckTest;

    @Test
    //@Ignore // Passed
    @InSequence(1263)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckNodeDoesNotExist_Failure() throws Exception {
        crlCheckTest.testEnableCrlCheck_NodeDoesNotExist_Failure();

    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1264)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckTrustCategoryDoesNotExist_Failure() throws Exception {
        crlCheckTest.testEnableCrlCheck_TrustCategoryDoesNotExist_Failure();

    }

    @Test
    //@Ignore // Passed
    @InSequence(1265)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckInvalidCertificateType_Failure() throws Exception {
        crlCheckTest.testEnableCrlCheck_InvalidCertificateType_Failure();

    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1266)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckNodeNotInSynch_Failure() throws Exception {
        crlCheckTest.testEnableCrlCheck_NodeNotInSynch_Failure();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1267)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckNodeDoesNotExist_PartialSuccess() throws Exception {
        crlCheckTest.testEnableCrlCheck_NodeDoesNotExist_PartialSuccess();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1268)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckNodeNotInSynch_PartialSuccess() throws Exception {
        crlCheckTest.testEnableCrlCheck_NodeNotInSynch_PartialSuccess();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1269)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDuplicateNodes_Failure() throws Exception {
        crlCheckTest.testEnableCrlCheck_DuplicateNodes_Failure();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1270)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckWithWrongFileContent_Failure() throws Exception {
        crlCheckTest.testEnableCrlCheck_WithWrongFileContent_Failure();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1271)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckInvalidNodesWithFile_Failure() throws Exception {
        crlCheckTest.testEnableCrlCheck_InvalidNodesWithFile_Failure();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1272)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckWithMulitpleNodes_Failure() throws Exception {
        crlCheckTest.testEnableCrlCheck_WithMulitpleNodes_Failure();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1273)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckFileWithMulitpleNodes_Success() throws Exception {
        crlCheckTest.testEnableCrlCheck_FileWithMulitpleNodes_Success();

    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1274)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckSingleNodeWithOutFile_Success() throws Exception {
        crlCheckTest.testEnableCrlCheck_SingleNodeWithOutFile_Success();

    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1275)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckMulitpleNodesWithOutFile_Success() throws Exception {
        crlCheckTest.testEnableCrlCheck_MulitpleNodesWithOutFile_Success();

    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1276)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableFileWithMulitpleNodes_Success() throws Exception {
        crlCheckTest.testDisableCrlCheck_FileWithMulitpleNodes_Success();

    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1277)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableMulitpleNodesWithOutFile_Success() throws Exception {
        crlCheckTest.testDisableCrlCheck_MulitpleNodesWithOutFile_Success();

    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1278)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableSingleNodeWithOutFile_Success() throws Exception {
        crlCheckTest.testDisableCrlCheck_SingleNodeWithOutFile_Success();

    }

    @Test
    //@Ignore // Passed
    @InSequence(1279)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableNodeDoesNotExist_Failure() throws Exception {
        crlCheckTest.testDisableCrlCheck_NodeDoesNotExist_Failure();

    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1280)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableTrustCategoryDoesNotExist_Failure() throws Exception {
        crlCheckTest.testDisableCrlCheck_TrustCategoryDoesNotExist_Failure();

    }

    @Test
    //@Ignore // Passed
    @InSequence(1281)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableInvalidCertificateType_Failure() throws Exception {
        crlCheckTest.testDisableCrlCheck_InvalidCertificateType_Failure();

    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1282)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableNodeNotInSynch_Failure() throws Exception {
        crlCheckTest.testDisableCrlCheck_NodeNotInSynch_Failure();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1283)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableNodeDoesNotExist_PartialSuccess() throws Exception {
        crlCheckTest.testDisableCrlCheck_NodeDoesNotExist_PartialSuccess();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1284)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableNodeNotInSynch_PartialSuccess() throws Exception {
        crlCheckTest.testDisableCrlCheck_NodeNotInSynch_PartialSuccess();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1285)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableDuplicateNodes_Failure() throws Exception {
        crlCheckTest.testDisableCrlCheck_DuplicateNodes_Failure();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1286)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableWithWrongFileContent_Failure() throws Exception {
        crlCheckTest.testDisableCrlCheck_WithWrongFileContent_Failure();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1287)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableInvalidNodesWithFile_Failure() throws Exception {
        crlCheckTest.testDisableCrlCheck_InvalidNodesWithFile_Failure();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1288)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableWithMulitpleNodes_Failure() throws Exception {
        crlCheckTest.testDisableCrlCheck_WithMulitpleNodes_Failure();
    }

    @Test
    @InSequence(1289)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerComEcimEnableOrDisableCRLCheckTaskHandlerOAM_ACTIVATED() throws Exception {
        workflowHandlerTest.testWfHandlerComEcimEnableOrDisableCRLCheckTaskHandlerOAM_ACTIVATED();
    }

    @Test
    @InSequence(1290)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerComEcimEnableOrDisableCRLCheckTaskHandlerIPSEC_ACTIVATED() throws Exception {
        workflowHandlerTest.testWfHandlerComEcimEnableOrDisableCRLCheckTaskHandlerIPSEC_ACTIVATED();
    }

    @Test
    @InSequence(1291)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerComEcimEnableOrDisableCRLCheckTaskHandlerOAM_DEACTIVATED() throws Exception {
        workflowHandlerTest.testWfHandlerComEcimEnableOrDisableCRLCheckTaskHandlerOAM_DEACTIVATED();
    }

    @Test
    @InSequence(1292)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerComEcimEnableOrDisableCRLCheckTaskHandlerIPSEC_DEACTIVATED() throws Exception {
        workflowHandlerTest.testWfHandlerComEcimEnableOrDisableCRLCheckTaskHandlerIPSEC_DEACTIVATED();
    }

    @Test
    @InSequence(1293)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testJobGetSingleJobCommand_FilteredJob() throws Exception {
        getJobCommadTests.getSingleJobCommand_FilteredJob();
    }

    @Test
    @InSequence(1294)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerCleanupM2MUserAndSmrsTask() throws Exception {
        workflowHandlerTest.testWfHandlerCleanupM2MUserAndSmrsTask();
    }

    @Inject
    private CPPCrlCheckEnableOrDisableTestImpl cppCrlCheckTest;

    @Test
    //@Ignore // To Be investigated
    @InSequence(1295)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckEnableCPPNodeDoesNotExist_Failure() throws Exception {
        cppCrlCheckTest.testEnableCrlCheck_CPPNodeDoesNotExist_Failure();

    }

    @Test
    //@Ignore // To Be investigated
    @InSequence(1296)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckEnableCPPInvalidCertificateType_Failure() throws Exception {
        cppCrlCheckTest.testEnableCrlCheck_CPPInvalidCertificateType_Failure();

    }

    @Test
    //@Ignore // To Be investigated
    @InSequence(1300)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckEnableCPPWithWrongFileContent_Failure() throws Exception {
        cppCrlCheckTest.testEnableCrlCheck_CPPWithWrongFileContent_Failure();
    }

    @Test
    //@Ignore // To Be investigated
    @InSequence(1301)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckEnableCPPInvalidNodesWithFile_Failure() throws Exception {
        cppCrlCheckTest.testEnableCrlCheck_CPPInvalidNodesWithFile_Failure();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1302)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckEnableSecurityDoesNotExist_Failure() throws Exception {
        cppCrlCheckTest.testEnableCrlCheck_SecurityDoesNotExist_Failure();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1303)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckEnableCPPNodeNotInSynch_Failure() throws Exception {
        cppCrlCheckTest.testEnableCrlCheck_CPPNodeNotInSynch_Failure();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1304)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckEnableCPPDuplicateNodes_Failure() throws Exception {
        cppCrlCheckTest.testEnableCrlCheck_CPPDuplicateNodes_Failure();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1305)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckEnableWithMulitpleCPPNodes_Failure() throws Exception {
        cppCrlCheckTest.testEnableCrlCheck_WithMulitpleCPPNodes_Failure();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1307)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckEnableCPPNodeDoesNotExist_PartialSuccess() throws Exception {
        cppCrlCheckTest.testEnableCrlCheck_CPPNodeDoesNotExist_PartialSuccess();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1308)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckEnableCPPNodeNotInSynch_PartialSuccess() throws Exception {
        cppCrlCheckTest.testEnableCrlCheck_CPPNodeNotInSynch_PartialSuccess();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1309)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckEnableFileWithMulitpleCPPNodes_Success() throws Exception {
        cppCrlCheckTest.testEnableCrlCheck_FileWithMulitpleCPPNodes_Success();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1310)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckEnableSingleCPPNodeWithOutFile_Success() throws Exception {
        cppCrlCheckTest.testEnableCrlCheck_SingleCPPNodeWithOutFile_Success();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1311)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckEnableMulitpleCPPNodesWithOutFile_Success() throws Exception {
        cppCrlCheckTest.testEnableCrlCheck_MulitpleCPPNodesWithOutFile_Success();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1312)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableCPPWithWrongFileContent_Failure() throws Exception {
        cppCrlCheckTest.testDisableCrlCheck_CPPWithWrongFileContent_Failure();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1313)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableCPPInvalidNodesWithFile_Failure() throws Exception {
        cppCrlCheckTest.testDisableCrlCheck_CPPInvalidNodesWithFile_Failure();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1314)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableCPPNodeDoesNotExist_Failure() throws Exception {
        cppCrlCheckTest.testDisableCrlCheck_CPPNodeDoesNotExist_Failure();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1315)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableCPPInvalidCertificateType_Failure() throws Exception {
        cppCrlCheckTest.testDisableCrlCheck_CPPInvalidCertificateType_Failure();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1316)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableSecurityDoesNotExist_Failure() throws Exception {
        cppCrlCheckTest.testDisableCrlCheck_SecurityDoesNotExist_Failure();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1317)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableCPPNodeNotInSynch_Failure() throws Exception {
        cppCrlCheckTest.testDisableCrlCheck_CPPNodeNotInSynch_Failure();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1318)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableCPPDuplicateNodes_Failure() throws Exception {
        cppCrlCheckTest.testDisableCrlCheck_CPPDuplicateNodes_Failure();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1319)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableWithMulitpleCPPNodes_Failure() throws Exception {
        cppCrlCheckTest.testDisableCrlCheck_WithMulitpleCPPNodes_Failure();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1320)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableCPPNodeDoesNotExist_PartialSuccess() throws Exception {
        cppCrlCheckTest.testDisableCrlCheck_CPPNodeDoesNotExist_PartialSuccess();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1321)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableCPPNodeNotInSynch_PartialSuccess() throws Exception {
        cppCrlCheckTest.testDisableCrlCheck_CPPNodeNotInSynch_PartialSuccess();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1322)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableFileWithMulitpleCPPNodes_Success() throws Exception {
        cppCrlCheckTest.testDisableCrlCheck_FileWithMulitpleCPPNodes_Success();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1323)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableSingleCPPNodeWithOutFile_Success() throws Exception {
        cppCrlCheckTest.testDisableCrlCheck_SingleCPPNodeWithOutFile_Success();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1324)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckDisableMulitpleCPPNodesWithOutFile_Success() throws Exception {
        cppCrlCheckTest.testDisableCrlCheck_MulitpleCPPNodesWithOutFile_Success();
    }

    @Inject
    private CrlCheckReadTestImpl crlCheckReadTest;

    @Test
    //@Ignore // Passed
    @InSequence(1325)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckReadWithEmptyFile() throws Exception {
        crlCheckReadTest.testReadCrlCheck_WithEmptyFile();
    }

    @Test
    //@Ignore // To Be investigated
    @InSequence(1326)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckReadWithWrongFileContent() throws Exception {
        crlCheckReadTest.testReadCrlCheck_WithWrongFileContent();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1327)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckReadWithMulitpleNodes() throws Exception {
        crlCheckReadTest.testReadCrlCheck_WithMulitpleNodes();
    }

    @Test
    //@Ignore // To Be investigated
    @InSequence(1328)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckReadNodeDoesNotExist() throws Exception {
        crlCheckReadTest.testReadCrlCheck_NodeDoesNotExist();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1329)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckReadTrustCategoryDoesNotExist() throws Exception {
        crlCheckReadTest.testReadCrlCheck_TrustCategoryDoesNotExist();
    }

    @Test
    //@Ignore // To Be investigated
    @InSequence(1330)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckReadInvalidCertificateType() throws Exception {
        crlCheckReadTest.testReadCrlCheck_InvalidCertificateType();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1331)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCrlCheckReadNodeNotInSynch() throws Exception {
        crlCheckReadTest.testReadCrlCheck_NodeNotInSynch();
    }

    @Test
    @Ignore("EServiceNotFoundException: no registered @EService implementation for CmReaderService in EAP7")
    @InSequence(1332)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testWfHandlerComEcimPrepareCheckOnDemandCrlDownloadActionProgressTask() throws Exception {
        workflowHandlerTest.testWfHandlerComEcimPrepareCheckOnDemandCrlDownloadActionProgressTask();
    }

    @Inject
    private OnDemandCrlDownloadTestImpl OnDemandCrlDownloadTest;

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1333)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testOnDemandCrlDownload_NodeDoesNotExist_PartialSuccess() throws Exception {
        OnDemandCrlDownloadTest.testOnDemandCrlDownload_NodeDoesNotExist_PartialSuccess();

    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1334)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testOnDemandCrlDownload_NodeNotInSynch_PartialSuccess() throws Exception {
        OnDemandCrlDownloadTest.testOnDemandCrlDownload_NodeNotInSynch_PartialSuccess();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1335)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testOnDemandCrlDownload_DuplicateNodes_Success() throws Exception {
        OnDemandCrlDownloadTest.testOnDemandCrlDownload_DuplicateNodes_Success();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1336)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testOnDemandCrlDownload_WithWrongFileContent_Failure() throws Exception {
        OnDemandCrlDownloadTest.testOnDemandCrlDownload_WithWrongFileContent_Failure();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1337)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testOnDemandCrlDownload_InvalidNodesWithFile_Failure() throws Exception {
        OnDemandCrlDownloadTest.testOnDemandCrlDownload_InvalidNodesWithFile_Failure();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1338)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testOnDemandCrlDownload_NodeDoesNotExist_Failure() throws Exception {
        OnDemandCrlDownloadTest.testOnDemandCrlDownload_NodeDoesNotExist_Failure();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1339)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testOnDemandCrlDownload_NodeNotInSynch_Failure() throws Exception {
        OnDemandCrlDownloadTest.testOnDemandCrlDownload_NodeNotInSynch_Failure();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1340)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testOnDemandCrlDownload_SingleNodeWithOutFile_Success() throws Exception {
        OnDemandCrlDownloadTest.testOnDemandCrlDownload_SingleNodeWithOutFile_Success();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1341)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testOnDemandCrlDownload_FileWithMulitpleNodes_Success() throws Exception {
        OnDemandCrlDownloadTest.testOnDemandCrlDownload_FileWithMulitpleNodes_Success();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1342)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testOnDemandCrlDownload_MulitpleNodesWithOutFile_Success() throws Exception {
        OnDemandCrlDownloadTest.testOnDemandCrlDownload_MulitpleNodesWithOutFile_Success();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1343)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testOnDemandCrlDownload_WithMulitpleNodes_Failure() throws Exception {
        OnDemandCrlDownloadTest.testOnDemandCrlDownload_WithMulitpleNodes_Failure();
    }

    @Inject
    private NodeSecurityReplicatedCacheSortingTestsImpl nodeSecurityReplicatedCacheTestsImpl;

    @Test
    //@Ignore // Passed
    @InSequence(1346)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testReplicatedCacheSortNodesConfigurationStatusRecord_Test() throws Exception {
        nodeSecurityReplicatedCacheTestsImpl.sortNodesConfigurationStatusRecord_Test();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1347)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testReplicatedCacheSortNodesConfigurationStatusRecord_2KNodesFromRealCache_Test() throws Exception {
        nodeSecurityReplicatedCacheTestsImpl.sortNodesConfigurationStatusRecord_2KNodesFromRealCache_Test();
    }

    @Inject
    private CppOnDemandCrlDownloadTest cppOnDemandCrlDownloadTest;

    @Test
    //@Ignore // Passed
    @InSequence(1348)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testDownloadCrlWithWrongFileContent_Failure() throws Exception {
        cppOnDemandCrlDownloadTest.testDownloadCrl_WithWrongFileContent_Failure();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1349)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testDownloadCrlCPPInvalidNodesWithFile_Failure() throws Exception {
        cppOnDemandCrlDownloadTest.testDownloadCrl_CPPInvalidNodesWithFile_Failure();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1350)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testDownloadCrlCPPNodeDoesNotExist_Failure() throws Exception {
        cppOnDemandCrlDownloadTest.testDownloadCrl_CPPNodeDoesNotExist_Failure();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1351)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testDownloadCrlSecurityDoesNotExist_Failure() throws Exception {
        cppOnDemandCrlDownloadTest.testDownloadCrl_SecurityDoesNotExist_Failure();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1352)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testDownloadCrlCPPNodeNotInSynch_Failure() throws Exception {
        cppOnDemandCrlDownloadTest.testDownloadCrl_CPPNodeNotInSynch_Failure();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1353)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testDownloadCrlWithMulitpleCPPNodes_Failure() throws Exception {
        cppOnDemandCrlDownloadTest.testDownloadCrl_WithMulitpleCPPNodes_Failure();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1354)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testDownloadCrlCPPNodeDoesNotExist_PartialSuccess() throws Exception {
        cppOnDemandCrlDownloadTest.testDownloadCrl_CPPNodeDoesNotExist_PartialSuccess();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1355)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testDownloadCrlCPPNodeNotInSynch_PartialSuccess() throws Exception {
        cppOnDemandCrlDownloadTest.testDownloadCrl_CPPNodeNotInSynch_PartialSuccess();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1356)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testDownloadCrlFileWithMulitpleCPPNodes_Success() throws Exception {
        cppOnDemandCrlDownloadTest.testDownloadCrl_FileWithMulitpleCPPNodes_Success();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1357)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testDownloadCrlSingleCPPNodeWithOutFile_Success() throws Exception {
        cppOnDemandCrlDownloadTest.testDownloadCrl_SingleCPPNodeWithOutFile_Success();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1358)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testDownloadCrlMulitpleCPPNodesWithOutFile_Success() throws Exception {
        cppOnDemandCrlDownloadTest.testDownloadCrl_MulitpleCPPNodesWithOutFile_Success();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1359)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredSwitch6391CreateCredentials() throws Exception {
        createCredTest.testSwitch6391CreateCredentials();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1360)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthSwitch6391SnmpAuthPriv() throws Exception {
        snmpAuth.switch6391SnmpAuthPriv();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1361)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthSwitch6391SnmpAuthNoPriv() throws Exception {
        snmpAuth.switch6391SnmpAuthNoPriv();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1362)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredMiniLink6352CreateCredentials() throws Exception {
        createCredTest.testMiniLink6352CreateCredentials();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1363)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthMiniLink6352SnmpAuthPriv() throws Exception {
        snmpAuth.miniLink6352SnmpAuthPriv();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1364)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthMiniLink6352SnmpAuthNoPriv() throws Exception {
        snmpAuth.miniLink6352SnmpAuthNoPriv();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1365)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredMiniLink6351CreateCredentials() throws Exception {
        createCredTest.testMiniLink6351CreateCredentials();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1366)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthMiniLink6351SnmpAuthPriv() throws Exception {
        snmpAuth.miniLink6351SnmpAuthPriv();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1367)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthMiniLink6351SnmpAuthNoPriv() throws Exception {
        snmpAuth.miniLink6351SnmpAuthNoPriv();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1368)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredMiniLinkPT2020CreateCredentials() throws Exception {
        createCredTest.testMiniLinkPT2020CreateCredentials();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1369)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthMiniLinkPT2020SnmpAuthPriv() throws Exception {
        snmpAuth.miniLinkPT2020SnmpAuthPriv();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1370)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthMiniLinkPT2020SnmpAuthNoPriv() throws Exception {
        snmpAuth.miniLinkPT2020SnmpAuthNoPriv();
    }

    @Test
    @Ignore("Test to be removed since it uses the node-security-jar embedded in nscs-test-ear.ear")
    @InSequence(1375)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testPkiPrepareInitEnrollmentForRBS13B() throws Exception {
        cppSecServTest.prepareInitEnrollmentForRBS13B();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1371)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthMiniLinkCn210SnmpAuthPriv() throws Exception {
        snmpAuth.miniLinkCn210SnmpAuthPriv();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1392)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthMiniLink665xSnmpAuthNoPriv() throws Exception {
        snmpAuth.miniLink665xSnmpAuthNoPriv();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1393)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthMiniLink669xSnmpAuthNoPriv() throws Exception {
        snmpAuth.miniLink669xSnmpAuthNoPriv();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1395)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthMiniLinkCn510R2SnmpAuthNoPriv() throws Exception {
        snmpAuth.miniLinkCn510R2SnmpAuthNoPriv();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1396)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthMiniLinkCn510R2SnmpAuthPriv() throws Exception {
        snmpAuth.miniLinkCn510R2SnmpAuthPriv();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1372)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthMiniLinkCn210SnmpAuthNoPriv() throws Exception {
        snmpAuth.miniLinkCn210SnmpAuthNoPriv();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1373)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthMiniLinkCn510R1SnmpAuthPriv() throws Exception {
        snmpAuth.miniLinkCn510R1SnmpAuthPriv();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1374)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthMiniLinkCn510R1SnmpAuthNoPriv() throws Exception {
        snmpAuth.miniLinkCn510R1SnmpAuthNoPriv();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1375)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthMiniLinkCn810R1SnmpAuthPriv() throws Exception {
        snmpAuth.miniLinkCn810R1SnmpAuthPriv();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1376)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthMiniLinkCn810R1SnmpAuthNoPriv() throws Exception {
        snmpAuth.miniLinkCn810R1SnmpAuthNoPriv();
    }

    @Test
    //@Ignore // TPassed
    @InSequence(1377)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthMiniLinkCn810R2SnmpAuthPriv() throws Exception {
        snmpAuth.miniLinkCn810R2SnmpAuthPriv();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1378)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthMiniLinkCn810R2SnmpAuthNoPriv() throws Exception {
        snmpAuth.miniLinkCn810R2SnmpAuthNoPriv();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1379)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredMiniLinkCn210CreateCredentials() throws Exception {
        createCredTest.testMiniLinkCn210CreateCredentials();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1380)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredMiniLinkCn510R1CreateCredentials() throws Exception {
        createCredTest.testMiniLinkCn510R1CreateCredentials();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1381)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredMiniLinkCn810R1CreateCredentials() throws Exception {
        createCredTest.testMiniLinkCn810R1CreateCredentials();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1382)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredMiniLinkCn810R2CreateCredentials() throws Exception {
        createCredTest.testMiniLinkCn810R2CreateCredentials();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1393)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredMiniLink665xCreateCredentials() throws Exception {
        createCredTest.testMiniLink665xCreateCredentials();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1394)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredMiniLink669xCreateCredentials() throws Exception {
        createCredTest.testMiniLink669xCreateCredentials();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1397)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredMiniLinkCn510R2CreateCredentials() throws Exception {
        createCredTest.testMiniLinkCn510R2CreateCredentials();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1383)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredFronthaul6392CreateCredentials() throws Exception {
        createCredTest.testFronthaul6392CreateCredentials();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1384)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthFronthaul6392SnmpAuthPriv() throws Exception {
        snmpAuth.fronthaul6392SnmpAuthPriv();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1385)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthFronthaul6392SnmpAuthNoPriv() throws Exception {
        snmpAuth.fronthaul6392SnmpAuthNoPriv();
    }

    @Inject
    private SecurityProtocolChangeTests securityProtocolChangeTests;

    private SecurityProtocol https = SecurityProtocol.HTTPS;
    private SecurityProtocol ftpes = SecurityProtocol.FTPES;

    @Test
    //@Ignore // Passed
    @InSequence(1386)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testHttpsDeactivateHttpsCommandShouldInformWhenSpecifiedNodeNotExists() throws Exception {
        securityProtocolChangeTests.nodeNotExistTest(https, https.deactivate());
    }

    @Test
    //@Ignore // Passed
    @InSequence(1387)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testHttpsActivateHttpsCommandShouldInformWhenSpecifiedNodeNotExists() throws Exception {
        securityProtocolChangeTests.nodeNotExistTest(https, https.activate());
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1388)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testHttpsActivateHttpsCommandShouldInformWhenSpecifiedNodeIsNotSynchronized() throws Exception {
        securityProtocolChangeTests.nodeNotSyncTest(https, https.activate());
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1389)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testHttpsDeactivateHttpsCommandShouldInformWhenSpecifiedNodeIsNotSynchronized() throws Exception {
        securityProtocolChangeTests.nodeNotSyncTest(https, https.deactivate());
    }

    @Test
    //@Ignore // Passed
    @InSequence(1390)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testHttpsActivateHttpsCommandShouldInformWhenNodeTypeIsWrong() throws Exception {
        securityProtocolChangeTests.wrongNodeTypeTest(https, https.activate());
    }

    @Test
    //@Ignore // Passed
    @InSequence(1391)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testHttpsDeactivateHttpsCommandShouldInformWhenNodeTypeIsWrong() throws Exception {
        securityProtocolChangeTests.wrongNodeTypeTest(https, https.deactivate());
    }

    @Test
    //@Ignore // Passed
    @InSequence(1392)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testHttpsActivateFtpesCommandShouldInformWhenSpecifiedNodeNotExists() throws Exception {
        securityProtocolChangeTests.nodeNotExistTest(ftpes, ftpes.activate());
    }

    @Test
    //@Ignore // Passed
    @InSequence(1393)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testHttpsDeactivateFtpesCommandShouldInformWhenSpecifiedNodeNotExists() throws Exception {
        securityProtocolChangeTests.nodeNotExistTest(ftpes, ftpes.deactivate());
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1394)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testHttpsActivateFtpesCommandShouldInformWhenSpecifiedNodeIsNotSynchronized() throws Exception {
        securityProtocolChangeTests.nodeNotSyncTest(ftpes, ftpes.activate());
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1395)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testHttpsDeactivateFtpesCommandShouldInformWhenSpecifiedNodeIsNotSynchronized() throws Exception {
        securityProtocolChangeTests.nodeNotSyncTest(ftpes, ftpes.deactivate());
    }

    @Test
    //@Ignore // Passed
    @InSequence(1396)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testHttpsActivateFtpesCommandShouldInformWhenNodeTypeIsWrong() throws Exception {
        securityProtocolChangeTests.wrongNodeTypeTest(ftpes, ftpes.activate());
    }

    @Test
    //@Ignore // Passed
    @InSequence(1397)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testHttpsDeactivateFtpesCommandShouldInformWhenNodeTypeIsWrong() throws Exception {
        securityProtocolChangeTests.wrongNodeTypeTest(ftpes, ftpes.deactivate());
    }

    @Test
    //@Ignore // Passed
    @InSequence(1398)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthMiniLink6366SnmpAuthNoPriv() throws Exception {
        snmpAuth.miniLink6366SnmpAuthNoPriv();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1399)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthMiniLink6366SnmpAuthPriv() throws Exception {
        snmpAuth.miniLink6366SnmpAuthPriv();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1400)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredMiniLink6366CreateCredentials() throws Exception {
        createCredTest.testMiniLink6366CreateCredentials();
    }

    @Test
    //@Ignore // Passed
    @InSequence(1401)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testSnmpAuthMiniLinkMW2SnmpAuthNoPriv() throws Exception {
        snmpAuth.miniLinkMW2SnmpAuthNoPriv();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(1402)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testCredMiniLinkMW2CreateCredentials() throws Exception {
        createCredTest.testMiniLinkMW2CreateCredentials();
    }
}