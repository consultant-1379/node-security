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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

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
import com.ericsson.oss.itpf.sdk.instrument.annotation.Profiled;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoAttributeException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.CheckCertAlreadyInstalledTask;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_CHECK_CERT_ALREADY_INSTALLED
 * </p>
 *
 * @author eanbuzz
 */
@WFTaskType(WorkflowTaskType.CPP_CHECK_CERT_ALREADY_INSTALLED)
@Local(WFTaskHandlerInterface.class)
public class CheckCertAlreadyInstalledTaskHandler implements WFQueryTaskHandler<CheckCertAlreadyInstalledTask>, WFTaskHandlerInterface {

    private static final String SUBJECT_ALT_NAME = "subjectAltName";
    private static final String NOT_VALID_AFTER = "notValidAfter";

    private static final String NOT_INSTALLED = "NOT_INSTALLED";
    private static final String INSTALLED = "INSTALLED";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Profiled
    @Override
    public String processTask(final CheckCertAlreadyInstalledTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);
        final NodeReference node = task.getNode();
        final NormalizableNodeReference normNode = readerService.getNormalizableNodeReference(node);
        final CmResponse certificateAttribute = readerService.getMOAttribute(normNode, Model.ME_CONTEXT.managedElement.ipSystem.ipSec.type(),
                Model.ME_CONTEXT.managedElement.ipSystem.ipSec.namespace(), IpSec.CERTIFICATE);
        if (certificateAttribute.getCmObjects().isEmpty()) {
            throw new MissingMoAttributeException(node.getFdn(), Model.ME_CONTEXT.managedElement.ipSystem.ipSec.type(), IpSec.CERTIFICATE);
        } else if (certificateAttribute.getCmObjects().size() > 1) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task,
                    "Too many results in the CMReader response : " + certificateAttribute.getCmObjects());
            throw new UnexpectedErrorException(
                    String.format("Got too many results " + certificateAttribute.getCmObjects().size() + " was expecting 1"));
        } else {
            final Map<String, Object> map = (Map<String, Object>) certificateAttribute.getCmObjects().iterator().next().getAttributes()
                    .get(IpSec.CERTIFICATE);
            final String subjectAltName = (String) map.get(SUBJECT_ALT_NAME);
            final String notValidAfter = (String) map.get(NOT_VALID_AFTER);
            // check if subject alt name is the same
            if (subjectAltName == null) {
                return certNotInstalledOnNode(task.getNodeFdn(), task);
            }
            if (!subjectAltName.equals(task.getSubjectAltName())) {
                return certNotInstalledOnNode(task.getNodeFdn(), task);
            }
            // now check validity
            if (isValidityExpiredOrInvalid(notValidAfter, task)) {
                return certNotInstalledOnNode(task.getNodeFdn(), task);
            }
            nscsLogger.workFlowTaskHandlerOngoing(task, "Action Ongoing for node: " + node.getFdn());
            // it is the same subaltname and validity is ok. skip the cert installation
            return certAlreadyInstalledOnNode(task.getNodeFdn(), task);
        }
    }

    private String certAlreadyInstalledOnNode(final String nodeFdn, final CheckCertAlreadyInstalledTask task) {
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "IPsec [" + nodeFdn + "] : Certificate already installed on node with Success",
                NscsLogger.ALREADY_INSTALLED);
        return INSTALLED;
    }

    private String certNotInstalledOnNode(final String nodeFdn, final CheckCertAlreadyInstalledTask task) {
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "IPsec [" + nodeFdn + "] : Certificate is not installed on node");
        return NOT_INSTALLED;
    }

    private boolean isValidityExpiredOrInvalid(final String notValidAfter, final CheckCertAlreadyInstalledTask task) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            final Date certValidity = sdf.parse(notValidAfter);
            final Date currentTime = new Date();
            return currentTime.after(certValidity);
        } catch (final ParseException e) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, String.format("Could not parse UTC validy from node cert " + notValidAfter));
            return true;
        }
    }
}
