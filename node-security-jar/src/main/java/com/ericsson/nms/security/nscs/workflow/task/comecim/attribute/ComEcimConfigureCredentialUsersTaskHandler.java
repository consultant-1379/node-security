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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelInfo;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Ikev2PolicyProfile;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Ldap;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetconfTls;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NodeCredential;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TrustCategory;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.nms.security.nscs.utilities.NSCSComEcimNodeUtility;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimConfigureCredentialUsersTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * <p>
 * Task handler for WorkflowTaskType.COM_ECIM_CONFIGURE_CREDENTIAL_USERS. This is invoked in the COMIssueCert workflow.
 * </p>
 *
 * @author emaborz
 */
@WFTaskType(WorkflowTaskType.COM_ECIM_CONFIGURE_CREDENTIAL_USERS)
@Local(WFTaskHandlerInterface.class)
public class ComEcimConfigureCredentialUsersTaskHandler implements WFQueryTaskHandler<ComEcimConfigureCredentialUsersTask>, WFTaskHandlerInterface {

    private static final String VALID = "VALID";
    private static final String TRUE = "TRUE";
    private static final String EXTERNAL_CA = "EXTERNAL_CA";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsCMWriterService writerService;

    @Inject
    private NscsModelServiceImpl nscsModelServiceImpl;

    @Inject
    private NscsCapabilityModelService capabilityService;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    @Inject
    private NodeValidatorUtility nodeValidatorUtility;

    @Inject
    private NSCSComEcimNodeUtility nscsComEcimNodeUtility;

    @Override
    public String processTask(final ComEcimConfigureCredentialUsersTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        // Extract node parameters
        final NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(task.getNode());
        final String nodeName = task.getNode().getName();
        final String mirrorRootFdn = normalizable.getFdn();

        final Mo rootMo = capabilityService.getMirrorRootMo(normalizable);

        // Extract trusted certificate category parameter
        final String trustedCertCategory = task.getTrustedCertCategory();
        final String certificateType = NscsNodeUtility.getCertificateTypeFromTrustedCertCategory(trustedCertCategory);
        nscsLogger.debug(task, "From task : certificateType [" + certificateType + "]");

        // Extract output parameters set by previous handlers
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters for certificate type [" + certificateType + "]";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        // Get NodeCredential FDN and renewal mode parameters
        String nodeCredentialRenewalMode = null;
        final String nodeCredentialFdn = (String) outputParams.get(WorkflowOutputParameterKeys.NODE_CREDENTIAL_FDN.toString());
        if (nodeCredentialFdn != null) {
            nscsLogger.info(task, "From output params : NodeCredential FDN [" + nodeCredentialFdn + "]");

            // Get NodeCredential renewal mode set by previous handlers
            final String renewalMode = (String) outputParams.get(WorkflowOutputParameterKeys.RENEWAL_MODE.toString());
            if (renewalMode != null) {
                nodeCredentialRenewalMode = renewalMode;
                nscsLogger.info(task,
                        "From output params : renewalMode [" + renewalMode + "] nodeCredentialRenewalMode [" + nodeCredentialRenewalMode + "]");
            } else {
                final String infoMessage = "From output params: got null renewalMode";
                nscsLogger.info(task, infoMessage);
            }
        } else {
            // Since this handler is invoked in COMIssueCert workflow, the
            // NodeCredential name parameter shall be not null
            final String errorMessage = "Missing NodeCredential FDN param for certType [" + certificateType + "]";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        // Get TrustCategory FDN parameter
        final String trustCategoryFdn = (String) outputParams.get(WorkflowOutputParameterKeys.TRUST_CATEGORY_FDN.toString());
        final String isTrustDistributionRequired = task.getIsTrustDistributionRequired();
        if (TRUE.equals(isTrustDistributionRequired)) {
            if (trustCategoryFdn != null) {
                nscsLogger.info(task, "From output params : TrustCategory FDN [" + trustCategoryFdn + "]");

                if (EXTERNAL_CA.equals(task.getCertificateEnrollmentCa())) {
                    checkAndUpdateCrlInterface(task, normalizable, trustCategoryFdn);
                }

            } else {
                // Since this handler is invoked in COMIssueCert workflow, the
                // TrustCategory FDN parameter shall be not null
                final String errorMessage = "Missing TrustCategory FDN param for certType [" + certificateType + "]";
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            }
        }
        // Update NodeCredential MO, setting its renewal mode attribute to the
        // value present before enrollment
        if (nodeCredentialRenewalMode != null) {
            nscsLogger.info(task,
                    "NodeCredential [" + nodeCredentialFdn + "] : renewalMode attribute to be updated to value [" + nodeCredentialRenewalMode + "]");
            final NscsCMWriterService.WriterSpecificationBuilder nodeCredentialSpec = writerService.withSpecification();
            nodeCredentialSpec.setNotNullAttribute(NodeCredential.RENEWAL_MODE, nodeCredentialRenewalMode);
            nodeCredentialSpec.setFdn(nodeCredentialFdn);
            final String updateMessage = NscsLogger.stringifyUpdateParams("NodeCredential", nodeCredentialFdn);
            nscsLogger.info(task, "Updating " + updateMessage);
            try {
                nodeCredentialSpec.updateMO();
            } catch (final Exception e) {
                final String errorMessage = NscsLogger.stringifyException(e) + " while updating " + updateMessage;
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            }
            nscsLogger.workFlowTaskHandlerOngoing(task, "Successfully updated " + updateMessage);
        }

        // Update Users of credentials
        if (CertificateType.OAM.name().equals(certificateType)) {
            // Check and update (if needed) NetconfTls.
            final Mo netconfTlsMo = ((ComEcimManagedElement) rootMo).systemFunctions.sysM.netconfTls;
            final boolean isNetconfTlsUpdateNeeded = checkAndUpdateNetconfTls(task, mirrorRootFdn, netconfTlsMo, nodeCredentialFdn, trustCategoryFdn);
            nscsLogger.debug(task, "NetconfTls : isNetconfTlsUpdateNeeded [" + isNetconfTlsUpdateNeeded + "]");

            // Check and update (if needed) Ldap.
            if (capabilityService.isCliCommandSupported(normalizable, NscsCapabilityModelService.LDAP_COMMAND)) {
                final Mo ldapMo = ((ComEcimManagedElement) rootMo).systemFunctions.secM.userManagement.ldapAuthenticationMethod.ldap;
                final boolean isLdapUpdateNeeded = checkAndUpdateLdap(task, mirrorRootFdn, ldapMo, nodeCredentialFdn, trustCategoryFdn);
                nscsLogger.debug(task, "Ldap : isLdapUpdateNeeded [" + isLdapUpdateNeeded + "]");
            }
        } else if (CertificateType.IPSEC.name().equals(certificateType)) {
            if (capabilityService.isIkev2PolicyProfileSupported(normalizable)) {
                // Get Transport MO FDN
                final Mo transportMo = ((ComEcimManagedElement) rootMo).transport;
                final String readTransportMessage = NscsLogger.stringifyReadParams(mirrorRootFdn, transportMo.type());
                nscsLogger.debug(task, "Reading " + readTransportMessage);
                final String transportFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, transportMo);
                if (transportFdn == null || transportFdn.isEmpty()) {
                    final String errorMessage = "Error while reading " + readTransportMessage;
                    nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                    throw new MissingMoException(nodeName, transportMo.type());
                }

                // Check and create (if needed) Ikev2PolicyProfile
                final Mo ikev2PolicyProfileMo = ((ComEcimManagedElement) rootMo).transport.ikev2PolicyProfile;
                final String targetCategory = normalizable.getTargetCategory();
                final String nodeType = normalizable.getNeType();
                final String tMI = normalizable.getOssModelIdentity();
                nscsLogger.info(task, "Got targetCategory [{}] nodeType [{}] and tMI [{}]", targetCategory, nodeType, tMI);
                final NscsModelInfo ikev2PolicyProfileModelInfo = nscsModelServiceImpl.getModelInfo(targetCategory, nodeType, tMI,
                        ikev2PolicyProfileMo.type());
                nscsLogger.info(task, "Got modelInfo [" + ikev2PolicyProfileModelInfo + "]");
                final String ikev2PolicyProfileName = checkAndCreateOrUpdateIkev2PolicyProfile(task, transportFdn, ikev2PolicyProfileMo,
                        ikev2PolicyProfileModelInfo, nodeCredentialFdn, trustCategoryFdn);
                final String ikev2PolicyProfileFdn = ikev2PolicyProfileMo.getFdnByParentFdn(transportFdn, ikev2PolicyProfileName);
                nscsLogger.debug(task, "Successfully checked and possibly created Ikev2PolicyProfile [" + ikev2PolicyProfileFdn + "]");
            } else {
                nscsLogger.debug(task, "ikev2PolicyProfile is not supported");
            }
        } else {
            final String errorMessage = "Unknown certificate type [" + certificateType + "]";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        final String result = VALID;
        final String successMessage = "Successfully completed : Credential Users state is [" + result + "]";
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);

        return result;
    }

    /**
     * @param task
     * @param mirrorRootFdn
     * @param netconfTlsMo
     * @param nodeCredentialFdn
     * @param trustCategoryFdn
     * @return
     * @throws MissingMoException
     * @throws UnexpectedErrorException
     */
    private boolean checkAndUpdateNetconfTls(final ComEcimConfigureCredentialUsersTask task, final String mirrorRootFdn, final Mo netconfTlsMo,
            final String nodeCredentialFdn, final String trustCategoryFdn) throws MissingMoException, UnexpectedErrorException {
        boolean isNetconfTlsUpdateNeeded = false;
        String currentNodeCredentialFdn = null;
        String currentTrustCategoryFdn = null;
        final Map<String, Object> attributes = new HashMap<String, Object>();
        final String requestedAttrs[] = { NetconfTls.NODE_CREDENTIAL, NetconfTls.TRUST_CATEGORY };
        final String readMessage = NscsLogger.stringifyReadParams(mirrorRootFdn, netconfTlsMo.type(), requestedAttrs);
        nscsLogger.debug(task, "Reading " + readMessage);
        final String netconfTlsFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, netconfTlsMo, attributes, requestedAttrs);
        if (netconfTlsFdn != null) {
            currentNodeCredentialFdn = (String) attributes.get(NetconfTls.NODE_CREDENTIAL);
            currentTrustCategoryFdn = (String) attributes.get(NetconfTls.TRUST_CATEGORY);
        } else {
            final String errorMessage = "Error while reading " + readMessage;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new MissingMoException(mirrorRootFdn, netconfTlsMo.type());
        }
        nscsLogger.info(task, "Successfully read " + readMessage + " : attrs [" + attributes + "]");

        // Update NetconfTls
        final NscsCMWriterService.WriterSpecificationBuilder specification = writerService.withSpecification();
        if (nodeCredentialFdn != null && !nodeCredentialFdn.equals(currentNodeCredentialFdn)) {
            nscsLogger.info(task, "NetconfTls [" + netconfTlsFdn + "] : nodeCredential attribute change from [" + currentNodeCredentialFdn + "] to ["
                    + nodeCredentialFdn + "]");
            specification.setNotNullAttribute(NetconfTls.NODE_CREDENTIAL, nodeCredentialFdn);
            isNetconfTlsUpdateNeeded = true;
        }
        if (trustCategoryFdn != null && !trustCategoryFdn.equals(currentTrustCategoryFdn)) {
            nscsLogger.info(task, "NetconfTls [" + netconfTlsFdn + "] : trustCategory attribute change from [" + currentTrustCategoryFdn + "] to ["
                    + trustCategoryFdn + "]");
            specification.setNotNullAttribute(NetconfTls.TRUST_CATEGORY, trustCategoryFdn);
            isNetconfTlsUpdateNeeded = true;
        }
        if (isNetconfTlsUpdateNeeded) {
            specification.setFdn(netconfTlsFdn);
            final String updateMessage = NscsLogger.stringifyUpdateParams("NetconfTls", netconfTlsFdn);
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
        return isNetconfTlsUpdateNeeded;
    }

    /**
     * @param task
     * @param mirrorRootFdn
     * @param ldapMo
     * @param nodeCredentialFdn
     * @param trustCategoryFdn
     * @return
     * @throws MissingMoException
     * @throws UnexpectedErrorException
     */
    private boolean checkAndUpdateLdap(final ComEcimConfigureCredentialUsersTask task, final String mirrorRootFdn, final Mo ldapMo,
            final String nodeCredentialFdn, final String trustCategoryFdn) throws MissingMoException, UnexpectedErrorException {
        boolean isLdapUpdateNeeded = false;
        String currentNodeCredentialFdn = null;
        String currentTrustCategoryFdn = null;
        final Map<String, Object> attributes = new HashMap<String, Object>();
        final String requestedAttrs[] = { Ldap.NODE_CREDENTIAL, Ldap.TRUST_CATEGORY };
        final String readMessage = NscsLogger.stringifyReadParams(mirrorRootFdn, ldapMo.type(), requestedAttrs);
        nscsLogger.debug(task, "Reading " + readMessage);
        final String ldapFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, ldapMo, attributes, requestedAttrs);
        if (ldapFdn != null) {
            currentNodeCredentialFdn = (String) attributes.get(Ldap.NODE_CREDENTIAL);
            currentTrustCategoryFdn = (String) attributes.get(Ldap.TRUST_CATEGORY);
            nscsLogger.info(task, "Successfully read " + readMessage + " : attrs [" + attributes + "]");

            // Update Ldap
            final NscsCMWriterService.WriterSpecificationBuilder specification = writerService.withSpecification();
            if (nodeCredentialFdn != null && !nodeCredentialFdn.equals(currentNodeCredentialFdn)) {
                nscsLogger.info(task, "Ldap [" + ldapFdn + "] : nodeCredential attribute change from [" + currentNodeCredentialFdn + "] to ["
                        + nodeCredentialFdn + "]");
                specification.setNotNullAttribute(Ldap.NODE_CREDENTIAL, nodeCredentialFdn);
                isLdapUpdateNeeded = true;
            }
            if (trustCategoryFdn != null && !trustCategoryFdn.equals(currentTrustCategoryFdn)) {
                nscsLogger.info(task, "Ldap [" + ldapFdn + "] : trustCategory attribute change from [" + currentTrustCategoryFdn + "] to ["
                        + trustCategoryFdn + "]");
                specification.setNotNullAttribute(Ldap.TRUST_CATEGORY, trustCategoryFdn);
                isLdapUpdateNeeded = true;
            }
            if (isLdapUpdateNeeded) {
                specification.setFdn(ldapFdn);
                final String updateMessage = NscsLogger.stringifyUpdateParams("Ldap", ldapFdn);
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
        } else {
            final String infoMessage = "Null Ldap FDN found while reading " + readMessage;
            nscsLogger.info(task, infoMessage);
        }
        return isLdapUpdateNeeded;
    }

    /**
     * @param task
     * @param transportFdn
     * @param ikev2PolicyProfileMo
     * @param ikev2PolicyProfileModelInfo
     * @param nodeCredentialFdn
     * @param trustCategoryFdn
     * @return
     * @throws UnexpectedErrorException
     */
    private String checkAndCreateOrUpdateIkev2PolicyProfile(final ComEcimConfigureCredentialUsersTask task, final String transportFdn,
            final Mo ikev2PolicyProfileMo, final NscsModelInfo ikev2PolicyProfileModelInfo, final String nodeCredentialFdn,
            final String trustCategoryFdn) throws UnexpectedErrorException {

        String ikev2PolicyProfileName = null;
        String currentCredentialFdn = null;
        String currentTrustCategoryFdn = null;
        final Map<String, Object> attributes = new HashMap<String, Object>();
        final String requestedAttrs[] = { Ikev2PolicyProfile.CREDENTIAL, Ikev2PolicyProfile.TRUST_CATEGORY };
        final String readMessage = NscsLogger.stringifyReadParams(transportFdn, ikev2PolicyProfileMo.type(), requestedAttrs);
        nscsLogger.debug(task, "Reading " + readMessage);
        final String ikev2PolicyProfileFdn = nscsComEcimNodeUtility.getIkev2PolicyProfileFdn(transportFdn, ikev2PolicyProfileMo, attributes, requestedAttrs,
                readerService.getNormalizableNodeReference(task.getNode()));
        if (ikev2PolicyProfileFdn != null) {
            nscsLogger.info(task, "Already existent Ikev2PolicyProfile [" + ikev2PolicyProfileFdn + "]");
            currentCredentialFdn = (String) attributes.get(Ikev2PolicyProfile.CREDENTIAL);
            currentTrustCategoryFdn = (String) attributes.get(Ikev2PolicyProfile.TRUST_CATEGORY);
            nscsLogger.info(task, "Successfully read " + readMessage + " : attrs [" + attributes + "]");

            // Since this handler is invoked by COMIssueCert workflow, both
            // nodeCredentialFdn and trustCategoryFdn shall be specified.
            // If the Ikev2PolicyProfile MO already exists, its attributes
            // credential and trustCategory can't be changed since they are
            // immutable
            boolean isIkev2PolicyProfileUpdateNeeded = false;
            final NscsCMWriterService.WriterSpecificationBuilder specification = writerService.withSpecification();
            if (!nodeCredentialFdn.equals(currentCredentialFdn)) {
                // Update Ikev2PolicyProfile credential attribute
                nscsLogger.info(task, "Ikev2PolicyProfile [" + ikev2PolicyProfileFdn + "] : credential attribute change from [" + currentCredentialFdn
                        + "] to [" + nodeCredentialFdn + "]");
                isIkev2PolicyProfileUpdateNeeded = true;
                specification.setNotNullAttribute(Ikev2PolicyProfile.CREDENTIAL, nodeCredentialFdn);
            }
            if (trustCategoryFdn != null && !trustCategoryFdn.equals(currentTrustCategoryFdn)) {
                // Update Ikev2PolicyProfile trustCategory attribute
                nscsLogger.info(task, "Ikev2PolicyProfile [" + ikev2PolicyProfileFdn + "] : trustCategory attribute change from ["
                        + currentTrustCategoryFdn + "] to [" + trustCategoryFdn + "]");
                isIkev2PolicyProfileUpdateNeeded = true;
                specification.setNotNullAttribute(Ikev2PolicyProfile.TRUST_CATEGORY, trustCategoryFdn);
            }
            if (isIkev2PolicyProfileUpdateNeeded) {
                // Update Ikev2PolicyProfile MO
                specification.setFdn(ikev2PolicyProfileFdn);
                final String updateMessage = NscsLogger.stringifyUpdateParams("Ikev2PolicyProfile", ikev2PolicyProfileFdn);
                nscsLogger.debug(task, "Updating " + updateMessage);
                try {
                    specification.updateMO();
                } catch (final Exception e) {
                    final String errorMessage = NscsLogger.stringifyException(e) + " while updating " + updateMessage;
                    nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                    throw new UnexpectedErrorException(errorMessage);
                }
                nscsLogger.workFlowTaskHandlerOngoing(task, "Successfully updated " + updateMessage);
            }
        } else {
            nscsLogger.info(task, "Not yet created Ikev2PolicyProfile under Transport [" + transportFdn + "]");
            ikev2PolicyProfileName = createIkev2PolicyProfile(task, transportFdn, ikev2PolicyProfileModelInfo, nodeCredentialFdn, trustCategoryFdn);
        }

        return ikev2PolicyProfileName;
    }

    /**
     * @param task
     * @param transportFdn
     * @param ikev2PolicyProfileModelInfo
     * @param nodeCredentialFdn
     * @param trustCategoryFdn
     * @return
     * @throws UnexpectedErrorException
     */
    private String createIkev2PolicyProfile(final ComEcimConfigureCredentialUsersTask task, final String transportFdn,
            final NscsModelInfo ikev2PolicyProfileModelInfo, final String nodeCredentialFdn, final String trustCategoryFdn)
            throws UnexpectedErrorException {

        final String ikev2PolicyProfileType = ikev2PolicyProfileModelInfo.getName();
        final String ikev2PolicyProfileNamespace = ikev2PolicyProfileModelInfo.getNamespace();
        final String ikev2PolicyProfileVersion = ikev2PolicyProfileModelInfo.getVersion();
        final String ikev2PolicyProfileName = nscsComEcimNodeUtility.getIkev2PolicyProfileMOName();
        final Map<String, Object> ikev2PolicyProfileAttributes = new HashMap<String, Object>();
        ikev2PolicyProfileAttributes.put(ModelDefinition.Ikev2PolicyProfile.CREDENTIAL, nodeCredentialFdn);
        ikev2PolicyProfileAttributes.put(ModelDefinition.Ikev2PolicyProfile.TRUST_CATEGORY, trustCategoryFdn);
        final String createMessage = NscsLogger.stringifyCreateParams(transportFdn, ikev2PolicyProfileType, ikev2PolicyProfileNamespace,
                ikev2PolicyProfileVersion, ikev2PolicyProfileName, ikev2PolicyProfileAttributes);
        nscsLogger.info(task, "Creating " + createMessage);
        try {
            writerService.createMibRoot(transportFdn, ikev2PolicyProfileType, ikev2PolicyProfileNamespace, ikev2PolicyProfileVersion,
                    ikev2PolicyProfileName, ikev2PolicyProfileAttributes);
        } catch (final Exception e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while creating " + createMessage;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        nscsLogger.workFlowTaskHandlerOngoing(task, "Successfully created " + createMessage);
        return ikev2PolicyProfileName;
    }

    private void checkAndUpdateCrlInterface(final ComEcimConfigureCredentialUsersTask task, final NormalizableNodeReference normRef,
            final String trustCategoryFdn) {

        final boolean crlInterfaceUpdateSupported = nodeValidatorUtility.validateNodeTypeForExtCa(normRef);
        final MoObject trustCategoryMoObj = readerService.getMoObjectByFdn(trustCategoryFdn);
        if (crlInterfaceUpdateSupported && trustCategoryMoObj != null) {

            final String inputCrlInterface = task.getInterfaceFdn();
            String actualCrlInterface = null;
            try {
                actualCrlInterface = trustCategoryMoObj.getAttribute(TrustCategory.CRL_INTERFACE);

                if (inputCrlInterface != null && !inputCrlInterface.equals(actualCrlInterface)) {
                    final NscsCMWriterService.WriterSpecificationBuilder trustCategorySpec = writerService.withSpecification();
                    trustCategorySpec.setNotNullAttribute(TrustCategory.CRL_INTERFACE, inputCrlInterface);
                    trustCategorySpec.setFdn(trustCategoryFdn);
                    trustCategorySpec.updateMO();
                }

            } catch (final Exception e) {
                final String updateMessage = NscsLogger.stringifyUpdateParams("CrlInterface", trustCategoryFdn);
                final String errorMessage = NscsLogger.stringifyException(e) + " while updating " + updateMessage;
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            }
            nscsLogger.info(task, "crlInterface changes from [" + actualCrlInterface + "] to [" + inputCrlInterface + "]");

        }
    }

}
