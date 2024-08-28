/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CrlCheckCommandTest {

    @InjectMocks
    CrlCheckCommand cRLCheckCommand;

    String expected;
    Map<String, Object> prop;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        expected = "certtype";
        prop = new HashMap<>();
        prop.put(CrlCheckCommand.CERT_TYPE_PROPERTY, expected);
        cRLCheckCommand.setProperties(prop);
    }

    /**
     * Test method for {@link com.ericsson.nms.security.nscs.api.command.types.CRLCheckCommand#getCertType()}.
     */
    @Test
    public void testGetCertType() {
        Assert.assertEquals(expected, cRLCheckCommand.getCertType());
    }

}
