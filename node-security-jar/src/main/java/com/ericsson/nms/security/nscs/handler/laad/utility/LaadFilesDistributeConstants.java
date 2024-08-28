/*------------------------------------------------------------------------------
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
package com.ericsson.nms.security.nscs.handler.laad.utility;

/**
 * This class hold the constant values related to LAAD Files Distribute handler
 *
 * @author tcsgoja
 */
public class LaadFilesDistributeConstants {

    private LaadFilesDistributeConstants() {
        super();
    }

    public static final String LAAD_DISTRIBUTE_EXECUTED = "Successfully started a job to distribute LAAD files to node(s)";
    public static final String LAAD_DISTRIBUTE_PARTIALLY_FAILED = "Successfully started a job to distribute LAAD files to some nodes.";
    public static final String LAAD_DISTRIBUTE_PARTIALLY_EXECUTED = LAAD_DISTRIBUTE_PARTIALLY_FAILED
            + " Perform 'secadm job get -j %s' to get progress information."
            + " Error details are listed below for other nodes:";
    public static final String LAAD_DISTRIBUTE_NOT_EXECUTED = "LAAD distribute command has not been executed for all nodes. Error details are listed below:";
    public static final int NO_OF_COLUMNS = 3;
    public static final String LAAD_DISTRIBUTE_NODE_TYPE_SUGGESTED_SOLUTION = "Check online help for supported node type(s) to distribute LAAD files.";
    public static final String CERT_FILENAME_EXTENSION = ".der";
    public static final String AUTHENTICATION_FILE = "authentication" + LaadFilesDistributeConstants.CERT_FILENAME_EXTENSION;
    public static final String AUTHORIZATION_FILE = "authorization" + LaadFilesDistributeConstants.CERT_FILENAME_EXTENSION;
    public static final String GET_AND_UPLOADING_LAAD_FILES_TO_SMRS = "Get and Uploading LAAD Files to SMRS";
    public static final String UPLOADING_LAAD_FILES_TO_SMRS = "Uploading LAAD Files to SMRS";
    public static final String INSTALLING_LAAD_FILES_ON_NODE = "Installing LAAD Files on Node: ";
}
