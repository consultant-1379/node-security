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

public enum RevocationReason {
	
	UNSPECIFIED("unspecified"),
	KEY_COMPROMISE  ("keyCompromise"),
	CA_COMPROMISE ("CACompromise"),
	AFFILIATION_CHANGED ("affiliationChanged"),
	SUPERSEDED ("superseded"),
	CESSATION_OF_OPERATION ("cessationOfOperation"),
	CERTIFICATE_HOLD ("certificateHold"),
	REMOVE_FROM_CRL ("removeFromCRL"),
	PRIVILEGE_WITHDRAWN ("privilegeWithdrawn"),
	AA_COMPROMISE ("AACompromise");
	
	
    private String revocationReasonValue;
	
	private RevocationReason(final String revocationReason) {
		this.revocationReasonValue = revocationReason;
	}
	
	@Override
	public String toString() {
		return this.revocationReasonValue;
	}
	
	/**
	 * @param value input to translate to RevocationReason
	 * @return Get the RevocationReason from a String value 
	 */
	public static RevocationReason getRevocationReasonFromValue(final String value) {
		RevocationReason retValue = RevocationReason.UNSPECIFIED;
		for(RevocationReason rr : RevocationReason.values()) {
			if(rr.toString().equals(value)){
				retValue = rr;
				break;
			}
		}
		return retValue;
	}

	

}
