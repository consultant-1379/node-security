package com.ericsson.nms.security.nscs.api.exception

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification

class PlatformSpecificConfigurationProviderExceptionTest extends CdiSpecification {

    @ObjectUnderTest
    PlatformSpecificConfigurationProviderException platformSpecificConfigurationProviderException

    private String errorMessage = PlatformSpecificConfigurationProviderException.LDAP_BASE_DN_INVALID

    def setup() {}

    def 'Test Empty constructor'() {
        given:
            platformSpecificConfigurationProviderException = new PlatformSpecificConfigurationProviderException()
        when:
            throw platformSpecificConfigurationProviderException
        then:
            PlatformSpecificConfigurationProviderException e = thrown()
            e.getMessage().contains(NscsErrorCodes.PLATFORM_CONFIGURATION_UNAVAILABLE)
    }

    def 'Test Constructor with message'() {
        given:
            platformSpecificConfigurationProviderException = new PlatformSpecificConfigurationProviderException(errorMessage)
        when:
            throw platformSpecificConfigurationProviderException
        then:
            PlatformSpecificConfigurationProviderException e = thrown()
            e.getMessage().contains(PlatformSpecificConfigurationProviderException.LDAP_BASE_DN_INVALID) &&
            e.getMessage().contains(NscsErrorCodes.PLATFORM_CONFIGURATION_UNAVAILABLE)
    }

    def 'Test returned Error Code is right'() {
        given:
            platformSpecificConfigurationProviderException = new PlatformSpecificConfigurationProviderException(errorMessage)
        when:
            Integer errorType = platformSpecificConfigurationProviderException.getErrorType().toInt()
        then:
            errorType == NscsServiceException.ErrorType.PLATFORM_CONFIGURATION_UNAVAILABLE.toInt()
    }
}
