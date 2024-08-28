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

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.data.*;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.RealTimeSecLog;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;

/**
 * This class provides the implementation to get the values of Real Time Sec Log on node.
 * 
 * @author xvekkar
 *
 */
public class GetRtselConfigurationDetailsImpl {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    /**
     * Get the Map  for rtsel, that will have the attributes and its respective values from the DPS for CPP(ERBS/MGW) platform type.
     * 
     * @param normNode
     *            i.e NormalizableNodeReference a reference to MeContext or similar Managed Object.
     * 
     * @return map having attributes and its respective value for Real Time Sec Log.
     * 
     * @throws MissingMoException exception throws for GetRtselConfigurationDetailsImpl
     * 
     */
    public Map<String, Object> getRtselConfigurationDetails(final NormalizableNodeReference normNode) throws MissingMoException {
        nscsLogger.info("Start of GetRtselConfigurationDetailsImpl::getRtselConfigurationDetails method: normNodeFdn[{}]", normNode.getFdn());
        final String mirrorRootFdn = normNode.getFdn();
        final Mo realTimeSecLogMo = Model.ME_CONTEXT.managedElement.systemFunctions.security.realTimeSecLog;
        final String requestedAttrs[] = { RealTimeSecLog.CONN_ATTEMPT_TIME_OUT, RealTimeSecLog.EXT_SERVER_APP_NAME, RealTimeSecLog.FEATURE_STATE, RealTimeSecLog.EXT_SERVER_LOG_LEVEL,
                RealTimeSecLog.STATUS, RealTimeSecLog.EXT_SERVER_LIST_CONFIG };
        final Map<String, Object> rtselDetails = new HashMap<String, Object>();
        final String readMessage = NscsLogger.stringifyReadParams(mirrorRootFdn, realTimeSecLogMo.type(), requestedAttrs);
        final String moFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, realTimeSecLogMo.type(), realTimeSecLogMo.namespace(), rtselDetails, requestedAttrs);

        if (moFdn == null) {
            nscsLogger.info("Error while reading MO {}", readMessage);
            throw new MissingMoException(mirrorRootFdn, realTimeSecLogMo.type());
        }

        nscsLogger.debug("rtselDetailsMap in GetRtselConfigurationDetailsImpl::getRtselConfigurationDetails method: [{}]", rtselDetails);
        nscsLogger.info("End of GetRtselConfigurationDetailsImpl::getRtselConfigurationDetails method");
        return rtselDetails;
    }

}
