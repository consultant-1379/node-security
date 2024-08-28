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
public class KeyGeneratorCreateMissingAttributesValidatorTest {
	
	@InjectMocks
	KeyGeneratorCreateMissingAttributesValidator missingAttrValidator;
	
	@Spy
    private final Logger logger = LoggerFactory.getLogger(KeyGeneratorCreateMissingAttributesValidator.class);
	
	@Mock
	private CommandContext context;
	
	private final KeyGeneratorCommand command = new KeyGeneratorCommand();
	
	private final Map<String,Object> prop = new HashMap<>();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		command.setCommandType(NscsCommandType.CREATE_SSH_KEY);
		
	}

	/**
	 * Test method for {@link com.ericsson.nms.security.nscs.handler.validation.impl.KeyGeneratorCreateMissingAttributesValidator#validate(com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand, com.ericsson.nms.security.nscs.handler.CommandContext)}.
	 */
	@Test
	public void testValidate() {
		
		prop.put(KeyGeneratorCommand.ALGORITHM_TYPE_SIZE_PROPERTY,"RSA_1024");
		
		command.setProperties(prop);
		
		missingAttrValidator.validate(command, context);
	}
	
	@Test(expected=CommandSyntaxException.class)
	public void testValidate__syntaxerror() {
		
		command.setProperties(prop);
		
		missingAttrValidator.validate(command, context);
	}

}
