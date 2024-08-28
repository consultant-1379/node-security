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

package com.ericsson.nms.security.nscs.handler.ciphersconfig.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.NodeCiphers;

/**
 * This class provides the common utility methods required to implement the functionality of ciphers configuration i.e set and get ciphers.
 *
 * @author xchowja
 */
public class CiphersConfigurationUtil {

    /**
     * This method will get the valid protocol types for ciphers configuration commands 'set ciphers' and 'get ciphers' from the capability model and
     * returns those as a list of String objects
     *
     * @return List<String> list of valid protocol types for set ciphers command
     * @throws NscsCapabilityModelException
     *             is thrown when error occurs while getting valid protocol types from NSCS capability model service
     */
    public List<String> getValidProtocolTypesForCiphersConfiguration() {
        final List<String> validProtocolTypesForSetCiphers = new ArrayList<>();
        validProtocolTypesForSetCiphers.add(CiphersConstants.PROTOCOL_TYPE_SSH);
        validProtocolTypesForSetCiphers.add(CiphersConstants.PROTOCOL_TYPE_TLS);
        return validProtocolTypesForSetCiphers;
    }

    /**
     * This method will get the valid command arguments to set TLS ciphers from the capability model and returns those as a list of String objects
     *
     * @return List<String> list of valid command arguments to set TLS ciphers
     * @throws NscsCapabilityModelException
     *             is thrown when error occurs while getting valid arguments from NSCS capability model service
     */
    public List<String> getValidArgsToSetTlsCiphers() {
        return Arrays.asList("cipherfilter");
    }

    /**
     * This method will get the valid command arguments to set SSH ciphers from the capability model and returns those as a list of String objects
     *
     * @return List<String> list of valid command arguments to set SSH ciphers
     * @throws NscsCapabilityModelException
     *             is thrown when error occurs while getting valid arguments from NSCS capability model service
     */
    public List<String> getValidArgsToSetSshCiphers() {
        final List<String> validProtocolTypesForSetCiphers = new ArrayList<>();
        validProtocolTypesForSetCiphers.add(CiphersConstants.ENCRYPT_ALGOS);
        validProtocolTypesForSetCiphers.add(CiphersConstants.KEX);
        validProtocolTypesForSetCiphers.add(CiphersConstants.MACS);
        return validProtocolTypesForSetCiphers;
    }

    /**
     * This method will prepare the response for list of SSH algorithms(keyExchangeAlgorithm,encryptionAlgorithm,macAlgorithm) with their respective
     * headers and add all those ciphers in a single list.
     *
     * @return List<String> list having all the SSH algorithms with their respective headers.
     */
    public List<String> prepareSshCiphersResponse(final List<String> keyExchangeAlgorithms, final List<String> encryptionAlgorithms,
            final List<String> macAlgorithms) {
        final List<String> ciphersList = new ArrayList<>();
        if (keyExchangeAlgorithms != null) {
            ciphersList.add(CiphersConstants.KEY_EXCHANGE_ALGORITHMS);
            ciphersList.addAll(keyExchangeAlgorithms);
        }
        if (encryptionAlgorithms != null) {
            ciphersList.add(CiphersConstants.ENCRYPTION_ALGORITHMS);
            ciphersList.addAll(encryptionAlgorithms);
        }
        if (macAlgorithms != null) {
            ciphersList.add(CiphersConstants.MAC_ALGORITHMS);
            ciphersList.addAll(macAlgorithms);
        }
        return ciphersList;
    }

    /**
     * This method process the nodeCiphers object and returns the input protocol type.
     *
     * @param nodeCiphers
     *            object of type NodeCiphers
     * @return the list of protocol type
     */
    public static List<String> getInputProcotolTypes(final NodeCiphers nodeCiphers) {
        final List<String> protocolTypes = new ArrayList<>();
        if (nodeCiphers.getSshProtocol() != null) {
            protocolTypes.add(CiphersConstants.PROTOCOL_TYPE_SSH);
        }
        if (nodeCiphers.getTlsProtocol() != null) {
            protocolTypes.add(CiphersConstants.PROTOCOL_TYPE_TLS);
        }
        return protocolTypes;
    }
}
