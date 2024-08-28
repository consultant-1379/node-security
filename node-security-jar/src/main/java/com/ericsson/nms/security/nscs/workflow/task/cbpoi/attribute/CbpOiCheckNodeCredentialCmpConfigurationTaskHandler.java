/*-----------------------------------------------------------------------------
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
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.utilities.CbpOiMoNaming;
import com.ericsson.nms.security.nscs.utilities.NscsCbpOiNodeUtility;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiCheckNodeCredentialCmpConfigurationTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;
import com.ericsson.oss.services.security.nscs.dps.NscsDpsUtils;

/**
 * <p>
 * Task handler for WorkflowTaskType.CBP_OI_CHECK_NODE_CREDENTIAL_CMP_CONFIG.
 * </p>
 * <p>
 * Check the node credential CMP configuration if node credential MO is already present.
 * </p>
 * <p>
 * Required incoming internal parameters:
 * <ul>
 * <li>ENROLLMENT_INFO</li>
 * <li>CMP_SERVER_GROUP_NAME</li>
 * </ul>
 * </p>
 * <p>
 * Produced outgoing internal parameters:
 * <ul>
 * <li>ASYMMETRIC_KEYS_CMP_FDN : always</li>
 * <li>ASYMMETRIC_KEY_CMP_FDN : if asymmetric-key cmp MO is already present</li>
 * <li>ASYMMETRIC_KEY_NAME : always</li>
 * <li>IS_START_CMP_REQUIRED : if asymmetric-key cmp MO is not yet present or if asymmetric-key cmp MO is already present but with unexpected
 * attributes cmp-server-group or trusted-certs</li>
 * <li>RENEWAL_MODE : always, automatic if asymmetric-key cmp MO is not yet present or the value present on existent asymmetric-key cmp MO</li>
 * <li>TRUSTED_CERTS_NAME : always</li>
 * </ul>
 * </p>
 */
@WFTaskType(WorkflowTaskType.CBP_OI_CHECK_NODE_CREDENTIAL_CMP_CONFIG)
@Local(WFTaskHandlerInterface.class)
public class CbpOiCheckNodeCredentialCmpConfigurationTaskHandler implements WFQueryTaskHandler<CbpOiCheckNodeCredentialCmpConfigurationTask>, WFTaskHandlerInterface {

    private static final String PASSED = "PASSED";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCbpOiNodeUtility nscsCbpOiNodeUtility;

    @Inject
    private NscsDpsUtils nscsDpsUtils;

    @Inject
    private NscsCMReaderService readerService;

    @Override
    public String processTask(final CbpOiCheckNodeCredentialCmpConfigurationTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        final NormalizableNodeReference normalizableNodeRef = readerService.getNormalizableNodeReference(task.getNode());

        // Extract output parameters that shall have been already set by previous handlers
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        String result = null;
        try {
            final String checkResult = checkNodeCredentialCmpConfiguration(task, normalizableNodeRef, outputParams);
            result = serializeCheckNodeCredentialCmpConfigurationResult(task, checkResult, outputParams);
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "Successfully completed", checkResult);
        } catch (final Exception e) {
            final String errorMessage = e.getMessage();
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw e;
        }
        return result;
    }

    /**
     * Checks the node credential CMP configuration if node credential MO is already present.
     * 
     * @param task
     *            the task.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param outputParams
     *            the output parameters.
     * @return the result of the check of the node credential CMP configuration.
     */
    private String checkNodeCredentialCmpConfiguration(final CbpOiCheckNodeCredentialCmpConfigurationTask task,
            final NormalizableNodeReference normalizableNodeRef, final Map<String, Serializable> outputParams) {

        final String certificateType = NscsNodeUtility.getCertificateTypeFromTrustedCertCategory(task.getTrustedCertCategory());
        nscsLogger.info(task, "From task : certificate type [{}]", certificateType);

        // Extract enrollment info from the output parameters
        final String serializedEnrollmentInfo = (String) outputParams.get(WorkflowOutputParameterKeys.ENROLLMENT_INFO.toString());
        final ScepEnrollmentInfoImpl enrollmentInfo = NscsObjectSerializer.readObject(serializedEnrollmentInfo);
        if (enrollmentInfo == null) {
            final String errorMessage = "Missing enrollment info internal parameter";
            throw new UnexpectedErrorException(errorMessage);
        }
        nscsLogger.info(task, "From output params : enrollment info [{}]", enrollmentInfo);

        final String cmpServerGroupName = (String) outputParams.get(WorkflowOutputParameterKeys.CMP_SERVER_GROUP_NAME.toString());
        if (cmpServerGroupName == null) {
            final String errorMessage = "Missing cmp-server-group name internal parameter";
            throw new UnexpectedErrorException(errorMessage);
        }
        nscsLogger.info(task, "From output params : cmp server group name [{}]", cmpServerGroupName);

        final Map<String, Serializable> nodeCredentialParamsMap = new HashMap<>();
        nodeCredentialParamsMap.put(WorkflowOutputParameterKeys.CMP_SERVER_GROUP_NAME.toString(), cmpServerGroupName);

        final ManagedObject asymmetricKeysCmpMO = checkKeystoreHierarchy(task, normalizableNodeRef, certificateType, enrollmentInfo, nodeCredentialParamsMap);

        updateOutputParams(task, asymmetricKeysCmpMO, nodeCredentialParamsMap, outputParams);

        return PASSED;
    }

    /**
     * Updates the output parameters.
     * 
     * @param task
     *            the task.
     * @param asymmetricKeyCmpMO
     *            the asymmetric-keys cmp MO.
     * @param nodeCredentialParamsMap
     *            map containing the parameters of node credential MO.
     * @param outputParams
     *            the output parameters.
     */
    private void updateOutputParams(final CbpOiCheckNodeCredentialCmpConfigurationTask task, final ManagedObject asymmetricKeysCmpMO,
            final Map<String, Serializable> nodeCredentialParamsMap, final Map<String, Serializable> outputParams) {

        outputParams.put(WorkflowOutputParameterKeys.ASYMMETRIC_KEYS_CMP_FDN.toString(), asymmetricKeysCmpMO.getFdn());

        nscsLogger.info(task, "Internal NSCS params [{}]", nodeCredentialParamsMap);

        updateOutputParam(WorkflowOutputParameterKeys.ASYMMETRIC_KEY_CMP_FDN, nodeCredentialParamsMap, outputParams);
        updateOutputParam(WorkflowOutputParameterKeys.ASYMMETRIC_KEY_NAME, nodeCredentialParamsMap, outputParams);
        updateOutputParam(WorkflowOutputParameterKeys.IS_START_CMP_REQUIRED, nodeCredentialParamsMap, outputParams);
        updateOutputParam(WorkflowOutputParameterKeys.RENEWAL_MODE, nodeCredentialParamsMap, outputParams,
                ModelDefinition.ASYMMETRIC_KEY_CMP_RENEWAL_MODE_AUTOMATIC);
        updateOutputParam(WorkflowOutputParameterKeys.TRUSTED_CERTS_NAME, nodeCredentialParamsMap, outputParams);
    }

    /**
     * Updates the given output parameter according to correspondent input parameter. If input parameter is not specified the parameter is not added
     * to output parameters.
     * 
     * @param paramKey
     *            the parameter to update.
     * @param inputParams
     *            the input parameters.
     * @param outputParams
     *            the output parameters.
     */
    private void updateOutputParam(final WorkflowOutputParameterKeys paramKey, final Map<String, Serializable> inputParams,
            final Map<String, Serializable> outputParams) {

        if (inputParams.containsKey(paramKey.toString())) {
            outputParams.put(paramKey.toString(), inputParams.get(paramKey.toString()));
        }
    }

    /**
     * Updates the given output parameter according to correspondent input parameter. If input parameter is not specified the parameter is added to
     * output parameters with a given default value.
     * 
     * @param paramKey
     *            the parameter to update.
     * @param inputParams
     *            the input parameters.
     * @param outputParams
     *            the output parameters.
     * @param defaultValue
     *            the default to be used if parameter is not specified in input parameters.
     */
    private void updateOutputParam(final WorkflowOutputParameterKeys paramKey, final Map<String, Serializable> inputParams,
            final Map<String, Serializable> outputParams, final Serializable defaultValue) {

        if (inputParams.containsKey(paramKey.toString())) {
            outputParams.put(paramKey.toString(), inputParams.get(paramKey.toString()));
        } else {
            outputParams.put(paramKey.toString(), defaultValue);
        }
    }

    /**
     * Checks the keystore hierarchy and creates the missing MOs for the given node reference.
     * 
     * The keystore MO shall be present at this step.
     * 
     * @param task
     *            the task.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param certificateType
     *            the certificate type.
     * @param enrollmentInfo
     *            the PKI enrollment info.
     * @param nodeCredentialParamsMap
     *            map containing the parameters of node credential MO.
     * @return the asymmetric-keys cmp MO.
     */
    private ManagedObject checkKeystoreHierarchy(final CbpOiCheckNodeCredentialCmpConfigurationTask task,
            final NormalizableNodeReference normalizableNodeRef, final String certificateType, final ScepEnrollmentInfoImpl enrollmentInfo,
            final Map<String, Serializable> nodeCredentialParamsMap) {

        ManagedObject asymmetricKeysCmpMO = null;

        final String moType = ModelDefinition.KEYSTORE_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_KEYSTORE_NS;
        final ManagedObject keystoreMO = nscsDpsUtils.getNodeHierarchyTopMo(normalizableNodeRef, refMimNs, moType, CbpOiMoNaming.getName(moType));
        if (keystoreMO != null) {
            asymmetricKeysCmpMO = checkAsymmetricKeysHierarchy(task, keystoreMO, normalizableNodeRef, certificateType, enrollmentInfo, nodeCredentialParamsMap);
        } else {
            final String errorMsg = String.format("Missing keystore MO for node %s", normalizableNodeRef.getName());
            nscsLogger.error(task, errorMsg);
            throw new MissingMoException(errorMsg);
        }
        return asymmetricKeysCmpMO;
    }

    /**
     * Checks the asymmetric-keys hierarchy and creates the missing MOs under the given parent MO for the given node reference.
     * 
     * @param task
     *            the task.
     * @param parentMo
     *            the parent MO.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param certificateType
     *            the certificate type.
     * @param enrollmentInfo
     *            the PKI enrollment info.
     * @param nodeCredentialParamsMap
     *            map containing the parameters of node credential MO.
     * @return the asymmetric-keys cmp MO.
     */
    private ManagedObject checkAsymmetricKeysHierarchy(final CbpOiCheckNodeCredentialCmpConfigurationTask task, final ManagedObject parentMo,
            final NormalizableNodeReference normalizableNodeRef, final String certificateType, final ScepEnrollmentInfoImpl enrollmentInfo,
            final Map<String, Serializable> nodeCredentialParamsMap) {

        ManagedObject asymmetricKeysCmpMO = null;

        final ManagedObject asymmetricKeysMO = nscsDpsUtils.getChildMo(parentMo, normalizableNodeRef, ModelDefinition.ASYMMETRIC_KEYS_TYPE,
                CbpOiMoNaming.getName(ModelDefinition.ASYMMETRIC_KEYS_TYPE));
        if (asymmetricKeysMO != null) {
            nscsLogger.info(task, "Already created asymmetric-keys MO under keystore");
            asymmetricKeysCmpMO = checkAsymmetricKeysCmp(task, asymmetricKeysMO, normalizableNodeRef);
            checkAsymmetricKeyHierarchy(task, asymmetricKeysMO, normalizableNodeRef, certificateType, enrollmentInfo, nodeCredentialParamsMap);
        } else {
            asymmetricKeysCmpMO = createAsymmetricKeysHierarchy(task, parentMo, normalizableNodeRef);
            nscsLogger.info(task, "Created asymmetric-keys hierarchy under keystore : start-cmp required");
            final String asymmetricKeyName = nscsCbpOiNodeUtility.getNodeCredentialName(normalizableNodeRef, certificateType);
            nodeCredentialParamsMap.put(WorkflowOutputParameterKeys.ASYMMETRIC_KEY_NAME.toString(), asymmetricKeyName);
            final String trustedCertsName = nscsCbpOiNodeUtility.getTrustCategoryName(normalizableNodeRef, certificateType);
            nodeCredentialParamsMap.put(WorkflowOutputParameterKeys.TRUSTED_CERTS_NAME.toString(), trustedCertsName);
            nodeCredentialParamsMap.put(WorkflowOutputParameterKeys.IS_START_CMP_REQUIRED.toString(), "TRUE");
            nodeCredentialParamsMap.put(WorkflowOutputParameterKeys.RENEWAL_MODE.toString(),
                    ModelDefinition.ASYMMETRIC_KEY_CMP_RENEWAL_MODE_AUTOMATIC);
        }

        return asymmetricKeysCmpMO;
    }

    /**
     * Checks the asymmetric-keys cmp MO under the given parent MO for the given node reference.
     * 
     * The absence of the asymmetric-keys cmp MO does not necessarily trigger the start-cmp action because this MO is a pure container whose creation
     * is not mediated to node. This implies that the MO is present only in DPS until a synchronization is performed; the synchronization deletes the
     * MO from DPS also. If MO is not present during enrollment, it is created and enrollment goes on.
     * 
     * @param task
     *            the task.
     * @param parentMo
     *            the parent MO.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @return the asymmetric-keys cmp MO.
     */
    private ManagedObject checkAsymmetricKeysCmp(final CbpOiCheckNodeCredentialCmpConfigurationTask task, final ManagedObject parentMo,
            final NormalizableNodeReference normalizableNodeRef) {

        final String unscopedMoType = ModelDefinition.CMP_TYPE;
        ManagedObject asymmetricKeysCmpMO = nscsDpsUtils.getChildMo(parentMo, normalizableNodeRef, unscopedMoType,
                CbpOiMoNaming.getName(unscopedMoType));
        if (asymmetricKeysCmpMO != null) {
            nscsLogger.info(task, "Already created cmp MO under asymmetric-keys");
        } else {
            asymmetricKeysCmpMO = createAsymmetricKeysCmp(task, parentMo, normalizableNodeRef);
            nscsLogger.info(task, "Created cmp MO under asymmetric-keys");
        }
        return asymmetricKeysCmpMO;
    }

    /**
     * Checks the asymmetric-key hierarchy under the given parent MO for the given node reference and certificate type.
     * 
     * @param task
     *            the task.
     * @param parentMo
     *            the parent MO.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param certificateType
     *            the certificate type.
     * @param enrollmentInfo
     *            the PKI enrollment info.
     * @param nodeCredentialParamsMap
     *            map containing the parameters of node credential MO.
     * @return the asymmetric-key cmp MO or null if not yet existing.
     */
    private ManagedObject checkAsymmetricKeyHierarchy(final CbpOiCheckNodeCredentialCmpConfigurationTask task, final ManagedObject parentMo,
            final NormalizableNodeReference normalizableNodeRef, final String certificateType, final ScepEnrollmentInfoImpl enrollmentInfo,
            final Map<String, Serializable> nodeCredentialParamsMap) {

        ManagedObject asymmetricKeyCmpMO = null;

        final String asymmetricKeyName = nscsCbpOiNodeUtility.getNodeCredentialName(normalizableNodeRef, certificateType);
        nodeCredentialParamsMap.put(WorkflowOutputParameterKeys.ASYMMETRIC_KEY_NAME.toString(), asymmetricKeyName);
        final String trustedCertsName = nscsCbpOiNodeUtility.getTrustCategoryName(normalizableNodeRef, certificateType);
        nodeCredentialParamsMap.put(WorkflowOutputParameterKeys.TRUSTED_CERTS_NAME.toString(), trustedCertsName);
        final ManagedObject asymmetricKeyMO = nscsDpsUtils.getChildMo(parentMo, normalizableNodeRef, ModelDefinition.ASYMMETRIC_KEY_TYPE,
                asymmetricKeyName);
        if (asymmetricKeyMO != null) {
            nscsLogger.info(task, "Already created asymmetric-key MO [{}] under asymmetric-keys", asymmetricKeyName);
            asymmetricKeyCmpMO = checkAsymmetricKeyCmp(task, asymmetricKeyMO, normalizableNodeRef, nodeCredentialParamsMap);
            /**
             * TORF-634157 - added extra check to verify if the subject name of the already existent certificate is different from the subject name
             * contained in PKI enrollment info. If they differ, start-cmp is required. The extra check is performed only if cmp MO under
             * asymmetric-key MO is present, otherwise a start-cmp is required anyway.
             * 
             * Note that, if cmp MO under asymmetric-key MO is present, the certificates hierarchy under asymmetric-key MO shall be present.
             */
            if (asymmetricKeyCmpMO != null) {
                checkAsymmetricKeyCertificatesHierarchy(task, asymmetricKeyMO, normalizableNodeRef, asymmetricKeyName, enrollmentInfo,
                        nodeCredentialParamsMap);
            }
        } else {
            nscsLogger.info(task, "start-cmp required : missing asymmetric-key MO [{}] under asymmetric-keys", asymmetricKeyName);
            nodeCredentialParamsMap.put(WorkflowOutputParameterKeys.IS_START_CMP_REQUIRED.toString(), "TRUE");
            nodeCredentialParamsMap.put(WorkflowOutputParameterKeys.RENEWAL_MODE.toString(),
                    ModelDefinition.ASYMMETRIC_KEY_CMP_RENEWAL_MODE_AUTOMATIC);
        }
        return asymmetricKeyCmpMO;
    }

    /**
     * Checks the certificates hierarchy under the given parent MO for the given node reference, certificate name and PKI enrollment info.
     * 
     * The check is performed only if cmp MO under asymmetric-key MO is present, and, in this case, the certificates hierarchy under asymmetric-key MO
     * shall be present.
     * 
     * @param task
     *            the task.
     * @param parentMo
     *            the parent MO.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param nodeCredentialName
     *            the certificate name.
     * @param enrollmentInfo
     *            the PKI enrollment info.
     * @param nodeCredentialParamsMap
     *            map containing the parameters of node credential MO.
     */
    private void checkAsymmetricKeyCertificatesHierarchy(final CbpOiCheckNodeCredentialCmpConfigurationTask task, final ManagedObject parentMo,
            final NormalizableNodeReference normalizableNodeRef, final String nodeCredentialName, final ScepEnrollmentInfoImpl enrollmentInfo,
            final Map<String, Serializable> nodeCredentialParamsMap) {
        final String unscopedMoType = ModelDefinition.KEYSTORE_CERTIFICATES_TYPE;
        final ManagedObject asymmetricKeyCertificatesMO = nscsDpsUtils.getChildMo(parentMo, normalizableNodeRef, unscopedMoType,
                CbpOiMoNaming.getName(unscopedMoType));
        if (asymmetricKeyCertificatesMO != null) {
            checkAsymmetricKeyCertificate(task, asymmetricKeyCertificatesMO, normalizableNodeRef, nodeCredentialName, enrollmentInfo,
                    nodeCredentialParamsMap);
        } else {
            final String errorMsg = String.format("Missing certificates MO under asymmetric-key %s for node %s", nodeCredentialName,
                    normalizableNodeRef.getName());
            nscsLogger.error(task, errorMsg);
            throw new MissingMoException(errorMsg);
        }
    }

    /**
     * Checks the certificate under the given parent MO for the given node reference, certificate name and PKI enrollment info.
     * 
     * The check is performed only if cmp MO under asymmetric-key MO is present and certificates MO is present under asymmetric-key MO, and, in this
     * case, the certificate MO under certificates MO shall be present.
     * 
     * @param task
     *            the task.
     * @param parentMo
     *            the parent MO.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param nodeCredentialName
     *            the certificate name.
     * @param enrollmentInfo
     *            the PKI enrollment info.
     * @param nodeCredentialParamsMap
     *            map containing the parameters of node credential MO.
     */
    private void checkAsymmetricKeyCertificate(final CbpOiCheckNodeCredentialCmpConfigurationTask task,
            final ManagedObject parentMo, final NormalizableNodeReference normalizableNodeRef, final String nodeCredentialName,
            final ScepEnrollmentInfoImpl enrollmentInfo, final Map<String, Serializable> nodeCredentialParamsMap) {
        final String unscopedMoType = ModelDefinition.KEYSTORE_CERTIFICATE_TYPE;
        final ManagedObject asymmetricKeyCertificateMO = nscsDpsUtils.getChildMo(parentMo, normalizableNodeRef, unscopedMoType, nodeCredentialName);
        if (asymmetricKeyCertificateMO != null) {
            final String cert = asymmetricKeyCertificateMO.getAttribute(ModelDefinition.KEYSTORE_CERTIFICATE_CERT_ATTR);
            final X509Certificate x509Certificate = nscsCbpOiNodeUtility.getNodeCredentialX509Certificate(cert);
            if (x509Certificate != null) {
                final String certSubjectName = nscsCbpOiNodeUtility.getSubject(x509Certificate);
                final String pkiSubjectName = enrollmentInfo.getDistinguishedName();
                if (!CertDetails.matchesDN(certSubjectName, pkiSubjectName)) {
                    nscsLogger.info(task,
                            "start-cmp required : certificate MO [{}] under asymmetric-key/certificates has subject name [{}] different from PKI subject name [{}] for node [{}]",
                            nodeCredentialName, certSubjectName, pkiSubjectName, normalizableNodeRef);
                    nodeCredentialParamsMap.put(WorkflowOutputParameterKeys.IS_START_CMP_REQUIRED.toString(), "TRUE");
                }
            }
        } else {
            final String errorMsg = String.format("Missing certificate MO %s under certificates MO under asymmetric-key %s for node %s",
                    nodeCredentialName, nodeCredentialName, normalizableNodeRef.getName());
            nscsLogger.error(task, errorMsg);
            throw new MissingMoException(errorMsg);
        }

    }

    /**
     * Checks the asymmetric-key cmp MO under the given parent MO for the given node reference.
     * 
     * @param task
     *            the task.
     * @param parentMo
     *            the parent MO.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param nodeCredentialParamsMap
     *            map containing the parameters of node credential MO.
     * @return the asymmetric-key cmp MO.
     */
    private ManagedObject checkAsymmetricKeyCmp(final CbpOiCheckNodeCredentialCmpConfigurationTask task, final ManagedObject parentMo,
            final NormalizableNodeReference normalizableNodeRef, final Map<String, Serializable> nodeCredentialParamsMap) {

        final String unscopedMoType = ModelDefinition.CMP_TYPE;
        ManagedObject asymmetricKeyCmpMO = nscsDpsUtils.getChildMo(parentMo, normalizableNodeRef, unscopedMoType,
                CbpOiMoNaming.getName(unscopedMoType));
        if (asymmetricKeyCmpMO != null) {
            nodeCredentialParamsMap.put(WorkflowOutputParameterKeys.ASYMMETRIC_KEY_CMP_FDN.toString(), asymmetricKeyCmpMO.getFdn());
            nscsLogger.info(task, "Already created cmp MO under asymmetric-key");
            asymmetricKeyCmpMO = checkAndUpdateAsymmetricKeyCmpAttributes(task, asymmetricKeyCmpMO, nodeCredentialParamsMap);
        } else {
            nscsLogger.info(task, "start-cmp required : missing cmp MO under asymmetric-key");
            nodeCredentialParamsMap.put(WorkflowOutputParameterKeys.IS_START_CMP_REQUIRED.toString(), "TRUE");
            nodeCredentialParamsMap.put(WorkflowOutputParameterKeys.RENEWAL_MODE.toString(),
                    ModelDefinition.ASYMMETRIC_KEY_CMP_RENEWAL_MODE_AUTOMATIC);
        }
        return asymmetricKeyCmpMO;
    }

    /**
     * Checks and updates the attributes of asymmetric-key cmp MO for the given node reference.
     * 
     * @param task
     *            the task.
     * @param asymmetricKeyCmpMO
     *            the asymmetric-key cmp MO.
     * @param nodeCredentialParamsMap
     *            map containing the parameters of node credential MO.
     * @return the asymmetric-key cmp MO.
     */
    private ManagedObject checkAndUpdateAsymmetricKeyCmpAttributes(final CbpOiCheckNodeCredentialCmpConfigurationTask task,
            final ManagedObject asymmetricKeyCmpMO, final Map<String, Serializable> nodeCredentialParamsMap) {

        ManagedObject updatedAsymmetricKeyCmpMO = asymmetricKeyCmpMO;

        // check cmp-server-group
        final String expectedCmpServerGroupName = (String) nodeCredentialParamsMap.get(WorkflowOutputParameterKeys.CMP_SERVER_GROUP_NAME.toString());
        final String actualCmpServerGroupName = asymmetricKeyCmpMO.getAttribute(ModelDefinition.ASYMMETRIC_KEY_CMP_CMP_SERVER_GROUP_ATTR);
        if (!expectedCmpServerGroupName.equals(actualCmpServerGroupName)) {
            nscsLogger.info(task, "start-cmp required : cmp-server-group name : expected [{}] actual [{}]", expectedCmpServerGroupName,
                    actualCmpServerGroupName);
            nodeCredentialParamsMap.put(WorkflowOutputParameterKeys.IS_START_CMP_REQUIRED.toString(), "TRUE");
        }
        /*
         * check trusted-certs
         * 
         * Before changes related to https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/TORF-516761, if trusted-certs was not null or not empty
         * a start-cmp was in any case required to reset it to null, to avoid the trust category overwriting performed by the node during enrollment.
         * Since the node no more overwrites the trust category during online enrollment, the attribute is managed
         */
        final String expectedTrustedCertsName = (String) nodeCredentialParamsMap.get(WorkflowOutputParameterKeys.TRUSTED_CERTS_NAME.toString());
        final String actualTrustedCertsName = asymmetricKeyCmpMO.getAttribute(ModelDefinition.ASYMMETRIC_KEY_CMP_TRUSTED_CERTS_ATTR);
        if (!expectedTrustedCertsName.equals(actualTrustedCertsName)) {
            nscsLogger.info(task, "start-cmp required : trusted-certs : expected [{}] actual [{}]", expectedTrustedCertsName, actualTrustedCertsName);
            nodeCredentialParamsMap.put(WorkflowOutputParameterKeys.IS_START_CMP_REQUIRED.toString(), "TRUE");
        }
        // check renewal-mode
        final String actualRenewalMode = asymmetricKeyCmpMO.getAttribute(ModelDefinition.ASYMMETRIC_KEY_CMP_RENEWAL_MODE_ATTR);
        nodeCredentialParamsMap.put(WorkflowOutputParameterKeys.RENEWAL_MODE.toString(), actualRenewalMode);
        if (!ModelDefinition.ASYMMETRIC_KEY_CMP_RENEWAL_MODE_MANUAL.equals(actualRenewalMode)) {
            nscsLogger.info(task, "renewal-mode : expected [manual] actual [{}]", actualRenewalMode);
            final Map<String, Object> asymmetricKeyCmpAttributes = new HashMap<>();
            asymmetricKeyCmpAttributes.put(ModelDefinition.ASYMMETRIC_KEY_CMP_RENEWAL_MODE_ATTR,
                    ModelDefinition.ASYMMETRIC_KEY_CMP_RENEWAL_MODE_MANUAL);
            updatedAsymmetricKeyCmpMO = nscsDpsUtils.updateMo(asymmetricKeyCmpMO, asymmetricKeyCmpAttributes);
            nscsLogger.info(task, "updated cmp MO under asymmetric-key : renewal-mode : set [manual]");
        }
        return updatedAsymmetricKeyCmpMO;
    }

    /**
     * Creates the asymmetric-keys MO and its hierarchy under the given parent MO for a given node reference.
     * 
     * @param task
     *            the task.
     * @param parentMo
     *            the parent MO.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @return the asymmetric-keys cmp MO created in the hierarchy.
     */
    private ManagedObject createAsymmetricKeysHierarchy(final CbpOiCheckNodeCredentialCmpConfigurationTask task, final ManagedObject parentMo,
            final NormalizableNodeReference normalizableNodeRef) {

        nscsLogger.info(task, "Creating asymmetric-keys hierarchy under keystore");
        final ManagedObject asymmetricKeysMO = nscsDpsUtils.createChildMo(parentMo, normalizableNodeRef, ModelDefinition.CBP_OI_KEYSTORE_NS,
                ModelDefinition.ASYMMETRIC_KEYS_TYPE, CbpOiMoNaming.getName(ModelDefinition.ASYMMETRIC_KEYS_TYPE), null);
        return createAsymmetricKeysCmp(task, asymmetricKeysMO, normalizableNodeRef);
    }

    /**
     * Creates the asymmetric-keys cmp MO under the given parent MO for a given node reference.
     * 
     * @param task
     *            the task.
     * @param parentMo
     *            the parent MO.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @return the asymmetric-keys cmp MO created.
     */
    private ManagedObject createAsymmetricKeysCmp(final CbpOiCheckNodeCredentialCmpConfigurationTask task, final ManagedObject parentMo,
            final NormalizableNodeReference normalizableNodeRef) {

        nscsLogger.info(task, "creating cmp MO under asymmetric-keys");
        final String unscopedMoType = ModelDefinition.CMP_TYPE;
        final String refMimNamespace = ModelDefinition.CBP_OI_KEYSTORE_EXT_NS;
        return nscsDpsUtils.createChildMo(parentMo, normalizableNodeRef, refMimNamespace, unscopedMoType, CbpOiMoNaming.getName(unscopedMoType),
                null);
    }

    /**
     * Prepares the return value of the task handler serializing a WorkflowQueryTaskResult containing both the result of the check performed by the
     * task handler and the output parameters.
     * 
     * @param task
     *            the task.
     * @param checkResult
     *            the result of the check performed by the task handler.
     * @param outputParams
     *            the output parameters.
     * @return the serialized result of the task handler.
     */
    private String serializeCheckNodeCredentialCmpConfigurationResult(final CbpOiCheckNodeCredentialCmpConfigurationTask task,
            final String checkResult, final Map<String, Serializable> outputParams) {

        final String message = String.format("serializing check node credential CMP configuration result [%s]", checkResult);

        nscsLogger.debug(task, message);

        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(checkResult, outputParams);
        String encodedWfQueryTaskResult = null;
        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessage = String.format("%s while %s", NscsLogger.stringifyException(e), message);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        return encodedWfQueryTaskResult;
    }

}
