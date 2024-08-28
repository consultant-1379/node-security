/*------------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2018
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.dps.eventhandling;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.exception.DatabaseUnavailableException;
import com.ericsson.oss.itpf.sdk.core.retry.RetriableCommand;
import com.ericsson.oss.itpf.sdk.core.retry.RetriableCommandException;
import com.ericsson.oss.itpf.sdk.core.retry.RetryContext;
import com.ericsson.oss.itpf.sdk.core.retry.RetryManager;
import com.ericsson.oss.itpf.sdk.core.retry.RetryPolicy;
import com.ericsson.oss.itpf.sdk.eventbus.classic.EventConsumerBean;
import com.ericsson.oss.services.model.security.channel.SecurityDpsNotificationChannel;
import com.ericsson.oss.services.security.nscs.dps.availability.NscsDpsAvailabilityStatus;

/**
 * Subscribes the listener to the Clustered-security-dps-notification-event queue
 */
@Singleton
@Startup
public class NscsDpsSecurityEventSubscriber {

    private static final int TIMER_STARTUP_DELAY_IN_MILLISECONDS = 240000;
    private static final Integer MAX_ATTEMPTS = 90;
    private static final Integer WAIT_TIME_IN_SECONDS = 2;
    private static final String SECURITY_DPS_NOTIFICATION_CHANNEL_FILTER = "bucketName = 'Live' AND " + "(type ='CmFunction' "
            + "OR type = 'Security' " + "OR type = 'ComConnectivityInformation' " + "OR type = 'CppConnectivityInformation'"
            + "OR type = 'RncFeature')";
    private static final String LOG_TAG = "[NSCS_STARTUP] ";

    private final Logger logger = LoggerFactory.getLogger(NscsDpsSecurityEventSubscriber.class);

    private EventConsumerBean securityEventConsumerBean;

    @Inject
    private TimerService timerService;

    @Inject
    private RetryManager retryManager;

    @Inject
    private NscsDpsAvailabilityStatus nscsDpsAvailabilityStatus;

    @Inject
    private NscsDpsSecurityEventListener nscsDpsSecurityEventListener;

    @PostConstruct
    public void scheduleNscsDpsSecurityEventListenerSubscription() {
        logger.info(LOG_TAG + "scheduling DPS security event listener subscription in " + TIMER_STARTUP_DELAY_IN_MILLISECONDS + " ms");
        securityEventConsumerBean = new EventConsumerBean(SecurityDpsNotificationChannel.CHANNEL_URI);
        securityEventConsumerBean.setFilter(SECURITY_DPS_NOTIFICATION_CHANNEL_FILTER);
        initializeTimer(TIMER_STARTUP_DELAY_IN_MILLISECONDS);
    }

    @PreDestroy
    public void unsubscribeDpsSecurityEventListener() {
        stopListener();
    }

    @Timeout
    public void suscribeDpsSecurityEventListener(final Timer timer) {
        final RetryPolicy retryPolicy = RetryPolicy.builder().attempts(MAX_ATTEMPTS).waitInterval(WAIT_TIME_IN_SECONDS, TimeUnit.SECONDS)
                .retryOn(Exception.class).build();
        try {
            retryManager.executeCommand(retryPolicy, new SubscribeDpsSecurityEventListenerCommand());
        } catch (final RetriableCommandException e) {
            logger.error(LOG_TAG + "DPS security event listener subscription failed within " + MAX_ATTEMPTS * WAIT_TIME_IN_SECONDS + " seconds");
            logger.error(LOG_TAG + "occurred exception[" + e.getClass().getCanonicalName() + "] message[" + e.getMessage() + "]");
        } catch (final Exception e) {
            logger.error(LOG_TAG + "DPS security event listener subscription failed");
            logger.error(LOG_TAG + "occurred unexpected exception[" + e.getClass().getCanonicalName() + "] message[" + e.getMessage() + "]");
        }
    }

    /**
     * Command to subscribe the DPS security event listener implementing the {@link RetriableCommand} interface
     */
    class SubscribeDpsSecurityEventListenerCommand implements RetriableCommand<Object> {

        @Override
        public Object execute(final RetryContext retryContext) throws Exception {
            final int currentAttempt = retryContext.getCurrentAttempt();
            final String message = LOG_TAG + "subscribing DPS security event listener. Attempt " + currentAttempt + " of " + MAX_ATTEMPTS;
            if (currentAttempt == 1) {
                logger.info(message);
            } else {
                logger.warn(message);
            }
            if (!nscsDpsAvailabilityStatus.isDpsAvailable()) {
                logger.warn(LOG_TAG + "DPS not yet available");
                throw new DatabaseUnavailableException();
            }
            startListener();
            // API needs a return
            return null;
        }

    }

    public void startListener() {
        securityEventConsumerBean.startListening(nscsDpsSecurityEventListener);
    }

    public void stopListener() {
        securityEventConsumerBean.stopListening();
    }

    /**
     * Initializes startup timer
     *
     * @param timerStartupDelayInMs
     */
    private void initializeTimer(final int timerStartupDelayInMs) {
        final TimerConfig timerConfig = new TimerConfig();
        timerConfig.setPersistent(false);
        timerService.createSingleActionTimer(timerStartupDelayInMs, timerConfig);
    }

}
