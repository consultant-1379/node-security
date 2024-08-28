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
package com.ericsson.nms.security.nscs.handler.validation.impl;

import static com.ericsson.nms.security.nscs.handler.command.impl.MockUtils.setupCommandContext;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.SnmpAuthnopriv;
import com.ericsson.nms.security.nscs.api.command.types.SnmpAuthpriv;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.NetworkElementSecurityAlreadyExistsException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.wrapper.NodeException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.oss.itpf.security.cryptography.CryptographyService;

/**
 * Tests the SnmpAuthnoprivParamsValidator
 *
 * @see SnmpAuthnoprivParamsValidator
 * @author ebarmos, emelant
 */
@RunWith(MockitoJUnitRunner.class)
public class SnmpAuthnoprivParamsValidatorTest {

    private static final String THEPASSORD = "password";

    private static final String THENODE = "DUSG2";
    private static final String AUTH_PROTOCOL = "MD5";
    private static final String AUTH_PROTOCOL_WRONG = "MD2";
    public static final String NODE_NAME_HEADER = "Node Name        ";
    public static final String NODE_SECURITY_LEVEL_HEADER = "Node Security Level";
    public static final String CHAR_ENCODING = "UTF-8";

    @Spy
    private final Logger logger = LoggerFactory.getLogger(NscsCMWriterService.class);

    @InjectMocks
    private SnmpAuthnoprivParamsValidator beanUnderTest;

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
    NscsCMWriterService.WriterSpecificationBuilder specificationBuilder;

    @Mock
    NodeReference nodeReference;

    @Before
    public void setupTest() throws IllegalAccessException {
        setupCommandContext(commandContext, THENODE);
    }

    @Test
    public void testProcess_validate() throws Exception {

        System.out.println("------------------ testProcess_validate------------------");

        final SnmpAuthnopriv command = setUpData(false, false);

        final List<NormalizableNodeReference> validNodes = new ArrayList<>();

        when(nodeReference.getName()).thenReturn(THENODE);
        when(normalNode.getNormalizedRef()).thenReturn(nodeReference);
        validNodes.add(normalNode);

        when(commandContext.getValidNodes()).thenReturn(validNodes);

        when(nscsCapabilityModelService.isCliCommandSupported(normalNode, NscsCapabilityModelService.SNMP_COMMAND)).thenReturn(true);

        boolean commandError = false;
        try {
            beanUnderTest.validate(command, commandContext);
        } catch (final Exception e) {
            commandError = true;
        }

        assertFalse(commandError);

    }

    @Test
    public void testProcess_validate_wrong_attribute() throws Exception {

        final SnmpAuthnopriv command = setUpData(false, true);

        final List<NormalizableNodeReference> validNodes = new ArrayList<>();

        when(nodeReference.getName()).thenReturn(THENODE);
        when(normalNode.getNormalizedRef()).thenReturn(nodeReference);
        validNodes.add(normalNode);

        when(commandContext.getValidNodes()).thenReturn(validNodes);

        when(nscsCapabilityModelService.isCliCommandSupported(normalNode, NscsCapabilityModelService.SNMP_COMMAND)).thenReturn(true);

        boolean commandError = false;
        try {
            beanUnderTest.validate(command, commandContext);
        } catch (final Exception e) {
            if (e instanceof InvalidArgumentValueException) {
                commandError = true;
            }
        }

        assertTrue(commandError);
    }

    @Test
    public void testProcess_validate_wrong_argument() throws Exception {

        final SnmpAuthnopriv command = setUpData(true, false);

        final List<NormalizableNodeReference> validNodes = new ArrayList<>();

        when(nodeReference.getName()).thenReturn(THENODE);
        when(normalNode.getNormalizedRef()).thenReturn(nodeReference);
        validNodes.add(normalNode);

        when(commandContext.getValidNodes()).thenReturn(validNodes);

        when(nscsCapabilityModelService.isCliCommandSupported(normalNode, NscsCapabilityModelService.SNMP_COMMAND)).thenReturn(true);

        boolean commandError = false;
        try {
            beanUnderTest.validate(command, commandContext);
        } catch (final Exception e) {
            if (e instanceof CommandSyntaxException) {
                commandError = true;
            }
        }

        assertTrue(commandError);

        when(nscsCapabilityModelService.isCliCommandSupported(normalNode, NscsCapabilityModelService.SNMP_COMMAND)).thenReturn(false);

        commandError = false;
        try {
            beanUnderTest.validate(command, commandContext);
        } catch (final Exception e) {
            if (e instanceof CommandSyntaxException) {
                commandError = true;
            }
        }

        assertTrue(commandError);

    }

    private SnmpAuthpriv setUpData(final boolean wrongCommang, final boolean wrongAttribute) {
        final SnmpAuthpriv command = buildCommand(wrongCommang, wrongAttribute);

        setUpMocks();
        return command;
    }

    @SuppressWarnings("unchecked")
    private void setUpMocks() {

        final byte[] password = THEPASSORD.getBytes();

        when(cMWriterService.withSpecification()).thenReturn(specificationBuilder);

        when(cryptographyService.encrypt(any(byte[].class))).thenReturn(password);

        doThrow(new NodeException(new NodeRef(THENODE), new NetworkElementSecurityAlreadyExistsException())).when(commandContext)
                .setAsInvalidOrFailedAndThrow(any(Collection.class), any(NscsServiceException.class));
    }

    private SnmpAuthpriv buildCommand(final boolean wrongCommang, final boolean wrongAttribute) {
        final SnmpAuthpriv command = new SnmpAuthpriv();
        command.setCommandType(NscsCommandType.SNMP_AUTHPRIV);
        final Map<String, Object> commandMap = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;
            {
                {
                    if (wrongCommang) {
                        put(SnmpAuthpriv.PRIV_ALGO_PARAM, NetworkElementSecurity.PRIV_PROTOCOL);
                    } else {
                        if (wrongAttribute) {
                            put(SnmpAuthpriv.AUTH_ALGO_PARAM, AUTH_PROTOCOL_WRONG);
                        } else {
                            put(SnmpAuthpriv.AUTH_ALGO_PARAM, AUTH_PROTOCOL);
                        }
                    }

                    put(SnmpAuthpriv.AUTH_PWD_PARAM, NetworkElementSecurity.AUTH_KEY);
                }
            }
        };
        command.setProperties(commandMap);
        return command;
    }

}
