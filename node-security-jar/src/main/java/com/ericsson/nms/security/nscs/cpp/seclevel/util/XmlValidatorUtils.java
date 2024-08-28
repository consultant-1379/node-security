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
package com.ericsson.nms.security.nscs.cpp.seclevel.util;


import org.slf4j.Logger;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import java.io.*;

/**
 * Utility class to validate XML with XSD.
 * @author emehsau
 *
 */

public class XmlValidatorUtils {
	
	
	public static final String SecLevelXsdFileName = "ValidatorInputForCertIssue.xsd";
	 
	@Inject
	 private Logger logger;

	
	 
	 /**
	  * This method will load the given file and return the InputStream.
	  * @param fileName : file name
	  * @return {@link InputStream} InputStream for given file.
	  */
	 public InputStream getFileResourceAsStream(final String fileName){
	    	final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
	    	logger.debug("Schema file for validation is [{}]", is);
	    	return is;
	    }
	 
	 
	 /**
		 * This method will validate whether content of given XML file is valid with XSD or not.
		 * 
		 * @param fileContent : File content of XML
		 * @return {@link Boolean} <p>true : if XML is valid</p> <p>false : if XML is invalid</p>
		 */

	 // SL2 dheeraj code
		 public boolean validateXMLSchemaForSecLevel(final String fileContent){

		        try {
		            final SchemaFactory factory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
		            final InputStream schemaInputStream = getFileResourceAsStream(SecLevelXsdFileName);
		            final Schema schema = factory.newSchema(new StreamSource(schemaInputStream));
		            final Validator validator = schema.newValidator();
		            validator.validate(new StreamSource(new StringReader(fileContent)));
		        } 
		        catch (IOException | SAXException e) 
		        {
		        	logger.info("User input xml validation is failed with xsd. Error is : [{}]" , e.getMessage());
		            return false;
		        }
		        return true;
		    } 
	
	
}
