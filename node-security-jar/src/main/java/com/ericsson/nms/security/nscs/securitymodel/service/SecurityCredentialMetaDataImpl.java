/**
 * 
 */
package com.ericsson.nms.security.nscs.securitymodel.service;


import com.ericsson.nms.security.nscs.api.credentials.SecurityCredentialMetaData;


public class SecurityCredentialMetaDataImpl extends SecurityCredentialMetaData{
	
	/**
	 * @param rootRequired the rootRequired to set
	 */
	public void setRootRequired(boolean rootRequired) {
		this.rootRequired = rootRequired;
	}

	/**
	 * @param secureRequired the secureRequired to set
	 */
	public void setSecureRequired(boolean secureRequired) {
		this.secureRequired = secureRequired;
	}

	/**
	 * @param unsecureRequired the unsecureRequired to set
	 */
	public void setUnsecureRequired(boolean unsecureRequired) {
		this.unsecureRequired = unsecureRequired;
	}

}
