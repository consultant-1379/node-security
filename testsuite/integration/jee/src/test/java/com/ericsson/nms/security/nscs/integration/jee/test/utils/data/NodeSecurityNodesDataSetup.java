/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.integration.jee.test.utils.data;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CmFunction;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.SecurityFunction;
import com.ericsson.nms.security.nscs.integration.jee.test.command.ciphersconfig.CiphersConfigTestConstants;
import com.ericsson.nms.security.nscs.integration.jee.test.producer.EServiceProducer;
import com.ericsson.oss.itpf.datalayer.dps.BucketProperties;
import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation;

public abstract class NodeSecurityNodesDataSetup {
    protected String neName;
    protected String neNeType;

    @Inject
    protected EServiceProducer eserviceHolder;

    @Inject
    protected UserTransaction userTransaction;

    @Inject
    NscsCapabilityModelService nscsCapabilityModelService;

    @Inject
    protected Logger log;

    public NodeSecurityNodesDataSetup(final String neName, final String neNeType) {
        this.neName = neName;
        this.neNeType = neNeType;
    }

    public NodeSecurityNodesDataSetup() {
    }

    public void insertData() throws Exception {
        deleteAllNodes();
        createNode();
    }

    private void createNode(final String neName, final String neNeType, final String syncStatus) throws Exception {
        log.info("[NSCS_ARQ_NODES_DATA_SETUP] Create NetworkElement, no nePlatformType");

        beginTransaction();

        final DataBucket liveBucket = getLiveBucket();

        final ManagedObject networkElement = liveBucket.getMibRootBuilder().namespace(Model.NETWORK_ELEMENT.namespace())
                .type(Model.NETWORK_ELEMENT.type()).addAttribute(NetworkElement.NETWORK_ELEMENT_ID, "1")
                .addAttribute(NetworkElement.NE_TYPE, neNeType).name(neName).version(NodeSecurityDataConstants.NETWORK_ELEMENT_VERSION).create();
        log.info("Created NetworkElement.....  name {}, managedObject {}", neName, networkElement);

        liveBucket.getMibRootBuilder().namespace(Model.NETWORK_ELEMENT.securityFunction.namespace())
                .type(Model.NETWORK_ELEMENT.securityFunction.type()).addAttribute(SecurityFunction.SECURITY_FUNCTION_ID, "1").name("1")
                .version("1.0.0").parent(networkElement).create();
        log.info("Created SecurityFunctions.....   managedObject {}", networkElement);

        liveBucket.getMibRootBuilder().namespace(Model.NETWORK_ELEMENT.cmFunction.namespace()).type(Model.NETWORK_ELEMENT.cmFunction.type())
                .addAttribute(CmFunction.SYNC_STATUS, syncStatus).name("1").version("1.0.0").parent(networkElement).create();
        log.info("Created CmFunction.....   managedObject {}", networkElement);

        commitTransaction();

        log.info("[NSCS_ARQ_NODES_DATA_SETUP] Create NetworkElement, no nePlatformType : completed");
    }

    public void deleteAllNodes() throws Exception {
        deleteAllNodes(Model.NETWORK_ELEMENT.namespace(), Model.NETWORK_ELEMENT.type());
    }

    protected void deleteAllNodes(final String namespace, final String type) throws Exception {

        log.info("[NSCS_ARQ_NODES_DATA_SETUP] Deleting all POs with namespace [{}] , type [{}]", namespace, type);

        beginTransaction();

        final QueryBuilder queryBuilder = eserviceHolder.getDataPersistenceService().getQueryBuilder();
        final Query<TypeRestrictionBuilder> query = queryBuilder.createTypeQuery(namespace, type);

        commitTransaction();

        beginTransaction();

        try {
            DataBucket liveBucket = eserviceHolder.getDataPersistenceService().getLiveBucket();
            if (liveBucket != null) {
                log.info("[NSCS_ARQ_NODES_DATA_SETUP] reading all POs with namespace [{}] , type [{}]", namespace, type);
                final Iterator<PersistenceObject> iterator = liveBucket.getQueryExecutor().execute(query);
                final boolean hasPOs = (iterator != null) && iterator.hasNext();
                log.info("[NSCS_ARQ_NODES_DATA_SETUP] deleting all POs (hasPOs={}) with namespace [{}] , type [{}]", hasPOs, namespace, type);
                while ((iterator != null) && iterator.hasNext()) {
                    final PersistenceObject po = iterator.next();
                    log.info("[NSCS_ARQ_NODES_DATA_SETUP] deleting PO [{}]", po.toString());
                    liveBucket.deletePo(po);
                }
            }
        } catch (final Exception e) {
            log.error("[NSCS_ARQ_NODES_DATA_SETUP] deleteAllNodes: caught exception", e);

            rollbackTransaction();

            return;
        }

        commitTransaction();
    }

    private DataBucket getLiveBucket() {
        return eserviceHolder.getDataPersistenceService().getDataBucket("Live", BucketProperties.SUPPRESS_MEDIATION,
                BucketProperties.SUPPRESS_CONSTRAINTS);
    }

    private void createNode() throws Exception {
        createNode(neName, neNeType, ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name());
    }

    public String getNeName() {
        return neName;
    }

    protected String getTargetModelIdentityForTestNode(final String targetType) {
        // Get list of all TMIs for target type
        final List<String> targetModelIdentities = eserviceHolder.getNscsModelService().getTargetModelIdentities(TargetTypeInformation.CATEGORY_NODE,
                targetType);
        String ossModelIdentity = NodeSecurityDataConstants.ERBS_TARGET_TYPE.equals(targetType)
                ? NodeSecurityDataConstants.NETWORK_ELEMENT_CPP_OSS_MODEL_IDENTITY_VERSION
                : NodeSecurityDataConstants.NETWORK_ELEMENT_OSS_MODEL_IDENTITY_RADIO_VERSION;
        // Search TMI which supports capabilities for tests
        for (String tmi : targetModelIdentities) {
            NodeModelInformation nodeModelInfo = new NodeModelInformation(tmi, NodeModelInformation.ModelIdentifierType.OSS_IDENTIFIER, targetType);
            if (nscsCapabilityModelService.isCrlCheckCommandSupported(nodeModelInfo)
                    && nscsCapabilityModelService.isCrlDownloadCommandSupported(nodeModelInfo)) {
                ossModelIdentity = tmi;
                break;
            }
        }
        return ossModelIdentity;
    }

    protected String getTargetModelIdentityForCiphersTestNode(final String targetType, final Boolean ciphersConfigurationSupported) {
        // Get list of all TMIs for target type
        final List<String> targetModelIdentities = eserviceHolder.getNscsModelService().getTargetModelIdentities(TargetTypeInformation.CATEGORY_NODE,
                targetType);
        String targetModelIdentity;
        // Set default TMI
        if (NodeSecurityDataConstants.ERBS_TARGET_TYPE.equals(targetType)) {
            targetModelIdentity = ciphersConfigurationSupported ? CiphersConfigTestConstants.CPP_SUPPORTED_NODE_RELEASE_VERSION
                    : CiphersConfigTestConstants.CPP_UNSUPPORTED_NODE_RELEASE_VERSION;
        } else {
            targetModelIdentity = ciphersConfigurationSupported ? CiphersConfigTestConstants.COM_ECIM_SUPPORTED_NODE_RELEASE_VERSION
                    : CiphersConfigTestConstants.COM_ECIM_UNSUPPORTED_NODE_RELEASE_VERSION;
        }
        // Search TMI which supports Ciphers capability for tests
        for (String tmi : targetModelIdentities) {
            NodeModelInformation nodeModelInfo = new NodeModelInformation(tmi, NodeModelInformation.ModelIdentifierType.OSS_IDENTIFIER, targetType);
            if (ciphersConfigurationSupported.equals(nscsCapabilityModelService.isCiphersConfigurationSupported(nodeModelInfo))) {
                targetModelIdentity = tmi;
                break;
            }
        }
        return targetModelIdentity;
    }

    private int getTransactionStatus() throws Exception {
        try {
            return userTransaction.getStatus();
        } catch (final SystemException e) {
            log.error("[NSCS_ARQ_NODES_DATA_SETUP] transaction : status : FAILED", e);
            throw e;
        } catch (final Exception e) {
            log.error("[NSCS_ARQ_NODES_DATA_SETUP] transaction : status : ERROR", e);
            throw e;
        }
    }

    protected void beginTransaction() throws Exception {
        log.info("[NSCS_ARQ_NODES_DATA_SETUP] transaction : begin : STARTED : status [{}]", getTransactionStatus());
        try {
            //            userTransaction.setTransactionTimeout(120);
            userTransaction.begin();
            log.info("[NSCS_ARQ_NODES_DATA_SETUP] transaction : begin : SUCCESS : status [{}]", getTransactionStatus());
        } catch (final NotSupportedException | SystemException e) {
            log.error("[NSCS_ARQ_NODES_DATA_SETUP] transaction : begin : FAILED", e);
            throw e;
        } catch (final Exception e) {
            log.error("[NSCS_ARQ_NODES_DATA_SETUP] transaction : begin : ERROR", e);
            throw e;
        }
    }

    protected void commitTransaction() throws Exception {
        log.info("[NSCS_ARQ_NODES_DATA_SETUP] transaction : commit : STARTED : status [{}]", getTransactionStatus());
        try {
            userTransaction.commit();
            log.info("[NSCS_ARQ_NODES_DATA_SETUP] transaction : commit : SUCCESS : status [{}]", getTransactionStatus());
        } catch (final SecurityException | IllegalStateException | RollbackException | HeuristicMixedException | HeuristicRollbackException
                | SystemException e) {
            log.error("[NSCS_ARQ_NODES_DATA_SETUP] transaction : commit : FAILED", e);
            throw e;
        } catch (final Exception e) {
            log.error("[NSCS_ARQ_NODES_DATA_SETUP] transaction : commit : ERROR", e);
            throw e;
        }
    }

    protected void rollbackTransaction() throws Exception {
        log.info("[NSCS_ARQ_NODES_DATA_SETUP] transaction : rollback : STARTED : status [{}]", getTransactionStatus());
        try {
            userTransaction.rollback();
            log.info("[NSCS_ARQ_NODES_DATA_SETUP] transaction : rollback : SUCCESS : status [{}]", getTransactionStatus());
        } catch (final IllegalStateException | SecurityException | SystemException e) {
            log.error("[NSCS_ARQ_NODES_DATA_SETUP] transaction : rollback : FAILED", e);
            throw e;
        } catch (final Exception e) {
            log.error("[NSCS_ARQ_NODES_DATA_SETUP] transaction : rollback : ERROR", e);
            throw e;
        }
    }

}
