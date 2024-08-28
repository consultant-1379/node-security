/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2018
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/

package com.ericsson.oss.services.security.nscs.cucumber.test.steps.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonFileHelper {

    /**
     * Writes to given file name as JSON file.
     *
     * @param fileName
     *            the file name without .json extension
     * @param writeValue
     *            the write value
     */
    public void write(final String fileName, final Object writeValue) {
        File file;
        FileOutputStream outputStream = null;
        final String jsonFileName = System.getenv("DOCKER_DIR") + "/config/" + fileName + ".json";
        try {
            file = new File(jsonFileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            outputStream = new FileOutputStream(file);
            final ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            Assert.assertNotNull("null write value : ", writeValue);
            mapper.writeValue(outputStream, writeValue);
            outputStream.flush();
            outputStream.close();
        } catch (final IOException e) {
            Assert.fail(jsonFileName + ": I/O exception writing the file");
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (final IOException e) {
                Assert.fail(jsonFileName + ": I/O exception closing the output stream.");
            }
        }
    }
}
