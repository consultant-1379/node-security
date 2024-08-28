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
package com.ericsson.nms.security.nscs.cpp.ipsec.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class XmlValidatorUtilsTest {

	@Spy
	private final Logger logger = LoggerFactory
			.getLogger(CppIpSecStatusUtility.class);
	
	@InjectMocks
    XmlValidatorUtils testObj;


	@Test
	public void testGetFileResourceAsStream() {
		 InputStream is = testObj.getFileResourceAsStream("SampleInputFile.xml");
		 assertNotNull("Input stream can't be null", is);
		 boolean isValidFile = testObj.validateXMLSchema(getStringFromInputStream(is));
		 Assert.assertTrue("Validation must be successful", isValidFile);
	}
	
	@Test
	public void testvalidateXMLSchemaForInvalidData() {
		 InputStream is = testObj.getFileResourceAsStream("SampleInvalidInputFile.xml");
		 assertNotNull("Input stream can't be null", is);
		 boolean isValidFile = testObj.validateXMLSchema(getStringFromInputStream(is));
		 Assert.assertFalse("Validation must be faisled", isValidFile);
	}
	
	private String getStringFromInputStream(final InputStream is) {
		BufferedReader br = null;
		final StringBuilder sb = new StringBuilder();
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			//Nothing to do
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					//Nothing to do
				}
			}
		}
		return sb.toString();

	}
	 

}
