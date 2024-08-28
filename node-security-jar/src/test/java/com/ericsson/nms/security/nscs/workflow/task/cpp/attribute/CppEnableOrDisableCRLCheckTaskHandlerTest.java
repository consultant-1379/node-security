/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.cpp.attribute;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelMock;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CppManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TrustCategory;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService.WriterSpecificationBuilder;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NSCSComEcimNodeUtility;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.CPPEnableOrDisableCRLCheckTask;

/**
 * @author xchowja
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CppEnableOrDisableCRLCheckTaskHandlerTest {

    private static final String CPP_NODE_NAME = "CPP-NODE-123";
    private static final String CPP_FDN_WITHOUT_ME_CONTEXT = "ManagedElement=" + CPP_NODE_NAME;
    private static final String CPP_OSS_MODEL_IDENTITY = "397-5538-366";

    private static final String CPP_NODE_ROOT_FDN = String.format("ManagedElement=%s", CPP_NODE_NAME);
    private static final String CPP_NODE_CERT_M_FDN = String.format("%s,SystemFunctions=1", CPP_NODE_ROOT_FDN);
    private static final String CPP_NODE_SECURITY_FDN = String.format("%s,Security=Security", CPP_NODE_CERT_M_FDN);

    private static final NodeReference NODE = new NodeRef(CPP_FDN_WITHOUT_ME_CONTEXT);
    private final NormalizableNodeReference mockNormNode = new MockNormalizableNodeRef();
    private final NodeReference cppNodeRef = new NodeRef(CPP_NODE_NAME);

    @Spy
    private final Logger logger = LoggerFactory.getLogger(CheckTrustedOAMAlreadyInstalledTaskHandler.class);

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    private NscsCMReaderService nscsCMReaderService;

    @Mock
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Mock
    private NSCSComEcimNodeUtility nscsComEcimNodeUtility;

    @Mock
    private NscsCMWriterService nscsCMWriterService;

    @InjectMocks
    private CPPEnableOrDisableCRLCheckTaskHandler cppEnableORDisableCRLCheckTaskHandler;

    @InjectMocks
    NscsCapabilityModelService nscsCapabilityModelServiceTest;

    @Mock
    private NormalizableNodeReference cppNodeNormNodeRef;

    @Mock
    private NormalizableNodeReference cppNormNodeRef;

    @InjectMocks
    NscsCapabilityModelMock capabilityModel;

    private CPPEnableOrDisableCRLCheckTask cppEnableORDisableCRLCheckTask;
    private String certType = CertificateType.IPSEC.toString();
    CppManagedElement cppManagedElement = null;

    private static final String CRL_CHECK_ACTIVATED = "ACTIVATED";
    private static final String CRL_CHECK_DEACTIVATED = "DEACTIVATED";

    @Mock
    TrustCategory trustCategory;

    @Mock
    WriterSpecificationBuilder writerSpecificationBuilder;

    @Before
    public void setup() {

        when(nscsCMReaderService.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(mockNormNode);

        doReturn(CPP_NODE_NAME).when(cppNormNodeRef).getName();
        doReturn(CPP_FDN_WITHOUT_ME_CONTEXT).when(cppNormNodeRef).getFdn();
        doReturn("RadioNode").when(cppNormNodeRef).getNeType();
        doReturn(CPP_OSS_MODEL_IDENTITY).when(cppNormNodeRef).getOssModelIdentity();
        doReturn(cppNodeRef).when(cppNormNodeRef).getNormalizedRef();
        doReturn(true).when(cppNormNodeRef).hasNormalizedRef();
        doReturn(cppNormNodeRef).when(nscsCMReaderService).getNormalizedNodeReference(cppNodeRef);

        when(nscsCMReaderService.getNormalizableNodeReference(cppNodeRef)).thenReturn(cppNormNodeRef);
        doReturn(CPP_NODE_NAME).when(cppNormNodeRef).getName();
        doReturn(CPP_NODE_ROOT_FDN).when(cppNormNodeRef).getFdn();

        cppManagedElement = Model.ME_CONTEXT.rncManagedElement;
        when(nscsCapabilityModelService.getMirrorRootMo(eq(cppNormNodeRef))).thenReturn(cppManagedElement);

        final String CRL_CHECK_ENABLED = "CRLCheckEnabled";
        final NscsCMWriterService.WriterSpecificationBuilder specificationBuilder = org.mockito.Mockito
                .mock(NscsCMWriterService.WriterSpecificationBuilder.class);
        when(specificationBuilder.setNotNullAttribute(any(String.class), any(Object.class))).thenReturn(specificationBuilder);
        when(specificationBuilder.setFdn(CPP_NODE_SECURITY_FDN)).thenReturn(specificationBuilder);
        when(nscsCMWriterService.withSpecification(CPP_NODE_SECURITY_FDN)).thenReturn(specificationBuilder);
        when(specificationBuilder.setAttribute(TrustCategory.CRL_CHECK, CRL_CHECK_ENABLED)).thenReturn(specificationBuilder);
        when(nscsComEcimNodeUtility.getTrustCategoryFdn(CPP_NODE_ROOT_FDN, cppManagedElement, "IPSEC", cppNormNodeRef))
                .thenReturn(CPP_NODE_SECURITY_FDN);
        when(nscsComEcimNodeUtility.getTrustCategoryFdn(CPP_NODE_ROOT_FDN, cppManagedElement, "OAM", cppNormNodeRef))
                .thenReturn(CPP_NODE_SECURITY_FDN);

    }

    @Test
    public void testProcessTaskWithUnexpectedErrorException() {
        cppEnableORDisableCRLCheckTask = new CPPEnableOrDisableCRLCheckTask();
        cppEnableORDisableCRLCheckTask.setCertType(certType);
        cppEnableORDisableCRLCheckTask.setCrlCheckStatus(CRL_CHECK_ACTIVATED);
        cppEnableORDisableCRLCheckTask.setNode(NODE);

        boolean thrown = false;
        try {
            cppEnableORDisableCRLCheckTaskHandler.processTask(cppEnableORDisableCRLCheckTask);
        } catch (final UnexpectedErrorException e) {
            assertTrue(e != null);
            thrown = true;

        }
        assertFalse(thrown);
    }

    @Test
    public void testUnexpectedErrorExceptionForDisable() {
        cppEnableORDisableCRLCheckTask = new CPPEnableOrDisableCRLCheckTask();
        cppEnableORDisableCRLCheckTask.setCertType(certType);
        cppEnableORDisableCRLCheckTask.setCrlCheckStatus(CRL_CHECK_DEACTIVATED);
        cppEnableORDisableCRLCheckTask.setNode(NODE);

        boolean thrown = false;
        try {
            cppEnableORDisableCRLCheckTaskHandler.processTask(cppEnableORDisableCRLCheckTask);
        } catch (final UnexpectedErrorException e) {
            assertTrue(e != null);
            thrown = true;

        }
        assertFalse(thrown);
    }

    @Test
    public void testProcessTaskForEnable() {
        cppEnableORDisableCRLCheckTask = new CPPEnableOrDisableCRLCheckTask();
        cppEnableORDisableCRLCheckTask.setCertType(certType);
        cppEnableORDisableCRLCheckTask.setCrlCheckStatus(CRL_CHECK_ACTIVATED);
        cppEnableORDisableCRLCheckTask.setNode(NODE);

        Mockito.when(nscsCMWriterService.withSpecification(CPP_NODE_SECURITY_FDN)).thenReturn(writerSpecificationBuilder);
        Mockito.when(writerSpecificationBuilder.setAttribute(TrustCategory.CRL_CHECK, CRL_CHECK_ACTIVATED)).thenReturn(writerSpecificationBuilder);
        Mockito.doNothing().when(writerSpecificationBuilder).updateMO();
        when((CppManagedElement) nscsCapabilityModelService.getMirrorRootMo(any(NormalizableNodeReference.class))).thenReturn(cppManagedElement);

        boolean thrown = false;

        try {
            cppEnableORDisableCRLCheckTaskHandler.processTask(cppEnableORDisableCRLCheckTask);
            thrown = false;
        } catch (final UnexpectedErrorException e) {
            assertTrue(e != null);
            thrown = true;

        }
        assertFalse(thrown);
    }

    @Test
    public void testProcessTaskForDisable() {
        cppEnableORDisableCRLCheckTask = new CPPEnableOrDisableCRLCheckTask();
        cppEnableORDisableCRLCheckTask.setCertType(certType);
        cppEnableORDisableCRLCheckTask.setCrlCheckStatus(CRL_CHECK_DEACTIVATED);
        cppEnableORDisableCRLCheckTask.setNode(NODE);

        Mockito.when(nscsCMWriterService.withSpecification(CPP_NODE_SECURITY_FDN)).thenReturn(writerSpecificationBuilder);
        Mockito.when(writerSpecificationBuilder.setAttribute(TrustCategory.CRL_CHECK, CRL_CHECK_DEACTIVATED)).thenReturn(writerSpecificationBuilder);
        Mockito.doNothing().when(writerSpecificationBuilder).updateMO();
        when((CppManagedElement) nscsCapabilityModelService.getMirrorRootMo(any(NormalizableNodeReference.class))).thenReturn(cppManagedElement);

        boolean thrown = false;

        try {
            cppEnableORDisableCRLCheckTaskHandler.processTask(cppEnableORDisableCRLCheckTask);
            thrown = false;
        } catch (final UnexpectedErrorException e) {
            assertTrue(e != null);
            thrown = true;
        }
        assertFalse(thrown);
    }

    private class MockNormalizableNodeRef implements NormalizableNodeReference {

        private static final long serialVersionUID = -3799671708615088019L;

        @Override
        public String getName() {
            return CPP_NODE_NAME;
        }

        @Override
        public String getFdn() {
            return CPP_NODE_ROOT_FDN;
        }

        @Override
        public boolean hasNormalizedRef() {
            return false;
        }

        @Override
        public NodeReference getNormalizedRef() {
            return null;
        }

        @Override
        public boolean hasNormalizableRef() {
            return false;
        }

        @Override
        public NodeReference getNormalizableRef() {
            return null;
        }

        @Override
        public String getTargetCategory() {
            return TargetTypeInformation.CATEGORY_NODE;
        }

        @Override
        public String getNeType() {
            return "RNC";
        }

        @Override
        public String getOssModelIdentity() {
            return "";
        }
    }

}
