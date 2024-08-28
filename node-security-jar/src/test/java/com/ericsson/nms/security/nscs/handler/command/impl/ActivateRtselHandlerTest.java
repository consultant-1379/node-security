/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.command.impl;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse;
import com.ericsson.nms.security.nscs.api.command.types.RtselCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidInputXMLFileException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.XmlOperatorUtils;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.enrollmentinfo.request.NodeDetailsUnMarshaller;
import com.ericsson.nms.security.nscs.enrollmentinfo.request.NodeDetailsValidator;
import com.ericsson.nms.security.nscs.enrollmentinfo.request.model.NodeDetails;
import com.ericsson.nms.security.nscs.enrollmentinfo.request.model.NodeDetailsList;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.impl.RtselValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.rtsel.request.model.NodeFdns;
import com.ericsson.nms.security.nscs.rtsel.request.model.NodeInfo;
import com.ericsson.nms.security.nscs.rtsel.request.model.NodeRtselConfig;
import com.ericsson.nms.security.nscs.rtsel.request.model.Nodes;
import com.ericsson.nms.security.nscs.rtsel.request.model.RtselConfiguration;
import com.ericsson.nms.security.nscs.rtsel.utility.ActivateRtselResponseBuilder;
import com.ericsson.nms.security.nscs.rtsel.utility.RtselConstants;
import com.ericsson.nms.security.nscs.rtsel.utility.RtselXMLValidator;
import com.ericsson.nms.security.nscs.util.CliUtil;
import com.ericsson.nms.security.nscs.utilities.XMLUnMarshallerUtility;
import com.ericsson.nms.security.nscs.utilities.XmlValidatorUtility;
import com.ericsson.nms.security.nscs.utilities.XsdErrorHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * Test Class for ActivateRtselHandler.
 * 
 * @author zkakven
 * 
 */

@RunWith(MockitoJUnitRunner.class)
public class ActivateRtselHandlerTest {

    @InjectMocks
    ActivateRtselHandler activateRtselHandler;

    @Mock
    CommandContext context;

    @Mock
    NscsLogger nscsLogger;

    @Mock
    CliUtil cliUtil;

    @Mock
    XmlValidatorUtility xmlValidatorUtility;

    @Mock
    NodeModelInformation nodeModelInformation;

    @Mock
    XMLUnMarshallerUtility xmlUnMarshallerUtility;

    @Mock
    NscsCMReaderService nscsCmReaderService;

    @Mock
    XmlOperatorUtils xmlOperatorUtils;

    @Mock
    NodeDetailsUnMarshaller nodeDetailsUnMarshaller;

    @Mock
    NodeDetailsValidator nodeDetailsValidator;

    @Mock
    NscsCapabilityModelService nscsCapabilityModelService;

    @Mock
    RtselValidator rtselValidator;

    @Mock
    RtselXMLValidator rtselXMLValidator;

    @Mock
    private ActivateRtselResponseBuilder activateRtselResponseBuilder;

    @Mock
    private NscsContextService nscsContextService;

    private final String FILE_URI = "file:";
    private final String ENROLLMENT_MODE_TYPE = "CMPv2_INITIAL";
    private final String NODE_FDN = "LTE02ERBS00003";
    private final String NODE_FDN1 = "LTE02ERBS00003";
    private final String NODE_FDN2 = "LTE02ERBS00002";
    private final String ENTITY_PROFILE_NAME = "MicroRBSOAM_CHAIN_EP";
    private final String KEY_SIZE = "RSA_2048";
    private final File XML_FILE = new File("src/test/resources/Rtsel_Activate.xml");
    private String fileData;
    private int ConnAttemptTimeOut = 12;
    private String ExtServerLogLevel = "INFO";
    private String ExtServerAppName = "Ericsson";
    private RtselCommand command = new RtselCommand();
    XsdErrorHandler xsdErrorHandler = new XsdErrorHandler();
    RtselConfiguration rtselConfiguration = new RtselConfiguration();
    private Map<String, Object> properties = new HashMap<String, Object>();
    private NodeRtselConfig nodeRtselConfig = new NodeRtselConfig();
    final Set<String> duplicateNodes = new HashSet<String>();
    private Nodes nodes = new Nodes();
    NodeInfo nodeInfo = new NodeInfo();
    NodeFdns nodeFdns = new NodeFdns();

    private List<NodeDetails> nodeDetailsList = new ArrayList<NodeDetails>();
    private NodeDetailsList nodeSpecificConfigurationsBatch = new NodeDetailsList();
    final Map<String, Object> validNodesMap = new HashMap<String, Object>();

    final List<Map<String, Object>> nodeInfoDetailsList = new ArrayList<Map<String, Object>>();
    final List<Map<String, Object>> validNodesInXml = new ArrayList<Map<String, Object>>();
    NscsMessageCommandResponse nscsCommandResponseExcepted = new NscsMessageCommandResponse();
    private final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();

    @Before
    public void setUp() throws IOException {

        properties.put("command", NscsCommandType.RTSEL_ACTIVATE);
        command.setProperties(properties);

        List<String> nodeFdnsList = new ArrayList<String>();
        nodeFdnsList.add(NODE_FDN);
        nodeFdnsList.add(NODE_FDN1);
        nodeFdnsList.add(NODE_FDN2);
        nodeFdns.getNodeFdn().addAll(nodeFdnsList);

        nodeInfo.setNodeFdns(nodeFdns);
        nodeInfo.setEnrollmentMode(ENROLLMENT_MODE_TYPE);
        nodeInfo.setEntityProfileName(ENTITY_PROFILE_NAME);
        nodeInfo.setKeySize(KEY_SIZE);

        nodes.getNodeInfo().add(nodeInfo);

        nodeRtselConfig.setNodes(nodes);
        nodeRtselConfig.setConnAttemptTimeOut(ConnAttemptTimeOut);
        nodeRtselConfig.setExtServerLogLevel(ExtServerLogLevel);
        nodeRtselConfig.setExtServerAppName(ExtServerAppName);

        nodeSpecificConfigurationsBatch.setList(nodeDetailsList);
        fileData = getFileData(XML_FILE);
        nscsCommandResponseExcepted.setMessage("RTSEL Activated Sucessfull");
    }

    @Test
    public void testProcess() {
        rtselConfiguration.getNodeRtselConfig().add(nodeRtselConfig);
        duplicateNodes.add(NODE_FDN);
        xsdErrorHandler.setValid(true);
        Mockito.when(cliUtil.getCommandInputData(command, FILE_URI)).thenReturn(fileData);
        Mockito.when(xmlValidatorUtility.validateXMLSchemaWithErrorHandler(fileData, RtselConstants.RTSEL_XSD_VALIDATOR_FILE)).thenReturn(xsdErrorHandler);
        Mockito.when(xmlUnMarshallerUtility.xMLUnmarshaller(fileData, RtselConfiguration.class)).thenReturn(rtselConfiguration);
        Mockito.when(rtselValidator.getDuplicateNodesForActivateRtsel(rtselConfiguration.getNodeRtselConfig(), invalidNodesErrorMap)).thenReturn(duplicateNodes);
        activateRtselHandler.process(command, context);
    }

    @Test
    public void testProcess_AllValidNodes() {
        rtselConfiguration.getNodeRtselConfig().add(nodeRtselConfig);
        duplicateNodes.add(NODE_FDN);
        xsdErrorHandler.setValid(true);
        Mockito.when(cliUtil.getCommandInputData(command, FILE_URI)).thenReturn(fileData);
        Mockito.when(xmlValidatorUtility.validateXMLSchemaWithErrorHandler(fileData, RtselConstants.RTSEL_XSD_VALIDATOR_FILE))
                .thenReturn(xsdErrorHandler);
        Mockito.when(xmlUnMarshallerUtility.xMLUnmarshaller(fileData, RtselConfiguration.class)).thenReturn(rtselConfiguration);
        Mockito.when(rtselValidator.getDuplicateNodesForActivateRtsel(rtselConfiguration.getNodeRtselConfig(), invalidNodesErrorMap))
                .thenReturn(duplicateNodes);
        Mockito.doNothing().when(rtselValidator).validateNode(Mockito.any(), Mockito.any(), Mockito.any());
        activateRtselHandler.process(command, context);
    }

    @Test(expected = InvalidInputXMLFileException.class)
    public void testProcess_InvalidXMLFailedException() {
        xsdErrorHandler.setValid(false);
        Mockito.when(cliUtil.getCommandInputData(command, FILE_URI)).thenReturn(fileData);
        Mockito.when(xmlValidatorUtility.validateXMLSchemaWithErrorHandler(fileData, RtselConstants.RTSEL_XSD_VALIDATOR_FILE)).thenReturn(xsdErrorHandler);
        activateRtselHandler.process(command, context);
        final NscsMessageCommandResponse nscsMessageCommandResponse = (NscsMessageCommandResponse) activateRtselHandler.process(command, context);
        assertTrue(nscsMessageCommandResponse.getMessage().contains(NscsErrorCodes.INVALID_INPUT_XML_FILE));
    }

    @Test(expected = InvalidInputXMLFileException.class)
    public void testProcess_XMLSchemaValidationsFailedException() {
        xsdErrorHandler.setValid(false);
        Mockito.when(cliUtil.getCommandInputData(command, FILE_URI)).thenReturn(fileData);
        Mockito.when(xmlValidatorUtility.validateXMLSchemaWithErrorHandler(fileData, RtselConstants.RTSEL_XSD_VALIDATOR_FILE)).thenReturn(xsdErrorHandler);
        final NscsMessageCommandResponse nscsMessageCommandResponse = (NscsMessageCommandResponse) activateRtselHandler.process(command, context);
        assertTrue(nscsMessageCommandResponse.getMessage().contains(NscsErrorCodes.XML_SCHEMA_VALIDATIONS_FAILED));
    }

    private String getFileData(File xmlFile) throws IOException {
        Reader fileReader = new FileReader(xmlFile);
        final BufferedReader bufReader = new BufferedReader(fileReader);
        StringBuilder sb = new StringBuilder();
        String line = bufReader.readLine();
        while (line != null) {
            sb.append(line).append("\n");
            line = bufReader.readLine();
        }
        return sb.toString();
    }

}
