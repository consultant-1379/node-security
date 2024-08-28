<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<xsl:if test="Nodes/Node/DisableOMConfiguration">
            <xsl:apply-templates select="Nodes/Node/DisableOMConfiguration"/>
        </xsl:if>
	</xsl:template>
	<xsl:template match="Nodes/Node/DisableOMConfiguration">
		<xsl:comment>
            ProductNumber  = 'CXC 179 9121/4'       R-State  = 'R2C'
			DocumentNumber = '6/006 91-HRB 105 500'
			Node version: L14B
			File    : IpForOamSetting.xml
			Purpose : Template for the IpForOamSetting file for OaM,
			for an OAM IP configuration of the following type:
			o No IPsec for OAM          
			o OAM has a separate IpInterface/VLAN
        </xsl:comment>
		<xsl:variable name="ipAddressOaMOuter" select="./ipAddressOaMOuter" /> 
		<xsl:variable name="dnsServer1" select="./dnsServer1" />
		<xsl:variable name="dnsServer2" select="./dnsServer2" />
		<xsl:variable name="defaultRouter0" select="./defaultRouter0" />
		<xsl:variable name="networkPrefixLength" select="./networkPrefixLength" />
		<xsl:variable name="remoteIpAddress" select="./remoteIpAddress" />
		<xsl:variable name="remoteIpAddressMask" select="./remoteIpAddressMask" />
		<xsl:variable name="vid" select="./vid" />
		<xsl:element name="SiteBasic">
			<xsl:attribute name="xsi:noNamespaceSchemaLocation">SiteBasic.xsd</xsl:attribute>
			<xsl:call-template name="createFormat">                    	
            	<xsl:with-param name="format" select='"T"' />
			</xsl:call-template>
			<xsl:call-template name="createIp">                    	
            	<xsl:with-param name="dnsServer1" select="$dnsServer1" />
            	<xsl:with-param name="dnsServer2" select="$dnsServer2" />
            	<xsl:with-param name="ipAddress" select="$ipAddressOaMOuter" />
            	<xsl:with-param name="ipHostLinkId" select='"1"' />
            	<xsl:with-param name="ipInterfaceMoRef" select='"DU-1-IP-3"' />
            	<xsl:with-param name="userLabel" select='"OAM IP address"' />
			</xsl:call-template>
			<xsl:call-template name="createIpInterface">
            	<xsl:with-param name="ipInterfaceId" select='"3"' />
            	<xsl:with-param name="ipInterfaceSlot" select='"DU-1"' />
            	<xsl:with-param name="defaultRouter0" select="$defaultRouter0" />
            	<xsl:with-param name="networkPrefixLength" select="$networkPrefixLength" />
            	<xsl:with-param name="vid" select="$vid" />
            	<!-- <xsl:with-param name="accessControlListRef" select="' '" /> -->
            </xsl:call-template>
            <!-- AccessControlList is not required in the sitebasic file for ipsec deactivation -->
<!--             <xsl:call-template name="createIpSystem">
            	<xsl:with-param name="ipAddressOaMOuter" select="$ipAddressOaMOuter" />
            	<xsl:with-param name="remoteIpAddress" select="$remoteIpAddress" />
            	<xsl:with-param name="remoteIpAddressMask" select="$remoteIpAddressMask" />
            </xsl:call-template> -->
		</xsl:element>
	</xsl:template>
	<!-- AccessControlList is not required in the sitebasic file for ipsec deactivation -->
<!-- 	<xsl:template name="createAclEntries">
        <xsl:param name="aclPriority"/>
        <xsl:param name="aclAction"/>
        <xsl:param name="icmpType"/>
        <xsl:param name="localIpAddress"/>
        <xsl:param name="localIpAddressMask"/>
        <xsl:param name="localPort"/>
        <xsl:param name="localPortFiltering"/>
        <xsl:param name="protocol"/>
        <xsl:param name="remoteIpAddress"/>
        <xsl:param name="remoteIpAddressMask"/>
        <xsl:param name="remotePort"/>
        <xsl:param name="remotePortFiltering"/>
        <xsl:element name="AclEntries">
            <xsl:attribute name="aclPriority"><xsl:value-of select="$aclPriority"/></xsl:attribute>
            <xsl:attribute name="aclAction"><xsl:value-of select="$aclAction"/></xsl:attribute>
            <xsl:attribute name="icmpType"><xsl:value-of select="$icmpType"/></xsl:attribute>
            <xsl:attribute name="localIpAddress"><xsl:value-of select="$localIpAddress"/></xsl:attribute>
            <xsl:attribute name="localIpAddressMask"><xsl:value-of select="$localIpAddressMask"/></xsl:attribute>
            <xsl:attribute name="localPort"><xsl:value-of select="$localPort"/></xsl:attribute>
            <xsl:attribute name="localPortFiltering"><xsl:value-of select="$localPortFiltering"/></xsl:attribute>
            <xsl:attribute name="protocol"><xsl:value-of select="$protocol"/></xsl:attribute>
            <xsl:attribute name="remoteIpAddress"><xsl:value-of select="$remoteIpAddress"/></xsl:attribute>
            <xsl:attribute name="remoteIpAddressMask"><xsl:value-of select="$remoteIpAddressMask"/></xsl:attribute>
            <xsl:attribute name="remotePort"><xsl:value-of select="$remotePort"/></xsl:attribute>
            <xsl:attribute name="remotePortFiltering"><xsl:value-of select="$remotePortFiltering"/></xsl:attribute>
        </xsl:element>
    </xsl:template> -->
    <xsl:template name="createFormat">
    	<xsl:param name="format"/>
    	<xsl:element name="Format">
			<xsl:attribute name="revision"><xsl:value-of select="$format" /></xsl:attribute>
		</xsl:element>
    </xsl:template>
    <xsl:template name="createIp">
    	<xsl:param name="dnsServer1"/>
    	<xsl:param name="dnsServer2"/>
    	<xsl:param name="ipAddress"/>
    	<xsl:param name="ipHostLinkId"/>
    	<xsl:param name="ipInterfaceMoRef"/>
    	<xsl:param name="userLabel"/>    	
    	<xsl:element name="Ip">
			<xsl:attribute name="dnsServer1"><xsl:value-of select="$dnsServer1" /></xsl:attribute>
			<xsl:attribute name="dnsServer2"><xsl:value-of select="$dnsServer2" /></xsl:attribute>				
			<xsl:element name="IpHostLink">
				<xsl:attribute name="ipAddress"><xsl:value-of select="$ipAddress" /></xsl:attribute>
				<xsl:attribute name="ipHostLinkId"><xsl:value-of select="$ipHostLinkId" /></xsl:attribute>
				<xsl:attribute name="ipInterfaceMoRef"><xsl:value-of select="$ipInterfaceMoRef" /></xsl:attribute>
				<xsl:attribute name="userLabel"><xsl:value-of select="$userLabel" /></xsl:attribute>
			</xsl:element>		
		</xsl:element>
	</xsl:template>	
	<xsl:template name="createIpInterface">  
		<xsl:param name="ipInterfaceId"/>
		<xsl:param name="ipInterfaceSlot"/>
		<xsl:param name="defaultRouter0"/>
		<xsl:param name="networkPrefixLength"/>
		<xsl:param name="vid"/>
		<xsl:param name="accessControlListRef"/>
		<xsl:element name="IpInterface">
			<xsl:attribute name="ipInterfaceId"><xsl:value-of select="$ipInterfaceId" /></xsl:attribute>
			<xsl:attribute name="ipInterfaceSlot"><xsl:value-of select="$ipInterfaceSlot" /></xsl:attribute>
			<xsl:attribute name="defaultRouter0"><xsl:value-of select="$defaultRouter0" /></xsl:attribute>
			<xsl:attribute name="networkPrefixLength"><xsl:value-of select="$networkPrefixLength" /></xsl:attribute>
			<xsl:attribute name="vid"><xsl:value-of select="$vid" /></xsl:attribute>
			<xsl:attribute name="accessControlListRef"><xsl:value-of select="$accessControlListRef" /></xsl:attribute>
		</xsl:element>
	</xsl:template>
	<!-- AccessControlList is not required in the sitebasic file for ipsec deactivation -->
<!-- 	<xsl:template name="createIpSystem">
    	<xsl:param name="ipAddressOaMOuter"/>
    	<xsl:param name="remoteIpAddress"/>
    	<xsl:param name="remoteIpAddressMask"/>
    	<xsl:element name="IpSystem">
				<xsl:element name="AccessControlList">
					<xsl:attribute name="accessControlListId"><xsl:value-of select='"3"' /></xsl:attribute>
					<xsl:call-template name="createAclEntries">
                    	<xsl:with-param name="aclPriority" select='"0"' />
						<xsl:with-param name="aclAction" select='"BYPASS"' />
						<xsl:with-param name="icmpType" select='"256"' />
						<xsl:with-param name="localIpAddress" select="$ipAddressOaMOuter" />
						<xsl:with-param name="localIpAddressMask" select='"32"' />
						<xsl:with-param name="localPort" select='"0"' />
						<xsl:with-param name="localPortFiltering" select='"FALSE"' />
						<xsl:with-param name="protocol" select='"TCP"' />
						<xsl:with-param name="remoteIpAddress" select="$remoteIpAddress" />
						<xsl:with-param name="remoteIpAddressMask" select="$remoteIpAddressMask" />
						<xsl:with-param name="remotePort" select='"0"' />
						<xsl:with-param name="remotePortFiltering" select='"FALSE"' />                    	
                	</xsl:call-template>                	
                	<xsl:call-template name="createAclEntries">
                		<xsl:with-param name="aclPriority" select='"1"' />
						<xsl:with-param name="aclAction" select='"BYPASS"' />
						<xsl:with-param name="icmpType" select='"256"' />						
						<xsl:with-param name="localIpAddress" select="$ipAddressOaMOuter" />
						<xsl:with-param name="localIpAddressMask" select='"32"' />
						<xsl:with-param name="localPort" select='"0"' />
						<xsl:with-param name="localPortFiltering" select='"FALSE"' />
						<xsl:with-param name="protocol" select='"UDP"' />
						<xsl:with-param name="remoteIpAddress" select="$remoteIpAddress" />
						<xsl:with-param name="remoteIpAddressMask" select="$remoteIpAddressMask" />
						<xsl:with-param name="remotePort" select='"0"' />
						<xsl:with-param name="remotePortFiltering" select='"FALSE"' />
					</xsl:call-template>			
					<xsl:call-template name="createAclEntries">
                		<xsl:with-param name="aclPriority" select='"2"' />
						<xsl:with-param name="aclAction" select='"BYPASS"' />
						<xsl:with-param name="icmpType" select='"256"' />						
						<xsl:with-param name="localIpAddress" select="$ipAddressOaMOuter" />
						<xsl:with-param name="localIpAddressMask" select='"32"' />
						<xsl:with-param name="localPort" select='"0"' />
						<xsl:with-param name="localPortFiltering" select='"FALSE"' />
						<xsl:with-param name="protocol" select='"ICMP"' />
						<xsl:with-param name="remoteIpAddress" select="$remoteIpAddress" />
						<xsl:with-param name="remoteIpAddressMask" select="$remoteIpAddressMask" />
						<xsl:with-param name="remotePort" select='"0"' />
						<xsl:with-param name="remotePortFiltering" select='"FALSE"' />
					</xsl:call-template>		
					<xsl:call-template name="createAclEntries">
                		<xsl:with-param name="aclPriority" select='"3"' />
						<xsl:with-param name="aclAction" select='"DROP"' />
						<xsl:with-param name="icmpType" select='"0"' />						
						<xsl:with-param name="localIpAddress" select='"0.0.0.0"' />
						<xsl:with-param name="localIpAddressMask" select='"0"' />
						<xsl:with-param name="localPort" select='"0"' />
						<xsl:with-param name="localPortFiltering" select='"FALSE"' />
						<xsl:with-param name="protocol" select='"ANY"' />
						<xsl:with-param name="remoteIpAddress" select='"0.0.0.0"' />
						<xsl:with-param name="remoteIpAddressMask" select='"0"' />
						<xsl:with-param name="remotePort" select='"0"' />
						<xsl:with-param name="remotePortFiltering" select='"FALSE"' />
					</xsl:call-template>
				</xsl:element>
			</xsl:element>
    </xsl:template>  -->
</xsl:stylesheet>