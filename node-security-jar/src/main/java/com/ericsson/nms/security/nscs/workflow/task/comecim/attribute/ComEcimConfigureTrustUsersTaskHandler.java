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
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Ikev2PolicyProfile;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Ldap;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetconfTls;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.nms.security.nscs.utilities.NSCSComEcimNodeUtility;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimConfigureTrustUsersTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;


/**
 * <p>
 * Task handler for WorkflowTaskType.COM_ECIM_CONFIGURE_TRUST_USERS
 * </p>
 *
 * @author emaborz
 */
@WFTaskType(WorkflowTaskType.COM_ECIM_CONFIGURE_TRUST_USERS)
@Local(WFTaskHandlerInterface.class)
public class ComEcimConfigureTrustUsersTaskHandler implements WFQueryTaskHandler<ComEcimConfigureTrustUsersTask>, WFTaskHandlerInterface {

    private static final String VALID = "VALID";
    private static final String IS_ONLINE_ENROLLMENT = "TRUE";

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
    private NSCSComEcimNodeUtility nscsComEcimNodeUtility;

    @Override
    public String processTask(final ComEcimConfigureTrustUsersTask task) {

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

        // Get CertM MO FDN
        final Mo certMMo = ((ComEcimManagedElement) rootMo).systemFunctions.secM.certM;
        final String readMessage = NscsLogger.stringifyReadParams(mirrorRootFdn, certMMo.type());
        nscsLogger.debug(task, "Reading " + readMessage);
        final String certMFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, certMMo);
        if (certMFdn == null || certMFdn.isEmpty()) {
            final String errorMessage = "Error while reading " + readMessage;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new MissingMoException(nodeName, certMMo.type());
        }

        // Extract output parameters set by previous handlers
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters for certificate type [" + certificateType + "]";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        // Get IsOnlineEnrollment parameter to understand if the handler is invoked in COMIssueCert workflow (true)
        // or in COMIssueTrust workflow (false)
        final boolean isOnlineEnrollment = IS_ONLINE_ENROLLMENT
                .equalsIgnoreCase((String) outputParams.get(WorkflowOutputParameterKeys.IS_ONLINE_ENROLLMENT.toString()));
        nscsLogger.info(task, "From output params : isOnlineEnrollment [" + isOnlineEnrollment + "]");

        // Get TrustCategory FDN parameter
        final String trustCategoryFdn = (String) outputParams.get(WorkflowOutputParameterKeys.TRUST_CATEGORY_FDN.toString());
        if (trustCategoryFdn != null) {
            nscsLogger.info(task, "From output params : TrustCategory FDN [" + trustCategoryFdn + "]");
        } else {
            // This handler can be invoked in both COMIssueTrust and
            // COMIssueCert workflows, in any case the TrustCategory name
            // parameter shall be not null
            final String errorMessage = "Missing TrustCategory FDN param for certType [" + certificateType + "]";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        // Update Users of trusts
        if (CertificateType.OAM.name().equals(certificateType)) {
            // Check and update (if needed) NetconfTls.
            final Mo netconfTlsMo = ((ComEcimManagedElement) rootMo).systemFunctions.sysM.netconfTls;
            final boolean isNetconfTlsUpdateNeeded = checkAndUpdateNetconfTlsForTrust(task, mirrorRootFdn, netconfTlsMo, trustCategoryFdn);
            nscsLogger.debug(task, "NetconfTls : isNetconfTlsUpdateNeeded [" + isNetconfTlsUpdateNeeded + "]");

            // Check and update (if needed) Ldap MO.
            if (capabilityService.isCliCommandSupported(normalizable, NscsCapabilityModelService.LDAP_COMMAND)) {
                final Mo ldapMo = ((ComEcimManagedElement) rootMo).systemFunctions.secM.userManagement.ldapAuthenticationMethod.ldap;
                final boolean isLdapUpdateNeeded = checkAndUpdateLdapForTrust(task, mirrorRootFdn, ldapMo, trustCategoryFdn);
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

                // Check and create or update (if needed) Ikev2PolicyProfile
                final Mo ikev2PolicyProfileMo = ((ComEcimManagedElement) rootMo).transport.ikev2PolicyProfile;
                final String targetCategory = normalizable.getTargetCategory();
                final String nodeType = normalizable.getNeType();
                final String tMI = normalizable.getOssModelIdentity();
                nscsLogger.info(task, "Got targetCategory [{}] nodeType [{}] and tMI [{}]", targetCategory, nodeType, tMI);
                final NscsModelInfo ikev2PolicyProfileModelInfo = nscsModelServiceImpl.getModelInfo(targetCategory, nodeType, tMI,
                        ikev2PolicyProfileMo.type());
                nscsLogger.info(task, "Got modelInfo [" + ikev2PolicyProfileModelInfo + "]");
                final String ikev2PolicyProfileName = checkAndCreateOrUpdateIkev2PolicyProfileForTrust(task, transportFdn, ikev2PolicyProfileMo,
                        ikev2PolicyProfileModelInfo, isOnlineEnrollment, trustCategoryFdn);
                if (ikev2PolicyProfileName != null) {
                    final String ikev2PolicyProfileFdn = ikev2PolicyProfileMo.getFdnByParentFdn(transportFdn, ikev2PolicyProfileName);
                    nscsLogger.debug(task, "Successfully checked and possibly created Ikev2PolicyProfile [" + ikev2PolicyProfileFdn + "]");
                } else {
                    nscsLogger.debug(task, "No Ikev2PolicyProfile MO, it will be created later");
                }
            } else {
                nscsLogger.debug(task, "ikev2PolicyProfile is not supported");
            }
        } else {
            final String errorMessage = "Unknown certificate type [" + certificateType + "]";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        final String result = VALID;
        final String successMessage = "Successfully completed : Trust Users state is [" + result + "]";
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);

        return result;
    }

    /**
     * @param task
     * @param mirrorRootFdn
     * @param netconfTlsMo
     * @param trustCategoryFdn
     * @return
     * @throws MissingMoException
     * @throws UnexpectedErrorException
     */
    private boolean checkAndUpdateNetconfTlsForTrust(final ComEcimConfigureTrustUsersTask task, final String mirrorRootFdn, final Mo netconfTlsMo,
            final String trustCategoryFdn) throws MissingMoException, UnexpectedErrorException {

        boolean isNetconfTlsUpdateNeeded = false;
        String currentTrustCategoryFdn = null;
        final Map<String, Object> attributes = new HashMap<String, Object>();
        final String requestedAttrs[] = { NetconfTls.NODE_CREDENTIAL, NetconfTls.TRUST_CATEGORY };
        final String readMessage = NscsLogger.stringifyReadParams(mirrorRootFdn, netconfTlsMo.type(), requestedAttrs);
        nscsLogger.debug(task, "Reading " + readMessage);
        final String netconfTlsFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, netconfTlsMo, attributes, requestedAttrs);
        if (netconfTlsFdn != null) {
            nscsLogger.info(task, "Successfully read " + readMessage + " : attrs [" + attributes + "]");
            currentTrustCategoryFdn = (String) attributes.get(NetconfTls.TRUST_CATEGORY);
        } else {
            final String errorMessage = "Error while reading " + readMessage;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new MissingMoException(mirrorRootFdn, netconfTlsMo.type());
        }

        if (trustCategoryFdn != null && !trustCategoryFdn.equals(currentTrustCategoryFdn)) {
            // Update NetconfTls trustCategory attribute
            final NscsCMWriterService.WriterSpecificationBuilder specification = writerService.withSpecification();
            nscsLogger.info(task, "NetconfTls [" + netconfTlsFdn + "] : trustCategory attribute change from [" + currentTrustCategoryFdn + "] to ["
                    + trustCategoryFdn + "]");
            isNetconfTlsUpdateNeeded = true;
            specification.setNotNullAttribute(NetconfTls.TRUST_CATEGORY, trustCategoryFdn);
            specification.setFdn(netconfTlsFdn);
            final String updateMessage = NscsLogger.stringifyUpdateParams("NetconfTls", netconfTlsFdn);
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
        return isNetconfTlsUpdateNeeded;
    }

    /**
     * @param task
     * @param mirrorRootFdn
     * @param ldapMo
     * @param trustCategoryFdn
     * @return
     * @throws MissingMoException
     * @throws UnexpectedErrorException
     */
    private boolean checkAndUpdateLdapForTrust(final ComEcimConfigureTrustUsersTask task, final String mirrorRootFdn, final Mo ldapMo,
            final String trustCategoryFdn) throws MissingMoException, UnexpectedErrorException {

        boolean isLdapUpdateNeeded = false;
        String currentTrustCategoryFdn = null;
        final Map<String, Object> attributes = new HashMap<String, Object>();
        final String requestedAttrs[] = { Ldap.NODE_CREDENTIAL, Ldap.TRUST_CATEGORY };
        final String readMessage = NscsLogger.stringifyReadParams(mirrorRootFdn, ldapMo.type(), requestedAttrs);
        nscsLogger.debug(task, "Reading " + readMessage);
        final String ldapFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, ldapMo, attributes, requestedAttrs);
        if (ldapFdn != null) {
            nscsLogger.info(task, "Successfully read " + readMessage + " : attrs [" + attributes + "]");
            currentTrustCategoryFdn = (String) attributes.get(Ldap.TRUST_CATEGORY);
            if (trustCategoryFdn != null && !trustCategoryFdn.equals(currentTrustCategoryFdn)) {
                // Update Ldap trustCategory attribute
                final NscsCMWriterService.WriterSpecificationBuilder specification = writerService.withSpecification();
                nscsLogger.info(task, "Ldap [" + ldapFdn + "] : trustCategory attribute change from [" + currentTrustCategoryFdn + "] to ["
                        + trustCategoryFdn + "]");
                isLdapUpdateNeeded = true;
                specification.setNotNullAttribute(Ldap.TRUST_CATEGORY, trustCategoryFdn);
                specification.setFdn(ldapFdn);
                final String updateMessage = NscsLogger.stringifyUpdateParams("Ldap", ldapFdn);
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
            final String infoMessage = "Null Ldap FDN found while reading " + readMessage;
            nscsLogger.info(task, infoMessage);
        }
        return isLdapUpdateNeeded;
    }

    /**
     * This handler can be invoked in both COMIssueTrust and COMIssueCert workflows.
     *
     * In case of COMIssueTrust the trustCategoryFdn is not null and the isOnlineEnrollment is false. If Ikev2PolicyProfile MO already exists, its
     * trustCategory attribute shall have the same value of trustCategoryFdn while, if Ikev2PolicyProfile MO doesn't yet exist, it is not possible to
     * create it setting only the trustCategory attribute since the model states that 'Either both credential and trustCategory must be set or neither
     * of them'.
     *
     * In case of COMIssueCert the trustCategoryFdn is not null and the isOnlineEnrollment is true. In this case, if Ikev2PolicyProfile MO already
     * exists, its trustCategory attribute shall have the same value of trustCategoryFdn while, if Ikev2PolicyProfile MO doesn't exist, do nothing, it
     * will be created in ComEcimConfigureCredentialUsersTaskHandler at the end of COMIssueCert workflow.
     *
     * @param task
     * @param transportFdn
     * @param ikev2PolicyProfileMo
     * @param ikev2PolicyProfileModelInfo
     * @param isOnlineEnrollment
     * @param trustCategoryFdn
     * @return
     * @throws UnexpectedErrorException
     */
    private String checkAndCreateOrUpdateIkev2PolicyProfileForTrust(final ComEcimConfigureTrustUsersTask task, final String transportFdn,
            final Mo ikev2PolicyProfileMo, final NscsModelInfo ikev2PolicyProfileModelInfo, final boolean isOnlineEnrollment,
            final String trustCategoryFdn) throws UnexpectedErrorException {

        String ikev2PolicyProfileName = null;
        String currentTrustCategoryFdn = null;
        final Map<String, Object> attributes = new HashMap<String, Object>();
        final String requestedAttrs[] = { Ikev2PolicyProfile.CREDENTIAL, Ikev2PolicyProfile.TRUST_CATEGORY };
        final String readMessage = NscsLogger.stringifyReadParams(transportFdn, ikev2PolicyProfileMo.type(), requestedAttrs);
        nscsLogger.debug(task, "Reading " + readMessage);
        final String ikev2PolicyProfileFdn = nscsComEcimNodeUtility.getIkev2PolicyProfileFdn(transportFdn, ikev2PolicyProfileMo, attributes, requestedAttrs,
                readerService.getNormalizableNodeReference(task.getNode()));
        if (ikev2PolicyProfileFdn != null) {
            /**
             * Already existent Ikev2PolicyProfile MO.
             */
            ikev2PolicyProfileName = ikev2PolicyProfileMo.extractName(ikev2PolicyProfileFdn);
            currentTrustCategoryFdn = (String) attributes.get(Ikev2PolicyProfile.TRUST_CATEGORY);
            nscsLogger.debug(task, "Successfully read " + readMessage + " : attrs [" + attributes + "]");

            /**
             * In case of COMIssueTrust, update if needed the trustCategory attribute (it should never occur)
             */
            if (trustCategoryFdn != null && !trustCategoryFdn.equals(currentTrustCategoryFdn)) {
                if (!isOnlineEnrollment) {
                    // Update Ikev2PolicyProfile trustCategory attribute
                    final NscsCMWriterService.WriterSpecificationBuilder specification = writerService.withSpecification();
                    nscsLogger.info(task, "Ikev2PolicyProfile [" + ikev2PolicyProfileFdn + "] : trustCategory attribute change from ["
                            + currentTrustCategoryFdn + "] to [" + trustCategoryFdn + "]");
                    specification.setNotNullAttribute(Ikev2PolicyProfile.TRUST_CATEGORY, trustCategoryFdn);
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
                } else {
                    nscsLogger.info(task, "Ikev2PolicyProfile [" + ikev2PolicyProfileFdn + "] : skipping trustCategory attribute change from ["
                            + currentTrustCategoryFdn + "] to [" + trustCategoryFdn + "]");
                }
            }
        } else {
            /**
             * Not yet existent Ikev2PolicyProfile MO. In case of COMIssueTrust, create it.
             */
            if (!isOnlineEnrollment) {
                nscsLogger.info(task, "Not yet created Ikev2PolicyProfile under Transport [" + transportFdn + "]. Creating it!");
                ikev2PolicyProfileName = createIkev2PolicyProfileForTrust(task, transportFdn, ikev2PolicyProfileModelInfo, trustCategoryFdn);
            } else {
                nscsLogger.info(task, "Not yet created Ikev2PolicyProfile under Transport [" + transportFdn + "]. Skipping its creation!");
            }
        }
        return ikev2PolicyProfileName;
    }

    /**
     * @param task
     * @param transportFdn
     * @param ikev2PolicyProfileModelInfo
     * @param trustCategoryFdn
     * @return
     * @throws UnexpectedErrorException
     */
    private String createIkev2PolicyProfileForTrust(final ComEcimConfigureTrustUsersTask task, final String transportFdn,
            final NscsModelInfo ikev2PolicyProfileModelInfo, final String trustCategoryFdn) throws UnexpectedErrorException {

        final String ikev2PolicyProfileType = ikev2PolicyProfileModelInfo.getName();
        final String ikev2PolicyProfileNamespace = ikev2PolicyProfileModelInfo.getNamespace();
        final String ikev2PolicyProfileVersion = ikev2PolicyProfileModelInfo.getVersion();
        final String ikev2PolicyProfileName = nscsComEcimNodeUtility.getIkev2PolicyProfileMOName();
        final Map<String, Object> ikev2PolicyProfileAttributes = new HashMap<String, Object>();
        ikev2PolicyProfileAttributes.put(ModelDefinition.Ikev2PolicyProfile.TRUST_CATEGORY, trustCategoryFdn);
        final String createMessage = NscsLogger.stringifyCreateParams(transportFdn, ikev2PolicyProfileType, ikev2PolicyProfileNamespace,
                ikev2PolicyProfileVersion, ikev2PolicyProfileName, ikev2PolicyProfileAttributes);
        nscsLogger.debug(task, "Creating " + createMessage);
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

}
