/**
 * 
 */
package com.ericsson.nms.security.nscs.api.credentials;

import java.io.Serializable;

/**
 * Pojo that describes the user credentials.
 *   - username: the username credential.
 *   - password: the password credential.
 *
 */
public class UserCredentials implements Serializable {
	
	private static final long serialVersionUID = -5027682249167388791L;
	
	final String username;
	final String password;
	
	
	/**
	 * @param username: the wanted user name.
	 * @param password: the wanted user password.
	 */
	public UserCredentials(String username, String password) {
		this.username = username;
		this.password = password;
	}


	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}


	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

}
