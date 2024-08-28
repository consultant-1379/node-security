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
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiCheckCmpConfigurationTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;
import com.ericsson.oss.services.security.nscs.dps.NscsDpsUtils;

/**
 * <p>
 * Task handler for WorkflowTaskType.CBP_OI_CHECK_CMP_CONFIG.
 * </p>
 * <p>
 * Check the CMP configuration creating or updating the certificate authority MO and the CMP server group hierarchy of MOs.
 * </p>
 * <p>
 * Required incoming internal parameters: ENROLLMENT_INFO
 * </p>
 * <p>
 * Produced outgoing internal parameters: CMP_SERVER_GROUP_NAME
 * </p>
 */
@WFTaskType(WorkflowTaskType.CBP_OI_CHECK_CMP_CONFIG)
@Local(WFTaskHandlerInterface.class)
public class CbpOiCheckCmpConfigurationTaskHandler implements WFQueryTaskHandler<CbpOiCheckCmpConfigurationTask>, WFTaskHandlerInterface {

    private static final String PASSED = "PASSED";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsDpsUtils nscsDpsUtils;

    @Inject
    private NscsCbpOiNodeUtility nscsCbpOiNodeUtility;

    @Override
    public String processTask(final CbpOiCheckCmpConfigurationTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        final NormalizableNodeReference normalizableNodeRef = readerService.getNormalizableNodeReference(task.getNode());
        nscsLogger.info(task, "From task : mirrorRootFdn [{}] targetType [{}] targetModelIdentity [{}]", normalizableNodeRef.getFdn(),
                normalizableNodeRef.getNeType(), normalizableNodeRef.getOssModelIdentity());

        // Extract output parameters that shall have been already set by previous handlers
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        String result = null;
        String checkResult = null;
        try {
            final String cmpServerGroupName = checkCmpConfiguration(task, normalizableNodeRef, outputParams);
            nscsLogger.info(task, "returned cmp-server-group name [{}]", cmpServerGroupName);
            checkResult = PASSED;
            result = serializeCheckCmpConfigurationResult(task, checkResult, cmpServerGroupName, outputParams);
        } catch (final Exception e) {
            final String errorMessage = e.getMessage();
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw e;
        }

        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "Successfully completed", checkResult);
        return result;
    }

    /**
     * Checks the CMP configuration creating or updating the certificate authority MO and the CMP server group hierarchy of MOs.
     * 
     * @param task
     *            the task.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param outputParams
     *            the output parameters.
     * @return the name of the cmp-server-group MO.
     */
    private String checkCmpConfiguration(final CbpOiCheckCmpConfigurationTask task, final NormalizableNodeReference normalizableNodeRef,
            final Map<String, Serializable> outputParams) {

        // Extract enrollment info from the output parameters
        final String serializedEnrollmentInfo = (String) outputParams.get(WorkflowOutputParameterKeys.ENROLLMENT_INFO.toString());
        final ScepEnrollmentInfoImpl enrollmentInfo = NscsObjectSerializer.readObject(serializedEnrollmentInfo);
        if (enrollmentInfo == null) {
            final String errorMessage = "Missing enrollment info internal parameter";
            nscsLogger.error(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        nscsLogger.info(task, "From output params : enrollment info [{}]", enrollmentInfo);

        final String certificateType = NscsNodeUtility.getCertificateTypeFromTrustedCertCategory(task.getTrustedCertCategory());
        nscsLogger.info(task, "From task : certificate type [{}]", certificateType);

        final ManagedObject cmpServerGroupMO = checkKeystoreHierarchy(task, normalizableNodeRef, certificateType, enrollmentInfo);
        return cmpServerGroupMO.getName();
    }

    /**
     * Checks the keystore hierarchy and creates the missing MOs for the given node reference.
     * 
     * @param task
     *            the task.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param certificateType
     *            the certificate type.
     * @param enrollmentInfo
     *            the enrollment info.
     * @return the cmp-server-group MO.
     */
    private ManagedObject checkKeystoreHierarchy(final CbpOiCheckCmpConfigurationTask task, final NormalizableNodeReference normalizableNodeRef,
            final String certificateType, final ScepEnrollmentInfoImpl enrollmentInfo) {

        ManagedObject cmpServerGroupMO = null;

        final String moType = ModelDefinition.KEYSTORE_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_KEYSTORE_NS;
        final ManagedObject keystoreMO = nscsDpsUtils.getNodeHierarchyTopMo(normalizableNodeRef, refMimNs, moType, CbpOiMoNaming.getName(moType));
        if (keystoreMO != null) {
            cmpServerGroupMO = checkKeystoreCmpHierarchy(task, keystoreMO, normalizableNodeRef, certificateType, enrollmentInfo);
        } else {
            cmpServerGroupMO = createKeystoreHierarchy(task, normalizableNodeRef, certificateType, enrollmentInfo);
        }
        return cmpServerGroupMO;
    }

    /**
     * Checks the keystore$$cmp hierarchy and creates the missing MOs under the given parent MO for the given node reference.
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
     *            the enrollment info.
     * @return the cmp-server-group MO.
     */
    private ManagedObject checkKeystoreCmpHierarchy(final CbpOiCheckCmpConfigurationTask task, final ManagedObject parentMo,
            final NormalizableNodeReference normalizableNodeRef, final String certificateType, final ScepEnrollmentInfoImpl enrollmentInfo) {
        ManagedObject cmpServerGroupMO = null;
        final String unscopedMoType = ModelDefinition.CMP_TYPE;
        final ManagedObject keystoreCmpMO = nscsDpsUtils.getChildMo(parentMo, normalizableNodeRef, unscopedMoType,
                CbpOiMoNaming.getName(unscopedMoType));
        if (keystoreCmpMO != null) {
            final ManagedObject certificateAuthorityMO = checkCertificateAuthoritiesHierarchy(task, keystoreCmpMO, normalizableNodeRef,
                    enrollmentInfo);
            final String certificateAuthorityName = certificateAuthorityMO.getAttribute(ModelDefinition.CERTIFICATE_AUTHORITY_NAME_ATTR);
            cmpServerGroupMO = checkCmpServerGroupsHierarchy(task, keystoreCmpMO, normalizableNodeRef, certificateType, enrollmentInfo,
                    certificateAuthorityName);
        } else {
            cmpServerGroupMO = createKeystoreCmpHierarchy(task, parentMo, normalizableNodeRef, certificateType, enrollmentInfo);
        }
        return cmpServerGroupMO;
    }

    /**
     * Checks the certificate-authorities hierarchy and creates the missing MOs under the given parent MO for the given node reference.
     * 
     * @param task
     *            the task.
     * @param parentMo
     *            the parent MO.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param enrollmentInfo
     *            the enrollment info.
     * @return the certificate-authority MO.
     */
    private ManagedObject checkCertificateAuthoritiesHierarchy(final CbpOiCheckCmpConfigurationTask task, final ManagedObject parentMo,
            final NormalizableNodeReference normalizableNodeRef, final ScepEnrollmentInfoImpl enrollmentInfo) {
        ManagedObject certificateAuthorityMO = null;
        final ManagedObject certificateAuthoritiesMO = nscsDpsUtils.getChildMo(parentMo, normalizableNodeRef,
                ModelDefinition.CERTIFICATE_AUTHORITIES_TYPE, CbpOiMoNaming.getName(ModelDefinition.CERTIFICATE_AUTHORITIES_TYPE));
        if (certificateAuthoritiesMO != null) {
            certificateAuthorityMO = checkCertificateAuthority(task, certificateAuthoritiesMO, normalizableNodeRef, enrollmentInfo);
        } else {
            certificateAuthorityMO = createCertificateAuthoritiesHierarchy(task, parentMo, normalizableNodeRef, enrollmentInfo);
            nscsLogger.info(task,
                    "Created certificate-authority MO with FDN [{}] moName [{}] name [{}] during certificate-authorities hierarchy creation",
                    certificateAuthorityMO.getFdn(), certificateAuthorityMO.getName(),
                    certificateAuthorityMO.getAttribute(ModelDefinition.CERTIFICATE_AUTHORITY_NAME_ATTR));
        }
        return certificateAuthorityMO;
    }

    /**
     * Checks the certificate-authority and creates it if missing under the given parent MO for the given node reference.
     * 
     * @param task
     *            the task.
     * @param parentMo
     *            the parent MO.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param enrollmentInfo
     *            the enrollment info.
     * @return the certificate-authority MO.
     */
    private ManagedObject checkCertificateAuthority(final CbpOiCheckCmpConfigurationTask task, final ManagedObject parentMo,
            final NormalizableNodeReference normalizableNodeRef, final ScepEnrollmentInfoImpl enrollmentInfo) {
        ManagedObject certificateAuthorityMO;
        certificateAuthorityMO = nscsDpsUtils.getChildMoByAttrAsDn(parentMo, normalizableNodeRef, ModelDefinition.CERTIFICATE_AUTHORITY_TYPE,
                ModelDefinition.CERTIFICATE_AUTHORITY_NAME_ATTR, getCertificateAuthorityName(task, enrollmentInfo));
        if (certificateAuthorityMO != null) {
            nscsLogger.info(task, "Already existent certificate-authority MO with FDN [{}] moName [{}] name [{}]", certificateAuthorityMO.getFdn(),
                    certificateAuthorityMO.getName(), certificateAuthorityMO.getAttribute(ModelDefinition.CERTIFICATE_AUTHORITY_NAME_ATTR));
        } else {
            certificateAuthorityMO = createCertificateAuthority(task, parentMo, normalizableNodeRef, enrollmentInfo);
            nscsLogger.info(task, "Created certificate-authority MO with FDN [{}] moName [{}] name [{}] under existent parentFdn [{}]",
                    certificateAuthorityMO.getFdn(), certificateAuthorityMO.getName(),
                    certificateAuthorityMO.getAttribute(ModelDefinition.CERTIFICATE_AUTHORITY_NAME_ATTR), parentMo.getFdn());
        }
        return certificateAuthorityMO;
    }

    /**
     * Checks the cmp-server-groups hierarchy and creates the missing MOs under the given parent MO for the given node reference.
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
     *            the enrollment info.
     * @param certificateAuthorityName
     *            the name of the certificate-authority MO.
     * @return the cmp-server-group MO.
     */
    private ManagedObject checkCmpServerGroupsHierarchy(final CbpOiCheckCmpConfigurationTask task, final ManagedObject parentMo,
            final NormalizableNodeReference normalizableNodeRef, final String certificateType, final ScepEnrollmentInfoImpl enrollmentInfo,
            final String certificateAuthorityName) {
        ManagedObject cmpServerGroupMO;
        final ManagedObject cmpServerGroupsMO = nscsDpsUtils.getChildMo(parentMo, normalizableNodeRef,
                ModelDefinition.CMP_SERVER_GROUPS_TYPE, CbpOiMoNaming.getName(ModelDefinition.CMP_SERVER_GROUPS_TYPE));
        if (cmpServerGroupsMO != null) {
            cmpServerGroupMO = checkCmpServerGroupHierarchy(task, cmpServerGroupsMO, normalizableNodeRef, certificateType, enrollmentInfo,
                    certificateAuthorityName);
        } else {
            cmpServerGroupMO = createCmpServerGroupsHierarchy(task, parentMo, normalizableNodeRef, certificateType, enrollmentInfo,
                    certificateAuthorityName);
        }
        return cmpServerGroupMO;
    }

    /**
     * Checks the cmp-server-group hierarchy and creates the missing MOs under the given parent MO for the given node reference.
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
     *            the enrollment info.
     * @param certificateAuthorityName
     *            the name of the certificate-authority MO.
     * @return the cmp-server-group MO.
     */
    private ManagedObject checkCmpServerGroupHierarchy(final CbpOiCheckCmpConfigurationTask task, final ManagedObject parentMo,
            final NormalizableNodeReference normalizableNodeRef, final String certificateType, final ScepEnrollmentInfoImpl enrollmentInfo,
            final String certificateAuthorityName) {
        ManagedObject cmpServerGroupMO;
        cmpServerGroupMO = nscsDpsUtils.getChildMo(parentMo, normalizableNodeRef, ModelDefinition.CMP_SERVER_GROUP_TYPE,
                CbpOiMoNaming.getNameByCertificateType(ModelDefinition.CMP_SERVER_GROUP_TYPE, certificateType));
        if (cmpServerGroupMO != null) {
            checkCmpServer(task, cmpServerGroupMO, normalizableNodeRef, certificateType, enrollmentInfo, certificateAuthorityName);
        } else {
            cmpServerGroupMO = createCmpServerGroupHierarchy(task, parentMo, normalizableNodeRef, certificateType, enrollmentInfo,
                    certificateAuthorityName);
        }
        return cmpServerGroupMO;
    }

    /**
     * Checks the cmp-serverMO and creates it if missing under the given parent MO for the given node reference.
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
     *            the enrollment info.
     * @param certificateAuthorityName
     *            the name of the certificate-authority MO.
     * @return the cmp-server MO.
     */
    private ManagedObject checkCmpServer(final CbpOiCheckCmpConfigurationTask task, ManagedObject parentMo,
            final NormalizableNodeReference normalizableNodeRef, final String certificateType, final ScepEnrollmentInfoImpl enrollmentInfo,
            final String certificateAuthorityName) {
        ManagedObject cmpServerMO = nscsDpsUtils.getChildMo(parentMo, normalizableNodeRef, ModelDefinition.CMP_SERVER_TYPE,
                CbpOiMoNaming.getName(ModelDefinition.CMP_SERVER_TYPE));
        if (cmpServerMO != null) {
            cmpServerMO = updateCmpServer(task, cmpServerMO, normalizableNodeRef, certificateType, enrollmentInfo, certificateAuthorityName);
        } else {
            cmpServerMO = createCmpServer(task, parentMo, normalizableNodeRef, certificateType, enrollmentInfo, certificateAuthorityName);
        }
        return cmpServerMO;
    }

    /**
     * Creates the keystore MO and its hierarchy for the given node reference and for the given certificate type.
     * 
     * @param task
     *            the task.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param certificateType
     *            the type of certificate.
     * @param enrollmentInfo
     *            the enrollment info.
     * @return the cmp-server-group MO created in the hierarchy.
     */
    private ManagedObject createKeystoreHierarchy(final CbpOiCheckCmpConfigurationTask task, final NormalizableNodeReference normalizableNodeRef,
            final String certificateType, final ScepEnrollmentInfoImpl enrollmentInfo) {

        final String moType = ModelDefinition.KEYSTORE_TYPE;
        final String refMimNamespace = ModelDefinition.CBP_OI_KEYSTORE_NS;
        final String moName = CbpOiMoNaming.getName(moType);
        final ManagedObject keystoreMO = nscsDpsUtils.createNodeHierarchyTopMo(normalizableNodeRef, refMimNamespace, moType, moName);
        nscsLogger.info(task, "Successfully created MO [{}]", keystoreMO.getFdn());

        return createKeystoreCmpHierarchy(task, keystoreMO, normalizableNodeRef, certificateType, enrollmentInfo);
    }

    /**
     * Creates the keystore cmp MO and its hierarchy under the given parent MO for a given node reference and for the given certificate type.
     * 
     * @param task
     *            the task.
     * @param parentMo
     *            the parent MO.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param certificateType
     *            the type of certificate.
     * @param enrollmentInfo
     *            the enrollment info.
     * @return the cmp-server-group MO created in the hierarchy.
     */
    private ManagedObject createKeystoreCmpHierarchy(final CbpOiCheckCmpConfigurationTask task, final ManagedObject parentMo,
            final NormalizableNodeReference normalizableNodeRef, final String certificateType, final ScepEnrollmentInfoImpl enrollmentInfo) {

        final String unscopedMoType = ModelDefinition.CMP_TYPE;
        final String refMimNamespace = ModelDefinition.CBP_OI_KEYSTORE_EXT_NS;
        final ManagedObject keystoreCmpMO = nscsDpsUtils.createChildMo(parentMo, normalizableNodeRef, refMimNamespace, unscopedMoType,
                CbpOiMoNaming.getName(unscopedMoType), null);
        final ManagedObject certificateAuthorityMO = createCertificateAuthoritiesHierarchy(task, keystoreCmpMO, normalizableNodeRef, enrollmentInfo);
        nscsLogger.info(task, "Created certificate-authority MO with FDN [{}] moName [{}] name [{}] during keystore cmp hierarchy creation",
                certificateAuthorityMO.getFdn(), certificateAuthorityMO.getName(),
                certificateAuthorityMO.getAttribute(ModelDefinition.CERTIFICATE_AUTHORITY_NAME_ATTR));
        return createCmpServerGroupsHierarchy(task, keystoreCmpMO, normalizableNodeRef, certificateType, enrollmentInfo,
                certificateAuthorityMO.getAttribute(ModelDefinition.CERTIFICATE_AUTHORITY_NAME_ATTR));
    }

    /**
     * Creates the certificate-authorities MO and its hierarchy under the given parent MO for a given node reference.
     * 
     * @param task
     *            the task.
     * @param parentMo
     *            the parent MO.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param enrollmentInfo
     *            the enrollment info.
     * @return the certificate-authority MO created in the hierarchy.
     */
    private ManagedObject createCertificateAuthoritiesHierarchy(final CbpOiCheckCmpConfigurationTask task, final ManagedObject parentMo,
            final NormalizableNodeReference normalizableNodeRef, final ScepEnrollmentInfoImpl enrollmentInfo) {

        final ManagedObject certificateAuthoritiesMO = nscsDpsUtils.createChildMo(parentMo, normalizableNodeRef,
                ModelDefinition.CBP_OI_KEYSTORE_EXT_NS, ModelDefinition.CERTIFICATE_AUTHORITIES_TYPE,
                CbpOiMoNaming.getName(ModelDefinition.CERTIFICATE_AUTHORITIES_TYPE), null);
        return createCertificateAuthority(task, certificateAuthoritiesMO, normalizableNodeRef, enrollmentInfo);
    }

    /**
     * Creates the certificate-authority MO of given name under the given parent MO for a given node reference.
     * 
     * @param task
     *            the task.
     * @param parentMo
     *            the parent MO.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param enrollmentInfo
     *            the enrollment info.
     * @return the created certificate-authority MO.
     */
    private ManagedObject createCertificateAuthority(final CbpOiCheckCmpConfigurationTask task, final ManagedObject parentMo,
            final NormalizableNodeReference normalizableNodeRef, final ScepEnrollmentInfoImpl enrollmentInfo) {

        final String certificateAuthorityName = getCertificateAuthorityName(task, enrollmentInfo);
        nscsLogger.info(task, "From enrollment info : certificate authority DN [{}]", certificateAuthorityName);
        final Map<String, Object> certificateAuthorityAttributes = new HashMap<>();
        certificateAuthorityAttributes.put(ModelDefinition.CERTIFICATE_AUTHORITY_NAME_ATTR, certificateAuthorityName);
        return nscsDpsUtils.createChildMo(parentMo, normalizableNodeRef, ModelDefinition.CBP_OI_KEYSTORE_EXT_NS,
                ModelDefinition.CERTIFICATE_AUTHORITY_TYPE, certificateAuthorityName, certificateAuthorityAttributes);
    }

    /**
     * Gets the name of the certificate-authority MO from the given enrollment information.
     * 
     * @param task
     *            the task.
     * @param enrollmentInfo
     *            the enrollment information.
     * @return the name of the certificate-authority MO.
     */
    private String getCertificateAuthorityName(final CbpOiCheckCmpConfigurationTask task, final ScepEnrollmentInfoImpl enrollmentInfo) {
        final String pkiDn = enrollmentInfo.getCertificateAuthorityDn();
        final String bcX500Name = CertDetails.getBcX500Name(pkiDn);
        if (bcX500Name != null) {
            nscsLogger.info(task, "certificate-authority name [{}] for PKI DN [{}]", bcX500Name, pkiDn);
            return bcX500Name;
        } else {
            final String errorMessage = String.format("Wrong or unsupported DN [%s] from enrollment info internal parameter", pkiDn);
            nscsLogger.error(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
    }

    /**
     * Creates the cmp-servers-groups MO and its hierarchy under the given parent MO for a given node reference and for the given certificate type.
     * 
     * @param task
     *            the task.
     * @param parentMo
     *            the parent MO.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param certificateType
     *            the type of certificate.
     * @param enrollmentInfo
     *            the enrollment info.
     * @param certificateAuthorityName
     *            the name of the certificate-authority MO.
     * @return the cmp-server-group MO created in the hierarchy.
     */
    private ManagedObject createCmpServerGroupsHierarchy(final CbpOiCheckCmpConfigurationTask task, final ManagedObject parentMo,
            final NormalizableNodeReference normalizableNodeRef, final String certificateType, final ScepEnrollmentInfoImpl enrollmentInfo,
            final String certificateAuthorityName) {

        final ManagedObject cmpServerGroupsMO = nscsDpsUtils.createChildMo(parentMo, normalizableNodeRef, ModelDefinition.CBP_OI_KEYSTORE_EXT_NS,
                ModelDefinition.CMP_SERVER_GROUPS_TYPE, CbpOiMoNaming.getName(ModelDefinition.CMP_SERVER_GROUPS_TYPE), null);
        return createCmpServerGroupHierarchy(task, cmpServerGroupsMO, normalizableNodeRef, certificateType, enrollmentInfo, certificateAuthorityName);
    }

    /**
     * Creates the cmp-server-group MO and its hierarchy under the given parent MO for a given node reference and for the given certificate type.
     * 
     * @param task
     *            the task.
     * @param parentMo
     *            the parent MO.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param certificateType
     *            the type of certificate.
     * @param enrollmentInfo
     *            the enrollment info.
     * @param certificateAuthorityName
     *            the name of the certificate-authority MO.
     * @return the cmp-server-group MO created in the hierarchy.
     */
    private ManagedObject createCmpServerGroupHierarchy(final CbpOiCheckCmpConfigurationTask task, final ManagedObject parentMo,
            final NormalizableNodeReference normalizableNodeRef, final String certificateType, final ScepEnrollmentInfoImpl enrollmentInfo,
            final String certificateAuthorityName) {

        final ManagedObject cmpServerGroupMO = nscsDpsUtils.createChildMo(parentMo, normalizableNodeRef, ModelDefinition.CBP_OI_KEYSTORE_EXT_NS,
                ModelDefinition.CMP_SERVER_GROUP_TYPE, CbpOiMoNaming.getNameByCertificateType(ModelDefinition.CMP_SERVER_GROUP_TYPE, certificateType),
                null);
        createCmpServer(task, cmpServerGroupMO, normalizableNodeRef, certificateType, enrollmentInfo, certificateAuthorityName);
        return cmpServerGroupMO;
    }

    /**
     * Creates the cmp-server MO under the given parent MO for a given node reference and for the given input parameters.
     * 
     * @param task
     *            the task.
     * @param parentMo
     *            the parent MO.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param certificateType
     *            the type of certificate.
     * @param enrollmentInfo
     *            the enrollment info.
     * @param certificateAuthorityName
     *            the name of the certificate-authority MO.
     * @return the cmp-server MO created.
     */
    private ManagedObject createCmpServer(final CbpOiCheckCmpConfigurationTask task, final ManagedObject parentMo,
            final NormalizableNodeReference normalizableNodeRef, final String certificateType, final ScepEnrollmentInfoImpl enrollmentInfo,
            final String certificateAuthorityName) {

        final Map<String, Object> cmpServerAttributes = getCmpServerAttributes(task, normalizableNodeRef, certificateType, enrollmentInfo,
                certificateAuthorityName);
        return nscsDpsUtils.createChildMo(parentMo, normalizableNodeRef, ModelDefinition.CBP_OI_KEYSTORE_EXT_NS, ModelDefinition.CMP_SERVER_TYPE,
                CbpOiMoNaming.getName(ModelDefinition.CMP_SERVER_TYPE), cmpServerAttributes);
    }

    /**
     * Updates the given cmp-server MO for the given node reference.
     * 
     * @param task
     *            the task.
     * @param cmpServerMO
     *            the cmp-server MO.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param certificateType
     *            the type of certificate.
     * @param enrollmentInfo
     *            the enrollment info.
     * @param certificateAuthorityName
     *            the name of the certificate-authority MO.
     * @return the updated cmp-server MO.
     */
    private ManagedObject updateCmpServer(final CbpOiCheckCmpConfigurationTask task, final ManagedObject cmpServerMO,
            final NormalizableNodeReference normalizableNodeRef, final String certificateType, final ScepEnrollmentInfoImpl enrollmentInfo,
            final String certificateAuthorityName) {

        final Map<String, Object> cmpServerAttributes = getCmpServerAttributes(task, normalizableNodeRef, certificateType, enrollmentInfo,
                certificateAuthorityName);
        return nscsDpsUtils.updateMo(cmpServerMO, cmpServerAttributes);
    }

    /**
     * Gets the cmp-server attributes for the given node reference.
     * 
     * @param task
     *            the task.
     * @param normalizableNodeRef
     *            the normalizable node reference.
     * @param certificateType
     *            the type of certificate.
     * @param enrollmentInfo
     *            the enrollment info.
     * @param certificateAuthorityName
     *            the name of the certificate-authority MO.
     * @param enrollmentTrustCategoryName
     *            the name of the enrollment trust category MO.
     * @return the attributes of the cmp-server MO.
     */
    private Map<String, Object> getCmpServerAttributes(final CbpOiCheckCmpConfigurationTask task, final NormalizableNodeReference normalizableNodeRef,
            final String certificateType, final ScepEnrollmentInfoImpl enrollmentInfo, final String certificateAuthorityName) {

        final Map<String, Object> cmpServerAttributes = new HashMap<>();
        final String enrollmentTrustCategoryName = nscsCbpOiNodeUtility.getEnrollmentTrustCategoryName(normalizableNodeRef, certificateType);
        nscsLogger.info(task, "enrollment trust category name [{}]", enrollmentTrustCategoryName);

        cmpServerAttributes.put(ModelDefinition.CMP_SERVER_CA_CERTS_ATTR, enrollmentTrustCategoryName);
        cmpServerAttributes.put(ModelDefinition.CMP_SERVER_CERTIFICATE_AUTHORITY_ATTR, certificateAuthorityName);
        cmpServerAttributes.put(ModelDefinition.CMP_SERVER_URI_ATTR, enrollmentInfo.getServerURL());
        return cmpServerAttributes;
    }

    /**
     * Prepares the return value of the task handler serializing a WorkflowQueryTaskResult containing both the result of the check performed by the
     * task handler and the output parameters.
     * 
     * @param task
     *            the task.
     * @param checkResult
     *            the result of the check performed by the task handler.
     * @param cmpServerGroupName
     *            the name of the cmp-server-group MO.
     * @param outputParams
     *            the output parameters.
     * @return the serialized result of the task handler.
     */
    private String serializeCheckCmpConfigurationResult(final CbpOiCheckCmpConfigurationTask task, final String checkResult,
            final String cmpServerGroupName, final Map<String, Serializable> outputParams) {

        outputParams.put(WorkflowOutputParameterKeys.CMP_SERVER_GROUP_NAME.toString(), cmpServerGroupName);

        final String message = String.format("serializing check CMP configuration result [%s]", checkResult);

        nscsLogger.debug(task, message);

        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(checkResult, outputParams);
        String encodedWfQueryTaskResult = null;
        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessage = String.format("%s while %s", NscsLogger.stringifyException(e), message);
            throw new UnexpectedErrorException(errorMessage);
        }
        return encodedWfQueryTaskResult;
    }
}
