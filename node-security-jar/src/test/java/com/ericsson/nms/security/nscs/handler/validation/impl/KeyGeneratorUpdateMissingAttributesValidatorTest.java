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
package com.ericsson.nms.security.nscs.handler.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.KeyGeneratorCommand;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;
import com.ericsson.nms.security.nscs.handler.CommandContext;

@RunWith(MockitoJUnitRunner.class)
public class KeyGeneratorUpdateMissingAttributesValidatorTest {

	@InjectMocks
	private KeyGeneratorUpdateMissingAttributesValidator keyGenUpdateMissingValidator;
	
	@Spy
    private final Logger logger = LoggerFactory.getLogger(KeyGeneratorUpdateMissingAttributesValidator.class);
	
	@Mock
	private CommandContext context;
	
	private final KeyGeneratorCommand command = new KeyGeneratorCommand();
	
	private final Map<String,Object> prop = new HashMap<>();
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		command.setCommandType(NscsCommandType.UPDATE_SSH_KEY);
	}

	/**
	 * Test method for {@link com.ericsson.nms.security.nscs.handler.validation.impl.KeyGeneratorUpdateMissingAttributesValidator#validate(com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand, com.ericsson.nms.security.nscs.handler.CommandContext)}.
	 */
	@Test
	public void testValidate() {
		prop.put(KeyGeneratorCommand.ALGORITHM_TYPE_SIZE_PROPERTY,"RSA_1024");
		prop.put(KeyGeneratorCommand.NODE_LIST_PROPERTY, "sgsn0001");
		
		command.setProperties(prop);
		
		keyGenUpdateMissingValidator.validate(command, context);
	}

	@Test
	public void testValidate__nodefile() {
		prop.put(KeyGeneratorCommand.ALGORITHM_TYPE_SIZE_PROPERTY,"RSA_1024");
		prop.put(KeyGeneratorCommand.NODE_LIST_FILE_PROPERTY, "abc.txt");
		
		command.setProperties(prop);
		
		keyGenUpdateMissingValidator.validate(command, context);
	}
	
	@Test
	public void testValidate__noalgorithm() {
		prop.put(KeyGeneratorCommand.NODE_LIST_PROPERTY, "sgsn0001");
		
		command.setProperties(prop);
		
		keyGenUpdateMissingValidator.validate(command, context);
	}
	
	@Test(expected=CommandSyntaxException.class)
	public void testValidate__noparam() {
				
		command.setProperties(prop);
		
		keyGenUpdateMissingValidator.validate(command, context);
	}
}
