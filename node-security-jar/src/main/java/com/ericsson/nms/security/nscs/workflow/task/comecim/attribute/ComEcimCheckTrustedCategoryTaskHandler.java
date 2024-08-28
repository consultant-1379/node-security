/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelInfo;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelServiceException;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TrustCategory;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TrustedCertificate;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TrustedCertificate.ManagedCertificateState;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.util.NscsTrustedEntityInfo;
import com.ericsson.nms.security.nscs.utilities.ComEcimMoNaming;
import com.ericsson.nms.security.nscs.utilities.NSCSCertificateUtility;
import com.ericsson.nms.security.nscs.utilities.NSCSComEcimNodeUtility;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimCheckTrustedCategoryTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * <p>
 * Task handler for WorkflowTaskType.COM_ECIM_CHECK_TRUSTED_ALREADY_INSTALLED
 * </p>
 *
 * @author elucbot
 */
@WFTaskType(WorkflowTaskType.COM_ECIM_CHECK_TRUSTED_CATEGORY)
@Local(WFTaskHandlerInterface.class)
public class ComEcimCheckTrustedCategoryTaskHandler implements WFQueryTaskHandler<ComEcimCheckTrustedCategoryTask>, WFTaskHandlerInterface {

    private static final String VALID = "VALID";

    private static final String TRUSTED_CERT_CERTIFICATE_CONTENT = TrustedCertificate.CERTIFICATE_CONTENT;
    private static final String TRUSTED_CERT_MANAGED_STATE = TrustedCertificate.MANAGED_STATE;

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
    private ComEcimMoNaming comEcimMoNaming;

    @Inject
    private NSCSComEcimNodeUtility comEcimNodeUtility;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    @Override
    public String processTask(final ComEcimCheckTrustedCategoryTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        final String trustCerts = task.getTrustCerts();
        final String certificateType = NscsNodeUtility.getCertificateTypeFromTrustedCertCategory(trustCerts);
        nscsLogger.info(task, "From task : certificateType [" + certificateType + "]");
        final NodeReference node = task.getNode();
        final NormalizableNodeReference normNode = readerService.getNormalizableNodeReference(node);
        final String mirrorRootFdn = normNode.getFdn();
        final String nodeName = task.getNode().getName();
        final String targetCategory = normNode.getTargetCategory();
        final String nodeType = normNode.getNeType();
        final String tMI = normNode.getOssModelIdentity();
        final String modelInfoParams = String.format("From task: targetCategory [%s] nodeType [%s] tMI [%s]", targetCategory, nodeType, tMI);

        /*
         * Extract output parameters possibly set by previous handlers
         */
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Missing internal parameters for certificate type [" + certificateType + "]";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }

        final ComEcimManagedElement targetRootMo = (ComEcimManagedElement) capabilityService.getMirrorRootMo(normNode);
        final Mo trustCategoryMo = targetRootMo.systemFunctions.secM.certM.trustCategory;
        NscsModelInfo trustCategoryModelInfo = null;
        final Set<String> toBeAssociatedTrustedCertificateFdns = new HashSet<String>();

        /*
         * Get CertM MO FDN
         */
        final Mo certMMo = targetRootMo.systemFunctions.secM.certM;
        final String readMessage = NscsLogger.stringifyReadParams(mirrorRootFdn, certMMo.type());
        nscsLogger.debug(task, "Reading " + readMessage);
        final String certMFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, certMMo);
        if (certMFdn == null || certMFdn.isEmpty()) {
            final String errorMessage = "Error while reading " + readMessage;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new MissingMoException(nodeName, certMMo.type());
        }

        /*
         * Extract the to be installed trusted entities
         */
        final String serializedtrustedEntitiesInfo = (String) outputParams.get(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString());
        final List<NscsTrustedEntityInfo> trustedEntitiesInfo = NscsObjectSerializer.readObject(serializedtrustedEntitiesInfo);
        final String serializedEnrollmentCaEntityInfo = (String) outputParams.get(WorkflowOutputParameterKeys.ENROLLMENT_CA_ENTITY.toString());
        NscsTrustedEntityInfo enrollmentCaEntityInfo = NscsObjectSerializer.readObject(serializedEnrollmentCaEntityInfo);
        String enrollmentCaFdn = null;

        /*
         * Extract the to be reserved TrustedCertificate FDNs
         */
        final String serializedToBeReservedTrustedCertificateFdns = (String) outputParams
                .get(WorkflowOutputParameterKeys.TRUSTED_CERTIFICATE_FDN_LIST.toString());
        final List<String> toBeReservedTrustedCertificateFdns = NscsObjectSerializer.readObject(serializedToBeReservedTrustedCertificateFdns);
        if ((trustedEntitiesInfo == null || trustedEntitiesInfo.isEmpty()) && (enrollmentCaEntityInfo == null)
                && (toBeReservedTrustedCertificateFdns == null || toBeReservedTrustedCertificateFdns.isEmpty())) {
            nscsLogger.info(task, "From output params : there were neither trusted entities to be installed nor TrustedCertificate to be reserved");
        } else {
            if (trustedEntitiesInfo != null) {
                nscsLogger.info(task, "From output params : there were [" + trustedEntitiesInfo.size() + "] trusted entities to be installed");
            }
            if (enrollmentCaEntityInfo != null) {
                nscsLogger.info(task, "From output params : there was enrollment CA entity to be installed");
            }
            if (toBeReservedTrustedCertificateFdns != null) {
                nscsLogger.info(task,
                        "From output params : there were [" + toBeReservedTrustedCertificateFdns.size() + "] TrustedCertificate to be reserved");
            }

            final Mo trustedCertificateMo = targetRootMo.systemFunctions.secM.certM.trustedCertificate;

            /*
             * Get TrustedCertificate MOs of given node
             */
            final String reservedByMoAttribute = comEcimNodeUtility.getTrustedCertificateReservedByMoAttribute(normNode);
            nscsLogger.info(task, "The node supports {} as TrustedCertificate reserved-by MO attribute", reservedByMoAttribute);
            final String[] requestedAttrs = { reservedByMoAttribute, TRUSTED_CERT_CERTIFICATE_CONTENT, TRUSTED_CERT_MANAGED_STATE };
            final String readTrustedMessage = NscsLogger.stringifyReadParams(mirrorRootFdn, trustedCertificateMo.type(), requestedAttrs);
            nscsLogger.info(task, "Reading from node " + readTrustedMessage);
            final CmResponse cmResponseInstalledTrustedCertificates = readerService.getMos(mirrorRootFdn, trustedCertificateMo.type(),
                    trustedCertificateMo.namespace(), requestedAttrs);
            if (cmResponseInstalledTrustedCertificates == null || cmResponseInstalledTrustedCertificates.getCmObjects() == null
                    || cmResponseInstalledTrustedCertificates.getCmObjects().isEmpty()) {
                final String errorMessage = "Error while reading from node " + readTrustedMessage;
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            }
            nscsLogger.info(task,
                    "Found on node [" + cmResponseInstalledTrustedCertificates.getCmObjects().size() + "] MOs while reading " + readTrustedMessage);

            for (final CmObject cmObject : cmResponseInstalledTrustedCertificates.getCmObjects()) {

                final Map<String, Object> trustedCertificateAttrs = cmObject.getAttributes();
                boolean isToBeInstalledTrustedCertificate = false;
                final String trustedCertificateFdn = cmObject.getFdn();
                final String issuerNode = NSCSCertificateUtility.getTrustedCertificateIssuer(trustedCertificateAttrs);
                final BigInteger serialNode = NSCSCertificateUtility.getTrustedCertificateSerialNumber(trustedCertificateAttrs);
                final List<String> reservedByMoAttributeValue = NSCSCertificateUtility
                        .getTrustedCertificateReservedByMoAttributeValue(trustedCertificateAttrs, reservedByMoAttribute);
                nscsLogger.info(task, "Current node TrustedCertificate[{}]: SN [{}] issuer [{}] {} [{}]", trustedCertificateFdn, serialNode,
                        issuerNode, reservedByMoAttribute, reservedByMoAttributeValue);

                /*
                 * Check if current TrustedCertificate corresponds to one of the to be installed trusted entities (if any), only not associated
                 * TrustedCertificate can be involved.
                 */
                if ((trustedEntitiesInfo != null) && (reservedByMoAttributeValue == null || reservedByMoAttributeValue.isEmpty())) {
                    final Iterator<NscsTrustedEntityInfo> itTrustedEntitiesInfo = trustedEntitiesInfo.iterator();
                    while (itTrustedEntitiesInfo.hasNext()) {
                        final NscsTrustedEntityInfo trustedEntityInfo = itTrustedEntitiesInfo.next();
                        final String issuer = trustedEntityInfo.getIssuer();
                        final BigInteger serial = trustedEntityInfo.getSerialNumber();

                        if (issuer == null || serial == null) {
                            nscsLogger.error(task, "PKI returned an invalid CA [" + trustedEntityInfo.stringify() + "]. Skipping and removing it!");
                            itTrustedEntitiesInfo.remove();
                            continue;
                        } else {
                            nscsLogger.info(task, "Current trusted entity to be installed : " + trustedEntityInfo.stringify());
                            if ((serialNode != null && serialNode.equals(serial) && issuerNode != null
                                    && CertDetails.matchesNotAlignedToRfcDN(issuerNode, issuer))) {
                                isToBeInstalledTrustedCertificate = true;
                                nscsLogger.info(task, "Found not associated installed TrustedCertificate[" + trustedCertificateFdn + "]");

                                /*
                                 * Add current TrustedCertificate FDN to the to be associated set
                                 */
                                toBeAssociatedTrustedCertificateFdns.add(trustedCertificateFdn);
                                itTrustedEntitiesInfo.remove();
                                break;
                            }
                        }
                    }
                }

                if (enrollmentCaEntityInfo != null) {
                    final String issuer = enrollmentCaEntityInfo.getIssuer();
                    final BigInteger serial = enrollmentCaEntityInfo.getSerialNumber();
                    if (issuer != null && serial != null) {
                        nscsLogger.info(task, "Enrollment CA entity to be installed : " + enrollmentCaEntityInfo.stringify());
                        if ((serial.equals(serialNode) && issuerNode != null && CertDetails.matchesNotAlignedToRfcDN(issuerNode, issuer))) {
                            nscsLogger.info(task, "FOUND enrollment CA installed TrustedCertificate[" + trustedCertificateFdn + "]");
                            enrollmentCaFdn = trustedCertificateFdn;
                            enrollmentCaEntityInfo = null;
                        }
                    }
                }

                /*
                 * If the current TrustedCertificate does not correspond to one of the to be installed trusted entities, check if it is one of the to
                 * be reserved TrustedCertificate FDNs (if any).
                 */
                if (!isToBeInstalledTrustedCertificate && toBeReservedTrustedCertificateFdns != null) {
                    final Iterator<String> itToBeReservedTrustedCertificateFdns = toBeReservedTrustedCertificateFdns.iterator();
                    while (itToBeReservedTrustedCertificateFdns.hasNext()) {
                        final String toBeReservedTrustedCertificateFdn = itToBeReservedTrustedCertificateFdns.next();

                        if (toBeReservedTrustedCertificateFdn == null || toBeReservedTrustedCertificateFdn.isEmpty()) {
                            nscsLogger.error(task,
                                    "Previous workflow handlers passed an invalid to be reserved TrustedCertificate FDN. Skipping and removing it!");
                            itToBeReservedTrustedCertificateFdns.remove();
                            continue;
                        } else {
                            nscsLogger.info(task, "Current to be reserved TrustedCertificate FDN : " + toBeReservedTrustedCertificateFdn);
                            if (toBeReservedTrustedCertificateFdn.equals(trustedCertificateFdn)) {
                                nscsLogger.info(task, "Found to be reserved TrustedCertificate [" + trustedCertificateFdn + "]");
                                /*
                                 * Add current TrustedCertificate to the to be associated set
                                 */
                                toBeAssociatedTrustedCertificateFdns.add(trustedCertificateFdn);
                                itToBeReservedTrustedCertificateFdns.remove();
                                break;
                            }
                        }
                    }
                }
            }

            /*
             * Check that all to be installed trusted entity certificates have been distributed and all the to be reserved TrustedCertificate are
             * present
             */
            if ((trustedEntitiesInfo != null && !trustedEntitiesInfo.isEmpty()) || (enrollmentCaEntityInfo != null)) {
                // Node doesn't contain all expected to be installed trusted certificates
                if (trustedEntitiesInfo != null) {
                    nscsLogger.error(task, "There are still [" + trustedEntitiesInfo.size() + "] trusted entities to be installed on node.");
                    for (final NscsTrustedEntityInfo trustedEntityInfo : trustedEntitiesInfo) {
                        nscsLogger.info(task, "Missing on node : " + trustedEntityInfo.stringify());
                    }
                }
                if (enrollmentCaEntityInfo != null) {
                    nscsLogger.info(task, "Enrollment CA missing on node : " + enrollmentCaEntityInfo.stringify());
                }
                final String errorMessage = "Not all trusted entities have been correctly installed on node";
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            } else if (toBeReservedTrustedCertificateFdns != null && !toBeReservedTrustedCertificateFdns.isEmpty()) {
                // Node doesn't contain all expected to be reserved TrustedCertificate
                nscsLogger.error(task,
                        "There are still [" + toBeReservedTrustedCertificateFdns.size() + "] TrustedCertificate to be reserved on node.");
                for (final String toBeReservedTrustedCertificateFdn : toBeReservedTrustedCertificateFdns) {
                    nscsLogger.info(task, "Missing on node : " + toBeReservedTrustedCertificateFdn);
                }
                final String errorMessage = "Not all trusted certificates have been correctly reserved on node";
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            } else {
                /*
                 * All required trusted entities have been correctly installed and to be reserved TrustedCertificate correctly identified
                 */
                nscsLogger.info(task, "All trusted entities have been correctly installed and reserved.");
            }

            /*
             * Check and update managedState attribute of involved TrustedCertificate MOs
             */
            final Iterator<String> itTrustedCertificates = toBeAssociatedTrustedCertificateFdns.iterator();
            while (itTrustedCertificates.hasNext()) {
                final String trustedCertificateFdn = itTrustedCertificates.next();
                final MoObject trustedCertificateMoObj = readerService.getMoObjectByFdn(trustedCertificateFdn);
                if (trustedCertificateMoObj == null) {
                    final String errorMessage = "TrustedCertificate [" + trustedCertificateFdn + "] not found for node";
                    nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                    throw new MissingMoException(nodeName, trustedCertificateMo.type());
                }
                final String managedState = ManagedCertificateState.ENABLED.name();
                final boolean isTrustedCertificateUpdateNeeded = checkAndUpdateTrustedCertificate(task, trustedCertificateFdn,
                        trustedCertificateMoObj, managedState);
                nscsLogger.debug(task, "TrustedCertificate [" + trustedCertificateFdn + "] : isTrustedCertificateUpdateNeeded ["
                        + isTrustedCertificateUpdateNeeded + "]");
            }
        }

        // Check whether the TrustCategory has already been created
        String trustCategoryFdn = comEcimNodeUtility.getTrustCategoryFdn(mirrorRootFdn, targetRootMo, certificateType, normNode);
        MoObject trustCategoryMoObj = null;
        boolean isToBeCreatedTrustCategory = false;
        String trustCategoryName = null;
        if (trustCategoryFdn != null && !trustCategoryFdn.isEmpty()) {
            // The TrustCategory should be "already created"
            trustCategoryName = trustCategoryMo.extractName(trustCategoryFdn);
            nscsLogger.debug(task, "TrustCategory [" + trustCategoryFdn + "] should be already created");
            trustCategoryMoObj = readerService.getMoObjectByFdn(trustCategoryFdn);
            if (trustCategoryMoObj == null) {
                final String errorMessage = "TrustCategory [" + trustCategoryFdn + "] not found for node";
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new MissingMoException(nodeName, trustCategoryMo.type());
            }
            nscsLogger.info(task, "Already created TrustCategory [" + trustCategoryFdn + "]");
        } else {
            /*
             * The TrustCategory should be "not yet created", it could be already present as result of a previous failed enrollment
             */
            try {
                trustCategoryModelInfo = nscsModelServiceImpl.getModelInfo(targetCategory, nodeType, tMI, trustCategoryMo.type());
            } catch (NscsModelServiceException | IllegalArgumentException e) {
                final String errorMessage = "Got null NSCS Model Infos for " + modelInfoParams;
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            }
            if (trustCategoryModelInfo == null) {
                final String errorMessage = "Got null NSCS Model Infos for " + modelInfoParams;
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            }
            nscsLogger.info(task, "Successfully got model info " + trustCategoryModelInfo);
            trustCategoryName = comEcimMoNaming.getDefaultName(trustCategoryModelInfo.getName(), certificateType, normNode);
            trustCategoryFdn = trustCategoryMo.getFdnByParentFdn(certMFdn, trustCategoryName);
            nscsLogger.info(task, "TrustCategory [" + trustCategoryFdn + "] should be not yet created");
            trustCategoryMoObj = readerService.getMoObjectByFdn(trustCategoryFdn);
            if (trustCategoryMoObj == null) {
                isToBeCreatedTrustCategory = true;
                nscsLogger.info(task, "TrustCategory [" + trustCategoryFdn + "] not yet created");
            } else {
                nscsLogger.info(task, "TrustCategory [" + trustCategoryFdn + "] already created without credential user");
            }
        }

        if (!isToBeCreatedTrustCategory) {
            /*
             * The TrustCategory has been already created. Check if trustedCertificates attribute of already created TrustCategory shall be updated
             */
            if (toBeAssociatedTrustedCertificateFdns != null && !toBeAssociatedTrustedCertificateFdns.isEmpty()) {
                final boolean isTrustCategoryUpdateNeeded = checkAndUpdateTrustCategory(task, trustCategoryFdn, trustCategoryMoObj,
                        toBeAssociatedTrustedCertificateFdns);
                nscsLogger.debug(task,
                        "TrustCategory [" + trustCategoryFdn + "] : isTrustCategoryUpdateNeeded [" + isTrustCategoryUpdateNeeded + "]");
            } else {
                nscsLogger.info(task, "TrustCategory [" + trustCategoryFdn + "] doesn't require update");
            }
        } else {
            // Not yet created trust category
            final String toBeCreatedTrustCategoryName = comEcimMoNaming.getDefaultName(trustCategoryModelInfo.getName(), certificateType, normNode);
            trustCategoryName = createTrustCategory(task, certMFdn, toBeCreatedTrustCategoryName, trustCategoryModelInfo,
                    toBeAssociatedTrustedCertificateFdns);
            trustCategoryFdn = trustCategoryMo.getFdnByParentFdn(certMFdn, trustCategoryName);
        }

        return trustCategoryIsValid(task, trustCategoryFdn, enrollmentCaFdn, outputParams);
    }

    /**
     * @param task
     * @param certMFdn
     * @param trustCategoryName
     * @param trustCategoryModelInfo
     * @param toBeAssociatedTrustedCertificateFdns
     * @return
     * @throws UnexpectedErrorException
     */
    private String createTrustCategory(final ComEcimCheckTrustedCategoryTask task, final String certMFdn, final String trustCategoryName,
            final NscsModelInfo trustCategoryModelInfo, final Set<String> toBeAssociatedTrustedCertificateFdns) throws UnexpectedErrorException {

        final String trustCategoryType = trustCategoryModelInfo.getName();
        final String trustCategoryNamespace = trustCategoryModelInfo.getNamespace();
        final String trustCategoryVersion = trustCategoryModelInfo.getVersion();
        final Map<String, Object> trustCategoryAttributes = new HashMap<String, Object>();
        if (toBeAssociatedTrustedCertificateFdns == null || toBeAssociatedTrustedCertificateFdns.isEmpty()) {
            nscsLogger.info(task, "No TrustedCertificate MO shall be associated with to be created TrustCategory [" + trustCategoryName + "]");
        } else {
            /*
             * Configure attribute 'trustedCertificates' with list of TrustedCertificate MOs to be associated
             */
            final List<String> trustedCertificates = new ArrayList<String>();
            trustedCertificates.addAll(toBeAssociatedTrustedCertificateFdns);
            trustCategoryAttributes.put(TrustCategory.TRUSTED_CERTIFICATES, trustedCertificates);
            if (task.getInterfaceFdn() != null) {
                trustCategoryAttributes.put("crlInterface", task.getInterfaceFdn());
            }

        }
        final String createMessage = NscsLogger.stringifyCreateParams(certMFdn, trustCategoryType, trustCategoryNamespace, trustCategoryVersion,
                trustCategoryName, trustCategoryAttributes);
        nscsLogger.info(task, "Creating " + createMessage);
        try {
            writerService.createMo(certMFdn, trustCategoryType, trustCategoryNamespace, trustCategoryVersion, trustCategoryName,
                    trustCategoryAttributes);
        } catch (final Exception e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while creating " + createMessage;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        nscsLogger.workFlowTaskHandlerOngoing(task, "Successfully created " + createMessage);
        return trustCategoryName;
    }

    /**
     * @param task
     * @param trustCategoryFdn
     * @param trustCategoryMoObj
     * @param toBeAssociatedTrustedCertificateFdns
     * @return
     * @throws UnexpectedErrorException
     */
    private boolean checkAndUpdateTrustCategory(final ComEcimCheckTrustedCategoryTask task, final String trustCategoryFdn,
            final MoObject trustCategoryMoObj, final Set<String> toBeAssociatedTrustedCertificateFdns) throws UnexpectedErrorException {

        boolean isTrustCategoryUpdateNeeded = false;
        if (toBeAssociatedTrustedCertificateFdns == null || toBeAssociatedTrustedCertificateFdns.isEmpty()) {
            nscsLogger.info(task,
                    "No new TrustedCertificate MOs shall be associated with TrustCategory [" + trustCategoryFdn + "], skipping operation...");
        } else {
            /*
             * Update already created TrustCategory MO adding the to be associated TrustedCertificate MOs to existing ones
             */
            isTrustCategoryUpdateNeeded = true;
            final List<String> currentTrustedCertificateFdns = trustCategoryMoObj.getAttribute(TrustCategory.TRUSTED_CERTIFICATES);

            nscsLogger.info(task, "TrustCategory [" + trustCategoryFdn + "] : current trustedCertificates [" + currentTrustedCertificateFdns + "]");
            if (currentTrustedCertificateFdns != null && !currentTrustedCertificateFdns.isEmpty()) {
                toBeAssociatedTrustedCertificateFdns.addAll(currentTrustedCertificateFdns);
                nscsLogger.info(task,
                        "TrustCategory [" + trustCategoryFdn + "] : updating trustedCertificates to [" + toBeAssociatedTrustedCertificateFdns + "]");
            }
            final NscsCMWriterService.WriterSpecificationBuilder specification = writerService.withSpecification();
            final List<String> trustedCertificates = new ArrayList<String>();
            trustedCertificates.addAll(toBeAssociatedTrustedCertificateFdns);

            specification.setNotNullAttribute(TrustCategory.TRUSTED_CERTIFICATES, trustedCertificates);
            specification.setFdn(trustCategoryFdn);

            if (task.getInterfaceFdn() != null) {
                final String crlInterface = trustCategoryMoObj.getAttribute("crlInterface");
                if (!task.getInterfaceFdn().equals(crlInterface)) {
                    specification.setAttribute("crlInterface", task.getInterfaceFdn());
                }
            }

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
        return isTrustCategoryUpdateNeeded;
    }

    /**
     * @param task
     * @param trustedCertificateFdn
     * @param trustedCertificateMoObj
     * @param managedState
     * @return
     * @throws UnexpectedErrorException
     */
    private boolean checkAndUpdateTrustedCertificate(final ComEcimCheckTrustedCategoryTask task, final String trustedCertificateFdn,
            final MoObject trustedCertificateMoObj, final String managedState) throws UnexpectedErrorException {

        boolean isTrustedCertificateUpdateNeeded = false;

        /*
         * Update already created TrustCategory MO adding the to be associated TrustedCertificate MOs to existing ones
         */
        isTrustedCertificateUpdateNeeded = true;
        final NscsCMWriterService.WriterSpecificationBuilder trustedCertificateSpec = writerService.withSpecification();
        final String actualManagedState = trustedCertificateMoObj.getAttribute(TrustedCertificate.MANAGED_STATE);
        if (managedState != null && !managedState.equals(actualManagedState)) {
            trustedCertificateSpec.setNotNullAttribute(TrustedCertificate.MANAGED_STATE, managedState);
            isTrustedCertificateUpdateNeeded = true;
            nscsLogger.info(task, "managedState changes from [" + actualManagedState + "] to [" + managedState + "]");
        }
        if (isTrustedCertificateUpdateNeeded) {
            trustedCertificateSpec.setFdn(trustedCertificateFdn);
            final String updateMessage = NscsLogger.stringifyUpdateParams("TrustedCertificate", trustedCertificateFdn);
            nscsLogger.info(task, "Updating " + updateMessage);
            try {
                trustedCertificateSpec.updateMO();
            } catch (final Exception e) {
                final String errorMessage = NscsLogger.stringifyException(e) + " while updating " + updateMessage;
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            }
            nscsLogger.workFlowTaskHandlerOngoing(task, "Successfully updated " + updateMessage);
        }
        return isTrustedCertificateUpdateNeeded;
    }

    /**
     *
     * @param task
     * @param trustCategoryFdn
     * @param enrollmentCaFdn
     * @param outputParams
     * @return
     */
    private String trustCategoryIsValid(final ComEcimCheckTrustedCategoryTask task, final String trustCategoryFdn, final String enrollmentCaFdn,
            final Map<String, Serializable> outputParams) {
        final String state = VALID;
        return serializeResult(task, state, trustCategoryFdn, enrollmentCaFdn, outputParams);
    }

    /**
     * @param task
     * @param result
     * @param trustCategoryFdn
     * @param enrollmentCaFdn
     * @return It may return null string
     */
    private String serializeResult(final ComEcimCheckTrustedCategoryTask task, final String result, final String trustCategoryFdn,
            final String enrollmentCaFdn, Map<String, Serializable> outputParams) {
        String encodedWfQueryTaskResult = null;
        if (outputParams == null) {
            nscsLogger.info(task, "Initializing output params!");
            outputParams = new HashMap<String, Serializable>();
        }
        if (trustCategoryFdn != null) {
            outputParams.put(WorkflowOutputParameterKeys.TRUST_CATEGORY_FDN.toString(), trustCategoryFdn);
        }
        if (enrollmentCaFdn != null) {
            outputParams.put(WorkflowOutputParameterKeys.ENROLLMENT_CA_TRUSTED_CERTIFICATE_FDN.toString(), enrollmentCaFdn);
        }
        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(result, outputParams);

        nscsLogger.debug(task, "Serializing result [" + result + "]");
        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while serializing output params";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        final String successMessage = "Successfully completed : TrustCategory [" + trustCategoryFdn + "] state is [" + result + "]";
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        return encodedWfQueryTaskResult;
    }
}
