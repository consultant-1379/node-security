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

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.command.types.CiphersConfigCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moget.impl.ComEcimMOGetServiceImpl;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CiphersConfigurationUtil;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CiphersConstants;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CiphersProtocolManager;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CiphersResponseBuilderFactory;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CiphersSshProtocolManager;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CiphersTlsProtocolManager;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.TlsCiphersMapImpl;
import com.ericsson.nms.security.nscs.handler.validation.ciphersconfig.GetCiphersValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;

/**
 * Test class for GetCiphersHandler.
 *
 * @author xkumkam
 */
@RunWith(MockitoJUnitRunner.class)
public class GetCiphersHandlerTest {

    @InjectMocks
    GetCiphersHandler getCiphersHandler;

    @Mock
    CiphersConfigCommand command;

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    private CommandContext context;

    @Mock
    NodeReference node;

    @Mock
    private NscsCMReaderService nscsCMReaderService;

    @Mock
    private NormalizableNodeReference normNode;

    @Mock
    private GetCiphersValidator getCiphersValidator;

    @Mock
    private CiphersResponseBuilderFactory ciphersProtocolManagerFactory;

    @Mock
    private TlsCiphersMapImpl tlsCiphersMap;

    @Mock
    private CiphersSshProtocolManager sshManager;

    @Mock
    private CiphersTlsProtocolManager tlsManager;

    @Mock
    private CiphersProtocolManager manager;

    @Mock
    private CiphersConfigurationUtil ciphersConfigurationUtil;

    final List<NodeReference> inputNodes = new ArrayList<NodeReference>();
    final String nodeName = "node1";

    final Map<String, List<String>> ciphersMap = new HashMap<String, List<String>>();
    ComEcimMOGetServiceImpl comEcimMOGetServiceImpl = null;
    List<NodeReference> validNodesList = new ArrayList<NodeReference>();
    final NodeReference nodeReference = new NodeRef(nodeName);
    List<String> validProtocolTypesForSetCiphers = null;

    @Before
    public void setUp() {
        validNodesList.add(nodeReference);
        inputNodes.add(nodeReference);
        comEcimMOGetServiceImpl = mock(ComEcimMOGetServiceImpl.class);
        Mockito.when(nscsCMReaderService.getNormalizableNodeReference(Matchers.any(NodeReference.class))).thenReturn(normNode);

        validProtocolTypesForSetCiphers = new ArrayList<String>();
        validProtocolTypesForSetCiphers.add(CiphersConstants.PROTOCOL_TYPE_SSH);
        validProtocolTypesForSetCiphers.add(CiphersConstants.PROTOCOL_TYPE_TLS);

    }

    /**
     * test method for {@link GetCiphersHandler#process()} Success scenario
     */
    @Test
    public void testProcess() {
        Mockito.when(command.getProtocolProperty()).thenReturn("SSL/HTTPS/TLS");
        Mockito.when(command.getNodes()).thenReturn(inputNodes);
        Mockito.when(ciphersConfigurationUtil.getValidProtocolTypesForCiphersConfiguration()).thenReturn(validProtocolTypesForSetCiphers);
        Mockito.when(ciphersProtocolManagerFactory.getCiphersmanager(command.getProtocolProperty())).thenReturn(sshManager);
        getCiphersHandler.process(command, context);
        Mockito.verify(nscsLogger, Mockito.times(1)).commandHandlerFinishedWithSuccess(command, "Command executed successfully");

    }

    /**
     * test method for {@link GetCiphersHandler#process()} with SSH protocol type
     */
    @Test
    public void testProcess_SSH() {
        final NodeReference nodeReference2 = new NodeRef("node2");
        inputNodes.add(nodeReference2);
        Mockito.when(command.getProtocolProperty()).thenReturn("SSH/SFTP");
        Mockito.when(ciphersConfigurationUtil.getValidProtocolTypesForCiphersConfiguration()).thenReturn(validProtocolTypesForSetCiphers);
        Mockito.when(command.getNodes()).thenReturn(inputNodes);
        Mockito.when(ciphersProtocolManagerFactory.getCiphersmanager(command.getProtocolProperty())).thenReturn(sshManager);
        getCiphersHandler.process(command, context);
        Mockito.verify(nscsLogger, Mockito.times(1)).commandHandlerFinishedWithSuccess(command, "Command executed successfully");
    }

    /**
     * test method for {@link GetCiphersHandler#process()} with TLS protocol type
     */
    @Test
    public void testProcess_TLS() {
        final NodeReference nodeReference2 = new NodeRef("node2");
        inputNodes.add(nodeReference2);
        Mockito.when(command.getProtocolProperty()).thenReturn("SSL/HTTPS/TLS");
        Mockito.when(ciphersConfigurationUtil.getValidProtocolTypesForCiphersConfiguration()).thenReturn(validProtocolTypesForSetCiphers);
        Mockito.when(command.getNodes()).thenReturn(inputNodes);
        Mockito.when(ciphersProtocolManagerFactory.getCiphersmanager(command.getProtocolProperty())).thenReturn(tlsManager);
        getCiphersHandler.process(command, context);
        Mockito.verify(nscsLogger, Mockito.times(1)).commandHandlerFinishedWithSuccess(command, "Command executed successfully");
    }

    /**
     * test method for {@link GetCiphersHandler#process()} Invalid protocol scenario
     */
    @Test(expected = InvalidArgumentValueException.class)
    public void testProcess_InvalidProtocol() {
        Mockito.when(command.getProtocolProperty()).thenReturn("protocol");
        Mockito.when(command.getNodes()).thenReturn(inputNodes);

        getCiphersHandler.process(command, context);
    }
}
