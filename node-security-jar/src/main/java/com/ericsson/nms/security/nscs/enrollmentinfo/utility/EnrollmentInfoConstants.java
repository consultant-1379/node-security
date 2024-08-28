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
package com.ericsson.nms.security.nscs.enrollmentinfo.utility;

/**
 * 
 * This class hold the constant values related to the GenerateEnrollmentInfoFileHandler
 */

public class EnrollmentInfoConstants {

    private EnrollmentInfoConstants() {
        super();
    }

    public static final String SUCCESS_MESSAGE = "Enrollment information file generated successfully.";

    public static final String CMPv2_INITIAL = "CMPv2_INITIAL";
    public static final String CMPv2_VC = "CMPv2_VC";
    public static final String OAM = "OAM";
    public static final String XSD_VALIDATOR_FILE = "NodeDetailsSchema.xsd";
    public static final String ENROLLMENT_CONFIGURATION_XML = "-Enrollment-Configuration.xml";
    public static final String ENROLLMENT_INFO_COMMAND_INITIATED_FOR_SOME_NODES = "Enrollment information files generation is partially successful.The following are the details pertaining to invalid node:";
    public static final String ENROLLMENT_INFO_FAILED_FOR_ALL_NODES = "EnrollmentInfo files generation has failed for all nodes. Details are given below :";
    public static final int NO_OF_COLUMNS = 2;
    public static final String ENROLLMENT_INFO_XML_FILE_NAME = "EnrollmentInfo";
    public static final String ENROLLMENT_CONFIGURATION_ZIP_FILE_NAME = "EnrollmentConfiguration";

    public static final String NODE_NAME = "NodeName";
    public static final String COMMON_NAME ="CommonName";
    public static final String ENROLLMENT_MODE ="EnrollmentMode";
    public static final String ENTITY_PROFILE_NAME = "EntityProfileName";
    public static final String SUBJECT_ALT_NAME ="SubjectAltName";
    public static final String SUBJECT_ALT_NAME_FORMAT = "SubjectAltNameFormat";
    public static final String ALGORITHM_KEYS ="AlgorithmKeys";
    public static final String ENTITY_CATEGORY ="EntityCategory";
    public static final String MODEL_INFO = "ModelInfo";
    public static final String STANDARD_PROTOCOL_FAMILY = "StandardProtocolFamily";
    public static final String NORMALIZABLE_NODE_REFERENCE ="NormalizableNodeReference";
    public static final String IO_EXCEPTION_FILE_FORMAT = "IOException occurred while creating file identifier for %s";

    // OTP
    public static final String OTP_COUNT = "OtpCount";
    public static final Integer MIN_OTP_COUNT = 0;
    public static final String OTP_VALIDITY_PERIOD_IN_MINUTES = "OtpValidityPeriodInMinutes";
    public static final Integer NEVER_EXPIRING_OTP_VALIDITY_PERIOD = -1;
    public static final Integer NUM_MINUTES_IN_A_DAY = 1440;
    public static final Integer MAX_OTP_VALIDITY_PERIOD_IN_DAYS = 30;
    public static final Integer MAX_OTP_VALIDITY_PERIOD_IN_MINUTES = MAX_OTP_VALIDITY_PERIOD_IN_DAYS * NUM_MINUTES_IN_A_DAY;
    public static final Integer MIN_OTP_VALIDITY_PERIOD_IN_MINUTES = 1;

}
