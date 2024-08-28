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
package com.ericsson.oss.services.nodes.dto;

import java.util.List;

import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord;
import com.ericsson.oss.services.nodes.dto.interfaces.Filter;

/**
 * FilterDTO sent by Node Security Configuration UI
 * @author egicass
 *
 */
public class NodesFilterDTO {
	
	private String name;
	private List<String> securityLevel;
	private List<String> ipsecconfig;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the securityLevel
	 */
	public List<String> getSecurityLevel() {
		return securityLevel;
	}
	/**
	 * @param securityLevel the securityLevel to set
	 */
	public void setSecurityLevel(List<String> securityLevel) {
		this.securityLevel = securityLevel;
	}
	

	public List<String> getIpsecconfig() {
		return ipsecconfig;
	}
	public void setIpsecconfig(List<String> ipsecconfig) {
		this.ipsecconfig = ipsecconfig;
	}


}
