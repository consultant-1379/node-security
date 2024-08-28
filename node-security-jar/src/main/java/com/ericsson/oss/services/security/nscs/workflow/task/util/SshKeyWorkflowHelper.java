/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2023
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.workflow.task.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.ssh.SSHKeyGenConstants;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.services.security.nscs.dps.NscsDpsUtils;

/**
 * Auxiliary class containing helper utilities for SSH key workflow task handlers.
 */
public class SshKeyWorkflowHelper {

    private static final String NES_ALGORITHM_AND_KEY_SIZE = ModelDefinition.NetworkElementSecurity.ALGORITHM_AND_KEY_SIZE;
    private static final String NES_SSH_PUBLIC_KEY = ModelDefinition.NetworkElementSecurity.ENM_SSH_PUBLIC_KEY;
    private static final String NES_SSH_PRIVATE_KEY = ModelDefinition.NetworkElementSecurity.ENM_SSH_PRIVATE_KEY;

    private static final String SUCCESSFULLY_UPDATED_MO_CURRENT_OLD_ATTRS_FORMAT = "Successfully updated MO [%s] current [%s] old [%s]";

    @Inject
    private NscsDpsUtils nscsDpsUtils;

    /**
     * Update the given NetworkElementSecurity MO with the given algorithm and key size and the given SSH keys.
     * 
     * Note that the update of an attribute is performed only if its value is not null since all involved attributes are modeled with
     * notNullConstraint.
     * 
     * @param networkElementSecurityMO
     *            the NetworkElementSecurity MO.
     * @param algorithmAndKeySize
     *            the algorithm and key size.
     * @param publicSSHKey
     *            the public key.
     * @param encryptedPrivateSSHKey
     *            the encrypted private key.
     * @return the update result.
     */
    public String updateNetworkElementSecurityMO(final ManagedObject networkElementSecurityMO, final String algorithmAndKeySize,
            final String publicSSHKey, final String encryptedPrivateSSHKey) {

        final Map<String, Object> oldNetworkElementSecurityAttributes = networkElementSecurityMO
                .getAttributes(Arrays.asList(NES_ALGORITHM_AND_KEY_SIZE, NES_SSH_PRIVATE_KEY, NES_SSH_PUBLIC_KEY));

        final Map<String, Object> networkElementSecurityAttributes = new HashMap<>();
        addNotNullAttribute(NES_ALGORITHM_AND_KEY_SIZE, algorithmAndKeySize, networkElementSecurityAttributes);
        addNotNullAttribute(NES_SSH_PRIVATE_KEY, encryptedPrivateSSHKey, networkElementSecurityAttributes);
        addNotNullAttribute(NES_SSH_PUBLIC_KEY, publicSSHKey, networkElementSecurityAttributes);

        nscsDpsUtils.updateMo(networkElementSecurityMO, networkElementSecurityAttributes);

        return String.format(SUCCESSFULLY_UPDATED_MO_CURRENT_OLD_ATTRS_FORMAT, networkElementSecurityMO.getFdn(),
                stringifyNetworkElementSecurityAttributes(networkElementSecurityAttributes),
                stringifyNetworkElementSecurityAttributes(oldNetworkElementSecurityAttributes));
    }

    /**
     * Add to the given attribute map an entry with key the attribute name and value the attribute value only if the attribute value is not null.
     * 
     * @param attributeName
     *            the attribute name.
     * @param attributeValue
     *            the attribute value.
     * @param attributes
     *            the attribute map.
     */
    private void addNotNullAttribute(final String attributeName, final String attributeValue, final Map<String, Object> attributes) {
        if (attributeValue != null) {
            attributes.put(attributeName, attributeValue);
        }
    }

    /**
     * Stringify the given NetworkElementSecurity MO attributes hiding the SSH keys if it is not null.
     * 
     * @param networkElementSecurityAttributes
     *            the NetworkElementSecurity MO attributes.
     * @return the stringified NetworkElementSecurity MO attributes.
     */
    private String stringifyNetworkElementSecurityAttributes(final Map<String, Object> networkElementSecurityAttributes) {
        final Map<String, Object> stringifiedNetworkElementSecurityAttributes = new HashMap<>();
        stringifiedNetworkElementSecurityAttributes.put(NES_ALGORITHM_AND_KEY_SIZE, networkElementSecurityAttributes.get(NES_ALGORITHM_AND_KEY_SIZE));
        stringifiedNetworkElementSecurityAttributes.put(NES_SSH_PRIVATE_KEY,
                obfuscateSshKeyValue((String) networkElementSecurityAttributes.get(NES_SSH_PRIVATE_KEY)));
        stringifiedNetworkElementSecurityAttributes.put(NES_SSH_PUBLIC_KEY,
                obfuscateSshKeyValue((String) networkElementSecurityAttributes.get(NES_SSH_PUBLIC_KEY)));
        return stringifiedNetworkElementSecurityAttributes.toString();
    }

    /**
     * Obfuscate the given SSH key value.
     * 
     * @param sshKeyValue
     *            the SSH key value.
     * @return the obfuscated SSH key value.
     */
    private String obfuscateSshKeyValue(final String sshKeyValue) {

        if (sshKeyValue == null || sshKeyValue.isEmpty() || SSHKeyGenConstants.SSH_KEY_INVALID.equals(sshKeyValue)) {
            return sshKeyValue;
        }
        return SSHKeyGenConstants.OBFUSCATED_VALID_SSH_KEY;
    }

}
