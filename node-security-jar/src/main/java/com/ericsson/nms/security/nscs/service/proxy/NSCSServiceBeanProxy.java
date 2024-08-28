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

import javax.ejb.Local;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.*;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.CAEntity;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;

/**
 * This is the interface of a proxy bean used to decouple timer handlers from the execution of a task. 
 * This bean will be injected into the timer handlers via @EJB annotation to make a new transaction
 * start. A fail in a task execution causes the rollback of the entire new transaction, but not the timer.
 */
@Local
public interface NSCSServiceBeanProxy {

	/**
	 * @param action the action
	 * @param moFdn teh moFdn
	 * @return 
	 * 			Boolean if action completed without errors
	 * @see MOActionService#performMOActionByMoFdn(String, MoActionWithoutParameter)
	 */
	boolean performMOActionByMoFdn(String moFdn, MoActionWithoutParameter action);
	
	/**
	 * @param moFdn the moFdn
	 * @param action the action
	 * @param params the params
	 * @return 
	 * 			Boolean if action completed without errors
	 * @see MOActionService#performMOActionByMoFdn(String, MoActionWithParameter, MoParams)
	 */
	boolean performMOActionByMoFdn(final String moFdn, final MoActionWithParameter action, final MoParams params);

	/**
	 * @param caEntity the caEntity
	 * @param nodeRef the nodeRef
	 * @return
	 * 			String of TDPS URI or null in case of errors
	 */
	String getTrustDistributionPointUrl(CAEntity caEntity, NormalizableNodeReference nodeRef);
	
	
	/**
	 * @param node the node
	 * @param moType the moType
	 * @param namespace the namespace
	 * @param attribute the attribute
	 * @return
	 * 			CmResponse or null is case of errors
	 * @see NscsCMReaderService#getMOAttribute(NodeReference, String, String, String)
	 */
	CmResponse getMOAttribute(final NodeReference node,
			final String moType, final String namespace, final String attribute);

	/**
	 * @param actionTask the actionTask
	 */
	void processWorkflowActionTask(WorkflowActionTask actionTask);

	/**
	 * @param actionTask the actionTask
	 * @return  the progress workflow
	 */
	String processWorkflowQueryTask(WorkflowQueryTask actionTask);

	/**
	 * @param asyncActionProgressAttribute the asyncActionProgressAttribute
	 * @param moFdn the moFdn
	 * @return the progress action attribute
	 */
	Map<String, Object> getAsyncActionProgressAttribute(
			String asyncActionProgressAttribute, String moFdn);

	/**
	 * @param asyncActionProgressAttribute the asyncActionProgressAttribute
	 * @param normNode the normNode
	 * @param targetMo the targetMo
	 * @return the action progress attribute
	 */
	Map<String, Object> getAsyncActionProgressAttribute(
			String asyncActionProgressAttribute,
			NormalizableNodeReference normNode, Mo targetMo);
}
