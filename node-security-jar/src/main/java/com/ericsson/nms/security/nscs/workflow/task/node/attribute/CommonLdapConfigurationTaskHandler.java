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

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.ldap.utility.NscsObjectSerializerUtility;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.CommonLdapConfigurationTask;
import com.ericsson.oss.services.security.nscs.ldap.service.MOLdapServiceFactory;
import com.ericsson.oss.services.security.nscs.workflow.task.util.LdapWorkflowHelper;

/**
 * <p>
 * Task handler for WorkflowTaskType.LDAP_CONFIGURATION. Configure LDAP on given node.
 * </p>
 */
@WFTaskType(WorkflowTaskType.LDAP_CONFIGURATION)
@Local(WFTaskHandlerInterface.class)
public class CommonLdapConfigurationTaskHandler implements WFQueryTaskHandler<CommonLdapConfigurationTask>, WFTaskHandlerInterface {

    private static final String DONE = "DONE";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private MOLdapServiceFactory moLdapServiceFactory;

    @Inject
    private LdapWorkflowHelper ldapWorkflowHelper;

    @Inject
    private NscsObjectSerializerUtility nscsObjectSerializerUtility;

    @Override
    public String processTask(final CommonLdapConfigurationTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        try {
            final NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(task.getNode());
            ldapWorkflowHelper.getLdapConfiguration(task, normalizable);
            moLdapServiceFactory.validateLdapConfiguration(task, normalizable);
            moLdapServiceFactory.ldapConfigure(task, normalizable);
        } catch (final Exception e) {
            final String errorMessage = String.format("%s while configuring LDAP on node", NscsLogger.stringifyException(e));
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw e;
        }

        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, DONE);
        return nscsObjectSerializerUtility.serializeResult(task.getLdapWorkflowContext());
    }

}
