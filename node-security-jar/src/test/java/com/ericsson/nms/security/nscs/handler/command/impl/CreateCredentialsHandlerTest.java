package com.ericsson.nms.security.nscs.handler.command.impl;

import static com.ericsson.nms.security.nscs.handler.command.impl.MockUtils.setupCommandContext;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.Before;
import org.junit.Ignore;
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
import com.ericsson.nms.security.nscs.api.command.types.CredentialsCommand;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.command.types.TargetGroupsCommand;
import com.ericsson.nms.security.nscs.api.exception.NetworkElementSecurityAlreadyExistsException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.wrapper.NodeException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelInfo;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.itpf.security.cryptography.CryptographyService;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;

/**
 * Tests the CreateCredentialsHandler that Creates a NetworkElementSecurity Mo associated to each of the specified nodes
 * 
 * @see CreateCredentialsHandler
 * @author eabdsin
 */

@RunWith(MockitoJUnitRunner.class)
public class CreateCredentialsHandlerTest {

    public static final String NODE_NAME_HEADER = "Node Name        ";
    public static final String NODE_SECURITY_LEVEL_HEADER = "Node Security Level";
    public static final String CHAR_ENCODING = "UTF-8";
    private static final String PASSWORD2 = "password";
    private static final String NETWORK_ELEMENT_SECURITY_NS = "networkElementSecurityNamespace";
    private static final String NETWORK_ELEMENT_SECURITY_VERSION = "networkElementSecurityVersion";
    private static final String NODE12 = "node1";
    private static final String TARGET_GROUP3 = "TargetGroup3";
    private static final String TARGET_GROUP2 = "TargetGroup2";
    private static final String TARGET_GROUP1 = "TargetGroup1";
    @Spy
    private final Logger logger = LoggerFactory.getLogger(NscsCMWriterService.class);

    @InjectMocks
    private CreateCredentialsHandler beanUnderTest;

    @Mock
    private NscsCMWriterService cMWriterService;

    @Mock
    private NscsCMReaderService cMReaderService;

    @Mock
    private CryptographyService cryptographyService;

    @Mock
    private NscsModelServiceImpl nscsModelServiceImpl;

    @Mock
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Mock
    private CommandContext commandContext;

    @Before
    public void setupTest() {
        setupCommandContext(commandContext, NODE12);
    }

    /**
     * Tests CreateCredentialsHandler Positive flow
     * 
     * @throws Exception
     */
    @Ignore
    @Test
    public void testProcess_CreateCredentialsHandler_Positive() throws Exception {
        final byte[] password = PASSWORD2.getBytes();

        final CredentialsCommand CredentialsCommand = setUpData(buildEmptyCmResponse(), password);

        final NscsCommandResponse nscsResponse1 = beanUnderTest.process(CredentialsCommand, commandContext);

        assertTrue(
                CreateCredentialsHandler.ALL_CREDENTIALS_WERE_CREATED_SUCCESSFULLY.equals(((NscsMessageCommandResponse) nscsResponse1).getMessage()));

    }

    private CredentialsCommand setUpData(final CmResponse cmResponseEmpty, final byte[] password) {
        final CredentialsCommand CredentialsCommand = buildCredentialsCommand();
        final List<String> targetGroups = new ArrayList<String>();
        final CmResponse cmResponse = buildCmResponse(targetGroups);
        final Collection<CmObject> cmObjects = cmResponse.getCmObjects();
        final CmObject cmObject = cmObjects.iterator().next();
        final String networkElementSecurityType = Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.type();
        final String networkElementSecurityNamespace = NETWORK_ELEMENT_SECURITY_NS;
        final String networkElementSecurityVersion = NETWORK_ELEMENT_SECURITY_VERSION;

        final NscsCMWriterService.WriterSpecificationBuilder specificationBuilder = org.mockito.Mockito
                .mock(NscsCMWriterService.WriterSpecificationBuilder.class);

        setUpMocks(CredentialsCommand, targetGroups, cmResponse, cmObject, networkElementSecurityType, networkElementSecurityNamespace,
                networkElementSecurityVersion, specificationBuilder, cmResponseEmpty, password);
        return CredentialsCommand;
    }

    @SuppressWarnings("unchecked")
    private void setUpMocks(final CredentialsCommand CredentialsCommand, final List<String> targetGroups, final CmResponse cmResponse,
                            final CmObject cmObject, final String networkElementSecurityType, final String networkElementSecurityNamespace,
                            final String networkElementSecurityVersion, final NscsCMWriterService.WriterSpecificationBuilder specificationBuilder,
                            final CmResponse cmResponseEmpty, final byte[] password) {
        mockNscsModelServiceImpl(networkElementSecurityType, networkElementSecurityNamespace, networkElementSecurityVersion);
        mockCmReaderService(CredentialsCommand, cmResponse, cmResponseEmpty);
        mockCmWriterService(cmObject, networkElementSecurityType, networkElementSecurityNamespace, networkElementSecurityVersion,
                specificationBuilder);
        mockSpecificationBuilder(targetGroups, cmResponse, specificationBuilder);
        when(cryptographyService.encrypt(any(byte[].class))).thenReturn(password);
        doThrow(new NodeException(new NodeRef("node1"), new NetworkElementSecurityAlreadyExistsException())).when(commandContext)
                .setAsInvalidOrFailedAndThrow(any(Collection.class), any(NscsServiceException.class));
    }

    private void mockSpecificationBuilder(final List<String> targetGroups, final CmResponse cmResponse,
                                          final NscsCMWriterService.WriterSpecificationBuilder specificationBuilder) {
        when(specificationBuilder.setAttribute(NetworkElementSecurity.TARGET_GROUPS, targetGroups)).thenReturn(specificationBuilder);
        when(specificationBuilder.setAttribute(any(String.class), any(Object.class))).thenReturn(specificationBuilder);
        when(specificationBuilder.setParent(any(String.class))).thenReturn(specificationBuilder);
    }

    private void mockNscsModelServiceImpl(final String networkElementSecurityModel, final String networkElementSecurityNamespace,
                                          final String networkElementSecurityVersion) {
        when(nscsModelServiceImpl.getLatestVersionOfNormalizedModel(networkElementSecurityModel)).thenReturn(new NscsModelInfo(
                SchemaConstants.DPS_PRIMARYTYPE, networkElementSecurityNamespace, networkElementSecurityModel, networkElementSecurityVersion));
    }

    private void mockCmWriterService(final CmObject cmObject, final String networkElementSecurityType, final String networkElementSecurityNamespace,
                                     final String networkElementSecurityVersion,
                                     final NscsCMWriterService.WriterSpecificationBuilder specificationBuilder) {
        when(cMWriterService.withSpecification(networkElementSecurityType, networkElementSecurityNamespace, networkElementSecurityVersion))
                .thenReturn(specificationBuilder);
        when(cMWriterService.withSpecification(cmObject.getFdn())).thenReturn(specificationBuilder);
    }

    private void mockCmReaderService(final CredentialsCommand credentialsCommand, final CmResponse cmResponse, final CmResponse cmResponseEmpty) {
        when(cMReaderService.getMOAttribute(credentialsCommand.getNodes(), Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.type(),
                Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.namespace(), NetworkElementSecurity.TARGET_GROUPS))
                        .thenReturn(cmResponse);
        when(cMReaderService.getMOAttribute(eq(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.type()),
                eq(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.namespace()), eq(NetworkElementSecurity.NETWORK_ELEMENT_SECURITY_ID),
                anyListOf(String.class))).thenReturn(cmResponseEmpty);
        when(cMReaderService.getMOAttribute(anyListOf(NodeReference.class), eq(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.type()),
                eq(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.namespace()),
                eq(NetworkElementSecurity.NETWORK_ELEMENT_SECURITY_ID))).thenReturn(cmResponseEmpty);
    }

    private CmResponse buildCmResponse(final List<String> targetGroups) {
        final CmResponse cmResponse = new CmResponse();
        final Map<String, Object> attributesMap = new HashMap<>();

        attributesMap.put(NetworkElementSecurity.TARGET_GROUPS, targetGroups);

        final Collection<CmObject> cmObjects = new ArrayList<>(1);
        final CmObject cmObject = new CmObject();
        cmObject.setAttributes(attributesMap);
        cmObject.setFdn(NODE12);
        cmObjects.add(cmObject);
        cmResponse.setTargetedCmObjects(cmObjects);
        cmResponse.setStatusCode(0);
        return cmResponse;

    }

    private List<String> buildTargetGroupList() {
        final List<String> targetGroups = new ArrayList<String>();
        targetGroups.add(TARGET_GROUP1);
        targetGroups.add(TARGET_GROUP2);
        targetGroups.add(TARGET_GROUP3);
        return targetGroups;
    }

    private CredentialsCommand buildCredentialsCommand() {
        final CredentialsCommand targetGroupsCommand = new CredentialsCommand();
        targetGroupsCommand.setCommandType(NscsCommandType.CREATE_CREDENTIALS);
        final Map<String, Object> commandMap = new HashMap<String, Object>() {

            private static final long serialVersionUID = 1L;

            {
                {
                    put(NscsNodeCommand.NODE_LIST_PROPERTY, Arrays.asList(NODE12));
                    put(TargetGroupsCommand.TARGET_GROUP_PROPERTY, buildTargetGroupList());

                    put(CredentialsCommand.NORMAL_USER_NAME_PROPERTY, NetworkElementSecurity.NORMAL_USER_NAME);

                    put(CredentialsCommand.NORMAL_USER_PASSWORD_PROPERTY, NetworkElementSecurity.NORMAL_USER_PASSWORD);
                    put(CredentialsCommand.ROOT_USER_NAME_PROPERTY, NetworkElementSecurity.ROOT_USER_NAME);
                    put(CredentialsCommand.ROOT_USER_PASSWORD_PROPERTY, NetworkElementSecurity.ROOT_USER_PASSWORD);
                    put(CredentialsCommand.SECURE_USER_NAME_PROPERTY, NetworkElementSecurity.SECURE_USER_NAME);
                    put(CredentialsCommand.SECURE_USER_PASSWORD_PROPERTY, NetworkElementSecurity.SECURE_USER_PASSWORD);
                    put(CredentialsCommand.NWIEA_SECURE_USER_NAME_PROPERTY, NetworkElementSecurity.NWIEA_SECURE_USER_NAME);
                    put(CredentialsCommand.NWIEA_SECURE_PASSWORD_PROPERTY, NetworkElementSecurity.NWIEA_SECURE_USER_PASSWORD);
                    put(CredentialsCommand.NWIEB_SECURE_USER_NAME_PROPERTY, NetworkElementSecurity.NWIEB_SECURE_USER_NAME);
                    put(CredentialsCommand.NWIEB_SECURE_PASSWORD_PROPERTY, NetworkElementSecurity.NWIEB_SECURE_USER_PASSWORD);
                }
            }
        };
        targetGroupsCommand.setProperties(commandMap);
        return targetGroupsCommand;
    }

    private CmResponse buildEmptyCmResponse() {
        final CmResponse cmResponseEmpty = new CmResponse();
        final Collection<CmObject> cmObjectsEmpty = new ArrayList<>(1);
        cmResponseEmpty.setTargetedCmObjects(cmObjectsEmpty);
        return cmResponseEmpty;
    }
}
