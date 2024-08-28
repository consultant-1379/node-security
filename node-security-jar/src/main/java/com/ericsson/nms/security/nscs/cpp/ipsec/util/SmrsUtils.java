package com.ericsson.nms.security.nscs.cpp.ipsec.util;

import com.ericsson.nms.security.nscs.api.resources.FileResources;
import com.ericsson.nms.security.nscs.cpp.model.SmrsAccountInfo;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.resources.Resources;

/**
 * SMRS Utils.
 *
 * @author ediniku
 */
public class SmrsUtils {
	
	@EServiceRef
    private FileResources fileResources;

    /**
     * Utility to upload file to SMRS
     *
     * @param smrsAccountInfo
     *            SMRS account information.
     * @param fileName
     *            file name.
     * @param content
     *            content of the file that will be uploaded to SMRS.
     * @return URI where the file was placed inside SMRS.
     */
    public String uploadFileToSmrs(final SmrsAccountInfo smrsAccountInfo, final String fileName, final byte[] content) {
        final String filePath = smrsAccountInfo.getSmrsDir()  + fileName;
        if (!fileResources.existsFileSystemResource(filePath))
            fileResources.writeFileSystemResource(filePath, content);
        final String settingUri = smrsAccountInfo.prepareUri(fileName);
        return settingUri;
    }
    
    public String uploadSummaryFileToSmrs(final SmrsAccountInfo smrsAccountInfo, final String fileName, final byte[] content) {
        final String filePath = smrsAccountInfo.getSmrsDir()  + fileName;
        fileResources.writeFileSystemResource(filePath, content);
        final String settingUri = smrsAccountInfo.prepareUri(fileName);
        return settingUri;
    }

    /**
     * Utility to delete files from SMRS Server
     *
     * @param smrsAccountInfo
     *            SMRS account info.
     * @param fileName
     *            file name.
     * @return <code>true</code> if file was deleted. <code>false</code> otherwise.
     */
    public boolean deleteFilesFromSmrs(final SmrsAccountInfo smrsAccountInfo, final String fileName) {
        return Resources.getFileSystemResource(smrsAccountInfo.getSmrsDir() +  fileName).delete();
    }
}