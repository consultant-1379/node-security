package com.ericsson.nms.security.nscs.integration.jee.test.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.integration.jee.test.events.FMMessageSender;
import com.ericsson.nms.security.nscs.integration.jee.test.rest.RestHelper;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityDataSetup;
import com.ericsson.oss.itpf.smrs.SmrsAccount;
import com.ericsson.oss.itpf.smrs.SmrsService;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowNames;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;
import com.ericsson.oss.services.wfs.api.instance.WorkflowInstance;

public class WorkflowEngineTest implements WorkflowTests {

    private static final String _272 = "27";

    private static final int _27 = 27;

    private static final int _9 = 9;

    private static final String NODE_123 = "node123";
    private static final String NODE_123_FDN = "MeContext=" + NODE_123;
    private static final NodeReference NODE = new NodeRef(NODE_123_FDN);
    private static final String _92 = "9";

    public static final int PROTO_TIMEOUT = 8000;
    public static final int WF_FAIL_TIMEOUT = 8000;
    public static final int WF_SL2_TIMEOUT = 18000;
    public static final int WF_MEDIUM_TIMEOUT = 60000;
    public static final int WF_LONG_TIMEOUT = 120000;

    @Inject
    private WorkflowHandler wfh;

    @Inject
    private SmrsService smrsService;

    @Inject
    private NscsPkiEntitiesManagerIF nscsPkiManager;

    @Inject
    private NodeSecurityDataSetup data;

    @Inject
    private FMMessageSender fmmessageSender;

    @Inject
    private Logger logger;

    @Override
    public void testWfEngineStartWorkflowProto() throws Exception {

        logger.info("testWfEngineStartWorkflowProto..........................................starts");

        final List<NodeReference> nodes = new LinkedList<>();
        nodes.add(NODE);

        final Set<WorkflowInstance> workflows = wfh.startWorkflowInstances(nodes, "proto");
        Thread.sleep(PROTO_TIMEOUT);
        assertWorkflowsInstantiated(workflows, "proto");
        assertCertsPublished(NODE.getName());

        cleanCertsPublished(NODE.getName());

        logger.info("testWfEngineStartWorkflowProto..........................................ends");
    }

    @Override
    public void testWfEngineStartWorkflowCPPActivateSL2() throws Exception {

        logger.info("testWfEngineStartWorkflowCPPActivateSL2..........................................starts");

        setup();
        final List<NodeReference> nodes = new LinkedList<>();
        nodes.add(NODE);
        final Set<WorkflowInstance> workflows = wfh.startWorkflowInstances(nodes, "CPPActivateSL2");
        Thread.sleep(15000);

        assertWorkflowsInstantiated(workflows, "CPPActivateSL2");

        final ProcessedAlarmEvent processedAlarmEvent = new ProcessedAlarmEvent();
        processedAlarmEvent.setFdn(NODE_123_FDN);

        processedAlarmEvent.setAlarmId(new Long(_9));
        processedAlarmEvent.setSpecificProblem(_92);
        fmmessageSender.sendMessage(processedAlarmEvent);
        Thread.sleep(15000);
        processedAlarmEvent.setAlarmId(new Long(_27));
        processedAlarmEvent.setSpecificProblem(_272);
        fmmessageSender.sendMessage(processedAlarmEvent);
        Thread.sleep(15000);
        tearDown();

        logger.info("testWfEngineStartWorkflowCPPActivateSL2..........................................ends");
    }

    @Override
    public void testWfEngineStartWorkflowCPPDeactivateSL2() throws Exception {

        logger.info("testWfEngineStartWorkflowCPPDeactivateSL2..........................................starts");

        setup();

        final List<NodeReference> nodes = new LinkedList<>();
        nodes.add(NODE);
        final Set<WorkflowInstance> workflows = wfh.startWorkflowInstances(nodes, "CPPDeactivateSL2");
        Thread.sleep(15000);

        assertWorkflowsInstantiated(workflows, "CPPDeactivateSL2");

        tearDown();

        logger.info("testWfEngineStartWorkflowCPPDeactivateSL2..........................................ends");
    }

    @Override
    public void testWfEngineStartWorkflowCPPActivateSL2_Negative() throws Exception {

        logger.info("testWfEngineStartWorkflowCPPActivateSL2_Negative..........................................starts");

        /*
         * Starts workflow on a not existent node
         */
        final List<NodeReference> nodes = new LinkedList<>();
        final String nodeName = "node1";
        nodes.add(new NodeRef(nodeName));

        final Map<String, Object> workflowVars = new HashMap<String, Object>();
        workflowVars.put(WorkflowParameterKeys.TRUSTED_CATEGORY.toString(), TrustedCertCategory.CORBA_PEERS.toString());

        final Set<WorkflowInstance> workflows = wfh.startWorkflowInstances(nodes, "CPPActivateSL2", workflowVars);
        Thread.sleep(WF_SL2_TIMEOUT);

        assertWorkflowsInstantiated(workflows, "CPPActivateSL2");

        Thread.sleep(8000);

        assertCertsNotPublished(nodeName);

        logger.info("testWfEngineStartWorkflowCPPActivateSL2_Negative..........................................ends");
    }

    @Override
    public void testWfEngineStartWorkflowSSHKeyGeneration() throws Exception {

        logger.info("testWfEngineStartWorkflowSSHKeyGeneration..........................................starts");

        setup();
        final String wfName = WorkflowNames.WORKFLOW_SSHKeyGeneration.toString();
        final List<NodeReference> nodes = new LinkedList<>();
        nodes.add(NODE);

        final Map<String, Object> workflowVars = new HashMap<String, Object>();
        workflowVars.put(WorkflowParameterKeys.KEY_SSHKEYS_GENERATION_ISCREATE.toString(), "true");
        workflowVars.put(WorkflowParameterKeys.KEY_SSHKEYS_GENERATION_ALGORITHM.toString(), AlgorithmKeys.RSA_2048.toString());

        final Set<WorkflowInstance> workflows = wfh.startWorkflowInstances(nodes, wfName, workflowVars);
        Thread.sleep(WF_LONG_TIMEOUT);

        assertWorkflowsInstantiated(workflows, wfName);

        tearDown();

        logger.info("testWfEngineStartWorkflowSSHKeyGeneration..........................................ends");
    }

    @Override
    public void testWfEngineStartWorkflowCPPReissueCertificate() throws Exception {

        logger.info("testWfEngineStartWorkflowCPPReissueCertificate..........................................starts");

        this.startWorkflowCPPReissueCertificate(true);

        logger.info("testWfEngineStartWorkflowCPPReissueCertificate..........................................ends");
    }

    @Override
    public void testWfEngineStartWorkflowCPPIssueCertificate() throws Exception {

        logger.info("testWfEngineStartWorkflowCPPIssueCertificate..........................................starts");

        this.startWorkflowCPPReissueCertificate(false);

        logger.info("testWfEngineStartWorkflowCPPIssueCertificate..........................................ends");

    }

    private void startWorkflowCPPReissueCertificate(final boolean isReissueRequired) throws Exception {
        setup();
        final String wfName = WorkflowNames.WORKFLOW_CPPIssueCertificate.toString();
        final List<NodeReference> nodes = new LinkedList<>();
        nodes.add(NODE);

        final Map<String, Object> workflowVars = new HashMap<String, Object>();
        workflowVars.put(WorkflowParameterKeys.CERTIFICATE_ISREISSUE.toString(), String.valueOf(isReissueRequired));
        workflowVars.put(WorkflowParameterKeys.ENTITY_PROFILE_NAME.toString(), "Entity");
        workflowVars.put(WorkflowParameterKeys.ENROLLMENT_MODE.toString(), EnrollmentMode.SCEP.toString());
        workflowVars.put(WorkflowParameterKeys.KEY_SSHKEYS_GENERATION_ALGORITHM.toString(), AlgorithmKeys.RSA_1024.toString());
        if (isReissueRequired) {
            workflowVars.put(WorkflowParameterKeys.CERTIFICATE_ID.toString(), "334455");
            workflowVars.put(WorkflowParameterKeys.CERTIFICATE_AUTHORITY_ID.toString(), "ABCCBA");
            workflowVars.put(WorkflowParameterKeys.REVOCATION_REASON.toString(), "keyCompromise");
        }

        final Set<WorkflowInstance> workflows = wfh.startWorkflowInstances(nodes, wfName, workflowVars);
        Thread.sleep(WF_MEDIUM_TIMEOUT);

        assertWorkflowsInstantiated(workflows, wfName);

        tearDown();
    }

    @Override
    public void testWfEngineStartWorkflowRevokeNodeCertificate() throws Exception {

        logger.info("testWfEngineStartWorkflowRevokeNodeCertificate..........................................starts");

        setup();
        final String wfName = WorkflowNames.WORKFLOW_RevokeNodeCertificate.toString();
        final List<NodeReference> nodes = new LinkedList<>();
        nodes.add(NODE);

        final Map<String, Object> workflowVars = new HashMap<String, Object>();
        workflowVars.put(WorkflowParameterKeys.CERTIFICATE_ID.toString(), "111334455");
        workflowVars.put(WorkflowParameterKeys.CERTIFICATE_AUTHORITY_ID.toString(), "000ABCCBA");
        workflowVars.put(WorkflowParameterKeys.REVOCATION_REASON.toString(), "keyCompromise");

        final Set<WorkflowInstance> workflows = wfh.startWorkflowInstances(nodes, wfName, workflowVars);
        Thread.sleep(15000);

        assertWorkflowsInstantiated(workflows, wfName);

        tearDown();

        logger.info("testWfEngineStartWorkflowRevokeNodeCertificate..........................................ends");
    }

    @Override
    public void testWfEngineStartWorkflowCPPIssueTrustCert() throws Exception {

        logger.info("testWfEngineStartWorkflowCPPIssueTrustCert..........................................starts");

        setup();
        final String wfName = WorkflowNames.WORKFLOW_CPPIssueTrustCert.toString();
        final List<NodeReference> nodes = new LinkedList<>();
        nodes.add(NODE);

        final Map<String, Object> workflowVars = new HashMap<String, Object>();
        workflowVars.put(WorkflowParameterKeys.TRUSTED_CATEGORY.toString(), TrustedCertCategory.CORBA_PEERS.toString());

        final Set<WorkflowInstance> workflows = wfh.startWorkflowInstances(nodes, wfName, workflowVars);
        Thread.sleep(15000);

        assertWorkflowsInstantiated(workflows, wfName);

        tearDown();

        logger.info("testWfEngineStartWorkflowCPPIssueTrustCert..........................................ends");
    }

    @Override
    public void testRestWorkflowStartWorkflowForOneNode() throws Exception {

        logger.info("testRestWorkflowStartWorkflowForOneNode..........................................starts");

        final HttpResponse response = restStartWorkflows("proto", "node_rest_test");
        Thread.sleep(PROTO_TIMEOUT);
        assertEquals("Status code should be 200 OK", 200, response.getStatusLine().getStatusCode());

        final String responseContent = EntityUtils.toString(response.getEntity(), "UTF-8");

        assertTrue(responseContent.toLowerCase().indexOf("workflows: 1") > 0);
        tearDown();

        logger.info("testRestWorkflowStartWorkflowForOneNode..........................................ends");
    }

    @Override
    public void testRestWorkflowStartWorkflowForMultipleNodes() throws Exception {

        logger.info("testRestWorkflowStartWorkflowForMultipleNodes..........................................starts");

        final HttpResponse response = restStartWorkflows("proto", "rest_multiple1&rest_multiple3&rest_multiple2");
        Thread.sleep(PROTO_TIMEOUT);
        assertEquals("Status code should be 200 OK", 200, response.getStatusLine().getStatusCode());

        final String responseContent = EntityUtils.toString(response.getEntity(), "UTF-8");
        System.out.println("testRestInterfaceStartWorkflowForMultipleNodes()");
        System.out.println(responseContent);
        assertTrue(responseContent.toLowerCase().indexOf("workflows: 3") > 0);
        tearDown();

        logger.info("testRestWorkflowStartWorkflowForMultipleNodes..........................................ends");
    }

    @Override
    public void testRestWorkflowStartNonExistingWorkflow() throws Exception {

        logger.info("testRestWorkflowStartNonExistingWorkflow..........................................starts");

        final HttpResponse response = restStartWorkflows("non_existing_workflow", "node_for_non_existing_wf");
        Thread.sleep(WF_FAIL_TIMEOUT);
        assertEquals("Status code should be 500", 500, response.getStatusLine().getStatusCode());
        tearDown();

        logger.info("testRestWorkflowStartNonExistingWorkflow..........................................ends");
    }

    @Override
    public void testRestWorkflowStartActivateSL2Workflow() throws Exception {

        logger.info("testRestWorkflowStartActivateSL2Workflow..........................................starts");

        final HttpResponse response = restStartWorkflows("CPPActivateSL2", "node_for_non_existing_wf");
        Thread.sleep(WF_FAIL_TIMEOUT);
        assertEquals("Status code should be 200 OK", 200, response.getStatusLine().getStatusCode());
        tearDown();

        logger.info("testRestWorkflowStartActivateSL2Workflow..........................................ends");
    }

    @Override
    public void testRestWorkflowStartCPPIssueWorkflow() throws Exception {

        logger.info("testRestWorkflowStartCPPIssueWorkflow..........................................starts");

        String wfVariables = "";
        wfVariables += WorkflowParameterKeys.CERTIFICATE_ISREISSUE.toString() + "=" + "false";
        wfVariables += "," + WorkflowParameterKeys.ENTITY_PROFILE_NAME.toString() + "=" + "Entity";
        wfVariables += "," + WorkflowParameterKeys.ENROLLMENT_MODE.toString() + "=" + EnrollmentMode.SCEP.toString();
        wfVariables += "," + WorkflowParameterKeys.KEY_SSHKEYS_GENERATION_ALGORITHM.toString() + "=" + AlgorithmKeys.RSA_1024.toString();

        final HttpResponse response = restStartWorkflowsWithVariables(WorkflowNames.WORKFLOW_CPPIssueCertificate.toString(), "NODE_123_FDN",
                wfVariables);
        Thread.sleep(WF_FAIL_TIMEOUT);
        assertEquals("Status code should be 200 OK", 200, response.getStatusLine().getStatusCode());
        tearDown();

        logger.info("testRestWorkflowStartCPPIssueWorkflow..........................................ends");
    }

    @Override
    public void testRestWorkflowStartCPPReissueWorkflow() throws Exception {

        logger.info("testRestWorkflowStartCPPReissueWorkflow..........................................starts");

        String wfVariables = "";
        wfVariables += WorkflowParameterKeys.CERTIFICATE_ISREISSUE.toString() + "=" + "true";
        wfVariables += "," + WorkflowParameterKeys.CERTIFICATE_ID.toString() + "=" + "334455";
        wfVariables += "," + WorkflowParameterKeys.CERTIFICATE_AUTHORITY_ID.toString() + "=" + "ABCCBA";
        wfVariables += "," + WorkflowParameterKeys.ENTITY_PROFILE_NAME.toString() + "=" + "Entity";
        wfVariables += "," + WorkflowParameterKeys.ENROLLMENT_MODE.toString() + "=" + EnrollmentMode.SCEP.toString();
        wfVariables += "," + WorkflowParameterKeys.KEY_SSHKEYS_GENERATION_ALGORITHM.toString() + "=" + AlgorithmKeys.RSA_1024.toString();
        wfVariables += "," + WorkflowParameterKeys.REVOCATION_REASON.toString() + "=" + "keyCompromise";

        final HttpResponse response = restStartWorkflowsWithVariables(WorkflowNames.WORKFLOW_CPPIssueCertificate.toString(), "NODE_123_FDN",
                wfVariables);
        Thread.sleep(WF_FAIL_TIMEOUT);
        assertEquals("Status code should be 200 OK", 200, response.getStatusLine().getStatusCode());
        tearDown();

        logger.info("testRestWorkflowStartCPPReissueWorkflow..........................................ends");
    }

    @Override
    public void testRestWorkflowStartSSHKeyGenerationWorkflow_Create() throws Exception {

        logger.info("testRestWorkflowStartSSHKeyGenerationWorkflow_Create..........................................starts");

        this.executeRestSSHKeyGeneration(true);

        logger.info("testRestWorkflowStartSSHKeyGenerationWorkflow_Create..........................................ends");
    }

    @Override
    public void testRestWorkflowStartSSHKeyGenerationWorkflow_Update() throws Exception {

        logger.info("testRestWorkflowStartSSHKeyGenerationWorkflow_Update..........................................starts");

        this.executeRestSSHKeyGeneration(false);

        logger.info("testRestWorkflowStartSSHKeyGenerationWorkflow_Update..........................................ends");
    }

    @Override
    public void testRestWorkflowWorkflowDebug_getwfStatus() throws Exception {

        logger.info("testRestWorkflowWorkflowDebug_getwfStatus..........................................starts");

        final HttpResponse response = restWorkflowsDebug("getwfstatus");
        Thread.sleep(WF_FAIL_TIMEOUT);
        assertEquals("Status code should be 200 OK", 200, response.getStatusLine().getStatusCode());
        tearDown();

        logger.info("testRestWorkflowWorkflowDebug_getwfStatus..........................................ends");
    }

    @Override
    public void testRestWorkflowWorkflowDebug_getwfStatusWithListOfnodes() throws Exception {

        logger.info("testRestWorkflowWorkflowDebug_getwfStatusWithListOfnodes..........................................starts");

        final HttpResponse response = restWorkflowsDebug("getwfstatus?nodeList=NODE_123_FDN");
        Thread.sleep(WF_FAIL_TIMEOUT);
        assertEquals("Status code should be 200 OK", 200, response.getStatusLine().getStatusCode());
        tearDown();

        logger.info("testRestWorkflowWorkflowDebug_getwfStatusWithListOfnodes..........................................ends");
    }

    @Override
    public void testRestWorkflowWorkflowDebug_getwfstats() throws Exception {

        logger.info("testRestWorkflowWorkflowDebug_getwfstats..........................................starts");

        final HttpResponse response = restWorkflowsDebug("getwfstats");
        Thread.sleep(WF_FAIL_TIMEOUT);
        assertEquals("Status code should be 200 OK", 200, response.getStatusLine().getStatusCode());
        tearDown();

        logger.info("testRestWorkflowWorkflowDebug_getwfstats..........................................ends");
    }

    @Override
    public void testRestWorkflowWorkflowDebug_getwffinalstatus() throws Exception {

        logger.info("testRestWorkflowWorkflowDebug_getwffinalstatus..........................................starts");

        final HttpResponse response = restWorkflowsDebug("getwffinalstatus");
        Thread.sleep(WF_FAIL_TIMEOUT);
        assertEquals("Status code should be 200 OK", 200, response.getStatusLine().getStatusCode());
        tearDown();

        logger.info("testRestWorkflowWorkflowDebug_getwffinalstatus..........................................ends");
    }

    @Override
    public void testRestWorkflowWorkflowDebug_getwffinalstatus_Success() throws Exception {

        logger.info("testRestWorkflowWorkflowDebug_getwffinalstatus_Success..........................................starts");

        final HttpResponse response = restWorkflowsDebug("getwffinalstatus/success/count");
        Thread.sleep(WF_FAIL_TIMEOUT);
        assertEquals("Status code should be 200 OK", 200, response.getStatusLine().getStatusCode());
        tearDown();

        logger.info("testRestWorkflowWorkflowDebug_getwffinalstatus_Success..........................................ends");
    }

    @Override
    public void testRestWorkflowWorkflowDebug_getwffinalstatus_Failed() throws Exception {

        logger.info("testRestWorkflowWorkflowDebug_getwffinalstatus_Failed..........................................starts");

        final HttpResponse response = restWorkflowsDebug("getwffinalstatus/failed/count");
        Thread.sleep(WF_FAIL_TIMEOUT);
        assertEquals("Status code should be 200 OK", 200, response.getStatusLine().getStatusCode());
        tearDown();

        logger.info("testRestWorkflowWorkflowDebug_getwffinalstatus_Failed..........................................ends");
    }

    @Override
    public void testRestWorkflowWorkflowDebug_getwffinalstatus_Timeout() throws Exception {

        logger.info("testRestWorkflowWorkflowDebug_getwffinalstatus_Timeout..........................................starts");

        final HttpResponse response = restWorkflowsDebug("getwffinalstatus/timeout/count");
        Thread.sleep(WF_FAIL_TIMEOUT);
        assertEquals("Status code should be 200 OK", 200, response.getStatusLine().getStatusCode());
        tearDown();

        logger.info("testRestWorkflowWorkflowDebug_getwffinalstatus_Timeout..........................................ends");
    }

    @Override
    public void testRestWorkflowWorkflowDebug_resetwfinstances() throws Exception {

        logger.info("testRestWorkflowWorkflowDebug_resetwfinstances..........................................starts");

        final HttpResponse response = restWorkflowsDebug("resetwfinstances");
        Thread.sleep(WF_FAIL_TIMEOUT);
        assertEquals("Status code should be 200 OK", 200, response.getStatusLine().getStatusCode());
        tearDown();

        logger.info("testRestWorkflowWorkflowDebug_resetwfinstances..........................................ends");
    }

    @Override
    public void testWfEngineStartWorkflowCOMIssueTrustCert() throws Exception {

        logger.info("testWfEngineStartWorkflowCOMIssueTrustCert..........................................starts");

        logger.warn("testStartWorkflowCOMIssueTrustCertEnroll..starts - NOT IMPLEMENTED YET!!!!");
        final String wfName = WorkflowNames.WORKFLOW_COMECIM_ComIssueTrustCert.toString();
        final List<NodeReference> nodes = new LinkedList<>();
        nodes.add(NODE);

        /*
         * setup();
         */

        final Map<String, Object> workflowVars = new HashMap<String, Object>();
        workflowVars.put(WorkflowParameterKeys.TRUSTED_CATEGORY.toString(), TrustedCertCategory.CORBA_PEERS.toString());
        workflowVars.put(WorkflowParameterKeys.CERTIFICATE_ENROLLMENT_CA.toString(), "ENM_PKI_CA");
        final Set<WorkflowInstance> workflows = wfh.startWorkflowInstances(nodes, wfName, workflowVars);
        Thread.sleep(15000);

        assertWorkflowsInstantiated(workflows, wfName);

        tearDown();

        logger.warn("testStartWorkflowCOMIssueTrustCertEnroll.....ends");

        logger.info("testWfEngineStartWorkflowCOMIssueTrustCert..........................................ends");
    }

    @Override
    public void testWfEngineStartWorkflowCOMIssueCert() throws Exception {

        logger.info("testWfEngineStartWorkflowCOMIssueCert..........................................starts");

        logger.warn("testStartWorkflowCOMIssueCert..starts - NOT IMPLEMENTED YET!!!!");
        final String wfName = WorkflowNames.WORKFLOW_COMECIM_ComIssueCert.toString();
        final List<NodeReference> nodes = new LinkedList<>();
        nodes.add(NODE);

        /*
         * setup();
         */

        final Map<String, Object> workflowVars = new HashMap<String, Object>();
        workflowVars.put(WorkflowParameterKeys.TRUSTED_CATEGORY.toString(), TrustedCertCategory.CORBA_PEERS.toString());
        final Set<WorkflowInstance> workflows = wfh.startWorkflowInstances(nodes, wfName, workflowVars);
        Thread.sleep(15000);

        assertWorkflowsInstantiated(workflows, wfName);

        tearDown();

        logger.warn("testStartWorkflowCOMIssueCert.....ends");

        logger.info("testWfEngineStartWorkflowCOMIssueCert..........................................ends");
    }

    @Override
    public void testWfEngineStartWorkflowCPPRemoveTrustOAM() throws Exception {

        logger.info("testWfEngineStartWorkflowCPPRemoveTrustOAM..........................................starts");

        logger.warn("testStartWorkflowCPPRemoveTrustOAM..starts!!!!");
        final String wfName = WorkflowNames.WORKFLOW_CPPRemoveTrustOAM.toString();
        final List<NodeReference> nodes = new LinkedList<>();
        nodes.add(NODE);

        /*
         * setup();
         */

        final Map<String, Object> workflowVars = new HashMap<String, Object>();
        workflowVars.put(WorkflowParameterKeys.TRUSTED_CATEGORY.toString(), TrustedCertCategory.CORBA_PEERS.toString());
        workflowVars.put(WorkflowParameterKeys.CERTIFICATE_AUTHORITY_ID.toString(), "Authority");
        workflowVars.put(WorkflowParameterKeys.CERTIFICATE_ID.toString(), "123456789");
        final Set<WorkflowInstance> workflows = wfh.startWorkflowInstances(nodes, wfName, workflowVars);
        Thread.sleep(15000);

        assertWorkflowsInstantiated(workflows, wfName);

        tearDown();

        logger.warn("testStartWorkflowCPPRemoveTrustOAM.....ends");

        logger.info("testWfEngineStartWorkflowCPPRemoveTrustOAM..........................................ends");
    }

    @Override
    public void testWfEngineStartWorkflowCPPRemoveTrustNewIPSEC() throws Exception {

        logger.info("testWfEngineStartWorkflowCPPRemoveTrustNewIPSEC..........................................starts");

        logger.warn("testStartWorkflowCPPRemoveTrustNewIPSEC..starts!!!!");
        final String wfName = WorkflowNames.WORKFLOW_CPPRemoveTrustIPSEC.toString();
        final List<NodeReference> nodes = new LinkedList<>();
        nodes.add(NODE);

        /*
         * setup();
         */

        final Map<String, Object> workflowVars = new HashMap<String, Object>();
        workflowVars.put(WorkflowParameterKeys.CERTIFICATE_AUTHORITY_ID.toString(), "Authority");
        workflowVars.put(WorkflowParameterKeys.CERTIFICATE_ID.toString(), "123456789");
        final Set<WorkflowInstance> workflows = wfh.startWorkflowInstances(nodes, wfName, workflowVars);
        Thread.sleep(15000);

        assertWorkflowsInstantiated(workflows, wfName);

        tearDown();

        logger.warn("testStartWorkflowCPPRemoveTrustNewIPSEC.....ends");

        logger.info("testWfEngineStartWorkflowCPPRemoveTrustNewIPSEC..........................................ends");
    }

    @Override
    public void testWfEngineStartWorkflowCOMRemoveTrust() throws Exception {

        logger.info("testWfEngineStartWorkflowCOMRemoveTrust..........................................starts");

        logger.warn("testStartWorkflowCOMRemoveTrust..starts!!!!");
        final String wfName = WorkflowNames.WORKFLOW_COMECIMRemoveTrust.toString();
        final List<NodeReference> nodes = new LinkedList<>();
        nodes.add(NODE);

        /*
         * setup();
         */

        final Map<String, Object> workflowVars = new HashMap<String, Object>();
        workflowVars.put(WorkflowParameterKeys.TRUSTED_CATEGORY.toString(), TrustedCertCategory.CORBA_PEERS.toString());
        workflowVars.put(WorkflowParameterKeys.CERTIFICATE_AUTHORITY_ID.toString(), "Authority");
        workflowVars.put(WorkflowParameterKeys.CERTIFICATE_ID.toString(), "123456789");
        final Set<WorkflowInstance> workflows = wfh.startWorkflowInstances(nodes, wfName, workflowVars);
        Thread.sleep(15000);

        assertWorkflowsInstantiated(workflows, wfName);

        tearDown();

        logger.warn("testStartWorkflowCOMRemoveTrust.....ends");

        logger.info("testWfEngineStartWorkflowCOMRemoveTrust..........................................ends");
    }

    private void setup() throws Exception {
        data.deleteAllNodes();
        data.createNode(NODE_123);
    }

    private void tearDown() throws Exception {
        data.deleteAllNodes();
    }

    private HttpResponse restWorkflowsDebug(final String debugURL) throws Exception {
        final String url = String.format("%s/%s", RestHelper.getRestHttpUrl(RestHelper.NODE_SECURITY_WORKFLOW_PATH), debugURL);

        final HttpGet httpget = new HttpGet(new URL(url).toExternalForm());
        final HttpClient httpclient = HttpClientBuilder.create().build();
        final HttpResponse response = httpclient.execute(httpget);
        return response;
    }

    private HttpResponse restStartWorkflows(final String wfName, final String nodes) throws Exception {
        final String url = String.format("%s/%s/%s", RestHelper.getRestHttpUrl(RestHelper.NODE_SECURITY_WORKFLOW_PATH), wfName, nodes);
        final HttpGet httpget = new HttpGet(new URL(url).toExternalForm());
        final HttpClient httpclient = HttpClientBuilder.create().build();
        final HttpResponse response = httpclient.execute(httpget);
        return response;
    }

    private HttpResponse restStartWorkflowsWithVariables(final String wfName, final String nodes, final String wfVariables) throws Exception {
        final String url = String.format("%s/%s/%s?wfVariables=%s", RestHelper.getRestHttpUrl(RestHelper.NODE_SECURITY_WORKFLOW_PATH), wfName, nodes,
                wfVariables);
        logger.info("Invoking restStartWorkflowsWithVariables [{}]", url);
        final HttpGet httpget = new HttpGet(new URL(url).toExternalForm());
        final HttpClient httpclient = HttpClientBuilder.create().build();
        final HttpResponse response = httpclient.execute(httpget);
        return response;
    }

    /**
     * Makes sure all CA certificates are stored in SMRS for the given node name (neType is hard-coded as ERBS).
     * 
     * @param nodeName
     *            the given node name.
     * 
     * @throws Exception
     *             in any unexpected conditions occur.
     */
    private void assertCertsPublished(final String nodeName) throws Exception {
        logger.info("assertCertsPublished for node name {}", nodeName);
        final SmrsAccount smrsaccount = smrsService.getNodeSpecificAccount(nscsPkiManager.getSmrsAccountTypeForNscs(), "ERBS", nodeName);
        final String homeDirectory = smrsaccount.getHomeDirectory();
        final Map<String, List<X509Certificate>> cas = nscsPkiManager.getCAsTrusts();
        for (final List<X509Certificate> certs : cas.values()) {
            for (final X509Certificate cert : certs) {
                final CertSpec certSpec = new CertSpec(cert);
                final String fileName = certSpec.getFileName();
                final String fileFullPath = homeDirectory + File.separator + fileName;
                final File file = new File(fileFullPath);
                assertTrue("File does not exist: " + fileFullPath, file.isFile());
            }
        }
    }

    /**
     * Makes sure no CA certificates are stored in SMRS for the given node name (neType is hard-coded as ERBS).
     * 
     * @param nodeName
     *            the given node name.
     * 
     * @throws Exception
     *             in any unexpected conditions occur.
     */
    private void assertCertsNotPublished(final String nodeName) throws Exception {
        logger.info("assertCertsNotPublished for node name {}", nodeName);
        final SmrsAccount smrsaccount = smrsService.getNodeSpecificAccount(nscsPkiManager.getSmrsAccountTypeForNscs(), "ERBS", nodeName);
        final String homeDirectory = smrsaccount.getHomeDirectory();
        final Map<String, List<X509Certificate>> cas = nscsPkiManager.getCAsTrusts();
        for (final List<X509Certificate> certs : cas.values()) {
            for (final X509Certificate cert : certs) {
                final CertSpec certSpec = new CertSpec(cert);
                final String fileName = certSpec.getFileName();
                final String fileFullPath = homeDirectory + File.separator + fileName;
                final File file = new File(fileFullPath);
                assertFalse("File exists: " + fileFullPath, file.exists());
            }
        }
    }

    /**
     * Deletes SMRS account for the given node name (neType is hard-coded as ERBS).
     * 
     * @param nodeName
     *            the given node name.
     * 
     * @throws Exception
     *             in any unexpected conditions occur.
     */
    private void cleanCertsPublished(final String nodeName) throws Exception {
        logger.info("cleanCertsPublished for node name {}", nodeName);
        final SmrsAccount smrsaccount = smrsService.getNodeSpecificAccount(nscsPkiManager.getSmrsAccountTypeForNscs(), "ERBS", nodeName);
        final String homeDirectory = smrsaccount.getHomeDirectory();
        final Map<String, List<X509Certificate>> cas = nscsPkiManager.getCAsTrusts();
        for (final List<X509Certificate> certs : cas.values()) {
            for (final X509Certificate cert : certs) {
                final CertSpec certSpec = new CertSpec(cert);
                final String fileName = certSpec.getFileName();
                final String fileFullPath = homeDirectory + File.separator + fileName;
                final File file = new File(fileFullPath);
                assertTrue("File not removed: " + fileFullPath, file.delete());
            }
        }
        assertTrue("SMRS account not deleted", smrsService.deleteSmrsAccount(smrsaccount));
    }

    private void executeRestSSHKeyGeneration(final boolean isCreate) throws Exception {
        String wfVariables = "";
        wfVariables += WorkflowParameterKeys.KEY_SSHKEYS_GENERATION_ISCREATE.toString() + "=" + String.valueOf(isCreate);
        wfVariables += "," + WorkflowParameterKeys.KEY_SSHKEYS_GENERATION_ALGORITHM.toString() + "=" + AlgorithmKeys.RSA_1024.toString();

        final HttpResponse response = restStartWorkflowsWithVariables(WorkflowNames.WORKFLOW_SSHKeyGeneration.toString(), "NODE_123_FDN",
                wfVariables);
        Thread.sleep(WF_LONG_TIMEOUT);
        assertEquals("Status code should be 200 OK", 200, response.getStatusLine().getStatusCode());
        tearDown();
    }

    private void assertWorkflowsInstantiated(final Set<WorkflowInstance> workflows, final String workflowId) {

        for (final WorkflowInstance wf : workflows) {
            logger.info("assertWorkflowsInstantiated getBusinessKey {} getWorkflowDefinitionId {} getId {}", wf.getBusinessKey(),
                    wf.getWorkflowDefinitionId(), wf.getId());
            assertNotNull("WorkflowInstance should not be null", wf);
            assertNotNull("WorkflowInstance definitionId should not be null", wf.getWorkflowDefinitionId());
            assertNotNull("WorkflowInstance id should not be null", wf.getId());
            assertNotNull("WorkflowInstance businessKey should not be null", wf.getBusinessKey());
            assertTrue(String.format("WorkflowInstance should contain the same workflow id [%s] [%s]", wf.getWorkflowDefinitionId(), workflowId),
                    wf.getWorkflowDefinitionId().contains(workflowId));
        }
    }
}
