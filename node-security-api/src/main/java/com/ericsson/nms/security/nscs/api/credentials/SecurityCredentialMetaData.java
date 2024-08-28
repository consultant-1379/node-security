/**
 * 
 */
package com.ericsson.nms.security.nscs.api.credentials;

import java.io.Serializable;

import com.ericsson.nms.security.nscs.api.SecurityModelService;


/**
 * Abstract class that provides information about node capabilities.
 * An instance of this class can not be created. To get the needed instance use {@link SecurityModelService#getCredentialMetaData(String)} method.
 *
 */
public abstract class SecurityCredentialMetaData implements Serializable{
	
	private static final long serialVersionUID = 4189094987643185042L;
	
	protected boolean rootRequired;
	protected boolean secureRequired;
	protected boolean unsecureRequired;
	
	/**
	 * @return if root credentials are required.
	 */
	public boolean isRootRequired() {
		return rootRequired;
	}

	/**
	 * @return if secure credentials are required.
	 */
	public boolean isSecureRequired() {
		return secureRequired;
	}

	/**
	 * @return if unsecure credentials are required.
	 */
	public boolean isUnsecureRequired() {
		return unsecureRequired;
	}

	
}
