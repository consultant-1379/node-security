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
package com.ericsson.nms.security.nscs.api.cpp.level;

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;

/**
 * Enumeration listing all possible security levels of a node
 */
public enum CPPSecurityLevel {

	LEVEL_1("1"), LEVEL_2("2"), LEVEL_3("3"), LEVEL_NOT_SUPPORTED(
			"LEVEL_NOT_SUPPORTED");

	private String securityLevel;

	private CPPSecurityLevel(final String securityLevel) {
		this.securityLevel = securityLevel;
	}

	@Override
	public String toString() {
		return this.securityLevel;
	}

	/**
	 * Gets the level enum value according to the CPP specification
	 * 
	 * 1:LEVEL_1 2:LEVEL_2 3:LEVEL_3 LEVEL_NOT_SUPPORTED:
	 * LEVEL_NOT_SUPPORTED
	 * 
	 * @return value based on the level
	 * 
	 */
	public String getLevel() {
		// if (this.equals(LEVEL_NOT_SUPPORTED)) {
		// return this.toString();
		// }
		// else {
		// return String.format("LEVEL_%s", securityLevel);
		// }
		return this.name();
	}

	/**
	 * <p>
	 * Translates a String into an appropriated CPPSecurityLevel instance.
	 * </p>
	 * 
	 * @param level
	 *            level string. Accepted strings are :
	 *            "1","2","3","Level_1","Level_2","Level_3"
	 * @return CPPSecurityLevel instance
	 */
	public static CPPSecurityLevel getSecurityLevel(String level) {

		if (level.toLowerCase().startsWith("level_")) {
			level = stripOffLevel_(level);
		}

		if (CPPSecurityLevel.LEVEL_1.toString().equals(level)) {
			return CPPSecurityLevel.LEVEL_1;
		} else if (CPPSecurityLevel.LEVEL_2.toString().equals(level)) {
			return CPPSecurityLevel.LEVEL_2;
		} else if (CPPSecurityLevel.LEVEL_3.toString().equals(level)) {
			return CPPSecurityLevel.LEVEL_3;
		} else {
			return CPPSecurityLevel.LEVEL_NOT_SUPPORTED;
		}

	}

	public static String stripOffLevel_(final String str) {
		final String extractedLevel = str.substring((str.indexOf('_') + 1));

		return extractedLevel;

	}

	public SecurityLevel getNewSecurityLevel() {
		return SecurityLevel.valueOf(this.name());
	}
}
