package com.ericsson.nms.security.nscs.api.command.types;


/**
 * Representation of the create-credentials command
 * 
 * Created by emaynes on 09/05/2014.
 */
public class CredentialsCommand extends NscsNodeCommand {

    private static final long serialVersionUID = 4632291971986735849L;

    public static final String ROOT_USER_NAME_PROPERTY = "rootusername";
    public static final String ROOT_USER_PASSWORD_PROPERTY = "rootuserpassword";
    public static final String SECURE_USER_NAME_PROPERTY = "secureusername";
    public static final String SECURE_USER_PASSWORD_PROPERTY = "secureuserpassword";
    public static final String NWIEA_SECURE_USER_NAME_PROPERTY = "nwieasecureusername";
    public static final String NWIEA_SECURE_PASSWORD_PROPERTY = "nwieasecureuserpassword";
    public static final String NWIEB_SECURE_USER_NAME_PROPERTY = "nwiebsecureusername";
    public static final String NWIEB_SECURE_PASSWORD_PROPERTY = "nwiebsecureuserpassword";
    public static final String NORMAL_USER_NAME_PROPERTY = "normalusername";
    public static final String NORMAL_USER_PASSWORD_PROPERTY = "normaluserpassword";
    public static final String LDAP_APPLICATION_USER_NAME_PROPERTY = "ldapapplicationusername";
    public static final String LDAP_APPLICATION_USER_PASSWORD_PROPERTY = "ldapapplicationuserpassword";
    public static final String LDAP_USER_ENABLE_PROPERTY = "ldapuser";
    public static final String NODECLI_USER_NAME_PROPERTY = "nodecliusername";
    public static final String NODECLI_USER_PASSPHRASE_PROPERTY = "nodecliuserpassword";

    /**
     * @return the ldapApplicationUserNameProperty
     */
    public static String getLdapApplicationUserNameProperty() {
        return LDAP_APPLICATION_USER_NAME_PROPERTY;
    }


    /**
     * @return the ldapApplicationUserPasswordProperty
     */
    public static String getLdapApplicationUserPasswordProperty() {
        return LDAP_APPLICATION_USER_PASSWORD_PROPERTY;
    }

    public static final String CONTINUE_AFTER_FAIL = "continue";

    /**
     * @return String
     *          - The root user name entered in the command
     */
    public String getRootUserName() {
        return getValueString(ROOT_USER_NAME_PROPERTY);
    }

    /**
     * @return String
     *          - The root user's password entered in the command
     */
    public String getRootUserPassword() {
        return getValueString(ROOT_USER_PASSWORD_PROPERTY);
    }

    /**
     * @return String
     *          - The secure user's name entered in the command
     */
    public String getSecureUserName() {
        return getValueString(SECURE_USER_NAME_PROPERTY);
    }

    /**
     * @return String
     *          - The secure user's password entered in the command
     */
    public String getSecureUserPassword() {
        return getValueString(SECURE_USER_PASSWORD_PROPERTY);
    }

    /**
     * @return String
     *          - The NWI-E Side A secure user's name entered in the command
     */
    public String getNwieaSecureUserName() {
        return getValueString(NWIEA_SECURE_USER_NAME_PROPERTY);
    }

    /**
     * @return String
     *          - The NWI-E Side A secure user's password entered in the command for NWI-E Side A
     */
    public String getNwieaSecurePassword() {
        return getValueString(NWIEA_SECURE_PASSWORD_PROPERTY);
    }

    /**
     * @return String
     *          - The NWI-E Side B secure user's name entered in the command for NWI-E Side B
     */
    public String getNwiebSecureName() {
        return getValueString(NWIEB_SECURE_USER_NAME_PROPERTY);
    }

    /**
     * @return String
     *          - The NWI-E Side B secure user's password entered in the command for NWI-E Side B
     */
    public String getNwiebSecurePassword() {
        return getValueString(NWIEB_SECURE_PASSWORD_PROPERTY);
    }

    /**
     * @return String
     *          - The normal user's name entered in the command
     */
    public String getNormalUserName() {
        return getValueString(NORMAL_USER_NAME_PROPERTY);
    }

    /**
     * @return String
     *          - The normal user's password entered in the command
     */
    public String getNormalUserPassword() {
        return getValueString(NORMAL_USER_PASSWORD_PROPERTY);
    }
    
    /**
     * @return the ldapUserEnable
     */
    public String getLdapUserEnable() {
        return getValueString(LDAP_USER_ENABLE_PROPERTY);
    }

    /**
     * @return String - The nodecli user's name entered in the command
     */
    public String getNodeCliUserName() {
        return getValueString(NODECLI_USER_NAME_PROPERTY);
    }

    /**
     * @return String - The nodecli user's password entered in the command
     */
    public String getNodeCliUserPassword() {
        return getValueString(NODECLI_USER_PASSPHRASE_PROPERTY);
    }
  
    /**
     * To check whether continue option is enable or not
     * 
     * @return <code>true</code> if continue is enable, otherwise return is
     *         <code>false</code>
     */
    @Override
    public boolean isContinueSupported() {
        return hasProperty(CONTINUE_AFTER_FAIL);
    }


}
