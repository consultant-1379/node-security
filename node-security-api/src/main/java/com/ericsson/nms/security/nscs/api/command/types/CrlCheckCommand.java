/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.command.types;

/**
 * CrlCheckCommand Class for crlcheck related command.
 *
 * @author xkumkam
 *
 */
public class CrlCheckCommand extends NscsNodeCommand {

	private static final long serialVersionUID = 1647844098721531516L;

	public static final String CERT_TYPE_PROPERTY = "certtype";
	
	public static final String ALL = "ALL";

	public String getCertType() {
		return getValueString(CERT_TYPE_PROPERTY);
	}

}
