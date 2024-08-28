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
package com.ericsson.nms.security.nscs.ejb.credential;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.oss.itpf.sdk.cluster.MembershipChangeEvent;

@RunWith(MockitoJUnitRunner.class)
public class MembershipListenerTest {

    @Mock
    MembershipChangeEvent mce;

    @InjectMocks
    MembershipListener membershipListener;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void testChangeToMaster() {
        Mockito.when(mce.isMaster()).thenReturn(true);

        membershipListener.listenForMembershipChange(mce);

        assertTrue(membershipListener.isMaster());
    }

    @Test
    public void testChangeToSlave() {
        Mockito.when(mce.isMaster()).thenReturn(false);

        membershipListener.listenForMembershipChange(mce);

        assertFalse(membershipListener.isMaster());
    }

    @Test
    public void testStartAsSlave() {
        assertFalse(membershipListener.isMaster());
    }

}
