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

import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter;

/**
 * Auxiliary class modeling serializable info about a generic MO action without
 * parameters as passed through workflow tasks.
 * 
 * @author emaborz
 *
 */
public class WorkflowMoActionWithoutParams extends WorkflowMoAction {

	private static final long serialVersionUID = -2904655645367865355L;

	/*
	 * The MO action
	 */
	private MoActionWithoutParameter targetAction;

	/**
	 * @param targetMoFdn
	 * @param targetAction
	 */
	public WorkflowMoActionWithoutParams(final String targetMoFdn, final MoActionWithoutParameter targetAction) {
		super(targetMoFdn);
		this.targetAction = targetAction;
	}

	/**
	 * @param targetMoFdn
	 * @param targetAction
	 * @param maxPollTimes
	 */
	public WorkflowMoActionWithoutParams(final String targetMoFdn, final MoActionWithoutParameter targetAction,
			final int maxPollTimes) {
		super(targetMoFdn, maxPollTimes);
		this.targetAction = targetAction;
	}

	/**
	 * @return the targetAction
	 */
	public MoActionWithoutParameter getTargetAction() {
		return targetAction;
	}

	/**
	 * @param targetAction
	 *            the targetAction to set
	 */
	public void setTargetAction(final MoActionWithoutParameter targetAction) {
		this.targetAction = targetAction;
	}

}
