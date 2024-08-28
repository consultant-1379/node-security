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
package com.ericsson.nms.security.nscs.handler.command.utility

import com.ericsson.cds.cdi.support.spock.CdiSpecification

import spock.lang.Unroll

class NscsCapabilityDetailsTest extends CdiSpecification {

    def 'no capability values'() {
        given:
        NscsCapabilityDetails nscsCapabilityDetails = new NscsCapabilityDetails("MODEL", "NAME", "DEFAULT")
        expect:
        nscsCapabilityDetails != null
        and:
        nscsCapabilityDetails.getCapabilityModelName() == "MODEL"
        and:
        nscsCapabilityDetails.getCapabilityName() == "NAME"
        and:
        nscsCapabilityDetails.getDefaultValue() == "DEFAULT"
        and:
        nscsCapabilityDetails.getAllValues().isEmpty() == true
    }

    def 'add single value with null node type'() {
        given:
        NscsCapabilityDetails nscsCapabilityDetails = new NscsCapabilityDetails("MODEL", "NAME", "DEFAULT")
        when:
        nscsCapabilityDetails.addValue("VALUE", null, "OMI", "NOTES")
        then:
        nscsCapabilityDetails != null
        and:
        nscsCapabilityDetails.getCapabilityModelName() == "MODEL"
        and:
        nscsCapabilityDetails.getCapabilityName() == "NAME"
        and:
        nscsCapabilityDetails.getDefaultValue() == "DEFAULT"
        and:
        nscsCapabilityDetails.getAllValues().isEmpty() == true
    }

    @Unroll
    def 'add single value with OSS model identity #omi'() {
        given:
        NscsCapabilityDetails nscsCapabilityDetails = new NscsCapabilityDetails("MODEL", "NAME", "DEFAULT")
        when:
        nscsCapabilityDetails.addValue("VALUE", "CATEGORY:TYPE", omi, "NOTES")
        then:
        nscsCapabilityDetails != null
        and:
        nscsCapabilityDetails.getCapabilityModelName() == "MODEL"
        and:
        nscsCapabilityDetails.getCapabilityName() == "NAME"
        and:
        nscsCapabilityDetails.getDefaultValue() == "DEFAULT"
        and:
        nscsCapabilityDetails.getAllValues().isEmpty() == false
        and:
        nscsCapabilityDetails.getAllValues().size() == 1
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").isEmpty() == false
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").size() == 1
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE") != null
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE").getoMIs().isEmpty() == false
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE").getoMIs().size() == 1
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE").getoMIs().getAt(0) == omi
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE").getNotes() == "NOTES"
        where:
        omi << [null, "OMI"]
    }

    def 'add twice same value for same node type with different OSS model identity'() {
        given:
        NscsCapabilityDetails nscsCapabilityDetails = new NscsCapabilityDetails("MODEL", "NAME", "DEFAULT")
        and:
        nscsCapabilityDetails.addValue("VALUE", "CATEGORY:TYPE", "OMI", "NOTES")
        when:
        nscsCapabilityDetails.addValue("VALUE", "CATEGORY:TYPE", "OMI2", "NOTES")
        then:
        nscsCapabilityDetails != null
        and:
        nscsCapabilityDetails.getCapabilityModelName() == "MODEL"
        and:
        nscsCapabilityDetails.getCapabilityName() == "NAME"
        and:
        nscsCapabilityDetails.getDefaultValue() == "DEFAULT"
        and:
        nscsCapabilityDetails.getAllValues().isEmpty() == false
        and:
        nscsCapabilityDetails.getAllValues().size() == 1
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").isEmpty() == false
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").size() == 1
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE") != null
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE").getoMIs().isEmpty() == false
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE").getoMIs().size() == 2
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE").getoMIs().contains("OMI") == true
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE").getoMIs().contains("OMI2") == true
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE").getNotes() == "NOTES"
    }

    def 'add twice different value for same node type with different OSS model identity'() {
        given:
        NscsCapabilityDetails nscsCapabilityDetails = new NscsCapabilityDetails("MODEL", "NAME", "DEFAULT")
        and:
        nscsCapabilityDetails.addValue("VALUE", "CATEGORY:TYPE", "OMI", "NOTES")
        when:
        nscsCapabilityDetails.addValue("VALUE2", "CATEGORY:TYPE", "OMI2", "NOTES")
        then:
        nscsCapabilityDetails != null
        and:
        nscsCapabilityDetails.getCapabilityModelName() == "MODEL"
        and:
        nscsCapabilityDetails.getCapabilityName() == "NAME"
        and:
        nscsCapabilityDetails.getDefaultValue() == "DEFAULT"
        and:
        nscsCapabilityDetails.getAllValues().isEmpty() == false
        and:
        nscsCapabilityDetails.getAllValues().size() == 1
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").isEmpty() == false
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").size() == 2
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE") != null
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE").getoMIs().isEmpty() == false
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE").getoMIs().size() == 1
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE").getoMIs().contains("OMI") == true
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE").getNotes() == "NOTES"
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE2") != null
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE2").getoMIs().isEmpty() == false
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE2").getoMIs().size() == 1
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE2").getoMIs().contains("OMI2") == true
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE2").getNotes() == "NOTES"
    }

    def 'add twice different value for different node type with different OSS model identity'() {
        given:
        NscsCapabilityDetails nscsCapabilityDetails = new NscsCapabilityDetails("MODEL", "NAME", "DEFAULT")
        and:
        nscsCapabilityDetails.addValue("VALUE", "CATEGORY:TYPE", "OMI", "NOTES")
        when:
        nscsCapabilityDetails.addValue("VALUE2", "CATEGORY2:TYPE2", "OMI2", "NOTES")
        then:
        nscsCapabilityDetails != null
        and:
        nscsCapabilityDetails.getCapabilityModelName() == "MODEL"
        and:
        nscsCapabilityDetails.getCapabilityName() == "NAME"
        and:
        nscsCapabilityDetails.getDefaultValue() == "DEFAULT"
        and:
        nscsCapabilityDetails.getAllValues().isEmpty() == false
        and:
        nscsCapabilityDetails.getAllValues().size() == 2
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").isEmpty() == false
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").size() == 1
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE") != null
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE").getoMIs().isEmpty() == false
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE").getoMIs().size() == 1
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE").getoMIs().contains("OMI") == true
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY:TYPE").get("VALUE").getNotes() == "NOTES"
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY2:TYPE2").isEmpty() == false
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY2:TYPE2").size() == 1
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY2:TYPE2").get("VALUE2") != null
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY2:TYPE2").get("VALUE2").getoMIs().isEmpty() == false
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY2:TYPE2").get("VALUE2").getoMIs().size() == 1
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY2:TYPE2").get("VALUE2").getoMIs().contains("OMI2") == true
        and:
        nscsCapabilityDetails.getAllValues().get("CATEGORY2:TYPE2").get("VALUE2").getNotes() == "NOTES"
    }
}