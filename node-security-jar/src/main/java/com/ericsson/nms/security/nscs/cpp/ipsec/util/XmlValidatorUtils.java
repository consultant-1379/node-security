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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.validator.IpSecNodesErrorHandler;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.validator.IpSecNodesErrorHandlerData;

/**
 * Utility class to validate XML with XSD.
 * 
 * @author emehsau
 *
 */

public class XmlValidatorUtils {

    public static final String xsdFileName = "UserInputForIPsec.xsd";

    @Inject
    private Logger logger;

    /**
     * This method will validate whether content of given XML file is valid with XSD or not.
     * 
     * @param fileContent
     *            : File content of XML
     * @return {@link Boolean}
     *         <p>
     *         true : if XML is valid
     *         </p>
     *         <p>
     *         false : if XML is invalid
     *         </p>
     */
    public boolean validateXMLSchema(final String fileContent) {

        try {
            final SchemaFactory factory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            final InputStream schemaInputStream = getFileResourceAsStream(xsdFileName);
            final Schema schema = factory.newSchema(new StreamSource(schemaInputStream));
            final Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(fileContent)));
        } catch (IOException | SAXException e) {
            logger.error("User input xml validation is failed with xsd. Error is : [{}]", e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * @param nodes
     * @return If the given {@link Nodes}
     */
    public Map<String, HashMap<String, IpSecNodesErrorHandlerData>> validateXMLSchema(final Nodes nodes) throws JAXBException, ParserConfigurationException, IOException {

        JAXBContext jc = null;
        Map<String, HashMap<String, IpSecNodesErrorHandlerData>> elemErrorMap = null;
        try {
            jc = JAXBContext.newInstance(Nodes.class);
        } catch (JAXBException e2) {
            // TODO Auto-generated catch block
            logger.error(e2.getMessage());
            throw e2;
        }
        Marshaller jaxbMarshaller = null;
        try {
            jaxbMarshaller = jc.createMarshaller();
        } catch (JAXBException e4) {
            logger.error(e4.getMessage());
            throw e4;
        }

        StringWriter sw = new StringWriter();
        try {
            jaxbMarshaller.marshal(nodes, sw);
        } catch (JAXBException e3) {
            logger.error(e3.getMessage());
            throw e3;
        }

        String xmlString = sw.toString();
        final SchemaFactory factory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final InputStream schemaInputStream = getFileResourceAsStream(xsdFileName);
        Schema schema = null;
        try {
            schema = factory.newSchema(new StreamSource(schemaInputStream));
        } catch (SAXException e1) {
            logger.error(e1.getMessage());
            throw new JAXBException(e1.getMessage());
        }

        //        JAXBSource source = null;
        //        try {
        //            source = new JAXBSource(jc, nodes);
        //        } catch (JAXBException e1) {
        //            // TODO Auto-generated catch block
        //            e1.printStackTrace();
        //        }

        //Creating a SAXParser for our input XML
        //First the factory
        final SAXParserFactory factory1 = SAXParserFactory.newInstance();
        //Must be namespace aware to receive element names
        factory1.setNamespaceAware(true);
        //Setting the Schema for validation
        factory1.setSchema(schema);
        //Now the parser itself
        SAXParser parser = null;
        try {
            parser = factory1.newSAXParser();
        } catch (ParserConfigurationException | SAXException e2) {
            logger.error(e2.getMessage());
            throw new ParserConfigurationException(e2.getMessage());
        }

        IpSecNodesErrorHandler ipSecNodesErrorHandler = new IpSecNodesErrorHandler();
        try {
            parser.parse(new InputSource(new StringReader(xmlString)), ipSecNodesErrorHandler);
        } catch (SAXException e) {

            elemErrorMap = ipSecNodesErrorHandler.getElementErrorMap();

            //throw new SAXException("Invalid data for " + ipSecNodesErrorHandler.getElement());
        } catch (IOException e) {
            logger.error("Exception while validating: " + e.getMessage());
            throw e;
        }

        //return map of error
        return elemErrorMap;

    }

    /**
     * This method will load the given file and return the InputStream.
     * 
     * @param fileName
     *            : file name
     * @return {@link InputStream} InputStream for given file.
     */
    public InputStream getFileResourceAsStream(final String fileName) {
        final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        logger.debug("Schema file for validation is [{}]", is);
        return is;
    }

}
