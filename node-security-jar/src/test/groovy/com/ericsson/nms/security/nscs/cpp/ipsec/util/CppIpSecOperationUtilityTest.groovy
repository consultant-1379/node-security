/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.cpp.ipsec.util

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.ObjectFactory
import com.ericsson.nms.security.nscs.data.ModelDefinition
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation
import com.ericsson.oss.services.cm.cmshared.dto.CmObject
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse

class CppIpSecOperationUtilityTest extends CdiSpecification {

    @ObjectUnderTest
    CppIpSecOperationUtility cppIpSecOperationUtility

    private Nodes.Node nodeData_Enable_Conf1
    private Nodes.Node nodeData_Enable_Conf2
    private Nodes.Node nodeData_Disable
    private Nodes.Node nodedata_InstallTrustCert

    def setup() {
        nodeData_Enable_Conf1 = new Nodes.Node()
        nodeData_Enable_Conf1.setNodeFdn("node1")
        nodeData_Enable_Conf1.setSubAltName("ALT_SUB_NAME_001")

        nodeData_Enable_Conf2 = new Nodes.Node()
        nodeData_Enable_Conf2.setNodeFdn("node2")
        nodeData_Enable_Conf2.setSubAltName("ALT_SUB_NAME_002")

        nodeData_Disable = new Nodes.Node()
        nodeData_Disable.setNodeFdn("node3")
        nodeData_Disable.setSubAltName("ALT_SUB_NAME_003")

        nodedata_InstallTrustCert = new ObjectFactory().createNodesNode()
        nodedata_InstallTrustCert.setNodeFdn("node4")
        nodedata_InstallTrustCert.setSubAltName("ALT_SUB_NAME_004")

        setupIpSecConfigurationOne()
        setupIpSecConfigurationTwo()
        disableIpSecConfiguration()
        installCertsConfiguration()
    }

    @Override
    def addAdditionalInjectionProperties(final InjectionProperties injectionProperties) {}

    @ImplementationInstance
    NscsCMReaderService nscsCMReaderServiceMock = [
            exists : { final String fdn ->
                return true
            },
            getNormalizableNodeReference : { final NodeReference node ->
                return new MockNormalizableNodeRef()
            },
            getMOAttribute : {final NodeReference node, final String moType,
                              final String namespace, final String attribute ->
                CmResponse cmResponse = new CmResponse()
                CmObject cmObject = new CmObject()
                Map<String, Object> attributes = new LinkedHashMap(0)
                attributes.put(ModelDefinition.CmFunction.SYNC_STATUS, ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name())
                cmObject.setAttributes(attributes)
                Collection<CmObject> cmObjects = new ArrayList<>()
                cmObjects.add(cmObject)
                cmResponse.setTargetedCmObjects(cmObjects)
                return cmResponse
            }
    ] as NscsCMReaderService

    @ImplementationInstance
    CppIpSecStatusUtility cppIpSecStatusUtilityMockConf1 = [
            getIpSecFeatureState : { final NodeReference nodeRef ->
                return "ACTIVATED"
            },
            isOMActivated : { final NodeReference nodeRef, final String featureState ->
                return  true
            },
            isTrafficActivated : { final NodeReference nodeRef, final String featureState ->
                return  true
            },
            getOmConfigurationType : {
                return CppIpSecStatusUtility.CONFIGURATION_1
            }
    ] as CppIpSecStatusUtility

    @ImplementationInstance
    CppIpSecStatusUtility cppIpSecStatusUtilityMockConf2 = [
            getIpSecFeatureState : { final NodeReference nodeRef ->
                return "ACTIVATED"
            },
            isOMActivated : { final NodeReference nodeRef, final String featureState ->
                return  true
            },
            isTrafficActivated : { final NodeReference nodeRef, final String featureState ->
                return  true
            },
            getOmConfigurationType : {
                return CppIpSecStatusUtility.CONFIGURATION_2
            }
    ] as CppIpSecStatusUtility

    @ImplementationInstance
    CppIpSecStatusUtility cppIpSecStatusUtilityMockDisableConf = [
            getIpSecFeatureState : { final NodeReference nodeRef ->
                return "ACTIVATED"
            },
            isOMActivated : { final NodeReference nodeRef, final String featureState ->
                return  false
            },
            isTrafficActivated : { final NodeReference nodeRef, final String featureState ->
                return  true
            },
            getOmConfigurationType : {
                return CppIpSecStatusUtility.CONFIGURATION_2
            }
    ] as CppIpSecStatusUtility


    def "checkIpSecOperation actual = Configuration1, applied = configuration 1"() {
        given:
        setupIpSecConfigurationOne()
        cppIpSecOperationUtility.reader = nscsCMReaderServiceMock
        cppIpSecOperationUtility.cppIpSecStatusUtility = cppIpSecStatusUtilityMockConf1
        when:
        def cppIpSecConfigTypeRsp = cppIpSecOperationUtility.checkIpSecOperation(nodeData_Enable_Conf1)
        then:
        !cppIpSecConfigTypeRsp.getValid()
    }

    def "checkIpSecOperation actual = Configuration1, applied = configuration 2"() {
        given:
        setupIpSecConfigurationTwo()
        cppIpSecOperationUtility.reader = nscsCMReaderServiceMock
        cppIpSecOperationUtility.cppIpSecStatusUtility = cppIpSecStatusUtilityMockConf1
        when:
        def cppIpSecConfigTypeRsp = cppIpSecOperationUtility.checkIpSecOperation(nodeData_Enable_Conf2)
        then:
        !cppIpSecConfigTypeRsp.getValid()
    }

    def "checkIpSecOperation actual = Configuration2, applied = configuration 2"() {
        given:
        setupIpSecConfigurationTwo()
        cppIpSecOperationUtility.reader = nscsCMReaderServiceMock
        cppIpSecOperationUtility.cppIpSecStatusUtility = cppIpSecStatusUtilityMockConf2
        when:
        def cppIpSecConfigTypeRsp = cppIpSecOperationUtility.checkIpSecOperation(nodeData_Enable_Conf2)
        then:
        !cppIpSecConfigTypeRsp.getValid()
    }

    def "checkIpSecOperation actual = Configuration2, applied = configuration 1"() {
        given:
        setupIpSecConfigurationOne()
        cppIpSecOperationUtility.reader = nscsCMReaderServiceMock
        cppIpSecOperationUtility.cppIpSecStatusUtility = cppIpSecStatusUtilityMockConf2
        when:
        def cppIpSecConfigTypeRsp = cppIpSecOperationUtility.checkIpSecOperation(nodeData_Enable_Conf1)
        then:
        !cppIpSecConfigTypeRsp.getValid()
    }
    def "checkIpSecOperation applied = disable configuration"() {
        given:
        disableIpSecConfiguration()
        cppIpSecOperationUtility.reader = nscsCMReaderServiceMock
        cppIpSecOperationUtility.cppIpSecStatusUtility = cppIpSecStatusUtilityMockDisableConf
        when:
        def cppIpSecConfigTypeRsp = cppIpSecOperationUtility.checkIpSecOperation(nodeData_Disable)
        then:
        !cppIpSecConfigTypeRsp.getValid()
    }

    def "checkIpSecOperation applied = install certs"() {
        given:
        installCertsConfiguration()
        cppIpSecOperationUtility.reader = nscsCMReaderServiceMock
        when:
        def cppIpSecConfigTypeRsp = cppIpSecOperationUtility.checkIpSecOperation(nodedata_InstallTrustCert)
        then:
        cppIpSecConfigTypeRsp.getValid()
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
                new Nodes.Node.DisableOMConfiguration.RemoveTrust()
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

    private class MockNormalizableNodeRef implements NormalizableNodeReference {

        @Override
        String getName() {
            return "node1"
        }

        @Override
        String getFdn() {
            return String.format("ManagedElement=%s", "node1")
        }

        @Override
        boolean hasNormalizedRef() {
            return false
        }

        @Override
        NodeReference getNormalizedRef() {
            return new NodeRef("node1")
        }

        @Override
        boolean hasNormalizableRef() {
            return false
        }

        @Override
        NodeReference getNormalizableRef() {
            return null
        }

        @Override
        String getTargetCategory() {
            return TargetTypeInformation.CATEGORY_NODE
        }

        @Override
        String getNeType() {
            return "ERBS"
        }

        @Override
        String getOssModelIdentity() {
            return ""
        }
    }
}
