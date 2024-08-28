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
package com.ericsson.nms.security.nscs.workflow.task.cpp.moaction;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.enums.*;
import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.model.SmrsAccountInfo;
import com.ericsson.nms.security.nscs.cpp.model.TrustStoreInfo;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.param.AttributeSpecBuilder;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.smrs.api.exception.SmrsDirectoryException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.IssueInitTrustedCertEnrollmentTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

@RunWith(MockitoJUnitRunner.class)
public class IssueInitTrustedCertEnrollmentTaskHandlerTest {

    private static final String FDN = "MeContext=ERBS_001";
    private static final String NODE_TYPE = "ERBS";
    private static final String netsimM2mUserName = "mm-cert--1536871905";
    private static final String m2mHiddenWord = "HiddenWord";

    @Mock
    private NscsLogger nscsLogger;

    @InjectMocks
    private IssueInitTrustedCertEnrollmentTaskHandler handlerUnderTest;

    @Mock
    MOActionService moAction;

    @Mock
    CppSecurityService securityService;

    @Mock
    AttributeSpecBuilder builder;

    @Mock
    NscsCMReaderService readerService;

    @Mock
    NormalizableNodeReference normalizable;

    @Mock
    SystemRecorder systemRecorder;

    @Test
    public void handlerInvocationTest() throws SmrsDirectoryException, UnknownHostException, CppSecurityServiceException, CertificateException {

        final IssueInitTrustedCertEnrollmentTask task = mock(IssueInitTrustedCertEnrollmentTask.class);
        when(task.getNodeFdn()).thenReturn(FDN);
        when(task.getTrustCategory()).thenReturn(TrustedCertCategory.CORBA_PEERS);
        when(readerService.getTargetType(Mockito.anyString())).thenReturn(NODE_TYPE);
        when(readerService.getNormalizableNodeReference(Mockito.any(NodeReference.class))).thenReturn(normalizable);

        final List<SmrsAccountInfo> smrsAccountInfoList = new ArrayList<>();
        smrsAccountInfoList.add(new SmrsAccountInfo(netsimM2mUserName, m2mHiddenWord.toCharArray(), "localhost", "/home/smrs", "ERBS"));
        final TrustStoreInfo tsi = new TrustStoreInfo(TrustedCertCategory.CORBA_PEERS, new HashSet<CertSpec>(), smrsAccountInfoList, DigestAlgorithm.SHA1);
        when(securityService.getTrustStoreForNode(any(TrustedCertCategory.class), Mockito.any(NodeRef.class), eq(true), (TrustCategoryType)Mockito.any())).thenReturn(tsi);
        handlerUnderTest.processTask(task);

        verify(task, atLeast(1)).getNode();
        verify(securityService, atLeast(1)).getTrustStoreForNode(any(TrustedCertCategory.class), Mockito.any(NodeRef.class), eq(true), (TrustCategoryType)Mockito.any());
    }
}
