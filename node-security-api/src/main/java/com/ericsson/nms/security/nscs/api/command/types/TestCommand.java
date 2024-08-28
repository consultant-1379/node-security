/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.command.types;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;

public class TestCommand extends NscsPropertyCommand {

    private static final long serialVersionUID = 8343951441671419583L;

    private static final String TEST_WORKFLOWS = "workflows";

    /**
     * This method will return the optional workflows properties.
     *
     * @return the workflows property
     */
    public String getWorkflows() {
        if (hasProperty(TEST_WORKFLOWS)) {
            return getValueString(TEST_WORKFLOWS);
        }
        return null;
    }
}
