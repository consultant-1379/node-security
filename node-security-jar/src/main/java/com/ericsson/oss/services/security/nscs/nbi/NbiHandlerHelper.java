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
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.exception.NscsBadRequestException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.services.security.nscs.dps.NscsDpsUtils;

public class NbiHandlerHelper {

    private Logger logger = LoggerFactory.getLogger(NbiHandlerHelper.class);

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsDpsUtils nscsDpsUtils;

    @Inject
    private NscsModelServiceImpl nscsModelServiceImpl;

    /**
     * Get the normalized node reference for the given node.
     * 
     * @param nodeNameOrFdn
     *            the node name or FDN.
     * @return the normalized node reference or null if given node does not exist.
     */
    public NormalizableNodeReference getNormalizedNodeReference(final String nodeNameOrFdn) {
        final NodeReference nodeRef = new NodeRef(nodeNameOrFdn);
        return readerService.getNormalizedNodeReference(nodeRef);
    }

    /**
     * Get the normalized root MO for the given normalized node reference.
     * 
     * @param normalizedNodeRef
     *            the normalized node reference.
     * @return the normalized root MO.
     */
    public ManagedObject getNormalizedRootMO(final NormalizableNodeReference normalizedNodeRef) {
        return nscsDpsUtils.getNormalizedRootMo(normalizedNodeRef);
    }

    /**
     * Get the protocol family for the given specified IP family. If it is not specified, it is retrieved from the correspondent
     * ConnectivityInformation MO. If it is specified and invalid, an exception is thrown.
     * 
     * @param ipFamily
     *            the IP family.
     * @param normalizedRootMO
     *            the normalized root MO.
     * @param normalizedNodeRef
     *            the target as node reference.
     * @return the protocol family or null if protocol family cannot be retrieved.
     * @throws {@link
     *             NscsBadRequestException} if specified IP family is invalid.
     */
    public StandardProtocolFamily getProtocolFamily(final String ipFamily, final ManagedObject normalizedRootMO,
            final NormalizableNodeReference normalizedNodeRef) {
        StandardProtocolFamily protocolFamily = null;
        if (ipFamily == null) {
            protocolFamily = nscsDpsUtils.getProtocolFamilyFromConnectivityInformationMO(normalizedRootMO, normalizedNodeRef);
        } else {
            try {
                protocolFamily = StandardProtocolFamily.valueOf(ipFamily);
            } catch (final IllegalArgumentException e) {
                final String errorMsg = String.format("invalid IP family [%s]", ipFamily);
                logger.error(errorMsg);
                throw new NscsBadRequestException(errorMsg);
            }
        }
        return protocolFamily;
    }

    /**
     * Get the NetworkElementSecurity MO under the given normalized root MO.
     * 
     * @param normalizedRootMO
     *            the normalized root MO.
     * @return the NetworkElementSecurity MO or null if not existent.
     */
    public ManagedObject getNetworkElementSecurityMO(final ManagedObject normalizedRootMO) {
        return nscsDpsUtils.getNetworkElementSecurityMO(normalizedRootMO);
    }

    /**
     * Check if given attribute is defined for the given not null PrimaryType MO instance.
     * 
     * @param mo
     *            the not null PrimaryType MO instance.
     * @return true if the attribute is defined, false otherwise.
     */
    public Boolean isAttributeDefinedForPrimaryTypeMO(final ManagedObject mo, final String attributeName) {
        // get type, namespace and version of given MO instance
        final String type = mo.getType();
        final String ns = mo.getNamespace();
        final String version = mo.getVersion();
        return nscsModelServiceImpl.isAttributeDefinedForPrimaryTypeMO(ns, type, version, attributeName);
    }

    /**
     * Update the given MO with the given attributes.
     * 
     * @param mo
     *            the MO.
     * @param attributes
     *            the attributes.
     */
    public void updateMO(final ManagedObject mo, final Map<String, Object> attributes) {
        nscsDpsUtils.updateMo(mo, attributes);
    }

}
