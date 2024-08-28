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
package com.ericsson.nms.security.nscs.utilities;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.cpp.ipsec.util.CppIpSecStatusUtility;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.XmlValidatorUtils;

@RunWith(MockitoJUnitRunner.class)
public class XmlValidatorUtilityTest {
	
	public static final String xsdValidatorFileName = "ValidatorInputForCertIssue.xsd";
	
	@Spy
	private final Logger logger = LoggerFactory
			.getLogger(XmlValidatorUtility.class);
	
	@InjectMocks
    XmlValidatorUtility testObj;

	/**
	 * Test method for {@link com.ericsson.nms.security.nscs.utilities.XmlValidatorUtility#validateXMLSchema(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidateXMLSchema() {
		InputStream is = testObj.getFileResourceAsStream("SampleInputFileForCertificateIssueIPSEC.xml");
		assertNotNull("Input stream can't be null", is);
		boolean isValidFile = testObj.validateXMLSchema(getStringFromInputStream(is),xsdValidatorFileName);
		Assert.assertTrue("Validation must be successful", isValidFile);
	}

	/**
	 * Test method for {@link com.ericsson.nms.security.nscs.utilities.XmlValidatorUtility#validateXMLSchema(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidateXMLSchemaInvalidInput() {
		InputStream is = testObj.getFileResourceAsStream("SampleInvalidInputFileForCertificateIssue.xml");
		assertNotNull("Input stream can't be null", is);
		boolean isValidFile = testObj.validateXMLSchema(getStringFromInputStream(is),xsdValidatorFileName);
		Assert.assertFalse("Validation must be false", isValidFile);
	}

	/**
	 * Test method for {@link com.ericsson.nms.security.nscs.utilities.XmlValidatorUtility#getFileResourceAsStream(java.lang.String)}.
	 */
	@Test
	public void testGetFileResourceAsStream() {
		InputStream is = testObj.getFileResourceAsStream("SampleInputFileForCertificateIssueIPSEC.xml");
		 assertNotNull("Input stream can't be null", is);
		 //boolean isValidFile = testObj.validateXMLSchema(getStringFromInputStream(is),xsdValidatorFileName);
		 //Assert.assertTrue("Validation must be successful", isValidFile);
	}
	
        @Test
        public void testValidateXMLSchemaWithCommonName() {
                InputStream is = testObj.getFileResourceAsStream("SampleInputFileForCertificateIssueWithCommonName.xml");
                assertNotNull("Input stream can't be null", is);
                boolean isValidFile = testObj.validateXMLSchema(getStringFromInputStream(is),xsdValidatorFileName);
                Assert.assertTrue("Validation must be successful", isValidFile);
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
