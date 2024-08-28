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
package com.ericsson.nms.security.nscs.integration.jee.test.cpp;

public interface CppSecurityServiceTests {

	void prepareInitEnrollment() throws Exception;
	void prepareInitEnrollmentKey2048() throws Exception;
        void prepareInitEnrollmentIPv6() throws Exception;
        void prepareInstallCorbaTrust() throws Exception;
        void prepareInstallCorbaTrustForAP() throws Exception;
        void prepareInstallCorbaTrustNotSupportedCat() throws Exception;
        void getTrustdistributionPointUrlIPv4() throws Exception	;
        void getTrustdistributionPointUrlIPv6() throws Exception	;
        void prepareInitEnrollmentForRBS13B() throws Exception;
}
