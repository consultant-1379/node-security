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
package com.ericsson.nms.security.nscs.cpp.ipsec.wf;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.exception.IpSecActionException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node.DisableOMConfiguration;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node.DisableOMConfiguration.RemoveTrust;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node.EnableOMConfiguration1;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node.EnableOMConfiguration2;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node.InstallTrustCertificates;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node.SiteBasic;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.ObjectFactory;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.CppIpSecStatusUtility;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.CppIpSecStatusUtility.ConfigurationInfo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;

@RunWith(MockitoJUnitRunner.class)
public class CppIpSecWfsConfigurationTest {

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    private IpSecRequest mockIpSecRequest;

    @Mock
    private WorkflowHandler mockWfHandler;

    @InjectMocks
    private CppIpSecWfsConfiguration testObj;

    @Mock
    private NscsCMReaderService mockReader;

    @Mock
    private CppIpSecStatusUtility mockIpSecStatusUtility;

    @Mock
    private ConfigurationInfo mockConfInfo;

    @Mock
    private NormalizableNodeReference mockNormalizableNodeReference;

    private Node nodeData_Enable_Conf1;
    private Node nodeData_Enable_Conf2;
    private Node nodeData_Disable;
    private Node nodeData_siteBasic;

    @Before
    public void setupNode() {

        nodeData_Enable_Conf1 = new Node();
        nodeData_Enable_Conf1.setNodeFdn("MeContext=NODE_001");
        nodeData_Enable_Conf1.setSubAltName("ALT_SUB_NAME_001");

        nodeData_Enable_Conf2 = new Node();
        nodeData_Enable_Conf2.setNodeFdn("MeContext=NODE_002");
        nodeData_Enable_Conf2.setSubAltName("ALT_SUB_NAME_002");

        nodeData_Disable = new Node();
        nodeData_Disable.setNodeFdn("MeContext=NODE_003");
        nodeData_Disable.setSubAltName("ALT_SUB_NAME_003");

        final EnableOMConfiguration1 enableConf1 = new EnableOMConfiguration1();
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
        enableConf1.setPeerOaMIpAddress("192.168.51.1");
        enableConf1.setPeerIdentityIdFqdn("192.168.2.2");
        enableConf1.setPeerIdentityIdType("idType");
        enableConf1.setTsLocalIpAddressMask("192.168.1.23");
        final Nodes.Node.EnableOMConfiguration1.TsRemoteIpAddressRanges tsRemoteIpAddressRanges1 = new Nodes.Node.EnableOMConfiguration1.TsRemoteIpAddressRanges();
        tsRemoteIpAddressRanges1.setMask("20");
        tsRemoteIpAddressRanges1.setIpAddress("192.168.86.2");
        enableConf1.getTsRemoteIpAddressRanges().add(tsRemoteIpAddressRanges1);

        nodeData_Enable_Conf1.setEnableOMConfiguration1(enableConf1);

        final EnableOMConfiguration2 enableConf2 = new EnableOMConfiguration2();
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
        final Nodes.Node.EnableOMConfiguration2.TsRemoteIpAddressRanges tsRemoteIpAddressRanges2 = new Nodes.Node.EnableOMConfiguration2.TsRemoteIpAddressRanges();
        tsRemoteIpAddressRanges2.setMask("32");
        tsRemoteIpAddressRanges2.setIpAddress("192.168.88.2");
        enableConf2.getTsRemoteIpAddressRanges().add(tsRemoteIpAddressRanges2);

        nodeData_Enable_Conf2.setEnableOMConfiguration2(enableConf2);

        final DisableOMConfiguration disableConf = new DisableOMConfiguration();
        disableConf.setDefaultRouter0("1.1.1.1");
        disableConf.setDnsServer1("1.1.1.1");
        disableConf.setDnsServer2("2.2.2.2");
        disableConf.setIpAddressOaMOuter("3.3.3.3");
        disableConf.setNetworkPrefixLength(23L);
        disableConf.setRemoveCert(true);
        final RemoveTrust removeTrust = new RemoveTrust();
        removeTrust.setIssuer("Issuer1");
        removeTrust.setSerialNumber(111L);
        disableConf.setRemoveTrust(removeTrust);
        nodeData_Disable.setDisableOMConfiguration(disableConf);

    }

    @SuppressWarnings("unchecked")
    @Test
    @Ignore
    public void testProcessCommand_ForEnable_Conf1() {
        Mockito.when(mockIpSecRequest.getNodeFdn()).thenReturn("Node123");
        Mockito.when(mockIpSecRequest.getIpSecRequestType()).thenReturn(IpSecRequestType.IP_SEC_ENABLE_CONF1);
        Mockito.when(mockIpSecRequest.getXmlRepresntationOfNode()).thenReturn(nodeData_Enable_Conf1);
        testObj.configureIpSecWorkflow(mockIpSecRequest);
        Mockito.verify(mockWfHandler).startWorkflowInstance(Mockito.any(NodeReference.class), Mockito.anyString(), Mockito.anyMap());
    }

    @SuppressWarnings("unchecked")
    @Test
    @Ignore
    public void testProcessCommand_ForEnable_Conf2() {
        Mockito.when(mockIpSecRequest.getNodeFdn()).thenReturn("Node123");
        Mockito.when(mockIpSecRequest.getIpSecRequestType()).thenReturn(IpSecRequestType.IP_SEC_ENABLE_CONF2);
        Mockito.when(mockIpSecRequest.getXmlRepresntationOfNode()).thenReturn(nodeData_Enable_Conf2);
        testObj.configureIpSecWorkflow(mockIpSecRequest);
        Mockito.verify(mockWfHandler).startWorkflowInstance(Mockito.any(NodeReference.class), Mockito.anyString(), Mockito.anyMap());
    }

    @SuppressWarnings("unchecked")
    @Test
    @Ignore
    public void testProcessCommand_ForDisable() {
        Mockito.when(mockIpSecRequest.getNodeFdn()).thenReturn("Node123");
        Mockito.when(mockIpSecRequest.getIpSecRequestType()).thenReturn(IpSecRequestType.IP_SEC_DISABLE);
        Mockito.when(mockIpSecRequest.getXmlRepresntationOfNode()).thenReturn(nodeData_Disable);
        Mockito.when(mockReader.getNormalizableNodeReference(Mockito.any(NodeReference.class))).thenReturn(mockNormalizableNodeReference);
        Mockito.when(mockIpSecStatusUtility.getIpSecFeatureState(Mockito.any(NodeReference.class))).thenReturn("ACTIVATED");
        Mockito.when(mockIpSecStatusUtility.isOMActivated(Mockito.any(NodeReference.class), Mockito.anyString())).thenReturn(true);
        testObj.configureIpSecWorkflow(mockIpSecRequest);
        Mockito.verify(mockWfHandler).startWorkflowInstance(Mockito.any(NodeReference.class), Mockito.anyString(), Mockito.anyMap());
    }

    @SuppressWarnings("unchecked")
    @Test(expected = IpSecActionException.class)
    @Ignore
    public void testProcessCommand_ForException() {
        Mockito.when(mockIpSecRequest.getNodeFdn()).thenReturn("Node123");
        Mockito.when(mockIpSecRequest.getIpSecRequestType()).thenReturn(IpSecRequestType.IP_SEC_ENABLE_CONF1);
        Mockito.when(mockIpSecRequest.getXmlRepresntationOfNode()).thenReturn(nodeData_Enable_Conf1);
        Mockito.when(mockWfHandler.startWorkflowInstance(Mockito.any(NodeReference.class), Mockito.anyString(), Mockito.anyMap()))
                .thenThrow(RuntimeException.class);
        testObj.configureIpSecWorkflow(mockIpSecRequest);
        Mockito.verify(mockWfHandler).startWorkflowInstance(Mockito.any(NodeReference.class), Mockito.anyString(), Mockito.anyMap());
    }

    @SuppressWarnings("unchecked")
    @Test
    @Ignore
    public void testProcessCommand_ForInstallCerts() {
        final Node installTrustCertificatesNode = new ObjectFactory().createNodesNode();
        final InstallTrustCertificates installTrustCertificates = new ObjectFactory().createNodesNodeInstallTrustCertificates();
        installTrustCertificates.setRemoveTrustOnFailure(false);
        installTrustCertificates.setTrustedCertificateFilePath("/tmp/cert.pem");
        installTrustCertificatesNode.setInstallTrustCertificates(installTrustCertificates);
        Mockito.when(mockIpSecRequest.getNodeFdn()).thenReturn("Node123");
        Mockito.when(mockIpSecRequest.getIpSecRequestType()).thenReturn(IpSecRequestType.IP_SEC_INSTALL_CERTS);
        Mockito.when(mockIpSecRequest.getXmlRepresntationOfNode()).thenReturn(installTrustCertificatesNode);
        testObj.configureIpSecWorkflow(mockIpSecRequest);
        Mockito.verify(mockWfHandler).startWorkflowInstance(Mockito.any(NodeReference.class), Mockito.anyString(), Mockito.anyMap());
    }

    @Test
    @Ignore
    public void testProcessCommand_ForEnable_AlreadyEnabled() {
        Mockito.when(mockIpSecRequest.getNodeFdn()).thenReturn("Node123");
        Mockito.when(mockIpSecRequest.getIpSecRequestType()).thenReturn(IpSecRequestType.IP_SEC_ENABLE_CONF1);
        Mockito.when(mockIpSecRequest.getXmlRepresntationOfNode()).thenReturn(nodeData_Enable_Conf1);
        Mockito.when(mockReader.getNormalizableNodeReference(Mockito.any(NodeReference.class))).thenReturn(mockNormalizableNodeReference);
        Mockito.when(mockIpSecStatusUtility.getIpSecFeatureState(Mockito.any(NodeReference.class))).thenReturn("ACTIVATED");
        Mockito.when(mockIpSecStatusUtility.isOMActivated(Mockito.any(NodeReference.class), Mockito.anyString())).thenReturn(true);
        Mockito.when(mockIpSecStatusUtility.isOMActivated(Mockito.any(NodeReference.class), Mockito.anyString())).thenReturn(true);
        Mockito.when(mockIpSecStatusUtility.getConfigurationInfo()).thenReturn(mockConfInfo);
        Mockito.when(mockConfInfo.getOmConfigurationType()).thenReturn("(Configuration 1)");
        testObj.configureIpSecWorkflow(mockIpSecRequest);

    }

    @Test
    @Ignore
    public void testProcessCommand_ForDisable_AlreadyDisabled() {
        Mockito.when(mockIpSecRequest.getNodeFdn()).thenReturn("Node123");
        Mockito.when(mockIpSecRequest.getIpSecRequestType()).thenReturn(IpSecRequestType.IP_SEC_DISABLE);
        Mockito.when(mockIpSecRequest.getXmlRepresntationOfNode()).thenReturn(nodeData_Disable);
        Mockito.when(mockReader.getNormalizableNodeReference(Mockito.any(NodeReference.class))).thenReturn(mockNormalizableNodeReference);
        Mockito.when(mockIpSecStatusUtility.getIpSecFeatureState(Mockito.any(NodeReference.class))).thenReturn("DEACTIVATED");
        Mockito.when(mockIpSecStatusUtility.isOMActivated(Mockito.any(NodeReference.class), Mockito.anyString())).thenReturn(false);
        testObj.configureIpSecWorkflow(mockIpSecRequest);
    }

    @Test
    @Ignore
    public void testProcessCommand_ForSiteBasic() {
        Mockito.when(mockIpSecRequest.getNodeFdn()).thenReturn("Node123");
        Mockito.when(mockIpSecRequest.getIpSecRequestType()).thenReturn(IpSecRequestType.IP_SEC_ENABLE_CONF1);
        nodeData_siteBasic = nodeData_Enable_Conf1;
        nodeData_siteBasic.setSiteBasic(new SiteBasic());
        Mockito.when(mockIpSecRequest.getXmlRepresntationOfNode()).thenReturn(nodeData_siteBasic);
        testObj.configureIpSecWorkflow(mockIpSecRequest);
        Mockito.verify(mockWfHandler).startWorkflowInstance(Mockito.any(NodeReference.class), Mockito.anyString(), Mockito.anyMap());
    }
}
