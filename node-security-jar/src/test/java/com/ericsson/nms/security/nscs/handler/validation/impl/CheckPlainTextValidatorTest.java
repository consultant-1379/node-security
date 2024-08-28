/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.validation.impl;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;

@RunWith(MockitoJUnitRunner.class)
public class CheckPlainTextValidatorTest {
    @InjectMocks
    CheckPlainTextValidator checkPlainTextValidator = new CheckPlainTextValidator();
   
    @Spy
    private final Logger logger = LoggerFactory.getLogger(CheckPlainTextValidator.class);

    static NscsNodeCommand nscsNodeCommand;
    
    @BeforeClass
    public static void initialize() {
        nscsNodeCommand = new NscsNodeCommand();
        nscsNodeCommand.setCommandType(NscsCommandType.GET_CREDENTIALS);
    }

    @Test
    public void testValidateHide() {
        validate("hide");
    }

    @Test
    public void testValidateShow() {
        validate("show");
    }
    
    @Test
    public void testValidateWithoutProperty() {
        final Map<String, Object> properties = new HashMap<String, Object>();
        nscsNodeCommand.setProperties(properties);
        
        checkPlainTextValidator.validate(nscsNodeCommand, null);
    }
    
    @Test(expected = CommandSyntaxException.class)
    public void testValidateWrongPlaintext() {
        validate("Error");
    }

    private void validate(final String plaintext) {
        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("plaintext", plaintext);
        nscsNodeCommand.setProperties(properties);

        checkPlainTextValidator.validate(nscsNodeCommand, null);
    }

}
