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
package com.ericsson.nms.security.nscs.cpp.ipsec.util;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.*;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CmFunction;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.WfQueryService;


@RunWith(MockitoJUnitRunner.class)
public class IpSecNodeValidatorUtilityTest {

	@Mock
	private NscsLogger nscsLogger;

	@Mock
	private NscsCMReaderService mockReaderService;
	
	@Mock 
	private NormalizableNodeReference mockNormalizedNodeReference;
	
	@Mock
	private CmResponse mockCMResponse;
	
	@Mock
	private CmObject mockCmObject;
	
	@Mock
	private Map<String, Object> mockAttributeMap;
	
	@Mock
	private WfQueryService mockWfQuery;
	
	@Mock
	private NscsCapabilityModelService capabilityService;
	
	@InjectMocks
	private IpSecNodeValidatorUtility testObj;
	
	private String nodeName = "Node123";
	
	private NodeReference nodeRef = new NodeRef(nodeName);
	
	
	
	
	@Test
	public void testValidateNodeForIpSecOperation() {
		Mockito.when(capabilityService.getMirrorRootMo(mockNormalizedNodeReference)).thenReturn(Model.ME_CONTEXT.managedElement);
		Mockito.when(mockReaderService.exists(Mockito.anyString())).thenReturn(true);
		String fdn = Model.ME_CONTEXT.managedElement.ipSystem.ipSec.withNames(nodeRef.getName()).fdn();
		Mockito.when(mockReaderService.getNormalizableNodeReference(Mockito.any(NodeReference.class))).thenReturn(mockNormalizedNodeReference);
		Mockito.when(mockReaderService.getMOAttribute(Mockito.any(NormalizableNodeReference.class),
				Mockito.matches(Model.NETWORK_ELEMENT.cmFunction.type()),
				Mockito.matches(Model.NETWORK_ELEMENT.cmFunction.namespace()),
				Mockito.matches(CmFunction.SYNC_STATUS))).thenReturn(mockCMResponse);
		Mockito.when(mockCMResponse.getCmObjects()).thenReturn(Arrays.asList(mockCmObject));
		Mockito.when(mockCmObject.getAttributes()).thenReturn(mockAttributeMap);
		Mockito.when(mockAttributeMap.get(Mockito.matches(CmFunction.SYNC_STATUS))).thenReturn(ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name());
		Mockito.when(mockReaderService.exists(Mockito.matches(fdn))).thenReturn(true);
		Mockito.when(mockNormalizedNodeReference.getNormalizedRef()).thenReturn(nodeRef);
		Mockito.when(mockWfQuery.isWorkflowInProgress(Mockito.any(NodeRef.class))).thenReturn(false);
		assertTrue("Node is valid ", testObj.validateNodeForIpSecOperation(nodeRef));
	}

	@Test(expected=NetworkElementNotfoundException.class)
	public void testValidateNodeForIpSecOperation_InvalidIpSec() {
		Mockito.when(mockReaderService.exists(Mockito.matches(nodeRef.getFdn()))).thenReturn(false);
		testObj.validateNodeForIpSecOperation(nodeRef);
	}

	
	@Test(expected=IpSecMoNotFoundException.class)
	public void testValidateNodeForIpSecOperation_NotExists() {
		Mockito.when(capabilityService.getMirrorRootMo(mockNormalizedNodeReference)).thenReturn(Model.ME_CONTEXT.managedElement);
		Mockito.when(mockReaderService.exists(Mockito.matches(nodeRef.getFdn()))).thenReturn(true);
		String fdn = Model.ME_CONTEXT.managedElement.ipSystem.ipSec.withNames(nodeRef.getName()).fdn();
		Mockito.when(mockReaderService.getNormalizableNodeReference(Mockito.any(NodeReference.class))).thenReturn(mockNormalizedNodeReference);
		Mockito.when(mockReaderService.getMOAttribute(Mockito.any(NormalizableNodeReference.class),
				Mockito.matches(Model.NETWORK_ELEMENT.cmFunction.type()),
				Mockito.matches(Model.NETWORK_ELEMENT.cmFunction.namespace()),
				Mockito.matches(CmFunction.SYNC_STATUS))).thenReturn(mockCMResponse);
		Mockito.when(mockCMResponse.getCmObjects()).thenReturn(Arrays.asList(mockCmObject));
		Mockito.when(mockCmObject.getAttributes()).thenReturn(mockAttributeMap);
		Mockito.when(mockAttributeMap.get(Mockito.matches(CmFunction.SYNC_STATUS))).thenReturn(ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name());
		Mockito.when(mockReaderService.exists(Mockito.matches(fdn))).thenReturn(false);	
		testObj.validateNodeForIpSecOperation(nodeRef);
	}

	
	@Test(expected=NodeNotSynchronizedException.class)
	public void testValidateNodeForIpSecOperation_NotSynch() {
		Mockito.when(mockReaderService.exists(Mockito.matches(nodeRef.getFdn()))).thenReturn(true);
		String fdn = Model.ME_CONTEXT.managedElement.ipSystem.ipSec.withNames(nodeRef.getName()).fdn();
		Mockito.when(mockReaderService.exists(Mockito.matches(fdn))).thenReturn(true);	
		Mockito.when(mockReaderService.getNormalizableNodeReference(Mockito.any(NodeReference.class))).thenReturn(mockNormalizedNodeReference);
		Mockito.when(mockReaderService.getMOAttribute(Mockito.any(NormalizableNodeReference.class),
				Mockito.matches(Model.NETWORK_ELEMENT.cmFunction.type()),
				Mockito.matches(Model.NETWORK_ELEMENT.cmFunction.namespace()),
				Mockito.matches(CmFunction.SYNC_STATUS))).thenReturn(mockCMResponse);
		Mockito.when(mockCMResponse.getCmObjects()).thenReturn(Arrays.asList(mockCmObject));
		Mockito.when(mockCmObject.getAttributes()).thenReturn(mockAttributeMap);
		Mockito.when(mockAttributeMap.get(Mockito.matches(CmFunction.SYNC_STATUS))).thenReturn(ModelDefinition.CmFunction.SyncStatusValue.PENDING.name());
		testObj.validateNodeForIpSecOperation(nodeRef);
	}

	@Test(expected=NodeIsInWorkflowException.class)
	public void testValidateNodeForIpSecOperation_NodeInAnotherWF() {
		Mockito.when(capabilityService.getMirrorRootMo(mockNormalizedNodeReference)).thenReturn(Model.ME_CONTEXT.managedElement);
		Mockito.when(mockReaderService.exists(Mockito.anyString())).thenReturn(true);
		Mockito.when(mockReaderService.getNormalizableNodeReference(Mockito.any(NodeReference.class))).thenReturn(mockNormalizedNodeReference);
		Mockito.when(mockReaderService.getMOAttribute(Mockito.any(NormalizableNodeReference.class),
				Mockito.matches(Model.NETWORK_ELEMENT.cmFunction.type()),
				Mockito.matches(Model.NETWORK_ELEMENT.cmFunction.namespace()),
				Mockito.matches(CmFunction.SYNC_STATUS))).thenReturn(mockCMResponse);
		Mockito.when(mockCMResponse.getCmObjects()).thenReturn(Arrays.asList(mockCmObject));
		Mockito.when(mockCmObject.getAttributes()).thenReturn(mockAttributeMap);
		Mockito.when(mockAttributeMap.get(Mockito.matches(CmFunction.SYNC_STATUS))).thenReturn(ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name());
		Mockito.when(mockNormalizedNodeReference.getNormalizedRef()).thenReturn(nodeRef);
		Mockito.when(mockWfQuery.isWorkflowInProgress(Mockito.any(NodeRef.class))).thenReturn(true);
		assertTrue("Node is valid ", testObj.validateNodeForIpSecOperation(nodeRef));
	}
	
	@Test
	public void testIsNodeExists() {
		Mockito.when(mockReaderService.exists(Mockito.matches(nodeRef.getFdn()))).thenReturn(true);
		assertTrue("Node must Exists", testObj.isNodeExists(nodeRef));
	}

	
	@Test
	public void testIsNodeHasIpSecMO() {
		Mockito.when(capabilityService.getMirrorRootMo(mockNormalizedNodeReference)).thenReturn(Model.ME_CONTEXT.managedElement);
		Mockito.when(mockReaderService.exists(Mockito.anyString())).thenReturn(true);
		assertTrue("Node must have IpSecMO", testObj.isNodeHasIpSecMO(mockNormalizedNodeReference));
		
	}

	
	@Test
	public void testIsNodeSynchronized() {
		Mockito.when(mockReaderService.getNormalizableNodeReference(Mockito.any(NodeReference.class))).thenReturn(mockNormalizedNodeReference);
		Mockito.when(mockReaderService.getMOAttribute(Mockito.any(NormalizableNodeReference.class),
				Mockito.matches(Model.NETWORK_ELEMENT.cmFunction.type()),
				Mockito.matches(Model.NETWORK_ELEMENT.cmFunction.namespace()),
				Mockito.matches(CmFunction.SYNC_STATUS))).thenReturn(mockCMResponse);
		Mockito.when(mockCMResponse.getCmObjects()).thenReturn(Arrays.asList(mockCmObject));
		Mockito.when(mockCmObject.getAttributes()).thenReturn(mockAttributeMap);
		Mockito.when(mockAttributeMap.get(Mockito.matches(CmFunction.SYNC_STATUS))).thenReturn(ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name());
		assertTrue("Node must be synchronized", testObj.isNodeSynchronized(mockNormalizedNodeReference));
	}

}
