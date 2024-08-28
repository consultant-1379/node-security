/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.node.attribute;

import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.SetMoAttributesTask;

/**
 * <p>
 * Task handler for WorkflowNames.WORKFLOW_SET_MO_ATTRIBUTES
 * </p>
 * <p>
 * This class is used to set MO attributes on the target nodes.
 * </p>
 *
 * @author xchimvi
 */
@WFTaskType(WorkflowTaskType.SET_MO_ATTRIBUTES)
@Local(WFTaskHandlerInterface.class)
public class SetMoAttributesTaskHandler implements WFQueryTaskHandler<SetMoAttributesTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService cmReaderService;

    @Inject
    private NscsCMWriterService cmWriterService;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    private static final String MOS_CONFIGURED = "Mos_Configured";
    private static final String MOS_NOT_CONFIGURED = "Mos_Not_Configured";
    private static final String MoNameSpace = "";

    @Override
    public String processTask(final SetMoAttributesTask task) {

        String result = null;
        nscsLogger.workFlowTaskHandlerStarted(task);

        final NodeReference node = task.getNode();
        final NormalizableNodeReference normalizableNodeRef = cmReaderService.getNormalizableNodeReference(node);
        final String neType = normalizableNodeRef.getNeType();
        final String mirrorRootFdn = normalizableNodeRef.getFdn();
        nscsLogger.info(task, "From task : mirrorRootFdn [" + mirrorRootFdn + "] neType [" + neType + "]");
        final String targetModelIdentity = normalizableNodeRef.getOssModelIdentity();
        nscsLogger.info(task, "From task : nodeType [" + neType + "] targetModelIdentity [" + targetModelIdentity + "]");

        for (final String mo : task.getMoAttributes().keySet()) {
            final String moFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, mo, MoNameSpace);

            final String readMessage = NscsLogger.stringifyReadParams(mirrorRootFdn, mo);
            nscsLogger.debug(task, "Reading " + readMessage);

            if (moFdn == null || moFdn.isEmpty()) {
                final String errorMessage = "Error while reading " + readMessage;
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new MissingMoException(node.getName(), mo);
            }
            final SetMoAttrResult returnValue = setMoAttributes(task.getMoAttributes().get(mo), moFdn, mo);
            result = returnValue.getResult();

            if (MOS_NOT_CONFIGURED.equals(result)) {
                final String errorMessage = "Failed to update mo attributes : [" + result + "]";
                final String resultInfo = NscsLogger.stringifyException(returnValue.getException()) + " updating MO " + mo;
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage, resultInfo);
                throw new UnexpectedErrorException(resultInfo);
            }
            final String updateMessage = NscsLogger.stringifyUpdateParams(mo, moFdn);
            nscsLogger.workFlowTaskHandlerOngoing(task, "Successfully updated " + updateMessage);
        }
        if (MOS_CONFIGURED.equals(result)) {
            final String successMessage = "Successfully completed : [" + result + "]";
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        }
        return result;
    }

    private SetMoAttrResult setMoAttributes(final Map<String, Object> task, final String moFdn, final String mo) {
        final String updateMessage = NscsLogger.stringifyUpdateParams(mo, moFdn);
        try {
            final NscsCMWriterService.WriterSpecificationBuilder nodeAttrsSpec = cmWriterService.withSpecification();
            for (Map.Entry<String, Object> entry : task.entrySet()) {
                String attribute = entry.getKey();
                nodeAttrsSpec.setNotNullAttribute(attribute, task.get(attribute));
            }
            nodeAttrsSpec.setFdn(moFdn);
            nodeAttrsSpec.updateMO();
        } catch (final Exception e) {
            nscsLogger.info("Updating " + updateMessage);
            return new SetMoAttrResult(MOS_NOT_CONFIGURED, e);
        }
        return new SetMoAttrResult(MOS_CONFIGURED);
    }

    /**
     * <p>
     * Inner class for setMoAttributes result storage
     * </p>
     * <p>
     * This class is used to export result ando/or exception thrown when trying to set MO attributes on the target nodes.
     * </p>
     *
     * @author erosrob
     */
    private class SetMoAttrResult {
        private final String result;
        private final Exception exception;

        SetMoAttrResult(final String result, final Exception exception) {
            this.result = result;
            this.exception = exception;
        }

        SetMoAttrResult(final String result) {
            this(result, null);
        }

        /**
         * @return the result
         */
        public String getResult() {
            return result;
        }

        /**
         * @return the exception
         */
        public Exception getException() {
            return exception;
        }
    }

}
