/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.util;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.command.types.EnrollmentInfoFileCommand;
import com.ericsson.nms.security.nscs.api.command.types.LdapProxyGetCommand;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidFileContentException;

/**
 * Test Class for CliUtil.
 * 
 * @author tcsviku
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CliUtilTest {

    @InjectMocks
    CliUtil cliUtil;

    @Mock
    Logger logger;

    private final String propertyKey = "propertyKey";
    private final String propKey = "propKey";
    private byte[] fileContents;
    private String fileData;
    private final File XML_FILE = new File("src/test/resources/node.xml");
    private NscsNodeCommand command = new EnrollmentInfoFileCommand();
    private NscsPropertyCommand propertyCommand = new LdapProxyGetCommand();
    private Map<String, Object> properties = new HashMap<String, Object>();

    @Before
    public void setup() throws IOException {
        fileData = getFileData(XML_FILE);
        fileContents = fileData.getBytes();
        properties.put("propertyKey", fileContents);
        command.setProperties(properties);
        propertyCommand.setProperties(properties);

    }

    @Test
    public void testGetCommandInputData() {
        final String fileDataactual = cliUtil.getCommandInputData(command, propertyKey);
        assertEquals(fileData, fileDataactual);
    }

    @Test(expected = InvalidFileContentException.class)
    public void testGetCommandInputData_Exception() {
        cliUtil.getCommandInputData(command, propKey);
    }

    @Test
    public void testGetCommandInputData_PropertyCommand() {
        final String fileDataactual = cliUtil.getCommandInputData(propertyCommand, propertyKey);
        assertEquals(fileData, fileDataactual);
    }

    @Test(expected = InvalidFileContentException.class)
    public void testGetCommandInputData_PropertyCommand_Exception() {
        cliUtil.getCommandInputData(propertyCommand, propKey);
    }

    @Test
    public void testGetCommandInputDataWithNewExceptionHandling() {
        final String fileDataactual = cliUtil.getCommandInputDataWithNewExceptionHandling(command, propertyKey);
        assertEquals(fileData, fileDataactual);
    }

    @Test(expected = InvalidFileContentException.class)
    public void testGetCommandInputDataWithNewExceptionHandling_Exception() {
        cliUtil.getCommandInputDataWithNewExceptionHandling(command, propKey);
    }

    @Test
    public void testGetCommandInputDataWithNewExceptionHandling_PropertyCommand() {
        final String fileDataactual = cliUtil.getCommandInputDataWithNewExceptionHandling(propertyCommand, propertyKey);
        assertEquals(fileData, fileDataactual);
    }

    @Test(expected = InvalidFileContentException.class)
    public void testGetCommandInputDataWithNewExceptionHandling_PropertyCommand_Exception() {
        cliUtil.getCommandInputDataWithNewExceptionHandling(propertyCommand, propKey);
    }

    private String getFileData(File xmlFile) throws IOException {
        Reader fileReader = new FileReader(xmlFile);
        @SuppressWarnings("resource")
        BufferedReader bufReader = new BufferedReader(fileReader);

        StringBuilder sb = new StringBuilder();
        String line = bufReader.readLine();
        while (line != null) {
            sb.append(line).append("\n");
            line = bufReader.readLine();
        }
        return sb.toString();
    }
}
