/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.command.types

import com.ericsson.cds.cdi.support.spock.CdiSpecification

class CapabilityGetCommandTest extends CdiSpecification {

    def 'get all command'() {
        given:
        CapabilityGetCommand capabilityGetCommand = new CapabilityGetCommand()
        capabilityGetCommand.setProperties(["netype": "*"])
        expect:
        capabilityGetCommand.isAllNeTypes() == true
        and:
        capabilityGetCommand.getTargetCategory() == null
        and:
        capabilityGetCommand.getNeType() == null
        and:
        capabilityGetCommand.getOssModelIdentity() == null
        and:
        capabilityGetCommand.getCapabilityName() == null
        and:
        capabilityGetCommand.isSkipConsistencyCheck() == false
    }

    def 'get target type command'() {
        given:
        CapabilityGetCommand capabilityGetCommand = new CapabilityGetCommand()
        capabilityGetCommand.setProperties(["netype": "NETYPE"])
        expect:
        capabilityGetCommand.isAllNeTypes() == false
        and:
        capabilityGetCommand.getTargetCategory() == null
        and:
        capabilityGetCommand.getNeType() == "NETYPE"
        and:
        capabilityGetCommand.getOssModelIdentity() == null
        and:
        capabilityGetCommand.getCapabilityName() == null
        and:
        capabilityGetCommand.isSkipConsistencyCheck() == false
    }

    def 'get target category command'() {
        given:
        CapabilityGetCommand capabilityGetCommand = new CapabilityGetCommand()
        capabilityGetCommand.setProperties(["targetcategory": "TARGETCATEGORY"])
        expect:
        capabilityGetCommand.isAllNeTypes() == false
        and:
        capabilityGetCommand.getTargetCategory() == "TARGETCATEGORY"
        and:
        capabilityGetCommand.getNeType() == null
        and:
        capabilityGetCommand.getOssModelIdentity() == null
        and:
        capabilityGetCommand.getCapabilityName() == null
        and:
        capabilityGetCommand.isSkipConsistencyCheck() == false
    }

    def 'get target model identity command'() {
        given:
        CapabilityGetCommand capabilityGetCommand = new CapabilityGetCommand()
        capabilityGetCommand.setProperties(["ossmodelidentity": "OMI"])
        expect:
        capabilityGetCommand.isAllNeTypes() == false
        and:
        capabilityGetCommand.getTargetCategory() == null
        and:
        capabilityGetCommand.getNeType() == null
        and:
        capabilityGetCommand.getOssModelIdentity() == "OMI"
        and:
        capabilityGetCommand.getCapabilityName() == null
        and:
        capabilityGetCommand.isSkipConsistencyCheck() == false
    }

    def 'get capability name command'() {
        given:
        CapabilityGetCommand capabilityGetCommand = new CapabilityGetCommand()
        capabilityGetCommand.setProperties(["capabilityname": "CAPABILITYNAME"])
        expect:
        capabilityGetCommand.isAllNeTypes() == false
        and:
        capabilityGetCommand.getTargetCategory() == null
        and:
        capabilityGetCommand.getNeType() == null
        and:
        capabilityGetCommand.getOssModelIdentity() == null
        and:
        capabilityGetCommand.getCapabilityName() == "CAPABILITYNAME"
        and:
        capabilityGetCommand.isSkipConsistencyCheck() == false
    }

    def 'get skip consistency check command'() {
        given:
        CapabilityGetCommand capabilityGetCommand = new CapabilityGetCommand()
        capabilityGetCommand.setProperties(["skipconsistencycheck": ""])
        expect:
        capabilityGetCommand.isAllNeTypes() == false
        and:
        capabilityGetCommand.getTargetCategory() == null
        and:
        capabilityGetCommand.getNeType() == null
        and:
        capabilityGetCommand.getOssModelIdentity() == null
        and:
        capabilityGetCommand.getCapabilityName() == null
        and:
        capabilityGetCommand.isSkipConsistencyCheck() == true
    }
}
