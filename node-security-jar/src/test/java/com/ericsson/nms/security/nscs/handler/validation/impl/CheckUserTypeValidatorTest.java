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
public class CheckUserTypeValidatorTest {
    @InjectMocks
    CheckUserTypeValidator checkUserTypeValidator = new CheckUserTypeValidator();

    @Spy
    private final Logger logger = LoggerFactory.getLogger(CheckUserTypeValidator.class);

    static NscsNodeCommand nscsNodeCommand;

    @BeforeClass
    public static void initialize() {
        nscsNodeCommand = new NscsNodeCommand();
        nscsNodeCommand.setCommandType(NscsCommandType.GET_CREDENTIALS);
    }

    @Test
    public void testValidateRoot() {
        validate("root");
    }

    @Test
    public void testValidateNormal() {
        validate("normal");
    }

    @Test
    public void testValidateSecure() {
        validate("secure");
    }

    @Test
    public void testValidateNwieaSecure() {
        validate("nwieasecure");
    }

    @Test
    public void testValidateNwiebSecure() {
        validate("nwiebsecure");
    }

    @Test(expected = CommandSyntaxException.class)
    public void testValidateWrongUser() {
        validate("secureError");
    }

    @Test
    public void testValidateWithoutProperty() {
        final Map<String, Object> properties = new HashMap<String, Object>();
        nscsNodeCommand.setProperties(properties);

        checkUserTypeValidator.validate(nscsNodeCommand, null);
    }

    private void validate(final String user) {
        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("usertype", user);
        nscsNodeCommand.setProperties(properties);

        checkUserTypeValidator.validate(nscsNodeCommand, null);
    }

}
