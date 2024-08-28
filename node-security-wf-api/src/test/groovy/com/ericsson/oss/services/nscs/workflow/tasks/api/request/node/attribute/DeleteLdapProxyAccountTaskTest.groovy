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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType

import spock.lang.Unroll

class DeleteLdapProxyAccountTaskTest extends CdiSpecification {

    def "no-args constructor" () {
        given:
        when: "instantiating using no-arg constructor"
        def DeleteLdapProxyAccountTask task = new DeleteLdapProxyAccountTask()
        then: "task should not be null"
        task != null
        and: "task type should be DELETE_LDAP_PROXY_ACCOUNT"
        task.getTaskType() == WorkflowTaskType.DELETE_LDAP_PROXY_ACCOUNT
        and: "node FDN should be null"
        task.getNodeFdn() == null
        and: "short description should be the task short description"
        task.getShortDescription() == DeleteLdapProxyAccountTask.SHORT_DESCRIPTION
        and: "is renew should be null"
        task.getIsRenew() == null
        and: "ldap workflow context should be null"
        task.getLdapWorkflowContext() == null
    }

    @Unroll
    def "fdn constructor #fdn" () {
        given:
        when: "instantiating using fdn constructor"
        def DeleteLdapProxyAccountTask task = new DeleteLdapProxyAccountTask(fdn)
        then: "task should not be null"
        task != null
        and: "task type should be LDAP_CONFIGURATION"
        task.getTaskType() == WorkflowTaskType.DELETE_LDAP_PROXY_ACCOUNT
        and: "node FDN should be #expected"
        task.getNodeFdn() == expected
        and: "short description should be the task short description"
        task.getShortDescription() == DeleteLdapProxyAccountTask.SHORT_DESCRIPTION
        and: "is renew should be null"
        task.getIsRenew() == null
        and: "ldap workflow context should be null"
        task.getLdapWorkflowContext() == null
        where:
        fdn   | expected
        null  | null
        ""    | ""
        "fdn" | "fdn"
    }

    @Unroll
    def "set is renew #isrenew" () {
        given: "an instance of task"
        def DeleteLdapProxyAccountTask task = new DeleteLdapProxyAccountTask()
        when: "setting the is renew to #isrenew"
        task.setIsRenew(isrenew)
        then: "the read is renew should be #expected"
        task.getIsRenew() == expected
        where:
        isrenew || expected
        null   || null
        true   || true
        false  || false
    }

    @Unroll
    def "set ldap workflow context #outputparams" () {
        given: "an instance of task"
        def DeleteLdapProxyAccountTask task = new DeleteLdapProxyAccountTask()
        when: "setting the ldap workflow context to #outputParams"
        task.setLdapWorkflowContext(ldapworkflowcontext)
        then: "the read ldap workflow context should be #expected"
        task.getLdapWorkflowContext() == expected
        where:
        ldapworkflowcontext | expected
        null                | null
        [:]                 | [:]
        [par : "val"]       | [par : "val"]
    }
}
