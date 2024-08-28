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
package com.ericsson.nms.security.nscs.workflow.eventhandling;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author elucbot
 * 
 *         Observer on worfklow events
 *
 */
@ApplicationScoped
public class NScsWorkflowEventHandlingServiceBean {

    //    @Inject
    //    private Logger logger;

    //    public void receiveWorkflowUsertaskEvent(@Observes @Modeled final WorkflowUsertaskEvent event) {
    //        if (event != null) {
    //            logger.info("I got a {} notification! Event {}", event.getClass().getName(), event);
    //        } else {
    //            logger.error("Invalid data received on method {}", "receiveWorkflowUsertaskEvent()");
    //        }
    //    }
    //
    //    public void receiveWorkflowProgressEvent(@Observes @Modeled final WorkflowProgressEvent event) {
    //        if (event != null) {
    //            String eventAttrs = "NA";
    //            if (event.getEventAttributes() != null) {
    //                eventAttrs = "";
    //                for (final Map.Entry<String, String> entry : event.getEventAttributes().entrySet()) {
    //                    eventAttrs += String.format("key/value [%s]/[%s] - ", entry.getKey(), entry.getValue());
    //                }
    //            }
    //
    //            final String data = String.format("workflowInstanceId %s, " + "businessKey %s, " + "eventNodeName %s, " + "eventType %s, " + "isEndEvent %s, " + "eventAttrs %s",
    //                    event.getWorkflowInstanceId(), event.getBusinessKey(), event.getNodeName(), event.getEventType(), event.isEndEvent(), eventAttrs);
    //            logger.info("I got a {} notification! Data {}", event.getClass().getName(), data);
    //
    //        } else {
    //            logger.error("Invalid data received on method {}", "receiveWorkflowProgressEvent()");
    //        }
    //
    //    }
    //
    //    public void receiveWorkflowInstanceEvent(@Observes @Modeled final WorkflowInstanceEvent event) {
    //        if (event != null) {
    //            final String data = String.format("workflowInstanceId %s, businessKey %s, eventType %s", event.getWorkflowInstanceId(), event.getBusinessKey(), event.getEventType());
    //
    //            logger.info("I got a {} notification! Data {}", event.getClass().getName(), data);
    //        } else {
    //            logger.error("Invalid data received on method {}", "receiveWorkflowInstanceEvent()");
    //        }
    //    }
}
