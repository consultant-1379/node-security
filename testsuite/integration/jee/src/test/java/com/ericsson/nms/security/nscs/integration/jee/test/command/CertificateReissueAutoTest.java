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
package com.ericsson.nms.security.nscs.integration.jee.test.command;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.handler.command.impl.CertificateReissueAuto;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityDataConstants;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityDataSetup;
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityCategory;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.EntityProfile;

public class CertificateReissueAutoTest implements CertificateReissueAutoTests {

	@Inject
	Logger log;

	@Inject
	NodeSecurityDataSetup dataSetup;

	@Inject
	NscsPkiEntitiesManagerIF nscsPkiManager;

	@Inject
	CertificateReissueAuto certificateReissueAuto;

	@Override
	public void certificateReissueAutoProcess() throws Exception {
		try {
			// Create node
			final String nodeName = NodeSecurityDataConstants.NODE_AUTOREISSUE_NAME1;
			// Setup create node
			dataSetup.deleteAllNodes();
			// dataSetup.insertData();
			dataSetup.createNode(nodeName, "SYNCHRONIZED", SecurityLevel.LEVEL_2, NodeSecurityDataSetup.IpAddressVersion.IPv4);
			dataSetup.createNetworkElementSecurity(NodeSecurityDataSetup.networkElementNameFromMeContextName(nodeName));

			// Create entity
			final Entity endEntity = new Entity();
			endEntity.setType(EntityType.ENTITY);
			// endEntity.setEntityProfileName(CPP_NODE_EE_PROFILE);
			final EntityInfo entityInfo = new EntityInfo();
			entityInfo.setName(NodeSecurityDataConstants.NODE_AUTOREISSUE_NAME1 + "-OAM");
			entityInfo.setOTP("enmenm123");
			endEntity.setEntityInfo(entityInfo);
			final EntityCategory entityCategory = new EntityCategory();
			entityCategory.setName("NODE-OAM");
			endEntity.setCategory(entityCategory);

			final EntityProfile entityProfile = new EntityProfile();
			entityProfile.setName("DUSGen2OAM_EP");
			endEntity.setEntityProfile(entityProfile);

			nscsPkiManager.createEntity(endEntity);

			log.info("BULLS : Starting test process");

			certificateReissueAuto.process();
			log.info("BULLS : Ending test process");
		} catch (final Exception ex) {
			log.info("BULLS : exception caught");
			ex.printStackTrace();
		}

	}


}
