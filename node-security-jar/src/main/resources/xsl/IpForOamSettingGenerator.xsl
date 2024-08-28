<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:XSL="http://www.w3.org/1999/XSL/Transform"
        >
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
    <xsl:strip-space elements="*"/>
    <!--xsl:output omit-xml-declaration="no" indent="yes"/-->
    <xsl:variable name="ipAddress"/>
    <xsl:variable name="networkPrefixLength"/>
    <xsl:variable name="ipAddressOaMOuter"/>
    <xsl:variable name="ipAddressOaMInner"/>
    <xsl:variable name="ipAccessHostEtId"/>
    <xsl:variable name="vid"/>
    <xsl:template match="/">
        <xsl:comment>
            ProductNumber  = 'CXC 179 9121/4' R-State  = 'R2C'
            DocumentNumber = '6/006 91-HRB 105 500'
            Node version: L14B
            File    : IpForOamSetting.xml
            Purpose : Template for the IpForOamSetting file for OaM,
            for an OAM IP configuration of the following type:
            o IPsec for OAM is activated
            o OAM has a separate IpInterface/VLAN
            o OAM has a separate IpAccessHostEt (outer IP host)

        </xsl:comment>
        <xsl:if test="Nodes/Node/EnableOMConfiguration1">
            <xsl:apply-templates select="Nodes/Node/EnableOMConfiguration1"/>
        </xsl:if>
        <xsl:if test="Nodes/Node/EnableOMConfiguration2">
            <xsl:apply-templates select="Nodes/Node/EnableOMConfiguration2"/>
        </xsl:if>

    </xsl:template>
    <xsl:template match="Nodes/Node/EnableOMConfiguration1">
        <xsl:element name="SiteBasic" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
            <xsl:attribute name="xsi:noNamespaceSchemaLocation">SiteBasic.xsd</xsl:attribute>

            <xsl:variable name="networkPrefixLength"><xsl:value-of select="networkPrefixLength"/></xsl:variable>
            <xsl:variable name="ipAddressOaMOuter"><xsl:value-of select="ipAddressOaMOuter"/></xsl:variable>
            <xsl:variable name="ipAddressOaMInner"><xsl:value-of select="ipAddressOaMInner"/></xsl:variable>
            <xsl:variable name="ipAccessHostEtId"><xsl:value-of select="ipAccessHostEtId"/></xsl:variable>
            <xsl:variable name="peerOaMIpAddress"><xsl:value-of select="peerOaMIpAddress"/></xsl:variable>
            <xsl:variable name="remoteIpAddress"><xsl:value-of select="remoteIpAddress"/></xsl:variable>
            <xsl:variable name="remoteIpAddressMask"><xsl:value-of select="remoteIpAddressMask"/></xsl:variable>
			<xsl:variable name="vid"><xsl:value-of select="vid"/></xsl:variable>
			
            <xsl:element name="Format">
                <xsl:attribute name="revision">T</xsl:attribute>
            </xsl:element>
            <xsl:call-template name="prepareIp">
                <xsl:with-param name="ipAddressOaMInner" select="$ipAddressOaMInner"/>
            </xsl:call-template>
            <xsl:call-template name="prepareIpInterface">
                <xsl:with-param name="networkPrefixLength" select="$networkPrefixLength"/>
            </xsl:call-template>
            <xsl:element name="IpSystem">
                <xsl:element name="IpAccessHostEt">
                    <xsl:attribute name="ipAccessHostEtId"><xsl:value-of select="$ipAccessHostEtId"/></xsl:attribute>
                    <xsl:attribute name="ipInterfaceMoRef">DU-1-IP-3</xsl:attribute>
                    <xsl:attribute name="ipAddress"><xsl:value-of select="$ipAddressOaMOuter"/></xsl:attribute>
                    <xsl:attribute name="userLabel">IPsec tunnel endpoint for OAM</xsl:attribute>
                </xsl:element>
                <xsl:call-template name="prepareVpnInterface">
                    <xsl:with-param name="networkPrefixLength" select="$networkPrefixLength"/>
                    <xsl:with-param name="ipAccessHostEtRef" select="$ipAccessHostEtId"/>
                </xsl:call-template>
                <xsl:call-template name="prepareIpSec">
                    <xsl:with-param name="ipAddressOaMInner" select="$ipAddressOaMInner"/>
                    <xsl:with-param name="peerIpAddress" select="$peerOaMIpAddress"/>

                </xsl:call-template>
                <xsl:element name="AccessControlList">
                    <xsl:attribute name="accessControlListId">3</xsl:attribute>
                    <xsl:call-template name="prepareAclEntries">
                        <xsl:with-param name="aclPriority" select="0"/>
                        <xsl:with-param name="aclAction" select="'BYPASS'"/>
                        <xsl:with-param name="icmpType" select="256"/>
                        <xsl:with-param name="localIpAddress" select="$ipAddressOaMOuter"/>
                        <xsl:with-param name="localIpAddressMask" select="32"/>
                        <xsl:with-param name="remoteIpAddress" select="$remoteIpAddress"/>
                        <xsl:with-param name="remoteIpAddressMask" select="$remoteIpAddressMask"/>
                        <xsl:with-param name="protocol" select="'TCP'"/>
                    </xsl:call-template>
                    <xsl:call-template name="prepareAclEntries">
                        <xsl:with-param name="aclPriority" select="1"/>
                        <xsl:with-param name="aclAction" select="'BYPASS'"/>
                        <xsl:with-param name="icmpType" select="256"/>
                        <xsl:with-param name="localIpAddress" select="$ipAddressOaMOuter"/>
                        <xsl:with-param name="localIpAddressMask" select="32"/>
                        <xsl:with-param name="remoteIpAddress" select="$remoteIpAddress"/>
                        <xsl:with-param name="remoteIpAddressMask" select="$remoteIpAddressMask"/>
                        <xsl:with-param name="protocol" select="'UDP'"/>
                    </xsl:call-template>
                    <xsl:call-template name="prepareAclEntries">
                        <xsl:with-param name="aclPriority" select="2"/>
                        <xsl:with-param name="aclAction" select="'BYPASS'"/>
                        <xsl:with-param name="icmpType" select="256"/>
                        <xsl:with-param name="localIpAddress" select="$ipAddressOaMOuter"/>
                        <xsl:with-param name="localIpAddressMask" select="32"/>
                        <xsl:with-param name="remoteIpAddress" select="$remoteIpAddress"/>
                        <xsl:with-param name="remoteIpAddressMask" select="$remoteIpAddressMask"/>
                        <xsl:with-param name="protocol" select="'ICMP'"/>
                    </xsl:call-template>
                    <xsl:call-template name="prepareAclEntries">
                        <xsl:with-param name="aclPriority" select="3"/>
                        <xsl:with-param name="aclAction" select="'DROP'"/>
                        <xsl:with-param name="icmpType" select="0"/>
                        <xsl:with-param name="localIpAddress" select="'0.0.0.0'"/>
                        <xsl:with-param name="localIpAddressMask" select="0"/>
                        <xsl:with-param name="remoteIpAddress" select="'0.0.0.0'"/>
                        <xsl:with-param name="remoteIpAddressMask" select="0"/>
                        <xsl:with-param name="protocol" select="'ANY'"/>
                    </xsl:call-template>


                </xsl:element>

            </xsl:element>

        </xsl:element>
    </xsl:template>
    <xsl:template match="Nodes/Node/EnableOMConfiguration2">
        <xsl:element name="SiteBasic" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
            <xsl:attribute name="xsi:noNamespaceSchemaLocation">SiteBasic.xsd</xsl:attribute>

            <xsl:variable name="networkPrefixLength"><xsl:value-of select="networkPrefixLength"/></xsl:variable>
            <xsl:variable name="ipAddressOaMInner"><xsl:value-of select="ipAddressOaMInner"/></xsl:variable>
            <xsl:variable name="ipAccessHostEtRef"><xsl:value-of select="ipAccessHostEtRef"/></xsl:variable>
            <xsl:variable name="peerOaMIpAddress"><xsl:value-of select="peerOaMIpAddress"/></xsl:variable>
			<xsl:variable name="vid"><xsl:value-of select="vid"/></xsl:variable>
            <xsl:element name="Format">
                <xsl:attribute name="revision">T</xsl:attribute>
            </xsl:element>
            <xsl:call-template name="prepareIp">
                <xsl:with-param name="ipAddressOaMInner" select="$ipAddressOaMInner"/>
            </xsl:call-template>
            <xsl:element name="IpSystem">
                <xsl:call-template name="prepareVpnInterface">
                    <xsl:with-param name="ipAccessHostEtRef" select="$ipAccessHostEtRef"/>
                    <xsl:with-param name="networkPrefixLength" select="$networkPrefixLength"/>
                </xsl:call-template>
                <xsl:call-template name="prepareIpSec">
                    <xsl:with-param name="ipAddressOaMInner" select="$ipAddressOaMInner"/>
                    <xsl:with-param name="peerIpAddress" select="$peerOaMIpAddress"/>
                </xsl:call-template>
            </xsl:element>

        </xsl:element>
    </xsl:template>
    <xsl:template name="prepareAclEntries">
        <xsl:param name="aclPriority"/>
        <xsl:param name="aclAction"/>
        <xsl:param name="icmpType"/>
        <xsl:param name="localIpAddress"/>
        <xsl:param name="localIpAddressMask"/>
        <xsl:param name="remoteIpAddress"/>
        <xsl:param name="remoteIpAddressMask"/>
        <xsl:param name="protocol"/>


        <xsl:element name="AclEntries">
            <xsl:attribute name="aclPriority"><xsl:value-of select="$aclPriority"/></xsl:attribute>
            <xsl:attribute name="aclAction"><xsl:value-of select="$aclAction"/></xsl:attribute>
            <xsl:attribute name="icmpType"><xsl:value-of select="$icmpType"/></xsl:attribute>
            <xsl:attribute name="localIpAddress"><xsl:value-of select="$localIpAddress"/></xsl:attribute>
            <xsl:attribute name="localIpAddressMask"><xsl:value-of select="$localIpAddressMask"/></xsl:attribute>
            <xsl:attribute name="localPort">0</xsl:attribute>
            <xsl:attribute name="localPortFiltering">FALSE</xsl:attribute>
            <xsl:attribute name="protocol"><xsl:value-of select="$protocol"/></xsl:attribute>
            <xsl:attribute name="remoteIpAddress"><xsl:value-of select="$remoteIpAddress"/></xsl:attribute>
            <xsl:attribute name="remoteIpAddressMask"><xsl:value-of select="$remoteIpAddressMask"/></xsl:attribute>
            <xsl:attribute name="remotePort">0</xsl:attribute>
            <xsl:attribute name="remotePortFiltering">FALSE</xsl:attribute>
        </xsl:element>
    </xsl:template>
    <xsl:template name="prepareIp">
        <xsl:param name="ipAddressOaMInner"/>
        <xsl:element name="Ip">
            <xsl:attribute name="dnsServer1"><xsl:value-of select="dnsServer1"/> </xsl:attribute>
            <xsl:attribute name="dnsServer2"><xsl:value-of select="dnsServer2"/> </xsl:attribute>

            <xsl:element name="IpHostLink">
                <xsl:attribute name="ipAddress"><xsl:value-of select="$ipAddressOaMInner"/></xsl:attribute>
                <xsl:attribute name="ipHostLinkId">1</xsl:attribute>
                <xsl:attribute name="ipInterfaceMoRef">VPN-2</xsl:attribute>
                <xsl:attribute name="userLabel">OAM IP address</xsl:attribute>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template name="prepareIpSec">
        <xsl:param name="ipAddressOaMInner"/>
        <xsl:param name="peerIpAddress"/>
        <xsl:element name="IpSec">
            <xsl:attribute name="featureState">ACTIVATED</xsl:attribute>
            <xsl:element name="IkePeer">
                <xsl:attribute name="ikePeerId">2</xsl:attribute>
                <xsl:attribute name="peerIdentityIdFqdn"><xsl:value-of select="peerIdentityIdFqdn"/></xsl:attribute>
                <xsl:attribute name="peerIdentityIdType"><xsl:value-of select="peerIdentityIdType"/></xsl:attribute>
                <xsl:attribute name="peerIpAddress"><xsl:value-of select="$peerIpAddress"/></xsl:attribute>
                 <xsl:for-each select="ikePeerAllowedTransforms">
                     <xsl:for-each select="ikePeerAllowedTransform">
                        <xsl:element name="AllowedTransforms">
                            <xsl:attribute name="diffieHellmanGroup"><xsl:value-of select="diffieHellmanGroup"/></xsl:attribute>
                            <xsl:attribute name="encryptionAlgorithm"><xsl:value-of select="encryptionAlgorithm"/></xsl:attribute>
                            <xsl:attribute name="integrityAlgorithm"><xsl:value-of select="integrityAlgorithm"/></xsl:attribute>
                            <xsl:attribute name="pseudoRandomFunction"><xsl:value-of select="pseudoRandomFunction"/></xsl:attribute>
                        </xsl:element>
                    </xsl:for-each>
                </xsl:for-each>
            </xsl:element>
            <xsl:element name="IpSecTunnel">
                <xsl:attribute name="ikePeerRef">2</xsl:attribute>
                <xsl:attribute name="ipSecTunnelId">2</xsl:attribute>
                <xsl:attribute name="priority">1</xsl:attribute>
                <xsl:attribute name="tsLocalIpAddress"><xsl:value-of select="$ipAddressOaMInner"/></xsl:attribute>
                <xsl:attribute name="tsLocalIpAddressMask"><xsl:value-of select="tsLocalIpAddressMask"/></xsl:attribute>
                <xsl:attribute name="vpnInterfaceRef">2</xsl:attribute>
                <xsl:for-each select="tsRemoteIpAddressRanges">
                    <xsl:element name="TsRemoteIpAddressRanges">
                        <xsl:attribute name="ipAddress"><xsl:value-of select="ipAddress"/></xsl:attribute>
                        <xsl:attribute name="mask"><xsl:value-of select="mask"/></xsl:attribute>
                    </xsl:element>
                </xsl:for-each>
                <xsl:for-each select="ipSecTunnelAllowedTransforms">
                    <xsl:for-each select="ipSecTunnelAllowedTransform">
                        <xsl:element name="AllowedTransforms">
                            <xsl:attribute name="encryptionAlgorithm"><xsl:value-of select="encryptionAlgorithm"/></xsl:attribute>
                            <xsl:attribute name="integrityAlgorithm"><xsl:value-of select="integrityAlgorithm"/></xsl:attribute>
                        </xsl:element>
                    </xsl:for-each>
                </xsl:for-each>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template name="prepareIpInterface">
        <xsl:param name="networkPrefixLength"/>
        <xsl:element name="IpInterface">
            <xsl:attribute name="ipInterfaceId">3</xsl:attribute>
            <xsl:attribute name="ipInterfaceSlot">DU-1</xsl:attribute>
            <xsl:attribute name="defaultRouter0"><xsl:value-of select="defaultrouter0"/></xsl:attribute>
            <xsl:attribute name="networkPrefixLength"><xsl:value-of select="$networkPrefixLength"/></xsl:attribute>
            <xsl:attribute name="vid"><xsl:value-of select="vid"/></xsl:attribute>
            <xsl:attribute name="accessControlListRef">3</xsl:attribute>
        </xsl:element>
    </xsl:template>
    <xsl:template name="prepareVpnInterface">
        <xsl:param name="networkPrefixLength"/>
        <xsl:param name="ipAccessHostEtRef"/>
        <xsl:element name="VpnInterface">
            <xsl:attribute name="vpnInterfaceId">2</xsl:attribute>
            <xsl:attribute name="networkPrefixLength"><xsl:value-of select="$networkPrefixLength"/></xsl:attribute>
            <xsl:attribute name="ipAccessHostEtRef"><xsl:value-of select="$ipAccessHostEtRef"/></xsl:attribute>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
