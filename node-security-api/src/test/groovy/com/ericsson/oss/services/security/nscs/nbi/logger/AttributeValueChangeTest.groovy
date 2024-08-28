/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2024
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.nbi.logger

import com.ericsson.cds.cdi.support.spock.CdiSpecification

class AttributeValueChangeTest extends CdiSpecification {

    def 'constructor with string'() {
        given:
        def avc = new AttributeValueChange('attr', 'curr', 'old')
        expect:
        avc != null
        avc.getAttribute() == 'attr'
        avc.getCurrValue() == 'curr'
        avc.getOldValue() == 'old'
    }

    def 'constructor with integer'() {
        given:
        def avc = new AttributeValueChange('attr', 1, 2)
        expect:
        avc != null
        avc.getAttribute() == 'attr'
        avc.getCurrValue() == 1
        avc.getOldValue() == 2
    }

    def 'constructor with boolean'() {
        given:
        def avc = new AttributeValueChange('attr', true, false)
        expect:
        avc != null
        avc.getAttribute() == 'attr'
        avc.getCurrValue() == true
        avc.getOldValue() == false
    }

    def 'set attribute'() {
        given:
        def avc = new AttributeValueChange('attr', 'curr', 'old')
        when:
        avc.setAttribute("newattr")
        then:
        avc != null
        avc.getAttribute() == 'newattr'
        avc.getCurrValue() == 'curr'
        avc.getOldValue() == 'old'
    }

    def 'set current and old as string'() {
        given:
        def avc = new AttributeValueChange('attr', 'curr', 'old')
        when:
        avc.setCurrValue("newcurr")
        avc.setOldValue("newold")
        then:
        avc != null
        avc.getAttribute() == 'attr'
        avc.getCurrValue() == 'newcurr'
        avc.getOldValue() == 'newold'
    }

    def 'set current and old as integer'() {
        given:
        def avc = new AttributeValueChange('attr', 1, 2)
        when:
        avc.setCurrValue(3)
        avc.setOldValue(4)
        then:
        avc != null
        avc.getAttribute() == 'attr'
        avc.getCurrValue() == 3
        avc.getOldValue() == 4
    }

    def 'set current and old as boolean'() {
        given:
        def avc = new AttributeValueChange('attr', true, false)
        when:
        avc.setCurrValue(false)
        avc.setOldValue(true)
        then:
        avc != null
        avc.getAttribute() == 'attr'
        avc.getCurrValue() == false
        avc.getOldValue() == true
    }
}
