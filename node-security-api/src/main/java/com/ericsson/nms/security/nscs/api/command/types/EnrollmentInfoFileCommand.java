/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
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
 * EnrollmentInfoFileCommand class for security file related command.
 * 
 * @author xgvgvgv
 */

public class EnrollmentInfoFileCommand extends NscsNodeCommand {

    private static final long serialVersionUID = -5969140066423889022L;

    public static final String CERT_TYPE_PROPERTY = "certtype";

    public static final String ENROLLMENT_MODE = "enrollmentmode";

    public static final String VERBOSE = "verbose";

    /**
     * This method will return certificate type to generate security file.
     *
     * @return certificateType
     */
    public String getCertType() {
        return getValueString(CERT_TYPE_PROPERTY);
    }

    /**
     * This method will return Enrollment mode to generate security file.
     * 
     * @return enrollmentMode
     */

    public String getEnrollmentMode() {
        return getValueString(ENROLLMENT_MODE);
    }

    /**
     * To check whether verbose exists in command or not
     *
     * @return <code>true</code> if verbose is present, otherwise return is
     *         <code>false</code>
     */
    public boolean isVerbose() {
        return hasProperty(VERBOSE);
    }
}
