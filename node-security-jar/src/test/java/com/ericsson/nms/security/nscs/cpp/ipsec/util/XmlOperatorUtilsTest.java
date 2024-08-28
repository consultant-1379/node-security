package com.ericsson.nms.security.nscs.cpp.ipsec.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class XmlOperatorUtilsTest {

    @InjectMocks
    private XmlOperatorUtils xmlOperatorUtils;



    @Test
    public void testGetSummaryFileContent() throws Exception {
        String settingXml = readFileToString("src/test/resources/IpForOamSettingFile.xml");

        XmlOperatorUtils.SummaryXmlInfo sumXmlInfo = xmlOperatorUtils.getSummaryFileContent(settingXml, "IpForOamSettingFile.xml", "SHA");
        Assert.assertNotNull("Empty Content", sumXmlInfo.getContent());
        Assert.assertNotNull("Empty Hash value", sumXmlInfo.getHash());
    }

    @Test
    public void testByteArrayToString() throws Exception {
        String inputStr = "ericsson";
        String str = XmlOperatorUtils.byteArrayToString(inputStr.getBytes(), false);
        Assert.assertNotNull("Null String returned", str);
        Assert.assertFalse("Empty String returned", str.isEmpty());
    }

    private String readFileToString(String filePath){
        BufferedReader br = null;
        String fileContent = "";
        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader(filePath));
            while ((sCurrentLine = br.readLine()) != null) {
                fileContent += sCurrentLine;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return fileContent;
    }


}