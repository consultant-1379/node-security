package com.ericsson.nms.security.nscs.data.moaction;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class MoActionWithParameterTest {

	static Map<String, String> theActions = new HashMap<String, String>();
	static {
		theActions.put("Security_initCertEnrollment", "initCertEnrollment");
		theActions.put("Security_installTrustedCertificates", "installTrustedCertificates");
		theActions.put("IpSec_initCertEnrollment", "initCertEnrollment");
		theActions.put("IpSec_installTrustedCertificates", "installTrustedCertificates");
		theActions.put("IpSec_changeIpForOamSetting", "changeIpForOamSetting");
		theActions.put("IpSec_changeIpForOamSetting_old", "changeIpForOamSetting");
		theActions.put("ComEcim_CertM_installTrustedCertFromUri", "installTrustedCertFromUri");
		theActions.put("ComEcim_CertM_removeTrustedCert", "removeTrustedCert");
		theActions.put("ComEcim_NodeCredential_installCredentialFromUri", "installCredentialFromUri");
		theActions.put("ComEcim_NodeCredential_startOfflineCsrEnrollment", "startOfflineCsrEnrollment");
		theActions.put("ComEcim_NodeCredential_startOnlineEnrollment", "startOnlineEnrollment");
	}

	public static final String SUB_NETWORK_DN = "SubNetwork=sub1,SubNetwork=sub2";
	public static final String ROOT_MO_FDN = "MeContext=<nodeName>";
	public static final String CPP_SECURITY_FDN = ROOT_MO_FDN + ",ManagedElement=1,SystemFunctions=1,Security=1";
	public static final String CPP_IPSEC_FDN = ROOT_MO_FDN + ",ManagedElement=1,IpSystem=1,IpSec=1";
	public static final String CPP_RBS_CONF_FDN = ROOT_MO_FDN + ",ManagedElement=1,NodeManagementFunction=1,RbsConfiguration=1";
	public static final String CPP_OLD_RBS_CONF_FDN = ROOT_MO_FDN + ",ManagedElement=1,ENodeBFunction=1,RbsConfiguration=1";
	static Map<String, String> theFdns = new HashMap<String, String>();
	static {
		theFdns.put("Security_initCertEnrollment", CPP_SECURITY_FDN);
		theFdns.put("Security_installTrustedCertificates", CPP_SECURITY_FDN);
		theFdns.put("IpSec_initCertEnrollment", CPP_IPSEC_FDN);
		theFdns.put("IpSec_installTrustedCertificates", CPP_IPSEC_FDN);
		theFdns.put("IpSec_changeIpForOamSetting", CPP_RBS_CONF_FDN);
		theFdns.put("IpSec_changeIpForOamSetting_old", CPP_OLD_RBS_CONF_FDN);
	}
	static Map<String, String> theComEcimFdns = new HashMap<String, String>();
	static {
		theComEcimFdns.put("ComEcim_CertM_installTrustedCertFromUri", null);
		theComEcimFdns.put("ComEcim_CertM_removeTrustedCert", null);
		theComEcimFdns.put("ComEcim_NodeCredential_installCredentialFromUri", null);
		theComEcimFdns.put("ComEcim_NodeCredential_startOfflineCsrEnrollment", null);
		theComEcimFdns.put("ComEcim_NodeCredential_startOnlineEnrollment", null);
	}

	@Test
	public void ConstructorTest() {
		for (String key : theActions.keySet()) {
			final MoActionWithParameter param = MoActionWithParameter.valueOf(key);
			assertNotNull(param);
		}
	}

	@Test(expected=IllegalArgumentException.class)
	public void InvalidConstructorTest() {
		MoActionWithParameter.valueOf("UnknownAction");
	}

	@Test
	public void getActionTest() {
		for (String key : theActions.keySet()) {
			final MoActionWithParameter param = MoActionWithParameter.valueOf(key);
			assertEquals(theActions.get(key), param.getAction());
		}
	}

	@Test
	public void getFDNTest() {
		final String myNode = "myNode";
		for (String key : theFdns.keySet()) {
			final MoActionWithParameter param = MoActionWithParameter.valueOf(key);
			String expectedFdn = theFdns.get(key).replaceAll("<nodeName>", myNode);
			assertEquals(expectedFdn, param.getFDN(ROOT_MO_FDN.replaceAll("<nodeName>", myNode)));
			expectedFdn = SUB_NETWORK_DN + "," + expectedFdn;
			assertEquals(expectedFdn, param.getFDN(SUB_NETWORK_DN + "," + ROOT_MO_FDN.replaceAll("<nodeName>", myNode)));
		}
	}
	
	@Test
	public void getComEcimFDNTest() {
		for (String key : theComEcimFdns.keySet()) {
			final MoActionWithParameter param = MoActionWithParameter.valueOf(key);
			assertNull("Expected null fdn", param.getFDN(""));
		}
	}
}
