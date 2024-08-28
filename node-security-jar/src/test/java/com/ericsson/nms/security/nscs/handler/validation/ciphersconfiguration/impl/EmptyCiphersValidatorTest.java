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
package com.ericsson.nms.security.nscs.handler.validation.ciphersconfiguration.impl;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.*;
import com.ericsson.nms.security.nscs.handler.validation.ciphersconfig.EmptyCiphersValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;

@RunWith(MockitoJUnitRunner.class)
public class EmptyCiphersValidatorTest {

    @InjectMocks
    EmptyCiphersValidator emptyCiphersValidator;


    @Mock
    private NscsLogger nscsLogger;

    private final String encryptionAlgorithm = "3des-cbc";
    private final String keyExchangeAlgorithm = "diffie-hellman-group1-sha1";
    private final String macAlgorithm = "hmac-sha1";
    private final String cipherFilter = "RSA+SHA";

    private final String emptyEncryptionAlgorithm = "";
    private final String emptyKeyExchangeAlgorithm = "";
    private final String emptyMacAlgorithm = "";
    private final String emptyCipherFilter = "";


    private LinkedList<String> encryptCiphersList = new LinkedList<String>();
    private LinkedList<String> keyExchangeCiphersList = new LinkedList<String>();
    private LinkedList<String> macCiphersmacCiphersList = new LinkedList<String>();

    private LinkedList<String> emptyEmptyCiphersList = new LinkedList<String>();
    private LinkedList<String> emptyKeyExchangeCiphersList = new LinkedList<String>();
    private LinkedList<String> emptyMacCiphersmacCiphersList = new LinkedList<String>();

    private NodeCiphers nodeCiphers = new NodeCiphers();

    @Before
    public void setup() {



        encryptCiphersList = new LinkedList<String>();
        encryptCiphersList.add(encryptionAlgorithm);
        encryptCiphersList.add(emptyEncryptionAlgorithm);

        keyExchangeCiphersList = new LinkedList<String>();
        keyExchangeCiphersList.add(keyExchangeAlgorithm);
        keyExchangeCiphersList.add(emptyKeyExchangeAlgorithm);

        macCiphersmacCiphersList = new LinkedList<String>();
        macCiphersmacCiphersList.add(macAlgorithm);
        macCiphersmacCiphersList.add(emptyMacAlgorithm);

        emptyEmptyCiphersList = new LinkedList<String>();
        emptyEmptyCiphersList.add(emptyEncryptionAlgorithm);

        emptyKeyExchangeCiphersList = new LinkedList<String>();
        emptyKeyExchangeCiphersList.add(emptyKeyExchangeAlgorithm);

        emptyMacCiphersmacCiphersList = new LinkedList<String>();
        emptyMacCiphersmacCiphersList.add(emptyMacAlgorithm);

    }

    /**
     * Test method for {@link com.ericsson.nms.security.nscs.handler.validation.ciphersconfig.EmptyCiphersValidator#validate(com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.NodeCiphers, boolean)}.
     */
    @Test
    public void testValidate_EmptySSHCiphersAndEmptyTLSCipherFilterSupported_SuccessScenario() {

        nodeCiphers = prepareNodeCiphers(emptyCipherFilter, emptyEmptyCiphersList, emptyKeyExchangeCiphersList, emptyMacCiphersmacCiphersList);
        emptyCiphersValidator.validateNodeCiphers(nodeCiphers, true);
        Mockito.verify(nscsLogger).info("end validateCipherFilterForTLS()...");
    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.handler.validation.ciphersconfig.EmptyCiphersValidator#validate(com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.NodeCiphers, boolean)}
     * .
     */
    @Test(expected = InvalidArgumentValueException.class)
    public void testValidate_EmptySSHCiphersNotSupported_InvalidArgumentValueException() {

        nodeCiphers = prepareNodeCiphers(cipherFilter, emptyEmptyCiphersList, emptyKeyExchangeCiphersList, emptyMacCiphersmacCiphersList);
        emptyCiphersValidator.validateNodeCiphers(nodeCiphers, false);

    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.handler.validation.ciphersconfig.EmptyCiphersValidator#validate(com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.NodeCiphers, boolean)}
     * .
     */
    @Test(expected = InvalidArgumentValueException.class)
    public void testValidate_EmptyTLSCipherFilterNotSupported_InvalidArgumentValueException() {

        nodeCiphers = prepareNodeCiphers(emptyCipherFilter, encryptCiphersList, keyExchangeCiphersList, macCiphersmacCiphersList);
        emptyCiphersValidator.validateNodeCiphers(nodeCiphers, false);

    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.handler.validation.ciphersconfig.EmptyCiphersValidator#validate(com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.NodeCiphers, boolean)}
     * .
     */
    @Test
    public void testValidate_FilledSSHCiphersAndTLSCipherFilterWithEmptyCiphersSupported_SuccessScenario() {

        nodeCiphers = prepareNodeCiphers(cipherFilter, encryptCiphersList, keyExchangeCiphersList, macCiphersmacCiphersList);
        emptyCiphersValidator.validateNodeCiphers(nodeCiphers, true);
        Mockito.verify(nscsLogger).info("end validateCipherFilterForTLS()...");
    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.handler.validation.ciphersconfig.EmptyCiphersValidator#validate(com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.NodeCiphers, boolean)}
     * .
     */
    @Test
    public void testValidate_FilledSSHCiphersAndTLSCipherFilterWithEmptyCiphersNotSupported_SuccessScenario() {

        nodeCiphers = prepareNodeCiphers(cipherFilter, encryptCiphersList, keyExchangeCiphersList, macCiphersmacCiphersList);
        emptyCiphersValidator.validateNodeCiphers(nodeCiphers, false);
        Mockito.verify(nscsLogger).info("end validateCipherFilterForTLS()...");
    }


    private NodeCiphers prepareNodeCiphers(final String cipherFilter, final List<String> encCiphers, final List<String> keXCiphers,
            final List<String> mcCiphers) {

        final TlsProtocol tlsProtocol = new TlsProtocol();
        tlsProtocol.setCipherFilter(cipherFilter);

        final SshProtocol sshProtocol = new SshProtocol();


        final EncryptCiphers encryptCiphers = new EncryptCiphers();
        final KeyExchangeCiphers keyExchangeCiphers = new KeyExchangeCiphers();
        final MacCiphers macCiphers = new MacCiphers();

        encryptCiphers.setCipher(encCiphers);
        keyExchangeCiphers.setCipher(keXCiphers);
        macCiphers.setCipher(mcCiphers);

        sshProtocol.setEncryptCiphers(encryptCiphers);
        sshProtocol.setKeyExchangeCiphers(keyExchangeCiphers);
        sshProtocol.setMacCiphers(macCiphers);

        final NodeCiphers nCiphers = new NodeCiphers();

        nCiphers.setSshProtocol(sshProtocol);
        nCiphers.setTlsProtocol(tlsProtocol);

        return nCiphers;
    }

}