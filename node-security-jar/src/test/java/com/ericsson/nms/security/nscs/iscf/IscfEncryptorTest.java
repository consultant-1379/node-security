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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author ealemca
 */
@RunWith(MockitoJUnitRunner.class)
public class IscfEncryptorTest {

    /*
     * In the scope of FOSS activities, the bouncycastle library has been uplifted to 1.67 version. In the IscfEncryptor class, the deprecated
     * org.bouncycastle.crypto.engines.AESFastEngine has been replaced with org.bouncycastle.crypto.engines.AESEngine class. The following constants
     * contains the encrypted results generated with the org.bouncycastle.crypto.engines.AESFastEngine and they are used to verify the
     * backward-compatibility of the correspondent encrypted results generated with the org.bouncycastle.crypto.engines.AESEngine.
     */
    private static final byte[] AES_FAST_ENGINE_RESULT = new byte[] { -120, -52, 125, -119, 116, 30, 55, 42, 67, -55, -37, 100, -65, 70, -56, 100, 73,
            -101, 32, -84, -31, 22, -64, -63, -35, 114, -11, -126, 38, -22, -74, -56 };

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Spy
    private IscfConfigurationBean config;

    @InjectMocks
    private IscfEncryptor beanUnderTest;

    @Test
    public void testEncryptSimple() throws Exception {
        byte[] unencryptedContent = "Some content to encrypt".getBytes(IscfConstants.UTF8_CHARSET);
        byte[] ric = "RANDOMRIC".getBytes(IscfConstants.UTF8_CHARSET);
        byte[] salt = "RANDOMSALT".getBytes(IscfConstants.UTF8_CHARSET);
        byte[] result = beanUnderTest.encrypt(unencryptedContent, ric, salt);
        assertFalse(Arrays.equals(unencryptedContent, result));
        assertArrayEquals(result, AES_FAST_ENGINE_RESULT);
    }

    @Test
    public void testDecryptSimple() throws Exception {
        byte[] unencryptedContent = "Some content to encrypt".getBytes(IscfConstants.UTF8_CHARSET);
        byte[] ric = "RANDOMRIC".getBytes(IscfConstants.UTF8_CHARSET);
        byte[] salt = "RANDOMSALT".getBytes(IscfConstants.UTF8_CHARSET);
        byte[] encryptedContent = beanUnderTest.encrypt(unencryptedContent, ric, salt);
        assertArrayEquals(encryptedContent, AES_FAST_ENGINE_RESULT);
        byte[] decryptedContent = beanUnderTest.decrypt(encryptedContent, ric, salt);
        assertArrayEquals(unencryptedContent, decryptedContent);
    }

    @Test
    public void testCannotDecryptMessageWithDifferentSalt() throws Exception {
        exception.expect(IscfEncryptionException.class);
        // TODO use below if junit is updated to 4.11 or above (currently 4.10)
        // InvalidCipherTextException expectedCause = new InvalidCipherTextException();
        // exception.expectCause(is(expectedCause));
        byte[] unencryptedContent = "Some content to encrypt".getBytes(IscfConstants.UTF8_CHARSET);
        byte[] ric = "RANDOMRIC".getBytes(IscfConstants.UTF8_CHARSET);
        byte[] salt = "RANDOMSALT".getBytes(IscfConstants.UTF8_CHARSET);
        byte[] encryptedContent = beanUnderTest.encrypt(unencryptedContent, ric, salt);
        assertArrayEquals(encryptedContent, AES_FAST_ENGINE_RESULT);
        byte[] newSalt = "NEWRANDOMSALT".getBytes(IscfConstants.UTF8_CHARSET);
        byte[] decryptedContent = beanUnderTest.decrypt(encryptedContent, ric, newSalt);
        fail("Should not have gotten past decrypt( " + new String(decryptedContent, IscfConstants.UTF8_CHARSET) + " )");
    }

    @Test
    public void testCannotDecryptMessageWithDifferentRIC() throws Exception {
        exception.expect(IscfEncryptionException.class);
        // TODO use below if junit is updated to 4.11 or above (currently 4.10)
        // InvalidCipherTextException expectedCause = new InvalidCipherTextException();
        // exception.expectCause(is(expectedCause));
        byte[] unencryptedContent = "Some content to encrypt".getBytes(IscfConstants.UTF8_CHARSET);
        byte[] ric = "RANDOMRIC".getBytes(IscfConstants.UTF8_CHARSET);
        byte[] salt = "RANDOMSALT".getBytes(IscfConstants.UTF8_CHARSET);
        byte[] encryptedContent = beanUnderTest.encrypt(unencryptedContent, ric, salt);
        assertArrayEquals(encryptedContent, AES_FAST_ENGINE_RESULT);
        byte[] newRic = "NEWRANDOMSALT".getBytes(IscfConstants.UTF8_CHARSET);
        byte[] decryptedContent = beanUnderTest.decrypt(encryptedContent, newRic, salt);
        fail("Should not have gotten past decrypt( " + new String(decryptedContent, IscfConstants.UTF8_CHARSET) + " )");
    }

}
