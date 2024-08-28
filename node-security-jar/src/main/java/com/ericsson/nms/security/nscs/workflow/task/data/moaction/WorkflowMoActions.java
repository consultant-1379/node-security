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
import java.util.LinkedList;
import java.util.List;

/**
 * Auxiliary class modeling serializable info about a list of MO actions as
 * passed through workflow tasks.
 * 
 * Two main scenarios are managed: perform MO actions and check their progress
 * or check if a specific MO action is ongoing.
 * 
 * In perform and check scenario: a 'preparing' workflow task builds the list of
 * MO actions to be performed (with or without parameters) adding them as
 * PENDING to this object. The 'performing' workflow task gets the first PENDING
 * MO action, performs it (with or without parameters) and sets the entry to
 * ONGOING. The 'checking' workflow task search for the ONGOING entry in the
 * list and check its progress. If action is successfully finished, removes the
 * entry from the list. If action failed, aborts the process. If action is
 * ongoing and the max number of check loops has not been yet exceeded, checks
 * again after a timeout. If action is ongoing and the max number of check loops
 * has been exceeded, aborts the process.
 * 
 * In check scenario: a 'preparing' workflow task builds the list of MO actions
 * to be checked adding them as CHECK_IT to this object. The 'checking' workflow
 * task search for the CHECK_IT entry in the list and check its progress. If
 * action is ongoing, returns such info.
 * 
 * @author emaborz
 *
 */
public class WorkflowMoActions implements Serializable {

    private static final long serialVersionUID = -3937792118473517176L;

    /**
     * The MO actions.
     */
    private List<WorkflowMoAction> targetActions;

    public WorkflowMoActions() {
        super();
        this.targetActions = new LinkedList<WorkflowMoAction>();
    }

    /**
     * @param targetActions
     */
    public WorkflowMoActions(List<WorkflowMoAction> targetActions) {
        super();
        this.targetActions = targetActions;
    }

    /**
     * @return the targetActions
     */
    public List<WorkflowMoAction> getTargetActions() {
        return targetActions;
    }

    /**
     * @param targetActions
     *            the targetActions to set
     */
    public void setTargetActions(List<WorkflowMoAction> targetActions) {
        this.targetActions = targetActions;
    }

    /**
     * @param targetAction
     *            the targetAction to add
     */
    public void addTargetAction(WorkflowMoAction targetAction) {

        if (this.targetActions == null) {
            this.targetActions = new LinkedList<WorkflowMoAction>();
        }
        this.targetActions.add(targetAction);
    }

}
