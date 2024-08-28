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
package com.ericsson.nms.security.nscs.workflow.task.cpp.attribute;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Security;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoAttributeException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.ReadCertEnrollStateTask;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_READ_CERT_ENROLL_STATE
 * </p>
 * <p>
 * Fetches the value of the attribute CERT_ENROLL_STATE in the Security MO
 * </p>
 * @author emaynes on 18/06/2014.
 */
@WFTaskType(WorkflowTaskType.CPP_READ_CERT_ENROLL_STATE)
@Local(WFTaskHandlerInterface.class)
public class ReadCertEnrollStateTaskHandler implements WFQueryTaskHandler<ReadCertEnrollStateTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Override
    public String processTask(final ReadCertEnrollStateTask task) {
    	nscsLogger.workFlowTaskHandlerStarted(task);
        final NodeReference node = task.getNode();
        final NormalizableNodeReference normNode = readerService.getNormalizableNodeReference(node);
        nscsLogger.info("ReadCertEnrollStateTaskHandler Reading objects type [" + Model.ME_CONTEXT.managedElement.systemFunctions.security.type() + "], namespace [" + Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace() + "], attribute [" + Security.CERT_ENROLL_STATE + "]");
                
        String certEnrollState = null;
        final CmResponse cmCertEnrollStateAttribute = readerService.getMOAttribute(normNode,
                Model.ME_CONTEXT.managedElement.systemFunctions.security.type(),
                Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace(),
                Security.CERT_ENROLL_STATE);

        nscsLogger.info("ReadCertEnrollStateTaskHandler cmCertEnrollStateAttribute [" + cmCertEnrollStateAttribute + "], size [" + cmCertEnrollStateAttribute.getCmObjects().size() + "]");
        if (cmCertEnrollStateAttribute.getCmObjects().isEmpty()) {
        	final MissingMoAttributeException ex = new MissingMoAttributeException(node.getFdn(), Model.ME_CONTEXT.managedElement.systemFunctions.security.type(), Security.CERT_ENROLL_STATE );
           	nscsLogger.workFlowTaskHandlerFinishedWithError(task, "ReadCertEnrollStateTaskHandler.processTask() raises MissingMoAttributeException with message : " + ex.getMessage());
            throw ex;
        } else if (cmCertEnrollStateAttribute.getCmObjects().size() > 1) {
        	final UnexpectedErrorException ex = new UnexpectedErrorException(String.format("Got too many results (%s) was expecting 1",cmCertEnrollStateAttribute.getCmObjects().size() ));
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, "ReadCertEnrollStateTaskHandler.processTask() got too many results in the CMReader response : " + cmCertEnrollStateAttribute.getCmObjects());
            throw ex;
        } else {
            certEnrollState = (String) cmCertEnrollStateAttribute.getCmObjects().iterator().next().getAttributes().get(Security.CERT_ENROLL_STATE);
            nscsLogger.workFlowTaskHandlerOngoing(task, "Action Ongoing for node: " + node.getFdn());
        }
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "ReadCertEnrollStateTaskHandler.processTask() exiting - CERT_ENROLL_STATE for node [" + task.getNodeFdn() + "] is '" + certEnrollState + "' with Success.");
        return certEnrollState;
    }
}
