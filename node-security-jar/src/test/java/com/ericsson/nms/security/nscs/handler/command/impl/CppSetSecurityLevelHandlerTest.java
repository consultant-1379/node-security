package com.ericsson.nms.security.nscs.handler.command.impl;

/**
 * 
 */

import static com.ericsson.nms.security.nscs.handler.command.impl.MockUtils.setupCommandContext;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.command.*;
import com.ericsson.nms.security.nscs.api.command.types.CppSecurityLevelCommand;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedCommandArgumentException;
import com.ericsson.nms.security.nscs.cpp.level.*;
import com.ericsson.nms.security.nscs.data.*;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;

/**
 * Tests the CppSetSecurityLevelHandler that Starts the process to change security level of a node or list of nodes
 * 
 * @see CppSetSecurityLevelHandler
 * @author eabdsin
 */

@RunWith(MockitoJUnitRunner.class)
public class CppSetSecurityLevelHandlerTest {

    /**
     * 
     */
    private static final String _3 = "LEVEL_3";

    /**
     * 
     */
    private static final String _2 = "LEVEL_2";

    /**
     * 
     */
    private static final String _1 = "LEVEL_1";
    private static final String NODE12 = "node1";
    public static final String NODE_NAME_HEADER = "Node Name        ";
    public static final String NODE_SECURITY_LEVEL_HEADER = "Node Security Level";
    public static final String CHAR_ENCODING = "UTF-8";

    @Spy
    private final Logger logger = LoggerFactory.getLogger(CppSetSecurityLevelHandler.class);

    @InjectMocks
    private CppSetSecurityLevelHandler beanUnderTest;

    @Mock
    private NscsCMReaderService cMReaderService;

    @Mock
    private SecurityLevelProcessorFactory securityLevelProcessorFactory;

    @Mock
    private SystemRecorder systemRecorder;

    @Mock
    private CommandContext commandContext;

    @Before
    public void setupTest() {
        setupCommandContext(commandContext, NODE12);
    }

    /**
     * Tests the process to change security level of a node or list of nodes when current and required are the same
     * 
     * @throws Exception
     */
  //Xml Scalability purpose
    @Ignore
    @Test
    public void testProcess_CppSetSecurityLevelHandler_Negative_SameLevel() throws Exception {

        final CppSecurityLevelCommand cmd = setUpData(_1, _1);

        final NscsCommandResponse nscsResponse1 = beanUnderTest.process(cmd, commandContext);

        assertTrue(CppSetSecurityLevelHandler.SECURITY_LEVEL_INITIATED_CHECK_THE_LOGS_FOR_DETAILS.equals(((NscsMessageCommandResponse) nscsResponse1).getMessage()));

    }

    /**
     * Tests the process to change security level of a node or list of nodes when current is 1 and required is at 2
     * 
     * @throws Exception
     */
    //Xml Scalability purpose
    @Ignore
    @Test
    public void testProcess_CppSetSecurityLevelHandler_Positive_1_To_2() throws Exception {

        final CppSecurityLevelCommand cmd = setUpData(_1, _2);

        final NscsCommandResponse nscsResponse1 = beanUnderTest.process(cmd, commandContext);

        assertTrue(CppSetSecurityLevelHandler.SECURITY_LEVEL_INITIATED_CHECK_THE_LOGS_FOR_DETAILS.equals(((NscsMessageCommandResponse) nscsResponse1).getMessage()));

        verify(securityLevelProcessorFactory).createSecLevelProcessor(any(SecLevelRequest.class));

    }

    /**
     * Tests the process to change security level of a node or list of nodes when current is 1 and required is at 3
     *
     * @throws Exception
     */
    @Ignore("Not in scope of 14B")
    @Test
    public void testProcess_CppSetSecurityLevelHandler_Positive_1_To_3() throws Exception {

        final CppSecurityLevelCommand cmd = setUpData(_1, _3);

        final NscsCommandResponse nscsResponse1 = beanUnderTest.process(cmd, commandContext);

        assertTrue(CppSetSecurityLevelHandler.SECURITY_LEVEL_INITIATED_CHECK_THE_LOGS_FOR_DETAILS.equals(((NscsMessageCommandResponse) nscsResponse1).getMessage()));

        verify(securityLevelProcessorFactory).createSecLevelProcessor(any(SecLevelRequest.class));

    }

    /**
     * Tests the process to change security level of a node or list of nodes when current is 1 and required is at 3
     *
     * @throws Exception
     */
    @Test
    @Ignore("Not in scope of 14B")
    public void testProcess_CppSetSecurityLevelHandler_Negative_1_To_3() throws Exception {

        final CppSecurityLevelCommand cmd = setUpData(_3, _1);
        boolean thrown = false;
        try {

            beanUnderTest.process(cmd, commandContext);
            fail("Shouldn't come here");
        } catch (final UnsupportedCommandArgumentException e) {
            assertTrue(e != null);
            thrown = true;

        }
        assertTrue(thrown);

    }

    private CppSecurityLevelCommand setUpData(final String requiredLevel, final String currentLevel) {
        final CppSecurityLevelCommand CppSecurityLevelCommand = buildCppSecurityLevelCommand(requiredLevel);
        final CmResponse cmResponse = buildCmResponse(currentLevel);
        final Collection<CmObject> cmObjects = cmResponse.getCmObjects();
        final CmObject cmObject = cmObjects.iterator().next();

        org.mockito.Mockito.mock(NscsCMWriterService.WriterSpecificationBuilder.class);

        setUpMocks(CppSecurityLevelCommand, cmResponse, cmObject);
        return CppSecurityLevelCommand;
    }

    private void setUpMocks(final CppSecurityLevelCommand CppSecurityLevelCommand, final CmResponse cmResponse, final CmObject cmObject) {
        mockCmReaderService(CppSecurityLevelCommand, cmResponse);
        final SecLevelProcessor levelProcessor = mock(SecLevelProcessor.class);
        when(securityLevelProcessorFactory.createSecLevelProcessor(any(SecLevelRequest.class))).thenReturn(levelProcessor);

    }

    private void mockCmReaderService(final CppSecurityLevelCommand CppSecurityLevelCommand, final CmResponse cmResponse) {

        when(
                cMReaderService.getMOAttribute(NODE12, Model.ME_CONTEXT.managedElement.systemFunctions.security.type(), Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace(),
                        Model.ME_CONTEXT.managedElement.systemFunctions.security.OPERATIONAL_SECURITY_LEVEL)).thenReturn(cmResponse);

    }

    private CmResponse buildCmResponse(final String currentLevel) {
        final CmResponse cmResponse = new CmResponse();
        final Map<String, Object> attributesMap = new HashMap<>();

        attributesMap.put(Model.ME_CONTEXT.managedElement.systemFunctions.security.OPERATIONAL_SECURITY_LEVEL, currentLevel);

        final Collection<CmObject> cmObjects = new ArrayList<>(1);
        final CmObject cmObject = new CmObject();
        cmObject.setAttributes(attributesMap);
        cmObject.setFdn(NODE12);
        cmObjects.add(cmObject);

        cmResponse.setTargetedCmObjects(cmObjects);
        cmResponse.setStatusCode(0);
        return cmResponse;

    }

    private CppSecurityLevelCommand buildCppSecurityLevelCommand(final String requiredLevel) {
        final CppSecurityLevelCommand CppSecurityLevelCommand = new CppSecurityLevelCommand();
        CppSecurityLevelCommand.setCommandType(NscsCommandType.CPP_SET_SL);

        final Map<String, Object> commandMap = new HashMap<String, Object>() {

            private static final long serialVersionUID = 1L;

            {
                {
                    put(NscsNodeCommand.NODE_LIST_PROPERTY, Arrays.asList(NODE12));
                    put(CppSecurityLevelCommand.SECURITY_LEVEL_PROPERTY, requiredLevel);

                }
            }
        };
        CppSecurityLevelCommand.setProperties(commandMap);
        return CppSecurityLevelCommand;
    }

}
