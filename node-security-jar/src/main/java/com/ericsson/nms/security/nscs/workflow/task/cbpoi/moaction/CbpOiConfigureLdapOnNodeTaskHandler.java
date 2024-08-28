/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.cbpoi.moaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.CbpOiMoNaming;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.moaction.CbpOiConfigureLdapOnNodeTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;
import com.ericsson.oss.services.security.nscs.dps.NscsDpsUtils;
import javax.ejb.Local;

@WFTaskType(WorkflowTaskType.CBPOI_CONFIGURE_LDAP_ACTION)
@Local(WFTaskHandlerInterface.class)
public class CbpOiConfigureLdapOnNodeTaskHandler implements WFActionTaskHandler<CbpOiConfigureLdapOnNodeTask>, WFTaskHandlerInterface {

    private static final String FOUND_MO_OF_TYPE_UNDER_PARENT_MO = "Found MO [{}] of type [{}] under parent MO [{}]";
    private static final String NOT_FOUND_MO_OF_TYPE_UNDER_PARENT_MO = "Not found MO of type [{}] under parent MO [{}]";
    private static final String SUCCESSFULLY_UPDATED_MO_WITH_ATTRS = "Successfully updated MO [{}] with attrs [{}]";
    private static final String SUCCESSFULLY_CREATED_MO_WITH_ATTRS = "Successfully created MO [{}] with attrs [{}]";

    private static final String VALID = "VALID";
    private static final String LDAPS_MODE = "LDAPS";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsDpsUtils nscsDpsUtils;

    @Override
    public void processTask(final CbpOiConfigureLdapOnNodeTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        try {
            validateInputParams(task);

            final NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(task.getNode());

            checkLdapConfiguration(task, normalizable);

        } catch (final Exception e) {
            final String errorMessage = NscsLogger.stringifyException(e);
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw e;
        }

        final String successMessage = String.format("Successfully completed : Ldap state is [%s]", VALID);
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, successMessage);
    }

    /**
     * Validates the task parameters throwing an exception if check fails.
     * 
     * @param task
     *            the task.
     * @throws IllegalArgumentException
     *             if task parameters are not valid.
     */
    private void validateInputParams(final CbpOiConfigureLdapOnNodeTask task) {
        final Boolean useTls = task.getUseTls();
        if (!useTls) {
            final String errorMessage = "Only Tls transport security is configurable on CBP-OI node";
            nscsLogger.error(task, errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        final Map<String, Serializable> ldapWorkFlowContext = task.getLdapWorkFlowContext();
        final String baseDn = (String) ldapWorkFlowContext.get(WorkflowParameterKeys.BASE_DN.toString());
        if (baseDn == null || baseDn.isEmpty()) {
            final String errorMessage = String.format("Wrong base DN [%s]", baseDn);
            nscsLogger.error(task, errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Checks the LDAP configuration for the given node and according to the given task.
     * 
     * The system MO shall be always present under node root MO.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @throws MissingMoException
     *             if mandatory system MO is missing.
     */
    private void checkLdapConfiguration(final CbpOiConfigureLdapOnNodeTask task, final NormalizableNodeReference normalizable) {

        final String moType = ModelDefinition.SYSTEM_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_SYSTEM_NS;
        final ManagedObject systemMO = nscsDpsUtils.getNodeHierarchyTopMo(normalizable, refMimNs, moType, CbpOiMoNaming.getName(moType));
        if (systemMO == null) {
            final String errorMessage = String.format("Missing MO of type [%s] and ns [%s] for node [%s]", moType, refMimNs, normalizable);
            nscsLogger.error(task, errorMessage);
            throw new MissingMoException(errorMessage);
        }

        checkSystemLdapHierarchy(task, normalizable, systemMO);
    }

    /**
     * Checks the system,ldap hierarchy under the given parent system MO for the given node and according to the given task.
     * 
     * If system,ldap MO is present under system MO, it shall be only child MO and in this case checks its hierarchy.
     * 
     * If system,ldap MO is not present, it creates it and its hierarchy.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param systemMO
     *            the parent system MO.
     */
    private void checkSystemLdapHierarchy(final CbpOiConfigureLdapOnNodeTask task, final NormalizableNodeReference normalizable,
            final ManagedObject systemMO) {

        final String moType = ModelDefinition.LDAP_TYPE;

        final ManagedObject systemLdapMO = nscsDpsUtils.getOnlyChildMo(systemMO, normalizable, moType);
        if (systemLdapMO != null) {
            nscsLogger.info(task, FOUND_MO_OF_TYPE_UNDER_PARENT_MO, systemLdapMO.getFdn(), moType, systemMO.getFdn());
            checkSecurityHierarchy(task, normalizable, systemLdapMO);
            checkServersHierarchy(task, normalizable, systemLdapMO);
        } else {
            nscsLogger.info(task, NOT_FOUND_MO_OF_TYPE_UNDER_PARENT_MO, moType, systemMO.getFdn());
            createSystemLdapHierarchy(task, normalizable, systemMO);
        }
    }

    /**
     * Checks the security hierarchy under the given parent system,ldap MO for the given node and according to the given task.
     * 
     * The security MO shall be present and unique under the system,ldap MO and in this case updates it and checks its hierarchy.
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
    private void checkSecurityHierarchy(final CbpOiConfigureLdapOnNodeTask task, final NormalizableNodeReference normalizable,
            final ManagedObject systemLdapMO) {

        final String moType = ModelDefinition.CBP_OI_SECURITY_TYPE;
        ManagedObject securityMO = nscsDpsUtils.getOnlyChildMo(systemLdapMO, normalizable, moType);
        if (securityMO == null) {
            final String errorMessage = String.format("Missing MO of type [%s] under parent MO [%s]", moType, systemLdapMO.getFdn());
            nscsLogger.error(task, errorMessage);
            throw new MissingMoException(errorMessage);
        }
        final Map<String, Object> securityAttributes = getSecurityAttributes(task);
        securityMO = nscsDpsUtils.updateMo(securityMO, securityAttributes);
        nscsLogger.info(task, SUCCESSFULLY_UPDATED_MO_WITH_ATTRS, securityMO.getFdn(), securityAttributes);

        checkTlsMo(task, normalizable, securityMO);
        checkSimpleAuthenticatedMo(task, normalizable, securityMO);
    }

    /**
     * Gets the security MO attributes according to the given task parameters.
     * 
     * @param task
     *            the task.
     * @return the attributes of the security MO.
     */
    private Map<String, Object> getSecurityAttributes(final CbpOiConfigureLdapOnNodeTask task) {
        final Map<String, Object> securityAttributes = new HashMap<>();
        final Map<String, Serializable> ldapWorkFlowContext = task.getLdapWorkFlowContext();
        final String baseDn = (String) ldapWorkFlowContext.get(WorkflowParameterKeys.BASE_DN.toString());
        securityAttributes.put(ModelDefinition.SECURITY_USER_BASE_DN_ATTR, baseDn);
        return securityAttributes;
    }

    /**
     * Checks the tls MO under the given parent security MO for the given node and according to the given task.
     * 
     * If tls MO is present under security MO, it shall be only child MO.
     * 
     * If tls MO is not present under security MO, creates it.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param securityMO
     *            the parent security MO.
     */
    private void checkTlsMo(final CbpOiConfigureLdapOnNodeTask task, final NormalizableNodeReference normalizable, final ManagedObject securityMO) {

        final String moType = ModelDefinition.CBP_OI_TLS_TYPE;
        final ManagedObject tlsMO = nscsDpsUtils.getOnlyChildMo(securityMO, normalizable, moType);
        if (tlsMO != null) {
            nscsLogger.info(task, FOUND_MO_OF_TYPE_UNDER_PARENT_MO, tlsMO.getFdn(), moType, securityMO.getFdn());
        } else {
            nscsLogger.info(task, NOT_FOUND_MO_OF_TYPE_UNDER_PARENT_MO, moType, securityMO.getFdn());
            createTlsMo(task, normalizable, securityMO);
        }
    }

    /**
     * Checks the simple-authenticated MO under the given parent security MO for the given node and according to the given task.
     * 
     * If simple-authenticated MO is present under security MO, it shall be only child MO.
     * 
     * If simple-authenticated MO is not present under security MO, creates it.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param securityMO
     *            the parent security MO.
     */
    private void checkSimpleAuthenticatedMo(final CbpOiConfigureLdapOnNodeTask task, final NormalizableNodeReference normalizable,
            final ManagedObject securityMO) {

        final String moType = ModelDefinition.SIMPLE_AUTHENTICATED_TYPE;
        ManagedObject simpleAuthenticatedMO = nscsDpsUtils.getOnlyChildMo(securityMO, normalizable, moType);
        if (simpleAuthenticatedMO != null) {
            nscsLogger.info(task, FOUND_MO_OF_TYPE_UNDER_PARENT_MO, simpleAuthenticatedMO.getFdn(), moType, securityMO.getFdn());
            final Map<String, Object> simpleAuthenticatedAttributes = getSimpleAuthenticatedAttributes(task);
            simpleAuthenticatedMO = nscsDpsUtils.updateMo(simpleAuthenticatedMO, simpleAuthenticatedAttributes);
            nscsLogger.info(task, SUCCESSFULLY_UPDATED_MO_WITH_ATTRS, simpleAuthenticatedMO.getFdn(),
                    stringifySimpleAuthenticatedAttributes(simpleAuthenticatedAttributes));
        } else {
            nscsLogger.info(task, NOT_FOUND_MO_OF_TYPE_UNDER_PARENT_MO, moType, securityMO.getFdn());
            nscsLogger.info(task, "authentication-type is not simple-authenticated, configure simple-authenticated now");
            createSimpleAuthenticatedMo(task, normalizable, securityMO);
        }
    }

    /**
     * Gets the simple-authenticated MO attributes according to the given task parameters.
     * 
     * @param task
     *            the task.
     * @return the attributes of the simple-authenticated MO.
     */
    private Map<String, Object> getSimpleAuthenticatedAttributes(final CbpOiConfigureLdapOnNodeTask task) {
        final Map<String, Object> simpleAuthenticatedAttributes = new HashMap<>();
        final Map<String, Serializable> ldapWorkFlowContext = task.getLdapWorkFlowContext();
        final String bindDn = (String) ldapWorkFlowContext.get(WorkflowParameterKeys.BIND_DN.toString());
        final String bindCrd = (String) ldapWorkFlowContext.get(WorkflowParameterKeys.BIND_PASSWORD.toString());
        if (bindDn != null) {
            simpleAuthenticatedAttributes.put(ModelDefinition.SIMPLE_AUTHENTICATED_BIND_DN_ATTR, bindDn);
        }
        if (bindCrd != null) {
            simpleAuthenticatedAttributes.put(ModelDefinition.SIMPLE_AUTHENTICATED_BIND_CRD_ATTR, bindCrd);
        }
        return simpleAuthenticatedAttributes;
    }

    /**
     * Stringifies the given simple-authenticated MO attributes hiding the bind credentials.
     * 
     * @param simpleAuthenticatedAttributes
     *            the simple-authenticated MO attributes.
     * @return the stringified simple-authenticated MO attributes.
     */
    private String stringifySimpleAuthenticatedAttributes(final Map<String, Object> simpleAuthenticatedAttributes) {
        final Map<String, Object> stringifiedSimpleAuthenticatedAttributes = new HashMap<>();
        stringifiedSimpleAuthenticatedAttributes.put(ModelDefinition.SIMPLE_AUTHENTICATED_BIND_DN_ATTR,
                simpleAuthenticatedAttributes.get(ModelDefinition.SIMPLE_AUTHENTICATED_BIND_DN_ATTR));
        stringifiedSimpleAuthenticatedAttributes.put(ModelDefinition.SIMPLE_AUTHENTICATED_BIND_CRD_ATTR, "***");
        return stringifiedSimpleAuthenticatedAttributes.toString();
    }

    /**
     * Checks the servers hierarchy under the given parent system,ldap MO for the given node and according to the given task.
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
    private void checkServersHierarchy(final CbpOiConfigureLdapOnNodeTask task, final NormalizableNodeReference normalizable,
            final ManagedObject systemLdapMO) {

        final List<ManagedObject> primaryServerMOs = new ArrayList<>();
        final List<ManagedObject> fallbackServerMOs = new ArrayList<>();
        final List<ManagedObject> othersServerMOs = new ArrayList<>();

        final String moType = ModelDefinition.SERVER_TYPE;
        List<ManagedObject> serverMOs = nscsDpsUtils.getChildMos(systemLdapMO, normalizable, moType);
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
            updateServerHierarchy(task, normalizable, primaryServerMOs.get(0), true);
        } else {
            createServerHierarchy(task, normalizable, systemLdapMO, true);
        }

        if (fallbackServerMOs.size() == 1) {
            updateServerHierarchy(task, normalizable, fallbackServerMOs.get(0), false);
        } else {
            createServerHierarchy(task, normalizable, systemLdapMO, false);
        }

        if (!othersServerMOs.isEmpty()) {
            nscsLogger.info(task, "Delete unuseful server MOs");
            for (final ManagedObject otherServerMO : othersServerMOs) {
                final String otherServerFdn = otherServerMO.getFdn();
                nscsDpsUtils.deleteMo(otherServerMO);
                nscsLogger.info(task, "Successfully deleted server MO [{}]", otherServerFdn);
            }
        }
    }

    /**
     * Updates the given server hierarchy for the given node and according to the given isPrimary flag.
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
    private void updateServerHierarchy(final CbpOiConfigureLdapOnNodeTask task, final NormalizableNodeReference normalizable, ManagedObject serverMO,
            final Boolean isPrimary) {

        nscsLogger.info(task, "Found server [{}]", serverMO.getFdn());

        checkTcpHierarchy(task, normalizable, serverMO, isPrimary);
    }

    /**
     * Gets the server MO attributes according to the given isPrimary flag.
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
     * Checks the tcp hierarchy under the given parent server MO for the given node and according to the given task and to the given isPrimary flag.
     * 
     * The tcp MO shall be present and unique under the server MO and in this case updates it and checks its hierarchy.
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
    private void checkTcpHierarchy(final CbpOiConfigureLdapOnNodeTask task, final NormalizableNodeReference normalizable,
            final ManagedObject serverMO, final Boolean isPrimary) {

        final String moType = ModelDefinition.TCP_TYPE;
        ManagedObject tcpMO = nscsDpsUtils.getOnlyChildMo(serverMO, normalizable, moType);
        if (tcpMO == null) {
            final String errorMessage = String.format("Missing MO of type [%s] under parent MO [%s]", moType, serverMO.getFdn());
            nscsLogger.error(task, errorMessage);
            throw new MissingMoException(errorMessage);
        }
        final Map<String, Object> tcpAttributes = getTcpAttributes(task, isPrimary);
        tcpMO = nscsDpsUtils.updateMo(tcpMO, tcpAttributes);
        nscsLogger.info(task, SUCCESSFULLY_UPDATED_MO_WITH_ATTRS, tcpMO.getFdn(), tcpAttributes);

        final String tlsMode = task.getTlsMode();
        if (LDAPS_MODE.equals(tlsMode)) {
            updateLdapsMo(task, normalizable, tcpMO);
        } else {
            updateTcpLdapMo(task, normalizable, tcpMO);
        }
    }

    /**
     * Gets the tcp MO attributes according to the given task parameters and to the given isPrimary flag.
     * 
     * @param task
     *            the task.
     * @param isPrimary
     *            true if primary server, false if fallback server.
     * @return the attributes of the tcp MO.
     */
    private Map<String, Object> getTcpAttributes(final CbpOiConfigureLdapOnNodeTask task, final Boolean isPrimary) {
        final Map<String, Object> tcpAttributes = new HashMap<>();
        final Map<String, Serializable> ldapWorkFlowContext = task.getLdapWorkFlowContext();
        String address = null;
        if (isPrimary) {
            address = (String) ldapWorkFlowContext.get(WorkflowParameterKeys.LDAP_IP_ADDRESS.toString());
        } else {
            address = (String) ldapWorkFlowContext.get(WorkflowParameterKeys.FALLBACK_LDAP_IP_ADDRESS.toString());
        }
        if (address != null) {
            tcpAttributes.put(ModelDefinition.TCP_ADDRESS_ATTR, address);
        }
        return tcpAttributes;
    }

    /**
     * Checks the ldaps MO under the given parent tcp MO for the given node and according to the given task.
     * 
     * If ldaps MO is present under tcp MO, it shall be only child MO.
     * 
     * If ldaps MO is not present under tcp MO, creates it.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param tcpMO
     *            the parent tcp MO.
     */
    private void updateLdapsMo(final CbpOiConfigureLdapOnNodeTask task, final NormalizableNodeReference normalizable, final ManagedObject tcpMO) {

        final String moType = ModelDefinition.LDAPS_TYPE;
        ManagedObject ldapsMO = nscsDpsUtils.getOnlyChildMo(tcpMO, normalizable, moType);
        if (ldapsMO != null) {
            nscsLogger.info(task, FOUND_MO_OF_TYPE_UNDER_PARENT_MO, ldapsMO.getFdn(), moType, tcpMO.getFdn());
            final Map<String, Object> ldapsAttributes = getLdapsAttributes(task);
            ldapsMO = nscsDpsUtils.updateMo(ldapsMO, ldapsAttributes);
            nscsLogger.info(task, SUCCESSFULLY_UPDATED_MO_WITH_ATTRS, ldapsMO.getFdn(), ldapsAttributes);
        } else {
            nscsLogger.info(task, NOT_FOUND_MO_OF_TYPE_UNDER_PARENT_MO, moType, tcpMO.getFdn());
            createLdapsMo(task, normalizable, tcpMO);
        }
    }

    /**
     * Gets the ldaps MO attributes according to the given task parameters.
     * 
     * @param task
     *            the task.
     * @return the attributes of the ldaps MO.
     */
    private Map<String, Object> getLdapsAttributes(final CbpOiConfigureLdapOnNodeTask task) {
        final Map<String, Object> ldapsAttributes = new HashMap<>();
        final Map<String, Serializable> ldapWorkFlowContext = task.getLdapWorkFlowContext();
        final Integer port = (Integer) ldapWorkFlowContext.get(WorkflowParameterKeys.LDAP_SERVER_PORT.toString());
        if (port != null) {
            ldapsAttributes.put(ModelDefinition.LDAPS_PORT_ATTR, port);
        }
        return ldapsAttributes;
    }

    /**
     * Checks the tcp,ldap MO under the given parent tcp MO for the given node and according to the given task.
     * 
     * If tcp,ldap MO is present under tcp MO, it shall be only child MO.
     * 
     * If tcp,ldap MO is not present under tcp MO, creates it.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param tcpMO
     *            the parent tcp MO.
     */
    private void updateTcpLdapMo(CbpOiConfigureLdapOnNodeTask task, NormalizableNodeReference normalizable, ManagedObject tcpMO) {

        final String moType = ModelDefinition.LDAP_TYPE;

        ManagedObject tcpLdapMO = nscsDpsUtils.getOnlyChildMo(tcpMO, normalizable, moType);
        if (tcpLdapMO != null) {
            nscsLogger.info(task, FOUND_MO_OF_TYPE_UNDER_PARENT_MO, tcpLdapMO.getFdn(), moType, tcpMO.getFdn());
            final Map<String, Object> tcpLdapAttributes = getTcpLdapAttributes(task);
            tcpLdapMO = nscsDpsUtils.updateMo(tcpLdapMO, tcpLdapAttributes);
            nscsLogger.info(task, SUCCESSFULLY_UPDATED_MO_WITH_ATTRS, tcpLdapMO.getFdn(), tcpLdapAttributes);
        } else {
            nscsLogger.info(task, NOT_FOUND_MO_OF_TYPE_UNDER_PARENT_MO, moType, tcpMO.getFdn());
            createTcpLdapMo(task, normalizable, tcpMO);
        }
    }

    /**
     * Gets the tcp,ldap MO attributes according to the given task parameters.
     * 
     * @param task
     *            the task.
     * @return the attributes of the tcp,ldap MO.
     */
    private Map<String, Object> getTcpLdapAttributes(CbpOiConfigureLdapOnNodeTask task) {
        final Map<String, Object> tcpLdapAttributes = new HashMap<>();
        final Map<String, Serializable> ldapWorkFlowContext = task.getLdapWorkFlowContext();
        final Integer port = (Integer) ldapWorkFlowContext.get(WorkflowParameterKeys.LDAP_SERVER_PORT.toString());
        if (port != null) {
            tcpLdapAttributes.put(ModelDefinition.TCP_LDAP_PORT_ATTR, port);
        }
        return tcpLdapAttributes;
    }

    /**
     * Creates the system,ldap MO and its hierarchy under the given parent system MO for the given node and according to the given task.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param systemMO
     *            the parent system MO.
     */
    private void createSystemLdapHierarchy(final CbpOiConfigureLdapOnNodeTask task, final NormalizableNodeReference normalizable,
            final ManagedObject systemMO) {

        final String moType = ModelDefinition.LDAP_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_SYSTEM_EXT_NS;
        final ManagedObject systemLdapMO = nscsDpsUtils.createChildMo(systemMO, normalizable, refMimNs, moType, CbpOiMoNaming.getName(moType), null);
        nscsLogger.info(task, "Successfully created MO [{}]", systemLdapMO.getFdn());

        createSecurityHierarchy(task, normalizable, systemLdapMO);
        createServersHierarchy(task, normalizable, systemLdapMO);
    }

    /**
     * Creates the security MO and its hierarchy under the given parent system,ldap MO for the given node and according to the given task.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param systemLdapMO
     *            the parent system,ldap MO.
     */
    private void createSecurityHierarchy(final CbpOiConfigureLdapOnNodeTask task, final NormalizableNodeReference normalizable,
            final ManagedObject systemLdapMO) {

        final String moType = ModelDefinition.CBP_OI_SECURITY_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_SYSTEM_EXT_NS;

        final Map<String, Object> securityAttributes = getSecurityAttributes(task);
        final ManagedObject securityMO = nscsDpsUtils.createChildMo(systemLdapMO, normalizable, refMimNs, moType, CbpOiMoNaming.getName(moType),
                securityAttributes);
        nscsLogger.info(task, SUCCESSFULLY_CREATED_MO_WITH_ATTRS, securityMO.getFdn(), securityAttributes);

        createTlsMo(task, normalizable, securityMO);
        createSimpleAuthenticatedMo(task, normalizable, securityMO);
    }

    /**
     * Creates the tls MO under the given parent security MO for the given node and according to the given task.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param securityMO
     *            the parent security MO.
     */
    private void createTlsMo(final CbpOiConfigureLdapOnNodeTask task, final NormalizableNodeReference normalizable, final ManagedObject securityMO) {

        final String moType = ModelDefinition.CBP_OI_TLS_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_SYSTEM_EXT_NS;
        final ManagedObject tlsMO = nscsDpsUtils.createChildMo(securityMO, normalizable, refMimNs, moType, CbpOiMoNaming.getName(moType), null);
        nscsLogger.info(task, "Successfully created MO [{}]", tlsMO.getFdn());
    }

    /**
     * Creates the simple-authenticated MO under the given parent security MO for the given node and according to the given task.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param securityMO
     *            the parent security MO.
     */
    private void createSimpleAuthenticatedMo(CbpOiConfigureLdapOnNodeTask task, NormalizableNodeReference normalizable, ManagedObject securityMO) {

        final String moType = ModelDefinition.SIMPLE_AUTHENTICATED_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_SYSTEM_EXT_NS;

        final Map<String, Object> simpleAuthenticatedAttributes = getSimpleAuthenticatedAttributes(task);
        final ManagedObject simpleAuthenticatedMO = nscsDpsUtils.createChildMo(securityMO, normalizable, refMimNs, moType,
                CbpOiMoNaming.getName(moType), simpleAuthenticatedAttributes);
        nscsLogger.info(task, SUCCESSFULLY_CREATED_MO_WITH_ATTRS, simpleAuthenticatedMO.getFdn(),
                stringifySimpleAuthenticatedAttributes(simpleAuthenticatedAttributes));
    }

    /**
     * Creates the server MOs hierarchy under the given parent system,ldap MO for the given node and according to the given task.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param systemLdapMO
     *            the parent system,ldap MO.
     */
    private void createServersHierarchy(final CbpOiConfigureLdapOnNodeTask task, final NormalizableNodeReference normalizable,
            final ManagedObject systemLdapMO) {

        createServerHierarchy(task, normalizable, systemLdapMO, true);
        createServerHierarchy(task, normalizable, systemLdapMO, false);
    }

    /**
     * Creates the server MO and its hierarchy under the given parent system,ldap MO for the given node and according to the given task and to the
     * given isPrimary flag.
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
    private void createServerHierarchy(final CbpOiConfigureLdapOnNodeTask task, final NormalizableNodeReference normalizable,
            final ManagedObject systemLdapMO, final Boolean isPrimary) {

        final String moType = ModelDefinition.SERVER_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_SYSTEM_EXT_NS;

        final Map<String, Object> serverAttributes = getServerAttributes(isPrimary);
        final ManagedObject serverMO = nscsDpsUtils.createChildMo(systemLdapMO, normalizable, refMimNs, moType,
                CbpOiMoNaming.getServerName(isPrimary), serverAttributes);
        nscsLogger.info(task, SUCCESSFULLY_CREATED_MO_WITH_ATTRS, serverMO.getFdn(), serverAttributes);

        createTcpHierarchy(task, normalizable, serverMO, isPrimary);
    }

    /**
     * Creates the tcp MO and its hierarchy under the given parent server MO for the given node and according to the given task and the given
     * isPrimary flag.
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
    private void createTcpHierarchy(final CbpOiConfigureLdapOnNodeTask task, final NormalizableNodeReference normalizable,
            final ManagedObject serverMO, final Boolean isPrimary) {

        final String moType = ModelDefinition.TCP_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_SYSTEM_EXT_NS;

        final Map<String, Object> tcpAttributes = getTcpAttributes(task, isPrimary);
        final ManagedObject tcpMO = nscsDpsUtils.createChildMo(serverMO, normalizable, refMimNs, moType, CbpOiMoNaming.getName(moType),
                tcpAttributes);
        nscsLogger.info(task, SUCCESSFULLY_CREATED_MO_WITH_ATTRS, tcpMO.getFdn(), tcpAttributes);

        final String tlsMode = task.getTlsMode();
        if (LDAPS_MODE.equals(tlsMode)) {
            createLdapsMo(task, normalizable, tcpMO);
        } else {
            createTcpLdapMo(task, normalizable, tcpMO);
        }
    }

    /**
     * Creates the ldaps MO under the given parent tcp MO for the given node and according to the given task.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param tcpMO
     *            the parent tcp MO.
     */
    private void createLdapsMo(final CbpOiConfigureLdapOnNodeTask task, final NormalizableNodeReference normalizable, final ManagedObject tcpMO) {

        final String moType = ModelDefinition.LDAPS_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_SYSTEM_EXT_NS;

        final Map<String, Object> ldapsAttributes = getLdapsAttributes(task);
        final ManagedObject ldapsMO = nscsDpsUtils.createChildMo(tcpMO, normalizable, refMimNs, moType, CbpOiMoNaming.getName(moType),
                ldapsAttributes);
        nscsLogger.info(task, SUCCESSFULLY_CREATED_MO_WITH_ATTRS, ldapsMO.getFdn(), ldapsAttributes);
    }

    /**
     * Creates the tcp$$ldap MO under the given parent tcp MO for the given node and according to the given task.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @param tcpMO
     *            the parent tcp MO.
     */
    private void createTcpLdapMo(final CbpOiConfigureLdapOnNodeTask task, final NormalizableNodeReference normalizable, final ManagedObject tcpMO) {

        final String moType = ModelDefinition.LDAP_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_SYSTEM_EXT_NS;

        final Map<String, Object> tcpLdapAttributes = getTcpLdapAttributes(task);
        final ManagedObject tcpLdapMO = nscsDpsUtils.createChildMo(tcpMO, normalizable, refMimNs, moType, CbpOiMoNaming.getName(moType),
                tcpLdapAttributes);
        nscsLogger.info(task, SUCCESSFULLY_CREATED_MO_WITH_ATTRS, tcpLdapMO.getFdn(), tcpLdapAttributes);
    }

}
