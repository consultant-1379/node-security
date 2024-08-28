/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.rtsel.utility;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.*;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;

/**
 * This is utility for RTSEL
 * 
 * @author tcsramc
 * 
 */
public class RtselUtility {
    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private NscsCMWriterService writer;

    /**
     * This method sets the enrollment mode in NetworkElementSecurity(NES) MO.
     * 
     * @param enrollmentMode
     *            enrollment mode given in the input.
     * @param nodeFdn
     *            Node
     */
    public void setEnrollmentMode(final String enrollmentMode, final String nodeFdn) {

        try {

            final NodeReference nodeRef = new NodeRef(nodeFdn);
            final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(nodeRef);

            nscsLogger.info("Updating Enrollment Mode in NetworkElementSecurity MO {}", nodeFdn);

            final String networkElementSecurityFdn = Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.withNames(normNode.getName()).fdn();

            nscsLogger.info("Updating Enrollment Mode in NetworkElementSecurity MO {} NES fdn", networkElementSecurityFdn);

            writer.withSpecification(networkElementSecurityFdn).setAttribute(NetworkElementSecurity.ENROLLMENT_MODE, enrollmentMode).updateMO();

            nscsLogger.info("EnrolmentMode succesfully set for node {}", nodeFdn);

        } catch (final Exception exception) {
            nscsLogger.info("Update of Enrollment mode in NetworkElementSecurity MO failed!", exception);

        }

    }
}
