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

class CbpOiRestoreRenewalModeTaskTest extends CdiSpecification {

    def "no-args constructor" () {
        given:
        when: "instantiating using no-arg constructor"
        def CbpOiRestoreRenewalModeTask task = new CbpOiRestoreRenewalModeTask()
        then: "task should not be null"
        task != null
        and: "task type should be CBP_OI_RESTORE_RENEWAL_MODE"
        task.getTaskType() == WorkflowTaskType.CBP_OI_RESTORE_RENEWAL_MODE
        and: "node FDN should be null"
        task.getNodeFdn() == null
        and: "short description should be the task short description"
        task.getShortDescription() == CbpOiRestoreRenewalModeTask.SHORT_DESCRIPTION
        and: "output parameters should be null"
        task.getOutputParams() == null
    }

    @Unroll
    def "fdn constructor" () {
        given:
        when: "instantiating using fdn constructor"
        def CbpOiRestoreRenewalModeTask task = new CbpOiRestoreRenewalModeTask(fdn)
        then: "task should not be null"
        task != null
        and: "task type should be CBP_OI_RESTORE_RENEWAL_MODE"
        task.getTaskType() == WorkflowTaskType.CBP_OI_RESTORE_RENEWAL_MODE
        and: "node FDN should be #expected"
        task.getNodeFdn() == expected
        and: "short description should be the task short description"
        task.getShortDescription() == CbpOiRestoreRenewalModeTask.SHORT_DESCRIPTION
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
        def CbpOiRestoreRenewalModeTask task = new CbpOiRestoreRenewalModeTask()
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
    def "set is reissue" () {
        given: "an instance of task"
        def CbpOiRestoreRenewalModeTask task = new CbpOiRestoreRenewalModeTask()
        when: "setting the is reissue to #isreissue"
        task.setIsReissue(isreissue)
        then: "the read is reissue should be #expected"
        task.getIsReissue() == expected
        where:
        isreissue | expected
        null      | null
        ""        | ""
        "true"    | "true"
        "false"   | "false"
    }

    @Unroll
    def "set enrollment mode" () {
        given: "an instance of task"
        def CbpOiRestoreRenewalModeTask task = new CbpOiRestoreRenewalModeTask()
        when: "setting the enrollment mode to #enrollmentmode"
        task.setEnrollmentMode(enrollmentmode)
        then: "the read enrollment mode should be #expected"
        task.getEnrollmentMode() == expected
        where:
        enrollmentmode   | expected
        null             | null
        ""               | ""
        "CMPv2_INITIAL"  | "CMPv2_INITIAL"
    }

    @Unroll
    def "set certificate type" () {
        given: "an instance of task"
        def CbpOiRestoreRenewalModeTask task = new CbpOiRestoreRenewalModeTask()
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
