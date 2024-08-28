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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.RtselCommand;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.command.impl.MockUtils;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.rtsel.utility.RtselConfigurationDetailsResponseBuilder;

/**
 * Test class for RtselConfigurationDetailsValidator.
 * 
 * @author xvekkar
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class RtselConfigurationDetailsValidatorTest {

    @InjectMocks
    RtselConfigurationDetailsValidator rtselConfigurationDetailsValidator;

    @Mock
    NscsLogger nscsLogger;

    @Mock
    RtselConfigurationDetailsResponseBuilder responseBuilder;

    @Mock
    RtselCommand command;

    @Mock
    NscsCMReaderService reader;

    private final String nodeName = "LTE102ERBS00001";
    private final List<NodeReference> inputNodes = new ArrayList<NodeReference>();
    private final NormalizableNodeReference nodeReference = MockUtils.createNormalizableNodeRef(nodeName);;
    private final List<NormalizableNodeReference> validNodesList = new ArrayList<NormalizableNodeReference>();
    private final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();
    private NscsServiceException nscsServiceException;

    @Test
    public void testValidateNodes() {
        validNodesList.add(nodeReference);
        inputNodes.add(nodeReference);
        invalidNodesErrorMap.put(nodeReference, nscsServiceException);
        Mockito.when(command.getNodes()).thenReturn(inputNodes);
        rtselConfigurationDetailsValidator.validateNodes(command, validNodesList, invalidNodesErrorMap, NscsCommandType.RTSEL_GET);
    }
}
