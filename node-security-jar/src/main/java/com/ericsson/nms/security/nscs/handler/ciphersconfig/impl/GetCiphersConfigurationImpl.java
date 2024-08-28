/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.security.nscs.handler.ciphersconfig.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelConstants;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CiphersConstants;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.oss.services.security.nscs.command.util.CiphersCommandHelper;

/**
 * This class provides the implementation to get the values of ciphers attributes for both TLS and SSH protocols in case of all supported platforms
 * types.
 *
 * @author xchowja
 */
public class GetCiphersConfigurationImpl {

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    @Inject
    private Logger logger;

    @Inject
    private NscsCapabilityModelService capabilityModel;

    /**
     * Get the Map<String, Map<String, List<String>>> object for SSH protocol, that will have the attributes required for supported and enabled
     * ciphers and its respective values from the DPS for both COM/ECIM and CPP platform type.
     *
     * @param normNode
     *            i.e NormalizableNodeReference a reference to MeContext or similar Managed Object.
     * @param protocolType
     *            i.e either SSH/SFTP or SSL/HTTPS/TLS
     * @return Map<String, Map<String, List<String>>> having the attributes and its respective values for supported and enabled ciphers for SSH
     *         protocol.
     */
    public Map<String, Map<String, List<String>>> getSshCiphers(final NormalizableNodeReference normNode, final String protocolType) {
        logger.info("Start of GetCiphersConfigurationImpl::getSshCiphers method: normNodeFdn[{}],protocolType[{}]", normNode.getFdn(), protocolType);
        final String mirrorRootFdn = normNode.getFdn();
        final Map<String, String> cipherMoNames = CiphersCommandHelper.getCipherMoNames();
        final Map<String, Map<String, String>> cipherMoAttributes = capabilityModel.getCipherMoAttributes(normNode);
        final Map<String, String> moAttributes = cipherMoAttributes.get(protocolType);

        final Map<String, Map<String, List<String>>> ciphersMap = prepareSshCiphersMap(mirrorRootFdn, moAttributes, cipherMoNames.get(protocolType));

        logger.debug("ciphersMap in GetCiphersConfigurationImpl::getSshCiphers method: [{}]", ciphersMap);
        logger.info("End of GetCiphersConfigurationImpl::getSshCiphers method ");

        return ciphersMap;
    }

    private Map<String, Map<String, List<String>>> prepareSshCiphersMap(final String mirrorRootFdn, final Map<String, String> sshAttributes,
            final String cipherMoName) {
        final String requestedAttrs[] = {
            sshAttributes.get(NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SUPPORTED_KEY_EXCHANGE),
            sshAttributes.get(NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SUPPORTED_CIPHER),
            sshAttributes.get(NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SUPPORTED_MAC),
            sshAttributes.get(NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SELECTED_KEY_EXCHANGE),
            sshAttributes.get(NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SELECTED_CIPHER),
            sshAttributes.get(NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SELECTED_MAC) };
        final Map<String, Object> attributes = new HashMap<String, Object>();
        final String sshFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, cipherMoName, " ", attributes, requestedAttrs);
        Map<String, Map<String, List<String>>> ciphersMap = null;
        if (sshFdn != null) {
            ciphersMap = new HashMap<String, Map<String, List<String>>>();
            ciphersMap.put(CiphersConstants.SUPPORTED_CIPHERS, prepareSupportedSshCiphersMap(sshAttributes, attributes));
            ciphersMap.put(CiphersConstants.ENABLED_CIPHERS, prepareEnabledSshCiphersMap(sshAttributes, attributes));
        } else {
            logger.error("Get Ssh Fdn failed for mirrorRootFdn[{}] Mo[{}] to get SSH Ciphers", mirrorRootFdn, cipherMoName);
        }
        return ciphersMap;
    }

    private Map<String, List<String>> prepareSupportedSshCiphersMap(final Map<String, String> sshAttributes, final Map<String, Object> attributes) {
        final Map<String, List<String>> ciphersMap = new HashMap<String, List<String>>();
        ciphersMap.put(CiphersConstants.KEY_EXCHANGE_ALGORITHMS, getValueForCipher(
                attributes.get(sshAttributes.get(NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SUPPORTED_KEY_EXCHANGE))));
        ciphersMap.put(CiphersConstants.ENCRYPTION_ALGORITHMS, getValueForCipher(
                attributes.get(sshAttributes.get(NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SUPPORTED_CIPHER))));
        ciphersMap.put(CiphersConstants.MAC_ALGORITHMS, getValueForCipher(
                attributes.get(sshAttributes.get(NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SUPPORTED_MAC))));

        return ciphersMap;
    }

    private Map<String, List<String>> prepareEnabledSshCiphersMap(final Map<String, String> sshAttributes, final Map<String, Object> attributes) {
        final Map<String, List<String>> ciphersMap = new HashMap<String, List<String>>();
        ciphersMap.put(CiphersConstants.SELECTED_KEY_EXCHANGE_ALGORITHMS, getValueForCipher(
                attributes.get(sshAttributes.get(NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SELECTED_KEY_EXCHANGE))));
        ciphersMap.put(CiphersConstants.SELECTED_ENCRYPTION_ALGORITHMS, getValueForCipher(
                attributes.get(sshAttributes.get(NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SELECTED_CIPHER))));
        ciphersMap.put(CiphersConstants.SELECTED_MAC_ALGORITHMS, getValueForCipher(
                attributes.get(sshAttributes.get(NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SELECTED_MAC))));

        return ciphersMap;
    }

    private List<String> getValueForCipher(final Object obj) {
        if (obj != null) {
            return nscsNodeUtility.convertStringToList(obj.toString());
        }
        return null;
    }
}
