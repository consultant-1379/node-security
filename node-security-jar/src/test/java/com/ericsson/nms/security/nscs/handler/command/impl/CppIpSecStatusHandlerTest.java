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
package com.ericsson.nms.security.nscs.handler.command.impl;

import com.ericsson.nms.security.nscs.api.command.*;
import com.ericsson.nms.security.nscs.api.command.types.CppIpSecStatusCommand;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.CppIpSecStatusUtility;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.IpSecNodeValidatorUtility;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.logger.NscsLogger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CppIpSecStatusHandlerTest {

	private static final String NODE12 = "node1";

	@InjectMocks
	private CppIpSecStatusHandler testObj;
	
	@Mock
	private NscsLogger nscsLogger;
	 
	@Mock
	private CppIpSecStatusUtility mockIpSecStatusUtility;

	@Mock
        private IpSecNodeValidatorUtility mockIpSecNodeValidatorUtility;

	@Mock
	private NscsCMReaderService cMReaderService;

	private static final String NODE1 = "node1";

	private static final String ACTIVATED = "ACTIVATED";
	private static final String DEACTIVATED = "DEACTIVATED";
	private static final String UNKNOWN = "UNKNOWN";
	private static final String[] CMD_RESPONSE_FAILURE = new String[] { DEACTIVATED,
			DEACTIVATED };
	private static final String[] CMD_RESPONSE = new String[] { ACTIVATED,
		ACTIVATED };
	private static final String[] CMD_RESPONSE_UNKNOWN = new String[] { UNKNOWN,
		UNKNOWN };
	
	
	@Spy
    private final Logger logger = LoggerFactory.getLogger(CppIpSecStatusHandler.class);
	@Mock
	private CommandContext mockCommandContext;

	@Before
	public void setupTest() {
		MockUtils.setupCommandContext(mockCommandContext, NODE12);
	}

	@Test
	public void testProcess_IpSecStatus_withOneNode_Negetive() throws Exception {
		Mockito.when(mockIpSecStatusUtility.getIpSecFeatureState(Mockito.any(NodeReference.class))).thenReturn(ACTIVATED);
		final CppIpSecStatusCommand statusCommand = setupCommand();
		Mockito.when(mockIpSecStatusUtility.isOMActivated(Mockito.any(NodeReference.class), Mockito.matches(ACTIVATED))).thenReturn(false);
		final NscsCommandResponse nscsResponse1 = testObj.process(statusCommand,
				mockCommandContext);

		assertTrue("Should be of name multiple value pair response type",
				nscsResponse1.isNameMultipleValueResponseType());
		final NscsNameMultipleValueCommandResponse commandResponse = ((NscsNameMultipleValueCommandResponse) nscsResponse1);

		final Iterator<NscsNameMultipleValueCommandResponse.Entry> iterator = commandResponse
				.iterator();
		int index = 0;
		while (iterator.hasNext()) {
			final NscsNameMultipleValueCommandResponse.Entry content = iterator
					.next();
			if (index == 0) {
				assertTrue(CppIpSecStatusHandler.STATUS_HEADER[0]
						.equals(content.getName()));
				for (int i = 0; i < commandResponse.getValueSize(); i++) {
					assertTrue(CppIpSecStatusHandler.STATUS_HEADER[i + 1]
							.equals(content.getValues()[i]));
				}
				++index;
			} else {
				assertEquals(NODE1, content.getName());
				for (int i = 1; i < commandResponse.getValueSize(); i++) {
					assertEquals(CMD_RESPONSE_FAILURE[i], content.getValues()[i]);
				}
			}
		}
	}
	
	@Test
	public void testProcess_IpSecStatus_withOneNode_Positive() throws Exception {
		Mockito.when(mockIpSecStatusUtility.getIpSecFeatureState(Mockito.any(NodeReference.class))).thenReturn(ACTIVATED);
		final CppIpSecStatusCommand statusCommand = setupCommand();
		Mockito.when(mockIpSecStatusUtility.isOMActivated(Mockito.any(NodeReference.class), Mockito.matches(ACTIVATED))).thenReturn(true);
		Mockito.when(mockIpSecStatusUtility.isTrafficActivated(Mockito.any(NodeReference.class), Mockito.matches(ACTIVATED))).thenReturn(true);
		final NscsCommandResponse nscsResponse1 = testObj.process(statusCommand,
				mockCommandContext);

		assertTrue("Should be of name multiple value pair response type",
				nscsResponse1.isNameMultipleValueResponseType());
		final NscsNameMultipleValueCommandResponse commandResponse = ((NscsNameMultipleValueCommandResponse) nscsResponse1);

		final Iterator<NscsNameMultipleValueCommandResponse.Entry> iterator = commandResponse
				.iterator();
		int index = 0;
		while (iterator.hasNext()) {
			final NscsNameMultipleValueCommandResponse.Entry content = iterator
					.next();
			if (index == 0) {
				assertTrue(CppIpSecStatusHandler.STATUS_HEADER[0]
						.equals(content.getName()));
				for (int i = 0; i < commandResponse.getValueSize(); i++) {
					assertTrue(CppIpSecStatusHandler.STATUS_HEADER[i + 1]
							.equals(content.getValues()[i]));
				}
				++index;
			} else {
				assertEquals(NODE1, content.getName());
				for (int i = 1; i < commandResponse.getValueSize(); i++) {
					assertEquals(CMD_RESPONSE[i], content.getValues()[i]);
				}
			}
		}
	}
	
	@Test
	public void testProcess_IpSecStatus_with_InvalidNode() throws Exception {
		Mockito.when(mockIpSecStatusUtility.getIpSecFeatureState(Mockito.any(NodeReference.class))).thenReturn("UNKNOWN");
		CppIpSecStatusCommand statusCommand = setupCommand();
		Mockito.when(mockIpSecStatusUtility.isOMActivated(Mockito.any(NodeReference.class), Mockito.matches("UNKNOWN"))).thenReturn(false);
		final NscsCommandResponse nscsResponse1 = testObj.process(statusCommand,
				mockCommandContext);

		assertTrue("Should be of name multiple value pair response type",
				nscsResponse1.isNameMultipleValueResponseType());
		final NscsNameMultipleValueCommandResponse commandResponse = ((NscsNameMultipleValueCommandResponse) nscsResponse1);

		final Iterator<NscsNameMultipleValueCommandResponse.Entry> iterator = commandResponse
				.iterator();
		int index = 0;
		while (iterator.hasNext()) {
			final NscsNameMultipleValueCommandResponse.Entry content = iterator
					.next();
			if (index == 0) {
				assertTrue(CppIpSecStatusHandler.STATUS_HEADER[0]
						.equals(content.getName()));
				for (int i = 0; i < commandResponse.getValueSize(); i++) {
					assertTrue(CppIpSecStatusHandler.STATUS_HEADER[i + 1]
							.equals(content.getValues()[i]));
				}
				++index;
			} else {
				assertEquals(NODE1, content.getName());
				for (int i = 1; i < commandResponse.getValueSize(); i++) {
					assertEquals(CMD_RESPONSE_UNKNOWN[i], content.getValues()[i]);
				}
			}
		}
	}
	
	
	 private CppIpSecStatusCommand setupCommand() {
	        final CppIpSecStatusCommand command = new CppIpSecStatusCommand();
	        command.setCommandType(NscsCommandType.CPP_IPSEC_STATUS);
	        final Map<String, Object> commandMap = new HashMap<String, Object>() {
	            private static final long serialVersionUID = 1L;
	            {
	                {
	                    put(CppIpSecStatusCommand.CONFIGURATION, "true");
	                }
	            }
	        };
	        command.setProperties(commandMap);
	        return command;
	    }


}
