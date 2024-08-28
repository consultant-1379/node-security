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
package com.ericsson.nms.security.nscs.util;

import java.io.Serializable;

import com.ericsson.nms.security.nscs.api.exception.InvalidVersionException;

/**
 * This interface represents a reference to a generic version
 */
public interface Version extends Serializable {

	/**
	 * Check whether the version has a valid format
	 * @return true if valid, false otherwise
	 */
	boolean isValid();
	
	/**
	 * Check whether the version is less than the given one
	 * @param version The given version to compare with
	 * @return true if less than, false otherwise
	 * @throws An exception is thrown if given version is not valid
	 */
	boolean isLessThan(final String version) throws InvalidVersionException;
	
	/**
	 * Check whether the version is less than or equal to the given one
	 * @param version The given version to compare with
	 * @return true if less than or equal to, false otherwise
	 * @throws An exception is thrown if given version is not valid
	 */
	boolean isLessThanOrEqualTo(final String version) throws InvalidVersionException;
	
	/**
	 * Check whether the version is equal to the given one
	 * @param version The given version to compare with
	 * @return true if equal to, false otherwise
	 * @throws An exception is thrown if given version is not valid
	 */
	boolean isEqualTo(final String version) throws InvalidVersionException;
	
	/**
	 * Check whether the version is greater than the given one
	 * @param version The given version to compare with
	 * @return true if greater than, false otherwise
	 * @throws An exception is thrown if given version is not valid
	 */
	boolean isGreaterThan(final String version) throws InvalidVersionException;
	
	/**
	 * Check whether the version is greater than or equal to the given one
	 * @param version The given version to compare with
	 * @return true if greater than or equal to, false otherwise
	 * @throws An exception is thrown if given version is not valid
	 */
	boolean isGreaterThanOrEqualTo(final String version) throws InvalidVersionException;
}
