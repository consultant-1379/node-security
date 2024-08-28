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

class CiphersSshProtocolManagerSpec extends AbstractCiphersProtocolManagerSpec {
    @ObjectUnderTest
    private CiphersSshProtocolManager objectUnderTest

    @ImplementationInstance
    NscsCapabilityModelService capabilityModel = [
        getCipherMoAttributes : { final NormalizableNodeReference normNodeRef ->
            Map<String, Map<String, String>> tmp = [(CiphersConstants.PROTOCOL_TYPE_SSH):[(NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SUPPORTED_KEY_EXCHANGE):"supported-key",
                    (NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SUPPORTED_CIPHER):"supported",
                    (NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SUPPORTED_MAC):"supported-mac",
                    (NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SELECTED_KEY_EXCHANGE):"selected-key",
                    (NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SELECTED_CIPHER):"selected",
                    (NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SELECTED_MAC):"selected-mac"]]
            return tmp
        }
    ] as NscsCapabilityModelService

    @ImplementationInstance
    NscsNodeUtility nscsNodeUtility = [
        getSingleInstanceMoFdn : { String mirrorRootFdn, String moName, String moNameSpace, Map<String, Object> attributes, String... requestedAttrs ->
            attributes.put(requestedAttrs[0], "xxx")
            attributes.put(requestedAttrs[1], "yyy")
            attributes.put(requestedAttrs[2], "zzz")
            attributes.put(requestedAttrs[3], "xxx")
            attributes.put(requestedAttrs[4], "yyy")
            attributes.put(requestedAttrs[5], "")
            return "fdn=..."
        }
    ] as NscsNodeUtility

    protected List<List<String>> getExpectedHeader() {
        return [
            ["Node Name", "Supported Ciphers", "Enabled Ciphers", "Error Details"]
        ]
    }
    protected List<List<String>> getExpectedOk() {
        return [
            ["NetworkElement=LTE01dg2ERBS0001", "KEY EXCHANGE ALGORITHMS", "KEY EXCHANGE ALGORITHMS", "NA"],
            ["", "xxx", "xxx", ""],
            ["", "ENCRYPTION ALGORITHMS", "ENCRYPTION ALGORITHMS", ""],
            ["", "yyy", "yyy", ""],
            ["", "MAC ALGORITHMS", "MAC ALGORITHMS", ""],
            ["", "zzz", "", ""]
        ]
    }
    protected List<List<String>> getExpectedFail() {
        return [
            ["NetworkElement=LTE01dg2ERBS0001", "NA", "NA", "The node specified is not synchronized"]
        ]
    }
    protected CiphersProtocolManager getObjectUnderTest() {
        return objectUnderTest
    }
}
