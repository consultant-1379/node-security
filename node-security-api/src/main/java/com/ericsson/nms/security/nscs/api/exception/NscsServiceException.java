package com.ericsson.nms.security.nscs.api.exception;

import javax.ejb.ApplicationException;

/**
 * Base exception for all Nscs services. Created by emaynes on 01/05/2014.
 */
@ApplicationException(rollback = true)
public abstract class NscsServiceException extends RuntimeException {
    private static final long serialVersionUID = -5164326722913363644L;

    private String suggestedSolution = NscsErrorCodes.SUGGESTED_SOLUTION_CONSULT_ERROR_LOGS;

    public static final int ERROR_CODE_START_INT = 10000;

    public NscsServiceException() {
    }

    public NscsServiceException(final String message) {
        super(message);
    }

    public NscsServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public NscsServiceException(final Throwable cause) {
        super(cause);
    }

    /**
     * @return error code of the exception
     */
    public int getErrorCode() {
        return ERROR_CODE_START_INT + getErrorType().toInt();
    }

    /**
     * Each subclass of NscsServiceException has it's own ErrorType
     *
     * @return the Error type
     */
    public abstract ErrorType getErrorType();

    /**
     * Gets the proposed solution for this error.
     *
     * @return String with the proposed solution or empty String.
     */
    public String getSuggestedSolution() {
        return suggestedSolution;
    }

    /**
     * Sets the proposed solution for this exception.
     *
     * @param suggestedSolution
     *            String of the proposed solution
     * @return this instance
     */
    public NscsServiceException setSuggestedSolution(final String suggestedSolution) {
        this.suggestedSolution = suggestedSolution == null ? "" : suggestedSolution;
        return this;
    }

    /**
     * <p>
     * Convenience method to set proposed solution.
     * </p>
     * <p>
     * Internally this method call String.format(suggestedSolution, args)
     * </p>
     *
     * @param suggestedSolution
     *            Suggested solution message. You can use any valid String.format placeholder.
     * @param args
     *            arguments so be placed at the placeholders.
     * @return this instance
     */
    public NscsServiceException setSuggestedSolution(final String suggestedSolution, final Object... args) {
        return setSuggestedSolution(String.format(suggestedSolution, args));
    }

    /**
     * Convenience method to subclasses so the can easily create messages with the format 'message part1 : message part2'
     *
     * @param part1 the part1
     * @param part2 the part2
     * @return formatted message
     */
    protected static String formatMessage(final String part1, final String part2) {
        return String.format("%s : %s", part1, part2);
    }

    /**
     * Enumeration of all error types reported by Nscs.
     * <p>
     * Each element should be used by ONE subclass of NscsServiceException in order to allow proper handling of error codes.
     * </p>
     */
    public enum ErrorType {
        UNDEFINED(0),
        INVALID_CPP_NODE(0),
        LAAD_FILE_INSTALLATION_ERROR(0),
        UNSUPPORTED_COMMAND_ARGUMENT_ERROR(0),
        MISSING_MANDATORY_ATTRIBUTE_ERROR(0),
        TARGET_GROUP_UPDATE_ERROR(0),
        INVALID_TARGET_GROUP_ERROR(0),
        UNSUPPORTED_PLATFORM(0),
        COMMAND_SYNTAX_ERROR(1),
        INVALID_NODE_FILE_ERROR(2),
        DUPLICATE_NODE_NAMES_ERROR(3),
        INVALID_NODE_NAME_ERROR(4),
        NODE_NOT_SYNCHED(5),
        NETWORK_ELEMENT_SECURITY_NOTFOUND_ERROR(6),
        NETWORK_ELEMENT_NOTFOUND_ERROR(7),
        SECURITY_FUNCTION_NOTFOUND_ERROR(8),
        NETWORK_ELEMENT_SECURITY_ALREADY_EXISTS_ERROR(9),
        MULTIPLE_ERRORS(10),
        REQUESTED_LEVEL_ALREADY_SET_ERROR(11),
        IP_SEC_ACTION_ERROR(12),
        INVALID_FILE_CONTENT(13),
        INVALID_INPUT_XML_FILE(14),
        NODE_IS_IN_WORKFLOW(15),
        UNASSOCIATED_NETWORK_ELEMENT_ERROR(16),
        MAX_NODES_EXCEEDED_ERROR(17),
        IP_SEC_NOT_FOUND_ERROR(18),
        CAPABILITY_MODEL_ERROR(19),
        CERTIFICATE_ISSUE_WF_FAILED(20),
        NODE_NOT_CERTIFIABLE(21),
        INVALID_INPUT_NODE_LIST_FOR_COMMAND(22),
        REQUESTED_ENTITY_PROFILE_NAME_DOES_NOT_EXIST(22),
        DEFAULT_ENTITY_PROFILE_NAME_DOES_NOT_EXIST(22),
        REQUESTED_ALGORITHM_KEY_SIZE_IS_NOT_SUPPORTED_FOR_THIS_NODE(22),
        REQUESTED_SUBJECT_ALTERNATIVE_NAME_TYPE_IS_NOT_SUPPORTED(22),
        REQUESTED_SUBJECT_ALTERNATIVE_NAME_IS_INVALID(22),
        SUBJECT_ALT_NAME_AND_SUBJ_ALT_NAME_TYPE_CANT_BE_EMPTY(22),
        TRUST_DISTRIBUTE_WF_FAILED(23),
        NODE_NOT_SUPERVISED(24),
        INVALID_ENTITY_CATEGORY(24),
        CERTIFICATE_REISSUE_WF_FAILED(25),
        ENTITY_FOR_NODE_NOT_FOUND(26),
        ENTITY_WITH_VALID_CATEGORY_NOT_FOUND(27),
        ENTITY_WITH_ACTIVE_CERTIFICATE_NOT_FOUND(28),
        NO_NODES_WITH_ENTITIES_WITH_SPECIFIED_CATEGORY(29),
        COULD_NOT_READ_MO_ATTRIBUTES(30),
        NO_VALID_NODE_FOUND(31),
        TRUST_REMOVE_WF_FAILED(32),
        MODEL_SERVICE_ERROR(33),
        DATA_ACCESS_ERROR(99),
        UNEXPECTED_ERROR(99),
        COMMAND_HANDLER_NOT_FOUND_ERROR(99),
        UNEXPECTED_COMMAND_TYPE(99),
        DATA_ACCESS_SYSTEM_ERROR(99),
        SET_SECURITY_LEVEL_ERROR(99),
        UNSUPPORTED_NODE_TYPE(90),
        INVALID_ARGUMENT_VALUE(91),
        KEYPAIR_NOT_FOUND(92),
        KEYPAIR_ALREADY_GENERATED(93),
        KEYGEN_HANDLER_ERROR(94),
        VALID_NODES_NORMALIZABLE_NODES_MISMATCH(95),
        LDAP_CONFIGURATION_FAILED(96),
        UNSUPPORTED_CERTIFICATE_TYPE(97),
        INVALID_JOB(99),
        SECURITY_VIOLATION_ERROR(99),
        UNSUPPORTED_NODE_RELEASE_VERSION(98),
        TRUST_CATEGORY_MO_DOES_NOT_EXISTS(101),
        CRLCHECK_ENABLE_OR_DISABLE_WF_FAILED(102),
        SECURITY_MO_DOES_NOT_EXISTS(103),
        ON_DEMAND_CRL_DOWNLOAD_WF_FAILED(104),
        SET_CIPHERS_WF_FAILED(105),
        UNSUPPORTED_ALGORITHM(106),
        RTSEL_WF_FAILED(107),
        SERVER_NAME_NOT_FOUND(108),
        HTTPS_ACTIVATE_OR_DEACTIVATE_WF_FAILED(109),
        GET_HTTPS_WF_FAILED(110),
        FTPES_ACTIVATE_OR_DEACTIVATE_WF_FAILED(111),
        DATABASE_UNAVAILABLE(112),
        INVALID_SAVED_SEARCH_NAME_ERROR(113),
        INVALID_COLLECTION_NAME_ERROR(114),
        INVALID_NODE_NAME_EXPRESSION_ERROR(115),
        INVALID_ALARM_SERVICE_STATE(116),
        LAAD_FILES_DISTRIBUTION_WF_FAILED(117),
        UNSUPPORTED_TRUST_CATEGORY_TYPE(118),
        NTP_OPERATION_NOT_SUPPORTED(119),
        NTP_CONFIGURE_OR_REMOVE_WF_FAILED(120),
        NTP_KEY_NOT_FOUND(121),
        NTP_KEY_MAPPING_NOT_FOUND(122),
        SSO_NOT_SUPPORTED_FOR_THE_NODE_TYPE(123),
        IMPORT_NODE_SSH_PRIVATE_KEY_HANDLER_ERROR(124),
        LDAP_CONFIGURE_WF_FAILED(125),
        TOO_MANY_CHILD_MOS(126),
        PLATFORM_CONFIGURATION_UNAVAILABLE(127),
        BAD_REQUEST(128),
        MO_TYPE_NOT_FOUND(129),
        LDAP_PROXY_FAILED(130),
        GENERATE_ENROLLMENT_INFO_FAILED(131),
        IPSEC_CONFIGURE_WF_FAILED(132),
        SSH_KEY_WF_FAILED(133),
        SSH_INVALID_KEY_GENERATED(134),
        INVALID_ALGORITH_KEY_SIZE_IN_NETWORK_ELEMENT_SECURITY(135);

        private int errorCode;

    private ErrorType(final int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Get the integer value of this ErrorType
     *
     * @return errorCode
     */
    public int toInt() {
        return this.errorCode;
    }

    /**
     * Get the String value of this ErrorType
     *
     * @return errorCode
     */
    @Override
    public String toString() {
        return String.valueOf(this.errorCode);
    }

    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NscsServiceException)) {
            return false;
        }

        final NscsServiceException that = (NscsServiceException) o;

        if (!(getErrorCode() == that.getErrorCode())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return getErrorType().hashCode();
    }
}
