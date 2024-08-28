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
package com.ericsson.nms.security.nscs.api.pki.cache;

public class PkiCachedRecord<T> {

	private final long millis;
	private final T value;

	/**
	 * @param millis the milliseconds
	 * @param value the value
	 */
	public PkiCachedRecord(final long millis, final T value) {
		super();
		this.millis = millis;
		this.value = value;
	}

	/**
	 * @return the millis
	 */
	public long getMillis() {
		return millis;
	}

	/**
	 * @return the value
	 */
	public T getValue() {
		return value;
	}

}
