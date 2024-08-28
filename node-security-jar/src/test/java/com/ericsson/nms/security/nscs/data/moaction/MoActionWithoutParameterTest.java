package com.ericsson.nms.security.nscs.data.moaction;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class MoActionWithoutParameterTest {

	static Map<String, String> theActions = new HashMap<String, String>();
	static {
		theActions.put("Security_cancelCertEnrollment", "cancelCertEnrollment");
		theActions.put("Security_cancelInstallTrustedCertificates", "cancelInstallTrustedCertificates");
		theActions.put("Security_adaptSecurityLevel", "adaptSecurityLevel");
		theActions.put("Security_confirmNewCreds", "confirmNewCreds");
		theActions.put("IpSec_cancelCertEnrollment", "cancelCertEnrollment");
		theActions.put("IpSec_cancelInstallTrustedCertificates", "cancelInstallTrustedCertificates");
		theActions.put("ComEcim_CertM_cancel", "cancel");
		theActions.put("ComEcim_CertM_downloadCrl", "downloadCrl");
		theActions.put("ComEcim_NodeCredential_cancelEnrollment", "cancelEnrollment");
	}

	public static final String SUB_NETWORK_DN = "SubNetwork=sub1,SubNetwork=sub2";
	public static final String ROOT_MO_FDN = "MeContext=<nodeName>";
	public static final String CPP_SECURITY_FDN = ROOT_MO_FDN + ",ManagedElement=1,SystemFunctions=1,Security=1";
	public static final String CPP_IPSEC_FDN = ROOT_MO_FDN + ",ManagedElement=1,IpSystem=1,IpSec=1";
	static Map<String, String> theFdns = new HashMap<String, String>();
	static {
		theFdns.put("Security_cancelCertEnrollment", CPP_SECURITY_FDN);
		theFdns.put("Security_cancelInstallTrustedCertificates", CPP_SECURITY_FDN);
		theFdns.put("Security_adaptSecurityLevel", CPP_SECURITY_FDN);
		theFdns.put("Security_confirmNewCreds", CPP_SECURITY_FDN);
		theFdns.put("IpSec_cancelCertEnrollment", CPP_IPSEC_FDN);
		theFdns.put("IpSec_cancelInstallTrustedCertificates", CPP_IPSEC_FDN);
	}

	static Map<String, String> theComEcimFdns = new HashMap<String, String>();
	static {
		theComEcimFdns.put("ComEcim_CertM_cancel", null);
		theComEcimFdns.put("ComEcim_CertM_downloadCrl", null);
		theComEcimFdns.put("ComEcim_NodeCredential_cancelEnrollment", null);
	}
	
	@Test
	public void ConstructorTest() {
		for (String key : theActions.keySet()) {
			final MoActionWithoutParameter param = MoActionWithoutParameter.valueOf(key);
			assertNotNull(param);
		}
	}

	@Test(expected=IllegalArgumentException.class)
	public void InvalidConstructorTest() {
		MoActionWithoutParameter.valueOf("UnknownAction");
	}

	@Test
	public void getActionTest() {
		for (String key : theActions.keySet()) {
			final MoActionWithoutParameter param = MoActionWithoutParameter.valueOf(key);
			assertEquals(theActions.get(key), param.getAction());
		}
	}

	@Test
	public void getFDNTest() {
		final String myNode = "myNode";
		for (String key : theFdns.keySet()) {
			final MoActionWithoutParameter param = MoActionWithoutParameter.valueOf(key);
			String expectedFdn = theFdns.get(key).replaceAll("<nodeName>", myNode);
			assertEquals(expectedFdn, param.getFDN(ROOT_MO_FDN.replaceAll("<nodeName>", myNode)));
			expectedFdn = SUB_NETWORK_DN + "," + expectedFdn;
			assertEquals(expectedFdn, param.getFDN(SUB_NETWORK_DN + "," + ROOT_MO_FDN.replaceAll("<nodeName>", myNode)));
		}
	}
	
	@Test
	public void getComEcimFDNTest() {
		for (String key : theComEcimFdns.keySet()) {
			final MoActionWithoutParameter param = MoActionWithoutParameter.valueOf(key);
			assertNull("Expected null fdn", param.getFDN(""));
		}
	}
}
