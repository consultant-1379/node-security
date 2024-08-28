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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
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
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;

@RunWith(MockitoJUnitRunner.class)
public class KeyGeneratorSupportedNodeTypeValidatorTest {

    @InjectMocks
    private KeyGeneratorSupportedNodeTypeValidator supportedNodeType;

    @Spy
    private final Logger logger = LoggerFactory.getLogger(KeyGeneratorSupportedNodeTypeValidator.class);

    @Mock
    private CommandContext context;

    private final KeyGeneratorCommand command = new KeyGeneratorCommand();

    private final Map<String, Object> prop = new HashMap<>();

    private static final String SGSN_NODE_NAME = "SGSN123___";
    private static final String SGSN_FDN = "NetworkElement=" + SGSN_NODE_NAME;

    @Mock
    private NscsCMReaderService reader;

    @Mock
    private NscsCapabilityModelService capabilityModel;

    @Mock
    private NormalizableNodeReference sgsnNormNodeRef;

    private final NodeReference sgsnNodeRef = new NodeRef(SGSN_NODE_NAME);

    final Set<String> params = new HashSet<>();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        command.setCommandType(NscsCommandType.CREATE_SSH_KEY);

        when(context.getValidNodes()).thenReturn(new LinkedList<NormalizableNodeReference>(Collections.singletonList(sgsnNormNodeRef)));

        doReturn(SGSN_NODE_NAME).when(sgsnNormNodeRef).getName();
        doReturn(SGSN_FDN).when(sgsnNormNodeRef).getFdn();
        doReturn("SGSN-MME").when(sgsnNormNodeRef).getNeType();
        doReturn(sgsnNodeRef).when(sgsnNormNodeRef).getNormalizedRef();
        doReturn(true).when(sgsnNormNodeRef).hasNormalizedRef();
        doReturn(sgsnNormNodeRef).when(reader).getNormalizableNodeReference(sgsnNodeRef);

    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.handler.validation.impl.KeyGeneratorSupportedNodeTypeValidator#validate(com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand, com.ericsson.nms.security.nscs.handler.CommandContext)}
     * .
     */
    @Test
    public void testValidate() {

        prop.put(KeyGeneratorCommand.ALGORITHM_TYPE_SIZE_PROPERTY, "RSA_1024");

        command.setProperties(prop);

        when(capabilityModel.isCliCommandSupported(sgsnNormNodeRef, NscsCapabilityModelService.SSHKEY_COMMAND)).thenReturn(true);

        supportedNodeType.validate(command, context);

        Assert.assertTrue(context.getInvalidNodes().size() == 0);
    }

    @Test
    public void testValidate_unsupported() {

        prop.put(KeyGeneratorCommand.ALGORITHM_TYPE_SIZE_PROPERTY, "RSA_1024");

        command.setProperties(prop);

        when(capabilityModel.isCliCommandSupported(sgsnNormNodeRef, NscsCapabilityModelService.SSHKEY_COMMAND)).thenReturn(false);

        supportedNodeType.validate(command, context);

        //		Assert.assertTrue(context.getInvalidNodes().size() > 0);
    }
}
