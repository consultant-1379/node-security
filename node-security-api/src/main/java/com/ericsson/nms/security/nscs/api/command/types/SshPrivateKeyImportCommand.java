/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.command.types;

/**
 * SshPrivateKeyUpdate command class for enmSshPrivateKey attribute update.
 *
 * @author zkttmnk
 */

public class SshPrivateKeyImportCommand extends NscsNodeCommand {

    private static final long serialVersionUID = -5267972378522721946L;

    public static final String SSH_PRIVATE_KEY_FILE_PROPERTY = "sshprivatekeyfile";
    public static final String NODE_NAME = "nodename";
    public static final String FILE_NAME = "fileName";

    /**
     * @return the fileName property value
     */
    public String getFileName() {
        return getValueString(FILE_NAME);
    }

    /**
     * @return String returns sshprivatekeyfile property value
     */
    public String getSshPrivateKeyFile() {
        return getValueString(SSH_PRIVATE_KEY_FILE_PROPERTY);
    }

    /**
     * @return the nodename property value
     */
    public String getNodeName() {
        return getValueString(NODE_NAME);
    }

}
