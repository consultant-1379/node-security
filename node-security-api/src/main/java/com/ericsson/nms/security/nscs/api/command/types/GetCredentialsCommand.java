package com.ericsson.nms.security.nscs.api.command.types;


/**
 * Representation of the get credentials command
 *
 */
public class GetCredentialsCommand extends NscsNodeCommand {

    private static final long serialVersionUID = 1703846753876177424L;

    public static final String USER_TYPE_PROPERTY = "usertype";
    public static final String PLAIN_TEXT_PROPERTY = "plaintext";
    public static final String PLAIN_TEXT_SHOW = "show";
    public static final String PLAIN_TEXT_HIDE = "hide";

    public static final String ROOT_USER_NAME_PROPERTY = "root";
    public static final String SECURE_USER_NAME_PROPERTY = "secure";
    public static final String NORMAL_USER_NAME_PROPERTY = "normal";
    public static final String NWIEA_SECURE_USER_NAME_PROPERTY = "nwieasecure";
    public static final String NWIEB_SECURE_USER_NAME_PROPERTY = "nwiebsecure";
    public static final String NODECLI_USER_NAME_PROPERTY = "nodecli";

    /**
     * @return String - The user type entered in the command
     */
    public String getUserType() {
        return getValueString(USER_TYPE_PROPERTY);
    }

    /**
     * @return String - The plain text option entered in the command
     */
    public String getPlainText() {
        return getValueString(PLAIN_TEXT_PROPERTY);
    }

}
