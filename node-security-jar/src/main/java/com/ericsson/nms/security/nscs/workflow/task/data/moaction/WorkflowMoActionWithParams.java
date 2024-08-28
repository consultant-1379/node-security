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
package com.ericsson.nms.security.nscs.workflow.task.data.moaction;

import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;

/**
 * Auxiliary class modeling serializable info about a generic MO action with
 * parameters as passed through workflow tasks.
 * 
 * @author emaborz
 *
 */
public class WorkflowMoActionWithParams extends WorkflowMoAction {

	private static final long serialVersionUID = -4344829204984243675L;

	/**
	 * The MO action
	 */
	private MoActionWithParameter targetAction;

	/**
	 * The MO action parameters
	 */
	private WorkflowMoParams targetActionParams;

	/**
	 * @param targetMoFdn
	 * @param targetAction
	 */
	public WorkflowMoActionWithParams(final String targetMoFdn, final MoActionWithParameter targetAction) {
		super(targetMoFdn);
		this.targetAction = targetAction;
		this.targetActionParams = null;
	}

	/**
	 * @param targetMoFdn
	 * @param targetAction
	 * @param maxPollTimes
	 */
	public WorkflowMoActionWithParams(final String targetMoFdn, final MoActionWithParameter targetAction,
			final WorkflowMoParams targetActionParams, final int maxPollTimes) {
		super(targetMoFdn, maxPollTimes);
		this.targetAction = targetAction;
		this.targetActionParams = targetActionParams;
	}

	/**
	 * @return the targetAction
	 */
	public MoActionWithParameter getTargetAction() {
		return targetAction;
	}

	/**
	 * @param targetAction
	 *            the targetAction to set
	 */
	public void setTargetAction(final MoActionWithParameter targetAction) {
		this.targetAction = targetAction;
	}

	/**
	 * @return the targetActionParams
	 */
	public WorkflowMoParams getTargetActionParams() {
		return targetActionParams;
	}

	/**
	 * @param targetActionParams
	 *            the targetActionParams to set
	 */
	public void setTargetActionParams(final WorkflowMoParams targetActionParams) {
		this.targetActionParams = targetActionParams;
	}

}
