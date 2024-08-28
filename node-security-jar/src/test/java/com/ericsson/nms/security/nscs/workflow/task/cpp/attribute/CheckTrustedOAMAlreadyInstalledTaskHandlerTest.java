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
package com.ericsson.nms.security.nscs.workflow.task.cpp.attribute;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.model.SmrsAccountInfo;
import com.ericsson.nms.security.nscs.cpp.model.TrustStoreInfo;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.laad.service.ResourcesBean;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.utilities.NSCSCertificateUtility;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.itpf.sdk.resources.Resource;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoAttributeException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.CheckTrustedOAMAlreadyInstalledTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

/**
 * @author enmadmin
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CheckTrustedOAMAlreadyInstalledTaskHandlerTest {
    private static final String CERT_PATH = "src/test/resources/cer2.pem";
    private static final String installedTrustCertificatesAttribute = ModelDefinition.Security.INSTALLED_TRUSTED_CERTIFICATES;
    private static final String SUBJECT_KEY = "subject";
    private static final String SUBJECT_VALUE = "O=Ericsson, OU=ericssonOAM, CN=atclvm387RootCA";
    private static final String SERIAL_NUM_KEY = "serialNumber";
    private static final String SERIAL_NUM_VALUE = "237097996";
    private static final String ISSUER_KEY = "issuer";
    private static final String ISSUER_VALUE = SUBJECT_VALUE;

    private static final String TRUST_STORE_INFO_SERIAL_NUMBER = "123456789";
    private static final String TRUST_STORE_INFO_SUBJECT = "O=Ericsson, OU=ericssonOAM, CN=DUMMYRootCA";
    private static final String TRUST_STORE_INFO_ISSUER = "DummyIssuer";

    private static TrustedCertCategory certCategory = TrustedCertCategory.CORBA_PEERS;

    private static final String INSTALLED = "INSTALLED";
    private static final String NOT_INSTALLED = "NOT_INSTALLED";

    private static final NodeReference NODE = new NodeRef("MeContext=LTE05ERBS00015");
    private final NormalizableNodeReference mockNormNode = new MockNormalizableNodeRef();

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    CheckTrustedOAMAlreadyInstalledTask mockCkTrstAlInTsk;

    @Mock
    private NscsCMReaderService mockReaderService;

    @Mock
    private ResourcesBean resourceBean;

    @Mock
    private Resource resource;

    @Mock
    private CppSecurityService mockSecurityService;

    @Mock
    private SystemRecorder systemRecorder;

    @Mock
    private TrustStoreInfo mockTrustStoreInfo;

    @Mock
    private NscsCapabilityModelService capabilityModel;

    @Mock
    private Mo mockedMo;

    @Mock
    private X509Certificate mockCert;

    @Mock
    private Principal x509PrincipalSubjectDN;

    @Mock
    private Principal x509PrincipalIssuerDN;

    @Mock
    private NSCSCertificateUtility certificateUtility;

    @InjectMocks
    private CheckTrustedOAMAlreadyInstalledTaskHandler ckTrstAlInsTskHndlr;

    @Before
    public void setup() throws FileNotFoundException {

        when(mockCkTrstAlInTsk.getNode()).thenReturn(NODE);
        when(resourceBean.getFileSystemResource(CERT_PATH)).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(new FileInputStream(CERT_PATH));
        when(mockReaderService.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(mockNormNode);

        when(mockCkTrstAlInTsk.getTrustCerts()).thenReturn(certCategory.toString());
    }

    private SmrsAccountInfo generateAccountInfo() {
        return Mockito.mock(SmrsAccountInfo.class);
    }

    /**
     * @param atts
     * @return
     * @throws Exception
     *             The method sets the mocked CertSpec with values data read from Map
     */
    private CertSpec generateCertSpec(final Map<String, Object> atts) throws Exception {
        final CertSpec mockSpec = Mockito.mock(CertSpec.class);

        BigInteger serialNumber = null;
        String subject = null;
        String issuer = null;

        serialNumber = BigInteger.valueOf(Long.parseLong(atts.get(SERIAL_NUM_KEY).toString()));
        subject = atts.get(SUBJECT_KEY).toString();
        issuer = atts.get(ISSUER_KEY).toString();

        doReturn(x509PrincipalSubjectDN).when(mockCert).getSubjectDN();
        doReturn(x509PrincipalIssuerDN).when(mockCert).getIssuerDN();

        doReturn(serialNumber).when(mockCert).getSerialNumber();
        doReturn(subject).when(x509PrincipalSubjectDN).toString();
        doReturn(issuer).when(x509PrincipalIssuerDN).toString();

        doReturn(mockCert).when(mockSpec).getCertHolder();

        return mockSpec;
    }

    private void setUp_AlreadyInstalled_EmptyContentForGetTrustStoreInfo() {
        //    	when(certificateUtility.extractDetailsFromMap(any(List.class))).thenReturn(extractDetailsFromMap(createListOfCertificateData()));
        when(certificateUtility.extractDetailsFromMap(any(List.class)))
                .thenReturn(extractDetailsFromMap(createListOfCertificateData(SERIAL_NUM_VALUE, SUBJECT_VALUE, ISSUER_VALUE)));
        final CmResponse cmResponse = buildCmResponse(NODE.getName(), createTrustOAM());
        when(mockReaderService.getMOAttribute(any(NodeReference.class), any(String.class), any(String.class), any(String.class)))
                .thenReturn(cmResponse);
        /*
         * We mock the response from CppSecurityBean, method getTrustStoreInfo() with dummy empty response, so the task handler will return
         * 'INSTALLED' state, because no new certificates must be added to the node
         */
        try {
            when(mockSecurityService.getTrustStoreForNode(eq(certCategory), Mockito.any(NodeRef.class), eq(false))).thenReturn(mockTrustStoreInfo);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void setUp_AlreadyInstalled_SameCertsOnNodeAndFromGetTrustStoreInfo() throws Exception {
        when(certificateUtility.extractDetailsFromMap(any(List.class)))
                .thenReturn(extractDetailsFromMap(createListOfCertificateData(SERIAL_NUM_VALUE, SUBJECT_VALUE, ISSUER_VALUE)));
        final List<Map<String, Object>> atts = createTrustOAM();
        final CmResponse cmResponse = buildCmResponse(NODE.getName(), atts);
        when(mockReaderService.getMOAttribute(any(NodeReference.class), any(String.class), any(String.class), any(String.class)))
                .thenReturn(cmResponse);
        /*
         * We mock the response from CppSecurityBean, method getTrustStoreInfo() with a response containing the same Certificates as the ones returned
         * by the node, so the task handler will return 'INSTALLED' state, because no new certificates must be added to the node
         */
        final TrustStoreInfo trustStoreinfo = generateTestTrustStoreInfo(certCategory, atts);
        try {
            when(mockSecurityService.getTrustStoreForNode(eq(certCategory), Mockito.any(NodeRef.class), eq(false))).thenReturn(trustStoreinfo);
        } catch (final Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unchecked")
    private TrustStoreInfo generateTestTrustStoreInfo(final TrustedCertCategory category, final List<Map<String, Object>> atts) throws Exception {
        final Set<CertSpec> certSpecs = new HashSet<>();
        final List<SmrsAccountInfo> accountInfos = new ArrayList<>();
        for (final Map<String, Object> attributes : atts) {
            final List<Map<String, Object>> listMap = (List<Map<String, Object>>) attributes.get(installedTrustCertificatesAttribute);
            certSpecs.add(generateCertSpec(listMap.get(0)));
        }
        accountInfos.add(generateAccountInfo());
        final TrustStoreInfo trust = new TrustStoreInfo(category, certSpecs, accountInfos, DigestAlgorithm.SHA1);
        return trust;
    }

    private void setUp_NotInstalled_EmptyResponseFromReader() {
        /*
         * We mock the response from NSCSReader, with a response containing empty attributes to read from, so the task handler will return
         * 'NOT_INSTALLED' state, because new certificates must be added to the node
         */
        final CmResponse cmResponse = buildCmResponse(NODE.getName(), new ArrayList<Map<String, Object>>());
        when(mockReaderService.getMOAttribute(any(NodeReference.class), any(String.class), any(String.class), any(String.class)))
                .thenReturn(cmResponse);
    }

    public List<CertDetails> extractDetailsFromMap(final List<Map<String, Object>> certs) {
        final List<CertDetails> details = new ArrayList<>();
        for (final Map<String, Object> cert : certs) {
            details.add(new CertDetails(cert));
        }
        return details;
    }

    private List<Map<String, Object>> createListOfCertificateData(final String serial, final String subject, final String issuer) {
        final List<Map<String, Object>> attributesListMap = new ArrayList<Map<String, Object>>();
        final Map<String, Object> singleAttMap = new HashMap<String, Object>();
        singleAttMap.put(SERIAL_NUM_KEY, serial);
        singleAttMap.put(SUBJECT_KEY, subject);
        singleAttMap.put(ISSUER_KEY, issuer);
        attributesListMap.add(singleAttMap);
        return attributesListMap;
    }

    private void setUp_NotInstalled() throws Exception {
        final CmResponse cmResponse = buildCmResponse(NODE.getName(), createTrustOAM());
        when(mockReaderService.getMOAttribute(any(NodeReference.class), any(String.class), any(String.class), any(String.class)))
                .thenReturn(cmResponse);
        /*
         * We mock the response from CppSecurityBean, method getTrustStoreInfo() with a response containing different Certificates, so the task
         * handler will return 'NOT_INSTALLED' state, because new certificates must be added to the node
         */
        final List<Map<String, Object>> attListMap = new ArrayList<Map<String, Object>>();
        final Map<String, Object> attMap = new HashMap<String, Object>();
        final List<Map<String, Object>> attributesListMap = createListOfCertificateData(TRUST_STORE_INFO_SERIAL_NUMBER, TRUST_STORE_INFO_SUBJECT,
                TRUST_STORE_INFO_ISSUER);

        attMap.put(installedTrustCertificatesAttribute, attributesListMap);
        attListMap.add(attMap);

        final TrustStoreInfo trustStoreinfo = generateTestTrustStoreInfo(certCategory, attListMap);
        try {
            when(mockSecurityService.getTrustStoreForNode(eq(certCategory), Mockito.any(NodeRef.class), eq(false))).thenReturn(trustStoreinfo);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private CmResponse buildCmResponse(final String nodeName, final List<Map<String, Object>> attributesMapList) {
        final CmResponse cmResponse = new CmResponse();

        final int listSize = attributesMapList.size();

        final Collection<CmObject> cmObjects = new ArrayList<>(listSize);

        for (final Map<String, Object> attr : attributesMapList) {
            final CmObject cmObject = new CmObject();
            cmObject.setAttributes(attr);
            cmObject.setFdn("MeContext=" + nodeName);
            cmObjects.add(cmObject);
        }
        cmResponse.setTargetedCmObjects(cmObjects);
        cmResponse.setStatusCode(0);
        return cmResponse;
    }

    private List<Map<String, Object>> createTrustOAM() {
        final List<Map<String, Object>> attrMapList = new ArrayList<Map<String, Object>>();
        final Map<String, Object> OAMTrustCertContent = new HashMap<String, Object>();

        final List<Map<String, Object>> trustCerts = createListOfCertificateData(SERIAL_NUM_VALUE, SUBJECT_VALUE, ISSUER_VALUE);

        OAMTrustCertContent.put(installedTrustCertificatesAttribute, trustCerts);

        attrMapList.add(OAMTrustCertContent);

        return attrMapList;
    }

    private class MockNormalizableNodeRef implements NormalizableNodeReference {

        private static final long serialVersionUID = -3799671708615088019L;

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

    @Ignore
    @Test
    public void testProcessTask_AlreadyInstalled_EmptyContentForGetTrustStoreInfo() throws Exception {
        setUp_AlreadyInstalled_EmptyContentForGetTrustStoreInfo();
        Assert.assertEquals("Expected Installed response", INSTALLED, ckTrstAlInsTskHndlr.processTask(mockCkTrstAlInTsk));
    }

    @Ignore
    @Test
    public void testProcessTask_AlreadyInstalled_SameCertsOnNodeAndFromGetTrustStoreInfo() throws Exception {
        setUp_AlreadyInstalled_SameCertsOnNodeAndFromGetTrustStoreInfo();
        Assert.assertEquals("Expected Installed response", INSTALLED, ckTrstAlInsTskHndlr.processTask(mockCkTrstAlInTsk));
    }

    @Ignore
    @Test
    public void testProcessTask_NotInstalled() throws Exception {
        setUp_NotInstalled();
        Assert.assertEquals("Expected Not Installed response", NOT_INSTALLED, ckTrstAlInsTskHndlr.processTask(mockCkTrstAlInTsk));
    }

    @Ignore
    @Test(expected = MissingMoAttributeException.class)
    public void testProcessTask_AlreadyInstalled_EmptyResponseFromReader() throws Exception {
        setUp_NotInstalled_EmptyResponseFromReader();
        final String result = ckTrstAlInsTskHndlr.processTask(mockCkTrstAlInTsk);
    }
}
