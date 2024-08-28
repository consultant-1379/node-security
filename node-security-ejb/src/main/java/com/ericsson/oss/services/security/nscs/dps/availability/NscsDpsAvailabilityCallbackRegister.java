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
package com.ericsson.oss.services.security.nscs.dps.availability;

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

import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.core.retry.RetriableCommand;
import com.ericsson.oss.itpf.sdk.core.retry.RetriableCommandException;
import com.ericsson.oss.itpf.sdk.core.retry.RetryContext;
import com.ericsson.oss.itpf.sdk.core.retry.RetryManager;
import com.ericsson.oss.itpf.sdk.core.retry.RetryPolicy;

/**
 * Registers DPS availability callback
 */
@Singleton
@Startup
public class NscsDpsAvailabilityCallbackRegister {

    private static final int TIMER_STARTUP_DELAY_IN_MILLISECONDS = 120000;
    private static final Integer MAX_ATTEMPTS = 90;
    private static final Integer WAIT_TIME_IN_SECONDS = 2;
    private static final String LOG_TAG = "[NSCS_STARTUP] ";

    private final Logger logger = LoggerFactory.getLogger(NscsDpsAvailabilityCallbackRegister.class);

    @EServiceRef
    private DataPersistenceService dataPersistenceService;

    @Inject
    private NscsDpsAvailabilityCallback nscsDpsAvailabilityCallback;

    @Inject
    private TimerService timerService;

    @Inject
    private RetryManager retryManager;

    @PostConstruct
    public void scheduleNscsDpsAvailabilityCallbackRegistration() {
        logger.info(LOG_TAG + "scheduling DPS availability callback registration in " + TIMER_STARTUP_DELAY_IN_MILLISECONDS + " ms");
        initializeTimer(TIMER_STARTUP_DELAY_IN_MILLISECONDS);
    }

    @PreDestroy
    public void deregisterNscsDpsAvailabilityCallback() {
        dataPersistenceService.deregisterDpsAvailabilityCallback("Deregistering NSCS DPS availability callback");
    }

    @Timeout
    public void registerDpsAvailabilityCallback(final Timer timer) {
        final RetryPolicy retryPolicy = RetryPolicy.builder().attempts(MAX_ATTEMPTS).waitInterval(WAIT_TIME_IN_SECONDS, TimeUnit.SECONDS)
                .retryOn(Exception.class).build();
        try {
            retryManager.executeCommand(retryPolicy, new RegisterDpsAvailabilityCallbackCommand());
        } catch (final RetriableCommandException e) {
            logger.error(LOG_TAG + "DPS availability callback registration failed within " + MAX_ATTEMPTS * WAIT_TIME_IN_SECONDS + " seconds");
            logger.error(LOG_TAG + "occurred exception[" + e.getClass().getCanonicalName() + "] message[" + e.getMessage() + "]");
        } catch (final Exception e) {
            logger.error(LOG_TAG + "DPS availability callback registration failed");
            logger.error(LOG_TAG + "occurred unexpected exception[" + e.getClass().getCanonicalName() + "] message[" + e.getMessage() + "]");
        }
    }

    /**
     * Command to register the DPS availability callback implementing the {@link RetriableCommand} interface
     */
    class RegisterDpsAvailabilityCallbackCommand implements RetriableCommand<Object> {

        @Override
        public Object execute(final RetryContext retryContext) throws Exception {
            final int currentAttempt = retryContext.getCurrentAttempt();
            final String message = LOG_TAG + "registering DPS availability callback. Attempt " + currentAttempt + " of " + MAX_ATTEMPTS;
            if (currentAttempt == 1) {
                logger.info(message);
            } else {
                logger.warn(message);
            }
            dataPersistenceService.registerDpsAvailabilityCallback(nscsDpsAvailabilityCallback);
            // API needs a return
            return null;
        }

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
