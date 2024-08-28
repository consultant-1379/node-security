/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.util.NscsCbpOiTrustedEntityInfo;
import com.ericsson.nms.security.nscs.utilities.CbpOiMoNaming;
import com.ericsson.nms.security.nscs.utilities.NscsCbpOiNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiCheckCertificatesAlreadyInstalledTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;
import com.ericsson.oss.services.security.nscs.dps.NscsDpsUtils;
import javax.ejb.Local;


@WFTaskType(WorkflowTaskType.CBPOI_CHECK_CERTIFICATES_ALREADY_INSTALLED)
@Local(WFTaskHandlerInterface.class)
public class CbpOiCheckCertificatesAlreadyInstalledTaskHandler implements WFQueryTaskHandler<CbpOiCheckCertificatesAlreadyInstalledTask>, WFTaskHandlerInterface {

    private static final String NOT_ALL_INSTALLED = "NOT_ALL_INSTALLED";
    private static final String INSTALLED = "INSTALLED";

    @Inject
    NscsLogger nscsLogger;

    @Inject
    NscsCMReaderService readerService;

    @Inject
    NscsCapabilityModelService nscsCapabilityModelService;

    @Inject
    NscsCbpOiNodeUtility nscsCbpOiNodeUtility;

    @Inject
    NscsDpsUtils nscsDpsUtils;

    @Override
    public String processTask(final CbpOiCheckCertificatesAlreadyInstalledTask checkCertificatesTask) {

        nscsLogger.workFlowTaskHandlerStarted(checkCertificatesTask);

        final NodeReference node = checkCertificatesTask.getNode();
        final NormalizableNodeReference normNode = readerService.getNormalizableNodeReference(node);
        final String nodeName = node.getName();
        nscsLogger.info(checkCertificatesTask, "From task : node [{}]", nodeName);

        /*
         * Extract output parameters possibly set by previous handlers
        */
        final Map<String, Serializable> outputParams = checkCertificatesTask.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Output params not yet set! ";
            nscsLogger.workFlowTaskHandlerFinishedWithError(checkCertificatesTask, errorMessage);
            throw new WorkflowTaskException(errorMessage);
        }

        final String serializedTrustedEntitiesInfo = (String) outputParams.get(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString());

        Set<NscsCbpOiTrustedEntityInfo> toBeInstalledTrustedEntitiesInfo = NscsObjectSerializer.readObject(serializedTrustedEntitiesInfo);

        // Build set of trust categories names
        Set<String> trustCategoriesNames = new HashSet<>();
        for (NscsCbpOiTrustedEntityInfo trustedEntity : toBeInstalledTrustedEntitiesInfo) {
            trustCategoriesNames.add(trustedEntity.getTrustCategoryName());
        }

        /*
         * Get from node installed trust certificates for each Trust Category
         */

        // Get installed trust Map <key: TrustCategory, value: <key: certificate name, value: certificate details>>
        Map<String, Map<String, CertDetails>> nodeTrustCategoriesTrusts;
        try {
            nodeTrustCategoriesTrusts = getTrustCertificatesInstalledOnNode(checkCertificatesTask, normNode, trustCategoriesNames);
        } catch (final Exception e) {
            final String errorMessage = NscsLogger.stringifyException(e);
            nscsLogger.workFlowTaskHandlerFinishedWithError(checkCertificatesTask, errorMessage);
            throw e;
        }

        // Remove from Trusted Entities the entries relative to already installed certificates
        getFilteredTrustedEntitiesInfo(nodeTrustCategoriesTrusts, toBeInstalledTrustedEntitiesInfo);

        if (!toBeInstalledTrustedEntitiesInfo.isEmpty()) {

            final Set<NscsCbpOiTrustedEntityInfo> renamedTrustedEntities = updateTrustedEntitiesNames(checkCertificatesTask,
                    toBeInstalledTrustedEntitiesInfo, nodeTrustCategoriesTrusts);
            return trustNotInstalledOnNode(checkCertificatesTask, renamedTrustedEntities, outputParams);
        }

        outputParams.remove(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString());

        return trustAlreadyInstalledOnNode(checkCertificatesTask, outputParams);
    }

    /**
     * Gets the trusted certificates installed under the given trust categories of the given node.
     * 
     * A map is returned with key equal to trust category name, value equal to a map of trusted certificates.
     * 
     * The map of trusted certificates for a trust category has key equal to the certificate name and value equal to the certificate details.
     * 
     * @param task
     *            the task.
     * @param node
     *            the normalizable node reference.
     * @param trustCategories
     *            the set of trust categories names.
     * @return the map of installed trusted certificates.
     */
    private Map<String, Map<String, CertDetails>> getTrustCertificatesInstalledOnNode(final CbpOiCheckCertificatesAlreadyInstalledTask task,
            final NormalizableNodeReference node, final Set<String> trustCategories) {

        final Map<String, Map<String, CertDetails>> nodeTrustCertificatesForCategories = new HashMap<>();

        final String moType = ModelDefinition.TRUSTSTORE_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_TRUSTSTORE_NS;
        final ManagedObject truststoreMO = nscsDpsUtils.getNodeHierarchyTopMo(node, refMimNs, moType, CbpOiMoNaming.getName(moType));
        if (truststoreMO != null) {
            nscsLogger.info(task, "Found MO [{}] of type [{}] for node [{}]", truststoreMO.getFdn(), moType, node);

            for (final String trustCategory : trustCategories) {
                final ManagedObject certificatesMO = nscsDpsUtils.getChildMo(truststoreMO, node, ModelDefinition.TRUSTSTORE_CERTIFICATES_TYPE,
                        trustCategory);
                if (certificatesMO != null) {
                    final Map<String, CertDetails> nodeTrustCategoryCertificates = getCertDetailsOfTrustCategory(task, node, certificatesMO);
                    if (!nodeTrustCategoryCertificates.isEmpty()) {
                        nodeTrustCertificatesForCategories.put(trustCategory, nodeTrustCategoryCertificates);
                    }
                    nscsLogger.info(task, "Trust category [{}] contains trusts [{}]", trustCategory, nodeTrustCategoryCertificates.keySet());
                }
            }
        }
        return nodeTrustCertificatesForCategories;
    }

    /**
     * Gets the trusted certificates installed under the given trust category MO of the given node.
     * 
     * A map is returned with key equal to the certificate name and value equal to the certificate details.
     * 
     * @param task
     *            the task.
     * @param node
     *            the normalizable node reference.
     * @param certificatesMO
     *            the trust category MO.
     * @return the map of installed trusted certificates.
     */
    private Map<String, CertDetails> getCertDetailsOfTrustCategory(final CbpOiCheckCertificatesAlreadyInstalledTask task,
            final NormalizableNodeReference node, final ManagedObject certificatesMO) {

        Map<String, CertDetails> trustCategoryCertDetails = new HashMap<>();

        // Get trusted certificates under trust category
        final String moType = ModelDefinition.TRUSTSTORE_CERTIFICATE_TYPE;
        final List<ManagedObject> certificateMOs = nscsDpsUtils.getChildMos(certificatesMO, node, moType);
        for (final ManagedObject certificateMO : certificateMOs) {
            final String cert = certificateMO.getAttribute(ModelDefinition.TRUSTSTORE_CERTIFICATE_CERT_ATTR);
            try {
                final X509Certificate x509Certificate = nscsCbpOiNodeUtility.convertToX509Cert(cert);
                final String certName = certificateMO.getAttribute(ModelDefinition.TRUSTSTORE_CERTIFICATE_NAME_ATTR);
                trustCategoryCertDetails.put(certName, new CertDetails(x509Certificate));
            } catch (final CertificateException e) {
                final String errorMessage = String.format("%s while converting certificate PEM to X509", NscsLogger.stringifyException(e));
                nscsLogger.error(task, errorMessage, e);
            }
        }
        return trustCategoryCertDetails;
    }

    private Set<NscsCbpOiTrustedEntityInfo> getFilteredTrustedEntitiesInfo(final Map<String, Map<String, CertDetails>> nodeTrustCategoriesTrustCerts,
                                                                           final Set<NscsCbpOiTrustedEntityInfo> toBeInstalledTrustedEntities) {
        NscsCbpOiTrustedEntityInfo toBeRemovedTrustedEntityInfo;
        String trustCategoryName;
        for (Map.Entry<String, Map<String, CertDetails>> trustCategoryTrustsEntry : nodeTrustCategoriesTrustCerts.entrySet()) { // For all trust categories on node
            trustCategoryName = trustCategoryTrustsEntry.getKey();
            for (CertDetails nodeCertDetails : trustCategoryTrustsEntry.getValue().values()) { // For all node certificates for Trust Category
                toBeRemovedTrustedEntityInfo = getAlreadyInstalledTrustedEntity(trustCategoryName, toBeInstalledTrustedEntities, nodeCertDetails);
                if (toBeRemovedTrustedEntityInfo != null) {
                    nscsLogger.info("To be installed trusts: removing certificate [{}]", toBeRemovedTrustedEntityInfo.getName());
                    toBeInstalledTrustedEntities.remove(toBeRemovedTrustedEntityInfo);
                }
                if (toBeInstalledTrustedEntities.isEmpty()) {
                    break;
                }
            }
        }
        return toBeInstalledTrustedEntities;
    }

    private NscsCbpOiTrustedEntityInfo getAlreadyInstalledTrustedEntity(final String trustCategory,
                                                                        final Set<NscsCbpOiTrustedEntityInfo> toBeInstalledTrustedEntities,
                                                                        final CertDetails alreadyInstalledTrustCertificate) {
        NscsCbpOiTrustedEntityInfo alreadyInstalledTrustedEntity = null;
        for (NscsCbpOiTrustedEntityInfo trustedEntityInfo : toBeInstalledTrustedEntities) {  // For all trusted entities to be installed
            if (trustedEntityInfo.getTrustCategoryName().equals(trustCategory) &&
                    trustedEntityInfo.getIssuer().equals(alreadyInstalledTrustCertificate.getIssuer()) &&
                    trustedEntityInfo.getSerialNumber().equals(alreadyInstalledTrustCertificate.getSerial())) {
                alreadyInstalledTrustedEntity = trustedEntityInfo;
                break;
            }
        }
        return alreadyInstalledTrustedEntity;
    }

    private String getTrustCertificateUniqueName(final String pkiCertName, Set<String> nodeTrustCategoryCertNames) {
        Integer suffix = 1;
        String newName = pkiCertName + '-' + suffix.toString();
        if (nodeTrustCategoryCertNames != null) {
            boolean searchEqualName;
            do {
                searchEqualName = false;
                for (String nodeCertName : nodeTrustCategoryCertNames) {
                    if (newName.equals(nodeCertName)) {
                        ++suffix;
                        newName = pkiCertName + '-' + suffix.toString();
                        searchEqualName = true;
                        break;
                    }
                }
            } while (searchEqualName);
        }
        return newName;
    }

    private Set<NscsCbpOiTrustedEntityInfo> updateTrustedEntitiesNames(final CbpOiCheckCertificatesAlreadyInstalledTask task,
                                                                       final Set<NscsCbpOiTrustedEntityInfo> toBeInstalledTrustedEntitiesInfo,
                                                                       final Map<String, Map<String, CertDetails>> installedTrustCategoriesCerts) {
        // Manage duplicated certificate names already present on node
        Set<String> nodeInstalledCertNames;
        for (NscsCbpOiTrustedEntityInfo trustedEntityInfo : toBeInstalledTrustedEntitiesInfo) {
            Map<String, CertDetails> nodeTrustCategoryCerts = installedTrustCategoriesCerts.get(trustedEntityInfo.getTrustCategoryName());
            nodeInstalledCertNames = (nodeTrustCategoryCerts != null) ? nodeTrustCategoryCerts.keySet() : null;
            String newName = getTrustCertificateUniqueName(trustedEntityInfo.getName(), nodeInstalledCertNames);
            if (!newName.equals(trustedEntityInfo.getName())) {
                nscsLogger.info(task, "Change trust cert name from [{}] to [{}]",
                        trustedEntityInfo.getName(), newName);
                trustedEntityInfo.setName(newName);
            }
        }
        return toBeInstalledTrustedEntitiesInfo;
    }

    private String trustAlreadyInstalledOnNode(final CbpOiCheckCertificatesAlreadyInstalledTask task,
                                               final Map<String, Serializable> outPutParams) {
        final String state = INSTALLED;
        return getSerializedWorkflowTaskResult(task, state, outPutParams);
    }

    private String trustNotInstalledOnNode(final CbpOiCheckCertificatesAlreadyInstalledTask task,
                                           final Set<NscsCbpOiTrustedEntityInfo> trustedEntitiesInfo,
                                           final Map<String, Serializable> outPutParams) {
        final String state = NOT_ALL_INSTALLED;
        final List<NscsCbpOiTrustedEntityInfo> trustedEntitiesInfoList = new ArrayList<>();
        trustedEntitiesInfoList.addAll(trustedEntitiesInfo);
        return serializeResult(task, state, trustedEntitiesInfoList, outPutParams);
    }

    private String serializeResult(final CbpOiCheckCertificatesAlreadyInstalledTask task, final String result,
                                   final List<NscsCbpOiTrustedEntityInfo> trustedEntitiesInfo,
                                   final Map<String, Serializable> outPutParams) {
        // Build trusted entities list for logging
        final List<NscsCbpOiTrustedEntityInfo> loggedTrustedEntitiesInfo = new ArrayList<>();
        for (final NscsCbpOiTrustedEntityInfo trustInfo : trustedEntitiesInfo) {
            NscsCbpOiTrustedEntityInfo loggedTrustInfo = new NscsCbpOiTrustedEntityInfo(trustInfo);
            loggedTrustInfo.setBase64PemCertificate("Hidden PEM certificate");  // Hide certificate
            loggedTrustedEntitiesInfo.add(loggedTrustInfo);
        }
        nscsLogger.info(task, "Serializing trusted entities info [{}]", (Object) loggedTrustedEntitiesInfo);
        /*
         * Serialize trusted entities info in output parameters
         */
        String serializedTrustedEntitiesInfo;
        try {
            serializedTrustedEntitiesInfo = NscsObjectSerializer.writeObject(trustedEntitiesInfo);
        } catch (final IOException e1) {
            final String errorMessage = NscsLogger.stringifyException(e1) + " while serializing trusted entities info";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        outPutParams.put(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString(), serializedTrustedEntitiesInfo);

        return getSerializedWorkflowTaskResult(task, result, outPutParams);
    }

    private String getSerializedWorkflowTaskResult(final CbpOiCheckCertificatesAlreadyInstalledTask task,
                                                   final String taskResult, final Map<String, Serializable> outputParams){
        final WorkflowQueryTaskResult workflowTaskResult = new WorkflowQueryTaskResult(taskResult, outputParams);
        nscsLogger.debug(task, "Serializing result [{}]", taskResult);
        String serializedWorkflowTaskResult;
        try {
            serializedWorkflowTaskResult = NscsObjectSerializer.writeObject(workflowTaskResult);
        } catch (final IOException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while serializing output params";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        final String message = "Successfully completed : trusted certificates installation state is [" + taskResult + "]";
        if (INSTALLED.equals(taskResult)) {
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, message, NscsLogger.ALREADY_INSTALLED);
        } else {
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, message);
        }
        return serializedWorkflowTaskResult;
    }
}
