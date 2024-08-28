package com.ericsson.nms.security.nscs.handler.command.impl;

/**
 *
 */
import static com.ericsson.nms.security.nscs.handler.command.impl.MockUtils.setupCommandContext;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.GetCertEnrollStateCommand;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec.IpSecCertEnrollStateValue;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Security.CertEnrollStateValue;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.moget.MOGetServiceFactory;
import com.ericsson.nms.security.nscs.data.moget.param.CertStateInfo;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.utility.NscsGetCertEnrollStateResponseBuilder;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.util.ExtendedCertDetails;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;

/**
 * Tests the Listings i.e. the security level of the requested nodes
 *
 * @see CppGetSecurityLevelHandler
 *
 * @author eabdsin
 */

@RunWith(MockitoJUnitRunner.class)
public class GetCertEnrollStateHandlerTest {

    private static final String NODE12 = "node1";
    private static final String NODE_NAME_HEADER = "Node Name";
    private static final String CERT_ENROLL_STATE_KEY = "Enroll State";
    private static final String CERT_ENROLL_ERROR_MSG_KEY = "Enroll Error Message";
    private static final String SERIAL_NUM_KEY = "Serial Number";
    private static final String ISSUER_KEY = "Issuer";
    private static final String SUBJECT_KEY = "Subject";
    private static final String SUBJ_ALT_NAME_KEY = "Subject Alternative Name";
    private static final String NOT_APPLICABLE = "Not Applicable";

    private static String[] certEnrollHeaderValue = { CERT_ENROLL_STATE_KEY, CERT_ENROLL_ERROR_MSG_KEY, SUBJECT_KEY, SERIAL_NUM_KEY, ISSUER_KEY,
            SUBJ_ALT_NAME_KEY };

    @Spy
    private final Logger logger = LoggerFactory.getLogger(GetCertEnrollStateHandlerTest.class);

    @InjectMocks
    private GetCertEnrollStateHandler beanUnderTest;

    @Mock
    private NscsCMReaderService cMReaderService;

    @Mock
    private NscsCapabilityModelService capabilityModelService;

    @Mock
    private MOGetServiceFactory moGetServiceFactory;

    @Mock
    private CommandContext commandContext;

    @Mock
    private NscsCommandManager commandManager;

    @Mock
    private NodeValidatorUtility validatorUtility;

    /**
     * Tests the positive flow for GetCertEnrollmentStateHandler with IPSEC certType
     *
     * @throws Exception
     */
    @Test
    public void testProcess_GetCertEnrollStateHandler_CPP_IpSec_Positive() throws Exception {

        logger.info("Starting GetCertEnrollStateHandler_IpSec_Positive");

        final String certType = CertificateType.IPSEC.toString();
        final String nodeName = NODE12;
        final String nodeFdn = "NetworkElement=" + nodeName;
        final String certEnrollState = IpSecCertEnrollStateValue.IDLE.toString();
        final String certEnrollErrMsg = "Error if certEnrollState is ERROR";
        final String serialNum = "1234567";
        final String issuer = "myCA";
        final String subject = "node1-IPSEC";
        final String subjAltName = "192.168.1.200";

        final GetCertEnrollStateCommand getCertEnrollStateCommand = setUpDataCppIpSec(certType, nodeName, certEnrollState, certEnrollErrMsg,
                serialNum, issuer, subject, subjAltName);

        logger.info("test: certType for mock command is {}", getCertEnrollStateCommand.getCertType());

        final NscsCommandResponse nscsResponse1 = beanUnderTest.process(getCertEnrollStateCommand, commandContext);

        assertTrue("Should be of name value pair response type", nscsResponse1.isNameMultipleValueResponseType());

        final NscsNameMultipleValueCommandResponse commandResponse = ((NscsNameMultipleValueCommandResponse) nscsResponse1);

        final Iterator<NscsNameMultipleValueCommandResponse.Entry> iterator = commandResponse.iterator();

        final NscsNameMultipleValueCommandResponse.Entry title = iterator.next();
        assertTrue(NODE_NAME_HEADER.equals(title.getName()));

        for (int i = 0; i < certEnrollHeaderValue.length; i++) {
            assertTrue(certEnrollHeaderValue[i].equals(title.getValues()[i]));
        }

        final NscsNameMultipleValueCommandResponse.Entry content = iterator.next();

        assertEquals(nodeFdn, content.getName());
        final CertStateInfo certStateInfo = buildCertStateInfo("IPSEC", nodeFdn, certEnrollState, certEnrollErrMsg, issuer, serialNum, subject,
                subjAltName);
        final NscsGetCertEnrollStateResponseBuilder builder = new NscsGetCertEnrollStateResponseBuilder();
        assertArrayEquals(builder.formatRow(certStateInfo), content.getValues());
    }

    /**
     * Tests the positive flow for GetCertEnrollmentStateHandler with OAM CertType
     *
     * @throws Exception
     */
    @Test
    public void testProcess_GetCertEnrollStateHandler_CPP_OAM_Positive() throws Exception {

        logger.info("Starting GetCertEnrollStateHandler_OAM_Positive");

        final String certType = CertificateType.OAM.toString();
        final String nodeName = NODE12;
        final String nodeFdn = "NetworkElement=" + nodeName;
        final String certEnrollState = CertEnrollStateValue.IDLE.toString();
        final String certEnrollErrMsg = "Error if certEnrollState is ERROR";
        final String serialNum = "1234567";
        final String issuer = "myCA";
        final String subject = "node1-OAM";
        final String subjectAltName = NOT_APPLICABLE;

        final GetCertEnrollStateCommand getCertEnrollStateCommand = setUpDataOAM(certType, nodeName, certEnrollState, certEnrollErrMsg, serialNum,
                issuer, subject);

        final NscsCommandResponse nscsResponse1 = beanUnderTest.process(getCertEnrollStateCommand, commandContext);

        assertTrue("Should be of name value pair response type", nscsResponse1.isNameMultipleValueResponseType());
        final NscsNameMultipleValueCommandResponse commandResponse = ((NscsNameMultipleValueCommandResponse) nscsResponse1);

        final Iterator<NscsNameMultipleValueCommandResponse.Entry> iterator = commandResponse.iterator();

        final NscsNameMultipleValueCommandResponse.Entry title = iterator.next();
        assertTrue(NODE_NAME_HEADER.equals(title.getName()));

        for (int i = 0; i < certEnrollHeaderValue.length; i++) {
            assertTrue(certEnrollHeaderValue[i].equals(title.getValues()[i]));
        }

        final NscsNameMultipleValueCommandResponse.Entry content = iterator.next();

        assertEquals(nodeFdn, content.getName());
        final CertStateInfo certStateInfo = buildCertStateInfo("OAM", nodeFdn, certEnrollState, certEnrollErrMsg, issuer, serialNum, subject,
                subjectAltName);
        final NscsGetCertEnrollStateResponseBuilder builder = new NscsGetCertEnrollStateResponseBuilder();
        assertArrayEquals(builder.formatRow(certStateInfo), content.getValues());
    }

    /**
     * Tests the positive flow for GetCertEnrollmentStateHandller with IPSEC CertType and CertEnrollState = ERROR
     *
     * @throws Exception
     */
    @Test
    public void testProcess_GetCertEnrollStateHandler_IpSec_ErrorMsg_Positive() throws Exception {

        logger.info("Starting GetCertEnrollStateHandler_IpSec_ErrorMsg_Positive");

        final String certType = CertificateType.IPSEC.toString();
        final String nodeName = NODE12;
        final String nodeFdn = "NetworkElement=" + nodeName;
        final String certEnrollState = IpSecCertEnrollStateValue.ERROR.toString();
        final String certEnrollErrMsgAfterParsing = "Error if certEnrollState is ERROR";
        final String serialNum = "1234567";
        final String issuer = "myCA";
        final String subject = "node1-IPSEC";
        final String subjAltName = "192.168.1.200";

        final GetCertEnrollStateCommand getCertEnrollStateCommand = setUpDataCppIpSec(certType, nodeName, certEnrollState,
                certEnrollErrMsgAfterParsing, serialNum, issuer, subject, subjAltName);

        logger.info("test: certType for mock command is {}", getCertEnrollStateCommand.getCertType());

        final NscsCommandResponse nscsResponse1 = beanUnderTest.process(getCertEnrollStateCommand, commandContext);

        assertTrue("Should be of name value pair response type", nscsResponse1.isNameMultipleValueResponseType());
        final NscsNameMultipleValueCommandResponse commandResponse = ((NscsNameMultipleValueCommandResponse) nscsResponse1);

        final Iterator<NscsNameMultipleValueCommandResponse.Entry> iterator = commandResponse.iterator();

        final NscsNameMultipleValueCommandResponse.Entry title = iterator.next();
        assertTrue(NODE_NAME_HEADER.equals(title.getName()));

        for (int i = 0; i < certEnrollHeaderValue.length; i++) {
            assertTrue(certEnrollHeaderValue[i].equals(title.getValues()[i]));
        }

        final NscsNameMultipleValueCommandResponse.Entry content = iterator.next();

        assertEquals(nodeFdn, content.getName());
        final CertStateInfo certStateInfo = buildCertStateInfo("IPSEC", nodeFdn, certEnrollState, certEnrollErrMsgAfterParsing, issuer, serialNum,
                subject, subjAltName);
        final NscsGetCertEnrollStateResponseBuilder builder = new NscsGetCertEnrollStateResponseBuilder();
        assertArrayEquals(builder.formatRow(certStateInfo), content.getValues());
    }

    /**
     * Tests the positive flow for GetCertEnrollmentStateHandler with OAM certType and CertEnrollState to be converted from PREPARING_REQUEST to
     * ONGOING
     *
     * @throws Exception
     */
    @Test
    public void testProcess_GetCertEnrollStateHandler_OAM_Convert_CertEnrollState_Positive() throws Exception {

        logger.info("Starting GetCertEnrollStateHandler_OAM_Convert_CertEnrollState_Positive");

        final String certType = CertificateType.OAM.toString();
        final String nodeName = NODE12;
        final String nodeFdn = "NetworkElement=" + nodeName;
        final String certEnrollStateAfterConversion = IpSecCertEnrollStateValue.ONGOING.toString();
        final String certEnrollErrMsg = "Error if certEnrollState is ERROR";
        final String serialNum = "1234567";
        final String issuer = "myCA";
        final String subject = "node1-OAM";
        final String subjectAltName = NOT_APPLICABLE;

        final GetCertEnrollStateCommand getCertEnrollStateCommand = setUpDataOAM(certType, nodeName, certEnrollStateAfterConversion, certEnrollErrMsg,
                serialNum, issuer, subject);

        final NscsCommandResponse nscsResponse1 = beanUnderTest.process(getCertEnrollStateCommand, commandContext);

        assertTrue("Should be of name value pair response type", nscsResponse1.isNameMultipleValueResponseType());
        final NscsNameMultipleValueCommandResponse commandResponse = ((NscsNameMultipleValueCommandResponse) nscsResponse1);

        final Iterator<NscsNameMultipleValueCommandResponse.Entry> iterator = commandResponse.iterator();

        final NscsNameMultipleValueCommandResponse.Entry title = iterator.next();
        assertTrue(NODE_NAME_HEADER.equals(title.getName()));

        for (int i = 0; i < certEnrollHeaderValue.length; i++) {
            assertTrue(certEnrollHeaderValue[i].equals(title.getValues()[i]));
        }

        final NscsNameMultipleValueCommandResponse.Entry content = iterator.next();

        assertEquals(nodeFdn, content.getName());
        final CertStateInfo certStateInfo = buildCertStateInfo("OAM", nodeFdn, certEnrollStateAfterConversion, certEnrollErrMsg, issuer, serialNum,
                subject, subjectAltName);
        final NscsGetCertEnrollStateResponseBuilder builder = new NscsGetCertEnrollStateResponseBuilder();
        assertArrayEquals(builder.formatRow(certStateInfo), content.getValues());
    }

    private GetCertEnrollStateCommand setUpDataCppIpSec(final String certType, final String nodeName, final String certEnrollState,
            final String certEnrollErrMsg, final String serialNum, final String issuer, final String subjectName, final String subjAltName) {

        final GetCertEnrollStateCommand cmd = buildGetCertEnrollStateCommand(certType, nodeName);

        org.mockito.Mockito.mock(NscsCMWriterService.WriterSpecificationBuilder.class);

        final NodeReference nr = new NodeRef(nodeName);

        final CertStateInfo certStateInfo = buildCertStateInfo("IPSEC", nr.getFdn(), certEnrollState, certEnrollErrMsg, issuer, serialNum,
                subjectName, subjAltName);
        when(moGetServiceFactory.getCertificateIssueStateInfo(any(NodeReference.class), anyString())).thenReturn(certStateInfo);
        setUpMocksCppIpsec(nodeName);
        setupCommandContext(commandContext, nodeName);

        return cmd;
    }

    private GetCertEnrollStateCommand setUpDataOAM(final String certType, final String nodeName, final String certEnrollState,
            final String certEnrollErrMsg, final String serialNum, final String issuer, final String subject) {

        final GetCertEnrollStateCommand cmd = buildGetCertEnrollStateCommand(certType, nodeName);

        org.mockito.Mockito.mock(NscsCMWriterService.WriterSpecificationBuilder.class);

        final NodeReference nr = new NodeRef(nodeName);

        final String subjAltName = NOT_APPLICABLE;

        final CertStateInfo certStateInfo = buildCertStateInfo("OAM", nr.getFdn(), certEnrollState, certEnrollErrMsg, issuer, serialNum, subject,
                subjAltName);
        when(moGetServiceFactory.getCertificateIssueStateInfo(any(NodeReference.class), anyString())).thenReturn(certStateInfo);

        setUpMocksCppOAM(nodeName);

        setupCommandContext(commandContext, nodeName);

        return cmd;
    }

    private GetCertEnrollStateCommand buildGetCertEnrollStateCommand(final String certType, final String nodeName) {
        final GetCertEnrollStateCommand getCertEnrollStateCommand = new GetCertEnrollStateCommand();
        getCertEnrollStateCommand.setCommandType(NscsCommandType.GET_CERT_ENROLL_STATE);

        final Map<String, Object> commandMap = new HashMap<String, Object>() {

            private static final long serialVersionUID = 1L;

            {
                {
                    put(GetCertEnrollStateCommand.CERT_TYPE_PROPERTY, certType);
                    put(NscsNodeCommand.NODE_LIST_PROPERTY, Arrays.asList(nodeName));
                }
            }
        };
        getCertEnrollStateCommand.setProperties(commandMap);
        return getCertEnrollStateCommand;
    }

    private void setUpMocksCppOAM(final String nodeName) {
        setUpMocks();

        when(capabilityModelService.isCliCommandSupported(any(NormalizableNodeReference.class), anyString())).thenReturn(true);

        when(capabilityModelService.isCertTypeSupported(any(NormalizableNodeReference.class), eq("OAM"))).thenReturn(true);

        final List<NodeReference> inputList = new ArrayList<NodeReference>();
        final List<NodeReference> validNodesList = new ArrayList<NodeReference>();
        final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();
        final Map<String, String[]> invalidDynamicNodesMap = new HashMap<>();

        final NodeReference nodeRef = new NodeRef(nodeName);
        inputList.add(nodeRef);
        final String certType = "OAM";

        when(commandManager.validateNodesGetCertEnrollTrustInstallState(NscsCapabilityModelService.CERTIFICATE_COMMAND, certType, inputList,
                validNodesList, invalidNodesErrorMap, invalidDynamicNodesMap)).thenAnswer(new Answer<Boolean>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public Boolean answer(final InvocationOnMock invocation) {
                        final Object[] args = invocation.getArguments();

                        ((List<NodeReference>) args[3]).add(nodeRef);

                        return true;
                    }
                });

        final NormalizableNodeReference reference = mock(NormalizableNodeReference.class);
        doReturn(nodeName).when(reference).getName();
        doReturn(nodeRef).when(reference).getNormalizedRef();
        when(cMReaderService.getNormalizableNodeReference(nodeRef)).thenReturn(reference);
    }

    private void setUpMocksCppIpsec(final String nodeName) {
        setUpMocks();
        when(capabilityModelService.isCliCommandSupported(any(NormalizableNodeReference.class), anyString())).thenReturn(true);

        final List<NodeReference> inputList = new ArrayList<NodeReference>();
        final List<NodeReference> validNodesList = new ArrayList<NodeReference>();
        final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();
        final Map<String, String[]> invalidDynamicNodesMap = new HashMap<>();

        final NodeReference nodeRef = new NodeRef(nodeName);
        inputList.add(nodeRef);
        final String certType = "IPSEC";

        when(commandManager.validateNodesGetCertEnrollTrustInstallState(NscsCapabilityModelService.CERTIFICATE_COMMAND, certType, inputList,
                validNodesList, invalidNodesErrorMap, invalidDynamicNodesMap)).thenAnswer(new Answer<Boolean>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public Boolean answer(final InvocationOnMock invocation) {
                        final Object[] args = invocation.getArguments();
                        ((List<NodeReference>) args[3]).add(nodeRef);
                        return true;
                    }
                });

        final NormalizableNodeReference reference = mock(NormalizableNodeReference.class);
        doReturn(nodeName).when(reference).getName();
        doReturn(nodeRef).when(reference).getNormalizedRef();
        when(cMReaderService.getNormalizableNodeReference(nodeRef)).thenReturn(reference);
    }

    private void setUpMocks() {
    }

    private CertStateInfo buildCertStateInfo(final String certType, final String nodeName, final String certEnrollState, final String certInfoErrMsg,
            final String issuer, final String serialNum, final String subjectName, final String subjectAltName) {
        String subjectAlternativeName = subjectAltName;
        String certEnrollErrorMsg = certInfoErrMsg;
        if ("OAM".equals(certType)) {
            subjectAlternativeName = NOT_APPLICABLE;
            if (!CertEnrollStateValue.ERROR.toString().equals(certEnrollState)) {
                certEnrollErrorMsg = "";
            }
        } else {
            if (!IpSecCertEnrollStateValue.ERROR.toString().equals(certEnrollState)) {
                certEnrollErrorMsg = "";
            }
        }
        final List<CertDetails> certificates = Arrays
                .asList(ExtendedCertDetails.certDetailsFactory(issuer, serialNum, subjectName, subjectAlternativeName));
        final CertStateInfo certStateInfo = new CertStateInfo(nodeName, certEnrollState, certEnrollErrorMsg, certificates);
        return certStateInfo;
    }
}
