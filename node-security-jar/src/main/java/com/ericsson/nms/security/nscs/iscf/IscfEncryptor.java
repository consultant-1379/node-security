/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */
package com.ericsson.nms.security.nscs.iscf;

import java.security.Security;

import javax.inject.Inject;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encrypts and decrypts ISCF content such as certificates and one-time
 * passwords based on a given RBS Integrity Code and salt
 *
 * @author ealemca
 */
public class IscfEncryptor {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Inject
    private IscfConfigurationBean config;

    /**
     * Encrypt the content
     *
     * @param unencryptedContent
     * @param ric
     * @param salt
     * @return the encrypted content
     * @throws IscfEncryptionException
     */
    public byte[] encrypt(
            final byte[] unencryptedContent,
            final byte[] ric,
            final byte[] salt
    ) throws IscfEncryptionException {

        byte[] result = null;
        try {
            result = processRemovePadding(unencryptedContent, IscfConstants.ENCRYPT_FLAG, ric, salt);
        } catch (final DataLengthException | IllegalStateException | InvalidCipherTextException e) {
        	logger.warn("Exception when encrypting content");
            throw new IscfEncryptionException("Exception when encrypting content", e);
        }
        return result;

    }

    /**
     * Decrypt the content
     *
     * @param encryptedContent
     * @param ric
     * @param salt
     * @return the decrypted content
     * @throws IscfEncryptionException
     */
    public byte[] decrypt(
            final byte[] encryptedContent,
            final byte[] ric,
            final byte[] salt
    ) throws IscfEncryptionException {

        byte[] result = null;
        try {
            result = processRemovePadding(encryptedContent, IscfConstants.DECRYPT_FLAG, ric, salt);
        } catch (final DataLengthException | IllegalStateException | InvalidCipherTextException e) {
        	logger.warn("Exception when decrypting content");
            throw new IscfEncryptionException("Exception when decrypting content", e);
        }
        return result;

    }

    private byte[] processRemovePadding(
            final byte[] input,
            final boolean forEncryption,
            final byte[] ric,
            final byte[] salt
    ) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
        final int iterationCount = config.getCipherIterationCount();
        final int inputOffset = 0;
        int outputOffset = 0, outputLength = 0;
        int bytesProcessed;

        final PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator();
        gen.init(ric, salt, iterationCount);
        final CipherParameters params =
                gen.generateDerivedParameters(
                        config.getCipherKeySize(),
                        config.getCipherInitialisationVectorSize()
                );

        final PaddedBufferedBlockCipher myCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
        myCipher.init(forEncryption, params);

        final int maximumOutputLength = myCipher.getOutputSize(input.length);
        final byte[] obuf = new byte[maximumOutputLength];

        bytesProcessed = myCipher.processBytes(input, inputOffset, input.length, obuf, outputOffset);
        outputOffset += bytesProcessed;
        outputLength += bytesProcessed;

        bytesProcessed = myCipher.doFinal(obuf, outputOffset);

        outputOffset += bytesProcessed;
        outputLength += bytesProcessed;

        if (outputLength == obuf.length) {
            return obuf;
        } else {
            final byte[] truncatedOutput = new byte[outputLength];
            System.arraycopy(
                    obuf, 0,
                    truncatedOutput, 0,
                    outputLength
            );
            return truncatedOutput;
        }
    }
}
