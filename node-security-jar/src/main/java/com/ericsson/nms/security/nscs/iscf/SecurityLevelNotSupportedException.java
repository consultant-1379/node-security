/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */
package com.ericsson.nms.security.nscs.iscf;

/**
 *
 * @author ealemca
 */
public class SecurityLevelNotSupportedException extends RuntimeException {

	private static final long serialVersionUID = 5487897172834781680L;

	public SecurityLevelNotSupportedException(final String reason) {
        super(reason);
    }

}
