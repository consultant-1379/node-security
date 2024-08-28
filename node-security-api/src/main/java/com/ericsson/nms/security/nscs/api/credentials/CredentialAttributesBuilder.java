/**
 * 
 */
package com.ericsson.nms.security.nscs.api.credentials;

import java.io.Serializable;

/**
 * Java class that builds a {@link CredentialAttributes} instance that contains all user credentials.
 *
 */
public class CredentialAttributesBuilder implements Serializable {

    private static final long serialVersionUID = -1845052421293690893L;

    private UserCredentials rootCredentials = null;
    private UserCredentials secureCredentials = null;
    private UserCredentials nwieaSecureCredentials = null;
    private UserCredentials nwiebSecureCredentials = null;
    private UserCredentials unsecureCredentials = null;
    private UserCredentials nodeCliCredentials = null;

    public CredentialAttributesBuilder() {
        super();
    }

    /**
     * @param rootName:
     *            the wanted root username.
     * @param rootPwd:
     *            the wanted root user password.
     * @return this instance of {@link CredentialAttributesBuilder}
     */
    public CredentialAttributesBuilder addRoot(final String rootName, final String rootPwd) {
        rootCredentials = new UserCredentials(rootName, rootPwd);
        return this;
    }

    /**
     * @param secureName:
     *            the wanted secure username.
     * @param securePwd:
     *            the wanted secure user password.
     * @return this instance of {@link CredentialAttributesBuilder}
     */
    public CredentialAttributesBuilder addSecure(final String secureName, final String securePwd) {
        secureCredentials = new UserCredentials(secureName, securePwd);
        return this;
    }

    /**
     * @param nwieaSecureName:
     *            the wanted NWI-E Side A secure username.
     * @param nwieaSecurePwd:
     *            the wanted NWI-E Side A secure user password.
     * @return this instance of {@link CredentialAttributesBuilder}
     */
    public CredentialAttributesBuilder addNwieaSecure(final String nwieaSecureName, final String nwieaSecurePwd) {
        nwieaSecureCredentials = new UserCredentials(nwieaSecureName, nwieaSecurePwd);
        return this;
    }

    /**
     * @param nwiebSecureName:
     *            the wanted NWI-E Side B secure username.
     * @param nwiebSecurePwd:
                 the wanted NWI-E Side B secure user password.
     * @return this instance of {@link CredentialAttributesBuilder}
     */
    public CredentialAttributesBuilder addNwiebSecure(final String nwiebSecureName, final String nwiebSecurePwd) {
        nwiebSecureCredentials = new UserCredentials(nwiebSecureName, nwiebSecurePwd);
        return this;
    }

    /**
     * @param unsecureName:
     *            the wanted unsecure username.
     * @param unsecurePwd:
     *            the wanted unsecure user password.
     * @return this instance of {@link CredentialAttributesBuilder}
     */
    public CredentialAttributesBuilder addUnsecure(final String unsecureName, final String unsecurePwd) {
        unsecureCredentials = new UserCredentials(unsecureName, unsecurePwd);
        return this;
    }

    /**
     * @param nodeCliuser:
     *            the wanted nodeCli username.
     * @param nodeCliPwd:
     *            the wanted nodeCli user password.
     * @return this instance of {@link CredentialAttributesBuilder}
     */
    public CredentialAttributesBuilder addNodeCliUser(final String nodeCliuser, final String nodeCliPwd) {
        nodeCliCredentials = new UserCredentials(nodeCliuser, nodeCliPwd);
        return this;
    }

    /**
     * @return an instance of {@link CredentialAttributes} that contains root, unsecure, secure, nwieaSecure and nwiebSecure {@link UserCredentials}.
     */
    public CredentialAttributes build() {
        return new CredentialAttributes(rootCredentials, unsecureCredentials, secureCredentials, nwieaSecureCredentials, nwiebSecureCredentials, nodeCliCredentials);
    }

}
