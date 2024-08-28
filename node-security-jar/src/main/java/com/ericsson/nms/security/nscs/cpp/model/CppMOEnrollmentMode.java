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
package com.ericsson.nms.security.nscs.cpp.model;

public enum CppMOEnrollmentMode {

	SCEP("0"),
	CMPV2_VC("1"),
	CMPV2_INITIAL("2"),
	CMPV2_UPDATE("3"),
	MANUAL("4");
	
	private String enrollmentModeValue;
	
	private CppMOEnrollmentMode(final String enrollmentModeValue) {
		this.enrollmentModeValue = enrollmentModeValue;
	}
	
	public String getEnrollmentModeValue() {
		return this.enrollmentModeValue;
	}
}
