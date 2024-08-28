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

package com.ericsson.nms.security.nscs.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.cert.issue.CertIssueWfParams;
import com.ericsson.nms.security.nscs.api.enums.RevocationReason;
import com.ericsson.nms.security.nscs.api.exception.HttpsActivateOrDeactivateWfException;
import com.ericsson.nms.security.nscs.api.exception.LdapConfigureWfException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.Nodes.Node;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.ldap.entities.NodeSpecificLdapConfiguration;
import com.ericsson.nms.security.nscs.trust.distribution.input.xml.Nodes;
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.enums.JobGlobalStatusEnum;
import com.ericsson.oss.services.enums.WfStatusEnum;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowNames;

@RunWith(MockitoJUnitRunner.class)
public class NscsCommandManagerProcessorImplTest {

    @Mock
    private final Logger logger = LoggerFactory.getLogger(NscsCommandManagerProcessorImplTest.class);

    @Mock
    private WorkflowHandler wfHandler;

    @Mock
    private NscsCapabilityModelService capabilityModel;

    @Mock
    private NscsCMReaderService reader;

    @Mock
    private Node node;

    @InjectMocks
    private NscsCommandManagerProcessorImpl nscsCommandManagerProcessor;

    private JobStatusRecord jobStatusRecord;

    private final String testFdnName = "Test_node_001";

    private final int workflowId = 1;

    private final Map<String, Object> workflowVars = new HashMap<String, Object>();

    private final WfResult wfResult = new WfResult();

    private NodeReference nodeRef;

    private CertIssueWfParams certIssueWfParams = new CertIssueWfParams();

    @Before
    public void setup() {
        jobStatusRecord = new JobStatusRecord();
        final UUID jobId = UUID.randomUUID();
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.PENDING);
        jobStatusRecord.setUserId("user");
        jobStatusRecord.setJobId(jobId);

        nodeRef = new NodeRef(testFdnName);
        wfResult.setJobId(jobId);
        wfResult.setStatus(WfStatusEnum.SUCCESS);
        wfResult.setNodeName(testFdnName);

    }

    @Test
    public void testExecuteCertificateIssueSingleWf() {
        certIssueWfParams.setCertType("OAM");
        wfResultMock();
        Mockito.when(node.getNodeFdn()).thenReturn(testFdnName);
        Mockito.when(capabilityModel.getIssueOrReissueCertWf(Mockito.any(NodeReference.class),Mockito.anyString()))
                .thenReturn(WorkflowNames.WORKFLOW_COMECIM_ComIssueCert.toString());
        nscsCommandManagerProcessor.executeCertificateIssueSingleWf(null, node, certIssueWfParams, false,
                                    "", jobStatusRecord, 1);
    }

    @Test
    public void testExecuteCertificateReIssueSingleWf() {
        certIssueWfParams.setCertType("OAM");
        wfResultMock();
        Mockito.when(capabilityModel.getIssueOrReissueCertWf(Mockito.any(NodeReference.class),Mockito.anyString()))
                .thenReturn(WorkflowNames.WORKFLOW_COMECIM_ComIssueCert.toString());
        nscsCommandManagerProcessor.executeCertificateReIssueSingleWf(nodeRef, "", "OAM", jobStatusRecord, 1);
    }

    @Test
    public void testExecuteCertificateReIssueSingleWfWithEntity() {
        certIssueWfParams.setCertType("OAM");
        wfResultMock();
        EntityInfo entityInfo = new EntityInfo();
        entityInfo.setName(testFdnName + "-OAM");
        Entity entity = new Entity();
        entity.setEntityInfo(entityInfo);
        final Map<Entity, NodeReference> nodeMap = new HashMap();
        nodeMap.put(entity, nodeRef);
        nodeMap.entrySet().iterator().next();

        Mockito.when(capabilityModel.getIssueOrReissueCertWf(Mockito.any(NodeReference.class),Mockito.anyString()))
                .thenReturn(WorkflowNames.WORKFLOW_COMECIM_ComIssueCert.toString());
        nscsCommandManagerProcessor.executeCertificateReIssueSingleWf(nodeMap.entrySet().iterator().next(), "",
                            "OAM", jobStatusRecord, 1);
    }

    @Test
    public void testExecuteActivateHttpsWfs() {
        certIssueWfParams.setCertType("OAM");
        wfResultMock();
        nscsCommandManagerProcessor.executeActivateHttpsWfs(nodeRef, certIssueWfParams,true, RevocationReason.UNSPECIFIED.toString(), jobStatusRecord, workflowId);

    }

    @Test
    public void testExecuteDeactivateHttpsWfs() {
        wfResultMock();
        nscsCommandManagerProcessor.executeDeactivateHttpsWfs(nodeRef, jobStatusRecord, workflowId);

    }

    @Test(expected = HttpsActivateOrDeactivateWfException.class)
    public void testExecuteActivateHttpsWfsFailed() {
        certIssueWfParams.setCertType("OAM");
        throwExceptionMock();
        nscsCommandManagerProcessor.executeActivateHttpsWfs(nodeRef,certIssueWfParams,true, RevocationReason.UNSPECIFIED.toString(), jobStatusRecord, workflowId);

    }

    @Test(expected = HttpsActivateOrDeactivateWfException.class)
    public void testExecuteDeactivateHttpsWfsFailed() {
        throwExceptionMock();
        nscsCommandManagerProcessor.executeDeactivateHttpsWfs(nodeRef, jobStatusRecord, workflowId);
    }

    @Test
    public void testExecuteActivateFtpesWfs() {
       certIssueWfParams.setCertType("OAM");
        wfResultMock();
        nscsCommandManagerProcessor.executeActivateFtpesWfs(nodeRef, certIssueWfParams,true, RevocationReason.UNSPECIFIED.toString(), jobStatusRecord, workflowId);
    }

    @Test
    public void testExecuteDeactivateFtpesWfs() {
        wfResultMock();
        nscsCommandManagerProcessor.executeDeactivateFtpesWfs(nodeRef, jobStatusRecord, workflowId);
    }

    private void wfResultMock() {
        Mockito.when(
                wfHandler.getScheduledWorkflowInstanceResult(Mockito.any(NodeRef.class), Mockito.anyString(), Mockito.anyMap(),
                        Mockito.any(JobStatusRecord.class), Mockito.anyInt())).thenReturn(wfResult);
    }

    private void throwExceptionMock() {
        Mockito.when(
                wfHandler.getScheduledWorkflowInstanceResult(Mockito.any(NodeRef.class), Mockito.anyString(), Mockito.anyMap(),
                        Mockito.any(JobStatusRecord.class), Mockito.anyInt())).thenThrow(new RuntimeException("App exception"));
    }


    @Test
    public void testexecuteConfigureLdapWfs() {
        NodeSpecificLdapConfiguration nodeSpecificLdapConfiguration = new NodeSpecificLdapConfiguration();
        nodeSpecificLdapConfiguration.setNodeFdn("LTE08DG2ERBS00001");
        nodeSpecificLdapConfiguration.setUserLabel("Test");
        nodeSpecificLdapConfiguration.setTlsMode("LDAPS");
        nodeSpecificLdapConfiguration.setUseTls(true);
        wfResultMock();
        Mockito.when(capabilityModel.getLdapConfigureWorkflow(Mockito.any(NormalizableNodeReference.class)))
                .thenReturn(WorkflowNames.WORKFLOW_COMECIM_CONFIGURE_LDAP.getWorkflowName());

        nscsCommandManagerProcessor.executeConfigureLdapWfs(nodeSpecificLdapConfiguration, jobStatusRecord, workflowId);
    }

    @Test(expected = LdapConfigureWfException.class)
    public void testexecuteConfigureLdapWfsFailed() {
        NodeSpecificLdapConfiguration nodeSpecificLdapConfiguration = new NodeSpecificLdapConfiguration();
        nodeSpecificLdapConfiguration.setNodeFdn("LTE08DG2ERBS00001");
        nodeSpecificLdapConfiguration.setUserLabel("Test");
        nodeSpecificLdapConfiguration.setTlsMode("LDAPS");
        nodeSpecificLdapConfiguration.setUseTls(true);
        throwExceptionMock();
        Mockito.when(capabilityModel.getLdapConfigureWorkflow(Mockito.any(NormalizableNodeReference.class)))
                .thenReturn(WorkflowNames.WORKFLOW_COMECIM_CONFIGURE_LDAP.getWorkflowName());
        nscsCommandManagerProcessor.executeConfigureLdapWfs(nodeSpecificLdapConfiguration, jobStatusRecord, workflowId);
    }

    @Test
    public void testexecuteRenewLdapWf() {
        NodeSpecificLdapConfiguration nodeSpecificLdapConfiguration = new NodeSpecificLdapConfiguration();
        nodeSpecificLdapConfiguration.setNodeFdn("LTE08DG2ERBS00001");
        nodeSpecificLdapConfiguration.setUserLabel("Test");
        nodeSpecificLdapConfiguration.setTlsMode("LDAPS");
        nodeSpecificLdapConfiguration.setUseTls(true);
        wfResultMock();

        nscsCommandManagerProcessor.executeLdapWf(nodeSpecificLdapConfiguration, jobStatusRecord, workflowId, true);
    }

    @Test
    public void testExecuteTrustDistributeSingleWf() {
        final Nodes.NodeTrustInfo nodeInfo = new Nodes.NodeTrustInfo(nodeRef.getFdn());
        Mockito.when(capabilityModel.getTrustDistributeWf(nodeRef, "OAM"))
                .thenReturn(WorkflowNames.WORKFLOW_CBPOI_INSTALL_TRUST_CERTS.getWorkflowName());
        nscsCommandManagerProcessor.executeTrustDistributeSingleWf(nodeInfo, "OAM", "ENM_OAM_CA", jobStatusRecord, 1, null);
    }

    @Test
    public void testExecuteTrustDistributeSingleWf_InvalidWorkflow() {
        final Nodes.NodeTrustInfo nodeInfo = new Nodes.NodeTrustInfo(nodeRef.getFdn());
        Mockito.when(capabilityModel.getTrustDistributeWf(nodeRef, "OAM"))
                .thenReturn("InvalidWorkflowName");
        nscsCommandManagerProcessor.executeTrustDistributeSingleWf(nodeInfo, "OAM", "ENM_OAM_CA", jobStatusRecord, 1, null);
    }

    @Test
    public void testExecuteTrustRemoveSingleWf() {
        Mockito.when(capabilityModel.getTrustRemoveWf(nodeRef, "OAM")).thenReturn(WorkflowNames.WORKFLOW_CBPOI_REMOVE_TRUST.getWorkflowName());
        nscsCommandManagerProcessor.executeTrustRemoveSingleWf(nodeRef, "CN=Root", "1223435", "OAM", jobStatusRecord, 1);
    }
}