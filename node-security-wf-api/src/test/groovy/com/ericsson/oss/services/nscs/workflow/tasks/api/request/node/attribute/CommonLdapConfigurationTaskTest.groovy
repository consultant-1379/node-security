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

class CommonLdapConfigurationTaskTest extends CdiSpecification {

    def "no-args constructor" () {
        given:
        when: "instantiating using no-arg constructor"
        def CommonLdapConfigurationTask task = new CommonLdapConfigurationTask()
        then: "task should not be null"
        task != null
        and: "task type should be LDAP_CONFIGURATION"
        task.getTaskType() == WorkflowTaskType.LDAP_CONFIGURATION
        and: "node FDN should be null"
        task.getNodeFdn() == null
        and: "short description should be the task short description"
        task.getShortDescription() == CommonLdapConfigurationTask.SHORT_DESCRIPTION
        and: "TLS mode should be null"
        task.getTlsMode() == null
        and: "is TLS should be null"
        task.getIsTls() == null
        and: "user label should be null"
        task.getUserLabel() == null
        and: "is renew should be null"
        task.getIsRenew() == null
        and: "ldap workflow context should be null"
        task.getLdapWorkflowContext() == null
    }

    @Unroll
    def "fdn constructor #fdn" () {
        given:
        when: "instantiating using fdn constructor"
        def CommonLdapConfigurationTask task = new CommonLdapConfigurationTask(fdn)
        then: "task should not be null"
        task != null
        and: "task type should be LDAP_CONFIGURATION"
        task.getTaskType() == WorkflowTaskType.LDAP_CONFIGURATION
        and: "node FDN should be #expected"
        task.getNodeFdn() == expected
        and: "short description should be the task short description"
        task.getShortDescription() == CommonLdapConfigurationTask.SHORT_DESCRIPTION
        and: "TLS mode should be null"
        task.getTlsMode() == null
        and: "is TLS should be null"
        task.getIsTls() == null
        and: "user label should be null"
        task.getUserLabel() == null
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
    def "set TLS mode #tlsmode" () {
        given: "an instance of task"
        def CommonLdapConfigurationTask task = new CommonLdapConfigurationTask()
        when: "setting the TLS mode to #tlsmode"
        task.setTlsMode(tlsmode)
        then: "the read TLS mode should be #expected"
        task.getTlsMode() == expected
        where:
        tlsmode    || expected
        null       || null
        "LDAPS"    || "LDAPS"
        "STARTTLS" || "STARTTLS"
    }

    @Unroll
    def "set is TLS #istls" () {
        given: "an instance of task"
        def CommonLdapConfigurationTask task = new CommonLdapConfigurationTask()
        when: "setting the is TLS to #istls"
        task.setIsTls(istls)
        then: "the read is TLS should be #expected"
        task.getIsTls() == expected
        where:
        istls || expected
        null  || null
        true  || true
        false || false
    }

    @Unroll
    def "set user label #userlabel" () {
        given: "an instance of task"
        def CommonLdapConfigurationTask task = new CommonLdapConfigurationTask()
        when: "setting the user label to #userlabel"
        task.setUserLabel(userlabel)
        then: "the read user label should be #expected"
        task.getUserLabel() == expected
        where:
        userlabel || expected
        null      || null
        ""        || ""
        "label"   || "label"
    }

    @Unroll
    def "set is renew #isrenew" () {
        given: "an instance of task"
        def CommonLdapConfigurationTask task = new CommonLdapConfigurationTask()
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
    def "set ldap workflow context #ldapworkflowcontext" () {
        given: "an instance of task"
        def CommonLdapConfigurationTask task = new CommonLdapConfigurationTask()
        when: "setting the ldap workflow context to #ldapworkflowcontex"
        task.setLdapWorkflowContext(ldapworkflowcontex)
        then: "the read output parameters should be #expected"
        task.getLdapWorkflowContext() == expected
        where:
        ldapworkflowcontex  | expected
        null                | null
        [:]                 | [:]
        [par : "val"]       | [par : "val"]
    }
}
