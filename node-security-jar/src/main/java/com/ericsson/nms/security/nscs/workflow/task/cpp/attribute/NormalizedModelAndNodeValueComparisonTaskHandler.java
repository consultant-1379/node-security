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
package com.ericsson.nms.security.nscs.workflow.task.cpp.attribute;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.command.utility.NscsGetJobResponseBuilder;
import com.ericsson.nms.security.nscs.handler.command.utility.WebServerStatus;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.MoAttributeHandler;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.InvalidNodeException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;

/**
 * 
 * @author edudluk
 *
 */
class NormalizedModelAndNodeValueComparisonTaskHandler {

    @Inject
    private NscsLogger nscsLogger;
    @Inject
    private NscsCMReaderService readerService;
    @Inject
    private MoAttributeHandler moAttributeHandler;

    public void processTask(WorkflowActionTask task, WebServerStatus webServerStatus) {
        nscsLogger.workFlowTaskHandlerStarted(task);

        final NormalizableNodeReference normRef = readerService.getNormalizedNodeReference(task.getNode());
        if (normRef == null) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, "Could not find normalized node reference for "
                    + task.getNode() + ". Can't send SSH command to node.");
            throw new InvalidNodeException("Not a valid node: Cannot find normalized reference.");
        }

        nscsLogger.debug("Found normalized node reference : " + normRef.getFdn());

        final Mo cppConnectivityInformationMo = Model.NETWORK_ELEMENT.cppConnectivityInformation;
        String namespace = cppConnectivityInformationMo.namespace();
        String moType = cppConnectivityInformationMo.type();
        String attributeName = ModelDefinition.CppConnectivityInformation.HTTPS;

        final Boolean httpsAttributeInCppConnectivityInfo = Boolean.parseBoolean(
                (moAttributeHandler.getMOAttributeValue(normRef.getFdn(), moType, namespace, attributeName)));

        final String compareResponse = moAttributeHandler.match(webServerStatus, httpsAttributeInCppConnectivityInfo);

        StringBuilder additionalInfo = new StringBuilder();
        additionalInfo.append("Comparison operation with result ");
        additionalInfo.append("[");
        additionalInfo.append(compareResponse);
        additionalInfo.append("]");
        additionalInfo.append(" finished with Success");

        final Map<String, Object> additionalResults = new LinkedHashMap<String, Object>();
        additionalResults.put(NscsGetJobResponseBuilder.WORKFLOW_RESULT, compareResponse);
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, additionalInfo.toString(), additionalResults);

    }

}