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
package com.ericsson.nms.security.nscs.api.enums;

public enum CertificateType {
	
	IPSEC,
	OAM;

	/* 
	 * Return the Enum name
	 * @see java.lang.Enum#name()
	 */
	@Override
	public String toString() {
		return this.name();
	}

	public static CertificateType toCertificateType(String value) {
		CertificateType certificateType = null;
		switch (value) {
		case "IPSEC":
			certificateType = CertificateType.IPSEC;
			break;
		case "OAM":
			certificateType = CertificateType.OAM;
			break;
		default:
			break;
		}
		return certificateType;
	}
	

}
