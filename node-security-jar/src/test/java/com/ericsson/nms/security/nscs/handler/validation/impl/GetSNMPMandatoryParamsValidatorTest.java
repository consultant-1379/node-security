/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
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

import org.junit.Assert;
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
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;

@RunWith(MockitoJUnitRunner.class)
public class GetSNMPMandatoryParamsValidatorTest {
    @InjectMocks
    GetSNMPMandatoryParamsValidator checkGetSNMPParamsValidator = new GetSNMPMandatoryParamsValidator();

    @Spy
    private final Logger logger = LoggerFactory.getLogger(GetSNMPMandatoryParamsValidatorTest.class);

    static NscsNodeCommand nscsNodeCommand;

    @BeforeClass
    public static void initialize() {
        nscsNodeCommand = new NscsNodeCommand();
        nscsNodeCommand.setCommandType(NscsCommandType.GET_SNMP);
    }

    @Test
    public void positiveTest() {
        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("plaintext", "show");
        nscsNodeCommand.setProperties(properties);

        checkGetSNMPParamsValidator.validate(nscsNodeCommand, null);
    }

    @Test
    public void negativeTestNoParameters() {
        try {
            checkGetSNMPParamsValidator.validate(nscsNodeCommand, null);
        } catch (final NscsServiceException e) {
            Assert.fail();
        }
    }

    @Test
    public void negativeTestWrongParameters() {
        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("wrongparam", "show");
        nscsNodeCommand.setProperties(properties);

        checkGetSNMPParamsValidator.validate(nscsNodeCommand, null);
    }
}
