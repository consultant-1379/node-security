/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.ejb.timer;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.credentials.MembershipListenerInterface;
import com.ericsson.nms.security.nscs.ejb.credential.AutoReissueServiceBean;
import com.ericsson.nms.security.nscs.pib.configuration.ConfigurationListener;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.classic.EAccessControlBean;
import com.ericsson.oss.itpf.sdk.context.classic.ContextServiceBean;

@LocalBean
@Stateless
public class NodeSecurityAutoReissueTimer {

    private static final Logger log = LoggerFactory.getLogger(NodeSecurityAutoReissueTimer.class);
    private static final String AUTO_REISSUE_CONTEXT_USER_ID = "NO USER DATA";

    @Inject
    private EAccessControlBean eAccessControl;

    @Inject
    MembershipListenerInterface membershipListener;

    @EJB
    AutoReissueServiceBean autoReissueService;

    @Inject
    ConfigurationListener configurationListener;

    @Resource
    private SessionContext ctx;

    @Schedule(minute = "*/30", hour = "*", persistent = false)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void timeoutHandler() {

        eAccessControl.setAuthUserSubject(AUTO_REISSUE_CONTEXT_USER_ID);

        log.info("Starting Auto Reissue");

        if (membershipListener.isMaster() && configurationListener.getPibNeCertAutoRenewalEnabled()) {
            log.info("I'm master. Start Certificate Reissue Auto");
            autoReissueService.process();
        } else {
            log.info("I'm slave.");
        }
        log.info("End Auto Reissue");

        flushContext();

    }

    protected void flushContext() {
        new ContextServiceBean().flushContext();
    }

}
