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
package com.ericsson.nms.security.nscs.cucumber.helper.laad;

/**
 * This class holds the test constant values for Laad service.
 * 
 * @author xnagsow
 * 
 */
public class LaadTestDataConstants {

    public static final String COMMAND_LAAD_DISTRIBUTE = "laad distribute --nodelist ";
    public static final String COMMAND_SECADM = "secadm";
    public static final String TRUST_INSTALL_COMPLETED_EVENT_MESSAGE = "";
    public static final String COMMAND_TRUST_DISTRIBUTE = "trust distribute --trustcategory LAAD --nodelist ";
    public static final String COMMAND_TRUST_GET = "trust get --trustcategory LAAD --nodelist ";
    public static final String COMMAND_TRUST_REMOVE = "trust remove --trustcategory LAAD --issuer-dn \"CN=ENMInfrastructureCA, OU=EricssonOAM, O=Ericsson\" --serialnumber 1418227216286 --nodelist ";
    public static final String JOB_ID_PATTERN = "([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})";

    private LaadTestDataConstants() {
        throw new IllegalStateException("Constants class");
    }
}
