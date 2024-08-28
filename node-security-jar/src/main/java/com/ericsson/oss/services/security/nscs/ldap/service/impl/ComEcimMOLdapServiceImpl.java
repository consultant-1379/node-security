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
package com.ericsson.oss.services.security.nscs.ldap.service.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.ldap.utility.LdapConstants;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.CommonLdapConfigurationTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;
import com.ericsson.oss.services.security.nscs.dps.NscsDpsUtils;
import com.ericsson.oss.services.security.nscs.ldap.service.MOLdapService;
import com.ericsson.oss.services.security.nscs.ldap.service.MOLdapServiceType;

@MOLdapServiceType(moLdapServiceType = "ECIM")
public class ComEcimMOLdapServiceImpl implements MOLdapService {

    private static final String FOUND_MO_OF_TYPE_AND_NS_UNDER_MO_FORMAT = "Found MO [%s] of type [%s] and ns [%s] under MO [%s]";
    private static final String MISSING_MO_OF_TYPE_AND_NS_UNDER_MO_FORMAT = "Missing MO of type [%s] and ns [%s] under MO [%s]";
    private static final String SUCCESSFULLY_UPDATED_MO_WITH_ATTRS_FORMAT = "Successfully updated MO [%s] with attrs [%s]";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsDpsUtils nscsDpsUtils;

    @Override
    public void validateLdapConfiguration(final CommonLdapConfigurationTask task, final NormalizableNodeReference normalizable) {
        // no extra validation needed for COM/ECIM nodes
    }

    @Override
    public void ldapConfigure(final CommonLdapConfigurationTask task, final NormalizableNodeReference normalizable) {

        final String unscopedMoType = ModelDefinition.ECIM_LDAP_TYPE;
        final String refMimNs = ModelDefinition.REF_MIM_NS_ECIM_LDAP_AUTHENTICATION;
        ManagedObject ldapMO = nscsDpsUtils.getOnlyMO(normalizable, refMimNs, unscopedMoType);
        if (ldapMO != null) {
            final String foundMessage = String.format(FOUND_MO_OF_TYPE_AND_NS_UNDER_MO_FORMAT, ldapMO.getFdn(), unscopedMoType, refMimNs,
                    normalizable.getFdn());
            nscsLogger.info(task, foundMessage);
            final String previousBindDn = ldapMO.getAttribute(ModelDefinition.Ldap.BIND_DN);
            nscsLogger.info(task, "previous bindDn [{}] in MO [{}]", previousBindDn, ldapMO.getFdn());
            if (previousBindDn != null && !previousBindDn.isEmpty()) {
                final Map<String, Serializable> ldapWorkflowContext = task.getLdapWorkflowContext();
                ldapWorkflowContext.put(WorkflowOutputParameterKeys.PREVIOUS_BIND_DN.toString(), previousBindDn);
                task.setLdapWorkflowContext(ldapWorkflowContext);
            }
            final Map<String, Object> ldapAttributes = getLdapAttributes(task);
            ldapMO = nscsDpsUtils.updateMo(ldapMO, ldapAttributes);
            final String successfulUpdateMsg = String.format(SUCCESSFULLY_UPDATED_MO_WITH_ATTRS_FORMAT, ldapMO.getFdn(),
                    stringifyLdapAttributes(ldapAttributes));
            nscsLogger.workFlowTaskHandlerOngoing(task, successfulUpdateMsg);
        } else {
            final String missingMoMessage = String.format(MISSING_MO_OF_TYPE_AND_NS_UNDER_MO_FORMAT, unscopedMoType, refMimNs, normalizable.getFdn());
            nscsLogger.error(task, missingMoMessage);
            throw new MissingMoException(missingMoMessage);
        }
    }

    /**
     * Get the Ldap MO attributes according to the ldap workflow context of the given task.
     * 
     * @param task
     *            the task.
     * @return the attributes of the Ldap MO.
     */
    private Map<String, Object> getLdapAttributes(final CommonLdapConfigurationTask task) {

        final Map<String, Object> ldapAttributes = new HashMap<>();
        final Map<String, Serializable> ldapWorkflowContext = task.getLdapWorkflowContext();
        setNotNullAttribute(ldapAttributes, ModelDefinition.Ldap.BASE_DN,
                (String) ldapWorkflowContext.get(WorkflowOutputParameterKeys.BASE_DN.toString()));
        setNotNullAttribute(ldapAttributes, ModelDefinition.Ldap.BIND_DN,
                (String) ldapWorkflowContext.get(WorkflowOutputParameterKeys.BIND_DN.toString()));
        setNotNullAttribute(ldapAttributes, ModelDefinition.Ldap.BIND_PASSWORD,
                buildBindCrd((String) ldapWorkflowContext.get(WorkflowOutputParameterKeys.BIND_PASSWORD.toString())));
        setNotNullAttribute(ldapAttributes, ModelDefinition.Ldap.SERVER_PORT,
                (Integer) ldapWorkflowContext.get(WorkflowOutputParameterKeys.LDAP_SERVER_PORT.toString()));
        setNotNullAttribute(ldapAttributes, ModelDefinition.Ldap.LDAP_IP_ADDRESS,
                (String) ldapWorkflowContext.get(WorkflowOutputParameterKeys.LDAP_IP_ADDRESS.toString()));
        setNotNullAttribute(ldapAttributes, ModelDefinition.Ldap.FALLBACK_LDAP_IP_ADDRESS,
                (String) ldapWorkflowContext.get(WorkflowOutputParameterKeys.FALLBACK_LDAP_IP_ADDRESS.toString()));
        setNotNullAttribute(ldapAttributes, ModelDefinition.Ldap.TLS_MODE, task.getTlsMode());
        setNotNullAttribute(ldapAttributes, ModelDefinition.Ldap.USE_TLS, task.getIsTls());
        setNotNullAttribute(ldapAttributes, ModelDefinition.Ldap.USER_LABEL, task.getUserLabel());
        return ldapAttributes;
    }

    /**
     * Add in the given attributes map the given key-value entry if value is not null.
     * 
     * @param attributes
     *            the attributes map.
     * @param key
     *            the key.
     * @param value
     *            the value.
     */
    private void setNotNullAttribute(final Map<String, Object> attributes, final String key, final Object value) {
        if (value != null) {
            attributes.put(key, value);
        }
    }

    /**
     * Build ECIM structure for credential.
     * 
     * @param bindCrd
     *            the credential value.
     * @return the ECIM structure for credential.
     */
    private Map<String, Object> buildBindCrd(final String bindCrd) {
        final Map<String, Object> ldapBindCrd = new HashMap<>();
        ldapBindCrd.put(LdapConstants.CLEAR_TEXT, true);
        ldapBindCrd.put(LdapConstants.PASSWORD, bindCrd);
        return ldapBindCrd;

    }

    /**
     * Stringify the given Ldap MO attributes hiding the bind credentials.
     * 
     * @param ldapAttributes
     *            the LDAP MO attributes.
     * @return the stringified Ldap MO attributes.
     */
    private String stringifyLdapAttributes(final Map<String, Object> ldapAttributes) {
        final Map<String, Object> stringifiedLdapAttributes = new HashMap<>(ldapAttributes);
        stringifiedLdapAttributes.put(ModelDefinition.Ldap.BIND_PASSWORD, "***");
        return stringifiedLdapAttributes.toString();
    }
}
