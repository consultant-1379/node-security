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
package com.ericsson.nms.security.nscs.capabilitymodel.service;

import java.util.Properties;

import com.ericsson.nms.security.nscs.api.exception.PropertiesFileNotFoundException;
import com.ericsson.nms.security.nscs.util.PropertiesReader;

/**
 * Auxiliary class to provide access to NSCS Capability Model properties.
 * 
 * @author emaborz
 */
public class NscsCapabilityModelProperties {

	private final static String MOCK_CAPABILITYMODEL = "mock.capabilitymodel";

	private NscsCapabilityModelProperties() {
	} // Only static methods

	public static boolean useMockCapabilityModel() {
		return useMock(MOCK_CAPABILITYMODEL);
	}

	private static boolean useMock(final String name) {

		boolean ret = false;
		Properties props = null;
		try {
			props = PropertiesReader.getConfigProperties();
		} catch (PropertiesFileNotFoundException e) {
			// no properties file found, mock shall not be used
		}
		if (props != null && "true".equals(props.getProperty(name))) {
			ret = true;
		}
		return ret;
	}
}
