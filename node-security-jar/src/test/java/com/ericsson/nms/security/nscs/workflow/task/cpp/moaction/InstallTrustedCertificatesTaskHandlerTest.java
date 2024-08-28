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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.ericsson.nms.security.smrs.api.exception.SmrsDirectoryException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.InstallTrustedCertificatesTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

/**
 * Unit test for InitCertEnrollmentTaskHandlerTest
 * 
 * @author emaynes
 */

@RunWith(MockitoJUnitRunner.class)
public class InstallTrustedCertificatesTaskHandlerTest {

    private static final String FDN = "MeContext=ERBS_001";
    private static final String NODE_TYPE = "ERBS";

    @Spy
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @InjectMocks
    private InstallTrustedCertificatesTaskHandler handlerUnderTest;

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

    @Test
    public void handlerInvocationTest() throws SmrsDirectoryException, UnknownHostException, CppSecurityServiceException, CertificateException {

        final InstallTrustedCertificatesTask task = mock(InstallTrustedCertificatesTask.class);
        when(task.getNodeFdn()).thenReturn(FDN);
        when(readerService.getTargetType(Mockito.anyString())).thenReturn(NODE_TYPE);
        when(readerService.getNormalizableNodeReference(Mockito.any(NodeReference.class))).thenReturn(normalizable);
        final TrustStoreInfo tsi = new TrustStoreInfo(TrustedCertCategory.CORBA_PEERS, new HashSet<CertSpec>(), new ArrayList<SmrsAccountInfo>(),
                DigestAlgorithm.SHA1);
        when(securityService.getTrustStoreForNode(any(TrustedCertCategory.class), Mockito.any(NodeRef.class), eq(true),
                (TrustCategoryType)Mockito.any())).thenReturn(tsi);
        handlerUnderTest.processTask(task);

        verify(task, atLeast(1)).getNodeFdn();
        verify(securityService, atLeast(1)).getTrustStoreForNode(any(TrustedCertCategory.class), Mockito.any(NodeRef.class), eq(true),
                (TrustCategoryType)Mockito.any());
    }
}
