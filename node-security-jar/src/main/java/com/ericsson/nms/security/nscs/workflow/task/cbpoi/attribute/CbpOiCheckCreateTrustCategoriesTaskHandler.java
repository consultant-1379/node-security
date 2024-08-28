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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.NscsCbpOiTrustedEntityInfo;
import com.ericsson.nms.security.nscs.utilities.CbpOiMoNaming;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiCheckCreateTrustCategoriesTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;
import com.ericsson.oss.services.security.nscs.dps.NscsDpsUtils;

import javax.ejb.Local;

@WFTaskType(WorkflowTaskType.CBPOI_CHECK_CREATE_TRUST_CATEGORIES)
@Local(WFTaskHandlerInterface.class)
public class CbpOiCheckCreateTrustCategoriesTaskHandler implements WFQueryTaskHandler<CbpOiCheckCreateTrustCategoriesTask>, WFTaskHandlerInterface {

    private static final String PASSED = "PASSED";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsDpsUtils nscsDpsUtils;

    @Override
    public String processTask(CbpOiCheckCreateTrustCategoriesTask checkCreateTrustCategoriesTask) {
        nscsLogger.workFlowTaskHandlerStarted(checkCreateTrustCategoriesTask);

        final String successMessage = "Successfully checked and created Certificates objects for Trust Categories";
        final NodeReference node = checkCreateTrustCategoriesTask.getNode();
        final NormalizableNodeReference normNode = readerService.getNormalizableNodeReference(node);
        final String nodeName = node.getName();
        nscsLogger.info(checkCreateTrustCategoriesTask, "From task : node [{}]", nodeName);

        /*
         * Extract output parameters possibly set by previous handlers
         */
        final Map<String, Serializable> outputParams = checkCreateTrustCategoriesTask.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Output params not yet set! ";
            nscsLogger.workFlowTaskHandlerFinishedWithError(checkCreateTrustCategoriesTask, errorMessage);
            throw new WorkflowTaskException(errorMessage);
        }

        List<NscsCbpOiTrustedEntityInfo> trustedEntitiesInfo;
        try {
            final String serializedTrustedEntitiesInfo = (String) outputParams.get(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString());

            trustedEntitiesInfo = NscsObjectSerializer.readObject(serializedTrustedEntitiesInfo);

            final ManagedObject truststoreManagedObject = checkAndCreateTruststore(checkCreateTrustCategoriesTask, normNode);

            // Create "Certificates" objects relative to Trust Categories
            checkAndCreateTrustCategories(checkCreateTrustCategoriesTask, normNode, trustedEntitiesInfo, truststoreManagedObject);
        } catch (Exception e) {
            final String errorMessage = NscsLogger.stringifyException(e);
            nscsLogger.workFlowTaskHandlerFinishedWithError(checkCreateTrustCategoriesTask, errorMessage);
            throw e;
        }

        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(checkCreateTrustCategoriesTask, successMessage);
        return serializeResult(checkCreateTrustCategoriesTask, trustedEntitiesInfo, outputParams);
    }

    /**
     * Checks the presence of truststore MO for the given node and, if not present, creates it.
     * 
     * @param task
     *            the task.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @return the truststore MO.
     */
    private ManagedObject checkAndCreateTruststore(final CbpOiCheckCreateTrustCategoriesTask task,
            final NormalizableNodeReference normalizableNodeRef) {

        final String moType = ModelDefinition.TRUSTSTORE_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_TRUSTSTORE_NS;
        final String moName = CbpOiMoNaming.getName(moType);
        ManagedObject truststoreMO = nscsDpsUtils.getNodeHierarchyTopMo(normalizableNodeRef, refMimNs, moType, moName);
        if (truststoreMO == null) {
            truststoreMO = nscsDpsUtils.createNodeHierarchyTopMo(normalizableNodeRef, refMimNs, moType, moName);
            nscsLogger.info(task, "Successfully created MO [{}]", truststoreMO.getFdn());
        }
        return truststoreMO;
    }

    /**
     * Checks the presence of trust category MOs for the given node and the given trusted entities and, if not present, creates them under the given
     * truststore parent MO.
     * 
     * @param task
     *            the task.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param trustedEntitiesInfo
     *            the list of trusted entities.
     * @param truststoreMO
     *            the parent MO.
     */
    private void checkAndCreateTrustCategories(final CbpOiCheckCreateTrustCategoriesTask task, final NormalizableNodeReference normNode,
            List<NscsCbpOiTrustedEntityInfo> trustedEntitiesInfo, final ManagedObject truststoreMO) {

        if (!trustedEntitiesInfo.isEmpty()) {
            for (NscsCbpOiTrustedEntityInfo trustedEntityInfo : trustedEntitiesInfo) {
                ManagedObject trustCategoryMO = nscsDpsUtils.getChildMo(truststoreMO, normNode,
                        ModelDefinition.TRUSTSTORE_CERTIFICATES_TYPE, trustedEntityInfo.getTrustCategoryName());
                if (trustCategoryMO == null) {
                    final Map<String, Object> certificatesAttributes = new HashMap<>();
                    certificatesAttributes.put(ModelDefinition.TRUSTSTORE_CERTIFICATES_DESCRIPTION_ATTR,
                            trustedEntityInfo.getTrustCategoryName() + " Trust Category");
                    trustCategoryMO = nscsDpsUtils.createChildMo(truststoreMO, normNode, ModelDefinition.CBP_OI_TRUSTSTORE_NS,
                            ModelDefinition.TRUSTSTORE_CERTIFICATES_TYPE, trustedEntityInfo.getTrustCategoryName(), certificatesAttributes);
                    nscsLogger.info(task, "Successfully created MO [{}] with attrs [{}]", trustCategoryMO.getFdn(), certificatesAttributes);
                }
                //Update Trust Category FDN in TrustedEntityInfo
                trustedEntityInfo.setTrustCategoryFdn(trustCategoryMO.getFdn());
            }
        }
    }

    private String serializeResult(final CbpOiCheckCreateTrustCategoriesTask task, final List<NscsCbpOiTrustedEntityInfo> trustedEntitiesInfo,
            final Map<String, Serializable> outputParams) {

        Map<String, Serializable> outParams = outputParams;
        String serializedWorkflowTaskResult;
        /*
         * Serialize trusted entities info in output parameters
         */
        String serializedTrustedEntitiesInfo = null;
        try {
            serializedTrustedEntitiesInfo = NscsObjectSerializer.writeObject(trustedEntitiesInfo);
        } catch (final IOException e1) {
            final String errorMessage = NscsLogger.stringifyException(e1) + " while serializing trusted entities info";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        // Update trusted entities in output params
        outParams.put(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString(), serializedTrustedEntitiesInfo);
        final String taskResult = PASSED;
        final WorkflowQueryTaskResult workflowTaskResult = new WorkflowQueryTaskResult(taskResult, outParams);

        try {
            serializedWorkflowTaskResult = NscsObjectSerializer.writeObject(workflowTaskResult);
        } catch (final IOException exc) {
            final String errorMessage = NscsLogger.stringifyException(exc) + " while serializing output params";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        final String successMessage = "Successfully checked and created Trust Categories";
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        return serializedWorkflowTaskResult;
    }

}
