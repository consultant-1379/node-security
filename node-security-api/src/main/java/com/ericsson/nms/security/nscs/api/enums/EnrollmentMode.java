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
package com.ericsson.nms.security.nscs.api.enums;

public enum EnrollmentMode {

	SCEP("0"), 
	CMPv2_VC("1"), 
	CMPv2_INITIAL("2"), 
	CMPv2_UPDATE("3"), 
	MANUAL("4"), 
	OFFLINE_PKCS12("5"), 
	OFFLINE_CSR("6"), 
	ONLINE_SCEP("7"), 
	NOT_SUPPORTED("NOT_SUPPORTED");
	
	private String enrollmentModeValue;
	
	private EnrollmentMode(final String enrollmentMode) {
		this.enrollmentModeValue = enrollmentMode;
	}
	
	public String getEnrollmentModeValue() {
		return this.enrollmentModeValue;
	}
	
	/**
	 * @param value to translte in EnrollmentMode
	 * @return Get the EnrollmentMode from a String value 
	 */
	public static EnrollmentMode getEnrollmentModeFromValue(final String value) {
		EnrollmentMode retValue = EnrollmentMode.NOT_SUPPORTED;
		for(EnrollmentMode em : EnrollmentMode.values()) {
			if(em.getEnrollmentModeValue().equals(value)){
				retValue = em;
				break;
			}
		}
		return retValue;
	}
}
