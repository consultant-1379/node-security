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
package com.ericsson.nms.security.nscs

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.iscf.IscfResponse
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfSecDataDto
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfXmlComboDto
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfXmlIpsecDto
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfXmlOamDto
import com.ericsson.oss.services.security.nscs.iscf.IscfManager

class IscfRestResourceTest extends CdiSpecification {

    @ObjectUnderTest
    IscfRestResource iscfRestResource

    @MockedImplementation
    IscfManager iscfManager

    IscfResponse iscfResponse = mock(IscfResponse)
    SecurityDataResponse secDataResponse = mock(SecurityDataResponse)
    Random random = new Random();
    byte[] b = new byte[20];

    def "generate ISCF XML OAM"() {
        given:
        def dto = mock(IscfXmlOamDto)
        iscfManager.generateXmlOam(dto) >> iscfResponse
        random.nextBytes(b);
        iscfResponse.getIscfContent() >> b
        when:
        iscfRestResource.generateXmlOam(dto)
        then:
        notThrown(Exception)
    }

    def "generate ISCF XML IPSEC"() {
        given:
        def dto = mock(IscfXmlIpsecDto)
        iscfManager.generateXmlIpsec(dto) >> iscfResponse
        random.nextBytes(b);
        iscfResponse.getIscfContent() >> b
        when:
        iscfRestResource.generateXmlIpsec(dto)
        then:
        notThrown(Exception)
    }

    def "generate ISCF XML COMBO"() {
        given:
        def dto = mock(IscfXmlComboDto)
        iscfManager.generateXmlCombo(dto) >> iscfResponse
        random.nextBytes(b);
        iscfResponse.getIscfContent() >> b
        when:
        iscfRestResource.generateXmlCombined(dto)
        then:
        notThrown(Exception)
    }

    def "delete"() {
        given:
        def nscsResult = "success"
        def node = "this is the node"
        iscfManager.cancel(node) >> nscsResult
        when:
        iscfRestResource.deleteIscf(node)
        then:
        notThrown(Exception)
    }

    def "generate ISCF security data OAM"() {
        given:
        def dto = mock(IscfSecDataDto)
        iscfManager.generateSecurityDataOam(dto) >> secDataResponse
        when:
        iscfRestResource.generateSecurityDataOam(dto)
        then:
        notThrown(Exception)
    }

    def "generate ISCF security data IPSEC"() {
        given:
        def dto = mock(IscfSecDataDto)
        iscfManager.generateSecurityDataIpsec(dto) >> secDataResponse
        when:
        iscfRestResource.generateSecurityDataIpsec(dto)
        then:
        notThrown(Exception)
    }

    def "generate ISCF security data COMBO"() {
        given:
        def dto = mock(IscfSecDataDto)
        iscfManager.generateSecurityDataCombo(dto) >> secDataResponse
        when:
        iscfRestResource.generateSecurityDataCombined(dto)
        then:
        notThrown(Exception)
    }
}
