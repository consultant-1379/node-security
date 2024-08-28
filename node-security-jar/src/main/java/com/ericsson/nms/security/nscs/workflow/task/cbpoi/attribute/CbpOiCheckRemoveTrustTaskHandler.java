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
package com.ericsson.nms.security.nscs.workflow.task.cbpoi.attribute;

import java.io.IOException;
import java.io.Serializable;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.utilities.NscsCbpOiNodeUtility;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiCheckRemoveTrustTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;
import com.ericsson.oss.services.security.nscs.dps.NscsDpsUtils;

@WFTaskType(WorkflowTaskType.CBP_OI_CHECK_REMOVE_TRUST)
@Local(WFTaskHandlerInterface.class)
public class CbpOiCheckRemoveTrustTaskHandler implements WFQueryTaskHandler<CbpOiCheckRemoveTrustTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsCbpOiNodeUtility nscsCbpOiNodeUtility;

    @Inject
    private NscsDpsUtils nscsDpsUtils;

    @Override
    public String processTask(final CbpOiCheckRemoveTrustTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        final String trustCategory = NscsNodeUtility.getCertificateTypeFromTrustedCertCategory(task.getCertCategory());
        nscsLogger.info(task, "From task : trust category type [{}]", trustCategory);
        final NodeReference node = task.getNode();
        final NormalizableNodeReference normalizableNode = readerService.getNormalizableNodeReference(node);
        final String certificateSerialNumber = task.getCertificateSerialNumber();
        final String certificateIssuer = task.getIssuer();
        nscsLogger.info(task, "From task : serial number [{}] issuer [{}]", certificateSerialNumber, certificateIssuer);
        String errorMessage = null;
        if (certificateSerialNumber == null || certificateSerialNumber.isEmpty()) {
            errorMessage = "Can't remove trusted certificate : null or empty serial number";
        } else if (certificateIssuer == null || certificateIssuer.isEmpty()) {
            errorMessage = "Can't remove trusted certificate : null or empty issuer";
        }
        if (errorMessage != null) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            nscsLogger.info(task, "Output params not yet set!");
        }
        final List<String> toBeRemovedTrustedCertificateFdns = getToBeRemovedTrustedCertificateFdns(task, normalizableNode, trustCategory,
                certificateSerialNumber, certificateIssuer);

        if (toBeRemovedTrustedCertificateFdns.isEmpty()) {
            errorMessage = String.format("No trusted certificate found on the node with serialNumber [%s] and issuer [%s]", certificateSerialNumber,
                    certificateIssuer);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        return removeTrustedCertificates(task, toBeRemovedTrustedCertificateFdns, outputParams);
    }

    /**
     * Gets the list of FDNs of trusted certificate MOs of given serial number and issuer to be removed.
     * 
     * The list can contain more than one FDN since the same trusted certificate can be present under different trust categories.
     * 
     * @param task
     *            the task.
     * @param normalizableNode
     *            the normalizable node reference.
     * @param trustCategory
     *            the trust category type.
     * @param serialNumber
     *            the certificate serial number.
     * @param issuer
     *            the certificate issuer.
     * @return the list of FDNs of trusted certificate MOs to be removed. The list can be empty if no trusted certificate found.
     */
    private List<String> getToBeRemovedTrustedCertificateFdns(final CbpOiCheckRemoveTrustTask task, final NormalizableNodeReference normalizableNode,
            final String trustCategory, final String serialNumber, String issuer) {

        final List<String> toBeRemovedTrustedCertificateFdns = new ArrayList<>();
        final List<String> trustCategoryNames = nscsCbpOiNodeUtility.getTrustCategoryNames(normalizableNode, trustCategory);
        for (final String trustCategoryName : trustCategoryNames) {
            getToBeRemovedTrustedCertificateFdnsInTrustCategory(task, normalizableNode, trustCategoryName, serialNumber, issuer,
                    toBeRemovedTrustedCertificateFdns);
        }
        return toBeRemovedTrustedCertificateFdns;
    }

    /**
     * Gets the list of FDNs of trusted certificate MOs of given serial number and issuer in the trust category of given name to be removed.
     * 
     * @param task
     *            the task.
     * @param normalizableNode
     *            the normalizable node reference.
     * @param trustCategoryName
     *            the trust category name.
     * @param serialNumber
     *            the certificate serial number.
     * @param issuer
     *            the certificate issuer.
     * @param toBeRemovedTrustedCertificateFdns
     *            the list of FDNs of trusted certificate MOs to be removed.
     */
    private void getToBeRemovedTrustedCertificateFdnsInTrustCategory(final CbpOiCheckRemoveTrustTask task,
            final NormalizableNodeReference normalizableNode, final String trustCategoryName, final String serialNumber, String issuer,
            final List<String> toBeRemovedTrustedCertificateFdns) {

        final ManagedObject trustCategoryMO = nscsDpsUtils.getCertificatesMO(normalizableNode, trustCategoryName);
        if (trustCategoryMO != null) {
            final List<ManagedObject> trustedCertificateMOs = nscsDpsUtils.getChildMos(trustCategoryMO, normalizableNode,
                    ModelDefinition.TRUSTSTORE_CERTIFICATE_TYPE);
            for (final ManagedObject trustedCertificateMO : trustedCertificateMOs) {
                String certIssuer = null;
                String certSerialNumber = null;
                final String cert = trustedCertificateMO.getAttribute(ModelDefinition.TRUSTSTORE_CERTIFICATE_CERT_ATTR);
                try {
                    final X509Certificate x509Certificate = nscsCbpOiNodeUtility.convertToX509Cert(cert);
                    certIssuer = x509Certificate.getIssuerDN().getName();
                    certSerialNumber = x509Certificate.getSerialNumber().toString();
                    if (CertDetails.matchesSN(serialNumber, certSerialNumber) && CertDetails.matchesDN(issuer, certIssuer)) {
                        nscsLogger.info(task, "Found trusted certificate FDN [{}]", trustedCertificateMO.getFdn());
                        toBeRemovedTrustedCertificateFdns.add(trustedCertificateMO.getFdn());
                    }
                } catch (final CertificateException e) {
                    final String errorMessage = String.format("%s while converting certificate into x509 format", NscsLogger.stringifyException(e));
                    nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                    throw new UnexpectedErrorException(errorMessage);
                }
            }
        } else {
            final String errMsg = String.format("trust category MO with name [%s] not found for normalizableNode [%s]", trustCategoryName,
                    normalizableNode);
            nscsLogger.info(task, errMsg);
        }
    }

    /**
     * Serializes the state and outputParams to be passed to next task handlers.
     * 
     * @param task
     *            the task.
     * @param toBeRemovedTrustedCertificateFdns
     *            the list of FDNs of trusted certificate MOs to be removed.
     * @param outputParams
     *            the workflow outputParams to be set and sent to next task handlers.
     * @return the serialized state and outputParams to be passed to next task handlers.
     */
    private String removeTrustedCertificates(final CbpOiCheckRemoveTrustTask task, final List<String> toBeRemovedTrustedCertificateFdns,
            final Map<String, Serializable> outputParams) {
        final String state = "REMOVE";
        return serializeResult(task, state, toBeRemovedTrustedCertificateFdns, outputParams);

    }

    /**
     * Serializes the given state and outputParams to be passed to next task handlers.
     * 
     * @param task
     *            the task.
     * @param result
     *            the task handler result.
     * @param toBeRemovedTrustedCertificateFdns
     *            the list of FDNs of trusted certificate MOs to be removed.
     * @param outputParams
     *            the workflow outputParams to be set and sent to next task handlers.
     *
     * @return the encoded result of the task handler.
     */
    private String serializeResult(final CbpOiCheckRemoveTrustTask task, final String result, final List<String> toBeRemovedTrustedCertificateFdns,
            Map<String, Serializable> outputParams) {
        String encodedWorkflowQueryTaskResult = null;
        if (outputParams == null) {
            nscsLogger.info(task, " Initializing output params! ");
            outputParams = new HashMap<>();
        }
        /*
         * Serialize CertificateMoFdn's in output parameters
         */
        String serializedToBeRemovedTrustedCertificateFdns;
        try {
            serializedToBeRemovedTrustedCertificateFdns = NscsObjectSerializer.writeObject(toBeRemovedTrustedCertificateFdns);
        } catch (final IOException e1) {
            final String errorMessage = String.format("%s while serializing to be removed trusted certificate FDNs",
                    NscsLogger.stringifyException(e1));
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        outputParams.put(WorkflowOutputParameterKeys.TRUSTED_CERTIFICATE_FDN.toString(), serializedToBeRemovedTrustedCertificateFdns);
        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(result, outputParams);

        try {
            encodedWorkflowQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessage = String.format("%s while serializing output params", NscsLogger.stringifyException(e));
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        final String successMessage = String.format("Successfully completed : remove Trusted Certificate FDNs [%s] state is [%s]",
                toBeRemovedTrustedCertificateFdns, result);
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        return encodedWorkflowQueryTaskResult;
    }

}
