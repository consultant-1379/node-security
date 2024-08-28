/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.command.impl;

import static com.ericsson.nms.security.nscs.handler.command.impl.MockUtils.setupCommandContext;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.command.types.SnmpAuthpriv;
import com.ericsson.nms.security.nscs.api.exception.NetworkElementSecurityAlreadyExistsException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.wrapper.NodeException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.oss.itpf.security.cryptography.CryptographyService;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * Tests the SnmpAuthprivHandler that Updates a NetworkElementSecurity Mo associated to each of the specified nodes
 *
 * @see SnmpAuthprivHandler
 * @author ebarmos, emelant
 */
@RunWith(MockitoJUnitRunner.class)
public class SnmpAuthprivHandlerTest {

    private static final String THEPASSORD = "password";

    private static final String THENODE = "node1";
    private static final String THENODEFDN = "NetworkElement=node1";
    public static final String NODE_NAME_HEADER = "Node Name        ";
    public static final String NODE_SECURITY_LEVEL_HEADER = "Node Security Level";
    public static final String CHAR_ENCODING = "UTF-8";

    @Spy
    private final Logger logger = LoggerFactory.getLogger(NscsCMWriterService.class);

    @InjectMocks
    private SnmpAuthprivHandler beanUnderTest;

    @Mock
    private NscsCMWriterService cMWriterService;

    @Mock
    private CryptographyService cryptographyService;

    @Mock
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Mock
    private CommandContext commandContext;

    @Mock
    private NormalizableNodeReference normalNode;

    @Mock
    private NodeReference nodeRef;

    @Mock
    NscsCMWriterService.WriterSpecificationBuilder specificationBuilder;

    @Mock
    private NscsContextService nscsContextService;

    @Before
    public void setupTest() {
        setupCommandContext(commandContext, THENODE);
    }

    /**
     * Tests SnmpAuthprivHandler Negative flow
     *
     * @throws Exception
     */
    @Test
    public void testProcess_SnmpAuthprivHandler_Negative() throws Exception {
        final byte[] password = THEPASSORD.getBytes();

        final SnmpAuthpriv command = setUpData(password);

        final List<NormalizableNodeReference> validNodes = new ArrayList<>();

        when(normalNode.getName()).thenReturn(THENODE);
        validNodes.add(normalNode);
        when(commandContext.getValidNodes()).thenReturn(validNodes);

        when(nscsCapabilityModelService.isCliCommandSupported(normalNode, NscsCapabilityModelService.SNMP_COMMAND)).thenReturn(false);

        NscsCommandResponse nscsResponse = beanUnderTest.process(command, commandContext);

        verify(specificationBuilder, never()).updateMO();

        when(nscsCapabilityModelService.isCliCommandSupported(normalNode, NscsCapabilityModelService.SNMP_COMMAND)).thenReturn(true);

        boolean a;
        try {
            nscsResponse = beanUnderTest.process(null, commandContext);
            a = false;
        } catch (final Exception e) {
            a = true;
            assertTrue((e instanceof NscsServiceException));
        }

        assertTrue(a);
        verify(specificationBuilder, never()).updateMO();

    }

    /**
     * Tests SnmpAuthprivHandler Positive flow
     *
     * @throws Exception
     */
    @Test
    public void testProcess_SnmpAuthprivHandler_Positive() throws Exception {
        final byte[] password = THEPASSORD.getBytes();

        final SnmpAuthpriv command = setUpData(password);

        final List<NormalizableNodeReference> validNodes = new ArrayList<>();

        when(normalNode.getName()).thenReturn(THENODE);
        when(normalNode.getNormalizedRef()).thenReturn(nodeRef);
        when(nodeRef.getFdn()).thenReturn(THENODEFDN);
        validNodes.add(normalNode);
        when(commandContext.getValidNodes()).thenReturn(validNodes);

        when(nscsCapabilityModelService.isCliCommandSupported(normalNode, NscsCapabilityModelService.SNMP_COMMAND)).thenReturn(true);

        final NscsCommandResponse nscsResponse = beanUnderTest.process(command, commandContext);

        assertTrue(SnmpAuthprivHandler.SNMP_AUTHPRIV_COMMAND_OK.equals(((NscsMessageCommandResponse) nscsResponse).getMessage()));
        verify(specificationBuilder).setNotNullAttribute(ModelDefinition.NetworkElementSecurity.AUTH_PROTOCOL, command.getAuthAlgo());
        verify(specificationBuilder).setNotNullAttribute(ModelDefinition.NetworkElementSecurity.PRIV_PROTOCOL, command.getPrivAlgo());
        verify(specificationBuilder).setNotNullAttribute(ModelDefinition.NetworkElementSecurity.AUTH_KEY,
                beanUnderTest.encryptEncode(command.getAuthPwd()));
        verify(specificationBuilder).setNotNullAttribute(ModelDefinition.NetworkElementSecurity.PRIV_KEY,
                beanUnderTest.encryptEncode(command.getPrivPwd()));
        verify(specificationBuilder).setFdn(Model.getNomalizedRootMO(normalNode.getNormalizedRef().getFdn()).securityFunction.networkElementSecurity.withNames(normalNode.getName()).fdn());
        verify(specificationBuilder).updateMO();

    }

    @Test
    public void testProcess_encryptEncodeNull() {
        assertNull(beanUnderTest.encryptEncode(null));
    }

    private SnmpAuthpriv setUpData(final byte[] password) {
        final SnmpAuthpriv command = buildCommand();

        //specificationBuilder = org.mockito.Mockito.mock(NscsCMWriterService.WriterSpecificationBuilder.class);

        setUpMocks(specificationBuilder, password);
        return command;
    }

    private void setUpMocks(final NscsCMWriterService.WriterSpecificationBuilder specificationBuilder, final byte[] password) {

        when(cMWriterService.withSpecification()).thenReturn(specificationBuilder);

        when(cryptographyService.encrypt(any(byte[].class))).thenReturn(password);

        doThrow(new NodeException(new NodeRef(THENODE), new NetworkElementSecurityAlreadyExistsException())).when(commandContext)
                .setAsInvalidOrFailedAndThrow(any(Collection.class), any(NscsServiceException.class));
    }

    private SnmpAuthpriv buildCommand() {
        final SnmpAuthpriv command = new SnmpAuthpriv();
        command.setCommandType(NscsCommandType.SNMP_AUTHPRIV);
        final Map<String, Object> commandMap = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;
            {
                {
                    put(NscsNodeCommand.NODE_LIST_PROPERTY, Arrays.asList(THENODE));
                    put(SnmpAuthpriv.AUTH_ALGO_PARAM, Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.AUTH_PROTOCOL);
                    put(SnmpAuthpriv.AUTH_PWD_PARAM, Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.AUTH_KEY);
                    put(SnmpAuthpriv.PRIV_ALGO_PARAM, Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.PRIV_PROTOCOL);
                    put(SnmpAuthpriv.PRIV_PWD_PARAM, Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.PRIV_KEY);
                }
            }
        };
        command.setProperties(commandMap);
        return command;
    }
}
