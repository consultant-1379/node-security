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

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;

/**
 * This Utility is responsible for Validating the Node eligibility for Ldap Configuration.
 *
 * @author xsrirko
 */
public class LdapNodeValidatorUtility {

    @Inject
    private NscsCMReaderService reader;

    @Inject
    NodeValidatorUtility nodeValidatorUtility;

    @Inject
    private Logger logger;

    /**
     * This Method checks whether the Node is Valid for Ldap Configuration or not.
     *
     * @param nodeFdn
     * @return
     */
    public boolean validateNodeForLdapConfiguration(final String nodeFdn) {

        final NodeReference nodeRef = new NodeRef(nodeFdn);
        final NormalizableNodeReference normalizedReference = reader.getNormalizableNodeReference(nodeRef);

        if (null == normalizedReference) {
            logger.warn("Node [{}]  is not normalized.", nodeRef.getFdn());
            throw new InvalidNodeNameException();
        } else {
            if (!nodeValidatorUtility.isNodeSynchronized(normalizedReference)) {
                logger.warn("Node [{}]  is not synchronized", normalizedReference.getFdn());
                throw new NodeNotSynchronizedException();
            }
        }
        if (!nodeValidatorUtility.isCliCommandSupported(normalizedReference, NscsCapabilityModelService.LDAP_COMMAND)) {
            throw new UnsupportedNodeTypeException()
                    .setSuggestedSolution(NscsErrorCodes.PLEASE_CHECK_NSCS_CAPABILITY_MODEL_FOR_UNSUPPORTED_SECADM_CLI_COMMAND);
        }
        return true;
    }
}
