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
package com.ericsson.nms.security.nscs.pki;

import java.util.Properties;

import com.ericsson.nms.security.nscs.api.exception.PropertiesFileNotFoundException;
import com.ericsson.nms.security.nscs.util.PropertiesReader;

public class NscsPkiMockManagement {

	private final static String MOCK_ENTITY = "mock.entity";
	private final static String MOCK_PROFILE = "mock.profile";
	private final static String MOCK_CERTIFICATE = "mock.certificate";
	private final static String MOCK_CONFIGURATION = "mock.configuration";

	private NscsPkiMockManagement() {
	} // Only static methods

	public static boolean useMockEntityManager() {
		return useMock(MOCK_ENTITY);
	}

	public static boolean useMockProfileManager() {
		return useMock(MOCK_PROFILE);
	}

	public static boolean useMockCertificateManager() {
		return useMock(MOCK_CERTIFICATE);
	}

	public static boolean useMockPkiConfigurationManager() {
		return useMock(MOCK_CONFIGURATION);
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
