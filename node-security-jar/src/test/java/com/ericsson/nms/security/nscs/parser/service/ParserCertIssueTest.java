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

package com.ericsson.nms.security.nscs.parser.service;


import com.ericsson.nms.security.nscs.api.command.NscsCliCommand;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;
import com.ericsson.nms.security.nscs.api.exception.InvalidFileContentException;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.doReturn;

/**
 * 
 * Test class to validate IpSec related commands
 * 
 * @author emehsau
 *
 */

public class ParserCertIssueTest extends AbstractParserTest {

    private final Logger log = LoggerFactory.getLogger(ParserCertIssueTest.class);

    private static final byte[] EMPTY_BYTE_ARRAY = "".getBytes(Charset.forName("UTF-8"));

    private final byte[] XML_FILE_DATA = convertFileToByteArray("src/test/resources/SampleInputFileForCertificateIssue.xml");

    private final Map<String,Object> properties = new HashMap<>();


    /**
     * Positive Test with 
     * extended CertType: --certtype
     * extended Xml: --xmlfile
     * CertType value: IPSEC
     */
    @Test
    public void testCertIssueCommandWithXMLFile__extendedCertType__extendedXml__IPSEC() {
        //doReturn(null).when(fileCommandParserInstance).get();
        properties.put("file:", XML_FILE_DATA);
        final NscsCliCommand command = new NscsCliCommand("certificate issue --certtype IPSEC --xmlfile file:abc.xml", properties );
        parse(command);
    }
    
    /**
     * Positive Test with 
     * extended CertType: --certtype
     * extended Xml: --xmlfile 
     * CertType value: OAM
     */
    @Test
    public void testCertIssueCommandWithXMLFile__extendedCertType__extendedXml__OAM() {
        //doReturn(null).when(fileCommandParserInstance).get();
        properties.put("file:", XML_FILE_DATA);
        final NscsCliCommand command = new NscsCliCommand("certificate issue --certtype OAM --xmlfile file:abc.xml", properties );
        parse(command);
    }
    
    /**
     * Positive Test with 
     * shorten CertType: -ct
     * extended Xml: --xmlfile 
     */
    @Test
    public void testCertIssueCommandWithXMLFile__shortenCertType__extendedXml() {
    	//doReturn(null).when(fileCommandParserInstance).get();
    	properties.put("file:", XML_FILE_DATA);
    	final NscsCliCommand command = new NscsCliCommand("certificate issue -ct IPSEC --xmlfile file:abc.xml", properties );
    	parse(command);
    }
    
    /**
     * Positive Test with 
     * extended CertType: --certtype
     * shorten Xml: -xf
     */
    @Test
    public void testCertIssueCommandWithXMLFile__extendedCertType__shortenXml() {
    	//doReturn(null).when(fileCommandParserInstance).get();
    	properties.put("file:", XML_FILE_DATA);
    	final NscsCliCommand command = new NscsCliCommand("certificate issue --certtype IPSEC -xf file:abc.xml", properties );
    	parse(command);
    }

    /**
     * Positive Test with 
     * shorten CertType: -ct
     * shorten Xml: -xf
     */
    @Test
    public void testCertIssueCommandWithXMLFile__shortenCertType__shortenXml() {
    	//doReturn(null).when(fileCommandParserInstance).get();
    	properties.put("file:", XML_FILE_DATA);
    	final NscsCliCommand command = new NscsCliCommand("certificate issue -ct IPSEC -xf file:abc.xml", properties );
    	parse(command);
    }
    
    /**
     * Negative Test with 
     * invalid CertType: --certype
     */
    @Test(expected = CommandSyntaxException.class)
    public void testCertIssueCommandWithXMLFile__InvalidCertype() {
        //doReturn(null).when(fileCommandParserInstance).get();
        properties.put("file:", XML_FILE_DATA);
        final NscsCliCommand command = new NscsCliCommand("certificate issue --certype IPSEC --xmlfile file:abc.xml", properties );
        parse(command);
    }
    
    /**
     * Negative Test with 
     * invalid CertTypeValue: OEM . OEM is an invalid value but test does not fail. Values checks are made by command validators
     * and not by parser validator.
     */
    //@Test(expected = CommandSyntaxException.class)
    @Test
    public void testCertIssueCommandWithXMLFile__InvalidCertTypeValue() {
        //doReturn(null).when(fileCommandParserInstance).get();
        properties.put("file:", XML_FILE_DATA);
        final NscsCliCommand command = new NscsCliCommand("certificate issue --certtype OEM --xmlfile file:abc.xml", properties );
        parse(command);
    }
    
    /**
     * Negative Test with 
     * invalid shorten CertType: --ct
     */
    @Test(expected = CommandSyntaxException.class)
    public void testCertIssueCommandWithXMLFile__InvalidShortenParam() {
        //doReturn(null).when(fileCommandParserInstance).get();
        properties.put("file:", XML_FILE_DATA);
        final NscsCliCommand command = new NscsCliCommand("certificate issue --ct IPSEC --xmlfile file:abc.xml", properties );
        parse(command);
    }
    
    /**
     * Negative Test with 
     * invalid XmlFile: --file
     */
    @Test(expected = CommandSyntaxException.class)
    public void testCertIssueCommandWithXMLFile__InvalidFileParam() {
        //doReturn(null).when(fileCommandParserInstance).get();
        properties.put("file:", XML_FILE_DATA);
        final NscsCliCommand command = new NscsCliCommand("certificate issue -ct IPSEC --file file:abc.xml", properties );
        parse(command);
    }
    
    /**
     * Negative Test with 
     * invalid XmlFile param: xmlfile
     */
    @Test(expected = CommandSyntaxException.class)
    public void testCertIssueCommandWithXMLFile__InvalidXMLFileParam() {
        //doReturn(null).when(fileCommandParserInstance).get();
        properties.put("file:", XML_FILE_DATA);
        final NscsCliCommand command = new NscsCliCommand("certificate issue -ct IPSEC --xmlfile xmlfile:abc.xml", properties );
        parse(command);
    }
    
    /**
     * Negative Test with 
     * missing param: --certtype
     */
    @Test(expected = CommandSyntaxException.class)
    public void testCertIssueCommandWithXMLFile__MissingParam() {
        //doReturn(null).when(fileCommandParserInstance).get();
        properties.put("file:", XML_FILE_DATA);
        final NscsCliCommand command = new NscsCliCommand("certificate issue --xmlfile file:abc.xml", properties );
        parse(command);
    }
    
    /**
     * Negative Test with 
     * missing param: --xmlfile
     */
    @Test(expected = CommandSyntaxException.class)
    public void testCertIssueCommandWithXMLFile__MissingXmlFile() {
        //doReturn(null).when(fileCommandParserInstance).get();
        properties.put("file:", XML_FILE_DATA);
        final NscsCliCommand command = new NscsCliCommand("certificate issue -ct IPSEC", properties );
        parse(command);
    }
    
    /**
     * Negative Test with 
     * reverse order
     */
    @Test(expected = CommandSyntaxException.class)
    public void testCertIssueCommandWithXMLFile__ReverseOrder() {
        //doReturn(null).when(fileCommandParserInstance).get();
        properties.put("file:", XML_FILE_DATA);
        final NscsCliCommand command = new NscsCliCommand("certificate issue --xmlfile file:abc.xml --certtype IPSEC", properties );
        parse(command);
    }
 
    
    private byte[] convertFileToByteArray(final String fileLocation) {
        final File file = new File(fileLocation);
        FileInputStream fileInputStream=null;

        final byte[] fileToBeParsed = new byte[(int) file.length()];

        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(fileToBeParsed);
            fileInputStream.close();
        }catch(Exception e){
            log.error("File passed in was empty");
            e.printStackTrace();
        }
        return fileToBeParsed;
    }
}
