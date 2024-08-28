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
package com.ericsson.nms.security.nscs.workflow.task.cpp.moaction;

import static org.mockito.Matchers.any;

import java.util.*;

import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
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
import com.ericsson.nms.security.nscs.data.*;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParam;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.timer.IntervalJobAction.JobActionParameters;
import com.ericsson.nms.security.nscs.timer.IntervalJobService;
import com.ericsson.nms.security.nscs.util.EnrollingInformation;
import com.ericsson.nms.security.nscs.workflow.task.cpp.moaction.IssueInitCertEnrollmentIpSecTaskHandler.CertEnrollStateIntervalJob;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.IssueInitCertEnrollmentIpSecTask;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class IssueInitCertEnrollmentIpSecTaskHandlerTest {

    private static final String SUBJECT_ALT_NAME = "127.0.0.1";

    private static final SubjectAltNameFormat SUBJECT_ALT_NAME_TYPE = SubjectAltNameFormat.IPV4;

    private static final NodeReference NODE = new NodeRef("MeContext=ERBS_001");

    @Mock
    private WorkflowHandler workflowHandler;

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    private IssueInitCertEnrollmentIpSecTask mockIssueInitCertEnrollmentIpSecTask;

    @InjectMocks
    private IssueInitCertEnrollmentIpSecTaskHandler testObj;

    @Mock
    private MOActionService moActionService;

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
    private Map <String, MoParam> moParamMap;

    @Mock
    private MoParam moParam;

    private final Map<JobActionParameters, Object> params = new HashMap<>();

    private CertEnrollStateIntervalJob certEnrollJob;

    @Before
    public void setup() {
    	
     	NscsLogger logger = Mockito.mock(NscsLogger.class);
     	certEnrollJob = new CertEnrollStateIntervalJob(NODE, logger, mockIssueInitCertEnrollmentIpSecTask);
        when(mockIssueInitCertEnrollmentIpSecTask.getNodeFdn()).thenReturn(NODE.getFdn());
        when(mockIssueInitCertEnrollmentIpSecTask.getNode()).thenReturn(NODE);
        when(mockIssueInitCertEnrollmentIpSecTask.getSubjectAltName()).
        thenReturn(new SubjectAltNameStringType(SUBJECT_ALT_NAME));
        when(mockIssueInitCertEnrollmentIpSecTask.getSubjectAltNameType()).thenReturn(SUBJECT_ALT_NAME_TYPE);
        when(readerService.getNormalizableNodeReference(NODE)).thenReturn(normNode);
        when(
                readerService.getMOAttribute(normNode, Model.ME_CONTEXT.managedElement.ipSystem.ipSec.type(),
                        Model.ME_CONTEXT.managedElement.ipSystem.ipSec.namespace(), IpSec.CERT_ENROLL_STATE)).thenReturn(cmResponse);
        when(cmObject.getAttributes()).thenReturn(attributeMap);
        when(cmResponse.getCmObjects()).thenReturn(Arrays.asList(cmObject));
        when(capabilityService.getMirrorRootMo(normNode)).thenReturn(Model.ME_CONTEXT.managedElement);
        //
        params.put(JobActionParameters.WORKFLOW_HANDLER, workflowHandler);
        params.put(JobActionParameters.CM_READER, readerService);
        params.put(JobActionParameters.CAPABILITY_SERVICE, capabilityService);
    }

    @Test
    public void testProcessTaskSuccess() throws CppSecurityServiceException {
        //        when(securityService.generateIpsecEnrollmentInfo(NODE.getFdn(), SUBJECT_ALT_NAME)).thenReturn(enrollmentInfo);
        final MoParams moparams = new MoParams();

        ScepEnrollmentInfoImpl scepEnrollmentInfo = mock(ScepEnrollmentInfoImpl.class, withSettings().serializable());
        MoParams params = mock(MoParams.class, withSettings().serializable());

        final Map <String, Object> map = new HashMap<String, Object>();
        map.put("enrollmentData", params);


        when(params.getParam()).thenReturn(map);

        when(securityService.generateEnrollmentInfo(any(EnrollingInformation.class))).thenReturn(scepEnrollmentInfo);
        when(scepEnrollmentInfo.toIpSecMoParams()).thenReturn(params);
        when(scepEnrollmentInfo.getEnrollmentProtocol()).thenReturn(EnrollmentMode.CMPv2_VC.getEnrollmentModeValue());

        final EnrollmentMode enrollMode = EnrollmentMode.CMPv2_VC;
        when(securityService.enrollmentModeUpdate(any(String.class), any(String.class), any(Entity.class))).thenReturn(enrollMode);
        when(securityService.enrollmentModeUpdate(any(String.class), any(EnrollmentMode.class), any(String.class), any(Entity.class))).thenReturn(enrollMode);

        when(mopar.getParam()).thenReturn(map);
        when(mopar.getParamMap()).thenReturn(moParamMap);
        final ArgumentCaptor<ArrayList> moParamsListCaptor = ArgumentCaptor.forClass(ArrayList.class);

        testObj.processTask(mockIssueInitCertEnrollmentIpSecTask);
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
        Assert.assertTrue(certEnrollJob.doAction(params));
    }

    @Test
    public void test_handlerInvocationWithMissingAttribute() throws CppSecurityServiceException {
        when(cmResponse.getCmObjects()).thenReturn(new ArrayList<CmObject>());
        certEnrollJob.doAction(params);
    }

    @Test
    public void test_handlerInvocationWithMultipleAttribute() throws CppSecurityServiceException {
        when(cmResponse.getCmObjects()).thenReturn(Arrays.asList(cmObject, cmObject));
        certEnrollJob.doAction(params);
    }
}
