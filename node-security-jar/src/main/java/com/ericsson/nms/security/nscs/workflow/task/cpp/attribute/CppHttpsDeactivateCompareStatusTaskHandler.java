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

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.MoAttributeHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.CppHttpsDeactivateCompareStatusTask;

/**
 * Created by ekrzsia on 9/18/17.
 */
@WFTaskType(WorkflowTaskType.CPP_COMPARE_HTTPS_STATUS_FOR_DEACTIVATE)
@Local(WFTaskHandlerInterface.class)
public class CppHttpsDeactivateCompareStatusTaskHandler implements WFQueryTaskHandler<CppHttpsDeactivateCompareStatusTask>, WFTaskHandlerInterface {

    public static final String ATT_WEBSERVER = ModelDefinition.Security.WEBSERVER;
    public static final String ATT_HTTPS = ModelDefinition.CppConnectivityInformation.HTTPS;
    
    public static final ModelDefinition.Mo CPP_CI = Model.NETWORK_ELEMENT.cppConnectivityInformation;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private MoAttributeHandler moAttributeHandler;

    private static final String OMIT = "OMIT";
    private static final String CLI = "CLI";
    
    private static ModelDefinition.Security securityMO = Model.ME_CONTEXT.managedElement.systemFunctions.security;

    @Override
    public String processTask(final CppHttpsDeactivateCompareStatusTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        String cppCIHttps;
        String webServer;
        String result = null;

        try {
            final NormalizableNodeReference normalizableNodeReference = reader
                    .getNormalizableNodeReference(task.getNode());

            cppCIHttps = moAttributeHandler.getMOAttributeValue(normalizableNodeReference.getNormalizedRef().getFdn(),
                    CPP_CI.type(), CPP_CI.namespace(), ATT_HTTPS);
            
            webServer = moAttributeHandler.getMOAttributeValue(normalizableNodeReference.getNormalizableRef().getFdn(),
                    securityMO.type(), securityMO.namespace(), ATT_WEBSERVER);

        } catch (final NscsServiceException exception) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, "There is problem with attributes reading. "
                    + "Reading process returns with error: " + exception.getMessage());
            throw exception;
        }

        if ("HTTPS".equals(webServer)) {
            result = CLI;
        }
        else {
                result = OMIT;
        }

        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "Webserver [" + webServer + "] " +
                "and cppConnectivityInformation [" + cppCIHttps + "] attributes for node: [" + task.getNodeFdn() + "] were read successfully. " +
                "Successfully completed. It returns [" + result + "]");

        return result;

    }
}
