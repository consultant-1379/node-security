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
package com.ericsson.nms.security.nscs.integration.jee.test.utils;

import java.io.*;
import java.net.URLDecoder;

/**
 * This class will provide the utility method for file operations
 *
 */
public class FileUtility {

    /**
     * This method will get the target path for the current package.
     * 
     * @return target path
     * @throws UnsupportedEncodingException
     *             is thrown when error occurs while getting the target path.
     */
    public String getTargetPath() throws UnsupportedEncodingException {
        String responsePath = "";
        String path = this.getClass().getClassLoader().getResource("").getPath();
        String fullPath = URLDecoder.decode(path, "UTF-8");
        String pathArr[] = fullPath.split("/target");
        fullPath = pathArr[0];
        if (!File.separator.equalsIgnoreCase("/")) {
            responsePath = "\\";
        } else {
            responsePath = "";
        }
        responsePath = responsePath + new File(fullPath).getPath() + File.separator + "target" + File.separator + "test-classes";
        return responsePath;
    }

    public InputStream getResourceInputStream(String resourceName) throws UnsupportedEncodingException, IOException {
        return getClass().getClassLoader().getResourceAsStream(resourceName);
    }

    public byte[] readResourceFile(String resourceName) throws UnsupportedEncodingException, IOException {
        byte[] resourceContent = null;
        InputStream inps = getResourceInputStream(resourceName);
        if (inps == null) {
            return resourceContent;
        }
        if (inps.available() > 0) {
            resourceContent = new byte[inps.available()];
            inps.read(resourceContent);
        }
        return resourceContent;
    }

    /**
     * This method will covert the file content into bytes and returns the byte array.
     * 
     * @param filePath
     *            Path of the file.
     * @return byte array
     * @throws IOException
     *             is thrown when any error occurs while reading file content.
     */
    public byte[] fileToBytes(final String filePath) throws IOException {

        FileInputStream fileInputStream = null;

        File file = new File(filePath);

        byte[] bFile = new byte[(int) file.length()];

        fileInputStream = new FileInputStream(file);
        fileInputStream.read(bFile);
        fileInputStream.close();
        return bFile;
    }
}
