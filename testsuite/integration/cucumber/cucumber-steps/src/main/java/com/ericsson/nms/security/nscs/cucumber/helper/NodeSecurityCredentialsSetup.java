/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
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

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.cucumber.steps.NscsScriptEngineCommandHandlerTestSteps;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.oss.itpf.datalayer.dps.BucketProperties;
import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;

@Stateless
public class NodeSecurityCredentialsSetup {

    @Inject
    private EServiceProducer eServiceProducer;

    private static final Logger LOGGER = LoggerFactory.getLogger(NscsScriptEngineCommandHandlerTestSteps.class);

    public void createNetworkElementWithSecurityFunction(final String neName) throws Exception {
        final DataBucket liveBucket = getLiveBucket();
        final ManagedObject networkElement = liveBucket.getMibRootBuilder().namespace("OSS_NE_DEF").version("2.0.0").type("NetworkElement")
                   .addAttribute("neType", "ERBS")
                   .addAttribute("networkElementId", neName).name(neName).create();
        LOGGER.info("Test Setup, created: {}", networkElement.getFdn());
        addSecurityFunction(liveBucket, networkElement);
        LOGGER.info("Test Setup, Transaction Commited");
    }

    public void createNfvoWithSecurityFunction(final String nfvoName) {
        final DataBucket liveBucket = getLiveBucket();
        final ManagedObject nfvo = liveBucket.getMibRootBuilder().namespace("OSS_NE_DEF").version("1.0.0")
                   .type("NetworkFunctionVirtualizationOrchestrator")
                   .addAttribute("nfvoType", "RNFVO")
                   .addAttribute("networkFunctionVirtualizationOrchestratorId", nfvoName).name(nfvoName).create();
        LOGGER.info("Test Setup, created: {}", nfvo.getFdn());
        addSecurityFunction(liveBucket, nfvo);
        LOGGER.info("Test Setup, Transaction Commited");
    }

    public void deleteNodes(final String namespace, final String type) {
        final QueryBuilder queryBuilder = eServiceProducer.getDataPersistenceService().getQueryBuilder();
        final Query query = queryBuilder.createTypeQuery(namespace, type);

        final Iterator<PersistenceObject> iterator = getLiveBucket().getQueryExecutor().execute(query);
        while (iterator.hasNext()) {
            getLiveBucket().deletePo(iterator.next());
        }
    }

    private void addSecurityFunction(final DataBucket liveBucket, final ManagedObject parentManagedObject) {
        final ManagedObject securityFunction = liveBucket.getMibRootBuilder().namespace(NodeModelDefs.NE_SEC_NS)
                   .type(Model.NETWORK_ELEMENT.securityFunction.type())
                   .addAttribute(NodeModelDefs.SECURITY_FUNCTION_ID, "1").name("1").version("1.0.0").parent(parentManagedObject).create();
        LOGGER.info("Test Setup, created:  {}", securityFunction.getFdn());
    }

    private DataBucket getLiveBucket() {
        return getDataPersistenceService().getDataBucket("Live", BucketProperties.SUPPRESS_MEDIATION, BucketProperties.SUPPRESS_CONSTRAINTS);
    }

    private DataPersistenceService getDataPersistenceService() {
        return eServiceProducer.getDataPersistenceService();
    }
}