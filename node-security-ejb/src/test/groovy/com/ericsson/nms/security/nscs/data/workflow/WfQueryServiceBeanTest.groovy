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
package com.ericsson.nms.security.nscs.data.workflow

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.oss.services.security.nscs.context.NscsContextService
import com.ericsson.oss.services.wfs.api.query.Query
import com.ericsson.oss.services.wfs.jee.api.WorkflowQueryServiceRemote

class WfQueryServiceBeanTest extends CdiSpecification {

    @ObjectUnderTest
    WfQueryServiceBean wfQueryServiceBean

    @MockedImplementation
    NscsContextService nscsContextService

    @MockedImplementation
    WorkflowQueryServiceRemote queryService

    def 'object under test'() {
        expect:
        wfQueryServiceBean != null
    }

    def 'is workflow in progress' () {
        given:
        def nodeRef = mock(NodeReference.class)
        nodeRef.getFdn() >> "fdn"
        and:
        nscsContextService.getUserIdContextValue() >> "user-id"
        and:
        queryService.executeQuery(_ as Query) >> new ArrayList()
        when:
        def isInProgress = wfQueryServiceBean.isWorkflowInProgress(nodeRef)
        then:
        noExceptionThrown()
        and:
        isInProgress == false
        and:
        1 * wfQueryServiceBean.nscsContextService.setUserIdContextValue("Administrator")
        1 * wfQueryServiceBean.nscsContextService.setUserIdContextValue("user-id")
    }
}
