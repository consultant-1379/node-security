/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.fm.eventhandling;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.exception.WorkflowHandlerException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.eventbus.model.annotation.Modeled;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedAlarmEvent;
import com.ericsson.oss.services.fm.models.processedevent.ProcessedEventState;

/**
 * This service handles appropriate actions based on node events/alarms as sent by FM to Node Security
 *
 * @author egbobcs
 */
@ApplicationScoped
public class FMAlarmEventHandlerService implements FMAlarmEventHandler {

    private static final String CHANNEL_ID = "//global/NscsAlarmDivertQueue"/* "//global/FmProcessedEventChannel" */;
    private static final String FILTER_PATTERN = "managedObject='Security'";

    @Inject
    private Logger log;

    @Inject
    private NscsLogger nscsLogger;

    @EServiceRef
    WorkflowHandler workflowHandler;

    @Override
    public void receiveFMAlarmEvent(@Observes @Modeled(channelId = CHANNEL_ID, filter = FILTER_PATTERN) final ProcessedAlarmEvent payload) {
        log.info("Receiving and processing Security alarm from {} [{}]", CHANNEL_ID, payload);

        //Forwarding message to WF instance
        // Restore 14B TORF-38537 - Disabled for 14B to be re-enabled once pki solution is in place
        forwardMessageWorkflow(payload);

        //Later we could do additional things here if need for example take action if a node certificate is about to expire
        log.debug("Alarm processing finished");
    }

    public void forwardMessageWorkflow(final ProcessedAlarmEvent payload) {
        final String sp = payload.getSpecificProblem();
        log.debug("Specific problem is [{}]", sp);

        final CPPAlarmEvent message = CPPAlarmEvent.getFromSpecificProblem(sp);

        if ((message != null)) {
            final String messageToDispatch = message.toString();

            if (ProcessedEventState.ACTIVE_ACKNOWLEDGED.name().equals(payload.getAlarmState().name())
                    || ProcessedEventState.ACTIVE_UNACKNOWLEDGED.name().equals(payload.getAlarmState().name())) {

                log.info("Message type [{}] found relevant for security workflows, dispatching message to workflow handler", messageToDispatch);
                try {
                    workflowHandler.dispatchMessage(new NodeRef(payload.getFdn()), messageToDispatch);
                    final String messageToLog = String.format("Dispatched workflow message [%s], nodeFdn [%s]", messageToDispatch, payload.getFdn());
                    log.info(messageToLog);
                } catch (final WorkflowHandlerException e) {
                    // log.error("Cannot dispatch message to [{}], no execution is in waiting state for message [{}]", payload.getFdn(), message.toString());
                    nscsLogger.error("Cannot dispatch message to [{}], no execution is in waiting state for message [{}]", payload.getFdn(),
                            message.toString());
                }
            } else {
                log.info("Message type [{}] would be relevant for security workflows BUT ignored since the alarm state is [{}].",
                        messageToDispatch, payload.getAlarmState().name());
            }
        } else {
            log.debug("Message type is not relevant to security workflows, skipping");
            log.debug("Repeat error message from workflow : ", payload.getRecordType());
        }
    }

}
