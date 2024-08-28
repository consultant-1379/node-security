package com.ericsson.nms.security.nscs.workflow.task.data.moaction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParam;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;

@RunWith(MockitoJUnitRunner.class)
public class WorkflowMoActionsTest {

	private static final String CERT_M_FDN = "ManagedElement=DG2_00001,SystemFunctions=1,SecM=1,CertM=1";
	private static final String NODE_CREDENTIAL_FDN = "ManagedElement=DG2_00001,SystemFunctions=1,SecM=1,CertM=1,NodeCredential=1";
	private static final String SECURITY_FDN = "MeContext=ERBS_00001,ManagedElement=1,SystemFunctions=1,Security=1";
	private static final String ROOT_TDPS_URL = "http://192.168.0.155:8093/pki-ra-tdps/ca_entity/ENM_PKI_Root_CA/212f8e277a440023/active/ENM_PKI_Root_CA";
	private static final String INFRA_TDPS_URL = "http://192.168.0.155:8093/pki-ra-tdps/ca_entity/ENM_Infrastructure_CA/219123e5f581eb0f/active/ENM_PKI_Root_CA";
	private static final String ENM_TDPS_URL = "http://192.168.0.155:8093/pki-ra-tdps/ca_entity/ENM_OAM_CA/5f84d09b5755a36f/active/ENM_Infrastructure_CA";
	private static final String PASSWORD = "password";
	private static final List<String> tdpsUrls = Arrays.asList(ROOT_TDPS_URL, INFRA_TDPS_URL, ENM_TDPS_URL);
	private static final int POLL_TIMES = 10;

	@Test
	public void cancelInstallTrustedCertFromUriSeralizationTest() {
		// Prepare workflow MO actions
		final MoActionWithoutParameter theAction = MoActionWithoutParameter.ComEcim_CertM_cancel;
		WorkflowMoActions theMoActions = new WorkflowMoActions();
		WorkflowMoAction theMoAction = new WorkflowMoActionWithoutParams(CERT_M_FDN, theAction, POLL_TIMES);
		theMoActions.addTargetAction(theMoAction);

		// Serialize/deserialize workflow MO actions
		WorkflowMoActions decodedMoActions = serializeDeserializeWorkflowMoActions(theMoActions);
		assertNotNull(decodedMoActions);

		Iterator<WorkflowMoAction> it = decodedMoActions.getTargetActions().iterator();
		while (it.hasNext()) {
			WorkflowMoAction moAction = it.next();
			assertEquals(WorkflowMoActionState.PENDING, moAction.getState());
			assertEquals(POLL_TIMES, moAction.getMaxPollTimes());
			assertEquals(POLL_TIMES, moAction.getRemainingPollTimes());
			assertEquals(CERT_M_FDN, moAction.getTargetMoFdn());

			// Extract MO actions
			MoActionWithoutParameter action = ((WorkflowMoActionWithoutParams) moAction).getTargetAction();
			assertEquals(theAction, action);
		}
	}

	@Test
	public void installTrustedCertFromUriSeralizationTest() {
		// Prepare workflow MO actions
		final MoActionWithParameter theAction = MoActionWithParameter.ComEcim_CertM_installTrustedCertFromUri;
		WorkflowMoActions theMoActions = new WorkflowMoActions();
		Iterator<String> itTdpsUrls = tdpsUrls.iterator();
		while (itTdpsUrls.hasNext()) {
			String tdpsUrl = itTdpsUrls.next();
			final WorkflowMoParams theMoParams = getInstallTrustedCertFromUriParams(tdpsUrl);
			WorkflowMoAction theMoAction = new WorkflowMoActionWithParams(CERT_M_FDN, theAction, theMoParams,
					POLL_TIMES);
			theMoActions.addTargetAction(theMoAction);
		}

		// Serialize/deserialize workflow MO actions
		WorkflowMoActions decodedMoActions = serializeDeserializeWorkflowMoActions(theMoActions);
		assertNotNull(decodedMoActions);

		Iterator<WorkflowMoAction> it = decodedMoActions.getTargetActions().iterator();
		while (it.hasNext()) {
			WorkflowMoAction moAction = it.next();
			assertEquals(WorkflowMoActionState.PENDING, moAction.getState());
			assertEquals(POLL_TIMES, moAction.getMaxPollTimes());
			assertEquals(POLL_TIMES, moAction.getRemainingPollTimes());
			assertEquals(CERT_M_FDN, moAction.getTargetMoFdn());

			// Extract MO actions
			MoActionWithParameter action = ((WorkflowMoActionWithParams) moAction).getTargetAction();
			assertEquals(theAction, action);
			WorkflowMoParams moParams = ((WorkflowMoActionWithParams) moAction).getTargetActionParams();
			assertNotNull(moParams);

			// Extract MO params
			MoParams targetMoParams = new MoParams(moParams);
			assertTrue(targetMoParams.isMap());
			Map<String, MoParam> targetMoParamsMap = targetMoParams.getParamMap();
			final MoParam tdpsUrlParam = targetMoParamsMap.get("uri");
			assertTrue(tdpsUrlParam instanceof MoParam);
			assertTrue(tdpsUrlParam.isSimple());
			final Object tdpsUrl = tdpsUrlParam.getParam();
			assertTrue(tdpsUrl instanceof String);
			assertTrue(tdpsUrls.contains(tdpsUrl));
			assertFalse("{*****}".equals(tdpsUrlParam.toString()));
			final MoParam passwordParam = targetMoParamsMap.get("uriPassword");
			assertTrue(passwordParam.isSimple());
			final Object password = passwordParam.getParam();
			assertTrue(password instanceof String);
			assertTrue(PASSWORD.equals(password));
			assertTrue("{*****}".equals(passwordParam.toString()));
			final MoParam fingerprintParam = targetMoParamsMap.get("fingerprint");
			assertTrue(fingerprintParam instanceof MoParam);
			assertTrue(fingerprintParam.isSimple());
			final Object fingerprint = fingerprintParam.getParam();
			assertTrue(fingerprint instanceof String);
			assertTrue("NULL".equals(fingerprint));
			assertFalse("{*****}".equals(fingerprintParam.toString()));
		}
	}

	@Test
	public void cancelEnrollmentSeralizationTest() {
		// Prepare workflow MO actions
		final MoActionWithoutParameter theAction = MoActionWithoutParameter.ComEcim_NodeCredential_cancelEnrollment;
		WorkflowMoActions theMoActions = new WorkflowMoActions();
		WorkflowMoAction theMoAction = new WorkflowMoActionWithoutParams(NODE_CREDENTIAL_FDN, theAction, POLL_TIMES);
		theMoActions.addTargetAction(theMoAction);

		// Serialize/deserialize workflow MO actions
		WorkflowMoActions decodedMoActions = serializeDeserializeWorkflowMoActions(theMoActions);
		assertNotNull(decodedMoActions);

		Iterator<WorkflowMoAction> it = decodedMoActions.getTargetActions().iterator();
		while (it.hasNext()) {
			WorkflowMoAction moAction = it.next();
			assertEquals(WorkflowMoActionState.PENDING, moAction.getState());
			assertEquals(POLL_TIMES, moAction.getMaxPollTimes());
			assertEquals(POLL_TIMES, moAction.getRemainingPollTimes());
			assertEquals(NODE_CREDENTIAL_FDN, moAction.getTargetMoFdn());

			// Extract MO actions
			MoActionWithoutParameter action = ((WorkflowMoActionWithoutParams) moAction).getTargetAction();
			assertEquals(theAction, action);
		}
	}

	@Test
	public void enrollmentSeralizationTest() {
		// Prepare workflow MO actions
		final MoActionWithParameter theAction = MoActionWithParameter.ComEcim_NodeCredential_startOnlineEnrollment;
		WorkflowMoActions theMoActions = new WorkflowMoActions();
		final WorkflowMoParams theMoParams = getEnrollmentParams();
		WorkflowMoAction theMoAction = new WorkflowMoActionWithParams(NODE_CREDENTIAL_FDN, theAction, theMoParams,
				POLL_TIMES);
		theMoActions.addTargetAction(theMoAction);

		// Serialize/deserialize workflow MO actions
		WorkflowMoActions decodedMoActions = serializeDeserializeWorkflowMoActions(theMoActions);
		assertNotNull(decodedMoActions);

		Iterator<WorkflowMoAction> it = decodedMoActions.getTargetActions().iterator();
		while (it.hasNext()) {
			WorkflowMoAction moAction = it.next();
			assertEquals(WorkflowMoActionState.PENDING, moAction.getState());
			assertEquals(POLL_TIMES, moAction.getMaxPollTimes());
			assertEquals(POLL_TIMES, moAction.getRemainingPollTimes());
			assertEquals(NODE_CREDENTIAL_FDN, moAction.getTargetMoFdn());

			// Extract MO actions
			MoActionWithParameter action = ((WorkflowMoActionWithParams) moAction).getTargetAction();
			assertEquals(theAction, action);
			WorkflowMoParams moParams = ((WorkflowMoActionWithParams) moAction).getTargetActionParams();
			assertNotNull(moParams);

			// Extract MO params
			MoParams targetMoParams = new MoParams(moParams);
			assertTrue(targetMoParams.isMap());
			Map<String, MoParam> targetMoParamsMap = targetMoParams.getParamMap();
			final MoParam challengePasswordParam = targetMoParamsMap.get("challengePassword");
			assertTrue(challengePasswordParam.isSimple());
			final Object password = challengePasswordParam.getParam();
			assertTrue(password instanceof String);
			assertTrue(PASSWORD.equals(password));
			assertTrue("{*****}".equals(challengePasswordParam.toString()));
		}
	}

	@Test
	public void initCertEnrollmentParamsSeralizationTest() {
		final MoActionWithParameter theAction = MoActionWithParameter.Security_initCertEnrollment;
		WorkflowMoActions theMoActions = new WorkflowMoActions();
		final WorkflowMoParams theMoParams = getInitCertEnrollmentParams();
		WorkflowMoAction theMoAction = new WorkflowMoActionWithParams(SECURITY_FDN, theAction, theMoParams, POLL_TIMES);
		theMoActions.addTargetAction(theMoAction);

		// Serialize/deserialize MO actions
		WorkflowMoActions decodedMoActions = serializeDeserializeWorkflowMoActions(theMoActions);
		assertNotNull(decodedMoActions);

		Iterator<WorkflowMoAction> it = decodedMoActions.getTargetActions().iterator();
		while (it.hasNext()) {
			WorkflowMoActionWithParams action = (WorkflowMoActionWithParams) it.next();
			MoActionWithParameter moAction = ((WorkflowMoActionWithParams) action).getTargetAction();
			assertEquals(theAction, moAction);
			assertEquals(WorkflowMoActionState.PENDING, action.getState());
			assertEquals(POLL_TIMES, action.getMaxPollTimes());
			assertEquals(POLL_TIMES, action.getRemainingPollTimes());
			assertEquals(SECURITY_FDN, action.getTargetMoFdn());
			final WorkflowMoParams moParams = action.getTargetActionParams();
			assertNotNull(moParams);
			assertEquals(WorkflowMoParam.WorkFlowParamType.MAP, moParams.getParamType());
			assertEquals(1, moParams.getParamMap().entrySet().size());
			final Map<String, WorkflowMoParam> data = WorkflowMoParams.getParamMap(moParams.getParamMap().get("data"));
			assertEquals(5, data.entrySet().size());

			// GET each param
			assertEquals("caFingerPrint", data.get("caFingerPrint").getParam());

			final WorkflowMoParam pw = data.get("challengePassword");
			assertEquals("challengePassword", String.valueOf((char[]) pw.getParam()));
			assertEquals("distinguishedName", data.get("distinguishedName").getParam());
			assertEquals("enrollmentServerURL", data.get("enrollmentServerURL").getParam());
			assertEquals(60, data.get("rollbackTimeOut").getParam());

			// Extract MO params
			MoParams targetMoParams = new MoParams(moParams);
			assertTrue(targetMoParams.isMap());
			Map<String, MoParam> targetMoParamsMap = targetMoParams.getParamMap();
			assertEquals(1, targetMoParamsMap.entrySet().size());
			final MoParam dataParam = targetMoParamsMap.get("data");
			assertNotNull(dataParam);
			assertTrue(dataParam.isMap());
			@SuppressWarnings("unchecked")
			Map<String, MoParam> dataParamMap = (Map<String, MoParam>) dataParam.getParam();
			assertEquals(5, dataParamMap.entrySet().size());

			// GET each param
			final MoParam caFingerprintParam = dataParamMap.get("caFingerPrint");
			assertTrue(caFingerprintParam instanceof MoParam);
			assertTrue(caFingerprintParam.isSimple());
			final Object caFingerprint = caFingerprintParam.getParam();
			assertTrue(caFingerprint instanceof String);
			assertTrue("caFingerPrint".equals(caFingerprint));

			final MoParam passwordParam = dataParamMap.get("challengePassword");
			assertTrue(passwordParam.isSimple());
			final Object password = passwordParam.getParam();
			assertTrue(password instanceof char[]);
			assertTrue("challengePassword".equals(String.valueOf((char[]) password)));
			assertTrue("{*****}".equals(passwordParam.toString()));

			final MoParam distinguishedNameParam = dataParamMap.get("distinguishedName");
			assertTrue(distinguishedNameParam instanceof MoParam);
			assertTrue(distinguishedNameParam.isSimple());
			final Object distinguishedName = distinguishedNameParam.getParam();
			assertTrue(distinguishedName instanceof String);
			assertTrue("distinguishedName".equals(distinguishedName));

			final MoParam enrollmentServerURLParam = dataParamMap.get("enrollmentServerURL");
			assertTrue(enrollmentServerURLParam instanceof MoParam);
			assertTrue(enrollmentServerURLParam.isSimple());
			final Object enrollmentServerURL = enrollmentServerURLParam.getParam();
			assertTrue(enrollmentServerURL instanceof String);
			assertTrue("enrollmentServerURL".equals(enrollmentServerURL));

			final MoParam rollbackTimeOutParam = dataParamMap.get("rollbackTimeOut");
			assertTrue(rollbackTimeOutParam instanceof MoParam);
			assertTrue(rollbackTimeOutParam.isSimple());
			final Object rollbackTimeOut = rollbackTimeOutParam.getParam();
			assertTrue((int) rollbackTimeOut == 60);

		}
	}

	@Test
	public void installTrustedCertsParamsSeralizationTest() {
		final MoActionWithParameter theAction = MoActionWithParameter.Security_installTrustedCertificates;
		WorkflowMoActions theMoActions = new WorkflowMoActions();
		final WorkflowMoParams params = getInstallTrustedCertsParams();
		WorkflowMoAction moAction = new WorkflowMoActionWithParams(SECURITY_FDN, theAction, params, POLL_TIMES);
		theMoActions.addTargetAction(moAction);

		// Serialize/deserialize MO actions
		WorkflowMoActions decodedMoActions = serializeDeserializeWorkflowMoActions(theMoActions);
		assertNotNull(decodedMoActions);

		Iterator<WorkflowMoAction> it = decodedMoActions.getTargetActions().iterator();
		while (it.hasNext()) {
			WorkflowMoActionWithParams action = (WorkflowMoActionWithParams) it.next();
			final WorkflowMoParams moParams = action.getTargetActionParams();
			assertEquals(WorkflowMoParam.WorkFlowParamType.MAP, moParams.getParamType());
			final Map<String, WorkflowMoParam> data = moParams.getParamMap();
			assertEquals(4, data.entrySet().size());
			assertEquals("0", data.get("startTime").getParam());
			assertEquals("0", data.get("duration").getParam());
			final WorkflowMoParam cl = data.get("certSpecList");
			assertEquals(WorkflowMoParam.WorkFlowParamType.LIST, cl.getParamType());
			final List<WorkflowMoParam> certList = WorkflowMoParam.getList(cl);
			assertEquals(5, certList.size());
			final WorkflowMoParam cert0 = certList.get(0);
			assertEquals(WorkflowMoParam.WorkFlowParamType.MAP, cert0.getParamType());
			final Map<String, WorkflowMoParam> cert0data = WorkflowMoParams.getParamMap(cert0);
			assertEquals("CORBA_PEERS", cert0data.get("category").getParam());
			final WorkflowMoParam ail = data.get("accountInfoList");
			assertEquals(WorkflowMoParam.WorkFlowParamType.LIST, ail.getParamType());
			final List<WorkflowMoParam> accountInfoList = WorkflowMoParam.getList(ail);
			assertEquals(2, accountInfoList.size());

			// Extract MO params
			MoParams targetMoParams = new MoParams(moParams);
			assertTrue(targetMoParams.isMap());
			Map<String, MoParam> targetMoParamsMap = targetMoParams.getParamMap();
			assertEquals(4, targetMoParamsMap.entrySet().size());

			final MoParam startTimeParam = targetMoParamsMap.get("startTime");
			assertTrue(startTimeParam instanceof MoParam);
			assertTrue(startTimeParam.isSimple());
			final Object startTime = startTimeParam.getParam();
			assertTrue(startTime instanceof String);
			assertTrue("0".equals(startTime));
			assertFalse("{*****}".equals(startTimeParam.toString()));

			final MoParam durationParam = targetMoParamsMap.get("duration");
			assertTrue(durationParam instanceof MoParam);
			assertTrue(durationParam.isSimple());
			final Object duration = durationParam.getParam();
			assertTrue(duration instanceof String);
			assertTrue("0".equals(duration));
			assertFalse("{*****}".equals(durationParam.toString()));

			final MoParam certSpecListParam = targetMoParamsMap.get("certSpecList");
			assertNotNull(certSpecListParam);
			assertTrue(certSpecListParam.isList());
			@SuppressWarnings("unchecked")
			final List<MoParam> certSpecListParamList = (List<MoParam>) certSpecListParam.getParam();
			assertEquals(5, certSpecListParamList.size());
			for (int index = 0; index < 5; index++) {
				final MoParam certSpecMapParam = certSpecListParamList.get(index);
				assertTrue(certSpecMapParam.isMap());
				@SuppressWarnings("unchecked")
				Map<String, MoParam> certSpecMap = (Map<String, MoParam>) certSpecMapParam.getParam();

				final MoParam categoryParam = certSpecMap.get("category");
				assertTrue(categoryParam instanceof MoParam);
				assertTrue(categoryParam.isSimple());
				final Object category = categoryParam.getParam();
				assertTrue(category instanceof String);
				assertTrue("CORBA_PEERS".equals(category));

				final MoParam fileNameParam = certSpecMap.get("fileName");
				assertTrue(fileNameParam instanceof MoParam);
				assertTrue(fileNameParam.isSimple());
				final Object fileName = fileNameParam.getParam();
				assertTrue(fileName instanceof String);
				assertTrue(("abc.cer" + index).equals(fileName));

				final MoParam fingerprintParam = certSpecMap.get("fingerprint");
				assertTrue(fingerprintParam instanceof MoParam);
				assertTrue(fingerprintParam.isSimple());
				final Object fingerprint = fingerprintParam.getParam();
				assertTrue(fingerprint instanceof String);
				assertTrue(("dummyFingerprint" + index).equals(fingerprint));

				final MoParam serialNumberParam = certSpecMap.get("serialNumber");
				assertTrue(serialNumberParam instanceof MoParam);
				assertTrue(serialNumberParam.isSimple());
				final Object serialNumber = serialNumberParam.getParam();
				assertTrue(serialNumber instanceof String);
				assertTrue(("dummySerial" + index).equals(serialNumber));
			}

			final MoParam accountInfoListParam = targetMoParamsMap.get("accountInfoList");
			assertNotNull(accountInfoListParam);
			assertTrue(accountInfoListParam.isList());
			@SuppressWarnings("unchecked")
			final List<MoParam> accountInfoListParamList = (List<MoParam>) accountInfoListParam.getParam();
			assertEquals(2, accountInfoListParamList.size());

			for (int index = 0; index < 2; index++) {
				final MoParam accountInfoMapParam = accountInfoListParamList.get(index);
				assertTrue(accountInfoMapParam.isMap());
				@SuppressWarnings("unchecked")
				Map<String, MoParam> accountInfoMap = (Map<String, MoParam>) accountInfoMapParam.getParam();

				final MoParam passwordParam = accountInfoMap.get("password");
				assertTrue(passwordParam.isSimple());
				final Object password = passwordParam.getParam();
				assertTrue(password instanceof char[]);
				assertTrue(("dummyPassword" + index).equals(String.valueOf((char[]) password)));
				assertTrue("{*****}".equals(passwordParam.toString()));

				final MoParam remoteHostParam = accountInfoMap.get("remoteHost");
				assertTrue(remoteHostParam.isSimple());
				final Object remoteHost = remoteHostParam.getParam();
				assertTrue(remoteHost instanceof String);
				assertTrue(("dummyHost" + index).equals(remoteHost));
				assertFalse("{*****}".equals(remoteHostParam.toString()));

				final MoParam userIDParam = accountInfoMap.get("userID");
				assertTrue(userIDParam.isSimple());
				final Object userID = userIDParam.getParam();
				assertTrue(userID instanceof String);
				assertTrue(("dummyUser" + index).equals(userID));
				assertFalse("{*****}".equals(userIDParam.toString()));
			}
		}
	}

	/**
	 * @param moActions
	 * @return
	 */
	private WorkflowMoActions serializeDeserializeWorkflowMoActions(WorkflowMoActions moActions) {
		String encoded = null;
		try {
			encoded = NscsObjectSerializer.writeObject(moActions);
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertNotNull(encoded);
		WorkflowMoActions decoded = NscsObjectSerializer.readObject(encoded);
		return decoded;
	}

	private static WorkflowMoParams getInstallTrustedCertFromUriParams(final String tdpsUrl) {
		final WorkflowMoParams params = new WorkflowMoParams();
		params.addParam("uri", tdpsUrl);
		params.addParam("uriPassword", PASSWORD, true);
		params.addParam("fingerprint", "NULL");
		return params;
	}

	private static WorkflowMoParams getEnrollmentParams() {
		final WorkflowMoParams params = new WorkflowMoParams();
		params.addParam("challengePassword", PASSWORD, true);
		return params;
	}

	private static WorkflowMoParams getInitCertEnrollmentParams() {
		final WorkflowMoParams params = new WorkflowMoParams();
		final WorkflowMoParams data = new WorkflowMoParams();
		data.addParam("caFingerPrint", "caFingerPrint");
		data.addParam("challengePassword", "challengePassword".toCharArray(), true);
		data.addParam("distinguishedName", "distinguishedName");
		data.addParam("enrollmentServerURL", "enrollmentServerURL");
		data.addParam("rollbackTimeOut", 60);
		params.addParam("data", data);
		return params;
	}

	private static WorkflowMoParams getInstallTrustedCertsParams() {
		final WorkflowMoParams params = new WorkflowMoParams();
		final List<WorkflowMoParams> certSpecList = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			final WorkflowMoParams certSpec = new WorkflowMoParams();
			certSpec.addParam("category", "CORBA_PEERS");
			certSpec.addParam("fileName", "abc.cer" + i);
			certSpec.addParam("fingerprint", "dummyFingerprint" + i);
			certSpec.addParam("serialNumber", "dummySerial" + i);
			certSpecList.add(certSpec);
		}
		params.addParam("certSpecList", certSpecList);
		params.addParam("startTime", "0");
		params.addParam("duration", "0");
		final List<WorkflowMoParams> accountInfoList = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			final WorkflowMoParams accountInfo = new WorkflowMoParams();
			accountInfo.addParam("password", ("dummyPassword" + i).toCharArray(), true);
			accountInfo.addParam("remoteHost", "dummyHost" + i);
			accountInfo.addParam("userID", "dummyUser" + i);
			accountInfoList.add(accountInfo);
		}
		params.addParam("accountInfoList", accountInfoList);
		return params;
	}

}
