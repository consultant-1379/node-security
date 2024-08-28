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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.bouncycastle.asn1.x509.X509Name;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.param.AttributeSpecBuilder;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParam;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.iscf.DataCollectorTest;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.EnrollingInformation;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo;
import com.ericsson.oss.itpf.security.pki.common.model.EntityStatus;
import com.ericsson.oss.itpf.security.pki.common.model.Subject;
import com.ericsson.oss.itpf.security.pki.common.model.SubjectField;
import com.ericsson.oss.itpf.security.pki.common.model.SubjectFieldType;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltName;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameField;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameFieldType;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameString;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.IssueInitCertEnrollmentTask;
//import com.ericsson.nms.security.pkicore.model.*;
//import com.ericsson.nms.security.pkicore.model.EnrollmentServerInfo.EnrollmentServer;

@RunWith(MockitoJUnitRunner.class)
public class IssueInitCertEnrollmentTaskHandlerTest extends DataCollectorTest {

    private static final NodeReference NODE = new NodeRef("node123");

    private static final SubjectAltNameFormat subjectAltNameType = SubjectAltNameFormat.IPV4;

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    private MoParams mopar;

    @Mock
    private ScepEnrollmentInfoImpl enrollmentInfo;

    @InjectMocks
    private IssueInitCertEnrollmentTaskHandler handlerUnderTest;

    @Mock
    MOActionService moAction;
    @Mock
    CppSecurityService securityService;
    @Mock
    AttributeSpecBuilder builder;
    @Mock
    SystemRecorder systemRecorder;

    @Mock
    NscsCMReaderService readerService;

    @Mock
    NormalizableNodeReference normalizable;

    @Test(expected = UnexpectedErrorException.class)
    public void handlerInvocationTest_ThrowExceptionInvalidScepEnrollmentInfo() throws Exception {

        final IssueInitCertEnrollmentTask task = mock(IssueInitCertEnrollmentTask.class);
        when(task.getNodeFdn()).thenReturn(NODE.getFdn());
        when(task.getNode()).thenReturn(NODE);
        when(task.getRollbackTimeout()).thenReturn(10);
        when(task.getAlgoKeySize()).thenReturn(AlgorithmKeys.RSA_1024);
        when(task.getEntityProfileName()).thenReturn("Profile");
        when(task.getSubjectAltName()).thenReturn(new SubjectAltNameStringType("ABC"));
        when(task.getSubjectAltNameType()).thenReturn(subjectAltNameType);
        when(normalizable.getFdn()).thenReturn(NODE.getFdn());
        when(readerService.getNormalizableNodeReference(Mockito.any(NodeReference.class))).thenReturn(normalizable);
        //final Entity entity = new Entity(new X509Name("CN="+ NODE));
        //final EnrollmentServerInfo esi = new EnrollmentServerInfo(EnrollmentServer.SCEP_CORBA, "localhost", "fingerprint".getBytes());
        final Entity entity = new Entity();
        //        esi.setcAFingerPrint("fingerprint");
        final EntityInfo entityInfo = new EntityInfo();
        final Subject subject = new Subject();

        final SubjectField subjectFieldCN = new SubjectField();
        subjectFieldCN.setType(SubjectFieldType.COMMON_NAME);
        subjectFieldCN.setValue("common");

        final List<SubjectField> entSubjectFieldList = new ArrayList<>();
        entSubjectFieldList.add(subjectFieldCN);
        subject.setSubjectFields(entSubjectFieldList);
        entityInfo.setSubject(subject);
        entity.setEntityInfo(entityInfo);

        final Map<String, Object> map = new HashMap<String, Object>();
        final Map<String, MoParam> moParamMap = new HashMap<String, MoParam>();
        map.put("data", mopar);
        //ScepEnrollmentInfoImpl enrollmentInfo = new ScepEnrollmentInfoImpl(entity, esi, 50);
        when(enrollmentInfo.toMoParams()).thenReturn(mopar);
        when(enrollmentInfo.getEnrollmentProtocol()).thenReturn(EnrollmentMode.CMPv2_VC.getEnrollmentModeValue());

        when(mopar.getParam()).thenReturn(map);
        when(mopar.getParamMap()).thenReturn(moParamMap);

        when(securityService.generateEnrollmentInfo(any(EnrollingInformation.class))).thenReturn(enrollmentInfo);
        //        final ArgumentCaptor<ArrayList> moParamsListCaptor = ArgumentCaptor.forClass(ArrayList.class);

        final EnrollmentMode enrollMode = EnrollmentMode.CMPv2_VC;
        when(securityService.enrollmentModeUpdate(any(String.class), any(String.class), any(Entity.class))).thenReturn(enrollMode);
        when(securityService.enrollmentModeUpdate(any(String.class), any(EnrollmentMode.class), any(String.class), any(Entity.class))).thenReturn(enrollMode);

        handlerUnderTest.processTask(task);

        verify(task, atLeast(1)).getNodeFdn();
        verify(securityService, atLeast(1)).generateEnrollmentInfo(any(EnrollingInformation.class));
        //        verify(moAction, atLeast(1)).performMOAction(eq(NODE.getFdn()), eq(MoActionWithParameter.Security_initCertEnrollment),
        //                moParamsListCaptor.capture());
        //        final List<MoParams> moParamsList = moParamsListCaptor.getValue();
        //        assertEquals(2, moParamsList.size());
    }

    @Test
    public void handlerInvocationTest() throws Exception {

        Subject subject = new Subject();
        EntityInfo entityInfo = new EntityInfo();
        final Map<SubjectFieldType, String> subjMap = new HashMap<>();
        subjMap.put(SubjectFieldType.COMMON_NAME, "test");
        SubjectField subjectFieldCN = new SubjectField();
        subjectFieldCN.setType(SubjectFieldType.COMMON_NAME);
        subjectFieldCN.setValue("test");
        List<SubjectField> entSubjectFieldList = new ArrayList<>();
        entSubjectFieldList.add(subjectFieldCN);
        subject.setSubjectFields(entSubjectFieldList);
        entityInfo.setSubject(subject);

        SubjectAltNameField subjectAltNameField = new SubjectAltNameField();
        subjectAltNameField.setType(SubjectAltNameFieldType.IP_ADDRESS);
        SubjectAltNameString subjectAltNameValueString = new SubjectAltNameString();
        subjectAltNameValueString.setValue("1.1.1.1");
        subjectAltNameField.setValue(subjectAltNameValueString);
        List<SubjectAltNameField> subjectAltNameValueList = new ArrayList<>();
        subjectAltNameValueList.add(subjectAltNameField);
        SubjectAltName subjectAltNameValues = new SubjectAltName();
        subjectAltNameValues.setSubjectAltNameFields(subjectAltNameValueList);
        entityInfo.setSubjectAltName(subjectAltNameValues);

        entityInfo.setName("test-oam");
        entityInfo.setSubject(subject);
        entityInfo.setOTP(ISCF_TEST_OTP_STR);
        entityInfo.setStatus(EntityStatus.NEW);

        Entity ee = new Entity();
        ee.setType(EntityType.ENTITY);
        ee.setEntityInfo(entityInfo);

        final ScepEnrollmentInfoImpl enrollInfo = new ScepEnrollmentInfoImpl(ee, "https://localhost:8443/app/resource", null, DigestAlgorithm.SHA1, 10, "challengePWD", "2048", EnrollmentMode.CMPv2_VC,
                null, null);
        enrollInfo.setServerCertFingerPrint("the-fingerprint".getBytes());

        final IssueInitCertEnrollmentTask task = mock(IssueInitCertEnrollmentTask.class);
        when(task.getNodeFdn()).thenReturn(NODE.getFdn());
        when(task.getNode()).thenReturn(NODE);
        when(task.getRollbackTimeout()).thenReturn(10);
        when(task.getAlgoKeySize()).thenReturn(AlgorithmKeys.RSA_1024);
        when(task.getEntityProfileName()).thenReturn("Profile");
        when(task.getSubjectAltName()).thenReturn(new SubjectAltNameStringType("ABC"));
        when(task.getSubjectAltNameType()).thenReturn(subjectAltNameType);
        when(normalizable.getFdn()).thenReturn(NODE.getFdn());
        when(readerService.getNormalizableNodeReference(Mockito.any(NodeReference.class))).thenReturn(normalizable);

        final Map<String, Object> map = new HashMap<String, Object>();
        final Map<String, MoParam> moParamMap = new HashMap<String, MoParam>();
        map.put("data", mopar);

        when(mopar.getParam()).thenReturn(map);
        when(mopar.getParamMap()).thenReturn(moParamMap);

        when(securityService.generateEnrollmentInfo(any(EnrollingInformation.class))).thenReturn(enrollInfo);
        final ArgumentCaptor<ArrayList> moParamsListCaptor = ArgumentCaptor.forClass(ArrayList.class);

        final EnrollmentMode enrollMode = EnrollmentMode.CMPv2_VC;
        when(securityService.enrollmentModeUpdate(any(String.class), any(String.class), any(Entity.class))).thenReturn(enrollMode);
        when(securityService.enrollmentModeUpdate(any(String.class), any(EnrollmentMode.class), any(String.class), any(Entity.class))).thenReturn(enrollMode);

        handlerUnderTest.processTask(task);

        verify(task, atLeast(1)).getNodeFdn();
        verify(securityService, atLeast(1)).generateEnrollmentInfo(any(EnrollingInformation.class));
        verify(moAction, atLeast(1)).performMOAction(eq(NODE.getFdn()), eq(MoActionWithParameter.Security_initCertEnrollment), moParamsListCaptor.capture());
        final List<MoParams> moParamsList = moParamsListCaptor.getValue();
        assertEquals(2, moParamsList.size());
    }
}
