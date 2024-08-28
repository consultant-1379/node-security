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

/**
 * Enumeration listing all possible security levels of a node
 */
public enum SecurityLevel {
	UNKNOWN("UNKNOWN"),
	LEVEL_1("1"),
	LEVEL_2("2"),
	LEVEL_3("3"),
	LEVEL_NOT_SUPPORTED("LEVEL_NOT_SUPPORTED");

	private String securityLevel;

	private SecurityLevel(final String securityLevel) {
		this.securityLevel = securityLevel;
	}

	@Override
	public String toString() {
		return this.securityLevel;
	}

	/**
	 * Get the level enum value.
	 * 
	 * @return value based on the level
	 * 
	 */
	public String getLevel() {
		return this.name();
	}

	/**
	 * <p>
	 * Translates a String into an appropriated SecurityLevel instance.
	 * </p>
	 * 
	 * @param level
	 *            level string. Accepted strings are :
	 *            "1","2","3","Level_1","Level_2","Level_3"
	 * @return SecurityLevel instance
	 */
	public static SecurityLevel getSecurityLevel(String level) {

		if(level != null)
		{
			if (level.toLowerCase().startsWith("level_")) {
				level = stripOffLevel_(level);
			}
	
			if (SecurityLevel.LEVEL_1.toString().equals(level)) {
				return SecurityLevel.LEVEL_1;
			} else if (SecurityLevel.LEVEL_2.toString().equals(level)) {
				return SecurityLevel.LEVEL_2;
			} else if (SecurityLevel.LEVEL_3.toString().equals(level)) {
				return SecurityLevel.LEVEL_3;
			} else {
				return SecurityLevel.LEVEL_NOT_SUPPORTED;
			}
		}
		else
		{
			return SecurityLevel.LEVEL_NOT_SUPPORTED;
		}
	}

	public static String stripOffLevel_(final String str) {
		final String extractedLevel = str.substring((str.indexOf('_') + 1));

		return extractedLevel;
	}
}
