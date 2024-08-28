/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2023
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.node.attribute;

import java.io.Serializable;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.NscsLdapProxyException;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.DeleteLdapProxyAccountTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;
import com.ericsson.oss.services.security.nscs.workflow.task.util.LdapWorkflowHelper;

/**
 * <p>
 * Task handler for WorkflowTaskType.DELETE_LDAP_PROXY_ACCOUNT. Delete LDAP Proxy Account previously configured on given node.
 * </p>
 */
@WFTaskType(WorkflowTaskType.DELETE_LDAP_PROXY_ACCOUNT)
@Local(WFTaskHandlerInterface.class)
public class DeleteLdapProxyAccountTaskHandler implements WFQueryTaskHandler<DeleteLdapProxyAccountTask>, WFTaskHandlerInterface {

    private static final String SUCCESSFULLY_DELETED_PREVIOUS_PROXY_ACCOUNT = "Successfully deleted previous proxy account [%s]";
    private static final String FAILED_DELETE_NOT_EXISTENT_PREVIOUS_PROXY_ACCOUNT = "Failed delete of not existent previous proxy account [%s]";

    private static final String PREVIOUS_BIND_DN_DELETED = "PREVIOUS_BIND_DN_DELETED";
    private static final String PREVIOUS_BIND_DN_NOT_DELETED = "PREVIOUS_BIND_DN_NOT_DELETED";
    private static final String NO_PREVIOUS_BIND_DN_TO_DELETE = "NO_PREVIOUS_BIND_DN_TO_DELETE";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private LdapWorkflowHelper ldapWorkflowHelper;

    @Override
    public String processTask(final DeleteLdapProxyAccountTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        String result = null;
        final Boolean isRenew = task.getIsRenew();
        nscsLogger.info(task, "From task : isRenew [{}]", isRenew);
        final Map<String, Serializable> ldapWorkflowContext = task.getLdapWorkflowContext();
        final String previousBindDn = (String) ldapWorkflowContext.get(WorkflowOutputParameterKeys.PREVIOUS_BIND_DN.toString());
        nscsLogger.info(task, "From task : previousBindDn [{}]", previousBindDn);
        if (previousBindDn != null && !previousBindDn.isEmpty()) {
            if (isRenew) {
                final Boolean isDeleted = ldapWorkflowHelper.deleteProxyAccount(previousBindDn);
                if (isDeleted) {
                    final String successfulDeleteMsg = String.format(SUCCESSFULLY_DELETED_PREVIOUS_PROXY_ACCOUNT, previousBindDn);
                    nscsLogger.workFlowTaskHandlerOngoing(task, successfulDeleteMsg);
                    result = PREVIOUS_BIND_DN_DELETED;
                } else {
                    final String failedDeleteMsg = String.format(FAILED_DELETE_NOT_EXISTENT_PREVIOUS_PROXY_ACCOUNT, previousBindDn);
                    nscsLogger.workFlowTaskHandlerFinishedWithError(task, failedDeleteMsg);
                    throw new NscsLdapProxyException(failedDeleteMsg);
                }
            } else {
                nscsLogger.info(task, "Previous proxy account [{}] not deleted", previousBindDn);
                result = PREVIOUS_BIND_DN_NOT_DELETED;
            }
        } else {
            nscsLogger.info(task, "No previous proxy account [{}] to delete", previousBindDn);
            result = NO_PREVIOUS_BIND_DN_TO_DELETE;
        }
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "Successfully performed.", result);
        return result;
    }

}
