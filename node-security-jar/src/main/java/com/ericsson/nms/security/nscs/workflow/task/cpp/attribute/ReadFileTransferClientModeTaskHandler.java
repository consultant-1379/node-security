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

import java.util.Arrays;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Security;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.nms.security.nscs.workflow.task.cpp.WFMessageConstants;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.InvalidMoAttributeValueException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoAttributeException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.ReadFileTransferClientModeTask;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_READ_FILE_TRANSFER_CLIENT_MODE
 * </p>
 * <p>
 * Fetches file transfer client mode of the node an returns it.
 * </p>
 *
 * @author emaynes on 18/06/2014.
 */
@WFTaskType(WorkflowTaskType.CPP_READ_FILE_TRANSFER_CLIENT_MODE)
@Local(WFTaskHandlerInterface.class)
public class ReadFileTransferClientModeTaskHandler implements WFQueryTaskHandler<ReadFileTransferClientModeTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Override
    public String processTask(final ReadFileTransferClientModeTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);
        final NodeReference node = task.getNode();

        final NormalizableNodeReference normNode = readerService.getNormalizableNodeReference(node);

        String resultTransferMode;
        final CmResponse fileTransferClientMode = readerService.getMOAttribute(normNode,
                Model.ME_CONTEXT.managedElement.systemFunctions.security.type(), Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace(),
                Security.FILE_TRANSFER_CLIENT_MODE);

        if (fileTransferClientMode.getCmObjects().isEmpty()) {
            final MissingMoAttributeException ex = new MissingMoAttributeException(node.getFdn(),
                    Model.ME_CONTEXT.managedElement.systemFunctions.security.type(), Security.FILE_TRANSFER_CLIENT_MODE);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task,
                    "ReadFileTransferClientModeTaskHandler.processTask raises MissingMoAttributeException with message : " + ex.getMessage());
            throw ex;
        } else if (fileTransferClientMode.getCmObjects().size() > 1) {
            final UnexpectedErrorException ex = new UnexpectedErrorException(
                    String.format("Got too many results (%s) was expecting 1", fileTransferClientMode.getCmObjects().size()));
            nscsLogger.workFlowTaskHandlerFinishedWithError(task,
                    "ReadFileTransferClientModeTaskHandler.processTask() raises UnexpectedErrorException with message : " + ex.getMessage());
            throw ex;
        } else {
            final String transferClientMode = (String) fileTransferClientMode.getCmObjects().iterator().next().getAttributes()
                    .get(Security.FILE_TRANSFER_CLIENT_MODE);
            ModelDefinition.Security.FileTransferClientModeValue transferClientModeValue = null;
            try {
                transferClientModeValue = ModelDefinition.Security.FileTransferClientModeValue.valueOf(transferClientMode);
            } catch (final IllegalArgumentException e) {
                final InvalidMoAttributeValueException ex = new InvalidMoAttributeValueException(node.getFdn(), Security.FILE_TRANSFER_CLIENT_MODE,
                        transferClientMode, Arrays.asList(ModelDefinition.Security.FileTransferClientModeValue.values()).toString());
                nscsLogger.workFlowTaskHandlerFinishedWithError(task,
                        "ReadFileTransferClientModeTaskHandler.processTask() raises InvalidMoAttributeValueException with message : "
                                + ex.getMessage());
                throw ex;
            }
            if (ModelDefinition.Security.FileTransferClientModeValue.FTP.equals(transferClientModeValue)) {
                resultTransferMode = WFMessageConstants.CPP_FILE_TRANSFER_CLIENT_MODE_UNSECURE;
            } else {
                resultTransferMode = WFMessageConstants.CPP_FILE_TRANSFER_CLIENT_MODE_SECURE;
            }
        }

        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task,
                "ReadFileTransferClientModeTaskHandler.processTask() exiting - File transfer client mode for " + node + " is " + resultTransferMode
                        + "with Success.");
        return resultTransferMode;
    }
}
