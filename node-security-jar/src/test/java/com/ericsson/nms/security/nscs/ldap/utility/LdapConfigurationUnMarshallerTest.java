/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.ldap.utility;

import static org.junit.Assert.assertNotNull;

import java.io.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.ldap.entities.LdapConfigurations;

@RunWith(MockitoJUnitRunner.class)
public class LdapConfigurationUnMarshallerTest {

    @InjectMocks
    LdapConfigurationUnMarshaller ldapConfigurationUnMarshaller;

    /**
     * Test to UnMarshall the LdapConfiguration.xml.
     */
    @Test
    public void testBuildLdapConfigurationFromXMLContent() {

        final String ldapConfigFileContent = getLdapConfigFileContent("src/test/resources/ldap/LdapConfiguration.xml");
        LdapConfigurations ldapConfigurations = ldapConfigurationUnMarshaller.buildLdapConfigurationFromXMLContent(ldapConfigFileContent);
        assertNotNull(ldapConfigurations.getList());
    }

    /**
     * @param filePath
     * @return
     */
    private String getLdapConfigFileContent(final String filePath) {
        File file = new File(filePath);
        BufferedReader reader;
        StringBuilder fileContent = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(file));

            String line = null;

            String ls = System.getProperty("line.separator");

            while ((line = reader.readLine()) != null) {
                fileContent.append(line);
                fileContent.append(ls);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileContent.toString();
    }

}
