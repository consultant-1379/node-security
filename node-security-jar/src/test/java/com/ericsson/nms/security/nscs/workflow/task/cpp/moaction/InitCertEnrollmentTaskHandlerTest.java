package com.ericsson.nms.security.nscs.workflow.task.cpp.moaction;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfo;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.param.AttributeSpecBuilder;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParam;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo;
import com.ericsson.oss.itpf.security.pki.common.model.Subject;
import com.ericsson.oss.itpf.security.pki.common.model.SubjectField;
import com.ericsson.oss.itpf.security.pki.common.model.SubjectFieldType;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.InitCertEnrollmentTask;
import static org.junit.Assert.assertEquals;
import org.mockito.ArgumentCaptor;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
/**
 * Unit test for InitCertEnrollmentTaskHandlerTest
 * @author emaynes
 */
@RunWith(MockitoJUnitRunner.class)
public class InitCertEnrollmentTaskHandlerTest {

    private static final NodeReference NODE = new NodeRef("node123");
    

    @Mock
    private NscsLogger nscsLogger;

    @InjectMocks
    private InitCertEnrollmentTaskHandler handlerUnderTest;

    @Mock MOActionService moAction;
    @Mock CppSecurityService securityService;
    @Mock Logger log;
	@Mock AttributeSpecBuilder builder;
    @Mock
    private NscsCapabilityModelService capabilityService;
    @Mock
    private ScepEnrollmentInfoImpl enrollmentInfo;
    @Mock
    private MoParams mopar;
    @Mock
    private Map <String, MoParam> moParamMap;
	
	@Mock
	NscsCMReaderService readerService;
	
	@Mock
	NormalizableNodeReference normalizable;
    
	//Xml Scalability purpose
    @Test    
    public void handlerInvocationTest() throws Exception {
    	
        final InitCertEnrollmentTask task = mock(InitCertEnrollmentTask.class);
        when(task.getNodeFdn()).thenReturn(NODE.getFdn());
        when(task.getNode()).thenReturn(NODE);
        when(task.getRollbackTimeout()).thenReturn(10);
        when(normalizable.getFdn()).thenReturn(NODE.getFdn());
        when(readerService.getNormalizableNodeReference(Mockito.any(NodeReference.class))).thenReturn(normalizable);
        
//        final EndEntity ee = new EndEntity(new X509Name("CN="+ NODE));
        final Entity ee = new Entity();
        EntityInfo entityInfo = new EntityInfo();
        entityInfo.setName(NODE.getName());
        entityInfo.setId(1);
        
        final Map<SubjectFieldType, String> subjMap = new HashMap<>();
        subjMap.put(SubjectFieldType.COMMON_NAME, NODE.getName());
        Subject subject = new Subject();
        
        SubjectField subjectFieldCN = new SubjectField();
        subjectFieldCN.setType(SubjectFieldType.COMMON_NAME);
        subjectFieldCN.setValue(NODE.getName());

        List<SubjectField> entSubjectFieldList = new ArrayList<>();
        entSubjectFieldList.add(subjectFieldCN);
        subject.setSubjectFields(entSubjectFieldList);
        entityInfo.setSubject(subject);
        
        ee.setEntityInfo(entityInfo);
 
//        ee.setName("CN="+ NODE);
//        final EnrollmentInfo esi = new EnrollmentInfo(EnrollmentServer.SCEP_CORBA, "localhost", "fingerprint".getBytes());        
//        final EnrollmentInfo esi = new EnrollmentInfo();        
//        esi.setEnrollmentURL("localhost");
        
    	Map <String, Object> map = new HashMap<>();
    	map.put("data", mopar);
        when(mopar.getParam()).thenReturn(map);
//        when(enrollmentInfo.toMoParams()).thenReturn(mopar);
//        Mockito.doCallRealMethod().when(enrollmentInfo).setRollbackTimeout(Mockito.anyInt());
        when(mopar.getParamMap()).thenReturn(moParamMap);

        when(securityService.generateOamEnrollmentInfo(any(String.class)))
                .thenReturn(generateTestScepInfo(ee, "localhost", 50));
        when(securityService.enrollmentModeUpdate(any(String.class), any(String.class), any(Entity.class)))
                .thenReturn(EnrollmentMode.CMPv2_VC);
        final ArgumentCaptor<ArrayList> moParamsListCaptor = ArgumentCaptor.forClass(ArrayList.class);
        
        handlerUnderTest.processTask(task);

        verify(task, atLeast(1)).getNodeFdn();
        verify(securityService, atLeast(1)).generateOamEnrollmentInfo(any(String.class));
        verify(moAction, atLeast(1)).performMOAction(eq(NODE.getFdn()), eq(MoActionWithParameter.Security_initCertEnrollment),
                moParamsListCaptor.capture());
        final List<MoParams> moParamsList = moParamsListCaptor.getValue();
        assertEquals(2, moParamsList.size());
    }
      
    protected ScepEnrollmentInfo generateTestScepInfo(final Entity ee, 
            final String enrollmentUrl, final int rollbackTimeOut) {
        ScepEnrollmentInfo scep = null;
        try {
            scep = new ScepEnrollmentInfoImpl(
                    ee,
                    enrollmentUrl,
                    null,
                    DigestAlgorithm.MD5,
                    rollbackTimeOut,
                    "challengePassword", "2048", EnrollmentMode.CMPv2_VC, null,null);
        } catch (NoSuchAlgorithmException | CertificateEncodingException ex) {
            log.debug("Cannot initialize CA fingerPrint due to exception", ex);
        }
        if ((scep != null) && (scep.getServerCertFingerPrint() == null))
            scep.setServerCertFingerPrint("DummyFingerPrint".getBytes());
        return scep;
    }

}
