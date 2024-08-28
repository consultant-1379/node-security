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

import java.util.HashMap;
import java.util.Map;

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
public class CheckLdapUserOptionValidatorTest {
    @InjectMocks
    CheckLdapUserOptionValidator checkLdapUserOptionValidator = new CheckLdapUserOptionValidator();
   
    @Spy
    private final Logger logger = LoggerFactory.getLogger(CheckLdapUserOptionValidator.class);

    static NscsNodeCommand nscsNodeCommand;
    
    @BeforeClass
    public static void initialize() {
        nscsNodeCommand = new NscsNodeCommand();
        nscsNodeCommand.setCommandType(NscsCommandType.CREATE_CREDENTIALS);
    }

    @Test
    public void testValidateDisable() {
        validate("disable");
    }

    @Test
    public void testValidateEnable() {
        validate("enable");
    }
    
    @Test
    public void testValidateWithoutProperty() {
        final Map<String, Object> properties = new HashMap<String, Object>();
        nscsNodeCommand.setProperties(properties);
        
        checkLdapUserOptionValidator.validate(nscsNodeCommand, null);
    }
    
    @Test(expected = CommandSyntaxException.class)
    public void testValidateWrongLdapUserStatus() {
        validate("Error");
    }

    private void validate(final String status) {
        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("ldapuser", status);
        nscsNodeCommand.setProperties(properties);

        checkLdapUserOptionValidator.validate(nscsNodeCommand, null);
    }

}
