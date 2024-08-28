package com.ericsson.nms.security.nscs.handler.command.utility;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.security.cryptography.CryptographyService;

@RunWith(MockitoJUnitRunner.class)
public class PasswordHelperTest {

    private static String TESTPASSWD = "marameo@123_despicable42";
    private static String ENCRYPTEDPASSWD = "xyz@!x";


    @Mock
    CryptographyService cryptographyService;
    
    @InjectMocks
    PasswordHelper passwordHelper;
    

    @Test
    public void testEncryptDecrypt() {

        Mockito.when(cryptographyService.encrypt(TESTPASSWD.getBytes())).thenReturn(ENCRYPTEDPASSWD.getBytes());
        Mockito.when(cryptographyService.decrypt(ENCRYPTEDPASSWD.getBytes())).thenReturn(TESTPASSWD.getBytes());
        String passwordEncrypted = passwordHelper.encryptEncode(TESTPASSWD);
        Mockito.verify(cryptographyService, Mockito.times(1)).encrypt(Mockito.any(byte[].class));
        String passwordDecrypted = passwordHelper.decryptDecode(passwordEncrypted);
        Mockito.verify(cryptographyService, Mockito.times(1)).decrypt(Mockito.any(byte[].class));
        assertTrue(TESTPASSWD.equals(passwordDecrypted));

    }
}
