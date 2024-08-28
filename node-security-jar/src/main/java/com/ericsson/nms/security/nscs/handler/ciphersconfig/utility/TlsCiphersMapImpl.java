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

package com.ericsson.nms.security.nscs.handler.ciphersconfig.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelConstants;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.oss.services.security.nscs.command.util.CiphersCommandHelper;

public class TlsCiphersMapImpl {
    private static final Logger logger = LoggerFactory.getLogger(TlsCiphersMapImpl.class);

    private static final Pattern RECORD_CAPABILITY_MODEL_PATTERN = Pattern.compile("\\{(.*?)\\}");
    private static final Pattern NAME_CAPABILITY_MODEL_PATTERN = Pattern.compile("(?:| )name=(.*?)(?:,|$)");
    private static final Pattern PROTOCOL_VERSION_CAPABILITY_MODEL_PATTERN = Pattern.compile("(?:| )protocolVersion=(.*?)(?:,|$)");

    @Inject
    private NscsNodeUtility nscsNodeUtility;
    @Inject
    private NscsCapabilityModelService capabilityModel;

    /**
     * Get the Map<String, List<String>> object for TLS protocol, that will have the supported and enabled ciphers and its respective values from the
     * DPS for both COM/ECIM and CPP platform type.
     *
     * @param normNode
     *            i.e NormalizableNodeReference a reference to MeContext or similar Managed Object.
     * @param protocolType
     *            i.e either SSH/SFTP or SSL/HTTPS/TLS
     * @return Map<String, List<String>> having supported and enabled ciphers and its respective values for TLS protocol
     */
    public Map<String, List<CipherTlsProtocolInfo>> getCiphers(final NormalizableNodeReference normNode, final String protocolType) {
        logger.info("Start of TlsCiphersMapImpl::getCiphers method: normNodeFdn[{}],protocolType[{}]", normNode.getFdn(), protocolType);

        final String mirrorRootFdn = normNode.getFdn();
        final Map<String, String> cipherMoNames = CiphersCommandHelper.getCipherMoNames();
        final Map<String, Map<String, String>> cipherMoAttributes = capabilityModel.getCipherMoAttributes(normNode);
        final Map<String, String> moAttributes = cipherMoAttributes.get(protocolType);

        final Map<String, List<CipherTlsProtocolInfo>> ciphersMap = prepareCiphersMap(mirrorRootFdn, moAttributes, cipherMoNames.get(protocolType));

        logger.debug("ciphersMap in TlsCiphersMapImpl::getCiphers method: [{}]", ciphersMap);
        logger.info("End of TlsCiphersMapImpl::getCiphers method ");

        return ciphersMap;
    }

    private List<CipherTlsProtocolInfo> parseCiphers(final String ciphers, final boolean isEnabled) {
        final List<CipherTlsProtocolInfo> cipherSuites = new ArrayList<>();
        if (ciphers != null && !ciphers.isEmpty()) {
            final Matcher matcher = RECORD_CAPABILITY_MODEL_PATTERN.matcher(ciphers);
            while (matcher.find()) {
                final String ciphersCapabilityModel = matcher.group(1);
                final String cipherSuite = parseCiphersRecord(ciphersCapabilityModel, NAME_CAPABILITY_MODEL_PATTERN);
                final String protocolVersion = parseCiphersRecord(ciphersCapabilityModel, PROTOCOL_VERSION_CAPABILITY_MODEL_PATTERN);
                if (cipherSuite != null) {
                    cipherSuites.add(new CipherTlsProtocolInfo(cipherSuite, protocolVersion != null ? protocolVersion : "", isEnabled));
                }
            }
        }
        return cipherSuites;
    }

    private String parseCiphersRecord(final String record, final Pattern pattern) {
        final Matcher matcher = pattern.matcher(record);
        return matcher.find() ? matcher.group(1) : null;
    }

    private Map<String, List<CipherTlsProtocolInfo>> prepareCiphersMap(final String mirrorRootFdn, final Map<String, String> tlsAttributes,
            final String cipherMoName) {
        final Map<String, Object> attributes = new HashMap<>();
        final Map<String, List<CipherTlsProtocolInfo>> ciphersMap = new HashMap<>();
        final String[] requestedAttrs = { tlsAttributes.get(NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SUPPORTED_CIPHER),
            tlsAttributes.get(NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_ENABLED_CIPHER) };
        final String tlsFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, cipherMoName, " ", attributes, requestedAttrs);
        if (tlsFdn != null) {
            final String supportedCiphersKey = tlsAttributes.get(NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_SUPPORTED_CIPHER);
            final String enabledCiphersKey = tlsAttributes.get(NscsCapabilityModelConstants.NSCS_CAPABILITY_CIPHERS_CONFIG_PARAMS_ENABLED_CIPHER);

            if (attributes.get(supportedCiphersKey) != null) {
                final List<CipherTlsProtocolInfo> supportedCiphersFromNode = parseCiphers(attributes.get(supportedCiphersKey).toString(), false);
                logger.info("supportedCiphersFromNode in prepareTlsCiphersMap method: [{}]", supportedCiphersFromNode.size());

                final List<CipherTlsProtocolInfo> ciphersFromNode = parseCiphers(attributes.get(enabledCiphersKey).toString(), true);
                logger.info("enabledCiphersFromNode in prepareTlsCiphersMap method: [{}]", ciphersFromNode.size());
                for (final Iterator<CipherTlsProtocolInfo> iter = supportedCiphersFromNode.iterator(); iter.hasNext();) {
                    final CipherTlsProtocolInfo tempSupportedCipher = iter.next();
                    final int ciphersIndex = ciphersFromNode.indexOf(tempSupportedCipher);
                    if (ciphersIndex != -1) {
                        final CipherTlsProtocolInfo ciphers = ciphersFromNode.get(ciphersIndex);
                        ciphers.setProtocolVersion(tempSupportedCipher.getProtocolVersion());
                    } else {
                        ciphersFromNode.add(tempSupportedCipher);
                    }
                }
                ciphersMap.put(CiphersConstants.SUPPORTED_CIPHERS, ciphersFromNode);
            } else {
                ciphersMap.put(CiphersConstants.SUPPORTED_CIPHERS, null);
            }
        } else {
            logger.error("Get Tls Fdn failed for mirrorRootFdn[{}] Mo[{}] to get TLS Ciphers", mirrorRootFdn, cipherMoName);
        }
        return ciphersMap;
    }
}
