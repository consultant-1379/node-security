/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package com.ericsson.nms.security.nscs.cpp.level.processor;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
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
 * Class for testing ActivateSecurityLevelCommandHandlerImpl.
 *
 * Contains the following tests:
 *
 * <ul>
 * <li>testProcessCommandActivateLevel2</li>
 * <li>testProcessCommandActivateLevel1</li>
 * <li>testProcessCommandActivateLevel3MinLevel1</li>
 * <li>testProcessCommandActivateLevel2ThrowsException</li>
 * </ul>
 *
 * @author ealemca
 */
@RunWith(MockitoJUnitRunner.class)
public class ActivateSecurityLevelCommandHandlerImplTest {

    private static final String NODE_PREFIX = "TEST_ERBS_";
    private static final String FDN_PREFIX = "MeContext=";
    private static final String NODE_FDN = "nodeFDN";

    Random rand = new Random();

    @Spy
    private final Logger log = LoggerFactory.getLogger(ActivateSecurityLevelCommandHandlerImpl.class);

    @Mock
    CppSecurityService cppService;

    @Mock
    SystemRecorder systemRecorder;

    @Mock
    WorkflowHandler wfHandler;

    @Mock
    SecLevelRequest cmd;

    @InjectMocks
    ActivateSecurityLevelCommandHandlerImpl activateSecurityLevelCommandHandlerImpl;

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
     * Test of procActivateSecurityLevelCommandHandlerImplvateStatusCommandHandlerImpl.
     *
     * Testing instantiating of the activate level 2 security workflow when <code>CPPSecurityLevel.LEVEL_2</code> is requested
     */
    @Test
    public void testProcessCommandActivateLevel2() {
        log.debug("testProcessCommandActivateLevel2");
        doReturn(SecurityLevel.LEVEL_2).when(cmd).getRequiredSecurityLevel();
        doReturn(SecurityLevel.LEVEL_1).when(cmd).getCurrentSecurityLevel();
        doReturn(SecLevelRequestType.ACTIVATE_SECURITY_LEVEL).when(cmd).getSecLevelRequestType();
        activateSecurityLevelCommandHandlerImpl.processCommand(cmd);
        verify(wfHandler).startWorkflowInstance(Mockito.any(NodeReference.class),
                eq(ActivateSecurityLevelCommandHandlerImpl.ACTIVATE_LEVEL_2_WORKFLOW_ID));
    }

    /**
     * Test of procActivateSecurityLevelCommandHandlerImplvateStatusCommandHandlerImpl.
     *
     * Testing instantiating of the activate level 2 security workflow when <code>CPPSecurityLevel.LEVEL_2</code> is requested
     */
    @Test
    public void testProcessCommandActivateLevel2WithJobStatus() {
        log.debug("testProcessCommandActivateLevel2");
        doReturn(SecurityLevel.LEVEL_2).when(cmd).getRequiredSecurityLevel();
        doReturn(SecurityLevel.LEVEL_1).when(cmd).getCurrentSecurityLevel();
        doReturn(SecLevelRequestType.ACTIVATE_SECURITY_LEVEL).when(cmd).getSecLevelRequestType();
        activateSecurityLevelCommandHandlerImpl.processCommand(cmd, jobStatusRecord, 1);
        verify(wfHandler).getScheduledWorkflowInstanceResult(Mockito.any(NodeReference.class), Mockito.anyString(), Mockito.anyMap(),
                Mockito.any(JobStatusRecord.class), Mockito.anyInt());
    }

    /**
     * Test of procActivateSecurityLevelCommandHandlerImplvateStatusCommandHandlerImpl.
     *
     * Testing instantiating of the activate level 2 security workflow when <code>CPPSecurityLevel.LEVEL_2</code> is requested
     */
    @Test
    public void testProcessCommandActivateLevel2WithJobStatus_InvalidRequiredLevel() {
        log.debug("testProcessCommandActivateLevel2");
        doReturn(SecurityLevel.LEVEL_1).when(cmd).getRequiredSecurityLevel();
        doReturn(SecurityLevel.LEVEL_1).when(cmd).getCurrentSecurityLevel();
        doReturn(SecLevelRequestType.ACTIVATE_SECURITY_LEVEL).when(cmd).getSecLevelRequestType();
        activateSecurityLevelCommandHandlerImpl.processCommand(cmd, jobStatusRecord, 1);
        verifyZeroInteractions(wfHandler);
    }

    /**
     * Test of procActivateSecurityLevelCommandHandlerImplvateStatusCommandHandlerImpl.
     *
     * Testing instantiating of the activate level 2 security workflow when <code>CPPSecurityLevel.LEVEL_2</code> is requested
     */
    @Test
    public void testProcessCommandActivateLevel2WithJobStatusWithResultValue() {
        log.debug("testProcessCommandActivateLevel2");
        doReturn(SecurityLevel.LEVEL_2).when(cmd).getRequiredSecurityLevel();
        doReturn(SecurityLevel.LEVEL_1).when(cmd).getCurrentSecurityLevel();
        doReturn(SecLevelRequestType.ACTIVATE_SECURITY_LEVEL).when(cmd).getSecLevelRequestType();
        doReturn(result).when(wfHandler).getScheduledWorkflowInstanceResult(Mockito.any(NodeReference.class), Mockito.anyString(), Mockito.anyMap(),
                Mockito.any(JobStatusRecord.class), Mockito.anyInt());
        final WfResult result2 = activateSecurityLevelCommandHandlerImpl.processCommand(cmd, jobStatusRecord, 1);
    }

    @Test
    public void testProcessCommand() throws Exception {

        when(cppService.getTrustStoreForNode(eq(TrustedCertCategory.CORBA_PEERS), Mockito.any(NodeRef.class), Mockito.anyBoolean()))
                .thenReturn(getTrustStoreInfo());
        final SecLevelRequest cmd = new SecLevelRequest();
        cmd.setNodeFDN(NODE_FDN);
        cmd.setCurrentSecurityLevel(SecurityLevel.LEVEL_1);
        cmd.setRequiredSecurityLevel(SecurityLevel.LEVEL_2);
        activateSecurityLevelCommandHandlerImpl.processCommand(cmd, jobStatusRecord, 1);
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
