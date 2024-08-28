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
package com.ericsson.nms.security.nscs.workflow.task.cbpoi.attribute

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiConfigureTrustReferenceTask
import spock.lang.Shared

class CbpOiConfigureTrustReferenceTaskHandlerTest extends CdiSpecification {

    @ObjectUnderTest
    CbpOiConfigureTrustReferenceTaskHandler configureTrustReferenceTaskHandler;

    @MockedImplementation
    NscsLogger nscsLogger

    @Shared
    def task = new CbpOiConfigureTrustReferenceTask()

    def 'When task handler is invoked, then result is always successful' () {
        given: 'No params are received from previous task'
        when: 'task handler is invoked'
        configureTrustReferenceTaskHandler.processTask(task)
        then: 'workflow result is successful'
        1 * nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, _ as String) >> {
            String logMessage = it[1]
            assert logMessage.contains("DONE")
        }
    }

}
