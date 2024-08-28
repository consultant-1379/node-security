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

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.exception.CertificateReissueWfException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.handler.command.impl.CertificateReissueAuto;

@RunWith(MockitoJUnitRunner.class)
public class AutoReissueServiceBeanTest {

    @Mock
    CertificateReissueAuto certificateReissueAuto;

    @InjectMocks
    AutoReissueServiceBean autoReissueServiceBean;

    @Test
    public void testSuccessfully() throws NscsServiceException {
        autoReissueServiceBean.process();
        assertTrue(true);
    }

    @Test(expected = CertificateReissueWfException.class)
    public void testCertificateReissueWfException() throws NscsServiceException {
        Mockito.doThrow(CertificateReissueWfException.class).when(certificateReissueAuto).process();
        autoReissueServiceBean.process();
        assertTrue(true);
    }

}
