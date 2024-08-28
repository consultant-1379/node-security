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
package com.ericsson.nms.security.nscs.ssh;

public enum SSHKeyGenCommand {
	SSH_KEY_CREATE("Copy_key"), 
	SSH_KEY_UPDATE("Update_key"),
	SSH_KEY_DELETE("Delete_key");

	private final String text;

	SSHKeyGenCommand(final String value) {
		this.text = value;
	}

	@Override
	public String toString() {
		return this.text;
	}
}
