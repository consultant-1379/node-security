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
package com.ericsson.nms.security.nscs.handler.command.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.types.RtselCommand;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.impl.RtselConfigurationDetailsValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.rtsel.utility.RtselConfigurationDetailsResponseBuilder;

/**
 * Test class for RtselConfigurationDetailsHandler.
 * 
 * @author xvekkar
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class RtselConfigurationDetailsHandlerTest {

    @InjectMocks
    RtselConfigurationDetailsHandler rtselConfigurationDetailsHandler;

    @Mock
    CommandContext context;

    @Mock
    NscsLogger nscsLogger;

    @Mock
    RtselConfigurationDetailsValidator getRTSELServerDetailsValidator;

    @Mock
    RtselConfigurationDetailsResponseBuilder responseBuilder;

    private final RtselCommand command = new RtselCommand();

    private final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(8);

    @SuppressWarnings("unchecked")
    @Test
    public void testProcess() {
        Mockito.when(responseBuilder.buildRtselDetailsResponse(Mockito.anyMap(), Mockito.anyMap())).thenReturn(response);
        NscsCommandResponse nscsCommandResponse = rtselConfigurationDetailsHandler.process(command, context);
        assertEquals(nscsCommandResponse.getResponseType().toString(), "NAME_MULTIPLE_VALUE");
    }
}
