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
package com.ericsson.nms.security.nscs.api.command.types;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class KeyGeneratorCommandTest {

    @InjectMocks
    KeyGeneratorCommand keygencommand;

    /**
     * Test method for {@link com.ericsson.nms.security.nscs.api.command.types.KeyGeneratorCommand#getAlgorithmTypeSize()}.
     */
    @Test
    public void testGetAlgorithmTypeSize() {
        String expected = "RSA_1024";
        Map<String, Object> prop = new HashMap<>();
        prop.put(KeyGeneratorCommand.ALGORITHM_TYPE_SIZE_PROPERTY, expected);
        keygencommand.setProperties(prop);
        Assert.assertEquals(expected, keygencommand.getAlgorithmTypeSize());
    }

    @Test
    public void testGetAlgorithmTypeSize_False() {
        String expected = "RSA_1024";
        String otherKeyProp = "Other";
        Map<String, Object> prop = new HashMap<>();
        prop.put(otherKeyProp, expected);
        keygencommand.setProperties(prop);

        Assert.assertNotSame(expected, keygencommand.getAlgorithmTypeSize());

        Assert.assertEquals(expected, keygencommand.getProperties().get(otherKeyProp));
    }
}
