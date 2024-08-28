/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
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
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.ReadLaadInstallationFailureTask;

/**
 * Task handler for WorkflowTaskType.CPP_READ_LOCALAADATABASE_INSTALLATION_FAILURE.
 * Fetches the value of the attribute localAADatabaseInstallationFailure in the Security MO
 *
 * @author xkihari
 */
@WFTaskType(WorkflowTaskType.CPP_READ_LAAD_INSTALLATION_FAILURE)
@Local(WFTaskHandlerInterface.class)
public class ReadLaadInstallationFailureTaskHandler implements WFQueryTaskHandler<ReadLaadInstallationFailureTask>, WFTaskHandlerInterface {

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsLogger nscsLogger;

    @Override
    public String processTask(ReadLaadInstallationFailureTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);

        final NodeReference node = task.getNode();
        final NormalizableNodeReference normNode = readerService.getNormalizableNodeReference(node);

        String resultFlag = "";

        final String getMessage = String.format("attribute [%s] of %s MO", Security.LOCAL_AA_DATABASE_INSTALLATION_FAILURE, Model.ME_CONTEXT.managedElement.systemFunctions.security.type());

        nscsLogger.info(task, "Reading " + getMessage);
        final CmResponse laadInstallationFailureAttributeValue = readerService.getMOAttribute(normNode, Model.ME_CONTEXT.managedElement.systemFunctions.security.type(),
                Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace(), Security.LOCAL_AA_DATABASE_INSTALLATION_FAILURE);

        nscsLogger.debug(task, "Read " + getMessage + ": response [" + laadInstallationFailureAttributeValue + "]");

        if (laadInstallationFailureAttributeValue.getCmObjects().isEmpty()) {
            final MissingMoAttributeException missingMoAttributeException = new MissingMoAttributeException(node.getFdn(), Model.ME_CONTEXT.managedElement.systemFunctions.security.type(),
                    Security.LOCAL_AA_DATABASE_INSTALLATION_FAILURE);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task,
                    "ReadLocalAADatabaseInstallationFailureTask.processTask() raises MissingMoAttributeException with message : " + missingMoAttributeException.getMessage());
            throw missingMoAttributeException;
        } else {
            final Boolean localAADatabaseInstallationFailure = (Boolean) laadInstallationFailureAttributeValue.getCmObjects().iterator().next().getAttributes()
                    .get(Security.LOCAL_AA_DATABASE_INSTALLATION_FAILURE);

            nscsLogger.info(task, " Successfully read " + getMessage + ": value [" + localAADatabaseInstallationFailure + "]");

            if (localAADatabaseInstallationFailure != null) {
                resultFlag = localAADatabaseInstallationFailure.toString();
            }

            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "Return localAADatabaseInstallationFailure: " + resultFlag);
        }
        return resultFlag;
    }

}
