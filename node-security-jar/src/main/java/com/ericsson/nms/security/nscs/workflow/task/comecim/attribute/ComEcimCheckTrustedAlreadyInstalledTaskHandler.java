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

import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TrustCategory;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TrustedCertificate;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.util.NscsTrustedEntityInfo;
import com.ericsson.nms.security.nscs.utilities.NSCSCertificateUtility;
import com.ericsson.nms.security.nscs.utilities.NSCSComEcimNodeUtility;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoAction;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionState;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActionWithParams;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoActions;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimCheckTrustedAlreadyInstalledTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;

/**
 * <p>
 * Task handler for WorkflowTaskType.COM_ECIM_CHECK_TRUSTED_ALREADY_INSTALLED
 * </p>
 *
 * @author elucbot
 */
@WFTaskType(WorkflowTaskType.COM_ECIM_CHECK_TRUSTED_ALREADY_INSTALLED)
@Local(WFTaskHandlerInterface.class)
public class ComEcimCheckTrustedAlreadyInstalledTaskHandler implements WFQueryTaskHandler<ComEcimCheckTrustedAlreadyInstalledTask>, WFTaskHandlerInterface {

    private static final String NOT_INSTALLED = "NOT_INSTALLED";
    private static final String INSTALLED = "INSTALLED";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    @Inject
    private NscsCapabilityModelService capabilityService;

    @Inject
    private NSCSComEcimNodeUtility comEcimNodeUtility;

    @Override
    public String processTask(final ComEcimCheckTrustedAlreadyInstalledTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        final String trustCerts = task.getTrustCerts();
        final String certificateType = NscsNodeUtility.getCertificateTypeFromTrustedCertCategory(trustCerts);
        nscsLogger.info(task, "From task : certificate type [" + certificateType + "]");
        final NodeReference node = task.getNode();
        final NormalizableNodeReference normNode = readerService.getNormalizableNodeReference(node);
        final String mirrorRootFdn = normNode.getFdn();
        final String nodeName = task.getNode().getName();
        String enrollmentCaName = null;

        /*
         * Extract output parameters possibly set by previous handlers
         */
        final Map<String, Serializable> outputParams = task.getOutputParams();
        if (outputParams == null) {
            final String errorMessage = "Output params not yet set! ";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new WorkflowTaskException(errorMessage);
        } else {
            final String serializedEnrollmentInfo = (String) task.getOutputParams().get(WorkflowOutputParameterKeys.ENROLLMENT_INFO.toString());
            final ScepEnrollmentInfoImpl enrollmentInfo = NscsObjectSerializer.readObject(serializedEnrollmentInfo);
            if (enrollmentInfo == null) {
                nscsLogger.info(task, "Enrollment info not set in output params");
            } else {
                enrollmentCaName = enrollmentInfo.getEnrollmentCaName();
                nscsLogger.info(task, "From enrollment info : enrollment CA name [" + enrollmentCaName + "]");
            }
        }
        /*
         * Get CertM MO FDN
         */
        final ComEcimManagedElement targetRootMo = (ComEcimManagedElement) capabilityService.getMirrorRootMo(normNode);
        final Mo certMMo = targetRootMo.systemFunctions.secM.certM;
        final String readCertMMessage = NscsLogger.stringifyReadParams(mirrorRootFdn, certMMo.type());
        nscsLogger.debug(task, "Reading " + readCertMMessage);
        final String certMFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, certMMo);
        if (certMFdn == null || certMFdn.isEmpty()) {
            final String errorMessage = "Error while reading " + readCertMMessage;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new MissingMoException(nodeName, certMMo.type());
        }

        String serializedTrustedEntitiesInfo = null;
        String serializedEnrollmentCaEntityInfo = null;
        Set<NscsTrustedEntityInfo> trustedEntitiesInfo = new HashSet<>();
        if (task.getOutputParams() != null) {
            serializedTrustedEntitiesInfo = (String) task.getOutputParams().get(WorkflowOutputParameterKeys.TRUSTED_ENTITY_INFO.toString());

            trustedEntitiesInfo = NscsObjectSerializer.readObject(serializedTrustedEntitiesInfo);
            serializedEnrollmentCaEntityInfo = (String) task.getOutputParams().get(WorkflowOutputParameterKeys.ENROLLMENT_CA_ENTITY.toString());
        }
        NscsTrustedEntityInfo enrollmentCaEntityInfo = NscsObjectSerializer.readObject(serializedEnrollmentCaEntityInfo);

        /*
         * Get from node installed TrustedCertificate MOs
         */
        final Mo trustedCertificateMo = targetRootMo.systemFunctions.secM.certM.trustedCertificate;
        final String reservedByMoAttribute = comEcimNodeUtility.getTrustedCertificateReservedByMoAttribute(normNode);
        nscsLogger.info(task, "The node supports {} as TrustedCertificate reserved-by MO attribute", reservedByMoAttribute);
        final String[] requestedAttrs = { reservedByMoAttribute, TrustedCertificate.CERTIFICATE_CONTENT, TrustedCertificate.MANAGED_STATE };
        final String readTrustedMessage = NscsLogger.stringifyReadParams(certMFdn, trustedCertificateMo.type(), requestedAttrs);
        nscsLogger.info(task, "Reading from node " + readTrustedMessage);
        final CmResponse nodeTrustedCertificates = readerService.getMos(certMFdn, trustedCertificateMo.type(), trustedCertificateMo.namespace(), requestedAttrs);
        if (nodeTrustedCertificates == null || nodeTrustedCertificates.getCmObjects() == null) {
            final String errorMessage = "Error while reading from node " + readTrustedMessage;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        nscsLogger.info(task, "Found on node [" + nodeTrustedCertificates.getCmObjects().size() + "] MOs while reading " + readTrustedMessage);

        /*
         * Check which trusted CAs are to be installed (trustedEntitiesInfo), which already installed TrustedCertificate MOs are to be reserved (toBeReservedTrustedCertificateFdns). Moreover, if
         * enrollment CA is specified, check if it is to be installed (enrollmentCaEntityInfo) or already present (enrollmentCaFdn).
         */
        final Set<String> toBeReservedTrustedCertificateFdns = new HashSet<String>();
        String enrollmentCaFdn = null;

        final String trustCategoryFdn = comEcimNodeUtility.getTrustCategoryFdn(mirrorRootFdn, targetRootMo, certificateType, normNode);
        List<String> trustCategoryTrustedCertificates = null;
        if (trustCategoryFdn == null || trustCategoryFdn.isEmpty()) {
            nscsLogger.info(task, "TrustCategory [" + trustCategoryFdn + "] not yet created for certificate type [" + certificateType + "]");
        } else {
            final MoObject trustCategoryMoObj = readerService.getMoObjectByFdn(trustCategoryFdn);
            final Mo trustCategoryMo = targetRootMo.systemFunctions.secM.certM.trustCategory;
            if (trustCategoryMoObj == null) {
                final String errorMessage = "TrustCategory [" + trustCategoryFdn + "] not found for node";
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new MissingMoException(task.getNode().getName(), trustCategoryMo.type());
            }
            trustCategoryTrustedCertificates = trustCategoryMoObj.getAttribute(TrustCategory.TRUSTED_CERTIFICATES);
            nscsLogger.info(task, "Found TrustCategory [" + trustCategoryFdn + "] for certificate type [" + certificateType + "] : trustedCertificates [" + trustCategoryTrustedCertificates + "]");
        }

        nscsLogger.info(task, "Searching for already installed trusted CAs");
        final Iterator<NscsTrustedEntityInfo> itPkiTrustedEntitiesInfo = trustedEntitiesInfo.iterator();
        while (itPkiTrustedEntitiesInfo.hasNext()) {
            final NscsTrustedEntityInfo pkiTrustedEntityInfo = itPkiTrustedEntitiesInfo.next();
            final String pkiIssuer = pkiTrustedEntityInfo.getIssuer();
            final BigInteger pkiSerialNumber = pkiTrustedEntityInfo.getSerialNumber();
            if (pkiIssuer == null || pkiSerialNumber == null) {
                nscsLogger.error(task, "PKI returned an invalid trusted entity [" + pkiTrustedEntityInfo.stringify() + "]. Skipping and removing it!");
                itPkiTrustedEntitiesInfo.remove();
                continue;
            } else {
                nscsLogger.info(task, "Current PKI trusted entity : SN [" + pkiSerialNumber + "] issuer [" + pkiIssuer + "]");
                String toBeReservedTrustedCertificateFdn = null;
                boolean isInstalled = false;
                final Iterator<CmObject> itNodeInstalledTrustedCertificates = nodeTrustedCertificates.getCmObjects().iterator();
                while (itNodeInstalledTrustedCertificates.hasNext()) {
                    final CmObject nodeInstalledTrustedCertificate = itNodeInstalledTrustedCertificates.next();
                    final Map<String, Object> nodeInstalledTrustedCertificateAttrs = nodeInstalledTrustedCertificate.getAttributes();
                    final String nodeIssuer = NSCSCertificateUtility.getTrustedCertificateIssuer(nodeInstalledTrustedCertificateAttrs);
                    final BigInteger nodeSerialNumber = NSCSCertificateUtility.getTrustedCertificateSerialNumber(nodeInstalledTrustedCertificateAttrs);
                    nscsLogger.info(task, "Current TrustedCertificate : SN [" + nodeSerialNumber + "] issuer [" + nodeIssuer + "]");
                    if ((nodeSerialNumber != null && nodeSerialNumber.equals(pkiSerialNumber) && nodeIssuer != null && CertDetails.matchesNotAlignedToRfcDN(nodeIssuer, pkiIssuer))) {
                        final String trustedCertificateFdn = nodeInstalledTrustedCertificate.getFdn();
                        final List<String> reservedByMoAttributeValue = NSCSCertificateUtility
                                .getTrustedCertificateReservedByMoAttributeValue(nodeInstalledTrustedCertificateAttrs, reservedByMoAttribute);
                        nscsLogger.info(task, "FOUND TrustedCertificate [{}] : {} [{}]", trustedCertificateFdn, reservedByMoAttribute,
                                reservedByMoAttributeValue);
                        if (reservedByMoAttributeValue != null) {
                            if (reservedByMoAttributeValue.contains(trustCategoryFdn)) {
                                if (trustCategoryTrustedCertificates == null || !trustCategoryTrustedCertificates.contains(trustedCertificateFdn)) {
                                    final String errorMessage = "Inconsistent configuration from node : mismatch between TrustedCertificate [" + trustedCertificateFdn + "] and TrustCategory ["
                                            + trustCategoryFdn + "]";
                                    nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                                    throw new UnexpectedErrorException(errorMessage);
                                }
                                isInstalled = true;
                                nscsLogger.info(task, "Already installed for involved TrustCategory [" + trustCategoryFdn + "]");
                                if (enrollmentCaEntityInfo != null && enrollmentCaEntityInfo.equals(pkiTrustedEntityInfo)) {
                                    enrollmentCaFdn = trustedCertificateFdn;
                                    enrollmentCaEntityInfo = null;
                                    nscsLogger.info(task, "Already installed enrollment CA [" + enrollmentCaFdn + "]");
                                }
                                break;
                            } else {
                                nscsLogger.info(task, "Already installed for other TrustCategory!");
                            }
                            toBeReservedTrustedCertificateFdn = trustedCertificateFdn;
                            if (enrollmentCaEntityInfo != null && enrollmentCaEntityInfo.equals(pkiTrustedEntityInfo)) {
                                enrollmentCaFdn = trustedCertificateFdn;
                                enrollmentCaEntityInfo = null;
                                nscsLogger.info(task, "Already installed enrollment CA [" + enrollmentCaFdn + "]");
                            }
                        } else {
                            /*
                             * Precedence is given to TrustedCertificate reserved by other TrustCategory
                             */
                            if (toBeReservedTrustedCertificateFdn == null || toBeReservedTrustedCertificateFdn.isEmpty()) {
                                toBeReservedTrustedCertificateFdn = trustedCertificateFdn;
                            }
                            if (enrollmentCaEntityInfo != null && enrollmentCaEntityInfo.equals(pkiTrustedEntityInfo) && enrollmentCaFdn == null) {
                                enrollmentCaFdn = trustedCertificateFdn;
                                nscsLogger.info(task, "Candidate already installed enrollment CA [" + enrollmentCaFdn + "]");
                            }
                        }
                    }
                }
                if (isInstalled) {
                    nscsLogger.info(task, "Removing the already installed trusted entity : SN [" + pkiSerialNumber + "] issuer [" + pkiIssuer + "]");
                    itPkiTrustedEntitiesInfo.remove();
                } else {
                    if (toBeReservedTrustedCertificateFdn != null && !toBeReservedTrustedCertificateFdn.isEmpty()) {
                        nscsLogger.info(task, "Removing the to be reserved trusted entity : SN [" + pkiSerialNumber + "] issuer [" + pkiIssuer + "]");
                        toBeReservedTrustedCertificateFdns.add(toBeReservedTrustedCertificateFdn);
                        itPkiTrustedEntitiesInfo.remove();
                    } else {
                        nscsLogger.info(task, "Not yet installed trusted entity : SN [" + pkiSerialNumber + "] issuer [" + pkiIssuer + "]");
                    }
                    if (enrollmentCaEntityInfo != null && enrollmentCaFdn != null) {
                        enrollmentCaEntityInfo = null;
                        nscsLogger.info(task, "Already installed enrollment CA [" + enrollmentCaFdn + "]. It was just candidate.");
                    }
                }
            }
        }

        if (enrollmentCaEntityInfo != null) {
            final String pkiIssuer = enrollmentCaEntityInfo.getIssuer();
            final BigInteger pkiSerialNumber = enrollmentCaEntityInfo.getSerialNumber();
            if (pkiIssuer == null || pkiSerialNumber == null) {
                final String errorMessage = "PKI returned an invalid enrollment CA entity [" + enrollmentCaEntityInfo.stringify() + "].";
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            }
            nscsLogger.info(task, "Searching for enrollment CA : SN [" + pkiSerialNumber + "] issuer [" + pkiIssuer + "]");
            boolean isEnrollmentCaInstalled = false;
            final Iterator<CmObject> itNodeInstalledTrustedCertificates = nodeTrustedCertificates.getCmObjects().iterator();
            while (itNodeInstalledTrustedCertificates.hasNext()) {
                final CmObject nodeInstalledTrustedCertificate = itNodeInstalledTrustedCertificates.next();
                final Map<String, Object> nodeInstalledTrustedCertificateAttrs = nodeInstalledTrustedCertificate.getAttributes();
                final String nodeIssuer = NSCSCertificateUtility.getTrustedCertificateIssuer(nodeInstalledTrustedCertificateAttrs);
                final BigInteger nodeSerialNumber = NSCSCertificateUtility.getTrustedCertificateSerialNumber(nodeInstalledTrustedCertificateAttrs);
                nscsLogger.info(task, "Current TrustedCertificate : SN [" + nodeSerialNumber + "] issuer [" + nodeIssuer + "]");
                if ((nodeSerialNumber != null && nodeSerialNumber.equals(pkiSerialNumber) && nodeIssuer != null && CertDetails.matchesNotAlignedToRfcDN(nodeIssuer, pkiIssuer))) {
                    final String trustedCertificateFdn = nodeInstalledTrustedCertificate.getFdn();
                    final List<String> reservedByMoAttributeValue = NSCSCertificateUtility
                            .getTrustedCertificateReservedByMoAttributeValue(nodeInstalledTrustedCertificateAttrs, reservedByMoAttribute);
                    nscsLogger.info(task, "FOUND TrustedCertificate [{}] for enrollment CA : {} [{}]", trustedCertificateFdn, reservedByMoAttribute,
                            reservedByMoAttributeValue);
                    isEnrollmentCaInstalled = true;
                    enrollmentCaFdn = trustedCertificateFdn;
                    nscsLogger.info(task, "Already installed enrollment CA [" + enrollmentCaFdn + "]");
                    break;
                }
            }
            if (isEnrollmentCaInstalled) {
                nscsLogger.info(task, "Removing the already installed enrollment CA : SN [" + pkiSerialNumber + "] issuer [" + pkiIssuer + "]");
                enrollmentCaEntityInfo = null;
            } else {
                nscsLogger.info(task, "Not yet installed enrollment CA : SN [" + pkiSerialNumber + "] issuer [" + pkiIssuer + "]");
            }

        }

        if ((trustedEntitiesInfo != null && !trustedEntitiesInfo.isEmpty()) || enrollmentCaEntityInfo != null) {
            if (trustedEntitiesInfo != null) {
                nscsLogger.info(task, "There are [" + trustedEntitiesInfo.size() + "] trusted entities to be installed on node.");
                for (final NscsTrustedEntityInfo trustedEntityInfo : trustedEntitiesInfo) {
                    nscsLogger.info(task, "To node : " + trustedEntityInfo.stringify());
                }
            }
            if (toBeReservedTrustedCertificateFdns != null && !toBeReservedTrustedCertificateFdns.isEmpty()) {
                nscsLogger.info(task, "There are [" + toBeReservedTrustedCertificateFdns.size() + "] TrustedCertificate to be reserved on node.");
                for (final String toBeReservedTrustedCertificateFdn : toBeReservedTrustedCertificateFdns) {
                    nscsLogger.info(task, "To node : " + toBeReservedTrustedCertificateFdn);
                }
            }
            if (enrollmentCaEntityInfo != null) {
                nscsLogger.info(task, "There is enrollment CA to be installed on node : " + enrollmentCaEntityInfo.stringify());
            } else {
                if (enrollmentCaFdn != null) {
                    nscsLogger.info(task, "Enrollment CA [" + enrollmentCaName + "] already installed on node at FDN [" + enrollmentCaFdn + "]");
                } else {
                    nscsLogger.info(task, "Enrollment CA not required.");
                }
            }
            return trustNotInstalledOnNode(task, trustedEntitiesInfo, enrollmentCaEntityInfo, certMFdn, toBeReservedTrustedCertificateFdns, enrollmentCaFdn);
        } else {
            if (toBeReservedTrustedCertificateFdns != null && !toBeReservedTrustedCertificateFdns.isEmpty()) {
                nscsLogger.info(task, "There are no trusted entities to be installed on node.");
                nscsLogger.info(task, "There are [" + toBeReservedTrustedCertificateFdns.size() + "] TrustedCertificate to be reserved on node.");
                for (final String toBeReservedTrustedCertificateFdn : toBeReservedTrustedCertificateFdns) {
                    nscsLogger.info(task, "To node : " + toBeReservedTrustedCertificateFdn);
                }
            } else {
                nscsLogger.info(task, "All trusted entities already installed.");
            }
            if (enrollmentCaFdn != null) {
                nscsLogger.info(task, "Enrollment CA [" + enrollmentCaName + "] already installed on node at FDN [" + enrollmentCaFdn + "]");
            } else {
                nscsLogger.info(task, "Enrollment CA not required.");
            }
            return trustAlreadyInstalledOnNode(task, toBeReservedTrustedCertificateFdns, enrollmentCaFdn);
        }

    }

    /**
     *
     * @param task
     * @param toBeReservedTrustedCertificateFdns
     * @param enrollmentCaFdn
     * @param outputParams
     * @return
     */
    private String trustAlreadyInstalledOnNode(final ComEcimCheckTrustedAlreadyInstalledTask task, final Set<String> toBeReservedTrustedCertificateFdns, final String enrollmentCaFdn) {
        final String state = INSTALLED;
        List<String> toBeReservedTrustedCertificateFdnsList = null;
        if (toBeReservedTrustedCertificateFdns != null && !toBeReservedTrustedCertificateFdns.isEmpty()) {
            toBeReservedTrustedCertificateFdnsList = new ArrayList<String>();
            toBeReservedTrustedCertificateFdnsList.addAll(toBeReservedTrustedCertificateFdns);
        }
        return serializeResult(task, state, null, null, null, toBeReservedTrustedCertificateFdnsList, enrollmentCaFdn);
    }

    /**
     *
     * @param task
     * @param trustedEntitiesInfo
     * @param certMFdn
     * @param toBeReservedTrustedCertificateFdns
     * @param enrollmentCaFdn
     * @param enrollmentCaEntityInfo
     * @param outputParams
     * @return
     */
    private String trustNotInstalledOnNode(final ComEcimCheckTrustedAlreadyInstalledTask task, final Set<NscsTrustedEntityInfo> trustedEntitiesInfo,
            final NscsTrustedEntityInfo enrollmentCaEntityInfo, final String certMFdn, final Set<String> toBeReservedTrustedCertificateFdns, final String enrollmentCaFdn) {
        final String state = NOT_INSTALLED;
        final List<NscsTrustedEntityInfo> trustedEntitiesInfoList = new ArrayList<NscsTrustedEntityInfo>();
        trustedEntitiesInfoList.addAll(trustedEntitiesInfo);
        List<String> toBeReservedTrustedCertificateFdnsList = null;
        if (toBeReservedTrustedCertificateFdns != null && !toBeReservedTrustedCertificateFdns.isEmpty()) {
            toBeReservedTrustedCertificateFdnsList = new ArrayList<String>();
            toBeReservedTrustedCertificateFdnsList.addAll(toBeReservedTrustedCertificateFdns);
        }
        return serializeResult(task, state, trustedEntitiesInfoList, enrollmentCaEntityInfo, certMFdn, toBeReservedTrustedCertificateFdnsList, enrollmentCaFdn);
    }

    /**
     * @param task
     * @param result
     * @param trustedEntitiesInfo
     * @param enrollmentCaEntityInfo
     * @param certMFdn
     * @param enrollmentCaFdn
     * @param toBeReservedTrustedCertificateFdns
     * @param outputParams
     * @return It may return null string
     */
    private String serializeResult(final ComEcimCheckTrustedAlreadyInstalledTask task, final String result, final List<NscsTrustedEntityInfo> trustedEntitiesInfo,
            final NscsTrustedEntityInfo enrollmentCaEntityInfo, final String certMFdn, final List<String> toBeReservedTrustedCertificateFdnsList, final String enrollmentCaFdn) {
        Map<String, Serializable> outPutParams = null;
        if (task.getOutputParams() == null) {
            nscsLogger.info(task, "Initializing output params");
            outPutParams = new HashMap<>();
        }else{
            outPutParams = task.getOutputParams();
        }

        /*
         * Serialize trusted entities info in output parameters
         */
        if (trustedEntitiesInfo != null && !trustedEntitiesInfo.isEmpty()) {
            String serializedtrustedEntitiesInfo = null;
            try {
                serializedtrustedEntitiesInfo = NscsObjectSerializer.writeObject(trustedEntitiesInfo);
            } catch (final IOException e1) {
                final String errorMessage = NscsLogger.stringifyException(e1) + " while serializing trusted entities info";
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            }
            outPutParams.put(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString(), serializedtrustedEntitiesInfo);
        }

        /*
         * Serialize to be reserved trusted certificate FDNs in output parameters
         */
        if (toBeReservedTrustedCertificateFdnsList != null && !toBeReservedTrustedCertificateFdnsList.isEmpty()) {
            String serializedToBeReservedTrustedCertificateFdns = null;
            try {
                serializedToBeReservedTrustedCertificateFdns = NscsObjectSerializer.writeObject(toBeReservedTrustedCertificateFdnsList);
            } catch (final IOException e2) {
                final String errorMessage = NscsLogger.stringifyException(e2) + " while serializing to be reserved TrustedCerticate FDNs";
                nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
                throw new UnexpectedErrorException(errorMessage);
            }
            outPutParams.put(WorkflowOutputParameterKeys.TRUSTED_CERTIFICATE_FDN_LIST.toString(), serializedToBeReservedTrustedCertificateFdns);
        }

        /*
         * Serialize enrollment CA entity info in output parameters
         */
        if (enrollmentCaEntityInfo != null) {
            outPutParams.put(WorkflowOutputParameterKeys.ENROLLMENT_CA_ENTITY.toString(), getserializedEnrollmentCaEntityInfo(task, enrollmentCaEntityInfo));
        }

        /*
         * Add enrollment CA certificate FDN in output parameters
         */
        if (enrollmentCaFdn != null && !enrollmentCaFdn.isEmpty()) {
            outPutParams.put(WorkflowOutputParameterKeys.ENROLLMENT_CA_TRUSTED_CERTIFICATE_FDN.toString(), enrollmentCaFdn);
        }

        if (certMFdn != null) {
            // Set MO action to be checked in following service tasks
            final WorkflowMoAction moAction = new WorkflowMoActionWithParams(certMFdn, MoActionWithParameter.ComEcim_CertM_installTrustedCertFromUri);
            moAction.setState(WorkflowMoActionState.CHECK_IT);
            final WorkflowMoActions moActions = new WorkflowMoActions();
            moActions.addTargetAction(moAction);
            final String addActionMessage = "Added to MO_ACTIONS " + NscsLogger.stringifyAction(moAction);
            nscsLogger.workFlowTaskHandlerOngoing(task, addActionMessage);

            // Serialize MO actions in output parameters
            outPutParams.put(WorkflowOutputParameterKeys.MO_ACTIONS.toString(), getSerializedMoActions(task,moActions));
        }
        return getEncodedWfQueryTaskResult(task,result,outPutParams);
    }

    private String getserializedEnrollmentCaEntityInfo(final ComEcimCheckTrustedAlreadyInstalledTask task,final NscsTrustedEntityInfo enrollmentCaEntityInfo){
        String serializedEnrollmentCaEntityInfo = null;
        try {
            serializedEnrollmentCaEntityInfo = NscsObjectSerializer.writeObject(enrollmentCaEntityInfo);
        } catch (final IOException e1) {
            final String errorMessage = NscsLogger.stringifyException(e1) + " while serializing enrollment CA entity info";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        return serializedEnrollmentCaEntityInfo;
    }

    private String getSerializedMoActions(final ComEcimCheckTrustedAlreadyInstalledTask task, final WorkflowMoActions moActions){
        String serializedMoActions = null;
        try {
            serializedMoActions = NscsObjectSerializer.writeObject(moActions);
        } catch (final IOException e1) {
            final String errorMessage = NscsLogger.stringifyException(e1) + " while serializing MO actions";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        return serializedMoActions;
    }

    private String getEncodedWfQueryTaskResult(final ComEcimCheckTrustedAlreadyInstalledTask task, final String result, final Map<String, Serializable> outputParams ){
        String encodedWfQueryTaskResult = null;
        final WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(result, outputParams);
        nscsLogger.debug(task, "Serializing result [" + result + "]");
        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (final IOException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while serializing output params";
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        final String successMessage = "Successfully completed : trusted certificates installation state is [" + result + "]";
        if (INSTALLED.equals(result)) {
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage, NscsLogger.ALREADY_INSTALLED);
        } else {
            nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
        }
        return encodedWfQueryTaskResult;
    }
}
