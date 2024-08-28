/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.comecim.moaction;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.data.*;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.ldap.utility.LdapConstants;
import com.ericsson.nms.security.nscs.utilities.NSCSComEcimNodeUtility;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.moaction.ComEcimConfigureLdapOnNodeTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * 
 * <p>
 * Task handler for WorkflowTaskType.COM_ECIM_CONFIGURE_LDAP_ACTION
 * </p>
 * <p>
 * Handles the Ldap Configuration Action on ECIM Node
 * </p>
 * 
 * @author xsrirko
 * 
 */

//TODO:Placed the class in the existing package which is not line with Ericsson package guidelines.
@WFTaskType(WorkflowTaskType.COM_ECIM_CONFIGURE_LDAP_ACTION)
@Local(WFTaskHandlerInterface.class)
public class ComEcimConfigureLdapOnNodeTaskHandler implements WFActionTaskHandler<ComEcimConfigureLdapOnNodeTask>, WFTaskHandlerInterface {
    @Inject
    private NscsCMWriterService configurationWriter;

    @Inject
    private NscsCMReaderService configurationReader;

    @Inject
    NodeValidatorUtility nodeValidatorUtility;

    @Inject
    NSCSComEcimNodeUtility nscsComEcimNodeUtility;

    @Inject
    Logger logger;

    @Override
    public void processTask(final ComEcimConfigureLdapOnNodeTask task) {
        logger.debug("Started processing ComEcimConfigureLdapActionTask task : {}", task);

        final String nodeFdn = task.getNodeFdn();
        final NormalizableNodeReference normalizedReference = configurationReader.getNormalizableNodeReference(new NodeRef(nodeFdn));
        final NscsCMWriterService.WriterSpecificationBuilder specification = buildLdapSettingsMo(normalizedReference, task);

        specification.updateMO();
    }

    private NscsCMWriterService.WriterSpecificationBuilder buildLdapSettingsMo(final NormalizableNodeReference normalizedReference, final ComEcimConfigureLdapOnNodeTask task) {

        Map<String, Serializable> ldapWorkFlowContext = task.getLdapWorkFlowContext();

        Map<String, Object> bindPassword = buildBindPasswordObject((String) ldapWorkFlowContext.get(WorkflowParameterKeys.BIND_PASSWORD.toString()));

        final NscsCMWriterService.WriterSpecificationBuilder specification = configurationWriter.withSpecification(nscsComEcimNodeUtility.getLdapMoFdn(normalizedReference));
        specification.setNotNullAttribute(ModelDefinition.Ldap.BASE_DN, ldapWorkFlowContext.get(WorkflowParameterKeys.BASE_DN.toString()));
        specification.setNotNullAttribute(ModelDefinition.Ldap.BIND_DN, ldapWorkFlowContext.get(WorkflowParameterKeys.BIND_DN.toString()));
        specification.setNotNullAttribute(ModelDefinition.Ldap.BIND_PASSWORD, bindPassword);

        specification.setNotNullAttribute(ModelDefinition.Ldap.SERVER_PORT, ldapWorkFlowContext.get(WorkflowParameterKeys.LDAP_SERVER_PORT.toString()));
        specification.setNotNullAttribute(ModelDefinition.Ldap.LDAP_IP_ADDRESS, ldapWorkFlowContext.get(WorkflowParameterKeys.LDAP_IP_ADDRESS.toString()));
        specification.setNotNullAttribute(ModelDefinition.Ldap.FALLBACK_LDAP_IP_ADDRESS, ldapWorkFlowContext.get(WorkflowParameterKeys.FALLBACK_LDAP_IP_ADDRESS.toString()));

        specification.setNotNullAttribute(ModelDefinition.Ldap.TLS_MODE, task.getTlsMode());
        specification.setNotNullAttribute(ModelDefinition.Ldap.USE_TLS, task.getUseTls());

        specification.setNotNullAttribute(ModelDefinition.Ldap.USER_LABEL, task.getUserLabel());

        return specification;

    }

    private Map<String, Object> buildBindPasswordObject(final String bindPassword) {
        Map<String, Object> ldapBindPassword = new HashMap<String, Object>();
        ldapBindPassword.put(LdapConstants.CLEAR_TEXT, true);
        ldapBindPassword.put(LdapConstants.PASSWORD, bindPassword);
        return ldapBindPassword;

    }

}
