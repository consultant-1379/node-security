package com.ericsson.nms.security.nscs.handler.command.utility;

import java.nio.charset.StandardCharsets;

import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;

import com.ericsson.oss.itpf.security.cryptography.CryptographyService;

public class PasswordHelper {

    
    @Inject
    CryptographyService cryptographyService;

    public String encryptEncode(final String text) {
        if (text == null) {
            return null;
        }
        return encode(encrypt(text));
    }

    public String decryptDecode(final String text) {
        return decrypt(decode(text));
    }

    private String encode(final byte[] bytes) {
        return DatatypeConverter.printBase64Binary(bytes);
    }

    private byte[] encrypt(final String text) {
        return cryptographyService.encrypt(text.getBytes(StandardCharsets.UTF_8));
    }

    private byte[] decode(final String value) {
        return DatatypeConverter.parseBase64Binary(value);
    }

    private String decrypt(final byte[] encryptedBytes) {
        return new String(cryptographyService.decrypt(encryptedBytes));
    }
    
    
   
}
