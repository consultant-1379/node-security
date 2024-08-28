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
package com.ericsson.nms.security.nscs.ldap.utility;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.ldap.entities.NodeSpecificLdapConfiguration;

/**
 * Utility service responsible for validating the user provided LDAP
 * configuration(reconfiguration) per node.
 * 
 * @author xsrirko
 * 
 */
@ApplicationScoped
public class UserProvidedLdapConfigurationValidator {

    @Inject
    LdapNodeValidatorUtility ldapNodeValidatorUtility;

    public UserProvidedLdapConfigurationValidator() {
    }

    /**
     * This method validates the User provided LDAP Configuration.
     * 
     * @param nodeSpecificLdapConfiguration
     * @throws InvalidNodeNameException
     * @throws InvalidFileContentException
     * @throws NodeNotSynchronizedException
     * @throws UnsupportedNodeTypeException
     */
    public void validate(final NodeSpecificLdapConfiguration nodeSpecificLdapConfiguration) throws CouldNotReadMoAttributeException,InvalidNodeNameException, InvalidFileContentException, 
            NodeNotSynchronizedException, UnsupportedNodeTypeException {
        validateNodeFdn(nodeSpecificLdapConfiguration);
    }

    private void validateNodeFdn(final NodeSpecificLdapConfiguration nodeSpecificLdapConfiguration) throws  CouldNotReadMoAttributeException,InvalidNodeNameException, NodeNotSynchronizedException,
            UnsupportedNodeTypeException {

        final String nodeFdn = nodeSpecificLdapConfiguration.getNodeFdn();
        ldapNodeValidatorUtility.validateNodeForLdapConfiguration(nodeFdn);

    }

    @SuppressWarnings("unused")
	private void validateTlsMode(final NodeSpecificLdapConfiguration nodeSpecificLdapConfiguration) throws InvalidFileContentException {

        if (nodeSpecificLdapConfiguration.getUseTls() == true && !nodeSpecificLdapConfiguration.getTlsMode().equals(LdapConstants.STARTTLS)) {
            throw new InvalidFileContentException();
        }

        if (nodeSpecificLdapConfiguration.getUseTls() == false && !nodeSpecificLdapConfiguration.getTlsMode().equals(LdapConstants.LDAPS)) {
            throw new InvalidFileContentException();
        }

    }

}
