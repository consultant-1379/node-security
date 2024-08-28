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
package com.ericsson.oss.services.security.nscs.command

import com.ericsson.cds.cdi.support.spock.CdiSpecification

class CommandHandlerStatsFormatterTest extends CdiSpecification {

    def 'constructor'() {
        given:
        def commandHandlerStatsFormatter = new CommandHandlerStatsFormatter()
        when:
        def eventData = commandHandlerStatsFormatter.toEventData()
        then:
        commandHandlerStatsFormatter != null
        and:
        commandHandlerStatsFormatter.getCommandId() == null
        and:
        commandHandlerStatsFormatter.getStartTimeInMillis() != null
        and:
        commandHandlerStatsFormatter.getNumItems() == null
        and:
        commandHandlerStatsFormatter.getNumSuccessItems() == null
        and:
        commandHandlerStatsFormatter.getNumErrorItems() == null
        and:
        eventData != null
        and:
        !eventData.isEmpty()
        and:
        eventData.containsKey("COMMAND_ID")
        and:
        eventData.get("COMMAND_ID") == null
        and:
        eventData.containsKey("COMMAND_DURATION")
        and:
        eventData.get("COMMAND_DURATION") != null
        and:
        !eventData.containsKey("COMMAND_NUM_ITEMS")
        and:
        !eventData.containsKey("COMMAND_NUM_SUCCESS_ITEMS")
        and:
        !eventData.containsKey("COMMAND_NUM_ERROR_ITEMS")
    }

    def 'set command identifier'() {
        given:
        def commandHandlerStatsFormatter = new CommandHandlerStatsFormatter()
        and:
        commandHandlerStatsFormatter.setCommandId("COMMAND_IDENTIFIER")
        when:
        def eventData = commandHandlerStatsFormatter.toEventData()
        then:
        commandHandlerStatsFormatter != null
        and:
        commandHandlerStatsFormatter.getCommandId() == "COMMAND_IDENTIFIER"
        and:
        commandHandlerStatsFormatter.getStartTimeInMillis() != null
        and:
        commandHandlerStatsFormatter.getNumItems() == null
        and:
        commandHandlerStatsFormatter.getNumSuccessItems() == null
        and:
        commandHandlerStatsFormatter.getNumErrorItems() == null
        and:
        eventData != null
        and:
        !eventData.isEmpty()
        and:
        eventData.containsKey("COMMAND_ID")
        and:
        eventData.get("COMMAND_ID") == "COMMAND_IDENTIFIER"
        and:
        eventData.containsKey("COMMAND_DURATION")
        and:
        eventData.get("COMMAND_DURATION") != null
        and:
        !eventData.containsKey("COMMAND_NUM_ITEMS")
        and:
        !eventData.containsKey("COMMAND_NUM_SUCCESS_ITEMS")
        and:
        !eventData.containsKey("COMMAND_NUM_ERROR_ITEMS")
    }

    def 'set num of items'() {
        given:
        def commandHandlerStatsFormatter = new CommandHandlerStatsFormatter()
        and:
        commandHandlerStatsFormatter.setNumItems(3)
        when:
        def eventData = commandHandlerStatsFormatter.toEventData()
        then:
        commandHandlerStatsFormatter != null
        and:
        commandHandlerStatsFormatter.getCommandId() == null
        and:
        commandHandlerStatsFormatter.getStartTimeInMillis() != null
        and:
        commandHandlerStatsFormatter.getNumItems() == 3
        and:
        commandHandlerStatsFormatter.getNumSuccessItems() == null
        and:
        commandHandlerStatsFormatter.getNumErrorItems() == null
        and:
        eventData != null
        and:
        !eventData.isEmpty()
        and:
        eventData.containsKey("COMMAND_ID")
        and:
        eventData.get("COMMAND_ID") == null
        and:
        eventData.containsKey("COMMAND_DURATION")
        and:
        eventData.get("COMMAND_DURATION") != null
        and:
        eventData.containsKey("COMMAND_NUM_ITEMS")
        and:
        eventData.get("COMMAND_NUM_ITEMS") == 3
        and:
        !eventData.containsKey("COMMAND_NUM_SUCCESS_ITEMS")
        and:
        !eventData.containsKey("COMMAND_NUM_ERROR_ITEMS")
    }

    def 'set num of success items'() {
        given:
        def commandHandlerStatsFormatter = new CommandHandlerStatsFormatter()
        and:
        commandHandlerStatsFormatter.setNumSuccessItems(2)
        when:
        def eventData = commandHandlerStatsFormatter.toEventData()
        then:
        commandHandlerStatsFormatter != null
        and:
        commandHandlerStatsFormatter.getCommandId() == null
        and:
        commandHandlerStatsFormatter.getStartTimeInMillis() != null
        and:
        commandHandlerStatsFormatter.getNumItems() == null
        and:
        commandHandlerStatsFormatter.getNumSuccessItems() == 2
        and:
        commandHandlerStatsFormatter.getNumErrorItems() == null
        and:
        eventData != null
        and:
        !eventData.isEmpty()
        and:
        eventData.containsKey("COMMAND_ID")
        and:
        eventData.get("COMMAND_ID") == null
        and:
        eventData.containsKey("COMMAND_DURATION")
        and:
        eventData.get("COMMAND_DURATION") != null
        and:
        !eventData.containsKey("COMMAND_NUM_ITEMS")
        and:
        eventData.containsKey("COMMAND_NUM_SUCCESS_ITEMS")
        and:
        eventData.get("COMMAND_NUM_SUCCESS_ITEMS") == 2
        and:
        !eventData.containsKey("COMMAND_NUM_ERROR_ITEMS")
    }

    def 'set num of error items'() {
        given:
        def commandHandlerStatsFormatter = new CommandHandlerStatsFormatter()
        and:
        commandHandlerStatsFormatter.setNumErrorItems(1)
        when:
        def eventData = commandHandlerStatsFormatter.toEventData()
        then:
        commandHandlerStatsFormatter != null
        and:
        commandHandlerStatsFormatter.getCommandId() == null
        and:
        commandHandlerStatsFormatter.getStartTimeInMillis() != null
        and:
        commandHandlerStatsFormatter.getNumItems() == null
        and:
        commandHandlerStatsFormatter.getNumSuccessItems() == null
        and:
        commandHandlerStatsFormatter.getNumErrorItems() == 1
        and:
        eventData != null
        and:
        !eventData.isEmpty()
        and:
        eventData.containsKey("COMMAND_ID")
        and:
        eventData.get("COMMAND_ID") == null
        and:
        eventData.containsKey("COMMAND_DURATION")
        and:
        eventData.get("COMMAND_DURATION") != null
        and:
        !eventData.containsKey("COMMAND_NUM_ITEMS")
        and:
        !eventData.containsKey("COMMAND_NUM_SUCCESS_ITEMS")
        and:
        eventData.containsKey("COMMAND_NUM_ERROR_ITEMS")
        and:
        eventData.get("COMMAND_NUM_ERROR_ITEMS") == 1
    }

    def 'command completed with total and success and failed'() {
        given:
        def commandHandlerStatsFormatter = new CommandHandlerStatsFormatter()
        and:
        commandHandlerStatsFormatter.setNumItems(3)
        commandHandlerStatsFormatter.setNumSuccessItems(1)
        commandHandlerStatsFormatter.setNumErrorItems(1)
        when:
        def eventData = commandHandlerStatsFormatter.toEventData()
        then:
        commandHandlerStatsFormatter != null
        and:
        commandHandlerStatsFormatter.getCommandId() == null
        and:
        commandHandlerStatsFormatter.getStartTimeInMillis() != null
        and:
        commandHandlerStatsFormatter.getNumItems() == 3
        and:
        commandHandlerStatsFormatter.getNumSuccessItems() == 1
        and:
        commandHandlerStatsFormatter.getNumErrorItems() == 1
        and:
        eventData != null
        and:
        !eventData.isEmpty()
        and:
        eventData.containsKey("COMMAND_ID")
        and:
        eventData.get("COMMAND_ID") == null
        and:
        eventData.containsKey("COMMAND_DURATION")
        and:
        eventData.get("COMMAND_DURATION") != null
        and:
        eventData.containsKey("COMMAND_NUM_ITEMS")
        and:
        eventData.get("COMMAND_NUM_ITEMS") == 3
        and:
        eventData.containsKey("COMMAND_NUM_SUCCESS_ITEMS")
        and:
        eventData.get("COMMAND_NUM_SUCCESS_ITEMS") == 1
        and:
        eventData.containsKey("COMMAND_NUM_ERROR_ITEMS")
        and:
        eventData.get("COMMAND_NUM_ERROR_ITEMS") == 1
    }
}
