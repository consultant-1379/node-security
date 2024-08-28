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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.Nodes;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.Nodes.Node;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Security;
import com.ericsson.nms.security.nscs.data.ModelDefinition.FmFunction;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;

@RunWith(MockitoJUnitRunner.class)
public class CppSetSecurityLevelValidatorTest {

    @Spy
    private final Logger logger = LoggerFactory.getLogger(CppSetSecurityLevelValidatorTest.class);

    @Mock
    private NodeValidatorUtility nodeValidatorUtility;

    @InjectMocks
    private CppSetSecurityLevelValidator cppSetSecurityLevelValidator;

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    NormalizableNodeReference normNode;

    @Mock
    NormalizableNodeReference normalizableNodeReference;

    @Mock
    NormalizableNodeReference normalizableReference;

    @Mock
    private NscsCMReaderService reader;

    @Mock
    CmResponse cmResponse;

    @Mock
    CmObject cmObject;

    @Mock
    Map<String, Object> securityLevels;

    final String DUMMY_OSS_MODEL_IDENTITY = "123-456-789";
    final String testFdnName = "Test_node_001";
    final String testSubjAltName = "10.0.0.1";
    final String testSubjAltNameType = "ip_address";
    final String testEnrollmentMode = "scep";
    final String testKeysize = "rsa_1024";
    final String currentServiceState = FmFunction.AlarmStatusValue.IN_SERVICE.name();
    final List<Node> inputList = new LinkedList<Node>();
    List<NodeReference> inputNodeRefList = new LinkedList<NodeReference>();
    List<NodeReference> validNodesList;
    Map<NodeReference, NscsServiceException> invalidNodesErrorMap;

    @Before
    public void setup() {
        final Node testNode = new Node(testFdnName);
        testNode.setSubjectAltName(testSubjAltName);
        testNode.setSubjectAltNameType(testSubjAltNameType);
        testNode.setEnrollmentMode(testEnrollmentMode);
        testNode.setKeySize(testKeysize);
        inputList.add(testNode);
    }

    /**
     * test method for {@link NscsCommandManagerBean#validateNodesForSecurityLevelChange()}
     */

    @Test
    public void testValidateNodesForSecurityLevelChangeSuccess() {
        final List<NormalizableNodeReference> validNodesList = new ArrayList<NormalizableNodeReference>();
        final Map<Node, NscsServiceException> invalidNodesErrorMap = new HashMap<>();
        final List<Node> inputNodes = new ArrayList<Nodes.Node>(inputList);
        final List<Node> uniqueNodes = new ArrayList<Nodes.Node>(new HashSet<Nodes.Node>(inputNodes));
        final Map<String, SecurityLevel> currentSecurityLevels = new HashMap<String, SecurityLevel>();
        final Map<String, String> requestedEnrollmentModes = new HashMap<String, String>();

        final List<CmObject> cmObjects = new ArrayList<CmObject>();
        cmObjects.add(cmObject);

        logger.debug("Number of input nodes {}", uniqueNodes.size());
        final SecurityLevel requiredSecurityLevel = SecurityLevel.LEVEL_2;

        final String fdn = inputNodes.get(0).getNodeFdn();
        final NodeReference nodeRef = new NodeRef(fdn);
        doReturn(true).when(nodeValidatorUtility).validateNode(nodeRef);
        doReturn(normNode).when(reader).getNormalizableNodeReference(nodeRef);
        doReturn("ERBS").when(normNode).getNeType();
        doReturn(true).when(nodeValidatorUtility).isNeTypeSupported(normNode, NscsCapabilityModelService.SECURITYLEVEL_COMMAND);
        doReturn(fdn).when(normNode).getFdn();
        doReturn(cmResponse).when(reader).getMOAttribute(fdn, Model.ME_CONTEXT.managedElement.systemFunctions.security.type(),
                Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace(),
                Model.ME_CONTEXT.managedElement.systemFunctions.security.OPERATIONAL_SECURITY_LEVEL);
        doReturn(true).when(nodeValidatorUtility).isEnrollmentModeSupported(Mockito.anyString(), Mockito.any(NormalizableNodeReference.class));
        doReturn(cmObjects).when(cmResponse).getCmObjects();
        doReturn(securityLevels).when(cmObject).getAttributes();

        doReturn("LEVEL_1").when(securityLevels).get(Security.OPERATIONAL_SECURITY_LEVEL);

        final boolean areInputNodesValid = cppSetSecurityLevelValidator.validateNodes(uniqueNodes, validNodesList, invalidNodesErrorMap,
                currentSecurityLevels, requiredSecurityLevel, requestedEnrollmentModes);
        assertTrue("Success Scenario", areInputNodesValid);
    }

    /**
     * test method for {@link NscsCommandManagerBean#validateNodesForSecurityLevelChange()}
     */
    @Test
    public void testValidateNodesForSecurityLevelChangePartialSuccess() {

        final List<NormalizableNodeReference> validNodesList = new ArrayList<NormalizableNodeReference>();
        final Map<Node, NscsServiceException> invalidNodesErrorMap = new HashMap<>();

        final Node testNode1 = new Node("Test_node_002");
        testNode1.setSubjectAltName(testSubjAltName);
        testNode1.setSubjectAltNameType(testSubjAltNameType);
        testNode1.setEnrollmentMode(testEnrollmentMode);
        testNode1.setKeySize(testKeysize);
        inputList.add(testNode1);

        final List<Node> inputNodes = new ArrayList<Nodes.Node>(inputList);
        final List<Node> uniqueNodes = new ArrayList<Nodes.Node>(new HashSet<Nodes.Node>(inputNodes));
        final Map<String, SecurityLevel> currentSecurityLevels = new HashMap<String, SecurityLevel>();
        final Map<String, String> requestedEnrollmentModes = new HashMap<String, String>();
        final List<CmObject> cmObjects = new ArrayList<CmObject>();
        cmObjects.add(cmObject);

        logger.debug("Number of input nodes {}", uniqueNodes.size());
        final SecurityLevel requiredSecurityLevel = SecurityLevel.LEVEL_2;

        final String fdn1 = inputNodes.get(0).getNodeFdn();
        final String fdn2 = inputNodes.get(1).getNodeFdn();
        final List<NodeReference> nodeRefList = new ArrayList<NodeReference>();
        nodeRefList.add(new NodeRef(fdn1));
        nodeRefList.add(new NodeRef(fdn2));
        final String enrollmentMode = inputNodes.get(0).getEnrollmentMode();

        doReturn(true).when(nodeValidatorUtility).validateNode(nodeRefList.get(0));
        doReturn(normNode).when(reader).getNormalizableNodeReference(nodeRefList.get(0));
        doReturn(fdn1).when(normNode).getFdn();
        doReturn("ERBS").when(normNode).getNeType();
        doReturn(true).when(nodeValidatorUtility).isNeTypeSupported(normNode, NscsCapabilityModelService.SECURITYLEVEL_COMMAND);
        doReturn(cmResponse).when(reader).getMOAttribute(fdn1, Model.ME_CONTEXT.managedElement.systemFunctions.security.type(),
                Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace(), Security.OPERATIONAL_SECURITY_LEVEL);
        doReturn(true).when(nodeValidatorUtility).isEnrollmentModeSupported(enrollmentMode, normNode);

        doReturn(cmObjects).when(cmResponse).getCmObjects();
        doReturn(securityLevels).when(cmObject).getAttributes();
        doReturn("LEVEL_1").when(securityLevels).get(Security.OPERATIONAL_SECURITY_LEVEL);

        doReturn(true).when(nodeValidatorUtility).validateNode(nodeRefList.get(1));
        doReturn(normalizableNodeReference).when(reader).getNormalizableNodeReference(nodeRefList.get(1));
        doReturn(fdn2).when(normalizableNodeReference).getFdn();
        doReturn("RadioTNode").when(normalizableNodeReference).getNeType();
        doReturn(false).when(nodeValidatorUtility).isNeTypeSupported(normalizableNodeReference, NscsCapabilityModelService.SECURITYLEVEL_COMMAND);

        final boolean areInputNodesValid = cppSetSecurityLevelValidator.validateNodes(uniqueNodes, validNodesList, invalidNodesErrorMap,
                currentSecurityLevels, requiredSecurityLevel, requestedEnrollmentModes);
        assertFalse("Partial Success Scenario", areInputNodesValid);

    }

    /**
     * test method for {@link NscsCommandManagerBean#validateNodesForSecurityLevelChange()}
     */
    @Test
    public void testValidateNodesForSecurityLevelChangeFailure() {

        final List<NormalizableNodeReference> validNodesList = new ArrayList<NormalizableNodeReference>();
        final Map<Node, NscsServiceException> invalidNodesErrorMap = new HashMap<>();

        final Node testNode1 = new Node("Test_node_002");
        testNode1.setSubjectAltName(testSubjAltName);
        testNode1.setSubjectAltNameType(testSubjAltNameType);
        testNode1.setEnrollmentMode("cmp");
        testNode1.setKeySize(testKeysize);

        final Node testNode2 = new Node("Test_node_003");
        testNode2.setSubjectAltName(testSubjAltName);
        testNode2.setSubjectAltNameType(testSubjAltNameType);
        testNode2.setEnrollmentMode(testEnrollmentMode);
        testNode2.setKeySize(testKeysize);
        inputList.add(testNode1);
        inputList.add(testNode2);

        final List<Node> inputNodes = new ArrayList<Nodes.Node>(inputList);
        final List<Node> uniqueNodes = new ArrayList<Nodes.Node>(new HashSet<Nodes.Node>(inputNodes));
        final Map<String, SecurityLevel> currentSecurityLevels = new HashMap<String, SecurityLevel>();
        final Map<String, String> requestedEnrollmentModes = new HashMap<String, String>();
        final List<CmObject> cmObjects = new ArrayList<CmObject>();
        cmObjects.add(cmObject);

        logger.debug("Number of input nodes {}", uniqueNodes.size());
        final SecurityLevel requiredSecurityLevel = SecurityLevel.LEVEL_2;

        final String fdn1 = inputNodes.get(0).getNodeFdn();
        final String fdn2 = inputNodes.get(1).getNodeFdn();
        final String fdn3 = inputNodes.get(2).getNodeFdn();

        final List<NodeReference> nodeRefList = new ArrayList<NodeReference>();
        nodeRefList.add(new NodeRef(fdn1));
        nodeRefList.add(new NodeRef(fdn2));
        nodeRefList.add(new NodeRef(fdn3));

        final String enrollmentMode1 = inputNodes.get(0).getEnrollmentMode();
        final String enrollmentMode2 = inputNodes.get(1).getEnrollmentMode();

        doReturn(true).when(nodeValidatorUtility).validateNode(nodeRefList.get(0));
        doReturn(normNode).when(reader).getNormalizableNodeReference(nodeRefList.get(0));
        doReturn(fdn1).when(normNode).getFdn();
        doReturn("ERBS").when(normNode).getNeType();
        doReturn(true).when(nodeValidatorUtility).isNeTypeSupported(normNode, NscsCapabilityModelService.SECURITYLEVEL_COMMAND);
        doReturn(cmResponse).when(reader).getMOAttribute(fdn1, Model.ME_CONTEXT.managedElement.systemFunctions.security.type(),
                Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace(), Security.OPERATIONAL_SECURITY_LEVEL);
        doReturn(true).when(nodeValidatorUtility).isEnrollmentModeSupported(enrollmentMode1, normNode);

        doReturn(cmObjects).when(cmResponse).getCmObjects();
        doReturn(securityLevels).when(cmObject).getAttributes();
        doReturn("LEVEL_2").when(securityLevels).get(Security.OPERATIONAL_SECURITY_LEVEL);

        doReturn(true).when(nodeValidatorUtility).validateNode(nodeRefList.get(1));
        doReturn(normalizableNodeReference).when(reader).getNormalizableNodeReference(nodeRefList.get(1));
        doReturn(fdn2).when(normalizableNodeReference).getFdn();
        doReturn("ERBS").when(normalizableNodeReference).getNeType();
        doReturn(false).when(nodeValidatorUtility).isNeTypeSupported(normalizableNodeReference, NscsCapabilityModelService.SECURITYLEVEL_COMMAND);
        doReturn(cmResponse).when(reader).getMOAttribute(fdn2, Model.ME_CONTEXT.managedElement.systemFunctions.security.type(),
                Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace(), Security.OPERATIONAL_SECURITY_LEVEL);
        doReturn(false).when(nodeValidatorUtility).isEnrollmentModeSupported(enrollmentMode2, normalizableNodeReference);

        doReturn(true).when(nodeValidatorUtility).validateNode(nodeRefList.get(2));
        doReturn(normalizableReference).when(reader).getNormalizableNodeReference(nodeRefList.get(2));
        doReturn(fdn3).when(normalizableReference).getFdn();
        doReturn("ERBS").when(normalizableReference).getNeType();
        doReturn(true).when(nodeValidatorUtility).isNeTypeSupported(normalizableReference, NscsCapabilityModelService.SECURITYLEVEL_COMMAND);
        doReturn(null).when(reader).getMOAttribute(fdn3, Model.ME_CONTEXT.managedElement.systemFunctions.security.type(),
                Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace(), Security.OPERATIONAL_SECURITY_LEVEL);

        final boolean areInputNodesValid = cppSetSecurityLevelValidator.validateNodes(uniqueNodes, validNodesList, invalidNodesErrorMap,
                currentSecurityLevels, requiredSecurityLevel, requestedEnrollmentModes);

        assertFalse("Failure Scenario", areInputNodesValid);

    }
}
