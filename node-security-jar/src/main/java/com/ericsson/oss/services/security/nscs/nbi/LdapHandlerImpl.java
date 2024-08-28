/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2024
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.nbi;

import java.net.StandardProtocolFamily;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.exception.NetworkElementSecurityNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.NodeDoesNotExistException;
import com.ericsson.nms.security.nscs.api.exception.NscsBadRequestException;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.ldap.control.LdapConfigurationProvider;
import com.ericsson.nms.security.nscs.ldap.utility.LdapConstants;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.services.security.nscs.nbi.resources.NscsLdapResponse;
import com.ericsson.oss.services.security.nscs.nbi.resources.NscsResourceInstance;
import com.ericsson.oss.services.security.nscs.nbi.resources.NscsResourcesConstants;

public class LdapHandlerImpl {

    private static final String NULL_PROXY_ACCOUNT_DN = null;
    private static final String PROXY_ACCOUNT_DN = NetworkElementSecurity.PROXY_ACCOUNT_DN;
    private static final Logger logger = LoggerFactory.getLogger(LdapHandlerImpl.class);

    @Inject
    private NbiHandlerHelper nbiHandlerHelper;

    @Inject
    private LdapConfigurationProvider ldapConfigurationProvider;

    /**
     * Create the LDAP configuration for the given node.
     * 
     * @param nodeNameOrFdn
     *            the node name or FDN.
     * @param ipFamily
     *            the IP family.
     * @return the LDAP configuration.
     */
    public NscsLdapResponse createLdapConfiguration(final String nodeNameOrFdn, final String ipFamily) {

        logger.info("createLdapConfiguration: start for {} and {}", nodeNameOrFdn, ipFamily);

        final NormalizableNodeReference normalizedNodeRef = nbiHandlerHelper.getNormalizedNodeReference(nodeNameOrFdn);
        if (normalizedNodeRef == null) {
            throw new NodeDoesNotExistException(nodeNameOrFdn);
        }
        final ManagedObject normalizedRootMO = nbiHandlerHelper.getNormalizedRootMO(normalizedNodeRef);
        final StandardProtocolFamily protocolFamily = nbiHandlerHelper.getProtocolFamily(ipFamily, normalizedRootMO, normalizedNodeRef);
        if (protocolFamily == null) {
            throw new NscsBadRequestException("Unknown IP family");
        }
        final ManagedObject networkElementSecurityMO = nbiHandlerHelper.getNetworkElementSecurityMO(normalizedRootMO);
        if (networkElementSecurityMO == null) {
            throw new NetworkElementSecurityNotfoundException();
        }

        final Boolean isPrevProxyAccountToBeDeleted = true;
        final Map<String, Object> ldapConfiguration = createLdapConfigurationForNode(nodeNameOrFdn, networkElementSecurityMO,
                isPrevProxyAccountToBeDeleted);

        return buildLdapResponse(protocolFamily, ldapConfiguration);
    }

    /**
     * Create the LDAP configuration for the given node and update the correspondent NetworkElementSecurity MO possibly deleting the previous proxy
     * account (if present) unless the delete is explicitly disallowed.
     * 
     * @param nodeNameOrFdn
     *            the node name or FDN.
     * @param networkElementSecurityMO
     *            the NetworkElementSecurity MO.
     * @param isPrevProxyAccountToBeDeleted
     *            true if previous proxy account (if present) shall to be deleted, false otherwise.
     * @return the LDAP configuration.
     */
    public Map<String, Object> createLdapConfigurationForNode(final String nodeNameOrFdn, final ManagedObject networkElementSecurityMO,
            final Boolean isPrevProxyAccountToBeDeleted) {

        // create a new proxy account
        final Map<String, Object> ldapConfiguration = ldapConfigurationProvider.getLdapServerConfiguration();

        final String newProxyAccountDn = (String) ldapConfiguration.get(LdapConstants.BIND_DN);
        final NscsResourceInstance resourceInstance = updateNetworkElementSecurityMO(nodeNameOrFdn, networkElementSecurityMO, newProxyAccountDn,
                isPrevProxyAccountToBeDeleted);
        logger.info("Previous proxy account DN {}", resourceInstance.getSubResourceId());
        return ldapConfiguration;
    }

    /**
     * Delete LDAP configuration for the given node.
     * 
     * @param nodeNameOrFdn
     *            the node name or FDN.
     * @return The deleted resource instance.
     */
    public NscsResourceInstance deleteLdapConfiguration(final String nodeNameOrFdn) {

        logger.info("deleteLdapConfiguration: start for {}", nodeNameOrFdn);

        final NormalizableNodeReference normalizedNodeRef = nbiHandlerHelper.getNormalizedNodeReference(nodeNameOrFdn);
        if (normalizedNodeRef == null) {
            throw new NodeDoesNotExistException(nodeNameOrFdn);
        }
        final ManagedObject normalizedRootMO = nbiHandlerHelper.getNormalizedRootMO(normalizedNodeRef);
        final ManagedObject networkElementSecurityMO = nbiHandlerHelper.getNetworkElementSecurityMO(normalizedRootMO);
        if (networkElementSecurityMO == null) {
            throw new NetworkElementSecurityNotfoundException();
        }
        final Boolean isPrevProxyAccountToBeDeleted = true;
        return deleteLdapConfigurationForNode(nodeNameOrFdn, networkElementSecurityMO, isPrevProxyAccountToBeDeleted);
    }

    /**
     * Delete the LDAP configuration for the given node and update the correspondent NetworkElementSecurity MO possibly deleting the previous proxy
     * account (if present) unless the delete is explicitly disallowed.
     * 
     * @param nodeNameOrFdn
     *            the node name or FDN.
     * @param networkElementSecurityMO
     *            the NetworkElementSecurity MO.
     * @param isPrevProxyAccountToBeDeleted
     *            true if previous proxy account (if present) shall to be deleted, false otherwise.
     * @return the LDAP configuration.
     */
    public NscsResourceInstance deleteLdapConfigurationForNode(final String nodeNameOrFdn, final ManagedObject networkElementSecurityMO,
            final Boolean isPrevProxyAccountToBeDeleted) {
        return updateNetworkElementSecurityMO(nodeNameOrFdn, networkElementSecurityMO, NULL_PROXY_ACCOUNT_DN, isPrevProxyAccountToBeDeleted);
    }

    /**
     * Update the NetworkElementSecurity MO according to the given proxy account.
     * 
     * Update is performed only if the given MO has the proxyAccountDn attribute. If attribute is present and containing a not null proxy account, it
     * is deleted.
     * 
     * @param nodeNameOrFdn
     *            the node name or FDN.
     * @param networkElementSecurityMO
     *            the NetworkElementSecurity MO.
     * @param newProxyAccountDn
     *            the proxy account DN.
     * @param isPrevProxyAccountToBeDeleted
     *            true if previous proxy account (if present) is to be deleted, false otherwise.
     * @return the resource instance containing previously set proxy account DN (if any)
     */
    private NscsResourceInstance updateNetworkElementSecurityMO(final String nodeNameOrFdn, final ManagedObject networkElementSecurityMO,
            final String newProxyAccountDn, final Boolean isPrevProxyAccountToBeDeleted) {
        final Boolean isDeleteOperation = (newProxyAccountDn == null);
        String previousProxyAccountDn = null;
        String status = null;
        if (hasProxyAccountDnAttribute(networkElementSecurityMO)) {
            previousProxyAccountDn = networkElementSecurityMO.getAttribute(PROXY_ACCOUNT_DN);
            logger.info("NetworkElementSecurity MO [{}] has [{}] attribute with value [{}]", networkElementSecurityMO.getFdn(), PROXY_ACCOUNT_DN,
                    previousProxyAccountDn);
            if (previousProxyAccountDn != null) {
                if (isPrevProxyAccountToBeDeleted) {
                    status = deleteProxyAccount(previousProxyAccountDn, isDeleteOperation);
                } else {
                    status = NscsResourcesConstants.STATUS_OK;
                    logger.info("Previous proxy account [{}] shall not be deleted.", previousProxyAccountDn);
                }
            } else {
                status = isDeleteOperation ? NscsResourcesConstants.STATUS_NOT_FOUND : NscsResourcesConstants.STATUS_OK;
            }
            final Map<String, Object> networkElementSecurityAttributes = new HashMap<>();
            networkElementSecurityAttributes.put(PROXY_ACCOUNT_DN, newProxyAccountDn);
            nbiHandlerHelper.updateMO(networkElementSecurityMO, networkElementSecurityAttributes);
        } else {
            logger.info("NetworkElementSecurity MO [{}] does not have [{}] attribute", networkElementSecurityMO.getFdn(), PROXY_ACCOUNT_DN);
            status = NscsResourcesConstants.STATUS_NO_CONTENT;
        }
        return buildResourceInstance(nodeNameOrFdn, status, previousProxyAccountDn);
    }

    /**
     * Delete the specified proxy account.
     * 
     * @param proxyAccountDn
     *            the to be deleted not null proxy account.
     * @param isDeleteOperation
     *            true if a delete operation is ongoing: this influences the returned status.
     * @return the returned status.
     */
    private String deleteProxyAccount(final String proxyAccountDn, final Boolean isDeleteOperation) {
        String status = null;
        final Boolean deleted = ldapConfigurationProvider.deleteProxyAccount(proxyAccountDn);
        if (!deleted) {
            status = isDeleteOperation ? NscsResourcesConstants.STATUS_GONE : NscsResourcesConstants.STATUS_OK;
            logger.info("Already deleted proxy account [{}], returning status [{}].", proxyAccountDn, status);
        } else {
            status = NscsResourcesConstants.STATUS_OK;
            logger.info("Successfully deleted proxy account [{}].", proxyAccountDn);
        }
        return status;
    }

    /**
     * Build the resource instance for delete.
     * 
     * @param nodeNameOrFdn
     *            the node name or FDN.
     * @param status
     *            the status.
     * @param previousProxyAccountDn
     *            the previous proxy account.
     * @return the resource instance.
     */
    private NscsResourceInstance buildResourceInstance(final String nodeNameOrFdn, final String status, final String previousProxyAccountDn) {
        final NscsResourceInstance resourceInstance = new NscsResourceInstance();
        resourceInstance.setStatus(status);
        resourceInstance.setResource(NscsResourcesConstants.NODES_RESOURCE);
        resourceInstance.setResourceId(nodeNameOrFdn);
        resourceInstance.setSubResource(NscsResourcesConstants.NODES_RESOURCE_LDAP_SUB_RESOURCE);
        resourceInstance.setSubResourceId(previousProxyAccountDn);
        return resourceInstance;
    }

    /**
     * Check if given instance of NetworkElementSecurity MO has proxyAccountDn attribute.
     * 
     * @param networkElementSecurityMO
     *            the instance of NetworkElementSecurity MO.
     * @return true if the instance of NetworkElementSecurity MO has proxyAccountDn attribute, false otherwise.
     */
    private Boolean hasProxyAccountDnAttribute(final ManagedObject networkElementSecurityMO) {
        return nbiHandlerHelper.isAttributeDefinedForPrimaryTypeMO(networkElementSecurityMO, PROXY_ACCOUNT_DN);
    }

    /**
     * Build the LDAP response for the given protocol family and the given LDAP configuration.
     * 
     * @param protocolFamily
     *            the protocol family.
     * @param ldapConfiguration
     *            the LDAP configuration.
     * @return the LDAP response.
     */
    private NscsLdapResponse buildLdapResponse(final StandardProtocolFamily protocolFamily, final Map<String, Object> ldapConfiguration) {
        final NscsLdapResponse ldapResponse = new NscsLdapResponse();
        ldapResponse.setBaseDn((String) ldapConfiguration.get(LdapConstants.BASE_DN));
        ldapResponse.setBindDn((String) ldapConfiguration.get(LdapConstants.BIND_DN));
        ldapResponse.setBindPassword((String) ldapConfiguration.get(LdapConstants.BIND_PASSWORD));
        if (StandardProtocolFamily.INET.equals(protocolFamily)) {
            ldapResponse.setLdapIpAddress((String) ldapConfiguration.get(LdapConstants.LDAP_IPV4_ADDRESS));
            ldapResponse.setFallbackLdapIpAddress((String) ldapConfiguration.get(LdapConstants.FALLBACK_LDAP_IPV4_ADDRESS));
        } else {
            ldapResponse.setLdapIpAddress((String) ldapConfiguration.get(LdapConstants.LDAP_IPV6_ADDRESS));
            ldapResponse.setFallbackLdapIpAddress((String) ldapConfiguration.get(LdapConstants.FALLBACK_LDAP_IPV6_ADDRESS));
        }
        ldapResponse.setTlsPort((String) ldapConfiguration.get(LdapConstants.TLS_PORT));
        ldapResponse.setLdapsPort((String) ldapConfiguration.get(LdapConstants.LDAPS_PORT));
        return ldapResponse;
    }

}
