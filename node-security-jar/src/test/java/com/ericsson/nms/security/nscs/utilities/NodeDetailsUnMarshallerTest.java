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
package com.ericsson.nms.security.nscs.utilities;

import java.io.*;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.enrollmentinfo.request.NodeDetailsUnMarshaller;
import com.ericsson.nms.security.nscs.enrollmentinfo.request.model.NodeDetailsList;

/**
 * Test Class for NodeDetailsUnMarshellar.
 * 
 * @author tcsviku
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class NodeDetailsUnMarshallerTest {

    @InjectMocks
    NodeDetailsUnMarshaller nodeDetailsUnMarshaller;

    @Mock
    Logger logger;

    final File XML_FILE = new File("src/test/resources/node.xml");
    private String fileData;

    @Test
    public void tesBuildVsecurityConfigurationFromXMLContent() throws IOException {
        String fileData = getFileData(XML_FILE);
        NodeDetailsList nodeDetailsList = nodeDetailsUnMarshaller.buildNodeDetailsFromXmlContent(fileData);
        Assert.assertNotNull(nodeDetailsList);
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
