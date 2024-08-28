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
package com.ericsson.nms.security.nscs.workflow.task.comecim.attribute;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.ldap.control.LdapConfigurationProvider;
import com.ericsson.nms.security.nscs.ldap.utility.NscsObjectSerializerUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimGetLdapCommonConfigurationTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * 
 * <p>
 * Task handler for WorkflowTaskType.COM_ECIM_GET_LDAP_COMMON_CONFIG
 * </p>
 * <p>
 * Gets the Common Ldap Configuration
 * </p>
 * 
 * @author xsrirko
 * 
 */
//TODO:Placed the class in the existing package which is not line with Ericsson package guidelines.
@WFTaskType(WorkflowTaskType.COM_ECIM_GET_LDAP_COMMON_CONFIG)
@Local(WFTaskHandlerInterface.class)
public class ComEcimLdapCommonConfigTaskHandler implements WFQueryTaskHandler<ComEcimGetLdapCommonConfigurationTask>, WFTaskHandlerInterface {

    @Inject
    private NscsObjectSerializerUtility nscsObjectSerializerUtility;

    @Inject
    private LdapConfigurationProvider ldapConfigurationProvider;

    @Inject
    private NscsCMReaderService configurationReader;

    @Inject
    private Logger logger;

    @Override
    public String processTask(final ComEcimGetLdapCommonConfigurationTask task) {

        logger.debug("Started processing ComEcimGetLdapCommonConfigurationTask task : {}", task);

        final String nodeFdn = task.getNodeFdn();
        final NormalizableNodeReference normalizedReference = configurationReader.getNormalizedNodeReference(new NodeRef(nodeFdn));

        Map<String, Serializable> ldapWorkFlowContext = new HashMap<String, Serializable>();
        ldapWorkFlowContext.put(WorkflowParameterKeys.LDAP_IP_ADDRESS.toString(), ldapConfigurationProvider.getPrimaryLdapServerIPAddress(normalizedReference));
        ldapWorkFlowContext.put(WorkflowParameterKeys.FALLBACK_LDAP_IP_ADDRESS.toString(), ldapConfigurationProvider.getSecondaryLdapServerIPAddress(normalizedReference));
        ldapWorkFlowContext.put(WorkflowParameterKeys.LDAP_SERVER_PORT.toString(), ldapConfigurationProvider.getLdapPort(task.getUseTls(), task.getTlsMode()));

        return nscsObjectSerializerUtility.serializeResult(ldapWorkFlowContext);
    }

}
