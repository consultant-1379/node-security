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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.CbpOiMoNaming;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.CommonLdapConfigurationTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;
import com.ericsson.oss.services.security.nscs.dps.NscsDpsUtils;
import com.ericsson.oss.services.security.nscs.ldap.service.MOLdapService;
import com.ericsson.oss.services.security.nscs.ldap.service.MOLdapServiceType;

@MOLdapServiceType(moLdapServiceType = "EOI")
public class CbpOiMOLdapServiceImpl implements MOLdapService {

    private static final String ONLY_TLS_TRANSPORT_SECURITY_IS_CONFIGURABLE = "Only Tls transport security is configurable on CBP-OI node";
    private static final String MISSING_MO_OF_TYPE_AND_NS_UNDER_MO_FORMAT = "Missing MO of type [%s] and ns [%s] under MO [%s]";
    private static final String MISSING_CHILD_MO_OF_TYPE_UNDER_PARENT_MO_FORMAT = "Missing child MO of type [%s] under parent MO [%s]";
    private static final String FOUND_CHILD_MO_OF_TYPE_UNDER_PARENT_MO_FORMAT = "Found child MO [%s] of type [%s] under parent MO [%s]";
    private static final String NOT_FOUND_CHILD_MO_OF_TYPE_UNDER_PARENT_MO_FORMAT = "Not found child MO of type [%s] under parent MO [%s]";
    private static final String SUCCESSFULLY_UPDATED_MO_WITH_ATTRS_FORMAT = "Successfully updated MO [%s] with attrs [%s]";
    private static final String SUCCESSFULLY_DELETED_MO_UNDER_PARENT_MO_FORMAT = "Successfully deleted MO [%s] under parent MO [%s]";
    private static final String SUCCESSFULLY_CREATED_MO_WITH_ATTRS_FORMAT = "Successfully created MO [%s] with attrs [%s]";

    private static final String LDAPS_MODE = "LDAPS";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsDpsUtils nscsDpsUtils;

    @Override
    public void validateLdapConfiguration(final CommonLdapConfigurationTask task, final NormalizableNodeReference normalizable) {
        final Boolean isTls = task.getIsTls();
        if (!isTls) {
            final String errorMessage = ONLY_TLS_TRANSPORT_SECURITY_IS_CONFIGURABLE;
            nscsLogger.error(task, errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    @Override
    public void ldapConfigure(final CommonLdapConfigurationTask task, final NormalizableNodeReference normalizable) {

        final String unscopedMoType = ModelDefinition.SYSTEM_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_SYSTEM_NS;
        final ManagedObject systemMO = nscsDpsUtils.getNodeHierarchyTopMo(normalizable, refMimNs, unscopedMoType, CbpOiMoNaming.getName(unscopedMoType));
        if (systemMO == null) {
            final String missingMoMessage = String.format(MISSING_MO_OF_TYPE_AND_NS_UNDER_MO_FORMAT, unscopedMoType, refMimNs, normalizable.getFdn());
            nscsLogger.error(task, missingMoMessage);
            throw new MissingMoException(missingMoMessage);
        }

        configureSystemLdapHierarchy(task, normalizable, systemMO);
    }

    /**
     * Configure the system,ldap hierarchy under the given parent system MO for the given node and according to the ldap workflow context of the given
     * task.
     * 
     * If system,ldap MO is present under system MO, it shall be only child MO and in this case configure its hierarchy.
     * 
     * If system,ldap MO is not present, create it and its hierarchy.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param systemMO
     *            the parent system MO.
     */
    private void configureSystemLdapHierarchy(final CommonLdapConfigurationTask task, final NormalizableNodeReference normalizable,
            final ManagedObject systemMO) {

        final String unscopedMoType = ModelDefinition.LDAP_TYPE;

        final ManagedObject systemLdapMO = nscsDpsUtils.getOnlyChildMo(systemMO, normalizable, unscopedMoType);
        if (systemLdapMO != null) {
            final String foundMoMessage = String.format(FOUND_CHILD_MO_OF_TYPE_UNDER_PARENT_MO_FORMAT, systemLdapMO.getFdn(), unscopedMoType,
                    systemMO.getFdn());
            nscsLogger.info(task, foundMoMessage);
            configureSecurityHierarchy(task, normalizable, systemLdapMO);
            configureServersHierarchy(task, normalizable, systemLdapMO);
        } else {
            final String notFoundMoMessage = String.format(NOT_FOUND_CHILD_MO_OF_TYPE_UNDER_PARENT_MO_FORMAT, unscopedMoType, systemMO.getFdn());
            nscsLogger.info(task, notFoundMoMessage);
            createSystemLdapHierarchy(task, normalizable, systemMO);
        }
    }

    /**
     * Configure the security hierarchy under the given parent system,ldap MO for the given node and according to the ldap workflow context of the
     * given task.
     * 
     * The security MO shall be present and unique under the system,ldap MO and in this case update it and its hierarchy.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param systemLdapMO
     *            the parent system,ldap MO.
     * @throws MissingMoException
     *             if the mandatory security MO is missing under the existent system,ldap MO.
     */
    private void configureSecurityHierarchy(final CommonLdapConfigurationTask task, final NormalizableNodeReference normalizable,
            final ManagedObject systemLdapMO) {

        final String unscopedMoType = ModelDefinition.CBP_OI_SECURITY_TYPE;
        ManagedObject securityMO = nscsDpsUtils.getOnlyChildMo(systemLdapMO, normalizable, unscopedMoType);
        if (securityMO == null) {
            final String missingMoMessage = String.format(MISSING_CHILD_MO_OF_TYPE_UNDER_PARENT_MO_FORMAT, unscopedMoType, systemLdapMO.getFdn());
            nscsLogger.error(task, missingMoMessage);
            throw new MissingMoException(missingMoMessage);
        }
        final Map<String, Object> securityAttributes = getSecurityAttributes(task);
        securityMO = nscsDpsUtils.updateMo(securityMO, securityAttributes);
        final String successfulUpdateMsg = String.format(SUCCESSFULLY_UPDATED_MO_WITH_ATTRS_FORMAT, securityMO.getFdn(), securityAttributes);
        nscsLogger.workFlowTaskHandlerOngoing(task, successfulUpdateMsg);

        configureTlsMo(task, normalizable, securityMO);
        configureSimpleAuthenticatedMo(task, normalizable, securityMO);
    }

    /**
     * Get the security MO attributes according to the ldap workflow context of the given task.
     * 
     * @param task
     *            the task.
     * @return the attributes of the security MO.
     */
    private Map<String, Object> getSecurityAttributes(final CommonLdapConfigurationTask task) {
        final Map<String, Object> securityAttributes = new HashMap<>();
        final Map<String, Serializable> ldapWorkflowContext = task.getLdapWorkflowContext();
        final String baseDn = (String) ldapWorkflowContext.get(WorkflowOutputParameterKeys.BASE_DN.toString());
        securityAttributes.put(ModelDefinition.SECURITY_USER_BASE_DN_ATTR, baseDn);
        return securityAttributes;
    }

    /**
     * Configure the tls MO under the given parent security MO for the given node and according to the ldap workflow context of the given task.
     * 
     * If tls MO is present under security MO, it shall be only child MO.
     * 
     * If tls MO is not present under security MO, create it.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param securityMO
     *            the parent security MO.
     */
    private void configureTlsMo(final CommonLdapConfigurationTask task, final NormalizableNodeReference normalizable,
            final ManagedObject securityMO) {

        final String unscopedMoType = ModelDefinition.CBP_OI_TLS_TYPE;
        final ManagedObject tlsMO = nscsDpsUtils.getOnlyChildMo(securityMO, normalizable, unscopedMoType);
        if (tlsMO != null) {
            final String foundMoMessage = String.format(FOUND_CHILD_MO_OF_TYPE_UNDER_PARENT_MO_FORMAT, tlsMO.getFdn(), unscopedMoType,
                    securityMO.getFdn());
            nscsLogger.info(task, foundMoMessage);
        } else {
            final String notFoundMoMessage = String.format(NOT_FOUND_CHILD_MO_OF_TYPE_UNDER_PARENT_MO_FORMAT, unscopedMoType, securityMO.getFdn());
            nscsLogger.info(task, notFoundMoMessage);
            createTlsMo(task, normalizable, securityMO);
        }
    }

    /**
     * Configure the simple-authenticated MO under the given parent security MO for the given node and according to the ldap workflow context of the
     * given task.
     * 
     * If simple-authenticated MO is present under security MO, it shall be only child MO.
     * 
     * If simple-authenticated MO is not present under security MO, create it.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param securityMO
     *            the parent security MO.
     */
    private void configureSimpleAuthenticatedMo(final CommonLdapConfigurationTask task, final NormalizableNodeReference normalizable,
            final ManagedObject securityMO) {

        final String unscopedMoType = ModelDefinition.SIMPLE_AUTHENTICATED_TYPE;
        ManagedObject simpleAuthenticatedMO = nscsDpsUtils.getOnlyChildMo(securityMO, normalizable, unscopedMoType);
        if (simpleAuthenticatedMO != null) {
            final String foundMoMessage = String.format(FOUND_CHILD_MO_OF_TYPE_UNDER_PARENT_MO_FORMAT, simpleAuthenticatedMO.getFdn(), unscopedMoType,
                    securityMO.getFdn());
            nscsLogger.info(task, foundMoMessage);
            final String previousBindDn = simpleAuthenticatedMO.getAttribute(ModelDefinition.SIMPLE_AUTHENTICATED_BIND_DN_ATTR);
            nscsLogger.info(task, "previous bindDn [{}] in MO [{}]", previousBindDn, simpleAuthenticatedMO.getFdn());
            if (previousBindDn != null && !previousBindDn.isEmpty()) {
                final Map<String, Serializable> ldapWorkflowContext = task.getLdapWorkflowContext();
                ldapWorkflowContext.put(WorkflowOutputParameterKeys.PREVIOUS_BIND_DN.toString(), previousBindDn);
                task.setLdapWorkflowContext(ldapWorkflowContext);
            }
            final Map<String, Object> simpleAuthenticatedAttributes = getSimpleAuthenticatedAttributes(task);
            simpleAuthenticatedMO = nscsDpsUtils.updateMo(simpleAuthenticatedMO, simpleAuthenticatedAttributes);
            final String successfulUpdateMsg = String.format(SUCCESSFULLY_UPDATED_MO_WITH_ATTRS_FORMAT, simpleAuthenticatedMO.getFdn(),
                    stringifySimpleAuthenticatedAttributes(simpleAuthenticatedAttributes));
            nscsLogger.workFlowTaskHandlerOngoing(task, successfulUpdateMsg);
        } else {
            final String notFoundMoMessage = String.format(NOT_FOUND_CHILD_MO_OF_TYPE_UNDER_PARENT_MO_FORMAT, unscopedMoType, securityMO.getFdn());
            nscsLogger.info(task, notFoundMoMessage);
            nscsLogger.info(task, "authentication-type is not simple-authenticated, configure simple-authenticated now");
            createSimpleAuthenticatedMo(task, normalizable, securityMO);
        }
    }

    /**
     * Get the simple-authenticated MO attributes according to the ldap workflow context of the given task parameters.
     * 
     * @param task
     *            the task.
     * @return the attributes of the simple-authenticated MO.
     */
    private Map<String, Object> getSimpleAuthenticatedAttributes(final CommonLdapConfigurationTask task) {
        final Map<String, Object> simpleAuthenticatedAttributes = new HashMap<>();
        final Map<String, Serializable> ldapWorkflowContext = task.getLdapWorkflowContext();
        final String bindDn = (String) ldapWorkflowContext.get(WorkflowOutputParameterKeys.BIND_DN.toString());
        final String bindCrd = (String) ldapWorkflowContext.get(WorkflowOutputParameterKeys.BIND_PASSWORD.toString());
        if (bindDn != null) {
            simpleAuthenticatedAttributes.put(ModelDefinition.SIMPLE_AUTHENTICATED_BIND_DN_ATTR, bindDn);
        }
        if (bindCrd != null) {
            simpleAuthenticatedAttributes.put(ModelDefinition.SIMPLE_AUTHENTICATED_BIND_CRD_ATTR, bindCrd);
        }
        return simpleAuthenticatedAttributes;
    }

    /**
     * Stringify the given simple-authenticated MO attributes hiding the bind credentials.
     * 
     * @param simpleAuthenticatedAttributes
     *            the simple-authenticated MO attributes.
     * @return the stringified simple-authenticated MO attributes.
     */
    private String stringifySimpleAuthenticatedAttributes(final Map<String, Object> simpleAuthenticatedAttributes) {
        final Map<String, Object> stringifiedSimpleAuthenticatedAttributes = new HashMap<>(simpleAuthenticatedAttributes);
        stringifiedSimpleAuthenticatedAttributes.put(ModelDefinition.SIMPLE_AUTHENTICATED_BIND_CRD_ATTR, "***");
        return stringifiedSimpleAuthenticatedAttributes.toString();
    }

    /**
     * Configure the servers hierarchy under the given parent system,ldap MO for the given node and according to the ldap workflow context of the
     * given task.
     * 
     * A list of server MOs can be present under the system,ldap MO.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param systemLdapMO
     *            the parent system,ldap MO.
     */
    private void configureServersHierarchy(final CommonLdapConfigurationTask task, final NormalizableNodeReference normalizable,
            final ManagedObject systemLdapMO) {

        final List<ManagedObject> primaryServerMOs = new ArrayList<>();
        final List<ManagedObject> fallbackServerMOs = new ArrayList<>();
        final List<ManagedObject> othersServerMOs = new ArrayList<>();

        final String unscopedMoType = ModelDefinition.SERVER_TYPE;
        List<ManagedObject> serverMOs = nscsDpsUtils.getChildMos(systemLdapMO, normalizable, unscopedMoType);
        for (final ManagedObject serverMO : serverMOs) {
            if (CbpOiMoNaming.getServerName(true).equals(serverMO.getAttribute(ModelDefinition.LDAP_SERVER_NAME_ATTR))) {
                primaryServerMOs.add(serverMO);
            } else if (CbpOiMoNaming.getServerName(false).equals(serverMO.getAttribute(ModelDefinition.LDAP_SERVER_NAME_ATTR))) {
                fallbackServerMOs.add(serverMO);
            } else {
                othersServerMOs.add(serverMO);
            }
        }

        if (primaryServerMOs.size() == 1) {
            configureServerHierarchy(task, normalizable, primaryServerMOs.get(0), true);
        } else {
            createServerHierarchy(task, normalizable, systemLdapMO, true);
        }

        if (fallbackServerMOs.size() == 1) {
            configureServerHierarchy(task, normalizable, fallbackServerMOs.get(0), false);
        } else {
            createServerHierarchy(task, normalizable, systemLdapMO, false);
        }

        if (!othersServerMOs.isEmpty()) {
            nscsLogger.info(task, "Delete unuseful server MOs");
            for (final ManagedObject otherServerMO : othersServerMOs) {
                final String otherServerFdn = otherServerMO.getFdn();
                nscsDpsUtils.deleteMo(otherServerMO);
                final String successfullDeleteMsg = String.format(SUCCESSFULLY_DELETED_MO_UNDER_PARENT_MO_FORMAT, otherServerFdn,
                        systemLdapMO.getFdn());
                nscsLogger.workFlowTaskHandlerOngoing(task, successfullDeleteMsg);
            }
        }
    }

    /**
     * Configure the given server hierarchy for the given node and according to the ldap workflow context of the given task and the given isPrimary
     * flag.
     * 
     * The server MO is immutable so it could not be updated.
     * 
     * In any case no need here to update it since it exists with expected attributes (name).
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param serverMO
     *            the server MO.
     * @param isPrimary
     *            true if primary server, false if fallback server.
     */
    private void configureServerHierarchy(final CommonLdapConfigurationTask task, final NormalizableNodeReference normalizable,
            final ManagedObject serverMO, final Boolean isPrimary) {

        final String foundMoMessage = String.format("Found %s server MO [%s]", isPrimary ? "primary" : "fallback", serverMO.getFdn());
        nscsLogger.info(task, foundMoMessage);

        configureTcpHierarchy(task, normalizable, serverMO, isPrimary);
    }

    /**
     * Configure the tcp hierarchy under the given parent server MO for the given node and according to the ldap workflow context of the given task
     * and to the given isPrimary flag.
     * 
     * The tcp MO shall be present and unique under the server MO and in this case update it and check its hierarchy.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param serverMO
     *            the parent server MO.
     * @param isPrimary
     *            true if primary server, false if fallback server.
     * @throws MissingMoException
     *             if the mandatory tcp MO is missing under the existent server MO.
     */
    private void configureTcpHierarchy(final CommonLdapConfigurationTask task, final NormalizableNodeReference normalizable,
            final ManagedObject serverMO, final Boolean isPrimary) {

        final String unscopedMoType = ModelDefinition.TCP_TYPE;
        ManagedObject tcpMO = nscsDpsUtils.getOnlyChildMo(serverMO, normalizable, unscopedMoType);
        if (tcpMO == null) {
            final String missingMoMessage = String.format(MISSING_CHILD_MO_OF_TYPE_UNDER_PARENT_MO_FORMAT, unscopedMoType, serverMO.getFdn());
            nscsLogger.error(task, missingMoMessage);
            throw new MissingMoException(missingMoMessage);
        }
        final Map<String, Object> tcpAttributes = getTcpAttributes(task, isPrimary);
        tcpMO = nscsDpsUtils.updateMo(tcpMO, tcpAttributes);
        final String successfulUpdateMsg = String.format(SUCCESSFULLY_UPDATED_MO_WITH_ATTRS_FORMAT, tcpMO.getFdn(), tcpAttributes);
        nscsLogger.workFlowTaskHandlerOngoing(task, successfulUpdateMsg);

        final String tlsMode = task.getTlsMode();
        if (LDAPS_MODE.equals(tlsMode)) {
            configureLdapsMo(task, normalizable, tcpMO);
        } else {
            configureTcpLdapMo(task, normalizable, tcpMO);
        }
    }

    /**
     * Get the tcp MO attributes according to the ldap workflow context of the given task and to the given isPrimary flag.
     * 
     * @param task
     *            the task.
     * @param isPrimary
     *            true if primary server, false if fallback server.
     * @return the attributes of the tcp MO.
     */
    private Map<String, Object> getTcpAttributes(final CommonLdapConfigurationTask task, final Boolean isPrimary) {
        final Map<String, Object> tcpAttributes = new HashMap<>();
        final Map<String, Serializable> ldapWorkflowContext = task.getLdapWorkflowContext();
        String address = null;
        if (isPrimary) {
            address = (String) ldapWorkflowContext.get(WorkflowOutputParameterKeys.LDAP_IP_ADDRESS.toString());
        } else {
            address = (String) ldapWorkflowContext.get(WorkflowOutputParameterKeys.FALLBACK_LDAP_IP_ADDRESS.toString());
        }
        if (address != null) {
            tcpAttributes.put(ModelDefinition.TCP_ADDRESS_ATTR, address);
        }
        return tcpAttributes;
    }

    /**
     * Configure the ldaps MO under the given parent tcp MO for the given node and according to the ldap workflow context of the given task.
     * 
     * If ldaps MO is present under tcp MO, it shall be only child MO.
     * 
     * If ldaps MO is not present under tcp MO, create it.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param tcpMO
     *            the parent tcp MO.
     */
    private void configureLdapsMo(final CommonLdapConfigurationTask task, final NormalizableNodeReference normalizable, final ManagedObject tcpMO) {

        final String unscopedMoType = ModelDefinition.LDAPS_TYPE;
        ManagedObject ldapsMO = nscsDpsUtils.getOnlyChildMo(tcpMO, normalizable, unscopedMoType);
        if (ldapsMO != null) {
            final String foundMoMessage = String.format(FOUND_CHILD_MO_OF_TYPE_UNDER_PARENT_MO_FORMAT, ldapsMO.getFdn(), unscopedMoType,
                    tcpMO.getFdn());
            nscsLogger.info(task, foundMoMessage);
            final Map<String, Object> ldapsAttributes = getLdapsAttributes(task);
            ldapsMO = nscsDpsUtils.updateMo(ldapsMO, ldapsAttributes);
            final String successfulUpdateMsg = String.format(SUCCESSFULLY_UPDATED_MO_WITH_ATTRS_FORMAT, ldapsMO.getFdn(), ldapsAttributes);
            nscsLogger.workFlowTaskHandlerOngoing(task, successfulUpdateMsg);
        } else {
            final String notFoundMoMessage = String.format(NOT_FOUND_CHILD_MO_OF_TYPE_UNDER_PARENT_MO_FORMAT, unscopedMoType, tcpMO.getFdn());
            nscsLogger.info(task, notFoundMoMessage);
            createLdapsMo(task, normalizable, tcpMO);
        }
    }

    /**
     * Get the ldaps MO attributes according to the ldap workflow context of the given task.
     * 
     * @param task
     *            the task.
     * @return the attributes of the ldaps MO.
     */
    private Map<String, Object> getLdapsAttributes(final CommonLdapConfigurationTask task) {
        final Map<String, Object> ldapsAttributes = new HashMap<>();
        final Map<String, Serializable> ldapWorkflowContext = task.getLdapWorkflowContext();
        final Integer port = (Integer) ldapWorkflowContext.get(WorkflowOutputParameterKeys.LDAP_SERVER_PORT.toString());
        if (port != null) {
            ldapsAttributes.put(ModelDefinition.LDAPS_PORT_ATTR, port);
        }
        return ldapsAttributes;
    }

    /**
     * Configure the tcp,ldap MO under the given parent tcp MO for the given node and according to the ldap workflow context of the given task.
     * 
     * If tcp,ldap MO is present under tcp MO, it shall be only child MO.
     * 
     * If tcp,ldap MO is not present under tcp MO, create it.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param tcpMO
     *            the parent tcp MO.
     */
    private void configureTcpLdapMo(final CommonLdapConfigurationTask task, final NormalizableNodeReference normalizable, final ManagedObject tcpMO) {

        final String unscopedMoType = ModelDefinition.LDAP_TYPE;

        ManagedObject tcpLdapMO = nscsDpsUtils.getOnlyChildMo(tcpMO, normalizable, unscopedMoType);
        if (tcpLdapMO != null) {
            final String foundMoMessage = String.format(FOUND_CHILD_MO_OF_TYPE_UNDER_PARENT_MO_FORMAT, tcpLdapMO.getFdn(), unscopedMoType,
                    tcpMO.getFdn());
            nscsLogger.info(task, foundMoMessage);
            final Map<String, Object> tcpLdapAttributes = getTcpLdapAttributes(task);
            tcpLdapMO = nscsDpsUtils.updateMo(tcpLdapMO, tcpLdapAttributes);
            final String successfulUpdateMsg = String.format(SUCCESSFULLY_UPDATED_MO_WITH_ATTRS_FORMAT, tcpLdapMO.getFdn(), tcpLdapAttributes);
            nscsLogger.workFlowTaskHandlerOngoing(task, successfulUpdateMsg);
        } else {
            final String notFoundMoMessage = String.format(NOT_FOUND_CHILD_MO_OF_TYPE_UNDER_PARENT_MO_FORMAT, unscopedMoType, tcpMO.getFdn());
            nscsLogger.info(task, notFoundMoMessage);
            createTcpLdapMo(task, normalizable, tcpMO);
        }
    }

    /**
     * Get the tcp,ldap MO attributes according to the ldap workflow context of the given task.
     * 
     * @param task
     *            the task.
     * @return the attributes of the tcp,ldap MO.
     */
    private Map<String, Object> getTcpLdapAttributes(final CommonLdapConfigurationTask task) {
        final Map<String, Object> tcpLdapAttributes = new HashMap<>();
        final Map<String, Serializable> ldapWorkflowContext = task.getLdapWorkflowContext();
        final Integer port = (Integer) ldapWorkflowContext.get(WorkflowOutputParameterKeys.LDAP_SERVER_PORT.toString());
        if (port != null) {
            tcpLdapAttributes.put(ModelDefinition.TCP_LDAP_PORT_ATTR, port);
        }
        return tcpLdapAttributes;
    }

    /**
     * Create the system,ldap MO and its hierarchy under the given parent system MO for the given node and according to the ldap workflow context of
     * the given task.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param systemMO
     *            the parent system MO.
     */
    private void createSystemLdapHierarchy(final CommonLdapConfigurationTask task, final NormalizableNodeReference normalizable,
            final ManagedObject systemMO) {

        final String unscopedMoType = ModelDefinition.LDAP_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_SYSTEM_EXT_NS;
        final ManagedObject systemLdapMO = nscsDpsUtils.createChildMo(systemMO, normalizable, refMimNs, unscopedMoType,
                CbpOiMoNaming.getName(unscopedMoType), null);
        final String successfulCreateMsg = String.format(SUCCESSFULLY_CREATED_MO_WITH_ATTRS_FORMAT, systemLdapMO.getFdn(), null);
        nscsLogger.workFlowTaskHandlerOngoing(task, successfulCreateMsg);

        createSecurityHierarchy(task, normalizable, systemLdapMO);
        createServersHierarchy(task, normalizable, systemLdapMO);
    }

    /**
     * Create the security MO and its hierarchy under the given parent system,ldap MO for the given node and according to the ldap workflow context of
     * the given task.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param systemLdapMO
     *            the parent system,ldap MO.
     */
    private void createSecurityHierarchy(final CommonLdapConfigurationTask task, final NormalizableNodeReference normalizable,
            final ManagedObject systemLdapMO) {

        final String unscopedMoType = ModelDefinition.CBP_OI_SECURITY_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_SYSTEM_EXT_NS;

        final Map<String, Object> securityAttributes = getSecurityAttributes(task);
        final ManagedObject securityMO = nscsDpsUtils.createChildMo(systemLdapMO, normalizable, refMimNs, unscopedMoType,
                CbpOiMoNaming.getName(unscopedMoType), securityAttributes);
        final String successfulCreateMsg = String.format(SUCCESSFULLY_CREATED_MO_WITH_ATTRS_FORMAT, securityMO.getFdn(), securityAttributes);
        nscsLogger.workFlowTaskHandlerOngoing(task, successfulCreateMsg);

        createTlsMo(task, normalizable, securityMO);
        createSimpleAuthenticatedMo(task, normalizable, securityMO);
    }

    /**
     * Create the tls MO under the given parent security MO for the given node and according to the ldap workflow context of the given task.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param securityMO
     *            the parent security MO.
     */
    private void createTlsMo(final CommonLdapConfigurationTask task, final NormalizableNodeReference normalizable, final ManagedObject securityMO) {

        final String unscopedMoType = ModelDefinition.CBP_OI_TLS_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_SYSTEM_EXT_NS;
        final ManagedObject tlsMO = nscsDpsUtils.createChildMo(securityMO, normalizable, refMimNs, unscopedMoType,
                CbpOiMoNaming.getName(unscopedMoType), null);
        final String successfulCreateMsg = String.format(SUCCESSFULLY_CREATED_MO_WITH_ATTRS_FORMAT, tlsMO.getFdn(), null);
        nscsLogger.workFlowTaskHandlerOngoing(task, successfulCreateMsg);
    }

    /**
     * Create the simple-authenticated MO under the given parent security MO for the given node and according to the ldap workflow context of the
     * given task.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param securityMO
     *            the parent security MO.
     */
    private void createSimpleAuthenticatedMo(final CommonLdapConfigurationTask task, final NormalizableNodeReference normalizable,
            final ManagedObject securityMO) {

        final String unscopedoType = ModelDefinition.SIMPLE_AUTHENTICATED_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_SYSTEM_EXT_NS;

        final Map<String, Object> simpleAuthenticatedAttributes = getSimpleAuthenticatedAttributes(task);
        final ManagedObject simpleAuthenticatedMO = nscsDpsUtils.createChildMo(securityMO, normalizable, refMimNs, unscopedoType,
                CbpOiMoNaming.getName(unscopedoType), simpleAuthenticatedAttributes);
        final String successfulCreateMsg = String.format(SUCCESSFULLY_CREATED_MO_WITH_ATTRS_FORMAT, simpleAuthenticatedMO.getFdn(),
                stringifySimpleAuthenticatedAttributes(simpleAuthenticatedAttributes));
        nscsLogger.workFlowTaskHandlerOngoing(task, successfulCreateMsg);
    }

    /**
     * Create the server MOs hierarchy under the given parent system,ldap MO for the given node and according to the ldap workflow context of the
     * given task.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param systemLdapMO
     *            the parent system,ldap MO.
     */
    private void createServersHierarchy(final CommonLdapConfigurationTask task, final NormalizableNodeReference normalizable,
            final ManagedObject systemLdapMO) {

        createServerHierarchy(task, normalizable, systemLdapMO, true);
        createServerHierarchy(task, normalizable, systemLdapMO, false);
    }

    /**
     * Create the server MO and its hierarchy under the given parent system,ldap MO for the given node and according to the ldap workflow context of
     * the given task and to the given isPrimary flag.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param systemLdapMO
     *            the parent system,ldap MO.
     * @param isPrimary
     *            true if primary server, false if fallback server.
     */
    private void createServerHierarchy(final CommonLdapConfigurationTask task, final NormalizableNodeReference normalizable,
            final ManagedObject systemLdapMO, final Boolean isPrimary) {

        final String unscopedMoType = ModelDefinition.SERVER_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_SYSTEM_EXT_NS;

        final Map<String, Object> serverAttributes = getServerAttributes(isPrimary);
        final ManagedObject serverMO = nscsDpsUtils.createChildMo(systemLdapMO, normalizable, refMimNs, unscopedMoType,
                CbpOiMoNaming.getServerName(isPrimary), serverAttributes);
        final String successfulCreateMsg = String.format(SUCCESSFULLY_CREATED_MO_WITH_ATTRS_FORMAT, serverMO.getFdn(), serverAttributes);
        nscsLogger.workFlowTaskHandlerOngoing(task, successfulCreateMsg);

        createTcpHierarchy(task, normalizable, serverMO, isPrimary);
    }

    /**
     * Get the server MO attributes according to the given isPrimary flag.
     * 
     * @param isPrimary
     *            true if primary server, false if fallback server.
     * @return the attributes of the server MO.
     */
    private Map<String, Object> getServerAttributes(final Boolean isPrimary) {

        final Map<String, Object> serverAttributes = new HashMap<>();
        final String serverName = CbpOiMoNaming.getServerName(isPrimary);
        if (serverName != null) {
            serverAttributes.put(ModelDefinition.LDAP_SERVER_NAME_ATTR, serverName);
        }
        return serverAttributes;
    }

    /**
     * Create the tcp MO and its hierarchy under the given parent server MO for the given node and according to the ldap workflow context of the given
     * task and the given isPrimary flag.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param serverMO
     *            the parent server MO.
     * @param isPrimary
     *            true if primary server, false if fallback server.
     */
    private void createTcpHierarchy(final CommonLdapConfigurationTask task, final NormalizableNodeReference normalizable,
            final ManagedObject serverMO, final Boolean isPrimary) {

        final String unscopedMoType = ModelDefinition.TCP_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_SYSTEM_EXT_NS;

        final Map<String, Object> tcpAttributes = getTcpAttributes(task, isPrimary);
        final ManagedObject tcpMO = nscsDpsUtils.createChildMo(serverMO, normalizable, refMimNs, unscopedMoType,
                CbpOiMoNaming.getName(unscopedMoType), tcpAttributes);
        final String successfulCreateMsg = String.format(SUCCESSFULLY_CREATED_MO_WITH_ATTRS_FORMAT, tcpMO.getFdn(), tcpAttributes);
        nscsLogger.workFlowTaskHandlerOngoing(task, successfulCreateMsg);

        final String tlsMode = task.getTlsMode();
        if (LDAPS_MODE.equals(tlsMode)) {
            createLdapsMo(task, normalizable, tcpMO);
        } else {
            createTcpLdapMo(task, normalizable, tcpMO);
        }
    }

    /**
     * Create the ldaps MO under the given parent tcp MO for the given node and according to the ldap workflow context of the given task.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param tcpMO
     *            the parent tcp MO.
     */
    private void createLdapsMo(final CommonLdapConfigurationTask task, final NormalizableNodeReference normalizable, final ManagedObject tcpMO) {

        final String unscopedMoType = ModelDefinition.LDAPS_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_SYSTEM_EXT_NS;

        final Map<String, Object> ldapsAttributes = getLdapsAttributes(task);
        final ManagedObject ldapsMO = nscsDpsUtils.createChildMo(tcpMO, normalizable, refMimNs, unscopedMoType, CbpOiMoNaming.getName(unscopedMoType),
                ldapsAttributes);
        final String successfulCreateMsg = String.format(SUCCESSFULLY_CREATED_MO_WITH_ATTRS_FORMAT, ldapsMO.getFdn(), ldapsAttributes);
        nscsLogger.workFlowTaskHandlerOngoing(task, successfulCreateMsg);
    }

    /**
     * Create the tcp$$ldap MO under the given parent tcp MO for the given node and according to the ldap workflow context of the given task.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param tcpMO
     *            the parent tcp MO.
     */
    private void createTcpLdapMo(final CommonLdapConfigurationTask task, final NormalizableNodeReference normalizable, final ManagedObject tcpMO) {

        final String unscopedMoType = ModelDefinition.LDAP_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_SYSTEM_EXT_NS;

        final Map<String, Object> tcpLdapAttributes = getTcpLdapAttributes(task);
        final ManagedObject tcpLdapMO = nscsDpsUtils.createChildMo(tcpMO, normalizable, refMimNs, unscopedMoType,
                CbpOiMoNaming.getName(unscopedMoType), tcpLdapAttributes);
        final String successfulCreateMsg = String.format(SUCCESSFULLY_CREATED_MO_WITH_ATTRS_FORMAT, tcpLdapMO.getFdn(), tcpLdapAttributes);
        nscsLogger.workFlowTaskHandlerOngoing(task, successfulCreateMsg);
    }

}
