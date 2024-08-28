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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.moaction;


import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CbpOiConfigureLdapOnNodeTaskTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private static final String NODE_FDN = "ERBS_01";
    private static final String BIND_PASSWORD_UNDER_TEST = "xso53wwv";
    private static final String BIND_DN_UNDER_TEST = "cn=ProxyAccount_1,ou=proxyagent,ou=com,dc=rani-venm-1,dc=com";
    private static final String BASE_DN_UNDER_TEST = "dc=rani-venm-1,dc=com";
    private static final Integer LDAP_SERVER_PORT_UNDER_TEST = 1636;
    private static final String LDAP_IP_ADDRESS_UNDER_TEST = "10.129.10.246";
    private static final String FALLBACK_LDAP_IP_ADDRESS_UNDER_TEST = "10.129.10.247";

    @Test
    public void test_CbpOiConfigureLdapOnNodeTask() {
        log.info("CbpOiConfigureLdapOnNodeTask");
        final CbpOiConfigureLdapOnNodeTask task = new CbpOiConfigureLdapOnNodeTask();
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CBPOI_CONFIGURE_LDAP_ACTION, task.getTaskType());
        assertEquals("Unexpected short description", CbpOiConfigureLdapOnNodeTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_CbpOiConfigureLdapOnNodeTask_NoDefault() {
        log.info("test_CbpOiConfigureLdapOnNodeTask_NoDefault");
        final CbpOiConfigureLdapOnNodeTask task = new CbpOiConfigureLdapOnNodeTask(NODE_FDN);
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CBPOI_CONFIGURE_LDAP_ACTION, task.getTaskType());
        assertEquals("Unexpected Name", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected short description", CbpOiConfigureLdapOnNodeTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void test_CbpOiConfigureLdapOnNodeTask_LdapFlowContext() {
        log.info("test_CbpOiConfigureLdapOnNodeTask_NoDefault");
        final CbpOiConfigureLdapOnNodeTask task = new CbpOiConfigureLdapOnNodeTask(NODE_FDN);
        final Map<String, Serializable> outputParams = new HashMap<>();
        outputParams.put("label", "prova");
        task.setLdapWorkFlowContext(outputParams);
        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CBPOI_CONFIGURE_LDAP_ACTION, task.getTaskType());
        assertEquals("Unexpected Name", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected short description", CbpOiConfigureLdapOnNodeTask.SHORT_DESCRIPTION, task.getShortDescription());
        assertEquals("Unexpected ldapworkflow context", outputParams, task.getLdapWorkFlowContext());
    }

    @Test
    public void test_CbpOiConfigureLdapOnNodeTask_InputParams() {
        log.info("test_CbpOiConfigureLdapOnNodeTask_NoDefault");
        final CbpOiConfigureLdapOnNodeTask task = new CbpOiConfigureLdapOnNodeTask(NODE_FDN);
        final Map<String, Serializable> outputParams = new HashMap<>();
        outputParams.put(WorkflowParameterKeys.BIND_PASSWORD.toString(), BIND_PASSWORD_UNDER_TEST);
        outputParams.put(WorkflowParameterKeys.BIND_DN.toString(), BIND_DN_UNDER_TEST);
        outputParams.put(WorkflowParameterKeys.BASE_DN.toString(), BASE_DN_UNDER_TEST);
        outputParams.put(WorkflowParameterKeys.LDAP_SERVER_PORT.toString(), LDAP_SERVER_PORT_UNDER_TEST);
        outputParams.put(WorkflowParameterKeys.LDAP_IP_ADDRESS.toString(), LDAP_IP_ADDRESS_UNDER_TEST);
        outputParams.put(WorkflowParameterKeys.FALLBACK_LDAP_IP_ADDRESS.toString(), FALLBACK_LDAP_IP_ADDRESS_UNDER_TEST);

        task.setLdapWorkFlowContext(outputParams);
        task.setTlsMode("LDAPS");
        task.setUseTls(true);
        task.setUserLabel("label");

        task.setBindPassword((String) task.getLdapWorkFlowContext().get(WorkflowParameterKeys.BIND_PASSWORD.toString()));
        task.setBindDn((String) task.getLdapWorkFlowContext().get(WorkflowParameterKeys.BIND_DN.toString()));
        task.setBaseDn((String) task.getLdapWorkFlowContext().get(WorkflowParameterKeys.BASE_DN.toString()));
        task.setServerPort((Integer) task.getLdapWorkFlowContext().get(WorkflowParameterKeys.LDAP_SERVER_PORT.toString()));
        task.setLdapIpAddress((String) task.getLdapWorkFlowContext().get(WorkflowParameterKeys.LDAP_IP_ADDRESS.toString()));
        task.setFallbackLdapIpAddress((String) task.getLdapWorkFlowContext().get(WorkflowParameterKeys.FALLBACK_LDAP_IP_ADDRESS.toString()));

        assertNotNull("Task is null!", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CBPOI_CONFIGURE_LDAP_ACTION, task.getTaskType());
        assertEquals("Unexpected Name", NODE_FDN, task.getNodeFdn());
        assertEquals("Unexpected short description", CbpOiConfigureLdapOnNodeTask.SHORT_DESCRIPTION, task.getShortDescription());
        assertEquals("Unexpected ldapworkflow context", "LDAPS", task.getTlsMode());
        assertEquals("Unexpected ldapworkflow context", true, task.getUseTls());
        assertEquals("Unexpected ldapworkflow context", "label", task.getUserLabel());

        assertEquals("Expected task param", BIND_PASSWORD_UNDER_TEST, task.getBindPassword());
        assertEquals("Expected task param", BIND_DN_UNDER_TEST, task.getBindDn());
        assertEquals("Expected task param", BASE_DN_UNDER_TEST, task.getBaseDn());
        assertEquals("Expected task param", LDAP_SERVER_PORT_UNDER_TEST, task.getServerPort());
        assertEquals("Expected task param", LDAP_IP_ADDRESS_UNDER_TEST, task.getLdapIpAddress());
        assertEquals("Expected task param", FALLBACK_LDAP_IP_ADDRESS_UNDER_TEST, task.getFallbackLdapIpAddress());
    }
}
