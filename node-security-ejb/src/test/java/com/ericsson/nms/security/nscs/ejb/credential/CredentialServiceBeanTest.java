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
package com.ericsson.nms.security.nscs.ejb.credential;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import com.ericsson.oss.services.security.nscs.context.NscsContextService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.NscsService;
import com.ericsson.nms.security.nscs.api.command.NscsCliCommand;
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsNameValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.credentials.CredentialAttributes;
import com.ericsson.nms.security.nscs.api.credentials.CredentialAttributesBuilder;
import com.ericsson.nms.security.nscs.api.credentials.SnmpV3Attributes;
import com.ericsson.nms.security.nscs.api.enums.SnmpAuthProtocol;
import com.ericsson.nms.security.nscs.api.enums.SnmpPrivProtocol;
import com.ericsson.nms.security.nscs.api.enums.SnmpSecurityLevel;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.CredentialServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;

@RunWith(MockitoJUnitRunner.class)
public class CredentialServiceBeanTest {

    @Spy
    private final Logger logger = LoggerFactory.getLogger(CredentialServiceBean.class);
    @Mock
    NscsCMReaderService reader;
    @Mock
    NscsCapabilityModelService capabilityModel;
    @InjectMocks
    CredentialServiceBean credentialServiceBean;
    @Mock
    private NscsService nscsService;
    @Mock
    private NscsContextService nscsContextService;

    private static final String HEADER_NODE = "Node";
    private static final String HEADER_AUTHPASSWORD = "Auth Password";
    private static final String HEADER_PRIVPASSWORD = "Priv Password";
    private static final String HEADER_AUTH_ALGO = "Auth Algo";
    private static final String HEADER_PRIV_ALGO = "Priv Algo";

    @Test
    public void testCreateCredentials() throws NscsServiceException {

        final String inputNodeName = "node1";
        final NodeReference nodeRef = new NodeRef(inputNodeName);
        final NormalizableNodeReference nnr = Mockito.mock(NormalizableNodeReference.class);
        Mockito.when(reader.getNormalizedNodeReference(nodeRef)).thenReturn(nnr);
        Mockito.when(reader.exists(Mockito.anyString())).thenReturn(false);
        Mockito.when(capabilityModel.getExpectedCredentialsParams(nnr)).thenReturn(
                Arrays.asList("normalusername", "normaluserpassword", "rootusername", "rootuserpassword", "secureusername", "secureuserpassword"));

        final CredentialAttributesBuilder cab = new CredentialAttributesBuilder();
        final CredentialAttributes credentialAttributes = cab.addSecure("mySecureName", "mySecurePwd").addUnsecure("myUnsecureName", "myUnsecurePwd")
                .addRoot("myRootName", "myRootPwd").build();
        credentialServiceBean.createNodeCredentials(credentialAttributes, inputNodeName);
        final NscsCommandResponse response = null;
        Mockito.when(nscsService.processCommand(Mockito.any(NscsCliCommand.class))).thenReturn(response);

        assertTrue(true);
    }

    @Test
    public void testCreateNwiECredentials() throws NscsServiceException {

        final String inputNodeName = "node1";

        final NodeReference nodeRef = new NodeRef(inputNodeName);

        final NormalizableNodeReference nnr = Mockito.mock(NormalizableNodeReference.class);
        Mockito.when(reader.getNormalizedNodeReference(nodeRef)).thenReturn(nnr);

        Mockito.when(reader.exists(Mockito.anyString())).thenReturn(false);
        Mockito.when(capabilityModel.getExpectedCredentialsParams(nnr))
                .thenReturn(Arrays.asList("normalusername", "normaluserpassword", "rootusername", "rootuserpassword", "secureusername",
                        "secureuserpassword", "nwieaSecureUserName", "nwieaSecureUserPwd", "nwiebSecureUserName", "nwiebSecureUserPwd"));

        final CredentialAttributesBuilder cab = new CredentialAttributesBuilder();
        final CredentialAttributes credentialAttributes = cab.addSecure("mySecureName", "mySecurePwd")
                .addNwieaSecure("myNwieASecureName", "myNwieASecurePwd").addNwiebSecure("myNwieBSecureName", "myNwieBSecurePwd")
                .addUnsecure("myUnsecureName", "myUnsecurePwd").addRoot("myRootName", "myRootPwd").addNodeCliUser("myCliUserName", "myCliUserPwd")
                .build();
        credentialServiceBean.createNodeCredentials(credentialAttributes, inputNodeName);
        final NscsCommandResponse response = null;
        Mockito.when(nscsService.processCommand(Mockito.any(NscsCliCommand.class))).thenReturn(response);
        assertTrue(true);
    }

    @Test
    public void testConfigureSnmpV3AuthNoPriv() throws NscsServiceException {

        final SnmpSecurityLevel securityLevel = SnmpSecurityLevel.AUTH_NO_PRIV;
        final SnmpV3Attributes snmpV3Attributes = new SnmpV3Attributes(SnmpAuthProtocol.SHA1, "test1234");
        final List<String> nodes = new ArrayList<>();
        nodes.add("node1");

        credentialServiceBean.configureSnmpV3(securityLevel, snmpV3Attributes, nodes);
        final NscsCommandResponse response = null;
        Mockito.when(nscsService.processCommand(Mockito.any(NscsPropertyCommand.class))).thenReturn(response);

        assertTrue(true);
    }

    @Test
    public void testConfigureSnmpV3AuthNoPrivWithNullAuthPwd() throws NscsServiceException {

        final SnmpSecurityLevel securityLevel = SnmpSecurityLevel.AUTH_NO_PRIV;
        final SnmpV3Attributes snmpV3Attributes = new SnmpV3Attributes(SnmpAuthProtocol.MD5, null);
        final List<String> nodes = new ArrayList<>();
        nodes.add("node1");

        try {
            credentialServiceBean.configureSnmpV3(securityLevel, snmpV3Attributes, nodes);
        } catch (final CredentialServiceException credException) {
            assertEquals("Password validation fails for auth_password: Null or empty value", credException.getMessage());
        }
    }

    @Test
    public void testConfigureSnmpV3AuthPriv() throws NscsServiceException {

        final SnmpSecurityLevel securityLevel = SnmpSecurityLevel.AUTH_PRIV;
        final SnmpV3Attributes snmpV3Attributes = new SnmpV3Attributes(SnmpAuthProtocol.MD5, "test1234", SnmpPrivProtocol.AES128, "test5678");
        final List<String> nodes = new ArrayList<>();
        nodes.add("node1");
        nodes.add("node2");

        credentialServiceBean.configureSnmpV3(securityLevel, snmpV3Attributes, nodes);
        final NscsCommandResponse response = null;
        Mockito.when(nscsService.processCommand(Mockito.any(NscsPropertyCommand.class))).thenReturn(response);

        assertTrue(true);
    }

    @Test
    public void testConfigureSnmpV3AuthPrivWithEmptyPrivPwd() throws NscsServiceException {

        final SnmpSecurityLevel securityLevel = SnmpSecurityLevel.AUTH_PRIV;
        final SnmpV3Attributes snmpV3Attributes = new SnmpV3Attributes(SnmpAuthProtocol.SHA1, "test1234", SnmpPrivProtocol.DES, "  ");
        final List<String> nodes = new ArrayList<>();
        nodes.add("node1");

        try {
            credentialServiceBean.configureSnmpV3(securityLevel, snmpV3Attributes, nodes);
        } catch (final CredentialServiceException credException) {
            assertEquals("Password validation fails for priv_password: Null or empty value", credException.getMessage());
        }
    }

    @Test
    public void testConfigureSnmpV3NoAuthNoPriv() throws NscsServiceException {

        final SnmpSecurityLevel securityLevel = SnmpSecurityLevel.NO_AUTH_NO_PRIV;
        final SnmpV3Attributes snmpV3Attributes = new SnmpV3Attributes();
        final List<String> nodes = new ArrayList<>();
        nodes.add("node1");

        try {
            credentialServiceBean.configureSnmpV3(securityLevel, snmpV3Attributes, nodes);
        } catch (final CredentialServiceException credException) {
            assertEquals("No configuration is needed for security level NO_AUTH_NO_PRIV", credException.getMessage());
        }
    }

    @Test
    public void testGetSnmpV3ConfigurationForOneNodeWithPlainText() throws NscsServiceException {

        final List<String> nodes = new ArrayList<>();
        nodes.add("node1");

        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(4);
        final String[] configuration = new String[] {"SHA1", "authKey1", "DES", "privKey1"};
        response.add("node1", configuration);

        doReturn(response).when(nscsService).processCommand(any(NscsPropertyCommand.class));

        final Map<String, SnmpV3Attributes> getResult = credentialServiceBean.getSnmpV3Configuration(true, nodes);
        final SnmpV3Attributes snmpV3Configuration = getResult.get("node1");

        assertEquals("SHA1", snmpV3Configuration.getAuthProtocolAttr().toString());
        assertEquals("authKey1", snmpV3Configuration.getAuthKey());
        assertEquals("DES", snmpV3Configuration.getPrivProtocolAttr().toString());
        assertEquals("privKey1", snmpV3Configuration.getPrivKey());
    }

    @Test
    public void testGetSnmpV3ConfigurationFromUnexpectedResponse() throws NscsServiceException {

        final List<String> nodes = new ArrayList<>();
        nodes.add("node1");

        final NscsNameValueCommandResponse response = NscsCommandResponse.nameValue();

        doReturn(response).when(nscsService).processCommand(any(NscsPropertyCommand.class));

        final Map<String, SnmpV3Attributes> getResult = credentialServiceBean.getSnmpV3Configuration(false, nodes);

        assertTrue(getResult.isEmpty());
    }

    @Test
    public void testGetSnmpV3ConfigurationFromResponseWithoutData() throws NscsServiceException {

        final List<String> nodes = new ArrayList<>();
        nodes.add("node1");

        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(4);
        response.add(HEADER_NODE, new String[] { HEADER_AUTH_ALGO, HEADER_AUTHPASSWORD, HEADER_PRIV_ALGO, HEADER_PRIVPASSWORD });

        doReturn(response).when(nscsService).processCommand(any(NscsPropertyCommand.class));

        final Map<String, SnmpV3Attributes> getResult = credentialServiceBean.getSnmpV3Configuration(true, nodes);

        assertTrue(getResult.isEmpty());
    }

    @Test
    public void testGetSnmpV3ConfigurationFromResponseWithDataMoreThanFour() throws NscsServiceException {

        final List<String> nodes = new ArrayList<>();
        nodes.add("node1");

        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(5);
        final String[] configuration = new String[] {"MD5", "***********", "DES", "***********", "dummy item"};
        response.add("node1", configuration);

        doReturn(response).when(nscsService).processCommand(any(NscsPropertyCommand.class));

        final Map<String, SnmpV3Attributes> getResult = credentialServiceBean.getSnmpV3Configuration(false, nodes);
        final SnmpV3Attributes snmpV3Configuration = getResult.get("node1");

        assertEquals("MD5", snmpV3Configuration.getAuthProtocolAttr().toString());
        assertEquals("***********", snmpV3Configuration.getAuthKey());
        assertEquals("DES", snmpV3Configuration.getPrivProtocolAttr().toString());
        assertEquals("***********", snmpV3Configuration.getPrivKey());
     }

    @Test
    public void testGetSnmpV3ConfigurationFromResponseWithDataLessThanFour() throws NscsServiceException {

        final List<String> nodes = new ArrayList<>();
        nodes.add("node1");

        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(3);
        final String[] configuration = new String[] {"SHA1", "dummyauthkey", "DES"};
        response.add("node1", configuration);

        doReturn(response).when(nscsService).processCommand(any(NscsPropertyCommand.class));

        try {
            credentialServiceBean.getSnmpV3Configuration(true, nodes);
        } catch (final CredentialServiceException credException) {
            assertEquals("Missing SNMPv3 attribute(s) in response" + Arrays.toString(configuration), credException.getMessage());
        }
     }

    @Test
    public void testGetSnmpV3ConfigurationForMultipleNodes() throws NscsServiceException {

        final List<String> nodes = new ArrayList<>();
        nodes.add("node1");
        nodes.add("node2");

        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(4);
        final String[] configuration1 = new String[] {"MD5", "authkey1", "AES128", "privkey1"};
        final String[] configuration2 = new String[] {"SHA1", "authkey2", "AES128", "privkey2"};
        response.add("node1", configuration1);
        response.add("node2", configuration2);

        doReturn(response).when(nscsService).processCommand(any(NscsPropertyCommand.class));

        final Map<String, SnmpV3Attributes> getResult = credentialServiceBean.getSnmpV3Configuration(true, nodes);
        final SnmpV3Attributes snmpV3Configuration1 = getResult.get("node1");
        final SnmpV3Attributes snmpV3Configuration2 = getResult.get("node2");

        assertEquals("MD5", snmpV3Configuration1.getAuthProtocolAttr().toString());
        assertEquals("authkey1", snmpV3Configuration1.getAuthKey());
        assertEquals("AES128", snmpV3Configuration1.getPrivProtocolAttr().toString());
        assertEquals("privkey1", snmpV3Configuration1.getPrivKey());

        assertEquals("SHA1", snmpV3Configuration2.getAuthProtocolAttr().toString());
        assertEquals("authkey2", snmpV3Configuration2.getAuthKey());
        assertEquals("AES128", snmpV3Configuration2.getPrivProtocolAttr().toString());
        assertEquals("privkey2", snmpV3Configuration2.getPrivKey());
     }
}
