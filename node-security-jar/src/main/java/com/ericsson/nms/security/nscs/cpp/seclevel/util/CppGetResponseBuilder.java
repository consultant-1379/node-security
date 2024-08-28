/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.cpp.seclevel.util;

import java.util.*;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.Constants;

/**
 * This class defines the methods to build the response for the requested nodes
 *
 * @author xkihari
 */
public class CppGetResponseBuilder {

    @Inject
    NscsLogger nscsLogger;

    /**
     * @param responseData
     *            responseData maintains security level status and local rbac status for the requested nodes
     * @param invalidNodesError
     *            It captures the error nodes information
     * @author xkihari
     * @return the NscsCommandResponse
     */
    public NscsCommandResponse buildResponse(final List<CppGetSecurityLevelDetails> responseData,
            final Map<NodeReference, NscsServiceException> invalidNodesError) {

        final String[] getSecurityLevelHeader = new String[] { CppGetSecurityLevelConstants.NODE_NAME_HEADER,
                CppGetSecurityLevelConstants.NODE_SECURITY_LEVEL_HEADER, CppGetSecurityLevelConstants.LOCAL_AA_MODE,
                CppGetSecurityLevelConstants.ERROR_DETAILS_HEADER, CppGetSecurityLevelConstants.SUGGESTED_SOLUTION };

        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(CppGetSecurityLevelConstants.NO_OF_COLUMNS);
        response.add(getSecurityLevelHeader[0], Arrays.copyOfRange(getSecurityLevelHeader, 1, getSecurityLevelHeader.length));

        for (final CppGetSecurityLevelDetails entry : responseData) {
            String suggestedSolution = "";
            String errorDetailsMsg = "";
            String securityLevel = entry.getSecurityLevelStatus();
            final String localRbacStatus = entry.getLocalRbacStatus();
            if (CppGetSecurityLevelConstants.UNDEFINED_SECURITY_LEVEL.equals(securityLevel)) {
                securityLevel = Constants.ERROR;
            }

            if (CppGetSecurityLevelConstants.NA.equals(localRbacStatus)) {
                errorDetailsMsg = CppGetSecurityLevelConstants.LOCAL_AA_MODE_DETAILS_MSG + "\n";
                suggestedSolution = CppGetSecurityLevelConstants.LOCAL_AA_MODE_SUGGESTED_SOLUTION + "\n";
            } else if (Constants.ERROR.equals(securityLevel)) {
                errorDetailsMsg = errorDetailsMsg.concat(CppGetSecurityLevelConstants.NODE_SECURITY_LEVEL_ERROR_DETAILS_MESSAGE);
                suggestedSolution = suggestedSolution.concat(CppGetSecurityLevelConstants.NODE_SECURITY_LEVEL_SUGGESTED_SOLUTION);
            } else if (errorDetailsMsg.isEmpty()) {
                errorDetailsMsg = CppGetSecurityLevelConstants.NA;
                suggestedSolution = CppGetSecurityLevelConstants.NA;
            }
            response.add(entry.getNodeName(), new String[] { securityLevel, localRbacStatus, errorDetailsMsg, suggestedSolution });
        }

        if (invalidNodesError.size() > 0) {
            for (java.util.Map.Entry<NodeReference, NscsServiceException> entry : invalidNodesError.entrySet()) {
                response.add(entry.getKey().getName(),
                        new String[] { Constants.ERROR, Constants.ERROR, entry.getValue().getMessage(), entry.getValue().getSuggestedSolution() });
            }
        }
        return response;
    }
}
