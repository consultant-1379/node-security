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
package com.ericsson.nms.security.nscs.integration.jee.test.command;

public interface CreateCredentialsTests {

	void cppCreateCredentialsWith2ValidNodes() throws Exception;
	void cppCreateCredentialsWith2ValidNodesTwice() throws Exception;
	void cppCreateCredentialsWithInvalidNodeTest() throws Exception;
	void cppCreateCredentialsWithStarTest() throws Exception;
	void cppCreateCredentialsWithMissingAttributeTest() throws Exception;
	void testAddTargetGroupsWhenSecurityMODoesNotExist() throws Exception;
	void testAddTargetGroupsWhenAllSecurityMOsExist() throws Exception;
	void testAddTargetGroupsWhenSomeSecurityMOsExist() throws Exception;
	void testAddTargetGroupsWhenTargetGroupsAlreadySet() throws Exception;
	void testAddTargetGroupsWhenTargetGroupsNotAlreadySet() throws Exception;

	void cppCreateCredentialsWith2ValidNetworkElementNodes() throws Exception;

	void testMiniLinkIndoorCreateCredentials() throws Exception;
	void testMiniLinkCn210CreateCredentials() throws Exception;
	void testMiniLinkCn510R1CreateCredentials() throws Exception;
	void testMiniLinkCn510R2CreateCredentials() throws Exception;
	void testMiniLinkCn810R1CreateCredentials() throws Exception;
	void testMiniLinkCn810R2CreateCredentials() throws Exception;
	void testMiniLink665xCreateCredentials() throws Exception;
	void testMiniLink669xCreateCredentials() throws Exception;
	void testMiniLinkMW2CreateCredentials() throws Exception;
	void testMiniLink6352CreateCredentials() throws Exception;
	void testMiniLink6351CreateCredentials() throws Exception;
	void testMiniLink6366CreateCredentials() throws Exception;
	void testMiniLinkPT2020CreateCredentials() throws Exception;
	void testSwitch6391CreateCredentials() throws Exception;
	void testFronthaul6392CreateCredentials() throws Exception;

	void testCiscoAsr9000CreateCredentials() throws Exception;
	void testCiscoAsr900CreateCredentials() throws Exception;
	void testJuniperMxCreateCredentials() throws Exception;
}
