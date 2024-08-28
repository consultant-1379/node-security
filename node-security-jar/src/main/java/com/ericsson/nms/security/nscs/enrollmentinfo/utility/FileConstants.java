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
package com.ericsson.nms.security.nscs.enrollmentinfo.utility;

/**
 * 
 * This class holds the constant values related to the File handling
 */
public class FileConstants {

    public static final String XML_CONTENT_TYPE = "application/xml";
    public static final String GZIP_CONTENT_TYPE = "application/gzip";
    public static final String FILE_URI = "file:";
    public final static String TMP_DIR = "/ericsson/batch/data/export/3gpp_export";
    public final static String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final String TAR_GZ_EXTENSION = ".tar.gz";
    public static final String XML_EXTENSION = ".xml";
    public static final String GZ_EXTENSION = ".gz";
}
