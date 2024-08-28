package com.ericsson.nms.security.nscs.cpp.model;

import java.io.File;

/**
 * Contains SMRS related account information.
 */
public class SmrsAccountInfo extends AccountInfo {

    private static final long serialVersionUID = 1L;

    private final String smrsDir;
    
    private final String smrsRelativePath;
    
   
    public SmrsAccountInfo(final String userName, final char[] password, final String host, 
            final String smrsDir, final String smrsRelPath) {
        super(userName, password, host);
        this.smrsDir = smrsDir;
        if ((smrsRelPath != null) && (!smrsRelPath.endsWith(File.separator)))
            this.smrsRelativePath = smrsRelPath + File.separator;
        else
            this.smrsRelativePath = smrsRelPath;
    }

    /**
     * Gets SMRS directory path.
     * 
     * @return SMRS directory path.
     */
    public String getSmrsDir() {
        return smrsDir;
    }

    public String getSmrsRelativePath() {
        return smrsRelativePath;
    }

    /**
     * Prepare URI for the file placed on SMRS server
     * 
     * @param fileName
     *            file name.
     * @return URI.
     */
    public String prepareUri(final String fileName) {
        return "sftp://" + this.getUserName() + ":" + new String(this.getPassword()) + "@" + this.getHost() + "/"+ this.getSmrsRelativePath() + fileName;
    }
    
    //TODO Remove this workaround once correct SFTP M2M directory for node is available from SmrsService API
//    private String getNormalizedSmrsPath(final String path){
//    	String baseSmrsPath = getParentDirPath(this.smrsRootDir); 
//    	if(path.startsWith(baseSmrsPath)){
//    		return path.replace(baseSmrsPath, "");
//    	}
//    	return path;
//    }
    
    //TODO Remove this workaround once correct SFTP M2M directory for node is available from SmrsService API
//    private String getParentDirPath(final String path) {
//        final boolean endsWithSlash = path.endsWith(File.separator);
//        return path.substring(0, path.lastIndexOf(File.separatorChar, endsWithSlash ? path.length() - 2 : path.length() - 1));
//    }

}
