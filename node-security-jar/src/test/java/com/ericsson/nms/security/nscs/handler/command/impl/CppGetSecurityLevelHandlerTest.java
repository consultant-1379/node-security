package com.ericsson.nms.security.nscs.handler.command.impl;

import static com.ericsson.nms.security.nscs.handler.command.impl.MockUtils.setupCommandContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.types.CppSecurityLevelCommand;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.level.SecLevelProcessor;
import com.ericsson.nms.security.nscs.cpp.seclevel.util.*;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.impl.CppGetSecurityLevelValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NormalizedNodeUtils;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;

/**
 * Tests the Listings i.e. the security level of the requested nodes
 *
 * @see CppGetSecurityLevelHandler
 *
 * @author eabdsin
 */

@RunWith(MockitoJUnitRunner.class)
public class CppGetSecurityLevelHandlerTest {

    private static final String _1 = "1";
    private static final String NODE12 = "node1";
    public static final String NODE_NAME_HEADER = "Node Name        ";
    public static final String NODE_SECURITY_LEVEL_HEADER = "Node Security Level";
    public static final String CHAR_ENCODING = "UTF-8";

    @Spy
    private final Logger logger = LoggerFactory.getLogger(CppSetSecurityLevelHandler.class);

    @InjectMocks
    private CppGetSecurityLevelHandler beanUnderTest;

    @Mock
    private NscsCMReaderService cMReaderService;

    @Mock
    private CommandContext commandContext;

    @Mock
    NormalizedNodeUtils cppGetSecurityLevelUtility;

    @Mock
    CppGetSecurityLevelValidator cppGetSecurityLevelValidator;

    @Mock
    NscsLogger nscsLogger;

    @Mock
    NscsCommandResponse nscsCommandResponse;

    @Mock
    CppGetResponseBuilder cppGetResponseBuilder;

    @Mock
    CppGetSecurityLevelDetails cppGetSecurityLevelDetails;

    /**
     * Tests the negative flow for CppSetSecurityLevelHandler
     *
     * @throws Exception
     */
    @Test
    public void testProcess_CppSetSecurityLevelHandler_Positive() throws Exception {

        final CppSecurityLevelCommand cppGetSecurityLevelCommand = setUpData(_1);
        when(cppGetSecurityLevelValidator.validateNodes(anyListOf(NormalizableNodeReference.class), anyListOf(NormalizableNodeReference.class),
                anyMapOf(NodeReference.class, NscsServiceException.class))).thenReturn(true);
        final Map<String, String> secLevelDetails = new HashMap<String, String>();
        final String[] getSecurityLevelHeader = new String[] { CppGetSecurityLevelConstants.NODE_NAME_HEADER,
                CppGetSecurityLevelConstants.NODE_SECURITY_LEVEL_HEADER, CppGetSecurityLevelConstants.LOCAL_AA_MODE,
                CppGetSecurityLevelConstants.ERROR_DETAILS_HEADER, CppGetSecurityLevelConstants.SUGGESTED_SOLUTION };
        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(CppGetSecurityLevelConstants.NO_OF_COLUMNS);
        response.add(getSecurityLevelHeader[0], Arrays.copyOfRange(getSecurityLevelHeader, 1, getSecurityLevelHeader.length));
        secLevelDetails.put(NODE12, _1);
        when(cppGetResponseBuilder.buildResponse(anyListOf(CppGetSecurityLevelDetails.class),
                anyMapOf(NodeReference.class, NscsServiceException.class))).thenReturn(response);
        final NscsCommandResponse nscsResponse1 = beanUnderTest.process(cppGetSecurityLevelCommand, commandContext);
        final NscsNameMultipleValueCommandResponse commandResponse = ((NscsNameMultipleValueCommandResponse) nscsResponse1);
        final Iterator<NscsNameMultipleValueCommandResponse.Entry> iterator = commandResponse.iterator();
        final NscsNameMultipleValueCommandResponse.Entry title = iterator.next();
        assertTrue(CppGetSecurityLevelConstants.NODE_NAME_HEADER.equals(title.getName()));

    }

    /**
     * Tests the Empty List Scenario for CppSetSecurityLevelHandler
     *
     */
    @Test
    public void testProcess_CppGetSecurityLevelHandler_EmptyList_WithoutLevel() {
        final CmResponse cmResponse = mock(CmResponse.class);
        when(cmResponse.getCmObjects()).thenReturn(new ArrayList<CmObject>());
        doReturn(cmResponse).when(cMReaderService).getMOAttribute(any(List.class), anyString(), anyString(), anyString(), anyString());

        final CppSecurityLevelCommand cmd = mock(CppSecurityLevelCommand.class);
        when(cmd.getSecurityLevel()).thenReturn(null);

        setupCommandContext(commandContext, NODE12);

        final NscsCommandResponse nscsResponse1 = beanUnderTest.process(cmd, commandContext);//invoke

        assertTrue(nscsResponse1.isMessageResponseType());
        assertEquals(CppGetSecurityLevelConstants.ERROR_IN_READING_OPERATIONAL_SECURITY_LEVEL_ATTRIBUTE_FOR_THE_NODES,
                ((NscsMessageCommandResponse) nscsResponse1).getMessage());
    }

    @Test
    public void testProcess_CppGetSecurityLevelHandler_EmptyList_WithLevel() {
        final CmResponse cmResponse = mock(CmResponse.class);
        when(cmResponse.getCmObjects()).thenReturn(new ArrayList<CmObject>());
        doReturn(cmResponse).when(cMReaderService).getMOAttribute(any(List.class), anyString(), anyString(), anyString(), anyString());

        final CppSecurityLevelCommand cmd = mock(CppSecurityLevelCommand.class);
        when(cmd.getSecurityLevel()).thenReturn(_1);

        setupCommandContext(commandContext, NODE12);

        final NscsCommandResponse nscsResponse1 = beanUnderTest.process(cmd, commandContext);//invoke
        when(cmResponse.getCmObjects()).thenReturn(Collections.EMPTY_LIST);
        assertTrue(nscsResponse1.isMessageResponseType());
        assertEquals(NscsErrorCodes.NO_NODES_FOUND_AT_REQUESTED_SECURITY_LEVEL, ((NscsMessageCommandResponse) nscsResponse1).getMessage());
    }

    @Test
    public void testFormatLevel() {
        String input = "LEVEL_1";
        String expected = "level 1";
        String result = CppGetSecurityLevelHandler.formatLevel(input);
        assertEquals("Output string is not in proper format", result, expected);

        input = null;
        expected = "";
        result = CppGetSecurityLevelHandler.formatLevel(input);
        assertEquals("Output string is not in proper format", result, expected);
    }

    private CppSecurityLevelCommand setUpData(final String currentLevel) {
        final CppSecurityLevelCommand cmd = buildCppGetSecurityLevelCommand();
        final CmResponse cmResponse = buildCmResponse(currentLevel);
        final Collection<CmObject> cmObjects = cmResponse.getCmObjects();
        final CmObject cmObject = cmObjects.iterator().next();

        org.mockito.Mockito.mock(NscsCMWriterService.WriterSpecificationBuilder.class);

        setUpMocks(cmd, cmResponse, cmObject);
        setupCommandContext(commandContext, NODE12);
        return cmd;

    }

    private void setUpMocks(final NscsNodeCommand cppGetSecurityLevelCommand, final CmResponse cmResponse, final CmObject cmObject) {
        mockCmReaderService(cppGetSecurityLevelCommand, cmResponse);
        final SecLevelProcessor levelProcessor = mock(SecLevelProcessor.class);

    }

    private void mockCmReaderService(final NscsNodeCommand CppGetSecurityLevelCommand, final CmResponse cmResponse) {


        when(cMReaderService.getMOAttribute(eq(Model.ME_CONTEXT.managedElement.systemFunctions.security.type()),
                eq(Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace()),
                eq(Model.ME_CONTEXT.managedElement.systemFunctions.security.OPERATIONAL_SECURITY_LEVEL), anyString(), anyListOf(String.class)))
                        .thenReturn(cmResponse);
        when(cMReaderService.getMOAttribute(anyListOf(NodeReference.class), eq(Model.ME_CONTEXT.managedElement.systemFunctions.security.type()),
                eq(Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace()),
                eq(Model.ME_CONTEXT.managedElement.systemFunctions.security.OPERATIONAL_SECURITY_LEVEL), anyString())).thenReturn(cmResponse);

    }

    private CmResponse buildCmResponse(final String currentLevel) {
        final CmResponse cmResponse = new CmResponse();
        final Map<String, Object> attributesMap = new HashMap<>();

        attributesMap.put(Model.ME_CONTEXT.managedElement.systemFunctions.security.OPERATIONAL_SECURITY_LEVEL, currentLevel);

        final Collection<CmObject> cmObjects = new ArrayList<>(1);
        final CmObject cmObject = new CmObject();
        cmObject.setAttributes(attributesMap);
        cmObject.setFdn("MeContext=" + NODE12);
        cmObjects.add(cmObject);

        cmResponse.setTargetedCmObjects(cmObjects);
        cmResponse.setStatusCode(0);
        return cmResponse;

    }

    private CppSecurityLevelCommand buildCppGetSecurityLevelCommand() {
        final CppSecurityLevelCommand cppGetSecurityLevelCommand = new CppSecurityLevelCommand();
        cppGetSecurityLevelCommand.setCommandType(NscsCommandType.CPP_GET_SL);

        final Map<String, Object> commandMap = new HashMap<String, Object>() {

            private static final long serialVersionUID = 1L;

            {
                {
                    put(NscsNodeCommand.NODE_LIST_PROPERTY, Arrays.asList(NODE12));
                }
            }
        };
        cppGetSecurityLevelCommand.setProperties(commandMap);
        return cppGetSecurityLevelCommand;
    }

}