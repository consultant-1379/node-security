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
package com.ericsson.nms.security.nscs.handler.command.impl;

import static com.ericsson.nms.security.nscs.data.Model.NETWORK_ELEMENT;
import static com.ericsson.nms.security.nscs.handler.command.impl.MockUtils.setupCommandContext;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse.Entry;
import com.ericsson.nms.security.nscs.api.command.types.GetCredentialsCommand;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.utility.PasswordHelper;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;

@RunWith(MockitoJUnitRunner.class)
public class GetCredentialsHandlerTest {

    private static final String NODE1 = "node1";
    private static final String NODE2 = "node2";
    private static final String NODE3 = "node3";

    @Spy
    private final Logger logger = LoggerFactory.getLogger(NscsCMWriterService.class);

    @InjectMocks
    private GetCredentialsHandler getCredentialsHandler;

    @Mock
    private NscsCMReaderService cMReaderService;

    @Mock
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Mock
    private PasswordHelper passwordHelper;

    @Mock
    private CommandContext commandContext;

    private GetCredentialsCommand setUpData(final String usertype, final String plaintext, final String[] nodes) {
        setupCommandContext(commandContext, nodes);
        buildEmptyCmResponse();
        final GetCredentialsCommand getCredentialCommand = new GetCredentialsCommand();
        getCredentialCommand.setCommandType(NscsCommandType.GET_CREDENTIALS);
        final Map<String, Object> commandMap = new HashMap<String, Object>() {

            private static final long serialVersionUID = 1L;
            {
                {
                    if (usertype != null) {
                        put(GetCredentialsCommand.USER_TYPE_PROPERTY, usertype);
                    }
                    if (plaintext != null) {
                        put(GetCredentialsCommand.PLAIN_TEXT_PROPERTY, plaintext);
                    }
                }
            }
        };
        getCredentialCommand.setProperties(commandMap);
        return getCredentialCommand;
    }

    // 1 TEST
    @Test
    public void testProcess_GetCredentialsHandler_UserTypeNormal_PlainTextHide() throws Exception {
        final String[] nodes = new String[] { NODE1 };
        positiveTest(nodes, GetCredentialsCommand.NORMAL_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_HIDE);
    }

    // 2 TEST
    @Test
    public void testProcess_GetCredentialsHandler_UserTypeNormal_PlainTextShow() throws Exception {
        final String[] nodes = new String[] { NODE1 };
        positiveTest(nodes, GetCredentialsCommand.NORMAL_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_SHOW);
    }

    // 3 TEST
    @Test
    public void testProcess_GetCredentialsHandler_UserTypeSecure_PlainTextHide() throws Exception {
        final String[] nodes = new String[] { NODE1 };
        positiveTest(nodes, GetCredentialsCommand.SECURE_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_HIDE);
    }

    // 4 TEST
    @Test
    public void testProcess_GetCredentialsHandler_UserTypeSecure_PlainTextShow() throws Exception {
        final String[] nodes = new String[] { NODE1 };
        positiveTest(nodes, GetCredentialsCommand.SECURE_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_SHOW);
    }

    // 5 TEST
    @Test
    public void testProcess_GetCredentialsHandler_UserTypeRoot_PlainTextHide() throws Exception {
        final String[] nodes = new String[] { NODE1 };
        positiveTest(nodes, GetCredentialsCommand.ROOT_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_HIDE);
    }

    // 6 TEST
    @Test
    public void testProcess_GetCredentialsHandler_UserTypeRoot_PlainTextShow() throws Exception {
        final String[] nodes = new String[] { NODE1 };
        positiveTest(nodes, GetCredentialsCommand.ROOT_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_SHOW);
    }

    // 7 TEST
    @Test
    public void testProcess_GetCredentialsHandler_UserTypeNwiea_Secure_PlainTextHide() throws Exception {
        final String[] nodes = new String[] { NODE1 };
        positiveTest(nodes, GetCredentialsCommand.NWIEA_SECURE_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_HIDE);
    }

    // 8 TEST
    @Test
    public void testProcess_GetCredentialsHandler_UserTypeNwiea_Secure_PlainTextShow() throws Exception {
        final String[] nodes = new String[] { NODE1 };
        positiveTest(nodes, GetCredentialsCommand.NWIEA_SECURE_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_SHOW);
    }

    // 9 TEST
    @Test
    public void testProcess_GetCredentialsHandler_UserTypeNwieb_Secure_PlainTextHide() throws Exception {
        final String[] nodes = new String[] { NODE1 };
        positiveTest(nodes, GetCredentialsCommand.NWIEB_SECURE_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_HIDE);
    }

    // 10 TEST
    @Test
    public void testProcess_GetCredentialsHandler_UserTypeNwieb_Secure_PlainTextShow() throws Exception {
        final String[] nodes = new String[] { NODE1 };
        positiveTest(nodes, GetCredentialsCommand.NWIEB_SECURE_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_SHOW);
    }

    // 11 TEST
    @Test
    public void testProcess_GetCredentialsHandler_UserTypeNormal_PlainTextHide_ManyNodes() throws Exception {
        final String[] nodes = new String[] { NODE1, NODE2, NODE3 };
        positiveTest(nodes, GetCredentialsCommand.NORMAL_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_HIDE);
    }

    // 12 TEST
    @Test
    public void testProcess_GetCredentialsHandler_UserTypeNormal_PlainTextShow_ManyNodes() throws Exception {
        final String[] nodes = new String[] { NODE1, NODE2, NODE3 };
        positiveTest(nodes, GetCredentialsCommand.NORMAL_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_SHOW);
    }

    // 13 TEST
    @Test
    public void testProcess_GetCredentialsHandler_UserTypeSecure_PlainTextHide_ManyNodes() throws Exception {
        final String[] nodes = new String[] { NODE1, NODE2, NODE3 };
        positiveTest(nodes, GetCredentialsCommand.SECURE_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_HIDE);
    }

    // 14 TEST
    @Test
    public void testProcess_GetCredentialsHandler_UserTypeSecure_PlainTextShow_ManyNodes() throws Exception {
        final String[] nodes = new String[] { NODE1, NODE2, NODE3 };
        positiveTest(nodes, GetCredentialsCommand.SECURE_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_SHOW);
    }

    // 15 TEST
    @Test
    public void testProcess_GetCredentialsHandler_UserTypeRoot_PlainTextHide_ManyNodes() throws Exception {
        final String[] nodes = new String[] { NODE1, NODE2, NODE3 };
        positiveTest(nodes, GetCredentialsCommand.ROOT_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_HIDE);
    }

    // 16 TEST
    @Test
    public void testProcess_GetCredentialsHandler_UserTypeRoot_PlainTextShow_ManyNodes() throws Exception {
        final String[] nodes = new String[] { NODE1, NODE2, NODE3 };
        positiveTest(nodes, GetCredentialsCommand.ROOT_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_SHOW);
    }

    // 17 TEST
    @Test
    public void testProcess_GetCredentialsHandler_UserTypeNwiea_Secure_PlainTextHide_ManyNodes() throws Exception {
        final String[] nodes = new String[] { NODE1, NODE2, NODE3 };
        positiveTest(nodes, GetCredentialsCommand.NWIEA_SECURE_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_HIDE);
    }

    // 18 TEST
    @Test
    public void testProcess_GetCredentialsHandler_UserTypeNwiea_Secure_PlainTextShow_ManyNodes() throws Exception {
        final String[] nodes = new String[] { NODE1, NODE2, NODE3 };
        positiveTest(nodes, GetCredentialsCommand.NWIEA_SECURE_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_SHOW);
    }

    // 19 TEST
    @Test
    public void testProcess_GetCredentialsHandler_UserTypeNwieb_Secure_PlainTextHide_ManyNodes() throws Exception {
        final String[] nodes = new String[] { NODE1, NODE2, NODE3 };
        positiveTest(nodes, GetCredentialsCommand.NWIEB_SECURE_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_HIDE);
    }

    // 20 TEST
    @Test
    public void testProcess_GetCredentialsHandler_UserTypeNwieb_Secure_PlainTextShow_ManyNodes() throws Exception {
        final String[] nodes = new String[] { NODE1, NODE2, NODE3 };
        positiveTest(nodes, GetCredentialsCommand.NWIEB_SECURE_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_SHOW);
    }

    // 21 TEST
    @Test
    public void testProcess_GetCredentialsHandler_Cpp_NotConfigured() throws Exception {
        final String[] nodes = new String[] { NODE1 };
        final GetCredentialsCommand getCredentialsCommand = setUpData(GetCredentialsCommand.ROOT_USER_NAME_PROPERTY,
                GetCredentialsCommand.PLAIN_TEXT_SHOW, nodes);

        mockCredentialsCppNotConfigured(getCredentialsCommand);

        final NscsNameMultipleValueCommandResponse nscsResponse = (NscsNameMultipleValueCommandResponse) getCredentialsHandler
                .process(getCredentialsCommand, commandContext);

        assertTrue(nscsResponse.size() == (nodes.length + 1));

        verifyResponseNotConfigured(nodes, nscsResponse, GetCredentialsCommand.ROOT_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_SHOW);
    }

    // 22 TEST
    @Test
    public void testProcess_GetCredentialsHandler_ComECIM_NotApplicable() throws Exception {
        final String[] nodes = new String[] { NODE1 };
        final GetCredentialsCommand getCredentialsCommand = setUpData(GetCredentialsCommand.ROOT_USER_NAME_PROPERTY,
                GetCredentialsCommand.PLAIN_TEXT_SHOW, nodes);

        mockCredentialsComEcim(getCredentialsCommand);

        final NscsNameMultipleValueCommandResponse nscsResponse = (NscsNameMultipleValueCommandResponse) getCredentialsHandler
                .process(getCredentialsCommand, commandContext);

        assertTrue(nscsResponse.size() == (nodes.length + 1));

        verifyResponseNotApplicable(nodes, nscsResponse, GetCredentialsCommand.ROOT_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_SHOW);
    }

    // 23 TEST
    @Test
    public void testProcess_GetCredentialsHandler_UserTypeNodeCli_PlainTextHide() throws Exception {
        final String[] nodes = new String[] { NODE1 };
        positiveTest(nodes, GetCredentialsCommand.NODECLI_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_HIDE);
    }

    // 24 TEST
    @Test
    public void testProcess_GetCredentialsHandler_UserTypeNodeCli_PlainTextShow() throws Exception {
        final String[] nodes = new String[] { NODE1 };
        positiveTest(nodes, GetCredentialsCommand.NODECLI_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_SHOW);
    }

    // 25 TEST
    @Test
    public void testProcess_GetCredentialsHandler_UserTypeNodeCli_PlainTextHide_ManyNodes() throws Exception {
        final String[] nodes = new String[] { NODE1, NODE2, NODE3 };
        positiveTest(nodes, GetCredentialsCommand.NODECLI_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_HIDE);
    }

    // 26 TEST
    @Test
    public void testProcess_GetCredentialsHandler_UserTypeNodeCli_PlainTextShow_ManyNodes() throws Exception {
        final String[] nodes = new String[] { NODE1, NODE2, NODE3 };
        positiveTest(nodes, GetCredentialsCommand.NODECLI_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_SHOW);
    }

    @Test
    public void testProcess_GetCredentialsHandler_NoUserType_PlainTextHide() throws Exception {
        final String[] nodes = new String[] { NODE1 };
        positiveTest(nodes, null, GetCredentialsCommand.PLAIN_TEXT_HIDE);
    }

    @Test
    public void testProcess_GetCredentialsHandler_NoUserType_NoPlainText() throws Exception {
        final String[] nodes = new String[] { NODE1 };
        positiveTest(nodes, null, null);
    }

    @Test
    public void testProcess_GetCredentialsHandler_UserTypeRoot_NoPlainText() throws Exception {
        final String[] nodes = new String[] { NODE1 };
        positiveTest(nodes, GetCredentialsCommand.ROOT_USER_NAME_PROPERTY, null);
    }

    @Test
    public void testProcess_GetCredentialsHandler_ComECIM() throws Exception {
        final String[] nodes = new String[] { NODE1 };
        final GetCredentialsCommand getCredentialsCommand = setUpData(GetCredentialsCommand.SECURE_USER_NAME_PROPERTY,
                GetCredentialsCommand.PLAIN_TEXT_SHOW, nodes);

        mockCredentialsComEcim(getCredentialsCommand);

        final NscsNameMultipleValueCommandResponse nscsResponse = (NscsNameMultipleValueCommandResponse) getCredentialsHandler
                .process(getCredentialsCommand, commandContext);

        assertTrue(nscsResponse.size() == (nodes.length + 1));

        verifyResponseNotApplicable(nodes, nscsResponse, GetCredentialsCommand.SECURE_USER_NAME_PROPERTY, GetCredentialsCommand.PLAIN_TEXT_SHOW);
    }

    private void positiveTest(final String[] nodes, final String usertype, String plaintext) {
        final GetCredentialsCommand getCredentialsCommand = setUpData(usertype, plaintext, nodes);

        mockCredentialsCpp(getCredentialsCommand);

        final NscsNameMultipleValueCommandResponse nscsResponse = (NscsNameMultipleValueCommandResponse) getCredentialsHandler
                .process(getCredentialsCommand, commandContext);
        if (usertype == null) {
            assertTrue(nscsResponse.size() == (nodes.length * 6 + 1));
        } else {
            assertTrue(nscsResponse.size() == (nodes.length + 1));
        }
        if (plaintext == null) {
            plaintext = GetCredentialsCommand.PLAIN_TEXT_HIDE;
        }
        if (usertype == null) {
            verifyResponse(nodes, nscsResponse, GetCredentialsCommand.NORMAL_USER_NAME_PROPERTY, plaintext);
            verifyResponse(nodes, nscsResponse, GetCredentialsCommand.SECURE_USER_NAME_PROPERTY, plaintext);
            verifyResponse(nodes, nscsResponse, GetCredentialsCommand.ROOT_USER_NAME_PROPERTY, plaintext);
            verifyResponse(nodes, nscsResponse, GetCredentialsCommand.NWIEA_SECURE_USER_NAME_PROPERTY, plaintext);
            verifyResponse(nodes, nscsResponse, GetCredentialsCommand.NWIEB_SECURE_USER_NAME_PROPERTY, plaintext);
            verifyResponse(nodes, nscsResponse, GetCredentialsCommand.NODECLI_USER_NAME_PROPERTY, plaintext);
        } else {
            verifyResponse(nodes, nscsResponse, usertype, plaintext);
        }

    }

    private void verifyResponse(final String[] nodes, final NscsNameMultipleValueCommandResponse nscsResponse, final String usertype,
            final String plaintext) {
        final String userNameLabel = GetCredentialsHandler.cliToDpsMap.get(usertype)[0];
        final String userPasswordLabel = GetCredentialsHandler.cliToDpsMap.get(usertype)[1];

        for (final String node : nodes) {
            final Iterator it = nscsResponse.iterator();
            it.next(); // SKIP HEADER

            while (it.hasNext()) {
                final Entry entry = (Entry) it.next();
                if (entry.getName().equals(node)) {
                    assertTrue(entry.getName().equals(node));
                    final String[] lista = entry.getValues();
                    if (lista[0].startsWith(userNameLabel)) {
                        assertTrue(lista[0].equals(userNameLabel + ":" + userNameLabel + "-" + node));
                        if (plaintext.equals(GetCredentialsCommand.PLAIN_TEXT_HIDE)) {
                            assertTrue(lista[1].equals(userPasswordLabel + ":" + GetCredentialsHandler.PASSWORD_HIDE));
                        } else {
                            assertTrue(lista[1].equals(userPasswordLabel + ":" + userPasswordLabel + "-" + node));
                        }
                    }
                }
            }
        }
    }

    private void verifyResponseNotConfigured(final String[] nodes, final NscsNameMultipleValueCommandResponse nscsResponse, final String usertype,
            final String plaintext) {
        final String userNameLabel = GetCredentialsHandler.cliToDpsMap.get(usertype)[0];
        final String userPasswordLabel = GetCredentialsHandler.cliToDpsMap.get(usertype)[1];

        for (final String node : nodes) {
            final Iterator it = nscsResponse.iterator();
            it.next(); // SKIP HEADER

            while (it.hasNext()) {
                final Entry entry = (Entry) it.next();
                if (entry.getName().equals(node)) {
                    assertTrue(entry.getName().equals(node));
                    final String[] lista = entry.getValues();
                    assertTrue(lista[0].equals(userNameLabel + ":" + GetCredentialsHandler.NOT_CONFIGURED));
                    if (plaintext.equals(GetCredentialsCommand.PLAIN_TEXT_HIDE)) {
                        assertTrue(lista[1].equals(userPasswordLabel + ":" + GetCredentialsHandler.PASSWORD_HIDE));
                    } else {
                        assertTrue(lista[1].equals(userPasswordLabel + ":" + GetCredentialsHandler.NOT_CONFIGURED));
                    }
                }
            }
        }
    }

    private void verifyResponseNotApplicable(final String[] nodes, final NscsNameMultipleValueCommandResponse nscsResponse, final String usertype,
            final String plaintext) {
        final String userNameLabel = GetCredentialsHandler.cliToDpsMap.get(usertype)[0];
        final String userPasswordLabel = GetCredentialsHandler.cliToDpsMap.get(usertype)[1];

        for (final String node : nodes) {
            final Iterator it = nscsResponse.iterator();
            it.next(); // SKIP HEADER

            while (it.hasNext()) {
                final Entry entry = (Entry) it.next();
                if (entry.getName().equals(node)) {
                    assertTrue(entry.getName().equals(node));
                    final String[] lista = entry.getValues();
                    if (userNameLabel.equals(NetworkElementSecurity.SECURE_USER_NAME)) {
                        assertTrue(lista[0].equals(userNameLabel + ":" + userNameLabel + "-" + node));
                    } else {
                        assertTrue(lista[0].equals(userNameLabel + ":" + GetCredentialsHandler.NOT_APPLICABLE));
                    }

                    if (plaintext.equals(GetCredentialsCommand.PLAIN_TEXT_HIDE)) {
                        assertTrue(lista[1].equals(userPasswordLabel + ":" + GetCredentialsHandler.PASSWORD_HIDE));
                    } else {
                        if (userPasswordLabel.equals(NetworkElementSecurity.SECURE_USER_PASSWORD)) {
                            assertTrue(lista[1].equals(userPasswordLabel + ":" + userPasswordLabel + "-" + node));
                        } else {
                            assertTrue(lista[1].equals(userPasswordLabel + ":" + GetCredentialsHandler.NOT_APPLICABLE));
                        }
                    }
                }
            }
        }
    }

    /**
     * @param getCredentialsCommand
     */
    private void mockCredentialsCpp(final GetCredentialsCommand getCredentialsCommand) {
        final ArrayList<String> expectedParams = new ArrayList<String>();
        expectedParams.add(NetworkElementSecurity.NORMAL_USER_NAME);
        expectedParams.add(NetworkElementSecurity.SECURE_USER_NAME);
        expectedParams.add(NetworkElementSecurity.ROOT_USER_NAME);
        expectedParams.add(NetworkElementSecurity.NWIEA_SECURE_USER_NAME);
        expectedParams.add(NetworkElementSecurity.NWIEB_SECURE_USER_NAME);
        expectedParams.add(NetworkElementSecurity.NODECLI_USER_NAME);
        expectedParams.add(NetworkElementSecurity.NORMAL_USER_PASSWORD);
        expectedParams.add(NetworkElementSecurity.SECURE_USER_PASSWORD);
        expectedParams.add(NetworkElementSecurity.ROOT_USER_PASSWORD);
        expectedParams.add(NetworkElementSecurity.NWIEA_SECURE_USER_PASSWORD);
        expectedParams.add(NetworkElementSecurity.NWIEB_SECURE_USER_PASSWORD);
        expectedParams.add(NetworkElementSecurity.NODECLI_USER_PASSPHRASE);
        for (final NormalizableNodeReference node : commandContext.getValidNodes()) {
            final String nodoFdn = NETWORK_ELEMENT.securityFunction.networkElementSecurity.withNames(node.getName()).fdn();
            final MoObject moObj = org.mockito.Mockito.mock(MoObject.class);
            when(cMReaderService.getMoObjectByFdn(nodoFdn)).thenReturn(moObj);

            when(nscsCapabilityModelService.getExpectedCredentialsParams(node)).thenReturn(expectedParams);
            final String name = node.getName();
            when(moObj.getAttribute(NetworkElementSecurity.NORMAL_USER_NAME)).thenReturn(NetworkElementSecurity.NORMAL_USER_NAME + "-" + name);
            when(moObj.getAttribute(NetworkElementSecurity.NORMAL_USER_PASSWORD))
                    .thenReturn(NetworkElementSecurity.NORMAL_USER_PASSWORD + "-" + name);
            when(moObj.getAttribute(NetworkElementSecurity.SECURE_USER_NAME)).thenReturn(NetworkElementSecurity.SECURE_USER_NAME + "-" + name);
            when(moObj.getAttribute(NetworkElementSecurity.SECURE_USER_PASSWORD))
                    .thenReturn(NetworkElementSecurity.SECURE_USER_PASSWORD + "-" + name);
            when(moObj.getAttribute(NetworkElementSecurity.ROOT_USER_NAME)).thenReturn(NetworkElementSecurity.ROOT_USER_NAME + "-" + name);
            when(moObj.getAttribute(NetworkElementSecurity.ROOT_USER_PASSWORD)).thenReturn(NetworkElementSecurity.ROOT_USER_PASSWORD + "-" + name);
            when(moObj.getAttribute(NetworkElementSecurity.NWIEA_SECURE_USER_NAME))
                    .thenReturn(NetworkElementSecurity.NWIEA_SECURE_USER_NAME + "-" + name);
            when(moObj.getAttribute(NetworkElementSecurity.NWIEA_SECURE_USER_PASSWORD))
                    .thenReturn(NetworkElementSecurity.NWIEA_SECURE_USER_PASSWORD + "-" + name);
            when(moObj.getAttribute(NetworkElementSecurity.NWIEB_SECURE_USER_NAME))
                    .thenReturn(NetworkElementSecurity.NWIEB_SECURE_USER_NAME + "-" + name);
            when(moObj.getAttribute(NetworkElementSecurity.NWIEB_SECURE_USER_PASSWORD))
                    .thenReturn(NetworkElementSecurity.NWIEB_SECURE_USER_PASSWORD + "-" + name);
            when(moObj.getAttribute(NetworkElementSecurity.NODECLI_USER_NAME))
            .thenReturn(NetworkElementSecurity.NODECLI_USER_NAME + "-" + name);
            when(moObj.getAttribute(NetworkElementSecurity.NODECLI_USER_PASSPHRASE))
            .thenReturn(NetworkElementSecurity.NODECLI_USER_PASSPHRASE + "-" + name);
        }

        when(passwordHelper.decryptDecode(Mockito.anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(final InvocationOnMock invocation) throws Throwable {
                final Object[] args = invocation.getArguments();
                return (String) args[0];
            }
        });
    }

    private void mockCredentialsCppNotConfigured(final GetCredentialsCommand getCredentialsCommand) {
        final ArrayList<String> expectedParams = new ArrayList<String>();
        expectedParams.add(NetworkElementSecurity.NORMAL_USER_NAME);
        expectedParams.add(NetworkElementSecurity.NORMAL_USER_PASSWORD);
        expectedParams.add(NetworkElementSecurity.SECURE_USER_NAME);
        expectedParams.add(NetworkElementSecurity.SECURE_USER_PASSWORD);
        expectedParams.add(NetworkElementSecurity.ROOT_USER_NAME);
        expectedParams.add(NetworkElementSecurity.ROOT_USER_PASSWORD);

        for (final NormalizableNodeReference node : commandContext.getValidNodes()) {
            final String nodoFdn = NETWORK_ELEMENT.securityFunction.networkElementSecurity.withNames(node.getName()).fdn();
            final MoObject moObj = org.mockito.Mockito.mock(MoObject.class);
            when(cMReaderService.getMoObjectByFdn(nodoFdn)).thenReturn(moObj);
            when(nscsCapabilityModelService.getExpectedCredentialsParams(node)).thenReturn(expectedParams);
            when(moObj.getAttribute(NetworkElementSecurity.NORMAL_USER_NAME)).thenReturn(null);
            when(moObj.getAttribute(NetworkElementSecurity.NORMAL_USER_PASSWORD)).thenReturn(null);
            when(moObj.getAttribute(NetworkElementSecurity.SECURE_USER_NAME)).thenReturn(null);
            when(moObj.getAttribute(NetworkElementSecurity.SECURE_USER_PASSWORD)).thenReturn(null);
            when(moObj.getAttribute(NetworkElementSecurity.ROOT_USER_NAME)).thenReturn(null);
            when(moObj.getAttribute(NetworkElementSecurity.ROOT_USER_PASSWORD)).thenReturn(null);
        }

        when(passwordHelper.decryptDecode(Mockito.anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(final InvocationOnMock invocation) throws Throwable {
                final Object[] args = invocation.getArguments();
                return (String) args[0];
            }
        });
    }

    private void mockCredentialsComEcim(final GetCredentialsCommand getCredentialsCommand) {
        final ArrayList<String> expectedParams = new ArrayList<String>();
        expectedParams.add(NetworkElementSecurity.SECURE_USER_NAME);
        expectedParams.add(NetworkElementSecurity.SECURE_USER_PASSWORD);
        for (final NormalizableNodeReference node : commandContext.getValidNodes()) {
            final String nodoFdn = NETWORK_ELEMENT.securityFunction.networkElementSecurity.withNames(node.getName()).fdn();
            final MoObject moObj = org.mockito.Mockito.mock(MoObject.class);
            when(cMReaderService.getMoObjectByFdn(nodoFdn)).thenReturn(moObj);
            when(nscsCapabilityModelService.getExpectedCredentialsParams(node)).thenReturn(expectedParams);
            when(moObj.getAttribute(NetworkElementSecurity.NORMAL_USER_NAME)).thenReturn(null);
            when(moObj.getAttribute(NetworkElementSecurity.NORMAL_USER_PASSWORD)).thenReturn(null);
            final String name = node.getName();
            when(moObj.getAttribute(NetworkElementSecurity.SECURE_USER_NAME)).thenReturn(NetworkElementSecurity.SECURE_USER_NAME + "-" + name);
            when(moObj.getAttribute(NetworkElementSecurity.SECURE_USER_PASSWORD))
                    .thenReturn(NetworkElementSecurity.SECURE_USER_PASSWORD + "-" + name);
            when(moObj.getAttribute(NetworkElementSecurity.ROOT_USER_NAME)).thenReturn(null);
            when(moObj.getAttribute(NetworkElementSecurity.ROOT_USER_PASSWORD)).thenReturn(null);
        }
        when(passwordHelper.decryptDecode(Mockito.anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(final InvocationOnMock invocation) throws Throwable {
                final Object[] args = invocation.getArguments();
                return (String) args[0];
            }
        });
    }

    private CmResponse buildEmptyCmResponse() {
        final CmResponse cmResponseEmpty = new CmResponse();
        final Collection<CmObject> cmObjectsEmpty = new ArrayList<>(1);
        cmResponseEmpty.setTargetedCmObjects(cmObjectsEmpty);
        return cmResponseEmpty;
    }
}
