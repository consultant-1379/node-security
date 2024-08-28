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
 *---------------------------------------------------------------------------- */
package com.ericsson.nms.security.nscs.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.types.EnrollmentInfoFileCommand;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;

/**
 * Test Class for FileDownloadUtil.
 * 
 * @author tcsviku
 * 
 */

@RunWith(MockitoJUnitRunner.class)
public class FileUtilTest {

    @InjectMocks
    FileUtil fileUtil;

    @Mock
    Logger logger;

    @Mock
    ExportCacheItemsHolder exportCacheItemsHolder;

    @Mock
    DownloadFileHolder downloadFileHolder;

    private byte[] fileContents;
    private String fileData;
    private final File XML_FILE = new File("src/test/resources/node.xml");
    private NscsNodeCommand command = new EnrollmentInfoFileCommand();
    private Map<String, Object> properties = new HashMap<String, Object>();
    private static final String FILENAME = "fileName";
    private static final String CONTENTTYPE = "contentType";
    private static final String TEMP_FILE_DOWNLOAD_PATH = "/ericsson/batch/data/export/3gpp_export/";

    @Before
    public void setup() throws IOException {
        fileData = getFileData(XML_FILE);
        fileContents = fileData.getBytes();
        properties.put("propertyKey", fileContents);
        command.setProperties(properties);

    }

    @Test
    public void testcreateDeletableDownloadFileIdentifier() {
        try {
            Mockito.doNothing().when(exportCacheItemsHolder).save(FILENAME, downloadFileHolder);
            final String fileIdentifier = fileUtil.createDeletableDownloadFileIdentifier(fileContents, FILENAME, CONTENTTYPE);
            Assert.assertTrue(fileIdentifier.contains(TEMP_FILE_DOWNLOAD_PATH));
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testConvertFiletoByteArray() throws IOException {

        File file = new File("/src/test/resources/testFileAll.txt");
        byte[] expectedByteArray = fileUtil.convertFiletoByteArray(file);
        Assert.assertNotNull(expectedByteArray);
    }

    private String getFileData(File xmlFile) throws IOException {
        Reader fileReader = new FileReader(xmlFile);
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