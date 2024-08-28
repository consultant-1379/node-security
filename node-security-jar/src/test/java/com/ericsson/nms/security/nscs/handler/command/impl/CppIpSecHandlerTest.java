/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
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
import com.ericsson.nms.security.nscs.api.command.types.CppIpSecCommand;
import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.*;
import com.ericsson.nms.security.nscs.cpp.ipsec.wf.CppIpSecWfsConfiguration;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.logger.NscsLogger;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.*;
import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class CppIpSecHandlerTest {

	private final String EMPTY_STRING = "";

	private final String FILE_XML = "file:abc.xml";

	/* Used in the test for now */
	private final String IP_SEC_INITIATED_CHECK_THE_LOGS_FOR_DETAILS = "IPsec activation/deactivation change initiated for %d valid node(s), check the system logs for the progress.";

	private final String IP_SEC_NOT_INITIATED = "IPsec activation/deactivation not initiated as all provided node(s) are invalid. Error Detail(s) for respective node(s) is/are listed below.";

	@Mock
	private NscsLogger nscsLogger;

	@Mock
	private CommandContext mockCommandContext;

	@Mock
	private CppIpSecStatusHandler mockCppIpSecStatusHandler;

	@Mock
	private CppIpSecStatusUtility mockIpSecStatusUtility;

	@Mock
	private CppIpSecWfsConfiguration mockIpSecWfHandler;

	@Mock
	private XmlValidatorUtils mockUtility;

	@Mock
	private IpSecNodeValidatorUtility mockNodeValidatorUtil;

	@InjectMocks
	private CppIpSecHandler testObj;

	//Xml Scalability purpose
    @Ignore
	@Test
	public void testProcess() {
		Mockito.when(mockUtility.validateXMLSchema(Mockito.anyString()))
				.thenReturn(true);
		Mockito.when(
				mockNodeValidatorUtil.validateNodeForIpSecOperation(Mockito
						.any(NodeReference.class))).thenReturn(true);
		final CppIpSecCommand command = setupCommand();
		final NscsCommandResponse response = testObj.process(command,
				mockCommandContext);
		Assert.assertNotNull("Response can't be null", response);
		Assert.assertEquals("Expecting workflow started successfully message.",
				IP_SEC_INITIATED_CHECK_THE_LOGS_FOR_DETAILS,
				((NscsMessageCommandResponse) response).getMessage());
	}

	//Xml Scalability purpose
    @Ignore
	@Test(expected = InvalidFileContentException.class)
	public void testProcessWithInvalidXML() {
		final CppIpSecCommand command = setupCommandWithInvalidXML();
		testObj.process(command, mockCommandContext);
	}
	//Xml Scalability purpose
    @Ignore
	@Test(expected = InvalidInputXMLFileException.class)
	public void testProcessWithInvalidData() {
		Mockito.when(mockUtility.validateXMLSchema(Mockito.anyString()))
				.thenReturn(false);
		final CppIpSecCommand command = setupCommandWithInvalidData();
		testObj.process(command, mockCommandContext);
	}

	//Xml Scalability purpose
    @Ignore
	@Test
	public void testProcessForEnableTrafficIpSec() {
		Mockito.when(mockUtility.validateXMLSchema(Mockito.anyString()))
				.thenReturn(true);
		Mockito.when(
				mockNodeValidatorUtil.validateNodeForIpSecOperation(Mockito
						.any(NodeReference.class))).thenReturn(true);
		final CppIpSecCommand command = setupCommandForEnableTraffic();
		final NscsCommandResponse response = testObj.process(command,
				mockCommandContext);
		Assert.assertNotNull("Response can't be null", response);
		Assert.assertEquals("Expecting workflow started successfully message.",
				IP_SEC_INITIATED_CHECK_THE_LOGS_FOR_DETAILS,
				((NscsMessageCommandResponse) response).getMessage());
	}

  //Xml Scalability purpose
    @Ignore
	@Test
	public void testProcessForEnableTrafficIpSec_validationError() {
		Mockito.when(mockUtility.validateXMLSchema(Mockito.anyString()))
				.thenReturn(true);
		Mockito.doThrow(NetworkElementNotfoundException.class)
				.when(mockNodeValidatorUtil)
				.validateNodeForIpSecOperation(Mockito.any(NodeReference.class));
		final CppIpSecCommand command = setupCommandForEnableTraffic();
		final NscsCommandResponse response = testObj.process(command,
				mockCommandContext);
		Assert.assertNotNull("Response can't be null", response);
		Assert.assertTrue("Response object must be NameValueResponseType()",
				response.isNameMultipleValueResponseType());
		Assert.assertEquals("Expected message ",
				IP_SEC_NOT_INITIATED,
				((NscsNameMultipleValueCommandResponse) response)
						.getAdditionalInformation());
		Assert.assertEquals("Only one node has error", 2,
				((NscsNameMultipleValueCommandResponse) response).getValueSize());
	}

	@SuppressWarnings("serial")
	private CppIpSecCommand setupCommand() {
		final CppIpSecCommand command = new CppIpSecCommand();
		command.setCommandType(NscsCommandType.CPP_IPSEC);
		final byte[] INPUT_FILE_CONTENT = convertFileToByteArray("src/test/resources/SampleInputFile.xml");
		final Map<String, Object> commandMap = new HashMap<String, Object>() {
			{
				{
					put(CppIpSecCommand.XML_FILE_PROPERTY, FILE_XML);
					put(CppIpSecCommand.FORCE_UPDATE, EMPTY_STRING);
					put(FILE_XML, INPUT_FILE_CONTENT);
				}
			}
		};
		command.setProperties(commandMap);
		return command;
	}

	@SuppressWarnings("serial")
	private CppIpSecCommand setupCommandWithInvalidData() {
		final CppIpSecCommand command = new CppIpSecCommand();
		command.setCommandType(NscsCommandType.CPP_IPSEC);
		final byte[] INPUT_FILE_CONTENT = convertFileToByteArray("src/test/resources/SampleInvalidInputFile.xml");
		final Map<String, Object> commandMap = new HashMap<String, Object>() {

			{
				{
					put(CppIpSecCommand.XML_FILE_PROPERTY, FILE_XML);
					put(CppIpSecCommand.FORCE_UPDATE, EMPTY_STRING);
					put(FILE_XML, INPUT_FILE_CONTENT);
				}
			}
		};
		command.setProperties(commandMap);
		return command;
	}

	@SuppressWarnings("serial")
	private CppIpSecCommand setupCommandWithInvalidXML() {
		final CppIpSecCommand command = new CppIpSecCommand();
		command.setCommandType(NscsCommandType.CPP_IPSEC);
		final byte[] INPUT_FILE_CONTENT = convertFileToByteArray("src/test/resources/SampleInlidInputFile.xml");
		final Map<String, Object> commandMap = new HashMap<String, Object>() {

			{
				{
					put(CppIpSecCommand.XML_FILE_PROPERTY, FILE_XML);
					put(CppIpSecCommand.FORCE_UPDATE, EMPTY_STRING);
					put(FILE_XML, INPUT_FILE_CONTENT);
				}
			}
		};
		command.setProperties(commandMap);
		return command;
	}

	@SuppressWarnings("serial")
	private CppIpSecCommand setupCommandForEnableTraffic() {
		final CppIpSecCommand command = new CppIpSecCommand();
		command.setCommandType(NscsCommandType.CPP_IPSEC);
		final byte[] INPUT_FILE_CONTENT = convertFileToByteArray("src/test/resources/SampleInputFile.xml");
		final Map<String, Object> commandMap = new HashMap<String, Object>() {

			{
				{
					put(CppIpSecCommand.CONTINUE_AFTER_FAIL, EMPTY_STRING);
					put(CppIpSecCommand.XML_FILE_PROPERTY, FILE_XML);
					put(CppIpSecCommand.FORCE_UPDATE, EMPTY_STRING);
					put(FILE_XML, INPUT_FILE_CONTENT);

				}
			}
		};
		command.setProperties(commandMap);
		return command;
	}

	private byte[] convertFileToByteArray(final String fileLocation) {
		final File file = new File(fileLocation);
		FileInputStream fileInputStream = null;

		final byte[] fileToBeParsed = new byte[(int) file.length()];

		try {
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(fileToBeParsed);
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					// As this is JUnit, we are not logging the proper error.
					e.printStackTrace();
				}
			}
		}
		return fileToBeParsed;
	}

}
