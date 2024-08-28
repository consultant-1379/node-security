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
package com.ericsson.nms.security.nscs.api.command.types;

public class GetNodeSpecificPasswordCommand extends NscsNodeCommand {

    private static final long serialVersionUID = -8359120387968357126L;
    public static final String NODE_SPECIFIC_NETYPE = "netype";

    public String getNeType() {
        return getValueString(NODE_SPECIFIC_NETYPE);
    }
}