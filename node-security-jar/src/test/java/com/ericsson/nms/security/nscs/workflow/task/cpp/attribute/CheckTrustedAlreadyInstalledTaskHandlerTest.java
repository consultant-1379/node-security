package com.ericsson.nms.security.nscs.workflow.task.cpp.attribute;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.model.TrustStoreInfo;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.laad.service.ResourcesBean;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.utilities.NSCSCertificateUtility;
import com.ericsson.nms.security.smrs.api.exception.SmrsDirectoryException;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation;
import com.ericsson.oss.itpf.sdk.resources.Resource;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoAttributeException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.CheckTrustedAlreadyInstalledTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

/**
 * Created with IntelliJ IDEA. User: ediniku Date: 06/11/14 Time: 14:31 To change this template use File | Settings | File Templates.
 */
@RunWith(MockitoJUnitRunner.class)
public class CheckTrustedAlreadyInstalledTaskHandlerTest {
    private static final NodeReference NODE = new NodeRef("MeContext=LTE05ERBS00015");
    private final NormalizableNodeReference mockNormNode = new mockNormalizableNodeRef();
    public static final String MANDATORY_IP_SEC_ATTRIBUTE = "IpSecId";
    public static final String MANDATORY_IP_SEC_ATTRIBUTE_VALUE = "1";
    public static final String IP_SEC_CERTIFICATE_SERIALNUMBER_ATTRIBUTE = "serialNumber";
    public static final String IP_SEC_CERTIFICATE_SERIALNUMBER_ATTRIBUTE_VALUE = "12345";
    public static final String IP_SEC_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE = "subjectAltName";
    public static final String IP_SEC_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE_VALUE = "test_subject";
    public static final String IP_SEC_CERTIFICATE_NOTVALIDAFTER_ATTRIBUTE = "notValidAfter";
    public static final String IP_SEC_CERTIFICATE_NOTVALIDAFTER_ATTRIBUTE_VALUE = "20141106210627";
    public static final String IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_SUBJECT_ATTRIBUTE = "subject";
    public static final String IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_SUBJECT_ATTRIBUTE_VALUE = "O=Ericsson, OU=ericssonOAM, CN=atclvm387RootCA";
    public static final String IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_ISSUER_ATTRIBUTE = "issuer";
    public static final String IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_ISSUER_ATTRIBUTE_VALUE = "O=Ericsson, OU=ericssonOAM, CN=atclvm387RootCA";
    public static final String IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_SERIALNUMBER_ATTRIBUTE = "serialNumber";
    public static final String IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_SERIALNUMBER_ATTRIBUTE_VALUE = "237097996";
    public static final String IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE = "subjectAltName";
    public static final String IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE_VALUE = "test_subject";
    public static final String IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_NOTVALIDAFTER_ATTRIBUTE = "notValidAfter";
    public static final String IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_NOTVALIDAFTER_ATTRIBUTE_VALUE = "20141106210627";
    public static final String IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_SUBJECT_ATTRIBUTE = "subject";
    public static final String IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_SUBJECT_ATTRIBUTE_VALUE = "CN=atrcxb2302-7.athtem.eei.ericsson.se, OU=\"ericssonTOR=\", O=Ericsson";
    public static final String IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_ISSUER_ATTRIBUTE = "issuer";
    public static final String IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_ISSUER_ATTRIBUTE_VALUE = "O=Ericsson, OU=ericssonOAM, CN=TORMgmtRootCA";
    public static final String IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_SERIALNUMBER_ATTRIBUTE = "serialNumber";
    public static final String IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_SERIALNUMBER_ATTRIBUTE_VALUE = "2050815638";
    public static final String IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE = "subjectAltName";
    public static final String IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE_VALUE = "test_subject_1";
    public static final String IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_NOTVALIDAFTER_ATTRIBUTE = "notValidAfter";
    public static final String IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_NOTVALIDAFTER_ATTRIBUTE_VALUE = "20141201221111";
    public static final String IP_SEC_2_INSTALLED_TRUSTED_CERTIFICATE_SUBJECT_ATTRIBUTE = "subject";
    public static final String IP_SEC_2_INSTALLED_TRUSTED_CERTIFICATE_SUBJECT_ATTRIBUTE_VALUE = "CN=atclvm387";
    public static final String IP_SEC_2_INSTALLED_TRUSTED_CERTIFICATE_ISSUER_ATTRIBUTE = "issuer";
    public static final String IP_SEC_2_INSTALLED_TRUSTED_CERTIFICATE_ISSUER_ATTRIBUTE_VALUE = "O=Ericsson, OU=ericssonOAM, CN=atclvm387MSCertCA";
    public static final String IP_SEC_2_INSTALLED_TRUSTED_CERTIFICATE_SERIALNUMBER_ATTRIBUTE = "serialNumber";
    public static final String IP_SEC_2_INSTALLED_TRUSTED_CERTIFICATE_SERIALNUMBER_ATTRIBUTE_VALUE = "1220540640";
    public static final String IP_SEC_2_INSTALLED_TRUSTED_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE = "subjectAltName";
    public static final String IP_SEC_2_INSTALLED_TRUSTED_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE_VALUE = "test_subject_2";
    public static final String IP_SEC_2_INSTALLED_TRUSTED_CERTIFICATE_NOTVALIDAFTER_ATTRIBUTE = "notValidAfter";
    public static final String IP_SEC_2_INSTALLED_TRUSTED_CERTIFICATE_NOTVALIDAFTER_ATTRIBUTE_VALUE = "20141201221113";

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    CheckTrustedAlreadyInstalledTask mockCkTrstAlInTsk;

    @Mock
    private NscsCMReaderService mockReaderService;

    @Mock
    private ResourcesBean resourceBean;

    @Mock
    private Resource resource;

    @Mock
    private CppSecurityService mockSecurityService;

    @Mock
    private TrustStoreInfo mockTrustStoreInfo;

    @Mock
    private NSCSCertificateUtility certificateUtility;

    @InjectMocks
    private CheckTrustedAlreadyInstalledTaskHandler ckTrstAlInsTskHndlr;

    public List<CertDetails> extractDetailsFromMap(final List<Map<String, Object>> certs) {
        final List<CertDetails> details = new ArrayList<>();
        for (final Map<String, Object> cert : certs) {
            details.add(new CertDetails(cert));
        }
        return details;
    }

    private void setUp_positive1() throws FileNotFoundException {
        when(mockCkTrstAlInTsk.getNode()).thenReturn(NODE);
        when(resourceBean.getFileSystemResource("src/test/resources/cer2.pem")).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(new FileInputStream("src/test/resources/cer2.pem"));

        when(certificateUtility.extractDetailsFromMap(any(List.class))).thenReturn(extractDetailsFromMap(createIpSecTrustCertificateData()));

        final CmResponse cmResponse = buildCmResponse(NODE.getName(), createIpSec());
        when(mockReaderService.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(mockNormNode);
        when(mockReaderService.getMOAttribute(any(NodeReference.class), eq(Model.ME_CONTEXT.managedElement.ipSystem.ipSec.type()),
                eq(Model.ME_CONTEXT.managedElement.ipSystem.ipSec.namespace()), eq(ModelDefinition.IpSec.INSTALLED_TRUSTED_CERTIFICATES)))
                        .thenReturn(cmResponse);

        when(mockCkTrstAlInTsk.getTrustCerts()).thenReturn("src/test/resources/cer2.pem");

        try {
            when(mockSecurityService.getTrustStoreForNode(eq(TrustedCertCategory.IPSEC), Mockito.any(NodeRef.class), eq(false)))
                    .thenReturn(mockTrustStoreInfo);
        } catch (SmrsDirectoryException | CertificateException | UnknownHostException | CppSecurityServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void setUp_positive2() throws SmrsDirectoryException, CertificateException {
        when(mockCkTrstAlInTsk.getNode()).thenReturn(NODE);
        when(resourceBean.getFileSystemResource("IPSEC")).thenThrow(FileNotFoundException.class);
        final CmResponse cmResponse = buildCmResponse(NODE.getName(), createIpSec());
        when(mockReaderService.getMOAttribute(any(NodeReference.class), eq(Model.ME_CONTEXT.managedElement.ipSystem.ipSec.type()),
                eq(Model.ME_CONTEXT.managedElement.ipSystem.ipSec.namespace()), eq(ModelDefinition.IpSec.INSTALLED_TRUSTED_CERTIFICATES)))
                        .thenReturn(cmResponse);

        when(mockReaderService.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(mockNormNode);
        when(mockCkTrstAlInTsk.getTrustCerts()).thenReturn(String.valueOf(TrustedCertCategory.IPSEC));
        try {
            when(mockSecurityService.getTrustStoreForNode(eq(TrustedCertCategory.IPSEC), Mockito.any(NodeRef.class), eq(false)))
                    .thenReturn(mockTrustStoreInfo);
        } catch (final CppSecurityServiceException e) {
            e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
        } catch (final UnknownHostException e) {
            e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
        }
        when(certificateUtility.extractDetailsFromMap(any(List.class))).thenReturn(extractDetailsFromMap(createIpSecTrustCertificateData()));

    }

    private void setUp_negative1() {
        when(mockCkTrstAlInTsk.getNode()).thenReturn(NODE);
        final CmResponse cmResponse = buildCmResponse_empty(NODE.getName());
        when(mockReaderService.getMOAttribute(any(NodeReference.class), eq(Model.ME_CONTEXT.managedElement.ipSystem.ipSec.type()),
                eq(Model.ME_CONTEXT.managedElement.ipSystem.ipSec.namespace()), eq(ModelDefinition.IpSec.INSTALLED_TRUSTED_CERTIFICATES)))
                        .thenReturn(cmResponse);
        when(mockCkTrstAlInTsk.getTrustCerts()).thenReturn("src/test/resources/cer2.pem");

    }

    private void setUp_negative2() {
        when(mockCkTrstAlInTsk.getNode()).thenReturn(NODE);
        final CmResponse cmResponse = buildCmResponse_negative(NODE.getName(), createIpSec());
        when(mockReaderService.getMOAttribute(any(NodeReference.class), eq(Model.ME_CONTEXT.managedElement.ipSystem.ipSec.type()),
                eq(Model.ME_CONTEXT.managedElement.ipSystem.ipSec.namespace()), eq(ModelDefinition.IpSec.INSTALLED_TRUSTED_CERTIFICATES)))
                        .thenReturn(cmResponse);
        when(mockCkTrstAlInTsk.getTrustCerts()).thenReturn("src/test/resources/cer2.pem");

    }

    @Test
    public void testProcessTask_positive() throws Exception {
        setUp_positive1();
        Assert.assertEquals("Expected Installed response", "INSTALLED", ckTrstAlInsTskHndlr.processTask(mockCkTrstAlInTsk));

    }

    @Test
    public void testProcessTask_positive_calling_pkiMgr() throws Exception {
        setUp_positive2();
        Assert.assertEquals("Expected Installed response", "INSTALLED", ckTrstAlInsTskHndlr.processTask(mockCkTrstAlInTsk));

    }

    @Test(expected = MissingMoAttributeException.class)
    public void testProcessTask_negative_empty_response() throws Exception {
        setUp_negative1();
        ckTrstAlInsTskHndlr.processTask(mockCkTrstAlInTsk);

    }

    @Test(expected = UnexpectedErrorException.class)
    public void testProcessTask_negative_more_than_one_response() throws Exception {
        setUp_negative2();
        ckTrstAlInsTskHndlr.processTask(mockCkTrstAlInTsk);

    }

    private CmResponse buildCmResponse(final String nodeName, final Map<String, Object> attributesMap) {
        final CmResponse cmResponse = new CmResponse();
        final Collection<CmObject> cmObjects = new ArrayList<>(1);
        final CmObject cmObject = new CmObject();
        cmObject.setAttributes(attributesMap);
        cmObject.setFdn("MeContext=" + nodeName);
        cmObjects.add(cmObject);

        cmResponse.setTargetedCmObjects(cmObjects);
        cmResponse.setStatusCode(0);
        return cmResponse;

    }

    private CmResponse buildCmResponse_empty(final String nodeName) {
        final CmResponse cmResponse = new CmResponse();
        final Map<String, Object> attributesMap = new HashMap<>();
        cmResponse.setStatusCode(0);
        return cmResponse;

    }

    private CmResponse buildCmResponse_negative(final String nodeName, final Map<String, Object> attributesMap) {
        final CmResponse cmResponse = new CmResponse();
        final Collection<CmObject> cmObjects = new ArrayList<>(1);
        final CmObject cmObject = new CmObject();
        cmObject.setAttributes(attributesMap);
        cmObject.setFdn("MeContext=" + nodeName);
        cmObjects.add(cmObject);
        final CmObject cmObject1 = new CmObject();
        cmObjects.add(cmObject1);

        cmResponse.setTargetedCmObjects(cmObjects);
        cmResponse.setStatusCode(0);
        return cmResponse;

    }

    private List<Map<String, Object>> createIpSecTrustCertificateData() {

        final Map<String, Object> trustCert1 = createMapAndInsertValues(IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_ISSUER_ATTRIBUTE,
                IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_ISSUER_ATTRIBUTE_VALUE, IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_SUBJECT_ATTRIBUTE,
                IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_SUBJECT_ATTRIBUTE_VALUE, IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_NOTVALIDAFTER_ATTRIBUTE,
                IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_NOTVALIDAFTER_ATTRIBUTE_VALUE, IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_SERIALNUMBER_ATTRIBUTE,
                IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_SERIALNUMBER_ATTRIBUTE_VALUE, IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE,
                IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE_VALUE);

        final Map<String, Object> trustCert2 = createMapAndInsertValues(IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_ISSUER_ATTRIBUTE,
                IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_ISSUER_ATTRIBUTE_VALUE, IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_SUBJECT_ATTRIBUTE,
                IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_SUBJECT_ATTRIBUTE_VALUE, IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_NOTVALIDAFTER_ATTRIBUTE,
                IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_NOTVALIDAFTER_ATTRIBUTE_VALUE, IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_SERIALNUMBER_ATTRIBUTE,
                IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_SERIALNUMBER_ATTRIBUTE_VALUE, IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE,
                IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE_VALUE);

        final Map<String, Object> trustCert3 = createMapAndInsertValues(IP_SEC_2_INSTALLED_TRUSTED_CERTIFICATE_ISSUER_ATTRIBUTE,
                IP_SEC_2_INSTALLED_TRUSTED_CERTIFICATE_ISSUER_ATTRIBUTE_VALUE, IP_SEC_2_INSTALLED_TRUSTED_CERTIFICATE_SUBJECT_ATTRIBUTE,
                IP_SEC_2_INSTALLED_TRUSTED_CERTIFICATE_SUBJECT_ATTRIBUTE_VALUE, IP_SEC_2_INSTALLED_TRUSTED_CERTIFICATE_NOTVALIDAFTER_ATTRIBUTE,
                IP_SEC_2_INSTALLED_TRUSTED_CERTIFICATE_NOTVALIDAFTER_ATTRIBUTE_VALUE, IP_SEC_2_INSTALLED_TRUSTED_CERTIFICATE_SERIALNUMBER_ATTRIBUTE,
                IP_SEC_2_INSTALLED_TRUSTED_CERTIFICATE_SERIALNUMBER_ATTRIBUTE_VALUE, IP_SEC_2_INSTALLED_TRUSTED_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE,
                IP_SEC_2_INSTALLED_TRUSTED_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE_VALUE);

        final List<Map<String, Object>> trustCerts = new ArrayList<>();
        trustCerts.add(trustCert1);
        trustCerts.add(trustCert2);
        trustCerts.add(trustCert3);

        return trustCerts;
    }

    public Map<String, Object> createIpSec() {
        final Map<String, Object> mandatoryIpSecAttributes = createMapAndInsertValues(MANDATORY_IP_SEC_ATTRIBUTE, MANDATORY_IP_SEC_ATTRIBUTE_VALUE,
                Model.ME_CONTEXT.managedElement.ipSystem.ipSec.FEATURE_STATE, ModelDefinition.IpSec.StateVals.ENABLED.toString(),
                Model.ME_CONTEXT.managedElement.ipSystem.ipSec.LICENSE_STATE, ModelDefinition.IpSec.ActivationVals.ACTIVATED.toString(),
                Model.ME_CONTEXT.managedElement.ipSystem.ipSec.TRUSTED_CERT_INST_STATE,
                ModelDefinition.IpSec.IpSecTrustedCertInstallStateValue.ERROR.toString(),
                Model.ME_CONTEXT.managedElement.ipSystem.ipSec.CERT_ENROLL_STATE, ModelDefinition.IpSec.IpSecCertEnrollStateValue.ERROR.toString());

        final Map<String, Object> ipSecCertInfo = createMapAndInsertValues(IP_SEC_CERTIFICATE_SERIALNUMBER_ATTRIBUTE,
                IP_SEC_CERTIFICATE_SERIALNUMBER_ATTRIBUTE_VALUE, IP_SEC_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE,
                IP_SEC_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE_VALUE);
        mandatoryIpSecAttributes.put(Model.ME_CONTEXT.managedElement.ipSystem.ipSec.CERTIFICATE, ipSecCertInfo);

        mandatoryIpSecAttributes.put(Model.ME_CONTEXT.managedElement.ipSystem.ipSec.INSTALLED_TRUSTED_CERTIFICATES,
                createIpSecTrustCertificateData());

        return mandatoryIpSecAttributes;
    }

    private Map<String, Object> createMapAndInsertValues(final String... keyValues) {
        final Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < keyValues.length; i += 2) {
            map.put(keyValues[i], keyValues[i + 1]);
        }

        return map;
    }

    private class mockNormalizableNodeRef implements NormalizableNodeReference {

        @Override
        public String getName() {
            return "LTE05ERBS00015";
        }

        @Override
        public String getFdn() {
            return "MeContext=LTE05ERBS00015";
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
            return "ERBS";
        }

        @Override
        public String getOssModelIdentity() {
            return "";
        }
    }
}
