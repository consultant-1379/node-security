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
package com.ericsson.nms.security.nscs.service.proxy;

import java.util.Map;

import javax.ejb.*;
import javax.inject.Inject;

import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.*;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.utilities.NSCSComEcimNodeUtility;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.CAEntity;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.WorkflowTaskService;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;

@Stateless
public class NSCSServiceBeanProxyImpl implements NSCSServiceBeanProxy {

    @Inject
    MOActionService moAction;
    
    @Inject
    CppSecurityService securityService;
    
    @Inject
    NscsCMReaderService readerService;
    
    //@Inject
	@EServiceRef
    WorkflowTaskService workflowTaskService;
    
    @Inject
    NSCSComEcimNodeUtility nscsComEcimNodeUtility;
    
    @Inject
    private Logger logger;
    
	@Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean performMOActionByMoFdn(String moFdn,
			MoActionWithoutParameter action) {
		boolean result = false;
		try {
			moAction.performMOActionByMoFdn(moFdn, action);
			result = true;
		} catch (Exception e) {
			logger.error("Exception while performing performMOActionByMoFdn node [{}], action [{}], exception [{}]", moFdn, action.getAction(), e.getMessage());		
		}
		return result;
	}

	@Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean performMOActionByMoFdn(String moFdn,
			MoActionWithParameter action, MoParams params) {
		boolean result = false;

		try {
			moAction.performMOActionByMoFdn(moFdn, action, params);
			result = true;
		} catch (Exception e) {
			logger.error("Exception while performing performMOActionByMoFdn node [{}], action [{}], params [{}], exception [{}]", moFdn, action.getAction(), params, e.getMessage());		
		}
		return result;
	}

	@Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String getTrustDistributionPointUrl(CAEntity caEntity, NormalizableNodeReference nodeRef) {
		String tdpsUrl = null;
		try {
			tdpsUrl = securityService.getTrustDistributionPointUrl(caEntity, nodeRef);
		} catch (CppSecurityServiceException e) {
			logger.error("Exception while performing getTrustDistributionPointUrl caEntity [{}], exception [{}]", caEntity.getCertificateAuthority().getName(), e.getMessage());		
		}
		
		return tdpsUrl;
	}

	@Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public CmResponse getMOAttribute(NodeReference node, String moType,
			String namespace, String attribute) {
		CmResponse response = null;
		
		try {
			response = readerService.getMOAttribute(node,
					moType,
					namespace,
					attribute);
		} catch (Exception e) {
			logger.error("Exception while performing getMOAttribute on node [{}], exception [{}]", node.getFdn(), e.getMessage());		
		}
		
		return response;
	}
	
	@Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void processWorkflowActionTask(WorkflowActionTask actionTask) {
		try {
			workflowTaskService.processTask(actionTask);
		} catch (Exception e) {
			logger.error("Exception while performing processTask for WorkflowActionTask on node [{}], exception [{}]", actionTask.getNodeFdn(), e.getMessage());		
		}
		
	}
	
	@Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String processWorkflowQueryTask(WorkflowQueryTask queryTask) {
		String result = null;
		try {
			result = workflowTaskService.processTask(queryTask);
		} catch (Exception e) {
			logger.error("Exception while performing processTask for WorkflowQueryTask on node [{}], exception [{}]", queryTask.getNodeFdn(), e.getMessage());		
		}
		return result;
	}
	
	@Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Map<String, Object> getAsyncActionProgressAttribute(final String asyncActionProgressAttribute, 
    		final String moFdn){
    	Map<String, Object> result = null;
    	try {
    		result =  nscsComEcimNodeUtility.getAsyncActionProgressAttribute(asyncActionProgressAttribute, moFdn);
		} catch (MissingMoException | UnexpectedErrorException e) {
			logger.error("Exception while performing getAsyncActionProgressAttribute on node [{}], exception [{}]", moFdn, e.getMessage());		
		}
    	return result;
    }
	
	@Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Map<String, Object> getAsyncActionProgressAttribute(final String asyncActionProgressAttribute, 
    		final NormalizableNodeReference normNode, final Mo targetMo) {
    	Map<String, Object> result = null;
    	try {
    		result =  nscsComEcimNodeUtility.getAsyncActionProgressAttribute(asyncActionProgressAttribute, normNode, targetMo);
		} catch (MissingMoException | UnexpectedErrorException e) {
			logger.error("Exception while performing getAsyncActionProgressAttribute on node [{}], exception [{}]", normNode.getFdn(), e.getMessage());		
		}
    	return result;
    }


}
