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

package com.ericsson.nms.security.nscs.handler.ciphersconfig.utility

import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelConstants
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility

class CiphersTlsProtocolManagerSpec extends AbstractCiphersProtocolManagerSpec {
    @ObjectUnderTest
    private CiphersTlsProtocolManager objectUnderTest

    def supportedCapabilities = "{name=xxx, protocolVersion=pvx1 pvx1.2}, " +
    "{protocolVersion=pvy, name=yyy}, " +
    "{name=zzz}, " +
    "{encryption=AESGCM, name=ECDHE-RSA-AES256-GCM-SHA384, protocolVersion=TLSv1.2, export=, keyExchange=kECDH, mac=AEAD, authentication=aRSA}"
    def enabledCapabilities = "{name=yyy}" +
    "{encryption=AESGCM, name=ECDHE-RSA-AES256-GCM-SHA384, protocolVersion=TLSv1.2, export=, keyExchange=kECDH, mac=AEAD, authentication=aRSA}"

    @ImplementationInstance
    NscsCapabilityModelService capabilityModel = [
        getCipherMoAttributes : { final NormalizableNodeReference normNodeRef ->
            Map<String, Map<String, String>> tlsAttributes = [(CiphersConstants.PROTOCOL_TYPE_TLS):[(NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SUPPORTED_CIPHER):"supported",
                    (NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_ENABLED_CIPHER):"enabled"]]
            return tlsAttributes
        }
    ] as NscsCapabilityModelService

    @ImplementationInstance
    NscsNodeUtility nscsNodeUtility = [
        getSingleInstanceMoFdn : { String mirrorRootFdn, String moName, String moNameSpace, Map<String, Object> attributes, String... requestedAttrs ->
            attributes.put(requestedAttrs[0], supportedCapabilities)
            attributes.put(requestedAttrs[1], enabledCapabilities)
            return "fdn=..."
        }
    ] as NscsNodeUtility

    protected List<List<String>> getExpectedHeader() {
        return [
            ["Node Name", "Supported Ciphers", "Enabled Ciphers", "Error Details", "Protocol Version"]
        ]
    }
    protected List<List<String>> getExpectedOk() {
        return [
            ["NetworkElement=LTE01dg2ERBS0001", "yyy", "yyy", "NA", "pvy"],
            ["", "ECDHE-RSA-AES256-GCM-SHA384", "ECDHE-RSA-AES256-GCM-SHA384", "", "TLSv1.2"],
            ["", "xxx", "", "", "pvx1 pvx1.2"],
            ["", "zzz", "", "", ""]
        ]
    }
    protected List<List<String>> getExpectedFail() {
        return [
            ["NetworkElement=LTE01dg2ERBS0001", "NA", "NA", "The node specified is not synchronized", "NA"]
        ]
    }
    protected CiphersProtocolManager getObjectUnderTest() {
        return objectUnderTest
    }
}
