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
package com.ericsson.nms.security.nscs.workflow.task.comecim.attribute;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CertificateContent;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TrustCategory;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TrustedCertificate;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.utilities.NSCSComEcimNodeUtility;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimCheckRemoveTrustTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

@WFTaskType(WorkflowTaskType.COM_ECIM_CHECK_REMOVE_TRUST)
@Local(WFTaskHandlerInterface.class)
public class ComEcimCheckRemoveTrustTaskHandler implements WFQueryTaskHandler<ComEcimCheckRemoveTrustTask>, WFTaskHandlerInterface {

    private static final String REMOVE = "REMOVE";
    private static final String DO_NOT_REMOVE = "DO_NOT_REMOVE";

    private static final String TRUSTED_CERTIFICATE_CONTENT = TrustedCertificate.CERTIFICATE_CONTENT;
    private static final String TRUST_CATEGORY_TRUSTED_CERTIFICATES = TrustCategory.TRUSTED_CERTIFICATES;

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsCapabilityModelService capabilityService;

    @Inject
    private NscsCMWriterService writerService;

    @Inject
    private NSCSComEcimNodeUtility comEcimNodeUtility;

    @SuppressWarnings("unchecked")
    @Override
    public String processTask(final ComEcimCheckRemoveTrustTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        final String trustedCertCategory = task.getCertCategory();
        final String certificateType = NscsNodeUtility.getCertificateTypeFromTrustedCertCategory(trustedCertCategory);
        nscsLogger.info(task, "From task : certificate type[" + certificateType + "]");

        final NodeReference node = task.getNode();
        final NormalizableNodeReference normNode = readerService.getNormalizableNodeReference(node);
        final String mirrorRootFdn = normNode.getFdn();

        final String certSN = task.getCertificateSN();
        final String certIssuer = task.getIssuer();

        String errorMsg = null;

        if (certSN == null || certSN.isEmpty()) {
            errorMsg = "Can't remove trusted certificate : null or empty serial number";
        } else if (certIssuer == null || certIssuer.isEmpty()) {
            errorMsg = "Can't remove trusted certificate : null or empty issuer";
        }

        if (errorMsg != null) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMsg);
            throw new UnexpectedErrorException(errorMsg);
        }

        // Extract output parameters possibly set by previous handlers
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            nscsLogger.info(task, "Output params not yet set!");
        }

        final ComEcimManagedElement targetRootMo = (ComEcimManagedElement) capabilityService.getMirrorRootMo(normNode);
        final String trustCategoryFdn = comEcimNodeUtility.getTrustCategoryFdn(mirrorRootFdn, targetRootMo, certificateType, normNode);
        if (trustCategoryFdn == null) {
            final String errorMessage = "No TrustCategory MO referenced by Trust Users for certificate type [" + certificateType + "]";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        nscsLogger.info(task, "Read trustCategoryFdn [" + trustCategoryFdn + "]");
        final Mo trustCategoryMo = targetRootMo.systemFunctions.secM.certM.trustCategory;
        final MoObject trustCategoryMoObj = readerService.getMoObjectByFdn(trustCategoryFdn);
        if (trustCategoryMoObj == null) {
            final String errorMessage = "Not found TrustCategory MO with FDN [" + trustCategoryFdn + "]";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new MissingMoException(node.getName(), trustCategoryMo.type());
        }

        // Read attribute from TrustCategory MO and remove the reference to the
        // Trusted Certificate to remove
        final List<String> readTrustedCertificatesList = trustCategoryMoObj.getAttribute(TRUST_CATEGORY_TRUSTED_CERTIFICATES);
        nscsLogger.info(task, "Read trustedCertificates [" + readTrustedCertificatesList + "]");
        final List<String> trustedCertificatesList = new ArrayList<String>();
        if (readTrustedCertificatesList != null && !readTrustedCertificatesList.isEmpty()) {
            trustedCertificatesList.addAll(readTrustedCertificatesList);
        }

        String trustedCertificateFdn = null;
        boolean isTrustedCertificateToBeRemoved = false;
        final Mo trustedCertificateMo = targetRootMo.systemFunctions.secM.certM.trustedCertificate;
        final String reservedByMoAttribute = comEcimNodeUtility.getTrustedCertificateReservedByMoAttribute(normNode);
        nscsLogger.info(task, "The node supports {} as TrustedCertificate reserved-by MO attribute", reservedByMoAttribute);
        final Iterator<String> it = trustedCertificatesList.iterator();
        while (it.hasNext()) {
            final String currentTrustedCertificateFdn = it.next();
            final MoObject trustedCertificateMoObj = readerService.getMoObjectByFdn(currentTrustedCertificateFdn);
            if (trustedCertificateMoObj == null) {
                final String errorMessage = "Not found TrustedCertificate MO with FDN [" + currentTrustedCertificateFdn + "]";
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new MissingMoException(node.getName(), trustedCertificateMo.type());
            }

            final Map<String, Object> certificateContent = (Map<String, Object>) trustedCertificateMoObj.getAttribute(TRUSTED_CERTIFICATE_CONTENT);
            if (certificateContent != null) {
                final String currentCertSN = (String) certificateContent.get(CertificateContent.SERIAL_NUMBER);
                final String currentCertIssuer = (String) certificateContent.get(CertificateContent.ISSUER);
                nscsLogger.info(task, "On node : TrustedCertificate [" + currentTrustedCertificateFdn + "] : SN [" + currentCertSN + "] ISSUER ["
                        + currentCertIssuer + "]");
                if (CertDetails.matchesSN(certSN, currentCertSN) && CertDetails.matchesNotAlignedToRfcDN(certIssuer, currentCertIssuer)) {
                    trustedCertificateFdn = currentTrustedCertificateFdn;
                    final List<String> reservedByMoAttributeValue = (List<String>) trustedCertificateMoObj.getAttribute(reservedByMoAttribute);
                    nscsLogger.info(task,
                            "FOUND TrustedCertificate FDN [{}] : {} [{}]", trustedCertificateFdn, reservedByMoAttribute, reservedByMoAttributeValue);
                    reservedByMoAttributeValue.remove(trustCategoryFdn);
                    if (reservedByMoAttributeValue.isEmpty()) {
                        isTrustedCertificateToBeRemoved = true;
                        nscsLogger.info(task, "Found TrustedCertificate FDN [" + trustedCertificateFdn + "] can be removed");
                    } else {
                        nscsLogger.info(task, "Found TrustedCertificate FDN [" + trustedCertificateFdn
                                + "] cannot be removed since it is reserved by other trust category");
                    }
                    break;
                }
            } else {
                nscsLogger.error(task, "NULL CertificateContent for TrustedCertificate [" + currentTrustedCertificateFdn + "]");
            }
        }

        if (trustedCertificateFdn != null) {
            /**
             * Given the TrustedCertificate MO to be deleted, we need to remove its reference in the involved TrustCategory and update this MO. Then
             * we can delete the TrustedCertificate MO, invoking CertM specific action.
             */
            trustedCertificatesList.remove(trustedCertificateFdn);
            nscsLogger.info(task, "Removing reference to TrustedCertificate [" + trustedCertificateFdn + "] from TrustCategory [" + trustCategoryFdn
                    + "] trustedCertificates attribute : new value [" + trustedCertificatesList + "]");
            updateTrustCategory(task, trustCategoryFdn, trustedCertificatesList);
        } else {
            final String errorMessage = "Not found installed TrustedCertificate MO with SN [" + certSN + "] and issuer [" + certIssuer + "]";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        if (isTrustedCertificateToBeRemoved) {
            return removeTrustedCertificate(task, trustedCertificateFdn, outputParams);
        } else {
            return doNotRemoveTrustedCertificate(task, trustedCertificateFdn, outputParams);
        }

    }

    /**
     * @param task
     * @param trustCategoryFdn
     * @param trustedCertificates
     * @throws UnexpectedErrorException
     */
    private void updateTrustCategory(final ComEcimCheckRemoveTrustTask task, final String trustCategoryFdn, final List<String> trustedCertificates)
            throws UnexpectedErrorException {

        final NscsCMWriterService.WriterSpecificationBuilder specification = writerService.withSpecification();
        specification.setNotNullAttribute(TRUST_CATEGORY_TRUSTED_CERTIFICATES, trustedCertificates);
        specification.setFdn(trustCategoryFdn);
        final String updateMessage = NscsLogger.stringifyUpdateParams("TrustCategory", trustCategoryFdn);
        nscsLogger.info(task, "Updating " + updateMessage);
        try {
            specification.updateMO();
        } catch (final Exception e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while updating " + updateMessage;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        nscsLogger.workFlowTaskHandlerOngoing(task, "Successfully updated " + updateMessage);
    }

    /**
     *
     * @param task
     * @param trustedCertificateFdn
     * @param outputParams
     * @return
     */
    private String removeTrustedCertificate(final ComEcimCheckRemoveTrustTask task, final String trustedCertificateFdn,
            final Map<String, Serializable> outputParams) {
        final String state = REMOVE;
        return serializeResult(task, state, trustedCertificateFdn, outputParams);

    }

    /**
     *
     * @param task
     * @param trustedCertificateFdn
     * @param outputParams
     * @return
     */
    private String doNotRemoveTrustedCertificate(final ComEcimCheckRemoveTrustTask task, final String trustedCertificateFdn,
            final Map<String, Serializable> outputParams) {
        final String state = DO_NOT_REMOVE;
        return serializeResult(task, state, trustedCertificateFdn, outputParams);

    }

    /**
     *
     * @param task
     * @param result
     * @param trustedCertificateFdn
     * @param outputParams
     * @return
     */
    private String serializeResult(final ComEcimCheckRemoveTrustTask task, final String result, final String trustedCertificateFdn,
            Map<String, Serializable> outputParams) {
        String encodedWfQueryTaskResult = null;
        if (outputParams == null) {
            nscsLogger.info(task, "Initializing output params!");
            outputParams = new HashMap<String, Serializable>();
        }
        outputParams.put(WorkflowOutputParameterKeys.TRUSTED_CERTIFICATE_FDN.toString(), trustedCertificateFdn);
        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(result, outputParams);

        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while serializing output params";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        final String successMessage = "Successfully completed : remove TrustCertificate [" + trustedCertificateFdn + "] state is [" + result + "]";
        if (DO_NOT_REMOVE.equals(result)) {
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage, NscsLogger.DO_NOT_REMOVE);
        } else {
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        }
        return encodedWfQueryTaskResult;
    }

}
