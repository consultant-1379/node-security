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
package com.ericsson.nms.security.nscs.util;

import java.io.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.exception.PropertiesFileNotFoundException;

/**
 * Auxiliary class to manage the configuration property parameters used in node
 * security. A property file is loaded with a location precedence rule:
 * <ul>
 * <li>the file is searched with full path</li>
 * <li>the file is searched into the default node security configuration
 * directory /ericsson/tor/data/secserv</li>
 * <li>the file is searched into the /tmp directory</li>
 * <li>the file is searched into node-security-jar module.</li>
 * </ul>
 */
public class PropertiesReader {

	private static final Logger logger = LoggerFactory
			.getLogger(PropertiesReader.class);

	private static final String NSCS_PROPERTY_NAME = "nscs.name.properties";
	private static final String DEFAULT_PROPERTY_FILE_NAME = "nscs.properties";
	private static final String DEFAULT_NSCS_CONFIGURATION_DIR = "/ericsson/tor/data/secserv/";
	private static Map<String, Properties> propertiesMap = new HashMap<String, Properties>();

	private PropertiesReader() {
	}

	/**
	 * Return the properties content of the default node security configuration
	 * file. Usually, this file is included into the node-security-jar module.
	 * 
	 * @return Properties
	 * @throws PropertiesFileNotFoundException
	 *             if no property file is found
	 */
	public static Properties getConfigProperties() throws PropertiesFileNotFoundException {
		String filename = System.getProperty(NSCS_PROPERTY_NAME,DEFAULT_PROPERTY_FILE_NAME);
		return getProperties(filename);
	}

	/**
	 * Return the property content of the file @param filename. The file is read
	 * when the method getProperties is firstly invoked.
	 * 
	 * @param filename
	 * @return properties
	 * @throws PropertiesFileNotFoundException
	 *             if no property file is found
	 */
	public static synchronized Properties getProperties(final String filename)
			throws PropertiesFileNotFoundException {

		Properties props = null;

		if (propertiesMap.containsKey(filename.trim().toLowerCase())) {
			props = propertiesMap.get(filename.trim().toLowerCase());
		} else {

			InputStream input = null;
			logger.info("Looking for property file.... {}", filename);
			try {
				// firstly assumed that the file has a full path
				input = readFromFullPath(filename);

				// secondly assumed that the file is in node security
				// configuration dir
				if (input == null) {
					input = readFromDefaultConfigurationDir(filename);
				}

				// thirdly assumed that the file is in /tmp/
				if (input == null) {
					input = readFromTmp(filename);
				}

				// fourthly assumed that that the file is in the jar
				if (input == null) {
					input = readFromJar(filename);
				}

				if (input != null) {
					props = new Properties();
					// load a properties file from class path, inside static
					// method
					props.load(input);
					propertiesMap.put(filename.trim().toLowerCase(), props);
				} else {
					throw new PropertiesFileNotFoundException(
							"Properties file not found: " + filename);
				}
			} catch (final Exception e) {
				throw new PropertiesFileNotFoundException(
						"Error retrieving properties file : " + filename);
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (final IOException e) {
						logger.error(e.getMessage());
					}
				}
			}
		}

		return props;
	}

	/**
	 * @param filename
	 * @return
	 */
	private static InputStream readFromJar(final String filename) {
		InputStream input;
		String filteredFilename = filename;
		if (filteredFilename != null && filteredFilename.length() > 1
				&& filteredFilename.charAt(0) == File.separatorChar) {
			filteredFilename = filteredFilename.substring(1,
					filteredFilename.length());
		}
		input = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(filteredFilename);
		logger.info("Found file {} from classpath as resource.", filteredFilename);
		return input;
	}

	/**
	 * @param filename
	 * @return
	 */
	private static InputStream readFromTmp(final String filename) {
		InputStream input = null;
		try {
			final File inputfile = new File("/tmp/" + filename);
			if (inputfile != null && inputfile.exists()) {
				input = new FileInputStream(inputfile);
				logger.info("Found file {} from tmp path.", inputfile);
			}
		} catch (final Exception e) {
			input = null;
		}
		return input;
	}

	/**
	 * @param filename
	 * @return
	 */
	private static InputStream readFromDefaultConfigurationDir(
			final String filename) {
		InputStream input = null;
		try {
			final File inputfile = new File(DEFAULT_NSCS_CONFIGURATION_DIR
					+ filename);
			if (inputfile != null && inputfile.exists()) {
				input = new FileInputStream(inputfile);
				logger.info("Found file {} from config path.", inputfile);
			}
		} catch (final Exception e) {
			input = null;
		}
		return input;
	}

	/**
	 * @param filename
	 * @return
	 */
	private static InputStream readFromFullPath(final String filename) {
		InputStream input = null;
		try {
			final File inputfile = new File(filename);
			if (inputfile != null && inputfile.exists()) {
				input = new FileInputStream(inputfile);
				logger.info("Found file {} from full path.", inputfile);
			}
		} catch (final Exception e) {
			input = null;
		}
		return input;
	}

}
