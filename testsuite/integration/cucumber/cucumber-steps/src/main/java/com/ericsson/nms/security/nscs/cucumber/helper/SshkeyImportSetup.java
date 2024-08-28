/*------------------------------------------------------------------------------
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

package com.ericsson.nms.security.nscs.cucumber.helper;

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

@Stateless
public class SshkeyImportSetup {

    @Inject
    private EServiceProducer eServiceProducer;

    private static final Logger LOGGER = LoggerFactory.getLogger(NscsScriptEngineCommandHandlerTestSteps.class);

    public void createNetworkElementWithSecurityFunctionAndMeContext(final String neName) throws Exception {
        final DataBucket liveBucket = getLiveBucket();
        final ManagedObject networkElement = liveBucket.getMibRootBuilder().namespace("OSS_NE_DEF").version("2.0.0").type("NetworkElement")
                .addAttribute("neType", NodeSecurityDataConstants.VECE_TARGET_TYPE).addAttribute("networkElementId", neName).name(neName).create();
        LOGGER.info("Created NetworkElement: {}", networkElement.getFdn());

        final ManagedObject sfMO = addSecurityFunction(liveBucket, networkElement);
        LOGGER.info("Created SecurityFunction MO {}", sfMO);

        final ManagedObject MeContextMo = createMeContext(liveBucket, neName);
        networkElement.addAssociation("nodeRootRef", MeContextMo);
        LOGGER.info("Association Created with MeContextMo {}", MeContextMo);

        LOGGER.info("Test Setup, Transaction Commited");
    }

    private ManagedObject addSecurityFunction(final DataBucket liveBucket, final ManagedObject parentManagedObject) {
        final ManagedObject securityFunction = liveBucket.getMibRootBuilder().namespace(NodeModelDefs.NE_SEC_NS)
                .type(Model.NETWORK_ELEMENT.securityFunction.type()).addAttribute(NodeModelDefs.SECURITY_FUNCTION_ID, "1").name("1").version("1.0.0")
                .parent(parentManagedObject).create();
        return securityFunction;
    }

    private ManagedObject createMeContext(final DataBucket liveBucket, final String nodeName) {

        final ManagedObject MeContextMo = liveBucket.getMibRootBuilder().namespace(NodeSecurityDataConstants.TOP_NAMESPACE).type("MeContext")
                .addAttribute("neType", NodeSecurityDataConstants.VECE_TARGET_TYPE).addAttribute("MeContextId", "1")
                .version(NodeSecurityDataConstants.TOP_NAMESPACE_VERSION).name(nodeName).create();

        LOGGER.info("Created MeContext.....  name {}, managedObject {}, fdn {}", nodeName, MeContextMo, MeContextMo.getFdn());
        return MeContextMo;
    }

    private DataBucket getLiveBucket() {
        return getDataPersistenceService().getDataBucket("Live", BucketProperties.SUPPRESS_MEDIATION, BucketProperties.SUPPRESS_CONSTRAINTS);
    }

    private DataPersistenceService getDataPersistenceService() {
        return eServiceProducer.getDataPersistenceService();
    }
}