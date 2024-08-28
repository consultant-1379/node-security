/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.security.nscs.cpp.ipsec.input.xml;

import org.junit.Assert;
import org.junit.Test;

import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node.DisableOMConfiguration;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node.DisableOMConfiguration.RemoveTrust;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node.EnableOMConfiguration1;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node.EnableOMConfiguration2;

public class NodesTest {

	@Test
	public void testGetterSetter() {
		
		EnableOMConfiguration1 enableConf1 = new EnableOMConfiguration1();		
		enableConf1.setRemoveTrustOnFailure(true);
		enableConf1.setTrustedCertificateFilePath("/home/smrs/abc.pem");		
		enableConf1.setDnsServer1("1.1.1.1");
		enableConf1.setDnsServer2("2.2.2.2");
		enableConf1.setIpAddressOaMInner("3.3.3.3");
		enableConf1.setNetworkPrefixLength(32L);
		enableConf1.setIpAccessHostEtId("abcdef");
		enableConf1.setDefaultrouter0("192.168.20.1");
		enableConf1.setIpAddressOaMOuter("192.168.32.2");
		enableConf1.setRemoteIpAddress("192.168.99.8");
		enableConf1.setRemoteIpAddressMask("32");
		enableConf1.setPeerOaMIpAddress("192.168.51.1");
		enableConf1.setPeerIdentityIdFqdn("192.168.2.2");
		enableConf1.setPeerIdentityIdType("idType");		
		enableConf1.setTsLocalIpAddressMask("192.168.1.23");		
		Nodes.Node.EnableOMConfiguration1.TsRemoteIpAddressRanges tsRemoteIpAddressRanges1 = new Nodes.Node.EnableOMConfiguration1.TsRemoteIpAddressRanges();
		tsRemoteIpAddressRanges1.setMask("20");
		tsRemoteIpAddressRanges1.setIpAddress("192.168.86.2");		
		enableConf1.getTsRemoteIpAddressRanges().add(tsRemoteIpAddressRanges1);
		
		Assert.assertEquals(enableConf1.isRemoveTrustOnFailure(), true);
		Assert.assertEquals(enableConf1.getTrustedCertificateFilePath(), "/home/smrs/abc.pem");
		Assert.assertEquals(enableConf1.getDnsServer1(), "1.1.1.1");
		Assert.assertEquals(enableConf1.getDnsServer2(), "2.2.2.2");
		Assert.assertEquals(enableConf1.getIpAddressOaMInner(), "3.3.3.3");
		Assert.assertEquals(enableConf1.getNetworkPrefixLength(), 32L);
		Assert.assertEquals(enableConf1.getIpAccessHostEtId(), "abcdef");
		Assert.assertEquals(enableConf1.getDefaultrouter0(), "192.168.20.1");
		Assert.assertEquals(enableConf1.getIpAddressOaMOuter(), "192.168.32.2");
		Assert.assertEquals(enableConf1.getRemoteIpAddress(), "192.168.99.8");
		Assert.assertEquals(enableConf1.getRemoteIpAddressMask(), "32");
		Assert.assertEquals(enableConf1.getPeerOaMIpAddress(), "192.168.51.1");
		Assert.assertEquals(enableConf1.getPeerIdentityIdFqdn(), "192.168.2.2");
		Assert.assertEquals(enableConf1.getPeerIdentityIdType(), "idType");
		Assert.assertEquals(enableConf1.getTsRemoteIpAddressRanges().get(0).getMask(), "20");
		Assert.assertEquals(enableConf1.getTsLocalIpAddressMask(), "192.168.1.23");
		Assert.assertEquals(enableConf1.getTsRemoteIpAddressRanges().get(0).getIpAddress(), "192.168.86.2");
		
		EnableOMConfiguration2 enableConf2 = new EnableOMConfiguration2();
		enableConf2.setRemoveTrustOnFailure(true);
		enableConf2.setTrustedCertificateFilePath("/home/smrs/abc.pem");		
		enableConf2.setDnsServer1("1.1.1.1");
		enableConf2.setDnsServer2("2.2.2.2");
		enableConf2.setNetworkPrefixLength(32L);
		enableConf2.setIpAccessHostEtRef("HostEtRef");
		enableConf2.setIpAddressOaMInner("3.3.3.3");
		enableConf2.setPeerOaMIpAddress("192.168.51.1");
		enableConf2.setPeerIdentityIdFqdn("192.168.2.2");
		enableConf2.setPeerIdentityIdType("idType");		
		enableConf2.setTsLocalIpAddressMask("192.168.1.23");				
		Nodes.Node.EnableOMConfiguration2.TsRemoteIpAddressRanges tsRemoteIpAddressRanges2 = new Nodes.Node.EnableOMConfiguration2.TsRemoteIpAddressRanges();
		tsRemoteIpAddressRanges2.setMask("32");
		tsRemoteIpAddressRanges2.setIpAddress("192.168.88.2");		
		enableConf2.getTsRemoteIpAddressRanges().add(tsRemoteIpAddressRanges2);
		
		Assert.assertEquals(enableConf2.isRemoveTrustOnFailure(), true);
		Assert.assertEquals(enableConf2.getTrustedCertificateFilePath(), "/home/smrs/abc.pem");
		Assert.assertEquals(enableConf2.getDnsServer1(), "1.1.1.1");
		Assert.assertEquals(enableConf2.getDnsServer2(), "2.2.2.2");
		Assert.assertEquals(enableConf2.getNetworkPrefixLength(), 32L);
		Assert.assertEquals(enableConf2.getIpAccessHostEtRef(), "HostEtRef");
		Assert.assertEquals(enableConf2.getIpAddressOaMInner(), "3.3.3.3");
		Assert.assertEquals(enableConf2.getPeerOaMIpAddress(), "192.168.51.1");
		Assert.assertEquals(enableConf2.getPeerIdentityIdFqdn(), "192.168.2.2");
		Assert.assertEquals(enableConf2.getPeerIdentityIdType(), "idType");
		Assert.assertEquals(enableConf2.getTsRemoteIpAddressRanges().get(0).getMask(), "32");
		Assert.assertEquals(enableConf2.getTsLocalIpAddressMask(), "192.168.1.23");
		Assert.assertEquals(enableConf2.getTsRemoteIpAddressRanges().get(0).getIpAddress(), "192.168.88.2");		
					
		DisableOMConfiguration disableConf = new DisableOMConfiguration();
		disableConf.setDefaultRouter0("1.1.1.1");
		disableConf.setDnsServer1("1.1.1.1");
		disableConf.setDnsServer2("2.2.2.2");
		disableConf.setIpAddressOaMOuter("3.3.3.3");
		disableConf.setRemoteIpAddress("4.4.4.4");
		disableConf.setRemoteIpAddressMask("32");
		disableConf.setNetworkPrefixLength(23L);
		disableConf.setRemoveCert(true);		
		RemoveTrust removeTrust = new RemoveTrust();
		removeTrust.setIssuer("Issuer1");
		removeTrust.setSerialNumber(111L);
		disableConf.setRemoveTrust(removeTrust);
		
		Assert.assertEquals(disableConf.getDefaultRouter0(), "1.1.1.1");
		Assert.assertEquals(disableConf.getDnsServer1(), "1.1.1.1");
		Assert.assertEquals(disableConf.getDnsServer2(), "2.2.2.2");
		Assert.assertEquals(disableConf.getIpAddressOaMOuter(), "3.3.3.3");
		Assert.assertEquals(disableConf.getRemoteIpAddress(), "4.4.4.4");
		Assert.assertEquals(disableConf.getRemoteIpAddressMask(), "32");
		Assert.assertEquals(disableConf.getNetworkPrefixLength(), 23L);
		Assert.assertEquals(disableConf.isRemoveCert(), true);		
		Assert.assertNotNull(disableConf.getRemoveTrust());
		Assert.assertEquals(disableConf.getRemoveTrust().getIssuer(), "Issuer1");
		Assert.assertEquals(disableConf.getRemoveTrust().getSerialNumber(), 111L);	
	}
}
