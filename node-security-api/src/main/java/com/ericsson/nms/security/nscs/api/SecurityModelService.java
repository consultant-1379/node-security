/**
 * 
 */
package com.ericsson.nms.security.nscs.api;

import com.ericsson.nms.security.nscs.api.credentials.SecurityCredentialMetaData;


public interface SecurityModelService {

	/**
	 * The method return a {@link SecurityCredentialMetaData} instance that provides all credential capabilities meta data about given node.
	 * @param nodeName: name that identifies wanted node.
	 * @return a {@link SecurityCredentialMetaData} instance.
	 */
	public SecurityCredentialMetaData getCredentialMetaData(final String nodeName);
}
