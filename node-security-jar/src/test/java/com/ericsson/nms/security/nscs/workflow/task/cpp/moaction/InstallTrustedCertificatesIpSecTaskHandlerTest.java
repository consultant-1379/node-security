/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
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
import static org.mockito.Mockito.when;

import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
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

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.enums.TrustCategoryType;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.model.TrustStoreInfo;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.timer.IntervalJobAction.JobActionParameters;
import com.ericsson.nms.security.nscs.timer.IntervalJobService;
import com.ericsson.nms.security.nscs.workflow.task.cpp.moaction.InstallTrustedCertificatesIpSecTaskHandler.InstallTrustedCertsIntervalJob;
import com.ericsson.nms.security.smrs.api.exception.SmrsDirectoryException;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.InstallTrustedCertificatesIpSecTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

@RunWith(MockitoJUnitRunner.class)
public class InstallTrustedCertificatesIpSecTaskHandlerTest {

    private static final NodeReference NODE = new NodeRef("MeContext=ERBS_001");
    private static final String NODE_TYPE = "ERBS";

    @Spy
    private final Logger logger = LoggerFactory.getLogger(InstallTrustedCertificatesIpSecTaskHandler.class);

    @Mock
    private InstallTrustedCertificatesIpSecTask mockInstallTrustedTask;

    @Mock
    private MOActionService mockMOActionService;

    @Mock
    private WorkflowHandler workflowHandler;

    @Mock
    CppSecurityService mockSecurityService;

    @Mock
    private NscsCMReaderService readerService;

    @Mock
    MoParams mockMoParams;

    @Mock
    TrustStoreInfo mockTrustStoreInfo;

    @Mock
    NscsCMReaderService mockNscsCMReaderService;

    @Mock
    CmResponse mockCmResponse;

    @Mock
    private CmObject mockCmObj;

    @Mock
    private Map<String, Object> attributeMap;

    @Mock
    private CmResponse cmResponse;

    @Mock
    private CmObject cmObject;

    @Mock
    private NormalizableNodeReference normNode;

    @InjectMocks
    private InstallTrustedCertificatesIpSecTaskHandler testObj;

    @Mock
    private CppSecurityService securityService;

    @Mock
    private TrustStoreInfo trustStoreInfo;

    @Mock
    private SystemRecorder systemRecorder;

    @Mock
    private IntervalJobService timerJobService;

    @Mock
    private NscsCapabilityModelService capabilityService;

    private Map<JobActionParameters, Object> params = new HashMap<>();

    private InstallTrustedCertsIntervalJob certEnrollJob = new InstallTrustedCertsIntervalJob(NODE);

    @Before
    public void setUp() throws Exception {
        when(mockInstallTrustedTask.getNodeFdn()).thenReturn(NODE.getFdn());
        when(mockInstallTrustedTask.getNode()).thenReturn(NODE);
        when(readerService.getNormalizableNodeReference(NODE)).thenReturn(normNode);
        when(readerService.getTargetType(NODE.getFdn())).thenReturn(NODE_TYPE);
        when(readerService.readAttributesFromDelegate(Mockito.anyString(), Mockito.anyString())).thenReturn(attributeMap);
        when(cmObject.getAttributes()).thenReturn(attributeMap);
        when(cmResponse.getCmObjects()).thenReturn(Arrays.asList(cmObject));
        when(capabilityService.getMirrorRootMo(normNode)).thenReturn(Model.ME_CONTEXT.managedElement);
        //
        params.put(JobActionParameters.WORKFLOW_HANDLER, workflowHandler);
        params.put(JobActionParameters.CM_READER, readerService);
        params.put(JobActionParameters.SYSTEM_RECORDER, systemRecorder);
        params.put(JobActionParameters.CAPABILITY_SERVICE, capabilityService);
    }

    @Test
    public void testProcessTaskSuccess() throws SmrsDirectoryException, UnknownHostException, CppSecurityServiceException, CertificateException {
        when(securityService.getTrustStoreForNode(any(TrustedCertCategory.class), Mockito.any(NodeRef.class), eq(true),
                (TrustCategoryType)Mockito.any())).thenReturn(trustStoreInfo);
        when(trustStoreInfo.toMoParamsIpSec()).thenReturn(new MoParams());
        testObj.processTask(mockInstallTrustedTask);
        Mockito.verify(timerJobService, atLeast(1)).createIntervalJob(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
                Mockito.any(InstallTrustedCertsIntervalJob.class));
        Mockito.verify(mockMOActionService, atLeast(1)).performMOAction(Mockito.any(String.class), Mockito.any(MoActionWithParameter.class),
                Mockito.any(MoParams.class));
    }

    @Test
    public void testCertEnrollmentTimerJobNullValue() {
        Assert.assertTrue(certEnrollJob.doAction(params));
    }

    @Test
    public void testCertEnrollmentTimerJobErrorValue() {
        final String attValue = ModelDefinition.IpSec.IpSecCertEnrollStateValue.ERROR.toString();
        when(attributeMap.get(IpSec.TRUSTED_CERT_INST_STATE)).thenReturn(attValue);
        Assert.assertTrue(certEnrollJob.doAction(params));
    }

    @Test
    public void testCertEnrollmentTimerJobIdleValue() {
        final String attValue = ModelDefinition.IpSec.IpSecCertEnrollStateValue.IDLE.toString();
        when(attributeMap.get(IpSec.TRUSTED_CERT_INST_STATE)).thenReturn(attValue);
        Assert.assertTrue(certEnrollJob.doAction(params));
    }

    @Test
    public void testCertEnrollmentTimerJobOngoinValue() {
        final String attValue = ModelDefinition.IpSec.IpSecCertEnrollStateValue.ONGOING.toString();
        when(attributeMap.get(IpSec.TRUSTED_CERT_INST_STATE)).thenReturn(attValue);
        Assert.assertFalse(certEnrollJob.doAction(params));
    }

}
