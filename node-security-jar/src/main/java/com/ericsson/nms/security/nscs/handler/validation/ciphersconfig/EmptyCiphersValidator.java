/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.validation.ciphersconfig;

import java.util.*;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.NodeCiphers;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CiphersConstants;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.Constants;

/**
 * This class defines the methods to validate empty ciphers for SSH and TLS protocol for CPP nodes.
 *
 * @author tcsgoja
 */
public class EmptyCiphersValidator {

    @Inject
    protected NscsLogger nscsLogger;

    /**
     * This method validates empty ciphers for SSH protocol and empty cipher filter for TLS protocol and throws InvalidArgumentValueException for
     * unsupported nodes.
     *
     * @param nodeCiphers
     * @param isEmptyCipherSupported
     */
    public void validateNodeCiphers(final NodeCiphers nodeCiphers, final boolean isEmptyCipherSupported) throws InvalidArgumentValueException {

        if (nodeCiphers.getSshProtocol() != null) {
            validateNodeCiphersForSSH(nodeCiphers, isEmptyCipherSupported);
        }
        if (nodeCiphers.getTlsProtocol() != null) {
            validateNodeCipherFilterForTLS(nodeCiphers, isEmptyCipherSupported);
        }
    }

    private void validateNodeCiphersForSSH(final NodeCiphers nodeCiphers, final boolean isEmptyCipherSupported) throws InvalidArgumentValueException {
        nscsLogger.info("start validateCiphersForSSH...");
        final List<String> emptyCiphers = new ArrayList<String>();
        if (!isEmptyCipherSupported) {
            if (nodeCiphers.getSshProtocol().getEncryptCiphers() != null
                    && removeEmptyValuesFromList(nodeCiphers.getSshProtocol().getEncryptCiphers().getCipher()).isEmpty()) {
                emptyCiphers.add(CiphersConstants.ENCRYPTION_ALGORITHMS);
            }
            if (nodeCiphers.getSshProtocol().getKeyExchangeCiphers() != null
                    && removeEmptyValuesFromList(nodeCiphers.getSshProtocol().getKeyExchangeCiphers().getCipher()).isEmpty()) {
                emptyCiphers.add(CiphersConstants.KEY_EXCHANGE_ALGORITHMS);
            }
            if (nodeCiphers.getSshProtocol().getMacCiphers() != null
                    && removeEmptyValuesFromList(nodeCiphers.getSshProtocol().getMacCiphers().getCipher()).isEmpty()) {
                emptyCiphers.add(CiphersConstants.MAC_ALGORITHMS);
            }
            if (!emptyCiphers.isEmpty()) {
                nscsLogger.error("Empty Algorithm(s) Found in {} ", emptyCiphers);
                throw new InvalidArgumentValueException(NscsErrorCodes.INVALID_INPUT_VALUE, "Empty Algorithm(s) Found in " + emptyCiphers)
                        .setSuggestedSolution(NscsErrorCodes.SPECIFY_VALID_CIPHERS);
            }
        } else {
            if (nodeCiphers.getSshProtocol().getEncryptCiphers() != null) {
                removeEmptyValuesFromList(nodeCiphers.getSshProtocol().getEncryptCiphers().getCipher());
            }
            if (nodeCiphers.getSshProtocol().getKeyExchangeCiphers() != null) {
                removeEmptyValuesFromList(nodeCiphers.getSshProtocol().getKeyExchangeCiphers().getCipher());
            }
            if (nodeCiphers.getSshProtocol().getMacCiphers() != null) {
                removeEmptyValuesFromList(nodeCiphers.getSshProtocol().getMacCiphers().getCipher());
            }
        }
        nscsLogger.info("end validateCiphersForSSH...");
    }

    private List<String> removeEmptyValuesFromList(final List<String> ciphersList) {
        ciphersList.removeAll(Arrays.asList(Constants.EMPTY_STRING));
        return ciphersList;
    }

    private void validateNodeCipherFilterForTLS(final NodeCiphers nodeCiphers, final boolean isEmptyCipherSupported)
            throws InvalidArgumentValueException {
        nscsLogger.info("start validateCipherFilterForTLS()...");
        if (!isEmptyCipherSupported && nodeCiphers.getTlsProtocol().getCipherFilter().isEmpty()) {
            nscsLogger.error("Empty value Found in {} ", CiphersConstants.CIPHER_FILTER);
            throw new InvalidArgumentValueException(NscsErrorCodes.INVALID_INPUT_VALUE, NscsErrorCodes.EMPTY_CIPHERFILTER_VALUE)
                    .setSuggestedSolution(NscsErrorCodes.PROVIDE_VALID_CIPHERFILTER);
        }
        nscsLogger.info("end validateCipherFilterForTLS()...");
    }

}
