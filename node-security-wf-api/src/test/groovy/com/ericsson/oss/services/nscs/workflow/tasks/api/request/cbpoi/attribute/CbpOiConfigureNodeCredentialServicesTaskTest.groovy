/*-----------------------------------------------------------------------------
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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType

import spock.lang.Unroll

class CbpOiConfigureNodeCredentialServicesTaskTest extends CdiSpecification {

    def "no-args constructor" () {
        given:
        when: "instantiating using no-arg constructor"
        def CbpOiConfigureNodeCredentialServicesTask task = new CbpOiConfigureNodeCredentialServicesTask()
        then: "task should not be null"
        task != null
        and: "task type should be CBP_OI_CONFIGURE_NODE_CREDENTIAL_SERVICES"
        task.getTaskType() == WorkflowTaskType.CBP_OI_CONFIGURE_NODE_CREDENTIAL_SERVICES
        and: "node FDN should be null"
        task.getNodeFdn() == null
        and: "short description should be the task short description"
        task.getShortDescription() == CbpOiConfigureNodeCredentialServicesTask.SHORT_DESCRIPTION
        and: "output parameters should be null"
        task.getOutputParams() == null
    }

    @Unroll
    def "fdn constructor" () {
        given:
        when: "instantiating using fdn constructor"
        def CbpOiConfigureNodeCredentialServicesTask task = new CbpOiConfigureNodeCredentialServicesTask(fdn)
        then: "task should not be null"
        task != null
        and: "task type should be CBP_OI_CONFIGURE_NODE_CREDENTIAL_SERVICES"
        task.getTaskType() == WorkflowTaskType.CBP_OI_CONFIGURE_NODE_CREDENTIAL_SERVICES
        and: "node FDN should be #expected"
        task.getNodeFdn() == expected
        and: "short description should be the task short description"
        task.getShortDescription() == CbpOiConfigureNodeCredentialServicesTask.SHORT_DESCRIPTION
        and: "output parameters should be null"
        task.getOutputParams() == null
        where:
        fdn   | expected
        null  | null
        ""    | ""
        "fdn" | "fdn"
    }

    @Unroll
    def "set output parameters" () {
        given: "an instance of task"
        def CbpOiConfigureNodeCredentialServicesTask task = new CbpOiConfigureNodeCredentialServicesTask()
        when: "setting the output parameters to #outputParams"
        task.setOutputParams(outputParams)
        then: "the read output parameters should be #expected"
        task.getOutputParams() == expected
        where:
        outputParams   | expected
        null           | null
        [:]            | [:]
        [par : "val"]  | [par : "val"]
    }

    @Unroll
    def "set is trust distribution required" () {
        given: "an instance of task"
        def CbpOiConfigureNodeCredentialServicesTask task = new CbpOiConfigureNodeCredentialServicesTask()
        when: "setting the is trust distribution required to #isrequired"
        task.setIsTrustDistributionRequired(isrequired)
        then: "the read is trust distribution required should be #expected"
        task.getIsTrustDistributionRequired() == expected
        where:
        isrequired | expected
        null       | null
        ""         | ""
        "true"     | "true"
        "false"    | "false"
    }

    @Unroll
    def "set certificate type" () {
        given: "an instance of task"
        def CbpOiConfigureNodeCredentialServicesTask task = new CbpOiConfigureNodeCredentialServicesTask()
        when: "setting the certificate type to #certtype"
        task.setTrustedCertCategory(certtype)
        then: "the read certificate type should be #expected"
        task.getTrustedCertCategory() == expected
        where:
        certtype   | expected
        null       | null
        ""         | ""
        "OAM"      | "OAM"
    }
}
