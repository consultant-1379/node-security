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

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.enrollmentinfo.utility.FileConstants;

/**
 * 
 * This class is used to File download operation.
 */
public class FileUtil {

    // TODO: TORF-175644
    // File path to be used for NSCS downloaded file
    private static final String TEMP_FILE_DOWNLOAD_PATH = "/ericsson/batch/data/export/3gpp_export/"; //NOSONAR
    private static final int TEMP_FILE_DOWNLOAD_COUNT = 8;
    private static final String TEMP_FILE_DOWNLOAD_VALID_PW_CHARS = "abcdefghijklmnopqrstuvwxyz";
    @Inject
    private Logger logger;

    @Inject
    ExportCacheItemsHolder exportCacheItemsHolder;

    /**
     * Save in cache a deletable download file identifier.
     * 
     * @param fileContents
     *            bytes to be written to the download file holder.
     * @param fileName
     *            name of the download file.
     * @param contentType
     *            type of content of download file.
     * @return the deletable download file identifier.
     * @throws IOException
     *             thrown when failure occurs while preparing download file identifier.
     */
    public String createDeletableDownloadFileIdentifier(final byte[] fileContents, final String fileName, final String contentType)
            throws IOException {

        final String fileIdentifier = String.format("%s_%s_%s", TEMP_FILE_DOWNLOAD_PATH,
                System.currentTimeMillis(), generateRandomString());

        final DownloadFileHolder downloadFileHolder = generateDeletableDownloadFileHolder(fileName, contentType, fileContents);

        logger.info("Deletable download file holder of length {} saved in cache with key {}", fileContents.length, fileIdentifier);
        exportCacheItemsHolder.save(fileIdentifier, downloadFileHolder);

        return fileIdentifier;
    }

    /**
     * Method to convert file data to byteArray data
     * 
     * @param file
     *            name of the file to convert from file to byteArray
     * @return byte array
     * @throws IOException
     *             thrown when failure occurs while converting file to byte array.
     */
    public byte[] convertFiletoByteArray(final File file) throws IOException {

        FileInputStream fileInputStream = null;
        byte[] fileInByteArray = new byte[(int) file.length()];

        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(fileInByteArray);
            fileInputStream.close();
        } catch (Exception exception) {
            logger.error("Error Occured while Converting File to byteArray{} ", exception.getMessage());
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
        return fileInByteArray;
    }

    /**
     * Method for creating the archive and zip the file.
     * 
     * @param nodeByteArrayOutputStream
     *            each node byte array output stream to put into zip file
     * @param zipFilePathName
     *            name of the zip file path
     * @return byte [] return the all nodes byte array object to download the file
     * @throws IOException
     *             thrown when failure occurs while converting file to byte array.
     */
    public byte[] getArchiveFileBytes(final Map<String, ByteArrayOutputStream> nodeByteArrayOutputStream, final String zipFilePathName) throws IOException {
        logger.info("File Archiving process has been started");
        final String zipFilePath = FileConstants.TMP_DIR + FileConstants.FILE_SEPARATOR + zipFilePathName + System.currentTimeMillis() + FileConstants.TAR_GZ_EXTENSION;

        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        GzipCompressorOutputStream gzipCompressorOutputStream = null;
        TarArchiveOutputStream tarArchiveOutputStream = null;
        ByteArrayInputStream byteArrayInputStream = null;
        File tarFile = null;
        try {
            tarFile = new File(zipFilePath);
            fileOutputStream = new FileOutputStream(tarFile);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            gzipCompressorOutputStream = new GzipCompressorOutputStream(bufferedOutputStream);
            tarArchiveOutputStream = new TarArchiveOutputStream(gzipCompressorOutputStream);
            final Set<Entry<String, ByteArrayOutputStream>> entrySet = nodeByteArrayOutputStream.entrySet();
            for (final java.util.Map.Entry<String, ByteArrayOutputStream> entry : entrySet) {
                TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(entry.getKey());
                byte[] byteArrayData = entry.getValue().toByteArray();
                tarArchiveEntry.setSize(byteArrayData.length);
                tarArchiveOutputStream.putArchiveEntry(tarArchiveEntry);
                byteArrayInputStream = new ByteArrayInputStream(byteArrayData);
                IOUtils.copy(byteArrayInputStream, tarArchiveOutputStream);
                byteArrayInputStream.close();
                tarArchiveOutputStream.closeArchiveEntry();
            }
        } finally {
            if (byteArrayInputStream != null) {
                byteArrayInputStream.close();
            }
            if (tarArchiveOutputStream != null) {
                tarArchiveOutputStream.close();
            }
            if (gzipCompressorOutputStream != null) {
                gzipCompressorOutputStream.close();
            }
            if (bufferedOutputStream != null) {
                bufferedOutputStream.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
        logger.info("File Archiving is completed");
        return convertFiletoByteArray(tarFile);
    }

    /**
     * Create a deletable download file holder.
     * 
     * @param fileContents
     *            bytes to be written to the download file holder.
     * @param fileName
     *            name of the download file.
     * @param contentType
     *            type of content of download file.
     * @return the deletable download file holder.
     */
    private DownloadFileHolder generateDeletableDownloadFileHolder(final String fileName, final String contentType, final byte[] fileContents) {

        final DownloadFileHolder downloadFileHolder = new DownloadFileHolder();
        downloadFileHolder.setFileName(fileName);
        downloadFileHolder.setContentType(contentType);
        downloadFileHolder.setContentToBeDownloaded(fileContents);
        downloadFileHolder.setDeletable(true);
        return downloadFileHolder;
    }

    /**
     * This method is to validate the file extension type.
     *
     * @param fileName
     *            name of the input file
     * @param allowedExtensionTypes
     *            allowed file extension types, Multiple file extension types can be passed by separating with "|"  Ex : txt|csv|xml
     * @return {@link Boolean}
     *         <p>
     *         true: if input file extension is in allowedExtensionTypes
     *         </p>
     *         false: if input file extension is not in allowedExtensionTypes
     */
    public boolean isValidFileExtension(final String fileName, final String allowedExtensionTypes) {
        final String regex = "([^\\s]+(\\.(?i)(" + allowedExtensionTypes + "))$)";
        final Pattern fileExtnPtrn = Pattern.compile(String.format(regex));
        final Matcher mtch = fileExtnPtrn.matcher(fileName);
        if (mtch.matches()) {
            return true;
        }
        return false;
    }

    private String generateRandomString() {
        SecureRandom random = new SecureRandom();

        StringBuilder sb = new StringBuilder(TEMP_FILE_DOWNLOAD_COUNT);
        for (int i = 0; i < TEMP_FILE_DOWNLOAD_COUNT; i++) {
            int rndCharAt = random.nextInt(TEMP_FILE_DOWNLOAD_VALID_PW_CHARS.length());
            char rndChar = TEMP_FILE_DOWNLOAD_VALID_PW_CHARS.charAt(rndCharAt);
            sb.append(rndChar);
        }
        return sb.toString();
    }

}
