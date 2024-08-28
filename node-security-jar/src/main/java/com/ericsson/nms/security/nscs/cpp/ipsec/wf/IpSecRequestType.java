/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.security.nscs.cpp.ipsec.wf;

/**
 * <p>
 * Enum that holds all the types of IpSecRequest like IP_SEC_ENABLE_CONF1, IP_SEC_ENABLE_CONF2, IP_SEC_DISABLE or IP_SEC_INSTALL_CERTS (all operations which can change the state of node)
 * </p>
 * @author esneani.
 */
public enum IpSecRequestType {
	
	IP_SEC_ENABLE_CONF1("IpSecEnableConf1"), 
	IP_SEC_ENABLE_CONF2("IpSecEnableConf2"),
	IP_SEC_DISABLE("IpSecDisable"), 
	IP_SEC_INSTALL_CERTS("IpSecInstallCertificates");
 
	private final String requestType;
 
	IpSecRequestType(final String requestType) {
		this.requestType = requestType;
	}
 
	public String getRequestType() {
		return requestType;
	} 
}