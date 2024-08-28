/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
//TODO: This package is not following the Ericsson package naming conventions.
package com.ericsson.nms.security.nscs.workflow.task.comecim.attribute;

import java.io.Serializable;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.ldap.control.IdentityManagementProxy;
import com.ericsson.nms.security.nscs.ldap.control.PlatformSpecificConfigurationProvider;
import com.ericsson.nms.security.nscs.ldap.utility.NscsObjectSerializerUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountData;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimGetNodeSpecificLdapConfigurationTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * 
 * <p>
 * Task handler for WorkflowTaskType.COM_ECIM_GET_LDAP_NODE_CONFIG
 * </p>
 * <p>
 * Gets the Node Specific Ldap Configuration
 * </p>
 * 
 * @author xsrirko
 * 
 */
//TODO:Placed the class in the existing package which is not line with Ericsson package guidelines.
@WFTaskType(WorkflowTaskType.COM_ECIM_GET_LDAP_NODE_CONFIG)
@Local(WFTaskHandlerInterface.class)
public class ComEcimNodeSpecificLdapConfigTaskHandler implements WFQueryTaskHandler<ComEcimGetNodeSpecificLdapConfigurationTask>, WFTaskHandlerInterface {

    @Inject
    private IdentityManagementProxy identityManagementProxy;

    @Inject
    private PlatformSpecificConfigurationProvider platformSpecificConfigurationProvider;

    @Inject
    private NscsObjectSerializerUtility nscsObjectSerializerUtility;

    @Inject
    private Logger logger;

    @Override
    public String processTask(final ComEcimGetNodeSpecificLdapConfigurationTask task) {
        logger.debug("Started processing ComEcimGetLdapNodeConfigurationTask task : {}", task);

        final ProxyAgentAccountData proxyAgentAccountData = identityManagementProxy.createProxyAgentAccount();

        final Map<String, Serializable> ldapWorkFlowContext = task.getLdapWorkFlowContext();
        ldapWorkFlowContext.put(WorkflowParameterKeys.BIND_DN.toString(), proxyAgentAccountData.getUserDN());
        ldapWorkFlowContext.put(WorkflowParameterKeys.BIND_PASSWORD.toString(), proxyAgentAccountData.getUserPassword());
        ldapWorkFlowContext.put(WorkflowParameterKeys.BASE_DN.toString(), platformSpecificConfigurationProvider.getBaseDN());

        return nscsObjectSerializerUtility.serializeResult(ldapWorkFlowContext);
    }

}
