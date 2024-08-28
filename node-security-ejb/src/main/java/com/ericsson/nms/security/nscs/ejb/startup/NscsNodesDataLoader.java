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
package com.ericsson.nms.security.nscs.ejb.startup;

import javax.annotation.*;
import javax.ejb.*;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.ejb.credential.MembershipListener;
import com.ericsson.oss.services.nscs.nodes.cache.service.interfaces.NscsNodesCacheHandler;

/**
 * Nodes are no more loaded at system startup NO MORE USED
 */

//@Startup
//@Singleton
public class NscsNodesDataLoader {

   /* Logger logger = LoggerFactory.getLogger(NscsNodesDataLoader.class);
    public static final long INITIAL_DELAY = 10 * 60 * 1000;

    @Inject
    NscsNodesDataLoaderProcessor processor;

    @Inject
    MembershipListener listener;

    @Inject
    NscsNodesCacheHandler handler;

    @Resource
    private TimerService timerService;

    private Timer nodeStatusDataLoadTimer;

    @PostConstruct
    public void startAlarmTimer() {
        logger.info("Starting the node status data load timer");
        nodeStatusDataLoadTimer = timerService.createSingleActionTimer(INITIAL_DELAY, createNonPersistentTimerConfig());
    }

    private TimerConfig createNonPersistentTimerConfig() {
        final TimerConfig timerConfig = new TimerConfig();
        timerConfig.setPersistent(false);
        return timerConfig;
    }

    @Timeout
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void timeOut() {

        try {
            if (listener.isMaster() && handler.isCacheEmpty()) {
                processor.load();
            }

        } catch (final Exception e) {
            logger.error(getClass() + "·loadCache: error loading nodes from DPS {}", e.getMessage());
        }

    }

    @PreDestroy
    public void cleanUp() {
        if (nodeStatusDataLoadTimer != null) {
            nodeStatusDataLoadTimer.cancel();
        }
    }*/
}