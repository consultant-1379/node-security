package com.ericsson.nms.security.nscs.cpp.ipsec.wf

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.ObjectFactory

class CppIpSecWfsConfigurationFcTest extends CdiSpecification {

    @ObjectUnderTest
    CppIpSecWfsConfiguration cppIpSecWfsConfiguration

    private Nodes.Node nodeData_Enable_Conf1
    private Nodes.Node nodeData_Enable_Conf2
    private Nodes.Node nodeData_Disable
    private Nodes.Node nodedata_InstallTrustCert

    def setup() {
        nodeData_Enable_Conf1 = new Nodes.Node()
        nodeData_Enable_Conf1.setNodeFdn("MeContext=NODE_001")
        nodeData_Enable_Conf1.setSubAltName("ALT_SUB_NAME_001")

        nodeData_Enable_Conf2 = new Nodes.Node()
        nodeData_Enable_Conf2.setNodeFdn("MeContext=NODE_002")
        nodeData_Enable_Conf2.setSubAltName("ALT_SUB_NAME_002")

        nodeData_Disable = new Nodes.Node()
        nodeData_Disable.setNodeFdn("MeContext=NODE_003")
        nodeData_Disable.setSubAltName("ALT_SUB_NAME_003")

        nodedata_InstallTrustCert = new ObjectFactory().createNodesNode()
        nodedata_InstallTrustCert.setNodeFdn("MeContext=NODE_004")
        nodedata_InstallTrustCert.setSubAltName("ALT_SUB_NAME_004")

        setupIpSecConfigurationOne()
        setupIpSecConfigurationTwo()
        disableIpSecConfiguration()
        installCertsConfiguration()
    }

    @Override
    Object addAdditionalInjectionProperties(final InjectionProperties injectionProperties) {
    }

    def "setup configuration one ipsec workflow"() {
        given:
        IpSecRequest ipSecRequest = new IpSecRequest()
        ipSecRequest.setNodeFdn("MeContext=NODE_001")
        ipSecRequest.setForceUpdate(true)
        ipSecRequest.setIpSecRequestType(IpSecRequestType.IP_SEC_ENABLE_CONF1)
        ipSecRequest.setXmlRepresntationOfNode(nodeData_Enable_Conf1)
        when:
        IpSecRequestWfsConfiguration ipSecRequestWfsConfiguration =
                cppIpSecWfsConfiguration.configureIpSecWorkflow(ipSecRequest)
        then:
        ipSecRequestWfsConfiguration.getNodeFdn() == ipSecRequest.getNodeFdn() &&
        ipSecRequestWfsConfiguration.getWorkflowName() == "CPPActivateIpSec"
    }

    def "setup configuration two ipsec workflow"() {
        given:
        IpSecRequest ipSecRequest = new IpSecRequest()
        ipSecRequest.setNodeFdn("MeContext=NODE_002")
        ipSecRequest.setForceUpdate(true)
        ipSecRequest.setIpSecRequestType(IpSecRequestType.IP_SEC_ENABLE_CONF2)
        ipSecRequest.setXmlRepresntationOfNode(nodeData_Enable_Conf2)
        when:
        IpSecRequestWfsConfiguration ipSecRequestWfsConfiguration =
                cppIpSecWfsConfiguration.configureIpSecWorkflow(ipSecRequest)
        then:
        ipSecRequestWfsConfiguration.getNodeFdn() == ipSecRequest.getNodeFdn() &&
                ipSecRequestWfsConfiguration.getWorkflowName() == "CPPActivateIpSec"
    }

    def "disable configuration ipsec workflow"() {
        given:
        IpSecRequest ipSecRequest = new IpSecRequest()
        ipSecRequest.setNodeFdn("MeContext=NODE_003")
        ipSecRequest.setForceUpdate(true)
        ipSecRequest.setIpSecRequestType(IpSecRequestType.IP_SEC_DISABLE)
        ipSecRequest.setXmlRepresntationOfNode(nodeData_Disable)
        when:
        IpSecRequestWfsConfiguration ipSecRequestWfsConfiguration =
                cppIpSecWfsConfiguration.configureIpSecWorkflow(ipSecRequest)
        then:
        ipSecRequestWfsConfiguration.getNodeFdn() == ipSecRequest.getNodeFdn() &&
                ipSecRequestWfsConfiguration.getWorkflowName() == "CPPDeactivateIpSec"
    }

    def "install cert configuration ipsec workflow"() {
        given:
        IpSecRequest ipSecRequest = new IpSecRequest()
        ipSecRequest.setNodeFdn("MeContext=NODE_004")
        ipSecRequest.setForceUpdate(true)
        ipSecRequest.setIpSecRequestType(IpSecRequestType.IP_SEC_INSTALL_CERTS)
        ipSecRequest.setXmlRepresntationOfNode(nodedata_InstallTrustCert)
        when:
        IpSecRequestWfsConfiguration ipSecRequestWfsConfiguration =
                cppIpSecWfsConfiguration.configureIpSecWorkflow(ipSecRequest)
        then:
        ipSecRequestWfsConfiguration.getNodeFdn() == ipSecRequest.getNodeFdn() &&
                ipSecRequestWfsConfiguration.getWorkflowName() == "CPPInstallCertificatesIpSec"
    }

    private void setupIpSecConfigurationOne() {
        final Nodes.Node.EnableOMConfiguration1 enableConf1 = new Nodes.Node.EnableOMConfiguration1()
        enableConf1.setRemoveTrustOnFailure(true)
        enableConf1.setTrustedCertificateFilePath("/home/smrs/abc.pem")
        enableConf1.setDnsServer1("1.1.1.1")
        enableConf1.setDnsServer2("2.2.2.2")
        enableConf1.setIpAddressOaMInner("3.3.3.3")
        enableConf1.setNetworkPrefixLength(32L)
        enableConf1.setIpAccessHostEtId("abcdef")
        enableConf1.setDefaultrouter0("192.168.20.1")
        enableConf1.setIpAddressOaMOuter("192.168.32.2")
        enableConf1.setRemoteIpAddress("192.168.99.8")
        enableConf1.setPeerOaMIpAddress("192.168.51.1")
        enableConf1.setPeerIdentityIdFqdn("192.168.2.2")
        enableConf1.setPeerIdentityIdType("idType")
        enableConf1.setTsLocalIpAddressMask("192.168.1.23")
        final Nodes.Node.EnableOMConfiguration1.TsRemoteIpAddressRanges tsRemoteIpAddressRanges1 =
                new Nodes.Node.EnableOMConfiguration1.TsRemoteIpAddressRanges()
        tsRemoteIpAddressRanges1.setMask("20")
        tsRemoteIpAddressRanges1.setIpAddress("192.168.86.2")
        enableConf1.getTsRemoteIpAddressRanges().add(tsRemoteIpAddressRanges1)
        nodeData_Enable_Conf1.setEnableOMConfiguration1(enableConf1)
    }

    private void setupIpSecConfigurationTwo() {
        final Nodes.Node.EnableOMConfiguration2 enableConf2 = new Nodes.Node.EnableOMConfiguration2()
        enableConf2.setRemoveTrustOnFailure(true)
        enableConf2.setTrustedCertificateFilePath("/home/smrs/abc.pem")
        enableConf2.setDnsServer1("1.1.1.1")
        enableConf2.setDnsServer2("2.2.2.2")
        enableConf2.setNetworkPrefixLength(32L)
        enableConf2.setIpAccessHostEtRef("HostEtRef")
        enableConf2.setIpAddressOaMInner("3.3.3.3")
        enableConf2.setPeerOaMIpAddress("192.168.51.1")
        enableConf2.setPeerIdentityIdFqdn("192.168.2.2")
        enableConf2.setPeerIdentityIdType("idType")
        enableConf2.setTsLocalIpAddressMask("192.168.1.23")
        final Nodes.Node.EnableOMConfiguration2.TsRemoteIpAddressRanges tsRemoteIpAddressRanges2 =
                new Nodes.Node.EnableOMConfiguration2.TsRemoteIpAddressRanges()
        tsRemoteIpAddressRanges2.setMask("32")
        tsRemoteIpAddressRanges2.setIpAddress("192.168.88.2")
        enableConf2.getTsRemoteIpAddressRanges().add(tsRemoteIpAddressRanges2)
        nodeData_Enable_Conf2.setEnableOMConfiguration2(enableConf2)
    }

    private void disableIpSecConfiguration() {
        final Nodes.Node.DisableOMConfiguration disableConf = new Nodes.Node.DisableOMConfiguration()
        disableConf.setDefaultRouter0("1.1.1.1")
        disableConf.setDnsServer1("1.1.1.1")
        disableConf.setDnsServer2("2.2.2.2")
        disableConf.setIpAddressOaMOuter("3.3.3.3")
        disableConf.setNetworkPrefixLength(23L)
        disableConf.setRemoveCert(true)
        final Nodes.Node.DisableOMConfiguration.RemoveTrust removeTrust =
                new Nodes.Node.DisableOMConfiguration.RemoveTrust();
        removeTrust.setIssuer("Issuer1")
        removeTrust.setSerialNumber(111L)
        disableConf.setRemoveTrust(removeTrust)
        nodeData_Disable.setDisableOMConfiguration(disableConf)
    }

    private void installCertsConfiguration() {
        final Nodes.Node.InstallTrustCertificates installTrustCertificates = new ObjectFactory().createNodesNodeInstallTrustCertificates()
        installTrustCertificates.setRemoveTrustOnFailure(false)
        installTrustCertificates.setTrustedCertificateFilePath("/tmp/cert.pem")
        nodedata_InstallTrustCert.setInstallTrustCertificates(installTrustCertificates)
    }
}
