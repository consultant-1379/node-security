package com.ericsson.nms.security.nscs.logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

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

import com.ericsson.nms.security.nscs.api.command.NscsCliCommand;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.oss.itpf.sdk.recording.CommandPhase;
import com.ericsson.oss.services.dto.WfResult;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkFlowNodeTask;
import com.ericsson.oss.services.security.nscs.command.CommandHandlerStatsFormatter;
import com.ericsson.oss.services.security.nscs.command.EventDataCommandIdentifier;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

@RunWith(MockitoJUnitRunner.class)
public class NscsLoggerTest {

    private static final String TASK_SHORT_DESCRIPTION = "Do something";
    private static final String PREVIOUS_TASK_SHORT_DESCRIPTION = "Read something";
    private static final String startedTask = "[" + TASK_SHORT_DESCRIPTION + " ...]";
    private static final String additionalInfo = "Successfully performed action [doSomething] : params [{nullParam:{NULL},uriPassword:{*****},uri:{http://192.168.0.155:8093/service/todo/something}}] : fdn [ManagedElement=LTE01dg2ERBS00001,SystemFunctions=1,SecM=1,CertM=1]";
    private static final String ongoingTask = "[" + TASK_SHORT_DESCRIPTION + " ... ongoing]";
    private static final String ongoingResult = "...doSomething ......status....";
    private static final String ongoingResultExpected = "doSomething status.";
    private static final String ongoingResultTask = "[" + TASK_SHORT_DESCRIPTION + " ... " + ongoingResultExpected + "]";
    private static final String otherOngoingResult = "...doSomething ......progress...";
    private static final String otherOngoingResultExpected = "doSomething progress";
    private static final String otherOngoingResultTask = "[" + TASK_SHORT_DESCRIPTION + " ... " + otherOngoingResultExpected + "]";
    private static final String successResult = "...done...... ok....";
    private static final String successResultExpected = "done ok.";
    private static final String finishedSuccessResultTask = "[" + TASK_SHORT_DESCRIPTION + ": " + successResultExpected + "]";
    private static final String otherSuccessResult = "...done...... ok....";
    private static final String finishedFailedTask = "[" + TASK_SHORT_DESCRIPTION + " failed]";
    private static final String errorResult = "...doSomething ......timeout....";
    private static final String errorResultExpected = "doSomething timeout.";
    private static final String finishedFailedResultTask = "[" + TASK_SHORT_DESCRIPTION + " failed: " + errorResultExpected + "]";
    private static final String otherErrorResult = "...doSomething ......failure....";
    private static final String prevMessage = "[" + PREVIOUS_TASK_SHORT_DESCRIPTION + " failed: reason of fail]";

    @InjectMocks
    private NscsLogger beanUnderTest;

    @Spy
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Mock
    private WorkFlowNodeTask task;

    @Mock
    private NodeReference nodeRef;

    @Mock
    private NscsSystemRecorder systemRecorder;

    @Mock
    private NscsJobCacheHandler cacheHandler;

    @Mock
    private NscsContextService nscsContextService;

    @Mock
    private NscsCompactAuditLogger nscsCompactAuditLogger;

    @Mock
    private NscsRemoteEjbLogger nscsRemoteEjbLogger;

    @Before
    public void setup() {
        when(task.getNode()).thenReturn(nodeRef);
        when(task.stringify()).thenReturn(new StringBuilder("testTask string"));
        final UUID uuid = UUID.randomUUID();
        final String wfWakeId = uuid.toString();
        when(task.getWfWakeId()).thenReturn(wfWakeId);
        when(task.getShortDescription()).thenReturn(TASK_SHORT_DESCRIPTION);
    }

    @Test
    public void commandStartedTest() {
        final String commandString = "credentials create --secureusername netsim --secureuserpassword netsim -n LTE01dg2ERBS00003";
        final NscsCliCommand command = new NscsCliCommand(commandString);
        beanUnderTest.commandStarted("secadm ", command);
        Mockito.verify(systemRecorder, Mockito.times(1)).recordCommand(Mockito.anyString(), Mockito.any(CommandPhase.class), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void commandStartedWithNullCommandTest() {
        beanUnderTest.commandStarted("secadm ", null);
        Mockito.verify(systemRecorder, Mockito.times(1)).recordCommand(Mockito.anyString(), Mockito.any(CommandPhase.class), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void commandFinishedWithSuccessTest() {
        final String commandString = "credentials create --secureusername netsim --secureuserpassword netsim -n LTE01dg2ERBS00003";
        final NscsCliCommand command = new NscsCliCommand(commandString);
        beanUnderTest.commandFinishedWithSuccess("secadm ", command, "response message");
        Mockito.verify(systemRecorder, Mockito.times(1)).recordCommand(Mockito.anyString(), Mockito.any(CommandPhase.class), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void commandFinishedWithErrorTest() {
        final String commandString = "credentials create --secureusername netsim --secureuserpassword netsim -n LTE01dg2ERBS00003";
        final NscsCliCommand command = new NscsCliCommand(commandString);
        beanUnderTest.commandFinishedWithError("secadm ", command, "response message");
        Mockito.verify(systemRecorder, Mockito.times(1)).recordCommand(Mockito.anyString(), Mockito.any(CommandPhase.class), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void commandFinishedWithErrorWithNullCommandTest() {
        beanUnderTest.commandFinishedWithError("secadm ", null, "response message");
        Mockito.verify(systemRecorder, Mockito.times(1)).recordCommand(Mockito.anyString(), Mockito.any(CommandPhase.class), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void commandStartedAndFinishedWithSuccessTest() {
        final String commandString = "credentials create --secureusername netsim --secureuserpassword netsim -n LTE01dg2ERBS00003";
        final NscsCliCommand command = new NscsCliCommand(commandString);
        beanUnderTest.commandStarted("secadm ", command);
        beanUnderTest.commandFinishedWithSuccess("secadm ", command, "response message");
        Mockito.verify(systemRecorder, Mockito.times(2)).recordCommand(Mockito.anyString(), Mockito.any(CommandPhase.class), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void commandStartedAndFinishedWithErrorTest() {
        final String commandString = " credentials create --secureusername netsim --secureuserpassword netsim -n LTE01dg2ERBS00003";
        final NscsCliCommand command = new NscsCliCommand(commandString);
        beanUnderTest.commandStarted("secadm ", command);
        beanUnderTest.commandFinishedWithError("secadm ", command, "response message");
        Mockito.verify(systemRecorder, Mockito.times(2)).recordCommand(Mockito.anyString(), Mockito.any(CommandPhase.class), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());
    }

    @Test(expected = NullPointerException.class)
    public void commandStartedAndFinishedWithSuccessWithNullCommandTest() {
        beanUnderTest.commandStarted("secadm ", null);
        beanUnderTest.commandFinishedWithSuccess("secadm ", null, "response message");
        Mockito.verify(systemRecorder, Mockito.times(2)).recordCommand(Mockito.anyString(), Mockito.any(CommandPhase.class), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void commandStartedAndFinishedWithErrorWithNullCommandTest() {
        beanUnderTest.commandStarted("secadm ", null);
        beanUnderTest.commandFinishedWithError("secadm ", null, "response message");
        Mockito.verify(systemRecorder, Mockito.times(2)).recordCommand(Mockito.anyString(), Mockito.any(CommandPhase.class), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void restFinishedWithSuccessTest() {
        beanUnderTest.restFinishedWithSuccess();
        Mockito.verify(nscsCompactAuditLogger, Mockito.times(1)).recordRestFinishedWithSuccessCompactAudit();
    }

    @Test
    public void restFinishedWithErrorTest() {
        beanUnderTest.restFinishedWithError();
        Mockito.verify(nscsCompactAuditLogger, Mockito.times(1)).recordRestFinishedWithErrorCompactAudit();
    }

    @Test
    public void commandHandlerStartedTest() {
        final NscsPropertyCommand nscsPropertyCommand = new NscsPropertyCommand();
        nscsPropertyCommand.setCommandType(NscsCommandType.LDAP_PROXY_DELETE);
        beanUnderTest.commandHandlerStarted(nscsPropertyCommand);
        Mockito.verify(systemRecorder, Mockito.times(1)).recordCommand(Mockito.anyString(), Mockito.any(CommandPhase.class), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void commandHandlerOngoingTest() {
        final NscsPropertyCommand nscsPropertyCommand = new NscsPropertyCommand();
        nscsPropertyCommand.setCommandType(NscsCommandType.LDAP_PROXY_DELETE);
        beanUnderTest.commandHandlerOngoing(nscsPropertyCommand, additionalInfo);
        Mockito.verify(systemRecorder, Mockito.times(1)).recordCommand(Mockito.anyString(), Mockito.any(CommandPhase.class), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void commandHandlerFinishedWithSuccessTest() {
        final String commandString = "credentials create --secureusername netsim --secureuserpassword netsim -n LTE01dg2ERBS00003";
        final NscsCliCommand command = new NscsCliCommand(commandString);
        beanUnderTest.commandStarted("secadm", command);
        beanUnderTest.commandFinishedWithSuccess("secadm", command, "response message");
        Mockito.verify(systemRecorder, Mockito.times(2)).recordCommand(Mockito.anyString(), Mockito.any(CommandPhase.class), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void commandHandlerFinishedWithErrorTest() {
        final NscsPropertyCommand nscsPropertyCommand = new NscsPropertyCommand();
        nscsPropertyCommand.setCommandType(NscsCommandType.LDAP_PROXY_DELETE);
        beanUnderTest.commandHandlerFinishedWithError(nscsPropertyCommand, "response message");
        Mockito.verify(systemRecorder, Mockito.times(1)).recordCommand(Mockito.anyString(), Mockito.any(CommandPhase.class), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void commandHandlerStartedAndFinishedWithSuccessTest() {
        final NscsPropertyCommand nscsPropertyCommand = new NscsPropertyCommand();
        nscsPropertyCommand.setCommandType(NscsCommandType.LDAP_PROXY_DELETE);
        beanUnderTest.commandHandlerStarted(nscsPropertyCommand);
        beanUnderTest.commandHandlerFinishedWithSuccess(nscsPropertyCommand, "response message");
        Mockito.verify(systemRecorder, Mockito.times(2)).recordCommand(Mockito.anyString(), Mockito.any(CommandPhase.class), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void commandHandlerStartedAndFinishedWithSuccessWithEventTest() {
        final NscsPropertyCommand nscsPropertyCommand = new NscsPropertyCommand();
        nscsPropertyCommand.setCommandType(NscsCommandType.LDAP_PROXY_DELETE);
        beanUnderTest.commandHandlerStarted(nscsPropertyCommand);
        beanUnderTest.updateCommandHandlerStatsFormatter(EventDataCommandIdentifier.LDAP_PROXY_DELETE, null, null, null);
        beanUnderTest.commandHandlerFinishedWithSuccess(nscsPropertyCommand, "response message");
        Mockito.verify(systemRecorder, Mockito.times(1)).recordCommandHandlerCompletedEvent(Mockito.any(CommandHandlerStatsFormatter.class));
        Mockito.verify(systemRecorder, Mockito.times(2)).recordCommand(Mockito.anyString(), Mockito.any(CommandPhase.class), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void commandHandlerStartedAndFinishedWithErrorTest() {
        final NscsPropertyCommand nscsPropertyCommand = new NscsPropertyCommand();
        nscsPropertyCommand.setCommandType(NscsCommandType.LDAP_PROXY_DELETE);
        beanUnderTest.commandHandlerStarted(nscsPropertyCommand);
        beanUnderTest.commandHandlerFinishedWithError(nscsPropertyCommand, "response message");
        Mockito.verify(systemRecorder, Mockito.times(2)).recordCommand(Mockito.anyString(), Mockito.any(CommandPhase.class), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void commandHandlerStartedAndFinishedWithErrorWithEventTest() {
        final NscsPropertyCommand nscsPropertyCommand = new NscsPropertyCommand();
        nscsPropertyCommand.setCommandType(NscsCommandType.LDAP_PROXY_DELETE);
        beanUnderTest.commandHandlerStarted(nscsPropertyCommand);
        beanUnderTest.updateCommandHandlerStatsFormatter(EventDataCommandIdentifier.LDAP_PROXY_DELETE, null, null, null);
        beanUnderTest.commandHandlerFinishedWithError(nscsPropertyCommand, "response message");
        Mockito.verify(systemRecorder, Mockito.times(0)).recordCommandHandlerCompletedEvent(Mockito.any(CommandHandlerStatsFormatter.class));
        Mockito.verify(systemRecorder, Mockito.times(2)).recordCommand(Mockito.anyString(), Mockito.any(CommandPhase.class), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void workFlowStarted() {
        beanUnderTest.workFlowStarted("wf name", "wf instance id", "node key", "additional info");
        Mockito.verify(systemRecorder, Mockito.times(1)).recordCommand(Mockito.anyString(), Mockito.any(CommandPhase.class), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void workFlowFinishedWithSuccess() {
        beanUnderTest.workFlowFinishedWithSuccess("wf name", "wf instance id", "node key", "additional info");
        Mockito.verify(systemRecorder, Mockito.times(1)).recordCommand(Mockito.anyString(), Mockito.any(CommandPhase.class), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void workFlowFinishedWithError() {
        beanUnderTest.workFlowFinishedWithError("wf name", "wf instance id", "node key", "additional info");
        Mockito.verify(systemRecorder, Mockito.times(1)).recordCommand(Mockito.anyString(), Mockito.any(CommandPhase.class), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void workFlowTaskHandlerStartedWithNullMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = null;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerStarted(task);
        final String newMessage = wfResult.getMessage();
        assertEquals("[" + TASK_SHORT_DESCRIPTION + " ... ]", newMessage);
    }

    @Test
    public void workFlowTaskHandlerStartedWithEmptyMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = "";
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerStarted(task);
        final String newMessage = wfResult.getMessage();
        assertEquals("[" + TASK_SHORT_DESCRIPTION + " ... ]", newMessage);
    }

    @Test
    public void workFlowTaskHandlerStartedWithMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = prevMessage;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerStarted(task);
        final String newMessage = wfResult.getMessage();
        assertEquals(prevMessage + "[" + TASK_SHORT_DESCRIPTION + " ... ]", newMessage);
    }

    @Test
    public void workFlowTaskHandlerOngoingWithNull() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = null;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertNull(newMessage);
    }

    @Test
    public void workFlowTaskHandlerOngoingResultWithNull() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = null;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo, ongoingResult);
        final String newMessage = wfResult.getMessage();
        assertNull(newMessage);
    }

    @Test
    public void workFlowTaskHandlerOngoingWithEmptyMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = "";
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertTrue(newMessage.isEmpty());
    }

    @Test
    public void workFlowTaskHandlerOngoingResultWithEmptyMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = "";
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo, ongoingResult);
        final String newMessage = wfResult.getMessage();
        assertTrue(newMessage.isEmpty());
    }

    @Test
    public void workFlowTaskHandlerOngoingWithPrevious() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = prevMessage;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertEquals(prevMessage, newMessage);
    }

    @Test
    public void workFlowTaskHandlerOngoingResultWithPrevious() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = prevMessage;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo, ongoingResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(prevMessage, newMessage);
    }

    @Test
    public void workFlowTaskHandlerOngoingWithStartedMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = startedTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertEquals(ongoingTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerOngoingResultWithStartedMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = startedTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo, ongoingResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(ongoingResultTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerOngoingResultWithOngoing() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = ongoingTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo, ongoingResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(ongoingResultTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerOngoingWithPreviousAndStartedMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = prevMessage + startedTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertEquals(prevMessage + ongoingTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerOngoingResultWithPreviousAndStartedMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = prevMessage + startedTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo, ongoingResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(prevMessage + ongoingResultTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerOngoingWithOngoingMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = ongoingTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertEquals(oldMessage, newMessage);
    }

    @Test
    public void workFlowTaskHandlerOngoingResultWithOngoingMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = ongoingTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo, ongoingResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(ongoingResultTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerOngoingResultWithOngoingResultMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = ongoingResultTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo, otherOngoingResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(otherOngoingResultTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerOngoingWithPreviousAndOngoingMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = prevMessage + ongoingTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertEquals(oldMessage, newMessage);
    }

    @Test
    public void workFlowTaskHandlerOngoingResultWithPreviousAndOngoingMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = prevMessage + ongoingTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo, ongoingResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(prevMessage + ongoingResultTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerOngoingResultWithPreviousAndOngoingResultMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = prevMessage + ongoingResultTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo, otherOngoingResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(prevMessage + otherOngoingResultTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerOngoingWithFinishedSuccessMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = finishedSuccessResultTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertEquals(oldMessage, newMessage);
    }

    @Test
    public void workFlowTaskHandlerOngoingResultWithFinishedSuccessMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = finishedSuccessResultTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo, ongoingResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(oldMessage, newMessage);
    }

    @Test
    public void workFlowTaskHandlerOngoingWithPreviousAndFinishedSuccessMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = prevMessage + finishedSuccessResultTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertEquals(oldMessage, newMessage);
    }

    @Test
    public void workFlowTaskHandlerOngoingResultWithPreviousAndFinishedSuccessMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = prevMessage + finishedSuccessResultTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo, ongoingResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(oldMessage, newMessage);
    }

    @Test
    public void workFlowTaskHandlerOngoingWithFinishedFailMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = finishedFailedTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertEquals(oldMessage, newMessage);
    }

    @Test
    public void workFlowTaskHandlerOngoingResultWithFinishedFailMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = finishedFailedTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo, ongoingResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(oldMessage, newMessage);
    }

    @Test
    public void workFlowTaskHandlerOngoingWithPreviousAndFinishedFailMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = prevMessage + finishedFailedTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertEquals(oldMessage, newMessage);
    }

    @Test
    public void workFlowTaskHandlerOngoingResultWithPreviousAndFinishedFailMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = prevMessage + finishedFailedTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo, ongoingResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(oldMessage, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithErrorWithNull() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = null;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithError(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertNull(newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithErrorResultWithNull() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = null;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithError(task, additionalInfo, errorResult);
        final String newMessage = wfResult.getMessage();
        assertNull(newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithErrorWithEmptyMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = "";
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithError(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertTrue(newMessage.isEmpty());
    }

    @Test
    public void workFlowTaskHandlerFinishedWithErrorResultWithEmptyMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = "";
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithError(task, additionalInfo, errorResult);
        final String newMessage = wfResult.getMessage();
        assertTrue(newMessage.isEmpty());
    }

    @Test
    public void workFlowTaskHandlerFinishedWithErrorWithPrevious() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = prevMessage;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithError(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertEquals(oldMessage, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithErrorResultWithPrevious() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = prevMessage;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithError(task, additionalInfo, errorResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(oldMessage, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithErrorWithStartedMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = startedTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithError(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertEquals(finishedFailedTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithErrorResultWithStartedMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = startedTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithError(task, additionalInfo, errorResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(finishedFailedResultTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithErrorWithPreviousAndStartedMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = prevMessage + startedTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithError(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertEquals(prevMessage + finishedFailedTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithErrorResultWithPreviousAndStartedMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = prevMessage + startedTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithError(task, additionalInfo, errorResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(prevMessage + finishedFailedResultTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithErrorWithOngoingMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = ongoingTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithError(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertEquals(finishedFailedTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithErrorResultWithOngoingMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = ongoingTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithError(task, additionalInfo, errorResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(finishedFailedResultTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithErrorWithOngoingResultMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = ongoingResultTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithError(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertEquals(finishedFailedTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithErrorResultWithOngoingResultMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = ongoingResultTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithError(task, additionalInfo, errorResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(finishedFailedResultTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithErrorWithFinishedSuccessResultMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = finishedSuccessResultTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithError(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertEquals(finishedSuccessResultTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithErrorResultWithFinishedSuccessResultMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = finishedSuccessResultTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithError(task, additionalInfo, errorResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(finishedSuccessResultTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithErrorWithFinishedWithErrorMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = finishedFailedTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithError(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertEquals(finishedFailedTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithErrorResultWithFinishedWithErrorMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = finishedFailedTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithError(task, additionalInfo, otherErrorResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(finishedFailedTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithErrorWithFinishedWithErrorResultMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = finishedFailedResultTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithError(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertEquals(finishedFailedResultTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithErrorResultWithFinishedWithErrorResultMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = finishedFailedResultTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithError(task, additionalInfo, otherErrorResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(finishedFailedResultTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithSuccessWithNull() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = null;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertNull(newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithSuccessResultWithNull() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = null;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo, successResult);
        final String newMessage = wfResult.getMessage();
        assertNull(newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithSuccessWithEmptyMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = "";
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertTrue(newMessage.isEmpty());
    }

    @Test
    public void workFlowTaskHandlerFinishedWithSuccessResultWithEmptyMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = "";
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo, successResult);
        final String newMessage = wfResult.getMessage();
        assertTrue(newMessage.isEmpty());
    }

    @Test
    public void workFlowTaskHandlerFinishedWithSuccessWithPrevious() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = prevMessage;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertEquals(oldMessage, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithSuccessResultWithPrevious() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = prevMessage;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo, successResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(oldMessage, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithSuccessWithStartedMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = startedTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertTrue(newMessage.isEmpty());
    }

    @Test
    public void workFlowTaskHandlerFinishedWithSuccessResultWithStartedMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = startedTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo, successResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(finishedSuccessResultTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithSuccessWithPreviousAndStartedMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = prevMessage + startedTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertEquals(prevMessage, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithSuccessResultWithPreviousAndStartedMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = prevMessage + startedTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo, successResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(prevMessage + finishedSuccessResultTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithSuccessWithOngoingMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = ongoingTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertTrue(newMessage.isEmpty());
    }

    @Test
    public void workFlowTaskHandlerFinishedWithSuccessResultWithOngoingMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = ongoingTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo, successResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(finishedSuccessResultTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithSuccessWithOngoingResultMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = ongoingResultTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertTrue(newMessage.isEmpty());
    }

    @Test
    public void workFlowTaskHandlerFinishedWithSuccessResultWithOngoingResultMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = ongoingResultTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo, successResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(finishedSuccessResultTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithSuccessWithFinishedSuccessResultMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = finishedSuccessResultTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertEquals(finishedSuccessResultTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithSuccessResultWithFinishedSuccessResultMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = finishedSuccessResultTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo, otherSuccessResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(finishedSuccessResultTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithSuccessWithFinishedWithErrorMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = finishedFailedTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertEquals(finishedFailedTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithSuccessResultWithFinishedWithErrorMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = finishedFailedTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo, successResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(finishedFailedTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithSuccessWithFinishedWithErrorResultMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = finishedFailedResultTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo);
        final String newMessage = wfResult.getMessage();
        assertEquals(finishedFailedResultTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerFinishedWithSuccessResultWithFinishedWithErrorResultMessage() {
        final WfResult wfResult = new WfResult();
        final String oldMessage = finishedFailedResultTask;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo, successResult);
        final String newMessage = wfResult.getMessage();
        assertEquals(finishedFailedResultTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerLifeCycleFinishedWithSuccessWithNullMessage() {
        final WfResult wfResult = new WfResult();
        String oldMessage = null;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerStarted(task);
        String newMessage = wfResult.getMessage();
        assertEquals("[" + TASK_SHORT_DESCRIPTION + " ... ]", newMessage);
        oldMessage = newMessage;
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo);
        newMessage = wfResult.getMessage();
        assertEquals(ongoingTask, newMessage);
        oldMessage = newMessage;
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo, ongoingResult);
        newMessage = wfResult.getMessage();
        assertEquals(ongoingResultTask, newMessage);
        oldMessage = newMessage;
        beanUnderTest.workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo);
        newMessage = wfResult.getMessage();
        assertTrue(newMessage.isEmpty());
    }

    @Test
    public void workFlowTaskHandlerLifeCycleFinishedWithSuccessResultWithNullMessage() {
        final WfResult wfResult = new WfResult();
        String oldMessage = null;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerStarted(task);
        String newMessage = wfResult.getMessage();
        assertEquals("[" + TASK_SHORT_DESCRIPTION + " ... ]", newMessage);
        oldMessage = newMessage;
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo);
        newMessage = wfResult.getMessage();
        assertEquals(ongoingTask, newMessage);
        oldMessage = newMessage;
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo, ongoingResult);
        newMessage = wfResult.getMessage();
        assertEquals(ongoingResultTask, newMessage);
        oldMessage = newMessage;
        beanUnderTest.workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo, successResult);
        newMessage = wfResult.getMessage();
        assertEquals(finishedSuccessResultTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerLifeCycleFinishedWithErrorWithNullMessage() {
        final WfResult wfResult = new WfResult();
        String oldMessage = null;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerStarted(task);
        String newMessage = wfResult.getMessage();
        assertEquals("[" + TASK_SHORT_DESCRIPTION + " ... ]", newMessage);
        oldMessage = newMessage;
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo);
        newMessage = wfResult.getMessage();
        assertEquals(ongoingTask, newMessage);
        oldMessage = newMessage;
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo, ongoingResult);
        newMessage = wfResult.getMessage();
        assertEquals(ongoingResultTask, newMessage);
        oldMessage = newMessage;
        beanUnderTest.workFlowTaskHandlerFinishedWithError(task, additionalInfo);
        newMessage = wfResult.getMessage();
        assertEquals(finishedFailedTask, newMessage);
    }

    @Test
    public void workFlowTaskHandlerLifeCycleFinishedWithErrorResultWithNullMessage() {
        final WfResult wfResult = new WfResult();
        String oldMessage = null;
        wfResult.setMessage(oldMessage);
        when(cacheHandler.getWfResult(any(UUID.class))).thenReturn(wfResult);
        beanUnderTest.workFlowTaskHandlerStarted(task);
        String newMessage = wfResult.getMessage();
        assertEquals("[" + TASK_SHORT_DESCRIPTION + " ... ]", newMessage);
        oldMessage = newMessage;
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo);
        newMessage = wfResult.getMessage();
        assertEquals(ongoingTask, newMessage);
        oldMessage = newMessage;
        beanUnderTest.workFlowTaskHandlerOngoing(task, additionalInfo, ongoingResult);
        newMessage = wfResult.getMessage();
        assertEquals(ongoingResultTask, newMessage);
        oldMessage = newMessage;
        beanUnderTest.workFlowTaskHandlerFinishedWithError(task, additionalInfo, errorResult);
        newMessage = wfResult.getMessage();
        assertEquals(finishedFailedResultTask, newMessage);
    }

    @Test
    public void renoteEjbStartedTest() {
        beanUnderTest.remoteEjbStarted();
        Mockito.verify(nscsRemoteEjbLogger, Mockito.times(1)).recordRemoteEjbStarted();
    }

    @Test
    public void renoteEjbFinishedWithSuccessTest() {
        beanUnderTest.remoteEjbFinishedWithSuccess();
        Mockito.verify(nscsRemoteEjbLogger, Mockito.times(1)).recordRemoteEjbFinishedWithSuccess();
    }

    @Test
    public void remoteEjbFinishedWithErrorTest() {
        beanUnderTest.remoteEjbFinishedWithError();
        Mockito.verify(nscsRemoteEjbLogger, Mockito.times(1)).recordRemoteEjbFinishedWithError();
    }

    @Test
    public void errorWithTaskAndThrowableAndErrorMessage() {
        final Exception e = new Exception("exception message");
        beanUnderTest.error(task, e, "error message");
        Mockito.verify(logger, Mockito.times(1)).error(any(String.class), any(Throwable.class));
    }

    @Test
    public void errorWithTaskAndThrowableAndNullErrorMessage() {
        final Exception e = new Exception("exception message");
        beanUnderTest.error(task, e, null);
        Mockito.verify(logger, Mockito.times(1)).error(any(String.class), any(Throwable.class));
    }

    @Test
    public void errorWithNullTaskAndThrowableAndErrorMessage() {
        final Exception e = new Exception("exception message");
        beanUnderTest.error(null, e, "error message");
        Mockito.verify(logger, Mockito.times(1)).error(any(String.class), any(Throwable.class));
    }
}
