package com.ericsson.nms.security.nscs.cpp.level.processor;

/**
 *
 */
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.scheduler.WorkflowSchedulerInterface;
import com.ericsson.nms.security.nscs.cpp.level.SecLevelRequest;
import com.ericsson.nms.security.nscs.cpp.level.SecLevelRequestType;
import com.ericsson.nms.security.nscs.cpp.model.SmrsAccountInfo;
import com.ericsson.nms.security.nscs.cpp.model.TrustStoreInfo;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.enums.JobGlobalStatusEnum;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

/**
 * Tests the SecLevelProcessor implementation that starts a security level de-activation for a list of nodes
 *
 * @see ActivateStatusCommandHandlerImpl
 * @author eabdsin
 */
@RunWith(MockitoJUnitRunner.class)
public class DeactivateStatusCommandHandlerImplTest {

    private static final String NODE_FDN = "MeContext=ERBS001";
    private static final String NODE_PREFIX = "TEST_ERBS_";
    private static final String FDN_PREFIX = "MeContext=";

    Random rand = new Random();

    @Spy
    private final Logger log = LoggerFactory.getLogger(DeactivateStatusCommandHandlerImpl.class);

    @Mock
    CppSecurityService cppService;

    @Mock
    SystemRecorder systemRecorder;

    @Mock
    WorkflowHandler wfHandler;

    @Mock
    SecLevelRequest cmd;

    @InjectMocks
    DeactivateStatusCommandHandlerImpl deactivateStatusCommandHandlerImpl;

    @Mock
    private WorkflowSchedulerInterface workflowScheduler;

    @Mock
    NscsLogger nscsLogger;

    JobStatusRecord jobStatusRecord;

    WfResult result;

    @Before
    public void setup() {
        doReturn(NODE_PREFIX + String.valueOf(rand.nextInt((100 - 1) + 1) + 100)).when(cmd).getNodeName();
        doReturn(FDN_PREFIX + NODE_PREFIX + String.valueOf(rand.nextInt((100 - 1) + 1) + 100)).when(cmd).getNodeFDN();

        result = new WfResult();
        result.setJobId(UUID.randomUUID());
        result.setWfWakeId(UUID.randomUUID());

        jobStatusRecord = new JobStatusRecord();
        final UUID jobId = UUID.randomUUID();
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.PENDING);
        jobStatusRecord.setUserId("user");
        jobStatusRecord.setJobId(jobId);
    }

    /**
     * Test of procDeactivateStatusCommandHandlerImplvateStatusCommandHandlerImpl.
     *
     * Testing instantiating of the activate level 2 security workflow when <code>CPPSecurityLevel.LEVEL_2</code> is requested
     */
    @Test
    public void testProcessCommandDeactivateLevel1() {
        log.debug("testProcessCommandDeactivateLevel1");
        doReturn(SecurityLevel.LEVEL_1).when(cmd).getRequiredSecurityLevel();
        doReturn(SecurityLevel.LEVEL_2).when(cmd).getCurrentSecurityLevel();
        doReturn(SecLevelRequestType.DEACTIVATE_SECURITY_LEVEL).when(cmd).getSecLevelRequestType();
        deactivateStatusCommandHandlerImpl.processCommand(cmd);
        verify(wfHandler).startWorkflowInstance(Mockito.any(NodeReference.class),
                eq(DeactivateStatusCommandHandlerImpl.DEACTIVATE_LEVEL_2_WORKFLOW_ID));
    }

    /**
     * Test of procDeactivateStatusCommandHandlerImplvateStatusCommandHandlerImpl.
     *
     * Testing instantiating of the activate level 2 security workflow when <code>CPPSecurityLevel.LEVEL_2</code> is requested
     */
    @Test
    public void testProcessCommandDeactivateLevel1WithJobStatus() {
        log.debug("testProcessCommandDeactivateLevel1");
        doReturn(SecurityLevel.LEVEL_1).when(cmd).getRequiredSecurityLevel();
        doReturn(SecurityLevel.LEVEL_2).when(cmd).getCurrentSecurityLevel();
        doReturn(SecLevelRequestType.DEACTIVATE_SECURITY_LEVEL).when(cmd).getSecLevelRequestType();
        deactivateStatusCommandHandlerImpl.processCommand(cmd, jobStatusRecord, 1);
        verify(wfHandler).getScheduledWorkflowInstanceResult(Mockito.any(NodeReference.class), Mockito.anyString(), Mockito.anyMap(),
                Mockito.any(JobStatusRecord.class), Mockito.anyInt());
    }

    /**
     * Test of procDeactivateStatusCommandHandlerImplvateStatusCommandHandlerImpl.
     *
     * Testing instantiating of the activate level 2 security workflow when <code>CPPSecurityLevel.LEVEL_2</code> is requested
     */
    @Test
    public void testProcessCommandDeactivateLevel1WithJobStatusWithResultValue() {
        log.debug("testProcessCommandDeactivateLevel1");
        doReturn(SecurityLevel.LEVEL_1).when(cmd).getRequiredSecurityLevel();
        doReturn(SecurityLevel.LEVEL_2).when(cmd).getCurrentSecurityLevel();
        doReturn(SecLevelRequestType.DEACTIVATE_SECURITY_LEVEL).when(cmd).getSecLevelRequestType();
        doReturn(result).when(wfHandler).getScheduledWorkflowInstanceResult(Mockito.any(NodeReference.class), Mockito.anyString(), Mockito.anyMap(),
                Mockito.any(JobStatusRecord.class), Mockito.anyInt());
        deactivateStatusCommandHandlerImpl.processCommand(cmd, jobStatusRecord, 1);
    }

    @Test
    public void testProcessCommand() throws Exception {

        when(cppService.getTrustStoreForNode(eq(TrustedCertCategory.CORBA_PEERS), Mockito.any(NodeRef.class), Mockito.anyBoolean()))
                .thenReturn(getTrustStoreInfo());
        final SecLevelRequest cmd = new SecLevelRequest();
        cmd.setNodeFDN(NODE_FDN);
        cmd.setCurrentSecurityLevel(SecurityLevel.LEVEL_2);
        cmd.setRequiredSecurityLevel(SecurityLevel.LEVEL_1);
        deactivateStatusCommandHandlerImpl.processCommand(cmd, jobStatusRecord, 1);
        verify(wfHandler).getScheduledWorkflowInstanceResult(Mockito.any(NodeReference.class), Mockito.anyString(), Mockito.anyMap(),
                Mockito.any(JobStatusRecord.class), Mockito.anyInt());

    }

    public TrustStoreInfo getTrustStoreInfo() {
        final List<SmrsAccountInfo> accounts = new ArrayList<>();
        final Set<CertSpec> certSpecs = new HashSet<>();
        final TrustStoreInfo trustStoreInfo = new TrustStoreInfo(TrustedCertCategory.CORBA_PEERS, certSpecs, accounts, DigestAlgorithm.SHA1);
        return trustStoreInfo;
    }
}
