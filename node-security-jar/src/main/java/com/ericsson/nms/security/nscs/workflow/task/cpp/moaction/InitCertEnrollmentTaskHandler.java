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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NSCSCppNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.InitCertEnrollmentTask;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_INITIALIZE_CERT_ENROLLMENT
 * </p>
 * <p>
 * Initialize the certificate enrollment of the node
 * </p>
 *
 * @author emaynes on 16/06/2014.
 */
@WFTaskType(WorkflowTaskType.CPP_INIT_CERT_ENROLLMENT)
@Local(WFTaskHandlerInterface.class)
public class InitCertEnrollmentTaskHandler implements WFActionTaskHandler<InitCertEnrollmentTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private MOActionService moAction;

    @Inject
    private CppSecurityService cppSecService;

    @Inject
    private NscsCMReaderService readerService;

    @Override
    public void processTask(final InitCertEnrollmentTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);
        final NodeReference node = task.getNode();

        final String nodeName = node.getName();
        ScepEnrollmentInfoImpl enrollmentInfo = null;

        try {

            enrollmentInfo = (ScepEnrollmentInfoImpl) cppSecService.generateOamEnrollmentInfo(nodeName);

            enrollmentInfo.setRollbackTimeout(task.getRollbackTimeout());

            // Override EnrollmentMode to support KUR
            final EnrollmentMode updateEnrollMode = cppSecService.enrollmentModeUpdate(task.getNodeFdn(), CertificateType.OAM.toString(),
                    enrollmentInfo.getEntity());
            enrollmentInfo.setEnrollmentProtocol(updateEnrollMode.getEnrollmentModeValue());
            nscsLogger.info("enrollmentInfo in InitCertEnrollmentTaskHandler processTask method:" + enrollmentInfo);

        } catch (final CppSecurityServiceException e) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, "processTask InitCertEnrollmentTask for node [" + task.getNodeFdn() + "] failed.");
            throw new WorkflowTaskException("processTask InitCertEnrollmentTask failed", e);
        }

        final MoParams mopar = enrollmentInfo.toMoParams();
        final List<MoParams> moParamsList = new ArrayList<>();
        moParamsList.add(convertMoParamsForMoAction(mopar));

        if (enrollmentInfo.getEnrollmentProtocol().equals(EnrollmentMode.CMPv2_VC.getEnrollmentModeValue())) {
            enrollmentInfo.setEnrollmentProtocol(EnrollmentMode.CMPv2_INITIAL.getEnrollmentModeValue());
            final MoParams moparInitial = enrollmentInfo.toMoParams();
            moParamsList.add(convertMoParamsForMoAction(moparInitial));
        }

        final NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(node);
        nscsLogger.workFlowTaskHandlerOngoing(task, "Performing Action for node: [" + task.getNodeFdn() + "]");
        moAction.performMOAction(normalizable.getFdn(), MoActionWithParameter.Security_initCertEnrollment, moParamsList);
        final String shortDescription = String.format(NscsLogger.ACTION_PERFORMED_WAITING_EVENT_FORMAT,
                MoActionWithParameter.Security_initCertEnrollment.getAction());
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "processTask InitCertEnrollmentTask for node [" + node + "] is finished",
                shortDescription);
    }

    private MoParams convertMoParamsForMoAction(final MoParams moparams) {
        @SuppressWarnings("unchecked")
        final Map<String, Object> map = (Map<String, Object>) moparams.getParam();

        for (final String key : map.keySet()) {
            //Recursive call for each items in the MAP
            final Object value = map.get(key);
            nscsLogger.debug("Before changes MoParams MAP key/value: " + key + "/" + value.toString());
        }

        final MoParams enrollmentDataParams = (MoParams) map.get("data");

        final MoParams newEnrollmentDataParams = NSCSCppNodeUtility.updateMoParamsUsingNodeModelValues(enrollmentDataParams);
        map.put("data", newEnrollmentDataParams);

        for (final String key : map.keySet()) {
            //Recursive call for each items in the MAP
            final Object value = map.get(key);
            nscsLogger.debug("After changes MoParams MAP key/value: " + key + "/" + value.toString());
        }
        return moparams;
    }

}
