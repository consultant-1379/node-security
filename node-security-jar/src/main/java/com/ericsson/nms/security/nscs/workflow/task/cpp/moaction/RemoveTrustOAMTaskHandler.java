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

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.enums.TrustCategoryType;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.data.moget.MOGetServiceFactory;
import com.ericsson.nms.security.nscs.data.moget.param.CertStateInfo;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskResult;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.RemoveTrustOAMTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

@WFTaskType(WorkflowTaskType.CPP_REMOVE_TRUST_OAM)
@Local(WFTaskHandlerInterface.class)
public class RemoveTrustOAMTaskHandler implements WFQueryTaskHandler<RemoveTrustOAMTask>, WFTaskHandlerInterface {

    private static final String DELETE_FAILED = WFTaskResult.FALSE.getTaskResult();
    private static final String DELETE_SUCCESS = WFTaskResult.TRUE.getTaskResult();
    public static final String SERIAL_NUM_KEY = "serialNumber";
    public static final String ISSUER_KEY = "issuer";
    private static final String ERROR_MESSAGE_TRUST_REMOVE="Can't remove %s Trust cert for node: %s, null or empty %s";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private MOGetServiceFactory moGetServiceFactory;

    @Inject
    private MOActionService moAction;

    @Override
    public String processTask(final RemoveTrustOAMTask task) {
        String result = DELETE_FAILED;
        nscsLogger.workFlowTaskHandlerStarted(task);

        String trustCategory = TrustCategoryType.OAM.toString();

        if (task.getCertCategory().equals(TrustedCertCategory.LOCAL_AA_DB_FILE_SIGNERS.toString())) {
            trustCategory = TrustCategoryType.LAAD.toString();
        }
        nscsLogger.info("Removing trusts of category {} on the Node {}", trustCategory, task.getNodeFdn());

        final NodeReference node = task.getNode();
        final NormalizableNodeReference normNode = readerService.getNormalizableNodeReference(node);

        final String nodeTrustCategory  = task.getCertCategory();
        final String certSN = task.getCertificateSN();
        final String certIssuer = task.getIssuer();

        String errorMsg = "";
        boolean isError = false;

        if (nodeTrustCategory  == null || nodeTrustCategory .isEmpty()) {
            errorMsg = String.format(ERROR_MESSAGE_TRUST_REMOVE,trustCategory,normNode.getFdn()," nodeTrustCategory");
            isError = true;
        } else if (certSN == null || certSN.isEmpty()) {
            errorMsg = String.format(ERROR_MESSAGE_TRUST_REMOVE,trustCategory,normNode.getFdn()," certSN");
            isError = true;
        } else if (certIssuer == null || certIssuer.isEmpty()) {
            errorMsg = String.format(ERROR_MESSAGE_TRUST_REMOVE,trustCategory,normNode.getFdn()," certIssuer");
            isError = true;
        }

        if (isError) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMsg);
            final UnexpectedErrorException ex = new UnexpectedErrorException(errorMsg);
            throw ex;
        }

        nscsLogger.info("Getting Trust Certificate list from node, normNode.getFdn():" + normNode.getFdn() + ", trustCategory: " + trustCategory + ", nodeTrustCategory " + nodeTrustCategory );
        final CertStateInfo trustCertificateInfo = moGetServiceFactory.getTrustCertificateStateInfo(normNode, trustCategory);
        if (trustCertificateInfo == null || trustCertificateInfo.getCertificates() == null || trustCertificateInfo.getCertificates().size() == 0) {
            errorMsg = String.format("Can't remove Trust cert for node: " + normNode.getFdn() + ", null or empty trust cert list for trustCategory: "
                    + trustCategory);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMsg);
            final UnexpectedErrorException ex = new UnexpectedErrorException(errorMsg);
            throw ex;
        }

        boolean isFound = false;
        String nodeCertIssuer = certIssuer;
        //TODO
        //Is the Category really needed to find out the correct trust installed?
        for (final CertDetails cert : trustCertificateInfo.getCertificates()) {
            if (CertDetails.matchesDN(certIssuer, cert.getIssuer()) && certSN.equals(cert.getSerial().toString())) {
                isFound = true;
                nodeCertIssuer = cert.getIssuer();
                break;
            }
        }

        //If not found, we assume an error, since the command validator should have detected that this node really needs to have its Trust cert removed
        if (isFound) {

            final MoParams mopar = new MoParams();

            mopar.addParam("serialNumber", certSN);
            mopar.addParam("issuer", nodeCertIssuer);
            mopar.addParam("category", nodeTrustCategory);

            final MoActionWithParameter action = MoActionWithParameter.Security_removeTrustedCert;
            nscsLogger.info("Ready to perform action:" + action.getAction() + ", on node:" + node.getName() + ", trustCategory:" + trustCategory
                    + ", serialNumber:" + certSN + ", issuer:" + certIssuer + ", category:" + nodeTrustCategory + ".");

            try {
                moAction.performMOAction(normNode.getFdn(), action, mopar);
                result = DELETE_SUCCESS;
                nscsLogger.workFlowTaskHandlerOngoing(task, "Node Security Service - Deleting " + trustCategory + " Trust Certificate");
            } catch (final Exception e) {
                errorMsg = String.format("Exception when removing %s Trust cert for node: %s" , trustCategory, normNode.getFdn());
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMsg);
            }
        } else {
            errorMsg = String.format(
                    "Can't remove Trust cert for node:%s, no trust certificate installed for trustCategory:%s, SN:%s, Issuer:%s, Category: %s",
                    normNode.getFdn(), trustCategory, certSN, certIssuer, nodeTrustCategory);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMsg);
            final UnexpectedErrorException ex = new UnexpectedErrorException(errorMsg);
            throw ex;
        }

        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "Returning value: " + result);
        return result;
    }
}
