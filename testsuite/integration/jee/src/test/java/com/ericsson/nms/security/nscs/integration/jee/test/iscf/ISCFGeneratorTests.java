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
package com.ericsson.nms.security.nscs.integration.jee.test.iscf;

public interface ISCFGeneratorTests {

    void testIscfGeneratorReturnsByteArray() throws Exception;

    void testRestIscfGeneratorRestInterface() throws Exception;

    void testRestIscfGeneratorRestInterfaceShouldThrowErrorInvalidLevels() throws Exception;

    void testRestIscfGeneratorRestInterfaceShouldThrowErrorNullLevels() throws Exception;

    void testRestIscfGeneratorRestInterfaceIpsecTrafficAndOam() throws Exception;

    void testRestIscfGeneratorRestInterfaceCombined() throws Exception;

    void testIscfCancelSecLevelAndIpsec() throws Exception;

    void testIscfCancelIpsecFqdn() throws Exception;

    void testIscfSecurityDataGeneratorOam() throws Exception;
    
    void testIscfSecurityDataGeneratorCombo() throws Exception;

    void testIscfNodeModernizationOAM() throws Exception;

    void testIscfNodeModernizationCombo() throws Exception;

}

