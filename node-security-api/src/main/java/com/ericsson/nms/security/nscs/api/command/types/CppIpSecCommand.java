/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package com.ericsson.nms.security.nscs.api.command.types;

/**
 * Representation of the cpp-ipsec command
 *
 * @author emehsau
 */

public class CppIpSecCommand extends NscsIpSecCommand {

    private static final long serialVersionUID = 8425337571622369372L;
    public static final String FORCE_UPDATE = "update";
    public static final String CONTINUE_AFTER_FAIL = "continue";

    /**
     * To check whether force update is enable or not
     *
     * @return <code>true</code> if force update is enable, otherwise return is
     * <code>false</code>
     */
    public boolean isUpdate() {
        return hasProperty(FORCE_UPDATE);
    }

    /**
     * To check whether continue option is enable or not
     *
     * @return <code>true</code> if continue is enable, otherwise return is
     * <code>false</code>
     */
    public boolean isContinueSupported() {
        return hasProperty(CONTINUE_AFTER_FAIL);
    }

}
