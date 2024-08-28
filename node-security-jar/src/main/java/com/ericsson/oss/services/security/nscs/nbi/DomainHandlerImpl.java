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
import java.util.Locale;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.exception.NetworkElementSecurityNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.NodeDoesNotExistException;
import com.ericsson.nms.security.nscs.api.exception.NscsBadRequestException;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.EnrollmentRequestInfo;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.enrollmentinfo.request.NodeDetailsValidator;
import com.ericsson.nms.security.nscs.enrollmentinfo.request.model.NodeDetails;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.EnrollmentInfoProvider;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.EnrollmentInfo;
import com.ericsson.nms.security.nscs.enrollmentinfo.service.EnrollmentInfoServiceException;
import com.ericsson.nms.security.nscs.iscf.IscfCancelHandler;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.services.security.nscs.nbi.resources.NscsResourceInstance;
import com.ericsson.oss.services.security.nscs.nbi.resources.NscsResourcesConstants;

public class DomainHandlerImpl {

    private static final Logger logger = LoggerFactory.getLogger(DomainHandlerImpl.class);

    @Inject
    private NbiHandlerHelper nbiHandlerHelper;

    @Inject
    private NodeDetailsValidator nodeDetailsValidator;

    @Inject
    private EnrollmentInfoProvider enrollmentInfoProvider;

    @Inject
    private IscfCancelHandler iscfCancelHandler;

    /**
     * Generate enrollment info for the given node details.
     * 
     * @param nodeNameOrFdn
     *            the node name or FDN.
     * @param domainName
     *            the domain name.
     * @param ipFamily
     *            the IP family.
     * @param nodeDetails
     *            the node details.
     * @return the enrollment info.
     */
    public EnrollmentInfo generateEnrollmentInfo(final String nodeNameOrFdn, final String domainName, final String ipFamily,
            final NodeDetails nodeDetails) {

        logger.info("generateEnrollmentInfo: start for {} and {} and {} and {}", nodeNameOrFdn, domainName, ipFamily, nodeDetails);

        final NormalizableNodeReference normalizedNodeRef = nbiHandlerHelper.getNormalizedNodeReference(nodeNameOrFdn);
        if (normalizedNodeRef == null) {
            throw new NodeDoesNotExistException(nodeNameOrFdn);
        }
        nodeDetailsValidator.validateDomainName(domainName);
        final ManagedObject normalizedRootMO = nbiHandlerHelper.getNormalizedRootMO(normalizedNodeRef);
        final StandardProtocolFamily protocolFamily = nbiHandlerHelper.getProtocolFamily(ipFamily, normalizedRootMO, normalizedNodeRef);
        if (protocolFamily == null) {
            throw new NscsBadRequestException("Unknown IP family");
        }
        // set the protocol family in the requested node details
        nodeDetails.setIpVersion(protocolFamily);
        final ManagedObject networkElementSecurityMO = nbiHandlerHelper.getNetworkElementSecurityMO(normalizedRootMO);
        if (networkElementSecurityMO == null) {
            throw new NetworkElementSecurityNotfoundException();
        }

        final EnrollmentRequestInfo enrollmentRequestInfo = nodeDetailsValidator.validateNbiRequest(normalizedNodeRef, nodeDetails);
        EnrollmentInfo enrollmentInfo;
        try {
            enrollmentInfo = enrollmentInfoProvider.getEnrollmentInfo(enrollmentRequestInfo, true);
        } catch (final EnrollmentInfoServiceException e) {
            throw new UnexpectedErrorException(e);
        }
        return enrollmentInfo;
    }

    /**
     * Delete enrollment info for the given node and domain.
     * 
     * @param nodeNameOrFdn
     *            the node name or FDN.
     * @param domainName
     *            the domain name.
     * @return the deleted resource instance.
     */
    public NscsResourceInstance deleteEnrollmentInfo(final String nodeNameOrFdn, final String domainName) {

        logger.info("deleteEnrollmentInfo: start for {} and {}", nodeNameOrFdn, domainName);

        final NormalizableNodeReference normalizedNodeRef = nbiHandlerHelper.getNormalizedNodeReference(nodeNameOrFdn);
        if (normalizedNodeRef == null) {
            throw new NodeDoesNotExistException(nodeNameOrFdn);
        }
        nodeDetailsValidator.validateDomainName(domainName);
        final String endEntityName = String.format("%s-%s", normalizedNodeRef.getName(), domainName.toLowerCase(Locale.ENGLISH));
        logger.info("deleteEnrollmentInfo: endEntityName {}", endEntityName);
        iscfCancelHandler.cancel(endEntityName);

        return buildDeletedResourceInstance(nodeNameOrFdn, endEntityName);
    }

    /**
     * Build the deleted resource instance.
     * 
     * @param nodeNameOrFdnthe
     *            node name or FDN.
     * @param endEntityName
     *            the name of the deleted End Entity.
     * @return the deleted resource instance.
     */
    private NscsResourceInstance buildDeletedResourceInstance(final String nodeNameOrFdn, final String endEntityName) {
        final NscsResourceInstance resourceInstance = new NscsResourceInstance();
        resourceInstance.setStatus(NscsResourcesConstants.STATUS_OK);
        resourceInstance.setResource(NscsResourcesConstants.NODES_RESOURCE);
        resourceInstance.setResourceId(nodeNameOrFdn);
        resourceInstance.setSubResource(NscsResourcesConstants.NODES_RESOURCE_DOMAINS_SUB_RESOURCE);
        resourceInstance.setSubResourceId(endEntityName);
        return resourceInstance;
    }

}
