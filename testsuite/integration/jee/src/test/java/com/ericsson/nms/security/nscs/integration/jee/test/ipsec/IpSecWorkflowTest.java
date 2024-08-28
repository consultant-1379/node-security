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
package com.ericsson.nms.security.nscs.integration.jee.test.ipsec;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.integration.jee.test.setup.PIBHelper;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.IntegrationTestBase;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityDataSetup;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowNames;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;
import com.ericsson.oss.services.wfs.api.instance.WorkflowInstance;

@RunWith(Arquillian.class)
@Stateless
public class IpSecWorkflowTest extends IntegrationTestBase {

    private static final String NODE_123 = "node123";
    private static final String NODE_123_FDN = "MeContext=" + NODE_123;
    private static final NodeReference NODE = new NodeRef(NODE_123_FDN);

    @Inject
    private NodeSecurityDataSetup data;

    @Inject
    private WorkflowHandler wfh;

    @Inject
    private Logger logger;

    @BeforeClass
    @OperateOnDeployment("nscs-test-ear.ear")
    public static void beforeTests() throws Exception {
        PIBHelper.changeSmrsFolderConfig();
    }

    @Test
    @Ignore("CmReaderService must be uplifted to EAP 7")
    @InSequence(100)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testIpSecWorkflowInstantied() throws Exception {
        logger.info("testIpSecWorkflowInstantied..........................................starts");

        data.deleteAllNodes();
        data.createNode(NODE_123);

        final List<NodeReference> nodes = new LinkedList<>();
        nodes.add(NODE);
        final Set<WorkflowInstance> workflows = wfh.startWorkflowInstances(nodes, "CPPActivateIpSec");
        Thread.sleep(15000);

        assertWorkflowsInstantiated(workflows, "CPPActivateIpSec");

        logger.info("testIpSecWorkflowInstantied..........................................end");

    }

    @Test
    //@Ignore // Passed
    @InSequence(102)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testStartWorkflowCPPInstallCertificatesIpSec() throws Exception {
        logger.info("testStartWorkflowCPPInstallCertificatesIpSec..........................................starts");
        data.deleteAllNodes();
        data.createNode(NODE_123);
        final List<NodeReference> nodes = new LinkedList<>();
        nodes.add(NODE);
        final Map<String, Object> workflowVars = new HashMap<String, Object>();

        workflowVars.put("SUB_ALT_NAME", "002_SubAltName");
        workflowVars.put("TRUST_CERTS", "/tmp/cer2.pem");
        workflowVars.put("REMOVE_TRUST", "true");

        final Set<WorkflowInstance> workflows = wfh.startWorkflowInstances(nodes, "CPPInstallCertificatesIpSec", workflowVars);
        Thread.sleep(50000);
        assertNotNull("WorkflowInstance definitionId should not be null", workflows.iterator().next().getWorkflowDefinitionId());
        logger.info("testStartWorkflowCPPInstallCertificatesIpSec..........................................end");
    }

    @Test
    //@Ignore // Passed
    @InSequence(103)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testStartWorkflowCPPIssueReissueCertificate_IpSec_Issue() throws Exception {
        logger.info("testStartWorkflowCPPIssueReissueCertificate_IpSec_Issue..........................................starts");

        data.deleteAllNodes();
        data.createNode(NODE_123);
        final String workflowName = WorkflowNames.WORKFLOW_CPPIssueReissueCertificate_IpSec.toString();
        final List<NodeReference> nodes = new LinkedList<>();
        nodes.add(NODE);
        final Map<String, Object> workflowVars = new HashMap<String, Object>();
        workflowVars.put(WorkflowParameterKeys.CERTIFICATE_ISREISSUE.toString(), "false");
        workflowVars.put(WorkflowParameterKeys.IPSEC_SUB_ALT_NAME.toString(), "002_SubAltName");
        workflowVars.put(WorkflowParameterKeys.IPSEC_SUB_ALT_NAME_TYPE.toString(), "Type");
        workflowVars.put(WorkflowParameterKeys.ENTITY_PROFILE_NAME.toString(), "Entity");
        workflowVars.put(WorkflowParameterKeys.ENROLLMENT_MODE.toString(), EnrollmentMode.SCEP.toString());
        workflowVars.put(WorkflowParameterKeys.KEY_SSHKEYS_GENERATION_ALGORITHM.toString(), AlgorithmKeys.RSA_2048.toString());

        final Set<WorkflowInstance> workflows = wfh.startWorkflowInstances(nodes, workflowName, workflowVars);
        Thread.sleep(50000);
        assertWorkflowsInstantiated(workflows, workflowName);

        logger.info("testStartWorkflowCPPIssueReissueCertificate_IpSec_Issue..........................................end");

    }

    @Test
    //@Ignore // Passed
    @InSequence(104)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testStartWorkflowCPPIssueReissueCertificate_IpSec_Reissue() throws Exception {
        logger.info("testStartWorkflowCPPIssueReissueCertificate_IpSec_Reissue..........................................starts");

        data.deleteAllNodes();
        data.createNode(NODE_123);
        final String workflowName = WorkflowNames.WORKFLOW_CPPIssueReissueCertificate_IpSec.toString();
        final List<NodeReference> nodes = new LinkedList<>();
        nodes.add(NODE);
        final Map<String, Object> workflowVars = new HashMap<String, Object>();
        workflowVars.put(WorkflowParameterKeys.CERTIFICATE_ISREISSUE.toString(), "true");
        workflowVars.put(WorkflowParameterKeys.IPSEC_SUB_ALT_NAME.toString(), "002_SubAltName");
        workflowVars.put(WorkflowParameterKeys.IPSEC_SUB_ALT_NAME_TYPE.toString(), "Type");
        workflowVars.put(WorkflowParameterKeys.CERTIFICATE_ID.toString(), "123");
        workflowVars.put(WorkflowParameterKeys.CERTIFICATE_AUTHORITY_ID.toString(), "ABC");
        workflowVars.put(WorkflowParameterKeys.ENTITY_PROFILE_NAME.toString(), "Entity");
        workflowVars.put(WorkflowParameterKeys.ENROLLMENT_MODE.toString(), EnrollmentMode.SCEP.toString());
        workflowVars.put(WorkflowParameterKeys.KEY_SSHKEYS_GENERATION_ALGORITHM.toString(), AlgorithmKeys.RSA_1024.toString());
        workflowVars.put(WorkflowParameterKeys.REVOCATION_REASON.toString(), "keyCompromise");

        final Set<WorkflowInstance> workflows = wfh.startWorkflowInstances(nodes, workflowName, workflowVars);
        Thread.sleep(50000);
        assertWorkflowsInstantiated(workflows, workflowName);
        logger.info("testStartWorkflowCPPIssueReissueCertificate_IpSec_Reissue..........................................end");

    }

    @Test
    //@Ignore // Passed
    @InSequence(105)
    @OperateOnDeployment("nscs-test-ear.ear")
    public void testStartWorkflowCPPIssueTrustCertIpSec() throws Exception {
        logger.info("testStartWorkflowCPPIssueTrustCertIpSec..........................................starts");

        data.deleteAllNodes();
        data.createNode(NODE_123);
        final String workflowName = WorkflowNames.WORKFLOW_CPPIssueTrustCertIpSec.toString();
        final List<NodeReference> nodes = new LinkedList<>();
        nodes.add(NODE);
        final Map<String, Object> workflowVars = new HashMap<String, Object>();
        workflowVars.put(WorkflowParameterKeys.TRUST_CERTS.toString(), TrustedCertCategory.IPSEC.toString());

        final Set<WorkflowInstance> workflows = wfh.startWorkflowInstances(nodes, workflowName, workflowVars);
        Thread.sleep(50000);
        assertWorkflowsInstantiated(workflows, workflowName);

        logger.info("testStartWorkflowCPPIssueTrustCertIpSec..........................................end");

    }

    private void assertWorkflowsInstantiated(final Set<WorkflowInstance> workflows, final String workflowId) {
        for (final WorkflowInstance wf : workflows) {
            logger.info("ipsec assertWorkflowsInstantiated getBusinessKey {} getWorkflowDefinitionId {} getId {}", wf.getBusinessKey(),
                    wf.getWorkflowDefinitionId(), wf.getId());
            assertNotNull("WorkflowInstance should not be null", wf);
            assertNotNull("WorkflowInstance definitionId should not be null", wf.getWorkflowDefinitionId());
            assertNotNull("WorkflowInstance id should not be null", wf.getId());
            assertNotNull("WorkflowInstance businessKey should not be null", wf.getBusinessKey());
            assertTrue("WorkflowInstance should contain the same workflow id", wf.getWorkflowDefinitionId().contains(workflowId));
        }
    }

}
