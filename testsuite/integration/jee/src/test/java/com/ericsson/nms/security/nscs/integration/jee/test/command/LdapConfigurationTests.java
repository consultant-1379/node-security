/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.integration.jee.test.command;

public interface LdapConfigurationTests {

    void ldapValidConfigurationTest() throws Exception;

    void ldapInvalidConfigurationTest() throws Exception;

    void ldapConfigurationPartialInvalidNodeTest() throws Exception;

    void ldapConfigurationAllInvalidNodeTest() throws Exception;

    void ldapConfigurationManualTest() throws Exception;

    void ldapValidReConfigurationTest() throws Exception;

    void ldapReConfigurationPartialInvalidNodeTest() throws Exception;

    void ldapReConfigurationAllInvalidNodeTest() throws Exception;

    void ldapInvalidReConfigurationTest() throws Exception;

}
