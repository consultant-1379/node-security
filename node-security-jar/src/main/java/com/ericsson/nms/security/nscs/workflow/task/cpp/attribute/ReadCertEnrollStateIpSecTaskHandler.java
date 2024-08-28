/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
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
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec;
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
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.ReadCertEnrollStateIpSecTask;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_READ_CERT_ENROLL_IPSEC_STATE
 * </p>
 * 
 * @author emehsau
 */
@WFTaskType(WorkflowTaskType.CPP_READ_CERT_ENROLL_IPSEC_STATE)
@Local(WFTaskHandlerInterface.class)
public class ReadCertEnrollStateIpSecTaskHandler implements WFQueryTaskHandler<ReadCertEnrollStateIpSecTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Override
    public String processTask(final ReadCertEnrollStateIpSecTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);
        final NodeReference node = task.getNode();
        final NormalizableNodeReference normNode = readerService.getNormalizableNodeReference(node);
        String certEnrollState;
        nscsLogger.workFlowTaskHandlerOngoing(task, "Action Ongoing for node: " + task.getNodeFdn());
        final CmResponse cmCertEnrollStateAttribute = readerService.getMOAttribute(normNode, Model.ME_CONTEXT.managedElement.ipSystem.ipSec.type(),
                Model.ME_CONTEXT.managedElement.ipSystem.ipSec.namespace(), IpSec.CERT_ENROLL_STATE);
        if (cmCertEnrollStateAttribute.getCmObjects().isEmpty()) {
        	final MissingMoAttributeException ex = new MissingMoAttributeException(node.getFdn(), Model.ME_CONTEXT.managedElement.ipSystem.ipSec.type(), IpSec.CERT_ENROLL_STATE);
        	nscsLogger.workFlowTaskHandlerFinishedWithError(task, "ReadCertEnrollStateIpSecTaskHandler.processTask() raises MissingMoAttributeException with message " + ex.getMessage());
            throw ex;
        } else if (cmCertEnrollStateAttribute.getCmObjects().size() > 1) {
            final UnexpectedErrorException ex = new UnexpectedErrorException(String.format("Got too many results " + cmCertEnrollStateAttribute.getCmObjects().size() + " was expecting 1"));
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, "ReadCertEnrollStateIpSecTaskHandler.processTask() raises UnexpectedErrorException with message " + ex.getMessage());
            throw ex;
        } else {
            certEnrollState = (String) cmCertEnrollStateAttribute.getCmObjects().iterator().next().getAttributes().get(IpSec.CERT_ENROLL_STATE);
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "ReadCertEnrollStateIpSecTaskHandler.processTask() exiting - CPP_READ_CERT_ENROLL_IPSEC_STATE for node " + task.getNodeFdn() + " is '" + certEnrollState + "'.");
            return certEnrollState;
        }
    }
}
