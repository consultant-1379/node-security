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

import java.io.Serializable;

/**
 * Auxiliary abstract class modeling serializable info about a generic MO action
 * as passed through workflow tasks.
 * 
 * @author emaborz
 *
 */
public abstract class WorkflowMoAction implements Serializable {

	private static final long serialVersionUID = -7415976034761478446L;

	/**
	 * The FDN of the MO on which action is performed.
	 */
	private String targetMoFdn;

	/**
	 * The action state as known by workflows.
	 */
	private WorkflowMoActionState state;

	/**
	 * The max number of poll times checking the action progress.
	 */
	private int maxPollTimes;

	/**
	 * The remaining number of poll times checking the action progress.
	 */
	private int remainingPollTimes;

	/**
	 * @param targetMoFdn
	 */
	public WorkflowMoAction(final String targetMoFdn) {
		super();
		this.targetMoFdn = targetMoFdn;
		this.state = WorkflowMoActionState.PENDING;
		this.maxPollTimes = 1;
		this.remainingPollTimes = 1;
	}

	/**
	 * @param targetMoFdn
	 * @param maxPollTimes
	 */
	public WorkflowMoAction(final String targetMoFdn, final int maxPollTimes) {
		super();
		this.targetMoFdn = targetMoFdn;
		this.state = WorkflowMoActionState.PENDING;
		this.maxPollTimes = maxPollTimes;
		this.remainingPollTimes = maxPollTimes;
	}

	/**
	 * @param targetMoFdn
	 * @param state
	 */
	public WorkflowMoAction(final String targetMoFdn, final WorkflowMoActionState state) {
		super();
		this.targetMoFdn = targetMoFdn;
		this.state = state;
		this.maxPollTimes = 1;
		this.remainingPollTimes = 1;
	}

	/**
	 * @return the targetMoFdn
	 */
	public String getTargetMoFdn() {
		return targetMoFdn;
	}

	/**
	 * @param targetMoFdn
	 *            the targetMoFdn to set
	 */
	public void setTargetMoFdn(final String targetMoFdn) {
		this.targetMoFdn = targetMoFdn;
	}

	/**
	 * @return the state
	 */
	public WorkflowMoActionState getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(final WorkflowMoActionState state) {
		this.state = state;
	}

	/**
	 * @return the maxPollTimes
	 */
	public int getMaxPollTimes() {
		return maxPollTimes;
	}

	/**
	 * @param maxPollTimes
	 *            the maxPollTimes to set
	 */
	public void setMaxPollTimes(final int maxPollTimes) {
		this.maxPollTimes = maxPollTimes;
	}

	/**
	 * @return the remainingPollTimes
	 */
	public int getRemainingPollTimes() {
		return remainingPollTimes;
	}

	/**
	 * @param remainingPollTimes
	 *            the remainingPollTimes to set
	 */
	public void setRemainingPollTimes(final int remainingPollTimes) {
		this.remainingPollTimes = remainingPollTimes;
	}

}
