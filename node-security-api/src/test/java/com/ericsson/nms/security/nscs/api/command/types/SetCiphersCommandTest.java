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
package com.ericsson.nms.security.nscs.api.command.types;

import java.util.HashMap;
import java.util.Map;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test Class for SetCiphersCommand
 * 
 * @author xkumkam
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SetCiphersCommandTest {

    @InjectMocks
    CiphersConfigCommand ciphersConfigCommand;

    public static final String PROTOCOL_PROPERTY = "protocol";

    public static final String ENCRYPT_ALGOS_PROPERTY = "encryptalgos";

    public static final String KEX_PROPERTY = "kex";

    public static final String MACS_PROPERTY = "macs";

    public static final String CIPHER_FILTER_PROPERTY = "cipherFilter";

    Map<String, Object> prop;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        prop = new HashMap<>();
        prop.put(CiphersConfigCommand.PROTOCOL_PROPERTY, PROTOCOL_PROPERTY);
        prop.put(CiphersConfigCommand.ENCRYPT_ALGOS_PROPERTY, ENCRYPT_ALGOS_PROPERTY);
        prop.put(CiphersConfigCommand.KEX_PROPERTY, KEX_PROPERTY);
        prop.put(CiphersConfigCommand.MACS_PROPERTY, MACS_PROPERTY);
        prop.put(CiphersConfigCommand.CIPHER_FILTER_PROPERTY, CIPHER_FILTER_PROPERTY);
        ciphersConfigCommand.setProperties(prop);
    }

    /**
     * Test method for {@link com.ericsson.nms.security.nscs.api.command.types.CiphersConfigCommand#getProtocolTypeProperty()}.
     */
    @Test
    public void testGetProtocolTypeProperty() {
        Assert.assertEquals(PROTOCOL_PROPERTY, ciphersConfigCommand.getProtocolProperty());
    }

    /**
     * Test method for {@link com.ericsson.nms.security.nscs.api.command.types.CiphersConfigCommand#getEncryptAlgosProperty()}.
     */
    @Test
    public void testGetEncryptAlgosProperty() {
        Assert.assertEquals(ENCRYPT_ALGOS_PROPERTY, ciphersConfigCommand.getEncryptAlgosProperty());
    }

    /**
     * Test method for {@link com.ericsson.nms.security.nscs.api.command.types.CiphersConfigCommand#getKexProperty()}.
     */
    @Test
    public void testGetKexProperty() {
        Assert.assertEquals(KEX_PROPERTY, ciphersConfigCommand.getKexProperty());
    }

    /**
     * Test method for {@link com.ericsson.nms.security.nscs.api.command.types.CiphersConfigCommand#getMacsProperty()}.
     */
    @Test
    public void testGetMacsProperty() {
        Assert.assertEquals(MACS_PROPERTY, ciphersConfigCommand.getMacsProperty());
    }

    /**
     * Test method for {@link com.ericsson.nms.security.nscs.api.command.types.CiphersConfigCommand#getCipherFilterProperty()}.
     */
    @Test
    public void testGetCipherFilterProperty() {
        Assert.assertEquals(CIPHER_FILTER_PROPERTY, ciphersConfigCommand.getCipherFilterProperty());
    }
}
