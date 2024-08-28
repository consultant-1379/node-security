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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import com.ericsson.nms.security.nscs.api.exception.KeypairAlreadyGeneratedException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;

@RunWith(MockitoJUnitRunner.class)
public class KeyGeneratorCreateMissingKeyPairValidatorTest {

    @InjectMocks
    private KeyGeneratorAlreadyGeneratedValidator createMissingKeyPair;

    @Spy
    private final Logger logger = LoggerFactory.getLogger(KeyGeneratorAlreadyGeneratedValidator.class);

    @Mock
    private CommandContext context;

    private final KeyGeneratorCommand command = new KeyGeneratorCommand();

    private final Map<String, Object> prop = new HashMap<>();

    private static final String SGSN_NODE_NAME = "SGSN123___";
    private static final String SGSN_FDN = "NetworkElement=" + SGSN_NODE_NAME;

    @Mock
    private NscsCMReaderService reader;

    @Mock
    private NormalizableNodeReference sgsnNormNodeRef;

    @Mock
    private CmResponse cmResponseHlink;

    @Mock
    private KeypairAlreadyGeneratedException keypairAlreadyGeneratedexception;

    @Mock
    private CmObject cmObjIntfs;

    private final NodeReference sgsnNodeRef = new NodeRef(SGSN_NODE_NAME);

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        command.setCommandType(NscsCommandType.CREATE_SSH_KEY);

        doReturn(SGSN_NODE_NAME).when(sgsnNormNodeRef).getName();
        doReturn(SGSN_FDN).when(sgsnNormNodeRef).getFdn();
        doReturn("SGSN-MME").when(sgsnNormNodeRef).getNeType();
        doReturn(sgsnNodeRef).when(sgsnNormNodeRef).getNormalizedRef();
        doReturn(true).when(sgsnNormNodeRef).hasNormalizedRef();
        doReturn(sgsnNormNodeRef).when(reader).getNormalizableNodeReference(sgsnNodeRef);
    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.handler.validation.impl.KeyGeneratorAlreadyGeneratedValidator#validate(com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand, com.ericsson.nms.security.nscs.handler.CommandContext)}
     * .
     */
    @Test
    public void testValidate() {

        Assert.assertTrue(context.getInvalidNodes().size() == 0);

        prop.put(KeyGeneratorCommand.ALGORITHM_TYPE_SIZE_PROPERTY, "RSA_1024");

        command.setProperties(prop);

        final NodeReference nodeRef = new NodeRef(SGSN_FDN);

        when(context.getValidNodes()).thenReturn(new LinkedList<NormalizableNodeReference>(Collections.singletonList(sgsnNormNodeRef)));

        final List<NodeReference> nodeRefL = new LinkedList<>();
        when(context.toNormalizedRef(context.getValidNodes())).thenReturn(nodeRefL);

        when(reader.getMOAttribute(nodeRefL, Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.type(),
                Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.namespace(), NetworkElementSecurity.ENM_SSH_PUBLIC_KEY))
                        .thenReturn(cmResponseHlink);

        when(cmResponseHlink.getCmObjects()).thenReturn(Collections.singleton(cmObjIntfs));

        when(cmObjIntfs.getFdn()).thenReturn(SGSN_FDN);
        when(cmObjIntfs.getName()).thenReturn(SGSN_NODE_NAME);

        doThrow(RuntimeException.class).when(context).setAsInvalidOrFailed(nodeRef, keypairAlreadyGeneratedexception);

        createMissingKeyPair.validate(command, context);

    }

}
