package com.ericsson.nms.security.nscs.api.credentials;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.ericsson.nms.security.nscs.api.command.types.CredentialsCommand;

/**
 * Pojo that collects the User credentials. - rootUser: the {@link UserCredentials} instance representing the credentials for a root user -
 * unsecureUser: the {@link UserCredentials} instance representing the credentials for a normal user - secureUser: the {@link UserCredentials}
 * instance representing the credentials for a secure user
 */
public class CredentialAttributes implements Serializable {

    private static final long serialVersionUID = -4937675269870658782L;

    UserCredentials rootUser;
    UserCredentials unSecureUser;
    UserCredentials nwieaSecureUser;
    UserCredentials nwiebSecureUser;
    UserCredentials secureUser;
    UserCredentials nodeCliUser;

    /**
     * @param rootUser
     *            - the root user object
     * @param unSecureUser
     *            - the unSecure user object
     * @param secureUser
     *            - the secure user object
     */
    public CredentialAttributes(final UserCredentials rootUser, final UserCredentials unSecureUser, final UserCredentials secureUser) {
        this.rootUser = rootUser;
        this.unSecureUser = unSecureUser;
        this.secureUser = secureUser;
        this.nodeCliUser = null;
        this.nwieaSecureUser = null;
        this.nwiebSecureUser = null;
    }

    /**
     * @param rootUser
     *            - the root user object
     * @param unSecureUser
     *            - the unSecure user object
     * @param secureUser
     *            - the secure user object
     * @param nodeCliUser
     *            - the nodeCLI user object
     */
    public CredentialAttributes(final UserCredentials rootUser, final UserCredentials unSecureUser, final UserCredentials secureUser, final UserCredentials nodeCliUser ) {
        this.rootUser = rootUser;
        this.unSecureUser = unSecureUser;
        this.secureUser = secureUser;
        this.nodeCliUser = nodeCliUser;
        this.nwieaSecureUser = null;
        this.nwiebSecureUser = null;
    }

      /**
     * @param rootUser
     *            - the root user object
     * @param unSecureUser
     *            - the unSecure user object
     * @param secureUser
     *            - the secure user object
     * @param nwieaSecureUser
     *            - the NWI-E Side A secure User object (this is required only for BSC Node)
     * @param nwiebSecureUser
     *            - the NWI-E Side B secure User object (this is required only for BSC Node)
     */
    public CredentialAttributes(final UserCredentials rootUser, final UserCredentials unSecureUser, final UserCredentials secureUser,
                                final UserCredentials nwieaSecureUser, final UserCredentials nwiebSecureUser) {
        this.rootUser = rootUser;
        this.unSecureUser = unSecureUser;
        this.secureUser = secureUser;
        this.nwieaSecureUser = nwieaSecureUser;
        this.nwiebSecureUser = nwiebSecureUser;
        this.nodeCliUser = null;
    }


    /**
     * @param rootUser
     *            - the root user object
     * @param unSecureUser
     *            - the unSecure user object
     * @param secureUser
     *            - the secure user object
     * @param nwieaSecureUser
     *            - the NWI-E Side A secure User object (this is required only for BSC Node)
     * @param nwiebSecureUser
     *            - the NWI-E Side B secure User object (this is required only for BSC Node)
     * @param nodeCliCredentials
     *            - the nodeCLI user object
     */
    public CredentialAttributes(final UserCredentials rootUser, final UserCredentials unSecureUser, final UserCredentials secureUser,
                                final UserCredentials nwieaSecureUser, final UserCredentials nwiebSecureUser, final UserCredentials nodeCliCredentials) {
        this.rootUser = rootUser;
        this.unSecureUser = unSecureUser;
        this.secureUser = secureUser;
        this.nwieaSecureUser = nwieaSecureUser;
        this.nwiebSecureUser = nwiebSecureUser;
        this.nodeCliUser = nodeCliCredentials;
    }

    /**
     * @return the NWI-E Side A secureUser
     */
    public UserCredentials getNwieaSecureUser() {
        return nwieaSecureUser;
    }

    /**
     * Set NWI-E A Secure User
     *
     * @param nwieaSecureUser
     *            - the NWI-E Side A secure User object (this is required only for BSC Node)
     */
    public void setNwieaSecureUser(final UserCredentials nwieaSecureUser) {
        this.nwieaSecureUser = nwieaSecureUser;
    }

    /**
     * @return the NWI-E Side B secureUser
     */
    public UserCredentials getNwiebSecureUser() {
        return nwiebSecureUser;
    }

    /**
     * Set NWI-B Secure User
     *
     * @param nwiebSecureUser
     *            - the NWI-E Side B secure User object (this is required only for BSC Node)
     */
    public void setNwiebSecureUser(final UserCredentials nwiebSecureUser) {
        this.nwiebSecureUser = nwiebSecureUser;
    }

    /**
     * @return the rootUser
     */
    public UserCredentials getRootUser() {
        return rootUser;
    }

    /**
     * Set Root User Object
     * 
     * @param rootUser
     *          - the root user object
     */
    public void setRootUser(final UserCredentials rootUser) {
        this.rootUser = rootUser;
    }

    /**
     * @return the unSecureUser
     */
    public UserCredentials getUnSecureUser() {
        return unSecureUser;
    }

    /**
     * Set unSecure User Object
     *
     * @param unSecureUser
     *          - the unSecure user object
     */
    public void setUnSecureUser(final UserCredentials unSecureUser) {
        this.unSecureUser = unSecureUser;
    }

    /**
     * @return the secureUser
     */
    public UserCredentials getSecureUser() {
        return secureUser;
    }

    /**
     * Set Secure User
     * 
     * @param secureUser
     *          - the secure user object
     */
    public void setSecureUser(final UserCredentials secureUser) {
        this.secureUser = secureUser;
    }

    /**
     * @return the NodeCli User
     */
    public UserCredentials getNodeCliUser() {
        return nodeCliUser;
    }

    /**
     * Set NodeCli User
     *
     * @param nodeCliUser
     */
    public void setNodeCliUser(final UserCredentials nodeCliUser) {
        this.nodeCliUser = nodeCliUser;
    }

    public Set<String> getActualCredentialAttributeKeys() {
        final Set<String> credentialList = new HashSet<>();
        if (this.rootUser != null) {
            credentialList.add(CredentialsCommand.ROOT_USER_NAME_PROPERTY);
            credentialList.add(CredentialsCommand.ROOT_USER_PASSWORD_PROPERTY);
        }
        if (this.secureUser != null) {
            credentialList.add(CredentialsCommand.SECURE_USER_NAME_PROPERTY);
            credentialList.add(CredentialsCommand.SECURE_USER_PASSWORD_PROPERTY);
        }
        if (this.nwieaSecureUser != null) {
            credentialList.add(CredentialsCommand.NWIEA_SECURE_USER_NAME_PROPERTY);
            credentialList.add(CredentialsCommand.NWIEA_SECURE_PASSWORD_PROPERTY);
        }
        if (this.nwiebSecureUser != null) {
            credentialList.add(CredentialsCommand.NWIEB_SECURE_USER_NAME_PROPERTY);
            credentialList.add(CredentialsCommand.NWIEB_SECURE_PASSWORD_PROPERTY);
        }
        if (this.unSecureUser != null) {
            credentialList.add(CredentialsCommand.NORMAL_USER_NAME_PROPERTY);
            credentialList.add(CredentialsCommand.NORMAL_USER_PASSWORD_PROPERTY);
        }
        if (this.nodeCliUser != null) {
            credentialList.add(CredentialsCommand.NODECLI_USER_NAME_PROPERTY);
            credentialList.add(CredentialsCommand.NODECLI_USER_PASSPHRASE_PROPERTY);
        }

        return credentialList;
    }

    @Override
    public String toString() {
        final String secureUName = (getSecureUser() == null) ? "null" : getSecureUser().getUsername();
        final String secureUPassword = (getSecureUser() == null) ? "*******" : "******";
        final String nwieaSecureUName = (getNwieaSecureUser() == null) ? "null" : getNwieaSecureUser().getUsername();
        final String nwieaSecureUPassword = (getNwieaSecureUser() == null) ? "*******" : "******";
        final String nwiebSecureUName = (getNwiebSecureUser() == null) ? "null" : getNwiebSecureUser().getUsername();
        final String nwiebSecureUPassword = (getNwiebSecureUser() == null) ? "*******" : "******";
        final String unsecureUName = (getUnSecureUser() == null) ? "null" : getUnSecureUser().getUsername();
        final String unsecureUPassword = (getUnSecureUser() == null) ? "*******" : "******";
        final String rootUName = (getRootUser() == null) ? "null" : getRootUser().getUsername();
        final String rootUPassword = (getRootUser() == null) ? "*******" : "******";
        final String nodeCliUName = (getNodeCliUser() == null) ? "null" : getNodeCliUser().getUsername();
        final String nodeCliUPWD = (getNodeCliUser() == null) ? "*******" : "******";

        final String response = String.format(
                "SecureUserName: %s, SecureUserPassword: %s; " + "UnsecureUserName: %s, UnsecureUserPassword: %s; "
                        + "nwieaSecureUserName: %s, nwieaSecureUserPassword: %s; " + "nwiebSecureUserName: %s, nwiebSecureUserPassword: %s; "
                        + "RootUserName: %s, RootUserPassword: %s;"
                        + "NodeCliUserName: %s, NodeCliUserPassword:%s",
                secureUName, secureUPassword, unsecureUName, unsecureUPassword, nwieaSecureUName, nwieaSecureUPassword, nwiebSecureUName,
                nwiebSecureUPassword, rootUName, rootUPassword, nodeCliUName, nodeCliUPWD);

        return response;
    }
}
