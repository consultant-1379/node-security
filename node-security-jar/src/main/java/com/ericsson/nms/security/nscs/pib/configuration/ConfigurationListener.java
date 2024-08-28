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

/**
 * This class is responsible to listen to Sfwk PIB update events and to process them accordingly.
 * 
 * @author eabdsin
 * 
 */
@ApplicationScoped
public class ConfigurationListener {

    @Inject
    @NotNull
    @Configured(propertyName = "neCertAutoRenewalTimer")
    private int neCertAutoRenewalTimer;


    @Inject
    @NotNull
    @Configured(propertyName = "neCertAutoRenewalEnabled")
    private boolean neCertAutoRenewalEnabled;

    @Inject
    @NotNull
    @Configured(propertyName = "neCertAutoRenewalMax")
    private int neCertAutoRenewalMax;

    @Inject
    @NotNull
    @Configured(propertyName = "enforcedIKEv2PolicyProfileID")
    private String enforcedIKEv2PolicyProfileID;

    @Inject
    private SystemRecorder systemRecorder;

    @Inject
    private Logger logger;

    /**
     * This method is responsible to listen to sfwk PIB configuration parameter and to store it.
     * 
     * @param neCertAutoRenewalTimer
     *            - the event to process
     */
    public void listenForNeCertAutoRenewalTimer(@Observes @ConfigurationChangeNotification(propertyName = "neCertAutoRenewalTimer") final int neCertAutoRenewalTimer) {

        logger.info("listenForNeCertAutoRenewalTimer invoked");
        if (neCertAutoRenewalTimer > 0) {
            logger.debug("Configuration change listener invoked since the neCertAutoRenewalTimer value has got changed in the model.The new neCertAutoRenewalTimer is {}",
                    neCertAutoRenewalTimer);
            this.neCertAutoRenewalTimer = neCertAutoRenewalTimer;
            recordEvent("Node security has accepted Configuration Change Notification propertyName : neCertAutoRenewalTimer ", String.valueOf(neCertAutoRenewalTimer));
        }

    }

    /**
     * This method is responsible to listen to sfwk PIB configuration parameter and to store it.
     * 
     * @param neCertAutoRenewalEnabled
     *            - the event to process
     */
    public void listenForNeCertAutoRenewalEnabled(@Observes @ConfigurationChangeNotification(propertyName = "neCertAutoRenewalEnabled") final boolean neCertAutoRenewalEnabled) {

        logger.info("listenForNeCertAutoRenewalEnabled invoked");
            logger.debug("Configuration change listener invoked since the neCertAutoRenewalEnabled value has got changed in the model.The new neCertAutoRenewalEnabled is {}",
                    neCertAutoRenewalEnabled);
            this.neCertAutoRenewalEnabled = neCertAutoRenewalEnabled;
            recordEvent("Node security has accepted Configuration Change Notification propertyName : neCertAutoRenewalEnabled ", String.valueOf(neCertAutoRenewalEnabled));

    }

    /**
     * This method is responsible to listen to sfwk PIB configuration parameter and to store it.
     * 
     * @param neCertAutoRenewalMax
     *            - the event to process
     */
    public void listenForNeCertAutoRenewalMax(@Observes @ConfigurationChangeNotification(propertyName = "neCertAutoRenewalMax") final int neCertAutoRenewalMax) {

        logger.info("listenForNeCertAutoRenewalMax invoked");
//        if (neCertAutoRenewalMax > 0) {
            logger.debug("Configuration change listener invoked since the neCertAutoRenewalMax value has got changed in the model.The new neCertAutoRenewalMax is {}",
                    neCertAutoRenewalMax);
            this.neCertAutoRenewalMax = neCertAutoRenewalMax;
            recordEvent("Node security has accepted Configuration Change Notification propertyName : neCertAutoRenewalMax ", String.valueOf(neCertAutoRenewalMax));
//        }

    }

    /**
     * This method is responsible to listen to PIB configuration parameter and to store it.
     *
     * @param enforcedIKEv2PolicyProfileID
     *            - the event to process
     */

    public void listenForEnforcedIKEv2PolicyProfileID(@Observes @ConfigurationChangeNotification(propertyName = "enforcedIKEv2PolicyProfileID") final String enforcedIKEv2PolicyProfileID) {

        logger.info("listenForEnforcedIKEv2PolicyProfileID invoked");
        this.enforcedIKEv2PolicyProfileID = enforcedIKEv2PolicyProfileID;
        recordEvent("Node security has accepted Configuration Change Notification propertyName : enforcedIKEv2PolicyProfileID ", enforcedIKEv2PolicyProfileID);
    }

    /**
     * This method returns the neCertAutoRenewalTimer fetched from nodesecuritymodel.
     * 
     * @return neCertAutoRenewalTimer
     * 
     */

    public int getPibNeCertAutoRenewalTimer() {
        logger.info("neCertAutoRenewalTimer: return value " + this.neCertAutoRenewalTimer);
        return this.neCertAutoRenewalTimer;
    }

    /**
     * This method returns the neCertAutoRenewalEnabled fetched from nodesecuritymodel.
     * 
     * @return neCertAutoRenewalEnabled
     * 
     */

    public boolean getPibNeCertAutoRenewalEnabled() {
        logger.info("neCertAutoRenewalEnabled: return value " + this.neCertAutoRenewalEnabled);
        return this.neCertAutoRenewalEnabled;
    }

    /**
     * This method returns the neCertAutoRenewalMax fetched from nodesecuritymodel.
     * 
     * @return neCertAutoRenewalMax
     * 
     */

    public int getPibNeCertAutoRenewalMax() {
        logger.info("neCertAutoRenewalMax: return value " + this.neCertAutoRenewalMax);
        return this.neCertAutoRenewalMax;
    }

    /**
     * This method returns the enforcedIKEv2PolicyProfileID fetched from nodesecuritymodel.
     *
     * @return enforcedIKEv2PolicyProfileID
     *
     */

    public String getEnforcedIKEv2PolicyProfileID() {
        logger.info("enforcedIKEv2PolicyProfileID: return value {}", this.enforcedIKEv2PolicyProfileID);
        return this.enforcedIKEv2PolicyProfileID;
    }

    /**
     * Records Event
     * 
     * @param paramValue
     *            The event to record
     */
    private void recordEvent(final String eventDesc, String paramValue) {
        systemRecorder.recordEvent(eventDesc, EventLevel.COARSE, "Parameter Value : " + paramValue, "Node security", "");
    }

    /**
     * For Unit Test purposes only
     * 
     * @param systemRecorder the systemRecorder
     */
    protected void setSystemRecorder(final SystemRecorder systemRecorder) {
        this.systemRecorder = systemRecorder;
    }

}
