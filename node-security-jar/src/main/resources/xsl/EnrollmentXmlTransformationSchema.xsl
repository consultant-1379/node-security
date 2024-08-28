<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8"
		indent="yes" />
	<xsl:template match="/Nodes">
		<xsl:element name="EnrollmentDetails" xmlns:xsi="http://www.w3.org/2001/XMLSchema">

			<xsl:element name="nodeEnrollmentDetails">
				<xsl:element name="Nodes">
					<xsl:for-each select="Node">
						<xsl:element name="Node">
							<xsl:if test="NodeFdn"><xsl:element name="NodeFdn"><xsl:value-of select="NodeFdn" /></xsl:element></xsl:if>
							<xsl:if test="EntityProfileName"><xsl:element name="EntityProfileName"><xsl:value-of select="EntityProfileName" /></xsl:element></xsl:if>
							<xsl:if test="SubjectAltName"><xsl:element name="SubjectAltName"><xsl:value-of select="SubjectAltName" /></xsl:element></xsl:if>
							<xsl:if test="SubjectAltNameType"><xsl:element name="SubjectAltNameType"><xsl:value-of select="SubjectAltNameType" /></xsl:element></xsl:if>
							<xsl:if test="EnrollmentMode"><xsl:element name="EnrollmentMode"><xsl:value-of select="EnrollmentMode" /></xsl:element></xsl:if>
							<xsl:if test="KeySize"><xsl:element name="KeySize"><xsl:value-of select="KeySize" /></xsl:element></xsl:if>
							<xsl:if test="CommonName"><xsl:element name="CommonName"><xsl:value-of select="CommonName" /></xsl:element></xsl:if>
						</xsl:element>
					</xsl:for-each>
				</xsl:element>
			</xsl:element>

		</xsl:element>
	</xsl:template>
</xsl:stylesheet>