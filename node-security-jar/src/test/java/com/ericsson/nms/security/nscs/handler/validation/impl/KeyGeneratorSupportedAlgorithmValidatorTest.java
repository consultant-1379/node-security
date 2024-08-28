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

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.KeyGeneratorCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class KeyGeneratorSupportedAlgorithmValidatorTest {

    @InjectMocks
    private KeyGeneratorSupportedAlgorithmValidator supportedAlgorithmValidator;

    @Spy
    private final Logger logger = LoggerFactory.getLogger(KeyGeneratorSupportedAlgorithmValidator.class);

    @Mock
    private NscsModelServiceImpl nscsModelServiceImpl;

    @Mock
    private CommandContext context;

    private final KeyGeneratorCommand command = new KeyGeneratorCommand();

    private final Map<String, Object> prop = new HashMap<>();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        command.setCommandType(NscsCommandType.CREATE_SSH_KEY);

        when(nscsModelServiceImpl.getSupportedAlgorithmAndKeySize()).thenReturn(Arrays.asList("RSA_1024", "RSA_2048", "RSA_4096"));

    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.handler.validation.impl.KeyGeneratorSupportedAlgorithmValidator#validate(com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand, com.ericsson.nms.security.nscs.handler.CommandContext)}.
     */
    @Test
    public void testValidate() {

        prop.put(KeyGeneratorCommand.ALGORITHM_TYPE_SIZE_PROPERTY, "RSA_1024");

        command.setProperties(prop);

        supportedAlgorithmValidator.validate(command, context);
    }

    @Test(expected = InvalidArgumentValueException.class)
    public void testValidate__invalidarg() {

        prop.put(KeyGeneratorCommand.ALGORITHM_TYPE_SIZE_PROPERTY, "RSA_8000");

        command.setProperties(prop);

        supportedAlgorithmValidator.validate(command, context);
    }

    @Test
    public void testValidate__missingprop() {

        command.setProperties(prop);

        supportedAlgorithmValidator.validate(command, context);
    }

}
