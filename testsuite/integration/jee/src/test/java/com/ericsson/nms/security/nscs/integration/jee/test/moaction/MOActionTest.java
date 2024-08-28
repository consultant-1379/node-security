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
package com.ericsson.nms.security.nscs.integration.jee.test.moaction;

import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.exception.DataAccessException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.model.*;
import com.ericsson.nms.security.nscs.data.moaction.*;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityDataSetup;
import com.ericsson.nms.security.nscs.utilities.NSCSCppNodeUtility;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

public class MOActionTest implements MOActionTests {

	@Inject
	MOActionService moAction;
	
	@Inject
	NodeSecurityDataSetup data;

	private static final String NODE_123 = "node123";
    private static final NodeReference NODE_REF_123 = new NodeRef(NODE_123);
    private static final String NORMALIZABLE_NODE_REF_FDN = "MeContext=" + NODE_123;

	@Override
	public void moActionWithoutParameter() throws Exception {
		setup();			
		//All we can test is the MO Action ACK from CM (=no exception is thrown) 
		//Once the mediation is working we will have e2e tests part of the WORKFLOW integration
		moAction.performMOAction(NORMALIZABLE_NODE_REF_FDN, MoActionWithoutParameter.Security_cancelCertEnrollment);
		tearDown();
	}

	@Override
	public void moActionInitCertEnrollment() throws Exception {
		setup();
		final MoParams params = getInitCertEnrollmentParams();	
		//All we can test is the MO Action ACK from CM (=no exception is thrown)
		//Once the mediation is working we will have e2e tests part of the WORKFLOW integration
		moAction.performMOAction(NORMALIZABLE_NODE_REF_FDN, MoActionWithParameter.Security_initCertEnrollment, params);
		tearDown();				
	}

	@Override
	public void moActionInstallTrustedCertificatesCorba() throws Exception {
		setup();
		final MoParams params = getInstallTrustedCertsParams();	
		//All we can test is the MO Action ACK from CM (=no exception is thrown)
		//Once the mediation is working we will have e2e tests part of the WORKFLOW integration
		moAction.performMOAction(NORMALIZABLE_NODE_REF_FDN, MoActionWithParameter.Security_installTrustedCertificates,  params);
		tearDown();				
	}
	@Override
	public void moActionNonExistingNode() throws Exception {
		try {
			moAction.performMOAction(NORMALIZABLE_NODE_REF_FDN + "_fake", MoActionWithoutParameter.Security_cancelCertEnrollment);
			Assert.fail("MO Action should fail if NE does not exits");
		} catch (final DataAccessException e) {
			Assert.assertTrue(e.getStatusMessage().contains("Unexpected Internal Error"));
		} 				
	}

	@Override
	public void moActionNonValidParams() throws Exception {
		setup();
		try {
			final MoParams params = new MoParams();
			moAction.performMOAction(NORMALIZABLE_NODE_REF_FDN, MoActionWithParameter.Security_installTrustedCertificates, params);
			Assert.fail("MO Action should fail if params not valid");
		} catch (final DataAccessException e) {
			Assert.assertTrue(e.getStatusMessage().contains("Unexpected Internal Error"));
		} finally {
			tearDown();
		}		
	}		

    @Override
    public void moActionAdaptSecurityLevel() throws Exception {
        setup();
		//All we can test is the MO Action ACK from CM (=no exception is thrown)
		//Once the mediation is working we will have e2e tests part of the WORKFLOW integration
		moAction.performMOAction(NORMALIZABLE_NODE_REF_FDN, MoActionWithoutParameter.Security_adaptSecurityLevel);
		tearDown();
    }
	public void setup() throws Exception {
		data.deleteAllNodes();
		data.createNode(NODE_123);
	}

	public void tearDown() throws Exception {
		data.deleteAllNodes();
	}
	
	public static MoParams getInitCertEnrollmentParams() {
		final MoParams params = ScepEnrollmentInfoImpl.toMoParams(
				"caFingerPrint".getBytes(), 
				"challengePassword", 
				"distinguishedName", 
				EnrollmentMode.SCEP.getEnrollmentModeValue(), 
				"enrollmentServerURL", 
				NSCSCppNodeUtility.CPP_KEY_LENGTH_1024, 
				0,
				true,
				true,
				"authority",
                                DigestAlgorithm.SHA1);
		return params;
	}

	public static MoParams getInstallTrustedCertsParams() {
		final List<MoParams> certSpecList = new ArrayList<>();
		for (int i = 0; i < 5; i++) {	
			certSpecList.add(CPPCertSpec.toMoParams(TrustedCertCategory.CORBA_PEERS, 
					 "fileName"+i, 
                                         "/root/smrs", 
					("fingerprint"+i).getBytes(), 
					 "serialNumber"+i,
                                         DigestAlgorithm.SHA1));	
		}
		final List<MoParams> accountInfoList = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			accountInfoList.add(AccountInfo.toMoParams(
					("password"+i).toCharArray(), 
					 "remoteHost"+i, 
					 "userID"+i));
		}
		final MoParams params = TrustStoreInfo.toMoParams(
				certSpecList, 
				"0", 
				0, 
				accountInfoList);
		return params;
	}
}
