/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.data.moget.impl

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.model.NodeReference

class ComEcimMOGetServiceImplTest extends CdiSpecification {

    @ObjectUnderTest
    private ComEcimMOGetServiceImpl comEcimMOGetServiceImpl

    @MockedImplementation
    private NodeReference nodeRef

    def "getNodeSupportedFormatOfKeyAlgorithm verification for radio comecim node"  () {
        when:
        String keyAlgorithm = comEcimMOGetServiceImpl.getNodeSupportedFormatOfKeyAlgorithm(nodeRef, "1")
        then:
        assert(keyAlgorithm.equals("RSA_2048"))
    }
}
