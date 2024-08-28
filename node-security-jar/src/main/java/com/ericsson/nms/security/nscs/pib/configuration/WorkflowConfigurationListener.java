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
package com.ericsson.nms.security.nscs.pib.configuration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.oss.itpf.modeling.annotation.constraints.NotNull;
import com.ericsson.oss.itpf.sdk.config.annotation.ConfigurationChangeNotification;
import com.ericsson.oss.itpf.sdk.config.annotation.Configured;
import com.ericsson.oss.itpf.sdk.recording.EventLevel;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;

@ApplicationScoped
public class WorkflowConfigurationListener {

    @Inject
    @NotNull
    @Configured(propertyName = "wfCongestionThreshold")
    private int wfCongestionThreshold;

    @Inject
    private SystemRecorder systemRecorder;

    @Inject
    private Logger logger;

    /**
     * This method is responsible to listen to sfwk PIB configuration parameter and to store it.
     *
     * @param wfCongestionThreshold
     *            - the event to process
     */
    public void listenForWfCongestionThreshold(@Observes @ConfigurationChangeNotification(propertyName = "wfCongestionThreshold") final int wfCongestionThreshold) {

        logger.debug("listenForWfCongestionThreshold ");
        if (wfCongestionThreshold > 0) {
            logger.debug(
                    "Configuration change listener invoked since the wfCongestionThreshold value has got changed in the model.The new wfCongestionThreshold is {}",
                    wfCongestionThreshold);
            this.wfCongestionThreshold = wfCongestionThreshold;
            recordEvent("Node security has accepted Configuration Change Notification propertyName : wfCongestionThreshold ",
                    String.valueOf(wfCongestionThreshold));
        }
    }

    /**
     * Records Event
     *
     * @param eventDesc
     *            The event to record
     * @param paramValue the paramValue
     */
    private void recordEvent(final String eventDesc, final String paramValue) {
        systemRecorder.recordEvent(eventDesc, EventLevel.COARSE, "Parameter Value : " + paramValue, "Node security", "");
    }

    /**
     * This method returns the wfCongestionThreshold fetched from nodesecuritymodel.
     *
     * @return wfCongestionThreshold
     *
     */

    public int getPibWfCongestionThreshold() {
        logger.debug("wfCongestionThreshold: return value " + this.wfCongestionThreshold);
        return this.wfCongestionThreshold;
    }

}