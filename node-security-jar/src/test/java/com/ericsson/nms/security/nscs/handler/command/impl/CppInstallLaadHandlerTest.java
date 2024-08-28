package com.ericsson.nms.security.nscs.handler.command.impl;

import static com.ericsson.nms.security.nscs.handler.command.impl.MockUtils.setupCommandContext;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.command.*;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.exception.LaadFileInstallationException;
import com.ericsson.nms.security.nscs.cpp.level.SecLevelProcessor;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.laad.service.InstallLaad;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;

/**
 * Tests the Installation of LAAD file into specified nodes
    *
    * @see CppInstallLaadHandler
    * @author eabdsin
    */
    @Ignore("LAAD not supported yet")
    @RunWith(MockitoJUnitRunner.class)
    public class CppInstallLaadHandlerTest {

        private static final String _3 = "LEVEL_3";
        private static final String _2 = "LEVEL_2";
        private static final String NODE12 = "node1";
        public static final String NODE_NAME_HEADER = "Node Name        ";
        public static final String NODE_SECURITY_LEVEL_HEADER = "Node Security Level";
        public static final String CHAR_ENCODING = "UTF-8";

        @Spy
        private final Logger logger = LoggerFactory.getLogger(CppSetSecurityLevelHandler.class);

    @InjectMocks
    private CppInstallLaadHandler beanUnderTest;

    @Mock
    InstallLaad laadService;

    @Mock
    private NscsCMReaderService cMReaderService;

    @Mock
    private CommandContext commandContext;

    @Before
    public void setupTest() {
        setupCommandContext(commandContext, NODE12);
    }

    /**
     * Tests the positive flow of CppInstallLaadHandler
     * 
     * @throws Exception
     */
    @Test
    public void testProcess_CppInstallLaadHandler_Positive() throws Exception {

        final NscsNodeCommand nscsNodeCommand = setUpData(_3);

        final NscsCommandResponse nscsResponse1 = beanUnderTest.process(nscsNodeCommand, commandContext);

        assertTrue(CppInstallLaadHandler.INSTALL_LAAD_INITIATED.equals(((NscsMessageCommandResponse) nscsResponse1).getMessage()));

    }

    /**
     * Tests the negative flow of CppInstallLaadHandler
     * 
     * @throws Exception
     */

    @Test
    public void testProcess_CppInstallLaadHandler_Negative() throws Exception {

        final NscsNodeCommand nscsNodeCommand = setUpData(_2);
        boolean thrown = false;
        try {

            beanUnderTest.process(nscsNodeCommand, commandContext);
            fail("Shouldn't come here");
        } catch (final LaadFileInstallationException e) {
            assertTrue(e != null);
            thrown = true;

        }
        assertTrue(thrown);

    }

    private NscsNodeCommand setUpData(final String currentLevel) {
        final NscsNodeCommand nscsNodeCommand = buildNscsNodeCommand();
        final CmResponse cmResponse = buildCmResponse(currentLevel);

        setUpMocks(cmResponse);
        return nscsNodeCommand;
    }

    private void setUpMocks(final CmResponse cmResponse) {
        mockCmReaderService(cmResponse);
        mock(SecLevelProcessor.class);

    }

    private void mockCmReaderService(final CmResponse cmResponse) {

        when(
                cMReaderService.getMOAttribute(eq(Model.ME_CONTEXT.managedElement.systemFunctions.security.type()),
                        eq(Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace()), eq(Model.ME_CONTEXT.managedElement.systemFunctions.security.OPERATIONAL_SECURITY_LEVEL), anyListOf(String.class))).thenReturn(
                cmResponse);
        when(
                cMReaderService.getMOAttribute(anyListOf(NodeReference.class), eq(Model.ME_CONTEXT.managedElement.systemFunctions.security.type()),
                        eq(Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace()), eq(Model.ME_CONTEXT.managedElement.systemFunctions.security.OPERATIONAL_SECURITY_LEVEL))).thenReturn(
                cmResponse);

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

    private NscsNodeCommand buildNscsNodeCommand() {
        final NscsNodeCommand cppSetSecurityLevelCommand = new NscsNodeCommand();
        cppSetSecurityLevelCommand.setCommandType(NscsCommandType.CPP_INSTALL_LAAD);

        final Map<String, Object> commandMap = new HashMap<String, Object>() {

            private static final long serialVersionUID = 1L;

            {
                {
                    put(NscsNodeCommand.NODE_LIST_PROPERTY, Arrays.asList(NODE12));
                }
            }
        };
        cppSetSecurityLevelCommand.setProperties(commandMap);
        return cppSetSecurityLevelCommand;
    }

}
