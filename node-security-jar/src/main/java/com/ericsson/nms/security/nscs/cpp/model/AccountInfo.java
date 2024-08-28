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
package com.ericsson.nms.security.nscs.cpp.model;

import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;

import java.io.Serializable;

/**
 * Information on an account (for example SFTP/SMRS).
 * 
 * @see <a href="https://cpp-mom.rnd.ki.sw.ericsson.se/cpp_c14/momdoc/CPP-LSV127-gen9-complete_vs_LSV125/AccountInfo.html">CPP MOM</a>
 * 
 * @author egbobcs
 */
public class AccountInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String userName;
	private final char[] password;
	private final String host;

	/**
	 * Constructor which sets all the fields according to the input parameters
	 * 
	 * @param userName
	 * @param password
	 * @param host
	 */
	public AccountInfo(final String userName, final char[] password, final String host) {
		this.userName = userName;
		this.password = password;
		this.host = host;
	}

	/**
	 * Gets the userName
	 * 
	 * @return userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Gets the password
	 * 
	 * @return password
	 */
	public char[] getPassword() {
		return password;
	}

	/**
	 * Gets the host
	 * 
	 * @return host
	 */
	public String getHost() {
		return host;
	}
	/**
	 * Returns the MoParams representation of the supplied values.
	 * @see <a href="https://cpp-mom.rnd.ki.sw.ericsson.se/cpp_c14/momdoc/CPP-LSV127-gen9-complete_vs_LSV125/AccountInfo.html">CPP MOM</a>
	 */
	public MoParams toMoParams() {		
		return toMoParams(getPassword(), getHost(), getUserName());
	}
	/**
	 * Returns the MoParams representation of the object's values.
	 * @see <a href="https://cpp-mom.rnd.ki.sw.ericsson.se/cpp_c14/momdoc/CPP-LSV127-gen9-complete_vs_LSV125/AccountInfo.html">CPP MOM</a>
	 */
	public static MoParams toMoParams(final char[] password, final String remoteHost, final String userID) {
		final MoParams params = new MoParams();
		params.addParam("password", new String(password), true);
		params.addParam("remoteHost", remoteHost);
		params.addParam("userID", userID);		
		return params;
	}
}
