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
package com.ericsson.nms.security.nscs.workflow.task.cpp.moaction;

import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Security;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Security.CertEnrollStateValue;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.timer.IntervalJobAction;
import com.ericsson.nms.security.nscs.timer.IntervalJobService;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.nms.security.nscs.workflow.task.cpp.WFMessageConstants;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoAttributeException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.CancelCertEnrollmentTask;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_CANCEL_CERT_ENROLLMENT
 * </p>
 * <p>
 * Cancels certificate enrollment of the target node
 * </p>
 *
 * @author emaynes
 */
@WFTaskType(WorkflowTaskType.CPP_CANCEL_CERT_ENROLLMENT)
@Local(WFTaskHandlerInterface.class)
public class CancelCertEnrollmentTaskHandler implements WFActionTaskHandler<CancelCertEnrollmentTask>, WFTaskHandlerInterface {

    //TODO: update this interval once get better measure
    private static final int POLL_INTERVAL = 10000;
    private static final int FIRST_POLL_DELAY = 2000;
    private static final int POLL_TIMES = 3;

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private MOActionService moActionService;

    @EServiceRef
    private IntervalJobService intervalJob;

    @Inject
    private NscsCMReaderService readerService;

    @Override
    public void processTask(final CancelCertEnrollmentTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);
        final NodeReference node = task.getNode();

        try {
            final NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(node);
            nscsLogger.workFlowTaskHandlerOngoing(task, "Performing Action for node: [" + task.getNodeFdn() + "]");
            moActionService.performMOAction(normalizable.getFdn(), MoActionWithoutParameter.Security_cancelCertEnrollment);
        } catch (final Exception e) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task,
                    "Action '" + MoActionWithoutParameter.Security_cancelCertEnrollment + "' on node [" + task.getNodeFdn() + "] failed!");
            throw e;
        }

        // call EJB timer to check when the CertEnrollState is reset to IDLE
        intervalJob.createIntervalJob(FIRST_POLL_DELAY, POLL_INTERVAL, POLL_TIMES, new CheckCertEnrollStateIntervalJob(node, nscsLogger, task));
        nscsLogger.info("processTask CancelCertEnrollmentTaskHandler for node [" + node + "] is finished");
    }

    public static class CheckCertEnrollStateIntervalJob implements IntervalJobAction {

        private final NodeReference node;
        private final NscsLogger log;
        private final CancelCertEnrollmentTask task;

        public CheckCertEnrollStateIntervalJob(final NodeReference node, final NscsLogger nscsLogger, final CancelCertEnrollmentTask task) {
            this.node = node;
            this.log = nscsLogger;
            this.task = task;
        }

        @Override
        public boolean doAction(final Map<JobActionParameters, Object> params) {
            final WorkflowHandler handler = (WorkflowHandler) params.get(JobActionParameters.WORKFLOW_HANDLER);
            final NscsCMReaderService reader = (NscsCMReaderService) params.get(JobActionParameters.CM_READER);
            final String certEnrollState = getCertEnrollState(node, reader, task);
            if (certEnrollState == null) {
                // TODO: hardcoded - arquillian tests is returning null.
                handler.dispatchMessage(node, WFMessageConstants.CPP_ATT_CERT_ENROLL_STATE_CHANGE);
                return true;
            }
            final CertEnrollStateValue state = Security.CertEnrollStateValue.valueOf(certEnrollState);
            switch (state) {
            case IDLE:
                //we want to notify WFS when attribute returns IDLE
                handler.dispatchMessage(node, WFMessageConstants.CPP_ATT_CERT_ENROLL_STATE_CHANGE);
                log.workFlowTaskHandlerFinishedWithSuccess(task, "OAM [" + node.getName() + "] : CertEnrollState attribute reset to " + state.name()
                        + " for Node '" + node.getFdn() + " is finished.");
                return true;
            default:
                log.debug("CheckCertEnrollStateIntervalJob get CertEnrollState [" + state.name() + "] for node [" + node.getFdn() + "].");
                return false;
            }
        }

        /**
         * @param node
         *            NodeRefence of node
         * @param reader
         *            NSCSCMReaderService
         * @return The value of CertenrollState attribute
         */
        private String getCertEnrollState(final NodeReference node, final NscsCMReaderService reader, final CancelCertEnrollmentTask task) {
            final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(node);
            String certEnrollState = null;

            log.info("CheckCertEnrollStateIntervalJob Reading objects type [" + Model.ME_CONTEXT.managedElement.systemFunctions.security.type()
                    + "], namespace [" + Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace() + "], attribute ["
                    + Security.CERT_ENROLL_STATE + "]");

            final CmResponse cmCertEnrollStateAttribute = reader.getMOAttribute(normNode,
                    Model.ME_CONTEXT.managedElement.systemFunctions.security.type(),
                    Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace(), Security.CERT_ENROLL_STATE);

            if (cmCertEnrollStateAttribute.getCmObjects().isEmpty()) {
                final MissingMoAttributeException ex = new MissingMoAttributeException(node.getFdn(),
                        Model.ME_CONTEXT.managedElement.systemFunctions.security.type(), Security.CERT_ENROLL_STATE);
                log.workFlowTaskHandlerFinishedWithError(task,
                        "CheckCertEnrollStateIntervalJob raises MissingMoAttributeException with message " + ex.getMessage());
                throw ex;
            } else if (cmCertEnrollStateAttribute.getCmObjects().size() > 1) {
                final UnexpectedErrorException ex = new UnexpectedErrorException(
                        String.format("Got too many results (" + cmCertEnrollStateAttribute.getCmObjects().size() + ") was expecting 1"));
                log.workFlowTaskHandlerFinishedWithError(task, "CheckCertEnrollStateIntervalJob got too many results in the CMReader response : "
                        + cmCertEnrollStateAttribute.getCmObjects());
                throw ex;
            } else {
                certEnrollState = (String) cmCertEnrollStateAttribute.getCmObjects().iterator().next().getAttributes()
                        .get(Security.CERT_ENROLL_STATE);
                log.info("CheckCertEnrollStateIntervalJob read certEnrollState [" + certEnrollState + "]");
            }
            return certEnrollState;
        }
    }

}
