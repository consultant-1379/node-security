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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;

/**
 * Test class for CiphersConfigurationUtil
 * 
 * @author tcsvijc
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class CiphersConfigurationUtilTest {

    final String algos = "[[SHA1,SHA256]]";
    List<String> result = new ArrayList<String>();

    @InjectMocks
    private CiphersConfigurationUtil ciphersConfigurationUtil;

    @InjectMocks
    private NscsNodeUtility nscsNodeUtility;

    /**
     * Test method for {@link CiphersConfigurationUtil#getValidProtocolTypesForSetCiphers()} to get valid protocol types for setciphers.
     */
    @Test
    public void testGetValidProtocolTypesForCiphersConfiguration() {
        result = ciphersConfigurationUtil.getValidProtocolTypesForCiphersConfiguration();
        assertNotNull(result);
        assertTrue(result.contains(CiphersConstants.PROTOCOL_TYPE_SSH));
        assertTrue(result.contains(CiphersConstants.PROTOCOL_TYPE_TLS));
    }

    /**
     * Test method for {@link CiphersConfigurationUtil#getValidArgsToSetTlsCiphers()} to get valid arguments of tls protocol
     */
    @Test
    public void testGetValidArgsToSetTlsCiphers() {
        result = ciphersConfigurationUtil.getValidArgsToSetTlsCiphers();
        assertNotNull(result);
        assertTrue(result.contains("cipherfilter"));
    }

    /**
     * Test method for {@link CiphersConfigurationUtil#getValidArgsToSetSshCiphers()} to get valid arguments of ssh protocol
     */
    @Test
    public void testGetValidArgsToSetSshCiphers() {
        result = ciphersConfigurationUtil.getValidArgsToSetSshCiphers();
        assertNotNull(result);
        assertTrue(result.contains(CiphersConstants.KEX));
        assertTrue(result.contains(CiphersConstants.MACS));
        assertTrue(result.contains(CiphersConstants.ENCRYPT_ALGOS));
    }

    /**
     * Test method for {@link nscsNodeUtility#getConvertStringToList()} to convert comma separated sting into list
     */
    @Test
    public void testConvertStringToList() {
        result = nscsNodeUtility.convertStringToList(algos);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}
