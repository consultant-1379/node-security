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
package com.ericsson.nms.security.nscs.ejb.timer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.credentials.MembershipListenerInterface;
import com.ericsson.nms.security.nscs.api.exception.CertificateReissueWfException;
import com.ericsson.nms.security.nscs.ejb.credential.AutoReissueServiceBean;
import com.ericsson.nms.security.nscs.pib.configuration.ConfigurationListener;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.classic.EAccessControlBean;


@RunWith(MockitoJUnitRunner.class)
public class NodeSecurityAutoReissueTimerTest {

    @Mock
    EAccessControlBean eAccessControl;

    @Mock
    MembershipListenerInterface membershipListener;

    @Mock
    AutoReissueServiceBean autoReissueService;

    @Mock
    ConfigurationListener configurationListener;


    @InjectMocks
    NodeSecurityAutoReissueTimerStub nodeSecurityAutoReissueTimer;

    @Test
    public void testSuccessfully() {
        Mockito.when(membershipListener.isMaster()).thenReturn(true);
        Mockito.when(configurationListener.getPibNeCertAutoRenewalEnabled()).thenReturn(true);
        nodeSecurityAutoReissueTimer.timeoutHandler();
    }

    @Test
    public void testSuccessfully2() {
        Mockito.when(membershipListener.isMaster()).thenReturn(false);
        Mockito.when(configurationListener.getPibNeCertAutoRenewalEnabled()).thenReturn(true);
        nodeSecurityAutoReissueTimer.timeoutHandler();
    }

    @Test
    public void testSuccessfully3() {
        Mockito.when(membershipListener.isMaster()).thenReturn(false);
        Mockito.when(configurationListener.getPibNeCertAutoRenewalEnabled()).thenReturn(false);
        nodeSecurityAutoReissueTimer.timeoutHandler();
    }

    @Test
    public void testSuccessfully4() {
        Mockito.when(membershipListener.isMaster()).thenReturn(true);
        Mockito.when(configurationListener.getPibNeCertAutoRenewalEnabled()).thenReturn(false);
        nodeSecurityAutoReissueTimer.timeoutHandler();
    }

    @Test(expected = CertificateReissueWfException.class)
    public void testFailed() {
        Mockito.when(membershipListener.isMaster()).thenReturn(true);
        Mockito.when(configurationListener.getPibNeCertAutoRenewalEnabled()).thenReturn(true);
        Mockito.doThrow(CertificateReissueWfException.class).when(autoReissueService).process();
        nodeSecurityAutoReissueTimer.timeoutHandler();
    }

}

class NodeSecurityAutoReissueTimerStub extends NodeSecurityAutoReissueTimer {
    @Override
    protected void flushContext() {}
}

