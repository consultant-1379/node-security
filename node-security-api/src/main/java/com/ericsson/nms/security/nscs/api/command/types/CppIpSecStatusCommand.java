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
package com.ericsson.nms.security.nscs.api.command.types;

/**
 * Representation of the cpp-ipsec-status command
 * 
 * @author emehsau
 */
public class CppIpSecStatusCommand extends NscsNodeCommand{

	private static final long serialVersionUID = -8643978144064918350L;
	
	public static final String CONFIGURATION = "configuration";
	
	 /**
     * To check whether Configuration is enable or not
     * 
     * @return <code>true</code> if configuration is enable, otherwise return is
     *         <code>false</code>
     */
	public boolean hasConfiguration(){
		 return hasProperty(CONFIGURATION);
	}

}
