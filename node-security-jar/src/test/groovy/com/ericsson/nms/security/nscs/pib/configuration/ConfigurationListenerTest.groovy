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
package com.ericsson.nms.security.nscs.pib.configuration

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.sdk.config.ConfigurationEnvironment
import com.ericsson.oss.mediation.modeling.schema.gen.net_momdtd.Out

class ConfigurationListenerTest extends CdiSpecification {

    @ObjectUnderTest
    ConfigurationListener configurationListener



    def "test listener for enforcedIKEv2PolicyProfileID changed to IKEV2"() {
        given:
        String enforcedIKEv2PolicyProfileID = "IKEV2"
        when:
        configurationListener.listenForEnforcedIKEv2PolicyProfileID(enforcedIKEv2PolicyProfileID)
        String result = configurationListener.getEnforcedIKEv2PolicyProfileID()
        then :
        assert (result = "IKEV2")
    }

    def "test listener for neCertAutoRenewalMax changed to 30"() {
        given:
        int neCertAutoRenewalMax = 30
        when:
        configurationListener.listenForNeCertAutoRenewalMax(neCertAutoRenewalMax)
        int result = configurationListener.getPibNeCertAutoRenewalMax()
        then :
        assert (result = 30)
    }

    def "test listener for neCertAutoRenewalEnabled changed to false"() {
        given:
        boolean neCertAutoRenewalEnabled = false
        when:
        configurationListener.listenForNeCertAutoRenewalEnabled(neCertAutoRenewalEnabled)
        boolean result = configurationListener.getPibNeCertAutoRenewalEnabled()
        then :
        assert (result == false)
    }

    def "test listener for neCertAutoRenewalTimer changed to false"() {
        given:
        int neCertAutoRenewalTimer = 40
        when:
        configurationListener.listenForNeCertAutoRenewalTimer(neCertAutoRenewalTimer)
        int result = configurationListener.getPibNeCertAutoRenewalTimer()
        then :
        assert (result = 30)
    }
}
