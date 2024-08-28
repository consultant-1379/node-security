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
package com.ericsson.nms.security.nscs.workflow.task.comecim.attribute;

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
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TrustCategory;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService.WriterSpecificationBuilder;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NSCSComEcimNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.cpp.attribute.CheckTrustedOAMAlreadyInstalledTaskHandler;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimEnableOrDisableCRLCheckTask;

/**
 * @author xchowja
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ComEcimEnableOrDisableCRLCheckTaskHandlerTest {

    private static final String RADIO_NODE_NAME = "RADIO-NODE-123";
    private static final String RADIO_FDN_WITHOUT_ME_CONTEXT = "ManagedElement=" + RADIO_NODE_NAME;
    private static final String RADIO_OSS_MODEL_IDENTITY = "397-5538-366";

    private static final String RADIO_NODE_ROOT_FDN = String.format("ManagedElement=%s", RADIO_NODE_NAME);
    private static final String RADIO_NODE_CERT_M_FDN = String.format("%s,SystemFunctions=1,SecM=1,CertM=1", RADIO_NODE_ROOT_FDN);
    private static final String RADIO_NODE_IPSEC_TRUST_CATEGORY_FDN = String.format("%s,TrustCategory=ipsecTrustCategory", RADIO_NODE_CERT_M_FDN);

    private static final NodeReference NODE = new NodeRef(RADIO_FDN_WITHOUT_ME_CONTEXT);
    private final NormalizableNodeReference mockNormNode = new MockNormalizableNodeRef();
    private final NodeReference radioNodeRef = new NodeRef(RADIO_NODE_NAME);

    @Spy
    private final Logger logger = LoggerFactory.getLogger(CheckTrustedOAMAlreadyInstalledTaskHandler.class);

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    private NscsCMReaderService nscsCMReaderService;

    @Mock
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Mock
    private NSCSComEcimNodeUtility nscsComEcimNodeUtility;;

    @Mock
    private NscsCMWriterService nscsCMWriterService;

    @InjectMocks
    private ComEcimEnableOrDisableCRLCheckTaskHandler comEcimEnableORDisableCRLCheckTaskHandler;

    @InjectMocks
    NscsCapabilityModelService nscsCapabilityModelServiceTest;

    @Mock
    private NormalizableNodeReference radioTNodeNormNodeRef;
    @Mock
    private NormalizableNodeReference radioNormNodeRef;

    @InjectMocks
    NscsCapabilityModelMock capabilityModel;

    private ComEcimEnableOrDisableCRLCheckTask comEcimEnableORDisableCRLCheckTask;
    private String certType = CertificateType.IPSEC.toString();
    ComEcimManagedElement comEcimManagedElement = null;

    private static final String CRL_CHECK_ACTIVATED = "ACTIVATED";
    private static final String CRL_CHECK_DEACTIVATED = "DEACTIVATED";

    @Mock
    TrustCategory trustCategory;

    @Mock
    WriterSpecificationBuilder writerSpecificationBuilder;

    @Before
    public void setup() {

        when(nscsCMReaderService.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(mockNormNode);

        doReturn(RADIO_NODE_NAME).when(radioNormNodeRef).getName();
        doReturn(RADIO_FDN_WITHOUT_ME_CONTEXT).when(radioNormNodeRef).getFdn();
        doReturn("RadioNode").when(radioNormNodeRef).getNeType();
        doReturn(RADIO_OSS_MODEL_IDENTITY).when(radioNormNodeRef).getOssModelIdentity();
        doReturn(radioNodeRef).when(radioNormNodeRef).getNormalizedRef();
        doReturn(true).when(radioNormNodeRef).hasNormalizedRef();
        doReturn(radioNormNodeRef).when(nscsCMReaderService).getNormalizedNodeReference(radioNodeRef);

        when(nscsCMReaderService.getNormalizableNodeReference(radioNodeRef)).thenReturn(radioNormNodeRef);
        doReturn(RADIO_NODE_NAME).when(radioNormNodeRef).getName();
        doReturn(RADIO_NODE_ROOT_FDN).when(radioNormNodeRef).getFdn();

        comEcimManagedElement = Model.ME_CONTEXT.comManagedElement;
        when(nscsCapabilityModelService.getMirrorRootMo(eq(radioNormNodeRef))).thenReturn(comEcimManagedElement);

        final String CRL_CHECK_ENABLED = "CRLCheckEnabled";
        final NscsCMWriterService.WriterSpecificationBuilder specificationBuilder = org.mockito.Mockito
                .mock(NscsCMWriterService.WriterSpecificationBuilder.class);
        when(specificationBuilder.setNotNullAttribute(any(String.class), any(Object.class))).thenReturn(specificationBuilder);
        when(specificationBuilder.setFdn(RADIO_NODE_IPSEC_TRUST_CATEGORY_FDN)).thenReturn(specificationBuilder);
        when(nscsCMWriterService.withSpecification(RADIO_NODE_IPSEC_TRUST_CATEGORY_FDN)).thenReturn(specificationBuilder);
        when(specificationBuilder.setAttribute(TrustCategory.CRL_CHECK, CRL_CHECK_ENABLED)).thenReturn(specificationBuilder);
        when(nscsComEcimNodeUtility.getTrustCategoryFdn(RADIO_NODE_ROOT_FDN, comEcimManagedElement, "IPSEC", radioNormNodeRef))
                .thenReturn(RADIO_NODE_IPSEC_TRUST_CATEGORY_FDN);
        when(nscsComEcimNodeUtility.getTrustCategoryFdn(RADIO_NODE_ROOT_FDN, comEcimManagedElement, "OAM", radioNormNodeRef))
                .thenReturn(RADIO_NODE_IPSEC_TRUST_CATEGORY_FDN);

    }

    @Test
    public void testProcessTaskWithUnexpectedErrorException() {
        comEcimEnableORDisableCRLCheckTask = new ComEcimEnableOrDisableCRLCheckTask();
        comEcimEnableORDisableCRLCheckTask.setCertType(certType);
        comEcimEnableORDisableCRLCheckTask.setCrlCheckStatus(CRL_CHECK_ACTIVATED);
        comEcimEnableORDisableCRLCheckTask.setNode(NODE);

        boolean thrown = false;
        try {
            comEcimEnableORDisableCRLCheckTaskHandler.processTask(comEcimEnableORDisableCRLCheckTask);
        } catch (final UnexpectedErrorException e) {
            assertTrue(e != null);
            thrown = true;

        }
        assertFalse(thrown);
    }

    @Test
    public void testUnexpectedErrorExceptionForDisable() {
        comEcimEnableORDisableCRLCheckTask = new ComEcimEnableOrDisableCRLCheckTask();
        comEcimEnableORDisableCRLCheckTask.setCertType(certType);
        comEcimEnableORDisableCRLCheckTask.setCrlCheckStatus(CRL_CHECK_DEACTIVATED);
        comEcimEnableORDisableCRLCheckTask.setNode(NODE);

        boolean thrown = false;
        try {
            comEcimEnableORDisableCRLCheckTaskHandler.processTask(comEcimEnableORDisableCRLCheckTask);
        } catch (final UnexpectedErrorException e) {
            assertTrue(e != null);
            thrown = true;

        }
        assertFalse(thrown);
    }

    @Test
    public void testProcessTaskForEnable() {
        comEcimEnableORDisableCRLCheckTask = new ComEcimEnableOrDisableCRLCheckTask();
        comEcimEnableORDisableCRLCheckTask.setCertType(certType);
        comEcimEnableORDisableCRLCheckTask.setCrlCheckStatus(CRL_CHECK_ACTIVATED);
        comEcimEnableORDisableCRLCheckTask.setNode(NODE);

        Mockito.when(nscsCMWriterService.withSpecification(RADIO_NODE_IPSEC_TRUST_CATEGORY_FDN)).thenReturn(writerSpecificationBuilder);
        Mockito.when(writerSpecificationBuilder.setAttribute(TrustCategory.CRL_CHECK, CRL_CHECK_ACTIVATED)).thenReturn(writerSpecificationBuilder);
        Mockito.doNothing().when(writerSpecificationBuilder).updateMO();
        when((ComEcimManagedElement) nscsCapabilityModelService.getMirrorRootMo(any(NormalizableNodeReference.class)))
                .thenReturn(comEcimManagedElement);

        boolean thrown = false;

        try {
            comEcimEnableORDisableCRLCheckTaskHandler.processTask(comEcimEnableORDisableCRLCheckTask);
            thrown = false;
        } catch (final UnexpectedErrorException e) {
            assertTrue(e != null);
            thrown = true;

        }
        assertFalse(thrown);
    }

    @Test
    public void testProcessTaskForDisable() {
        comEcimEnableORDisableCRLCheckTask = new ComEcimEnableOrDisableCRLCheckTask();
        comEcimEnableORDisableCRLCheckTask.setCertType(certType);
        comEcimEnableORDisableCRLCheckTask.setCrlCheckStatus(CRL_CHECK_DEACTIVATED);
        comEcimEnableORDisableCRLCheckTask.setNode(NODE);

        Mockito.when(nscsCMWriterService.withSpecification(RADIO_NODE_IPSEC_TRUST_CATEGORY_FDN)).thenReturn(writerSpecificationBuilder);
        Mockito.when(writerSpecificationBuilder.setAttribute(TrustCategory.CRL_CHECK, CRL_CHECK_DEACTIVATED)).thenReturn(writerSpecificationBuilder);
        Mockito.doNothing().when(writerSpecificationBuilder).updateMO();
        when((ComEcimManagedElement) nscsCapabilityModelService.getMirrorRootMo(any(NormalizableNodeReference.class)))
                .thenReturn(comEcimManagedElement);

        boolean thrown = false;

        try {
            comEcimEnableORDisableCRLCheckTaskHandler.processTask(comEcimEnableORDisableCRLCheckTask);
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
            return RADIO_NODE_NAME;
        }

        @Override
        public String getFdn() {
            return RADIO_NODE_ROOT_FDN;
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
            return "RadioTNode";
        }

        @Override
        public String getOssModelIdentity() {
            return "";
        }
    }

}
