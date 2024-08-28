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
package com.ericsson.nms.security.nscs.integration.jee.test.moaction;

public interface MOActionTests {

	void moActionWithoutParameter() throws Exception;
	void moActionInitCertEnrollment() throws Exception;
	void moActionInstallTrustedCertificatesCorba() throws Exception;
	void moActionAdaptSecurityLevel() throws Exception;
	void moActionNonExistingNode() throws Exception;
	void moActionNonValidParams() throws Exception;
}
