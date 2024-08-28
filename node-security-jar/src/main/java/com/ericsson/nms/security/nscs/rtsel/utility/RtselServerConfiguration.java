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

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.RealTimeSecLog;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;

/**
 * This class provides the implementation to get the values of Real Time Sec Log.
 * 
 * @author xchowja
 */
public class RtselServerConfiguration {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    /**
     * Get the List of MAP for RTSEL, that will have the attributes and its respective values from the DPS for CPP(ERBS/MGW) platform type.
     * 
     * @param normNode
     *            i.e NormalizableNodeReference a reference to MeContext or similar Managed Object.
     * @return map having attributes and its respective value for Real Time Sec Log.
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getRtselServerDetails(final NormalizableNodeReference normNode) {
        final String mirrorRootFdn = normNode.getFdn();
        final Mo realTimeSecLogMo = Model.ME_CONTEXT.managedElement.systemFunctions.security.realTimeSecLog;
        final String requestedAttrs[] = { RealTimeSecLog.EXT_SERVER_LIST_CONFIG };
        final Map<String, Object> attributes = new HashMap<String, Object>();
        final String realTimeSecLogMoFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, realTimeSecLogMo.type(), realTimeSecLogMo.namespace(), attributes, requestedAttrs);
        if (realTimeSecLogMoFdn == null || realTimeSecLogMoFdn.isEmpty()) {
            final String errorMessage = String.format("Null or empty mirrorRootFdn[%s] for realTimeSecLogMo[%s] and requestedAttrs[%s]", mirrorRootFdn, realTimeSecLogMo.type(), requestedAttrs);
            nscsLogger.error("Error while reading RealTimeSecLog MO {}", errorMessage);
            return null;
        }
        final List<Map<String, Object>> serverDetailsFromNode = (List<Map<String, Object>>) attributes.get(RealTimeSecLog.EXT_SERVER_LIST_CONFIG);
        nscsLogger.debug("ServerName details from the node:{}", serverDetailsFromNode);
        return serverDetailsFromNode;
    }
}
