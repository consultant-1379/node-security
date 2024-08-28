package com.ericsson.nms.security.nscs.handler.command.impl;

import java.util.*;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.*;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.pib.configuration.ConfigurationListener;
import com.ericsson.nms.security.nscs.pki.NscsPkiEntitiesManagerJar;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.oss.itpf.sdk.recording.ErrorSeverity;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;

public class CertificateReissueAuto {

    public static final String xsdValidatorFileName = "ValidatorInputForCertIssue.xsd";

    @Inject
    private SystemRecorder systemRecorder;

    @Inject
    private Logger logger;

    @EJB
    private NscsCommandManager commandManager;

    @EJB
    private NscsPkiEntitiesManagerIF nscsPkiManager;

    @Inject
    ConfigurationListener configurationListener;

    @Inject
    private NscsJobCacheHandler cacheHandler;

    public void process() throws NscsServiceException {

        logger.info("Reissue certificate auto");
        final String inputReason = "";

        List<Entity> entityList = null;
        //        final long deltaTime = (7 * 24 * 60 * 60 * 1000);

        final Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, configurationListener.getPibNeCertAutoRenewalTimer());
        final Date validityEndDate = c.getTime();

        logger.info("getEntitiesByCategoryAndValidate with validityEndDate " + validityEndDate);
        try {
            entityList = nscsPkiManager.getEntitiesByCategoryWithInvalidCertificate(validityEndDate,
                    configurationListener.getPibNeCertAutoRenewalMax(), NodeEntityCategory.IPSEC, NodeEntityCategory.OAM);
        } catch (final NscsPkiEntitiesManagerException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while getEntitiesByCategoryWithInvalidCertificate";
            logger.error(errorMessage);
            throw new InvalidArgumentValueException();
        }

        if (entityList == null || entityList.isEmpty()) {
            logger.info("No entities retrieved");
        } else {
            logger.info("entityList size: {}, entity: {}", entityList.size(), printEntityNamesAndSerialNumber(entityList));

            final String certType = "";
            final List<Entity> validEntities = entityList;
            final Map<Entity, NodeReference> associatedNodesEntity = new HashMap<>();
            final List<Entity> entitiesWithNode = new ArrayList<>();
            final List<Entity> entitiesWithoutNode = new ArrayList<>();
            for (final Entity entity : validEntities) {
                final String entityNodeName = NodeValidatorUtility.getNodeNameFromEntity(entity);

                logger.debug("nodeName {}", entityNodeName);
                if (!entityNodeName.isEmpty() && commandManager.isNodePresent(entityNodeName)) {
                    entitiesWithNode.add(entity);
                    final NodeReference associatedNode = new NodeRef(entityNodeName);
                    associatedNodesEntity.put(entity, associatedNode);
                } else {
                    entitiesWithoutNode.add(entity);
                }
            }

            if (!entitiesWithoutNode.isEmpty()) {
                logger.warn("Following valid entities have no nodes associated: {}", printEntityNames(entitiesWithoutNode));
            }

            // verify associated nodes are valid
            final Map<Entity, NodeReference> validNodes = new HashMap<>();
            final Map<Entity, NscsServiceException> blockingErrors = new HashMap<>();
            final Map<String, String[]> nonBlockingErrors = new HashMap<>();
            final List<Entity> dummyList = new ArrayList<>();
            final boolean areInputNodesValid = commandManager.validateNodesForCertificateReissue(associatedNodesEntity, dummyList, validNodes,
                    blockingErrors, nonBlockingErrors);

            final Map<Entity, NodeReference> validNodesForOam = new HashMap<>();
            final Map<Entity, NodeReference> validNodesForIpsec = new HashMap<>();
            if (validNodes != null && !validNodes.isEmpty()) {
                for (final Map.Entry<Entity, NodeReference> validNode : validNodes.entrySet()) {
                    final Entity entity = validNode.getKey();
                    final NodeEntityCategory nodeEntityCategory = NscsPkiEntitiesManagerJar.findNodeEntityCategory(entity.getCategory());
                    if (NodeEntityCategory.OAM.equals(nodeEntityCategory)) {
                        validNodesForOam.put(entity, validNode.getValue());
                    } else if (NodeEntityCategory.IPSEC.equals(nodeEntityCategory)) {
                        validNodesForIpsec.put(entity, validNode.getValue());
                    } else {
                        logger.warn("Unknown nodeEntityCategory {} for {}", nodeEntityCategory.toString(), validNode.getValue().getName());
                    }
                }
            }
            JobStatusRecord jobStatusRecord;
            if (areInputNodesValid) {
                //Starting workflow for:
                // -valid entities
                //- association with an existing node
                //- associated node is valid

                if (validNodesForOam != null && !validNodesForOam.isEmpty()) {

                    try {
                        jobStatusRecord = cacheHandler.insertJob(NscsCommandType.CERTIFICATE_REISSUE);
                        commandManager.executeCertificateReissueWfs(validNodesForOam, inputReason, CertificateType.OAM.name(), jobStatusRecord);
                    } catch (final Exception ex) {
                        final String errorMsg = String.format("OAM: exception %s msg %s", ex.getClass(), ex.getMessage());
                        logger.error(errorMsg);
                        throw new CertificateReissueWfException(errorMsg);
                    }
                }

                if (validNodesForIpsec != null && !validNodesForIpsec.isEmpty()) {

                    try {
                        jobStatusRecord = cacheHandler.insertJob(NscsCommandType.CERTIFICATE_REISSUE);
                        commandManager.executeCertificateReissueWfs(validNodesForIpsec, inputReason, CertificateType.IPSEC.name(), jobStatusRecord);
                    } catch (final Exception ex) {
                        final String errorMsg = String.format("IPSEC: exception %s msg %s", ex.getClass(), ex.getMessage());
                        logger.error(errorMsg);
                        throw new CertificateReissueWfException(errorMsg);
                    }
                }

            } else {
                //only NON BLOCKING ERRORS are found
                if (!nonBlockingErrors.isEmpty() && nonBlockingErrors.size() == blockingErrors.size()) {

                    if (validNodesForOam != null && !validNodesForOam.isEmpty()) {

                        try {
                            jobStatusRecord = cacheHandler.insertJob(NscsCommandType.CERTIFICATE_REISSUE);
                            commandManager.executeCertificateReissueWfs(validNodesForOam, inputReason, CertificateType.OAM.name(), jobStatusRecord);
                        } catch (final Exception ex) {
                            final String errorMsg = String.format("OAM: exception %s msg %s", ex.getClass(), ex.getMessage());
                            logger.error(errorMsg);
                            throw new CertificateReissueWfException(errorMsg);
                        }
                    }

                    if (validNodesForIpsec != null && !validNodesForIpsec.isEmpty()) {

                        try {
                            jobStatusRecord = cacheHandler.insertJob(NscsCommandType.CERTIFICATE_REISSUE);
                            commandManager.executeCertificateReissueWfs(validNodesForIpsec, inputReason, CertificateType.IPSEC.name(),
                                    jobStatusRecord);
                        } catch (final Exception ex) {
                            final String errorMsg = String.format("IPSEC: exception %s msg %s", ex.getClass(), ex.getMessage());
                            logger.error(errorMsg);
                            throw new CertificateReissueWfException(errorMsg);
                        }
                    }

                    final Set<java.util.Map.Entry<String, String[]>> entrySet = nonBlockingErrors.entrySet();
                    for (final java.util.Map.Entry<String, String[]> entry : entrySet) {
                        this.systemRecorder.recordError("ReissueAuto", ErrorSeverity.ERROR, "Node Security", entry.getKey(), entry.getValue()[0]);
                    }

                    //there are also BLOCKING ERRORS
                } else {

                    final Set<Entry<Entity, NscsServiceException>> entrySet = blockingErrors.entrySet();
                    for (final Entry<Entity, NscsServiceException> entry : entrySet) {
                        this.systemRecorder.recordError("ReissueAuto", ErrorSeverity.ERROR, "Node Security", entry.getKey().getEntityInfo().getName(),
                                entry.getValue().getMessage());
                    }

                }
            }
        }
    }

    private String printEntityNames(final List<Entity> entityList) {
        String entityNames = "";
        for (final Entity e : entityList) {
            entityNames += e.getEntityInfo().getName() + " ";
        }
        return entityNames;
    }

    private String printEntityNamesAndSerialNumber(final List<Entity> entityList) {
        final StringBuilder strb = new StringBuilder("");
        for (final Entity e : entityList) {
            final EntityInfo eInfo = e.getEntityInfo();
            if (eInfo != null) {
                strb.append("Entity name : ").append(eInfo.getName());
                strb.append(" SN : ");
                if (eInfo.getActiveCertificate() != null) {
                    strb.append(eInfo.getActiveCertificate().getSerialNumber());
                } else {
                    strb.append("NO active certificate");
                }
            } else {
                strb.append("NO ENTITY INFO!!!!");
            }
            strb.append("/n");
        }
        return strb.toString();
    }
}
