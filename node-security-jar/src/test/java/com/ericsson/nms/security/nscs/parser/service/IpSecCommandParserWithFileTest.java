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

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.spi.Bean;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * 
 * Test class to validate IpSec related commands
 * 
 * @author emehsau
 *
 */
@Ignore("TORF-38537 - not supported in 14B")
public class IpSecCommandParserWithFileTest extends AbstractParserTest {

    private final Logger log = LoggerFactory.getLogger(IpSecCommandParserWithFileTest.class);;

    private static final byte[] EMPTY_BYTE_ARRAY = "".getBytes(Charset.forName("UTF-8"));

    private final byte[] FILE_WITH_SPACES = convertFileToByteArray("src/test/resources/testFileSpaces.txt");
    private final byte[] FILE_WITH_TABS = convertFileToByteArray("src/test/resources/testFileTabs.txt");
    private final byte[] FILE_WITH_SEMI_COLONS = convertFileToByteArray("src/test/resources/testFileSemiColons.txt");
    private final byte[] FILE_WITH_NEW_LINE = convertFileToByteArray("src/test/resources/testFileNewLine.txt");
    private final byte[] EMPTY_FILE = convertFileToByteArray("src/test/resources/emptyFile.txt");
    private final byte[] XML_FILE_DATA = convertFileToByteArray("src/test/resources/SampleInputFile.xml");

    private final Map<String,Object> properties = new HashMap<>();
    private final List<String> expectedNodesFromFile = Arrays.asList("node1","node2","node3","node4","node5","MeContext=node6","NetworkElement=node7");

    @Test
    public void testEmptyFileSupplied() {
    	properties.put("file:", EMPTY_BYTE_ARRAY);
        parseCommandAndAssertFail("ipsec --status -nf file:abc.txt");
    }

    @Test
    public void testCommandWithFileParserNotNull() {
        properties.put("file:", FILE_WITH_SPACES);
        final NscsCliCommand command = new NscsCliCommand("ipsec --status --configuration -nf file:abc.txt", properties);
        parse(command);
    }
    
    @Test
    public void testStatusCommandWithValidOption() {
        when(beanManager.getBeans(any(Type.class), any(Annotation.class))).thenReturn(new HashSet<Bean<?>>());
        properties.put("file:", FILE_WITH_SPACES);
        final NscsCliCommand command = new NscsCliCommand("ipsec -s -cf -nf file:abc.txt", properties);
        parse(command);
    }

    @Test
    public void testIPSecCommandWithContinueOptionWithXMLFile() {
        when(beanManager.getBeans(any(Type.class), any(Annotation.class))).thenReturn(new HashSet<Bean<?>>());
        properties.put("file:", XML_FILE_DATA);
        final NscsCliCommand command = new NscsCliCommand("ipsec --continue --xmlfile file:abc.xml", properties);
        parse(command);
    }

    @Test
    public void testIPSecCommandWithoutOptionWithXMLFile() {
        when(beanManager.getBeans(any(Type.class), any(Annotation.class))).thenReturn(new HashSet<Bean<?>>());
        properties.put("file:", XML_FILE_DATA);
        final NscsCliCommand command = new NscsCliCommand("ipsec --xmlfile file:abc.xml", properties );
        parse(command);
    }

    @Test
    public void testIPSecCommandWithoutOption() {
        when(beanManager.getBeans(any(Type.class), any(Annotation.class))).thenReturn(new HashSet<Bean<?>>());
        properties.put("file:", XML_FILE_DATA);
        final NscsCliCommand command = new NscsCliCommand("ipsec -xf file:abc.xml", properties);
        parse(command);
    }

    @Test
    public void testStatusCommandForXMLInput() {
        when(beanManager.getBeans(any(Type.class), any(Annotation.class))).thenReturn(new HashSet<Bean<?>>());
        properties.put("file:", XML_FILE_DATA);
        final NscsCliCommand command = new NscsCliCommand("ipsec -s -xf file:abc.xml", properties);
        parseCommandAndAssertFail(command);
    }

    @Test
    public void testStatusCommandWithContinueOption() {
        when(beanManager.getBeans(any(Type.class), any(Annotation.class))).thenReturn(new HashSet<Bean<?>>());
        properties.put("file:", XML_FILE_DATA);
        final NscsCliCommand command = new NscsCliCommand("ipsec -s -c -nf file:abc.xml", properties);
        parseCommandAndAssertFail(command);
    }
    
    
    @Test
    public void testStatusCommandWithInvalidOption() {
        when(beanManager.getBeans(any(Type.class), any(Annotation.class))).thenReturn(new HashSet<Bean<?>>());
        properties.put("file:", XML_FILE_DATA);
        final NscsCliCommand command = new NscsCliCommand("ipsec -s -cf -c -nf file:abc.xml", properties);
        parseCommandAndAssertFail(command);
    }
    
    @Test
    public void testStatusCommandWithInvalidOption2() {
        when(beanManager.getBeans(any(Type.class), any(Annotation.class))).thenReturn(new HashSet<Bean<?>>());
        properties.put("file:", XML_FILE_DATA);
        final NscsCliCommand command = new NscsCliCommand("ipsec -cf -nf file:abc.xml", properties);
        parseCommandAndAssertFail(command);
    }

    @Test
    public void testValidStatusCommandWithXMLFile() {
        when(beanManager.getBeans(any(Type.class), any(Annotation.class))).thenReturn(new HashSet<Bean<?>>());
        properties.put("file:", FILE_WITH_SPACES);
        final NscsCliCommand command = new NscsCliCommand("ipsec -s -c -xf file:abc.xml", properties);
        parseCommandAndAssertFail(command);
    }
    
    @Test
    public void testInvalidStatusCommandWithXMLFile() {
        when(beanManager.getBeans(any(Type.class), any(Annotation.class))).thenReturn(new HashSet<Bean<?>>());
        properties.put("file:", FILE_WITH_SPACES);
        final NscsCliCommand command = new NscsCliCommand("ipsec -st -c -xf file:abc.xml", properties);
        parseCommandAndAssertFail(command);
    }

    @Test
    public void testCommandWithFileParserFileWithTabs() {
        properties.put("file:", FILE_WITH_TABS);
        final NscsCliCommand command = new NscsCliCommand("ipsec --status --configuration --nodefile file:abc.txt", properties);
        parseCommandAndAssertSuccess(command, expectedNodesFromFile);
    }

    @Test
    public void testInvalidCommand() {
        properties.put("file:", FILE_WITH_SEMI_COLONS);
        final NscsCliCommand command = new NscsCliCommand("ipsec --status --traffic -nf file:abc.txt", properties);
        parseCommandAndAssertFail(command);
    }

    @Test
    public void testStatusCommandWithNewLineSeparatedData() {
        properties.put("file:", FILE_WITH_NEW_LINE);
        final NscsCliCommand command = new NscsCliCommand("ipsec --status -nf file:abc.txt", properties);
        parseCommandAndAssertSuccess(command, expectedNodesFromFile);
    }

    @Test
    public void testStatusCommandWithoutConfiguration() {
        properties.put("file:", FILE_WITH_NEW_LINE);
        final NscsCliCommand command = new NscsCliCommand("ipsec --status -nf file:abc.txt", properties);
        parse(command);
    }

    
    @Test
    public void testStatusCommandWithoutConfigurationShortOption() {
        properties.put("file:", FILE_WITH_NEW_LINE);
        final NscsCliCommand command = new NscsCliCommand("ipsec -s -nf file:abc.txt", properties);
        parse(command);
    }
    @Test
    public void testEmptyFileForIPSec() {
        when(beanManager.getBeans(any(Type.class), any(Annotation.class))).thenReturn(new HashSet<Bean<?>>());
        properties.put("file:", FILE_WITH_NEW_LINE);
        final NscsCliCommand command = new NscsCliCommand("ipsec -c -xf file:abc.xml", properties);
        parse(command);
    }

    @Test
    public void testFileWithRubbish() {
        properties.put("file:", EMPTY_FILE);
        final NscsCliCommand command = new NscsCliCommand("ipsec --status -nf file:.txt", properties);
        parseCommandAndAssertFail(command);
    }
    
    @Test
    public void testValidSyntaxForStatus1() {
        properties.put("file:", FILE_WITH_NEW_LINE);
        final NscsCliCommand command = new NscsCliCommand("ipsec --status --configuration -nf file:abc.txt", properties);
        parseCommandAndAssertSuccess(command, expectedNodesFromFile);
    }
    
    
    @Test
    public void testInValidSyntaxForIpSec() {
        properties.put("file:", FILE_WITH_NEW_LINE);
        final NscsCliCommand command = new NscsCliCommand("ipsec --status --configuration --continue -nf file:abc.txt", properties);
        parseCommandAndAssertFail(command);
    }
    
    @Test
    public void testInValidSyntaxForIpSec1() {
        properties.put("file:", FILE_WITH_NEW_LINE);
        final NscsCliCommand command = new NscsCliCommand("ipsec --continue --configuration -nf file:abc.txt", properties);
        parseCommandAndAssertFail(command);
    }
    
    @Test
    public void testValidSyntaxForStatus2() {
        properties.put("file:", FILE_WITH_NEW_LINE);
        final NscsCliCommand command = new NscsCliCommand("ipsec --configuration --status -nf file:abc.txt", properties);
        parseCommandAndAssertFail(command);
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
