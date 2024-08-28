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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParam;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.timer.IntervalJobAction.JobActionParameters;
import com.ericsson.nms.security.nscs.timer.IntervalJobService;
import com.ericsson.nms.security.nscs.workflow.task.cpp.moaction.InitCertEnrollmentIpSecTaskHandler.CertEnrollStateIntervalJob;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.InitCertEnrollmentIpSecTask;

@RunWith(MockitoJUnitRunner.class)
public class InitCertEnrollmentIpSecTaskHandlerTest {

    private static final String SUBJECT_ALT_NAME = "127.0.0.1";

    private static final NodeReference NODE = new NodeRef("MeContext=ERBS_001");

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    private InitCertEnrollmentIpSecTask mockInitCertEnrollmentIpSecTask;

    @InjectMocks
    private InitCertEnrollmentIpSecTaskHandler testObj;

    @Mock
    private MOActionService moActionService;

    @Mock
    private WorkflowHandler workflowHandler;

    @Mock
    private CppSecurityService securityService;

    @Mock
    private ScepEnrollmentInfoImpl enrollmentInfo;

    @Mock
    private NscsCMReaderService readerService;

    @Mock
    private NormalizableNodeReference normNode;

    @Mock
    private CmResponse cmResponse;

    @Mock
    private CmObject cmObject;

    @Mock
    private IntervalJobService timerJobService;

    @Mock
    private Map<String, Object> attributeMap;

    @Mock
    private NscsCapabilityModelService capabilityService;

    @Mock
    private MoParams mopar;

    @Mock
    private Map<String, MoParam> moParamMap;

    private final Map<JobActionParameters, Object> params = new HashMap<>();

    private CertEnrollStateIntervalJob certEnrollJob;

    @Before
    public void setup() {

        final NscsLogger logger = Mockito.mock(NscsLogger.class);
        certEnrollJob = new CertEnrollStateIntervalJob(NODE, logger, mockInitCertEnrollmentIpSecTask);
        when(mockInitCertEnrollmentIpSecTask.getNodeFdn()).thenReturn(NODE.getFdn());
        when(mockInitCertEnrollmentIpSecTask.getNode()).thenReturn(NODE);
        when(mockInitCertEnrollmentIpSecTask.getSubjectAltName()).thenReturn(new SubjectAltNameStringType(SUBJECT_ALT_NAME));
        when(mockInitCertEnrollmentIpSecTask.getSubjectAltNameFormat()).thenReturn(SubjectAltNameFormat.IPV4);
        when(readerService.getNormalizableNodeReference(NODE)).thenReturn(normNode);
        when(normNode.getNeType()).thenReturn("ERBS");
        when(readerService.readAttributesFromDelegate(Mockito.anyString(), Mockito.anyString())).thenReturn(attributeMap);
        when(cmObject.getAttributes()).thenReturn(attributeMap);
        when(capabilityService.getMirrorRootMo(normNode)).thenReturn(Model.ME_CONTEXT.managedElement);
        when(cmResponse.getCmObjects()).thenReturn(Arrays.asList(cmObject));
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("enrollmentData", mopar);

        when(mopar.getParam()).thenReturn(map);
        when(enrollmentInfo.toIpSecMoParams()).thenReturn(mopar);
        when(enrollmentInfo.getEnrollmentProtocol()).thenReturn(EnrollmentMode.CMPv2_VC.getEnrollmentModeValue());

        when(mopar.getParamMap()).thenReturn(moParamMap);

        //
        params.put(JobActionParameters.WORKFLOW_HANDLER, workflowHandler);
        params.put(JobActionParameters.CM_READER, readerService);
        params.put(JobActionParameters.CAPABILITY_SERVICE, capabilityService);
    }

    @Test
    public void testProcessTaskSuccess() throws CppSecurityServiceException {
        when(securityService.generateIpsecEnrollmentInfo(NODE.getName(), new SubjectAltNameStringType(SUBJECT_ALT_NAME), SubjectAltNameFormat.IPV4))
                .thenReturn(enrollmentInfo);
        final ArgumentCaptor<ArrayList> moParamsListCaptor = ArgumentCaptor.forClass(ArrayList.class);
        testObj.processTask(mockInitCertEnrollmentIpSecTask);
        Mockito.verify(timerJobService, atLeast(1)).createIntervalJob(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
                Mockito.any(InitCertEnrollmentIpSecTaskHandler.CertEnrollStateIntervalJob.class));
        Mockito.verify(moActionService, atLeast(1)).performMOAction(Mockito.any(String.class), Mockito.any(MoActionWithParameter.class),
                moParamsListCaptor.capture());
        final List<MoParams> moParamsList = moParamsListCaptor.getValue();
        assertEquals(2, moParamsList.size());
    }

    @Test
    public void testCertEnrollmentTimerJobNullValue() {
        Assert.assertTrue(certEnrollJob.doAction(params));
    }

    @Test
    public void testCertEnrollmentTimerJobErrorValue() {
        final String attValue = ModelDefinition.IpSec.IpSecCertEnrollStateValue.ERROR.toString();
        when(attributeMap.get(IpSec.CERT_ENROLL_STATE)).thenReturn(attValue);
        Assert.assertTrue(certEnrollJob.doAction(params));
    }

    @Test
    public void testCertEnrollmentTimerJobIdleValue() {
        final String attValue = ModelDefinition.IpSec.IpSecCertEnrollStateValue.IDLE.toString();
        when(attributeMap.get(IpSec.CERT_ENROLL_STATE)).thenReturn(attValue);
        Assert.assertTrue(certEnrollJob.doAction(params));
    }

    @Test
    public void testCertEnrollmentTimerJobOngoinValue() {
        final String attValue = ModelDefinition.IpSec.IpSecCertEnrollStateValue.ONGOING.toString();
        when(attributeMap.get(IpSec.CERT_ENROLL_STATE)).thenReturn(attValue);
        Assert.assertFalse(certEnrollJob.doAction(params));
    }

}
