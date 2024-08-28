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
package com.ericsson.nms.security.nscs.ssh.handler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.ejb.ssh.eventhandling.SSHHandlerEventObserver;
import com.ericsson.nms.security.nscs.ejb.ssh.eventhandling.SSHHandlerEventObserver.SSHCommandsOutputs;
import com.ericsson.nms.security.nscs.ssh.SSHKeyGenCommand;
import com.ericsson.nms.security.nscs.workflow.task.cpp.WFMessageConstants;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.mediation.sec.model.SSHCommandFailure;
import com.ericsson.oss.mediation.sec.model.SSHCommandResult;
import com.ericsson.oss.mediation.sec.model.SSHCommandSuccess;
import com.ericsson.oss.services.nscs.workflow.impl.WorkflowHandlerImpl;

import static org.mockito.Mockito.times;

/**
 *
 * @author edobpet
 */
@RunWith(MockitoJUnitRunner.class)
public class SSHHandlerEventObserverTest {

    @Spy
    private final Logger logger = LoggerFactory.getLogger(SSHHandlerEventObserver.class);

    @Mock
    private WorkflowHandlerImpl workflowHandler;

    @Mock
    private NscsCMReaderService readerService;

    @Mock
    private SystemRecorder systemRecorder;

    @InjectMocks
    private SSHHandlerEventObserver eventObserver;

    private final String secmodeFS = "secmode -f s";
    private final String secmodeFU = "secmode -f u";
    private final String secmodeL1 = "secmode -l 1";
    private final String secmodeL2 = "secmode -l 2";
    private final String secmodeWS = "secmode -w s";
    private final String secmodeWU = "secmode -w u";
    private final String secmodeS = "secmode -s";

    private final String SECMODE_OK = "SECMODE_OK";
    private final String commandExecutedSuccessfullyMsg = "Command executed successfully";
    private final String SECMODE_ALREADY_SET = "SECMODE_ALREADY_SET";
    private final String alreadySetMsg = "The requested security mode was already set.";

    private final String cppCommandFileTransferClientModeSuccess = "CPPCommandFileTransferClientModeSuccess";
    private final String cppCommandFileTransferClientModeFail = "CPPCommandFileTransferClientModeFail";
    private final String cppCommandOperationalSecurityLevelSuccess = "CPPCommandOperationalSecurityLevelSuccess";
    private final String cppCommandHttpsSuccess = "CPPCommandHttpsSuccess";
    private final String cppCommandHttpsFail = "CPPCommandHttpsFail";
    private final String CppCommandHttps = "CppCommandHttps";
    private final String CppCommandHttp = "CppCommandHttp";
    private final String cppCommandOperationalSecurityLevelFail = "CPPCommandOperationalSecurityLevelFail";

    private final String secmode_s_https_active_output = "[responseMessage: secmode -s\n" +
            "Security configuration settings:\n" +
            "WebServer                    secure, https server is ON.\n" +
            "---End settings--------------------------------------------------------------------\n" +
            "$ ]";

    private final String secmode_s_https_deactive_output = "[responseMessage: secmode -s\n" +
            "Security configuration settings:\n" +
            "WebServer                    unsecure, https server is OFF.\n" +
            "---End settings--------------------------------------------------------------------\n" +
            "$ ]";

    private final NodeReference TEST_NODE = new NodeRef("testFDN");

    public SSHHandlerEventObserverTest() {
    }

    /**
     * Test of commandResultHandler method, of class SSHHandlerEventObserver.
     *
     */
    @Test
    public void test_secmodeFS_1() {
        final SSHCommandResult sshCommandResult = new SSHCommandSuccess("job1",
                SECMODE_OK + "\n" + commandExecutedSuccessfullyMsg, 4l, 5l, TEST_NODE.getFdn());
        sshCommandResult.setCommand(secmodeFS);
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler).dispatchMessage(TEST_NODE, cppCommandFileTransferClientModeSuccess);
    }

    @Test
    public void test_secmodeFS_2() {
        final SSHCommandResult sshCommandResult = new SSHCommandSuccess("job2",
                SECMODE_ALREADY_SET + "\n" + alreadySetMsg, 4l, 5l, TEST_NODE.getFdn());
        sshCommandResult.setCommand(secmodeFS);
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler).dispatchMessage(TEST_NODE, cppCommandFileTransferClientModeSuccess);
    }

    @Test
    public void test_secmodeFU_3() {
        final SSHCommandResult sshCommandResult = new SSHCommandSuccess("job3",
                SECMODE_OK + "\n" + commandExecutedSuccessfullyMsg, 4l, 5l, TEST_NODE.getFdn());
        sshCommandResult.setCommand(secmodeFU);
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler).dispatchMessage(TEST_NODE, cppCommandFileTransferClientModeSuccess);
    }

    @Test
    public void test_secmodeFU_4() {
        final SSHCommandResult sshCommandResult = new SSHCommandSuccess("job4",
                SECMODE_ALREADY_SET + "\n" + alreadySetMsg, 4l, 5l, TEST_NODE.getFdn());
        sshCommandResult.setCommand(secmodeFU);
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler).dispatchMessage(TEST_NODE, cppCommandFileTransferClientModeSuccess);
    }

    @Test
    public void test_secmodeL1_5() {
        final SSHCommandResult sshCommandResult = new SSHCommandSuccess("job5",
                SECMODE_ALREADY_SET + "\n" + alreadySetMsg, 4l, 5l, TEST_NODE.getFdn());
        sshCommandResult.setCommand(secmodeL1);
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler).dispatchMessage(TEST_NODE, cppCommandOperationalSecurityLevelSuccess);
    }

    @Test
    public void test_secmodeL1_6() {
        final SSHCommandResult sshCommandResult = new SSHCommandSuccess("job6",
                SECMODE_OK + "\n" + commandExecutedSuccessfullyMsg, 4l, 5l, TEST_NODE.getFdn());
        sshCommandResult.setCommand(secmodeL1);
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler).dispatchMessage(TEST_NODE, cppCommandOperationalSecurityLevelSuccess);
    }

    @Test
    public void test_secmodeL2_7() {
        final SSHCommandResult sshCommandResult = new SSHCommandSuccess("job7",
                SECMODE_ALREADY_SET + "\n" + alreadySetMsg, 4l, 5l, TEST_NODE.getFdn());
        sshCommandResult.setCommand(secmodeL2);
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler).dispatchMessage(TEST_NODE, cppCommandOperationalSecurityLevelSuccess);
    }

    @Test
    public void test_secmodeL2_8() {
        final SSHCommandResult sshCommandResult = new SSHCommandSuccess("job8",
                SECMODE_OK + "\n" + commandExecutedSuccessfullyMsg, 4l, 5l, TEST_NODE.getFdn());
        sshCommandResult.setCommand(secmodeL2);
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler).dispatchMessage(TEST_NODE, cppCommandOperationalSecurityLevelSuccess);
    }

    @Test
    public void test_secmodeL1_9() {
        final SSHCommandResult sshCommandResult = new SSHCommandSuccess("job9", "erroneous message", 4l, 5l,
                TEST_NODE.getFdn());
        sshCommandResult.setCommand(secmodeL1);
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(logger).warn("SSH command returned unexpected output [{}]", sshCommandResult.getCommandOutput());
    }

    @Test
    public void test_secmodeWS_OK_NotDispatch() {
        final SSHCommandResult sshCommandResult = new SSHCommandSuccess("job3",
                SECMODE_OK + "\n" + commandExecutedSuccessfullyMsg, 4l, 5l, TEST_NODE.getFdn());
        sshCommandResult.setCommand(secmodeWS);
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler, times(0)).dispatchMessage(TEST_NODE, cppCommandHttpsSuccess);
    }

    @Test
    public void test_secmodeWS_ALREADY_SET() {
        final SSHCommandResult sshCommandResult = new SSHCommandSuccess("job4",
                SECMODE_ALREADY_SET + "\n" + alreadySetMsg, 4l, 5l, TEST_NODE.getFdn());
        sshCommandResult.setCommand(secmodeWS);
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler, times(0)).dispatchMessage(TEST_NODE, cppCommandHttpsSuccess);
    }

    @Test
    public void test_secmodeWU_OK() {
        final SSHCommandResult sshCommandResult = new SSHCommandSuccess("job3",
                SECMODE_OK + "\n" + commandExecutedSuccessfullyMsg, 4l, 5l, TEST_NODE.getFdn());
        sshCommandResult.setCommand(secmodeWU);
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler, times(0)).dispatchMessage(TEST_NODE, cppCommandHttpsSuccess);
    }

    @Test
    public void test_secmodeWU_ALREADY_SET() {
        final SSHCommandResult sshCommandResult = new SSHCommandSuccess("job4",
                SECMODE_ALREADY_SET + "\n" + alreadySetMsg, 4l, 5l, TEST_NODE.getFdn());
        sshCommandResult.setCommand(secmodeWU);
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler, times(0)).dispatchMessage(TEST_NODE, cppCommandHttpsSuccess);
    }

    @Test
    public void test_secmodeS_HTTPS_ACTIVE() {
        final SSHCommandResult sshCommandResult = new SSHCommandSuccess("job3",
                secmode_s_https_active_output, 4l, 5l, TEST_NODE.getFdn());
        sshCommandResult.setCommand(secmodeS);
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler).dispatchMessage(TEST_NODE, CppCommandHttps);
    }

    @Test
    public void test_secmodeS_HTTPS_DEACTIVE() {
        final SSHCommandResult sshCommandResult = new SSHCommandSuccess("job4",
                secmode_s_https_deactive_output, 4l, 5l, TEST_NODE.getFdn());
        sshCommandResult.setCommand(secmodeS);
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler).dispatchMessage(TEST_NODE, CppCommandHttp);
    }

    @Test
    public void test_sshKey_create_success() {
        final SSHCommandResult sshCommandResult = new SSHCommandSuccess("job1",
                SSHCommandsOutputs.SSH_KEY_CREATE_OK.toString() + "\n" + commandExecutedSuccessfullyMsg, 4l, 5l,
                TEST_NODE.getFdn());
        sshCommandResult.setCommand(SSHKeyGenCommand.SSH_KEY_CREATE.toString());
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler).dispatchMessage(TEST_NODE,
                WFMessageConstants.SSH_KEY_GENERATION_COMMAND_SUCCESS);
    }

    @Test
    public void test_sshKey_update_success() {
        final SSHCommandResult sshCommandResult = new SSHCommandSuccess("job1",
                SSHCommandsOutputs.SSH_KEY_UPDATE_OK.toString() + "\n" + commandExecutedSuccessfullyMsg, 4l, 5l,
                TEST_NODE.getFdn());
        sshCommandResult.setCommand(SSHKeyGenCommand.SSH_KEY_UPDATE.toString());
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler).dispatchMessage(TEST_NODE,
                WFMessageConstants.SSH_KEY_GENERATION_COMMAND_SUCCESS);
    }

    //FAILUREs
    @Test
    public void test_secmodeFS_10() {
        final SSHCommandResult sshCommandResult = new SSHCommandFailure("job10", secmodeFS, "errorMessage", "errorType",
                TEST_NODE.getFdn());
        sshCommandResult.setCommand(secmodeFS);
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler).dispatchMessage(TEST_NODE, cppCommandFileTransferClientModeFail);
    }

    @Test
    public void test_secmodeFU_11() {
        final SSHCommandResult sshCommandResult = new SSHCommandFailure("job11", secmodeFU, "errorMessage", "errorType",
                TEST_NODE.getFdn());
        sshCommandResult.setCommand(secmodeFU);
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler).dispatchMessage(TEST_NODE, cppCommandFileTransferClientModeFail);
    }

    @Test
    public void test_secmodeL1_12() {
        final SSHCommandResult sshCommandResult = new SSHCommandFailure("job12", secmodeL1, "errorMessage", "errorType",
                TEST_NODE.getFdn());
        sshCommandResult.setCommand(secmodeL1);
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler).dispatchMessage(TEST_NODE, cppCommandOperationalSecurityLevelFail);
    }

    @Test
    public void test_secmodeL2_13() {
        final SSHCommandResult sshCommandResult = new SSHCommandFailure("job13", secmodeL2, "errorMessage", "errorType",
                TEST_NODE.getFdn());
        sshCommandResult.setCommand(secmodeL2);
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler).dispatchMessage(TEST_NODE, cppCommandOperationalSecurityLevelFail);
    }

    @Test
    public void test_secmodeWS_FAIL() {
        final SSHCommandResult sshCommandResult = new SSHCommandFailure("job13", secmodeWS, "errorMessage", "errorType",
                TEST_NODE.getFdn());
        sshCommandResult.setCommand(secmodeWS);
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler).dispatchMessage(TEST_NODE, cppCommandHttpsFail);
    }

    @Test
    public void test_secmodeWU_FAIL() {
        final SSHCommandResult sshCommandResult = new SSHCommandFailure("job13", secmodeWU, "errorMessage", "errorType",
                TEST_NODE.getFdn());
        sshCommandResult.setCommand(secmodeWU);
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler).dispatchMessage(TEST_NODE, cppCommandHttpsFail);
    }

    @Test
    public void test_invalidConstantsOnSuccess_14() {
        final SSHCommandResult sshCommandResult = new SSHCommandSuccess("job14", "invalid constant", 4l, 5l,
                TEST_NODE.getFdn());
        sshCommandResult.setCommand(secmodeFS);
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler).dispatchMessage(TEST_NODE, cppCommandFileTransferClientModeFail);
    }

    @Test
    public void test_invalidConstantsOnSuccess_15() {
        final SSHCommandResult sshCommandResult = new SSHCommandSuccess("job15", "invalid constant", 4l, 5l,
                TEST_NODE.getFdn());
        sshCommandResult.setCommand(secmodeFU);
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler).dispatchMessage(TEST_NODE, cppCommandFileTransferClientModeFail);
    }

    @Test
    public void test_invalidConstantsOnSuccess_16() {
        final SSHCommandResult sshCommandResult = new SSHCommandSuccess("job16", "invalid constant", 4l, 5l,
                TEST_NODE.getFdn());
        sshCommandResult.setCommand(secmodeL1);
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler).dispatchMessage(TEST_NODE, cppCommandOperationalSecurityLevelFail);
    }

    @Test
    public void test_invalidConstantsOnSuccess_17() {
        final SSHCommandResult sshCommandResult = new SSHCommandSuccess("job17", "invalid constant", 4l, 5l,
                TEST_NODE.getFdn());
        sshCommandResult.setCommand(secmodeL2);
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler).dispatchMessage(TEST_NODE, cppCommandOperationalSecurityLevelFail);
    }

    @Test
    public void test_sshKey_create_fail() {
        final SSHCommandResult sshCommandResult = new SSHCommandFailure("job1",
                SSHKeyGenCommand.SSH_KEY_CREATE.toString(), "errorMessage", "errorType", TEST_NODE.getFdn());
        sshCommandResult.setCommand(SSHKeyGenCommand.SSH_KEY_CREATE.toString());
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler).dispatchMessage(TEST_NODE, WFMessageConstants.SSH_KEY_GENERATION_COMMAND_FAIL);
    }

    @Test
    public void test_sshKey_update_fail() {
        final SSHCommandResult sshCommandResult = new SSHCommandFailure("job1",
                SSHKeyGenCommand.SSH_KEY_UPDATE.toString(), "errorMessage", "errorType", TEST_NODE.getFdn());
        sshCommandResult.setCommand(SSHKeyGenCommand.SSH_KEY_UPDATE.toString());
        eventObserver.commandResultHandler(sshCommandResult);
        Mockito.verify(workflowHandler).dispatchMessage(TEST_NODE, WFMessageConstants.SSH_KEY_GENERATION_COMMAND_FAIL);
    }

    @Test
    public void test_sshKey_command_fail() {
        final SSHCommandFailure sshCommandResult = new SSHCommandFailure("job1",
                SSHKeyGenCommand.SSH_KEY_UPDATE.toString(), "errorMessage", "errorType", TEST_NODE.getFdn());
        sshCommandResult.setCommand(SSHKeyGenCommand.SSH_KEY_UPDATE.toString());
        eventObserver.commandResultHandlerFailure(sshCommandResult);
        Mockito.verify(workflowHandler).dispatchMessage(TEST_NODE, WFMessageConstants.SSH_KEY_GENERATION_COMMAND_FAIL);
    }
}
