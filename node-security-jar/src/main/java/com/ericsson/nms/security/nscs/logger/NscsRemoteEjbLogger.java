package com.ericsson.nms.security.nscs.logger;

import com.ericsson.oss.itpf.sdk.recording.CommandPhase;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class NscsRemoteEjbLogger {

    private static final String NSCS_SOURCE = "Node Security Service";
    private static final String NSCS_RESOURCE = "Node";
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private NscsSystemRecorder nscsSystemRecorder;

    @Inject
    private NscsContextService nscsContextService;

    public void recordRemoteEjbStarted() {
        final String commandName = String.format("EJB : %s.%s", nscsContextService.getClassNameContextValue(), nscsContextService.getMethodNameContextValue());
        final CommandPhase commandPhase = CommandPhase.STARTED;
        final String source = NSCS_SOURCE;
        final String resource = NSCS_RESOURCE;
        final String additionalInfo = "";
        logger.debug("Calling systemRecorder with: commandName: {}, commandPhase: {}, source: {}, resource: {}, additionalInfo: {}", commandName,commandPhase,source,resource,additionalInfo);
        nscsSystemRecorder.recordCommand(commandName,commandPhase,source,resource,additionalInfo);
    }

    public void recordRemoteEjbFinishedWithSuccess() {
        final String commandName = String.format("EJB : %s.%s", nscsContextService.getClassNameContextValue(), nscsContextService.getMethodNameContextValue());
        final CommandPhase commandPhase = CommandPhase.FINISHED_WITH_SUCCESS;
        final String source = NSCS_SOURCE;
        final String resource = NSCS_RESOURCE;
        final String additionalInfo = String.format("Node [%s]", nscsContextService.getInputNodeNameContextValue());
        logger.debug("Calling systemRecorder with: commandName: {}, commandPhase: {}, source: {}, resource: {}, additionalInfo: {}", commandName,commandPhase,source,resource,additionalInfo);
        nscsSystemRecorder.recordCommand(commandName,commandPhase,source,resource,additionalInfo);
    }

    public void recordRemoteEjbFinishedWithError() {
        final String commandName = String.format("EJB : %s.%s", nscsContextService.getClassNameContextValue(), nscsContextService.getMethodNameContextValue());
        final CommandPhase commandPhase = CommandPhase.FINISHED_WITH_ERROR;
        final String source = NSCS_SOURCE;
        final String resource = NSCS_RESOURCE;
        final String additionalInfo = String.format("Node [%s], Error Details [%s]", nscsContextService.getInputNodeNameContextValue(), nscsContextService.getErrorDetailContextValue());
        logger.debug("Calling systemRecorder with: commandName: {}, commandPhase: {}, source: {}, resource: {}, additionalInfo: {}", commandName,commandPhase,source,resource,additionalInfo);
        nscsSystemRecorder.recordCommand(commandName,commandPhase,source,resource,additionalInfo);
    }
}
