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
package com.ericsson.nms.security.nscs.cucumber.helper;

import java.util.Iterator;

import javax.inject.Inject;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.oss.itpf.datalayer.dps.BucketProperties;
import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;

public abstract class NodeSecurityNodesDataSetup {
    protected String neName;
    protected String neNeType;

    @Inject
    protected EServiceProducer eserviceHolder;

    @Inject
    protected UserTransaction userTransaction;

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
        log.info("Create NetworkElement, no nePlatformType");

        userTransaction.begin();
        final DataBucket liveBucket = getLiveBucket();

        final ManagedObject networkElement = liveBucket.getMibRootBuilder().namespace(NodeModelDefs.NE_NS).type(Model.NETWORK_ELEMENT.type())
                .addAttribute(NodeModelDefs.NETWORK_ELEMENT_ID, "1").addAttribute(NodeModelDefs.NE_TYPE, neNeType).name(neName)
                .version(NodeSecurityDataConstants.NETWORK_ELEMENT_VERSION).create();
        log.info("Created NetworkElement.....  name {}, managedObject {}", neName, networkElement);

        liveBucket.getMibRootBuilder().namespace(NodeModelDefs.NE_SEC_NS).type(Model.NETWORK_ELEMENT.securityFunction.type())
                .addAttribute(NodeModelDefs.SECURITY_FUNCTION_ID, "1").name("1").version("1.0.0").parent(networkElement).create();
        log.info("Created SecurityFunctions.....   managedObject {}", networkElement);

        liveBucket.getMibRootBuilder().namespace(NodeModelDefs.NE_CM_NS).type(Model.NETWORK_ELEMENT.cmFunction.type())
                .addAttribute(NodeModelDefs.SYNC_STATUS, syncStatus).name("1").version("1.0.0").parent(networkElement).create();
        log.info("Created CmFunction.....   managedObject {}", networkElement);

        userTransaction.commit();

        log.info("Test Setup, Transaction Committed");
    }

    public void deleteAllNodes() throws Exception {
        deleteAllNodes(NodeModelDefs.NE_NS, Model.NETWORK_ELEMENT.type());
    }

    private void deleteAllNodes(final String namespace, final String type) throws Exception {
        userTransaction.begin();
        final QueryBuilder queryBuilder = eserviceHolder.getDataPersistenceService().getQueryBuilder();
        final Query query = queryBuilder.createTypeQuery(namespace, type);

        final Iterator<PersistenceObject> iterator = getLiveBucket().getQueryExecutor().execute(query);
        while (iterator.hasNext()) {
            final PersistenceObject po = iterator.next();
            eserviceHolder.getDataPersistenceService().getLiveBucket().deletePo(po);
        }
        userTransaction.commit();
    }

    private DataBucket getLiveBucket() {
        return eserviceHolder.getDataPersistenceService().getDataBucket("Live", BucketProperties.SUPPRESS_MEDIATION,
                BucketProperties.SUPPRESS_CONSTRAINTS);
    }

    private void createNode() throws Exception {
        createNode(neName, neNeType, NodeModelDefs.SyncStatusValue.SYNCHRONIZED.name());
    }

    public String getNeName() {
        return neName;
    }
}
